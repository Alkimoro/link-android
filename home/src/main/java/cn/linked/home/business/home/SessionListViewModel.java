package cn.linked.home.business.home;

import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.common.ChatManager;
import cn.linked.baselib.entity.ChatGroup;
import cn.linked.baselib.entity.ChatGroupMember;
import cn.linked.baselib.entity.ChatGroupType;
import cn.linked.baselib.entity.ChatMessage;
import cn.linked.baselib.repository.entry.ChatRepository;
import cn.linked.baselib.repository.entry.UserRepository;
import cn.linked.baselib.ui.BaseViewDelegate;
import cn.linked.commonlib.promise.Promise;
import cn.linked.home.business.home.repository.ChatSessionRepository;
import lombok.Getter;

public class SessionListViewModel {

    private final ChatRepository chatRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final UserRepository userRepository;
    private final ChatManager chatManager;

    @Getter
    private final MutableLiveData<BaseViewDelegate.ViewDelegateHandler<ChatSessionRecyclerAdapter>> adapterHandler = new MutableLiveData<>();
    @Getter
    private final List<ChatSessionItem> chatSessionItemList = new ArrayList<>();
    /** 第一项固定为null 对应一个EmptyItemView 用于保证 insert move 动画正确 */
    { chatSessionItemList.add(null); }

    public SessionListViewModel() {
        chatRepository = LinkApplication.getInstance().getAndCreateInstance(ChatRepository.class);
        userRepository = LinkApplication.getInstance().getAndCreateInstance(UserRepository.class);
        chatManager = LinkApplication.getInstance().getChatManager();
        chatSessionRepository = new ChatSessionRepository(LinkApplication.getInstance());
        initChatSessionItem();
        initChatManagerListener();
    }

    private void initChatManagerListener() {
        chatManager.addChatMessageListener(message -> {
            ChatSessionItem temp = new ChatSessionItem();
            temp.setGroupId(message.getGroupId());
            int index = chatSessionItemList.indexOf(temp);
            ChatSessionItem item;
            if(index < 0) {
                item = new ChatSessionItem();
                item.setGroupId(message.getGroupId());
                refreshSessionItemTitleAndImg(item);
            }else {
                item = chatSessionItemList.get(index);
            }
            refreshSessionItemChatMessage(item, message, true);
        });
        chatManager.addChannelActiveStateListener(state -> {
            if(state == ChatManager.CHANNEL_ACTIVE_STATE_ACTIVE) {
                // 每次channel重新连接 获取下新消息
                chatRepository.getNewestChatMessageFromNetwork().then(chatMessageList -> {
                    for(ChatMessage message : chatMessageList) {
                        ChatSessionItem temp = new ChatSessionItem();
                        temp.setGroupId(message.getGroupId());
                        int index = chatSessionItemList.indexOf(temp);
                        ChatSessionItem item;
                        if(index < 0) {
                            item = new ChatSessionItem();
                            item.setGroupId(message.getGroupId());
                            refreshSessionItemTitleAndImg(item);
                        }else {
                            item = chatSessionItemList.get(index);
                        }
                        refreshSessionItemChatMessage(item, message, true);
                    }
                    return null;
                });
            }
        });
    }

    private void refreshSessionItemTitleAndImg(ChatSessionItem item) {
        String groupId = item.getGroupId();
        chatRepository.getChatGroupById(groupId, false)
                .then(value -> {
                    if(value.getType() == ChatGroupType.GROUP) {
                        item.setImageURL(value.getImageUri());
                        item.setTitle(value.getName());
                        adapterHandler.postValue(adapter -> {
                            adapter.notifyItemChanged(chatSessionItemList.indexOf(item));
                        });
                    }else {
                        chatRepository.getChatGroupMember(groupId, false)
                                .then(memberList -> {
                                    if(memberList != null && memberList.size() == 2) {
                                        ChatGroupMember member = null;
                                        if(LinkApplication.getInstance().getCurrentUser().getId().equals(memberList.get(0).getUserId())) {
                                            member = memberList.get(1);
                                        }else { member = memberList.get(0); }
                                        ChatGroupMember finalMember = member;
                                        if(member != null) {
                                            userRepository.getUserById(member.getUserId(), false)
                                                    .then(user -> {
                                                        if(finalMember.getAlias() == null) {
                                                            item.setTitle(user.getName());
                                                        }else {
                                                            item.setTitle(finalMember.getAlias());
                                                        }
                                                        item.setImageURL(user.getImageUrl());
                                                        adapterHandler.postValue(adapter -> {
                                                            adapter.notifyItemChanged(chatSessionItemList.indexOf(item));
                                                        });
                                                        return null;
                                                    });
                                        }
                                    }
                                    return null;
                                });
                    }
                    return null;
                });
    }

    private void refreshSessionItemChatMessage(ChatSessionItem item, ChatMessage message, boolean moveFirst) {
        if(item == null) { return; }
        if(item.getChatMessage() != null && item.getChatMessage().getSequenceNumber() >= message.getSequenceNumber()) { return; }
        chatRepository.getUserChatGroupMember(message.getGroupId(),
                LinkApplication.getInstance().getCurrentUser().getId(), false)
                .then(currentUserMember -> {
                    getGroupMemberAndAlias(message.getGroupId(), message.getOwner(), false)
                            .then(param -> {
                                synchronized (chatSessionItemList) {
                                    int itemIndex = chatSessionItemList.indexOf(item);
                                    item.setChatMessage(message);
                                    if(currentUserMember.getHaveReadMessageMaxSequenceNum() != null) {
                                        item.setNum(message.getSequenceNumber() - currentUserMember.getHaveReadMessageMaxSequenceNum());
                                    }else {
                                        item.setNum(message.getSequenceNumber());
                                    }
                                    // 当前用户的消息不需要显示名称或别名
                                    if(!message.getOwner().equals(LinkApplication.getInstance().getCurrentUser().getId())) {
                                        item.setPrefix((String) param[1]);
                                    }
                                    if (itemIndex < 0) {
                                        // 如果moveFirst为false 则不会再让这个SessionItem显示
                                        if(moveFirst) {
                                            chatSessionItemList.add(1, item);
                                            chatSessionRepository.asyncAddActiveChatSession(item.getGroupId());
                                            adapterHandler.postValue(adapter -> {
                                                adapter.notifyItemInserted(1);
                                            });
                                        }
                                    } else {
                                        if(moveFirst) {
                                            chatSessionItemList.remove(itemIndex);
                                            chatSessionItemList.add(1, item);
                                            chatSessionRepository.asyncAddActiveChatSession(item.getGroupId());
                                            adapterHandler.postValue(adapter -> {
                                                adapter.notifyItemMoved(itemIndex, 1);
                                                adapter.notifyItemChanged(1);
                                            });
                                        }else {
                                            adapterHandler.postValue(adapter -> {
                                                adapter.notifyItemChanged(itemIndex);
                                            });
                                        }
                                    }
                                }
                                return null;
                            });
                    return null;
                });
    }

    private Promise<Object[]> getGroupMemberAndAlias(String groupId, Long userId, boolean onlyLoadFromNetwork) {
        Promise<Object[]> promise = new Promise<>();
        chatRepository.getUserChatGroupMember(groupId, userId, onlyLoadFromNetwork)
                .then(member -> {
                    Object[] params = new Object[2];
                    params[0] = member;
                    if(member.getAlias() == null) {
                        userRepository.getUserById(userId, onlyLoadFromNetwork)
                                .then(user -> {
                                    params[1] = user.getName();
                                    promise.resolve(params);
                                    return null;
                                });
                    }else {
                        params[1] = member.getAlias();
                        promise.resolve(params);
                    }
                    return null;
                });
        return promise;
    }

    private void initChatSessionItem() {
        List<String> activeChatSessionList = chatSessionRepository.getActiveChatSession();
        for (int i = 0; i < activeChatSessionList.size(); i++) {
            String groupId = activeChatSessionList.get(i);
            final ChatSessionItem item = new ChatSessionItem();
            item.setGroupId(groupId);
            refreshSessionItemTitleAndImg(item);
            chatRepository.getChatMessageFromLocal(item.getGroupId(), Long.MAX_VALUE, 1)
                    .then(messageList -> {
                        ChatMessage message = messageList.get(0);
                        if(message != null) {
                            refreshSessionItemChatMessage(item, message, false);
                        }
                        return null;
                    });
            chatSessionItemList.add(item);
        }
        adapterHandler.setValue(RecyclerView.Adapter::notifyDataSetChanged);

        // 初始化后 获取最新消息并更新
        chatRepository.getNewestChatMessageFromNetwork().then(chatMessageList -> {
            return chatMessageList;
        }, error -> {
            return null;
        }).then(messageList -> {
            Map<String, ChatMessage> groupIdMap = new TreeMap<>();
            if(messageList != null) {
                for(ChatMessage message : messageList) {
                    groupIdMap.put(message.getGroupId(), message);
                }
            }
            for(ChatSessionItem item : chatSessionItemList) {
                if(item == null) { continue; } // 跳过第一项
                if(groupIdMap.containsKey(item.getGroupId())) {
                    refreshSessionItemChatMessage(item, groupIdMap.get(item.getGroupId()), false);
                    groupIdMap.remove(item.getGroupId());
                }else {
                    chatRepository.getChatMessage(item.getGroupId(), Long.MAX_VALUE, 1).
                            then(list -> {
                                if(list != null && list.size() == 1) {
                                    refreshSessionItemChatMessage(item, list.get(0), false);
                                }
                                return null;
                            });
                }
            }
            for(Map.Entry<String, ChatMessage> entry : groupIdMap.entrySet()) {
                ChatSessionItem item = new ChatSessionItem();
                item.setGroupId(entry.getKey());
                refreshSessionItemTitleAndImg(item);
                refreshSessionItemChatMessage(item, entry.getValue(), true);
            }
            return null;
        });
    }
}
