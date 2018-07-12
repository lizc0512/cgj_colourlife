package com.youmai.hxsdk.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

/**
 * Created by YW on 2018/4/11.
 */
@Entity
public class EmployeeBean implements Parcelable {
    @Id
    private Long id;  //主键ID

    private String uid;  //用户id - uuid
    private String mobile;    //联系人号码
    private String realname; //姓名 - 联系人名
    private String avatar; //头像url
    private String sex;
    private String email;
    private String isFavorite; // 1或者2   1已经收藏 ，2未收藏
    private String jobName;
    private String landline;
    private String orgID;
    private String orgName;
    private String username; //拼音的姓 与 名的首字母
    private String enterprise_cornet;

    @Transient
    private String qq;

    @Transient
    private String Favoriteid;

    @Transient
    private String name;

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(String isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public String getOrgID() {
        return orgID;
    }

    public void setOrgID(String orgID) {
        this.orgID = orgID;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFavoriteid() {
        return Favoriteid;
    }

    public void setFavoriteid(String favoriteid) {
        Favoriteid = favoriteid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnterprise_cornet() {
        return this.enterprise_cornet;
    }

    public void setEnterprise_cornet(String enterprise_cornet) {
        this.enterprise_cornet = enterprise_cornet;
    }

    @Generated(hash = 1539865870)
    public EmployeeBean(Long id, String uid, String mobile, String realname, String avatar, String sex,
                        String email, String isFavorite, String jobName, String landline, String orgID, String orgName,
                        String username, String enterprise_cornet) {
        this.id = id;
        this.uid = uid;
        this.mobile = mobile;
        this.realname = realname;
        this.avatar = avatar;
        this.sex = sex;
        this.email = email;
        this.isFavorite = isFavorite;
        this.jobName = jobName;
        this.landline = landline;
        this.orgID = orgID;
        this.orgName = orgName;
        this.username = username;
        this.enterprise_cornet = enterprise_cornet;
    }

    @Generated(hash = 1600622815)
    public EmployeeBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.uid);
        dest.writeString(this.mobile);
        dest.writeString(this.realname);
        dest.writeString(this.avatar);
        dest.writeString(this.sex);
        dest.writeString(this.email);
        dest.writeString(this.isFavorite);
        dest.writeString(this.jobName);
        dest.writeString(this.landline);
        dest.writeString(this.orgID);
        dest.writeString(this.orgName);
        dest.writeString(this.username);
        dest.writeString(this.enterprise_cornet);
        dest.writeString(this.qq);
        dest.writeString(this.Favoriteid);
        dest.writeString(this.name);
    }

    protected EmployeeBean(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.uid = in.readString();
        this.mobile = in.readString();
        this.realname = in.readString();
        this.avatar = in.readString();
        this.sex = in.readString();
        this.email = in.readString();
        this.isFavorite = in.readString();
        this.jobName = in.readString();
        this.landline = in.readString();
        this.orgID = in.readString();
        this.orgName = in.readString();
        this.username = in.readString();
        this.enterprise_cornet = in.readString();
        this.qq = in.readString();
        this.Favoriteid = in.readString();
        this.name = in.readString();
    }

    public static final Creator<EmployeeBean> CREATOR = new Creator<EmployeeBean>() {
        @Override
        public EmployeeBean createFromParcel(Parcel source) {
            return new EmployeeBean(source);
        }

        @Override
        public EmployeeBean[] newArray(int size) {
            return new EmployeeBean[size];
        }
    };
}
