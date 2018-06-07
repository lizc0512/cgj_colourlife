package com.youmai.hxsdk.im;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.chat.MsgContent;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.proto.YouMaiMsg;
import com.youmai.hxsdk.socket.IMContentType;

/**
 * 作者：create by YW
 * 日期：2018.03.27 15:00
 * 描述：
 */

public class IMChat {

    private int mMsgType;
    private String mJsonBody;
    private MsgContent mContent;

    private YouMaiMsg.MsgData mImChat;
    private CacheMsgBean mMsgBean;

    public IMChat(YouMaiMsg.MsgData imChat, boolean isGroup) {
        mImChat = imChat;
        mJsonBody = imChat.getMsgContent();
        mMsgType = imChat.getContentType().getNumber();
        mContent = new MsgContent(mMsgType, mJsonBody);
        updateCacheBean(imChat, isGroup);
    }

    public IMChat(String pushMsg) {
        /*String srcPhone = PushJsonUtils.getValue(pushMsg, String.valueOf(IMContentType.CONTENT_PHONE));//对方手机号
        String type = PushJsonUtils.getValue(pushMsg, String.valueOf(IMContentType.CONTEXT_TEXT_TYPE));//对方手机号

        mMsgType = Integer.parseInt(type);
        mJsonBody = pushMsg;
        mContent = new MsgContent(mMsgType, mJsonBody);*/
    }


    private void updateCacheBean(YouMaiMsg.MsgData imChat, boolean isGroup) {
        mMsgBean = new CacheMsgBean();
        mMsgBean.setSenderUserId(imChat.getSrcUserId())
                .setSenderSex(imChat.getSrcSex())
                .setSenderMobile(imChat.getSrcMobile())
                .setSenderAvatar(imChat.getSrcAvatar())
                .setSenderRealName(imChat.getSrcRealname())

                .setTargetUserName(imChat.getSrcUserName())
                .setTargetAvatar(imChat.getSrcAvatar())

                //.setMsgTime(System.currentTimeMillis())
                .setMsgTime(imChat.getCreateTime())
                .setMsgStatus(CacheMsgBean.RECEIVE_UNREAD)
                .setMsgId(imChat.getMsgId())
                .setContentJsonBody(imChat.getMsgContent());

        if (isGroup) {
            mMsgBean.setGroupId(imChat.getGroupId())
                    .setTargetUuid(imChat.getGroupId() + "")
                    .setReceiverUserId(HuxinSdkManager.instance().getUuid())
                    .setTargetName(imChat.getGroupName());
        } else {
            mMsgBean.setTargetUuid(imChat.getSrcUserId())
                    .setReceiverUserId(imChat.getDestUserId())
                    .setTargetName(imChat.getSrcRealname());
        }

    }


    /**
     * @return
     */
    public MsgContent getContent() {
        return mContent;
    }

    /**
     * 消息类型
     *
     * @return
     */
    public int getMsgType() {
        return mMsgType;
    }


    public YouMaiMsg.MsgData getImChat() {
        return mImChat;
    }

    public CacheMsgBean getMsgBean() {
        return mMsgBean;
    }
}
