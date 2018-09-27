package com.tg.coloursteward.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.updateapk.UpdateManager;
import com.youmai.hxsdk.config.ColorsConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import cn.jpush.android.api.JPushInterface;


public class TokenUtils {
    public static String getDeviceInfor(Context context, String longitude, String latitude) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("networkType", getNetworkType(context));//网络类型
            jsonObject.put("imeiId", getImeiId(context));//和手机卡相关信息
            jsonObject.put("simNum", getSimNum(context));//和手机卡相关信息
            jsonObject.put("ipAddress", getIPAddress(context));//ip地址
            jsonObject.put("OsVersionName", getOsVersionName());//系统的名称
            jsonObject.put("OsVersionCode", getOsVersionCode());//系统的版本
            jsonObject.put("macAddress", getMac()); //macd地址
            jsonObject.put("batteryLevel", getBatteryInfor(context));//和手机卡相关信息
            jsonObject.put("ProvidersName", getProvidersName(context));//网络运营商
            jsonObject.put("MANUFACTURER", getDeviceName().toLowerCase());//手机制造商
            jsonObject.put("longitude", longitude);//经度
            jsonObject.put("latitude", latitude);//纬度
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private static String getBatteryInfor(Context context) {
        int level = 0;
        try {
            Intent batteryInfoIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            level = batteryInfoIntent.getIntExtra("level", 0);//电量（0-100）
        } catch (Exception e) {

        }
        return level + "%";
    }


    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return "";
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /***获取当前的网络类型***/
    public static String getNetworkType(Context context) {
        String strNetworkType = "";
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();


                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "3G";
                        } else {
                            strNetworkType = _strSubTypeName;
                        }
                        break;
                }
            }
        }
        return strNetworkType;
    }


    /****获取AndroidId***/
    public static String getAndroirdId(Context context) {
        String androidId = android.provider.Settings.Secure.getString(
                context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        if (TextUtils.isEmpty(androidId)) {
            androidId = "";
        }
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
                imei = tm.getDeviceId();
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
        String str = "";
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (macSerial == null || "".equals(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toLowerCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return macSerial;
    }

    public static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        if (TextUtils.isEmpty(text)) {
            text = "";
        }
        return text;
    }

    public static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

    /**
     * 获取唯一的UUID
     */
    public static String getUUID(Context context) {
        SharedPreferences shared = context.getSharedPreferences(Tools.PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = shared.edit();
        String m_szUniqueID = "";
        if (JPushInterface.isPushStopped(context)) {
            JPushInterface.resumePush(context);
        }
        if (!shared.contains("UniqueID")) {
            m_szUniqueID = JPushInterface.getRegistrationID(context);
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

    /***移动设备信息**/
    public static String getPhoneInfo(Context context) {
        String phoneModel = android.os.Build.MODEL;
        String makeVendor = getDeviceName().toLowerCase();
        String providername = getProvidersName(context);
        JSONObject json = new JSONObject();
        try {
            json.put("phoneModel", phoneModel);
            json.put("makeVendor", makeVendor);
            json.put("providername", providername);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     * 系统的版本名称
     **/
    public static String getOsVersionName() {
        return android.os.Build.ID;
    }

    /**
     * 系统的版本号
     **/
    public static String getOsVersionCode() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 移动设备的名称
     **/
    public static String getDeviceName() {
        return android.os.Build.MANUFACTURER;
    }

    /**
     * 移动设备的品牌
     **/
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 移动设备的型号
     **/
    public static String getDeviceType() {
        return android.os.Build.MODEL;
    }

    /****移动运营商****/
    public static String getProvidersName(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String ProvidersName = null;
        // 返回唯一的用户ID;就是这张卡的编号神马的
        String IMSI = "";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        } else {
            IMSI = telephonyManager.getSubscriberId();
        }
        // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
        if (TextUtils.isEmpty(IMSI)) {
            ProvidersName = "";
        } else {
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                ProvidersName = "中国移动";
            } else if (IMSI.startsWith("46001")) {
                ProvidersName = "中国联通";
            } else if (IMSI.startsWith("46003")) {
                ProvidersName = "中国电信";
            }
        }
        return ProvidersName;
    }

    /***4.0新接口的安全加密以后的请求参数Map**/
    public static Map<String, Object> getNewSaftyMap(Context context, Map<String, Object> paramsMap) {
        String version = UpdateManager.getVersionName(context);
        paramsMap.put("nonce_str", getRandomNonceStr());
        paramsMap.put("native_type", 1);
        paramsMap.put("version", version);
        String buff = "";
        try {
            List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(paramsMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, Object>>() {

                @Override
                public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                    return (o1.getKey()).toString().compareTo((o2.getKey()).toString());
                }
            });
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, Object> item : infoIds) {
                if (null != item && !TextUtils.isEmpty(item.getValue().toString())) {
                    String key = item.getKey();
                    String val = item.getValue().toString();
                    val = URLEncoder.encode(val, "utf-8");
                    val = val.replace(" ", "%20");
                    val = val.replace("*", "%2A");
                    val = val.replace("+", "%2B");
                    buf.append(key + "=" + val);
                    buf.append("&");
                }
            }
            buff = setMD5(buf.toString() + "secret=" + Contants.APP.secertKey).toUpperCase();
            paramsMap.put("signature", buff);
        } catch (Exception e) {
            return paramsMap;
        }
        return paramsMap;
    }

    /***4.0生成8位随机数算法*/
    private static String getRandomNonceStr() {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXZY";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 16; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /***md5加密***/
    public static String setMD5(String string) {
        MessageDigest md5;
        StringBuilder sb = new StringBuilder();
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(string.getBytes("UTF-8"));
            byte[] b = md5.digest();
            for (byte aB : b) {
                int temp = 0xFF & aB;
                String s = Integer.toHexString(temp);
                if (temp <= 0x0F) {
                    s = "0" + s;
                }
                sb.append(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static Map<String, String> getStringMap(Map<String, Object> paramsMap) {
        Iterator<String> it = paramsMap.keySet().iterator();
        Map<String, String> stringMap = new HashMap<>();
        while (it.hasNext()) {
            String key = it.next();
            String value = String.valueOf(paramsMap.get(key));
            stringMap.put(key, value);
        }
        return stringMap;
    }

    /**
     * 审批-金融平台未读审批参数加密
     *
     * @return
     */
    public static Map<String, Object> getNewBalance(Context context, Map<String, Object> paramsMap) {
        String map = null;
        paramsMap.put("cmdno", getRandomNonceStr());
        paramsMap.put("ver", "300");
        String buff = "";
        try {
            List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(paramsMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, Object>>() {
                @Override
                public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                    return (o1.getKey()).toString().compareTo((o2.getKey()).toString());
                }
            });
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, Object> item : infoIds) {
                if (null != item && !TextUtils.isEmpty(item.getValue().toString())) {
                    String key = item.getKey();
                    String val = item.getValue().toString();
                    val = URLEncoder.encode(val, "utf-8");
                    val = val.replace(" ", "%20");
                    val = val.replace("*", "%2A");
                    val = val.replace("+", "%2B");
                    buf.append(key + "=" + val);
                    buf.append("&");
                }
            }
            buf.deleteCharAt(buf.length() - 1);
            buff = setMD5(buf.toString() + ColorsConfig.getAppID());
            paramsMap.put("fp", buff);
        } catch (Exception e) {
            return paramsMap;
        }
        return paramsMap;
    }
}
