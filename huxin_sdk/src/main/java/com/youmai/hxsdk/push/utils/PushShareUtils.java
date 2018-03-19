package com.youmai.hxsdk.push.utils;

import android.content.Context;

import com.youmai.hxsdk.utils.AppUtils;


/**
 * Created by fylder on 2017/1/4.
 */

public class PushShareUtils {

    private static final String PUSH_TOKEN = "push_token";//实际token
    private static final String PUSH_LOCAL_TOKEN = "push_local_token";//本地token    "null"代表退出
    private static final String PUSH_SERVER_TOKEN = "push_server_token";//上传到服务的token

    private static final String PUSH_HUAWEI_SERVER = "is_connect_huawei";//是否需要接入华为服务

    /**
     * 推送token
     */
    public static void setPushToken(Context context, String token) {
        AppUtils.setStringSharedPreferences(context, PUSH_TOKEN, token);
    }

    /**
     * 推送token
     */
    public static String getPushToken(Context context) {
        return AppUtils.getStringSharedPreferences(context, PUSH_TOKEN, "");
    }

    /**
     * 推送服务上token
     */
    public static void setPushServerToken(Context context, String token) {
        AppUtils.setStringSharedPreferences(context, PUSH_SERVER_TOKEN, token);
    }

    /**
     * 推送服务上token
     */
    public static String getPushServerToken(Context context) {
        return AppUtils.getStringSharedPreferences(context, PUSH_SERVER_TOKEN, "");
    }

    /**
     * app本地token
     */
    public static void setPushLocalToken(Context context, String token) {
        AppUtils.setStringSharedPreferences(context, PUSH_LOCAL_TOKEN, token);
    }

    /**
     * app本地token
     */
    public static String getPushLocalToken(Context context) {
        return AppUtils.getStringSharedPreferences(context, PUSH_LOCAL_TOKEN, "");
    }

    /**
     * 华为服务
     */
    public static void setPushHuaweiServer(Context context, boolean connect) {
        AppUtils.setBooleanSharedPreferences(context, PUSH_HUAWEI_SERVER, connect);
    }

    /**
     * 华为服务
     */
    public static boolean getPushHuaweiServer(Context context) {
        return AppUtils.getBooleanSharedPreferences(context, PUSH_HUAWEI_SERVER, true);
    }

}
