package cn.linked.link;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import cn.linked.router.api.Router;

public class NavigationPagerAdapter extends FragmentStatePagerAdapter {
    public NavigationPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment result=null;
        switch (position) {
            case 0:
                try {
                    result=(Fragment)Router.route("home/homeFragment").newInstance();
                } catch (ReflectiveOperationException e) {
                }
                break;
            case 1:
                try {
                    result=(Fragment)Router.route("mine/mineFragment").newInstance();
                } catch (ReflectiveOperationException e) {
                }
                break;
        }
        return result;
    }

    @Override
    public int getCount() {
        return 1;
    }
}
