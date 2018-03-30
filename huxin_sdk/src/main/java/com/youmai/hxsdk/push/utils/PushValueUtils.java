package com.youmai.hxsdk.push.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * Created by fylder on 2017/1/9.
 */
public class PushValueUtils {

    public static final String FLYME_APPID = "Flyme_AppID";
    public static final String FLYME_APPKEY = "Flyme_AppKey";
    public static final String MI_AppID = "Mi_AppID";
    public static final String MI_AppKey = "Mi_AppKey";

    public static String getValue(Context c, String key) {

        String value;
        try {
            ApplicationInfo ai = c.getPackageManager().getApplicationInfo(c.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            if (bundle.get(key) instanceof String) {
                value = bundle.getString(key);
            } else if (bundle.get(key) instanceof Integer) {
                int longValue = bundle.getInt(key);
                value = String.valueOf(longValue);
            } else if (bundle.get(key) instanceof Float) {
                float floatValue = bundle.getFloat(key);
                value = String.valueOf(floatValue);
            } else {
                value = "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            value = "";
        }
        return value;
    }

}
