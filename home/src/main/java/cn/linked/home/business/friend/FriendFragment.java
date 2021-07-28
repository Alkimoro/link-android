package cn.linked.home.business.friend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.linked.baselib.BaseFragment;
import cn.linked.router.common.Route;

@Route(path = "home/friendFragment")
public class FriendFragment extends BaseFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = new FriendViewDelegate(this, container, new FriendViewModel()).getRootView();
        return view;
    }

}
