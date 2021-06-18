package cn.linked.baselib.common;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.entity.ChatMessage;
import cn.linked.baselib.repository.entry.ChatRepository;
import cn.linked.baselib.socket.ChatService;
import cn.linked.baselib.socket.ChatServiceException;
import cn.linked.baselib.socket.IChatController;
import cn.linked.commonlib.promise.Promise;

public class ChatManager {

    private ChatRepository chatRepository;

    private AtomicLong curAckId = new AtomicLong(0);

    private Context context;

    private IChatController chatController;

    private ExecutorService workers;
    private final int workerThreadNum = 5;

    private final Object lock = new Object();

    private Set<ChatMessageListener> chatMessageListenerSet = new HashSet<>();

    private ConcurrentHashMap<Long, Promise<ChatMessage>> noAckMessageMap = new ConcurrentHashMap<>();
    // 不需要线程安全 内部使用的时候 加了锁 已经保证了线程安全
    private HashSet<Long> waitSequenceNumSet = new HashSet<>();

    public ChatManager(@NonNull Context context) {
        this.context = context;
        chatRepository = getApplicationContext().getAndCreateInstance(ChatRepository.class);
        workers = Executors.newFixedThreadPool(workerThreadNum);
        Intent intent = new Intent();
        intent.setClass(context, ChatService.class);
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                chatController = IChatController.Stub.asInterface(service);
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                chatController = null;
            }
        },0);
    }

    public Promise<ChatMessage> sendChatMessage(String msg, String groupId) {
        Promise<ChatMessage> promise = new Promise<>();
        Runnable task = () -> {
            ChatMessage message = new ChatMessage();
            long tempAckId = curAckId.addAndGet(1);
            message.setAckId(tempAckId);
            message.setGroupId(groupId);
            message.setMessage(msg);
            // 等待连接上ChatService
            if(chatController == null) {
                synchronized (lock) {
                    try {
                        if(chatController == null) {
                            lock.wait();
                        }
                    } catch (InterruptedException ignored) { }
                }
            }
            try {
                int status = chatController.sendChatMessage(message);
                if(status == 0) {
                    noAckMessageMap.put(tempAckId, promise);
                }else {
                    promise.reject(new ChatServiceException());
                }
            } catch (RemoteException e) {
                promise.reject(e);
            }
        };
        workers.submit(task);
        return promise;
    }

    public void onChatMessageAck(ChatMessage ackMessage) {
        if(ackMessage !=null && ackMessage.getAckId() != null) {
            Promise<ChatMessage> promise = noAckMessageMap.get(ackMessage.getAckId());
            if(promise != null) {
                getApplicationContext().getCommonHandler().post(() -> {
                    promise.resolve(ackMessage);
                });
                noAckMessageMap.remove(ackMessage.getAckId());
                chatRepository.saveChatMessageToLocal(ackMessage);
            }
        }
    }

    public void onNetworkInactive() {

    }

    public interface ChatMessageListener {
        void onReceiveChatMessage(ChatMessage message);
    }

    public void onReceiveChatMessage(ChatMessage message) {
        if(message != null && message.getSequenceNumber() != null) {
            chatRepository.saveChatMessageToLocal(message);
            for (ChatMessageListener listener : chatMessageListenerSet) {
                listener.onReceiveChatMessage(message);
            }
        }
    }

    public void addChatMessageListener(@NonNull ChatMessageListener listener) {
        chatMessageListenerSet.add(listener);
    }

    public void removeChatMessageListener(@NonNull ChatMessageListener listener) {
        chatMessageListenerSet.remove(listener);
    }

    public Promise<Boolean> bindUser() {
        Promise<Boolean> promise = new Promise<>();
        Runnable task = () -> {
            try {
                int status = chatController.bindUser(getApplicationContext().getSessionId());
                if(status == 0) {
                    promise.resolve(true);
                }
            } catch (RemoteException ignored) { }
            promise.resolve(false);
        };
        workers.submit(task);
        return promise;
    }

    public LinkApplication getApplicationContext() {
        return (LinkApplication) context.getApplicationContext();
    }
}
