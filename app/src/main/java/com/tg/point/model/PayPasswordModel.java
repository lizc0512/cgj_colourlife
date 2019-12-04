package com.tg.point.model;

import android.content.Context;

import com.tg.coloursteward.baseModel.BaseModel;
import com.tg.coloursteward.baseModel.HttpListener;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.baseModel.RequestEncryptionUtils;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.util.RSAUtil;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件名:
 * 创建者:yuansongkai
 * 邮箱:827194927@qq.com
 * 创建日期:
 * 描述:
 **/
public class PayPasswordModel extends BaseModel {
    private String addPawdUrl = "/app/password/addPayPwd";//设置用户新支付密码
    private String setPawdUrl = "/app/password/setPayPwd";//修改用户支付密码
    private String checkPawdUrl = "/app/password/checkPayPwd";//修改用户支付密码
    private String getIdentityUrl = "/app/password/getIdentity";//修改用户支付密码
    private String validIdentityUrl = "/app/password/validIdentity";//验证用户实名信息

    public PayPasswordModel(Context context) {
        super(context);
    }

    public void addPayPassword(int what, String password, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        PublicKey publicKey = RSAUtil.keyStrToPublicKey(Contants.URl.publicKeyString);
        params.put("password", RSAUtil.encryptDataByPublicKey(password.getBytes(), publicKey));
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 16, addPawdUrl), RequestMethod.POST);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessage(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
                    } else if (resultCode == 1000) {//设置密码过于简单
                        newHttpResponse.OnHttpResponse(what, "1000");
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

    public void setPayPassword(int what, String password, String token, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        PublicKey publicKey = RSAUtil.keyStrToPublicKey(Contants.URl.publicKeyString);
        params.put("password", RSAUtil.encryptDataByPublicKey(password.getBytes(), publicKey));
        params.put("token", token);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 16, setPawdUrl), RequestMethod.POST);
        request(what, request, params, new HttpListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                int responseCode = response.getHeaders().getResponseCode();
                String result = response.get();
                if (responseCode == RequestEncryptionUtils.responseSuccess) {
                    int resultCode = showSuccesResultMessage(result);
                    if (resultCode == 0) {
                        newHttpResponse.OnHttpResponse(what, result);
                    } else if (resultCode == 1001) {//token过期
                        newHttpResponse.OnHttpResponse(what, "1001");
                    } else if (resultCode == 1000) {//设置密码过于简单
                        newHttpResponse.OnHttpResponse(what, "1000");
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

    public void checkPayPassword(int what, String password, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        PublicKey publicKey = RSAUtil.keyStrToPublicKey(Contants.URl.publicKeyString);
        params.put("password", RSAUtil.encryptDataByPublicKey(password.getBytes(), publicKey));
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 16, checkPawdUrl), RequestMethod.POST);
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

    public void getIdentityInfor(int what, final HttpResponse newHttpResponse) {
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 15, getIdentityUrl), RequestMethod.GET);
        request(what, request, null, new HttpListener<String>() {
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

    public void validIdentityInfor(int what, String mobile, String code, String identity_val, final HttpResponse newHttpResponse) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", mobile);
        params.put("code", code);
        params.put("identity_val", identity_val);
        final Request<String> request = NoHttp.createStringRequest(RequestEncryptionUtils.getRequestUrl(mContext, 16, validIdentityUrl), RequestMethod.POST);
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
