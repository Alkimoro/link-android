package cn.linked.baselib.common;

import android.content.Context;

import androidx.annotation.NonNull;

import com.tencent.mtt.hippy.HippyAPIProvider;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.common.Provider;
import com.tencent.mtt.hippy.modules.javascriptmodules.HippyJavaScriptModule;
import com.tencent.mtt.hippy.modules.nativemodules.HippyNativeModuleBase;
import com.tencent.mtt.hippy.uimanager.HippyViewController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatHippyAPIProvider implements HippyAPIProvider {

    private Context context;

    public ChatHippyAPIProvider(@NonNull Context context) {
        this.context=context;
    }

    /**
     * 接口：用来让JavaScript调用Java层的接口
     */
    @Override
    public Map<Class<? extends HippyNativeModuleBase>, Provider<? extends HippyNativeModuleBase>> getNativeModules(HippyEngineContext hippyContext) {
        Map<Class<? extends HippyNativeModuleBase>, Provider<? extends HippyNativeModuleBase>> result=new HashMap<>();
        result.put(ChatHippyModule.class,()->{
            return new ChatHippyModule(hippyContext,context);
        });
        return result;
    }

    /**
     * 接口：Java层用来调用JavaScript里的同名接口
     */
    @Override
    public List<Class<? extends HippyJavaScriptModule>> getJavaScriptModules() {
        List<Class<? extends HippyJavaScriptModule>> result=new ArrayList<>();
        result.add(ChatHippyJsModule.class);
        return result;
    }
    
    /**
     * 接口：用来构造各种JavaScript需要的自定义的View组件
     */
    @Override
    public List<Class<? extends HippyViewController>> getControllers() {
        return null;
    }

}
