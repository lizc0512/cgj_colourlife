package com.youmai.hxsdk.http;

/**
 * Created by colin on 2016/8/5.
 */

/**
 * 客户端升级下载监听器
 *
 * @author Jason
 */
public interface DownLoadingListener {

    void onProgress(int cur, int total);

    void downloadFail(String err);

    void downloadSuccess(String path);

}