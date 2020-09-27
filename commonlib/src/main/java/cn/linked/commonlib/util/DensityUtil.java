package cn.linked.commonlib.util;

import android.util.DisplayMetrics;
import android.util.TypedValue;

public class DensityUtil {
    public static DisplayMetrics metrics;
    public static float screenWidthInDp;
    public static float screenHeightInDp;
    public static void init(DisplayMetrics metrics){
        DensityUtil.metrics=metrics;
        DensityUtil.screenHeightInDp=metrics.heightPixels*160f/metrics.densityDpi;
        DensityUtil.screenWidthInDp=metrics.widthPixels*160f/metrics.densityDpi;
    }

    public static int dp2px(float dpValue) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpValue,metrics);
    }

    public static float sp2px(float spValue) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,spValue,metrics);
    }

}
