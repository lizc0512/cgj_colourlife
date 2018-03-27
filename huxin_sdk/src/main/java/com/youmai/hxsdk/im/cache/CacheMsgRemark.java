package com.youmai.hxsdk.im.cache;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.utils.GsonUtil;

/**
 * 主题备注消息
 * Created by fylder on 2017/4/18.
 */

public class CacheMsgRemark implements Parcelable, JsonFormate<CacheMsgRemark> {

    public static int TYPE_UPDATE = 0;
    public static int TYPE_PREVIEW = 1;
    public static int TYPE_MERGE = 2;
    public static int TYPE_REPLACE = 3;

    private String theme;
    private String remark;
    private long timestamp;

    private int type;//默认0:可进行合并或替换等操作  1:查看详情

    public CacheMsgRemark() {
    }

    protected CacheMsgRemark(Parcel in) {
        theme = in.readString();
        remark = in.readString();
        timestamp = in.readLong();
        type = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(theme);
        dest.writeString(remark);
        dest.writeLong(timestamp);
        dest.writeInt(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CacheMsgRemark> CREATOR = new Creator<CacheMsgRemark>() {
        @Override
        public CacheMsgRemark createFromParcel(Parcel in) {
            return new CacheMsgRemark(in);
        }

        @Override
        public CacheMsgRemark[] newArray(int size) {
            return new CacheMsgRemark[size];
        }
    };

    @Override
    public String toJson() {
        return GsonUtil.format(this);
    }

    @Override
    public CacheMsgRemark fromJson(String jsonStr) {
        return GsonUtil.parse(jsonStr, CacheMsgRemark.class);
    }


    public String getTheme() {
        return theme;
    }

    public CacheMsgRemark setTheme(String theme) {
        this.theme = theme;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public CacheMsgRemark setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public CacheMsgRemark setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public int getType() {
        return type;
    }

    public CacheMsgRemark setType(int type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        return "CacheMsgRemark{" +
                "theme='" + theme + '\'' +
                ", remark='" + remark + '\'' +
                ", timestamp=" + timestamp +
                ", type=" + type +
                '}';
    }

    @Override
    public JsonFormate cloneProto(JsonFormate body) {
        CacheMsgRemark cacheMsgRemark = (CacheMsgRemark) body;
        cacheMsgRemark.setRemark(remark)
                .setTheme(theme)
                .setTimestamp(timestamp)
                .setType(type);
        return cacheMsgRemark;
    }
}
