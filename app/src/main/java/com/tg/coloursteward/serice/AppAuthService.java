package com.tg.coloursteward.serice;

import android.app.Activity;
import android.os.Message;

import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.net.DES;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;

/**
 * 1.0授权
 */
public class AppAuthService implements MessageHandler.ResponseListener {
    public Activity context;
    private MessageHandler msgHand;
    private GetTwoRecordListener<String, String> listener;

    public AppAuthService(Activity context) {
        this.context = context;
        msgHand = new MessageHandler(context);
        msgHand.setResponseListener(this);
    }

    /**
     * 获取用户应用权限
     */
    public void getAppAuth(final GetTwoRecordListener<String, String> listener) {
        this.listener = listener;
        try {
            String appkey = DES.APP_KEY;
            String app_secret = DES.APP_SECRET;
            String timestamp = HttpTools.getTime();
            String signature = MD5.getMd5Value(appkey + timestamp + app_secret).toLowerCase();
            RequestConfig config = new RequestConfig(context, 0);
            config.handler = msgHand.getHandler();
            RequestParams params = new RequestParams();
            params.put("appkey", appkey);
            params.put("signature", signature);
            params.put("timestamp", timestamp);
            HttpTools.httpPost(Contants.URl.URL_ICETEST, "/jqfw/app/auth", config, params);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }


    @Override
    public void onRequestStart(Message msg, String hintString) {
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        int code = HttpTools.getCode(jsonString);
        if (code == 0) {
            if (listener != null) {
                listener.onFinish(jsonString, "", "");
            }
        } else {
            if (listener != null) {
//                listener.onFailed("获取认证参数失败！");
            }
        }
    }

    @Override
    public void onFail(Message msg, String hintString) {

    }
}