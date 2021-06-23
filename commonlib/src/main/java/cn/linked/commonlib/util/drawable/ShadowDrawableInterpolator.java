package cn.linked.commonlib.util.drawable;

import android.graphics.Color;

import cn.linked.commonlib.util.ColorUtil;

public class ShadowDrawableInterpolator {
    public static final int MODE_SQUARE=1;
    public static final int MODE_CUBE=2;
    public static final int MODE_LINEAR=-1;
    public static final int MODE_SIN=4;

    private static float[] calculateFraction(int length,int insertNum) {
        int total=length+insertNum*(length-1)+2;
        float[] result=new float[total];
        result[0]=result[1]=0;
        for(int i=2;i<total;i++) {
            result[i]=(i-2f)/(total-3);
        }
        return result;
    }

    private static float[] reFraction(float[] fraction,int mode) {
        float[] result=new float[fraction.length];
        for(int i=0;i<result.length;i++) {
            switch (mode) {
                case MODE_SQUARE:
                    result[i]=(float) Math.pow(fraction[i],2);
                    break;
                case MODE_CUBE:
                    result[i]=(float) Math.pow(fraction[i],3);
                    break;
                case MODE_SIN:
                    result[i]=(float) (Math.sin(fraction[i]*Math.PI/2));
                    break;
                default:
                    result[i]=fraction[i];
            }
        }
        return result;
    }

    public static Pair<Integer> calculate(int[] colors,int insertNum,int mode) {
        if(colors==null||colors.length<2||insertNum<0) {return null;}
        Pair<Integer> result=new Pair<>();

        result.fractions=calculateFraction(colors.length,insertNum);

        float[] reFraction=reFraction(result.fractions,mode);

        result.values=new Integer[reFraction.length];
        result.values[0]=result.values[1]=0;

        int index=2;
        for(int i=0;i<colors.length;i++) {
            result.values[index]=colors[i];
            index++;
            if(i==colors.length-1) {
                break;
            }
            int sr= ColorUtil.r(colors[i]);int er=ColorUtil.r(colors[i+1]);
            int sg=ColorUtil.g(colors[i]);int eg=ColorUtil.g(colors[i+1]);
            int sb=ColorUtil.b(colors[i]);int eb=ColorUtil.b(colors[i+1]);
            int sa=ColorUtil.a(colors[i]);int ea=ColorUtil.a(colors[i+1]);
            int s=i*(insertNum+1)+2;
            for(int j=0;j<insertNum;j++) {
                int r=(int)valueOfFraction(sr,er,reFraction[s],reFraction[s+insertNum+1],reFraction[index]);
                int g=(int)valueOfFraction(sg,eg,reFraction[s],reFraction[s+insertNum+1],reFraction[index]);
                int b=(int)valueOfFraction(sb,eb,reFraction[s],reFraction[s+insertNum+1],reFraction[index]);
                int a=(int)valueOfFraction(sa,ea,reFraction[s],reFraction[s+insertNum+1],reFraction[index]);
                result.values[index]=Color.argb(a,r,g,b);
                index++;
            }
        }

        return result;
    }

    private static float valueOfFraction(float startValue,float endValue,float startFraction,float endFraction,float targetFraction) {
        return startValue+(endValue-startValue)*((targetFraction-startFraction)/(endFraction-startFraction));
    }

    public static class Pair<T> {
        public T[] values;
        public float[] fractions;//0f to 1f

        public int[] intValues() {
            if(values!=null&&values.length>0&&values[0] instanceof Integer) {
                int[] result=new int[values.length];
                for(int i=0;i<result.length;i++) {
                    result[i]=(Integer)values[i];
                }
                return result;
            }
            return null;
        }

        public float[]  mappingFraction(float start,float end) {
            if(fractions!=null&&fractions.length>1) {
                float[] result = new float[fractions.length];
                float scale=(end-start);
                for(int i=1;i<result.length;i++) {
                    result[i]=start+fractions[i]*scale;
                }
                return result;
            }
            return null;
        }
    }
}
