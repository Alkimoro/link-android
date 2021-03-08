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

import cn.linked.baselib.entity.ChatMessage;
import cn.linked.baselib.entity.NetworkData;
import cn.linked.router.api.Router;
import cn.linked.router.common.Route;
import io.netty.channel.Channel;
import lombok.Getter;

@Route(path = "_chat/chatService")
public class ChatService extends Service {

    @Getter
    private boolean DEBUG=false;

    @Getter
    private int onStartCommandNum=0;

    @Getter
    private ChatClient chatClient;
    @Getter
    private IChatDispatcher chatDispatcher;
    @Getter
    private boolean isChatAppConnected;

    @Getter
    private IBinder chatController=new IChatController.Stub() {
        @Override
        public int sendChatMessage(ChatMessage message) throws RemoteException {
            Channel chatChannel=chatClient.getChatChannel();
            if(chatChannel!=null&&chatChannel.isWritable()) {
                chatChannel.writeAndFlush(NetworkData.formChatMessage(message,chatClient.getSessionId()).toJsonString());
                return 0;
            }
            return -1;
        }
        @Override
        public int bindUser(long userId,String sessionId) throws RemoteException {
            chatClient.setUserId(userId);
            return chatClient.setSessionId(sessionId);
        }
    };

    private ServiceConnection chatAppServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            chatDispatcher=IChatDispatcher.Stub.asInterface(service);
            isChatAppConnected=true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            chatDispatcher=null;
            isChatAppConnected=false;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化聊天客户端 ChatClient
        chatClient=new ChatClient(this);
        chatClient.init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStartCommandNum++;
        if(onStartCommandNum==1) {
            DEBUG=intent.getBooleanExtra("DEBUG",false);
        }
        Class<?> chatAppService=Router.route("_chat/chatAppService");
        if(chatAppService!=null) {
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

    @Nullable@Override
    public IBinder onBind(Intent intent) {
        return chatController;
    }

}
