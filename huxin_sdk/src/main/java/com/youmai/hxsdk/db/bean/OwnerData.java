package com.youmai.hxsdk.db.bean;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by ym on 2017/1/5.
 */

@Entity
public class OwnerData  {

    @Id
    private Long id;

    private String msisdn;

    private String vname;

    private String smallarea_name;

    private String housetype_name;

    private String unit_name;

    private String roomno;

    private int bool_ower;



    @Generated(hash = 614189485)
    public OwnerData(Long id, String msisdn, String vname, String smallarea_name,
            String housetype_name, String unit_name, String roomno, int bool_ower) {
        this.id = id;
        this.msisdn = msisdn;
        this.vname = vname;
        this.smallarea_name = smallarea_name;
        this.housetype_name = housetype_name;
        this.unit_name = unit_name;
        this.roomno = roomno;
        this.bool_ower = bool_ower;
    }

    @Generated(hash = 1420373616)
    public OwnerData() {
    }



    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsisdn() {
        return this.msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getVname() {
        return this.vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    public String getSmallarea_name() {
        return this.smallarea_name;
    }

    public void setSmallarea_name(String smallarea_name) {
        this.smallarea_name = smallarea_name;
    }

    public String getHousetype_name() {
        return this.housetype_name;
    }

    public void setHousetype_name(String housetype_name) {
        this.housetype_name = housetype_name;
    }

    public String getUnit_name() {
        return this.unit_name;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public String getRoomno() {
        return this.roomno;
    }

    public void setRoomno(String roomno) {
        this.roomno = roomno;
    }

    public int getBool_ower() {
        return this.bool_ower;
    }

    public void setBool_ower(int bool_ower) {
        this.bool_ower = bool_ower;
    }
    




}
