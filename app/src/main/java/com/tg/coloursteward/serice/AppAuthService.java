package com.tg.coloursteward.serice;

import android.app.Activity;
import android.text.TextUtils;

import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.model.HomeModel;
import com.tg.coloursteward.net.DES;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;

/**
 * 1.0授权
 */
public class AppAuthService implements HttpResponse {
    public Activity context;
    private GetTwoRecordListener<String, String> listener;
    private HomeModel homeModel;

    public AppAuthService(Activity context) {
        this.context = context;
    }

    /**
     * 获取用户应用权限
     */
    public void getAppAuth(final GetTwoRecordListener<String, String> listener) {
        homeModel = new HomeModel(context);
        this.listener = listener;
        String appkey = DES.APP_KEY;
        String app_secret = DES.APP_SECRET;
        String timestamp = HttpTools.getTime();
        String signature = MD5.getMd5Value(appkey + timestamp + app_secret).toLowerCase();
        homeModel.postAppAuth(0, appkey, signature, timestamp, this);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    listener.onFinish(result, "", "");
                }
                break;
        }
    }
}