package cn.linked.baselib;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.tencent.mtt.hippy.HippyAPIProvider;
import com.tencent.mtt.hippy.HippyEngine;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import cn.linked.baselib.common.AppCookieJar;
import cn.linked.baselib.common.AppHippyEngineMonitorAdapter;
import cn.linked.baselib.common.AppHippyHttpAdapter;
import cn.linked.baselib.common.AppHippyImageLoader;
import cn.linked.baselib.common.ChatHippyAPIProvider;
import cn.linked.baselib.common.ChatManager;
import cn.linked.baselib.config.Properties;
import cn.linked.router.api.Router;
import lombok.Getter;
import okhttp3.OkHttpClient;

public class LinkApplication extends Application {

    public static final String TAG = "LinkApplication";
    private static LinkApplication INSTANCE;

    public static final int APP_STATUS_NORMAL = 1;
    public static final int APP_STATUS_FORCE_KILLED = -1;
    public int appStatus = APP_STATUS_FORCE_KILLED;

    /**
     *  剩余异步初始化模块的数量
     *      必须在所有模块异步初始化和设置listener之前调用oneStepStart()
     * */
    private AtomicInteger initStepCount = new AtomicInteger(0);

    @Getter
    private HippyEngine hippyEngine;

    @Getter
    private ChatManager chatManager;

    @Getter
    private LinkAppInitListener initListener;

    @Getter
    private Handler commonHandler;

    @Getter
    private OkHttpClient httpClient;

    @Getter
    private AppDatabase appDatabase;

    private boolean isNotifiedInitListener = false;
    private boolean initSuccess = true;

    // 保存各种全局实例（单例）
    private final ConcurrentHashMap<String, Object> instances = new ConcurrentHashMap<>();

    public static LinkApplication getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("LinkApplication","Application on create");
        appDatabase = Room.databaseBuilder(this, AppDatabase.class, Properties.APP_DATABASE_NAME).build();
        commonHandler = new Handler();
        chatManager = new ChatManager(this);
        // 初始化 HttpClient
        initHttpClient();
        // 初始化HippyEngine
        if(Properties.ENABLE_HIPPY_ENGINE) { initHippyEngine(); }
        // 初始化Router
        Router.initLoadLazy(this);
        // 启动Chat相关Service
        Intent serviceIntent = new Intent();
        serviceIntent.setClass(this, Router.route("_chat/chatService"));
        startService(serviceIntent);
        INSTANCE = this;
    }

    private void initHttpClient() {
        httpClient=new OkHttpClient.Builder()
                .cookieJar(new AppCookieJar(this))
                .build();
    }

    public <T> T getInstance(@NonNull String name, Class<T> clazz) {
        Object o = instances.get(name);
        if(o != null && o.getClass() == clazz) {
            return (T) o;
        }
        return null;
    }

    /**
     *  默认采用Context为参数的构造器
     * */
    public <T> T getAndCreateInstance(@NonNull Class<T> clazz) {
        synchronized (instances) {
            String key = clazz.getName();
            if(instances.containsKey(key)) {
                return (T) instances.get(key);
            }else {
                try {
                    Object o = clazz.getConstructor(LinkApplication.class).newInstance(this);
                    instances.put(key,o);
                    return (T) o;
                }catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    Log.e(TAG, "getAndCreateInstance param clazz has no such Constructor");
                    return null;
                }
            }
        }
    }

    public boolean addInstance(@NonNull String name,@NonNull Object o) {
        synchronized (instances) {
            if (instances.containsKey(name)) {
                Log.e(TAG, "addInstance param name is already exist");
                return false;
            } else {
                instances.put(name, o);
                return true;
            }
        }
    }

    private void initHippyEngine() {
        oneStepStart();
        HippyEngine.EngineInitParams params = new HippyEngine.EngineInitParams();
        params.debugMode = Properties.DEBUG;
        params.context = getApplicationContext();
        params.iEngineType = HippyEngine.EngineType.VUE;
        params.engineMonitor = new AppHippyEngineMonitorAdapter();
        // hippy图片加载器
        params.imageLoader = new AppHippyImageLoader();
        AppHippyHttpAdapter httpAdapter = new AppHippyHttpAdapter(httpClient);
        params.httpAdapter = httpAdapter;
        // vendor包 为业务js包共享的
        params.coreJSAssetsPath = getHippyJsAssetsPath("vendor.android.js");
        params.groupId = 1;
        List<HippyAPIProvider> providers = new ArrayList<>();
        providers.add(new ChatHippyAPIProvider(this));
        // 用来提供Native modules、JavaScript modules、View controllers的管理器 1个或多个
        params.providers = providers;
        hippyEngine = HippyEngine.create(params);
        hippyEngine.initEngine((code,msg) -> {
            if(code == HippyEngine.STATUS_OK) {
                finishOneInitStep(true);
            }else {
                Log.e("HippyEngine",msg);
                finishOneInitStep(false);
            }
        });
    }

    public void oneStepStart() {
        initStepCount.addAndGet(1);
        initSuccess = false;
    }

    public void finishOneInitStep(boolean success) {
        int result = 0;
        if(success) {
            result = initStepCount.addAndGet(-1);
            if(result == 0) {
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
                    LinkAppInitListener listener = initListener;
                    if(listener != null) {
                        commonHandler.post(() -> {listener.onInitialized(isInitSuccess);});
                    }
                    isNotifiedInitListener = true;
                    initSuccess = isInitSuccess;
                }
            }
        }
    }

    // 当 initStepCount 为 0 时会立即通知 listener
    public void setInitListener(@NonNull LinkAppInitListener listener) {
        synchronized (this) {
            initListener = listener;
            if(isNotifiedInitListener || initStepCount.get() == 0) {
                commonHandler.post(() -> {listener.onInitialized(initSuccess);});
            }
        }
    }

    public String getSessionId() {
        return ((AppCookieJar) httpClient.cookieJar()).getSessionId();
    }

    public boolean isSessionInvalid() {
        // todo Retrofit 访问服务端接口
        return false;
    }

    public interface LinkAppInitListener {
        void onInitialized(boolean success);
    }

    public static String getHippyJsAssetsPath(String componentName) {
        return Properties.HIPPY_JS_DIR + componentName+".android.js";
    }

}
