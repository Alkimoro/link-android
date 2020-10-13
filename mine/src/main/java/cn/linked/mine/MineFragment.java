package cn.linked.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import cn.linked.commonlib.util.DensityUtil;
import cn.linked.commonlib.viewmodel.UpdatePanelViewModel;

public class MineFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view= (ViewGroup) inflater.inflate(R.layout.mine_fragment_layout,container,false);
        view.findViewById(R.id.button).setOnClickListener(v -> {
//            System.out.println(DensityUtil.metrics.density);
//            System.out.println(DensityUtil.metrics.densityDpi);
//            System.out.println(DensityUtil.metrics.xdpi);
//            System.out.println(DensityUtil.metrics.ydpi);
//            System.out.println(DensityUtil.metrics.heightPixels);
//            System.out.println(DensityUtil.metrics.widthPixels);
            UpdatePanelViewModel.showUpdatePanel(requireActivity());
        });
//        Animation animation=new TranslateAnimation(0,400,0,1000);
//        animation.setDuration(500);
//        animation.setRepeatCount(20);
//        view.findViewById(R.id.button).startAnimation(animation);
        return view;
    }
}
