package com.tg.coloursteward.serice;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.model.HomeModel;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.tg.coloursteward.util.Tools;

import org.json.JSONObject;

public class HomeService implements HttpResponse {
    public Context context;
    private HomeModel homeModel;
    private GetTwoRecordListener listener;

    public HomeService(Activity context) {
        this.context = context;
        homeModel = new HomeModel(context);
    }

    /**
     * 获取用户应用权限auth
     */
    public void getAuth(final GetTwoRecordListener<String, String> mListener) {
        String username = UserInfo.employeeAccount;
        String md5_pwd = Tools.getPassWordMD5(context);
        this.listener = mListener;
        Long nowTime = System.currentTimeMillis();
        String openID = SharedPreferencesUtils.getInstance().getStringData(SpConstants.accessToken.authOpenId, "");
        long saveCurrent = SharedPreferencesUtils.getInstance().getLongData(SpConstants.accessToken.authCurrentTime, 0l);
        long expiress = SharedPreferencesUtils.getInstance().getLongData(SpConstants.accessToken.authExpires_in, 0l);
        String token = SharedPreferencesUtils.getInstance().getStringData(SpConstants.accessToken.authToken, "");
        if (nowTime - saveCurrent <= expiress * 1000) {//auth在有效期内，直接返回缓存
            if (listener != null) {
                listener.onFinish(openID, token, String.valueOf(expiress));
            }
        } else {
            homeModel.getAuth(1, username, md5_pwd, this);
        }
    }

    /**
     * 获取用户应用权限（auth2）
     */
    public void getAuth2(GetTwoRecordListener<String, String> mListener) {
        this.listener = mListener;
        Long nowTime = System.currentTimeMillis();
        long saveCurrent = SharedPreferencesUtils.getInstance().getLongData(SpConstants.accessToken.auth2CurrentTime, 0l);
        long expiress = SharedPreferencesUtils.getInstance().getLongData(SpConstants.accessToken.auth2Expires_in, 0l);
        String token = SharedPreferencesUtils.getInstance().getStringData(SpConstants.accessToken.auth2Token, "");
        String username = SharedPreferencesUtils.getInstance().getStringData(SpConstants.accessToken.auth2Username, UserInfo.employeeAccount);
        if (nowTime - saveCurrent <= expiress * 1000 && !TextUtils.isEmpty(token) && !TextUtils.isEmpty(username)) {//auth2在有效期内，直接返回缓存
            if (listener != null) {
                listener.onFinish(username, token, String.valueOf(expiress));
            }
        } else {
            homeModel.getAuth2(0, this);
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    int codeInt = HttpTools.getCode(result);
                    JSONObject contentObject = HttpTools.getContentJSONObject(result);
                    if (codeInt == 0) {
                        String username = null;
                        try {
                            username = contentObject.getString("username");
                            String accessToken = contentObject.getString("access_token");
                            String expires_in = contentObject.getString("expires_in");
                            SharedPreferencesUtils.getInstance().saveStringData(SpConstants.accessToken.auth2Username, username);
                            SharedPreferencesUtils.getInstance().saveStringData(SpConstants.accessToken.auth2Token, accessToken);
                            SharedPreferencesUtils.getInstance().saveLongData(SpConstants.accessToken.auth2CurrentTime, System.currentTimeMillis());
                            SharedPreferencesUtils.getInstance().saveLongData(SpConstants.accessToken.auth2Expires_in, Long.valueOf(expires_in));
                            if (listener != null) {
                                listener.onFinish(username, accessToken, expires_in);
                            }
                        } catch (Exception e) {
                            if (listener != null) {
                                listener.onFailed("");
                            }
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailed("");
                        }
                    }
                } else {
                    if (listener != null) {
                        listener.onFailed("");
                    }
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        int codeInt = HttpTools.getCode(result);
                        JSONObject contentObject = HttpTools.getContentJSONObject(result);
                        if (codeInt == 0) {
                            String openID = contentObject.getString("openID");
                            String accessToken = contentObject.getString("accessToken");
                            String expires_in = contentObject.getString("expires_in");
                            SharedPreferencesUtils.getInstance().saveStringData(SpConstants.accessToken.authOpenId, openID);
                            SharedPreferencesUtils.getInstance().saveStringData(SpConstants.accessToken.authToken, accessToken);
                            SharedPreferencesUtils.getInstance().saveLongData(SpConstants.accessToken.authCurrentTime, System.currentTimeMillis());
                            SharedPreferencesUtils.getInstance().saveLongData(SpConstants.accessToken.authExpires_in, Long.valueOf(expires_in));
                            if (listener != null) {
                                listener.onFinish(openID, accessToken, expires_in);
                            }
                        } else {
                            if (listener != null) {
                                listener.onFailed("");
                            }
                        }
                    } catch (Exception e) {
                    }
                }
                break;
        }

    }
}