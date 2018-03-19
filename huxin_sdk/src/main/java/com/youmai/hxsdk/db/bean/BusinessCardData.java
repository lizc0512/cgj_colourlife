package com.youmai.hxsdk.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 名片主信息
 * Created by fylder on 2017/10/9.
 */
@Entity
public class BusinessCardData {
    @Id
    private Long id;

    private Long userId;//保留
    private String phone;
    private String name;
    private String headUrl;
    private String company;
    private String job;
    private String address;

    private String sex;
    private Long contact_id;


    @Generated(hash = 316192522)
    public BusinessCardData(Long id, Long userId, String phone, String name,
            String headUrl, String company, String job, String address, String sex,
            Long contact_id) {
        this.id = id;
        this.userId = userId;
        this.phone = phone;
        this.name = name;
        this.headUrl = headUrl;
        this.company = company;
        this.job = job;
        this.address = address;
        this.sex = sex;
        this.contact_id = contact_id;
    }

    @Generated(hash = 1460582827)
    public BusinessCardData() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Long getContact_id() {
        return contact_id;
    }

    public void setContact_id(Long contact_id) {
        this.contact_id = contact_id;
    }

    @Override
    public String toString() {
        return "BusinessCardData{" +
                "id=" + id +
                ", userId=" + userId +
                ", phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", headUrl='" + headUrl + '\'' +
                ", company='" + company + '\'' +
                ", job='" + job + '\'' +
                ", address='" + address + '\'' +
                ", sex='" + sex + '\'' +
                ", contact_id=" + contact_id +
                '}';
    }
}
