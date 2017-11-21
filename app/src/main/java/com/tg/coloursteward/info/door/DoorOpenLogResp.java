package com.tg.coloursteward.info.door;

import java.io.Serializable;

/**
 * Created by chengyun on 2016/6/24.
 */
public class DoorOpenLogResp implements Serializable {

    private String id;//记录编号
    private String creationtime;//开门时间，时间戳
    private String result;//结果，0成功，其它失败
    private String doorid;//门禁编号
    private String qrcode;//门禁二维码
    private String name;//门禁名称

    private String conntype;//链接类型

    public String getConntype() {
        return conntype;
    }

    public void setConntype(String conntype) {
        this.conntype = conntype;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreationtime() {
        return creationtime;
    }

    public void setCreationtime(String creationtime) {
        this.creationtime = creationtime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDoorid() {
        return doorid;
    }

    public void setDoorid(String doorid) {
        this.doorid = doorid;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
