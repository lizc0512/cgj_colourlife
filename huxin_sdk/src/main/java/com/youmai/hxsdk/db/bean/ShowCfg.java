package com.youmai.hxsdk.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by colin on 2017/2/21.
 */
@Entity
public class ShowCfg {

    @Id
    private Long table_id;

    private String version;

    private int interval;

    private int unit;

    private int limits;

    @Generated(hash = 217273349)
    public ShowCfg(Long table_id, String version, int interval, int unit,
            int limits) {
        this.table_id = table_id;
        this.version = version;
        this.interval = interval;
        this.unit = unit;
        this.limits = limits;
    }

    @Generated(hash = 1062481790)
    public ShowCfg() {
    }

    public Long getTable_id() {
        return table_id;
    }

    public void setTable_id(Long table_id) {
        this.table_id = table_id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getLimits() {
        return limits;
    }

    public void setLimits(int limits) {
        this.limits = limits;
    }

    @Override
    public String toString() {
        return "ShowCfg{" +
                "table_id=" + table_id +
                ", version='" + version + '\'' +
                ", interval=" + interval +
                ", unit=" + unit +
                ", limits=" + limits +
                '}';
    }
}
