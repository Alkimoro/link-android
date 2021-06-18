package cn.linked.baselib.socket;

import android.util.Log;

import androidx.annotation.NonNull;

import cn.linked.baselib.config.Properties;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
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

public class ChatClient extends SocketClient {

    public final String host;
    public final int port;

    private Bootstrap bootstrap;
    private EventLoopGroup workGroup;

    @Getter
    private ChatService chatService;

    @Getter
    private String sessionId;

    @Getter@Setter
    private Channel chatChannel;

    @Getter@Setter
    private int heartbeatIdle=50;

    @Getter@Setter
    private int lengthFieldLength=2;
    @Getter@Setter
    private int maxContentLength=10240;//10 kb

    public ChatClient(ChatService chatService) {
        if(chatService != null && !Properties.DEBUG) {
            host = Properties.CHAT_CLIENT_HOST;
            port = Properties.CHAT_CLIENT_PORT;
        }else {
            host = Properties.CHAT_CLIENT_DEBUG_HOST;
            port = Properties.CHAT_CLIENT_DEBUG_PORT;
        }
        this.chatService = chatService;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // 添加解码
        socketChannel.pipeline().addLast("idleStateHandler",new IdleStateHandler(0,heartbeatIdle,0));
        socketChannel.pipeline().addLast("heartbeatHandler",new ClientHeartbeatHandler());
        socketChannel.pipeline().addLast("packageDecoder",
                new LengthFieldBasedFrameDecoder(lengthFieldLength+maxContentLength,
                        0,lengthFieldLength,0,0,true));
        socketChannel.pipeline().addLast("utf8Decoder", new StringDecoder(CharsetUtil.UTF_8));
        socketChannel.pipeline().addLast("chatHandler", new ChatHandler(this));
        // 添加编码
        socketChannel.pipeline().addFirst("utf8Encoder", new StringEncoder(CharsetUtil.UTF_8));
        socketChannel.pipeline().addFirst("packageEncoder", new LengthFieldPrepender(lengthFieldLength,false));
    }

    @Override
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

    @Override
    public Future<?> connect() {
        if(bootstrap!=null) {
            return bootstrap.connect(host,port);
        }
        throw new RuntimeException("还未初始化");
    }

    @Override
    public Future<?> close() {
        bootstrap=null;
        Future<?> result=workGroup.shutdownGracefully();
        workGroup=null;
        return result;
    }

    public int setSessionId(@NonNull String sessionId) {
        if(this.sessionId==null) {
            this.sessionId=sessionId;
            this.connect();
            return 0;
        }else {
            try {
                if(chatChannel!=null) {
                    chatChannel.close().sync();
                    chatChannel = null;
                }
                this.sessionId=sessionId;
                this.connect();
                return 0;
            }catch (InterruptedException e) {
                Log.i("ChatClient","取消setUserId");
                return -1;
            }
        }
    }

}
