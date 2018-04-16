package com.tg.coloursteward.module.meassage;

import com.youmai.hxsdk.db.bean.CacheMsgBean;

/**
 * 最新沟通联系人
 */

public class ExCacheMsgBean extends CacheMsgBean {
    private String pinyin; // 姓名拼音
    private String simplepinyin;//简拼
    private String displayName;//姓名

    private int contactId;//通讯id  没有在通讯录内显示0
    private boolean mIsMultiNumber = false;//多号码识别

    public ExCacheMsgBean(CacheMsgBean bean) {
        super(bean);
    }

    public boolean isMultiNumber() {
        return mIsMultiNumber;
    }

    public void setIsMultiNumber(boolean isMulti) {
        mIsMultiNumber = isMulti;
    }


    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getSimplepinyin() {
        return simplepinyin;
    }

    public void setSimplepinyin(String simplepinyin) {
        this.simplepinyin = simplepinyin;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    @Override
    public String toString() {
        return "ExCacheMsgBean{" +
                "pinyin='" + pinyin + '\'' +
                ", simplepinyin='" + simplepinyin + '\'' +
                ", displayName='" + displayName + '\'' +
                ", contactId=" + contactId +
                ", msgId=" + getMsgId() +
                ", msgType=" + getMsgType() +
                ", msgStatus=" + getMsgStatus() +
                ", msgTime=" + getMsgTime() +
                ", targetUuid='" + getTargetUuid() + '\'' +
                ", contentJsonBody='" + getContentJsonBody() + '\'' +
                ", mIsMultiNumber=" + mIsMultiNumber +
                '}';
    }
}
