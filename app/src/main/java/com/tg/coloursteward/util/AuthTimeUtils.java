package com.tg.coloursteward.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tg.coloursteward.MyBrowserActivity;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.serice.HomeService;

import java.util.Date;

/**
 * 应用授权判断时间是否过期
 */

public class AuthTimeUtils {
    private static final String TAG = "AuthTimeUtils";

    private Intent intent;
    private Context mContext;
    private HomeService homeService;

    public void IsAuthTime(Context context, final String url,
                           String clientCode, String oauthType, String developerCode, final String param) {
        this.mContext = context;
        getAuth(url, clientCode, oauthType, developerCode, param);

    }

    /**
     * 应用授权
     *
     * @param url
     * @param clientCode
     * @param oauthType
     * @param developerCode
     */
    private void getAuth(final String url,
                         String clientCode, String oauthType, String developerCode, final String param) {
        Date dt = new Date();
        Long time = dt.getTime();
        long currentTime = Tools.getCurrentTime(mContext);
        long currentTime2 = Tools.getCurrentTime2(mContext);
        long ExpiresTime2 = Tools.getExpiresTime2(mContext);
        String openID = Tools.getOpenID(mContext);
        String accessToken = Tools.getAccessToken(mContext);
        String access_token = Tools.getAccess_token(mContext);
        if (StringUtils.isNotEmpty(openID) && StringUtils.isNotEmpty(accessToken) || StringUtils.isNotEmpty(access_token)) {
            Long nineHours = 1000 * 60 * 60 * 1L;
            if ("0".equals(oauthType) || oauthType == null)//oauth1认证
            {
                if (time - currentTime <= nineHours) {//判断保存时间是否超过9小时，超过则过期，需要重新获取
                    Log.d("printLog", "auth1.0");
                    String str = "?";
                    String URL;
                    if (url.contains(str)) {//Url有问号
                        URL = url + "&openID=" + openID + "&accessToken=" + accessToken + param;
                    } else {
                        URL = url + "?openID=" + openID + "&accessToken=" + accessToken + param;
                    }
                    Log.d(TAG, "auth1.0   URL=" + URL);
                    intent = new Intent(mContext, MyBrowserActivity.class);
                    intent.putExtra(MyBrowserActivity.KEY_URL, URL);
                    mContext.startActivity(intent);
                } else {
                    getAuthData(url, clientCode, oauthType, developerCode, param);
                }
            } else {
                if (time - currentTime2 <= ExpiresTime2 * 1000) {//判断保存时间是否超过9小时，超过则过期，需要重新获取
                    Log.d("printLog", "auth2.0");
                    String str = "?";
                    String URL;
                    if (url.contains(str)) {//Url有问号
                        URL = url + "&username=" + UserInfo.employeeAccount + "&access_token=" + access_token + param;
                    } else {
                        URL = url + "?username=" + UserInfo.employeeAccount + "&access_token=" + access_token + param;
                    }
                    Log.d(TAG, "auth2.0   URL=" + URL);
                    intent = new Intent(mContext, MyBrowserActivity.class);
                    intent.putExtra(MyBrowserActivity.KEY_URL, URL);
                    mContext.startActivity(intent);
                } else {
                    getAuthData(url, clientCode, oauthType, developerCode, param);
                }
            }
        } else {
            getAuthData(url, clientCode, oauthType, developerCode, param);
        }
    }

    private void getAuthData(final String url,
                             String clientCode, String oauthType, String developerCode, final String param) {
        Log.d("printLog", "oauthType=" + oauthType);
        if (homeService == null) {
            homeService = new HomeService(mContext);
        }
        if ("0".equals(oauthType) || oauthType == null)//oauth1认证
        {
            homeService.getAuth(clientCode, new GetTwoRecordListener<String, String>() {

                @Override
                public void onFinish(String openID, String accessToken, String Expire) {
                    Date dt = new Date();
                    Long time = dt.getTime();
                    Tools.saveOpenID(mContext, openID);
                    Tools.saveAccessToken(mContext, accessToken);
                    Tools.saveCurrentTime(mContext, time);
                    String str = "?";
                    String URL;
                    if (url.contains(str)) {//Url有问号
                        URL = url + "&openID=" + openID + "&accessToken=" + accessToken + param;
                    } else {
                        URL = url + "?openID=" + openID + "&accessToken=" + accessToken + param;
                    }
                    intent = new Intent(mContext, MyBrowserActivity.class);
                    intent.putExtra(MyBrowserActivity.KEY_URL, URL);
                    mContext.startActivity(intent);
                }

                @Override
                public void onFailed(String Message) {
                    // TODO Auto-generated method stub

                }
            });
        } else {//oauth2认证
            homeService.getAuth2(developerCode, new GetTwoRecordListener<String, String>() {

                @Override
                public void onFinish(String username, String accessToken, String Expire) {
                    Date dt = new Date();
                    Long time = dt.getTime();
                    Tools.saveAccess_token(mContext, accessToken);
                    Tools.saveCurrentTime2(mContext, time);
                    Tools.saveExpiresTime2(mContext, Long.parseLong(Expire));
                    String str = "?";
                    String URL;
                    if (url.contains(str)) {//Url有问号
                        URL = url + "&username=" + username + "&access_token=" + accessToken + param;
                    } else {
                        URL = url + "?username=" + username + "&access_token=" + accessToken + param;
                    }
                    intent = new Intent(mContext, MyBrowserActivity.class);
                    intent.putExtra(MyBrowserActivity.KEY_URL, URL);
                    mContext.startActivity(intent);
                }

                @Override
                public void onFailed(String Message) {
                    // TODO Auto-generated method stub

                }
            });
        }
    }
}
