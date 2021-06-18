package cn.linked.baselib.common;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.linked.baselib.entity.AppCookie;
import lombok.Getter;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class AppCookieJar implements CookieJar {

    public static final String fileName = "cookies";
    public static final String sessionName = "SESSION";

    @Getter
    private SharedPreferences store;

    public AppCookieJar(@NonNull Context context) {
        store=context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        SharedPreferences.Editor editor=store.edit();
        Set<String> stringSet=new HashSet<>();
        boolean isSaveSession=false;
        for(Cookie cookie:cookies) {
            stringSet.add(cookieToString(cookie));
            if(cookie.name().equals(sessionName)&&!isSaveSession) {
                isSaveSession=true;
                editor.putString(sessionName,cookie.value());
            }
        }
        editor.putStringSet(url.toString(),stringSet).apply();
    }

    private String cookieToString(Cookie cookie) {
        return String.format("{name:%s,value:%s,expiresAt:%s,domain:%s,path:%s,secure:%s,httpOnly:%s}",
                cookie.name(),cookie.value(),cookie.expiresAt(),cookie.domain(),cookie.path(),cookie.secure(),cookie.httpOnly());
    }

    public String getSessionId() {
        return store.getString(sessionName,null);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> result=new ArrayList<>();
        Set<String> cookieStringSet=store.getStringSet(url.toString(),null);
        if(cookieStringSet!=null) {
            Iterator<String> it=cookieStringSet.iterator();
            while (it.hasNext()) {
                AppCookie appCookie=JSON.parseObject(it.next(),AppCookie.class);
                Cookie cookie=appCookie.toOkHttpCookie();
                if(cookie!=null) {
                    result.add(cookie);
                }
            }
        }
        return result;
    }
}
