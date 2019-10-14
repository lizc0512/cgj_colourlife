package com.tg.coloursteward.constant;

import android.os.Environment;

/**
 * 全局变量
 */
public interface Contants {

    interface URl {

        //////////////    正式地址   //////////////////////////////
        String URL_ICETEST = "https://openapi.colourlife.com/v1";//
        String URL_OAUTH2 = "https://oauth2-cgj.colourlife.com";
        String SINGLE_DEVICE = "https://single.colourlife.com";
        String URL_NEW = "https://cgj-backyard.colourlife.com";
        String URL_ICESTAFF = "https://staff-ice.colourlife.com";
        String CLIENT_SECRET = "t2o0a1xl2lOmoPi4tuHf5uw4VZloXGs7y1Kd0Yoq";
        String URL_QRCODE = "https://qrcode.colourlife.com";
        String URL_IMPUSH = "https://impush-cgj.colourlife.com";
        String VERSION_ADDRESS = "https://version.colourlife.com";
        String URL_H5OAUTH = "https://oauth-czy.colourlife.com";
        String URL_LEKAI = "https://lekaiadminapi-door.colourlife.com";
        String environment = "release";
        String cqj_appid = "327494513335603200";
        String TOKEN_ADDRESS = "https://oauth2czy.colourlife.com";
        String USERINFO_ADDRESS = "https://user-czy.colourlife.com/app";

        ////////////////  测试地址   ///////////////////////////////////
//        String URL_ICETEST = "https://openapi-test.colourlife.com/v1";//
//        String URL_OAUTH2 = "https://oauth2-cgj-test.colourlife.com";
//        String SINGLE_DEVICE = "https://single-czytest.colourlife.com";
//        String URL_NEW = "https://cgj-backyard-test.colourlife.com";
//        String URL_ICESTAFF = "http://staff.ice.test.colourlife.com";
//        String CLIENT_SECRET = "xlsfrQS5R49upmfZbhlsrUzAt9HDA5K4ptLYsqK5";
//        String URL_QRCODE = "http://qrcode-czytest.colourlife.com";
//        String URL_IMPUSH = "https://impush-cgj-test.colourlife.com";
//        String VERSION_ADDRESS = "https://version-czytest.colourlife.com";
//        String URL_H5OAUTH = "https://oauth-czytest.colourlife.com";
//        String URL_LEKAI = "https://lekaiadminapi-doortest.colourlife.com";
//        String environment = "debug";
//        String cqj_appid = "323521861252157440";
//        String TOKEN_ADDRESS = "http://oauth2-czytest.colourlife.com";
//        String USERINFO_ADDRESS = "http://user.czytest.colourlife.com/app";
    }

    interface APP {
        String captchaURL = URl.URL_NEW + "/app/home/captcha/start";
        String validateURL = URl.URL_NEW + "/app/home/login/verify";
        /***彩管家4.0加密的秘钥***/
        String secertKey = "gbiwgbiwkgnkwgnkjbkkrthmnjwjgeh";
        /***彩之云4.0加密的秘钥***/
        String czySecertKey = "oy4x7fSh5RI4BNc78UoV4fN08eO5C4pj0daM0B8M";
        String buglyKeyId = "b3dcc32611";
        String czyClient_secret = "oy4x7fSh5RI4BNc78UoV4fN08eO5C4pj0daM0B8M";
        String WEIXIN_APP_ID = "wx2cd55a3733a9aa2e";
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
        String OA = "OA";
        String MOBILE = "mobile";
        String CALLBACKSCANRESULT = "CallBackScanResult";
    }

    interface Html5 {
        String HEAD_ICON_URL = "http://avatar.ice.colourlife.com";//头像
        String CALLBACKDeviceID = "CALLBACKDeviceID";
        String YJ = "http://mail.oa.colourlife.com:40060/login";
        String SP = "http://bpm.ice.colourlife.com/portal/login/sso.action";//2.0
        String QIANDAO = "http://eqd.oa.colourlife.com/cailife/sign/main";
    }

    interface storage {
        String TICKET = "ticket";
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
        String ALIAS = "alias";//ALIAS
        String Tags = "tags";//Tags
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
        String JSFPNUM = "jsfpmoney";//微服务即时分配金额
        String HomePageAd = "homepagead";
        String ImageType = "imagetype";
        String fragmentminedata = "{\"code\":0,\"message\":\"success\",\"content\":[{\"id\":1,\"data\":[{\"id\":4,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd7de4f644652159.png\",\"url\":\"colourlife:\\/\\/proto?type=redPacket\",\"name\":\"\\u6211\\u7684\\u996d\\u7968\",\"group_id\":1},{\"id\":5,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd84176091219280.png\",\"url\":\"colourlife:\\/\\/proto?type=findPwd\",\"name\":\"\\u627e\\u56de\\u652f\\u4ed8\\u5bc6\\u7801\",\"group_id\":1},{\"id\":2,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd8278f931489523.png\",\"url\":\"https:\\/\\/payroll-hr.colourlife.com\\/redirect\",\"name\":\"\\u6211\\u7684\\u5de5\\u8d44\\u6761\",\"group_id\":1}]},{\"id\":2,\"data\":[{\"id\":1,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd819d0ae5124346.png\",\"url\":\"colourlife:\\/\\/proto?type=invite\",\"name\":\"\\u9080\\u8bf7\",\"group_id\":2},{\"id\":6,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd84cea0f0210390.png\",\"url\":\"colourlife:\\/\\/proto?type=mydownload\",\"name\":\"\\u6211\\u7684\\u4e0b\\u8f7d\",\"group_id\":2},{\"id\":3,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd83307e22424071.png\",\"url\":\"colourlife:\\/\\/proto?type=setting\",\"name\":\"\\u8bbe\\u7f6e\",\"group_id\":2}]}],\"contentEncrypt\":\"\"}";
        String MICRODATA = "{\"code\":0,\"message\":\"success\",\"content\":[{\"type\":\"1\",\"content\":[{\"name\":\"\",\"data\":[{\"uuid\":\"3ae61f382bce44a2a4a7ac2247325745\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364490?fileid=3364490&ts=1565646708038&sign=7c0b312f022dcf60b65e2f735c2b090b\",\"redirect_url\":\"\",\"auth_type\":\"1\"}]}],\"id\":1},{\"type\":\"2\",\"content\":[{\"name\":\"\\u6570\\u636e\",\"data\":[{\"uuid\":\"communityStatistics\",\"name\":\"\\u4e0a\\u7ebf\\u9762\\u79ef\\uff08\\u4e07\\u33a1\\uff09\",\"data\":\"--\",\"redirect_url\":\"colourlife:\\/\\/proto?type=onlinEarea\",\"auth_type\":\"0\"},{\"uuid\":\"caihuiOrderStatistics\",\"name\":\"\\u4eca\\u65e5\\u5f69\\u60e0\\u8ba2\\u5355\",\"data\":\"--\",\"redirect_url\":\"http:\\/\\/caihui.colourlife.com\\/data_view\\/#\\/\",\"auth_type\":\"0\"},{\"uuid\":\"cshShares\",\"name\":\"\\u96c6\\u56e2\\u80a1\\u7968\",\"data\":\"--\",\"redirect_url\":\"http:\\/\\/image.sinajs.cn\\/newchart\\/hk_stock\\/min\\/01778.gif?20190813215701000059444\",\"auth_type\":\"0\"}]}],\"id\":2},{\"type\":\"3\",\"content\":[{\"name\":\"\\u5e38\\u7528\\u5e94\\u7528\",\"data\":[{\"uuid\":\"49f4a5f9ee3c45b88c07b4558ea5c51a\",\"name\":\"\\u7b7e\\u5230\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364567?fileid=3364567&ts=1565647434661&sign=5f1ad4322e49e24434a4f7da85494505\",\"redirect_url\":\"http:\\/\\/eqd.oa.colourlife.com\\/cailife\\/sign\\/main\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"59927103bdd7428fa4e2dcb806b96a01\",\"name\":\"\\u90ae\\u4ef6\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364620?fileid=3364620&ts=1565648748293&sign=f5d5548863dc52d8906ee83e5ecf726d\",\"redirect_url\":\"http:\\/\\/mail.oa.colourlife.com:40060\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"4b8b87adff5f41f7acd3a7b4e753c93f\",\"name\":\"\\u5ba1\\u6279\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364634?fileid=3364634&ts=1565647606846&sign=4ea7631a12da51d59583148781320774\",\"redirect_url\":\"http:\\/\\/bpm.ice.colourlife.com\\/portal\\/login\\/sso.action?number=0\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"adcfcf393d804e5898a1a3ecd89ffe55\",\"name\":\"\\u901a\\u77e5\\u516c\\u544a\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364647?fileid=3364647&ts=1565645216069&sign=b31b885e2e161169ff19d5b3b864aae1\",\"redirect_url\":\"http:\\/\\/notice.oa.colourlife.com:40063\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"}]},{\"name\":\"\\u534f\\u540c\\u529e\\u516c\",\"data\":[{\"uuid\":\"7a11a273311543d48dbf9e9ae672e4fd\",\"name\":\"\\u4efb\\u52a1\\u7cfb\\u7edf\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364662?fileid=3364662&ts=1565650310003&sign=99fa631577840d72bc7a40651614fe4e\",\"redirect_url\":\"http:\\/\\/mbee.oa.colourlife.com:4600\\/home\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"efcd9da4a6304067a31b8ddab0045064\",\"name\":\"\\u989d\\u5ea6\\u5ba1\\u6279\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364673?fileid=3364673&ts=1565653127790&sign=a34a8fa294116d19073ac1ff8e628725\",\"redirect_url\":\"http:\\/\\/finance.colourlife.com\\/wx\\/checkOauth2.html\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"52705414718c4f2e99c8d013deacfd03\",\"name\":\"\\u6587\\u4ef6\\u67dc\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364683?fileid=3364683&ts=1565648749160&sign=b5c794906e94ac27dd60b6ed1d2497b1\",\"redirect_url\":\"http:\\/\\/files.oa.colourlife.com:40064\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"}]},{\"name\":\"\\u7269\\u4e1a\\u7ba1\\u7406\",\"data\":[{\"uuid\":\"9b288e4aa73947239dda6bb3581e3c3c\",\"name\":\"\\u6295\\u8bc9\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364699?fileid=3364699&ts=1565646297722&sign=5248f02f8816fdfa7f1bd84d6711fdaa\",\"redirect_url\":\"http:\\/\\/servicing.czy.colourlife.com:40025\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"2b73a89bdc214aa983e1575941e960e1\",\"name\":\"\\u4fdd\\u517b\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364705?fileid=3364705&ts=1565647027908&sign=5cf27415b5af507619a990271c88bbac\",\"redirect_url\":\"http:\\/\\/eby.colourlife.com\\/front\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"e1a1370d03624a67a07fc9747805170c\",\"name\":\"\\u7eff\\u5316\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364712?fileid=3364712&ts=1565645689781&sign=135adb6a004e29037d501eb61cbffddf\",\"redirect_url\":\"http:\\/\\/eqj.colourlife.com\\/\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"dd71d7143a4b44d1b12631dfcd668758\",\"name\":\"\\u505c\\u8f66\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364725?fileid=3364725&ts=1565648389984&sign=92ad919f62295ea3d50e0a0069d48e41\",\"redirect_url\":\"http:\\/\\/m.aparcar.cn\\/oa\\/butler\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"ab096af8682c438cb64a88f73b5e9a42\",\"name\":\"\\u5165\\u4f19\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369364?fileid=3369364&ts=1565645813683&sign=ea64d278f08c73cd3109f57d3457588a\",\"redirect_url\":\"https:\\/\\/echeckin-cgj.colourlife.com\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"a8c1a407876d41b5a5bcb1081ffc47fc\",\"name\":\"\\u7ef4\\u4fee\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369392?fileid=3369392&ts=1565648092814&sign=14181c1c3025d49766881a2a92a15222\",\"redirect_url\":\"https:\\/\\/m.eshifu.cn\\/common\\/cgjOauth\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"76cae307a7de450fa2b31ab8636c4177\",\"name\":\"\\u88c5\\u4fee\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369405?fileid=3369405&ts=1565647554120&sign=5012eed3f0ba85afa9c47deefc129546\",\"redirect_url\":\"http:\\/\\/cgj.ezxvip.com\\/\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"53223e7e9d99479db8a172607cc7e4d7\",\"name\":\"\\u7535\\u68af\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369419?fileid=3369419&ts=1565647911204&sign=b1eac4dd7d792315693adf2ccfd6a79e\",\"redirect_url\":\"http:\\/\\/cgj.1lift.cn\\/cgj\\/auth\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"366b008f99674bc1a2625dea5577add1\",\"name\":\"\\u6536\\u8d39\\u7ba1\\u7406\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369440?fileid=3369440&ts=1565643846429&sign=8c33d8b4f54396537852c7820a4dff7b\",\"redirect_url\":\"http:\\/\\/ipos.colourlife.com\\/index.php?s=\\/Home\\/Index\\/oauthlogin.html&access_token=\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"dacfd9821b7f4339af88125b7bd597fe\",\"name\":\"\\u5fae\\u5546\\u5708\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369600?fileid=3369600&ts=1565648933130&sign=8ac7b16be8f380d9c29079c61d147b26\",\"redirect_url\":\"https:\\/\\/wsq.colourlife.com\\/agent\\/index\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"a62a2888bb0e47f1a821856603373a4b\",\"name\":\"\\u4e03\\u661f\\u8d28\\u68c0\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369629?fileid=3369629&ts=1565649423328&sign=9960178d72cf75dd09e02d151b003454\",\"redirect_url\":\"https:\\/\\/operation.colourlife.com\\/staff\\/login\",\"auth_type\":\"3\",\"quantity\":\"0\"},{\"uuid\":\"67001c324e9d458f917431d48e319c41\",\"name\":\"\\u96c6\\u4e2d\\u7ba1\\u63a7\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369653?fileid=3369653&ts=1565643422200&sign=81cf02dbae98fc5bfe80987c1fc25d56\",\"redirect_url\":\"https:\\/\\/datacollect-czy.colourlife.com\\/frontend\\/employee\\/\",\"auth_type\":\"3\",\"quantity\":\"0\"},{\"uuid\":\"3d8a3c760e0046969f6cbdeefd0dd2f3\",\"name\":\"\\u6284\\u8868\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369680?fileid=3369680&ts=1565646656213&sign=d60d30d72863279ff5959cdcd98e38d1\",\"redirect_url\":\"http:\\/\\/metering.charge.colourlife.com:40026\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"7c4175c771644c74b66b3c46791cfc15\",\"name\":\"\\u50ac\\u8d39\\u8ddf\\u8e2a\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369705?fileid=3369705&ts=1565649833009&sign=7101ff2ab5618c62c46792627fc7e11e\",\"redirect_url\":\"http:\\/\\/case.backyard.colourlife.com:4800\\/home\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"c46486a0521b48ecb011818ae986700d\",\"name\":\"\\u6536\\u8d39\\u7cfb\\u7edf\\u770b\\u677f\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369787?fileid=3369787&ts=1565648151067&sign=064aea458680171ee596bae0af7ca01b\",\"redirect_url\":\"https:\\/\\/sfkb-cgj.colourlife.com\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"a6382878459a4554b06ae8b9e2170dc2\",\"name\":\"\\u6765\\u8bbf\\u767b\\u8bb0\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3579365?fileid=3579365&ts=1565646194518&sign=1b2e5ce47d06d4a7c7d83214554c6037\",\"redirect_url\":\"https:\\/\\/visitor-czy.colourlife.com\",\"auth_type\":\"4\",\"quantity\":\"0\"},{\"uuid\":\"7427b62b84b141889ece7d1c13bae3e0\",\"name\":\"\\u4fdd\\u6d01\\u62a5\\u8868\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3588236?fileid=3588236&ts=1565647739469&sign=37ca6c3407815b52ccb22b21987e3b6c\",\"redirect_url\":\"https:\\/\\/ebj.colourlife.com\\/boot\\/#\\/cmlogin\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"cab738b2d25d46ed9ae54542413a2d37\",\"name\":\"\\u95e8\\u7981\\u7ba1\\u5bb6\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3679144?fileid=3679144&ts=1565644266411&sign=06463536a3d81e93ee3f06381fcc0f13\",\"redirect_url\":\"colourlife:\\/\\/proto?type=entranceGuard\",\"auth_type\":\"0\",\"quantity\":\"0\"}]},{\"name\":\"\\u4eba\\u4e8b\\u7ba1\\u7406\",\"data\":[{\"uuid\":\"460fe01100b445659a0ff0db6a5c13b0\",\"name\":\"\\u62db\\u8058\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369829?fileid=3369829&ts=1565649109854&sign=d521ab3c2488ed0e582fd635ae9a081c\",\"redirect_url\":\"https:\\/\\/zhaopin-cgj.colourlife.com\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"79cf2c988bcd4a35a785bd2b2d7fb5c3\",\"name\":\"E\\u542f\\u5b66\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369864?fileid=3369864&ts=1565646410235&sign=d18d9113a3530b0f0e6e841a1eb33e3d\",\"redirect_url\":\"https:\\/\\/peixun.colourlife.com\\/app\\/index.php?i=1&c=entry&m=uwechat_cms&do=mobile\",\"auth_type\":\"3\",\"quantity\":\"0\"}]},{\"name\":\"\\u5176\\u4ed6\",\"data\":[{\"uuid\":\"ab9cc8887a7444de94d91705b22451c3\",\"name\":\"\\u5bf9\\u516c\\u8d26\\u6237\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369881?fileid=3369881&ts=1565644685014&sign=3ce5486cbceb49d13f6c22dbc0668d08\",\"redirect_url\":\"colourlife:\\/\\/proto?type=dgzh\",\"auth_type\":\"0\",\"quantity\":\"0\"},{\"uuid\":\"1895f3e4ae084921abc39d51743dad1d\",\"name\":\"\\u95ee\\u5377\\u8c03\\u67e5\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369894?fileid=3369894&ts=1565647028054&sign=4d9d81aa904fd05cc3857a549538cfbe\",\"redirect_url\":\"https:\\/\\/service-czy.colourlife.com\\/cgjRedirect\",\"auth_type\":\"3\",\"quantity\":\"0\"},{\"uuid\":\"6c25b6c0c7b145dfb7328f6ca773d5e4\",\"name\":\"\\u7ee9\\u6548\\u5408\\u540c\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369921?fileid=3369921&ts=1565650191691&sign=4415f9d50a8bed23658c5da2bec495a9\",\"redirect_url\":\"https:\\/\\/qianyue-hr.colourlife.com\\/redirect\",\"auth_type\":\"3\",\"quantity\":\"0\"},{\"uuid\":\"e228084d96ce43228a320e063ffa26a1\",\"name\":\"\\u7ed1\\u5b9a\\u5f69\\u4e4b\\u4e91\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369935?fileid=3369935&ts=1565645945493&sign=18ae99fa86e773880667a53ec78885b1\",\"redirect_url\":\"https:\\/\\/evisit.colourlife.com\\/cgj\\/bindcustomer\\/bind_account.html\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"55b0e63dded74d92b4b91cca1c93d883\",\"name\":\"\\u5e02\\u573a\\u8fd0\\u8425\\u5e73\\u53f0\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369961?fileid=3369961&ts=1565650790596&sign=c72737a3952e017358f2548baccc83cf\",\"redirect_url\":\"https:\\/\\/market.colourlife.com\\/api\\/mobile\\/single_point\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"eb1a8685d5444d30844410d4b76970b5\",\"name\":\"\\u5ba1\\u62791.0\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3370001?fileid=3370001&ts=1565644795575&sign=59d4af51879ce873641470bd1a5c4fcf\",\"redirect_url\":\"http:\\/\\/spsso.colourlife.net\\/login.aspx\",\"auth_type\":\"1\",\"quantity\":\"0\"},{\"uuid\":\"ce8ef918e08542f683c8a7ab2c720983\",\"name\":\"\\u5408\\u4f5c\\u516c\\u53f8\\u5ba1\\u6279\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3370024?fileid=3370024&ts=1565646063106&sign=b9fdf0e6a3c2d325b2f279e082f73809\",\"redirect_url\":\"http:\\/\\/cgjbpm-stock.colourlife.com\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"09d89007575a440388d7c3a1642ad52f\",\"name\":\"\\u7eff\\u5316\\uff08\\u673a\\u52a8\\uff09\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3370036?fileid=3370036&ts=1565646720594&sign=6778d048fd6f0613a00921694b995547\",\"redirect_url\":\"http:\\/\\/caoquewang.colourlife.com\\/\",\"auth_type\":\"2\",\"quantity\":\"0\"}]}],\"id\":3}],\"contentEncrypt\":\"\"}";
    }

    interface DOWN {
        // 下载文件保存路径
        String DOWNLOAD_DIRECT = Environment.getExternalStorageDirectory()
                + "/colourlife/download/";
    }

    interface MAP {
        // 下载文件保存路径
        String ADDRESS = "map_address";
    }


}
