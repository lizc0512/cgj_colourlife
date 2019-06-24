package com.tg.user.model;

import android.content.Context;
import android.text.TextUtils;

import com.tg.coloursteward.BuildConfig;
import com.tg.coloursteward.baseModel.BaseModel;
import com.tg.coloursteward.baseModel.HttpListener;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.baseModel.RequestEncryptionUtils;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.net.DES;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.util.TokenUtils;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * @name ${lizc}
 * @class name：com.colourlife.user.model
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/6/17 10:42
 * @change
 * @chang time
 * @class describe
 */
public class UserModel extends BaseModel {
    private String gtUrl = "/app/home/login/verify";
    private String keySecretUrl = "/1.0/auth";
    private String oauthUrl = "/oauth/token";
    private String oauthUserUrl = "/oauth/user";
    private String singleDeviceUrl = "/cgjapp/single/device/login";
    private String skinUrl = "/newoa/config/skin";
    private String tsUrl = "/timestamp";
    private String mmsUrl = "/employee/sms/sendSms";
    private String findPwdUrl = "/orgms/voice/updatePassWordSMS";
    private String czyOauthUrl = "/app/authUser";
    private String singleExitUrl = "/cgjapp/single/device/logout";
    private String changePwdUrl = "/account/password";

    public UserModel(Context context) {
        super(context);
    }

    /**
     * @param what
     * @param geetest_challenge
     * @param geetest_validate
     * @param geetest_seccode
     * @param httpResponse      极验二次校验
     */
    public void postGt(int what, String geetest_challenge, String geetest_validate, String geetest_seccode, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("geetest_challenge", geetest_challenge);
        params.put("geetest_validate", geetest_validate);
        params.put("geetest_seccode", geetest_seccode);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 0, gtUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int code = showSuccesResultMessage(result);
                    if (code == 0) {
                        httpResponse.OnHttpResponse(what, result);
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

    /**
     * @param what
     * @param httpResponse auth获取key和secret
     */
    public void postKeyAndSecret(int what, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 1, keySecretUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        httpResponse.OnHttpResponse(what, result);
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
     * @param what
     * @param username
     * @param passwordMD5
     * @param httpResponse 获取oauthToken
     */
    public void postOauthToken(int what, String username, String passwordMD5, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", username);
        params.put("password", passwordMD5);
        params.put("client_id", "3");
        params.put("client_secret", Contants.URl.CLIENT_SECRET);
        params.put("grant_type", "password");
        params.put("scope", "*");
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 2, oauthUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
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

    /**
     * @param what
     * @param refresh_token
     * @param httpResponse  刷新oauthToken
     */
    public void postReOauthToken(int what, String refresh_token, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("client_id", "3");
        params.put("client_secret", Contants.URl.CLIENT_SECRET);
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", refresh_token);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 2, oauthUrl), RequestMethod.POST);
        request(what, request, RequestEncryptionUtils.getNewSaftyMap(mContext, params), new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    if (!TextUtils.isEmpty(result)) {
                        httpResponse.OnHttpResponse(what, result);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                showExceptionMessage(what, response);
            }
        }, true, false);
    }

    /**
     * @param what
     * @param access_token
     * @param httpResponse 获取用户信息
     */
    public void getOauthUser(int what, String access_token, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 2, oauthUserUrl), RequestMethod.GET);
        request.addHeader("Authorization", "Bearer " + access_token);
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
     * @param what
     * @param type
     * @param httpResponse 单设备登录
     */
    public void postSingleDevice(int what, String type, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("login_type", type);//登录方式,1静默和2密码
        params.put("device_type", "1");//登录设备类别，1：安卓，2：IOS
        params.put("version", BuildConfig.VERSION_NAME);//APP版本号
        params.put("device_code", TokenUtils.getUUID(mContext));//设备唯一编号
        params.put("device_info", TokenUtils.getDeviceInfor(mContext));//设备详细信息（json字符创）
        params.put("device_name", TokenUtils.getDeviceBrand() + TokenUtils.getDeviceType());//设备名称（如三星S9）
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 3, singleDeviceUrl), RequestMethod.POST);
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
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
//                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    /**
     * @param what
     * @param corpId
     * @param httpResponse 获取皮肤包
     */
    public void postSkin(int what, String corpId, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_id", corpId);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 4, skinUrl), RequestMethod.POST);
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
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
//                showExceptionMessage(what, response);
            }
        }, true, true);
    }

    /**
     * @param what
     * @param httpResponse 获取时间值
     */
    public void getTs(int what, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 4, tsUrl), RequestMethod.GET);
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
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
//                showExceptionMessage(what, response);
            }
        }, true, false);
    }

    /**
     * @param what
     * @param oa
     * @param name
     * @param mobile
     * @param type
     * @param httpResponse 发送短信接口
     */
    public void postSendMms(int what, String oa, String name, String mobile, String type, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", oa);
        params.put("realname", name);
        params.put("mobile", mobile);
        params.put("work_type", type);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 4, mmsUrl), RequestMethod.POST);
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

    /**
     * @param what
     * @param username
     * @param verify_code
     * @param newPassword
     * @param httpResponse 找回登录密码
     */
    public void putFindPwd(int what, String username, String verify_code, String newPassword, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userName", username);
        params.put("code", verify_code);
        params.put("password", newPassword);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 4, findPwdUrl), RequestMethod.PUT);
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
                            String message = HttpTools.getMessageString(result);
                            callback(message);
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

    /**
     * @param what
     * @param code
     * @param httpResponse 彩之云授权登录接口
     */
    public void getCzyLogin(int what, String code, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("code", code);
        params.put("token", DES.TOKEN);
        params.put("app_id", DES.APP_ID);
        params.put("client_secret", DES.TOKEN);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 5, czyOauthUrl), RequestMethod.GET);
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
        }, true, true);
    }

    /**
     * @param what
     * @param device_code
     * @param httpResponse 单设备退出
     */
    public void postSingleExit(int what, String device_code, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("device_code", device_code);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 3, singleExitUrl), RequestMethod.POST);
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
//                            showErrorCodeMessage(response);
                        }
                    } else {
//                        showErrorCodeMessage(response);
                    }
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
//                showExceptionMessage(what, response);
            }
        }, true, false);
    }

    /**
     * @param what
     * @param username
     * @param pwdold
     * @param pwdnew
     * @param httpResponse 修改登录密码
     */
    public void putChangePwd(int what, String username, String pwdold, String pwdnew, final HttpResponse httpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", username);
        params.put("oldpassword", pwdold);
        params.put("newpassword", pwdnew);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 4, changePwdUrl), RequestMethod.PUT);
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
