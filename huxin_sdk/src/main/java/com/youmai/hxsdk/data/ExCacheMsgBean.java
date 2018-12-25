package com.youmai.hxsdk.data;

import com.youmai.hxsdk.adapter.MessageAdapter;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.entity.MsgConfig;

/**
 * 最新沟通联系人
 */

public class ExCacheMsgBean extends CacheMsgBean {
    private int uiType;
    private MsgConfig.ContentBean.DataBean pushMsg;

    private String pinyin; // 姓名拼音
    private String simplepinyin;//简拼
    private String displayName;//姓名


    private int contactId;//通讯id  没有在通讯录内显示0
    private boolean mIsMultiNumber = false;//多号码识别


    public ExCacheMsgBean(MsgConfig.ContentBean.DataBean pushMsg) {
        this.pushMsg = pushMsg;
        setTargetUuid(pushMsg.getClient_code());

        /*setMsgTime(System.currentTimeMillis());
        String pushTime = pushMsg.getHomePushTime();
        if (!TextUtils.isEmpty(pushTime)) {
            try {
                Calendar calendar = TimeUtils.parseDate(pushTime, TimeUtils.DEFAULT_DATE_FORMAT);
                long millis = calendar.getTimeInMillis();
                setMsgTime(millis);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }*/

        uiType = MessageAdapter.ADAPTER_TYPE_PUSHMSG;
    }

    public ExCacheMsgBean(CacheMsgBean bean) {
        super(bean);
        if (bean.getGroupId() > 0) {
            uiType = MessageAdapter.ADAPTER_TYPE_GROUP;
        } else {
            uiType = MessageAdapter.ADAPTER_TYPE_SINGLE;
        }

    }

    public int getUiType() {
        return uiType;
    }

    public void setUiType(int uiType) {
        this.uiType = uiType;
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
        if (displayName == null) {
            displayName = "";
        }
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

    public MsgConfig.ContentBean.DataBean getPushMsg() {
        return pushMsg;
    }

    public void setPushMsg(MsgConfig.ContentBean.DataBean pushMsg) {
        this.pushMsg = pushMsg;
    }

}
