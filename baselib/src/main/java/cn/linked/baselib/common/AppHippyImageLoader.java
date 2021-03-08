package cn.linked.baselib.common;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tencent.mtt.hippy.adapter.image.HippyDrawable;
import com.tencent.mtt.hippy.adapter.image.HippyImageLoader;
import com.tencent.mtt.hippy.utils.ContextHolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class AppHippyImageLoader extends HippyImageLoader {
    @Override
    public void fetchImage(String url, Callback requestListener, Object param) {
        Glide.with(ContextHolder.getAppContext()).as(ByteArrayOutputStream.class).decode(ByteArrayDecoder.class)
                .load(url).into(new HippyGlideDrawableTarget(requestListener,url));
    }

    public static class ByteArrayDecoder implements ResourceDecoder<InputStream, ByteArrayOutputStream> {
        @Override
        public boolean handles(@NonNull InputStream source, @NonNull Options options) throws IOException {
            return true;
        }
        @Nullable
        @Override
        public Resource<ByteArrayOutputStream> decode(@NonNull InputStream source, int width, int height, @NonNull Options options) throws IOException {
            ByteArrayOutputStream result=new ByteArrayOutputStream();
            int temp=0;
            while ((temp=source.read())!=-1) {
                result.write(temp);
            }
            return new SimpleResource<>(result);
        }
    }

    public static class HippyGlideDrawableTarget extends CustomTarget<ByteArrayOutputStream> {

        private Callback callback;
        private HippyDrawable hippyDrawable;
        private String sourceDes;

        public HippyGlideDrawableTarget(@NonNull Callback callback,String sourceDes) {
            this.callback=callback;
            hippyDrawable=new HippyDrawable();
            this.sourceDes=sourceDes;
        }

        @Override
        public void onResourceReady(@NonNull ByteArrayOutputStream resource, @Nullable Transition<? super ByteArrayOutputStream> transition) {
//            try {
//                byte[] ss=new byte[resource.available()];
//                resource.read(ss);
//                System.out.println(resource+"============");
//                hippyDrawable.setData(ss);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            hippyDrawable.setData(resource.toByteArray());
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
