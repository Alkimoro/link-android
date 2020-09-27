package cn.linked.baselib;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

import cn.linked.commonlib.util.DensityUtil;

public class LinkApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("link application : init");
        // 初始化ARouter
        ARouterInit();
        //初始化DensityUtil
        DensityUtil.init(this.getResources().getDisplayMetrics());
    }
    private void ARouterInit(){
        if (BuildConfig.DEBUG) { //如果在debug模式下
            // 打印日志,默认关闭
            ARouter.openLog();
            // 开启调试模式，默认关闭(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            ARouter.openDebug();
            // 打印日志的时候打印线程堆栈
            ARouter.printStackTrace();
        }
        // 尽可能早，推荐在Application中初始化
        ARouter.init(this);
    }
}
