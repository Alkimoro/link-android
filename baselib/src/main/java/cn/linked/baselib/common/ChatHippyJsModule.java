package cn.linked.baselib.common;

import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.modules.javascriptmodules.HippyJavaScriptModule;

public interface ChatHippyJsModule extends HippyJavaScriptModule {

    public void onReceiveChatMessage(HippyMap message);

    public void onNetworkInactive();

}
