package com.youmai.hxsdk.keep;

import android.content.ContentValues;
import android.content.Context;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.utils.DeviceUtils;

/**
 * Created by colin on 2017/11/8.
 */

public class ReqRemindBean {

    //公共类型
    private int uid;
    private String msisdn = "";  //登陆用户电话
    private String sid = "";   //登陆的sid
    private String termid = "";  //设备id
    private String sign = "";   //签名
    private String v = "";   //接口版本

    private long msgId; //消息ID
    private String sendPhone = "";    //消息发送方手机号
    private String receivePhone = "";    //消息接收方手机号
    private int msgType;

    private String startTime = "";    //手机型号
    private String endTime = "";    //手机型号

    private int page;
    private int rows;


    /**
     * @param context
     */
    public ReqRemindBean(Context context) {
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


    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public void setSendPhone(String sendPhone) {
        this.sendPhone = sendPhone;
    }

    public void setReceivePhone(String receivePhone) {
        this.receivePhone = receivePhone;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public ContentValues getParams() {
        ContentValues params = new ContentValues();

        params.put("uid", uid);
        params.put("msisdn", msisdn);
        params.put("sid", sid);
        params.put("termid", termid);
        params.put("v", v);
        params.put("sign", sign);
        params.put("page", page);
        //params.put("rows", 100);

        return params;
    }

}
