package com.tg.coloursteward.serice;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.entity.Oauth2Entity;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.Oauth2CallBack;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.http.OkHttpConnector;
import com.youmai.smallvideorecord.utils.Log;

import org.json.JSONObject;


/**
 * 彩管家APP oauth2.0授权
 */
public class OAuth2ServiceUpdate {
    public Context context;
    private String access_token = "";
    private Oauth2CallBack oauth2CallBack;

    public OAuth2ServiceUpdate(Context context) {
        this.context = context;

    }

    /**
     * 获取鉴权token信息
     */
    public void getOAuth2Service(String username, String passwordMD5, Oauth2CallBack mOauth2CallBack) {
        this.oauth2CallBack = mOauth2CallBack;
        if (!TextUtils.isEmpty(Tools.getAccess_token2(context))) {//Access_token不为空
            if (!againGetToken()) {//不需要重新请求
                access_token = Tools.getAccess_token2(context);
                if (null != oauth2CallBack) {
                    oauth2CallBack.onData(access_token);
                }
            } else {//重新使用Refresh_token请求token
                ContentValues params = new ContentValues();
                params.put("client_id", "3");
                params.put("client_secret", Contants.URl.CLIENT_SECRET);
                params.put("grant_type", "refresh_token");
                params.put("refresh_token", Tools.getRefresh_token2(context));
                OkHttpConnector.httpPost(context, Contants.URl.URL_OAUTH2 + "/oauth/token", params, new IPostListener() {
                    @Override
                    public void httpReqResult(String jsonString) {
                        if (null != jsonString) {
                            try {
                                Oauth2Entity oauth2Entity = GsonUtils.gsonToBean(jsonString, Oauth2Entity.class);
                                access_token = saveAccessToken(oauth2Entity);
                                if (null != oauth2CallBack) {
                                    oauth2CallBack.onData(access_token);
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                });
            }
        } else {
            if (!TextUtils.isEmpty(passwordMD5)) {
                ContentValues params = new ContentValues();
                params.put("username", username);
                params.put("password", passwordMD5);
                params.put("client_id", "3");
                params.put("client_secret", Contants.URl.CLIENT_SECRET);
                params.put("grant_type", "password");
                params.put("scope", "*");
                OkHttpConnector.httpPost(context, Contants.URl.URL_OAUTH2 + "/oauth/token", params, new IPostListener() {
                    @Override
                    public void httpReqResult(String jsonString) {
                        if (null != jsonString) {
                            try {
                                JSONObject jsonObject = new JSONObject(jsonString);
                                if (jsonObject.has("code")) {
                                    int code = jsonObject.getInt("code");
                                    if (code != 0) {
                                        Tools.saveAccess_token2(context,"");
                                        String message = jsonObject.getString("message");
                                        ToastFactory.showToast(context, message);
                                    }
                                } else if (jsonObject.has("token_type")) {
                                    Oauth2Entity oauth2Entity = GsonUtils.gsonToBean(jsonString, Oauth2Entity.class);
                                    access_token = saveAccessToken(oauth2Entity);
                                    if (null != oauth2CallBack) {
                                        oauth2CallBack.onData(access_token);
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                });
            }
        }
    }

    private String saveAccessToken(Oauth2Entity oauth2Entity) {
        try {
            Tools.saveRefresh_token2Time(context, System.currentTimeMillis());
            Tools.saveToken_type(context, oauth2Entity.getToken_type());
            Tools.saveExpires_in(context, Long.valueOf(oauth2Entity.getExpires_in()));
            Tools.saveAccess_token2(context, oauth2Entity.getAccess_token());
            Tools.saveRefresh_token2(context, oauth2Entity.getRefresh_token());
            UserInfo.color_token = oauth2Entity.getAccess_token();
        } catch (Exception e) {
            Log.d("oauth2.0",e.toString());
        }
        return Tools.getAccess_token2(context);
    }

    /**
     * 判断授权是否超时
     *
     * @return
     */
    private boolean againGetToken() {
        long lastSaveTime = Tools.getRefresh_token2Time(context);
        long nowTime = System.currentTimeMillis();
        long distance = (nowTime - lastSaveTime) / 1000;
        long expires_in = Tools.getExpires_in(context);
        if (distance >= expires_in - 60 * 10) {
            return true; //需要刷新
        } else {
            return false;
        }
    }

}