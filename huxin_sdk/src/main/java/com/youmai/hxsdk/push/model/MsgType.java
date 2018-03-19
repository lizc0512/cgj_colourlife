package com.youmai.hxsdk.push.model;

/**
 * Created by colin on 2017/11/1.
 */

public class MsgType {
    private int msg_type;  // 0 系统公告  1 精选活动  2 玩转呼信  3 行业资讯   必填
    private long last_time;  //最近时间
    private String last_title; //最近标题


    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public long getLast_time() {
        return last_time;
    }

    public void setLast_time(long last_time) {
        this.last_time = last_time;
    }

    public String getLast_title() {
        return last_title;
    }

    public void setLast_title(String last_title) {
        this.last_title = last_title;
    }
}
