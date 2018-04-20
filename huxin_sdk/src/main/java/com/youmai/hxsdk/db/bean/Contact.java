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
    private Long id; //主键id

    private String uuid; //contactsId
    private String uid;  //用户id
    private String mobile;    //联系人号码
    private String realname; //姓名 - 联系人名
    private String avatar; //头像url
    private String sex;
    private String email;
    private String isFavorite; // 1或者2
    private String jobName;
    private String landline;
    private String orgID;
    private String orgName;
    private String username = ""; //拼音的姓 与 名的首字母

    private String sign;     //个性签名
    private boolean is_hx;   //true/false
    private String pinyin = "#"; //姓名拼音
    private String simplePinyin;//简拼

    public Contact(String name) {
        this.realname = name;
    }

    @Generated(hash = 514003334)
    public Contact(Long id, String uuid, String uid, String mobile, String realname,
            String avatar, String sex, String email, String isFavorite,
            String jobName, String landline, String orgID, String orgName,
            String username, String sign, boolean is_hx, String pinyin,
            String simplePinyin) {
        this.id = id;
        this.uuid = uuid;
        this.uid = uid;
        this.mobile = mobile;
        this.realname = realname;
        this.avatar = avatar;
        this.sex = sex;
        this.email = email;
        this.isFavorite = isFavorite;
        this.jobName = jobName;
        this.landline = landline;
        this.orgID = orgID;
        this.orgName = orgName;
        this.username = username;
        this.sign = sign;
        this.is_hx = is_hx;
        this.pinyin = pinyin;
        this.simplePinyin = simplePinyin;
    }

    @Generated(hash = 672515148)
    public Contact() {
    }

    @Override
    public String chinese() {
        return realname;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRealname() {
        return this.realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIsFavorite() {
        return this.isFavorite;
    }

    public void setIsFavorite(String isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getJobName() {
        return this.jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getLandline() {
        return this.landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public String getOrgID() {
        return this.orgID;
    }

    public void setOrgID(String orgID) {
        this.orgID = orgID;
    }

    public String getOrgName() {
        return this.orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSign() {
        return this.sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public boolean getIs_hx() {
        return this.is_hx;
    }

    public void setIs_hx(boolean is_hx) {
        this.is_hx = is_hx;
    }

    public String getPinyin() {
        return this.pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getSimplePinyin() {
        return this.simplePinyin;
    }

    public void setSimplePinyin(String simplePinyin) {
        this.simplePinyin = simplePinyin;
    }

}
