package cn.linked.commonlib.jni.diffpatch;

import android.util.Log;

import java.io.File;

public class BSDiffPatch implements IDiffPatch {

    static{
        System.loadLibrary("native-lib");
        Log.i("BSDiffPatch","jni library : native-lib load success");
    }

    public native int bsdiff(String oldFilePath, String newFilePath, String diffFilePath);

    public native int bspatch(String oldFilePath, String diffFilePath, String newFilePath);

    @Override
    public int diff(String oldFilePath, String newFilePath, String diffFilePath) {
        File oldFile = new File(oldFilePath);
        if (!oldFile.exists() || oldFile.length() == 0) return IDiffPatch.ERROR;

        File newFile = new File(oldFilePath);
        if (!newFile.exists() || newFile.length() == 0) return IDiffPatch.ERROR;

        return bsdiff(oldFilePath,newFilePath,diffFilePath);
    }

    @Override
    public int patch(String oldFilePath, String diffFilePath, String newFilePath) {
        File oldFile = new File(oldFilePath);
        if (!oldFile.exists() || oldFile.length() == 0) return IDiffPatch.ERROR;

        File newFile = new File(oldFilePath);
        if (!newFile.exists() || newFile.length() == 0) return IDiffPatch.ERROR;

        return bspatch(oldFilePath,diffFilePath,newFilePath);
    }
}
