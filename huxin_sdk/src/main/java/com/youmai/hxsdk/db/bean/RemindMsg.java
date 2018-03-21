package com.youmai.hxsdk.db.bean;


import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.im.cache.CacheMsgCall;
import com.youmai.hxsdk.im.cache.CacheMsgEmotion;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.im.cache.CacheMsgMap;
import com.youmai.hxsdk.im.cache.CacheMsgRemark;
import com.youmai.hxsdk.im.cache.CacheMsgShow;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.im.cache.CacheMsgVideo;
import com.youmai.hxsdk.im.cache.CacheMsgVoice;
import com.youmai.hxsdk.im.cache.ContactsDetailsBean;
import com.youmai.hxsdk.im.cache.JsonFormate;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.json.JSONException;
import org.json.JSONObject;
import org.greenrobot.greendao.annotation.Generated;

import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_BIZCARD;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_CALL;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_EMOTION;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_FILE;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_IMG;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_MAP;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_REMARK;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_SHOW;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_TXT;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_VIDEO;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_VOICE;


/**
 * Created by colin on 2017/10/31.
 */
@Entity
public class RemindMsg implements Parcelable {

    @Id
    private Long id;

    private int remindId;
    private Long msgId; //发送消息成功后IM后台回给的消息Id
    private String title;   //提醒标题
    private String remark;   //提醒备注
    private int msgType;  //IM消息类型
    private int msgIcon;  //提醒类型 对于提醒ICON
    private long remindTime;   //设置提醒的时间

    private String sendPhone;   //IM接收方号码
    private String receivePhone;   //IM发送方号码

    private long createTime;   //IM发送方号码

    private String fids;

    private long recTime;   //接收提醒的时间
    private boolean isRead;

    private String quickPhone; //快捷拨号


    public RemindMsg(JSONObject content) throws JSONException {
        this.remindId = content.optInt("remindId");
        this.msgId = content.optLong("msgId");
        this.title = content.optString("msgContent");
        this.remark = content.optString("remark");

        this.msgType = content.optInt("msgType");
        this.msgIcon = content.optInt("msgIcon");

        this.remindTime = content.optLong("remindTime");

        this.sendPhone = content.optString("sendPhone");   //IM接收方号码
        this.receivePhone = content.optString("receivePhone");  //IM发送方号码

        this.createTime = content.optLong("createTime");

        this.fids = content.optString("fids");  //IM发送方号码

        this.recTime = System.currentTimeMillis();

        this.quickPhone = content.optString("quickPhone");
    }

    @Generated(hash = 428977505)
    public RemindMsg(Long id, int remindId, Long msgId, String title, String remark, int msgType,
                     int msgIcon, long remindTime, String sendPhone, String receivePhone, long createTime,
                     String fids, long recTime, boolean isRead, String quickPhone) {
        this.id = id;
        this.remindId = remindId;
        this.msgId = msgId;
        this.title = title;
        this.remark = remark;
        this.msgType = msgType;
        this.msgIcon = msgIcon;
        this.remindTime = remindTime;
        this.sendPhone = sendPhone;
        this.receivePhone = receivePhone;
        this.createTime = createTime;
        this.fids = fids;
        this.recTime = recTime;
        this.isRead = isRead;
        this.quickPhone = quickPhone;
    }


    @Generated(hash = 375293469)
    public RemindMsg() {
    }


    public String getTitle() {
        String res;
        String format = "来自 %s %s聊天提醒";
        switch (msgType) {   //根据Item类别不同，选择不同的Item布局
            case MSG_TYPE_TXT:
                //CacheMsgTxt text = (CacheMsgTxt) getJsonBodyObj(msgType, title);
                res = String.format(format, getOtherName(), "文字");
                break;
            case MSG_TYPE_IMG:
                //CacheMsgImage image = (CacheMsgImage) getJsonBodyObj(msgType, title);
                res = String.format(format, getOtherName(), "图片");
                break;
            case MSG_TYPE_VIDEO:
                //CacheMsgVideo video = (CacheMsgVideo) getJsonBodyObj(msgType, title);
                res = String.format(format, getOtherName(), "视频");
                break;
            case MSG_TYPE_VOICE:
                //CacheMsgVoice voice = (CacheMsgVoice) getJsonBodyObj(msgType, title);
                res = String.format(format, getOtherName(), "语音");
                break;
            case MSG_TYPE_MAP:
                //CacheMsgMap map = (CacheMsgMap) getJsonBodyObj(msgType, title);
                res = String.format(format, getOtherName(), "位置");
                break;
            case MSG_TYPE_FILE:
                //CacheMsgFile file = (CacheMsgFile) getJsonBodyObj(msgType, title);
                res = String.format(format, getOtherName(), "文件");
                break;
            case MSG_TYPE_BIZCARD:
                //ContactsDetailsBean card = (ContactsDetailsBean) getJsonBodyObj(msgType, title);
                res = String.format(format, getOtherName(), "名片");
                break;
            default:
                res = "呼信提醒";
                break;
        }

        return res;
    }

    public String getOtherPhone() {
        String otherPhone;
        if (HuxinSdkManager.instance().getPhoneNum().equals(sendPhone)) {
            otherPhone = receivePhone;
        } else {
            otherPhone = sendPhone;
        }
        return otherPhone;
    }


    public String getOtherName() {
        String otherPhone;
        if (HuxinSdkManager.instance().getPhoneNum().equals(sendPhone)) {
            otherPhone = receivePhone;
        } else {
            otherPhone = sendPhone;
        }
        return HuxinSdkManager.instance().getContactName(otherPhone);
    }



    private JsonFormate getJsonBodyObj(int msgType, String contentJsonBody) {
        JsonFormate jsonBodyObj = null;
        switch (msgType) {
            case MSG_TYPE_TXT:
                jsonBodyObj = new CacheMsgTxt().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_IMG:
                jsonBodyObj = new CacheMsgImage().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_MAP:
                jsonBodyObj = new CacheMsgMap().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_VOICE:
                jsonBodyObj = new CacheMsgVoice().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_EMOTION:
                jsonBodyObj = new CacheMsgEmotion().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_FILE:
                jsonBodyObj = new CacheMsgFile().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_SHOW:
                jsonBodyObj = new CacheMsgShow().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_CALL:
                jsonBodyObj = new CacheMsgCall().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_BIZCARD:
                jsonBodyObj = new ContactsDetailsBean().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_REMARK:
                jsonBodyObj = new CacheMsgRemark().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_VIDEO:
                jsonBodyObj = new CacheMsgVideo().fromJson(contentJsonBody);
                break;
        }
        return jsonBodyObj;
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public int getRemindId() {
        return this.remindId;
    }


    public void setRemindId(int remindId) {
        this.remindId = remindId;
    }


    public Long getMsgId() {
        return this.msgId;
    }


    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getRemark() {
        return this.remark;
    }


    public void setRemark(String remark) {
        this.remark = remark;
    }


    public int getMsgType() {
        return this.msgType;
    }


    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }


    public int getMsgIcon() {
        return this.msgIcon;
    }


    public void setMsgIcon(int msgIcon) {
        this.msgIcon = msgIcon;
    }


    public long getRemindTime() {
        return this.remindTime;
    }


    public void setRemindTime(long remindTime) {
        this.remindTime = remindTime;
    }


    public String getSendPhone() {
        return this.sendPhone;
    }


    public void setSendPhone(String sendPhone) {
        this.sendPhone = sendPhone;
    }


    public String getReceivePhone() {
        return this.receivePhone;
    }


    public void setReceivePhone(String receivePhone) {
        this.receivePhone = receivePhone;
    }


    public long getCreateTime() {
        return this.createTime;
    }


    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }


    public String getFids() {
        return this.fids;
    }


    public void setFids(String fids) {
        this.fids = fids;
    }


    public long getRecTime() {
        return this.recTime;
    }


    public void setRecTime(long recTime) {
        this.recTime = recTime;
    }


    public boolean getIsRead() {
        return this.isRead;
    }


    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public String getQuickPhone() {
        return this.quickPhone;
    }


    public void setQuickPhone(String quickPhone) {
        this.quickPhone = quickPhone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeInt(this.remindId);
        dest.writeValue(this.msgId);
        dest.writeString(this.title);
        dest.writeString(this.remark);
        dest.writeInt(this.msgType);
        dest.writeInt(this.msgIcon);
        dest.writeLong(this.remindTime);
        dest.writeString(this.sendPhone);
        dest.writeString(this.receivePhone);
        dest.writeLong(this.createTime);
        dest.writeString(this.fids);
        dest.writeLong(this.recTime);
        dest.writeByte(this.isRead ? (byte) 1 : (byte) 0);
        dest.writeString(this.quickPhone);
    }

    protected RemindMsg(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.remindId = in.readInt();
        this.msgId = (Long) in.readValue(Long.class.getClassLoader());
        this.title = in.readString();
        this.remark = in.readString();
        this.msgType = in.readInt();
        this.msgIcon = in.readInt();
        this.remindTime = in.readLong();
        this.sendPhone = in.readString();
        this.receivePhone = in.readString();
        this.createTime = in.readLong();
        this.fids = in.readString();
        this.recTime = in.readLong();
        this.isRead = in.readByte() != 0;
        this.quickPhone = in.readString();
    }

    public static final Parcelable.Creator<RemindMsg> CREATOR = new Parcelable.Creator<RemindMsg>() {
        @Override
        public RemindMsg createFromParcel(Parcel source) {
            return new RemindMsg(source);
        }

        @Override
        public RemindMsg[] newArray(int size) {
            return new RemindMsg[size];
        }
    };
}
