package com.tg.coloursteward.model;

import android.content.Context;

import com.tg.coloursteward.baseModel.BaseModel;
import com.tg.coloursteward.baseModel.HttpListener;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.baseModel.RequestEncryptionUtils;
import com.tg.coloursteward.util.TokenUtils;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * @name ${lizc}
 * @class name：com.BeeFramework.model
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/1/24 10:42
 * @change
 * @chang time
 * @class describe
 */
public class H5OauthModel extends BaseModel {
    private String aouthUrl = "app/cgj/info";
    private String applicationUrl = "app/cgj/oauth";

    public H5OauthModel(Context context) {
        super(context);
    }

    public void getAppInfo(int what, String app_id, String response_type, String domain, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("app_id", app_id);
        params.put("response_type", response_type);
        params.put("domain", domain);
        params.put("device_uuid", TokenUtils.getUUID(mContext));
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 7, aouthUrl), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessage(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
                    } else {
                        showErrorCodeMessage(responseCode, response);
                    }
                } else {
                    showErrorCodeMessage(responseCode, response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, false, true);
    }


    /**
     * 获取鉴权code/access_token
     *
     * @param what
     * @param app_id
     * @param newHttpResponse
     */
    public void getApplicationOauth(int what, String app_id, String response_type, String domain, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("app_id", app_id);
        params.put("response_type", response_type);//	code/access_token
        params.put("domain", domain);//	code/access_token
        params.put("scope", "[]");
        params.put("device_uuid", TokenUtils.getUUID(mContext));
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 7, applicationUrl), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessage(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
                    } else {
                        showErrorCodeMessage(responseCode, response);
                    }
                } else {
                    showErrorCodeMessage(responseCode, response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, false, true);
    }
}
