package cn.linked.baselib.common;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import cn.linked.baselib.LinkApplication;
import lombok.Getter;

public class AppNetwork {

    @Getter
    private static NetworkBroadcastReceiver networkBroadcastReceiver = new NetworkBroadcastReceiver();

    private static final Map<Integer,NetworkState> networkStateMap = new HashMap<>();
    static {
        networkStateMap.put(TelephonyManager.NETWORK_TYPE_GPRS, NetworkState.NET_2G);
        // TelephonyManager.NETWORK_TYPE_GSM
        networkStateMap.put(16, NetworkState.NET_2G);
        networkStateMap.put(TelephonyManager.NETWORK_TYPE_CDMA, NetworkState.NET_2G);
        networkStateMap.put(TelephonyManager.NETWORK_TYPE_IDEN, NetworkState.NET_2G);
        networkStateMap.put(TelephonyManager.NETWORK_TYPE_1xRTT, NetworkState.NET_2G);
        networkStateMap.put(TelephonyManager.NETWORK_TYPE_EDGE, NetworkState.NET_2G);

        networkStateMap.put(TelephonyManager.NETWORK_TYPE_UMTS, NetworkState.NET_3G);
        // TelephonyManager.NETWORK_TYPE_TD_SCDMA
        networkStateMap.put(17, NetworkState.NET_3G);
        networkStateMap.put(TelephonyManager.NETWORK_TYPE_EVDO_0, NetworkState.NET_3G);
        networkStateMap.put(TelephonyManager.NETWORK_TYPE_EVDO_A, NetworkState.NET_3G);
        networkStateMap.put(TelephonyManager.NETWORK_TYPE_EVDO_B, NetworkState.NET_3G);
        networkStateMap.put(TelephonyManager.NETWORK_TYPE_EHRPD, NetworkState.NET_3G);
        networkStateMap.put(TelephonyManager.NETWORK_TYPE_HSDPA, NetworkState.NET_3G);
        networkStateMap.put(TelephonyManager.NETWORK_TYPE_HSPAP, NetworkState.NET_3G);
        networkStateMap.put(TelephonyManager.NETWORK_TYPE_HSUPA, NetworkState.NET_3G);
        networkStateMap.put(TelephonyManager.NETWORK_TYPE_HSPA, NetworkState.NET_3G);

        networkStateMap.put(TelephonyManager.NETWORK_TYPE_LTE, NetworkState.NET_4G);
        // TelephonyManager.NETWORK_TYPE_IWLAN
        networkStateMap.put(18, NetworkState.NET_4G);

        // TelephonyManager.NETWORK_TYPE_NR
        networkStateMap.put(20, NetworkState.NET_5G);
    }

    public static void registerNetworkBroadcastReceiver(@NonNull Context context) {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkBroadcastReceiver, intentFilter);
    }

    public static void unregisterNetworkBroadcastReceiver(@NonNull Context context) {
        context.unregisterReceiver(networkBroadcastReceiver);
    }

    public static void addNetworkListener(NetworkBroadcastReceiver.NetworkListener listener) {
        networkBroadcastReceiver.addNetworkListener(listener);
    }

    public static void removeNetworkListener(NetworkBroadcastReceiver.NetworkListener listener) {
        networkBroadcastReceiver.removeNetworkListener(listener);
    }

    /**
     * 判断是否有网络连接
     * */
    public static boolean isNetworkConnected() {
        Context context = LinkApplication.getInstance();
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断WIFI网络是否可用
     * */
    public static boolean isWifiConnected() {
        Context context = LinkApplication.getInstance();
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断数据流量是否可用
     * */
    public static boolean isMobileConnected() {
        Context context = LinkApplication.getInstance();
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 获取当前的网络类型信息
     * */
    public static NetworkState getNetworkState() {
        Context context = LinkApplication.getInstance();
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return NetworkState.NO_NETWORK;
        }
        int type = networkInfo.getType();
        if (type == ConnectivityManager.TYPE_WIFI) {
            return NetworkState.WIFI;
        } else {
            TelephonyManager telephony = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            int subType = telephony.getNetworkType();
            NetworkState state = networkStateMap.get(subType);
            if(state == null) {
                state = NetworkState.UNKNOWN;
            }
            return state;
        }
    }

    public enum NetworkState {
        NO_NETWORK,UNKNOWN,WIFI,NET_2G,NET_3G,NET_4G,NET_5G;
    }

}
