package com.youmai.hxsdk.im.cache;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONObject;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-12-08 15:01
 * Description:
 */
public class CacheMsgShow implements Parcelable, JsonFormate<CacheMsgShow> {

    private String version;

    private String msisdn;

    private String mphone;

    private String fid;

    private String pfid;

    private String title;

    private String vtime;

    private String detailurl;

    private String type;

    private String name;

    private String file_type;

    public String getVersion() {
        return version;
    }

    public CacheMsgShow setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public CacheMsgShow setMsisdn(String msisdn) {
        this.msisdn = msisdn;
        return this;
    }

    public String getMphone() {
        return mphone;
    }

    public CacheMsgShow setMphone(String mphone) {
        this.mphone = mphone;
        return this;
    }

    public String getFid() {
        return fid;
    }

    public CacheMsgShow setFid(String fid) {
        this.fid = fid;
        return this;
    }

    public String getPfid() {
        return pfid;
    }

    public CacheMsgShow setPfid(String pfid) {
        this.pfid = pfid;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public CacheMsgShow setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getVtime() {
        return vtime;
    }

    public CacheMsgShow setVtime(String vtime) {
        this.vtime = vtime;
        return this;
    }

    public String getDetailurl() {
        return detailurl;
    }

    public CacheMsgShow setDetailurl(String detailurl) {
        this.detailurl = detailurl;
        return this;
    }

    public String getType() {
        return type;
    }

    public CacheMsgShow setType(String type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public CacheMsgShow setName(String name) {
        this.name = name;
        return this;
    }

    public String getFile_type() {
        return file_type;
    }

    public CacheMsgShow setFile_type(String file_type) {
        this.file_type = file_type;
        return this;
    }

    @Override
    public String toJson() {
        return GsonUtil.format(this);
    }

    @Override
    public CacheMsgShow fromJson(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            version = jsonObject.optString("version");
            msisdn = jsonObject.optString("msisdn");
            mphone = jsonObject.optString("mphone");
            fid = jsonObject.optString("fid");
            pfid = jsonObject.optString("pfid");
            title = jsonObject.optString("title");
            vtime = jsonObject.optString("vtime");
            detailurl = jsonObject.optString("detailurl");
            type = jsonObject.optString("type");
            name = jsonObject.optString("name");
            file_type = jsonObject.optString("file_type");
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
        dest.writeString(this.version);
        dest.writeString(this.msisdn);
        dest.writeString(this.mphone);
        dest.writeString(this.fid);
        dest.writeString(this.pfid);
        dest.writeString(this.title);
        dest.writeString(this.vtime);
        dest.writeString(this.detailurl);
        dest.writeString(this.type);
        dest.writeString(this.name);
        dest.writeString(this.file_type);
    }

    public CacheMsgShow() {
    }

    protected CacheMsgShow(Parcel in) {
        this.version = in.readString();
        this.msisdn = in.readString();
        this.mphone = in.readString();
        this.fid = in.readString();
        this.pfid = in.readString();
        this.title = in.readString();
        this.vtime = in.readString();
        this.detailurl = in.readString();
        this.type = in.readString();
        this.name = in.readString();
        this.file_type = in.readString();
    }

    public static final Parcelable.Creator<CacheMsgShow> CREATOR = new Parcelable.Creator<CacheMsgShow>() {
        @Override
        public CacheMsgShow createFromParcel(Parcel source) {
            return new CacheMsgShow(source);
        }

        @Override
        public CacheMsgShow[] newArray(int size) {
            return new CacheMsgShow[size];
        }
    };
}
