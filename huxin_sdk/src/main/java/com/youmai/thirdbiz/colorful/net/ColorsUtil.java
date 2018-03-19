package com.youmai.thirdbiz.colorful.net;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.http.HttpConnector;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.utils.AppUtils;

import java.util.HashMap;
import java.util.List;


/**
 * Created by colin on 2016/11/11.
 */

public class ColorsUtil {

    /**
     * 获取稳定版appurl
     *
     * @param channel  渠道号 1 彩管家 2 彩之云
     * @param version  默认 1
     * @param listener 监听回调
     */
    public static void reqCgjAPPurl(String channel, String version, IPostListener listener) {

        String url = AppConfig.getShowHost() + "jhuxin-openapi/qy/openapp/1";

        Log.e("xx", "url=" + url);
        HashMap<String, String> params = new HashMap();

        params.put("v", version);

        ColorfulHttpConnector.doPost(url, params, listener);

    }


    /**
     * 请求业主信息.
     *
     * @param mobile
     * @param listener
     */
    public static void reqOwnerInfo(String mobile, IGetListener listener) {
        String url = ColorsConfig.COLOR_OWNER_HOST;
        long ts = System.currentTimeMillis() / 1000;

        ContentValues params = new ContentValues();
        params.put("mobile", mobile);
        params.put("ts", ts);
        params.put("appID", ColorsConfig.getAppID());
        params.put("sign", sign(ColorsConfig.getAppID(), ts));
        HttpConnector.httpGet(url, params, listener);
    }

    /**
     * 查询组织架构
     *
     * @param uuidList
     * @param listener
     */
    public static void reqOrgInfo(List<String> uuidList, IGetListener listener) {
        String url = ColorsConfig.COLOR_ORG_URL;
        long ts = System.currentTimeMillis() / 1000;

        String uuidStr = "";
        for (String uuidItem : uuidList) {
            if (TextUtils.isEmpty(uuidStr)) {
                uuidStr = uuidItem;
            } else {
                uuidStr += "," + uuidItem;
            }
        }

        Log.e("xx", "uuid=" + uuidStr);
        ContentValues params = new ContentValues();
        params.put("uuid", uuidStr);
        params.put("ts", ts);
        params.put("appID", ColorsConfig.getAppID());
        params.put("sign", sign(ColorsConfig.getAppID(), ts));
        HttpConnector.httpGet(url, params, listener);
    }

    /**
     * 添加业主信息.
     *
     * @param name
     * @param phone
     * @param
     * @param uuid
     * @param listener
     */
    public static void addOwnerInfo(String name, String phone, String IDCard, String uuid, IPostListener listener) {

        long ts = System.currentTimeMillis() / 1000;

        HashMap<String, String> params = new HashMap();

        params.put("owner_uuid", uuid);//"b2112339-2f11-4456-a332-8ba83c905f68" //房产UUID
        params.put("phone", phone);//联系人电话
        params.put("vdef2", name);//联系人
        params.put("personcardno", IDCard);//联系地址
        String userid = HuxinSdkManager.instance().getCgjUserId(); //"dageda";

        if (userid != null) {
            params.put("operator_username", userid);

        }

        params.put("ts", String.valueOf(ts));
        params.put("appID", ColorsConfig.getAppID());
        params.put("sign", sign(ColorsConfig.getAppID(), ts));

        ColorfulHttpConnector.doPut(ColorsConfig.COLOR_ADDOWNEAR_URL, params, listener);

    }

    /**
     * 获取省市区.
     *
     * @param listener
     */
    public static void reqRegions(IGetListener listener) {
        String url = ColorsConfig.COLOR_GET_REGIONS;
        long ts = System.currentTimeMillis() / 1000;

        ContentValues params = new ContentValues();
        params.put("type", "cgj");
        params.put("ts", ts);
        params.put("appID", ColorsConfig.getAppID());
        params.put("sign", sign(ColorsConfig.getAppID(), ts));
        HttpConnector.httpGet(url, params, listener);
    }


    /**
     * 获取小区.
     *
     * @param listener
     */
    public static void reqCommunity(String province, String city, String district, String provincecode, String citycode, String regioncode, IGetListener listener) {
        String url = ColorsConfig.COLOR_GET_COMMUNITY;
        long ts = System.currentTimeMillis() / 1000;

        ContentValues params = new ContentValues();

        params.put("province", province);
        params.put("city", city);
        params.put("region", district);
        params.put("provincecode", provincecode);
        params.put("citycode", citycode);
        params.put("regioncode", regioncode);
        params.put("appID", ColorsConfig.getAppID());
        params.put("sign", sign(ColorsConfig.getAppID(), ts));
        params.put("ts", ts);
        HttpConnector.httpGet(url, params, listener);
    }


    /**
     * 获取楼栋
     */

    public static void reBuilding(String communityId, IGetListener listener) {
        String url = ColorsConfig.COLOR_GET_BUILDING;
        long ts = System.currentTimeMillis() / 1000;
        Log.e("xx", "comoid=" + communityId);
        ContentValues params = new ContentValues();
        params.put("type", "cgj");
        params.put("smallarea_uuid", communityId);
        params.put("ts", ts);
        params.put("appID", ColorsConfig.getAppID());
        params.put("sign", sign(ColorsConfig.getAppID(), ts));
        HttpConnector.httpGet(url, params, listener);


    }

    /**
     * 获取单元信息
     */

    public static void reUnit(String BuildingId, IGetListener listener) {
        String url = ColorsConfig.COLOR_GET_UNITE;
        long ts = System.currentTimeMillis() / 1000;

        ContentValues params = new ContentValues();
        params.put("type", "cgj");
        params.put("housetype_uuid", BuildingId);
        params.put("ts", ts);
        params.put("appID", ColorsConfig.getAppID());
        params.put("sign", sign(ColorsConfig.getAppID(), ts));
        HttpConnector.httpGet(url, params, listener);


    }

    /**
     * 获取门牌号
     */

    public static void reHouseNumber(String unitId, IGetListener listener) {
        String url = ColorsConfig.COLOR_GET_HOUSE_NUMBER;
        long ts = System.currentTimeMillis() / 1000;

        ContentValues params = new ContentValues();
        params.put("type", "cgj");
        params.put("unit_uuid", unitId);
        params.put("ts", ts);
        params.put("appID", ColorsConfig.getAppID());
        params.put("sign", sign(ColorsConfig.getAppID(), ts));
        HttpConnector.httpGet(url, params, listener);


    }

    /**
     * 获取UUID
     */

    public static void reUuid(String house_uuid, IGetListener listener) {
        String url = ColorsConfig.COLOR_GET_UUID;
        long ts = System.currentTimeMillis() / 1000;
        ContentValues params = new ContentValues();
        params.put("house_uuid", house_uuid);
        params.put("ts", ts);
        params.put("appID", ColorsConfig.getAppID());
        params.put("sign", sign(ColorsConfig.getAppID(), ts));
        HttpConnector.httpGet(url, params, listener);


    }


    public static void addFeedbackInfo(String name, String phone, String content, String userId, IPostListener listener) {

        // String jsonStr = "{\"code\":0,\"message\":\"\\u67e5\\u8be2\\u6210\\u529f\",\"content\":{\"owner\":{\"borthdate\":\"\",\"concataddress\":\"\",\"concatperson\":\"\",\"czhouse_num\":\"0\",\"dmakedate\":\"2016-08-01\",\"dr\":\"0\",\"email\":\"\",\"gender\":\"1\",\"house_num\":\"1\",\"isapp\":\"N\",\"islock\":\"N\",\"isowner\":\"Y\",\"isverify\":\"N\",\"jg\":\"\",\"kzhouse_num\":\"0\",\"marrystate\":\"\",\"mz\":\"\\u6c49\",\"operator\":\"\\u5f20\\u5c0f\\u82f1\",\"operator_job\":\"\\u5ba2\\u6237\\u7ecf\\u7406(\\u7fe1\\u7fe0\\u661f\\u5149\\u56ed)\",\"operator_username\":\"zhxying\",\"owner_uuid\":\"01cda995-5118-47e6-b7e7-1a28c0a5a340\",\"ownertype\":\"001\",\"personcardno\":\"\",\"personcardtype\":\"\\u8eab\\u4efd\\u8bc1\",\"phone\":\"13602622461\",\"telephone\":\"\",\"ts\":\"2016-08-01 01:53:34\",\"vdef1\":\"\\u71d5\",\"vdef2\":\"\\u5229\\u82ac\",\"vname\":\"\\u71d5\\u5229\\u82ac\",\"zzhouse_num\":\"1\"},\"house\":[{\"constarea\":\"0.00\",\"dr\":\"0\",\"floor\":\"1\",\"glfdj\":\"1.00\",\"house_uuid\":\"bc81660f-91ce-4807-8b5c-fee71f92cbbb\",\"housetype_uuid\":\"516286f0-0376-11e6-8155-e247bfe54195\",\"housing_type\":\"0\",\"inarea\":\"85.94\",\"islock\":\"N\",\"moneyareanum\":\"85.94\",\"owner_uuid\":\"01cda995-5118-47e6-b7e7-1a28c0a5a340\",\"roomno\":\"002\",\"roomtype\":\"\\u4e8c\\u623f\\u4e00\\u5385\",\"salearea\":\"85.94\",\"smallarea_uuid\":\"2f6e0517-d5bd-4482-bc01-9c7556b1bc02\",\"state\":\"\\u6b63\\u5e38\",\"ts\":\"2016-08-01 01:53:34\",\"unit_uuid\":\"dafbbad0-8560-4981-b09f-1aad376ac329\",\"usestata\":\"\\u81ea\\u4f4f\\u6b63\\u5e38\\u4f7f\\u7528\",\"wyarea\":\"85.94\"}],\"bank\":[],\"car\":[],\"family\":[],\"history\":[]}}";
        String jsonStr = "[{\"userId\":\"" + userId + "\",\"userType\":\"cgj\"}]";

        Log.e("xx", "req=" + jsonStr);
/*
        String jsonString = userId;

        HashMap<String,String> map = new HashMap();
        map.put("userId",userId);
        map.put("userType","cji");
        */

        long ts = System.currentTimeMillis() / 1000;

        HashMap<String, String> params = new HashMap();

        params.put("caseTitle", "HuXinSDK反馈");
        params.put("caseDesc", content);
        params.put("caseTypeId", "moren");
        params.put("priority", "4");
        params.put("timePoint", "24");
        params.put("isOfferReward", "0");
        params.put("operatorId", userId);
        params.put("focusOnPeopleList", jsonStr);
        params.put("executorList", jsonStr);
        params.put("applicationId", "huxin");
        params.put("operatorType", "cgj");

//        params.put("phone", phone);//联系人电话
//        params.put("concatperson", name);//联系人
        params.put("ts", String.valueOf(ts));
        params.put("appID", ColorsConfig.getAppID());
        params.put("sign", sign(ColorsConfig.getAppID(), ts));
        ColorfulHttpConnector.doPost(ColorsConfig.COLOR_FEED_URL, params, listener);

    }

    private static String sign(String appId, long ts) {
        return AppUtils.md5(appId + ts + ColorsConfig.getToken() + false);
    }


    /**
     * 添加彩管家ICE平台字段
     *
     * @param param
     */
    public static void addColorSign(ContentValues param) {
        long ts = System.currentTimeMillis() / 1000;
        param.put("ts", String.valueOf(ts));
        param.put("appID", ColorsConfig.getAppID());
        param.put("sign", sign(ColorsConfig.getAppID(), ts));
    }

}
