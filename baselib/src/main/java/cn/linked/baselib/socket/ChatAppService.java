package cn.linked.baselib.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.common.ChatManager;
import cn.linked.baselib.entity.ChatMessage;
import cn.linked.baselib.socket.IChatDispatcher;
import cn.linked.router.common.Route;
import lombok.Getter;

@Route(path = "_chat/chatAppService")
public class ChatAppService extends Service {

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
        public void networkInactive() throws RemoteException {
            application().getChatManager().onNetworkInactive();
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

