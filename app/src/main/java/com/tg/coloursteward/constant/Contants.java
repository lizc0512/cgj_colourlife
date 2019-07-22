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

        //////////////    正式地址   //////////////////////////////
        String URL_ICETEST = "https://openapi.colourlife.com/v1";//2.0
        String URL_CPMOBILE = "http://cpmobile.colourlife.com";
        String URL_CAIHUI = "https://caihui-bishow.colourlife.com";
        String URL_OAUTH2 = "https://oauth2-cgj.colourlife.com";
        String SINGLE_DEVICE = "https://single.colourlife.com";
        String URL_NEW = "https://cgj-backyard.colourlife.com";
        String URL_ICESTAFF = "https://staff-ice.colourlife.com";
        String CLIENT_SECRET = "t2o0a1xl2lOmoPi4tuHf5uw4VZloXGs7y1Kd0Yoq";
        String URL_QRCODE = "https://qrcode.colourlife.com";
        String URL_IMPUSH = "https://impush-cgj.colourlife.com";
        String VERSION_ADDRESS = "https://version.colourlife.com";
        String URL_H5OAUTH = "https://oauth-czy.colourlife.com";
        String environment = "release";
        String cqj_appid = "327494513335603200";
        String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTFnAR7ORLx0jGzf9Ux1We7yHvRi+kQXKSRmtgBjDCXQzakGm2mrb6EupCkDbUcj4BUs7S7zm/rICQuVNC9fujeJGjcNWRg0XWVtm90XpbTqfKiXzGDHI9W8aULYZ3of/JJ9lyCyjqjigyCdLBPtQ27gOuboDzQuieR2ywPHawzQIDAQAB";
        String MICRODATA="";
        ////////////////  测试地址   ///////////////////////////////////
//        String URL_ICETEST = "https://openapi-test.colourlife.com/v1";//2.0e
//        String URL_CPMOBILE = "http://cpmobile-czytest.colourlife.com";
//        String URL_CAIHUI = "https://caihui-bishow.colourlife.com";
//        String URL_OAUTH2 = "https://oauth2-cgj-test.colourlife.com";
//        String SINGLE_DEVICE = "https://single-czytest.colourlife.com";
//        String URL_NEW = "https://cgj-backyard-test.colourlife.com";
//        String URL_ICESTAFF = "http://staff.ice.test.colourlife.com";
//        String CLIENT_SECRET = "xlsfrQS5R49upmfZbhlsrUzAt9HDA5K4ptLYsqK5";
//        String URL_QRCODE = "http://qrcode-czytest.colourlife.com";
//        String URL_IMPUSH = "https://impush-cgj-test.colourlife.com";
//        String VERSION_ADDRESS = "https://version-czytest.colourlife.com";
//        String URL_H5OAUTH = "https://oauth-czytest.colourlife.com";
//        String environment = "debug";
//        String cqj_appid = "323521861252157440";
//        String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDZDqnWph9LxtD0zgtGAYTTf2jY" +
//                "eV+ni5F1o0w3Fag4OOD1YHCRUCXIsFy+iJYmuPf5vMkZrkoiJmKBfkaIzNlrJZzH" +
//                "zq+LsPQNCF86p1nLsuHbkWNvyjOEPn/CUryP2Kxme4S+eEqLIeNwp70VOaMuPmRo" +
//                "EZxMDAgvc6Z0DWsVdQIDAQAB";
//        String MICRODATA = "{\"code\":0,\"message\":\"success\",\"content\":[{\"type\":\"1\",\"content\":[{\"name\":\"\",\"data\":[{\"uuid\":\"a197177bbfc849fcbed820be144f02e0\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31065?fileid=31065&ts=1563716777929&sign=6faaa1c50a0c7ca82aefe04c588ab0fe\",\"redirect_url\":\"https:\\/\\/baidu.com\",\"auth_type\":1}]}],\"id\":1},{\"type\":\"2\",\"content\":[{\"name\":\"\\u6570\\u636e\",\"data\":[{\"uuid\":\"communityStatistics\",\"name\":\"\\u4e0a\\u7ebf\\u9762\\u79ef\\uff08\\u4e07\\u33a1\\uff09\",\"data\":\"138815.75\",\"redirect_url\":\"colourlife:\\/\\/proto?type=onlinEarea\"},{\"uuid\":\"caihuiOrderStatistics\",\"name\":\"\\u4eca\\u65e5\\u5f69\\u60e0\\u8ba2\\u5355\",\"data\":\"3550\",\"redirect_url\":\"http:\\/\\/caihui.colourlife.com\\/data_view\\/#\\/\"},{\"uuid\":\"cshShares\",\"name\":\"\\u96c6\\u56e2\\u80a1\\u7968\",\"data\":\"5.70\",\"redirect_url\":\"http:\\/\\/image.sinajs.cn\\/newchart\\/hk_stock\\/min\\/01778.gif?20190722203824000062669\"}]}],\"id\":2},{\"type\":\"3\",\"content\":[{\"name\":\"\\u5e38\\u7528\\u5e94\\u7528\",\"data\":[{\"uuid\":\"f29775b14f9b4a64b7bdda6cbb060747\",\"name\":\"\\u7b7e\\u5230\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31078?fileid=31078&ts=1563717389868&sign=d39d5b3982ded2b9df58212f31b48a4a\",\"redirect_url\":\"http:\\/\\/eqd.oa.colourlife.com\\/cailife\\/sign\\/main\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"6d92b92065944d988d5339952c0194f3\",\"name\":\"\\u90ae\\u4ef6\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31079?fileid=31079&ts=1563717390267&sign=b6372b166c48e462fce409023c16ed79\",\"redirect_url\":\"http:\\/\\/mail.oa.colourlife.com:40060\\/login\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"31c871afb98a49f19a1f9d3e67216bc6\",\"name\":\"\\u5ba1\\u6279\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31080?fileid=31080&ts=1563717390426&sign=2713e4e5bdaedce8676b8ed4c552c8ce\",\"redirect_url\":\"http:\\/\\/bpm.ice.colourlife.com\\/portal\\/login\\/sso.action\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"e3fe97eab0be47f59a61607569e06eda\",\"name\":\"\\u901a\\u77e5\\u516c\\u544a\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31081?fileid=31081&ts=1563717390581&sign=54d81e0adb29d4c6b756fa85eccbf32e\",\"redirect_url\":\"http:\\/\\/notice.oa.colourlife.com:40063\\/login\",\"auth_type\":2,\"quantity\":0}]},{\"name\":\"\\u534f\\u540c\\u529e\\u516c\",\"data\":[{\"uuid\":\"0d5190828dc947adb0583a14ad3cd6a3\",\"name\":\"\\u4efb\\u52a1\\u7cfb\\u7edf\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31082?fileid=31082&ts=1563717390772&sign=59e273014f52b2c2c503d41951930b20\",\"redirect_url\":\"http:\\/\\/mbee.oa.colourlife.com:4600\\/home\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"e4fbc9cbaf46456b9a717c96d8471ee0\",\"name\":\"\\u989d\\u5ea6\\u5ba1\\u6279\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31083?fileid=31083&ts=1563717391324&sign=a8edccbf16bd1ce89ab8c3a229a155a4\",\"redirect_url\":\"http:\\/\\/finance.colourlife.com\\/wx\\/checkOauth2.html\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"a97546bdb92643c3a85ede99112f0353\",\"name\":\"\\u6587\\u4ef6\\u67dc\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31084?fileid=31084&ts=1563717391484&sign=9d46a1c7d28a61b97402d2771a5a39cf\",\"redirect_url\":\"http:\\/\\/files.oa.colourlife.com:40064\\/login\",\"auth_type\":2,\"quantity\":0}]},{\"name\":\"\\u7269\\u4e1a\\u7ba1\\u7406\",\"data\":[{\"uuid\":\"eea69a7d7dfc4655bb4e93cedad9f102\",\"name\":\"\\u6295\\u8bc9\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31085?fileid=31085&ts=1563717391648&sign=c1d4d0fdc04686cde19f1c53a41190ad\",\"redirect_url\":\"http:\\/\\/servicing.cgjtest.savour.fun\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"a9aeba6b6b9b440d8799fa4a3be5e8eb\",\"name\":\"\\u4fdd\\u5b89\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31086?fileid=31086&ts=1563717391814&sign=f4128771e70cfbacafd5c53b81c97a38\",\"redirect_url\":\"http:\\/\\/eba.colourlife.com\\/\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"f3c4b61dc10748e299f63ae7898bc470\",\"name\":\"\\u7eff\\u5316\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31087?fileid=31087&ts=1563717391972&sign=d6c18763b1afd914acc3a097688f8b6b\",\"redirect_url\":\"http:\\/\\/eqj2.colourlife.net\\/login.aspx\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"76f656edbd9748b1b01608568e6a3097\",\"name\":\"\\u4fdd\\u6d01\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31088?fileid=31088&ts=1563717392132&sign=693f9b4227d23f1e30800aad44cbacb0\",\"redirect_url\":\"http:\\/\\/ebj.test.colourlife.com\\/boot\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"1f4a0c2ba6de49328a9f8d79d774fe95\",\"name\":\"\\u4fdd\\u517b\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31089?fileid=31089&ts=1563717392285&sign=11c7cfb859074897073ae5bb7f76eb83\",\"redirect_url\":\"http:\\/\\/ebyv2.test.colourlife.com\\/master\\/login\\/colour\\/life\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"37ffa10843004dd99d2248065d398909\",\"name\":\"\\u88c5\\u4fee\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31090?fileid=31090&ts=1563717392441&sign=5182abdeaa6c75f57f020879440b0720\",\"redirect_url\":\"http:\\/\\/cgj.ezxvip.com:8800\\/\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"3cd5440b304947008d1110af07c2ce1f\",\"name\":\"\\u7ef4\\u4fee\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31091?fileid=31091&ts=1563717392591&sign=ec7ebba746465f98d84751dc51801617\",\"redirect_url\":\"http:\\/\\/esfsso.colourlife.net\\/goto.aspx\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"ef09c68e4547477dba4acff92a5c359b\",\"name\":\"\\u7535\\u68af\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31092?fileid=31092&ts=1563717392746&sign=298d0db7ed0e29c8e34f8824665625b2\",\"redirect_url\":\"http:\\/\\/cgj.1lift.cn\\/cgj\\/auth\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"ab9c1ab832bf48ffa364927719e0f6ce\",\"name\":\"\\u5165\\u4f19\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31093?fileid=31093&ts=1563717392903&sign=e6744ba3b6c388de51963b3346368799\",\"redirect_url\":\"https:\\/\\/echeckin-cgj.colourlife.com\\/login\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"c1cf3720489c49e2b7c4507f00ba43c7\",\"name\":\"\\u6284\\u8868\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31094?fileid=31094&ts=1563717393056&sign=a31b67565faf88f4aecd8ad49c66d81a\",\"redirect_url\":\"https:\\/\\/baidu.com\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"c40d913ffab744cab504e87bab24864c\",\"name\":\"\\u96c6\\u4e2d\\u91c7\\u96c6\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31095?fileid=31095&ts=1563717393205&sign=0cac69c8b8f9cb93cb827b8b4d51fd42\",\"redirect_url\":\"http:\\/\\/datacollect.czy.colourlife.com\\/app\\/index.html#\\/activity\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"c045643d0e2848a58a7e2b2f105117ae\",\"name\":\"\\u6d3b\\u52a8\\u91c7\\u96c6\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31096?fileid=31096&ts=1563717393354&sign=0034c91f32b8031b7a48f2f913501677\",\"redirect_url\":\"http:\\/\\/datacollect.czy.colourlife.com\\/app\\/index.html#\\/activity\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"d02054b2b57e4e5a877e125011880828\",\"name\":\"\\u50ac\\u8d39\\u8ddf\\u8e2a\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31098?fileid=31098&ts=1563717393506&sign=b5a8f88db7fb52b58c2854ea23235ea2\",\"redirect_url\":\"http:\\/\\/iceapi.colourlife.com:4800\\/home\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"2402c3db7b42418cb2e401f3fbadabda\",\"name\":\"\\u6536\\u8d39\\u7ba1\\u7406\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31097?fileid=31097&ts=1563717393664&sign=951b6f39828e62cbe1d0462f12fa3b14\",\"redirect_url\":\"https:\\/\\/sfkb-cgj.colourlife.com\",\"auth_type\":2,\"quantity\":0}]},{\"name\":\"\\u4eba\\u4e8b\\u7ba1\\u7406\",\"data\":[{\"uuid\":\"0692d6a25f5e4b6fa761808fbe202686\",\"name\":\"\\u62db\\u8058\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31100?fileid=31100&ts=1563717393822&sign=4372b16a451344aa1a2a29e399d4075d\",\"redirect_url\":\"http:\\/\\/recruit.cgjhr.test.colourlife.com\\/login\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"15fbd133fd464e758ad981e6c49cfd51\",\"name\":\"\\u57f9\\u8bad\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31101?fileid=31101&ts=1563717393965&sign=14242f2cc83e983f231618643d664b87\",\"redirect_url\":\"https:\\/\\/peixun-test.colourlife.com\\/app\\/index.php?i=1&c=entry&m=uwechat_cms&do=mobile\",\"auth_type\":2,\"quantity\":0}]},{\"name\":\"\\u5176\\u4ed6\",\"data\":[{\"uuid\":\"8709cec0fc1f4b5690825250f780315e\",\"name\":\"\\u5bf9\\u516c\\u8d26\\u6237\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31102?fileid=31102&ts=1563717394341&sign=f7c0273c45ca601a811ed84c21fefba9\",\"redirect_url\":\"colourlife:\\/\\/proto?type=dgzh\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"307414ca5eb24dada45c722c16794f5a\",\"name\":\"\\u95ee\\u5377\\u8c03\\u67e5\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31103?fileid=31103&ts=1563717394507&sign=0b42ca37e2a46efcd9e99801810a5a4f\",\"redirect_url\":\"https:\\/\\/service-czytest.colourlife.com\\/cgjRedirectToken\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"15fa8896f5cb4c21b4570e0ec7fbf7cc\",\"name\":\"\\u7ee9\\u6548\\u5408\\u540c\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31104?fileid=31104&ts=1563717394666&sign=a65f72992d1d5e7b7306f60e32c23bd3\",\"redirect_url\":\"https:\\/\\/baidu.com\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"353cd99c78494374b24e1096bf6dc927\",\"name\":\"\\u7ed1\\u5b9a\\u5f69\\u4e4b\\u4e91\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31105?fileid=31105&ts=1563717394831&sign=6d374913e13605fc309f0e46baa80c55\",\"redirect_url\":\"http:\\/\\/www.colourlife.com\\/bindCustomer\",\"auth_type\":2,\"quantity\":0},{\"uuid\":\"a250b286e66d4373a5a27e8e04a1f3f3\",\"name\":\"\\u5e02\\u573a\\u8fd0\\u8425\\u5e73\\u53f0\",\"img_url\":\"https:\\/\\/micro-file-test.colourlife.com\\/v1\\/down\\/31106?fileid=31106&ts=1563717394991&sign=30b09a9e869830c98d58fac8c2d0b95d\",\"redirect_url\":\"https:\\/\\/market-test.colourlife.com\\/api\\/mobile\\/single_point\",\"auth_type\":2,\"quantity\":0}]}],\"id\":3}],\"contentEncrypt\":\"\"}";
    }

    interface APP {
        String SeaHealthPackageName = "com.hikvi.ivms8700.hd.colorfulLife";
        String captchaURL = URl.URL_NEW + "/app/home/captcha/start";
        String validateURL = URl.URL_NEW + "/app/home/login/verify";
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
        String HEAD_ICON_URL = "http://avatar.ice.colourlife.com";//头像
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
