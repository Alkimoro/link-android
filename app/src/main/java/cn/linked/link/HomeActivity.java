package cn.linked.link;

import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;

import cn.linked.baselib.BaseActivity;
import cn.linked.commonlib.viewmodel.UpdatePanelViewModel;

public class HomeActivity extends BaseActivity {
    private long lastBackPressedTime=-1;
    private final long exitAppIntervalTime=500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdatePanelViewModel.showUpdatePanel(this);
    }

    @Override
    public void onBackPressed() {
        // 系统开机到当前的时间总数。它包括了系统深度睡眠的时间
        long time=SystemClock.elapsedRealtime();
        if(time-lastBackPressedTime<=exitAppIntervalTime){
            finish();
        }else{
            Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT).show();
        }
        lastBackPressedTime=time;
    }
}