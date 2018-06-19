package com.youmai.hxsdk.im.cache;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONObject;


public class CacheMsgRedPackage implements Parcelable, JsonFormat<CacheMsgRedPackage> {


    public static final String RED_PACKET_REVIEW = "查看利是";
    public static final String RED_PACKET_IS_OPEN_GROUP = "利是已被领完";
    public static final String RED_PACKET_IS_OPEN_SINGLE = "利是已领取";
    public static final String RED_PACKET_OVERDUE = "利是已过期";
    public static final String RED_PACKET_RECEIVE = "领取利是";


    private long msgId;
    private String value;
    private String redTitle;
    private String redStatus;

    public CacheMsgRedPackage() {
    }

    public String getValue() {
        return value;
    }

    public CacheMsgRedPackage setValue(String value) {
        this.value = value;
        return this;
    }

    public String getRedTitle() {
        return redTitle;
    }

    public CacheMsgRedPackage setRedTitle(String redTitle) {
        this.redTitle = redTitle;
        return this;
    }

    public long getMsgId() {
        return msgId;
    }

    public CacheMsgRedPackage setMsgId(long msgId) {
        this.msgId = msgId;
        return this;
    }

    public String getRedStatus() {
        return redStatus;
    }

    public CacheMsgRedPackage setRedStatus(String redStatus) {
        this.redStatus = redStatus;
        return this;
    }

    @Override
    public String toJson() {
        return GsonUtil.format(this);
    }

    @Override
    public CacheMsgRedPackage fromJson(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            value = jsonObject.optString("value");
            redTitle = jsonObject.optString("redTitle");
            msgId = jsonObject.optLong("msgId");
            redStatus = jsonObject.optString("redStatus");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.msgId);
        dest.writeString(this.value);
        dest.writeString(this.redTitle);
        dest.writeString(this.redStatus);
    }

    protected CacheMsgRedPackage(Parcel in) {
        this.msgId = in.readLong();
        this.value = in.readString();
        this.redTitle = in.readString();
        this.redStatus = in.readString();
    }

    public static final Creator<CacheMsgRedPackage> CREATOR = new Creator<CacheMsgRedPackage>() {
        @Override
        public CacheMsgRedPackage createFromParcel(Parcel source) {
            return new CacheMsgRedPackage(source);
        }

        @Override
        public CacheMsgRedPackage[] newArray(int size) {
            return new CacheMsgRedPackage[size];
        }
    };
}
