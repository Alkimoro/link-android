package cn.linked.commonlib.util;

public class ColorUtil {
    public static int a(int color){
        int a = color >>> 24;
        return a;
    }
    public static int r(int color){
        int r = ( color & 0xff0000 ) >> 16;
        return r;
    }
    public static int g(int color){
        int g = ( color & 0xff00 ) >> 8;
        return g;
    }
    public static int b(int color){
        int b = color & 0xff;
        return b;
    }
}
