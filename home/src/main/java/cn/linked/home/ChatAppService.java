package cn.linked.home;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.modules.HippyModuleManager;

import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.common.ChatHippyJsModule;
import cn.linked.baselib.common.ChatHippyModule;
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
            HippyMap params=new HippyMap();
            HippyModuleManager manager=application().getHippyEngine().getEngineContext().getModuleManager();
            manager.getNativeModule(ChatHippyModule.class).saveChatMessage(message);
            manager.getJavaScriptModule(ChatHippyJsModule.class).onReceiveChatMessage(params);
        }
        @Override
        public void deliverChatAck(ChatMessage ackMessage) throws RemoteException {
            application().getHippyEngine().getEngineContext().getModuleManager()
                    .getNativeModule(ChatHippyModule.class).chatMessageAck(ackMessage);
        }
        @Override
        public void networkInactive() throws RemoteException {
            application().getHippyEngine().getEngineContext().getModuleManager()
                    .getJavaScriptModule(ChatHippyJsModule.class).onNetworkInactive();
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
