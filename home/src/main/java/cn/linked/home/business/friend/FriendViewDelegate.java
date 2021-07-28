package cn.linked.home.business.friend;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.linked.baselib.BaseFragment;
import cn.linked.baselib.UIContext;
import cn.linked.baselib.ui.BaseViewDelegate;
import cn.linked.home.R;
import cn.linked.home.business.home.HomeFragment;
import cn.linked.home.business.home.HomeViewDelegate;

public class FriendViewDelegate extends BaseViewDelegate {

    private ViewGroup rootView;

    private FriendViewModel model;

    private HomeViewDelegate parentViewDelegate;

    private ViewGroup friendInfoBarButton;

    private RecyclerView friendList;

    public FriendViewDelegate(@NonNull UIContext uiContext, ViewGroup parent, FriendViewModel model) {
        super(uiContext);
        this.model = model;
        rootView = (ViewGroup) getUiContext().getLayoutInflater().inflate(R.layout.layout_friend, parent, false);
        friendInfoBarButton = rootView.findViewById(R.id.friendInfoBar);
        friendList = rootView.findViewById(R.id.friendList);
        parentViewDelegate = ((HomeFragment) ((BaseFragment) getUiContext()).getParentFragment()).getHomeViewDelegate();
        parentViewDelegate.addChildViewDelegate(this);
    }

    @Override
    public void init() {
        friendInfoBarButton.setOnClickListener(view -> {
            parentViewDelegate.getViewPager().setCurrentItem(0, true);
        });
    }

    @Override
    public void destroy() {
        model = null;
        parentViewDelegate = null;
    }

    @Override
    public ViewGroup getRootView() {
        return rootView;
    }

}
