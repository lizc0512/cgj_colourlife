package com.youmai.hxsdk.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
        String imei = getUUID(context);
        if (null == imei) {
            imei = String.valueOf(System.currentTimeMillis());
        }
        return imei;
    }

    public static String getUUID(Context context) {
        SharedPreferences shared = context.getSharedPreferences("park_cache_map", 0);
        SharedPreferences.Editor editor = shared.edit();
        String m_szUniqueID = "";
        if (!shared.contains("UniqueID")) {
            if (TextUtils.isEmpty(m_szUniqueID)) {
                String androidId = getAndroirdId(context);
                String tmDevice = getImeiId(context);
                String tmSerial = getSimNum(context);
                String macAddress = getMac();
                String m_szLongID = androidId + tmDevice + tmSerial + macAddress;
                MessageDigest m = null;
                try {
                    m = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
                byte p_md5Data[] = m.digest();

                for (int i = 0; i < p_md5Data.length; i++) {
                    int b = (0xFF & p_md5Data[i]);
                    if (b <= 0xF)
                        m_szUniqueID += "0";
                    m_szUniqueID += Integer.toHexString(b);
                }
                m_szUniqueID = m_szUniqueID.toLowerCase();
                if (m_szUniqueID.contains("-")) {
                    m_szUniqueID = m_szUniqueID.replace("-", "");
                }
            }
            if (TextUtils.isEmpty(m_szUniqueID)) {
                m_szUniqueID = UUID.randomUUID().toString().replace("-", "");
            }
            editor.putString("UniqueID", m_szUniqueID);
            editor.commit();
        } else {
            m_szUniqueID = shared.getString("UniqueID", UUID.randomUUID().toString().replace("-", ""));
        }
        return m_szUniqueID;
    }

    /****获取AndroidId***/
    public static String getAndroirdId(Context context) {
        String androidId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }


    public static String getImeiId(Context context) {
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


    public static String getSimNum(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String sn = "";
        if (tm != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            } else {
                sn = tm.getSimSerialNumber();
            }
        }
        return sn;
    }

    /**
     * 获取手机的MAC地址
     *
     * @return
     */
    public static String getMac() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
