package cn.linked.baselib.socket;

import android.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.linked.baselib.common.AppNetwork;
import cn.linked.baselib.common.NetworkBroadcastReceiver;
import cn.linked.baselib.config.Properties;
import cn.linked.baselib.entity.NetworkData;
import cn.linked.commonlib.promise.Promise;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import lombok.Getter;
import lombok.Setter;

public class ChatClient extends ChannelInitializer<SocketChannel> {

    public static final String TAG = "ChatClient";

    public final String host;
    public final int port;

    private Bootstrap bootstrap;
    private EventLoopGroup workGroup;

    @Getter
    private ChatService chatService;

    @Getter
    private String sessionId;

    @Getter
    private Channel chatChannel;
    private boolean isChatChannelConnecting = false;

    private ConcurrentHashMap<NetworkData<?>,Promise<?>> waitSendNetworkDataMap = new ConcurrentHashMap<>();

    @Getter@Setter
    private int heartbeatIdle = 50;

    @Getter@Setter
    private int lengthFieldLength = 2;
    @Getter@Setter
    private int maxContentLength = 10240;//10 kb

    private NetworkBroadcastReceiver.NetworkListener networkListener = state -> {
        if(Properties.DEBUG) {
            Log.i(TAG, "监听到网络状态变化，当前状态:" + AppNetwork.getNetworkState());
        }
        if(AppNetwork.isNetworkConnected()) {
            connect();
        }
    };

    public ChatClient(ChatService chatService) {
        if(chatService != null && !Properties.DEBUG) {
            host = Properties.CHAT_CLIENT_HOST;
            port = Properties.CHAT_CLIENT_PORT;
        }else {
            host = Properties.CHAT_CLIENT_DEBUG_HOST;
            port = Properties.CHAT_CLIENT_DEBUG_PORT;
        }
        this.chatService = chatService;
        AppNetwork.addNetworkListener(networkListener);
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        socketChannel.pipeline().addLast("idleStateHandler",new IdleStateHandler(0, heartbeatIdle, 0));

        socketChannel.pipeline().addLast("packageDecoder",
                new LengthFieldBasedFrameDecoder(lengthFieldLength+maxContentLength,
                        0,lengthFieldLength,0,0,true));
        socketChannel.pipeline().addLast("packageEncoder", new LengthFieldPrepender(lengthFieldLength,false));

        socketChannel.pipeline().addLast("utf8Decoder", new StringDecoder(CharsetUtil.UTF_8));
        socketChannel.pipeline().addLast("utf8Encoder", new StringEncoder(CharsetUtil.UTF_8));

        socketChannel.pipeline().addLast("inboundDataParseHandler",new InboundDataParseHandler());

        socketChannel.pipeline().addLast("heartbeatHandler",new ClientHeartbeatHandler());
        // ===业务Handler===
        socketChannel.pipeline().addLast("chatHandler", new ChatHandler(this));

    }

    public void init() {
        // new 一个工作线程组
        workGroup = new NioEventLoopGroup(1);
        bootstrap = new Bootstrap()
                .group(workGroup)
                .channel(NioSocketChannel.class)
                .handler(this)
                //.localAddress(localHost,localPort)
                // 禁用Nagle算法 Nagle算法就是为了尽可能发送大块数据，避免网络中充斥着许多小数据块。
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                // 两小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    public <T> Promise<?> sendNetworkData(NetworkData<T> networkData) {
        Promise<?> promise = new Promise<>();
        if(networkData == null || networkData.getSessionId() == null) {
            promise.reject(null);
        }else {
            if(chatChannel != null) {
                chatChannel.writeAndFlush(networkData.toJsonString());
                promise.resolve(null);
            }else if(isChatChannelConnecting) {
                putEntryToWaitSendNetworkDataMap(networkData, promise);
            }else {
                putEntryToWaitSendNetworkDataMap(networkData, promise);
                connect();
            }
        }
        return promise;
    }

    void channelActive(Channel channel) {
        this.chatChannel = channel;
        isChatChannelConnecting = false;
        NetworkData<Object> bindNetworkMessage = NetworkData.bindNetworkMessage(sessionId);
        channel.writeAndFlush(bindNetworkMessage.toJsonString());
        try {
            chatService.getChatDispatcher().channelActive();
        } catch (Exception ignored) { }
        sendAllWaitNetworkData();
    }

    void channelInactive(Channel channel) {
        if(channel != null && channel == this.chatChannel) {
            this.chatChannel = null;
            try {
                chatService.getChatDispatcher().channelInactive();
            }catch (Exception ignored) { }
        }
    }

    public void connect() {
        if(AppNetwork.isNetworkConnected() && !isChatChannelConnecting
                && sessionId != null && chatChannel == null) {
            synchronized (this) {
                if(!isChatChannelConnecting) {
                    isChatChannelConnecting = true;
                    bootstrap.connect(host, port).addListener(future -> {
                        if (!future.isSuccess()) {
                            rejectAllWaitNetworkData();
                            isChatChannelConnecting = false;
                        }
                    });
                }
            }
        }
    }

    public void destroy() {
        bootstrap = null;
        workGroup.shutdownGracefully();
        workGroup = null;
        AppNetwork.removeNetworkListener(networkListener);
        networkListener = null;
    }

    private void putEntryToWaitSendNetworkDataMap(NetworkData<?> data, Promise<?> promise) {
        if(data != null && promise != null) {
            synchronized (this) {
                waitSendNetworkDataMap.put(data, promise);
            }
        }
    }

    private void sendAllWaitNetworkData() {
        synchronized (this) {
            Channel channel = chatChannel;
            for (Map.Entry<NetworkData<?>, Promise<?>> entry : waitSendNetworkDataMap.entrySet()) {
                if (channel != null) {
                    channel.writeAndFlush(entry.getKey().toJsonString());
                    entry.getValue().resolve(null);
                } else {
                    entry.getValue().reject(null);
                }
            }
            waitSendNetworkDataMap.clear();
        }
    }

    private void rejectAllWaitNetworkData() {
        synchronized (this) {
            for (Map.Entry<NetworkData<?>, Promise<?>> entry : waitSendNetworkDataMap.entrySet()) {
                entry.getValue().reject(null);
            }
            waitSendNetworkDataMap.clear();
        }
    }

    // 保证先调用 setSessionId 再调用 connect
    public void setSessionId(String sessionId) {
        if (sessionId != null && !sessionId.equals(this.sessionId)) {
            if (chatChannel != null) {
                chatChannel.close();
                chatChannel = null;
            }
            rejectAllWaitNetworkData();
            this.sessionId = sessionId;
            connect();
        }
    }

}
