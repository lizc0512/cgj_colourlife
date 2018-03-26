package com.youmai.hxsdk.contact;

import com.youmai.hxsdk.contact.letter.bean.BaseIndexPinyinBean;

/**
 * 作者：create by YW
 * 日期：2018.03.20 14:23
 * 描述：
 */
public class CityBean extends BaseIndexPinyinBean {

    private String Province;

    private String city;//城市名字

    public CityBean() {
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        Province = province;
    }

    public CityBean(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String getTarget() {
        return city;
    }

    @Override
    public String toString() {
        return "CityBean{" +
                "Province='" + Province + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
