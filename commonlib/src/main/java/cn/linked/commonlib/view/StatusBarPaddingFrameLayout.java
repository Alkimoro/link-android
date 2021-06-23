package cn.linked.commonlib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.linked.commonlib.util.UnitUtil;

public class StatusBarPaddingFrameLayout extends FrameLayout {

    private int statusBarHeight = UnitUtil.getStatusBarHeight();

    public StatusBarPaddingFrameLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public StatusBarPaddingFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StatusBarPaddingFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public StatusBarPaddingFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setPadding(0, 0, 0, 0);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        top += statusBarHeight;
        super.setPadding(left, top, right, bottom);
    }

}
