package com.tg.user.oauth;

import android.content.Context;
import android.text.TextUtils;

import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.user.callback.Oauth2CallBack;
import com.tg.user.entity.Oauth2Entity;
import com.tg.user.model.UserModel;

import org.json.JSONObject;


/**
 * 彩管家APP oauth2.0授权
 */
public class OAuth2ServiceUpdate implements HttpResponse {
    public Context context;
    private String access_token = "";
    private Oauth2CallBack oauth2CallBack;
    private UserModel userModel;
    private String loginType;

    public OAuth2ServiceUpdate(Context context, String mLoginType) {
        this.context = context;
        userModel = new UserModel(context);
        this.loginType = mLoginType;
    }

    /**
     * 获取鉴权token信息
     */
    public void getOAuth2Service(String username, String passwordMD5, Oauth2CallBack mOauth2CallBack) {
        this.oauth2CallBack = mOauth2CallBack;
        if (!TextUtils.isEmpty(SharedPreferencesUtils.getKey(context, SpConstants.accessToken.accssToken))) {//Access_token不为空
            if (!againGetToken()) {//不需要重新请求
                access_token = SharedPreferencesUtils.getKey(context, SpConstants.accessToken.accssToken);
                if (null != oauth2CallBack) {
                    oauth2CallBack.onData(access_token);
                }
            } else {//重新使用Refresh_token请求token
                userModel.postReOauthToken(1, SharedPreferencesUtils.getKey(context, SpConstants.accessToken.refreshAccssToken), this);
            }
        } else {
            if (!TextUtils.isEmpty(passwordMD5)) {
                userModel.postOauthToken(0, username, passwordMD5, loginType, this);
            }
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.has("code")) {
                            int code = jsonObject.getInt("code");
                            if (code != 0) {
                                SharedPreferencesUtils.saveKey(context, SpConstants.accessToken.accssToken, "");
                                String message = jsonObject.getString("message");
                                ToastUtil.showShortToast(context, message);
                            }
                        } else if (jsonObject.has("token_type")) {
                            Oauth2Entity oauth2Entity = GsonUtils.gsonToBean(result, Oauth2Entity.class);
                            access_token = saveAccessToken(oauth2Entity);
                            if (null != oauth2CallBack) {
                                oauth2CallBack.onData(access_token);
                            }
                        }
                    } catch (Exception e) {
                    }
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        Oauth2Entity oauth2Entity = GsonUtils.gsonToBean(result, Oauth2Entity.class);
                        access_token = saveAccessToken(oauth2Entity);
                        if (null != oauth2CallBack) {
                            oauth2CallBack.onData(access_token);
                        }
                    } catch (Exception e) {
                    }
                }
                break;
        }
    }

    private String saveAccessToken(Oauth2Entity oauth2Entity) {
        SharedPreferencesUtils.saveRefresh_token2Time(context, System.currentTimeMillis());
        SharedPreferencesUtils.saveKey(context, SpConstants.accessToken.tokenType, oauth2Entity.getToken_type());
        SharedPreferencesUtils.saveExpires_in(context, Long.valueOf(oauth2Entity.getExpires_in()));
        SharedPreferencesUtils.saveKey(context, SpConstants.accessToken.accssToken, oauth2Entity.getAccess_token());
        SharedPreferencesUtils.saveKey(context, SpConstants.accessToken.refreshAccssToken, oauth2Entity.getRefresh_token());
        return oauth2Entity.getAccess_token();
    }

    /**
     * @return 判断授权是否超时
     */
    private boolean againGetToken() {
        long lastSaveTime = SharedPreferencesUtils.getRefresh_token2Time(context);
        long nowTime = System.currentTimeMillis();
        long distance = (nowTime - lastSaveTime) / 1000;
        long expires_in = SharedPreferencesUtils.getExpires_in(context);
        if (distance >= expires_in - 60 * 10) {
            return true; //需要刷新
        } else {
            return false;
        }
    }

}