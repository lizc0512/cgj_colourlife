package com.youmai.hxsdk.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.im.cache.CacheMsgCall;
import com.youmai.hxsdk.im.cache.CacheMsgEmotion;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.im.cache.CacheMsgMap;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.im.cache.CacheMsgVideo;
import com.youmai.hxsdk.im.cache.CacheMsgVoice;
import com.youmai.hxsdk.im.cache.JsonFormate;
import com.youmai.hxsdk.utils.LogUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-12-06 11:31
 * Description:  消息实体类
 */
@Entity
public class CacheMsgBean implements Parcelable, Cloneable {

    public static final int MSG_TYPE_EMOTION = 1; //表情
    public static final int MSG_TYPE_TXT = 2; //文字
    public static final int MSG_TYPE_IMG = 3; //图片
    public static final int MSG_TYPE_MAP = 4; //地图
    public static final int MSG_TYPE_VIDEO = 5; //视频
    public static final int MSG_TYPE_VOICE = 6; //声音
    public static final int MSG_TYPE_FILE = 7; //文件
    public static final int MSG_TYPE_CALL = 9; //通话

    public static final int MSG_READ_STATUS = 1; //已读
    public static final int MSG_UNREAD_STATUS = 0; //未读
    public static final int MSG_SEND_FLAG_SENDING = -1;
    public static final int MSG_SEND_FLAG_SUCCESS = 0;
    public static final int MSG_SEND_FLAG_FAIL = 4;

    @Id
    private Long id;  //消息ID

    private int msgType; //消息类型

    private long msgTime; //消息时间

    private int senderUserId; //发送者的userid

    private int receiverUserId; //接收者的userid

    private String senderPhone; //发送者的电话

    private String receiverPhone; //接收者的电话

    private String contentJsonBody;  //消息内容json body

    private int send_flag = 0;  //发送状态   0:发送成功  -1:正在发送  2:短信发送  4:发送错误

    private int isRightUI = 0;  // 0--true  1--false

    private int is_read = MSG_READ_STATUS;  //读取状态  1 已读   0 未读


    private Long msgId; //发送消息成功后IM后台回给的消息Id


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

    public int getSend_flag() {
        return send_flag;
    }

    public CacheMsgBean setSend_flag(int send_flag) {
        this.send_flag = send_flag;
        return this;
    }

    public int getIsRightUI() {
        return isRightUI;
    }

    public CacheMsgBean setIsRightUI(int isRightUI) {
        this.isRightUI = isRightUI;
        return this;
    }

    public boolean isRightUI() {
        return isRightUI == 0;
    }

    public CacheMsgBean setRightUI(boolean rightUI) {
        isRightUI = rightUI ? 0 : 1;
        return this;
    }

    public int getIs_read() {
        return is_read;
    }

    public CacheMsgBean setIs_read(int read) {
        is_read = read;
        return this;
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
            case MSG_TYPE_CALL:
                jsonBodyObj = new CacheMsgCall().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_VIDEO:
                jsonBodyObj = new CacheMsgVideo().fromJson(contentJsonBody);
                break;
        }

        return jsonBodyObj;
    }


    public CacheMsgBean(CacheMsgBean bean) {
        this.id = bean.getId();
        this.msgType = bean.getMsgType();
        this.msgTime = bean.getMsgTime();
        this.senderUserId = bean.getSenderUserId();
        this.receiverUserId = bean.getReceiverUserId();
        this.senderPhone = bean.getSenderPhone();
        this.receiverPhone = bean.getReceiverPhone();
        this.contentJsonBody = bean.getContentJsonBody();
        this.send_flag = bean.getSend_flag();
        this.isRightUI = bean.getIsRightUI();
        this.is_read = bean.getIs_read();
        this.msgId = bean.getMsgId();
    }


    public Object clone() {
        Object o = null;
        try {
            o = super.clone();
        } catch (CloneNotSupportedException e) {
            LogUtils.d("CacheMsgBean", "CloneNotSupportedException");
        }
        return o;
    }

    @Override
    public String toString() {
        return "CacheMsgBean{" +
                "id=" + id +
                ", msgType=" + msgType +
                ", msgTime=" + msgTime +
                ", senderUserId=" + senderUserId +
                ", receiverUserId=" + receiverUserId +
                ", senderPhone='" + senderPhone + '\'' +
                ", receiverPhone='" + receiverPhone + '\'' +
                ", contentJsonBody='" + contentJsonBody + '\'' +
                ", send_flag=" + send_flag +
                ", isRightUI=" + isRightUI +
                ", is_read=" + is_read +
                ", msgId=" + msgId +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeInt(this.msgType);
        dest.writeLong(this.msgTime);
        dest.writeInt(this.senderUserId);
        dest.writeInt(this.receiverUserId);
        dest.writeString(this.senderPhone);
        dest.writeString(this.receiverPhone);
        dest.writeString(this.contentJsonBody);
        dest.writeInt(this.send_flag);
        dest.writeInt(this.isRightUI);
        dest.writeInt(this.is_read);
        dest.writeValue(this.msgId);
    }

    protected CacheMsgBean(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.msgType = in.readInt();
        this.msgTime = in.readLong();
        this.senderUserId = in.readInt();
        this.receiverUserId = in.readInt();
        this.senderPhone = in.readString();
        this.receiverPhone = in.readString();
        this.contentJsonBody = in.readString();
        this.send_flag = in.readInt();
        this.isRightUI = in.readInt();
        this.is_read = in.readInt();
        this.msgId = (Long) in.readValue(Long.class.getClassLoader());
    }

    @Generated(hash = 336231755)
    public CacheMsgBean(Long id, int msgType, long msgTime, int senderUserId, int receiverUserId,
            String senderPhone, String receiverPhone, String contentJsonBody, int send_flag, int isRightUI,
            int is_read, Long msgId) {
        this.id = id;
        this.msgType = msgType;
        this.msgTime = msgTime;
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
        this.senderPhone = senderPhone;
        this.receiverPhone = receiverPhone;
        this.contentJsonBody = contentJsonBody;
        this.send_flag = send_flag;
        this.isRightUI = isRightUI;
        this.is_read = is_read;
        this.msgId = msgId;
    }

    @Generated(hash = 107805209)
    public CacheMsgBean() {
    }

    public static final Parcelable.Creator<CacheMsgBean> CREATOR = new Parcelable.Creator<CacheMsgBean>() {
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
