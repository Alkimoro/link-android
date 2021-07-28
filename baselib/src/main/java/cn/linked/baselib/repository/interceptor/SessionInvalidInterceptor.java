package cn.linked.baselib.repository.interceptor;

import android.content.Intent;

import com.alibaba.fastjson.JSON;

import java.io.IOException;

import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.entity.HttpResult;
import cn.linked.router.api.Router;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SessionInvalidInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if(response.body() != null && MediaType.get("application/json").equals(response.body().contentType())) {
            String responseBody = response.body().string();
            HttpResult<?> httpResult = JSON.parseObject(responseBody, HttpResult.class);
            if(httpResult != null && httpResult.getCode() == HttpResult.CODE_SESSION_INVALID) {
                redirect();
            }else {
                response = response.newBuilder()
                        .body(ResponseBody.create(response.body().contentType(), responseBody))
                        .build();
            }
        }
        return response;
    }

    private void redirect() {
        LinkApplication.getInstance().getCommonHandler().post(() ->{
            Class<?> clazz =  Router.route("app/loginActivity");
            if(clazz != null) {
                LinkApplication.getActivityManager().finishAllActivity(false);
                Intent intent = new Intent(LinkApplication.getInstance(), clazz);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                LinkApplication.getInstance().startActivity(intent);
            }
        });
    }

}
