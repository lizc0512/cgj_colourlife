package com.youmai.hxsdk.im.cache;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONObject;

/**
 * Author:  YW
 * Date:    2017-11-28 16:27
 * Description: 段子
 */
public class CacheMsgJoke implements Parcelable, JsonFormate<CacheMsgJoke> {

    public static final String JOKES = "/hx_im_jokes";

    private String msgJoke;
    private String voiceId;//服务端文件序列
    private String voicePath;//本地语音路径

    public CacheMsgJoke() {

    }

    public String getMsgJoke() {
        return msgJoke;
    }

    public CacheMsgJoke setMsgJoke(String msgJoke) {
        this.msgJoke = msgJoke;
        return this;
    }

    public String getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(String voiceId) {
        this.voiceId = voiceId;
    }

    public String getVoicePath() {
        return voicePath;
    }

    public void setVoicePath(String voicePath) {
        this.voicePath = voicePath;
    }

    @Override
    public String toJson() {
        return GsonUtil.format(this);
    }

    @Override
    public CacheMsgJoke fromJson(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            msgJoke = jsonObject.optString("msgJoke");
            voiceId = jsonObject.optString("voiceId");
            voicePath = jsonObject.optString("voicePath");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    protected CacheMsgJoke(Parcel in) {
        msgJoke = in.readString();
        voiceId = in.readString();
        voicePath = in.readString();
    }

    public static final Creator<CacheMsgJoke> CREATOR = new Creator<CacheMsgJoke>() {
        @Override
        public CacheMsgJoke createFromParcel(Parcel in) {
            return new CacheMsgJoke(in);
        }

        @Override
        public CacheMsgJoke[] newArray(int size) {
            return new CacheMsgJoke[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(msgJoke);
        dest.writeString(voiceId);
        dest.writeString(voicePath);
    }
}
