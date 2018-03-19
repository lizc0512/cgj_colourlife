package com.youmai.hxsdk.im.cache;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 作者：create by YW
 * 日期：2017.12.11 10:10
 * 描述：位置共享
 */
public class CacheMsgLShare implements Parcelable, JsonFormate<CacheMsgLShare> {

    private long targetId;
    private int receiveUserId;
    private String longitude; //位置的经度
    private String latitude; //位置的纬度
    private String receivePhone; //对方的号码
    private String receiveLocation; //对方共享的位置 location1, location2, ...
    private boolean answerOrReject; //对方接收或拒绝 true
    private boolean isEndOver; //位置结束
    private int status; //0: 邀请 1：

    public CacheMsgLShare() {

    }


    protected CacheMsgLShare(Parcel in) {
        targetId = in.readLong();
        receiveUserId = in.readInt();
        longitude = in.readString();
        latitude = in.readString();
        receivePhone = in.readString();
        receiveLocation = in.readString();
        answerOrReject = in.readByte() != 0;
        isEndOver = in.readByte() != 0;
    }

    public static final Creator<CacheMsgLShare> CREATOR = new Creator<CacheMsgLShare>() {
        @Override
        public CacheMsgLShare createFromParcel(Parcel in) {
            return new CacheMsgLShare(in);
        }

        @Override
        public CacheMsgLShare[] newArray(int size) {
            return new CacheMsgLShare[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(targetId);
        dest.writeInt(receiveUserId);
        dest.writeString(longitude);
        dest.writeString(latitude);
        dest.writeString(receivePhone);
        dest.writeString(receiveLocation);
        dest.writeByte((byte) (answerOrReject ? 1 : 0));
        dest.writeByte((byte) (isEndOver ? 1 : 0));
    }

    @Override
    public String toJson() {
        return GsonUtil.format(this);
    }

    @Override
    public CacheMsgLShare fromJson(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            targetId = jsonObject.optLong("targetId");
            receiveUserId = jsonObject.optInt("receiveUserId");
            longitude = jsonObject.optString("longitude");
            latitude = jsonObject.optString("latitude");
            receivePhone = jsonObject.optString("receivePhone");
            receiveLocation = jsonObject.optString("receiveLocation");
            answerOrReject = jsonObject.optBoolean("answerOrReject");
            isEndOver = jsonObject.optBoolean("isEndOver");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public long getTargetId() {
        return targetId;
    }

    public CacheMsgLShare setTargetId(long targetId) {
        this.targetId = targetId;
        return this;
    }

    public int getReceiveUserId() {
        return receiveUserId;
    }

    public CacheMsgLShare setReceiveUserId(int receiveUserId) {
        this.receiveUserId = receiveUserId;
        return this;
    }

    public String getLongitude() {
        return longitude;
    }

    public CacheMsgLShare setLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getLatitude() {
        return latitude;
    }

    public CacheMsgLShare setLatitude(String latitude) {
        this.latitude = latitude;
        return this;
    }

    public String getReceivePhone() {
        return receivePhone;
    }

    public CacheMsgLShare setReceivePhone(String receivePhone) {
        this.receivePhone = receivePhone;
        return this;
    }

    public String getReceiveLocation() {
        return receiveLocation;
    }

    public CacheMsgLShare setReceiveLocation(String receiveLocation) {
        this.receiveLocation = receiveLocation;
        return this;
    }

    public boolean isAnswerOrReject() {
        return answerOrReject;
    }

    public CacheMsgLShare setAnswerOrReject(boolean answerOrReject) {
        this.answerOrReject = answerOrReject;
        return this;
    }

    public boolean isEndOver() {
        return isEndOver;
    }

    public CacheMsgLShare setEndOver(boolean endOver) {
        isEndOver = endOver;
        return this;
    }
}
