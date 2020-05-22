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
        String isshowlead = "isshowlead";//是否展示过引导页面
        String LATITUDE = "str_latitude";//纬度
        String LONGITUDE = "str_longitude";//经度
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
        String THRID_CODE = "thrid_code";//彩之云授权登录code
        String SKINCODE = "skin_code";//皮肤包
        String CORPID = "corp_id";//租户Id
        String CORPNAME = "corp_name";//租户名称
        String ORG_UUID = "org_uuid";//租户Id
        String USER_MOBILE = "user_mobile";//租户Id
        String DEVICE_TOKEN = "device_token";//设备唯一token
        String BANK_LIST = "bank_list";//

        String LATITUDE = "str_latitude";//纬度
        String LONGITUDE = "str_longitude";//经度
        String FRAGMENTMINE = "fragmentmine";//我的页面
        String SALARY_TIME = "salary_time";//工资条打开时间
        String SALARY_ISINPUT = "salary_isinput";//工资条打开状态
        String TINYFRAGMENTTOP = "tinyfragmenttop";//微服务顶部
        String TINYFRAGMENTMID = "tinyfragmentmid";//微服务中部
        String JSFPNUM = "jsfpmoney";//微服务即时分配金额
        String DGZH_ACCOUNT = "dgzh_account";//对公账户金额
        String HomePageAd = "homepagead";
        String ImageType = "imagetype";
        String fragmentminedata = "{\"code\":0,\"message\":\"success\",\"content\":[{\"id\":1,\"data\":[{\"id\":4,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd7de4f644652159.png\",\"url\":\"colourlife:\\/\\/proto?type=redPacket\",\"name\":\"\\u6211\\u7684\\u996d\\u7968\",\"group_id\":1},{\"id\":5,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd84176091219280.png\",\"url\":\"colourlife:\\/\\/proto?type=findPwd\",\"name\":\"\\u627e\\u56de\\u652f\\u4ed8\\u5bc6\\u7801\",\"group_id\":1},{\"id\":2,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd8278f931489523.png\",\"url\":\"https:\\/\\/payroll-hr.colourlife.com\\/redirect\",\"name\":\"\\u6211\\u7684\\u5de5\\u8d44\\u6761\",\"group_id\":1}]},{\"id\":2,\"data\":[{\"id\":1,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd819d0ae5124346.png\",\"url\":\"colourlife:\\/\\/proto?type=invite\",\"name\":\"\\u9080\\u8bf7\",\"group_id\":2},{\"id\":6,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd84cea0f0210390.png\",\"url\":\"colourlife:\\/\\/proto?type=mydownload\",\"name\":\"\\u6211\\u7684\\u4e0b\\u8f7d\",\"group_id\":2},{\"id\":3,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd83307e22424071.png\",\"url\":\"colourlife:\\/\\/proto?type=setting\",\"name\":\"\\u8bbe\\u7f6e\",\"group_id\":2}]}],\"contentEncrypt\":\"\"}";
        String TINYFRAGMENTTOP_CACHE = "{\"code\":0,\"message\":\"success\",\"content\":[{\"id\":2,\"title\":\"\\u4e0a\\u7ebf\\u9762\\u79ef\",\"explain\":\"\\u603b\\u8ba1\\uff08\\u4e07\\u33a1\\uff09\",\"quantity\":128015.44,\"url\":\"colourlife:\\/\\/proto?type=onlinEarea\",\"auth_type\":2},{\"id\":1,\"title\":\"\\u96c6\\u56e2\\u80a1\\u7968\",\"explain\":\"\\u4eca\\u65e5\\u80a1\\u4ef7\",\"quantity\":4.3,\"url\":\"http:\\/\\/image.sinajs.cn\\/newchart\\/hk_stock\\/min\\/01778.gif?20181116164252000017942\",\"auth_type\":1},{\"id\":6,\"title\":\"\\u6211\\u7684\\u996d\\u7968\",\"explain\":\"\\u5f53\\u524d\\u4f59\\u989d\",\"quantity\":0.00,\"url\":\"colourlife:\\/\\/proto?type=redPacket\",\"auth_type\":2},{\"id\":3,\"title\":\"\\u4e0a\\u7ebf\\u5c0f\\u533a\",\"explain\":\"\\u5c0f\\u533a\\u6570\\u91cf\",\"quantity\":6673,\"url\":\"colourlife:\\/\\/proto?type=onlinEarea\",\"auth_type\":2},{\"id\":5,\"title\":\"\\u5f69\\u6167\\u6218\\u51b5\",\"explain\":\"\\u8ba2\\u5355\\u603b\\u6570\",\"quantity\":\"0\",\"url\":\"https:\\/\\/caihui-bishow.colourlife.com\\/orderData\\/orderData.html\",\"auth_type\":2},{\"id\":4,\"title\":\"\\u5373\\u65f6\\u5206\\u914d\",\"explain\":\"\\u5206\\u6210\\u91d1\\u989d\",\"quantity\":0,\"url\":\"colourlife:\\/\\/proto?type=jsfp\",\"auth_type\":2}],\"contentEncrypt\":\"\"}";//微服务顶部
        String TINYFRAGMENTMID_CACHE = "{\"code\":0,\"message\":\"success\",\"content\":[{\"name\":\"\\u9ed8\\u8ba4\\u9996\\u822a\",\"data\":[{\"id\":1,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c17696973f4b683543.png\",\"url\":\"http:\\/\\/mail.oa.colourlife.com:40060\\/login\",\"name\":\"\\u672a\\u8bfb\\u90ae\\u4ef6\",\"auth_type\":1,\"quantity\":0},{\"id\":2,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c176982e9ffd714183.png\",\"url\":\"http:\\/\\/bpm.ice.colourlife.com\\/portal\\/login\\/sso.action?number=0\",\"name\":\"\\u5f85\\u6211\\u5ba1\\u6279\",\"auth_type\":1,\"quantity\":0},{\"id\":44,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd92c6b6f0738846.png\",\"url\":\"http:\\/\\/eqd.backyard.colourlife.com\\/cailife\\/leave\\/index?\",\"name\":\"\\u8bf7\\u5047\",\"auth_type\":2,\"quantity\":null},{\"id\":45,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd9ea7ddf2846424.png\",\"url\":\"http:\\/\\/eqd.oa.colourlife.com\\/cailife\\/sign\\/main\",\"name\":\"\\u7b7e\\u5230\",\"auth_type\":1,\"quantity\":null}]},{\"name\":\"\\u5e38\\u7528\\u5e94\\u7528\",\"data\":[{\"id\":46,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce301f1f01513191.png\",\"url\":\"http:\\/\\/bpm.ice.colourlife.com\\/portal\\/login\\/sso.action\",\"name\":\"\\u5ba1\\u62792.0\",\"auth_type\":1},{\"id\":8,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdb6f1cdaf876743.png\",\"url\":\"http:\\/\\/mail.oa.colourlife.com:40060\\/login\",\"name\":\"\\u90ae\\u4ef63.0\",\"auth_type\":1},{\"id\":47,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce30b7de8b882377.png\",\"url\":\"http:\\/\\/spsso.colourlife.net\\/login.aspx\",\"name\":\"\\u5ba1\\u62791.0\",\"auth_type\":0},{\"id\":7,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cda28929fb159139.png\",\"url\":\"http:\\/\\/eqd.oa.colourlife.com\\/cailife\\/sign\\/main\",\"name\":\"e\\u7b7e\\u5230\",\"auth_type\":1},{\"id\":3,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cda78ce703807480.png\",\"url\":\"http:\\/\\/mbee.oa.colourlife.com:4600\\/home\",\"name\":\"\\u4efb\\u52a1\\u7cfb\\u7edf\",\"auth_type\":1},{\"id\":11,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdc0f1fa34102639.png\",\"url\":\"http:\\/\\/notice.oa.colourlife.com:40063\\/login\",\"name\":\"\\u901a\\u77e5\\u516c\\u544a2.0\",\"auth_type\":1},{\"id\":48,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce0fa0c02a632862.png\",\"url\":\"colourlife:\\/\\/proto?type=dgzh\",\"name\":\"\\u5bf9\\u516c\\u8d26\\u6237\",\"auth_type\":2},{\"id\":49,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5bed35370a961947522.png\",\"url\":\"https:\\/\\/caihui-bishow.colourlife.com\\/orderData\\/orderData.html\",\"name\":\"\\u5f69\\u60e0\\u6218\\u51b5\\u770b\\u677f\",\"auth_type\":1}]},{\"name\":\"\\u5176\\u4ed6\\u5e94\\u7528\",\"data\":[{\"id\":4,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cda8d9b879557352.png\",\"url\":\"http:\\/\\/files.oa.colourlife.com:40064\\/login\",\"name\":\"\\u6587\\u4ef6\\u67dc\",\"auth_type\":1},{\"id\":14,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce35809861452693.png\",\"url\":\"http:\\/\\/m.aparcar.cn\\/oa\\/butler\",\"name\":\"e\\u505c\\u8f66\",\"auth_type\":1},{\"id\":15,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce3726ea75215486.png\",\"url\":\"http:\\/\\/ipos.colourlife.com\\/index.php?s=\\/Home\\/Index\\/oauthlogin.html&access_token=\",\"name\":\"iPOS\",\"auth_type\":1},{\"id\":16,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdbf19cb74764600.png\",\"url\":\"http:\\/\\/case.backyard.colourlife.com:4800\\/home\",\"name\":\"\\u50ac\\u8d39\\u8ddf\\u8e2a\",\"auth_type\":1},{\"id\":17,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/local-5bdfae876de28452853.png\",\"url\":\"https:\\/\\/evisit.colourlife.com\\/cgj\\/bindcustomer\\/bind_account.html\",\"name\":\"\\u7ed1\\u5b9a\\u5f69\\u4e4b\\u4e91\",\"auth_type\":1},{\"id\":18,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdc847b602985042.png\",\"url\":\"http:\\/\\/evisit.colourlife.com\\/cgj\\/index.html\",\"name\":\"\\u4e0a\\u95e8\\u5bb6\\u8bbf\",\"auth_type\":1},{\"id\":21,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdccf06f9a125862.png\",\"url\":\"http:\\/\\/jsq.4008893893.com:8077\",\"name\":\"\\u5f69\\u4f4f\\u5b85\\u8ba1\\u7b97\\u5668\",\"auth_type\":1},{\"id\":22,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdce1b9698336224.png\",\"url\":\"http:\\/\\/eqj51.eqj.colourlife.com\\/login.aspx\",\"name\":\"\\u793e\\u533a\\u7ba1\\u7406\",\"auth_type\":1},{\"id\":25,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdd1283c33438572.png\",\"url\":\"http:\\/\\/cgj.ezxvip.com\\/\",\"name\":\"e\\u88c5\\u4fee\",\"auth_type\":1},{\"id\":27,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdd316ad58193610.png\",\"url\":\"http:\\/\\/caoquewang.colourlife.com\\/\",\"name\":\"e\\u6e05\\u6d01\",\"auth_type\":1},{\"id\":28,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdd3d50669784511.png\",\"url\":\"http:\\/\\/eby.colourlife.com\\/front\",\"name\":\"e\\u4fdd\\u517b\",\"auth_type\":1},{\"id\":29,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdd4dc7ad2140813.png\",\"url\":\"http:\\/\\/erh2.colourlife.net\\/login.aspx\",\"name\":\"e\\u5165\\u4f19\",\"auth_type\":0},{\"id\":30,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdd6424eb0641757.png\",\"url\":\"http:\\/\\/sc2.colourlife.net\\/login.aspx\",\"name\":\"\\u5e02\\u573a\\u90e8\\u7ba1\\u7406\",\"auth_type\":1},{\"id\":32,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/local-5bdfafa45c980917243.png\",\"url\":\"http:\\/\\/xj.colourlife.net:9080\\/apppatrol-ios\",\"name\":\"e\\u5de1\\u67e5\",\"auth_type\":1},{\"id\":33,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdfcd239bc857492.png\",\"url\":\"https:\\/\\/m.eshifu.cn\\/common\\/cgjOauth\",\"name\":\"e\\u7ef4\\u4fee\\u7ba1\\u7406\",\"auth_type\":1},{\"id\":34,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdfe2b9658277908.png\",\"url\":\"http:\\/\\/esfsso.colourlife.net\\/login.aspx\",\"name\":\"e\\u7ef4\\u4fee\\u5ba1\\u6279\",\"auth_type\":0},{\"id\":36,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce001af03b309387.png\",\"url\":\"http:\\/\\/cgj.1lift.cn\\/cgj\\/auth\",\"name\":\"e\\u7535\\u68af\",\"auth_type\":1},{\"id\":37,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce01c14f9b841785.png\",\"url\":\"http:\\/\\/ebj.colourlife.com\\/boot\",\"name\":\"e\\u4fdd\\u6d01\",\"auth_type\":1},{\"id\":38,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce049b1f5f901214.png\",\"url\":\"http:\\/\\/spsso.cgwy.paas.colourlife.com\\/login.aspx\",\"name\":\"\\u57ce\\u5173\\u5ba1\\u6279\",\"auth_type\":1},{\"id\":50,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5bed33915ab13158371.png\",\"url\":\"http:\\/\\/servicing.czy.colourlife.com:40025\\/login\",\"name\":\"\\u6295\\u8bc9\\u62a5\\u4fee2.0\\u7ba1\\u7406\",\"auth_type\":1},{\"id\":51,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce19a1c241931694.png\",\"url\":\"http:\\/\\/amwages.backyard.colourlife.com\\/index.php\\/Home\\/Login\\/login\",\"name\":\"\\u8ba1\\u63d0\\u7cfb\\u7edf\",\"auth_type\":1},{\"id\":52,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce1dc812a2224652.png\",\"url\":\"https:\\/\\/wsq.colourlife.com\\/agent\\/index\",\"name\":\"\\u5fae\\u5546\\u5708\",\"auth_type\":1},{\"id\":43,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce0917facc880507.png\",\"url\":\"http:\\/\\/recruit.cgjhr.test.colourlife.com\\/login\",\"name\":\"\\u62db\\u8058\",\"auth_type\":1},{\"id\":54,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce21448617913433.png\",\"url\":\"http:\\/\\/eba.colourlife.com\\/\",\"name\":\"e\\u5b89\\u5168\",\"auth_type\":1},{\"id\":55,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce2248db8a903286.png\",\"url\":\"http:\\/\\/datacollect.czy.colourlife.com\\/app\\/index.html#\\/activity\",\"name\":\"\\u6d3b\\u52a8\\u91c7\\u96c6\",\"auth_type\":1},{\"id\":42,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/local-5bdfb03ae0c10302586.png\",\"url\":\"http:\\/\\/finance.colourlife.com\\/wx\\/checkOauth2.html\",\"name\":\"\\u989d\\u5ea6\\u5ba1\\u6279\",\"auth_type\":1},{\"id\":56,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce255487db558099.png\",\"url\":\"https:\\/\\/market.colourlife.com\\/app\\/\",\"name\":\"\\u5e02\\u573a\\u8fd0\\u8425\\u5e73\\u53f0\",\"auth_type\":1},{\"id\":35,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdfef591e2222323.png\",\"url\":\"http:\\/\\/eqj.colourlife.com\\/\",\"name\":\"e\\u7eff\\u5316\",\"auth_type\":1},{\"id\":57,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce2621addb439974.png\",\"url\":\"https:\\/\\/echeckin-cgj.colourlife.com\\/login\",\"name\":\"E\\u5165\\u4f192.0\",\"auth_type\":1},{\"id\":58,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce27195c91231961.png\",\"url\":\"https:\\/\\/operation.colourlife.com\\/staff\\/login\",\"name\":\"\\u96c6\\u4e2d\\u7ba1\\u63a7\\u5e73\\u53f0\",\"auth_type\":1},{\"id\":59,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce297c038e802771.png\",\"url\":\"https:\\/\\/sfkb-cgj.colourlife.com\",\"name\":\"\\u6536\\u8d39\\u7cfb\\u7edf\\u770b\\u677f\",\"auth_type\":1},{\"id\":60,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c013ffcb77a2808962.png\",\"url\":\"https:\\/\\/peixun.colourlife.com\\/app\\/index.php?i=1&c=entry&m=uwechat_cms&do=mobile\",\"name\":\"E\\u8d77\\u5b66\",\"auth_type\":2}]}],\"contentEncrypt\":\"\"}";

        //新版彩钱包
        String COLOUR_WALLET_KEYWORD_SIGN = "colour_wallet_keyword_sign";//彩钱包显示饭票还是积分的标识
        String COLOUR_WALLET_ACCOUNT_LIST = "colour_wallet_account_list";//彩钱包的账户列表
        String COLOUR_OLD_WALLET_DIALOG = "colour_old_wallet_dialog";//旧版钱包的弹窗提示
        String COLOUR_AUTH_REAL_NAME = "colour_auth_real_name";//腾讯实名认证

    }
}
