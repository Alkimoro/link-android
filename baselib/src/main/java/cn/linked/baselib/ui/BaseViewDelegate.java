package cn.linked.baselib.ui;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import cn.linked.baselib.UIContext;
import lombok.Getter;

public abstract class BaseViewDelegate {

    @Getter
    private UIContext uiContext;
    private final List<LiveData<?>> liveDataList = new ArrayList<>();
    private final List<BaseViewDelegate> childViewDelegateList = new ArrayList<>();

    public BaseViewDelegate(@NonNull UIContext uiContext) {
        this.uiContext = uiContext;
        this.uiContext.getLifecycleOwner().getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if(event == Lifecycle.Event.ON_CREATE) {
                init();
            }else if(event == Lifecycle.Event.ON_DESTROY) {
                destroyInternal();
            }
        });
    }

    @MainThread
    public abstract void init();

    @MainThread
    public abstract void destroy();

    @MainThread
    public abstract ViewGroup getRootView();

    public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
        return uiContext.getLayoutInflater().inflate(resource, root, attachToRoot);
    }

    public View inflate(int resource, ViewGroup root) {
        return uiContext.getLayoutInflater().inflate(resource, root);
    }

    @MainThread
    public void destroyInternal() {
        for(int i = 0;i < liveDataList.size();i++) {
            liveDataList.get(i).removeObservers(uiContext.getLifecycleOwner());
        }
        liveDataList.clear();
        for(int i = 0;i < childViewDelegateList.size();i++) {
            childViewDelegateList.get(i).destroy();
        }
        childViewDelegateList.clear();
        uiContext = null;
        destroy();
    }

    public <T> void addAndObserve(@NonNull LiveData<T> liveData,@NonNull Observer<? super T> observer) {
        liveDataList.add(liveData);
        liveData.observe(uiContext.getLifecycleOwner(), observer);
    }

    public void addChildViewDelegate(@NonNull BaseViewDelegate viewDelegate) {
        childViewDelegateList.add(viewDelegate);
    }

}
