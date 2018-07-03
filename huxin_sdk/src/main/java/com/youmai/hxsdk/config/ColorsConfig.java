package com.youmai.hxsdk.config;


import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.youmai.hxsdk.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ColorsConfig {

    public static final String GROUP_DEFAULT_NAME = "GroupName:GOURP@#$%^&*()"; //默认群组名的前缀
    public static final String GROUP_EMPTY_MSG = "    "; //群组默认填充消息

    public static final String ColorLifeAppId = "9959f117-df60-4d1b-a354-776c20ffb8c7";  //彩生活服务集团
    public static final String ColorLifeAppName = "彩生活服务集团";  //彩生活服务集团
    public static final String HEAD_ICON_URL = "http://avatar.ice.colourlife.com/";//头像


    private static final String SECRET[] = new String[]{"IGXGh8BKPwjEtbcXD2KN", "IGXGh8BKPwjEtbcXD2KN", "TYHpsLtHeFXYRTekJbVv"};


    //彩管家 APPID TOEKN 定义
    private static final String COLOR_APPID[] = new String[]{"ICEYOUMAI-EF6C-4970-9AED-4CD8E063720F", "ICECGJLS-AOVE-VNU1-Y9JV-CMP6MUH6WCT2", "ICEXCGJ0-5F89-4E17-BC44-7A0DB101B245"};
    private static final String COLOR_TOKEN[] = new String[]{"AXPHrD48LRa8xYVkgV4c", "H13FNtwtT7IjLmxy25IT", "AXPHrD48LRa8xYVkgV4c"};

    //有麦 APPID TOEKN 定义
    private static final String YOUMAI_APPID[] = new String[]{"ICEYOUMAI-EF6C-4970-9AED-4CD8E063720F", "ICECGJLS-AOVE-VNU1-Y9JV-CMP6MUH6WCT2", "ICECGJLS-RF9A-5NY9-EREF-3VKPI6JOVW8J"};
    private static final String YOUMAI_TOKEN[] = new String[]{"AXPHrD48LRa8xYVkgV4c", "H13FNtwtT7IjLmxy25IT", "dKMtXVQ3fJLhWrgh9BEZ"};


    private final static String SOCKET_URL[] = new String[]{"https://openapi-test.colourlife.com/v1/", "https://openapi-test.colourlife.com/v1/", "https://openapi.colourlife.com/v1/"};


    private static String getIceHost() {
        return SOCKET_URL[AppConfig.LAUNCH_MODE];
    }


    public static final String LISHI_SHARECONFIG = getIceHost() + "clsfwopenapi/lishi/config/shareConfig";
    public static final String LISHI_STANDARDCONFIG = getIceHost() + "clsfwopenapi/lishi/config/standardConfig";
    public static final String LISHI_LIST = getIceHost() + "clsfwopenapi/lishi/cqb/fp/list";
    public static final String LISHI_SEND = getIceHost() + "clsfwopenapi/lishi/send";
    public static final String LISHI_OPEN = getIceHost() + "clsfwopenapi/lishi/open";
    public static final String LISHI_GRAB = getIceHost() + "clsfwopenapi/lishi/grab";
    public static final String LISHI_DETAIL = getIceHost() + "clsfwopenapi/lishi/detail";
    public static final String LISHI_SEND_DETAIL = getIceHost() + "clsfwopenapi/lishi/history/send/profile";
    public static final String LISHI_RECEIVE_DETAIL = getIceHost() + "clsfwopenapi/lishi/history/receive/profile";
    public static final String LISHI_SEND_LIST = getIceHost() + "clsfwopenapi/lishi/history/send/list";
    public static final String LISHI_RECEIVE_LIST = getIceHost() + "clsfwopenapi/lishi/history/receive/list";

    public static final String CP_MOBILE_HOST = "http://cpmobile.colourlife.com";
    public static final String CHECK_PAYPWD = "/1.0/caiRedPaket/checkPayPwd";


    private static String getToken() {
        return COLOR_TOKEN[AppConfig.LAUNCH_MODE];
    }

    public static String getAppID() {
        return COLOR_APPID[AppConfig.LAUNCH_MODE];
    }


    private static String getYouMaiToken() {
        return YOUMAI_TOKEN[AppConfig.LAUNCH_MODE];
    }


    public static String getYouMaiAppID() {
        return YOUMAI_APPID[AppConfig.LAUNCH_MODE];
    }


    /**
     * 红包 secret
     */
    public static String getSecret() {
        return SECRET[AppConfig.LAUNCH_MODE];
    }


    /**
     * 彩管家 sign 通用签名
     *
     * @param ts
     */
    private static String sign(long ts) {
        return AppUtils.md5(getAppID() + ts + getToken() + false);
    }


    /**
     * 有麦 sign 通用签名
     *
     * @param ts
     */
    private static String signYouMai(long ts) {
        return AppUtils.md5(getYouMaiAppID() + ts + getYouMaiToken() + false);
    }


    /**
     * 红包接口签名方法
     *
     * @param params
     * @return
     */
    public static String cpMobileSign(@NonNull ContentValues params, String nameSpace) {
        List<String> list = new ArrayList<>();
        try {
            for (Map.Entry<String, Object> entry : params.valueSet()) {
                String key = entry.getKey(); // name
                String value = entry.getValue().toString(); // value
                list.add(key + "=" + value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(nameSpace).append("?");

        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            sb.append(str);
            if (i < list.size() - 1) {
                sb.append("&");
            }
        }
        String sign = sb.toString();

        params.put("sign", AppUtils.md5(sign));


        return AppUtils.md5(sb.toString()).toUpperCase();

    }


    /**
     * 有麦 appid 通用签名
     *
     * @param params
     */
    public static void commonYouMaiParams(ContentValues params) {
        long ts = System.currentTimeMillis() / 1000;
        params.put("ts", ts);
        params.put("appID", getYouMaiAppID());
        params.put("sign", signYouMai(ts));
    }


    /**
     * 彩管家 appid 通用签名
     *
     * @param params
     */
    public static void commonParams(ContentValues params) {
        long ts = System.currentTimeMillis() / 1000;
        params.put("ts", ts);
        params.put("appID", getAppID());
        params.put("sign", sign(ts));
    }

    /**
     * 彩管家 appid 通用签名
     *
     * @param params
     */
    public static void commonParams(Map<String, Object> params) {
        long ts = System.currentTimeMillis() / 1000;
        params.put("ts", ts);
        params.put("appID", getAppID());
        params.put("sign", sign(ts));
    }


    public static String loadUrl(String fileId) {
        String url = AppConfig.ICE_LOAD_PATH;
        String appId = "colourlife";
        String fileToken = "LOCKW3v23#2";
        long ts = System.currentTimeMillis();

        String sign = AppUtils.md5(fileId + appId + ts + fileToken + false);

        StringBuilder sb = new StringBuilder();
        sb.append(url).append(fileId).append("?");
        sb.append("fileid").append("=").append(fileId).append("&");
        sb.append("ts").append("=").append(ts).append("&");
        sb.append("sign").append("=").append(sign);

        return sb.toString();
    }


}
