package cn.linked.baselib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import lombok.Getter;

public class BaseActivity extends AppCompatActivity implements UIContext {

    public static final String TAG = "BaseActivity";

    @Getter
    private static final ActivityManager activityManager = new ActivityManager();

    @Getter
    private ContentWrapperFragment contentWrapperFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Activity on create.class:" + getClass());
        activityManager.pushActivity(this);

        LinkApplication application = (LinkApplication) getApplication();
        switch(application.appStatus){
            case LinkApplication.APP_STATUS_FORCE_KILLED:
                restartApp();
                break;
        }

        setStatusBarAppearance();
        setNavigationBarAppearance();

        contentWrapperFragment = new ContentWrapperFragment();
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, contentWrapperFragment, "contentWrapperFragment").commit();
    }

    @Override
    public <T extends View> T findViewById(int id) {
        T v = super.findViewById(id);
        if(v != null) { return v; }
        return contentWrapperFragment.getLayoutView().findViewById(id);
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = getLayoutInflater().inflate(layoutResID, getContentView(), false);
        contentWrapperFragment.setLayoutView(view);
    }
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        view.setLayoutParams(params);
        contentWrapperFragment.setLayoutView(view);
    }
    @Override
    public void setContentView(View view) {
        contentWrapperFragment.setLayoutView(view);
    }

    protected void setStatusBarAppearance(){
        // 设置StatusBar颜色 以切合APP主题色
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            View decorView=getWindow().getDecorView();
            int systemUiVisibility=decorView.getSystemUiVisibility();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR|
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE|systemUiVisibility);
        }
    }

    protected void setNavigationBarAppearance(){
        // 设置NavigationBar颜色 以切合APP主题色
        getWindow().setNavigationBarColor(Color.WHITE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            View decorView=getWindow().getDecorView();
            int systemUiVisibility=decorView.getSystemUiVisibility();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|systemUiVisibility);
        }
    }

    protected void restartApp(){
        Log.i(TAG, "APP 重新启动 当前Activity：" + getClass().getName());
        getActivityManager().finishAllActivity(false);
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        startActivity(intent);
    }

    public ViewGroup getContentView() {
        return getWindow().getDecorView().findViewById(android.R.id.content);
    }

    public LinkApplication getLinkApplication() {
        return (LinkApplication) getApplication();
    }
 
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Activity on destroy.class:" + getClass());
        activityManager.removeActivity(this);
    }

    @Override
    public LifecycleOwner getLifecycleOwner() {
        return this;
    }

    @Override
    public Context getContext() {
        return this;
    }
}
