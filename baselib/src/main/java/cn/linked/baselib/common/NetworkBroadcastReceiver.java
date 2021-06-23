package cn.linked.baselib.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkBroadcastReceiver extends BroadcastReceiver {

    private Map<NetworkListener,Object> listenerMap = new ConcurrentHashMap<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        for(NetworkListener listener : listenerMap.keySet()) {
            listener.networkChanged(AppNetwork.getNetworkState());
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
