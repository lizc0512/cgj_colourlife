package com.youmai.hxsdk.push.receiver;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

import com.huawei.hms.support.api.push.PushReceiver;
import com.youmai.hxsdk.push.message.HuaweiPushReceiver;
import com.youmai.hxsdk.push.message.base.BaseReceiver;


/**
 * 华为推送广播接收
 * 接收Push所有消息的广播接收器
 * Created by fylder on 2016/12/26.
 * <p>
 * token获取{@link #onToken}
 * 消息接收{@link #onPushMsg(Context, byte[], Bundle)}
 * <p>
 * <p>
 * 消息统一管理{@link HuaweiPushReceiver}
 */
public class HuaweiMsgReceiver extends PushReceiver {

    private BaseReceiver baseReceiver;

    public HuaweiMsgReceiver() {
        HuaweiPushReceiver.getInstance().initReceiver(this);//注入
    }

    public void setBaseReceiver(BaseReceiver baseReceiver) {
        this.baseReceiver = baseReceiver;
    }

    @Override
    public void onToken(Context context, String token, Bundle extras) {
        if (baseReceiver != null) {
            baseReceiver.getToken(context, token);
        }
    }

    /**
     * 透传消息
     */
    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        try {
            //String content = Thread.currentThread().getName() + "\t收到一条消息： " + new String(msg, "UTF-8");
            if (baseReceiver != null) {
                baseReceiver.getMessage(context, new String(msg, "UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void onEvent(Context context, Event event, Bundle extras) {
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
            String content = "receive extented notification message: " + extras.getString(BOUND_KEY.pushMsgKey);
            // Log.d(HuaweiPushApiExample.TAG, content);
        }
        super.onEvent(context, event, extras);
    }

    @Override
    public void onPushState(Context context, boolean pushState) {
        try {
            String content = "The current push status： " + (pushState ? "Connected" : "Disconnected");
            // Log.d(HuaweiPushApiExample.TAG, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
