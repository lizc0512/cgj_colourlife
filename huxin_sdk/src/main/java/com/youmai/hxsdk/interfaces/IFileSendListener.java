package com.youmai.hxsdk.interfaces;

import com.youmai.hxsdk.interfaces.bean.FileBean;

/**
 * 作者：create by YW
 * 日期：2016.12.26 14:15
 * 描述：
 */

public interface IFileSendListener {
    void onProgress(int type, double progress, String path);
    void onImSuccess(int type, FileBean bean);
    void onImFail(int type, FileBean fileBean);
    void onImNotUser(int type,long msgId);
}
