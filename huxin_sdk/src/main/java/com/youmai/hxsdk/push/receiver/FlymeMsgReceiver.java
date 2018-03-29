package com.youmai.hxsdk.push.receiver;

import android.content.Context;
import android.util.Log;

import com.meizu.cloud.pushsdk.MzPushMessageReceiver;
import com.meizu.cloud.pushsdk.notification.PushNotificationBuilder;
import com.meizu.cloud.pushsdk.platform.message.PushSwitchStatus;
import com.meizu.cloud.pushsdk.platform.message.RegisterStatus;
import com.meizu.cloud.pushsdk.platform.message.SubAliasStatus;
import com.meizu.cloud.pushsdk.platform.message.SubTagsStatus;
import com.meizu.cloud.pushsdk.platform.message.UnRegisterStatus;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.push.message.MeizuPushReceiver;
import com.youmai.hxsdk.push.message.base.BaseReceiver;


/**
 * 魅族推送广播接收
 * Created by fylder on 2016/12/22.
 * <p>
 * token获取  新版{@link #onRegisterStatus}     旧版{@link #onRegister}
 * 消息接收{@link #onMessage(Context, String)}
 * <p>
 * <p>
 * 消息统一管理{@link MeizuPushReceiver}
 */
public class FlymeMsgReceiver extends MzPushMessageReceiver {

    private BaseReceiver baseReceiver;

    public FlymeMsgReceiver() {
        MeizuPushReceiver.getInstance().initReceiver(this);//注入
    }

    public void setBaseReceiver(BaseReceiver baseReceiver) {
        this.baseReceiver = baseReceiver;
    }

    /**
     * 旧版注册
     * 已不使用
     */
    @Override
    @Deprecated
    public void onRegister(Context context, String pushid) {
        //应用在接受返回的pushid
        if (baseReceiver != null) {
            baseReceiver.register(context, pushid);
        }
    }

    @Override
    public void onMessage(Context context, String s) {
        //接收服务器推送的消息
        if (baseReceiver != null) {
            baseReceiver.getMessage(context, s);
        }

    }

    @Override
    @Deprecated
    public void onUnRegister(Context context, boolean b) {
        //调用PushManager.unRegister(context）方法后，会在此回调反注册状态
        Log.d("push", "onUnRegister:反注册:" + b);
    }

    //设置通知栏小图标
    @Override
    public void onUpdateNotificationBuilder(PushNotificationBuilder pushNotificationBuilder) {
        pushNotificationBuilder.setmStatusbarIcon(R.drawable.img_msg);
    }

    @Override
    public void onPushStatus(Context context, PushSwitchStatus pushSwitchStatus) {
        //检查通知栏和透传消息开关状态回调
    }

    /**
     * 当前版本注册回调token
     */
    @Override
    public void onRegisterStatus(Context context, RegisterStatus registerStatus) {
        //新版订阅回调
        String pushid = registerStatus.getPushId();
        if (baseReceiver != null) {
            baseReceiver.register(context, pushid);
        }
    }

    /**
     * 当前版本反注册回调
     */
    @Override
    public void onUnRegisterStatus(Context context, UnRegisterStatus unRegisterStatus) {
        Log.w("push", "新版反订阅回调:" + unRegisterStatus);
        //新版反订阅回调
        if (!unRegisterStatus.isUnRegisterSuccess()) {
            Log.e("push", "反订阅失效");
        }
    }

    @Override
    public void onSubTagsStatus(Context context, SubTagsStatus subTagsStatus) {
        Log.i(TAG, "onSubTagsStatus " + subTagsStatus);
        //标签回调
    }

    @Override
    public void onSubAliasStatus(Context context, SubAliasStatus subAliasStatus) {
        Log.i(TAG, "onSubAliasStatus " + subAliasStatus);
        //别名回调
    }


}