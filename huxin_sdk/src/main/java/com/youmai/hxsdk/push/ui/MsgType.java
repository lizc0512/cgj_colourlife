package com.youmai.hxsdk.push.ui;

/**
 * Created by colin on 2017/11/23.
 */

public class MsgType {
    int msgType;
    String title;
    long recTime;


    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getRecTime() {
        return recTime;
    }

    public void setRecTime(long recTime) {
        this.recTime = recTime;
    }
}
