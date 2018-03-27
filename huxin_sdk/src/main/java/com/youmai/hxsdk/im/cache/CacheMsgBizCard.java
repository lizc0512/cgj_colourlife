package com.youmai.hxsdk.im.cache;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONObject;

/**
 * Created by lee on 2017/3/8.
 */

public class CacheMsgBizCard implements Parcelable, JsonFormat<CacheMsgBizCard> {
    public String name;
    public String phone;

    public String getName() {
        return name;
    }

    public CacheMsgBizCard setName(String name) {
        this.name = name;
        return this;
    }

    public CacheMsgBizCard() {
    }

    public String getPhone() {
        return phone;
    }

    public CacheMsgBizCard setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    protected CacheMsgBizCard(Parcel in) {
        name = in.readString();
        phone = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CacheMsgBizCard> CREATOR = new Creator<CacheMsgBizCard>() {
        @Override
        public CacheMsgBizCard createFromParcel(Parcel in) {
            return new CacheMsgBizCard(in);
        }

        @Override
        public CacheMsgBizCard[] newArray(int size) {
            return new CacheMsgBizCard[size];
        }
    };

    @Override
    public String toJson() {
        return GsonUtil.format(this);
    }

    @Override
    public CacheMsgBizCard fromJson(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            name = jsonObject.optString("name");
            phone = jsonObject.optString("phone");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public JsonFormat cloneProto(JsonFormat body) {
        CacheMsgBizCard cacheMsgBizCard = (CacheMsgBizCard) body;
        cacheMsgBizCard.setName(name);
        return cacheMsgBizCard;
    }
}
