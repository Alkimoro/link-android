package cn.linked.baselib.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkBroadcastReceiver extends BroadcastReceiver {

    private Map<NetworkListener,Object> listenerMap = new ConcurrentHashMap<>();
    private AppNetwork.NetworkState lastState;

    @Override
    public void onReceive(Context context, Intent intent) {
        AppNetwork.NetworkState currentState = AppNetwork.getNetworkState();
        if(currentState != lastState) {
            lastState = currentState;
            for (NetworkListener listener : listenerMap.keySet()) {
                listener.networkChanged(currentState);
            }
        }
    }

    public void addNetworkListener(NetworkListener listener) {
        if(listener != null) {
            listenerMap.put(listener, listener);
        }
    }

    public void removeNetworkListener(NetworkListener listener) {
        if(listener != null) {
            listenerMap.remove(listener);
        }
    }

    public static interface NetworkListener {
        void networkChanged(AppNetwork.NetworkState currentState);
    }

}
