package com.youmai.hxsdk.data;

import android.os.Parcel;
import android.os.Parcelable;

public class VedioSetting implements Parcelable {

    private String userId;
    private boolean openCamera;
    private boolean openVoice;
    private String roomName;
    private String adminId;
    private String nickName;
    private boolean isAgree;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isOpenCamera() {
        return openCamera;
    }

    public void setOpenCamera(boolean openCamera) {
        this.openCamera = openCamera;
    }

    public boolean isOpenVoice() {
        return openVoice;
    }

    public void setOpenVoice(boolean openVoice) {
        this.openVoice = openVoice;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isAgree() {
        return isAgree;
    }

    public void setAgree(boolean agree) {
        isAgree = agree;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeByte(this.openCamera ? (byte) 1 : (byte) 0);
        dest.writeByte(this.openVoice ? (byte) 1 : (byte) 0);
        dest.writeString(this.roomName);
        dest.writeString(this.adminId);
        dest.writeString(this.nickName);
        dest.writeByte(this.isAgree ? (byte) 1 : (byte) 0);
    }

    public VedioSetting() {
    }

    protected VedioSetting(Parcel in) {
        this.userId = in.readString();
        this.openCamera = in.readByte() != 0;
        this.openVoice = in.readByte() != 0;
        this.roomName = in.readString();
        this.adminId = in.readString();
        this.nickName = in.readString();
        this.isAgree = in.readByte() != 0;
    }

    public static final Creator<VedioSetting> CREATOR = new Creator<VedioSetting>() {
        @Override
        public VedioSetting createFromParcel(Parcel source) {
            return new VedioSetting(source);
        }

        @Override
        public VedioSetting[] newArray(int size) {
            return new VedioSetting[size];
        }
    };
}
