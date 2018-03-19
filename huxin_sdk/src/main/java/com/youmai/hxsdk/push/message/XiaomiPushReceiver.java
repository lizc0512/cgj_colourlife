package com.youmai.hxsdk.push.message;

import android.content.Context;
import android.util.Log;

import com.youmai.hxsdk.push.MorePushManager;
import com.youmai.hxsdk.push.receiver.XiaomiMsgReceiver;
import com.youmai.hxsdk.push.message.base.BaseReceiver;
import com.youmai.hxsdk.utils.StringUtils;

/**
 * 小米推送消息处理
 * Created by fylder on 2017/1/18.
 */

public class XiaomiPushReceiver extends BaseReceiver<XiaomiMsgReceiver> {

    private static XiaomiPushReceiver pushReceiver;

    public static XiaomiPushReceiver getInstance() {
        if (pushReceiver == null) {
            pushReceiver = new XiaomiPushReceiver();
        }
        return pushReceiver;
    }


    @Override
    public void initReceiver(XiaomiMsgReceiver receiver) {
        receiver.setBaseReceiver(this);
        setReceiver(receiver);
    }

    @Override
    public void register(Context context, String token) {
        if (!StringUtils.isEmpty(token)) {
            MorePushManager.sendToken(context, token);//向服务端发送token
        }
    }

    @Override
    public void getToken(Context context, String token) {
        if (!StringUtils.isEmpty(token)) {
            MorePushManager.sendToken(context, token);//向服务端发送token
        }
    }

    @Override
    public void getMessage(Context context, String message) {
        Log.w("push", "xiaomi push 有消息来啦:" + message);
        MorePushManager.getMessage(context, message);
    }
}
