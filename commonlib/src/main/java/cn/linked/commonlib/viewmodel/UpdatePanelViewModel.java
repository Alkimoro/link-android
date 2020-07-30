package cn.linked.commonlib.viewmodel;

import androidx.lifecycle.ViewModel;

public class UpdatePanelViewModel extends ViewModel {
    private String targetVersion;
    private String updateDetail;
    private String apkSize;
    private int updateProcess;
    public String getTargetVersion(){
        return targetVersion;
    }
    public String getUpdateDetail(){
        return updateDetail;
    }
    public String getApkSize(){
        return apkSize;
    }
}
