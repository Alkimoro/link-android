package cn.linked.home.business.home;

import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import cn.linked.baselib.BaseFragment;
import cn.linked.baselib.UIContext;
import cn.linked.baselib.ui.BaseViewDelegate;
import cn.linked.home.R;
import lombok.Getter;

public class HomeViewDelegate extends BaseViewDelegate {

    private ViewGroup rootView;
    private HomeViewModel model;

    private ImageView moreMenuButton;
    private ViewGroup moreMenuRootView;
    private PopupWindow moreMenuPopupWindow;
    private ViewGroup moreMenuCreateGroupButton;
    private ViewGroup moreMenuAddFriendButton;

    @Getter
    private ViewPager viewPager;

    private TextView userStateText;

    public HomeViewDelegate(@NonNull UIContext uiContext, ViewGroup parent, HomeViewModel model) {
        super(uiContext);
        this.model = model;
        rootView = (ViewGroup) uiContext.getLayoutInflater().inflate(R.layout.layout_home, parent, false);
        viewPager = rootView.findViewById(R.id.homeContentViewPager);
        moreMenuButton = rootView.findViewById(R.id.moreMenuButton);
        moreMenuRootView = (ViewGroup) uiContext.getLayoutInflater().inflate(R.layout.layout_home_more_menu, parent, false);
        moreMenuCreateGroupButton = moreMenuRootView.findViewById(R.id.menuItem1);
        moreMenuAddFriendButton = moreMenuRootView.findViewById(R.id.menuItem2);
        userStateText = rootView.findViewById(R.id.userStateText);

        viewPager.setAdapter(new HomeContentPagerAdapter(((BaseFragment) getUiContext()).getChildFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));
    }

    @Override
    public void init() {
        addAndObserve(model.getUserStateLiveData(), text -> {
            if(text != null) {
                userStateText.setText(text);
            }
        });
        moreMenuButton.setOnClickListener(view -> {
            Animation animation = new RotateAnimation(0, 45, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(200);
            animation.setFillAfter(true);
            moreMenuButton.startAnimation(animation);

            if(moreMenuPopupWindow == null) {
                moreMenuPopupWindow = new PopupWindow(moreMenuRootView,
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                moreMenuPopupWindow.setOutsideTouchable(true);
                moreMenuPopupWindow.setFocusable(true);
                moreMenuPopupWindow.setOnDismissListener(() -> {
                    Animation animation2 = new RotateAnimation(45, 0, Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    animation2.setDuration(200);
                    animation2.setFillAfter(true);
                    moreMenuButton.startAnimation(animation2);
                });
            }
            moreMenuPopupWindow.showAsDropDown(moreMenuButton, 0, 0);
        });
    }

    @Override
    public void destroy() {
        rootView = null;
        model = null;
        moreMenuPopupWindow = null;
    }

    @Override
    public ViewGroup getRootView() {
        return rootView;
    }

}
