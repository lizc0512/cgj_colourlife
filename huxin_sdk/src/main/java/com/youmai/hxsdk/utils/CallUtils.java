package com.youmai.hxsdk.utils;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.youmai.hxsdk.AcceptCallActivity;
import com.youmai.hxsdk.db.helper.BackGroundJob;
import com.youmai.hxsdk.entity.CallInfo;
import com.youmai.hxsdk.view.full.FloatViewUtil;

import java.lang.reflect.Method;

public class CallUtils {


    private CallUtils() {
        throw new AssertionError();
    }

    public static void acceptCall(Context context) {
        Intent intent = new Intent(context, AcceptCallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    public static void endCall(Context context) {
        try {
            //FloatViewUtil.instance().startCallHook(CallInfo.IsMOCalling());

            if (Build.MODEL.equals("vivo Y51A")) { // 屏蔽 vivo Y51A
                FloatViewUtil.instance().hideFloatView();
            }

            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class clazz = Class.forName(telephonyManager.getClass().getName());
            Method method = clazz.getDeclaredMethod("getITelephony");
            method.setAccessible(true);
            ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);
            telephonyService.endCall();

            FloatViewUtil.instance().setOutgoingCallTime(0);
            FloatViewUtil.instance().hideFloatViewDelay(2000); //防止弹屏卡死用户手机

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 是否支持MT（被叫）通话前,呼信全屏
     *
     * @return
     */
    public static boolean isSupportMTBefore(Context context) {
        return BackGroundJob.instance().isSupportMTBefore(context);
    }


    /**
     * 是否支持MO（主叫）通话前,呼信全屏
     *
     * @return
     */
    public static boolean isSupportMOBefore(Context context) {
        boolean res = false;

        if (AppUtils.isCanUseSim(context)
                && !isMutilSim()) {
            res = true;
        }

        return res;
    }


    /**
     * 不支持使用反射调用 endCall 挂断电话的设备
     *
     * @return
     */
    private static boolean isNotSupportEndCall() {
        boolean res = false;
        String model = android.os.Build.MODEL;
        switch (model) {
            case "OPPO R9m":
            case "DOOV A6":
            case "vivo X7":
                res = true;
                break;
        }

        String manufacturer = Build.MANUFACTURER;
        switch (manufacturer) {
            case "Meizu":  //不支持魅族网络电话
                res = true;
                break;
        }


        return res;
    }

    /**
     * 是否正在通话中
     *
     * @param context
     * @return
     */
    public static boolean isInCall(Context context) {
        boolean res = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
            if (telecomManager.isInCall()) {
                res = true;
            }
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK) {
                res = true;
            }
        }
        return res;
    }


    /**
     * MO 呼出时候是否出现双卡选择框机型
     *
     * @return
     */
    private static boolean isMutilSim() {
        boolean res = false;
        String model = android.os.Build.MODEL;
        switch (model) {
            case "Le X620":
            case "R9":
                res = true;
                break;
        }

        return res;
    }


    /**
     * 是否打开扬声器
     */
    public static boolean isSpeakerOn(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return audioManager.isSpeakerphoneOn();
    }


    /**
     * 打开扬声器
     */
    public static void openSpeaker(Context context) {
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setSpeakerphoneOn(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭扬声器
     */
    public static void closeSpeaker(Context context) {
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setSpeakerphoneOn(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 静音 -- 0
     *
     * @param context
     */
    public static void setQuite(Context context) {
        AudioManager mAudioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setRingerMode(0);
        //	mAudioManager.setMicrophoneMute(!mAudioManager.isMicrophoneMute());
    }

}
