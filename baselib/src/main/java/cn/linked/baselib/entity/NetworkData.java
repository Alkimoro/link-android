package cn.linked.baselib.entity;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NetworkData<T> {

    /**
     *  700 心跳
     *  701 绑定用户
     *  702 聊天消息
     *  703 聊天消息确认
     *  704 用户Session过期
     * */
    public static final int CODE_HEARTBEAT = 700;
    public static final int CODE_BIND_USER = 701;
    public static final int CODE_CHAT_MSG = 702;
    public static final int CODE_CHAT_ACK = 703;
    public static final int CODE_SESSION_INVALID = 704;

    private int code;
    private String msg;
    private T data;
    private String sessionId;

    public String toJsonString() {
        return JSON.toJSONString(this);
    }

    public static NetworkData<Object> bindNetworkMessage(String sessionId) {
        NetworkData<Object> bindNetworkMessage=new NetworkData<>();
        bindNetworkMessage.setCode(CODE_BIND_USER);
        bindNetworkMessage.setSessionId(sessionId);
        return bindNetworkMessage;
    }

    public static NetworkData<Object> heartbeatMessage() {
        NetworkData<Object> data=new NetworkData<>();
        data.code=CODE_HEARTBEAT;
        return data;
    }

    public static NetworkData<ChatMessage> formChatMessage(ChatMessage message,String sessionId) {
        NetworkData<ChatMessage> data=new NetworkData<>();
        data.code=CODE_CHAT_MSG;
        data.data=message;
        data.setSessionId(sessionId);
        return data;
    }

}