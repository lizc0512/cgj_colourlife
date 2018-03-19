package com.youmai.hxsdk.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 作者：create by YW
 * 日期：2017.02.21 15:59
 * 描述：记录通信录电话 & 打过的电话
 */
@Entity
public class PhoneCards {

    @Id
    private Long id;

    private String msisdn;

    @Generated(hash = 1923084550)
    public PhoneCards(Long id, String msisdn) {
        this.id = id;
        this.msisdn = msisdn;
    }

    @Generated(hash = 1418843921)
    public PhoneCards() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @Override
    public String toString() {
        return "PhoneCards{" +
                "id=" + id +
                ", msisdn='" + msisdn + '\'' +
                '}';
    }
}
