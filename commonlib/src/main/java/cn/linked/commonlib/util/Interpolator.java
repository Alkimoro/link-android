package cn.linked.commonlib.util;

import android.graphics.Color;

public class Interpolator {
    public static final int MODE_SQUARE=1;
    public static final int MODE_CUBE=2;
    public static final int MODE_LINEAR=-1;
    public static final int MODE_SIN=4;

    private static boolean validate(int length,int start,int targetNum) {
        if(length<=0||start<0) {return false;}
        if(start>=length-1) {return false;}
        if(targetNum<2) {return false;}
        return true;
    }

    private static int insertNumInTwoValues(int length,int start,int targetNum) {
        return (targetNum-(length-start))/(length-start-1);
    }

    private static float[] calculateFraction(int length,int start,int insertNum) {
        int total=length-start+insertNum*(length-start-1);
        float[] result=new float[total];
        for(int i=0;i<total;i++) {
            result[i]=(i+0f)/(total-1);
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

    public static Pair<Integer> calculate(int[] colors,int start,int targetNum,int mode) {
        if(!validate(colors.length,start,targetNum)) {return null;}
        Pair<Integer> result=new Pair<>();

        int insertNum=insertNumInTwoValues(colors.length,start,targetNum);
        result.fractions=calculateFraction(colors.length,start,insertNum);

        float[] reFraction=reFraction(result.fractions,mode);

        result.values=new Integer[reFraction.length];

        int index=0;
        for(int i=start;i<colors.length;i++) {
            int s=(i-start)*(insertNum+1);
            result.values[index]=colors[i];
            index++;
            if(i==colors.length-1) {
                break;
            }
            int sr=ColorUtil.r(colors[i]);int er=ColorUtil.r(colors[i+1]);
            int sg=ColorUtil.g(colors[i]);int eg=ColorUtil.g(colors[i+1]);
            int sb=ColorUtil.b(colors[i]);int eb=ColorUtil.b(colors[i+1]);
            int sa=ColorUtil.a(colors[i]);int ea=ColorUtil.a(colors[i+1]);
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

    public static Pair<Float> calculateNum(float[] values,int start,int targetNum,int mode) {
        if(!validate(values.length,start,targetNum)) {return null;}
        Pair<Float> result=new Pair<>();

        int insertNum=insertNumInTwoValues(values.length,start,targetNum);
        result.fractions=calculateFraction(values.length,start,insertNum);

        float[] reFraction=reFraction(result.fractions,mode);

        result.values=new Float[reFraction.length];

        int index=0;
        for(int i=start;i<values.length;i++) {
            int s=(i-start)*(insertNum+1);
            result.values[index]=values[i];
            index++;
            if(i==values.length-1) {
                break;
            }
            for(int j=0;j<insertNum;j++) {
                result.values[index]=valueOfFraction(values[i],values[i+1],reFraction[s],reFraction[s+insertNum+1],reFraction[index]);
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
                for(int i=0;i<result.length;i++) {
                    result[i]=start+fractions[i]*scale;
                }
                return result;
            }
            return null;
        }
    }
}
