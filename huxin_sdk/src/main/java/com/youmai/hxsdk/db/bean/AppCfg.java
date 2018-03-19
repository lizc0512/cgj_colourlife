package com.youmai.hxsdk.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by colin on 2017/2/21.
 */
@Entity
public class AppCfg {

    @Id
    private Long table_id;

    private int id;

    private String fileurl;

    private String filename;

    private String fversion;

    private String mark;

    private String update_version;

    private String channel;

    private String version;

    @Generated(hash = 1498948481)
    public AppCfg(Long table_id, int id, String fileurl, String filename,
            String fversion, String mark, String update_version, String channel,
            String version) {
        this.table_id = table_id;
        this.id = id;
        this.fileurl = fileurl;
        this.filename = filename;
        this.fversion = fversion;
        this.mark = mark;
        this.update_version = update_version;
        this.channel = channel;
        this.version = version;
    }

    @Generated(hash = 1847908250)
    public AppCfg() {
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

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFversion() {
        return fversion;
    }

    public void setFversion(String fversion) {
        this.fversion = fversion;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getUpdate_version() {
        return update_version;
    }

    public void setUpdate_version(String update_version) {
        this.update_version = update_version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "AppCfg{" +
                "table_id=" + table_id +
                ", id=" + id +
                ", fileurl='" + fileurl + '\'' +
                ", filename='" + filename + '\'' +
                ", fversion='" + fversion + '\'' +
                ", mark='" + mark + '\'' +
                ", update_version='" + update_version + '\'' +
                ", channel='" + channel + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
