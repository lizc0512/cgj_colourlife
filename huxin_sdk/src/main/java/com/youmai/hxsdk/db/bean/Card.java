package com.youmai.hxsdk.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by fylder on 2017/3/27.
 */
@Entity
public class Card implements Parcelable {

    @Id
    private Long id;

    private long contactID;//关联通讯录标识

    private String info;//简介

    private String picture;//附图

    private String address;//位置描述

    private double latitude;//地图位置坐标:纬度

    private double longitude;//地图位置坐标:经度

    private String mapview;//地图缩略图

    private String time;

    private String data1;

    private String data2;//用于保存号码归属地

    private String data3;

    protected Card(Parcel in) {
        contactID = in.readLong();
        info = in.readString();
        picture = in.readString();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        mapview = in.readString();
        time = in.readString();
        data1 = in.readString();
        data2 = in.readString();
        data3 = in.readString();
    }

    @Generated(hash = 2113723609)
    public Card(Long id, long contactID, String info, String picture,
            String address, double latitude, double longitude, String mapview,
            String time, String data1, String data2, String data3) {
        this.id = id;
        this.contactID = contactID;
        this.info = info;
        this.picture = picture;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mapview = mapview;
        this.time = time;
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
    }

    @Generated(hash = 52700939)
    public Card() {
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(contactID);
        dest.writeString(info);
        dest.writeString(picture);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(mapview);
        dest.writeString(time);
        dest.writeString(data1);
        dest.writeString(data2);
        dest.writeString(data3);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getContactID() {
        return contactID;
    }

    public void setContactID(long contactID) {
        this.contactID = contactID;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMapview() {
        return mapview;
    }

    public void setMapview(String mapview) {
        this.mapview = mapview;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    public String getData3() {
        return data3;
    }

    public void setData3(String data3) {
        this.data3 = data3;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", contactID=" + contactID +
                ", info='" + info + '\'' +
                ", picture='" + picture + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", mapview='" + mapview + '\'' +
                ", time='" + time + '\'' +
                ", data1='" + data1 + '\'' +
                ", data2='" + data2 + '\'' +
                ", data3='" + data3 + '\'' +
                '}';
    }

}
