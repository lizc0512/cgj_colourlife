package com.youmai.hxsdk.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.utils.AppUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by colin on 2017/10/31.
 */
@Entity
public class PushMsg implements Parcelable {

    @Id
    private Long id;

    private int msg_id;
    private String title;   //消息标题    必填
    private String text;   //消息内容     必填
    private int msg_type;  // 0 系统公告  1 精选活动  2 玩转呼信  3 行业资讯   必填
    private String publish_date;   //发布日期 必填


    private int open_type;  // 0 打开APP , 1 打开app activity ,2 浏览器打开url ,3 APP webview打开 url ,4 打开APP dialog图片展示 ,5 打开APP dialog文字展示，6 详情展示

    private int act_type;   // 打开app页面类型

    private String url;        // h5页面地址


    private String h_img;   //  1 精选活动  2 玩转呼信  3 行业资讯  页面UI图片
    private String v_img;   // 打开APP dialog展示  页面图片
    private String btn_name;  //dialog 按钮名称

    private boolean is_popup;  //未消息是否弹框提示
    private boolean is_click;  //消息是否被点击


    private boolean is_token;  //打开H5页面时候 是否需要客户端拼接账号信息
    private String extraJson;      //扩展字段

    private long rec_time;   //消息接收时间   （本地接收时间）
    private boolean is_read;  //是否已读


    public PushMsg(JSONObject content) throws JSONException {
        this.msg_id = content.optInt("msg_id");
        this.title = content.optString("title");
        this.text = content.optString("text");
        this.msg_type = content.optInt("msg_type");
        this.publish_date = content.optString("publish_date");

        this.open_type = content.optInt("open_type");

        this.act_type = content.optInt("act_type");
        this.url = content.optString("url");
        this.h_img = content.optString("h_img");
        this.v_img = content.optString("v_img");
        this.btn_name = content.optString("btn_name");

        this.is_popup = content.optBoolean("is_popup");
        this.is_token = content.optBoolean("is_token");
        this.extraJson = content.optString("extraJson");

        this.rec_time = System.currentTimeMillis();
        //long test = System.currentTimeMillis() / 1000 - new Random().nextInt(48 * 60 * 60);  //for test
        //this.rec_time = test * 1000;
    }


    @Generated(hash = 868472971)
    public PushMsg() {
    }


    @Generated(hash = 1020047701)
    public PushMsg(Long id, int msg_id, String title, String text, int msg_type, String publish_date, int open_type, int act_type, String url,
                   String h_img, String v_img, String btn_name, boolean is_popup, boolean is_click, boolean is_token, String extraJson, long rec_time,
                   boolean is_read) {
        this.id = id;
        this.msg_id = msg_id;
        this.title = title;
        this.text = text;
        this.msg_type = msg_type;
        this.publish_date = publish_date;
        this.open_type = open_type;
        this.act_type = act_type;
        this.url = url;
        this.h_img = h_img;
        this.v_img = v_img;
        this.btn_name = btn_name;
        this.is_popup = is_popup;
        this.is_click = is_click;
        this.is_token = is_token;
        this.extraJson = extraJson;
        this.rec_time = rec_time;
        this.is_read = is_read;
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public int getMsg_id() {
        return this.msg_id;
    }


    public void setMsg_id(int msg_id) {
        this.msg_id = msg_id;
    }


    public String getTitle() {
        return this.title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getText() {
        return this.text;
    }


    public void setText(String text) {
        this.text = text;
    }


    public int getMsg_type() {
        return this.msg_type;
    }


    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }


    public String getPublish_date() {
        return this.publish_date;
    }


    public void setPublish_date(String publish_date) {
        this.publish_date = publish_date;
    }


    public int getOpen_type() {
        return this.open_type;
    }


    public void setOpen_type(int open_type) {
        this.open_type = open_type;
    }


    public int getAct_type() {
        return this.act_type;
    }


    public void setAct_type(int act_type) {
        this.act_type = act_type;
    }


    public String getUrl() {
        return this.url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public String getH_img() {
        return this.h_img;
    }


    public void setH_img(String h_img) {
        this.h_img = h_img;
    }


    public String getV_img() {
        return this.v_img;
    }


    public void setV_img(String v_img) {
        this.v_img = v_img;
    }


    public String getBtn_name() {
        return this.btn_name;
    }


    public void setBtn_name(String btn_name) {
        this.btn_name = btn_name;
    }


    public boolean getIs_popup() {
        return this.is_popup;
    }


    public void setIs_popup(boolean is_popup) {
        this.is_popup = is_popup;
    }


    public boolean getIs_click() {
        return this.is_click;
    }


    public void setIs_click(boolean is_click) {
        this.is_click = is_click;
    }


    public boolean getIs_token() {
        return this.is_token;
    }


    public void setIs_token(boolean is_token) {
        this.is_token = is_token;
    }


    public String getExtraJson() {
        return this.extraJson;
    }


    public void setExtraJson(String extraJson) {
        this.extraJson = extraJson;
    }


    public long getRec_time() {
        return this.rec_time;
    }


    public void setRec_time(long rec_time) {
        this.rec_time = rec_time;
    }


    public boolean getIs_read() {
        return this.is_read;
    }


    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeInt(this.msg_id);
        dest.writeString(this.title);
        dest.writeString(this.text);
        dest.writeInt(this.msg_type);
        dest.writeString(this.publish_date);
        dest.writeInt(this.open_type);
        dest.writeInt(this.act_type);
        dest.writeString(this.url);
        dest.writeString(this.h_img);
        dest.writeString(this.v_img);
        dest.writeString(this.btn_name);
        dest.writeByte(this.is_popup ? (byte) 1 : (byte) 0);
        dest.writeByte(this.is_click ? (byte) 1 : (byte) 0);
        dest.writeByte(this.is_token ? (byte) 1 : (byte) 0);
        dest.writeString(this.extraJson);
        dest.writeLong(this.rec_time);
        dest.writeByte(this.is_read ? (byte) 1 : (byte) 0);
    }

    protected PushMsg(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.msg_id = in.readInt();
        this.title = in.readString();
        this.text = in.readString();
        this.msg_type = in.readInt();
        this.publish_date = in.readString();
        this.open_type = in.readInt();
        this.act_type = in.readInt();
        this.url = in.readString();
        this.h_img = in.readString();
        this.v_img = in.readString();
        this.btn_name = in.readString();
        this.is_popup = in.readByte() != 0;
        this.is_click = in.readByte() != 0;
        this.is_token = in.readByte() != 0;
        this.extraJson = in.readString();
        this.rec_time = in.readLong();
        this.is_read = in.readByte() != 0;
    }

    public static final Parcelable.Creator<PushMsg> CREATOR = new Parcelable.Creator<PushMsg>() {
        @Override
        public PushMsg createFromParcel(Parcel source) {
            return new PushMsg(source);
        }

        @Override
        public PushMsg[] newArray(int size) {
            return new PushMsg[size];
        }
    };


    public String getActivity() {
        String res;
        if (act_type == 1) {  //在线充值
            res = "com.youmai.huxin.app.activity.purse.PurseRechargePayActivity";
        } else if (act_type == 2) {  //推广收益
            res = "com.youmai.hxsdk.activity.PurseDetailsActivity2";
        } else if (act_type == 3) {  //我要推广
            res = "com.youmai.hxsdk.activity.ShareInviteActivity1";
        } else if (act_type == 4) {  //公告详情页面
            res = "com.youmai.hxsdk.push.ui.PushMsgDetailActivity";
        } else if (act_type == 5) {  //系统消息页面
            res = "com.youmai.hxsdk.push.ui.PushMsgTypeActivity";
        } else if (act_type == 6) {  //通话秀
            res = "com.youmai.huxin.app.activity.tabmine.UserShowActivity";
        } else {
            res = "com.youmai.huxin.app.activity.MainAct";
        }
        return res;
    }


    public Map<String, String> getExtra() {
        Map<String, String> res = null;
        try {
            res = AppUtils.jsonToMap(new JSONObject(extraJson));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

}
