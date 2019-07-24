package com.tg.coloursteward.constant;

import android.os.Environment;

/**
 * 全局变量
 */
public interface Contants {

    interface URl {

        //////////////    正式地址   //////////////////////////////
//        String URL_ICETEST = "https://openapi.colourlife.com/v1";//2.0
//        String URL_CPMOBILE = "http://cpmobile.colourlife.com";
//        String URL_CAIHUI = "https://caihui-bishow.colourlife.com";
//        String URL_OAUTH2 = "https://oauth2-cgj.colourlife.com";
//        String SINGLE_DEVICE = "https://single.colourlife.com";
//        String URL_NEW = "https://cgj-backyard.colourlife.com";
//        String URL_ICESTAFF = "https://staff-ice.colourlife.com";
//        String CLIENT_SECRET = "t2o0a1xl2lOmoPi4tuHf5uw4VZloXGs7y1Kd0Yoq";
//        String URL_QRCODE = "https://qrcode.colourlife.com";
//        String URL_IMPUSH = "https://impush-cgj.colourlife.com";
//        String VERSION_ADDRESS = "https://version.colourlife.com";
//        String URL_H5OAUTH = "https://oauth-czy.colourlife.com";
//        String environment = "release";
//        String cqj_appid = "327494513335603200";
//        String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTFnAR7ORLx0jGzf9Ux1We7yHvRi+kQXKSRmtgBjDCXQzakGm2mrb6EupCkDbUcj4BUs7S7zm/rICQuVNC9fujeJGjcNWRg0XWVtm90XpbTqfKiXzGDHI9W8aULYZ3of/JJ9lyCyjqjigyCdLBPtQ27gOuboDzQuieR2ywPHawzQIDAQAB";

        ////////////////  测试地址   ///////////////////////////////////
        String URL_ICETEST = "https://openapi-test.colourlife.com/v1";//2.0e
        String URL_CPMOBILE = "http://cpmobile-czytest.colourlife.com";
        String URL_CAIHUI = "https://caihui-bishow.colourlife.com";
        String URL_OAUTH2 = "https://oauth2-cgj-test.colourlife.com";
        String SINGLE_DEVICE = "https://single-czytest.colourlife.com";
        String URL_NEW = "https://cgj-backyard-test.colourlife.com";
        String URL_ICESTAFF = "http://staff.ice.test.colourlife.com";
        String CLIENT_SECRET = "xlsfrQS5R49upmfZbhlsrUzAt9HDA5K4ptLYsqK5";
        String URL_QRCODE = "http://qrcode-czytest.colourlife.com";
        String URL_IMPUSH = "https://impush-cgj-test.colourlife.com";
        String VERSION_ADDRESS = "https://version-czytest.colourlife.com";
        String URL_H5OAUTH = "https://oauth-czytest.colourlife.com";
        String environment = "debug";
        String cqj_appid = "323521861252157440";
        String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDZDqnWph9LxtD0zgtGAYTTf2jY" +
                "eV+ni5F1o0w3Fag4OOD1YHCRUCXIsFy+iJYmuPf5vMkZrkoiJmKBfkaIzNlrJZzH" +
                "zq+LsPQNCF86p1nLsuHbkWNvyjOEPn/CUryP2Kxme4S+eEqLIeNwp70VOaMuPmRo" +
                "EZxMDAgvc6Z0DWsVdQIDAQAB";
    }

    interface APP {
        String captchaURL = URl.URL_NEW + "/app/home/captcha/start";
        String validateURL = URl.URL_NEW + "/app/home/login/verify";
        /***彩管家4.0加密的秘钥***/
        String secertKey = "gbiwgbiwkgnkwgnkjbkkrthmnjwjgeh";
    }

    interface EMPLOYEE_LOGIN {
        String key = "EmployeeLogin_key";
        String secret = "EmployeeLogin_secret";
    }

    interface LOGO {
        //首页消息更新
        int CLEAR_HOMELIST = 30;

        int UPDATE_HOMELIST = 35;
    }

    interface PARAMETER {
        /**
         * 红包余额
         */
        String BALANCE = "balance";
        /**
         * 对公账户余额
         */
        String PUBLIC_ACCOUNT = "public_account";
        /**
         * 对公账户支付方atid
         */
        String PAY_ATID = "pay_atid";
        /**
         * 对公账户支付方ano
         */
        String PAY_ANO = "pay_ano";
        /**
         * 对公账户支付方typeName
         */
        String PAY_TYPE_NAME = "pay_type_name";
        /**
         * 对公账户支付方name
         */
        String PAY_NAME = "pay_name";
        /**
         * 对公账户收款方atid
         */
        String ACCEPT_ATID = "accept_atid";
        /**
         * 对公账户收款方ano
         */
        String ACCEPT_ANO = "accept_ano";
        /**
         * 对公账户收款方typeName
         */
        String ACCEPT_TYPE_NAME = "accept_type_name";
        /**
         * OA账号
         */
        String OA = "OA";
        /**
         * 给同事发红包（colleague）/转账到彩之云（czy）
         */
        String TRANSFERTO = "transferTo";
        /**
         * OaID
         */
        String USERID = "OaID";
        /**
         * mobile
         */
        String MOBILE = "mobile";
        /**
         * 提现金额
         */
        String WITHDRAW_AMOUNT = "withdrawAmount";
        /**
         * 发红包捎一句话
         */
        String TRANSFERNOTE = "transferNote";
        /**
         * 彩之云小区
         */
        String COMMUNITY = "community";
        /**
         * 持卡人
         */
        String CARDHOLDER = "cardholder";

        /**
         * 银行名称
         */
        String BANK_NAME = "bankName";

        /**
         * 银行卡号
         */
        String CARD_NUMBER = "cardNumber";
        /**
         * 银行ID号
         */
        String BANK_ID = "bankId";

        /**
         * 用户的银行卡ID
         */
        String BANK_CARD_ID = "bankCardId";
        String CALLBACKSCANRESULT = "CallBackScanResult";
    }

    interface Html5 {
        String HEAD_ICON_URL = "http://avatar.ice.colourlife.com";//头像
        String CALLBACKDeviceID = "CALLBACKDeviceID";
        String YJ = "http://mail.oa.colourlife.com:40060/login";
        String SP = "http://bpm.ice.colourlife.com/portal/login/sso.action";//2.0
        String  QIANDAO = "http://eqd.oa.colourlife.com/cailife/sign/main";
    }

    interface storage {
        String THRID_CODE = "thrid_code";//彩之云授权登录code
        String TICKET = "ticket";
        String ACCOUNT = "account";
        String DGZH_ACCOUNT = "dgzh_account";
        String AREAHOME = "area_home";
        String STOCKHOME = "stock_home";
        String TICKETHOME = "ticket_home";
        String COMMUNITYHOME = "community_home";
        String PERFORMANCEHOME = "performance_home";
        String ACCOUNTHOME = "account_home";
        String SKINCODE = "skin_code";//皮肤包
        String CORPID = "corp_id";//租户Id
        String APPAUTH = "app_auth";//appAuth
        String APPAUTHTIME = "app_auth_time";//appAuthtime
        String APPAUTH_1 = "app_aut_1";//appAuth  1.0
        String APPAUTHTIME_1 = "app_auth_time_1";//appAuthtime  1.0
        String ORGNAME = "org_name";//OrgName
        String ORGID = "org_id";//Orgid
        String ADVLIST = "advlist";//轮播图
        String ALIAS = "alias";//ALIAS
        String Tags = "tags";//Tags
        String ORGTYPE = "org_type";//Tags
        String PUBLIC_LIST = "public_list";//对公账户搜索历史列表
        String JTJJB = "jtjjb";//对公账户搜索历史列表
        String DEVICE_TOKEN = "device_token";//设备唯一token
        String LOGOIN_PHONE = "login_phone";//登录手机号
        String LOGOIN_PASSWORD = "login_password";//登录密码
        String LATITUDE = "str_latitude";//纬度
        String LONGITUDE = "str_longitude";//经度
        String FRAGMENTMINE = "fragmentmine";//我的页面
        String SALARY_TIME = "salary_time";//工资条打开时间
        String SALARY_ISINPUT = "salary_isinput";//工资条打开状态
        String TINYFRAGMENTTOP = "tinyfragmenttop";//微服务顶部
        String TINYFRAGMENTMID = "tinyfragmentmid";//微服务中部
        String JSFPNUM = "jsfpmoney";//微服务即时分配金额
        String HomePageAd = "homepagead";
        String ImageType = "imagetype";
        String fragmentminedata = "{\"code\":0,\"message\":\"success\",\"content\":[{\"id\":1,\"data\":[{\"id\":4,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd7de4f644652159.png\",\"url\":\"colourlife:\\/\\/proto?type=redPacket\",\"name\":\"\\u6211\\u7684\\u996d\\u7968\",\"group_id\":1},{\"id\":5,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd84176091219280.png\",\"url\":\"colourlife:\\/\\/proto?type=findPwd\",\"name\":\"\\u627e\\u56de\\u652f\\u4ed8\\u5bc6\\u7801\",\"group_id\":1},{\"id\":2,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd8278f931489523.png\",\"url\":\"https:\\/\\/payroll-hr.colourlife.com\\/redirect\",\"name\":\"\\u6211\\u7684\\u5de5\\u8d44\\u6761\",\"group_id\":1}]},{\"id\":2,\"data\":[{\"id\":1,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd819d0ae5124346.png\",\"url\":\"colourlife:\\/\\/proto?type=invite\",\"name\":\"\\u9080\\u8bf7\",\"group_id\":2},{\"id\":6,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd84cea0f0210390.png\",\"url\":\"colourlife:\\/\\/proto?type=mydownload\",\"name\":\"\\u6211\\u7684\\u4e0b\\u8f7d\",\"group_id\":2},{\"id\":3,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd83307e22424071.png\",\"url\":\"colourlife:\\/\\/proto?type=setting\",\"name\":\"\\u8bbe\\u7f6e\",\"group_id\":2}]}],\"contentEncrypt\":\"\"}";
        String TINYFRAGMENTTOP_CACHE = "{\"code\":0,\"message\":\"success\",\"content\":[{\"id\":2,\"title\":\"\\u4e0a\\u7ebf\\u9762\\u79ef\",\"explain\":\"\\u603b\\u8ba1\\uff08\\u4e07\\u33a1\\uff09\",\"quantity\":128015.44,\"url\":\"colourlife:\\/\\/proto?type=onlinEarea\",\"auth_type\":2},{\"id\":1,\"title\":\"\\u96c6\\u56e2\\u80a1\\u7968\",\"explain\":\"\\u4eca\\u65e5\\u80a1\\u4ef7\",\"quantity\":4.3,\"url\":\"http:\\/\\/image.sinajs.cn\\/newchart\\/hk_stock\\/min\\/01778.gif?20181116164252000017942\",\"auth_type\":1},{\"id\":6,\"title\":\"\\u6211\\u7684\\u996d\\u7968\",\"explain\":\"\\u5f53\\u524d\\u4f59\\u989d\",\"quantity\":0.00,\"url\":\"colourlife:\\/\\/proto?type=redPacket\",\"auth_type\":2},{\"id\":3,\"title\":\"\\u4e0a\\u7ebf\\u5c0f\\u533a\",\"explain\":\"\\u5c0f\\u533a\\u6570\\u91cf\",\"quantity\":6673,\"url\":\"colourlife:\\/\\/proto?type=onlinEarea\",\"auth_type\":2},{\"id\":5,\"title\":\"\\u5f69\\u6167\\u6218\\u51b5\",\"explain\":\"\\u8ba2\\u5355\\u603b\\u6570\",\"quantity\":\"0\",\"url\":\"https:\\/\\/caihui-bishow.colourlife.com\\/orderData\\/orderData.html\",\"auth_type\":2},{\"id\":4,\"title\":\"\\u5373\\u65f6\\u5206\\u914d\",\"explain\":\"\\u5206\\u6210\\u91d1\\u989d\",\"quantity\":0,\"url\":\"colourlife:\\/\\/proto?type=jsfp\",\"auth_type\":2}],\"contentEncrypt\":\"\"}";//微服务顶部
        String TINYFRAGMENTMID_CACHE = "{\"code\":0,\"message\":\"success\",\"content\":[{\"name\":\"\\u9ed8\\u8ba4\\u9996\\u822a\",\"data\":[{\"id\":1,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c17696973f4b683543.png\",\"url\":\"http:\\/\\/mail.oa.colourlife.com:40060\\/login\",\"name\":\"\\u672a\\u8bfb\\u90ae\\u4ef6\",\"auth_type\":1,\"quantity\":0},{\"id\":2,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c176982e9ffd714183.png\",\"url\":\"http:\\/\\/bpm.ice.colourlife.com\\/portal\\/login\\/sso.action?number=0\",\"name\":\"\\u5f85\\u6211\\u5ba1\\u6279\",\"auth_type\":1,\"quantity\":0},{\"id\":44,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd92c6b6f0738846.png\",\"url\":\"http:\\/\\/eqd.backyard.colourlife.com\\/cailife\\/leave\\/index?\",\"name\":\"\\u8bf7\\u5047\",\"auth_type\":2,\"quantity\":null},{\"id\":45,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd9ea7ddf2846424.png\",\"url\":\"http:\\/\\/eqd.oa.colourlife.com\\/cailife\\/sign\\/main\",\"name\":\"\\u7b7e\\u5230\",\"auth_type\":1,\"quantity\":null}]},{\"name\":\"\\u5e38\\u7528\\u5e94\\u7528\",\"data\":[{\"id\":46,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce301f1f01513191.png\",\"url\":\"http:\\/\\/bpm.ice.colourlife.com\\/portal\\/login\\/sso.action\",\"name\":\"\\u5ba1\\u62792.0\",\"auth_type\":1},{\"id\":8,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdb6f1cdaf876743.png\",\"url\":\"http:\\/\\/mail.oa.colourlife.com:40060\\/login\",\"name\":\"\\u90ae\\u4ef63.0\",\"auth_type\":1},{\"id\":47,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce30b7de8b882377.png\",\"url\":\"http:\\/\\/spsso.colourlife.net\\/login.aspx\",\"name\":\"\\u5ba1\\u62791.0\",\"auth_type\":0},{\"id\":7,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cda28929fb159139.png\",\"url\":\"http:\\/\\/eqd.oa.colourlife.com\\/cailife\\/sign\\/main\",\"name\":\"e\\u7b7e\\u5230\",\"auth_type\":1},{\"id\":3,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cda78ce703807480.png\",\"url\":\"http:\\/\\/mbee.oa.colourlife.com:4600\\/home\",\"name\":\"\\u4efb\\u52a1\\u7cfb\\u7edf\",\"auth_type\":1},{\"id\":11,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdc0f1fa34102639.png\",\"url\":\"http:\\/\\/notice.oa.colourlife.com:40063\\/login\",\"name\":\"\\u901a\\u77e5\\u516c\\u544a2.0\",\"auth_type\":1},{\"id\":48,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce0fa0c02a632862.png\",\"url\":\"colourlife:\\/\\/proto?type=dgzh\",\"name\":\"\\u5bf9\\u516c\\u8d26\\u6237\",\"auth_type\":2},{\"id\":49,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5bed35370a961947522.png\",\"url\":\"https:\\/\\/caihui-bishow.colourlife.com\\/orderData\\/orderData.html\",\"name\":\"\\u5f69\\u60e0\\u6218\\u51b5\\u770b\\u677f\",\"auth_type\":1}]},{\"name\":\"\\u5176\\u4ed6\\u5e94\\u7528\",\"data\":[{\"id\":4,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cda8d9b879557352.png\",\"url\":\"http:\\/\\/files.oa.colourlife.com:40064\\/login\",\"name\":\"\\u6587\\u4ef6\\u67dc\",\"auth_type\":1},{\"id\":14,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce35809861452693.png\",\"url\":\"http:\\/\\/m.aparcar.cn\\/oa\\/butler\",\"name\":\"e\\u505c\\u8f66\",\"auth_type\":1},{\"id\":15,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce3726ea75215486.png\",\"url\":\"http:\\/\\/ipos.colourlife.com\\/index.php?s=\\/Home\\/Index\\/oauthlogin.html&access_token=\",\"name\":\"iPOS\",\"auth_type\":1},{\"id\":16,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdbf19cb74764600.png\",\"url\":\"http:\\/\\/case.backyard.colourlife.com:4800\\/home\",\"name\":\"\\u50ac\\u8d39\\u8ddf\\u8e2a\",\"auth_type\":1},{\"id\":17,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/local-5bdfae876de28452853.png\",\"url\":\"https:\\/\\/evisit.colourlife.com\\/cgj\\/bindcustomer\\/bind_account.html\",\"name\":\"\\u7ed1\\u5b9a\\u5f69\\u4e4b\\u4e91\",\"auth_type\":1},{\"id\":18,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdc847b602985042.png\",\"url\":\"http:\\/\\/evisit.colourlife.com\\/cgj\\/index.html\",\"name\":\"\\u4e0a\\u95e8\\u5bb6\\u8bbf\",\"auth_type\":1},{\"id\":21,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdccf06f9a125862.png\",\"url\":\"http:\\/\\/jsq.4008893893.com:8077\",\"name\":\"\\u5f69\\u4f4f\\u5b85\\u8ba1\\u7b97\\u5668\",\"auth_type\":1},{\"id\":22,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdce1b9698336224.png\",\"url\":\"http:\\/\\/eqj51.eqj.colourlife.com\\/login.aspx\",\"name\":\"\\u793e\\u533a\\u7ba1\\u7406\",\"auth_type\":1},{\"id\":25,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdd1283c33438572.png\",\"url\":\"http:\\/\\/cgj.ezxvip.com\\/\",\"name\":\"e\\u88c5\\u4fee\",\"auth_type\":1},{\"id\":27,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdd316ad58193610.png\",\"url\":\"http:\\/\\/caoquewang.colourlife.com\\/\",\"name\":\"e\\u6e05\\u6d01\",\"auth_type\":1},{\"id\":28,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdd3d50669784511.png\",\"url\":\"http:\\/\\/eby.colourlife.com\\/front\",\"name\":\"e\\u4fdd\\u517b\",\"auth_type\":1},{\"id\":29,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdd4dc7ad2140813.png\",\"url\":\"http:\\/\\/erh2.colourlife.net\\/login.aspx\",\"name\":\"e\\u5165\\u4f19\",\"auth_type\":0},{\"id\":30,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdd6424eb0641757.png\",\"url\":\"http:\\/\\/sc2.colourlife.net\\/login.aspx\",\"name\":\"\\u5e02\\u573a\\u90e8\\u7ba1\\u7406\",\"auth_type\":1},{\"id\":32,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/local-5bdfafa45c980917243.png\",\"url\":\"http:\\/\\/xj.colourlife.net:9080\\/apppatrol-ios\",\"name\":\"e\\u5de1\\u67e5\",\"auth_type\":1},{\"id\":33,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdfcd239bc857492.png\",\"url\":\"https:\\/\\/m.eshifu.cn\\/common\\/cgjOauth\",\"name\":\"e\\u7ef4\\u4fee\\u7ba1\\u7406\",\"auth_type\":1},{\"id\":34,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdfe2b9658277908.png\",\"url\":\"http:\\/\\/esfsso.colourlife.net\\/login.aspx\",\"name\":\"e\\u7ef4\\u4fee\\u5ba1\\u6279\",\"auth_type\":0},{\"id\":36,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce001af03b309387.png\",\"url\":\"http:\\/\\/cgj.1lift.cn\\/cgj\\/auth\",\"name\":\"e\\u7535\\u68af\",\"auth_type\":1},{\"id\":37,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce01c14f9b841785.png\",\"url\":\"http:\\/\\/ebj.colourlife.com\\/boot\",\"name\":\"e\\u4fdd\\u6d01\",\"auth_type\":1},{\"id\":38,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce049b1f5f901214.png\",\"url\":\"http:\\/\\/spsso.cgwy.paas.colourlife.com\\/login.aspx\",\"name\":\"\\u57ce\\u5173\\u5ba1\\u6279\",\"auth_type\":1},{\"id\":50,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5bed33915ab13158371.png\",\"url\":\"http:\\/\\/servicing.czy.colourlife.com:40025\\/login\",\"name\":\"\\u6295\\u8bc9\\u62a5\\u4fee2.0\\u7ba1\\u7406\",\"auth_type\":1},{\"id\":51,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce19a1c241931694.png\",\"url\":\"http:\\/\\/amwages.backyard.colourlife.com\\/index.php\\/Home\\/Login\\/login\",\"name\":\"\\u8ba1\\u63d0\\u7cfb\\u7edf\",\"auth_type\":1},{\"id\":52,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce1dc812a2224652.png\",\"url\":\"https:\\/\\/wsq.colourlife.com\\/agent\\/index\",\"name\":\"\\u5fae\\u5546\\u5708\",\"auth_type\":1},{\"id\":43,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce0917facc880507.png\",\"url\":\"http:\\/\\/recruit.cgjhr.test.colourlife.com\\/login\",\"name\":\"\\u62db\\u8058\",\"auth_type\":1},{\"id\":54,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce21448617913433.png\",\"url\":\"http:\\/\\/eba.colourlife.com\\/\",\"name\":\"e\\u5b89\\u5168\",\"auth_type\":1},{\"id\":55,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce2248db8a903286.png\",\"url\":\"http:\\/\\/datacollect.czy.colourlife.com\\/app\\/index.html#\\/activity\",\"name\":\"\\u6d3b\\u52a8\\u91c7\\u96c6\",\"auth_type\":1},{\"id\":42,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/local-5bdfb03ae0c10302586.png\",\"url\":\"http:\\/\\/finance.colourlife.com\\/wx\\/checkOauth2.html\",\"name\":\"\\u989d\\u5ea6\\u5ba1\\u6279\",\"auth_type\":1},{\"id\":56,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce255487db558099.png\",\"url\":\"https:\\/\\/market.colourlife.com\\/app\\/\",\"name\":\"\\u5e02\\u573a\\u8fd0\\u8425\\u5e73\\u53f0\",\"auth_type\":1},{\"id\":35,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cdfef591e2222323.png\",\"url\":\"http:\\/\\/eqj.colourlife.com\\/\",\"name\":\"e\\u7eff\\u5316\",\"auth_type\":1},{\"id\":57,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce2621addb439974.png\",\"url\":\"https:\\/\\/echeckin-cgj.colourlife.com\\/login\",\"name\":\"E\\u5165\\u4f192.0\",\"auth_type\":1},{\"id\":58,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce27195c91231961.png\",\"url\":\"https:\\/\\/operation.colourlife.com\\/staff\\/login\",\"name\":\"\\u96c6\\u4e2d\\u7ba1\\u63a7\\u5e73\\u53f0\",\"auth_type\":1},{\"id\":59,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1ce297c038e802771.png\",\"url\":\"https:\\/\\/sfkb-cgj.colourlife.com\",\"name\":\"\\u6536\\u8d39\\u7cfb\\u7edf\\u770b\\u677f\",\"auth_type\":1},{\"id\":60,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c013ffcb77a2808962.png\",\"url\":\"https:\\/\\/peixun.colourlife.com\\/app\\/index.php?i=1&c=entry&m=uwechat_cms&do=mobile\",\"name\":\"E\\u8d77\\u5b66\",\"auth_type\":2}]}],\"contentEncrypt\":\"\"}";
        String MICRODATA = "{\"code\":0,\"message\":\"success\",\"content\":[{\"type\":\"1\",\"content\":[{\"name\":\"\",\"data\":[{\"uuid\":\"3ae61f382bce44a2a4a7ac2247325745\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364490?fileid=3364490&ts=1563848496438&sign=4bbe4ff1196c4a6fb1a4760ee80c2ede\",\"redirect_url\":\"\",\"auth_type\":\"1\"}]}],\"id\":1},{\"type\":\"2\",\"content\":[{\"name\":\"\\u6570\\u636e\",\"data\":[{\"uuid\":\"communityStatistics\",\"name\":\"\\u4e0a\\u7ebf\\u9762\\u79ef\\uff08\\u4e07\\u33a1\\uff09\",\"data\":\"138815.75\",\"redirect_url\":\"colourlife:\\/\\/proto?type=onlinEarea\",\"auth_type\":\"0\"},{\"uuid\":\"caihuiOrderStatistics\",\"name\":\"\\u4eca\\u65e5\\u5f69\\u60e0\\u8ba2\\u5355\",\"data\":\"2925\",\"redirect_url\":\"http:\\/\\/caihui.colourlife.com\\/data_view\\/#\\/\",\"auth_type\":\"0\"},{\"uuid\":\"cshShares\",\"name\":\"\\u96c6\\u56e2\\u80a1\\u7968\",\"data\":\"5.55\",\"redirect_url\":\"http:\\/\\/image.sinajs.cn\\/newchart\\/hk_stock\\/min\\/01778.gif?20190723201822000095685\",\"auth_type\":\"0\"}]}],\"id\":2},{\"type\":\"3\",\"content\":[{\"name\":\"\\u5e38\\u7528\\u5e94\\u7528\",\"data\":[{\"uuid\":\"49f4a5f9ee3c45b88c07b4558ea5c51a\",\"name\":\"\\u7b7e\\u5230\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364567?fileid=3364567&ts=1563848496838&sign=b4377cd688ee10987f5b96d181ffe572\",\"redirect_url\":\"http:\\/\\/eqd.oa.colourlife.com\\/cailife\\/sign\\/main\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"59927103bdd7428fa4e2dcb806b96a01\",\"name\":\"\\u90ae\\u4ef6\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364620?fileid=3364620&ts=1563848496983&sign=8c8bbc261198fa7d4864ac93e1f9364d\",\"redirect_url\":\"http:\\/\\/mail.oa.colourlife.com:40060\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"4b8b87adff5f41f7acd3a7b4e753c93f\",\"name\":\"\\u5ba1\\u6279\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364634?fileid=3364634&ts=1563848497120&sign=efab9b50d9dd0c69fa2d3a83703bc022\",\"redirect_url\":\"http:\\/\\/bpm.ice.colourlife.com\\/portal\\/login\\/sso.action?number=0\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"adcfcf393d804e5898a1a3ecd89ffe55\",\"name\":\"\\u901a\\u77e5\\u516c\\u544a\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364647?fileid=3364647&ts=1563848497250&sign=6d8263b114eb70f89f5e3cb0fd01f036\",\"redirect_url\":\"http:\\/\\/notice.oa.colourlife.com:40063\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"}]},{\"name\":\"\\u534f\\u540c\\u529e\\u516c\",\"data\":[{\"uuid\":\"7a11a273311543d48dbf9e9ae672e4fd\",\"name\":\"\\u4efb\\u52a1\\u7cfb\\u7edf\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364662?fileid=3364662&ts=1563848497387&sign=7979971dce9b288c2e70514756683311\",\"redirect_url\":\"http:\\/\\/mbee.oa.colourlife.com:4600\\/home\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"efcd9da4a6304067a31b8ddab0045064\",\"name\":\"\\u989d\\u5ea6\\u5ba1\\u6279\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364673?fileid=3364673&ts=1563848497544&sign=95ba93d70255405f3b1853d06175f776\",\"redirect_url\":\"http:\\/\\/finance.colourlife.com\\/wx\\/checkOauth2.html\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"52705414718c4f2e99c8d013deacfd03\",\"name\":\"\\u6587\\u4ef6\\u67dc\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364683?fileid=3364683&ts=1563848497684&sign=ba2050fd94a25ff4986ef89643a57534\",\"redirect_url\":\"http:\\/\\/files.oa.colourlife.com:40064\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"}]},{\"name\":\"\\u7269\\u4e1a\\u7ba1\\u7406\",\"data\":[{\"uuid\":\"9b288e4aa73947239dda6bb3581e3c3c\",\"name\":\"\\u6295\\u8bc9\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364699?fileid=3364699&ts=1563848497837&sign=4eae52793187caf067dcace711920bf4\",\"redirect_url\":\"http:\\/\\/tsbxmain.colourlife.net\\/login.aspx\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"2b73a89bdc214aa983e1575941e960e1\",\"name\":\"\\u4fdd\\u517b\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364705?fileid=3364705&ts=1563848497981&sign=6813a1412f1edf0596ac1f40cf7d03a4\",\"redirect_url\":\"http:\\/\\/eby.colourlife.com\\/front\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"e1a1370d03624a67a07fc9747805170c\",\"name\":\"\\u7eff\\u5316\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364712?fileid=3364712&ts=1563848498176&sign=c41200d8504f7683b336d7b2616a474c\",\"redirect_url\":\"http:\\/\\/eqj.colourlife.com\\/\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"dd71d7143a4b44d1b12631dfcd668758\",\"name\":\"\\u505c\\u8f66\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364725?fileid=3364725&ts=1563848498313&sign=cd492f777a66378ebbee2757ca9936ae\",\"redirect_url\":\"http:\\/\\/m.aparcar.cn\\/oa\\/butler\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"ab096af8682c438cb64a88f73b5e9a42\",\"name\":\"\\u5165\\u4f19\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369364?fileid=3369364&ts=1563848498455&sign=830ecb9cf9df9d82da1165a629fb4b7e\",\"redirect_url\":\"https:\\/\\/echeckin-cgj.colourlife.com\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"a8c1a407876d41b5a5bcb1081ffc47fc\",\"name\":\"\\u7ef4\\u4fee\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369392?fileid=3369392&ts=1563848498587&sign=1c7166a09b6da2f7628778af09124f9b\",\"redirect_url\":\"https:\\/\\/m.eshifu.cn\\/common\\/cgjOauth\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"76cae307a7de450fa2b31ab8636c4177\",\"name\":\"\\u88c5\\u4fee\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369405?fileid=3369405&ts=1563848498785&sign=aabd1287d7b707013a5cec354bc86c8f\",\"redirect_url\":\"http:\\/\\/cgj.ezxvip.com\\/\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"53223e7e9d99479db8a172607cc7e4d7\",\"name\":\"\\u7535\\u68af\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369419?fileid=3369419&ts=1563848498947&sign=a08d26dad64fa5385df2e437809c51ba\",\"redirect_url\":\"http:\\/\\/cgj.1lift.cn\\/cgj\\/auth\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"366b008f99674bc1a2625dea5577add1\",\"name\":\"\\u6536\\u8d39\\u7ba1\\u7406\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369440?fileid=3369440&ts=1563848499090&sign=9762c595454f89d085bedc80ef36ba18\",\"redirect_url\":\"http:\\/\\/ipos.colourlife.com\\/index.php?s=\\/Home\\/Index\\/oauthlogin.html&access_token=\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"dacfd9821b7f4339af88125b7bd597fe\",\"name\":\"\\u5fae\\u5546\\u5708\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369600?fileid=3369600&ts=1563848499233&sign=e7ce2373b21e2129ef7a64fd064d73c9\",\"redirect_url\":\"https:\\/\\/wsq.colourlife.com\\/agent\\/index\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"a62a2888bb0e47f1a821856603373a4b\",\"name\":\"\\u96c6\\u4e2d\\u7ba1\\u63a7\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369629?fileid=3369629&ts=1563848499363&sign=bbc2e2c65d1a10d09053c33699092e9e\",\"redirect_url\":\"https:\\/\\/operation.colourlife.com\\/staff\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"67001c324e9d458f917431d48e319c41\",\"name\":\"\\u6d3b\\u52a8\\u91c7\\u96c6\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369653?fileid=3369653&ts=1563848499551&sign=643274a9bac5da3aadd8ec1b3aaf7733\",\"redirect_url\":\"http:\\/\\/datacollect.czy.colourlife.com\\/app\\/index.html#\\/activity\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"3d8a3c760e0046969f6cbdeefd0dd2f3\",\"name\":\"\\u6284\\u8868\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369680?fileid=3369680&ts=1563848499694&sign=731ce90a978ce64d6c60776321c31bba\",\"redirect_url\":\"http:\\/\\/metering.charge.colourlife.com:40026\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"7c4175c771644c74b66b3c46791cfc15\",\"name\":\"\\u50ac\\u8d39\\u8ddf\\u8e2a\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369705?fileid=3369705&ts=1563848499823&sign=fbaef8982b088581061973eccd629ee2\",\"redirect_url\":\"http:\\/\\/case.backyard.colourlife.com:4800\\/home\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"c46486a0521b48ecb011818ae986700d\",\"name\":\"\\u6536\\u8d39\\u7cfb\\u7edf\\u770b\\u677f\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369787?fileid=3369787&ts=1563848499963&sign=4ec5db205c1cc5cc41017cb22c214202\",\"redirect_url\":\"https:\\/\\/sfkb-cgj.colourlife.com\",\"auth_type\":\"2\",\"quantity\":\"0\"}]},{\"name\":\"\\u4eba\\u4e8b\\u7ba1\\u7406\",\"data\":[{\"uuid\":\"460fe01100b445659a0ff0db6a5c13b0\",\"name\":\"\\u62db\\u8058\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369829?fileid=3369829&ts=1563848500127&sign=8c040ce2c278dc61d8596404acfa6407\",\"redirect_url\":\"https:\\/\\/zhaopin-cgj.colourlife.com\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"79cf2c988bcd4a35a785bd2b2d7fb5c3\",\"name\":\"E\\u542f\\u5b66\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369864?fileid=3369864&ts=1563848500265&sign=9813f59bc93ff6d7e4489b82cd06b34a\",\"redirect_url\":\"https:\\/\\/peixun.colourlife.com\\/app\\/index.php?i=1&c=entry&m=uwechat_cms&do=mobile\",\"auth_type\":\"2\",\"quantity\":\"0\"}]},{\"name\":\"\\u5176\\u4ed6\",\"data\":[{\"uuid\":\"ab9cc8887a7444de94d91705b22451c3\",\"name\":\"\\u5bf9\\u516c\\u8d26\\u6237\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369881?fileid=3369881&ts=1563848500402&sign=bf074d9ee023b34f23e7c91099f5967f\",\"redirect_url\":\"colourlife:\\/\\/proto?type=dgzh\",\"auth_type\":\"0\",\"quantity\":\"0\"},{\"uuid\":\"1895f3e4ae084921abc39d51743dad1d\",\"name\":\"\\u95ee\\u5377\\u8c03\\u67e5\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369894?fileid=3369894&ts=1563848500583&sign=6277cda50ad0c631771d63d7743b8ab5\",\"redirect_url\":\"https:\\/\\/service-czy.colourlife.com\\/cgjRedirect\",\"auth_type\":\"3\",\"quantity\":\"0\"},{\"uuid\":\"6c25b6c0c7b145dfb7328f6ca773d5e4\",\"name\":\"\\u7ee9\\u6548\\u5408\\u540c\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369921?fileid=3369921&ts=1563848500725&sign=c5f164c52d64bfb73c36c1818d825851\",\"redirect_url\":\"https:\\/\\/qianyue-hr.colourlife.com\\/redirect\",\"auth_type\":\"3\",\"quantity\":\"0\"},{\"uuid\":\"e228084d96ce43228a320e063ffa26a1\",\"name\":\"\\u7ed1\\u5b9a\\u5f69\\u4e4b\\u4e91\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369935?fileid=3369935&ts=1563848500869&sign=407027cf797e5cdae06c27a1ec0b6a78\",\"redirect_url\":\"https:\\/\\/evisit.colourlife.com\\/cgj\\/bindcustomer\\/bind_account.html\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"55b0e63dded74d92b4b91cca1c93d883\",\"name\":\"\\u5e02\\u573a\\u8fd0\\u8425\\u5e73\\u53f0\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369961?fileid=3369961&ts=1563848501008&sign=53330ece999d42a7c7ba23c73c98dd50\",\"redirect_url\":\"https:\\/\\/market.colourlife.com\\/api\\/mobile\\/single_point\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"eb1a8685d5444d30844410d4b76970b5\",\"name\":\"\\u5ba1\\u62791.0\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3370001?fileid=3370001&ts=1563848501192&sign=4902c4936e05e19d996c370febd66c5d\",\"redirect_url\":\"http:\\/\\/spsso.colourlife.net\\/login.aspx\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"ce8ef918e08542f683c8a7ab2c720983\",\"name\":\"\\u5408\\u4f5c\\u516c\\u53f8\\u5ba1\\u6279\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3370024?fileid=3370024&ts=1563848501338&sign=1713d8508debe1f444dbc4de662fbb06\",\"redirect_url\":\"http:\\/\\/cgjbpm-stock.colourlife.com\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"09d89007575a440388d7c3a1642ad52f\",\"name\":\"\\u7eff\\u5316\\uff08\\u673a\\u52a8\\uff09\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3370036?fileid=3370036&ts=1563848501467&sign=959f4990e0d881dd0482dbc6e521714a\",\"redirect_url\":\"http:\\/\\/caoquewang.colourlife.com\\/\",\"auth_type\":\"2\",\"quantity\":\"0\"}]}],\"id\":3}],\"contentEncrypt\":\"\"}";
    }

    interface DOWN {
        // 下载文件保存路径
        String DOWNLOAD_DIRECT = Environment.getExternalStorageDirectory()
                + "/colourlife/download/";
        String LOG_DIRECT = Environment.getExternalStorageDirectory()
                + "/colourlife/log/";
    }

    interface MAP {
        // 下载文件保存路径
        String ADDRESS = "map_address";
    }


}
