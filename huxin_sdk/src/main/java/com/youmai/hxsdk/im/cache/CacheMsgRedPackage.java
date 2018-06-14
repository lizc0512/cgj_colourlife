package com.youmai.hxsdk.im.cache;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONObject;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-12-06 14:59
 * Description: 地图
 */
public class CacheMsgRedPackage implements Parcelable, JsonFormat<CacheMsgRedPackage> {


    private String value;
    private String redTitle;

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
        dest.writeString(this.value);
        dest.writeString(this.redTitle);
    }

    protected CacheMsgRedPackage(Parcel in) {
        this.value = in.readString();
        this.redTitle = in.readString();
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
