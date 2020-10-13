package cn.linked.commonlib.view;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;

import cn.linked.commonlib.R;
import cn.linked.commonlib.util.ColorUtil;
import cn.linked.commonlib.util.IBlur;
import cn.linked.commonlib.util.RenderScriptBlur;


/**
 * 如果可能，建议用在弹出窗口上
 * 如果不在弹出窗口上，可能会产生冗余的mBitmapToBlur更新
 */
public class BlurView extends View {

    private float mDownsampleFactor; // default 4
    private int mOverlayColor;
    private float mBlurRadius; // default 10dp (0 < r <= 25)
    private float mResultRadius;
    private boolean applyBlur=true;

    private IBlur mBlurImpl;
    private Bitmap mBitmapToBlur, mBlurredBitmap;
    private Canvas mBlurringCanvas;
    private boolean mIsRendering;
    private Paint mPaint;
    private final Rect mRectSrc = new Rect(), mRectDst = new Rect();
    // mDecorView should be the root view of the activity (even if you are on a different window like a dialog)
    private View mDecorView;
    // If the view is on different root view (usually means we are on a PopupWindow),
    // we need to manually call invalidate() in onPreDraw(), otherwise we will not be able to see the changes
    private boolean mDifferentRoot;
    private static int RENDERING_COUNT;
    private int BLUR_IMPL_TYPE=0;

    public BlurView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBlurImpl = getBlurImpl(); // provide your own by override getBlurImpl()

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BlurView);
        mBlurRadius = a.getDimension(R.styleable.BlurView_blurRadius,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics()));
        mDownsampleFactor = a.getFloat(R.styleable.BlurView_downsampleFactor, 4);
        applyBlur=a.getBoolean(R.styleable.BlurView_applyBlur, true);
        mOverlayColor = a.getColor(R.styleable.BlurView_overlayColor, 0x0);
        a.recycle();

        mPaint=new Paint();
    }

    public BlurView(Context context) {
        super(context);
    }

    protected IBlur getBlurImpl() {
        if (BLUR_IMPL_TYPE == 0) {
            // try to use stock impl first
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                try {
                    RenderScriptBlur impl = new RenderScriptBlur();
                    BLUR_IMPL_TYPE = 1;
                    return impl;
                } catch (Throwable e) {
                    Log.w("BlurView",e);
                }
            }
        }
        BLUR_IMPL_TYPE = -1;
        return IBlur.EmptyBlur;
    }

    public void setBlurRadius(float radius) {
        if (mBlurRadius != radius) {
            mBlurRadius = radius;
            invalidate();
        }
    }

    public void setApplyBlur(boolean applyBlur){
        if(this.applyBlur!=applyBlur){
            this.applyBlur=applyBlur;
            invalidate();
        }
    }

    public void setDownsampleFactor(float factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("Downsample factor must be greater than 0.");
        }

        if (mDownsampleFactor != factor) {
            mDownsampleFactor = factor;
            invalidate();
        }
    }

    public void setOverlayColor(int color) {
        if (mOverlayColor != color) {
            mOverlayColor = color;
            invalidate();
        }
    }

    private void destroyBitmap() {
        if (mBitmapToBlur != null) {
            mBitmapToBlur.recycle();
            mBitmapToBlur = null;
        }
        if (mBlurredBitmap != null) {
            mBlurredBitmap.recycle();
            mBlurredBitmap = null;
        }
    }

    protected void destroy() {
        destroyBitmap();
        mBlurImpl.destroy();
    }

    protected boolean prepare() {
        if (mBlurRadius == 0) {
            return true;
        }

        float downsampleFactor = mDownsampleFactor;
        float radius = mBlurRadius / downsampleFactor;
        if (radius > 25) {
            //等价于 ：downsampleFactor=mBlurRadius/25
            downsampleFactor = downsampleFactor * radius / 25;
            radius = 25;
        }
        mResultRadius=radius;

        final int width = getWidth();
        final int height = getHeight();

        int scaledWidth = Math.max(1, (int) (width / downsampleFactor));
        int scaledHeight = Math.max(1, (int) (height / downsampleFactor));

        if (mBlurringCanvas == null || mBlurredBitmap == null
                || mBlurredBitmap.getWidth() != scaledWidth
                || mBlurredBitmap.getHeight() != scaledHeight) {
            destroyBitmap();

            boolean r = false;
            try {
                mBitmapToBlur = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
                if (mBitmapToBlur == null) {
                    return false;
                }
                // 软件渲染Canvas
                mBlurringCanvas = new Canvas(mBitmapToBlur);

                mBlurredBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
                if (mBlurredBitmap == null) {
                    return false;
                }
                r = true;
                if(!mBlurImpl.prepare(getContext(), mBitmapToBlur)){return false;}
            } catch (OutOfMemoryError e) {
                // Bitmap.createBitmap() may cause OOM error
                // Simply ignore and fallback
                return false;
            } finally {
                if (!r) {
                    destroy();
                }
            }
        }

        return true;
    }

    private boolean needDrawBlurredBitmap(){
        if(mBlurRadius==0){return false;}
        if(ColorUtil.a(mOverlayColor)==255){return false;}
        return applyBlur;
    }

    protected void blur(Bitmap bitmapToBlur, Bitmap blurredBitmap) {
        mBlurImpl.blur(bitmapToBlur, blurredBitmap,mResultRadius);
    }

    private final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            final int[] locations = new int[2];
            Bitmap oldBmp = mBlurredBitmap;
            View decor = mDecorView;
            if (needDrawBlurredBitmap()&&decor != null && isShown() && prepare()) {
                //boolean redrawBitmap = mBlurredBitmap != oldBmp;
                oldBmp = null;
                decor.getLocationOnScreen(locations);
                int x = -locations[0];
                int y = -locations[1];

                getLocationOnScreen(locations);
                x += locations[0];
                y += locations[1];

                // just erase transparent
                mBitmapToBlur.eraseColor(mOverlayColor & 0xffffff);

                int rc = mBlurringCanvas.save();
                mIsRendering = true;
                RENDERING_COUNT++;
                try {
                    mBlurringCanvas.scale(1.f * mBitmapToBlur.getWidth() / getWidth(), 1.f * mBitmapToBlur.getHeight() / getHeight());
                    mBlurringCanvas.translate(-x, -y);
                    if (decor.getBackground() != null) {
                        decor.getBackground().draw(mBlurringCanvas);
                    }
                    decor.draw(mBlurringCanvas);
                } catch (StopException e) {
                } finally {
                    mIsRendering = false;
                    RENDERING_COUNT--;
                    mBlurringCanvas.restoreToCount(rc);
                }

                blur(mBitmapToBlur, mBlurredBitmap);

                if (mDifferentRoot) {
                    invalidate();
                }

            }
            return true;
        }
    };

    protected View getActivityDecorView() {
        Context ctx = getContext();
        for (int i = 0; i < 4 && ctx != null && !(ctx instanceof Activity) && ctx instanceof ContextWrapper; i++) {
            ctx = ((ContextWrapper) ctx).getBaseContext();
        }
        if (ctx instanceof Activity) {
            return ((Activity) ctx).getWindow().getDecorView();
        } else {
            return null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDecorView = getActivityDecorView();
        if (mDecorView != null) {
            mDecorView.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
            mDifferentRoot = mDecorView.getRootView() != getRootView();
            if (mDifferentRoot) {
                mDecorView.postInvalidate();
            }
        } else {
            mDifferentRoot = false;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mDecorView != null) {
            mDecorView.getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
        }
        destroy();
        super.onDetachedFromWindow();
    }

    @Override
    public void draw(Canvas canvas) {
        if (mIsRendering) {
            // Quit here, don't draw views above me
            throw STOP_EXCEPTION;
        } else if (RENDERING_COUNT > 0) {
            // Doesn't support blurview overlap on another blurview
        } else {
            super.draw(canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(needDrawBlurredBitmap()) {
            drawBlurredBitmap(canvas, mBlurredBitmap);
        }
        drawMixedBackground(canvas);
    }

    protected void drawBlurredBitmap(Canvas canvas, Bitmap blurredBitmap) {
        if (blurredBitmap != null) {
            mRectSrc.right = blurredBitmap.getWidth();
            mRectSrc.bottom = blurredBitmap.getHeight();
            mRectDst.right = getWidth();
            mRectDst.bottom = getHeight();
            canvas.drawBitmap(blurredBitmap, mRectSrc, mRectDst, null);
        }
    }

    protected void drawMixedBackground(Canvas canvas){
        mRectDst.right = getWidth();
        mRectDst.bottom = getHeight();
        mPaint.setColor(mOverlayColor);
        canvas.drawRect(mRectDst,mPaint);
    }

    private static class StopException extends RuntimeException {
    }

    private static StopException STOP_EXCEPTION = new StopException();
}
