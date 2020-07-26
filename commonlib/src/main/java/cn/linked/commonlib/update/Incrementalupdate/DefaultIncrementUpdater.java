package cn.linked.commonlib.update.Incrementalupdate;

import android.app.Application;
import android.content.pm.ApplicationInfo;

import androidx.annotation.NonNull;

import java.io.File;

import cn.linked.commonlib.jni.diffpatch.IDiffPatch;
import cn.linked.commonlib.update.exception.UpdateInterruptException;

public class DefaultIncrementUpdater implements IIncrementalUpdater {

    public static final String TempDir="";

    private IDiffPatch diffPatch;
    private Application application;
    private File sourceAPK;
    private File patchAPK;
    private File fullAPK;
    private String sourceMD5;
    private String curMD5;

    public DefaultIncrementUpdater(@NonNull IDiffPatch diffPatch,@NonNull Application application,
                                   @NonNull File patchAPK,@NonNull String sourceMD5){
        this.diffPatch=diffPatch;
        this.application=application;
        this.sourceMD5=sourceMD5;
        this.patchAPK=patchAPK;
        try{
            String packageName=application.getPackageName();
            ApplicationInfo info=application.getPackageManager().getApplicationInfo(packageName, 0);
            this.sourceAPK = new File(info.sourceDir);
            if(this.sourceAPK==null||!this.sourceAPK.isFile()||!this.patchAPK.isFile()){
                throw new UpdateInterruptException("更新中断 : Updater初始化失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new UpdateInterruptException("更新中断 : Updater初始化失败");
        }
    }

    @Override
    public IDiffPatch getDiffPatch() {
        return this.diffPatch;
    }

    @Override
    public File getSourceAPK() {
        return sourceAPK;
    }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public boolean install() {
        return false;
    }
}
