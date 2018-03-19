package com.youmai.hxsdk.entity;

/**
 * Created by ym-005 on 2016/12/16.
 */

public class MccCode {
    String id;
    String mcc;
    String code;//前缀
    String country;//国家

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
