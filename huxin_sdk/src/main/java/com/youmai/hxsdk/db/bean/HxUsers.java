package com.youmai.hxsdk.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 作者：create by YW
 * 日期：2017.02.21 15:59
 * 描述：呼信用户信息表
 */
@Entity
public class HxUsers implements Parcelable {

    @Id
    private Long id;

    private long userId;

    private String sex;

    private String nname;

    private String msisdn;

    private String iconUrl;

    private String type;

    private String version;

    private String showType;

    protected HxUsers(Parcel in) {
        userId = in.readLong();
        sex = in.readString();
        nname = in.readString();
        msisdn = in.readString();
        iconUrl = in.readString();
        type = in.readString();
        version = in.readString();
        showType = in.readString();
    }

    @Generated(hash = 1720671756)
    public HxUsers(Long id, long userId, String sex, String nname, String msisdn,
                   String iconUrl, String type, String version, String showType) {
        this.id = id;
        this.userId = userId;
        this.sex = sex;
        this.nname = nname;
        this.msisdn = msisdn;
        this.iconUrl = iconUrl;
        this.type = type;
        this.version = version;
        this.showType = showType;
    }

    @Generated(hash = 896779534)
    public HxUsers() {
    }

    public static final Creator<HxUsers> CREATOR = new Creator<HxUsers>() {
        @Override
        public HxUsers createFromParcel(Parcel in) {
            return new HxUsers(in);
        }

        @Override
        public HxUsers[] newArray(int size) {
            return new HxUsers[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNname() {
        return nname;
    }

    public void setNname(String nname) {
        this.nname = nname;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getIconUrl() {
        return "";
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }

    @Override
    public String toString() {
        return "HxUsers{" +
                "id=" + id +
                ", userId=" + userId +
                ", sex='" + sex + '\'' +
                ", nname='" + nname + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", type='" + type + '\'' +
                ", version='" + version + '\'' +
                ", showType='" + showType + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(userId);
        dest.writeString(sex);
        dest.writeString(nname);
        dest.writeString(msisdn);
        dest.writeString(iconUrl);
        dest.writeString(type);
        dest.writeString(version);
        dest.writeString(showType);
    }
}
