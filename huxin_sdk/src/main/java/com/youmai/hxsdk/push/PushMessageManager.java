package com.youmai.hxsdk.push;

import android.content.Context;
import android.util.Log;

import com.youmai.hxsdk.im.IMChat;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.proto.YouMaiMsg;
import com.youmai.hxsdk.push.utils.PushJsonUtils;
import com.youmai.hxsdk.socket.IMContentType;
import com.youmai.hxsdk.socket.IMContentUtil;


/**
 * 收到推送消息，对弹屏判断
 * 有ui处理
 * Created by fylder on 2017/1/13.
 */
public class PushMessageManager {

    private static final String TAG = "push";


    /**
     * 推送消息的处理
     * <p>
     * <p>
     * 返回电话状态
     * <p>
     * CALL_STATE_IDLE 无任何状态时
     * CALL_STATE_OFFHOOK 接起电话时
     * CALL_STATE_RINGING 电话进来时
     *
     * @param message 推送传递的消息
     */
    public static void message(final Context context, final String message) {
        Log.e("TcpClient", "push message:" + message);

        IMChat msg = new IMChat(message);

        final String phone = PushJsonUtils.getValue(message, String.valueOf(IMContentType.CONTENT_PHONE));//对方手机号
        //服务已经被拉起，消息在默认接收器会被处理，内容不全如目标号码为空也会导致其它问题   IMMsgManager.getInstance().parseCharMsg(msg);
        IMMsgManager.instance().notifyMsg(msg, true, false);
    }

    /**
     * 判断消息的类型
     *
     * @param typeStr
     * @return 1：TEXT   2：PICTURE   3：LOCATION  4:FILE
     */
    private static int isType(String typeStr) {
        int iType;
        IMContentUtil imContentUtil = new IMContentUtil();
        if (typeStr.equals(IMContentUtil.getContentType(0, YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_TEXT_VALUE) + "")) {
            iType = 1;
        } else if (typeStr.equals(IMContentUtil.getContentType(0, YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_IMAGE_VALUE) + "")) {
            iType = 2;
        } else if (typeStr.equals(IMContentUtil.getContentType(0, YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION_VALUE) + "")) {
            iType = 3;
        } else if (typeStr.equals(IMContentUtil.getContentType(0, YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_FILE_VALUE) + "")) {
            iType = 4;
        } else {
            iType = 0;
        }
        return iType;
    }

    /**
     * 弹出图片
     */
    private static void pictureType(String message) {

        String phone = PushJsonUtils.getValue(message, String.valueOf(IMContentType.CONTENT_PHONE));//对方手机号
        String pictureID = PushJsonUtils.getValue(message, String.valueOf(IMContentType.CONTEXT_PICTURE_ID));//内容
        String msgid = PushJsonUtils.getValue(message, String.valueOf(IMContentType.CONTEXT_MSGID));//用户id

    }

    /**
     * 弹出地图
     */
    private static void locationType(String message) {

        String phone = PushJsonUtils.getValue(message, String.valueOf(IMContentType.CONTENT_PHONE));//对方手机号
        String msgid = PushJsonUtils.getValue(message, String.valueOf(IMContentType.CONTEXT_MSGID));//用户id

        String longitude = PushJsonUtils.getValue(message, String.valueOf(IMContentType.CONTEXT_LONGITUDE));
        String latitude = PushJsonUtils.getValue(message, String.valueOf(IMContentType.CONTEXT_LAITUDE));
        String zoomLevel = PushJsonUtils.getValue(message, String.valueOf(IMContentType.CONTEXT_SCALE));
        String address = PushJsonUtils.getValue(message, String.valueOf(IMContentType.CONTEXT_LABEL));


    }

    private static int getID(String idStr) {
        int id;
        try {
            id = Integer.valueOf(idStr);
        } catch (Exception e) {
            id = 0;
        }
        return id;
    }

}
