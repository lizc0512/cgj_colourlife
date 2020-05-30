package com.tg.setting.model;

import android.content.Context;
import android.text.TextUtils;

import com.tg.coloursteward.BuildConfig;
import com.tg.coloursteward.baseModel.BaseModel;
import com.tg.coloursteward.baseModel.HttpListener;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.baseModel.RequestEncryptionUtils;
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
    private String homeDelAllMsgUrl = "/app/home/delUserMsg";
    private String inviteShareUrl = "/app/home/share/info";
    private String setLoginPwdUrl = "/app/setPassword";

    public SettingModel(Context context) {
        super(context);
    }

    /**
     * @param what
     * @param type         类型。1：安卓，2：ios
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
                        }
                    } else {
                        if (isloading) {
                            showErrorCodeMessage(response);
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
            }
        }, true, isloading);

    }

    /**
     * 清空用户首页消息列表
     *
     * @param what
     * @param httpResponse
     */
    public void postDelAllMsg(int what, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(
                mContext, 0, homeDelAllMsgUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    }
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
            }
        }, true, false);
    }

    /**
     * 分享信息获取
     *
     * @param what
     * @param httpResponse
     */
    public void getShareInfo(int what, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(
                mContext, 0, inviteShareUrl), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessageTheme(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
            }
        }, true, false);
    }

    /**
     * 设置登陆密码
     *
     * @param what
     * @param password
     * @param httpResponse
     */
    public void postLoginPwd(int what, String password, HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("password", password);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(
                mContext, 5, setLoginPwdUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
                    }
                } else {
                    showErrorCodeMessage(response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }
}
