package cn.linked.mine;

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
import androidx.fragment.app.Fragment;

import cn.linked.router.common.Route;

@Route(path = "mine/mineFragment")
public class MineFragment extends Fragment {

    private MineFragmentViewModel viewModel;
    private boolean isViewModelInit=false;

    public MineFragment() {
        initTransition();
    }

    private void initTransition() {
        Transition transition=new Slide(Gravity.LEFT);
        transition.setDuration(400);
        setEnterTransition(transition);
        setExitTransition(transition);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(!isViewModelInit) {
            viewModel = new MineFragmentViewModel(this);
            isViewModelInit=true;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return viewModel.getBinding().getRoot();
    }

}
