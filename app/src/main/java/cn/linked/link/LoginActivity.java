package cn.linked.link;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.tencent.mtt.hippy.HippyEngine;
import com.tencent.mtt.hippy.HippyRootView;

import cn.linked.baselib.BaseActivity;
import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.common.DefaultHippyModuleListener;
import cn.linked.router.common.Route;

@Route(path = "app/loginActivity")
public class LoginActivity extends BaseActivity {

    private HippyRootView hippyRootView;
    private final String hippyComponentName="loginApp";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HippyEngine.ModuleLoadParams params=new HippyEngine.ModuleLoadParams();
        params.context=this;
        params.componentName=hippyComponentName;
        params.jsAssetsPath= LinkApplication.getHippyJsAssetsPath(hippyComponentName);
        hippyRootView=getLinkApplication().getHippyEngine().loadModule(params,new DefaultHippyModuleListener());
        setContentView(hippyRootView);
    }

    @Override
    protected void onDestroy() {
        getLinkApplication().getHippyEngine().destroyModule(hippyRootView);
        super.onDestroy();
    }
}
