package cn.linked.baselib.common;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tencent.mtt.hippy.adapter.image.HippyDrawable;
import com.tencent.mtt.hippy.adapter.image.HippyImageLoader;
import com.tencent.mtt.hippy.utils.ContextHolder;

public class AppHippyImageLoader extends HippyImageLoader {

    @Override
    public HippyDrawable getImage(String source, Object param) {
        if(source!=null&&source.startsWith("/")) {
            if(source.startsWith("/assets/")) {
                source="assets://"+source.substring(8);
            }else {
                source="assets://"+source.substring(1);
            }
        }
        return super.getImage(source, param);
    }

    @Override
    public void fetchImage(String url, Callback requestListener, Object param) {
        Glide.with(ContextHolder.getAppContext()).as(byte[].class)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .load(url).into(new HippyGlideDrawableTarget(requestListener,url));
    }

    public static class HippyGlideDrawableTarget extends CustomTarget<byte[]> {

        private Callback callback;
        private HippyDrawable hippyDrawable;
        private String sourceDes;

        public HippyGlideDrawableTarget(@NonNull Callback callback,String sourceDes) {
            this.callback=callback;
            hippyDrawable=new HippyDrawable();
            this.sourceDes=sourceDes;
        }

        @Override
        public void onResourceReady(@NonNull byte[] resource, @Nullable Transition<? super byte[]> transition) {
            hippyDrawable.setData(resource);
            callback.onRequestSuccess(hippyDrawable);
        }

        @Override
        public void onLoadStarted(@Nullable Drawable placeholder) {
            super.onLoadStarted(placeholder);
            hippyDrawable.setDrawable(placeholder);
            callback.onRequestStart(hippyDrawable);
        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
            super.onLoadFailed(errorDrawable);
            hippyDrawable.setDrawable(errorDrawable);
            callback.onRequestFail(new Throwable("source load failed"),sourceDes);
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {
            hippyDrawable.setDrawable(placeholder);
            callback.onRequestFail(new Throwable("source load canceled"),sourceDes);
        }

    }
}
