package cn.linked.commonlib.util.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.linked.commonlib.util.ColorUtil;

public class ShadowDrawable extends Drawable {

    private int shadowStartColor=Color.BLACK;
    private int shadowEndColor=Color.TRANSPARENT;
    private int offsetX=0;
    private int offsetY=0;

    private int extension=0;
    private int shadowRadius=20;
    // shadowRadius 转换为 shadowRadiusInternal 的转换比例
    private final float shadowRadiusToInternalScale = 62f/40f;
    /**
     *  shadowRadius extension 设置后为保持跟html5 box-shadow表现效果一致 会进行转换
     *      转换结果 由 shadowRadiusInternal extensionInternal存储
     * */
    private int shadowRadiusInternal=(int) (shadowRadius*shadowRadiusToInternalScale);
    private int extensionInternal=extension+shadowRadius-shadowRadiusInternal;

    private int cornerRadiusLT=0;
    private int cornerRadiusLB=0;
    private int cornerRadiusRT=0;
    private int cornerRadiusRB=0;

    private int shadowSimple = 5;

    private Rect content;
    private boolean isCustomContent=false;
    private boolean isContentByMargin=true;
    private Path contentPath;

    private Paint cornerPaint=new Paint();
    private Paint linePaint=new Paint();
    private Paint shadowInnerPaint=new Paint();

    private Drawable background;

    public ShadowDrawable() {
        cornerPaint.setAntiAlias(false);
        linePaint.setAntiAlias(false);
        shadowInnerPaint.setAntiAlias(false);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    private Rect getNewContent() {
        Rect newContent=new Rect();
        View host=(View)getCallback();
        if(host!=null) {
            if(isContentByMargin) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) host.getLayoutParams();
                newContent.top = p.topMargin;
                newContent.bottom = host.getHeight() - p.bottomMargin;
                newContent.left = Math.max(p.leftMargin, p.getMarginStart());
                newContent.right = host.getWidth() - Math.max(p.rightMargin, p.getMarginEnd());
            }else{
                newContent.top=host.getPaddingTop();
                newContent.bottom=host.getHeight()-host.getPaddingBottom();
                newContent.left=Math.max(host.getPaddingLeft(),host.getPaddingStart());
                newContent.right=host.getWidth()-Math.max(host.getPaddingRight(),host.getPaddingEnd());
            }
        }
        return newContent;
    }

    @Override
    public void draw(Canvas canvas) {
        if(!isCustomContent) {
            Rect newContent = getNewContent();

            if (!newContent.equals(content)) {
                content = newContent;
                buildContentPath();
            }
        }

        drawShadow(canvas);

        // draw background
        if(background!=null) {
            canvas.clipPath(contentPath);
            background.setBounds(content);
            background.draw(canvas);
        }
    }

    private void drawShadow(Canvas canvas) {
        Rect e=getExtensionAndOffsetShadowRect();

        canvas.save();

        ShadowDrawableInterpolator.Pair<Integer> interpolation= ShadowDrawableInterpolator.calculate(new int[]{shadowStartColor, shadowEndColor},
                shadowSimple, ShadowDrawableInterpolator.MODE_SIN);

        if(shadowRadiusInternal>0) {
            linePaint.setShader(new LinearGradient(e.left, 0, e.left - shadowRadiusInternal, 0,
                    interpolation.intValues(), interpolation.fractions, Shader.TileMode.CLAMP));
            canvas.drawRect(e.left - shadowRadiusInternal, e.top + cornerRadiusLT, e.left, e.bottom - cornerRadiusLB, linePaint);

            cornerPaint.setShader(new RadialGradient(e.left + cornerRadiusLT, e.top + cornerRadiusLT,
                    shadowRadiusInternal + cornerRadiusLT,
                    interpolation.intValues(),
                    interpolation.mappingFraction(cornerRadiusLT * 1.0f / (cornerRadiusLT + shadowRadiusInternal),1f),
                    Shader.TileMode.CLAMP));
            canvas.drawRect(e.left - shadowRadiusInternal, e.top - shadowRadiusInternal,
                    e.left + cornerRadiusLT, e.top + cornerRadiusLT, cornerPaint);

            linePaint.setShader(new LinearGradient(0, e.top, 0, e.top - shadowRadiusInternal,
                    interpolation.intValues(), interpolation.fractions, Shader.TileMode.CLAMP));
            canvas.drawRect(e.left + cornerRadiusLT, e.top - shadowRadiusInternal, e.right - cornerRadiusRT, e.top, linePaint);

            cornerPaint.setShader(new RadialGradient(e.right - cornerRadiusRT, e.top + cornerRadiusRT,
                    shadowRadiusInternal + cornerRadiusRT,
                    interpolation.intValues(),
                    interpolation.mappingFraction(cornerRadiusRT * 1.0f / (cornerRadiusRT + shadowRadiusInternal),1f),
                    Shader.TileMode.CLAMP));
            canvas.drawRect(e.right - cornerRadiusRT, e.top - shadowRadiusInternal,
                    e.right + shadowRadiusInternal, e.top + cornerRadiusRT, cornerPaint);

            linePaint.setShader(new LinearGradient(e.right, 0, e.right + shadowRadiusInternal, 0,
                    interpolation.intValues(), interpolation.fractions, Shader.TileMode.CLAMP));
            canvas.drawRect(e.right, e.top + cornerRadiusRT, e.right + shadowRadiusInternal, e.bottom - cornerRadiusRB, linePaint);

            cornerPaint.setShader(new RadialGradient(e.right - cornerRadiusRB, e.bottom - cornerRadiusRB,
                    shadowRadiusInternal + cornerRadiusRB,
                    interpolation.intValues(),
                    interpolation.mappingFraction(cornerRadiusRB * 1.0f / (cornerRadiusRB + shadowRadiusInternal),1f),
                    Shader.TileMode.CLAMP));
            canvas.drawRect(e.right - cornerRadiusRB, e.bottom - cornerRadiusRB,
                    e.right + shadowRadiusInternal, e.bottom + shadowRadiusInternal, cornerPaint);

            linePaint.setShader(new LinearGradient(0, e.bottom, 0, e.bottom + shadowRadiusInternal,
                    interpolation.intValues(), interpolation.fractions, Shader.TileMode.CLAMP));
            canvas.drawRect(e.left + cornerRadiusLB, e.bottom, e.right - cornerRadiusRB, e.bottom + shadowRadiusInternal, linePaint);

            cornerPaint.setShader(new RadialGradient(e.left + cornerRadiusLB, e.bottom - cornerRadiusLB,
                    shadowRadiusInternal + cornerRadiusLB,
                    interpolation.intValues(),
                    interpolation.mappingFraction(cornerRadiusLB * 1.0f / (cornerRadiusLB + shadowRadiusInternal),1f),
                    Shader.TileMode.CLAMP));
            canvas.drawRect(e.left - shadowRadiusInternal, e.bottom - cornerRadiusLB,
                    e.left + cornerRadiusLB, e.bottom + shadowRadiusInternal, cornerPaint);
        }

        shadowInnerPaint.setColor(shadowStartColor);
        float sx=(content.right-content.left+2.0f*extensionInternal)/(content.right-content.left);
        float sy=(content.bottom-content.top+2.0f*extensionInternal)/(content.bottom-content.top);
        canvas.translate(-extensionInternal+offsetX,-extensionInternal+offsetY);
        canvas.scale(sx,sy,e.left+extensionInternal-offsetX,e.top+extensionInternal-offsetY);
        canvas.drawPath(contentPath,shadowInnerPaint);

        canvas.restore();
    }

    private Rect getExtensionAndOffsetShadowRect() {
        Rect rect=new Rect();
        rect.left=content.left-extensionInternal+offsetX;
        rect.top=content.top-extensionInternal+offsetY;
        rect.right=content.right+extensionInternal+offsetX;
        rect.bottom=content.bottom+extensionInternal+offsetY;
        return rect;
    }

    private void buildContentPath() {
        if(content==null) { return; }
        contentPath=new Path();
        contentPath.moveTo(content.left,content.top+cornerRadiusLT);
        if(cornerRadiusLT>0) {
            contentPath.arcTo(content.left,content.top,content.left+cornerRadiusLT*2,
                    content.top+cornerRadiusLT*2,180,90,false);
        }
        contentPath.lineTo(content.right-cornerRadiusRT,content.top);
        if(cornerRadiusRT>0) {
            contentPath.arcTo(content.right-cornerRadiusRT*2,content.top,content.right,
                    content.top+cornerRadiusRT*2,270,90,false);
        }
        contentPath.lineTo(content.right,content.bottom-cornerRadiusRB);
        if(cornerRadiusRB>0) {
            contentPath.arcTo(content.right-cornerRadiusRB*2,content.bottom-cornerRadiusRB*2,content.right,
                    content.bottom,0,90,false);
        }
        contentPath.lineTo(content.left+cornerRadiusLB,content.bottom);
        if(cornerRadiusLB>0) {
            contentPath.arcTo(content.left,content.bottom-cornerRadiusLB*2,content.left+cornerRadiusLB*2,
                    content.bottom,90,90,false);
        }
        contentPath.lineTo(content.left,content.top+cornerRadiusLT);
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
    }

    public Path getContentPath() {
        return contentPath;
    }

    public void setCorner(int corner) {
        setCorner(corner,corner,corner,corner);
    }

    public void setCornerLB(int corner) {
        setCorner(corner,cornerRadiusLT,cornerRadiusRB,cornerRadiusRT);
    }

    public void setCornerLT(int corner) {
        setCorner(cornerRadiusLB,corner,cornerRadiusRB,cornerRadiusRT);
    }

    public void setCornerRB(int corner) {
        setCorner(cornerRadiusLB,cornerRadiusLT,corner,cornerRadiusRT);
    }

    public void setCornerRT(int corner) {
        setCorner(cornerRadiusLB,cornerRadiusLT,cornerRadiusRB,corner);
    }

    public int getCornerLB() {
        return this.cornerRadiusLB;
    }

    public int getCornerLT() {
        return this.cornerRadiusLT;
    }

    public int getCornerRB() {
        return this.cornerRadiusRB;
    }
    public int getCornerRT() {
        return this.cornerRadiusRT;
    }

    public void setCorner(int lb,int lt,int rb,int rt) {
        boolean isChange=false;
        if(lb!=cornerRadiusLB) {
            cornerRadiusLB=lb;
            isChange=true;
        }
        if(lt!=cornerRadiusLT) {
            cornerRadiusLT=lt;
            isChange=true;
        }
        if(rb!=cornerRadiusRB) {
            cornerRadiusRB=rb;
            isChange=true;
        }
        if(rt!=cornerRadiusRT) {
            cornerRadiusRT=rt;
            isChange=true;
        }
        if(isChange) {
            buildContentPath();
            invalidateSelf();
        }
    }

    public void setBackground(Drawable background) {
        if(this.background!=background) {
            this.background=background;
            invalidateSelf();
        }
    }

    public void setShadowRadius(int radius) {
        if(shadowRadius!=radius) {
            shadowRadius=radius;
            shadowSimple = Math.max(5,shadowRadius/30+1);
            shadowRadiusInternal=(int) (shadowRadius*shadowRadiusToInternalScale);
            extensionInternal=extension+shadowRadius-shadowRadiusInternal;
            invalidateSelf();
        }
    }

    public void setContentRect(@NonNull Rect rect) {
        if(!rect.equals(content)) {
            isCustomContent=true;
            content=rect;
            buildContentPath();
            invalidateSelf();
        }
    }

    public Rect getContentRect() {
        return content;
    }

    public void setExtension(int extension) {
        if(this.extension!=extension) {
            this.extension=extension;
            extensionInternal=extension+shadowRadius-shadowRadiusInternal;
            invalidateSelf();
        }
    }

    public void setOffset(int x,int y) {
        boolean isChange=false;
        if(x!=offsetX) {
            offsetX=x;
            isChange=true;
        }
        if(y!=offsetY) {
            offsetY=y;
            isChange=true;
        }
        if(isChange) {
            invalidateSelf();
        }
    }

    public void setOffsetX(int x) {
        setOffset(x,offsetY);
    }

    public void setOffsetY(int y) {
        setOffset(offsetX,y);
    }

    public void setShadowEndColor(int color) {
        if(color!=shadowEndColor) {
            shadowEndColor=color;
            invalidateSelf();
        }
    }

    public void setShadowStartColor(int color,boolean changeEndColor) {
        if(color!=shadowStartColor) {
            shadowStartColor=color;
            if(changeEndColor) {
                shadowEndColor=Color.argb(0,ColorUtil.r(color),ColorUtil.g(color),ColorUtil.b(color));
            }
            invalidateSelf();
        }
    }

    public int getShadowStartColor() {
        return shadowStartColor;
    }

    public int getShadowEndColor() {
        return shadowEndColor;
    }

}
