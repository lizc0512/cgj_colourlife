package com.youmai.hxsdk.contact.search.cn;

import android.support.annotation.NonNull;

import com.youmai.hxsdk.contact.search.cn.CN;

/**
 * Created by you on 2017/9/11.
 */
public class Contact implements CN, Comparable<Contact> {

    private int contactId; //id
    private int iconUrl; //头像url
    private String displayName;//姓名
    private String pinyin="#"; // 姓名拼音
    private String simplePinyin;//简拼

    public Contact() {
    }

    public Contact(String name) {
        this.displayName = name;
    }

    @Override
    public String chinese() {
        return displayName;
    }

    public int getIconUrl() {
        return iconUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public void setIconUrl(int iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public void setSimplePinyin(String simplePinyin) {
        this.simplePinyin = simplePinyin;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "contactId=" + contactId +
                ", iconUrl=" + iconUrl +
                ", displayName='" + displayName + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", simplePinyin='" + simplePinyin + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull Contact o) {
        return 0;
    }
}
