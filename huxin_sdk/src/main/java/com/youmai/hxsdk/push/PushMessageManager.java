package com.youmai.hxsdk.push;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.push.utils.PushJsonUtils;
import com.youmai.hxsdk.db.bean.ChatMsg;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.entity.EmoInfo;
import com.youmai.hxsdk.interfaces.IFileReceiveListener;
import com.youmai.hxsdk.interfaces.bean.FileBean;
import com.youmai.hxsdk.interfaces.impl.FileReceiveListenerImpl;
import com.youmai.hxsdk.proto.YouMaiChat;
import com.youmai.hxsdk.socket.IMContentType;
import com.youmai.hxsdk.socket.IMContentUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

        ChatMsg msg = new ChatMsg(message);
        //服务已经被拉起，消息在默认接收器会被处理，内容不全如目标号码为空也会导致其它问题   IMMsgManager.getInstance().parseCharMsg(msg);

        final String phone = PushJsonUtils.getValue(message, String.valueOf(IMContentType.CONTENT_PHONE));//对方手机号
        try {
            //获得相应的系统服务
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_IDLE:  //无状态   进入IM界面
                    //TODO 无状态
                    Log.e("TcpClient", "push message:" + message);
//                    Intent intent = new Intent(context, IMConnectionActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtra(IMConnectFragment.DST_PHONE, phone);
//                    context.startActivity(intent);
                    IMMsgManager.getInstance().notifyMsg(msg, true);
                    break;
                case TelephonyManager.CALL_STATE_RINGING:   //被叫响铃状态    需要修改为所有消息均需展示在弹屏上（图片、位置、表情、文件）

                    TimerTask task = new TimerTask() {

                        @Override
                        public void run() {
                            String msgType = PushJsonUtils.getValue(message, String.valueOf(IMContentType.CONTEXT_TEXT_TYPE));//消息类型
                            switch (isType(msgType)) {
                                case 1:
                                    emotionType(context, message);//文本表情
                                    break;
                                case 2:
                                    pictureType(message);//图片
                                    break;
                                case 3:
                                    locationType(message);//地图
                                    break;
                                case 4:
                                    break;
                            }
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, 2000);//延迟300ms触发，要读取监听通话的状态
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:   //被叫接通状态    需要继续展示弹屏，消息展示在弹屏上面
                    TimerTask task2 = new TimerTask() {

                        @Override
                        public void run() {
                            String msgType2 = PushJsonUtils.getValue(message, String.valueOf(IMContentType.CONTEXT_TEXT_TYPE));//消息类型
                            Log.w("push", "msgType2:" + msgType2);
                            switch (isType(msgType2)) {
                                case 1:
                                    emotionType(context, message);//文本表情
                                    break;
                                case 2:
                                    pictureType(message);//图片
                                    break;
                                case 3:
                                    locationType(message);//地图
                                    break;
                                case 4:
                                    break;
                            }
                        }
                    };
                    Timer timer2 = new Timer();
                    timer2.schedule(task2, 2000);//延迟300ms触发，要读取监听通话的状态
                    break;
            }
        } catch (Exception e) {
            Log.w("push", "获取状态异常");
        }
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
        if (typeStr.equals(IMContentUtil.getContentType(0, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_TEXT_VALUE) + "")) {
            iType = 1;
        } else if (typeStr.equals(IMContentUtil.getContentType(0, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_IMAGE_VALUE) + "")) {
            iType = 2;
        } else if (typeStr.equals(IMContentUtil.getContentType(0, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION_VALUE) + "")) {
            iType = 3;
        } else if (typeStr.equals(IMContentUtil.getContentType(0, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_FILE_VALUE) + "")) {
            iType = 4;
        } else {
            iType = 0;
        }
        return iType;
    }

    /**
     * 弹出表情
     */
    private static void emotionType(Context context, String message) {

        String phone = PushJsonUtils.getValue(message, String.valueOf(IMContentType.CONTENT_PHONE));//对方手机号
        String content = PushJsonUtils.getValue(message, String.valueOf(IMContentType.CONTENT_TEXT));//内容
        String msgid = PushJsonUtils.getValue(message, String.valueOf(IMContentType.CONTEXT_MSGID));//用户id

        int userId = getID(msgid);
        if (userId != 0) {
            List<EmoInfo.EmoItem> dataList = new EmoInfo(context).getEmoList();
            for (EmoInfo.EmoItem emoItem : dataList) {

                if (emoItem.getEmoStr().equals(content)) {
                    final IFileReceiveListener listener = FileReceiveListenerImpl.getReceiveListener();
                    final FileBean fileBean = new FileBean().setUserId(userId)
                            .setFileMsgType(ChatMsg.MsgType.TEXT.ordinal())
                            .setDstPhone(phone)
                            .setTextContent(content);
                    // Log.w("push", "listener:" + listener);
                    if (null != listener) {
                        //要在主线程调用
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.w(TAG, "开始弹出表情\tthread:" + Thread.currentThread().getName());
                                listener.onImSuccess(ChatMsg.MsgType.TEXT.ordinal(), fileBean);
                            }
                        });
                    }
                    break;
                }
            }
        }
    }

    /**
     * 弹出图片
     */
    private static void pictureType(String message) {

        String phone = PushJsonUtils.getValue(message, String.valueOf(IMContentType.CONTENT_PHONE));//对方手机号
        String pictureID = PushJsonUtils.getValue(message, String.valueOf(IMContentType.CONTEXT_PICTURE_ID));//内容
        String msgid = PushJsonUtils.getValue(message, String.valueOf(IMContentType.CONTEXT_MSGID));//用户id

        int userId = getID(msgid);
        if (userId != 0) {
            final IFileReceiveListener listener = FileReceiveListenerImpl.getReceiveListener();
            final FileBean fileBean = new FileBean().setUserId(userId)
                    .setFileMsgType(ChatMsg.MsgType.PICTURE.ordinal())
                    .setDstPhone(phone)
                    //.setOriginPath(AppConfig.DOWNLOAD_IMAGE + msg.getMsgContent().getPicture().getPicUrl());//  ? 2017-1-13 17:38:23
                    .setOriginPath(AppConfig.DOWNLOAD_IMAGE + pictureID);
            if (null != listener) {
                //要在主线程调用
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onImSuccess(ChatMsg.MsgType.PICTURE.ordinal(), fileBean);
                    }
                });
            }
        }
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


        int userId = getID(msgid);
        if (userId != 0) {

            final IFileReceiveListener listener = FileReceiveListenerImpl.getReceiveListener();
            // ContentLocation mLocation = msg.getMsgContent().getLocation();
            String mLabelAddress = address;

            final String url = "http://restapi.amap.com/v3/staticmap?location="
                    + longitude + "," + latitude + "&zoom=" + zoomLevel
                    + "&scale=2&size=720*550&traffic=1&markers=mid,0xff0000,A:" + longitude
                    + "," + latitude + "&key=" + AppConfig.staticMapKey;

            final FileBean fileBean = new FileBean().setUserId(userId)
                    .setFileMsgType(ChatMsg.MsgType.LOCATION.ordinal())
                    .setDstPhone(phone)
                    .setLongitude(Double.valueOf(longitude))
                    .setLatitude(Double.valueOf(latitude))
                    .setAddress(mLabelAddress)
                    .setMapUrl(url);
            if (null != listener) {
                //要在主线程调用
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onImSuccess(ChatMsg.MsgType.LOCATION.ordinal(), fileBean);
                    }
                });
            }
        }
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
