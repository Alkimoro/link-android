package cn.linked.commonlib.viewmodel;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableByte;
import androidx.databinding.ObservableField;

import cn.linked.commonlib.databinding.UpdatePanelLayoutBinding;

public class UpdatePanelViewModel {
    private UpdatePanelLayoutBinding binding;
    private PopupWindow mWindow;
    private boolean canClose=true;
    // 目标升级版本（客户视角）
    private ObservableField<String> targetVersion=new ObservableField<>("加载中...");
    // 更新细节（多条需要换行）
    private ObservableField<String> updateDetail=new ObservableField<>("加载中...");
    // 升级包大小
    private ObservableField<String> apkSize=new ObservableField<>("加载中...");
    // 升级进度 0-100 %
    private ObservableByte updateProcess=new ObservableByte((byte)0);

    private AnimationSet appearAnimation;
    private AnimationSet disappearAnimation;
    private Animation buttonDisappearAnimation;
    private Animation rootDisappearAnimation;
    private Animation rootAppearAnimation;

    public static void showUpdatePanel(@NonNull Activity activity){
        UpdatePanelLayoutBinding bind=UpdatePanelLayoutBinding.inflate(activity.getLayoutInflater(),null,false);
        UpdatePanelViewModel model=new UpdatePanelViewModel();
        model.initialViewModel(bind);
        bind.setViewModel(model);
        model.mWindow=new PopupWindow(activity);
        model.mWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        model.mWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        model.mWindow.setContentView(bind.getRoot());
        model.mWindow.setFocusable(true);
        model.mWindow.setBackgroundDrawable(null);
        model.mWindow.showAtLocation(activity.findViewById(android.R.id.content), Gravity.CENTER,0,0);
        model.appear();
    }

    public void initialViewModel(@NonNull UpdatePanelLayoutBinding binding){
        this.binding=binding;
        binding.getRoot().setOnClickListener((v)->{closeView();});
        binding.button.setOnClickListener((v)->{executeUpdate();});
        initialAnimation();
    }
    private void initialAnimation(){
        Animation temp=null;

        rootDisappearAnimation=new AlphaAnimation(1,0);
        rootDisappearAnimation.setDuration(100);

        rootAppearAnimation=new AlphaAnimation(0,1);
        rootAppearAnimation.setDuration(200);

        appearAnimation=new AnimationSet(true);
        appearAnimation.setDuration(200);
        temp=new AlphaAnimation(0,1);
        appearAnimation.addAnimation(temp);
        temp=new ScaleAnimation(1.1f,1,1.1f,1,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        appearAnimation.addAnimation(temp);

        disappearAnimation=new AnimationSet(true);
        disappearAnimation.setDuration(100);
        temp=new ScaleAnimation(1,1.1f,1,1.1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        disappearAnimation.addAnimation(temp);
        temp=new AlphaAnimation(1,0);
        disappearAnimation.addAnimation(temp);
        disappearAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }
            @Override
            public void onAnimationEnd(Animation animation) {
                binding.getRoot().post(()->{mWindow.dismiss();});
            }
            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        buttonDisappearAnimation=new AlphaAnimation(1,0);
        buttonDisappearAnimation.setDuration(300);
        buttonDisappearAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }
            @Override
            public void onAnimationEnd(Animation animation) {
                binding.button.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
    }

    private void executeUpdate(){
        setCanClose(false);
        binding.button.startAnimation(buttonDisappearAnimation);
    }

    private void closeView(){
        if(canClose) {
            canClose=false;
            binding.detailPanel.startAnimation(disappearAnimation);
            binding.blurView.startAnimation(rootDisappearAnimation);
        }
    }

    private void appear(){
        binding.detailPanel.startAnimation(appearAnimation);
        binding.blurView.startAnimation(rootAppearAnimation);
    }

    public void setCanClose(boolean canClose) {
        this.canClose = canClose;
    }

    public ObservableField<String> getTargetVersion(){
        return targetVersion;
    }
    public ObservableField<String> getUpdateDetail(){
        return updateDetail;
    }
    public ObservableField<String> getApkSize(){
        return apkSize;
    }
    public ObservableByte getUpdateProcess(){
        return updateProcess;
    }
}
