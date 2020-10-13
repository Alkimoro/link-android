package cn.linked.baselib;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinkApplication application=(LinkApplication) getApplication();
        switch(application.appStatus){
            case LinkApplication.APP_STATUS_FORCE_KILLED:
                restartApp();
                break;
        }

        setStatusBarAppearance();
        setNavigationBarAppearance();
    }

    protected void setStatusBarAppearance(){
        // 设置StatusBar颜色 以切合APP主题色
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            View decorView=getWindow().getDecorView();
            int systemUiVisibility=decorView.getSystemUiVisibility();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR|systemUiVisibility);
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
        Log.i("Activity","APP 重新启动 当前Activity："+getClass().getName());
        Intent intent=getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        startActivity(intent);
        finish();
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
    }
}
