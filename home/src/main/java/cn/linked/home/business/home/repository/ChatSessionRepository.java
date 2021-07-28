package cn.linked.home.business.home.repository;

import androidx.annotation.NonNull;

import java.util.List;

import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.repository.entry.BaseRepository;

public class ChatSessionRepository extends BaseRepository {

    // 用户最近信息活跃的 聊天groupId 缓存
    private final ChatSessionLruCache chatSessionLruCache;

    public ChatSessionRepository(@NonNull LinkApplication application) {
        chatSessionLruCache = new ChatSessionLruCache(application);
    }

    // 获取用户最近活跃的 聊天session（groupId）
    public List<String> getActiveChatSession() {
        return chatSessionLruCache.getChatSessionList();
    }

    public void visitActiveChatSession(String groupId) {
        chatSessionLruCache.visit(groupId);
    }

    public void addActiveChatSession(String groupId) {
        chatSessionLruCache.put(groupId);
    }

    public void asyncVisitActiveChatSession(String groupId) {
        postTask(() -> {
            visitActiveChatSession(groupId);
        });
    }

    public void asyncAddActiveChatSession(String groupId) {
        postTask(() -> {
            addActiveChatSession(groupId);
        });
    }

}
