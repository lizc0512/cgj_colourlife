package com.tg.coloursteward.constant;

import android.os.Environment;

/**
 * 全局变量
 */
public interface Contants {

    interface URl {

        //////////////    正式地址   //////////////////////////////
//        String URL_ICETEST = "https://openapi.colourlife.com/v1";//
//        String URL_OAUTH2 = "https://oauth2-cgj.colourlife.com";
//        String SINGLE_DEVICE = "https://single.colourlife.com";
//        String URL_NEW = "https://cgj-backyard.colourlife.com";
//        String URL_ICESTAFF = "https://staff-ice.colourlife.com";
//        String CLIENT_SECRET = "t2o0a1xl2lOmoPi4tuHf5uw4VZloXGs7y1Kd0Yoq";
//        String URL_QRCODE = "https://qrcode.colourlife.com";
//        String URL_IMPUSH = "https://impush-cgj.colourlife.com";
//        String VERSION_ADDRESS = "https://version.colourlife.com";
//        String URL_H5OAUTH = "https://oauth-czy.colourlife.com";
//        String URL_LEKAI = "https://lekaiadminapi-door.colourlife.com";
//        String ACCOUNT_ADDRESS = "https://account-finance.colourlife.com/";//新版彩钱包
//        String DELIVERY_HOME_ADDRESS = "https://gexpressbackend-czy.colourlife.com";
//        String DELIVERY_NUMBER_ADDRESS = "https://kdbackend-czy.colourlife.com"; //快单号
//        String DELIVERY_COMPANY_ADDRESS = "https://gexpressbackend-czy.colourlife.com";//快递公司
//        String DELIVERY_ADDRESS_URL = "https://gexpress-czytest.colourlife.com/new_express/#/pages/address/address?type=selected";//快递地址url
//        String environment = "release";
//        String cqj_appid = "327494513335603200";
//        String TOKEN_ADDRESS = "https://oauth2czy.colourlife.com";
//        String publicKeyString = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTFnAR7ORLx0jGzf9Ux1We7yHvRi+kQXKSRmtgBjDCXQzakGm2mrb6EupCkDbUcj4BUs7S7zm/rICQuVNC9fujeJGj"
//                + "cNWRg0XWVtm90XpbTqfKiXzGDHI9W8aULYZ3of/JJ9lyCyjqjigyCdLBPtQ27gOu"
//                + "boDzQuieR2ywPHawzQIDAQAB";
//        int SAVENOHTTPRECORD = 0;
//        String introduce = "http://mapp.colourlife.com/introduce/introduce.html";
//        String privacy = "http://mapp.colourlife.com/xieyi/yinsi.html";
//        String agreement = "http://mapp.colourlife.com/xieyi/fuwuxieyi.html";

        ////////////////  测试地址   ///////////////////////////////////
        String URL_ICETEST = "https://openapi-test.colourlife.com/v1";//
        String URL_OAUTH2 = "https://oauth2-cgj-test.colourlife.com";
        String SINGLE_DEVICE = "https://single-czytest.colourlife.com";
        String URL_NEW = "https://cgj-backyard-test.colourlife.com";
        String URL_ICESTAFF = "http://staff.ice.test.colourlife.com";
        String CLIENT_SECRET = "xlsfrQS5R49upmfZbhlsrUzAt9HDA5K4ptLYsqK5";
        String URL_QRCODE = "http://qrcode-czytest.colourlife.com";
        String URL_IMPUSH = "https://impush-cgj-test.colourlife.com";
        String VERSION_ADDRESS = "https://version-czytest.colourlife.com";
        String URL_H5OAUTH = "https://oauth-czytest.colourlife.com";
        String URL_LEKAI = "https://lekaiadminapi-doortest.colourlife.com";
        String ACCOUNT_ADDRESS = "https://account-finance-test.colourlife.com";//新版彩钱包
        String DELIVERY_HOME_ADDRESS = "https://gexpressbackend-czytest.colourlife.com";
        String DELIVERY_NUMBER_ADDRESS = "https://kdbackend-czytest.colourlife.com"; //快单号
        String DELIVERY_COMPANY_ADDRESS = "https://gexpressbackend-czytest.colourlife.com";//快递公司
        String DELIVERY_ADDRESS_URL = "https://gexpress-czytest.colourlife.com/new_express/#/pages/address/address?type=selected";//快递地址url
        String environment = "debug";
        String cqj_appid = "323521861252157440";
        String TOKEN_ADDRESS = "http://oauth2-czytest.colourlife.com";
        String publicKeyString = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDZDqnWph9LxtD0zgtGAYT" +
                "Tf2jYeV+ni5F1o0w3Fag4OOD1YHCRUCXIsFy+iJYmuPf5vMkZrkoiJmKBfkaIzNlrJZzHzq+LsPQNCF86p1nLsuHbkWNvy" +
                "jOEPn/CUryP2Kxme4S+eEqLIeNwp70VOaMuPmRoEZxMDAgvc6Z0DWsVdQIDAQAB";
        int SAVENOHTTPRECORD = 1;
        String introduce = "http://mapp-czytest.colourlife.com/introduce/introduce.html";
        String privacy = "http://mapp-czytest.colourlife.com/xieyi/yinsi.html";
        String agreement = "http://mapp-czytest.colourlife.com/xieyi/fuwuxieyi.html";

    }

    interface APP {
        String CORP_UUID = "a8c58297436f433787725a94f780a3c9"; //彩生活租户ID
        String APP_KEY = "Q9PDXKXJbBCHDWF0CFS8MLeX";//合合 SDK识别key
        /***彩管家4.0加密的秘钥***/
        String secertKey = "gbiwgbiwkgnkwgnkjbkkrthmnjwjgeh";
        String buglyKeyId = "b3dcc32611";
        String WEIXIN_APP_ID = "wx2cd55a3733a9aa2e";
        String LICENSE_KEY = "5C96AA97189FB29346817D34BCCE17ADC5E5CB755341DEB2DE0F7F105924EE6B9441AAB0E7EFA35ADE2C722F4B2C3904B92E33EFE403AEADDB51CF477ABD6639D4923580A907CEC1D64F04AE245A6C8C22D96E0882E5C49D61E5DD8EAF262FCCE633";
    }

    interface EVENT {
        int changeCorp = 700;
        int changeOrg = 800;
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
        String DEVICE_TOKEN = "device_token";//设备唯一token
        String LATITUDE = "str_latitude";//纬度
        String LONGITUDE = "str_longitude";//经度
        String FRAGMENTMINE = "fragmentmine";//我的页面
        String SALARY_TIME = "salary_time";//工资条打开时间
        String SALARY_ISINPUT = "salary_isinput";//工资条打开状态
        String HomePageAd = "homepagead";
        String ImageType = "imagetype";
        String fragmentminedata = "{\"code\":0,\"message\":\"success\",\"content\":[{\"id\":1,\"data\":[{\"id\":4,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd7de4f644652159.png\",\"url\":\"colourlife:\\/\\/proto?type=redPacket\",\"name\":\"\\u6211\\u7684\\u996d\\u7968\",\"group_id\":1},{\"id\":5,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd84176091219280.png\",\"url\":\"colourlife:\\/\\/proto?type=findPwd\",\"name\":\"\\u627e\\u56de\\u652f\\u4ed8\\u5bc6\\u7801\",\"group_id\":1},{\"id\":2,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd8278f931489523.png\",\"url\":\"https:\\/\\/payroll-hr.colourlife.com\\/redirect\",\"name\":\"\\u6211\\u7684\\u5de5\\u8d44\\u6761\",\"group_id\":1}]},{\"id\":2,\"data\":[{\"id\":1,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd819d0ae5124346.png\",\"url\":\"colourlife:\\/\\/proto?type=invite\",\"name\":\"\\u9080\\u8bf7\",\"group_id\":2},{\"id\":6,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd84cea0f0210390.png\",\"url\":\"colourlife:\\/\\/proto?type=mydownload\",\"name\":\"\\u6211\\u7684\\u4e0b\\u8f7d\",\"group_id\":2},{\"id\":3,\"img\":\"https:\\/\\/pics-czy-cdn.colourlife.com\\/pro-5c1cd83307e22424071.png\",\"url\":\"colourlife:\\/\\/proto?type=setting\",\"name\":\"\\u8bbe\\u7f6e\",\"group_id\":2}]}],\"contentEncrypt\":\"\"}";
        String MICRODATA = "{\"code\":0,\"message\":\"success\",\"content\":[{\"type\":\"1\",\"content\":[{\"name\":\"banner\",\"data\":[{\"uuid\":\"004f4408af554d13944d001bae4fb032\",\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/10880393?fileid=10880393&ts=1585024188135&sign=ff9309162b9dd70a0588ed123d1d421c\",\"redirect_url\":\"https:\\/\\/colourhome.colourlife.com\\/epidemic_situation\\/home.html\",\"auth_type\":\"0\"}]}],\"id\":1},{\"type\":\"2\",\"content\":[{\"name\":\"\\u6570\\u636e\",\"data\":[{\"uuid\":\"communityStatistics\",\"name\":\"\\u5408\\u540c\\u9762\\u79ef\\uff08\\u4e07\\u33a1\\uff09\",\"data\":\"142373.96\",\"redirect_url\":\"colourlife:\\/\\/proto?type=onlinEarea\",\"auth_type\":\"0\"},{\"uuid\":\"caihuiOrderStatistics\",\"name\":\"\\u4eca\\u65e5\\u5f69\\u60e0\\u8ba2\\u5355\",\"data\":\"\",\"redirect_url\":\"https:\\/\\/caihui.colourlife.com\\/data_view\\/#\\/\",\"auth_type\":\"0\"},{\"uuid\":\"cshShares\",\"name\":\"\\u96c6\\u56e2\\u80a1\\u7968\",\"data\":\"\",\"redirect_url\":\"http:\\/\\/image.sinajs.cn\\/newchart\\/hk_stock\\/min\\/01778.gif?20200324190347000044861\",\"auth_type\":\"0\"}]}],\"id\":2},{\"type\":\"3\",\"content\":[{\"name\":\"\\u5e38\\u7528\\u5e94\\u7528\",\"data\":[{\"uuid\":\"49f4a5f9ee3c45b88c07b4558ea5c51a\",\"name\":\"\\u7b7e\\u5230\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364567?fileid=3364567&ts=1584972387195&sign=bd5b0bd2697088174cefbcb366e4f1e3\",\"redirect_url\":\"http:\\/\\/eqd.oa.colourlife.com\\/cailife\\/sign\\/main\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"59927103bdd7428fa4e2dcb806b96a01\",\"name\":\"\\u90ae\\u4ef6\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364620?fileid=3364620&ts=1584983799434&sign=ecb65b64acfa6df435067dfbfaebd926\",\"redirect_url\":\"http:\\/\\/mail.oa.colourlife.com:40060\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"4b8b87adff5f41f7acd3a7b4e753c93f\",\"name\":\"\\u5ba1\\u6279\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364634?fileid=3364634&ts=1585039650891&sign=e3380dec5af994154549ebf5243c0040\",\"redirect_url\":\"http:\\/\\/bpm.ice.colourlife.com\\/portal\\/login\\/sso.action?number=0\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"adcfcf393d804e5898a1a3ecd89ffe55\",\"name\":\"\\u901a\\u77e5\\u516c\\u544a\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364647?fileid=3364647&ts=1584998142700&sign=3600a243aea0134a49180fc726c5b14e\",\"redirect_url\":\"http:\\/\\/notice.oa.colourlife.com:40063\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"}]},{\"name\":\"\\u534f\\u540c\\u529e\\u516c\",\"data\":[{\"uuid\":\"7a11a273311543d48dbf9e9ae672e4fd\",\"name\":\"\\u4efb\\u52a1\\u7cfb\\u7edf\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364662?fileid=3364662&ts=1584997524457&sign=ffb1f9092ecfb7ec54eab8a6c1485095\",\"redirect_url\":\"http:\\/\\/mbee.oa.colourlife.com:4600\\/home\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"e96022dbbab249c58e58faf744a6a6a5\",\"name\":\"\\u75ab\\u60c5\\u4e0a\\u62a5\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/10875907?fileid=10875907&ts=1585017101169&sign=efb9f823501df4ecaa46a56ecbc60e84\",\"redirect_url\":\"https:\\/\\/service-czy.colourlife.com\\/care\",\"auth_type\":\"3\",\"quantity\":\"0\"},{\"uuid\":\"d93121ce3da042afb77ed3e4b0806630\",\"name\":\"\\u5065\\u5eb7\\u7533\\u62a5\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/11552503?fileid=11552503&ts=1585021463298&sign=19f8140583b690e6b8fb14dbd1c7fc48\",\"redirect_url\":\"https:\\/\\/wxpt-czy.colourlife.com\\/reporting_platform\\/\",\"auth_type\":\"4\",\"quantity\":\"0\"},{\"uuid\":\"e85e95cd6bce4288bad933c2f9ef8f75\",\"name\":\"\\u5feb\\u9012\\u7ba1\\u7406\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/11630029?fileid=11630029&ts=1585038875889&sign=0c3248b1246f4b3f08d1193f0b9f960a\",\"redirect_url\":\"https:\\/\\/gexpress-czy.colourlife.com\",\"auth_type\":\"4\",\"quantity\":\"0\"},{\"uuid\":\"a7f10faf17974046ad7cbfd95ec861f8\",\"name\":\"\\u4f01\\u4e1a\\u670d\\u52a1\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/11753775?fileid=11753775&ts=1584995407951&sign=fdead3e5f3a2196f9f877a860e9b6483\",\"redirect_url\":\"https:\\/\\/wxpt-czy.colourlife.com\\/colourlife_apply\\/\",\"auth_type\":\"4\",\"quantity\":\"0\"},{\"uuid\":\"0ff2a2dc968c4b06b760dd450562670d\",\"name\":\"\\u4e1a\\u4e3b\\u5ba1\\u6838\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/11824884?fileid=11824884&ts=1584969592196&sign=ade87c8099d9ea3b95f54630e3fdfbcf\",\"redirect_url\":\"https:\\/\\/wxpt-czy.colourlife.com\\/colourlife_userapply\\/\",\"auth_type\":\"4\",\"quantity\":\"0\"},{\"uuid\":\"52705414718c4f2e99c8d013deacfd03\",\"name\":\"\\u6587\\u4ef6\\u67dc\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364683?fileid=3364683&ts=1584969713326&sign=42e13172f787d07dda292faef0c939a0\",\"redirect_url\":\"http:\\/\\/files.oa.colourlife.com:40064\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"ce8ef918e08542f683c8a7ab2c720983\",\"name\":\"\\u5408\\u4f5c\\u516c\\u53f8\\u5ba1\\u6279\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3370024?fileid=3370024&ts=1584967301176&sign=67c4ec1d83cd10483a9c7ae023aba3e2\",\"redirect_url\":\"http:\\/\\/cgjbpm-stock.colourlife.com\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"}]},{\"name\":\"\\u7269\\u4e1a\\u7ba1\\u7406\",\"data\":[{\"uuid\":\"9b288e4aa73947239dda6bb3581e3c3c\",\"name\":\"\\u6295\\u8bc9\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364699?fileid=3364699&ts=1584967618155&sign=8de957c4c34cc9ca6c06706bf150cfd2\",\"redirect_url\":\"http:\\/\\/servicing.czy.colourlife.com:40025\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"2b73a89bdc214aa983e1575941e960e1\",\"name\":\"\\u4fdd\\u517b\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364705?fileid=3364705&ts=1584967760986&sign=8f76e306df71e14c8b24348f0e7a7aa6\",\"redirect_url\":\"http:\\/\\/ebyv2.colourlife.com\\/master\\/login\\/colour\\/life\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"e1a1370d03624a67a07fc9747805170c\",\"name\":\"\\u7eff\\u5316\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364712?fileid=3364712&ts=1585044381431&sign=874d252d48a69047b9334646a3dbd341\",\"redirect_url\":\"http:\\/\\/eqj.colourlife.com\\/\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"dd71d7143a4b44d1b12631dfcd668758\",\"name\":\"\\u505c\\u8f66\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3364725?fileid=3364725&ts=1584967970995&sign=cc831f3f5f280a5415d3db206df8e406\",\"redirect_url\":\"http:\\/\\/m.aparcar.cn\\/oa\\/butler\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"ab096af8682c438cb64a88f73b5e9a42\",\"name\":\"\\u5165\\u4f19\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369364?fileid=3369364&ts=1584980867693&sign=bfedbdd105e738f07f66f3126bcedf60\",\"redirect_url\":\"https:\\/\\/echeckin-cgj.colourlife.com\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"814d2debae77486e81b78a92ed86860c\",\"name\":\"\\u7ef4\\u4fee\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/7391253?fileid=7391253&ts=1584963254508&sign=54419f6260f0f425f05beadcb18a5001\",\"redirect_url\":\"http:\\/\\/esf.colourlife.com\\/commonentry\\/cgjOauth\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"76cae307a7de450fa2b31ab8636c4177\",\"name\":\"\\u88c5\\u4fee\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369405?fileid=3369405&ts=1584990492680&sign=b035ca9d56d38f1be646047281deadbc\",\"redirect_url\":\"http:\\/\\/cgj.ezxvip.com\\/\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"53223e7e9d99479db8a172607cc7e4d7\",\"name\":\"\\u7535\\u68af\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369419?fileid=3369419&ts=1584978996343&sign=87c3ddbbef306b1196cf8cd7ba6f03eb\",\"redirect_url\":\"http:\\/\\/cgj.1lift.cn\\/cgj\\/auth\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"366b008f99674bc1a2625dea5577add1\",\"name\":\"\\u6536\\u8d39\\u7ba1\\u7406\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369440?fileid=3369440&ts=1584975386949&sign=244fa1e48634d5ea6fa88da933540fa9\",\"redirect_url\":\"http:\\/\\/ipos.colourlife.com\\/index.php?s=\\/Home\\/Index\\/oauthlogin.html&access_token=\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"dacfd9821b7f4339af88125b7bd597fe\",\"name\":\"\\u5fae\\u5546\\u5708\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369600?fileid=3369600&ts=1584975343280&sign=a857e720b3cc0ad63e847fc117c07566\",\"redirect_url\":\"https:\\/\\/wsq.colourlife.com\\/agent\\/index\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"a62a2888bb0e47f1a821856603373a4b\",\"name\":\"\\u4e03\\u661f\\u8d28\\u68c0\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369629?fileid=3369629&ts=1585032114722&sign=9894702c929e2908faf6a5f7c2e035fa\",\"redirect_url\":\"https:\\/\\/operation.colourlife.com\\/staff\\/login\",\"auth_type\":\"3\",\"quantity\":\"0\"},{\"uuid\":\"67001c324e9d458f917431d48e319c41\",\"name\":\"\\u96c6\\u4e2d\\u7ba1\\u63a7\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369653?fileid=3369653&ts=1584974143114&sign=f955fee39b599aad8fd9e75a73a3506e\",\"redirect_url\":\"https:\\/\\/datacollect-czy.colourlife.com\\/frontend\\/employee\\/\",\"auth_type\":\"3\",\"quantity\":\"0\"},{\"uuid\":\"3d8a3c760e0046969f6cbdeefd0dd2f3\",\"name\":\"\\u6284\\u8868\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369680?fileid=3369680&ts=1584965131958&sign=1dd693f33d13e541e39bad8f42935aed\",\"redirect_url\":\"http:\\/\\/metering.charge.colourlife.com:40026\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"7c4175c771644c74b66b3c46791cfc15\",\"name\":\"\\u50ac\\u8d39\\u8ddf\\u8e2a\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369705?fileid=3369705&ts=1584979616164&sign=82f26d18ad3ef8aa08b9eef1533df89b\",\"redirect_url\":\"http:\\/\\/case.backyard.colourlife.com:4800\\/home\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"c46486a0521b48ecb011818ae986700d\",\"name\":\"\\u6536\\u8d39\\u7cfb\\u7edf\\u770b\\u677f\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369787?fileid=3369787&ts=1585039851094&sign=6bc4efdf4ab8ad6e8070433b9fbde828\",\"redirect_url\":\"https:\\/\\/sfkb-cgj.colourlife.com\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"a6382878459a4554b06ae8b9e2170dc2\",\"name\":\"\\u6765\\u8bbf\\u767b\\u8bb0\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3579365?fileid=3579365&ts=1584980167818&sign=1e44bfc5fcab614e5e67d626c28b6148\",\"redirect_url\":\"https:\\/\\/visitor-czy.colourlife.com\",\"auth_type\":\"4\",\"quantity\":\"0\"},{\"uuid\":\"7427b62b84b141889ece7d1c13bae3e0\",\"name\":\"\\u4fdd\\u6d01\\u62a5\\u8868\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3588236?fileid=3588236&ts=1585044118031&sign=c5c58843549f4eff76fe70cbc838f74a\",\"redirect_url\":\"https:\\/\\/ebj.colourlife.com\\/boot\\/#\\/cmlogin\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"cab738b2d25d46ed9ae54542413a2d37\",\"name\":\"\\u95e8\\u7981\\u7ba1\\u5bb6\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/5263185?fileid=5263185&ts=1585044653597&sign=38dcd210cddaae78556ba1801efdbe3a\",\"redirect_url\":\"colourlife:\\/\\/proto?type=entranceGuard\",\"auth_type\":\"0\",\"quantity\":\"0\"}]},{\"name\":\"\\u4eba\\u4e8b\\u7ba1\\u7406\",\"data\":[{\"uuid\":\"460fe01100b445659a0ff0db6a5c13b0\",\"name\":\"\\u62db\\u8058\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369829?fileid=3369829&ts=1584993498587&sign=f6a10d3fcac0de2fa11d313387f7a677\",\"redirect_url\":\"https:\\/\\/zhaopin-cgj.colourlife.com\\/login\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"79cf2c988bcd4a35a785bd2b2d7fb5c3\",\"name\":\"E\\u542f\\u5b66\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369864?fileid=3369864&ts=1585033847202&sign=2228dfbcc50ca212ff7a717cda1fbe01\",\"redirect_url\":\"https:\\/\\/peixun.colourlife.com\\/app\\/index.php?i=1&c=entry&m=uwechat_cms&do=mobile\",\"auth_type\":\"3\",\"quantity\":\"0\"}]},{\"name\":\"\\u5176\\u4ed6\",\"data\":[{\"uuid\":\"ab9cc8887a7444de94d91705b22451c3\",\"name\":\"\\u5bf9\\u516c\\u8d26\\u6237\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369881?fileid=3369881&ts=1584965399844&sign=aa1b13976ee391f732bd388ab55c9ed8\",\"redirect_url\":\"colourlife:\\/\\/proto?type=dgzh\",\"auth_type\":\"0\",\"quantity\":\"0\"},{\"uuid\":\"1895f3e4ae084921abc39d51743dad1d\",\"name\":\"\\u95ee\\u5377\\u8c03\\u67e5\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369894?fileid=3369894&ts=1584962253185&sign=d6e14b70e21158f9a6569ec3786b0c45\",\"redirect_url\":\"https:\\/\\/service-czy.colourlife.com\\/cgjRedirect\",\"auth_type\":\"3\",\"quantity\":\"0\"},{\"uuid\":\"6c25b6c0c7b145dfb7328f6ca773d5e4\",\"name\":\"\\u7ee9\\u6548\\u5408\\u540c\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369921?fileid=3369921&ts=1584969055608&sign=4f4e91c34d0ed7118853b0bceade0875\",\"redirect_url\":\"https:\\/\\/qianyue-hr.colourlife.com\\/redirect\",\"auth_type\":\"3\",\"quantity\":\"0\"},{\"uuid\":\"e228084d96ce43228a320e063ffa26a1\",\"name\":\"\\u7ed1\\u5b9a\\u5f69\\u4e4b\\u4e91\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369935?fileid=3369935&ts=1584982648202&sign=c84c909909d9ddb066f3cab08cd83f75\",\"redirect_url\":\"https:\\/\\/evisit.colourlife.com\\/cgj\\/bindcustomer\\/bind_account.html\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"55b0e63dded74d92b4b91cca1c93d883\",\"name\":\"\\u5e02\\u573a\\u8fd0\\u8425\\u5e73\\u53f0\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369961?fileid=3369961&ts=1584986725000&sign=a9323494253c3945c8ebdb1b99a23196\",\"redirect_url\":\"https:\\/\\/market.colourlife.com\\/api\\/mobile\\/single_point\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"eb1a8685d5444d30844410d4b76970b5\",\"name\":\"\\u5ba1\\u62791.0\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3370001?fileid=3370001&ts=1584967696417&sign=3d2c2e864ce201396b5330bb5a6f4680\",\"redirect_url\":\"http:\\/\\/spsso.colourlife.net\\/login.aspx\",\"auth_type\":\"1\",\"quantity\":\"0\"},{\"uuid\":\"09d89007575a440388d7c3a1642ad52f\",\"name\":\"\\u7eff\\u5316\\uff08\\u673a\\u52a8\\uff09\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3370036?fileid=3370036&ts=1585031725695&sign=4bfc9128930486d672149f1b630549a1\",\"redirect_url\":\"http:\\/\\/caoquewang.colourlife.com\\/\",\"auth_type\":\"2\",\"quantity\":\"0\"},{\"uuid\":\"e8a2e2632d5345dba9486dcae98e20e5\",\"name\":\"\\u4e3b\\u4efb\\u5ba1\\u6838\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/4527259?fileid=4527259&ts=1584965866833&sign=977898a37ff203ed9a5f3c88b67592c8\",\"redirect_url\":\"https:\\/\\/zhuren-hro.colourlife.com\",\"auth_type\":\"4\",\"quantity\":\"0\"},{\"uuid\":\"19149b114721418187d0982d1dfddf35\",\"name\":\"\\u5f69\\u5bcc\\u6218\\u51b5\\u770b\\u677f\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/5557580?fileid=5557580&ts=1585043459819&sign=ddf4196b3576bf57180e51e64019f4ba\",\"redirect_url\":\"https:\\/\\/caifu.colourlife.com\\/kanban\\/#\\/index\",\"auth_type\":\"0\",\"quantity\":\"0\"},{\"uuid\":\"a8c1a407876d41b5a5bcb1081ffc47fc\",\"name\":\"\\u505c\\u8f66\\u7ef4\\u4fee\",\"state\":0,\"img_url\":\"https:\\/\\/micro-file.colourlife.com\\/v1\\/down\\/3369392?fileid=3369392&ts=1585034068614&sign=f9e3e345d6a12ea47c430b9d5cd3a17c\",\"redirect_url\":\"https:\\/\\/m.eshifu.cn\\/common\\/cgjOauth\",\"auth_type\":\"2\",\"quantity\":\"0\"}]}],\"id\":3}],\"contentEncrypt\":\"\"}";
    }

    interface DOWN {
        // 下载文件保存路径
        String DOWNLOAD_DIRECT = Environment.getExternalStorageDirectory()
                + "/colourlife/download/";
    }
}
