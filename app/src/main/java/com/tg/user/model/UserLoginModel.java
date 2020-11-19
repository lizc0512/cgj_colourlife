package com.tg.user.model;

import android.content.Context;
import android.text.TextUtils;

import com.tg.coloursteward.baseModel.BaseModel;
import com.tg.coloursteward.baseModel.HttpListener;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.baseModel.RequestEncryptionUtils;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.user.entity.Oauth2Entity;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @name ${lizc}
 * @class name：com.colourlife.user.model
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/9/18 15:42
 * @change
 * @chang time
 * @class describe
 */
public class UserLoginModel extends BaseModel {
    private String sendCodeUrl = "/app/smsCode";
    private String bindwxUrl = "/app/bind/wechat";
    private String registerUrl = "/app/register";
    private String geetStartUrl = "/app/home/captcha/start";
    private String geetVerifyUrl = "/app/home/login/verify";

    public UserLoginModel() {
    }

    public UserLoginModel(Context context) {
        super(context);
    }

    /**
     * 获取短信或语音验证码
     *
     * @param what
     * @param mobile
     * @param work_type
     * @param sms_type
     * @param newHttpResponse
     */
    public void getSmsCode(int what, String mobile, int work_type, int sms_type, boolean isLoading, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", mobile);
        params.put("work_type", work_type);//1:注册，2：找回密码，3:验证登录，4：更换手机号码，6：绑定微信
        params.put("sms_type", sms_type);//1：短信验证码，2：语音验证码
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 5, sendCodeUrl),
                RequestMethod.POST);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessage(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
                    }
                } else {
                    showErrorCodeMessage(responseCode, response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {

                showExceptionMessage(what, response);
            }
        }, true, isLoading);
    }

    /**
     * 微信授权关联手机号
     *
     * @param what
     * @param openid
     * @param unionid**
     * @param mobile
     * @param code
     * @param newHttpResponse
     */
    public void postBindWeChat(int what, String openid, String unionid, String mobile, String code, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("openid", openid);
        params.put("unionid", unionid);
        params.put("mobile", mobile);
        params.put("code", code);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 5,
                bindwxUrl), RequestMethod.POST);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessage(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
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

    /**
     * 用户注册接口
     *
     * @param what
     * @param mobile
     * @param code
     * @param newHttpResponse
     */
    public void postRegister(int what, String mobile, String code, String password, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", mobile);
        params.put("code", code);
        params.put("password", password);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 5, registerUrl), RequestMethod.POST);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessage(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
                    }
                } else {
                    showErrorCodeMessage(responseCode, response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    /**
     * 极验第一次认证，获取参数
     *
     * @param what
     * @param newHttpResponse
     */
    public void getGeetStart(int what, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, geetStartUrl), RequestMethod.GET);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    newHttpResponse.OnHttpResponse(what, result);
                } else {
                    showErrorCodeMessage(responseCode, response);
                    newHttpResponse.OnHttpResponse(what, "");
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
                newHttpResponse.OnHttpResponse(what, "");
            }
        }, true, true);
    }

    /**
     * 极验第二次认证
     *
     * @param what
     * @param newHttpResponse
     */
    public void postGeetVerify(int what, String geetest_challenge, String geetest_validate, String geetest_seccode, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("geetest_challenge", geetest_challenge);
        params.put("geetest_validate", geetest_validate);
        params.put("geetest_seccode", geetest_seccode);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, geetVerifyUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
                    }
                } else {
                    showErrorCodeMessage(responseCode, response);
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, true);
    }
}
