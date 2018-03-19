package com.youmai.hxsdk.keep;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.utils.DeviceUtils;

/**
 * Created by colin on 2017/11/8.
 */

public class ReqKeepDel {

    //公共类型
    private int uid;
    private String msisdn = "";  //登陆用户电话
    private String sid = "";   //登陆的sid
    private String termid = "";  //设备id
    private String sign = "";   //签名
    private String v = "";   //接口版本

    private String ids; //消息ID


    /**
     * @param context
     */
    public ReqKeepDel(Context context) {
        init(context);
    }

    private void init(Context context) {
        uid = HuxinSdkManager.instance().getUserId();
        msisdn = HuxinSdkManager.instance().getPhoneNum();  //phoneNum
        sid = HuxinSdkManager.instance().getSession();
        termid = DeviceUtils.getIMEI(context);  //IMEI
        v = AppConfig.V;
        sign = AppConfig.appSign(msisdn, termid);
    }


    public void setIds(String id) {
        if (TextUtils.isEmpty(ids)) {
            ids = id;
        } else {
            ids += "," + id;
        }
    }

    public ContentValues getParams() {
        ContentValues params = new ContentValues();

        params.put("uid", uid);
        params.put("msisdn", msisdn);
        params.put("sid", sid);
        params.put("termid", termid);
        params.put("v", v);
        params.put("sign", sign);
        params.put("ids", ids);

        return params;
    }

}
