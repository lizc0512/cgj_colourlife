package com.youmai.hxsdk.im.cache;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONObject;

/**
 * Created by fylder on 2017/2/14.
 */

public class CacheMsgCall implements Parcelable, JsonFormate<CacheMsgCall> {

    private long duration;//时长
    private int type;//1：呼入   2：呼出  3：未接  4：挂断

    public long getDuration() {
        return duration;
    }

    public CacheMsgCall setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public int getType() {
        return type;
    }

    public CacheMsgCall setType(int type) {
        this.type = type;
        return this;
    }

    public CacheMsgCall() {
    }

    protected CacheMsgCall(Parcel in) {
        duration = in.readLong();
        type = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(duration);
        dest.writeInt(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CacheMsgCall> CREATOR = new Creator<CacheMsgCall>() {
        @Override
        public CacheMsgCall createFromParcel(Parcel in) {
            return new CacheMsgCall(in);
        }

        @Override
        public CacheMsgCall[] newArray(int size) {
            return new CacheMsgCall[size];
        }
    };

    @Override
    public String toJson() {
        return GsonUtil.format(this);
    }


    public CacheMsgCall fromJson(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            duration = jsonObject.optLong("duration");
            type = jsonObject.optInt("type");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
}
