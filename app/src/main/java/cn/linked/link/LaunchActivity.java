package cn.linked.link;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.linked.baselib.BaseActivity;
import cn.linked.baselib.LinkApplication;
import cn.linked.commonlib.util.common.LoopHandler;
import cn.linked.link.business.main.MainActivity;
import cn.linked.link.business.login.LoginActivity;
import cn.linked.link.databinding.LayoutLaunchBinding;
import cn.linked.router.common.Route;

@Route(path = "app/launchActivity")
public class LaunchActivity extends BaseActivity {
    // 最长展示的时间 以秒为单位
    private long totalShowTime = 3;
    private LoopHandler timerHandler;
    private Runnable timerMsg= this::skip;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivityManager().getActivityCount() > 1) {
            finish();
        }
        LayoutLaunchBinding launchBinding=LayoutLaunchBinding.inflate(getLayoutInflater(),null,false);
        launchBinding.setM(getResources().getDisplayMetrics());
        setContentView(launchBinding.getRoot());

        timerHandler=new LoopHandler((int)totalShowTime,1000,0);
        timerHandler.postDelayed(timerMsg,totalShowTime*1000);
        TextView skipButton=findViewById(R.id.skipButton);
        skipButton.setOnClickListener((e)->{skip();});
        timerHandler.loopPost(()->{
            int time=timerHandler.getNum()-timerHandler.getCurrentNum() - 1;
            skipButton.setText("跳过"+time);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void setStatusBarAppearance() {
        super.setStatusBarAppearance();
        View decorView=getWindow().getDecorView();
        int systemUiVisibility=decorView.getSystemUiVisibility();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|systemUiVisibility);
    }

    private void redirect(){
        Class<?> target= MainActivity.class;
        if(getLinkApplication().getSessionId()==null) {
            target= LoginActivity.class;
        }
        Intent intent=new Intent(getApplication(),target);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void skip(){
        timerHandler.removeCallbacks(timerMsg);
        getLinkApplication().setInitListener((success -> {if(success){redirect();}}));
    }

    private void initData(){

    }

    @Override
    protected void restartApp() {
        LinkApplication application=(LinkApplication) getApplication();
        application.appStatus=LinkApplication.APP_STATUS_NORMAL;
    }
}

