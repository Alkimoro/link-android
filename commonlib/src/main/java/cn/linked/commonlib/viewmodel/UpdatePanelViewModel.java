package cn.linked.commonlib.viewmodel;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableByte;
import androidx.databinding.ObservableField;

import cn.linked.commonlib.databinding.UpdatePanelLayoutBinding;
import cn.linked.commonlib.util.DensityUtil;

public class UpdatePanelViewModel {
    private UpdatePanelLayoutBinding binding;
    private boolean canClose=true;
    // 目标升级版本（客户视角）
    private ObservableField<String> targetVersion=new ObservableField<>("加载中...");
    // 更新细节（多条需要换行）
    private ObservableField<String> updateDetail=new ObservableField<>("加载中...");
    // 升级包大小
    private ObservableField<String> apkSize=new ObservableField<>("加载中...");
    // 升级进度 0-100 %
    private ObservableByte updateProcess=new ObservableByte((byte)0);

    private Animation appearAnimation;
    private Animation disappearAnimation;

    public static void showUpdatePanel(@NonNull LayoutInflater inflater,@NonNull ViewGroup container){
        UpdatePanelLayoutBinding bind=UpdatePanelLayoutBinding.inflate(inflater,container,false);
        UpdatePanelViewModel model=new UpdatePanelViewModel();
        model.initialViewModel(bind);
        bind.setViewModel(model);
        model.appear(container);
    }

    public void initialViewModel(@NonNull UpdatePanelLayoutBinding binding){
        this.binding=binding;
        binding.getRoot().setOnClickListener((v)->{closeView();});
        initialAnimation();
    }
    private void closeView(){
        if(canClose) {
            canClose=false;
            binding.detailPanel.startAnimation(disappearAnimation);
        }
    }
    private void initialAnimation(){
        int distance= DensityUtil.dp2px(360);
        appearAnimation=new TranslateAnimation(distance,0,0,0);
        appearAnimation.setDuration(300);
        disappearAnimation=new TranslateAnimation(0,distance,0,0);
        disappearAnimation.setDuration(300);
        disappearAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }
            @Override
            public void onAnimationEnd(Animation animation) {
                ((ViewGroup) binding.getRoot().getParent()).removeView(binding.getRoot());
            }
            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
    }
    private void appear(ViewGroup container){
        container.addView(binding.getRoot());
        binding.detailPanel.startAnimation(appearAnimation);
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
