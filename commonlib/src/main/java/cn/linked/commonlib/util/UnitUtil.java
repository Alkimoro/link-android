package cn.linked.commonlib.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class UnitUtil {

    public static int dp2px(float dpValue,DisplayMetrics metrics) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpValue,metrics);
    }

    public static float sp2px(float spValue,DisplayMetrics metrics) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,spValue,metrics);
    }

    public static int getStatusBarHeight() {
        int result = 0;
        try {
            int resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = Resources.getSystem().getDimensionPixelSize(resourceId);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

}
