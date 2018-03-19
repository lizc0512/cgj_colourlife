package com.youmai.hxsdk.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 名片附属信息
 * <p>
 * type分类
 * 号码:phone,
 * 邮箱:email,
 * 日期:data,
 * 网站:web,
 * 即时消息:im
 * Created by fylder on 2017/12/5.
 */
@Entity
public class BusinessCardInfo {

    public static final String TYPE_PHONE = "phone";
    public static final String TYPE_EMAIL = "email";
    public static final String TYPE_DATE = "date";
    public static final String TYPE_WEB = "web";
    public static final String TYPE_IM = "im";


    @Id
    private Long id;
    private String phone;
    private Long userId;
    private String type;
    private String info;
    private Integer info_type;


    @Generated(hash = 1096222470)
    public BusinessCardInfo(Long id, String phone, Long userId, String type,
                            String info, Integer info_type) {
        this.id = id;
        this.phone = phone;
        this.userId = userId;
        this.type = type;
        this.info = info;
        this.info_type = info_type;
    }

    @Generated(hash = 340329886)
    public BusinessCardInfo() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getInfo_type() {
        if (info_type == null) {
            return 0;
        } else {
            return info_type;
        }
    }

    public void setInfo_type(Integer info_type) {
        this.info_type = info_type;
    }
}
