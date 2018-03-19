package com.youmai.hxsdk.db.bean;

import com.youmai.hxsdk.entity.UserShow;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by colin on 2016/8/31.
 */
@Entity
public class UIData {

    @Id
    private Long id;

    private String msisdn;

    private String mphone;

    private String name;

    private String icon;

    private String num;

    private String type;

    private String data;

    public UIData(UserShow.DBean.SectionsBean bean) {
        name = bean.getName();
        icon = bean.getIcon();
        num = bean.getNum();
        type = bean.getType();
        data = bean.getData();
    }

    @Generated(hash = 1492208043)
    public UIData(Long id, String msisdn, String mphone, String name, String icon,
                  String num, String type, String data) {
        this.id = id;
        this.msisdn = msisdn;
        this.mphone = mphone;
        this.name = name;
        this.icon = icon;
        this.num = num;
        this.type = type;
        this.data = data;
    }

    @Generated(hash = 644669740)
    public UIData() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getMphone() {
        return mphone;
    }

    public void setMphone(String mphone) {
        this.mphone = mphone;
    }

    public String getData() {
        return data;
    }

    public String getType() {
        return type;
    }

    public String getNum() {
        return num;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getMsisdn() {
        return msisdn;
    }
}
