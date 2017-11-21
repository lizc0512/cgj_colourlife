package com.tg.coloursteward.info.door;

import java.io.Serializable;

/**
 * Created by chengyun on 2016/6/24.
 */
public class DoorFixedResp implements Serializable {

    private String doorid;//门禁编号
    private String doortype;//门禁二维码
    private String qrcode;//门禁类型
    private String conntype;//连接类型
    private String name;//门禁名称
    private String position;//门禁位置
    private String type;//小区类型，1住宅，2写字楼

    public String getDoorid() {
        return doorid;
    }

    public void setDoorid(String doorid) {
        this.doorid = doorid;
    }

    public String getDoortype() {
        return doortype;
    }

    public void setDoortype(String doortype) {
        this.doortype = doortype;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getConntype() {
        return conntype;
    }

    public void setConntype(String conntype) {
        this.conntype = conntype;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
