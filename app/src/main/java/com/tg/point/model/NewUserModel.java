package com.tg.point.model;

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
 * @name ${yuansk}
 * @class name：com.user.model
 * @class describe
 * @anthor ${ysk} QQ:827194927
 * @time 2018/2/26 11:29
 * @change
 * @chang time
 * @class describe  4.1.0 与用户改造相关的model
 */

public class NewUserModel extends BaseModel {
    private String sendCodeUrl = "/sms/sendCode";
    private String submitRealUrl = "/user/identity";
    private String getRealTokenUrl = "/user/bizToken";
    private String finishTask = "/user/finishTask";

    public NewUserModel(Context context) {
        super(context);
    }

    /****获取短信或语音验证码**/
    public void getSmsCode(int what, String mobile, int work_type, int sms_type, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", mobile);
        params.put("work_type", work_type);
        params.put("sms_type", sms_type);
        params.put("device_uuid", TokenUtils.getUUID(mContext));
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 13, sendCodeUrl), RequestMethod.POST);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessage(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
                    } else {
                        newHttpResponse.OnHttpResponse(what, "");
                    }
                } else {
                    newHttpResponse.OnHttpResponse(what, "");
                    showErrorCodeMessage(responseCode, response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                newHttpResponse.OnHttpResponse(what, "");
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    /**
     * 提交实名认证
     */
    public void submitRealName(int what, String identity_val, String identity_name, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("identity_val", identity_val);
        params.put("identity_name", identity_name);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, submitRealUrl), RequestMethod.POST);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessageTheme(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
                    } else {
                        newHttpResponse.OnHttpResponse(what, result);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    /**
     * 获取实名认证BizToken
     */
    public void getRealNameToken(int what, final HttpResponse newHttpResponse, boolean loading) {
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, getRealTokenUrl), RequestMethod.GET);
        request(what, request, null, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessageTheme(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                newHttpResponse.OnHttpResponse(what, "");
            }
        }, true, loading);
    }

    /**
     * 获取授权管理详情
     */
    public void finishTask(int what, String type, String come_from, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", type);//2实名认证、3意见反馈
        params.put("come_from", come_from);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, finishTask), RequestMethod.GET);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessageTheme(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
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
