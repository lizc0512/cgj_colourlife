package com.youmai.hxsdk.im;

import com.youmai.hxsdk.chat.MsgContent;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.im.cache.JsonFormat;
import com.youmai.hxsdk.proto.YouMaiChat;
import com.youmai.hxsdk.push.utils.PushJsonUtils;
import com.youmai.hxsdk.socket.IMContentType;
import com.youmai.hxsdk.socket.IMContentUtil;

/**
 * 作者：create by YW
 * 日期：2018.03.27 15:00
 * 描述：
 */

public class IMChat {

    YouMaiChat.IMChat_Personal mImChat;
    String mJsonBody;
    int mMsgType;
    MsgContent mContent;

    public void init(YouMaiChat.IMChat_Personal imChat) {
        mImChat = imChat;
        mJsonBody = imChat.getBody();
        mMsgType = getMsgType();
        mContent = new MsgContent(mMsgType, mJsonBody);
    }

    public void initPush(String pushMsg) {
        String srcPhone = PushJsonUtils.getValue(pushMsg, String.valueOf(IMContentType.CONTENT_PHONE));//对方手机号
        String type = PushJsonUtils.getValue(pushMsg, String.valueOf(IMContentType.CONTEXT_TEXT_TYPE));//对方手机号

        int typeInt = Integer.parseInt(type);
        mJsonBody = pushMsg;
        int contentType = IMContentUtil.getContentType(typeInt, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION_VALUE);
        mMsgType = parseMsgType(contentType);
        mContent = new MsgContent(mMsgType, mJsonBody);
    }

    private CacheMsgBean mMsgBean;

    public void updateCacheBean(CacheMsgBean msgBean) {
        msgBean.setReceiverPhone(mImChat.getTargetPhone())
                .setSenderPhone(mImChat.getSrcPhone())
                .setSenderUserId(mImChat.getSrcUsrId())
                .setReceiverUserId(mImChat.getTargetUserId())
                .setMsgTime(System.currentTimeMillis())
                .setMsgStatus(CacheMsgBean.RECEIVE_UNREAD)
                .setMsgId(mImChat.getMsgId());
        msgBean.setContentJsonBody(mJsonBody);
        mMsgBean = msgBean;
    }

    public JsonFormat setFormatBody(int msgType, JsonFormat body) {
        mMsgType = msgType;
        return getJsonBodyObj(body);
    }

    public JsonFormat getJsonBodyObj(JsonFormat jsonBodyObj) {
        jsonBodyObj.fromJson(mMsgBean.getContentJsonBody());
        jsonBodyObj = jsonBodyObj.cloneProto(jsonBodyObj);
        return jsonBodyObj;
    }

    /**
     *
     * @return
     */
    public MsgContent getContent() {
        return mContent;
    }

    /**
     * 消息类型
     * @return
     */
    public int getMsgType() {
        int contentType = mImChat.getContentType();
        int msgType = parseMsgType(contentType);
        if (msgType < -1) {
            if (contentType != 0) {
                msgType = parseMsgType(contentType);
            }
        }
        return msgType;
    }

    private int parseMsgType(int contentType) {

        int msgType;
        if (IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_TEXT_VALUE)
                || IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_SHORT_MESSAGE_VALUE)
                || IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_CONTACTS_VALUE)
                || IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_RECOMMEND_APP_VALUE)
                || IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_NO_DISTURB_VALUE)
                || IMContentUtil.HasContentType(contentType, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_KAQUAN_VALUE)) {
            msgType = IMConst.IM_TEXT_VALUE;
        } else if (IMContentUtil.HasContentType(contentType, IMConst.IM_IMAGE_VALUE)) {
            msgType = IMConst.IM_IMAGE_VALUE;
        } else if (IMContentUtil.HasContentType(contentType, IMConst.IM_URL_VALUE)) {
            msgType = IMConst.IM_URL_VALUE;
        } else if (IMContentUtil.HasContentType(contentType, IMConst.IM_AUDIO_VALUE)) {
            msgType = IMConst.IM_AUDIO_VALUE;
        } else if (IMContentUtil.HasContentType(contentType, IMConst.IM_VIDEO_VALUE)) {
            msgType = IMConst.IM_VIDEO_VALUE;
        } else if (IMContentUtil.HasContentType(contentType, IMConst.IM_LOCATION_VALUE)) {
            msgType = IMConst.IM_LOCATION_VALUE;
        } else if (IMContentUtil.HasContentType(contentType, IMConst.IM_LOCATIONSHARE_VALUE)) {
            msgType = IMConst.IM_LOCATIONSHARE_VALUE;
        } else if (IMContentUtil.HasContentType(contentType, IMConst.IM_FILE_VALUE)) {
            msgType = IMConst.IM_FILE_VALUE;
        } else if (IMContentUtil.HasContentType(contentType, IMConst.IM_BIZCARD_VALUE)) {
            msgType = IMConst.IM_BIZCARD_VALUE;
        } else if (IMContentUtil.HasContentType(contentType, IMConst.IM_LOCATION_INVITE_VALUE)) {
            msgType = IMConst.IM_LOCATION_INVITE_VALUE;
        } else if (IMContentUtil.HasContentType(contentType, IMConst.IM_LOCATION_ANSWER_VALUE)) {
            msgType = IMConst.IM_LOCATION_ANSWER_VALUE;
        } else if (IMContentUtil.HasContentType(contentType, IMConst.IM_LOCATION_QUIT_VALUE)) {
            msgType = IMConst.IM_LOCATION_QUIT_VALUE;
        } else {
            msgType = IMConst.IM_ERROR_VALUE;
        }
        return msgType;
    }

}
