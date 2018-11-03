package com.tg.coloursteward.constant;

import android.os.Environment;

/**
 * 全局变量
 */
public interface Contants {
    /**
     * 下载安装包保存目录
     */
    String downloadDir = "app/download/";


    interface First {
        String IS_FIRST_TIME_STARTING = "firt_time";
    }

    interface URl {

        /**
         * 正式地址
         //         */
//        String URL_ICETEST = "https://openapi.colourlife.com/v1";//2.0
//        String URL_CPMOBILE = "http://cpmobile.colourlife.com";
//        String URL_CAIHUI = "https://caihui-bishow.colourlife.com";
//        String URL_OAUTH2 = "https://oauth2-cgj.colourlife.com";
//        String SINGLE_DEVICE = "https://single.colourlife.com/";
//        String URL_NEW = "https://cgj-backyard.colourlife.com/";
//        String URL_ICESTAFF = "https://staff-ice.colourlife.com/";
//        String CLIENT_SECRET = "t2o0a1xl2lOmoPi4tuHf5uw4VZloXGs7y1Kd0Yoq";
//        String PUBLIC_KEY="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTFnAR7ORLx0jGzf9Ux1We7yHvRi+kQXKSRmtgBjDCXQzakGm2mrb6EupCkDbUcj4BUs7S7zm/rICQuVNC9fujeJGjcNWRg0XWVtm90XpbTqfKiXzGDHI9W8aULYZ3of/JJ9lyCyjqjigyCdLBPtQ27gOuboDzQuieR2ywPHawzQIDAQAB";
//////////////////////////////////////////////////////////////////////////////
        /**
         * 测试地址
//         */
        String URL_ICETEST = "https://openapi-test.colourlife.com/v1";//2.0
        String URL_CPMOBILE = "http://cpmobile-czytest.colourlife.com";
        String URL_CAIHUI = "https://caihui-bishow.colourlife.com";
        String URL_OAUTH2 = "https://oauth2-cgj-test.colourlife.com";
        String SINGLE_DEVICE = "https://single-czytest.colourlife.com/";
        String URL_NEW = "https://cgj-backyard-test.colourlife.com/";
        String URL_ICESTAFF = "http://staff.ice.test.colourlife.com/";
        String CLIENT_SECRET = "xlsfrQS5R49upmfZbhlsrUzAt9HDA5K4ptLYsqK5";
        String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDZDqnWph9LxtD0zgtGAYTTf2jY" +
                "eV+ni5F1o0w3Fag4OOD1YHCRUCXIsFy+iJYmuPf5vMkZrkoiJmKBfkaIzNlrJZzH" +
                "zq+LsPQNCF86p1nLsuHbkWNvyjOEPn/CUryP2Kxme4S+eEqLIeNwp70VOaMuPmRo" +
                "EZxMDAgvc6Z0DWsVdQIDAQAB";
/////////////////////////////////////////////////////////////////////////////////////
    }

    interface APP {
        String SeaHealthPackageName = "com.hikvi.ivms8700.hd.colorfulLife";
        String captchaURL=URl.URL_NEW+"app/home/captcha/start";
        String validateURL=URl.URL_NEW+"app/home/login/verify";
        /***彩管家4.0加密的秘钥***/
        String secertKey = "gbiwgbiwkgnkwgnkjbkkrthmnjwjgeh";
    }

    interface EMPLOYEE_LOGIN {
        String key = "EmployeeLogin_key";
        String secret = "EmployeeLogin_secret";
        String diff = "EmployeeLogin_diff";
    }

    interface LOGO {
        /*
         * 门禁模块
         */
        int DOOREDIT_LOGCLICK = 20;

        int DOOREDIT_MOVE = 21;

        int DOOREDIT_ADD = 22;

        int DOOREDIT_ADD_REFRESH = 23;

        int DOOREDIT_DELETE = 24;

        int DOOREDIT_DOOR_NAME = 25;

        /**
         * 首页消息更新
         */
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
        String HEAD_ICON_URL = "http://avatar.ice.colourlife.com/";//头像
        String URL_H5_LEAVE = "http://eqd.backyard.colourlife.com/cailife/leave/index?";//请假
        String CALLBACKDeviceID = "CALLBACKDeviceID";
        String YJ = "http://mail.oa.colourlife.com:40060/login";
        String SP1 = "http://spsso.colourlife.net/login.aspx";//1.0
        String SP = "http://bpm.ice.colourlife.com/portal/login/sso.action";//2.0
        String CASE = "http://iceapi.colourlife.com:4600/home";
        String QIANDAO = "http://eqd.oa.colourlife.com/cailife/sign/main";
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
        String EMPLOYEE_LOGIN = "employee_login";//登录OK
        String LATITUDE = "str_latitude";//纬度
        String LONGITUDE = "str_longitude";//经度
        String FRAGMENTMINE = "fragmentmine";//我的页面
        String SALARY_TIME = "salary_time";//工资条打开时间
        String SALARY_ISINPUT = "salary_isinput";//工资条打开状态
        String TINYFRAGMENTTOP = "tinyfragmenttop";//微服务顶部
        String TINYFRAGMENTMID = "tinyfragmentmid";//微服务中部
        String fragmentminedata ="{\"code\":0,\"message\":\"success\",\"content\":[{\"id\":1,\"data\":[{\"id\":4,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/local-5ba1a68e72ccf634122.png\",\"url\":\"colourlife:\\/\\/proto?type=redPacket\",\"name\":\"\\u6211\\u7684\\u996d\\u7968\",\"group_id\":1},{\"id\":5,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/local-5ba1a72589e96782755.png\",\"url\":\"colourlife:\\/\\/proto?type=findPwd\",\"name\":\"\\u627e\\u56de\\u652f\\u4ed8\\u5bc6\\u7801\",\"group_id\":1},{\"id\":2,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/local-5ba1a72f39a4d191428.png\",\"url\":\"https:\\/\\/payroll-hr.colourlife.com\\/redirect\",\"name\":\"\\u6211\\u7684\\u5de5\\u8d44\\u6761\",\"group_id\":1}]},{\"id\":2,\"data\":[{\"id\":7,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/local-5ba1a70d8feca995000.png\",\"url\":\"http:\\/\\/evisit.czytest.colourlife.com\\/cgj\\/bindcustomer\\/redirect\",\"name\":\"\\u5f69\\u4e4b\\u4e91\\u8d26\\u53f7\\u7ed1\\u5b9a\",\"group_id\":2},{\"id\":1,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/local-5ba1a6ca2dba5614749.png\",\"url\":\"colourlife:\\/\\/proto?type=invite\",\"name\":\"\\u9080\\u8bf7\",\"group_id\":2}]},{\"id\":3,\"data\":[{\"id\":6,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/local-5ba1a741708ca213058.png\",\"url\":\"colourlife:\\/\\/proto?type=mydownload\",\"name\":\"\\u6211\\u7684\\u4e0b\\u8f7d\",\"group_id\":3},{\"id\":3,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/local-5ba1a7379ec36340210.png\",\"url\":\"colourlife:\\/\\/proto?type=setting\",\"name\":\"\\u8bbe\\u7f6e\",\"group_id\":3}]}],\"contentEncrypt\":\"\"}";
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
