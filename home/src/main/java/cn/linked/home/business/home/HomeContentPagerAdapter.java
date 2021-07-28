package cn.linked.home.business.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import cn.linked.home.business.friend.FriendFragment;

public class HomeContentPagerAdapter extends FragmentStatePagerAdapter {

    public HomeContentPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return new SessionListFragment();
        }else {
            return new FriendFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
