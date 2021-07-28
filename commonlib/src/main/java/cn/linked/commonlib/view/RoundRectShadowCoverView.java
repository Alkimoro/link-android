package cn.linked.commonlib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import cn.linked.commonlib.R;
import cn.linked.commonlib.util.drawable.ShadowDrawable;

public class RoundRectShadowCoverView extends FrameLayout {

    private ShadowDrawable shadowDrawable;

    private ShadowDrawableTarget shadowDrawableTarget;

    private Drawable errorContentBackGround;

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
            errorContentBackGround = shadowDrawable.getBackground();
            Drawable temp;
            temp = typedArray.getDrawable(R.styleable.RoundRectShadowCoverView_errorBackground);
            errorContentBackGround = temp == null? errorContentBackGround : temp;

            setContentBackGroundURL(typedArray.getString(R.styleable.RoundRectShadowCoverView_backgroundURL));

            setBackgroundSwitchAniDuration(typedArray.getInt(R.styleable.RoundRectShadowCoverView_backgroundSwitchAniDuration, 0));

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
        shadowDrawableTarget = new ShadowDrawableTarget(this);
    }

    @MainThread
    public void setContentBackGroundURL(String url) {
        if(url != null && url.startsWith("http")) {
            Glide.with(this)
                    .load(url)
                    // todo 缓存到本地
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(shadowDrawableTarget);
        }
    }

    private static class ShadowDrawableTarget extends CustomTarget<Drawable> {
        private RoundRectShadowCoverView view;
        public ShadowDrawableTarget(RoundRectShadowCoverView view) {
            this.view = view;
        }
        @Override
        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
            if(resource instanceof Animatable) { ((Animatable) resource).start(); }
            view.shadowDrawable.setBackground(resource);
        }
        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {
            if(view.errorContentBackGround != null) { view.shadowDrawable.setBackground(view.errorContentBackGround); }
        }
    }

    public void setContentBackGround(Drawable drawable) {
        shadowDrawable.setBackground(drawable);
    }

    public void setBackgroundSwitchAniDuration(int duration) {
        shadowDrawable.setBackgroundSwitchDuration(duration);
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
