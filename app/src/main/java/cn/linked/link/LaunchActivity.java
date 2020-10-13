package cn.linked.link;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.linked.baselib.BaseActivity;
import cn.linked.baselib.LinkApplication;
import cn.linked.commonlib.util.common.LoopHandler;
import cn.linked.router.common.Route;

@Route(path = "app/launchActivity")
public class LaunchActivity extends BaseActivity {
    // 最长展示的时间 以秒为单位
    private long totalShowTime=3;
    private LoopHandler timerHandler;
    private Runnable timerMsg= this::redirect;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch);
        timerHandler=new LoopHandler((int)totalShowTime,1000,0);
        timerHandler.postDelayed(timerMsg,totalShowTime*1000);
        TextView skipButton=findViewById(R.id.skipButton);
        skipButton.setOnClickListener((e)->{skip();});
        timerHandler.loopPost(()->{
            int time=timerHandler.getNum()-timerHandler.getCurrentNum()-1;
            skipButton.setText("跳过"+time);
        });
    }

    @Override
    protected void setStatusBarAppearance() {
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void redirect(){
        Intent intent=new Intent(getApplication(),HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void skip(){
        timerHandler.removeCallbacks(timerMsg);
        redirect();
    }

    private void initData(){

    }

    @Override
    protected void restartApp() {
        LinkApplication application=(LinkApplication) getApplication();
        application.appStatus=LinkApplication.APP_STATUS_NORMAL;
    }
}

