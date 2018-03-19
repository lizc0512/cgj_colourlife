package com.youmai.hxsdk.im;

import com.youmai.hxsdk.db.bean.CacheMsgBean;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-12-06 16:32
 * Description:
 */
public interface IMMsgCallback {

     void onCallback(CacheMsgBean cacheMsgBean);
}
