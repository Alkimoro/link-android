package cn.linked.home;

import android.os.Bundle;

import androidx.annotation.Nullable;

import cn.linked.baselib.BaseActivity;
import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.repository.entry.UserRepository;
import cn.linked.baselib.ui.dialog.MessageDialog;
import cn.linked.home.business.home.HomeFragment;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_DayNight_NoActionBar);
        setContentView(R.layout.activity_main);
        getLinkApplication().getAndCreateInstance(UserRepository.class).login(100001L, "758481");
        getLinkApplication().getChatManager().bindUser().catchError(error -> {
            MessageDialog dialog = new MessageDialog(this);
            dialog.setMessage("聊天服务启动失败，请重启应用或清空数据").setButton("确定", v -> {
                dialog.cancel();
            });
            return null;
        });
        getSupportFragmentManager().beginTransaction()
                .add(R.id.moduleFragmentContainer, HomeFragment.class, null)
                .commit();
    }

    @Override
    protected void restartApp() {
        LinkApplication application=(LinkApplication) getApplication();
        application.appStatus=LinkApplication.APP_STATUS_NORMAL;
    }
}
