package cn.linked.baselib.callback;

import cn.linked.baselib.entity.ChatMessage;

interface IChatMessageResultCallback {

    void callback(in ChatMessage message);

}
