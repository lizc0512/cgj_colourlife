package com.tg.setting.model;

import android.content.Context;
import android.text.TextUtils;

import com.tg.coloursteward.BuildConfig;
import com.tg.coloursteward.baseModel.BaseModel;
import com.tg.coloursteward.baseModel.HttpListener;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.baseModel.RequestEncryptionUtils;
import com.tg.coloursteward.info.UserInfo;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * @name ${lizc}
 * @class name：com.tg.setting.model
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/6/21 10:43
 * @change
 * @chang time
 * @class describe
 */
public class SettingModel extends BaseModel {
    private String updateUrl = "/get/version";
    private String deleteAllUrl = "/push2/homepush/deleteall";

    public SettingModel(Context context) {
        super(context);
    }

    /**
     * @param what
     * @param type
     * @param isloading
     * @param httpResponse 检测升级
     */
    public void getUpdate(int what, String type, boolean isloading, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("app", "cgj");
        params.put("type", type);
        params.put("version", BuildConfig.VERSION_NAME);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 6, updateUrl), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        int code = showSuccesResultMessage(result);
                        if (code == 0) {
                            httpResponse.OnHttpResponse(what, result);
                        } else {
                            showErrorCodeMessage(response);
                        }
                    } else {
                        showErrorCodeMessage(response);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, isloading);

    }

    /**
     * @param what
     * @param username
     * @param source
     * @param httpResponse 清空首页消息列表
     */
    public void deleteAllNotice(int what, String username, int source, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("source", source);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 4, deleteAllUrl), RequestMethod.DELETE);
        request(what, request, RequestEncryptionUtils.getIceMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        int code = showSuccesResultMessage(result);
                        if (code == 0) {
                            httpResponse.OnHttpResponse(what, result);
                        } else {
                            showErrorCodeMessage(response);
                        }
                    } else {
                        showErrorCodeMessage(response);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);

    }
}
