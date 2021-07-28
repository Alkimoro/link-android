package cn.linked.baselib.repository.entry;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.common.AppNetwork;
import cn.linked.baselib.entity.ChatGroup;
import cn.linked.baselib.entity.ChatGroupMember;
import cn.linked.baselib.entity.ChatMessage;
import cn.linked.baselib.entity.HttpResult;
import cn.linked.baselib.repository.RetrofitManager;
import cn.linked.baselib.repository.dao.ChatDao;
import cn.linked.baselib.repository.protocol.ChatProtocol;
import cn.linked.commonlib.promise.Promise;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository extends BaseRepository {

    public static final String TAG = "ChatRepository";

    private final ChatDao chatDao;
    private final OkHttpClient httpClient;

    public ChatRepository(@NonNull LinkApplication application) {
        chatDao = application.getAppDatabase().chatDao();
        httpClient = application.getHttpClient();
    }

    public void saveChatMessageToLocal(@NonNull ChatMessage chatMessage) {
        postTask(() -> {chatDao.insertChatMessage(chatMessage);});
    }

    public Promise<ChatGroupMember> getUserChatGroupMember(@NonNull String groupId, @NonNull Long userId, boolean onlyLoadFromNetwork) {
        Promise<ChatGroupMember> promise = new Promise<>();
        postTask(() -> {
            if (!onlyLoadFromNetwork) {
                ChatGroupMember chatGroupMember = chatDao.findUserChatGroupMember(groupId, userId);
                if (chatGroupMember != null) {
                    promise.resolve(chatGroupMember);
                    return;
                }
            }
            if (AppNetwork.isNetworkConnected()) {
                RetrofitManager.getService(ChatProtocol.class).getUserChatGroupMember(groupId, userId)
                        .enqueue(new Callback<HttpResult<ChatGroupMember>>() {
                            @Override
                            public void onResponse(Call<HttpResult<ChatGroupMember>> call, Response<HttpResult<ChatGroupMember>> response) {
                                HttpResult<ChatGroupMember> httpResult = response.body();
                                if (httpResult != null && httpResult.getCode() == HttpResult.CODE_SUCCESS) {
                                    chatDao.upsertChatGroupMember(httpResult.getData());
                                    promise.resolve(httpResult.getData());
                                } else {
                                    promise.reject(new Exception("server response data error"));
                                }
                            }
                            @Override
                            public void onFailure(Call<HttpResult<ChatGroupMember>> call, Throwable t) {
                                promise.reject(new Exception("network request error"));
                            }
                        });
            } else {
                promise.reject(new Exception("network is disconnected"));
            }
        }, promise);
        return promise;
    }

    public Promise<List<ChatGroupMember>> getChatGroupMember(@NonNull String groupId, boolean onlyLoadFromNetwork) {
        Promise<List<ChatGroupMember>> promise = new Promise<>();
        postTask(() -> {
            if (!onlyLoadFromNetwork) {
                ChatGroup chatGroup = chatDao.findChatGroupById(groupId);
                if(chatGroup != null && chatGroup.getType().isFixedCount()) {
                    List<ChatGroupMember> chatGroupMemberList = chatDao.findChatGroupMemberByGroupId(groupId);
                    if(chatGroupMemberList != null) {
                        if(chatGroup.getType().getCount() == chatGroupMemberList.size()) {
                            promise.resolve(chatGroupMemberList); return;
                        }
                    }
                }
            }
            if (AppNetwork.isNetworkConnected()) {
                RetrofitManager.getService(ChatProtocol.class).getChatGroupMember(groupId)
                        .enqueue(new Callback<HttpResult<List<ChatGroupMember>>>() {
                            @Override
                            public void onResponse(Call<HttpResult<List<ChatGroupMember>>> call, Response<HttpResult<List<ChatGroupMember>>> response) {
                                HttpResult<List<ChatGroupMember>> httpResult = response.body();
                                if (httpResult != null && httpResult.getCode() == HttpResult.CODE_SUCCESS) {
                                    chatDao.upsertChatGroupMember(httpResult.getData());
                                    promise.resolve(httpResult.getData());
                                } else {
                                    promise.reject(new Exception("server response data error"));
                                }
                            }
                            @Override
                            public void onFailure(Call<HttpResult<List<ChatGroupMember>>> call, Throwable t) {
                                promise.reject(new Exception("network request error"));
                            }
                        });
            } else {
                promise.reject(new Exception("network is disconnected"));
            }
        }, promise);
        return promise;
    }

    public void setUserHaveReadMessageMaxSequenceNum(String groupId, Long maxSequenceNum) {
        postTask(() -> {
            Long userId = LinkApplication.getInstance().getCurrentUser().getId();
            chatDao.setUserHaveReadMessageMaxSequenceNum(groupId, userId, maxSequenceNum);
            if(AppNetwork.isNetworkConnected()) {
                RetrofitManager.getService(ChatProtocol.class)
                        .setUserHaveReadMessageMaxSequenceNum(groupId, maxSequenceNum)
                        .enqueue(new Callback<HttpResult<Boolean>>() {
                            @Override
                            public void onResponse(Call<HttpResult<Boolean>> call, Response<HttpResult<Boolean>> response) {
                                HttpResult<Boolean> httpResult = response.body();
                                if(httpResult != null && httpResult.getCode() == HttpResult.CODE_SUCCESS) {
                                    Log.i(TAG, "setUserHaveReadMessageMaxSequenceNum on network success");
                                }else {
                                    Log.i(TAG, "setUserHaveReadMessageMaxSequenceNum on network failed");
                                }
                            }
                            @Override
                            public void onFailure(Call<HttpResult<Boolean>> call, Throwable t) {
                                Log.i(TAG, "setUserHaveReadMessageMaxSequenceNum on network failed");
                            }
                        });
            }
        });
    }

    // 获取ChatGroup信息
    public Promise<ChatGroup> getChatGroupById(String groupId, boolean onlyLoadFromNetwork) {
        Promise<ChatGroup> promise = new Promise<>();
        postTask(() -> {
            if (groupId == null) {
                promise.reject(new NullPointerException("param groupId is null"));
                return;
            }
            if (!onlyLoadFromNetwork) {
                ChatGroup chatGroup = chatDao.findChatGroupById(groupId);
                if (chatGroup != null) {
                    promise.resolve(chatGroup);
                    return;
                }
            }
            if (AppNetwork.isNetworkConnected()) {
                RetrofitManager.getService(ChatProtocol.class)
                        .getChatGroupById(groupId).enqueue(new Callback<HttpResult<ChatGroup>>() {
                    @Override
                    public void onResponse(Call<HttpResult<ChatGroup>> call, Response<HttpResult<ChatGroup>> response) {
                        HttpResult<ChatGroup> httpResult = response.body();
                        if (httpResult != null && httpResult.getCode() == HttpResult.CODE_SUCCESS) {
                            chatDao.upsertChatGroup(httpResult.getData());
                            promise.resolve(httpResult.getData());
                        } else {
                            promise.reject(new Exception("server response data error"));
                        }
                    }
                    @Override
                    public void onFailure(Call<HttpResult<ChatGroup>> call, Throwable t) {
                        promise.reject(new Exception("network request error"));
                    }
                });
            } else {
                promise.reject(new Exception("network is disconnected"));
            }
        }, promise);
        return promise;
    }

    public Promise<List<ChatMessage>> getNewestChatMessageFromNetwork() {
        Promise<List<ChatMessage>> promise = new Promise<>();
        if(AppNetwork.isNetworkConnected()) {
            postTask(() -> {
                RetrofitManager.getService(ChatProtocol.class)
                        .getUserNewestChatMessage()
                        .enqueue(new Callback<HttpResult<List<ChatMessage>>>() {
                    @Override
                    public void onResponse(Call<HttpResult<List<ChatMessage>>> call,
                                           Response<HttpResult<List<ChatMessage>>> response) {
                        HttpResult<List<ChatMessage>> httpResult = response.body();
                        if(httpResult != null && httpResult.getCode() == HttpResult.CODE_SUCCESS) {
                            chatDao.insertChatMessage(httpResult.getData());
                            promise.resolve(httpResult.getData());
                        }else {
                            promise.reject(new Exception("server response data error"));
                        }
                    }
                    @Override
                    public void onFailure(Call<HttpResult<List<ChatMessage>>> call, Throwable t) {
                        promise.reject(t);
                    }
                });
            }, promise);
        }else {
            promise.reject(new Exception("network is disconnected"));
        }
        return promise;
    }

    public Promise<List<ChatMessage>> getChatMessageFromLocal(@NonNull String groupId, final Long maxSequenceNumber, @NonNull Integer num) {
        Promise<List<ChatMessage>> promise = new Promise<>();
        postTask(() -> {
            Long maxSequenceNum = maxSequenceNumber;
            if (maxSequenceNum == null || maxSequenceNum < 0) {
                maxSequenceNum = Long.MAX_VALUE;
            }
            if(num >= 0) {
                promise.resolve(chatDao.findChatMessage(groupId, maxSequenceNum, num));
            }else {
                promise.resolve(chatDao.findChatMessage(groupId, maxSequenceNum));
            }
        }, promise);
        return promise;
    }

    /**
     *  从本地数据库和网络获取 ChatMessage
     *      如果  本地数据库获取的数据条数 != num
     *              本地数据库获取的数据最大的 SequenceNumber != maxSequenceNumber (当 maxSequenceNumber 不为 Long.MAX_VALUE时)
     *      将会从网络加载数据 并 缓存到本地数据库
     *      如果  maxSequenceNumber == Long.MAX_VALUE 将会直接从网络拉取数据
     *      num < 0 默认拉取 1 ~ maxSequenceNumber的所有数据
     * */
    public Promise<List<ChatMessage>> getChatMessage(@NonNull String groupId, final Long maxSequenceNumber, @NonNull Integer num) {
        Promise<List<ChatMessage>> promise = new Promise<>();
        postTask(() -> {
            Long maxSequenceNum = maxSequenceNumber;
            if (maxSequenceNum == null || maxSequenceNum < 0) {
                maxSequenceNum = Long.MAX_VALUE;
            }
            boolean needRequestNetwork = false;
            final List<ChatMessage> list;
            if (maxSequenceNum != Long.MAX_VALUE) {
                if(num >= 0) { list = chatDao.findChatMessage(groupId, maxSequenceNum, num); }
                else { list = chatDao.findChatMessage(groupId, maxSequenceNum); }
                if(num >= 0) {
                    if (list.size() != Math.min(num, maxSequenceNum)
                            || !maxSequenceNum.equals(list.get(list.size() - 1).getSequenceNumber())) {
                        needRequestNetwork = true;
                    }
                }else {
                    if(list.size() == maxSequenceNum) {
                        needRequestNetwork = true;
                    }
                }
            } else {
                needRequestNetwork = true;
                if(num >= 0) { list = chatDao.findChatMessage(groupId, maxSequenceNum, num);
                }else { list = chatDao.findChatMessage(groupId, maxSequenceNum); }
            }
            if (needRequestNetwork && AppNetwork.isNetworkConnected()) {
                RetrofitManager.getService(ChatProtocol.class)
                        .getChatMessage(groupId, maxSequenceNum, num).enqueue(new Callback<HttpResult<List<ChatMessage>>>() {
                    @Override
                    public void onResponse(Call<HttpResult<List<ChatMessage>>> call, Response<HttpResult<List<ChatMessage>>> response) {
                        HttpResult<List<ChatMessage>> httpResult = response.body();
                        if (httpResult != null && httpResult.getCode() == HttpResult.CODE_SUCCESS) {
                            promise.resolve(httpResult.getData());
                            chatDao.insertChatMessage(httpResult.getData());
                        } else {
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
            } else {
                promise.resolve(list);
            }
        }, promise);
        return promise;
    }

}
