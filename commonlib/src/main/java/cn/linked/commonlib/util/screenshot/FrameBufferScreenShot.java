package cn.linked.commonlib.util.screenshot;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 需要Root 否则无权限访问
 */
public class FrameBufferScreenShot implements IScreenShot {

    public static final String DEVICE_NAME="/dev/graphics/fb0";

    private Process localProcess;
    private String command;

    public FrameBufferScreenShot(){
        File deviceFile = new File(DEVICE_NAME);
        command = "cat " + deviceFile.getAbsolutePath() + "\n";
        try {
            localProcess = Runtime.getRuntime().exec("su");
        }catch (IOException e){
            Log.e("FrameBufferScreenShot","IOException Object initial failed");
            e.printStackTrace();
        }
    }

    @Override
    public void shot(@NonNull Bitmap dest,@NonNull Rect rect) {
        if(localProcess==null){
            Log.e("FrameBufferScreenShot","this Object initial failed,shot() do nothing");
            return;
        }
        try {
            localProcess.getOutputStream().write(command.getBytes());
            InputStream inputStream=localProcess.getInputStream();
            Bitmap tempMap=BitmapFactory.decodeStream(inputStream);
            Canvas canvas=new Canvas(dest);
            canvas.drawBitmap(tempMap,rect,new Rect(0,0,dest.getWidth(),dest.getHeight()),null);
        }catch (IOException e){
            Log.e("FrameBufferScreenShot","IOException shot() is failed");
        }
    }

    @Override
    public void release() {
        if(localProcess!=null) {
            localProcess.destroy();
        }
        localProcess=null;
    }
}
