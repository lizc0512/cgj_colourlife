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
        String ACCOUNT_ADDRESS = "https://account-finance.colourlife.com/";//新版彩钱包
        String environment = "release";
        String cqj_appid = "327494513335603200";
        String TOKEN_ADDRESS = "https://oauth2czy.colourlife.com";
        String USERINFO_ADDRESS = "https://user-czy.colourlife.com/app";
        String publicKeyString = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTFnAR7ORLx0jGzf9Ux1We7yHvRi+kQXKSRmtgBjDCXQzakGm2mrb6EupCkDbUcj4BUs7S7zm/rICQuVNC9fujeJGj"
                + "cNWRg0XWVtm90XpbTqfKiXzGDHI9W8aULYZ3of/JJ9lyCyjqjigyCdLBPtQ27gOu"
                + "boDzQuieR2ywPHawzQIDAQAB";

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
//        String ACCOUNT_ADDRESS = "https://account-finance-test.colourlife.com";//新版彩钱包
//        String environment = "debug";
//        String cqj_appid = "323521861252157440";
//        String TOKEN_ADDRESS = "http://oauth2-czytest.colourlife.com";
//        String USERINFO_ADDRESS = "http://user.czytest.colourlife.com/app";
//        String publicKeyString = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDZDqnWph9LxtD0zgtGAYT" +
//                "Tf2jYeV+ni5F1o0w3Fag4OOD1YHCRUCXIsFy+iJYmuPf5vMkZrkoiJmKBfkaIzNlrJZzHzq+LsPQNCF86p1nLsuHbkWNvy" +
//                "jOEPn/CUryP2Kxme4S+eEqLIeNwp70VOaMuPmRoEZxMDAgvc6Z0DWsVdQIDAQAB";
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
        String LICENSE_KEY = "5C96AA97189FB29346817D34BCCE17ADC5E5CB755341DEB2DE0F7F105924EE6B9441AAB0E7EFA35ADE2C722F4B2C3904B92E33EFE403AEADDB51CF477ABD6639D4923580A907CEC1D64F04AE245A6C8C22D96E0882E5C49D61E5DD8EAF262FCCE633";
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
        String LATITUDE = "str_latitude";//纬度
        String LONGITUDE = "str_longitude";//经度
        String FRAGMENTMINE = "fragmentmine";//我的页面
        String SALARY_TIME = "salary_time";//工资条打开时间
        String SALARY_ISINPUT = "salary_isinput";//工资条打开状态
        String HomePageAd = "homepagead";
        String ImageType = "imagetype";
        String fragmentminedata = "{\"code\":0,\"message\":\"success\",\"content\":[{\"id\":1,\"data\":[{\"id\":4,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd7de4f644652159.png\",\"url\":\"colourlife:\\/\\/proto?type=redPacket\",\"name\":\"\\u6211\\u7684\\u996d\\u7968\",\"group_id\":1},{\"id\":5,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd84176091219280.png\",\"url\":\"colourlife:\\/\\/proto?type=findPwd\",\"name\":\"\\u627e\\u56de\\u652f\\u4ed8\\u5bc6\\u7801\",\"group_id\":1},{\"id\":2,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd8278f931489523.png\",\"url\":\"https:\\/\\/payroll-hr.colourlife.com\\/redirect\",\"name\":\"\\u6211\\u7684\\u5de5\\u8d44\\u6761\",\"group_id\":1}]},{\"id\":2,\"data\":[{\"id\":1,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd819d0ae5124346.png\",\"url\":\"colourlife:\\/\\/proto?type=invite\",\"name\":\"\\u9080\\u8bf7\",\"group_id\":2},{\"id\":6,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd84cea0f0210390.png\",\"url\":\"colourlife:\\/\\/proto?type=mydownload\",\"name\":\"\\u6211\\u7684\\u4e0b\\u8f7d\",\"group_id\":2},{\"id\":3,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd83307e22424071.png\",\"url\":\"colourlife:\\/\\/proto?type=setting\",\"name\":\"\\u8bbe\\u7f6e\",\"group_id\":2}]}],\"contentEncrypt\":\"\"}";
        String MICRODATA = "{\"code\":0,\"message\":\"success\",\"content\":[{\"type\":\"1\",\"content\":[{\"name\":\"banner\",\"data\":[{\"uuid\":\"004f4408af554d13944d001bae4fb032\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/10880393?fileid=10880393&ts=1583922994548&sign=137be550511716b2a6d07fdbc0196f33\",\"redirect_url\":\"https:\\/\\/colourhome.colourlife.com\\/epidemic_situation\\/home.html\",\"auth_type\":\"0\"}]}],\"id\":1},{\"type\":\"2\",\"content\":[{\"name\":\"\\u6570\\u636e\",\"data\":[{\"uuid\":\"communityStatistics\",\"name\":\"\\u5408\\u540c\\u9762\\u79ef\\uff08\\u4e07\\u33a1\\uff09\",\"data\":\"142373.96\",\"redirect_url\":\"colourlife:\\/\\/proto?type=onlinEarea\",\"auth_type\":\"0\"},{\"uuid\":\"caihuiOrderStatistics\",\"name\":\"\\u4eca\\u65e5\\u5f69\\u60e0\\u8ba2\\u5355\",\"data\":\"0\",\"redirect_url\":\"https:\\/\\/caihui.colourlife.com\\/data_view\\/#\\/\",\"auth_type\":\"0\"},{\"uuid\":\"cshShares\",\"name\":\"\\u96c6\\u56e2\\u80a1\\u7968\",\"data\":\"4.0\",\"redirect_url\":\"http:\\/\\/image.sinajs.cn\\/newchart\\/hk_stock\\/min\\/01778.gif?20200312085916000020680\",\"auth_type\":\"0\"}]}],\"id\":2},{\"type\":\"3\",\"content\":[{\"name\":\"\\u5e38\\u7528\\u5e94\\u7528\",\"data\":[{\"uuid\":\"49f4a5f9ee3c45b88c07b4558ea5c51a\",\"name\":\"\\u7b7e\\u5230\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364567?fileid=3364567&ts=1583962782024&sign=6e885c71f2fe6d47acfcb6bd1b11eb1c\",\"redirect_url\":\"http:\\/\\/eqd.oa.colourlife.com\\/cailife\\/sign\\/main\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"59927103bdd7428fa4e2dcb806b96a01\",\"name\":\"\\u90ae\\u4ef6\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364620?fileid=3364620&ts=1583960783545&sign=d593e3688dbb66b97ac889e43469c95a\",\"redirect_url\":\"http:\\/\\/mail.oa.colourlife.com:40060\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"4b8b87adff5f41f7acd3a7b4e753c93f\",\"name\":\"\\u5ba1\\u6279\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364634?fileid=3364634&ts=1583941394513&sign=702f0d9506056450489e14dcf8c7b8f7\",\"redirect_url\":\"http:\\/\\/bpm.ice.colourlife.com\\/portal\\/login\\/sso.action?number=0\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"adcfcf393d804e5898a1a3ecd89ffe55\",\"name\":\"\\u901a\\u77e5\\u516c\\u544a\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364647?fileid=3364647&ts=1583889832445&sign=36cd0536f6785c25175af913894c310d\",\"redirect_url\":\"http:\\/\\/notice.oa.colourlife.com:40063\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"}]},{\"name\":\"\\u534f\\u540c\\u529e\\u516c\",\"data\":[{\"uuid\":\"7a11a273311543d48dbf9e9ae672e4fd\",\"name\":\"\\u4efb\\u52a1\\u7cfb\\u7edf\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364662?fileid=3364662&ts=1583894810939&sign=4cb4a64e98774861e49e80ac5aff4345\",\"redirect_url\":\"http:\\/\\/mbee.oa.colourlife.com:4600\\/home\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"e96022dbbab249c58e58faf744a6a6a5\",\"name\":\"\\u75ab\\u60c5\\u4e0a\\u62a5\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/10875907?fileid=10875907&ts=1583916929610&sign=be4ad94fa5956bd1d06d8fda9acbacad\",\"redirect_url\":\"https:\\/\\/service-czy.colourlife.com\\/care\",\"auth_type\":\"3\",\"quantity\":\"0\"},{\"uuid\":\"d93121ce3da042afb77ed3e4b0806630\",\"name\":\"\\u5065\\u5eb7\\u7533\\u62a5\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/11552503?fileid=11552503&ts=1583922670991&sign=c3da7636cd78f24d12c3652d44a0eb1f\",\"redirect_url\":\"https:\\/\\/wxpt-czy.colourlife.com\\/reporting_platform\\/\",\"auth_type\":\"4\",\"quantity\":\"0\"},{\"uuid\":\"e85e95cd6bce4288bad933c2f9ef8f75\",\"name\":\"\\u5feb\\u9012\\u7ba1\\u7406\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/11630029?fileid=11630029&ts=1583941696547&sign=a9623f24c867050151d3375b70d14103\",\"redirect_url\":\"https:\\/\\/express-czy.colourlife.com\",\"auth_type\":\"4\",\"quantity\":\"0\"},{\"uuid\":\"a7f10faf17974046ad7cbfd95ec861f8\",\"name\":\"\\u4f01\\u4e1a\\u670d\\u52a1\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/11753775?fileid=11753775&ts=1583893480064&sign=bf13341c44d8fd35083b4023320cc817\",\"redirect_url\":\"https:\\/\\/wxpt-czy.colourlife.com\\/colourlife_apply\\/\",\"auth_type\":\"4\",\"quantity\":\"0\"},{\"uuid\":\"0ff2a2dc968c4b06b760dd450562670d\",\"name\":\"\\u4e1a\\u4e3b\\u5ba1\\u6838\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/11824884?fileid=11824884&ts=1583953155948&sign=f5826214cca5520fd30120cd62d0bfdd\",\"redirect_url\":\"https:\\/\\/wxpt-czy.colourlife.com\\/colourlife_userapply\\/\",\"auth_type\":\"4\",\"quantity\":\"0\"},{\"uuid\":\"52705414718c4f2e99c8d013deacfd03\",\"name\":\"\\u6587\\u4ef6\\u67dc\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364683?fileid=3364683&ts=1583952841288&sign=f7c0b3847e3709445e5eadcc063f0712\",\"redirect_url\":\"http:\\/\\/files.oa.colourlife.com:40064\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"ce8ef918e08542f683c8a7ab2c720983\",\"name\":\"\\u5408\\u4f5c\\u516c\\u53f8\\u5ba1\\u6279\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3370024?fileid=3370024&ts=1583953287184&sign=f8bf8150b33cacc14b074e5deea9137b\",\"redirect_url\":\"http:\\/\\/cgjbpm-stock.colourlife.com\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"}]},{\"name\":\"\\u7269\\u4e1a\\u7ba1\\u7406\",\"data\":[{\"uuid\":\"9b288e4aa73947239dda6bb3581e3c3c\",\"name\":\"\\u6295\\u8bc9\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364699?fileid=3364699&ts=1583958616036&sign=368c24b8159fd3a4f54d5deab4c841e8\",\"redirect_url\":\"http:\\/\\/servicing.czy.colourlife.com:40025\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"2b73a89bdc214aa983e1575941e960e1\",\"name\":\"\\u4fdd\\u517b\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364705?fileid=3364705&ts=1583945704803&sign=233b398ebb9d26a2963f11cdc2a55913\",\"redirect_url\":\"http:\\/\\/ebyv2.colourlife.com\\/master\\/login\\/colour\\/life\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"e1a1370d03624a67a07fc9747805170c\",\"name\":\"\\u7eff\\u5316\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364712?fileid=3364712&ts=1583948036682&sign=ad2cc1d39723cce5f790f58f27ecbb94\",\"redirect_url\":\"http:\\/\\/eqj.colourlife.com\\/\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"dd71d7143a4b44d1b12631dfcd668758\",\"name\":\"\\u505c\\u8f66\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364725?fileid=3364725&ts=1583949063276&sign=1d84d5868b018aeb799732744ce22033\",\"redirect_url\":\"http:\\/\\/m.aparcar.cn\\/oa\\/butler\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"ab096af8682c438cb64a88f73b5e9a42\",\"name\":\"\\u5165\\u4f19\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369364?fileid=3369364&ts=1583965099126&sign=3aba40ab9fa3eb92b5652ef555b43fd7\",\"redirect_url\":\"https:\\/\\/echeckin-cgj.colourlife.com\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"814d2debae77486e81b78a92ed86860c\",\"name\":\"\\u7ef4\\u4fee\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/7391253?fileid=7391253&ts=1583947054697&sign=15a627429acffe22f4cb929e405ad9b8\",\"redirect_url\":\"http:\\/\\/esf.colourlife.com\\/commonentry\\/cgjOauth\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"76cae307a7de450fa2b31ab8636c4177\",\"name\":\"\\u88c5\\u4fee\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369405?fileid=3369405&ts=1583894433322&sign=653c77996f37bcfee5d189bac8f8f370\",\"redirect_url\":\"http:\\/\\/cgj.ezxvip.com\\/\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"53223e7e9d99479db8a172607cc7e4d7\",\"name\":\"\\u7535\\u68af\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369419?fileid=3369419&ts=1583960559992&sign=ef06a07185a0b6322de3a8847a9a959c\",\"redirect_url\":\"http:\\/\\/cgj.1lift.cn\\/cgj\\/auth\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"366b008f99674bc1a2625dea5577add1\",\"name\":\"\\u6536\\u8d39\\u7ba1\\u7406\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369440?fileid=3369440&ts=1583965968761&sign=ba244a1416d3e41ccb79f9da7c96792e\",\"redirect_url\":\"http:\\/\\/ipos.colourlife.com\\/index.php?s=\\/Home\\/Index\\/oauthlogin.html&access_token=\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"dacfd9821b7f4339af88125b7bd597fe\",\"name\":\"\\u5fae\\u5546\\u5708\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369600?fileid=3369600&ts=1583960560323&sign=f3ada5479df7c6e422568d75a1197100\",\"redirect_url\":\"https:\\/\\/wsq.colourlife.com\\/agent\\/index\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"a62a2888bb0e47f1a821856603373a4b\",\"name\":\"\\u4e03\\u661f\\u8d28\\u68c0\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369629?fileid=3369629&ts=1583931698963&sign=794dade03e1dd766429c5661912ca219\",\"redirect_url\":\"https:\\/\\/operation.colourlife.com\\/staff\\/login\",\"auth_type\":\"3\",\"quantity\":\"0\"},{\"uuid\":\"67001c324e9d458f917431d48e319c41\",\"name\":\"\\u96c6\\u4e2d\\u7ba1\\u63a7\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369653?fileid=3369653&ts=1583960784346&sign=e7bf703cbfd9f74c813b296a0d591827\",\"redirect_url\":\"https:\\/\\/datacollect-czy.colourlife.com\\/frontend\\/employee\\/\",\"auth_type\":\"3\",\"quantity\":\"0\"},{\"uuid\":\"3d8a3c760e0046969f6cbdeefd0dd2f3\",\"name\":\"\\u6284\\u8868\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369680?fileid=3369680&ts=1583945987462&sign=03993caa481421a5c9ce3b3a8ac8c335\",\"redirect_url\":\"http:\\/\\/metering.charge.colourlife.com:40026\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"7c4175c771644c74b66b3c46791cfc15\",\"name\":\"\\u50ac\\u8d39\\u8ddf\\u8e2a\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369705?fileid=3369705&ts=1583964661278&sign=967828397ad4ac4f350dd1d8fe49c51e\",\"redirect_url\":\"http:\\/\\/case.backyard.colourlife.com:4800\\/home\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"c46486a0521b48ecb011818ae986700d\",\"name\":\"\\u6536\\u8d39\\u7cfb\\u7edf\\u770b\\u677f\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369787?fileid=3369787&ts=1583948288339&sign=3e18805746123474c73c00b003cc70ff\",\"redirect_url\":\"https:\\/\\/sfkb-cgj.colourlife.com\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"a6382878459a4554b06ae8b9e2170dc2\",\"name\":\"\\u6765\\u8bbf\\u767b\\u8bb0\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3579365?fileid=3579365&ts=1583964663445&sign=41dc4e90c389dfef491f2a08154cd58f\",\"redirect_url\":\"https:\\/\\/visitor-czy.colourlife.com\",\"auth_type\":\"4\",\"quantity\":\"0\"},{\"uuid\":\"7427b62b84b141889ece7d1c13bae3e0\",\"name\":\"\\u4fdd\\u6d01\\u62a5\\u8868\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3588236?fileid=3588236&ts=1583949177863&sign=d93758c272acae4e1a36ceff903ed162\",\"redirect_url\":\"https:\\/\\/ebj.colourlife.com\\/boot\\/#\\/cmlogin\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"cab738b2d25d46ed9ae54542413a2d37\",\"name\":\"\\u95e8\\u7981\\u7ba1\\u5bb6\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/5263185?fileid=5263185&ts=1583953156405&sign=174e3ab02cbb1def6bf44c788803be0b\",\"redirect_url\":\"colourlife:\\/\\/proto?type=entranceGuard\",\"auth_type\":\"0\",\"quantity\":\"0\"}]},{\"name\":\"\\u4eba\\u4e8b\\u7ba1\\u7406\",\"data\":[{\"uuid\":\"460fe01100b445659a0ff0db6a5c13b0\",\"name\":\"\\u62db\\u8058\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369829?fileid=3369829&ts=1583974163967&sign=c878839ba1a23ad27a5923d724139714\",\"redirect_url\":\"https:\\/\\/zhaopin-cgj.colourlife.com\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"79cf2c988bcd4a35a785bd2b2d7fb5c3\",\"name\":\"E\\u542f\\u5b66\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369864?fileid=3369864&ts=1583934452941&sign=5798c9cb51a584eacb1966b6e5415b0a\",\"redirect_url\":\"https:\\/\\/peixun.colourlife.com\\/app\\/index.php?i=1&c=entry&m=uwechat_cms&do=mobile\",\"auth_type\":\"3\",\"quantity\":\"0\"}]},{\"name\":\"\\u5176\\u4ed6\",\"data\":[{\"uuid\":\"ab9cc8887a7444de94d91705b22451c3\",\"name\":\"\\u5bf9\\u516c\\u8d26\\u6237\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369881?fileid=3369881&ts=1583954606824&sign=f9abab9baec2519cf98552401de610e1\",\"redirect_url\":\"colourlife:\\/\\/proto?type=dgzh\",\"auth_type\":\"0\",\"quantity\":\"0\"},{\"uuid\":\"1895f3e4ae084921abc39d51743dad1d\",\"name\":\"\\u95ee\\u5377\\u8c03\\u67e5\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369894?fileid=3369894&ts=1583950487554&sign=2fd7d8d16eeee25ed50639c02450f206\",\"redirect_url\":\"https:\\/\\/service-czy.colourlife.com\\/cgjRedirect\",\"auth_type\":\"3\",\"quantity\":\"0\"},{\"uuid\":\"6c25b6c0c7b145dfb7328f6ca773d5e4\",\"name\":\"\\u7ee9\\u6548\\u5408\\u540c\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369921?fileid=3369921&ts=1583951227140&sign=5a69331f2f50b9b20f641ebf605d9ee6\",\"redirect_url\":\"https:\\/\\/qianyue-hr.colourlife.com\\/redirect\",\"auth_type\":\"3\",\"quantity\":\"0\"},{\"uuid\":\"e228084d96ce43228a320e063ffa26a1\",\"name\":\"\\u7ed1\\u5b9a\\u5f69\\u4e4b\\u4e91\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369935?fileid=3369935&ts=1583968621849&sign=b90f09975ccd38556297df6110f1da0c\",\"redirect_url\":\"https:\\/\\/evisit.colourlife.com\\/cgj\\/bindcustomer\\/bind_account.html\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"55b0e63dded74d92b4b91cca1c93d883\",\"name\":\"\\u5e02\\u573a\\u8fd0\\u8425\\u5e73\\u53f0\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369961?fileid=3369961&ts=1583969650046&sign=2f3f4762b44aaa2c4269d84b51826450\",\"redirect_url\":\"https:\\/\\/market.colourlife.com\\/api\\/mobile\\/single_point\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"eb1a8685d5444d30844410d4b76970b5\",\"name\":\"\\u5ba1\\u62791.0\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3370001?fileid=3370001&ts=1583951099197&sign=94b1c37f3cda7db6343b8d6089d68f0e\",\"redirect_url\":\"http:\\/\\/spsso.colourlife.net\\/login.aspx\",\"auth_type\":\"1\",\"quantity\":\"0\"},{\"uuid\":\"09d89007575a440388d7c3a1642ad52f\",\"name\":\"\\u7eff\\u5316\\uff08\\u673a\\u52a8\\uff09\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3370036?fileid=3370036&ts=1583922974111&sign=231603cd69bb61bcd650971cbc413d63\",\"redirect_url\":\"http:\\/\\/caoquewang.colourlife.com\\/\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"e8a2e2632d5345dba9486dcae98e20e5\",\"name\":\"\\u4e3b\\u4efb\\u5ba1\\u6838\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/4527259?fileid=4527259&ts=1583950072791&sign=f5443edebec6f1ad5507f8a4936adff2\",\"redirect_url\":\"https:\\/\\/zhuren-hro.colourlife.com\",\"auth_type\":\"4\",\"quantity\":\"0\"},{\"uuid\":\"19149b114721418187d0982d1dfddf35\",\"name\":\"\\u5f69\\u5bcc\\u6218\\u51b5\\u770b\\u677f\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/5557580?fileid=5557580&ts=1583932964704&sign=fd224dcc8f4f8032ea55a6605536d573\",\"redirect_url\":\"https:\\/\\/caifu.colourlife.com\\/kanban\\/#\\/index\",\"auth_type\":\"0\",\"quantity\":\"0\"},{\"uuid\":\"a8c1a407876d41b5a5bcb1081ffc47fc\",\"name\":\"\\u505c\\u8f66\\u7ef4\\u4fee\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369392?fileid=3369392&ts=1583936232319&sign=5d088699091980496e8d678b6d3f2d8f\",\"redirect_url\":\"https:\\/\\/m.eshifu.cn\\/common\\/cgjOauth\",\"auth_type\":\"2\",\"quantity\":\"0\"}]}],\"id\":3}],\"contentEncrypt\":\"\"}";
    }

    interface DOWN {
        // 下载文件保存路径
        String DOWNLOAD_DIRECT = Environment.getExternalStorageDirectory()
                + "/colourlife/download/";
    }
}
