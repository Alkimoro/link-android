package cn.linked.commonlib.util.screenshot;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.view.PixelCopy;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * 平均耗时5ms
 * 耗时区间 3~10ms
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class PixelCopyScreenShot implements IScreenShot {
    private Window mWindow;
    private Handler mHandler;
    private static PixelCopy.OnPixelCopyFinishedListener mEmptyListener=(result)->{};

    public PixelCopyScreenShot(@NonNull Window window){
        mWindow=window;
        mHandler=new Handler();
    }

    @Override
    public void shot(@NonNull Bitmap dest,@NonNull Rect rect) {
        PixelCopy.request(mWindow,rect,dest,mEmptyListener,mHandler);
    }

    @Override
    public void release() {
        mWindow=null;
        mHandler=null;
    }
}
