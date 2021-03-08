package cn.linked.baselib.common;

import android.util.Log;

import com.tencent.mtt.hippy.HippyEngine;
import com.tencent.mtt.hippy.HippyRootView;
import com.tencent.mtt.hippy.common.HippyJsException;

public class DefaultHippyModuleListener implements HippyEngine.ModuleListener {
    @Override
    public void onInitialized(int statusCode, String msg, HippyRootView hippyRootView) {
        Log.i("HippyModuleInit","code:"+statusCode+";msg:"+msg+";View:"+hippyRootView.getName());
    }

    @Override
    public boolean onJsException(HippyJsException exception) {
        Log.w("HippyJsException",exception.getMessage());
        return false;
    }
}
