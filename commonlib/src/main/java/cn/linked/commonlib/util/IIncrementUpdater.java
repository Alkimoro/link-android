package cn.linked.commonlib.util;

import java.io.File;

import cn.linked.commonlib.jni.diffpatch.IDiffPatch;

public interface IIncrementUpdater {

    public IDiffPatch getDiffPatch();

    public File getSourceAPK();

    public boolean validate();

    public boolean install();
}
