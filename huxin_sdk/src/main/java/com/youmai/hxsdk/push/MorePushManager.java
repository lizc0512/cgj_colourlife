package com.youmai.hxsdk.push;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.util.Log;

import com.huawei.android.hms.agent.HMSAgent;
import com.meizu.cloud.pushsdk.PushManager;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.push.http.HttpPushManager;
import com.youmai.hxsdk.push.utils.HuaweiPushUtils;
import com.youmai.hxsdk.push.utils.PushPhoneUtils;
import com.youmai.hxsdk.push.utils.PushValueUtils;
import com.youmai.hxsdk.service.HuxinService;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.LogFile;

import java.util.List;


/**
 * 推送管理器,只针对三平台的推送
 * Created by fylder on 2016/12/26.
 */

public class MorePushManager {
    private static final String TAG = MorePushManager.class.getSimpleName();

    /**
     * 使用推送服务
     * 华为开启透传递
     * <p>
     * 注册时使用
     */
    public static void register(Context context) {
        try {
            PushPhoneUtils.ModelType brand = PushPhoneUtils.getBrand();
            switch (brand) {
                case meizu:
                    PushManager.register(context,
                            PushValueUtils.getValue(context, PushValueUtils.FLYME_APPID),
                            PushValueUtils.getValue(context, PushValueUtils.FLYME_APPKEY));
                    break;
                case xiaomi:
                    if (shouldInit(context)) {
                        MiPushClient.registerPush(context,
                                PushValueUtils.getValue(context, PushValueUtils.MI_AppID),
                                PushValueUtils.getValue(context, PushValueUtils.MI_AppKey));
                    }
                    break;
                case huawei:
                    HMSAgent.init((Application) context.getApplicationContext());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 暂时只有华为推送需要连接
     *
     * @param activity
     */
    public static void connect(final Activity activity) {
        try {
            PushPhoneUtils.ModelType brand = PushPhoneUtils.getBrand();
            switch (brand) {
                case meizu:
                    break;
                case xiaomi:
                    break;
                case huawei:
                    HuaweiPushUtils.getIntance().getToken(activity);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 反注册
     * 注销推送服务
     * <p>
     * 账号退出时使用
     */
    public static void unregister(Context context) {
        PushPhoneUtils.ModelType brand = PushPhoneUtils.getBrand();
        switch (brand) {
            case meizu:
                PushManager.unRegister(context,
                        PushValueUtils.getValue(context, PushValueUtils.FLYME_APPID),
                        PushValueUtils.getValue(context, PushValueUtils.FLYME_APPKEY));
                break;
            case xiaomi:
                MiPushClient.unregisterPush(context);
                break;
            case huawei:
                HuaweiPushUtils.getIntance().deleteToken(context);
                break;
        }
    }


    //原进程是否存在，小米推送用到
    private static boolean shouldInit(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 发送token注册
     */
    public static void sendToken(final Context context, final String token) {
        String brand = PushPhoneUtils.getBrand().ordinal() + "";
        HttpPushManager.register(token, brand,
                new HttpPushManager.PushListener() {
                    @Override
                    public void success(String msg) {
                        Log.w("push", "sendToken:" + msg);
                        AppUtils.setStringSharedPreferences(context, "huawei_token", token);
                    }

                    @Override
                    public void fail(String msg) {
                        Log.e("push", "register fail:" + msg);
                    }
                });
    }


    /**
     * 接收到消息
     * <p>
     * 并通知更新ui
     * (启动Service   ps:视情况需要)
     */
    public static void getMessage(final Context context, String message) {

        LogFile.inStance().toFile(message);

        IMMsgManager.instance().parseBulletin(message);

        // 0认为账号退出，消息不做处理
        if (AppUtils.getIntSharedPreferences(context, "uid_sdk", 0) != 0) {
            //启动服务
            Intent intent = new Intent(context.getApplicationContext(), HuxinService.class);
            context.startService(intent);

            PushMessageManager.message(context, message);//推送消息的处理
        }
    }
}
