package cn.linked.mine.business.mine;

import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

import cn.linked.baselib.BaseFragment;
import cn.linked.baselib.UIContext;
import cn.linked.baselib.ui.BaseViewDelegate;
import cn.linked.commonlib.viewmodel.UpdatePanelViewModel;
import cn.linked.mine.R;

public class MineViewDelegate extends BaseViewDelegate {

    private ViewGroup rootView;

    private Button button;

    public MineViewDelegate(@NonNull UIContext uiContext, ViewGroup parent, MineFragmentViewModel model) {
        super(uiContext);
        rootView = (ViewGroup) uiContext.getLayoutInflater().inflate(R.layout.mine_fragment_layout, parent, false);
        button = rootView.findViewById(R.id.button);
    }

    @Override
    public void init() {
        rootView.setOnClickListener(v -> {
            ((BaseFragment) getUiContext()).getParentFragmentManager().popBackStack();
        });
        button.setOnClickListener(v -> {
            System.out.println("是否硬件加速" + button.isHardwareAccelerated());
            UpdatePanelViewModel.showUpdatePanel(((BaseFragment) getUiContext()).requireActivity());
        });
    }

    @Override
    public void destroy() {

    }

    @Override
    public ViewGroup getRootView() {
        return rootView;
    }
}
