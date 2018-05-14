package com.tg.coloursteward.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import static android.content.Context.WIFI_SERVICE;

/**WIFI处理工具类
 * Created by prince70 on 2018/3/29.
 */

public class WiFiTools {
    /**
     * 判断当前是否为WiFi状态
     *
     * @return
     */
    public static boolean isWiFiState(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = null;
        if (connectivityManager != null) {
            activeNetInfo = connectivityManager.getActiveNetworkInfo();
        }
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public static boolean isSpecifyWifi(Context mContext, String SSID) {
        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = null;
        if (wifiManager != null) {
            wifiInfo = wifiManager.getConnectionInfo();
        }
        String ssid = null;
        String ssid_sub = null;
        if (wifiInfo != null) {
            ssid = wifiInfo.getSSID();
            ssid_sub = ssid.substring(1, ssid.length() - 1);
            Log.e("CGJ", "isSpecifyWifi: " + ssid_sub);
            Log.e("CGJ", "isSpecifyWifi: ssid" + ssid);
            Log.e("CGJ", "isSpecifyWifi: SSID" + SSID);
            Log.e("CGJ", "ssid.equals(SSID): " + ssid_sub.equals(SSID));
        }
        if (ssid != null && ssid_sub.equals(SSID)) {
            return true;
        }
        return false;
    }


}
