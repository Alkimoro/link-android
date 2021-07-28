package cn.linked.mine.business.mine;

import android.content.Context;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.linked.baselib.BaseFragment;
import cn.linked.router.common.Route;

@Route(path = "mine/mineFragment")
public class MineFragment extends BaseFragment {

    public MineFragment() {

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Transition transition = new Slide(Gravity.START);
        transition.setDuration(200);
        setEnterTransition(transition);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = new MineViewDelegate(this, container, new MineFragmentViewModel()).getRootView();
        return view;
    }

}
