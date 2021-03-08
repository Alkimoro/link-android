package cn.linked.baselib.common;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.mtt.hippy.adapter.http.HippyHttpAdapter;
import com.tencent.mtt.hippy.adapter.http.HippyHttpRequest;
import com.tencent.mtt.hippy.adapter.http.HippyHttpResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AppHippyHttpAdapter implements HippyHttpAdapter {

    @Getter
    private OkHttpClient okHttpClient;

    public AppHippyHttpAdapter(Context context) {
        okHttpClient=new OkHttpClient.Builder()
                .cookieJar(new AppCookieJar(context))
                .build();
    }

    public AppCookieJar getCookieJar() {
        return (AppCookieJar) okHttpClient.cookieJar();
    }

    @Override
    public void sendRequest(HippyHttpRequest request, HttpTaskCallback callback) {
        if(callback==null) {return;}
        Request.Builder okHttpRequestBuilder=new Request.Builder();
        fillData(okHttpRequestBuilder,request);
        okHttpClient.newCall(okHttpRequestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onTaskFailed(request,e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                HippyHttpResponse hippyResponse=parseResponse(response);
                try {
                    callback.onTaskSuccess(request,hippyResponse);
                }catch (Exception e) {
                    callback.onTaskFailed(request,e);
                }finally {
                    response.close();
                    hippyResponse.close();
                }
            }
        });
    }

    private HippyHttpResponse parseResponse(Response response) {
        HippyHttpResponse result=new HippyHttpResponse();
        result.setInputStream(response.body().byteStream());
        result.setResponseMessage(response.message());
        result.setStatusCode(response.code());
        Headers headers=response.headers();
        Map<String, List<String>> headerMap=new HashMap<>();
        for(int i=0;i<headers.size();i++) {
            String name=headers.name(i);
            if(headerMap.containsKey(name)) {
                headerMap.get(name).add(headers.value(i));
            }else {
                List<String> list=new ArrayList<>();
                list.add(headers.value(i));
                headerMap.put(name,list);
            }
        }
        result.setRspHeaderMap(headerMap);
        return result;
    }

    private void fillData(Request.Builder builder,HippyHttpRequest request) {
        Map<String, Object> headerMap = request.getHeaders();
        builder.url(request.getUrl());
        String contentType="application/json";
        if (headerMap != null && !headerMap.isEmpty()) {
            Set<String> keySets = headerMap.keySet();
            for (String key : keySets) {
                Object obj = headerMap.get(key);
                boolean isContentType="CONTENT-TYPE".equals(((String) key).toUpperCase());
                if (obj instanceof String) {
                    builder.header(key,(String) obj);
                    if(isContentType) {
                        contentType=(String) obj;
                    }
                }
                else if (obj instanceof List) {
                    List<?> requestProperties=(List<?>) obj;
                    if (!requestProperties.isEmpty()) {
                        for (Object oneReqProp : requestProperties) {
                            String prop=(String) oneReqProp;
                            if (!TextUtils.isEmpty(prop)) {
                                builder.addHeader(key,prop);
                            }
                        }
                    }
                }
            }
        }
        if(request.getMethod()!=null&&!"GET".equals(request.getMethod().toUpperCase())) {
            builder.method(request.getMethod().toUpperCase(),RequestBody.create(MediaType.parse(contentType),request.getBody().getBytes()));
        }
    }

    @Override
    public void destroyIfNeed() {
        okHttpClient=null;
    }
}
