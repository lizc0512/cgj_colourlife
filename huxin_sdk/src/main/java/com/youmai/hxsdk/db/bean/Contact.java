package com.youmai.hxsdk.db.bean;

import com.youmai.hxsdk.entity.cn.CN;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by YW on 2018/4/11.
 */
@Entity
public class Contact implements CN {

    @Id
    private int contactId; //主键id
    private String user_id;  //用户id - uuid
    private String phone;    //联系人号码
    private String nick_name; //姓名 - 联系人名
    private String avatar; //头像url
    private int sex;       //性别
    private String sign;     //个性签名
    private boolean is_hx;   //true/false
    private String pinyin = "#"; //姓名拼音
    private String simplePinyin;//简拼

    public String type = ""; //信息类型 企业：org 个人：user
    public String username =""; //拼音的姓 与 名的首字母

    public Contact() {
    }

    public Contact(String name) {
        this.nick_name = name;
    }

    @Generated(hash = 312957562)
    public Contact(int contactId, String user_id, String phone, String nick_name,
            String avatar, int sex, String sign, boolean is_hx, String pinyin,
            String simplePinyin, String type, String username) {
        this.contactId = contactId;
        this.user_id = user_id;
        this.phone = phone;
        this.nick_name = nick_name;
        this.avatar = avatar;
        this.sex = sex;
        this.sign = sign;
        this.is_hx = is_hx;
        this.pinyin = pinyin;
        this.simplePinyin = simplePinyin;
        this.type = type;
        this.username = username;
    }

    @Override
    public String chinese() {
        return nick_name;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setIs_hx(boolean is_hx) {
        this.is_hx = is_hx;
    }

    public boolean getIs_hx() {
        return this.is_hx;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getSimplePinyin() {
        return simplePinyin;
    }

    public void setSimplePinyin(String simplePinyin) {
        this.simplePinyin = simplePinyin;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "contactId=" + contactId +
                ", user_id='" + user_id + '\'' +
                ", phone='" + phone + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", sex=" + sex +
                ", sign='" + sign + '\'' +
                ", is_hx=" + is_hx +
                ", pinyin='" + pinyin + '\'' +
                ", simplePinyin='" + simplePinyin + '\'' +
                ", type='" + type + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
