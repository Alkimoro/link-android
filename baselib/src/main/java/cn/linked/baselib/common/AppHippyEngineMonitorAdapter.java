package cn.linked.baselib.common;

import com.tencent.mtt.hippy.HippyRootView;
import com.tencent.mtt.hippy.adapter.monitor.HippyEngineMonitorAdapter;
import com.tencent.mtt.hippy.adapter.monitor.HippyEngineMonitorEvent;

import java.util.List;

public class AppHippyEngineMonitorAdapter implements HippyEngineMonitorAdapter {
    @Override
    public void reportEngineLoadStart() {

    }

    @Override
    public void reportEngineLoadResult(int code, int loadTime, List<HippyEngineMonitorEvent> loadEvents, Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void reportModuleLoadComplete(HippyRootView rootView, int loadTime, List<HippyEngineMonitorEvent> loadEvents) {

    }

    @Override
    public boolean needReportBridgeANR() {
        return false;
    }

    @Override
    public void reportBridgeANR(String message) {

    }
}
