package cn.linked.baselib.common;

import android.content.Context;

import androidx.annotation.NonNull;

import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.annotation.HippyMethod;
import com.tencent.mtt.hippy.annotation.HippyNativeModule;
import com.tencent.mtt.hippy.modules.Promise;
import com.tencent.mtt.hippy.modules.nativemodules.HippyNativeModuleBase;

import cn.linked.baselib.LinkApplication;
import cn.linked.baselib.entity.ChatMessage;

@HippyNativeModule(name = "ChatHippyModule",thread = HippyNativeModule.Thread.BRIDGE)
public class ChatHippyModule extends HippyNativeModuleBase {

    private ChatManager chatManager;
    private Context context;

    public ChatHippyModule(HippyEngineContext hippyContext,@NonNull Context context) {
        super(hippyContext);
        this.context = context;
    }

    @HippyMethod(name = "sendChatMessage")
    public void sendChatMessage(String msg,long groupId,Promise promise) {
    }

    public void chatMessageAck(ChatMessage ackMessage) {
    }

    public void saveChatMessage(ChatMessage message) {
    }

    @HippyMethod(name = "getLastSequenceNum")
    public void getLastSequenceNum(Promise promise) {
    }

    @HippyMethod(name = "bindUser")
    public void bindUser(long user,Promise promise) {
    }

    public LinkApplication getApplicationContext() {
        return (LinkApplication) context.getApplicationContext();
    }

}
