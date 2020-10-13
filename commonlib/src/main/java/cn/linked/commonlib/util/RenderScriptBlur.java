package cn.linked.commonlib.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class RenderScriptBlur implements IBlur {
    private RenderScript mRenderScript;
    private ScriptIntrinsicBlur mBlurScript;
    private Allocation mBlurInput, mBlurOutput;
    @Override
    public boolean prepare(Context context, Bitmap buffer) {
        if (mRenderScript == null) {
            try {
                mRenderScript = RenderScript.create(context);
                mBlurScript = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
            } catch (android.renderscript.RSRuntimeException e) {
                if (isDebug(context)) {
                    throw e;
                } else {
                    // In release mode, just ignore
                    destroy();
                    return false;
                }
            }
        }
        mBlurInput = Allocation.createFromBitmap(mRenderScript, buffer,
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        mBlurOutput = Allocation.createTyped(mRenderScript, mBlurInput.getType());
        return true;
    }
    @Override
    public void blur(Bitmap input, Bitmap output,float radius) {
        mBlurScript.setRadius(radius);
        mBlurInput.copyFrom(input);
        mBlurScript.setInput(mBlurInput);
        mBlurScript.forEach(mBlurOutput);
        mBlurOutput.copyTo(output);
    }

    @Override
    public void destroy() {
        if (mBlurInput != null) {
            mBlurInput.destroy();
            mBlurInput = null;
        }
        if (mBlurOutput != null) {
            mBlurOutput.destroy();
            mBlurOutput = null;
        }
        if (mBlurScript != null) {
            mBlurScript.destroy();
            mBlurScript = null;
        }
        if (mRenderScript != null) {
            mRenderScript.destroy();
            mRenderScript = null;
        }
    }
    // android:debuggable="true" in AndroidManifest.xml (auto set by build tool)
    static Boolean DEBUG = null;

    static boolean isDebug(Context ctx) {
        if (DEBUG == null && ctx != null) {
            DEBUG = (ctx.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
        return DEBUG == Boolean.TRUE;
    }
}
