package com.youmai.hxsdk.push.http;

import android.content.Context;

import com.youmai.hxsdk.HuxinSdkManager;

import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.proto.YouMaiMsg;
import com.youmai.hxsdk.push.http.entity.PushMsgResult;
import com.youmai.hxsdk.push.http.entity.PushRegisterResult;
import com.youmai.hxsdk.socket.IMContentUtil;
import com.youmai.hxsdk.utils.GsonUtil;

/**
 * 推送与应用服务的请求
 * Created by fylder on 2017/1/4.
 */

public class HttpPushManager {


    /**
     * 注册token
     *
     * @param token 口令     "*********" or "null"   "null"说明用户退出
     * @param brand 品牌
     */
    public static void register(String token, String brand, final PushListener listener) {
        IPostListener iPostListener = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                PushRegisterResult resp = GsonUtil.parse(response, PushRegisterResult.class);
                if (resp != null){
                    if(resp.isSuccess()) {
                        listener.success(resp.getM());
                    }else{
                        listener.fail(resp.getM());
                    }
                } else {
                    listener.fail("推送失败！");// srsm resp.getM());
                }
            }
        };
        //HuxinSdkManager.instance().pushRegister(token, brand, iPostListener);

    }

    /**
     * 推送消息
     *
     * @param phoneNum    本机手机号
     * @param targetPhone 目标手机号
     * @param message     消息内容
     * @param type        消息类型
     */
    public static void pushMsg(String phoneNum, String targetPhone, String message, int type,
                               final PushListener listener) {

        IPostListener iPostListener = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                PushMsgResult resp = GsonUtil.parse(response, PushMsgResult.class);
                if (resp == null && listener != null) {
                    listener.fail("推送消息有误：");
                    return;
                }
                if (resp != null) {
                    String msg = resp.getM() == null ? "" : resp.getM();
                    String s = resp.getS();
                    if ("1".equals(s)) {
                        listener.success(response);
                    } else {
                        listener.fail(msg);
                    }
                }
            }
        };

        //HuxinSdkManager.instance().pushMsg(phoneNum, targetPhone, message, type, iPostListener);
    }

    /**
     * 针对发送文本
     *
     * @param targetPhone 目标手机号
     * @param content     文本内容
     */
    public static void pushMsgForText(Context context, int msgid, String targetPhone, String content, PushListener listener) {
        String myPhone = HuxinSdkManager.instance().getPhoneNum();
        IMContentUtil imContentUtil = new IMContentUtil();
        int type = IMContentUtil.getContentType(0, YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_TEXT_VALUE);
        imContentUtil.appendMsgid(msgid + "");
        imContentUtil.appendPhone(myPhone);
        imContentUtil.appendTextType(type + "");
        imContentUtil.appendText(content);
        String pushStr = imContentUtil.serializeToString();                 //转换推送message参数的格式
        pushMsg(myPhone, targetPhone, pushStr, type, listener);    //发送
    }

    /**
     * 针对发送名片文本
     *
     * @param targetPhone 目标手机号
     * @param content     文本内容
     */
    public static void pushMsgForCardText(int msgId, String targetPhone, String content, PushListener listener) {
        String myPhone = HuxinSdkManager.instance().getPhoneNum();
        IMContentUtil imContentUtil = new IMContentUtil();
        int type = IMContentUtil.getContentType(0, YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_BIZCARD_VALUE);
        imContentUtil.appendMsgid(msgId + "");
        imContentUtil.appendPhone(myPhone);
        imContentUtil.appendTextType(type + "");
        imContentUtil.appendText(content);
        String pushStr = imContentUtil.serializeToString();                 //转换推送message参数的格式
        pushMsg(myPhone, targetPhone, pushStr, type, listener);    //发送
    }

    /**
     * 针对发送位置，经纬度及地址
     *
     * @param targetPhone 目标手机号
     * @param longitude
     * @param latitude
     * @param zoomLevel
     * @param address
     */
    public static void pushMsgForLocation(int msgId, String targetPhone,
                                          double longitude, double latitude,
                                          int zoomLevel, String address,
                                          PushListener listener) {
        String myPhone = HuxinSdkManager.instance().getPhoneNum();
        IMContentUtil imContentUtil = new IMContentUtil();
        int type = IMContentUtil.getContentType(0, YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_LOCATION_VALUE);
        imContentUtil.appendMsgid(msgId + "");
        imContentUtil.appendPhone(myPhone);
        imContentUtil.appendTextType(type + "");
        imContentUtil.appendLongitude(longitude + "");
        imContentUtil.appendLaitude(latitude + "");
        imContentUtil.appendScale(zoomLevel + "");
        imContentUtil.appendLabel(address);
        String pushStr = imContentUtil.serializeToString();                 //转换推送message参数的格式
        pushMsg(myPhone, targetPhone, pushStr, type, listener);    //发送
    }

    /**
     * 针对发送图片
     *
     * @param targetPhone 目标手机号
     * @param fileId      图片文件标识
     */
    public static void pushMsgForPicture(int msgId, String targetPhone, String fileId, PushListener listener) {
        String myPhone = HuxinSdkManager.instance().getPhoneNum();
        IMContentUtil imContentUtil = new IMContentUtil();
        int type = IMContentUtil.getContentType(0, YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_IMAGE_VALUE);
        imContentUtil.appendMsgid(msgId + "");
        imContentUtil.appendPhone(myPhone);
        imContentUtil.appendTextType(type + "");
        imContentUtil.appendPictureId(fileId);
        String pushStr = imContentUtil.serializeToString();                 //转换推送message参数的格式
        pushMsg(myPhone, targetPhone, pushStr, type, listener);    //发送
    }

    /**
     * 针对发送语音
     *
     * @param targetPhone 目标手机号
     * @param fileId      图片文件标识
     * @param secondTimes 语音时间
     */
    public static void pushMsgForAudio(int msgId, String targetPhone, String fileId, String secondTimes, PushListener listener) {
        String myPhone = HuxinSdkManager.instance().getPhoneNum();
        IMContentUtil imContentUtil = new IMContentUtil();
        int type = IMContentUtil.getContentType(0, YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_AUDIO_VALUE);
        imContentUtil.appendMsgid(msgId + "");
        imContentUtil.appendPhone(myPhone);
        imContentUtil.appendTextType(type + "");
        imContentUtil.appendAudioId(fileId);
        imContentUtil.appendBarTime(secondTimes);
        String pushStr = imContentUtil.serializeToString();                 //转换推送message参数的格式
        pushMsg(myPhone, targetPhone, pushStr, type, listener);    //发送
    }

    /**
     * 针对发送大文件
     *
     * @param targetPhone 目标手机号
     * @param fileId      文件标识
     * @param fileName    文件名字
     * @param fileSize    文件大小
     */
    public static void pushMsgForBigFile(int msgId, String targetPhone, String fileId, String fileName, String fileSize, PushListener listener) {
        String myPhone = HuxinSdkManager.instance().getPhoneNum();
        IMContentUtil imContentUtil = new IMContentUtil();
        int type = IMContentUtil.getContentType(0, YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_FILE_VALUE);
        imContentUtil.appendMsgid(msgId + "");
        imContentUtil.appendPhone(myPhone);
        imContentUtil.appendTextType(type + "");
        imContentUtil.appendBigFileId(fileId, fileName, fileSize);
        imContentUtil.appendPictureId(fileId);
        String pushStr = imContentUtil.serializeToString();                 //转换推送message参数的格式
        pushMsg(myPhone, targetPhone, pushStr, type, listener);    //发送
    }

    /**
     * 推送结果监听器
     */
    public interface PushListener {

        void success(String msg);

        void fail(String msg);
    }
}
