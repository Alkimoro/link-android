package cn.linked.baselib.socket;

import cn.linked.baselib.entity.ChatMessage;

interface IChatDispatcher {

    void deliverChatMessage(in ChatMessage message);

    void deliverChatAck(in ChatMessage ackMessage);

    void channelActive();

    void channelInactive();

}
