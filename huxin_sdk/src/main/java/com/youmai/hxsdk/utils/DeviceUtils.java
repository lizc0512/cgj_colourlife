package com.youmai.hxsdk.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DeviceUtils {

    private static final String TAG = DeviceUtils.class.getSimpleName();

    private DeviceUtils() {
        throw new AssertionError();
    }


    /**
     * 获取设备的mac地址
     *
     * @param context
     * @return
     */
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 获取设备序列号
     *
     * @return
     */
    public static String getSerialNumber() {
        return android.os.Build.SERIAL;
    }

    /**
     * 获取android id
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    /**
     * 获取手机IMSI
     *
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        String imsi = null;
        try {
            TelephonyManager phoneManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imsi = phoneManager.getSubscriberId();
            Log.v(TAG, imsi);
        } catch (SecurityException e) {
            Log.e(TAG, "getIMSI error!");
            imsi = "";
        } catch (Exception e) {
            Log.e(TAG, "getIMSI error!");
            imsi = "";
        }

        if (imsi == null) {
            imsi = "";
        }
        return imsi;
    }

    /**
     * 获取手机IMEI
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = "";
        if (tm != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    imei = tm.getImei();
                } else {
                    imei = tm.getDeviceId();
                }
            }
        }
        return imei;
    }

    /**
     * 获取iccid SIM卡序列号
     *
     * @param context
     * @return
     */
    public static String getICCID(Context context) {
        String iccid = "";
        try {
            TelephonyManager phoneManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            iccid = phoneManager.getSimSerialNumber();
        } catch (SecurityException e) {
            Log.e(TAG, "getICCID error!");
            iccid = "";
        } catch (Exception e) {
            Log.e(TAG, "getICCID error!");
            iccid = "";
        }
        if (iccid == null) {
            iccid = "";
        }
        return iccid;
    }

    public static String getPhoneType() {
        String type = Build.BRAND + " " + Build.MODEL;
        try {
            type = URLEncoder.encode(type, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return type;
    }


    public static int getOsVer() {

        return Build.VERSION.SDK_INT;
    }

}
