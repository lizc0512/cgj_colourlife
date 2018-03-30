package com.youmai.hxsdk.socket;

import android.util.Log;

public abstract class ReceiveListener {

    public static final String TAG = "TcpClient";

    public static final int SOCKET_TIMEOUT = 1;

    private Runnable runnable;

    private String tarPhone;
    private String content;

    public ReceiveListener() {

        runnable = new Runnable() {
            @Override
            public void run() {
                onError(ReceiveListener.SOCKET_TIMEOUT);
            }
        };
    }


    public abstract void OnRec(PduBase pduBase);


    public void setTarPhone(String tarPhone) {
        this.tarPhone = tarPhone;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void onError(int errCode) {
        Log.e(TAG, "socket send error!");
        //HuxinSdkManager.instance().postSendImStatistics(-1, tarPhone, content, -1);
    }


    public Runnable getRunnable() {
        return runnable;
    }


}
