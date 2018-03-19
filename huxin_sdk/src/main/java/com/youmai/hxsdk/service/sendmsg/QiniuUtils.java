package com.youmai.hxsdk.service.sendmsg;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.entity.FileToken;
import com.youmai.hxsdk.entity.UploadFile;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.LogFile;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fylder on 2017/11/9.
 */

public class QiniuUtils {

    // 初始化、执行上传
    private volatile boolean isCancelled = false;
    private volatile boolean isSend = false;
    private UploadManager uploadManager;

    public QiniuUtils() {
        uploadManager = new UploadManager();
    }

    /**
     * post文件到七牛
     *
     * @param path
     * @param desPhone
     * @param progressHandler
     * @param postFile
     */
    public void postFileToQiNiu(final String path, final String desPhone,
                                final UpProgressHandler progressHandler,
                                final PostFile postFile) {

        postFileToQiNiu(new File(path), desPhone, progressHandler, postFile);
    }


    /**
     * post文件到七牛
     *
     * @param file
     * @param desPhone
     * @param progressHandler
     * @param postFile
     */
    public void postFileToQiNiu(final File file, final String desPhone,
                                final UpProgressHandler progressHandler,
                                final PostFile postFile) {
        IPostListener callback = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                FileToken resp = GsonUtil.parse(response, FileToken.class);
                if (resp == null) {
                    if (null != postFile) {
                        postFile.fail("get token  fail");
                    }
                    return;
                }
                if (resp.isSucess()) {
                    String fidKey = resp.getD().getFid();
                    String token = resp.getD().getUpToken();
                    Map<String, String> params = new HashMap<>();
                    params.put("x:type", "2");
                    params.put("x:msisdn", HuxinSdkManager.instance().getPhoneNum());
                    UploadOptions options = new UploadOptions(params, null, false, progressHandler,
                            new UpCancellationSignal() {
                                @Override
                                public boolean isCancelled() {
                                    isSend = true;
                                    return isCancelled;
                                }
                            });
                    UpCompletionHandler completionHandler = new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject response) {
                            if (response != null) {
                                isSend = false;
                                UploadFile resp = GsonUtil.parse(response.toString(), UploadFile.class);
                                if (resp == null) {
                                    if (null != postFile) {
                                        postFile.fail("upload file to qiniu fail");
                                    }
                                    return;
                                }
                                if (resp.isSucess()) {
                                    String fileId = resp.getD().getFileid();
                                    if (null != postFile) {
                                        postFile.success(fileId, desPhone);
                                    }
                                }
                            } else {
                                //快速连发图片会得到上一条response null
                                //info:{ResponseInfo---error:cancelled by user}
                                postFile.fail("response is null cause of cancelled by user");
                            }

                        }
                    };
                    uploadManager.put(file, fidKey, token, completionHandler, options);
                } else {
                    String log = resp.getM();
                    LogFile.inStance().toFile(log);
                }
            }

        };
        HuxinSdkManager.instance().getUploadFileToken(callback);
    }
}
