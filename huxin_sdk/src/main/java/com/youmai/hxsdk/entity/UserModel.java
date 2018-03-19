package com.youmai.hxsdk.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者：create by YW
 * 日期：2017.02.12 16:16
 * 描述：User信息对象
 */

public class UserModel implements Parcelable {

    public static final String NAME = "name";
    public static final String NUMBER = "number";
    public static final String SEX = "sex";
    public static final String COMPANY = "company";
    public static final String JOB = "job";
    public static final String EMAIL = "email";
    public static final String BIRTHDAY = "birthday";
    public static final String QQ = "qq";
    public static final String WEBSITE = "website";
    public static final String ADDRESS = "address";

    private String name;
    private String number;
    private String sex;
    private String company;
    private String job;
    private String email;
    private String birthday;
    private String qq;
    private String website;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getQQ() {
        return qq;
    }

    public void setQQ(String qq) {
        this.qq = qq;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.number);
        dest.writeString(this.sex);
        dest.writeString(this.company);
        dest.writeString(this.job);
        dest.writeString(this.email);
        dest.writeString(this.birthday);
        dest.writeString(this.qq);
        dest.writeString(this.website);
        dest.writeString(this.address);
    }

    public UserModel() {
    }

    protected UserModel(Parcel in) {
        this.name = in.readString();
        this.number = in.readString();
        this.sex = in.readString();
        this.company = in.readString();
        this.job = in.readString();
        this.email = in.readString();
        this.birthday = in.readString();
        this.qq = in.readString();
        this.website = in.readString();
        this.address = in.readString();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel source) {
            return new UserModel(source);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    @Override
    public String toString() {
        return "UserModel{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", sex='" + sex + '\'' +
                ", company='" + company + '\'' +
                ", job='" + job + '\'' +
                ", email='" + email + '\'' +
                ", birthday='" + birthday + '\'' +
                ", qq='" + qq + '\'' +
                ", website='" + website + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
