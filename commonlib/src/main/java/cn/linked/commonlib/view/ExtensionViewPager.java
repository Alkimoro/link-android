package cn.linked.commonlib.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EdgeEffect;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

public class ExtensionViewPager extends ViewPager {
    private EdgeEffect leftEdge;
    private EdgeEffect rightEdge;

    public ExtensionViewPager(@NonNull Context context) {
        super(context);
        init();
    }
    public ExtensionViewPager(@NonNull Context context,@NonNull AttributeSet attrs) {
        super(context,attrs);
        init();
    }

    private void init() {
        try {
            Field mLeftEdgeField=ViewPager.class.getDeclaredField("mLeftEdge");
            Field mRightEdgeField=ViewPager.class.getDeclaredField("mRightEdge");
            mLeftEdgeField.setAccessible(true);
            mRightEdgeField.setAccessible(true);
            leftEdge=((EdgeEffect)mLeftEdgeField.get(this));
            rightEdge=((EdgeEffect)mRightEdgeField.get(this));
            int color=Color.rgb(175,175,225);
            leftEdge.setColor(color);
            rightEdge.setColor(color);
        }catch (NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
            Log.w("ExtensionViewPager","EdgeEffect获取错误");
        }
    }

    @Override
    public void setCurrentItem(int item) {
        if(item>=getAdapter().getCount()){
            return;
        }
        super.setCurrentItem(item);
    }

    public EdgeEffect getLeftEdge() {
        return leftEdge;
    }

    public EdgeEffect getRightEdge() {
        return rightEdge;
    }
}
