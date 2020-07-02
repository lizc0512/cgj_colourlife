package com.example.commonlibrary;

/**
 * @name
 * @class name：com.example.commonlibrary
 * @class describe
 * @anthor QQ:510906433
 * @time 2020/6/10 16:50
 * @change
 * @chang time
 * @class describe
 */
public final class ConstantComm {

    public static final boolean EXTERNAL_RELEASE = false;//true,为正式环境，false为测试环境

    public static String environmentUrl = "";
    public static String URL_ICETEST = "";//
    public static String URL_OAUTH2 = "";
    public static String SINGLE_DEVICE = "";
    public static String URL_NEW = "";
    public static String URL_ICESTAFF = "";
    public static String CLIENT_SECRET = "";
    public static String URL_QRCODE = "";
    public static String URL_IMPUSH = "";
    public static String VERSION_ADDRESS = "";
    public static String URL_H5OAUTH = "";
    public static String URL_LEKAI = "";
    public static String ACCOUNT_ADDRESS = "";//新版彩钱包
    public static String DELIVERY_HOME_ADDRESS = "";
    public static String DELIVERY_NUMBER_ADDRESS = ""; //快单号
    public static String DELIVERY_COMPANY_ADDRESS = "";//快递公司
    public static String DELIVERY_ADDRESS_URL = "";//快递地址url
    public static String environment = "";
    public static String cqj_appid = "";
    public static String TOKEN_ADDRESS = "";
    public static String publicKeyString = "";
    public static int SAVENOHTTPRECORD = 0;

    public static String introduce = "";
    public static String privacy = "";
    public static String agreement = "";

    public ConstantComm() {
        releaseUrl();
    }

    public static void releaseUrl() {

        if (EXTERNAL_RELEASE) {  //正式环境

            environmentUrl = "2";

            URL_ICETEST = "https://openapi.colourlife.com/v1";//
            URL_OAUTH2 = "https://oauth2-cgj.colourlife.com";
            SINGLE_DEVICE = "https://single.colourlife.com";
            URL_NEW = "https://cgj-backyard.colourlife.com";
            URL_ICESTAFF = "https://staff-ice.colourlife.com";
            CLIENT_SECRET = "t2o0a1xl2lOmoPi4tuHf5uw4VZloXGs7y1Kd0Yoq";
            URL_QRCODE = "https://qrcode.colourlife.com";
            URL_IMPUSH = "https://impush-cgj.colourlife.com";
            VERSION_ADDRESS = "https://version.colourlife.com";
            URL_H5OAUTH = "https://oauth-czy.colourlife.com";
            URL_LEKAI = "https://lekaiadminapi-door.colourlife.com";
            ACCOUNT_ADDRESS = "https://account-finance.colourlife.com/";//新版彩钱包
            DELIVERY_HOME_ADDRESS = "https://gexpressbackend-czy.colourlife.com";
            DELIVERY_NUMBER_ADDRESS = "https://kdbackend-czy.colourlife.com"; //快单号
            DELIVERY_COMPANY_ADDRESS = "https://gexpressbackend-czy.colourlife.com";//快递公司
            DELIVERY_ADDRESS_URL = "https://gexpress-czytest.colourlife.com/new_express/#/pages/address/address";//快递地址url
            environment = "release";
            cqj_appid = "327494513335603200";
            TOKEN_ADDRESS = "https://oauth2czy.colourlife.com";
            publicKeyString = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTFnAR7ORLx0jGzf9Ux1We7yHvRi+kQXKSRmtgBjDCXQzakGm2mrb6EupCkDbUcj4BUs7S7zm/rICQuVNC9fujeJGj"
                    + "cNWRg0XWVtm90XpbTqfKiXzGDHI9W8aULYZ3of/JJ9lyCyjqjigyCdLBPtQ27gOu"
                    + "boDzQuieR2ywPHawzQIDAQAB";
            SAVENOHTTPRECORD = 0;
            introduce = "http://mapp.colourlife.com/introduce/introduce.html";
            privacy = "http://mapp.colourlife.com/xieyi/yinsi.html";
            agreement = "http://mapp.colourlife.com/xieyi/fuwuxieyi.html";
        } else {  //测试环境

            environmentUrl = "1";


            URL_ICETEST = "https://openapi-test.colourlife.com/v1";//
            URL_OAUTH2 = "https://oauth2-cgj-test.colourlife.com";
            SINGLE_DEVICE = "https://single-czytest.colourlife.com";
            URL_NEW = "https://cgj-backyard-test.colourlife.com";
            URL_ICESTAFF = "http://staff.ice.test.colourlife.com";
            CLIENT_SECRET = "xlsfrQS5R49upmfZbhlsrUzAt9HDA5K4ptLYsqK5";
            URL_QRCODE = "http://qrcode-czytest.colourlife.com";
            URL_IMPUSH = "https://impush-cgj-test.colourlife.com";
            VERSION_ADDRESS = "https://version-czytest.colourlife.com";
            URL_H5OAUTH = "https://oauth-czytest.colourlife.com";
            URL_LEKAI = "https://lekaiadminapi-doortest.colourlife.com";
            ACCOUNT_ADDRESS = "https://account-finance-test.colourlife.com";//新版彩钱包
            DELIVERY_HOME_ADDRESS = "https://gexpressbackend-czytest.colourlife.com";
            DELIVERY_NUMBER_ADDRESS = "https://kdbackend-czytest.colourlife.com"; //快单号
            DELIVERY_COMPANY_ADDRESS = "https://gexpressbackend-czytest.colourlife.com";//快递公司
            DELIVERY_ADDRESS_URL = "https://gexpress-czytest.colourlife.com/new_express/#/pages/address/address";//快递地址url
            environment = "debug";
            cqj_appid = "323521861252157440";
            TOKEN_ADDRESS = "http://oauth2-czytest.colourlife.com";
            publicKeyString = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDZDqnWph9LxtD0zgtGAYT" +
                    "Tf2jYeV+ni5F1o0w3Fag4OOD1YHCRUCXIsFy+iJYmuPf5vMkZrkoiJmKBfkaIzNlrJZzHzq+LsPQNCF86p1nLsuHbkWNvy" +
                    "jOEPn/CUryP2Kxme4S+eEqLIeNwp70VOaMuPmRoEZxMDAgvc6Z0DWsVdQIDAQAB";
            SAVENOHTTPRECORD = 1;
            introduce = "http://mapp-czytest.colourlife.com/introduce/introduce.html";
            privacy = "http://mapp-czytest.colourlife.com/xieyi/yinsi.html";
            agreement = "http://mapp-czytest.colourlife.com/xieyi/fuwuxieyi.html";

        }


    }

}
