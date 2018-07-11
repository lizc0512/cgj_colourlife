package com.youmai.hxsdk.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.entity.cn.CN;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

/**
 * Created by YW on 2018/4/11.
 */
@Entity
public class ContactBean implements CN, Parcelable {

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
    private int memberRole; //群成员角色

    private String sign;     //个性签名
    private boolean is_hx;   //true/false
    private String pinyin = "#"; //姓名拼音
    private String simplePinyin;//简拼

    private String orgType;
    private int uiType;

    //新增字段
    @Transient
    private String name;
    @Transient
    private String qq;

    public String getFavoriteid() {
        return Favoriteid;
    }

    public void setFavoriteid(String favoriteid) {
        Favoriteid = favoriteid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    @Transient

    private String phone_number;
    @Transient
    private String family;
    @Transient
    private String icon;
    @Transient
    private String Favoriteid;//企业短号
    @Transient
    private String accountUuid;//
    @Transient
    private String enterprise_cornet;//

    public ContactBean(String name) {
        this.realname = name;
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

    public int getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(int memberRole) {
        this.memberRole = memberRole;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getEnterprise_cornet() {
        return enterprise_cornet;
    }

    public void setEnterprise_cornet(String enterprise_cornet) {
        this.enterprise_cornet = enterprise_cornet;
    }

    public boolean isIs_hx() {
        return is_hx;
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

    public int getUiType() {
        return this.uiType;
    }

    public void setUiType(int uiType) {
        this.uiType = uiType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.uuid);
        dest.writeString(this.uid);
        dest.writeString(this.mobile);
        dest.writeString(this.realname);
        dest.writeString(this.avatar);
        dest.writeString(this.sex);
        dest.writeString(this.email);
        dest.writeString(this.isFavorite);
        dest.writeString(this.jobName);
        dest.writeString(this.landline);
        dest.writeString(this.orgID);
        dest.writeString(this.orgName);
        dest.writeString(this.username);
        dest.writeInt(this.memberRole);
        dest.writeString(this.sign);
        dest.writeByte(this.is_hx ? (byte) 1 : (byte) 0);
        dest.writeString(this.pinyin);
        dest.writeString(this.simplePinyin);
        dest.writeInt(this.uiType);
        dest.writeString(this.name);
        dest.writeString(this.qq);
        dest.writeString(this.phone_number);
        dest.writeString(this.family);
        dest.writeString(this.icon);
        dest.writeString(this.enterprise_cornet);
        dest.writeString(this.accountUuid);
        dest.writeString(this.Favoriteid);
    }


    public String getOrgType() {
        return this.orgType;
    }


    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    protected ContactBean(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.uuid = in.readString();
        this.uid = in.readString();
        this.mobile = in.readString();
        this.realname = in.readString();
        this.avatar = in.readString();
        this.sex = in.readString();
        this.email = in.readString();
        this.isFavorite = in.readString();
        this.jobName = in.readString();
        this.landline = in.readString();
        this.orgID = in.readString();
        this.orgName = in.readString();
        this.username = in.readString();
        this.memberRole = in.readInt();
        this.sign = in.readString();
        this.is_hx = in.readByte() != 0;
        this.pinyin = in.readString();
        this.simplePinyin = in.readString();
        this.uiType = in.readInt();
        this.name = in.readString();
        this.qq = in.readString();
        this.phone_number = in.readString();
        this.family = in.readString();
        this.icon = in.readString();
        this.accountUuid = in.readString();
        this.Favoriteid = in.readString();
    }




    @Generated(hash = 1283900925)
    public ContactBean() {
    }


    @Generated(hash = 35180811)
    public ContactBean(Long id, String uuid, String uid, String mobile,
            String realname, String avatar, String sex, String email,
            String isFavorite, String jobName, String landline, String orgID,
            String orgName, String username, int memberRole, String sign,
            boolean is_hx, String pinyin, String simplePinyin, String orgType,
            int uiType) {
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
        this.memberRole = memberRole;
        this.sign = sign;
        this.is_hx = is_hx;
        this.pinyin = pinyin;
        this.simplePinyin = simplePinyin;
        this.orgType = orgType;
        this.uiType = uiType;
    }


   

    public static final Creator<ContactBean> CREATOR = new Creator<ContactBean>() {
        @Override
        public ContactBean createFromParcel(Parcel source) {
            return new ContactBean(source);
        }

        @Override
        public ContactBean[] newArray(int size) {
            return new ContactBean[size];
        }
    };
}
