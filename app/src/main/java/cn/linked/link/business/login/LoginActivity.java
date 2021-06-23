package cn.linked.link.business.login;

import android.os.Bundle;

import androidx.annotation.Nullable;

import cn.linked.baselib.BaseActivity;
import cn.linked.router.common.Route;

@Route(path = "app/loginActivity")
public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new LoginViewDelegate(this, getContentView(), new LoginViewModel()).getRootView());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
