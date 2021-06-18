package cn.linked.baselib.repository.entry;

import androidx.annotation.NonNull;

import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.config.Properties;
import cn.linked.baselib.entity.HttpResult;
import cn.linked.baselib.entity.User;
import cn.linked.baselib.repository.protocol.UserProtocol;
import cn.linked.commonlib.promise.Promise;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserRepository {

    public static final String TAG = "UserRepository";

    private final OkHttpClient httpClient;

    public UserRepository(@NonNull LinkApplication application) {
        httpClient = application.getHttpClient();
    }

    public Promise<User> login(@NonNull Long userId,@NonNull String password) {
        Promise<User> promise = new Promise<>();
        new Retrofit.Builder().baseUrl(Properties.getBaseURL()).client(httpClient).build().create(UserProtocol.class)
                .login(userId, password).enqueue(new Callback<HttpResult<User>>() {
            @Override
            public void onResponse(Call<HttpResult<User>> call, Response<HttpResult<User>> response) {
                HttpResult<User> result = response.body();
                if(result != null && result.getCode() != HttpResult.CODE_SUCCESS) {
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

}
