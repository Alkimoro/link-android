package cn.linked.home.business.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.linked.baselib.BaseFragment;
import cn.linked.router.common.Route;
import lombok.Getter;

@Route(path = "home/homeFragment")
public class HomeFragment extends BaseFragment {

    @Getter
    private HomeViewDelegate homeViewDelegate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homeViewDelegate = new HomeViewDelegate(this, container, new HomeViewModel());
        return homeViewDelegate.getRootView();
    }
}
