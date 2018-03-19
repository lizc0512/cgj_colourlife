package com.youmai.hxsdk.config;


import android.content.Context;

import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.LanguageUtil;

import java.util.Locale;

public class AppConfig {

    /**
     * SDK 版本号
     */
    public static String SDK_VERSION = "V2.5";

    /**
     * 增加版本号参数
     */
    public static String V = "5";
    /**
     * 微信appid
     */
    public final static String weixinAppid = "wx8e315dcc82ecd7eb";
    /**
     * APP ID        1105106672
     * APP KEY    quMI0vPKaMGk62P9
     */
    public final static String QQAppid = "1105106672";
    public final static String QQAppKey = "quMI0vPKaMGk62P9";
    /**
     * 微信appid
     */
    //public final static String weixinSecret = "3c5461d6c547f41ef9026ee03274bfd5";
    /**
     * 高德提供的静态地图的KEY
     */
    public static String staticMapKey = "76ca0c2476c8739d4c85473929c126ec";

    /**
     * 谷歌提供的静态地图的KEY
     */
    public static String googleMapKey = "AIzaSyDkE2Y0XXL2zmRZMlPnJft2xenYDkoBJ5M";

    /**
     * 七牛 SecretKey
     */
    //public static String QiNiuSecretKey = "gcb2e34zD2velqZvz-IQG9Mw6VjetY__3wWcytd_";

    /**
     * goolge admob APP ID
     */
    //public static final String ADMOB_APP_ID = "ca-app-pub-6180836929150661~6849384939";
    //public static final String ADMOB_APP_ID = "ca-app-pub-6180836929150661~5835686163";

    /**
     * pref文件名定义
     */
    public static final String SHARED_PREFERENCES = "sdk_app";

    public final static int SDK_FOR_VENDOR = 1; // 0 : SDK  1: HUXIN APP 2:DIALER APP

    /**
     * HuXin 服务器连接配置
     */
    public final static int LAUNCH_MODE = 2; //0 本地测试,开发版        1服务器测试,测试版           2正式平台,正式版

    private final static String HTTP_HOST[] = new String[]{"http://192.168.0.42:8088/", "http://test2.huxin.biz/", "http://wap.ihuxin.net/"};

    private final static String SHOW_HOST[] = new String[]{"http://192.168.0.42:8989/", "http://test2.jweb.huxin.biz/", "http://api.ihuxin.net/"};

    private final static String SHOW_HOST_SSL[] = new String[]{"https://192.168.0.42:8989/", "https://test2.jweb.huxin.biz/", "https://api.ihuxin.net/"};

    private final static String SOCKET_URL[] = new String[]{"http://192.168.0.42:8000/", "http://test2.im.huxin.biz:8000/", "http://im.ihuxin.net:8000/"};

    private final static String SOCKET_HOST[] = new String[]{"192.168.0.42", "120.24.37.50", "120.77.1.224"};

    private final static String DOWNLOAD_HOST[] = new String[]{"http://test2.file.huxin.biz/", "http://test2.file.huxin.biz/", "http://file.ihuxin.net/"};

    private final static int SOCKET_PORT[] = new int[]{8003, 9951, 9951};//8886


    public static String getHttpHost() {
        return HTTP_HOST[LAUNCH_MODE];
    }

    public static String getShowHost() {
        return SHOW_HOST[LAUNCH_MODE];
    }

    //50环境测试和正式环境支持https
    public static String getShowHostSSL() {
        if (LAUNCH_MODE == 1 || LAUNCH_MODE == 2) {
            return SHOW_HOST_SSL[LAUNCH_MODE];
        }
        return SHOW_HOST[LAUNCH_MODE];
    }

    public static String getSocketUrl() {
        return SOCKET_URL[LAUNCH_MODE];
    }


    public static String getSocketHost() {
        return SOCKET_HOST[LAUNCH_MODE];
    }

    public static int getSocketPort() {
        return SOCKET_PORT[LAUNCH_MODE];
    }

    public static String getDownloadHost() {
        return DOWNLOAD_HOST[LAUNCH_MODE];
    }

    /**
     * 正式平台的非呼信用户发短信
     */
    public static final String SMS_HTTP_HOST = "http://h5.ihuxin.cn/";

    /* 英文版非呼信用户发短信*/
    public static final String SMS_HTTP_HOST_EN = "http://h5.ihuxin.cn/en/";

    /**
     * 图片文件下载地址
     */
    public static final String DOWNLOAD_IMAGE = getDownloadHost();


    /**
     * http post 文件前，获取文件token
     */
    public static final String GET_UPLOAD_FILE_TOKEN = getShowHost() + "jhuxin-openapi/qy/fileUpload/upToken";

    /**
     * 版本更新配置地址
     */
    public static final String APP_UPDATE_CONFIG = getHttpHost() + "svc/app/isnew";

    /**
     * 建议和投诉地址
     */

    public static final String UP_SUGGESSINS = getHttpHost() + "svc/suggest/nologinadvice";

    /**
     * 获取短信验证码
     */
    public static final String SMS_CODE = getShowHost() + "jhuxin-openapi/op/sms/checkcode";

    /**
     * http 登录
     */
    public static final String HTTP_LOGIN = getShowHost() + "jhuxin-openapi/op/login";


    /**
     * 请求用户show数据
     */
    public static final String SHOW_DATA = getShowHost() + "jhuxin-openapi/qy/show/";

    /**
     * 请求show模板数据
     */
    public static final String SHOW_TEMPLET = getShowHost() + "jhuxin-openapi/op/show/models";

    /**
     * 设置用户show数据
     */
    public static final String SETTING_SHOW = getShowHost() + "jhuxin-openapi/op/show/uset";

    /**
     * 取消秀
     */
    public static final String CANCEL_SHOW = getShowHost() + "jhuxin-openapi/op/show/cancel";

    /**
     * 请求代言列表数据
     */
    public static final String REPRESENT_DATA = getShowHost() + "jhuxin-openapi/op/represent";

    /**
     * 加入代言请求
     */
    public static final String REPRESENT_JOIN = getShowHost() + "jhuxin-openapi/op/represent/join";

    /**
     * 取消代言请求
     */
    public static final String REPRESENT_CANCEL = getShowHost() + "jhuxin-openapi/op/represent/cancel";

    /**
     * 代言历史
     */
    public static final String REPRESENT_HISTORY = getShowHost() + "jhuxin-openapi/qy/represent/history";

    /**
     * 获取代言完成奖励
     */
    public static final String REPRESENT_GET_AWARD = getShowHost() + "jhuxin-openapi/op/represent/exchange";

    /**
     * 代言红包雨奖励
     */
    public static final String REPRESENT_RED_PACKET_AWARD = getShowHost() + "jhuxin-openapi/op/represent/takeHxCoin";

    /**
     * 当前代言信息
     */
    public static final String REPRESENT_CURRENT_INFO = getShowHost() + "jhuxin-openapi/qy/represent/canInfo";

    /**
     * 我的卡券请求
     */
    public static final String COUPON_DATA = getShowHost() + "jhuxin-openapi/op/coupon/active";

    /**
     * 已失效的卡券请求
     */
    public static final String DISCOUPON_DATA = getShowHost() + "jhuxin-openapi/op/coupon/notactive";

    /**
     * 卡券详情请求
     */
    public static final String COUPON_DETAIL_DATA = getShowHost() + "jhuxin-openapi/op/coupon/";

    /**
     * 移除清空卡券请求
     */
    public static final String COUPON_REMOVE_DATA = getShowHost() + "jhuxin-openapi/op/coupon/remove";

    /**
     * 兑换卡券请求
     */
    public static final String COUPON_EXCHANGE_DATA = getShowHost() + "jhuxin-openapi/op/coupon/exchange/";

    /**
     * 挂机短信
     */
    public static final String HANGUP_SMS = getShowHost() + "jhuxin-openapi/op/sms/send";

    /**
     * 加入商家附属号码
     */
    public static final String JOIN_MERCHANT = getShowHost() + "jhuxin-openapi/op/pblWorker/join";

    /**
     * 商家附属号码秀详情
     */
    public static final String MERCHANT_DETAIL = getShowHost() + "jhuxin-openapi/op/pblWorker/";

    /**
     * 退出商家附属号码秀
     */
    public static final String EXIT_MERCHANT = getShowHost() + "jhuxin-openapi/op/pblWorker/remove/";

    /**
     * 推送注册
     */
    public static final String PUSH_REGISTER = getShowHost() + "jhuxin-openapi/op/im/register";

    /**
     * 推送消息发送
     */
    public static final String PUSH_MSG = getShowHost() + "jhuxin-openapi/op/im/push";

    /**
     * 获取告警号码
     */
    public static final String ALARM_NUMBERS = getShowHost() + "jhuxin-openapi/qy/alarm/numbers";

    /**
     * 触发告警
     */
    public static final String ALARM_TOUCH = getShowHost() + "jhuxin-openapi/op/alarm/touch";

    /**
     * 广告配置开关
     */
    public static final String CONFIG_AD = getShowHost() + "jhuxin-openapi/qy/config/onoff";

    /**
     * 号码归属地
     */
    public static final String LOCALE_NUMBER = getShowHost() + "jhuxin-openapi/op/phoneplace/";

    /**
     * 存储呼信用户个人信息 -- 需要用户登录
     */
    public static final String USER_INFO_SAVE = getShowHost() + "jhuxin-openapi/op/userstore/save";

    /**
     * 获取呼信用户个人信息 -- 需要用户登录
     */
    public static final String USER_INFO_GET = getShowHost() + "jhuxin-openapi/op/userstore/get";

    /**
     * 获取用户个人信息 -- 不需登录
     */
    public static final String USER_INFO_NOT_LOGIN_GET = getShowHost() + "jhuxin-openapi/qy/userinfo/";

    /**
     * 获取所有呼信用户show
     */
    public static final String ALL_SHOW_INFO = getShowHost() + "jhuxin-openapi/qy/shows";

    /**
     * 获取所有呼信用户信息
     */
    public static final String ALL_USER_INFO = getShowHost() + "jhuxin-openapi/qy/userinfos";

    /**
     * 获取配置信息
     */
    public static final String HNXIN_CONFIG = getShowHost() + "jhuxin-openapi/qy/config/get";

    /**
     * 新增和日活
     */
    public static final String STATS_INSTALL_URL = getShowHost() + "jhuxin-openapi/op/installActiveLog/save";

    /**
     * 通话后屏展示的消息动态列表
     */
    public static final String HOOK_STRATEGY_LIST = getShowHost() + "jhuxin-openapi/qy/show/later";

    /**
     * 弹屏配置开关
     */
    public static final String FLOAT_VIEW_CONFIG = getShowHost() + "jhuxin-openapi/op/czyPhone/takeOffStatus";

    /**
     * 获取钱包账号信息
     */
    public static final String PURSE_INFO_URL = getShowHostSSL() + "jhuxin-openapi/qy/pay/account";

    /**
     * 查询账户清单
     */
    public static final String PURSE_DETAILS = getShowHostSSL() + "jhuxin-openapi/qy/pay/bills";

    /**
     * 获取授权信息
     * ssl
     */
    public static final String PAY_AUTH_INFO = getShowHostSSL() + "jhuxin-openapi/qy/pay/authInfo";

    /**
     * 获取支付宝账号信息
     * ssl
     */
    public static final String PAY_ACCOUNT_INFO = getShowHostSSL() + "jhuxin-openapi/qy/pay/accountInfo";

    /**
     * 用户提现
     * ssl
     */
    public static final String PAY_WITHDRAW_CASH = getShowHostSSL() + "jhuxin-openapi/op/pay/withdrawCash";

    /**
     * 获取积分墙类型
     */
    public static final String AD_OFFER_WALL = getShowHost() + "jhuxin-openapi/qy/integralWall/type";

    /**
     * 获取积分墙列表
     */
    public static final String AD_OFFER_LIST = getShowHost() + "jhuxin-openapi/qy/integralWall/datas";

    /**
     * 获取积分墙详情
     */
    public static final String AD_OFFER_INFO = getShowHost() + "jhuxin-openapi/qy/integralWall/details";

    /**
     * 上传积分墙安装报告
     */
    public static final String AD_OFFER_APP_INSERT = getShowHost() + "jhuxin-openapi/op/integralWall/uploadReport";

    /**
     * 邀请码
     */
    public static final String INVITE_ACCEPT_CODE = getShowHost() + "jhuxin-openapi/op/userInvite/save";
    /**
     * 获取自己的邀请码
     */
    public static final String INVITE_GET_CODE = getShowHost() + "jhuxin-openapi/qy/userInvite/inviteCode";
    /**
     * 是否可以被邀请
     */
    public static final String IS_BE_INVITABLE = getShowHost() + "jhuxin-openapi/qy/userInvite/beInvitedAble";
    /**
     * 获取voip配置
     */
    public static final String VOIP_CONFIG = getShowHost() + "jhuxin-openapi/voip/config";
    /**
     * 触发回拨电话
     */
    public static final String VOIP_ON_CALLBACK = getShowHost() + "jhuxin-openapi/op/voip/callBack";
    /**
     * 触发回拨电话2
     */
    public static final String VOIP_ON_CALLBACK2 = getShowHost() + "jhuxin-openapi/op/voip/callBack2";
    /**
     * 过时，不在用
     * 上报回拨电话
     */
    public static final String VOIP_REPORT_NO_CALLBACK = getShowHost() + "jhuxin-openapi/op/voip/uploadNoCallBack";
    /**
     * 上报回拨电话
     */
    public static final String VOIP_REPORT_CALLBACK = getShowHost() + "jhuxin-openapi/op/voip/upload";
    /**
     * 获取抽奖转盘数据
     */
    public static final String LUCKY_PAN_DATA = getShowHost() + "jhuxin-openapi/qy/lotteryDraw/items";

    /**
     * 抽奖
     */
    public static final String LOTTERY_DRAW = getShowHost() + "jhuxin-openapi/op/lotteryDraw/take";

    /**
     * 兑换抽奖项(暂废弃)
     */
    public static final String LOTTERY_EXCHANGE = getShowHost() + "jhuxin-openapi/op/lotteryDraw/exchange";

    /**
     * 获取签到状态
     */
    public static final String SIGN_IN_STATUS = getShowHost() + "jhuxin-openapi/qy/isSignIn";

    /**
     * 签到
     */
    public static final String SIGN_IN = getShowHost() + "jhuxin-openapi/op/signIn";

    /**
     * 商品充值套餐
     */
    public static final String RECHARGE_COMBO = getShowHost() + "jhuxin-openapi/qy/goods/items";
    /**
     * voip账户信息
     */
    public static final String VOIP_ACCOUNT = getShowHost() + "jhuxin-openapi/qy/voip/account";
    /**
     * v充值卡充值（激活充值卡）
     */
    public static final String VOIP_CARD_ACTIVATE = getShowHost() + "jhuxin-openapi/op/voip/activate";
    /**
     * 获取voip电话状态
     */
    public static final String VOIP_PHONE_STAT = getShowHost() + "jhuxin-openapi/qy/voip/phone/status";
    /**
     * 查询voip充值(激活)记录
     */
    public static final String VOIP_RECHARGE_RECORD = getShowHost() + "jhuxin-openapi/qy/voip/activateRecords ";

    /**
     * 呼信注册用户信息获取
     */
    public static final String WECHAT_INFO = getShowHost() + "jhuxin-openapi/qy/userinfo";

    /**
     * IM消息统计
     */
    public static final String IM_STATISTICS = getShowHost() + "jhuxin-openapi/op/im/upload";

    /**
     * 收藏
     */
    public static final String COLLECT_SINGLE_SAVE = getShowHost() + "jhuxin-openapi/op/imcollect/save";

    /**
     * 查询IM收藏记录
     */
    public static final String COLLECT_QUERY = getShowHost() + "jhuxin-openapi/qy/imcollect/find";


    /**
     * 删除单条IM收藏记录
     */
    public static final String COLLECT_DEL = getShowHost() + "jhuxin-openapi/op/imcollect/del";

    /**
     * 提醒保存
     */
    public static final String REMIND_SAVE = getShowHost() + "jhuxin-openapi/op/imremind/save";

    /**
     * 查询提醒
     */
    public static final String REMIND_QUERY = getShowHost() + "jhuxin-openapi/qy/imremind/find";

    /**
     * 删除提醒
     */
    public static final String REMIND_DEL = getShowHost() + "jhuxin-openapi/op/imremind/del";

    /**
     * 讯飞语音转文字
     */
    public static final String VOICE_TO_TEXT = getShowHost() + "jhuxin-openapi/op/xfyun/volume2Text";

    /**
     * 讯飞文字转语音
     */
    public static final String TEXT_TO_VOICE = getShowHost() + "jhuxin-openapi/op/xfyun/text2Volume";

    /**
     * 统一用户头像宽度和高度
     */
    public static final int IMG_HEADER_W = 128;
    public static final int IMG_HEADER_H = 128;

    /**
     * 微信支付订单
     */
    public static final String WECHAT_ORDER = getShowHostSSL() + "jhuxin-openapi/op/pay/order";

    /**
     * tcp 负载均衡 host
     */
    public static String getTcpHost(Context context, int userId) {
        return getSocketUrl() + "api/v1/sum?userid=" + userId + "&lang=" + LanguageUtil.getLang(context);
    }

    /***
     * sign 签名
     *
     * @param phoneNum
     * @param imei
     * @return
     */
    public static String appSign(String phoneNum, String imei) {
        String channelUrl = "msisdn=" + phoneNum + "&termid=" + imei;
        return AppUtils.md5("00000" + channelUrl + "999999");
    }

    /**
     * 请求用户show数据地址
     */
    public static String getShowUrl(String phone, String version) {
        return SHOW_DATA + phone + "/" + version;
    }


    /**
     * 版本更新， apk下载地址
     *
     * @param context
     * @param filename
     * @return
     */
    public static String getAppUpdateUrl(Context context, String filename) {
        return getHttpHost() + "file/download/app/get"
                + "?filename=" + filename
                + "&channel_id=" + AppUtils.getChannelInfo(context);
    }


    /**
     * 根据用户头像下载地址
     *
     * @return
     */
    public static String getHeaderUrl(Context context, String phone) {
        return DOWNLOAD_IMAGE + phone
                + "?lang=" + LanguageUtil.getLang(context)
                + "&t=" + System.currentTimeMillis();
    }

    /**
     * 获取用户头像下载地址
     *
     * @param w
     * @param h
     * @param phone
     * @return
     */
    public static String getThumbHeaderUrl(Context context, int w, int h, String phone) {
        return DOWNLOAD_IMAGE + phone
                + "?lang=" + LanguageUtil.getLang(context) +
                "&imageView2/0/w/" + w + "/h/" + h +
                "&t=" + System.currentTimeMillis();
    }

    /**
     * 获取用户头像下载地址 后台已拼接好 V=1
     *
     * @param w
     * @param h
     * @return
     */
    public static String getVHeaderUrl(String userUrl, int w, int h) {
        return userUrl + "&imageView2/0/w/" + w + "/h/" + h;
    }

    /**
     * 获取图片下载地址
     *
     * @param fid
     * @return
     */
    public static String getImageUrl(Context context, String fid) {
        return DOWNLOAD_IMAGE + fid
                + "?lang=" + LanguageUtil.getLang(context);
    }


}
