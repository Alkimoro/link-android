package cn.linked.baselib;

import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

public class BaseFragment extends Fragment implements UIContext {

    public static final String TAG = "BaseFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "fragment on create. class:" + getClass());
        initTransition();
    }

    private void initTransition() {
        Transition transition = new Slide(Gravity.END);
        transition.setDuration(100);
        setEnterTransition(transition);

        transition = new Slide(Gravity.START);
        transition.setDuration(100);
        setExitTransition(transition);
    }

    @Override
    public LifecycleOwner getLifecycleOwner() {
        return this;
    }

}
