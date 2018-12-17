package com.youmai.hxsdk.videocall;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.im.cache.JsonFormat;
import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONObject;

public class CacheMsgSingleVideo implements Parcelable, JsonFormat<CacheMsgSingleVideo> {
    public String content;
    public String time;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeString(this.time);
    }

    public CacheMsgSingleVideo() {
    }

    protected CacheMsgSingleVideo(Parcel in) {
        this.content = in.readString();
        this.time = in.readString();
    }

    public static final Creator<CacheMsgSingleVideo> CREATOR = new Creator<CacheMsgSingleVideo>() {
        @Override
        public CacheMsgSingleVideo createFromParcel(Parcel source) {
            return new CacheMsgSingleVideo(source);
        }

        @Override
        public CacheMsgSingleVideo[] newArray(int size) {
            return new CacheMsgSingleVideo[size];
        }
    };

    @Override
    public String toJson() {
        return GsonUtil.format(this);
    }

    @Override
    public CacheMsgSingleVideo fromJson(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            content = jsonObject.optString("content");
            time = jsonObject.optString("time");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }


}
