package com.youmai.hxsdk.interfaces.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.db.bean.CacheMsgBean;

import java.io.File;

/**
 * 作者：create by YW
 * 日期：2016.12.26 17:50
 * 描述：
 */

public class FileBean implements Parcelable {

    /**
     * 公共类型
     */
    private int fileMsgType;
    private int userId;
    private String dstPhone;

    /**
     * 文件类型
     */
    private File file;
    private String fileName;
    private String fileLength;
    private String fileUrl;
    private int fileRes;
    private CacheMsgBean cacheMsgBean;
    private boolean isJumpFile;//文件打开不统计
    private String fileId;

    /**
     * 图片的原始路径
     *
     * @return
     */
    private String originPath;
    private String pictrueId;

    /**
     * 视频
     */
    private String localVideoPath;
    private String localFramePath;
    private long videoTime;
    private String videoFidUrl;
    private String videoPFidUrl;

    /**
     * 地图类型 userId, dstPhone, longitude, latitude, zoomLevel, address
     */
    private double longitude;
    private double latitude;
    private int zoomLevel;
    private String LocationAddress;
    private String mapUrl;

    /**
     * 文本类型
     *
     * @return
     */
    private String textContent;

    /**
     * 语音类型
     * audioDuration 时长
     *
     * @return
     */
    private String audioDuration;

    public FileBean() {
    }


    protected FileBean(Parcel in) {
        fileMsgType = in.readInt();
        userId = in.readInt();
        dstPhone = in.readString();
        fileName = in.readString();
        fileLength = in.readString();
        fileUrl = in.readString();
        fileRes = in.readInt();
        cacheMsgBean = in.readParcelable(CacheMsgBean.class.getClassLoader());
        isJumpFile = in.readByte() != 0;
        fileId = in.readString();
        originPath = in.readString();
        pictrueId = in.readString();
        localVideoPath = in.readString();
        localFramePath = in.readString();
        videoTime = in.readLong();
        videoFidUrl = in.readString();
        videoPFidUrl = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        zoomLevel = in.readInt();
        LocationAddress = in.readString();
        mapUrl = in.readString();
        textContent = in.readString();
        audioDuration = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(fileMsgType);
        dest.writeInt(userId);
        dest.writeString(dstPhone);
        dest.writeString(fileName);
        dest.writeString(fileLength);
        dest.writeString(fileUrl);
        dest.writeInt(fileRes);
        dest.writeParcelable(cacheMsgBean, flags);
        dest.writeByte((byte) (isJumpFile ? 1 : 0));
        dest.writeString(fileId);
        dest.writeString(originPath);
        dest.writeString(pictrueId);
        dest.writeString(localVideoPath);
        dest.writeString(localFramePath);
        dest.writeLong(videoTime);
        dest.writeString(videoFidUrl);
        dest.writeString(videoPFidUrl);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeInt(zoomLevel);
        dest.writeString(LocationAddress);
        dest.writeString(mapUrl);
        dest.writeString(textContent);
        dest.writeString(audioDuration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileBean> CREATOR = new Creator<FileBean>() {
        @Override
        public FileBean createFromParcel(Parcel in) {
            return new FileBean(in);
        }

        @Override
        public FileBean[] newArray(int size) {
            return new FileBean[size];
        }
    };

    public int getFileMsgType() {
        return fileMsgType;
    }

    public FileBean setFileMsgType(int fileMsgType) {
        this.fileMsgType = fileMsgType;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public FileBean setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public String getDstPhone() {
        return dstPhone;
    }

    public FileBean setDstPhone(String dstPhone) {
        this.dstPhone = dstPhone;
        return this;
    }

    public File getFile() {
        return file;
    }

    public FileBean setFile(File file) {
        this.file = file;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public FileBean setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getFileLength() {
        return fileLength;
    }

    public FileBean setFileLength(String fileLength) {
        this.fileLength = fileLength;
        return this;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public FileBean setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }

    public int getFileRes() {
        return fileRes;
    }

    public FileBean setFileRes(int fileRes) {
        this.fileRes = fileRes;
        return this;
    }

    public CacheMsgBean getCacheMsgBean() {
        return cacheMsgBean;
    }

    public FileBean setCacheMsgBean(CacheMsgBean cacheMsgBean) {
        this.cacheMsgBean = cacheMsgBean;
        return this;
    }

    public boolean isJumpFile() {
        return isJumpFile;
    }

    public FileBean setJumpFile(boolean jumpFile) {
        isJumpFile = jumpFile;
        return this;
    }

    public String getOriginPath() {
        return originPath;
    }

    public FileBean setOriginPath(String originPath) {
        this.originPath = originPath;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public FileBean setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public FileBean setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public String getAddress() {
        return LocationAddress;
    }

    public FileBean setAddress(String address) {
        this.LocationAddress = address;
        return this;
    }

    public int getZoomLevel() {
        return zoomLevel;
    }

    public FileBean setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
        return this;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public FileBean setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
        return this;
    }

    public String getTextContent() {
        return textContent;
    }

    public FileBean setTextContent(String textContent) {
        this.textContent = textContent;
        return this;
    }

    public String getAudioDuration() {
        return audioDuration;
    }

    public FileBean setAudioDuration(String audioDuration) {
        this.audioDuration = audioDuration;
        return this;
    }

    public String getFileId() {
        return fileId;
    }

    public FileBean setFileId(String fileId) {
        this.fileId = fileId;
        return this;
    }

    public String getPictrueId() {
        return pictrueId;
    }

    public FileBean setPictrueId(String pictrueId) {
        this.pictrueId = pictrueId;
        return this;
    }

    public long getVideoTime() {
        return videoTime;
    }

    public FileBean setVideoTime(long videoTime) {
        this.videoTime = videoTime;
        return this;
    }

    public String getLocalVideoPath() {
        return localVideoPath;
    }

    public FileBean setLocalVideoPath(String localVideoPath) {
        this.localVideoPath = localVideoPath;
        return this;
    }

    public String getLocalFramePath() {
        return localFramePath;
    }

    public FileBean setLocalFramePath(String localFramePath) {
        this.localFramePath = localFramePath;
        return this;
    }

    public String getVideoFidUrl() {
        return videoFidUrl;
    }

    public FileBean setVideoFidUrl(String videoFidUrl) {
        this.videoFidUrl = videoFidUrl;
        return this;
    }

    public String getVideoPFidUrl() {
        return videoPFidUrl;
    }

    public FileBean setVideoPFidUrl(String videoPFidUrl) {
        this.videoPFidUrl = videoPFidUrl;
        return this;
    }
}
