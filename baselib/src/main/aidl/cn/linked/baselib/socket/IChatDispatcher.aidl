package cn.linked.baselib.socket;

import cn.linked.baselib.entity.IChatMessage;
interface IChatDispatcher {

    void deliverChatMessage(in ChatMessage message);
    void deliverChatAck(in ChatMessage ackMessage);
    void networkInactive();

}
