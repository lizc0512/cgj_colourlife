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
         */
        //final String URL_ICETEST="http://iceapi.colourlife.com:8081/v1";//1.0
        String URL_ICETEST = "https://openapi.colourlife.com/v1";//2.0

        String URL_CPMOBILE = "http://cpmobile.colourlife.com";

//     String URL_ICETEST="http://avatar.ice.colourlife.com/v1";//2.0
        /**
         * 测试地址
         */
        //final String URL_ICETEST="http://openapi.test.colourlife.com/v1";//2.0

        String SeaHealthApiUrl = "http://183.136.184.209:80/api/serviceApi/userauth/gettoken";//海康api
        //	String CZY_BINDCUSTOMER="http://www.colourlife.com/bindCustomer";//绑定彩之云
        String CZY_BINDCUSTOMER = "https://evisit.colourlife.com/cgj/bindcustomer/bind_account.html";//绑定彩之云
        String URL_H5_LEAVE = "http://eqd.backyard.colourlife.com/cailife/leave/index?";//请假
        String HUXIN_H5_HELP = "http://www.colourlife.com/Introduction/CgjCall";//呼信(帮助)
        //	String HEAD_ICON_URL="http://iceapi.colourlife.com:8686/";//头像
        String HEAD_ICON_URL = "http://avatar.ice.colourlife.com/";//头像
        String Yj_Url = "http://emailsso.colourlife.net/login.aspx";//邮件
        String Sp_Url = "http://spsso.colourlife.net/login.aspx/";//审批
        String SeaHealthApkUrl = "http://spsso.colourlife.net/login.aspx/";//海康客户端下载
    }

    interface APP {
        String SeaHealthPackageName = "com.hikvi.ivms8700.hd.colorfulLife";
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
        String CALLBACKDeviceID = "CALLBACKDeviceID";
        String YJ = "http://mail.oa.colourlife.com:40060/login";
        String SP1 = "http://spsso.colourlife.net/login.aspx";//1.0
        String SP = "http://bpm.ice.colourlife.com/portal/login/sso.action";//2.0
        String CASE = "http://iceapi.colourlife.com:4600/home";
        String QIANDAO = "http://eqd.oa.colourlife.com/cailife/sign/main";
    }

    interface storage {
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
