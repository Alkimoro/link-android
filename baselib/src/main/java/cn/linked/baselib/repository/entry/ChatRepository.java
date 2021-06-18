package cn.linked.baselib.repository.entry;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.common.AppNetwork;
import cn.linked.baselib.config.Properties;
import cn.linked.baselib.entity.ChatMessage;
import cn.linked.baselib.entity.HttpResult;
import cn.linked.baselib.repository.dao.ChatDao;
import cn.linked.baselib.repository.protocol.ChatProtocol;
import cn.linked.commonlib.promise.Promise;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatRepository {

    public static final String TAG = "ChatRepository";

    private final ChatDao chatDao;
    private final OkHttpClient httpClient;

    public ChatRepository(@NonNull LinkApplication application) {
        chatDao = application.getAppDatabase().chatDao();
        httpClient = application.getHttpClient();
    }

    public void saveChatMessageToLocal(@NonNull ChatMessage chatMessage) {
        chatDao.insertChatMessage(chatMessage);
    }

    /**
     *  从本地数据库和网络获取 ChatMessage
     *      如果  本地数据库获取的数据条数 != num
     *              本地数据库获取的数据最大的 SequenceNumber != maxSequenceNumber (当 maxSequenceNumber 不为 Long.MAX_VALUE时)
     *      将会从网络加载数据 并 缓存到本地数据库
     *      如果  maxSequenceNumber == Long.MAX_VALUE 将会直接从网络拉取数据
     * */
    public Promise<List<ChatMessage>> getChatMessage(@NonNull String groupId, Long maxSequenceNumber, @NonNull Integer num) {
        Promise<List<ChatMessage>> promise = new Promise<>();
        if(maxSequenceNumber == null || maxSequenceNumber < 0) {
            maxSequenceNumber = Long.MAX_VALUE;
        }
        boolean needRequestNetwork = false;
        final List<ChatMessage> list;
        if(maxSequenceNumber != Long.MAX_VALUE) {
            list = chatDao.findChatMessage(groupId, maxSequenceNumber, num);
            if (list.size() != Math.min(num, maxSequenceNumber)
                    || !maxSequenceNumber.equals(list.get(list.size() - 1).getSequenceNumber())) {
                needRequestNetwork = true;
            }
        }else {
            needRequestNetwork = true;
            list = new ArrayList<>();
        }
        if(needRequestNetwork && (AppNetwork.isNetworkConnected() || Properties.DEBUG)) {
            new Retrofit.Builder().baseUrl(Properties.getBaseURL()).client(httpClient).build().create(ChatProtocol.class)
                    .getChatMessage(groupId, maxSequenceNumber, num).enqueue(new Callback<HttpResult<List<ChatMessage>>>() {
                @Override
                public void onResponse(Call<HttpResult<List<ChatMessage>>> call, Response<HttpResult<List<ChatMessage>>> response) {
                    HttpResult<List<ChatMessage>> httpResult = response.body();
                    if(httpResult != null && httpResult.getCode() == HttpResult.CODE_SUCCESS) {
                        promise.resolve(httpResult.getData());
                        chatDao.insertChatMessage(httpResult.getData());
                    }else {
                        Log.e(TAG, "getChatMessage not return all message because server internal error");
                        promise.resolve(list);
                    }
                }
                @Override
                public void onFailure(Call<HttpResult<List<ChatMessage>>> call, Throwable t) {
                    Log.e(TAG, "getChatMessage not return all message because network error");
                    promise.resolve(list);
                }
            });
        }else {
            promise.resolve(list);
        }
        return promise;
    }

}
