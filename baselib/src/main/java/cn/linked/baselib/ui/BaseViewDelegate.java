package cn.linked.baselib.ui;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseViewDelegate {

    private LifecycleOwner lifecycleOwner;
    private final List<LiveData<?>> liveDataList = new ArrayList<>();
    private final List<BaseViewDelegate> childViewDelegateList = new ArrayList<>();

    public BaseViewDelegate(@NonNull LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        this.lifecycleOwner.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if(event == Lifecycle.Event.ON_CREATE) {
                init();
            }else if(event == Lifecycle.Event.ON_DESTROY) {
                destroy();
            }
        });
    }

    @MainThread
    protected abstract void init();

    @MainThread
    public void destroy() {
        for(int i = 0;i < liveDataList.size();i++) {
            liveDataList.get(i).removeObservers(lifecycleOwner);
        }
        liveDataList.clear();
        for(int i = 0;i < childViewDelegateList.size();i++) {
            childViewDelegateList.get(i).destroy();
        }
        childViewDelegateList.clear();
        lifecycleOwner = null;
    }

    public <T> void addAndObserve(@NonNull LiveData<T> liveData,@NonNull Observer<? super T> observer) {
        liveDataList.add(liveData);
        liveData.observe(lifecycleOwner, observer);
    }

    public void addChildViewDelegate(@NonNull BaseViewDelegate viewDelegate) {
        childViewDelegateList.add(viewDelegate);
    }

}
