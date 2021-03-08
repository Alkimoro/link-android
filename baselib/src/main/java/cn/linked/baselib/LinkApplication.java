package cn.linked.baselib;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tencent.mtt.hippy.HippyAPIProvider;
import com.tencent.mtt.hippy.HippyEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cn.linked.baselib.common.AppCookieJar;
import cn.linked.baselib.common.AppHippyEngineMonitorAdapter;
import cn.linked.baselib.common.AppHippyHttpAdapter;
import cn.linked.baselib.common.AppHippyImageLoader;
import cn.linked.baselib.common.ChatHippyAPIProvider;
import cn.linked.router.api.Router;
import lombok.Getter;

public class LinkApplication extends Application {

    public static final String HIPPY_JS_DIR="hippyJsSource"+ File.pathSeparator;

    public static final boolean DEBUG=true;

    public static final int APP_STATUS_NORMAL=1;
    public static final int APP_STATUS_FORCE_KILLED=-1;
    public int appStatus=APP_STATUS_FORCE_KILLED;

    private AtomicInteger initStepCount=new AtomicInteger(1);

    @Getter
    private HippyEngine hippyEngine;

    @Getter
    private LinkAppInitListener initListener;

    @Getter
    private Handler commonHandler;

    @Getter
    private AppCookieJar cookieJar;

    private boolean isNotifiedInitListener=false;
    private boolean initSuccess=false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("LinkApplication","Application on create");
        commonHandler=new Handler();
        // 初始化HippyEngine
        initHippyEngine();
        // 初始化Router
        Router.initLoadLazy(this);
        // 启动Chat相关Service
        Intent serviceIntent=new Intent();
        serviceIntent.putExtra("DEBUG",LinkApplication.DEBUG);
        serviceIntent.setClass(this, Router.route("_chat/chatService"));
        startService(serviceIntent);
    }

    private void initHippyEngine() {
        HippyEngine.EngineInitParams params=new HippyEngine.EngineInitParams();
        params.debugMode=DEBUG;
        params.context=getApplicationContext();
        params.iEngineType= HippyEngine.EngineType.VUE;
        params.engineMonitor=new AppHippyEngineMonitorAdapter();
        // hippy图片加载器
        params.imageLoader=new AppHippyImageLoader();
        AppHippyHttpAdapter httpAdapter=new AppHippyHttpAdapter(this);
        params.httpAdapter=httpAdapter;
        // vendor包 为业务js包共享的
        params.coreJSAssetsPath=getHippyJsAssetsPath("vendor.android.js");
        cookieJar=httpAdapter.getCookieJar();
        params.groupId=1;
        List<HippyAPIProvider> providers = new ArrayList<>();
        providers.add(new ChatHippyAPIProvider(this));
        // 用来提供Native modules、JavaScript modules、View controllers的管理器 1个或多个
        params.providers=providers;
        hippyEngine=HippyEngine.create(params);
        hippyEngine.initEngine((code,msg)->{
            if(code==HippyEngine.STATUS_OK) {
                finishOneInitStep(true);
            }else {
                Log.e("HippyEngine",msg);
                finishOneInitStep(false);
            }
        });
    }

    public void finishOneInitStep(boolean success) {
        int result=0;
        if(success) {
            result = initStepCount.addAndGet(-1);
            if(result==0) {
                notifyInitListener(true);
            }
        }else {
            notifyInitListener(false);
        }
    }

    private void notifyInitListener(boolean isInitSuccess) {
        if(!isNotifiedInitListener) {
            synchronized (this) {
                if(!isNotifiedInitListener) {
                    LinkAppInitListener listener=initListener;
                    if(listener!=null) {
                        commonHandler.post(() -> {listener.onInitialized(isInitSuccess);});
                    }
                    isNotifiedInitListener=true;
                    initSuccess=isInitSuccess;
                }
            }
        }
    }

    public void setInitListener(@NonNull LinkAppInitListener listener) {
        synchronized (this) {
            initListener=listener;
            if(isNotifiedInitListener) {
                commonHandler.post(() -> {listener.onInitialized(initSuccess);});
            }
        }
    }

    public String getSessionId() {
        return cookieJar.getSessionId();
    }

    public boolean isSessionInvalid() {
        // todo Retrofit 访问服务端接口
        return false;
    }

    public static interface LinkAppInitListener {
        void onInitialized(boolean success);
    }

    public static String getHippyJsAssetsPath(String componentName) {
        return LinkApplication.HIPPY_JS_DIR+componentName+".android.js";
    }

}
