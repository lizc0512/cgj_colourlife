package com.youmai.hxsdk.interfaces;


import com.youmai.hxsdk.db.bean.ChatMsg;

/**
 * 作者：create by YW
 * 日期：2016.12.27 11:40
 * 描述：
 */

public interface OnChatMsg {
    void onCallback(ChatMsg msg);
}
