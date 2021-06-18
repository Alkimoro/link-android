package cn.linked.baselib.socket;

import cn.linked.baselib.entity.IChatMessage;
interface IChatController {

    int sendChatMessage(in ChatMessage message);

    int bindUser(in String sessionId);

}
