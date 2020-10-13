package cn.linked.commonlib.util;

import android.content.Context;
import android.graphics.Bitmap;

public interface IBlur {
    public boolean prepare(Context context, Bitmap buffer);
    public void blur(Bitmap input,Bitmap output,float radius);
    public void destroy();

    public static final IBlur EmptyBlur=new EmptyBlur();
    public static class EmptyBlur implements IBlur{
        @Override
        public boolean prepare(Context context, Bitmap buffer){return true;}
        @Override
        public void blur(Bitmap input, Bitmap output, float radius) { }
        @Override
        public void destroy() { }
    }
}
