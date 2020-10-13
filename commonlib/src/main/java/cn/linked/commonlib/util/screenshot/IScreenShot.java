package cn.linked.commonlib.util.screenshot;

import android.graphics.Bitmap;
import android.graphics.Rect;

public interface IScreenShot {
    public void shot(Bitmap dest, Rect rect);
    public void release();
}
