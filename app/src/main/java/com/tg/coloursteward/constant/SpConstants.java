package com.tg.coloursteward.constant;

/**
 * @name ${lizc}
 * @class name：com.colourlife.safelife.constants
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/6/14 16:38
 * @change
 * @chang time
 * @class describe
 */
public interface SpConstants {
    interface UserModel {
        String ISLOGIN = "isLogin";//是否登录
        String DIFFERENCE = "difference";//ts值
        String UPDATE = "update";
        String APKNAME = "apkname";
        String DOWNLOADERVERSION = "downloaderversion";
        String MICRODATA = "microdata";
        String MICROVIEWPAGERITEM = "microvpitem";
        String ACCOUNT_UUID = "account_uuid";
        String VERSION_NAME = "version_name";
        String ISNAMEAUTH = "nameauth";
        String ISUSERAJM = "userajm";
        String UPDATETIME_IMG = "updatetime_img";
        String SCANCODEOFFDATA = "scancodeoffdata";
        String HOMEDATA = "homedata";
        String HOMEDATACLEAR = "homedataclear";
        String NoHAVEPWD = "nohavepwd";//用户信息没有返回密码,需要提示
        String WAREHOUSECACHE = "warehousecache";//快递入仓缓存数据
    }

    interface MicroContant {
        String DATASHOW = "datashow";
    }

    interface accessToken {
        String accssToken = "access_token2";
        String refreshAccssToken = "refresh_token2";
        String tokenType = "token_type2";
        String auth2Username = "auth2username";
        String auth2Token = "auth2Token";
        String auth2CurrentTime = "auth2currenttime";
        String auth2Expires_in = "auth2expires_in";
        String authToken = "authtoken";
        String authOpenId = "authopenid";
        String authCurrentTime = "authcurrenttime";
        String authExpires_in = "authexpires_in";
        String authms2Token = "authmstoken";//应用鉴权2.0
    }

    interface storage {
        String DELIVERYAREA = "delivery_area";//彩快递管理页面小区
        String DELIVERYHOME = "delivery_home";//彩快递管理页面数据
        String DELIVERYNAME = "delivery_name";//彩快递小区名字
        String DELIVERYUUID = "delivery_uuid";//彩快递小区uuid
        String DELIVERYSHOW = "delivery_show";//是否展示快递数据
        String INVENTORYDATA = "inventory_data";//盘点页面数据
        String INVENTORYDELIVERY = "inventoryDeliveryList";//扫码盘点数据
        String THRID_CODE = "thrid_code";//彩之云授权登录code
        String CORPID = "corp_id";//租户Id
        String CORPNAME = "corp_name";//租户名称
        String CORPDATA = "corp_data";//租户数据
        String ORG_UUID = "org_uuid";//部门ID
        String DEVICE_TOKEN = "device_token";//设备唯一token

        String ORGNAME = "org_name";//部门名称
        String JSFPNUM = "jsfpmoney";//微服务即时分配金额
        String DGZH_ACCOUNT = "dgzh_account";//对公账户金额

        //新版彩钱包
        String COLOUR_WALLET_KEYWORD_SIGN = "colour_wallet_keyword_sign";//彩钱包显示饭票还是积分的标识
        String COLOUR_WALLET_ACCOUNT_LIST = "colour_wallet_account_list";//彩钱包的账户列表
        String COLOUR_AUTH_REAL_NAME = "colour_auth_real_name";//腾讯实名认证

    }
}
