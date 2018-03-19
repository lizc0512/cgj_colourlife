package com.youmai.hxsdk.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.entity.UserShow;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by colin on 2016/8/5.
 */
@Entity
public class ShowData implements Parcelable {

    @Id
    private Long id;

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

    @Generated(hash = 784987431)
    public ShowData() {
    }

    @Generated(hash = 1751101500)
    public ShowData(Long id, String version, String msisdn, String mphone,
                    String fid, String pfid, String title, String vtime, String detailurl,
                    String type, String name, String file_type) {
        this.id = id;
        this.version = version;
        this.msisdn = msisdn;
        this.mphone = mphone;
        this.fid = fid;
        this.pfid = pfid;
        this.title = title;
        this.vtime = vtime;
        this.detailurl = detailurl;
        this.type = type;
        this.name = name;
        this.file_type = file_type;
    }

    public ShowData(UserShow.DBean.ShowBean showBean) {
        fid = showBean.getFid();
        pfid = showBean.getPfid();
        title = showBean.getTitle();
        vtime = showBean.getVtime();
        detailurl = showBean.getDetailurl();
        type = showBean.getType();
        name = showBean.getName();
        file_type = showBean.getFile_type();
    }

    public String getName() {
        if (mphone.equals(msisdn)) {
            return name;
        } else {
            return "";
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getMphone() {
        return mphone;
    }

    public void setMphone(String mphone) {
        this.mphone = mphone;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getPfid() {
        return pfid;
    }

    public void setPfid(String pfid) {
        this.pfid = pfid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVtime() {
        return vtime;
    }

    public void setVtime(String vtime) {
        this.vtime = vtime;
    }

    public String getDetailurl() {
        return detailurl;
    }

    public void setDetailurl(String detailurl) {
        this.detailurl = detailurl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    @Override
    public String toString() {
        return "ShowData{" +
                "id=" + id +
                ", version='" + version + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", mphone='" + mphone + '\'' +
                ", fid='" + fid + '\'' +
                ", pfid='" + pfid + '\'' +
                ", title='" + title + '\'' +
                ", vtime='" + vtime + '\'' +
                ", detailurl='" + detailurl + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", file_type='" + file_type + '\'' +
                '}';
    }

    protected ShowData(Parcel in) {
        version = in.readString();
        msisdn = in.readString();
        mphone = in.readString();
        fid = in.readString();
        pfid = in.readString();
        title = in.readString();
        vtime = in.readString();
        detailurl = in.readString();
        type = in.readString();
        name = in.readString();
        file_type = in.readString();
    }

    public static final Creator<ShowData> CREATOR = new Creator<ShowData>() {
        @Override
        public ShowData createFromParcel(Parcel in) {
            return new ShowData(in);
        }

        @Override
        public ShowData[] newArray(int size) {
            return new ShowData[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(version);
        dest.writeString(msisdn);
        dest.writeString(mphone);
        dest.writeString(fid);
        dest.writeString(pfid);
        dest.writeString(title);
        dest.writeString(vtime);
        dest.writeString(detailurl);
        dest.writeString(type);
        dest.writeString(name);
        dest.writeString(file_type);
    }
}
