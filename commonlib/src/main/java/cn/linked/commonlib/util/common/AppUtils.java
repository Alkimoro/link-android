package cn.linked.commonlib.util.common;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class AppUtils {
    public static void exitApp(Context context){
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();
        for (ActivityManager.AppTask appTask : appTaskList) {
            appTask.finishAndRemoveTask();
        }
    }
}
