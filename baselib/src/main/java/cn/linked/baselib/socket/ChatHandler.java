package cn.linked.baselib.socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.linked.baselib.entity.ChatMessage;
import cn.linked.baselib.entity.NetworkData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ChatHandler extends ChannelInboundHandlerAdapter {

    public static final String TAG = "ChatHandler";

    private ChatClient chatClient;

    public ChatHandler(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    private IChatDispatcher getChatDispatcher() {
        return chatClient.getChatService().getChatDispatcher();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        chatClient.channelActive(ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        chatClient.channelInactive(ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NetworkData<?> message= (NetworkData<?>) msg;
        switch (message.getCode()) {
            case NetworkData.CODE_BIND_ACK : {
                chatClient.onBindAck(ctx.channel());
                break;
            }
            case NetworkData.CODE_CHAT_ACK : {
                ChatMessage chatAckMessage = JSON.toJavaObject((JSONObject) message.getData(), ChatMessage.class);
                getChatDispatcher().deliverChatAck(chatAckMessage);
                super.channelRead(ctx, msg);
                break;
            }
            case NetworkData.CODE_CHAT_MSG : {
                ChatMessage chatMessage = JSON.toJavaObject((JSONObject) message.getData(), ChatMessage.class);
                getChatDispatcher().deliverChatMessage(chatMessage);
                super.channelRead(ctx, msg);
                break;
            }
        }
    }

}
