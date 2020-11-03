package cn.linked.mine;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import cn.linked.commonlib.util.UnitUtil;
import cn.linked.commonlib.util.drawable.ShadowDrawable;
import cn.linked.commonlib.viewmodel.UpdatePanelViewModel;
import cn.linked.mine.databinding.MineFragmentLayoutBinding;

public class MineFragmentViewModel {

    private Fragment host;

    private MineFragmentLayoutBinding binding;

    public MineFragmentViewModel(@NonNull Fragment fragment) {
        host=fragment;
        binding=MineFragmentLayoutBinding.inflate(fragment.getLayoutInflater(),null,false);
        initView();
        initEvent();
    }

    private void initView() {
        ShadowDrawable shadowDrawable=new ShadowDrawable();
        shadowDrawable.setShadowRadius(UnitUtil.dp2px(30,host.getResources().getDisplayMetrics()));
        shadowDrawable.setShadowStartColor(Color.argb(20,0,221,255),true);
        shadowDrawable.setBackground(new ColorDrawable(Color.WHITE));
        binding.panel.setBackground(shadowDrawable);
    }

    private void initEvent() {
        binding.getRoot().setOnClickListener((v)->{
            host.getParentFragmentManager().popBackStack();
        });
        binding.button.setOnClickListener(v -> {
            System.out.println("是否硬件加速"+binding.button.isHardwareAccelerated());
            UpdatePanelViewModel.showUpdatePanel(host.requireActivity());
        });
    }

    public MineFragmentLayoutBinding getBinding() {
        return binding;
    }
}
