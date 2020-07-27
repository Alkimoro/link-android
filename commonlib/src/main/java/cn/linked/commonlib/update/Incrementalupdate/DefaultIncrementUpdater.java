package cn.linked.commonlib.update.Incrementalupdate;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.UUID;

import cn.linked.commonlib.jni.diffpatch.IDiffPatch;
import cn.linked.commonlib.update.exception.UpdateInterruptException;
import cn.linked.commonlib.update.util.MD5Validator;

public class DefaultIncrementUpdater implements IIncrementalUpdater {

    public final String TempDir;

    private IDiffPatch diffPatch;
    private Application application;
    private File sourceAPK;
    private File diffAPK;
    private File fullAPK;
    private String sourceMD5;
    private String curMD5;

    public DefaultIncrementUpdater(@NonNull IDiffPatch diffPatch,@NonNull Application application,
                                   @NonNull File diffAPK,@NonNull String sourceMD5){
        this.diffPatch=diffPatch;
        this.application=application;
        this.sourceMD5=sourceMD5;
        this.diffAPK=diffAPK;
        String fullPath=diffAPK.getAbsolutePath();
        this.TempDir=fullPath.substring(fullPath.lastIndexOf(File.separatorChar));
        try{
            String packageName=application.getPackageName();
            ApplicationInfo info=application.getPackageManager().getApplicationInfo(packageName, 0);
            this.sourceAPK = new File(info.sourceDir);
            if(!this.sourceAPK.isFile()||!this.diffAPK.isFile()){
                throw new UpdateInterruptException("更新中断 : Updater初始化失败");
            }
            this.fullAPK=createFullAPK();
        }catch (Exception e){
            e.printStackTrace();
            throw new UpdateInterruptException("更新中断 : Updater初始化失败");
        }
    }

    private File createFullAPK(){
        String filename=UUID.randomUUID().toString().replace("-","")+".apk";
        File fullAPK=new File(TempDir+filename);
        int result=this.diffPatch.patch(sourceAPK.getAbsolutePath(),diffAPK.getAbsolutePath(),fullAPK.getAbsolutePath());
        if(result==IDiffPatch.SUCCESS){
            return fullAPK;
        }else{
            throw new UpdateInterruptException("更新中断 : Updater构建APK失败");
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
        return MD5Validator.md5(fullAPK).equals(sourceMD5);
    }

    @Override
    public boolean install() {
        if(validate()){
            return true;
        }else{
            throw new UpdateInterruptException("更新中断 : APK验证失败");
        }
    }
}
