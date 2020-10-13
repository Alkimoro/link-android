package cn.linked.commonlib.util.screenshot;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;

public class DrawingCacheScreenShot implements IScreenShot {
    private View mDecorView;
    private boolean isDrawingCacheEnabled;

    public DrawingCacheScreenShot(@NonNull View view){
        mDecorView=view;
        isDrawingCacheEnabled=mDecorView.isDrawingCacheEnabled();
        if(!isDrawingCacheEnabled){
            mDecorView.setDrawingCacheEnabled(true);
        }
    }

    @Override
    public void shot(@NonNull Bitmap dest,@NonNull Rect rect) {
        Bitmap bitmap=mDecorView.getDrawingCache();
        Canvas canvas=new Canvas(dest);
        canvas.drawBitmap(bitmap,rect,new Rect(0,0,dest.getWidth(),dest.getHeight()),null);
        mDecorView.destroyDrawingCache();
    }

    @Override
    public void release() {
        if(!isDrawingCacheEnabled){
            mDecorView.setDrawingCacheEnabled(false);
        }
        mDecorView=null;
    }
}
