package cn.linked.commonlib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.linked.commonlib.R;
import cn.linked.commonlib.util.drawable.ShadowDrawable;

public class RoundRectShadowCoverView extends FrameLayout {

    private ShadowDrawable shadowDrawable;

    public RoundRectShadowCoverView(@NonNull Context context) {
        super(context);
        init(context, null);
    }
    public RoundRectShadowCoverView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    public RoundRectShadowCoverView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    public RoundRectShadowCoverView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.clipPath(shadowDrawable.getContentPath());
        super.dispatchDraw(canvas);
    }

    private void init(Context context, AttributeSet attrs) {
        shadowDrawable = new ShadowDrawable();
        if(attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundRectShadowCoverView);

            setContentBackGround(typedArray.getDrawable(R.styleable.RoundRectShadowCoverView_contentBackground));

            setShadowStartColor(typedArray.getColor(R.styleable.RoundRectShadowCoverView_shadowStartColor,
                    shadowDrawable.getShadowStartColor()));
            setShadowEndColor(typedArray.getColor(R.styleable.RoundRectShadowCoverView_shadowEndColor,
                    shadowDrawable.getShadowEndColor()));

            setShadowOffsetX(typedArray.getDimensionPixelSize(R.styleable.RoundRectShadowCoverView_shadowOffsetX, 0));
            setShadowOffsetY(typedArray.getDimensionPixelSize(R.styleable.RoundRectShadowCoverView_shadowOffsetY, 0));

            setShadowExtension(typedArray.getDimensionPixelSize(R.styleable.RoundRectShadowCoverView_shadowExtension, 0));

            setShadowRadius(typedArray.getDimensionPixelSize(R.styleable.RoundRectShadowCoverView_shadowRadius, 0));

            setCornerRadius(typedArray.getDimensionPixelSize(R.styleable.RoundRectShadowCoverView_cornerRadius, 0));
            setCornerRadiusLB(typedArray.getDimensionPixelSize(R.styleable.RoundRectShadowCoverView_cornerRadiusLB,
                    shadowDrawable.getCornerLB()));
            setCornerRadiusLT(typedArray.getDimensionPixelSize(R.styleable.RoundRectShadowCoverView_cornerRadiusLT,
                    shadowDrawable.getCornerLT()));
            setCornerRadiusRB(typedArray.getDimensionPixelSize(R.styleable.RoundRectShadowCoverView_cornerRadiusRB,
                    shadowDrawable.getCornerRB()));
            setCornerRadiusRT(typedArray.getDimensionPixelSize(R.styleable.RoundRectShadowCoverView_cornerRadiusRT,
                    shadowDrawable.getCornerRT()));

            typedArray.recycle();
        }
        setBackground(shadowDrawable);
    }

    public void setContentBackGround(Drawable drawable) {
        shadowDrawable.setBackground(drawable);
    }

    public void setShadowStartColor(int color) {
        shadowDrawable.setShadowStartColor(color, true);
    }

    public void setShadowEndColor(int color) {
        shadowDrawable.setShadowEndColor(color);
    }

    public void setShadowOffsetX(int offsetX) {
        shadowDrawable.setOffsetX(offsetX);
    }

    public void setShadowOffsetY(int offsetY) {
        shadowDrawable.setOffsetY(offsetY);
    }

    public void setShadowExtension(int extension) {
        shadowDrawable.setExtension(extension);
    }

    public void setShadowRadius(int radius) {
        shadowDrawable.setShadowRadius(radius);
    }

    public void setCornerRadius(int radius) {
        shadowDrawable.setCorner(radius);
    }

    public void setCornerRadiusLT(int radius) {
        shadowDrawable.setCornerLT(radius);
    }

    public void setCornerRadiusLB(int radius) {
        shadowDrawable.setCornerLB(radius);
    }

    public void setCornerRadiusRT(int radius) {
        shadowDrawable.setCornerRT(radius);
    }

    public void setCornerRadiusRB(int radius) {
        shadowDrawable.setCornerRB(radius);
    }

}
