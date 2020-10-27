package cn.linked.commonlib.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableByte;
import androidx.databinding.ObservableField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cn.linked.commonlib.databinding.UpdatePanelLayoutBinding;

public class UpdatePanelViewModel {
    private UpdatePanelLayoutBinding binding;
    private UpdatePanelPopupWindow mWindow;
    // 目标升级版本（客户视角）
    private ObservableField<String> targetVersion=new ObservableField<>("加载中...");
    // 更新细节（多条需要换行）
    private ObservableField<String> updateDetail=new ObservableField<>("加载中...");
    // 升级包大小
    private ObservableField<String> apkSize=new ObservableField<>("加载中...");
    // 升级进度 0-100 %
    private ObservableByte updateProcess=new ObservableByte((byte)0);

    private Animation buttonDisappearAnimation;

    public static void showUpdatePanel(@NonNull Activity activity){
        if(Thread.currentThread() == Looper.getMainLooper().getThread()&&!activity.isFinishing()) {
            Handler handler=new Handler();
            handler.postDelayed(()->{
                UpdatePanelLayoutBinding bind = UpdatePanelLayoutBinding.inflate(activity.getLayoutInflater(), null, false);
                UpdatePanelViewModel model = new UpdatePanelViewModel();
                bind.setViewModel(model);

                model.mWindow = new UpdatePanelPopupWindow(activity);
                model.mWindow.setContentView(bind.getRoot());
                model.initialViewModel(bind);
                model.mWindow.showByAnimation(activity);
            },500);
        }else{
            Log.e("UpdatePanelViewModel","必须在主线程调用 showUpdatePanel 方法！或 检查该activity 是否已经 finished");
        }
    }

    public void initialViewModel(@NonNull UpdatePanelLayoutBinding binding){
        this.binding=binding;
        binding.getRoot().setOnClickListener((v)->{closeView();});
        binding.button.setOnClickListener((v)->{executeUpdate();});
        initialAnimation();
    }
    private void initialAnimation(){
        Animation temp=null;

        Animation rootDisappearAnimation=new AlphaAnimation(1,0);
        rootDisappearAnimation.setDuration(100);
        mWindow.addEndAnimation(rootDisappearAnimation,binding.getRoot().getId());

        Animation rootAppearAnimation=new AlphaAnimation(0,1);
        rootAppearAnimation.setDuration(200);
        mWindow.addStartAnimation(rootAppearAnimation,binding.getRoot().getId());

        AnimationSet appearAnimation=new AnimationSet(true);
        appearAnimation.setDuration(200);
        temp=new AlphaAnimation(0,1);
        appearAnimation.addAnimation(temp);
        temp=new ScaleAnimation(1.1f,1,1.1f,1,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        appearAnimation.addAnimation(temp);
        mWindow.addStartAnimation(appearAnimation,binding.detailPanel.getId());

        AnimationSet disappearAnimation=new AnimationSet(true);
        disappearAnimation.setDuration(100);
        temp=new ScaleAnimation(1,1.1f,1,1.1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        disappearAnimation.addAnimation(temp);
        temp=new AlphaAnimation(1,0);
        disappearAnimation.addAnimation(temp);
        mWindow.addEndAnimation(disappearAnimation,binding.detailPanel.getId());

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
        mWindow.setCanCloseTag(false);
        if(buttonDisappearAnimation!=null) {
            binding.button.startAnimation(buttonDisappearAnimation);
        }
    }

    private void closeView(){
        mWindow.dismiss();
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

    static class UpdatePanelPopupWindow extends PopupWindow{
        private boolean canCloseTag=true;
        private boolean isClosing=false;
        private Runnable forceCloseListener;
        private HashMap<View,Animation> startAnimations=new HashMap<>();
        private HashMap<View,Animation> endAnimations=new HashMap<>();
        public UpdatePanelPopupWindow(Context context){
            super(context);
            setFocusable(true);
            setClippingEnabled(false);
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            setBackgroundDrawable(null);
        }

        public void showByAnimation(Activity activity){
            showAtLocation(activity.findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
            for (View t : startAnimations.keySet()) {
                t.startAnimation(startAnimations.get(t));
            }
            startAnimations.clear();
        }

        public void addStartAnimation(Animation animation,int viewId){
            View temp;
            if(animation!=null&&getContentView()!=null&&(temp=getContentView().findViewById(viewId))!=null) {
                this.startAnimations.put(temp,animation);
            }
        }

        public void addEndAnimation(Animation animation,int viewId){
            View temp;
            if(animation!=null&&getContentView()!=null&&(temp=getContentView().findViewById(viewId))!=null) {
                this.endAnimations.put(temp,animation);
            }
        }

        public void setForceCloseListener(Runnable listener){
            forceCloseListener=listener;
        }

        public void setCanCloseTag(boolean canCloseTag){
            this.canCloseTag=canCloseTag;
        }

        private void destroyData(){
            startAnimations.clear();
            endAnimations.clear();
            startAnimations=null;
            endAnimations=null;
        }

        @Override
        public void dismiss() {
            if(isClosing){return;}
            if(canCloseTag) {
                isClosing=true;
                long maxDuration=10;
                for(View t : endAnimations.keySet()){
                    Animation animation=endAnimations.get(t);
                    maxDuration=Math.max(maxDuration,animation.getDuration());
                    t.startAnimation(animation);
                }
                destroyData();
                new Handler().postDelayed(super::dismiss,maxDuration);
            }else{
                if(forceCloseListener!=null){
                    forceCloseListener.run();
                }
            }
        }
    }
}
