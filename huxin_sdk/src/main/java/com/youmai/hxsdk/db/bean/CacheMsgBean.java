package com.youmai.hxsdk.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.im.cache.CacheMsgEmotion;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.im.cache.CacheMsgMap;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.im.cache.CacheMsgVideo;
import com.youmai.hxsdk.im.cache.CacheMsgVoice;
import com.youmai.hxsdk.im.cache.JsonFormate;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-12-06 11:31
 * Description:  消息实体类
 */
@Entity
public class CacheMsgBean implements Parcelable {


    public static final int SEND_TEXT = 1;
    public static final int RECEIVE_TEXT = 2;

    public static final int SEND_IMAGE = 3;
    public static final int RECEIVE_IMAGE = 4;

    public static final int SEND_VOICE = 5;
    public static final int RECEIVE_VOICE = 6;

    public static final int SEND_VIDEO = 7;
    public static final int RECEIVE_VIDEO = 8;

    public static final int SEND_LOCATION = 9;
    public static final int RECEIVE_LOCATION = 10;

    public static final int SEND_FILE = 11;
    public static final int RECEIVE_FILE = 12;

    public static final int SEND_EMOTION = 13;
    public static final int RECEIVE_EMOTION = 14;

    public static final int SEND_DRAFT = 0;      //草稿
    public static final int SEND_GOING = 1;//正在发送
    public static final int SEND_SUCCEED = 2;    //发送成功
    public static final int SEND_FAILED = 3;  //发送失败
    public static final int RECEIVE_UNREAD = 4;   //接收到消息，未读
    public static final int RECEIVE_READ = 5;  //接收到消息，已读


//    public static final int MSG_TYPE_EMOTION = 1; //表情
//    public static final int MSG_TYPE_TXT = 2; //文字
//    public static final int MSG_TYPE_IMG = 3; //图片
//    public static final int MSG_TYPE_MAP = 4; //地图
//    public static final int MSG_TYPE_VIDEO = 5; //视频
//    public static final int MSG_TYPE_VOICE = 6; //声音
//    public static final int MSG_TYPE_FILE = 7; //文件
//    public static final int MSG_TYPE_CALL = 9; //通话

//    public static final int MSG_READ_STATUS = 1; //已读
//    public static final int MSG_UNREAD_STATUS = 0; //未读
//    public static final int MSG_SEND_FLAG_SENDING = -1;
//    public static final int MSG_SEND_FLAG_SUCCESS = 0;
//    public static final int MSG_SEND_FLAG_FAIL = 4;

    @Id
    private Long id;  //消息ID

    private Long msgId; //发送消息成功后IM后台回给的消息Id

    private int msgType; //消息类型

    private int msgStatus;

    private long msgTime; //消息时间

    private int senderUserId; //发送者的userid

    private int receiverUserId; //接收者的userid

    private String senderPhone; //发送者的电话

    private String receiverPhone; //接收者的电话

    private String contentJsonBody;  //消息内容json body


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMsgType() {
        return msgType;
    }

    public CacheMsgBean setMsgType(int msgType) {
        this.msgType = msgType;
        return this;
    }

    public int getMsgStatus() {
        return msgStatus;
    }

    public CacheMsgBean setMsgStatus(int msgStatus) {
        this.msgStatus = msgStatus;
        return this;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public CacheMsgBean setMsgTime(long msgTime) {
        this.msgTime = msgTime;
        return this;
    }

    public int getSenderUserId() {
        return senderUserId;
    }

    public CacheMsgBean setSenderUserId(int senderUserId) {
        this.senderUserId = senderUserId;
        return this;
    }

    public int getReceiverUserId() {
        return receiverUserId;
    }

    public CacheMsgBean setReceiverUserId(int receiverUserId) {
        this.receiverUserId = receiverUserId;
        return this;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public CacheMsgBean setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
        return this;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public CacheMsgBean setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
        return this;
    }

    /**
     * 设置json body obj
     *
     * @param jsonBodyObj
     * @return
     */
    public CacheMsgBean setJsonBodyObj(JsonFormate jsonBodyObj) {
        this.contentJsonBody = jsonBodyObj.toJson();
        return this;
    }

    public void setContentJsonBody(String contentJsonBody) {
        this.contentJsonBody = contentJsonBody;
    }

    public String getContentJsonBody() {
        return contentJsonBody;
    }


    public Long getMsgId() {
        return this.msgId;
    }

    public CacheMsgBean setMsgId(Long msgId) {
        this.msgId = msgId;
        return this;
    }

    public JsonFormate getJsonBodyObj() {
        JsonFormate jsonBodyObj = null;
        switch (msgType) {
            case SEND_TEXT:
            case RECEIVE_TEXT:
                jsonBodyObj = new CacheMsgTxt().fromJson(contentJsonBody);
                break;
            case SEND_IMAGE:
            case RECEIVE_IMAGE:
                jsonBodyObj = new CacheMsgImage().fromJson(contentJsonBody);
                break;
            case SEND_LOCATION:
            case RECEIVE_LOCATION:
                jsonBodyObj = new CacheMsgMap().fromJson(contentJsonBody);
                break;
            case SEND_VOICE:
            case RECEIVE_VOICE:
                jsonBodyObj = new CacheMsgVoice().fromJson(contentJsonBody);
                break;
            case SEND_EMOTION:
            case RECEIVE_EMOTION:
                jsonBodyObj = new CacheMsgEmotion().fromJson(contentJsonBody);
                break;
            case SEND_FILE:
            case RECEIVE_FILE:
                jsonBodyObj = new CacheMsgFile().fromJson(contentJsonBody);
                break;
            case SEND_VIDEO:
            case RECEIVE_VIDEO:
                jsonBodyObj = new CacheMsgVideo().fromJson(contentJsonBody);
                break;
        }

        return jsonBodyObj;
    }

    public boolean isRightUI() {
        boolean res = false;
        switch (msgType) {
            case SEND_TEXT:
                res = true;
                break;
            case SEND_IMAGE:
                res = true;
                break;
            case SEND_LOCATION:
                res = true;
                break;
            case SEND_VOICE:
                res = true;
                break;
            case SEND_EMOTION:
                res = true;
                break;
            case SEND_FILE:
                res = true;
                break;
            case SEND_VIDEO:
                res = true;
                break;
        }
        return res;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.msgId);
        dest.writeInt(this.msgType);
        dest.writeInt(this.msgStatus);
        dest.writeLong(this.msgTime);
        dest.writeInt(this.senderUserId);
        dest.writeInt(this.receiverUserId);
        dest.writeString(this.senderPhone);
        dest.writeString(this.receiverPhone);
        dest.writeString(this.contentJsonBody);
    }

    public CacheMsgBean() {
    }

    protected CacheMsgBean(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.msgId = (Long) in.readValue(Long.class.getClassLoader());
        this.msgType = in.readInt();
        this.msgStatus = in.readInt();
        this.msgTime = in.readLong();
        this.senderUserId = in.readInt();
        this.receiverUserId = in.readInt();
        this.senderPhone = in.readString();
        this.receiverPhone = in.readString();
        this.contentJsonBody = in.readString();
    }

    @Generated(hash = 1056906974)
    public CacheMsgBean(Long id, Long msgId, int msgType, int msgStatus, long msgTime,
                        int senderUserId, int receiverUserId, String senderPhone, String receiverPhone,
                        String contentJsonBody) {
        this.id = id;
        this.msgId = msgId;
        this.msgType = msgType;
        this.msgStatus = msgStatus;
        this.msgTime = msgTime;
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
        this.senderPhone = senderPhone;
        this.receiverPhone = receiverPhone;
        this.contentJsonBody = contentJsonBody;
    }

    public static final Creator<CacheMsgBean> CREATOR = new Creator<CacheMsgBean>() {
        @Override
        public CacheMsgBean createFromParcel(Parcel source) {
            return new CacheMsgBean(source);
        }

        @Override
        public CacheMsgBean[] newArray(int size) {
            return new CacheMsgBean[size];
        }
    };
}
