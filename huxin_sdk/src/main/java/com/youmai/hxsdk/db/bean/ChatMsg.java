package com.youmai.hxsdk.db.bean;


import android.os.Parcel;
import android.os.Parcelable;

import com.youmai.hxsdk.chat.MsgContent;
import com.youmai.hxsdk.proto.YouMaiChat;
import com.youmai.hxsdk.proto.YouMaiChat.IMChat_Personal;
import com.youmai.hxsdk.push.utils.PushJsonUtils;
import com.youmai.hxsdk.socket.IMContentType;
import com.youmai.hxsdk.socket.IMContentUtil;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by colin on 2016/7/22.
 */
@Entity
public class ChatMsg implements Parcelable {

    //ERROR预留错误类型，目前不做展示处理
    public enum MsgType {
        TEXT, EMO_TEXT, PICTURE, AUDIO, VIDEO, URL, LOCATION, BEGIN_LOCATION, SHOW_PICTURE,
        SHOW_VIDEO, BIG_FILE, BIZCARD, REMARK, ERROR, MAX_TYPE, JOKE_TEXT, LOCATION_SHARE,
        LOCATION_INVITE, LOCATION_ANSWER, LOCATION_QUIT
    }

    @Id
    private Long id;

    private long msgTime; //消息发送时间

    private long msgId; //消息ID

    private String srcName;  //发送方用户名

    private String srcPhone;  //发送方手机号码

    private int srcUserType; //发送方用户类型

    private int srcUsrId;  //发送方用户ID

    private String targetName;  //接收方用户名

    private String targetPhone; //接收方手机号码

    private int targetUserType;//接收方用户类型

    private int targetUserId; //接收方用户ID

    private String jsonBoby;

    private int contentType;

    // add by kevin start
    public boolean isShow = false;

    public String fid;

    public String mphone;

    public String detailurl;

    public String type;

    public String name;

    public String nickname;

    public String pfid; //缩略图

    public String file_type = "0"; //文件类型  0 为图片  1为视频
    //end by kevin ends

    @Transient
    private MsgContent msgContent;//根据jsonBoby得到

    @Transient
    private MsgType msgType; //根据contentType得到

    @Transient
    private boolean isOwner;  //false对方发送的消息 ,true自己发送的消息


    /************************************************************/
    /************************以上为类成员************************/
    /************************************************************/

    public ChatMsg(boolean isFromMe) {
        isOwner = isFromMe;
        msgTime = System.currentTimeMillis();
    }


    /**
     * 聊天IM界面构造
     *
     * @param imchat
     */
    public ChatMsg(IMChat_Personal imchat) {
        // 修复int溢出
        msgTime = (long) imchat.getTimestamp() * 1000;//服务器下发时间戳单位为秒

        contentType = imchat.getContentType();
        msgType = parseMsgType(contentType);
        if (msgType == null) {
            // 未知的消息类型
            return;
        }

        msgId = imchat.getMsgId();
        srcName = imchat.getSrcName();
        srcPhone = imchat.getSrcPhone();
        srcUserType = imchat.getSrcUserType();
        srcUsrId = imchat.getSrcUsrId();

        targetName = imchat.getTargetName();
        targetPhone = imchat.getTargetPhone();
        targetUserType = imchat.getTargetUserType();
        targetUserId = imchat.getTargetUserId();

        jsonBoby = imchat.getBody();

    }


    /**
     * for push message
     *
     * @param pushMsg
     */
    public ChatMsg(String pushMsg) {
        srcPhone = PushJsonUtils.getValue(pushMsg, String.valueOf(IMContentType.CONTENT_PHONE));//对方手机号
        String type = PushJsonUtils.getValue(pushMsg, String.valueOf(IMContentType.CONTEXT_TEXT_TYPE));//对方手机号

        int typeInt = Integer.parseInt(type);

        jsonBoby = pushMsg;
        IMContentUtil parser = new IMContentUtil(jsonBoby);

        contentType = IMContentUtil.getContentType(typeInt, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION_VALUE);
        msgType = parseMsgType(contentType);
    }

    protected ChatMsg(Parcel in) {
        msgTime = in.readLong();
        msgId = in.readLong();
        srcName = in.readString();
        srcPhone = in.readString();
        srcUserType = in.readInt();
        srcUsrId = in.readInt();
        targetName = in.readString();
        targetPhone = in.readString();
        targetUserType = in.readInt();
        targetUserId = in.readInt();
        jsonBoby = in.readString();
        contentType = in.readInt();
        isShow = in.readByte() != 0;
        fid = in.readString();
        mphone = in.readString();
        detailurl = in.readString();
        type = in.readString();
        name = in.readString();
        nickname = in.readString();
        pfid = in.readString();
        file_type = in.readString();
        msgContent = in.readParcelable(MsgContent.class.getClassLoader());
        isOwner = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(msgTime);
        dest.writeLong(msgId);
        dest.writeString(srcName);
        dest.writeString(srcPhone);
        dest.writeInt(srcUserType);
        dest.writeInt(srcUsrId);
        dest.writeString(targetName);
        dest.writeString(targetPhone);
        dest.writeInt(targetUserType);
        dest.writeInt(targetUserId);
        dest.writeString(jsonBoby);
        dest.writeInt(contentType);
        dest.writeByte((byte) (isShow ? 1 : 0));
        dest.writeString(fid);
        dest.writeString(mphone);
        dest.writeString(detailurl);
        dest.writeString(type);
        dest.writeString(name);
        dest.writeString(nickname);
        dest.writeString(pfid);
        dest.writeString(file_type);
        dest.writeParcelable(msgContent, flags);
        dest.writeByte((byte) (isOwner ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChatMsg> CREATOR = new Creator<ChatMsg>() {
        @Override
        public ChatMsg createFromParcel(Parcel in) {
            return new ChatMsg(in);
        }

        @Override
        public ChatMsg[] newArray(int size) {
            return new ChatMsg[size];
        }
    };

    private MsgType parseMsgType(int contentType) {
        MsgType msgType = null;
        if (IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_TEXT_VALUE)
                || IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_SHORT_MESSAGE_VALUE)
                || IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_CONTACTS_VALUE)
                || IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_RECOMMEND_APP_VALUE)
                || IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_NO_DISTURB_VALUE)
                || IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_KAQUAN_VALUE)) {
            msgType = MsgType.TEXT;
        } else if (IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_IMAGE_VALUE)) {
            msgType = MsgType.PICTURE;
        } else if (IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_AUDIO_VALUE)) {
            msgType = MsgType.AUDIO;
        } else if (IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_VIDEO_VALUE)) {
            msgType = MsgType.VIDEO;
        } else if (IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_URL_VALUE)) {
            msgType = MsgType.URL;
        } else if (IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION_VALUE)) {
            msgType = MsgType.LOCATION;
        } else if (IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATIONSHARE_VALUE)) {
            msgType = MsgType.BEGIN_LOCATION;
        } else if (IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_FILE_VALUE)) {
            msgType = MsgType.BIG_FILE;
        } else if (IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_BIZCARD_VALUE)) {
            msgType = MsgType.BIZCARD;
        } else if (IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_REMARK_VALUE)) {
            msgType = MsgType.REMARK;
        } else if (IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATIONSHARE_VALUE)) {
            msgType = MsgType.LOCATION_SHARE;
        } else if (IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION_INVITE_VALUE)) {
            msgType = MsgType.LOCATION_INVITE;
        } else if (IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION_ANSWER_VALUE)) {
            msgType = MsgType.LOCATION_ANSWER;
        } else if (IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION_QUIT_VALUE)) {
            msgType = MsgType.LOCATION_QUIT;
        } else {
            msgType = MsgType.ERROR;
        }

        return msgType;
    }

    //start set get method
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public void setSrcName(String srcName) {
        this.srcName = srcName;
    }

    public void setSrcPhone(String srcPhone) {
        this.srcPhone = srcPhone;
    }

    public void setSrcUserType(int srcUserType) {
        this.srcUserType = srcUserType;
    }

    public void setSrcUsrId(int srcUsrId) {
        this.srcUsrId = srcUsrId;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public void setTargetPhone(String targetPhone) {
        this.targetPhone = targetPhone;
    }

    public void setTargetUserType(int targetUserType) {
        this.targetUserType = targetUserType;
    }

    public void setTargetUserId(int targetUserId) {
        this.targetUserId = targetUserId;
    }

    public void setJsonBoby(String jsonBoby) {
        this.jsonBoby = jsonBoby;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public boolean getIsShow() {
        return this.isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow = isShow;
    }

    public String getFid() {
        return this.fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getMphone() {
        return this.mphone;
    }

    public void setMphone(String mphone) {
        this.mphone = mphone;
    }

    public String getDetailurl() {
        return this.detailurl;
    }

    public void setDetailurl(String detailurl) {
        this.detailurl = detailurl;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPfid() {
        return this.pfid;
    }

    public void setPfid(String pfid) {
        this.pfid = pfid;
    }

    public String getFile_type() {
        return this.file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public long getMsgId() {
        return msgId;
    }

    public String getSrcName() {
        return srcName;
    }

    public String getSrcPhone() {
        return srcPhone;
    }

    public int getSrcUserType() {
        return srcUserType;
    }

    public int getSrcUsrId() {
        return srcUsrId;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getTargetPhone() {
        return targetPhone;
    }

    public int getTargetUserType() {
        return targetUserType;
    }

    public int getTargetUserId() {
        return targetUserId;
    }

    public String getJsonBoby() {
        return jsonBoby;
    }

    public int getContentType() {
        return contentType;
    }

    public String getName() {
        return name;
    }

    public MsgType getMsgType() {
        if (msgType == null) {
            if (contentType != 0) {
                msgType = parseMsgType(contentType);
            }
        }

        return msgType;
    }


    public void setMsgType(MsgType type) {
        this.msgType = type;
    }


    public boolean isOwner() {
        return isOwner;
    }

    public MsgContent getMsgContent() {
        if (msgContent == null) {
            msgContent = new MsgContent();
        }
        return msgContent;
    }


    @Generated(hash = 1784406075)
    public ChatMsg(Long id, long msgTime, long msgId, String srcName, String srcPhone, int srcUserType, int srcUsrId,
                   String targetName, String targetPhone, int targetUserType, int targetUserId, String jsonBoby, int contentType,
                   boolean isShow, String fid, String mphone, String detailurl, String type, String name, String nickname, String pfid,
                   String file_type) {
        this.id = id;
        this.msgTime = msgTime;
        this.msgId = msgId;
        this.srcName = srcName;
        this.srcPhone = srcPhone;
        this.srcUserType = srcUserType;
        this.srcUsrId = srcUsrId;
        this.targetName = targetName;
        this.targetPhone = targetPhone;
        this.targetUserType = targetUserType;
        this.targetUserId = targetUserId;
        this.jsonBoby = jsonBoby;
        this.contentType = contentType;
        this.isShow = isShow;
        this.fid = fid;
        this.mphone = mphone;
        this.detailurl = detailurl;
        this.type = type;
        this.name = name;
        this.nickname = nickname;
        this.pfid = pfid;
        this.file_type = file_type;
    }

    @Generated(hash = 1355502543)
    public ChatMsg() {
    }

    @Override
    public String toString() {
        return "ChatMsg{" +
                "id=" + id +
                ", msgTime=" + msgTime +
                ", msgId=" + msgId +
                ", srcName='" + srcName + '\'' +
                ", srcPhone='" + srcPhone + '\'' +
                ", srcUserType=" + srcUserType +
                ", srcUsrId=" + srcUsrId +
                ", targetName='" + targetName + '\'' +
                ", targetPhone='" + targetPhone + '\'' +
                ", targetUserType=" + targetUserType +
                ", targetUserId=" + targetUserId +
                ", jsonBoby='" + jsonBoby + '\'' +
                ", contentType=" + contentType +
                ", isShow=" + isShow +
                ", fid='" + fid + '\'' +
                ", mphone='" + mphone + '\'' +
                ", detailurl='" + detailurl + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", pfid='" + pfid + '\'' +
                ", file_type='" + file_type + '\'' +
                ", msgContent=" + msgContent +
                ", msgType=" + msgType +
                ", isOwner=" + isOwner +
                '}';
    }

}
