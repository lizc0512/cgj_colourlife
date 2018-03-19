package com.youmai.hxsdk.interfaces;

import com.youmai.hxsdk.interfaces.bean.FileBean;

/**
 * 作者：create by YW
 * 日期：2016.12.27 11:40
 * 描述：
 */

public interface IFileReceiveListener {
    void onProgress(int type, double progress);
    void onImSuccess(int type, FileBean bean);
    void onImFail(int type, FileBean fileBean);
}
