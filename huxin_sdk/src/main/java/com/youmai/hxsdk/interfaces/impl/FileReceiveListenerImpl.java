package com.youmai.hxsdk.interfaces.impl;

import com.youmai.hxsdk.interfaces.IFileReceiveListener;

/**
 * 作者：create by YW
 * 日期：2016.12.27 11:42
 * 描述：
 */
public class FileReceiveListenerImpl {

    private static IFileReceiveListener listener;

    public static void setReceiveListener(IFileReceiveListener lis) {
        listener = lis;
    }

    public static IFileReceiveListener getReceiveListener() {
        return listener;
    }
}
