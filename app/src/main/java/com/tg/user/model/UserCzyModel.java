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
}
