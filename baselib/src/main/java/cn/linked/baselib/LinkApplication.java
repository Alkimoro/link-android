package cn.linked.baselib;

import android.app.Application;
import android.util.Log;

import cn.linked.commonlib.util.DensityUtil;
import cn.linked.router.api.Router;

public class LinkApplication extends Application {

    public static final int APP_STATUS_NORMAL=1;
    public static final int APP_STATUS_FORCE_KILLED=-1;
    public int appStatus=APP_STATUS_FORCE_KILLED;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("LinkApplication","Application on create!");
        // 初始化DensityUtil
        DensityUtil.init(this.getResources().getDisplayMetrics());
        // 初始化Router
        Router.init(this);
    }
}
