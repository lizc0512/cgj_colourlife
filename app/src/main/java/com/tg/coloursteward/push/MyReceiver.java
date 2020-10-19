package com.tg.coloursteward.push;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.tg.coloursteward.R;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.module.MainActivity;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.user.activity.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends JPushMessageReceiver {
    private static final String TAG = "JPush";
    private Context aContext;
    private LocalBroadcastManager mLocalBroadcastManager;
    private NotificationManager mNotifMan;
    private static int pushId = 0;// 推送通知栏信息的ID

    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        aContext = context;
        String contentType = customMessage.contentType;
        String message = customMessage.message;
        if (!TextUtils.isEmpty(contentType)) {
            if ("c6".equalsIgnoreCase(contentType) && !TextUtils.isEmpty(message)) { //单设备挤出登录
                sendNotification(context, message);
                Intent data6 = new Intent();
                data6.setAction(Contants.APP.ACTION_C6);
                context.sendBroadcast(data6);
            } else {
                sendNotification(context, message);
            }
        }
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
        aContext = context;
        processCustomMessage(context, notificationMessage.notificationExtras);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        aContext = context;
        openNotification(notificationMessage.notificationTitle, notificationMessage.notificationContent,
                notificationMessage.notificationExtras);
    }

    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage notificationMessage) {
        aContext = context;
        super.onNotifyMessageDismiss(context, notificationMessage);
    }

    @Override
    public void onNotifyMessageUnShow(Context context, NotificationMessage notificationMessage) {
        aContext = context;
        super.onNotifyMessageUnShow(context, notificationMessage);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(Context context, String message) {
        int notifysound = 0;
        if (mLocalBroadcastManager == null) {
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
        }
        int icon = R.drawable.logo;
        long when = System.currentTimeMillis();
        NotificationCompat.Builder builder;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "jpush_login";
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, message, importance);
            channel.setDescription(message);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(true);
            channel.setShowBadge(false);
            notificationManager.createNotificationChannel(channel);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        builder.setContentTitle(message);
        builder.setContentText(message);
        builder.setSmallIcon(icon);
        builder.setWhen(when);
        builder.setAutoCancel(true);
        Notification notification = new Notification.Builder(context).build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        if (notifysound == 0) {
            if (SharedPreferencesUtils.getInstance().getBooleanData("VOICE", true)) {
                notification.defaults = Notification.DEFAULT_ALL;
                builder.setDefaults(Notification.DEFAULT_ALL);
            }
        } else {
            notification.sound = null;
            notification.vibrate = null;
        }
        Intent intent;
        PendingIntent pi;
        mNotifMan = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        try {
            intent = new Intent(context, MyReceiver.class);
            intent.setAction(JPushInterface.ACTION_NOTIFICATION_OPENED);
            pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pi);
            Notification notification1 = builder.build();
            mNotifMan.notify(0, notification1);
            mNotifMan.notify(message, ++pushId, notification);
        } catch (Exception e) {

        }
    }

    public void message(Context context, Intent intent) {
        aContext = context;
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            //send the Registration Id to your server...
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            processCustomMessage(context, "bundle");
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            //Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            processCustomMessage(context, "bundle");
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            //Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            Boolean mainCreat = Tools.getMainStatus(aContext);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            //打开自定义的Activity
            if (!mainCreat) {
                startAPP(extras);
            } else {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    Intent intent2 = new Intent(aContext, MainActivity.class);
                    if (StringUtils.isNotEmpty(extras)) {
                        if (null != extraJson && extraJson.length() > 0) {
                            intent2.putExtra(MainActivity.KEY_EXTRAS, extras);
                            intent2.putExtra(MainActivity.ISSHOWPOP, "2");
                        }
                    }
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    aContext.startActivity(intent2);
                } catch (Exception e) {
                    Toast.makeText(aContext, "没有安装", Toast.LENGTH_LONG).show();
                }
            }
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        } else {
        }
    }

    private void openNotification(String title, String message, String extras) {
        Boolean mainCreat = Tools.getMainStatus(aContext);
        //打开自定义的Activity
        if (!mainCreat) {
            startAPP(extras);
        } else {
            try {
                if (null == extras) {
                    extras = "";
                }
                JSONObject extraJson = new JSONObject(extras);
                Intent intent2 = new Intent(aContext, MainActivity.class);
                if (StringUtils.isNotEmpty(extras)) {
                    if (null != extraJson && extraJson.length() > 0) {
                        intent2.putExtra(MainActivity.KEY_EXTRAS, extras);
                        intent2.putExtra(MainActivity.ISSHOWPOP, "2");
                    }
                }
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                aContext.startActivity(intent2);
            } catch (Exception e) {
                startAPP("");
            }
        }
    }

    private void processCustomMessage(Context context, String extras) {
        Boolean mainCreat = Tools.getMainStatus(aContext);
        if (!mainCreat) {

        } else {
            Intent msgIntent = new Intent(MainActivity.ACTION_UPDATE_PUSHINFO);
            if (StringUtils.isNotEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    if (null != extraJson && extraJson.length() > 0) {
                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
                    }
                    context.sendBroadcast(msgIntent);
                } catch (JSONException e) {
                }
            }
        }
    }

    /*
     * 启动一个app
     */
    public void startAPP(String extras) {
        try {
            if (TextUtils.isEmpty(extras)) {
                extras = "{}";
            }
            JSONObject extraJson = new JSONObject(extras);
            Intent intent = new Intent(aContext, LoginActivity.class);
            if (StringUtils.isNotEmpty(extras)) {
                if (null != extraJson && extraJson.length() > 0) {
                    intent.putExtra(MainActivity.KEY_EXTRAS, extras);
                }
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            aContext.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(aContext, "没有安装", Toast.LENGTH_LONG).show();
        }
    }
}
