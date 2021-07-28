package cn.linked.baselib.socket;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.callback.IBooleanResultCallback;
import cn.linked.baselib.entity.ChatMessage;
import cn.linked.baselib.entity.NetworkData;
import cn.linked.router.api.Router;
import cn.linked.router.common.Route;
import lombok.Getter;

@Route(path = "_chat/chatService")
public class ChatService extends Service {

    public static final String TAG = "ChatService";

    @Getter
    private int onStartCommandNum = 0;

    @Getter
    private ChatClient chatClient;
    @Getter
    private IChatDispatcher chatDispatcher;
    @Getter
    private boolean isChatAppConnected;

    @Getter
    private IBinder chatController = new IChatController.Stub() {
        @Override
        public void sendChatMessage(ChatMessage message, IBooleanResultCallback callback) throws RemoteException {
            chatClient.sendNetworkData(NetworkData.formChatMessage(message, chatClient.getSessionId()))
                    .then(value -> {
                        try {
                            callback.callback(true);
                        }catch (Exception ignored) { }
                        return null;
                    }, error -> {
                        try {
                            callback.callback(false);
                        }catch (Exception ignored) { }
                        return null;
                    });
        }
        @Override
        public void bindUser(String sessionId) throws RemoteException {
            chatClient.setSessionId(sessionId);
        }
    };

    private ServiceConnection chatAppServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            chatDispatcher = IChatDispatcher.Stub.asInterface(service);
            isChatAppConnected = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            chatDispatcher = null;
            isChatAppConnected = false;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate. application class:" + getApplication().getClass());
        // 初始化聊天客户端 ChatClient
        chatClient = new ChatClient(this);
        chatClient.init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        chatClient.destroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStartCommandNum++;
        Class<?> chatAppService = Router.route("_chat/chatAppService");
        if(chatAppService != null) {
            Intent appServiceIntent = new Intent();
            appServiceIntent.setPackage("cn.linked.link");
            appServiceIntent.setClass(getApplicationContext(), chatAppService);
            // flags 0 不自动创建和管理service 通过start/stopService管理service
            // 或 {@link Context.BIND_ABOVE_CLIENT} 提升service的重要性
            bindService(appServiceIntent, chatAppServiceConnection, Context.BIND_AUTO_CREATE);
            Log.i("ChatService","Bind ChatAppService");
        }
        return START_NOT_STICKY;
        //return START_STICKY;
    }

    public LinkApplication getAppApplication() {
        return (LinkApplication) getApplication();
    }

    @Nullable@Override
    public IBinder onBind(Intent intent) {
        return chatController;
    }

}
