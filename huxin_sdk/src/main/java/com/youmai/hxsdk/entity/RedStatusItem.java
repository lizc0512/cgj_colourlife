package com.youmai.hxsdk.entity;


/**
 * Created by colin on 2017/12/13.
 */

public class RedStatusItem extends RespBaseBean {

    private String name;
    private String time;
    private String money;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}
