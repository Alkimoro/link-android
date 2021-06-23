package cn.linked.home.business.home;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import cn.linked.baselib.UIContext;
import cn.linked.baselib.ui.BaseViewDelegate;
import cn.linked.home.R;

public class HomeViewDelegate extends BaseViewDelegate {

    private ViewGroup rootView;
    private HomeViewModel model;

    public HomeViewDelegate(@NonNull UIContext uiContext, ViewGroup parent, HomeViewModel model) {
        super(uiContext);
        this.model = model;
        rootView = (ViewGroup) uiContext.getLayoutInflater().inflate(R.layout.layout_home, parent, false);
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {
        rootView = null;
        model = null;
    }

    @Override
    public ViewGroup getRootView() {
        return rootView;
    }

}
