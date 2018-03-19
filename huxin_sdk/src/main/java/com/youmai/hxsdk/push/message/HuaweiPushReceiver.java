package com.youmai.hxsdk.push.message;

import android.content.Context;
import android.util.Log;

import com.youmai.hxsdk.push.MorePushManager;
import com.youmai.hxsdk.push.receiver.HuaweiMsgReceiver;
import com.youmai.hxsdk.push.message.base.BaseReceiver;
import com.youmai.hxsdk.utils.StringUtils;

/**
 * 华为推送消息处理
 * Created by fylder on 2017/1/18.
 */

public class HuaweiPushReceiver extends BaseReceiver<HuaweiMsgReceiver> {

    private static HuaweiPushReceiver pushReceiver;

    public static HuaweiPushReceiver getInstance() {
        if (pushReceiver == null) {
            pushReceiver = new HuaweiPushReceiver();
        }
        return pushReceiver;
    }


    @Override
    public void initReceiver(HuaweiMsgReceiver receiver) {
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
        Log.w("push", "huawei push 有消息来啦:" + message);
        MorePushManager.getMessage(context, message);
    }
}
