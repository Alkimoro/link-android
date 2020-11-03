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
import cn.linked.commonlib.util.Interpolator;

public class ShadowDrawable extends Drawable {

    private int shadowStartColor=Color.BLACK;
    private int shadowEndColor=Color.TRANSPARENT;
    private int offsetX=0;
    private int offsetY=0;
    private int extension=0;
    private int shadowRadius=20;

    private int cornerRadiusLT=0;
    private int cornerRadiusLB=0;
    private int cornerRadiusRT=0;
    private int cornerRadiusRB=0;

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

        Interpolator.Pair<Integer> interpolation=Interpolator.calculate(new int[]{shadowStartColor, shadowEndColor},
                0,5,Interpolator.MODE_SIN);

        if(shadowRadius>0) {
            linePaint.setShader(new LinearGradient(e.left, 0, e.left - shadowRadius, 0,
                    interpolation.intValues(), interpolation.fractions, Shader.TileMode.CLAMP));
            canvas.drawRect(e.left - shadowRadius, e.top + cornerRadiusLT, e.left, e.bottom - cornerRadiusLB, linePaint);

            cornerPaint.setShader(new RadialGradient(e.left + cornerRadiusLT, e.top + cornerRadiusLT,
                    shadowRadius + cornerRadiusLT,
                    interpolation.intValues(),
                    interpolation.mappingFraction(cornerRadiusLT * 1.0f / (cornerRadiusLT + shadowRadius),1f),
                    Shader.TileMode.CLAMP));
            canvas.drawRect(e.left - shadowRadius, e.top - shadowRadius,
                    e.left + cornerRadiusLT, e.top + cornerRadiusLT, cornerPaint);

            linePaint.setShader(new LinearGradient(0, e.top, 0, e.top - shadowRadius,
                    interpolation.intValues(), interpolation.fractions, Shader.TileMode.CLAMP));
            canvas.drawRect(e.left + cornerRadiusLT, e.top - shadowRadius, e.right - cornerRadiusRT, e.top, linePaint);

            cornerPaint.setShader(new RadialGradient(e.right - cornerRadiusRT, e.top + cornerRadiusRT,
                    shadowRadius + cornerRadiusRT,
                    interpolation.intValues(),
                    interpolation.mappingFraction(cornerRadiusRT * 1.0f / (cornerRadiusRT + shadowRadius),1f),
                    Shader.TileMode.CLAMP));
            canvas.drawRect(e.right - cornerRadiusRT, e.top - shadowRadius,
                    e.right + shadowRadius, e.top + cornerRadiusRT, cornerPaint);

            linePaint.setShader(new LinearGradient(e.right, 0, e.right + shadowRadius, 0,
                    interpolation.intValues(), interpolation.fractions, Shader.TileMode.CLAMP));
            canvas.drawRect(e.right, e.top + cornerRadiusRT, e.right + shadowRadius, e.bottom - cornerRadiusRB, linePaint);

            cornerPaint.setShader(new RadialGradient(e.right - cornerRadiusRB, e.bottom - cornerRadiusRB,
                    shadowRadius + cornerRadiusRB,
                    interpolation.intValues(),
                    interpolation.mappingFraction(cornerRadiusRB * 1.0f / (cornerRadiusRB + shadowRadius),1f),
                    Shader.TileMode.CLAMP));
            canvas.drawRect(e.right - cornerRadiusRB, e.bottom - cornerRadiusRB,
                    e.right + shadowRadius, e.bottom + shadowRadius, cornerPaint);

            linePaint.setShader(new LinearGradient(0, e.bottom, 0, e.bottom + shadowRadius,
                    interpolation.intValues(), interpolation.fractions, Shader.TileMode.CLAMP));
            canvas.drawRect(e.left + cornerRadiusLB, e.bottom, e.right - cornerRadiusRB, e.bottom + shadowRadius, linePaint);

            cornerPaint.setShader(new RadialGradient(e.left + cornerRadiusLB, e.bottom - cornerRadiusLB,
                    shadowRadius + cornerRadiusLB,
                    interpolation.intValues(),
                    interpolation.mappingFraction(cornerRadiusLB * 1.0f / (cornerRadiusLB + shadowRadius),1f),
                    Shader.TileMode.CLAMP));
            canvas.drawRect(e.left - shadowRadius, e.bottom - cornerRadiusLB,
                    e.left + cornerRadiusLB, e.bottom + shadowRadius, cornerPaint);
        }

        shadowInnerPaint.setColor(shadowStartColor);
        float sx=(content.right-content.left+2.0f*extension)/(content.right-content.left);
        float sy=(content.bottom-content.top+2.0f*extension)/(content.bottom-content.top);
        canvas.translate(-extension+offsetX,-extension+offsetY);
        canvas.scale(sx,sy,e.left+extension-offsetX,e.top+extension-offsetY);
        canvas.drawPath(contentPath,shadowInnerPaint);

        canvas.restore();
    }

    private Rect getExtensionAndOffsetShadowRect() {
        Rect rect=new Rect();
        rect.left=content.left-extension+offsetX;
        rect.top=content.top-extension+offsetY;
        rect.right=content.right+extension+offsetX;
        rect.bottom=content.bottom+extension+offsetY;
        return rect;
    }

    private void buildContentPath() {
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

    public void setCorner(int corner) {
        setCorner(corner,corner,corner,corner);
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

    public void setShadowStartColor(int color,boolean changeEndColor) {
        if(color!=shadowStartColor) {
            shadowStartColor=color;
            if(changeEndColor) {
                shadowEndColor=Color.argb(0,ColorUtil.r(color),ColorUtil.g(color),ColorUtil.b(color));
            }
            invalidateSelf();
        }
    }

}
