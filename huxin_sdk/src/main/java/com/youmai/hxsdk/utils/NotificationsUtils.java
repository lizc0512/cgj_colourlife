package com.youmai.hxsdk.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.youmai.hxsdk.R;

/**
 * 通知
 * Created by fylder on 2017/2/10.
 */

public class NotificationsUtils {

    static NotificationsUtils noticeManager;
    private static NotificationManager mNotificationManager;

    public NotificationsUtils() {

    }

    public static NotificationsUtils getInstance() {
        if (noticeManager == null) {
            noticeManager = new NotificationsUtils();
        }
        return noticeManager;
    }

    /**
     * 名片交换的请求通知
     *
     * @param content 名片内容
     */
    public void showBizCard(Context context, String content) {


    }

    /**
     * 收到对方同意交换的通知
     *
     * @param content 名片内容
     */
    public void getBizCard(Context context, String content) {


    }
}
