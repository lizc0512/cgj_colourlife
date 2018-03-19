package com.youmai.hxsdk.entity;

import android.content.ContentValues;
import android.content.Context;
import android.os.Build;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.DeviceUtils;
import com.youmai.hxsdk.utils.ScreenUtils;

/**
 * Created by colin on 2017/8/9.
 */


public class FloatViewConfig {
    /**
     * 配置开关版本配置
     */
    private static final int CONFIG_VER = 1;


    enum OS_TYPE {
        STANDARD_ANDROID(0),   //标准android
        EMUI(1),    //华为
        COLOR_OS(2), //oppo
        FUNTOUCH_OS(3), //vivo
        MIUI(4),  //小米
        FLYME(5),  //魅族
        YUN_OS(6), //阿里云OS
        DINGDING_OS(100),//丁丁OS
        IOS(10000);   //ios 使用


        private final int value;

        OS_TYPE(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }


    int android_version;  //android 版本（SDK_INT）
    int os_version;       //厂商系统版本 （VER_INT）
    OS_TYPE os_type;      //系统类型 （自定义）
    int config_version;   //开关配置版本
    String key_config;    //mian_key(自定义)

    String android_name; //android 版本名称
    String os_name;      //厂商系统名称
    String manufacturer; //厂商名称
    String model;       //设备型号
    String phoneNum;    //用户手机号
    String channel;     //用户渠道号
    String packageName; //应用包名
    String sdk_ver;     //呼信SDK 版本
    String screen_size;  //设备分辨率
    String imei;         //手机IMEI
    int appVerCode;         //app version code
    String appVerName;         //app version name
    String key_statistics; //辅助信息 key (自定义)


    public FloatViewConfig(Context context) {
        android_version = Build.VERSION.SDK_INT;
        os_version = getOsVersion();
        os_type = getOsType();
        config_version = CONFIG_VER;

        key_config = AppUtils.md5(String.valueOf(android_version) + os_version
                + os_type.value() + config_version);

        android_name = getAndroidVerName();
        os_name = getOsName();
        manufacturer = Build.MANUFACTURER;
        model = Build.MODEL;
        phoneNum = HuxinSdkManager.instance().getPhoneNum();
        channel = AppUtils.getChannelInfo(context);
        packageName = context.getPackageName();
        sdk_ver = AppConfig.SDK_VERSION;
        screen_size = ScreenUtils.getScreenPixels(context);
        imei = DeviceUtils.getIMEI(context);
        appVerCode = AppUtils.getVerCode(context);
        appVerName = AppUtils.getVersion(context);

        key_statistics = AppUtils.md5(android_name + os_name + manufacturer
                + model + phoneNum + channel + packageName + sdk_ver + screen_size
                + imei + appVerCode + appVerName);
    }

    // TODO: 2017/8/9
    private int getOsVersion() {
        int romCode = 0;
        return romCode;
    }


    // TODO: 2017/8/9
    private String getOsName() {
        String osName = "";
        return osName;
    }

    public static OS_TYPE getOsType() {
        OS_TYPE res;
        String brand = android.os.Build.MANUFACTURER;//手机品牌
        switch (brand.toLowerCase()) {
            case "huawei":
                res = OS_TYPE.EMUI;
                break;
            case "oppo":
                res = OS_TYPE.COLOR_OS;
                break;
            case "vivo":
                res = OS_TYPE.FUNTOUCH_OS;
                break;
            case "xiaomi":
                res = OS_TYPE.MIUI;
                break;
            case "meizu":
                res = OS_TYPE.FLYME;
                break;
            default:
                res = OS_TYPE.STANDARD_ANDROID;
                break;

        }
        return res;
    }


    private String getAndroidVerName() {
        String res;
        switch (Build.VERSION.SDK_INT) {
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH:
                res = "android 4.0";
                break;
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1:
                res = "android 4.0.3";
                break;
            case Build.VERSION_CODES.JELLY_BEAN:
                res = "android 4.1";
                break;
            case Build.VERSION_CODES.JELLY_BEAN_MR1:
                res = "android 4.2";
                break;
            case Build.VERSION_CODES.JELLY_BEAN_MR2:
                res = "android 4.3";
                break;
            case Build.VERSION_CODES.KITKAT:
                res = "android 4.4";
                break;
            case Build.VERSION_CODES.KITKAT_WATCH:
                res = "android 4.4W";
                break;
            case 21:
                res = "android 5.0";
                break;
            case 22:
                res = "android 5.1";
                break;
            case 23:
                res = "android 6.0";
                break;
            case 24:
                res = "android 7.0";
                break;
            case 25:
                res = "android 7.1.1";
                break;
            case 26:
                res = "android 8.0";
                break;
            default:
                res = "unknow";
                break;
        }
        return res;
    }


    public ContentValues getParams() {
        ContentValues params = new ContentValues();
        params.put("androidVersion", android_version);
        params.put("osVersion", os_version);
        params.put("osType", os_type.ordinal());
        params.put("configVersion", config_version);
        params.put("keyConfig", key_config);
        params.put("androidName", android_name);
        params.put("osName", os_name);
        params.put("manufacturer", manufacturer);
        params.put("model", model);
        params.put("phoneNum", phoneNum);
        params.put("channel", channel);
        params.put("packageName", packageName);
        params.put("sdkVer", sdk_ver);
        params.put("screenSize", screen_size);
        params.put("imei", imei);
        params.put("appVerCode", appVerCode);
        params.put("appVerName", appVerName);
        params.put("keyStatistics", key_statistics);

        return params;
    }
}
