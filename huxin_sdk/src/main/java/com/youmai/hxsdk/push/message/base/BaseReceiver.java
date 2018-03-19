package com.youmai.hxsdk.push.message.base;

import android.content.Context;

/**
 * 消息处理抽象类
 * Created by fylder on 2017/1/14.
 */
public abstract class BaseReceiver<T> {

    T receiver;

    public void setReceiver(T receiver) {
        if (this.receiver == null) {
            this.receiver = receiver;
        }
    }

    /**
     * 注入接收器
     */
    public abstract void initReceiver(T receiver);

    /**
     * 注册,也是为了拿到token
     */
    public abstract void register(Context context, String token);

    /**
     * 获取token
     */
    public abstract void getToken(Context context, String token);

    /**
     * 收到消息
     */
    public abstract void getMessage(Context context, String message);

}
