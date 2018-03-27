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
public class CacheMsgMap implements Parcelable, JsonFormat<CacheMsgMap> {

    public String address;

    public String location;

    public String imgUrl;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.address);
        dest.writeString(this.location);
        dest.writeString(this.imgUrl);
    }

    public CacheMsgMap() {
    }

    protected CacheMsgMap(Parcel in) {
        this.address = in.readString();
        this.location = in.readString();
        this.imgUrl = in.readString();
    }

    public static final Parcelable.Creator<CacheMsgMap> CREATOR = new Parcelable.Creator<CacheMsgMap>() {
        @Override
        public CacheMsgMap createFromParcel(Parcel source) {
            return new CacheMsgMap(source);
        }

        @Override
        public CacheMsgMap[] newArray(int size) {
            return new CacheMsgMap[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public CacheMsgMap setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public CacheMsgMap setLocation(String location) {
        this.location = location;
        return this;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public CacheMsgMap setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
        return this;
    }

    @Override
    public String toJson() {
        return GsonUtil.format(this);
    }

    @Override
    public CacheMsgMap fromJson(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            address = jsonObject.optString("address");
            location = jsonObject.optString("location");
            imgUrl = jsonObject.optString("imgUrl");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public JsonFormat cloneProto(JsonFormat body) {
        CacheMsgMap cacheMsgMap = (CacheMsgMap) body;
        cacheMsgMap.setAddress(address)
                .setImgUrl(imgUrl)
                .setAddress(address)
                .setLocation(location);
        return cacheMsgMap;
    }
}
