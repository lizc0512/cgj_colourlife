package com.youmai.hxsdk.interfaces.impl;

import com.youmai.hxsdk.interfaces.IFileSendListener;

/**
 * 作者：create by YW
 * 日期：2016.12.26 14:28
 * 描述：
 */

public class FileSendListenerImpl {

    private static IFileSendListener listener;

    public static void setListener(IFileSendListener lis) {
        listener = lis;
    }

    public static IFileSendListener getListener() {
        return listener;
    }


}
