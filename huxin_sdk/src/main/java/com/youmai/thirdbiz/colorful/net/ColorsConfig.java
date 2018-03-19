package com.youmai.thirdbiz.colorful.net;


import android.content.Context;

import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.LanguageUtil;

public class ColorsConfig {

    private static int LAUNCH_MODE_COLOR = 0; //0 测试       1正式


    public static final String COLOR_BUILD_VERSION = "V20_201702201007";


    private final static String HTTP_HOST[] = new String[]{"http://icetest.colourlife.net:8081/v1/youmaip/", "http://iceapi.colourlife.com:8081/v1/youmaip/"};

    private final static String SHOW_HOST[] = new String[]{"http://icetest.colourlife.net:8081/v1/youmai/", "http://iceapi.colourlife.com:8081/v1/youmai/"};

    private static final String COLOR_APPID[] = new String[]{"ICEYOUMAI-EF6C-4970-9AED-4CD8E063720F", "ICEYOUMAI-631E-4ED8-968D-F0A6F82DBCA7"};

    private static final String COLOR_TOKEN[] = new String[]{"D4DA442B81BC6CC120C6", "033047E9A6DD7E94E2D2"};

    private final static String HTTP_HOST_COLOR[] = new String[]{"http://icetest.colourlife.net:8081/v1/", "http://iceapi.colourlife.com:8081/v1/"};

    public static String getToken() {
        return COLOR_TOKEN[LAUNCH_MODE_COLOR];
    }

    public static String getAppID() {
        return COLOR_APPID[LAUNCH_MODE_COLOR];
    }


    public static String getHttpHost() {
        return HTTP_HOST[LAUNCH_MODE_COLOR];
    }

    public static String getShowHost() {
        return SHOW_HOST[LAUNCH_MODE_COLOR];
    }

    public static String getColorHttpHost() {
        return HTTP_HOST_COLOR[LAUNCH_MODE_COLOR];
    }


    //测试环境
    public static String COLOR_OWNER_HOST = getColorHttpHost() + "owner/simple";
    public static String COLOR_ORG_URL = getColorHttpHost() + "org/batch";
    public static String COLOR_FEED_URL = getColorHttpHost() + "case_task/task";
    public static String COLOR_ADDOWNEAR_URL = getColorHttpHost() + "owner";
    public static String COLOR_GET_REGIONS = getColorHttpHost() + "resource/regions"; //获取省市区


    public static String COLOR_GET_COMMUNITY = getColorHttpHost() + "community/area"; //获取小区
    public static String COLOR_GET_BUILDING = getColorHttpHost() + "building/list"; //获取楼栋
    public static String COLOR_GET_UNITE = getColorHttpHost() + "building/unit/list"; //获取单云号
    public static String COLOR_GET_HOUSE_NUMBER = getColorHttpHost() + "owner/roomno"; //获取门牌号
    public static String COLOR_GET_UUID = getColorHttpHost() + "owner/simple"; //获取UUID


    /**
     * http post 文件前，获取文件token
     */
    public static final String GET_POST_FILE_TOKEN = getHttpHost() + "svc/fileTokens/get";

    /**
     * 版本更新配置地址
     */
    public static final String APP_UPDATE_CONFIG = getHttpHost() + "svc/app/isnew";

    /**
     * 版本更新下载地址
     */
    public static final String APP_UPDATE_DOWNLOAD = getHttpHost() + "svc/app/filename";

    /**
     * 建议和投诉地址
     */

    public static final String UP_SUGGESSINS = getHttpHost() + "svc/suggest/nologinadvice";

    /**
     * 获取短信验证码
     */
    public static final String SMS_CODE = getShowHost() + "op/sms/checkcode";

    /**
     * http 登录
     */
    public static final String HTTP_LOGIN = getShowHost() + "op/login";

    /**
     * 请求用户show数据
     */
    public static final String SHOW_DATA = getShowHost() + "qy/show/";

    /**
     * 请求show模板数据
     */
    public static final String SHOW_TEMPLET = getShowHost() + "op/show/models";

    /**
     * 设置用户show数据
     */
    public static final String SETTING_SHOW = getShowHost() + "op/show/uset";

    /**
     * 请求代言列表数据
     */
    public static final String REPRESENT_DATA = getShowHost() + "op/represent";

    /**
     * 加入代言请求
     */
    public static final String REPRESENT_JOIN = getShowHost() + "op/represent/join";

    /**
     * 我的卡券请求
     */
    public static final String COUPON_DATA = getShowHost() + "op/coupon/active";

    /**
     * 已失效的卡券请求
     */
    public static final String DISCOUPON_DATA = getShowHost() + "op/coupon/notactive";

    /**
     * 卡券详情请求
     */
    public static final String COUPON_DETAIL_DATA = getShowHost() + "op/coupon/";

    /**
     * 移除清空卡券请求
     */
    public static final String COUPON_REMOVE_DATA = getShowHost() + "op/coupon/remove";

    /**
     * 兑换卡券请求
     */
    public static final String COUPON_EXCHANGE_DATA = getShowHost() + "op/coupon/exchange/";

    /**
     * 挂机短信
     */
    public static final String HANGUP_SMS = getShowHost() + "op/sms/send";

    /**
     * 加入商家附属号码
     */
    public static final String JOIN_MERCHANT = getShowHost() + "op/pblWorker/join";

    /**
     * 商家附属号码秀详情
     */
    public static final String MERCHANT_DETAIL = getShowHost() + "op/pblWorker/";

    /**
     * 退出商家附属号码秀
     */
    public static final String EXIT_MERCHANT = getShowHost() + "op/pblWorker/remove/";

    /**
     * 推送注册
     */
    public static final String PUSH_REGISTER = getShowHost() + "op/im/register";

    /**
     * 推送消息发送
     */
    public static final String PUSH_MSG = getShowHost() + "op/im/push";
    /**
     * 获取告警号码
     */
    public static final String ALARM_NUMBERS = getShowHost() + "qy/alarm/numbers?v=1";
    /**
     * 触发告警
     */
    public static final String ALARM_TOUCH = getShowHost() + "op/alarm/touch";

    /**
     * 广告配置开关
     */
    public static final String CONFIG_AD = getShowHost() + "qy/config/onoff";

    /**
     * 号码归属地
     */
    public static final String LOCALE_NUMBER = getShowHost() + "op/phoneplace/";

    /**
     * 存储呼信用户个人信息 -- 需要用户登录
     */
    public static final String USER_INFO_SAVE = getShowHost() + "op/userstore/save";

    /**
     * 获取呼信用户个人信息 -- 需要用户登录
     */
    public static final String USER_INFO_GET = getShowHost() + "op/userstore/get";

    /**
     * 获取用户个人信息 -- 不需登录
     */
    public static final String USER_INFO_NOT_LOGIN_GET = getShowHost() + "qy/userinfo/";

    /**
     * 获取所有呼信用户show
     */
    public static final String ALL_SHOW_INFO = getShowHost() + "qy/shows";

    /**
     * 获取所有呼信用户信息
     */
    public static final String ALL_USER_INFO = getShowHost() + "qy/userinfos/";

    /**
     * 获取配置信息
     */
    public static final String HNXIN_CONFIG = getShowHost() + "qy/config/get";

    /**
     * 新增和日活
     */
    public static final String STATS_INSTALL_URL = getShowHost() + "op/installActiveLog/save";

    /**
     * 请求用户show数据地址
     */
    public static String getShowUrl(String phone, String version) {
        return SHOW_DATA + phone + "/" + version;
    }
    

}
