package com.youmai.hxsdk.im.cache;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-12-06 15:52
 * Description:
 */
public interface JsonFormat<T> extends IProtoType {
    String toJson();

    T fromJson(String jsonStr);
}
