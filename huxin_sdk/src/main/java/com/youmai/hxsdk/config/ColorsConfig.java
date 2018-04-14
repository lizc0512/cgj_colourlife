package com.youmai.hxsdk.config;


import android.content.ContentValues;

import com.youmai.hxsdk.utils.AppUtils;

public class ColorsConfig {

    public static final String ColorLifeAppId_test = "739ca86c-ea5d-4dad-b8ae-f5277942d281";
    public static final String ColorLifeAppId = "9959f117-df60-4d1b-a354-776c20ffb8c7";  //彩生活服务集团
    public static final String ColorLifeAppName = "彩生活服务集团";  //彩生活服务集团


    private static int LAUNCH_MODE_COLOR = 1; //0 测试       1正式


    //private static final String COLOR_APPID[] = new String[]{"ICEYOUMAI-EF6C-4970-9AED-4CD8E063720F", "ICEYOUMAI-631E-4ED8-968D-F0A6F82DBCA7"};
    private static final String COLOR_APPID[] = new String[]{"ICEXCGJ0-5F89-4E17-BC44-7A0DB101B245", "ICEXCGJ0-5F89-4E17-BC44-7A0DB101B245"};

    //private static final String COLOR_TOKEN[] = new String[]{"D4DA442B81BC6CC120C6", "033047E9A6DD7E94E2D2"};
    private static final String COLOR_TOKEN[] = new String[]{"AXPHrD48LRa8xYVkgV4c", "AXPHrD48LRa8xYVkgV4c"};


    public static String getToken() {
        return COLOR_TOKEN[LAUNCH_MODE_COLOR];
    }

    public static String getAppID() {
        return COLOR_APPID[LAUNCH_MODE_COLOR];
    }


    private static String sign(long ts) {
        return AppUtils.md5(getAppID() + ts + getToken() + false);
    }

    public static void commonParams(ContentValues params) {
        //long ts = 1521095132;
        long ts = System.currentTimeMillis() / 1000;
        params.put("ts", ts);
        params.put("appID", getAppID());
        params.put("sign", sign(ts));
    }


}