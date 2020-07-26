package cn.linked.commonlib.jni.diffpatch;

import android.content.Context;

import androidx.annotation.NonNull;

public interface IDiffPatch{

    public static final int ERROR = -1;

    public static final int SUCCESS = 0;


    public int diff(String oldFilePath, String newFilePath, String diffFilePath);

    public int patch(String oldFilePath, String diffFilePath, String newFilePath);
}