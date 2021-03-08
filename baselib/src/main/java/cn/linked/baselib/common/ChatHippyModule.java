package cn.linked.baselib.common;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.annotation.HippyMethod;
import com.tencent.mtt.hippy.annotation.HippyNativeModule;
import com.tencent.mtt.hippy.modules.Promise;
import com.tencent.mtt.hippy.modules.nativemodules.HippyNativeModuleBase;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.entity.ChatMessage;
import cn.linked.baselib.socket.ChatService;
import cn.linked.baselib.socket.IChatController;

@HippyNativeModule(name = "ChatHippyModule",thread = HippyNativeModule.Thread.BRIDGE)
public class ChatHippyModule extends HippyNativeModuleBase {

    public static final String LAST_SEQUENCE_NUM_NAME="lastSequence";

    private Long userId;

    private AtomicLong curAckId=new AtomicLong(0);

    private Context context;

    private final SharedPreferences chatInternalDataStore;

    private IChatController chatController;

    private ExecutorService workers;
    private final int workerThreadNum=5;

    private final Object lock=new Object();

    private ConcurrentHashMap<Long,Promise> waitResolveMap=new ConcurrentHashMap<>();
    // 不需要线程安全 内部使用的时候 加了锁 已经保证了线程安全
    private HashSet<Long> waitSequenceNumSet=new HashSet<>();

    public ChatHippyModule(HippyEngineContext hippyContext,@NonNull Context context) {
        super(hippyContext);
        this.context=context;
        chatInternalDataStore=context.getSharedPreferences("chatInternalData",Context.MODE_PRIVATE);
        workers=Executors.newFixedThreadPool(workerThreadNum);
        Intent intent=new Intent();
        intent.setClass(context,ChatService.class);
        context.bindService(intent,new ServiceConnection(){
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                chatController=IChatController.Stub.asInterface(service);
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                chatController=null;
            }
        },0);
    }

    @HippyMethod(name = "sendChatMessage")
    public void sendChatMessage(String msg,long groupId,Promise promise) {
        if(userId!=null) {
            ChatMessage message = new ChatMessage();
            long tempAckId = curAckId.addAndGet(1);
            message.setAckId(tempAckId);
            message.setOwner(userId);
            message.setGroupId(groupId);
            message.setMessage(msg);
            workers.submit(()->{
                if(chatController==null) {
                    synchronized (lock) {
                        try {
                            if(chatController==null) {
                                lock.wait();
                            }
                        } catch (InterruptedException e) { }
                    }
                }
                try {
                    int status=chatController.sendChatMessage(message);
                    if(status==0) {
                        waitResolveMap.put(tempAckId,promise);
                    }else {
                        promise.reject(-1);
                    }
                } catch (RemoteException e) {
                    promise.reject(-2);
                }
            });
        }else {
            promise.reject(1);
        }
    }

    public void chatMessageAck(ChatMessage ackMessage) {
        if(ackMessage!=null&&ackMessage.getAckId()!=null) {
            Promise promise=waitResolveMap.get(ackMessage.getAckId());
            if(promise!=null) {
                promise.resolve(0);
                saveChatMessage(ackMessage);
            }
            waitResolveMap.remove(ackMessage.getAckId());
        }
    }

    public void saveChatMessage(ChatMessage message) {
        if(message!=null&&message.getSequenceNumber()!=null) {
            Long newSequenceNumber = message.getSequenceNumber();
            // todo 存储至本地数据库

            // todo 更新LAST_SEQUENCE_NUM
            synchronized (chatInternalDataStore) {
                long lastSequenceNumber = chatInternalDataStore.getLong(LAST_SEQUENCE_NUM_NAME, 0L);
                if(lastSequenceNumber==newSequenceNumber-1) {
                    while(waitSequenceNumSet.contains(newSequenceNumber+1)) {
                        newSequenceNumber++;
                        waitSequenceNumSet.remove(newSequenceNumber);
                    }
                    chatInternalDataStore.edit().putLong(LAST_SEQUENCE_NUM_NAME,newSequenceNumber).apply();
                }else {
                    waitSequenceNumSet.add(newSequenceNumber);
                }
            }
        }
    }

    @HippyMethod(name = "getLastSequenceNum")
    public void getLastSequenceNum(Promise promise) {
        if(promise!=null) {
            long lastSequenceNumber = chatInternalDataStore.getLong(LAST_SEQUENCE_NUM_NAME, 0L);
            promise.resolve(lastSequenceNumber);
        }
    }

    @HippyMethod(name = "bindUser")
    public void bindUser(long user,Promise promise) {
        workers.submit(()->{
            try {
                int status=chatController.bindUser(user,getApplicationContext().getSessionId());
                if(status==0) {
                    userId=user;
                    promise.resolve(0);
                }else {
                    promise.reject(-1);
                }
            } catch (RemoteException e) {
                promise.reject(-2);
            }
        });
    }

    public LinkApplication getApplicationContext() {
        return (LinkApplication) context.getApplicationContext();
    }

}
