package cn.linked.baselib;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import java.util.LinkedList;

public class ActivityManager {

    private LinkedList<BaseActivity> activityStack = new LinkedList<>();

    @MainThread
    public ActivityManager() {

    }

    @MainThread
    public void pushActivity(@NonNull BaseActivity activity) {
        activityStack.push(activity);
    }

    @MainThread
    public BaseActivity popActivity() {
        if(activityStack.size() == 0) {
            return null;
        }
        return activityStack.pop();
    }

    @MainThread
    public int getActivityCount() {
        return activityStack.size();
    }

    @MainThread
    public void removeActivity(BaseActivity baseActivity) {
        if(baseActivity != null) {
            activityStack.remove(baseActivity);
        }
    }

    @MainThread
    public void finishAllActivity(boolean keepNewest) {
        if(activityStack.size() == 1 && keepNewest) { return; }
        BaseActivity activity = null;
        int i = 0;
        while (activityStack.size() > 0) {
            if(i == 0) {
                activity = activityStack.pop();
                if(!keepNewest) {
                    activity.finish();
                }
            }else {
                activityStack.pop().finish();
            }
            i++;
        }
        if(keepNewest && activity != null) {
            activityStack.push(activity);
        }
    }

}
