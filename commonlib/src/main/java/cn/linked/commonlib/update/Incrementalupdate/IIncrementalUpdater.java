package cn.linked.commonlib.update.Incrementalupdate;

import java.io.File;

import cn.linked.commonlib.jni.diffpatch.IDiffPatch;

public interface IIncrementalUpdater {

    public IDiffPatch getDiffPatch();

    public File getSourceAPK();

    public boolean validate();

    public boolean install();
}
