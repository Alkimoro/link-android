package cn.linked.baselib.socket;

import cn.linked.baselib.entity.ChatMessage;
import cn.linked.baselib.callback.IBooleanResultCallback;

interface IChatController {

    void sendChatMessage(in ChatMessage message, IBooleanResultCallback callback);

    void bindUser(in String sessionId);

}
