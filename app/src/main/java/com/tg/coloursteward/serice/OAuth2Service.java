package com.tg.coloursteward.serice;

import android.app.Activity;
import android.content.ContentValues;
import android.text.TextUtils;

import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.entity.Oauth2Entity;
import com.tg.coloursteward.entity.UserInfoEntity;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.Tools;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.http.OkHttpConnector;


/**
 * 彩管家APP oauth2.0授权
 */
public class OAuth2Service {
    public Activity context;
    private GetTwoRecordListener<String, String> listener;

    public OAuth2Service(Activity context) {
        this.context = context;

    }

    /**
     * 获取鉴权token信息
     */
    public void getOAuth2Service(final String status, final GetTwoRecordListener<String, String> listener) {
        this.listener = listener;
        if (!TextUtils.isEmpty(Tools.getAccess_token2(context))) {//Access_token不为空
            if (!againGetToken()) {//不需要重新请求
                if (("userinfo").equals(status)) {
                    getUserInfo(listener);
                }
            } else {//重新使用Refresh_token请求token
                ContentValues params = new ContentValues();
                params.put("client_id", "3");
                params.put("client_secret", "xlsfrQS5R49upmfZbhlsrUzAt9HDA5K4ptLYsqK5");
                params.put("grant_type", "refresh_token");
                params.put("refresh_token", Tools.getRefresh_token2(context));
                OkHttpConnector.httpPost(Contants.URl.URL_OAUTH2 + "/oauth/token", params, new IPostListener() {
                    @Override
                    public void httpReqResult(String jsonString) {
                        if (null != jsonString) {
                            Oauth2Entity oauth2Entity = GsonUtils.gsonToBean(jsonString, Oauth2Entity.class);
                            saveAccessToken(oauth2Entity);
                            if (("userinfo").equals(status)) {
                                getUserInfo(listener);
                            }
                        }
                    }
                });
            }
        } else {
            String password = null;
            try {
                password = MD5.getMd5Value(Tools.getPassWord(context)).toLowerCase();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ContentValues params = new ContentValues();
            params.put("username", UserInfo.employeeAccount);
            params.put("password", password);
            params.put("client_id", "3");
            params.put("client_secret", "xlsfrQS5R49upmfZbhlsrUzAt9HDA5K4ptLYsqK5");
            params.put("grant_type", "password");
            params.put("scope", "*");
            OkHttpConnector.httpPost(Contants.URl.URL_OAUTH2 + "/oauth/token", params, new IPostListener() {
                @Override
                public void httpReqResult(String jsonString) {
                    if (null != jsonString) {
                        try {
                            Oauth2Entity oauth2Entity = GsonUtils.gsonToBean(jsonString, Oauth2Entity.class);
                            saveAccessToken(oauth2Entity);
                            if (("userinfo").equals(status)) {
                                getUserInfo(listener);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            });

        }
    }

    private void saveAccessToken(Oauth2Entity oauth2Entity) {
        Tools.saveRefresh_token2Time(context, System.currentTimeMillis());
        Tools.saveToken_type(context, oauth2Entity.getToken_type());
        Tools.saveExpires_in(context, Long.valueOf(oauth2Entity.getExpires_in()));
        Tools.saveAccess_token2(context, oauth2Entity.getAccess_token());
        Tools.saveRefresh_token2(context, oauth2Entity.getRefresh_token());
        if (listener != null) {
            listener.onFinish(Tools.getAccess_token2(context), "", "");
        }
    }

    /**
     * 根据access_token获取用户信息
     *
     * @param listener
     */
    public void getUserInfo(final GetTwoRecordListener<String, String> listener) {
        this.listener = listener;
        if (!TextUtils.isEmpty(Tools.getAccess_token2(context))) {//Access_token不为空
            if (!againGetToken()) {//不需要重新请求token,则直接请求用户数据
                ContentValues params = new ContentValues();
                params.put("Authorization", "Bearer " + Tools.getAccess_token2(context));
                getNetInfo(params);
            } else {//重新使用Refresh_token请求token
                ContentValues params = new ContentValues();
                params.put("client_id", "3");
                params.put("client_secret", "xlsfrQS5R49upmfZbhlsrUzAt9HDA5K4ptLYsqK5");
                params.put("grant_type", "refresh_token");
                params.put("refresh_token", Tools.getRefresh_token2(context));
                OkHttpConnector.httpPost(Contants.URl.URL_OAUTH2 + "/oauth/token", params, new IPostListener() {
                    @Override
                    public void httpReqResult(String jsonString) {
                        if (null != jsonString) {
                            Oauth2Entity oauth2Entity = GsonUtils.gsonToBean(jsonString, Oauth2Entity.class);
                            saveAccessToken(oauth2Entity);
                            ContentValues params = new ContentValues();
                            params.put("Authorization", "Bearer " + Tools.getAccess_token2(context));
                            getNetInfo(params);
                        }
                    }
                });
            }
        } else {
            getOAuth2Service("userinfo", listener);
        }
    }

    /**
     * 保存用户信息
     *
     * @param userInfoEntity
     */
    private void saveUserInfo(UserInfoEntity userInfoEntity) {
        if (listener != null) {
            listener.onFinish(userInfoEntity.getContent().getName(), "", "");
        }
    }

    private void getNetInfo(ContentValues params) {
        OkHttpConnector.httpGet(params, Contants.URl.URL_OAUTH2 + "/oauth/user", null, new IGetListener() {
            @Override
            public void httpReqResult(String jsonString) {
                if (null != jsonString) {
                    try {
                        Tools.savetokenUserInfo(context, jsonString);
                        UserInfoEntity userInfoEntity = GsonUtils.gsonToBean(jsonString, UserInfoEntity.class);
                        saveUserInfo(userInfoEntity);
                    } catch (Exception e) {
                    }
                }
            }
        });
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