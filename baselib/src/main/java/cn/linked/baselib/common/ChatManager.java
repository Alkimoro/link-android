package cn.linked.baselib.common;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.callback.IBooleanResultCallback;
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
    private final int workerThreadNum = 3;

    private Set<ChatMessageListener> chatMessageListenerSet = new HashSet<>();

    private final Map<ChatMessage, Promise<ChatMessage>> waitSendChatMessageMap = new HashMap<>();

    private ConcurrentHashMap<Long, Promise<ChatMessage>> noAckMessageMap = new ConcurrentHashMap<>();
    // 不需要线程安全 内部使用的时候 加了锁 已经保证了线程安全
    private HashSet<Long> waitSequenceNumSet = new HashSet<>();

    public ChatManager(@NonNull LinkApplication context) {
        this.context = context;
        chatRepository = getApplicationContext().getAndCreateInstance(ChatRepository.class);
        workers = Executors.newFixedThreadPool(workerThreadNum);
        Intent intent = new Intent();
        intent.setClass(context, ChatService.class);
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                IChatController temp = IChatController.Stub.asInterface(service);
                synchronized (waitSendChatMessageMap) {
                    for(Map.Entry<ChatMessage, Promise<ChatMessage>> entry : waitSendChatMessageMap.entrySet()) {
                        sendChatMessageInternal(entry.getKey(), entry.getValue());
                    }
                    chatController = temp;
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
                synchronized (waitSendChatMessageMap) {
                    if(chatController == null) {
                        waitSendChatMessageMap.put(message, promise);
                        return;
                    }
                }
            }
            sendChatMessageInternal(message, promise);
        };
        workers.submit(task);
        return promise;
    }

    private void sendChatMessageInternal(ChatMessage message, Promise<ChatMessage> promise) {
        try {
            chatController.sendChatMessage(message, new IBooleanResultCallback.Stub() {
                @Override
                public void callback(boolean value) throws RemoteException {
                    if(value) {
                        noAckMessageMap.put(message.getAckId(), promise);
                    }else {
                        promise.reject(new ChatServiceException());
                    }
                }
            });
        } catch (RemoteException e) {
            promise.reject(e);
        }
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

    public static final int CHANNEL_ACTIVE_STATE_ACTIVE = 1;
    public static final int CHANNEL_ACTIVE_STATE_INACTIVE = 2;
    public interface ChannelActiveStateListener {
        void onChannelActiveStateChange(int currentState);
    }
    private Set<ChannelActiveStateListener> channelActiveStateListenerSet = new HashSet<>();
    // 被ChatAppService通知调用
    public void onChannelActive() {
        for(ChannelActiveStateListener listener : channelActiveStateListenerSet) {
            listener.onChannelActiveStateChange(CHANNEL_ACTIVE_STATE_ACTIVE);
        }
    }
    // 被ChatAppService通知调用
    public void onChannelInactive() {
        for(ChannelActiveStateListener listener : channelActiveStateListenerSet) {
            listener.onChannelActiveStateChange(CHANNEL_ACTIVE_STATE_INACTIVE);
        }
    }
    public void addChannelActiveStateListener(@NonNull ChannelActiveStateListener listener) {
        channelActiveStateListenerSet.add(listener);
    }
    public void removeChannelActiveStateListener(@NonNull ChannelActiveStateListener listener) {
        channelActiveStateListenerSet.remove(listener);
    }

    public interface ChatMessageListener {
        void onReceiveChatMessage(ChatMessage message);
    }

    public void onReceiveChatMessage(ChatMessage message) {
        if(message != null && message.getSequenceNumber() != null) {
            // 保存 message
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

    public Promise<Void> bindUser() {
        Promise<Void> promise = new Promise<>();
        Runnable task = () -> {
            try {
                chatController.bindUser(getApplicationContext().getSessionId());
                promise.resolve(null);
            } catch (RemoteException e) {
                promise.reject(null);
            }
        };
        workers.submit(task);
        return promise;
    }

    public LinkApplication getApplicationContext() {
        return (LinkApplication) context.getApplicationContext();
    }
}
