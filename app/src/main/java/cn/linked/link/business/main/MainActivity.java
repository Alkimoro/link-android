package cn.linked.link.business.main;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import cn.linked.baselib.BaseActivity;
import cn.linked.baselib.ui.dialog.MessageDialog;
import cn.linked.commonlib.viewmodel.UpdatePanelViewModel;
import cn.linked.link.NavigationPagerAdapter;
import cn.linked.link.R;
import cn.linked.link.databinding.LayoutMainBinding;
import cn.linked.router.api.Router;
import cn.linked.router.common.Route;

@Route(path = "app/mainActivity")
public class MainActivity extends BaseActivity {
    private long lastBackPressedTime=-1;
    private final long exitAppIntervalTime=1000;

    private Fragment mineModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLinkApplication().getChatManager().bindUser().catchError(error -> {
            MessageDialog dialog = new MessageDialog(this);
            dialog.setMessage("聊天服务启动失败，请重启应用或清空数据").setButton("确定", v -> {
                dialog.cancel();
            });
            return null;
        });
        LayoutMainBinding activityMainBinding= LayoutMainBinding.inflate(getLayoutInflater(),null,false);
        setContentView(activityMainBinding.getRoot());
        initPager();
    }

    private void initPager(){
        ViewPager pager=findViewById(R.id.pager);
        NavigationPagerAdapter navigationPagerAdapter=new NavigationPagerAdapter(getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        pager.setAdapter(navigationPagerAdapter);
        View.OnClickListener l=(v)->{
            switch (v.getId()){
                case R.id.navigation_item_0:
                    pager.setCurrentItem(0);
                    break;
                case R.id.navigation_item_1:
                    pager.setCurrentItem(1);
                    break;
                case R.id.navigation_item_2:
                    pager.setCurrentItem(2);
                    break;
            }
        };
        View item=findViewById(R.id.navigation_item_0);
        item.setOnClickListener(l);
        item=findViewById(R.id.navigation_item_1);
        item.setOnClickListener(l);
        item=findViewById(R.id.navigation_item_2);
        item.setOnClickListener(l);
        item=findViewById(R.id.navigation_item_3);
        item.setOnClickListener((v)->{
            if(getSupportFragmentManager().findFragmentByTag("mine/mineFragment")!=null) {return;}
            try {
                if(mineModule==null){
                    mineModule=(Fragment)Router.route("mine/mineFragment").newInstance();
                }
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.main_content,mineModule,"mine/mineFragment");
                transaction.addToBackStack(null);
                transaction.commit();
            } catch (IllegalAccessException|InstantiationException e) {
                Log.e("HomeActivity","MineFragment模块加载失败");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdatePanelViewModel.showUpdatePanel(this);
    }

    @Override
    public void onBackPressed() {
        if(!getSupportFragmentManager().isStateSaved()&&getSupportFragmentManager().popBackStackImmediate()) {
            return;
        }
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