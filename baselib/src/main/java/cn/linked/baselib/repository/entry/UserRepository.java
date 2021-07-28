package cn.linked.baselib.repository.entry;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;

import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.common.AppNetwork;
import cn.linked.baselib.entity.HttpResult;
import cn.linked.baselib.entity.User;
import cn.linked.baselib.repository.RetrofitManager;
import cn.linked.baselib.repository.dao.UserDao;
import cn.linked.baselib.repository.protocol.UserProtocol;
import cn.linked.commonlib.promise.Promise;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository extends BaseRepository {

    public static final String TAG = "UserRepository";
    public static final String CURRENT_USER_SP_NAME = "currentUserSpName";
    public static final String CURRENT_USER_SP_KEY = "currentUserSpKey";

    private final OkHttpClient httpClient;
    private final UserDao userDao;
    // 用于存储当前登录的用户信息
    private final SharedPreferences currentUserSp;

    public UserRepository(@NonNull LinkApplication application) {
        httpClient = application.getHttpClient();
        userDao = application.getAppDatabase().userDao();
        currentUserSp = application.getSharedPreferences(CURRENT_USER_SP_NAME, Context.MODE_PRIVATE);
    }

    public Promise<User> login(@NonNull Long userId,@NonNull String password) {
        Promise<User> promise = new Promise<>();
        RetrofitManager.getService(UserProtocol.class)
                .login(userId, password).enqueue(new Callback<HttpResult<User>>() {
            @Override
            public void onResponse(Call<HttpResult<User>> call, Response<HttpResult<User>> response) {
                HttpResult<User> result = response.body();
                if(result != null && result.getCode() == HttpResult.CODE_SUCCESS) {
                    saveUser(result.getData());
                    userDao.upsertUser(result.getData());
                    promise.resolve(result.getData());
                }else {
                    String msg = result == null? null : result.getMsg();
                    promise.reject(new Exception(msg));
                }
            }
            @Override
            public void onFailure(Call<HttpResult<User>> call, Throwable t) {
                promise.reject(t);
            }
        });
        return promise;
    }

    public Promise<User> getUserById(Long id, boolean onlyLoadFromNetwork) {
        Promise<User> promise = new Promise<>();
        postTask(() -> {
            if (id == null) {
                promise.reject(new NullPointerException());
                return;
            }
            if (!onlyLoadFromNetwork) {
                User user = userDao.findUserById(id);
                if (user != null) {
                    promise.resolve(user);
                    return;
                }
            }
            if (AppNetwork.isNetworkConnected()) {
                RetrofitManager.getService(UserProtocol.class)
                        .getUserById(id).enqueue(new Callback<HttpResult<User>>() {
                    @Override
                    public void onResponse(Call<HttpResult<User>> call, Response<HttpResult<User>> response) {
                        HttpResult<User> httpResult = response.body();
                        if (httpResult != null && httpResult.getCode() == HttpResult.CODE_SUCCESS) {
                            userDao.upsertUser(httpResult.getData());
                            promise.resolve(httpResult.getData());
                        } else {
                            promise.reject(new Exception("server response data error"));
                        }
                    }

                    @Override
                    public void onFailure(Call<HttpResult<User>> call, Throwable t) {
                        promise.reject(new Exception("network request error"));
                    }
                });
            } else {
                promise.reject(new Exception("network is disconnected"));
            }
        }, promise);
        return promise;
    }

    public User getCurrentUser() {
        String userJson = currentUserSp.getString(CURRENT_USER_SP_KEY, null);
        if(userJson == null) {
            return null;
        }
        return JSON.parseObject(userJson, User.class);
    }

    private void saveUser(User user) {
        if(user != null) {
            currentUserSp.edit().putString(CURRENT_USER_SP_KEY, JSON.toJSONString(user)).apply();
        }
    }

}
