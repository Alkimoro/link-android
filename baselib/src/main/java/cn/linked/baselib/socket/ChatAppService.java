package cn.linked.baselib.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.common.ChatManager;
import cn.linked.baselib.entity.ChatMessage;
import cn.linked.router.common.Route;
import lombok.Getter;

@Route(path = "_chat/chatAppService")
public class ChatAppService extends Service {

    public static final String TAG = "ChatAppService";

    @Getter
    private IBinder chatDispatcher=new IChatDispatcher.Stub() {
        @Override
        public void deliverChatMessage(ChatMessage message) throws RemoteException {
            ChatManager manager = application().getChatManager();
            manager.onReceiveChatMessage(message);
        }
        @Override
        public void deliverChatAck(ChatMessage ackMessage) throws RemoteException {
            application().getChatManager().onChatMessageAck(ackMessage);
        }
        @Override
        public void channelActive() throws RemoteException {
            Log.i(TAG, "channelActive");
            application().getChatManager().onChannelActive();
        }
        @Override
        public void channelInactive() throws RemoteException {
            Log.i(TAG, "channelInactive");
            application().getChatManager().onChannelInactive();
        }
    };

    @Nullable@Override
    public IBinder onBind(Intent intent) {
        return chatDispatcher;
    }

    private LinkApplication application() {
        return (LinkApplication) getApplication();
    }

}

