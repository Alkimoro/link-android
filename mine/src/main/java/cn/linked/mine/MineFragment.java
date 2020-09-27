package cn.linked.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
            System.out.println(DensityUtil.metrics.density);
            System.out.println(DensityUtil.metrics.densityDpi);
            System.out.println(DensityUtil.metrics.xdpi);
            System.out.println(DensityUtil.metrics.ydpi);
            System.out.println(DensityUtil.metrics.heightPixels);
            System.out.println(DensityUtil.metrics.widthPixels);
            UpdatePanelViewModel.showUpdatePanel(inflater,requireActivity().findViewById(android.R.id.content));
        });
        return view;
    }
}
