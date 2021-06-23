package cn.linked.baselib;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

public class BaseFragment extends Fragment implements UIContext {

    @Override
    public LifecycleOwner getLifecycleOwner() {
        return this;
    }

}
