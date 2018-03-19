package com.youmai.hxsdk.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by colin on 2017/2/21.
 */
@Entity
public class StatsData implements Parcelable {

    @Id
    private Long table_id;

    private int id;

    private long time;

    private String url;

    private int isSend;        //0 表示没有发送 ， 1表示已经发送

    private int eventResult; //   1.成功  2.失败

    private int refDataId;

    private String customContent; //自定义内容

    @Generated(hash = 414774200)
    public StatsData(Long table_id, int id, long time, String url, int isSend, int eventResult,
                     int refDataId, String customContent) {
        this.table_id = table_id;
        this.id = id;
        this.time = time;
        this.url = url;
        this.isSend = isSend;
        this.eventResult = eventResult;
        this.refDataId = refDataId;
        this.customContent = customContent;
    }

    @Generated(hash = 579880628)
    public StatsData() {
    }

    public StatsData(int id, String customContent) {
        this.id = id;
        eventResult = 1;
        refDataId = 0;
        this.customContent = customContent;
    }

    public StatsData(int id, int eventResult, int refDataId, String customContent) {
        this.id = id;
        this.eventResult = eventResult;
        this.refDataId = refDataId;
        this.customContent = customContent;
    }

    public Long getTable_id() {
        return table_id;
    }

    public void setTable_id(Long table_id) {
        this.table_id = table_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsSend() {
        return isSend;
    }

    public void setIsSend(int isSend) {
        this.isSend = isSend;
    }

    public int getEventResult() {
        return eventResult;
    }

    public void setEventResult(int eventResult) {
        this.eventResult = eventResult;
    }

    public int getRefDataId() {
        return refDataId;
    }

    public void setRefDataId(int refDataId) {
        this.refDataId = refDataId;
    }

    public String getCustomContent() {
        return customContent;
    }

    public void setCustomContent(String customContent) {
        this.customContent = customContent;
    }

    @Override
    public String toString() {
        return "StatsData{" +
                "table_id=" + table_id +
                ", id=" + id +
                ", time=" + time +
                ", url='" + url + '\'' +
                ", isSend=" + isSend +
                ", eventResult=" + eventResult +
                ", refDataId=" + refDataId +
                ", customContent='" + customContent + '\'' +
                '}';
    }

    protected StatsData(Parcel in) {
        id = in.readInt();
        time = in.readLong();
        url = in.readString();
        isSend = in.readInt();
        eventResult = in.readInt();
        refDataId = in.readInt();
        customContent = in.readString();
    }

    public static final Creator<StatsData> CREATOR = new Creator<StatsData>() {
        @Override
        public StatsData createFromParcel(Parcel in) {
            return new StatsData(in);
        }

        @Override
        public StatsData[] newArray(int size) {
            return new StatsData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(time);
        dest.writeString(url);
        dest.writeInt(isSend);
        dest.writeInt(eventResult);
        dest.writeInt(refDataId);
        dest.writeString(customContent);
    }
}
