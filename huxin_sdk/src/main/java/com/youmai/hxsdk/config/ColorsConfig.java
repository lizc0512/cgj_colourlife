package com.youmai.hxsdk.config;


import android.content.ContentValues;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.entity.AuthConfig;
import com.youmai.hxsdk.entity.UploadResult;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.http.OkHttpConnector;
import com.youmai.hxsdk.service.sendmsg.PostFile;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GsonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorsConfig {

    public static final String GROUP_DEFAULT_NAME = "GroupName:GOURP@#$%^&*()"; //默认群组名的前缀
    public static final String GROUP_EMPTY_MSG = "    "; //群组默认填充消息

    public static final String ColorLifeAppId = "9959f117-df60-4d1b-a354-776c20ffb8c7";  //彩生活服务集团
    public static final String ColorLifeAppName = "彩生活服务集团";  //彩生活服务集团
    public static final String HEAD_ICON_URL = "http://avatar.ice.colourlife.com/";//头像

    /**
     * 租户ID
     */
    public static final String CORP_UUID = "a8c58297436f433787725a94f780a3c9"; //彩生活租户ID


    private static final String SECRET[] = new String[]{"IGXGh8BKPwjEtbcXD2KN", "IGXGh8BKPwjEtbcXD2KN", "TYHpsLtHeFXYRTekJbVv"};

    /**
     * 文件微服务上传地址
     */
    private static final String ICE_UPLOAD[] = new String[]{"https://micro-file-test.colourlife.com/v1/pcUploadFile", "https://micro-file-test.colourlife.com/v1/pcUploadFile", "https://micro-file.colourlife.com/v1/pcUploadFile"};

    /**
     * 文件微服务下载地址
     */
    private static final String ICE_DOWNLOAD[] = new String[]{"https://micro-file-test.colourlife.com/v1/down/", "https://micro-file-test.colourlife.com/v1/down/", "https://micro-file.colourlife.com/v1/down/"};

    //彩管家 APPID TOEKN 定义
    private static final String COLOR_APPID[] = new String[]{"ICEYOUMAI-EF6C-4970-9AED-4CD8E063720F", "ICEXCGJ0-5F89-4E17-BC44-7A0DB101B245", "ICEXCGJ0-5F89-4E17-BC44-7A0DB101B245"};
    private static final String COLOR_TOKEN[] = new String[]{"AXPHrD48LRa8xYVkgV4c", "AXPHrD48LRa8xYVkgV4c", "AXPHrD48LRa8xYVkgV4c"};

    //有麦 APPID TOEKN 定义
    private static final String YOUMAI_APPID[] = new String[]{"ICEYOUMAI-EF6C-4970-9AED-4CD8E063720F", "ICECGJLS-AOVE-VNU1-Y9JV-CMP6MUH6WCT2", "ICECGJLS-RF9A-5NY9-EREF-3VKPI6JOVW8J"};
    private static final String YOUMAI_TOKEN[] = new String[]{"AXPHrD48LRa8xYVkgV4c", "H13FNtwtT7IjLmxy25IT", "dKMtXVQ3fJLhWrgh9BEZ"};


    private final static String SOCKET_URL[] = new String[]{"https://openapi-test.colourlife.com/v1/", "https://openapi-test.colourlife.com/v1/", "https://openapi.colourlife.com/v1/"};


    private final static String ICECGJBY_URL[] = new String[]{"https://cgj-backyard-test.colourlife.com",
            "https://cgj-backyard-test.colourlife.com", "https://cgj-backyard.colourlife.com"};


    private static String getIceUpload() {
        return ICE_UPLOAD[AppConfig.LAUNCH_MODE];
    }

    private static String getIceDownload() {
        return ICE_DOWNLOAD[AppConfig.LAUNCH_MODE];
    }

    private static String getIceHost() {
        return SOCKET_URL[AppConfig.LAUNCH_MODE];
    }

    private static String getCgjByHost() {
        return ICECGJBY_URL[AppConfig.LAUNCH_MODE];
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

    private static final String ICE_AUTH = getIceHost() + "authms/auth/app";


    /**
     * 主组织架构
     */
    public static final String CONTACTS_MAIN_DATAS = getIceHost() + "txl2/contacts";


    /**
     * 根据组织架构ID获取下级联系人
     */
    public static final String CONTACTS_All_DATAS = getCgjByHost() + "/app/txl/contacts/childDatas";
    /**
     * 根据关键字查询联系人
     */
    public static final String CONTACTS_PEOPLE_DATAS = getCgjByHost() + "/app/txl/contacts/search";

    /**
     * 子组织架构
     */
    public static final String CONTACTS_CHILD_DATAS = getIceHost() + "txl2/contacts/childDatas";

    /**
     * 搜索联系人
     */
    public static final String CONTACTS_SEARCH = getIceHost() + "/app/txl/contacts/search";

    /**
     * 删除联系人
     */
    public static final String CONTACT_DEL = getIceHost() + "txl2/contacts/";

    /**
     * 彩管家验证支付密码host
     */
    public static final String CP_MOBILE_HOST = "http://cpmobile.colourlife.com";

    /**
     * 彩管家验证支付密码namespace
     */
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
    public static void commonParams(ContentValues params, long ts) {
        params.put("ts", ts);
        params.put("appID", getAppID());
        params.put("sign", sign(ts));
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
        String url = getIceDownload();
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


    public static void postFileToICE(final File file, final String desPhone, final PostFile postFile) {
        String accessToken = HuxinSdkManager.instance().getAccessToken();
        long expireTime = HuxinSdkManager.instance().getExpireTime();

        if (isNeedAuth(accessToken, expireTime)) {
            reqAuth(new IPostListener() {
                @Override
                public void httpReqResult(String response) {
                    AuthConfig bean = GsonUtil.parse(response, AuthConfig.class);
                    if (bean != null && bean.isSuccess()) {

                        String token = bean.getContent().getAccessToken();
                        long time = bean.getContent().getExpireTime();

                        HuxinSdkManager.instance().setAccessToken(token);
                        HuxinSdkManager.instance().setExpireTime(time);
                        HuxinSdkManager.instance().saveUserInfo();

                        upLoadFile(file, token, desPhone, postFile);
                    }
                }
            });
        } else {
            upLoadFile(file, accessToken, desPhone, postFile);
        }
    }


    /**
     * 是否需要鉴权（判断有无鉴权，或者鉴权已经过期）
     *
     * @param accessToken
     * @param expireTime
     * @return
     */
    public static boolean isNeedAuth(String accessToken, long expireTime) {
        long time = System.currentTimeMillis();
        boolean isAuth = false;

        if (expireTime == 0 || TextUtils.isEmpty(accessToken)) {
            isAuth = true;
        } else {
            if (expireTime <= time) {//token过期
                isAuth = true;
            }
        }
        return isAuth;
    }


    /**
     * ICE 鉴权2.0
     *
     * @param listener
     */
    public static void reqAuth(IPostListener listener) {
        String url = ICE_AUTH;

        String appKey = getAppID();
        String token = getToken();
        long ts = System.currentTimeMillis() / 1000;

        ContentValues params = new ContentValues();

        params.put("corp_uuid", CORP_UUID);
        params.put("app_uuid", appKey);
        params.put("signature", AppUtils.md5(appKey + ts + token));

        params.put("timestamp", ts);

        ColorsConfig.commonParams(params, ts);
        OkHttpConnector.httpPost(url, params, listener);
    }

    private static void upLoadFile(File file, String accessToken, final String desPhone, final PostFile postFile) {
        String url = getIceUpload();

        String fileUploadAccount = HuxinSdkManager.instance().getUserName();
        String fileUploadAppName = "彩管家";

        if (file.exists()) {
            Map<String, Object> params = new HashMap<>();
            params.put("auth_ver", "2.0");
            params.put("access_token", accessToken);
            params.put("fileLength", file.length());
            params.put("fileName", file.getName());
            params.put("fileUploadAccount", fileUploadAccount);
            params.put("fileUploadAppName", fileUploadAppName);
            params.put("file", file);

            ColorsConfig.commonParams(params);

            OkHttpConnector.httpPostMultipart(url, params, new IPostListener() {
                @Override
                public void httpReqResult(String response) {
                    UploadResult result = GsonUtil.parse(response, UploadResult.class);
                    if (result != null && result.isSuceess()) {
                        String fileId = result.getContent();
                        if (postFile != null) {
                            postFile.success(fileId, desPhone);
                        }
                    } else {
                        postFile.fail("上传文件服务器出错!!!");
                    }
                }
            });

        }
    }
}
