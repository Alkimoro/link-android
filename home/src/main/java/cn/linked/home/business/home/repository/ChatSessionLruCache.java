package cn.linked.home.business.home.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.LruCache;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.linked.baselib.LinkApplication;

public class ChatSessionLruCache {

    public static String CACHE_FILE_NAME_PREFIX = "chatSessionLruCache";
    public static int CACHE_MAX_SIZE = 1024;

    private SharedPreferences sharedPreferences;
    private LruCache<String, Object> lruCache;

    public ChatSessionLruCache(@NonNull LinkApplication application) {
        Long userId = application.getCurrentUser().getId();
        sharedPreferences = application.getSharedPreferences(CACHE_FILE_NAME_PREFIX + userId, Context.MODE_PRIVATE);
        lruCache = new LruCache<>(CACHE_MAX_SIZE);
        Map<String, ?> map = sharedPreferences.getAll();
        ArrayList<Map.Entry<String, ?>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, (a, b) -> {
            return (Integer) a.getValue() - (Integer) b.getValue();
        });
        for(Map.Entry<String, ?> entry : list) {
            lruCache.put(entry.getKey(), entry.getKey());
        }
    }

    public List<String> getChatSessionList() {
        Map<String, Object> map = lruCache.snapshot();
        List<String> result = new ArrayList<>(map.size());
        result.addAll(map.keySet());
        Collections.reverse(result);
        return result;
    }

    private void saveToDisk() {
        int i = 0;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for(Map.Entry<String, Object> entry : lruCache.snapshot().entrySet()) {
            // i 越大 则 key越是最近访问的
            editor.putInt(entry.getKey(), i);
            i++;
        }
        editor.apply();
    }

    public void visit(String key) {
        lruCache.get(key);
        saveToDisk();
    }

    public void put(String key) {
        lruCache.put(key, key);
        saveToDisk();
    }

}
