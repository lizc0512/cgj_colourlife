package com.tg.coloursteward.serice;

import android.content.ContentValues;
import android.content.Context;

import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.DES;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.Tools;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.http.OkHttpConnector;

import org.json.JSONObject;

import java.util.HashMap;

import static com.tg.coloursteward.net.HttpTools.getTime;

public class HomeService {
    public Context context;

    public HomeService(Context context) {
        this.context = context;
    }

    /**
     * 获取用户应用权限
     */
    public void getAuth(String clientCode, final GetTwoRecordListener<String, String> listener) {
        String username = UserInfo.employeeAccount;
        String md5_pwd = Tools.getPassWordMD5(context);
        String ts = getTime();
        String Sign = "";
        try {
            Sign = MD5.getMd5Value(DES.APP_ID + ts + DES.TOKEN + "false").toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ContentValues paramsMap = new ContentValues();
        paramsMap.put("username", username);
        paramsMap.put("password", md5_pwd);
        paramsMap.put("clientCode", "case");
        paramsMap.put("getExpire", "1");
        paramsMap.put("appID", DES.APP_ID);
        paramsMap.put("sign", Sign);
        paramsMap.put("ts", ts);
        OkHttpConnector.httpGet_net(context, null, Contants.URl.URL_ICETEST + "/auth",
                paramsMap, new IGetListener() {
                    @Override
                    public void httpReqResult(String response) {
                        String result = response.toString();
                        try {
                            int codeInt = HttpTools.getCode(result);
                            JSONObject contentObject = HttpTools.getContentJSONObject(response);
                            if (codeInt == 0) {
                                String openID = contentObject.getString("openID");
                                String accessToken = contentObject.getString("accessToken");
                                String expires_in = contentObject.getString("expires_in");
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
                });
    }

    /**
     * 获取用户应用权限（Oauth2）  access_token
     */
    public void getAuth2(String developerCode, final GetTwoRecordListener<String, String> listener) {
        String username = UserInfo.employeeAccount;
        String md5_pwd = Tools.getPassWordMD5(context);
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", md5_pwd);
        params.put("developerCode", "case");
        params.put("getExpire", "1");
        String url = null;
        HashMap<String, Object> paramsStr = null;
        if (params != null) {
            paramsStr = params.toHashMap();
        } else {
            paramsStr = null;
        }
        try {
            url = Contants.URl.URL_ICETEST + HttpTools.GetUrl(Contants.URl.URL_ICETEST, "/auth2", paramsStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkHttpConnector.httpGet_net(context, null, url,
                null, new IGetListener() {
                    @Override
                    public void httpReqResult(String response) {
                        String result = response.toString();
                        try {
                            int codeInt = HttpTools.getCode(result);
                            JSONObject contentObject = HttpTools.getContentJSONObject(response);
                            if (codeInt == 0) {
                                String username = contentObject.getString("username");
                                String accessToken = contentObject.getString("access_token");
                                String expires_in = contentObject.getString("expires_in");
                                if (listener != null) {
                                    listener.onFinish(username, accessToken, expires_in);
                                }
                            } else {
                                if (listener != null) {
                                    listener.onFailed("");
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }

}