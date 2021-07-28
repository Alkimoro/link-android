package cn.linked.baselib.repository.protocol;

import cn.linked.baselib.entity.HttpResult;
import cn.linked.baselib.entity.User;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserProtocol {

    @FormUrlEncoded
    @POST("link/userController/login")
    Call<HttpResult<User>> login(@Field("userId") Long userId, @Field("password") String password);

    @FormUrlEncoded
    @POST("link/userController/getUserById")
    Call<HttpResult<User>> getUserById(@Field("id") Long id);

}
