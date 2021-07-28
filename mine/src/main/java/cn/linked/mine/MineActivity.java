package cn.linked.mine;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.linked.baselib.BaseActivity;
import cn.linked.baselib.LinkApplication;
import cn.linked.mine.business.mine.MineFragment;

public class MineActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_DayNight_NoActionBar);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.moduleFragmentContainer, MineFragment.class, null)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("onSave");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
    }

    @Override
    protected void restartApp() {
        LinkApplication application=(LinkApplication) getApplication();
        application.appStatus=LinkApplication.APP_STATUS_NORMAL;
    }

}
