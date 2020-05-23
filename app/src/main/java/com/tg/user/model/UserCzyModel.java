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
public class UserCzyModel extends BaseModel {
    private String czyAuthUrl = "/oauth/token";
    private String sendCodeUrl = "/app/smsCode";
    private String bindwxUrl = "/app/bind/wechat";
    private String registerUrl = "/app/register";

    public UserCzyModel() {
    }

    public UserCzyModel(Context context) {
        super(context);
    }

    /**
     * 调用彩之云的授权登录获取access_token
     *
     * @param what
     * @param username
     * @param password
     * @param type
     * @param newHttpResponse
     */
    public void getAuthToken(int what, String username, String password, final String type, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("grant_type", "password");
        if ("1".equals(type) || "2".equals(type)) {
            params.put("password", RequestEncryptionUtils.setMD5(password));
        } else {
            params.put("password", password);
        }
        params.put("client_id", "2");
        params.put("client_secret", Contants.APP.czyClient_secret);
        params.put("type", type);
        params.put("username", username);
        final Request<String> request_oauthRegister = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 12, czyAuthUrl), RequestMethod.POST);
        request(what, request_oauthRegister, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.has("code")) {
                                showErrorCodeMessage(responseCode, response);
                            } else if (jsonObject.has("access_token")) {
                                Oauth2Entity oauth2Entity = GsonUtils.gsonToBean(result, Oauth2Entity.class);
                                newHttpResponse.OnHttpResponse(what, oauth2Entity.getAccess_token());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
     * @param unionid
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
}
