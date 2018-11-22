package com.tg.coloursteward.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.tg.coloursteward.MyBrowserActivity;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.view.dialog.ToastFactory;

import java.util.Date;

/**
 * 应用授权判断时间是否过期
 */

public class AuthTimeUtils {
    private Intent intent;
    private Activity mActivity;
    private HomeService homeService;

    public void IsAuthTime(Activity mActivity, final String url,
                           String clientCode, String oauthType, String developerCode, final String param) {
        this.mActivity = mActivity;
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
        long currentTime = Tools.getCurrentTime(mActivity);
        long currentTime2 = Tools.getCurrentTime2(mActivity);
        long ExpiresTime2 = Tools.getExpiresTime2(mActivity);
        String openID = Tools.getOpenID(mActivity);
        String accessToken = Tools.getAccessToken(mActivity);
        String access_token = Tools.getAccess_token(mActivity);
        if (StringUtils.isNotEmpty(openID) && StringUtils.isNotEmpty(accessToken) || StringUtils.isNotEmpty(access_token)) {
            Long nineHours = 1000 * 60 * 60 * 1L;
            if (!TextUtils.isEmpty(oauthType)) {
                if ("0".equals(oauthType))//oauth1认证
                {
                    if (time - currentTime <= nineHours) {//判断保存时间是否超过9小时，超过则过期，需要重新获取
                        String str = "?";
                        String URL;
                        if (url.contains(str)) {//Url有问号
                            URL = url + "&openID=" + openID + "&accessToken=" + accessToken + param;
                        } else {
                            URL = url + "?openID=" + openID + "&accessToken=" + accessToken + param;
                        }
                        intent = new Intent(mActivity, MyBrowserActivity.class);
                        intent.putExtra(MyBrowserActivity.KEY_URL, URL);
                        mActivity.startActivity(intent);
                    } else {
                        getAuthData(url, clientCode, oauthType, developerCode, param);
                    }
                } else if ("2".equals(oauthType)) {
                    LinkParseUtil.parse(mActivity, url, "");
                } else if ("1".equals(oauthType)) {
                    if (time - currentTime2 <= ExpiresTime2 * 1000) {//判断保存时间是否超过9小时，超过则过期，需要重新获取
                        String str = "?";
                        String URL;
                        if (url.contains(str)) {//Url有问号
                            URL = url + "&username=" + UserInfo.employeeAccount + "&access_token=" + access_token + param;
                        } else {
                            URL = url + "?username=" + UserInfo.employeeAccount + "&access_token=" + access_token + param;
                        }
                        intent = new Intent(mActivity, MyBrowserActivity.class);
                        intent.putExtra(MyBrowserActivity.KEY_URL, URL);
                        mActivity.startActivity(intent);
                    } else {
                        getAuthData(url, clientCode, oauthType, developerCode, param);
                    }
                } else {
                    LinkParseUtil.parse(mActivity, url, "");
                }
            } else {
                LinkParseUtil.parse(mActivity, url, "");
            }
        } else {
            getAuthData(url, clientCode, oauthType, developerCode, param);
        }
    }

    private void getAuthData(final String url,
                             String clientCode, String oauthType, String developerCode, final String param) {
        if (homeService == null) {
            homeService = new HomeService(mActivity);
        }
        if (!TextUtils.isEmpty(oauthType)) {
            if ("0".equals(oauthType))//oauth1认证
            {
                homeService.getAuth(clientCode, new GetTwoRecordListener<String, String>() {

                    @Override
                    public void onFinish(String openID, String accessToken, String Expire) {
                        Date dt = new Date();
                        Long time = dt.getTime();
                        Tools.saveOpenID(mActivity, openID);
                        Tools.saveAccessToken(mActivity, accessToken);
                        Tools.saveCurrentTime(mActivity, time);
                        String str = "?";
                        String URL;
                        if (url.contains(str)) {//Url有问号
                            URL = url + "&openID=" + openID + "&accessToken=" + accessToken + param;
                        } else {
                            URL = url + "?openID=" + openID + "&accessToken=" + accessToken + param;
                        }
                        intent = new Intent(mActivity, MyBrowserActivity.class);
                        intent.putExtra(MyBrowserActivity.KEY_URL, URL);
                        mActivity.startActivity(intent);
                    }

                    @Override
                    public void onFailed(String Message) {
                        ToastFactory.showToast(mActivity, Message);
                    }
                });
            } else if ("2".equals(oauthType)) {
                LinkParseUtil.parse(mActivity, url, "");
            } else if ("1".equals(oauthType)) {//oauth2认证
                homeService.getAuth2(developerCode, new GetTwoRecordListener<String, String>() {

                    @Override
                    public void onFinish(String username, String accessToken, String Expire) {
                        Date dt = new Date();
                        Long time = dt.getTime();
                        Tools.saveAccess_token(mActivity, accessToken);
                        Tools.saveCurrentTime2(mActivity, time);
                        Tools.saveExpiresTime2(mActivity, Long.parseLong(Expire));
                        String str = "?";
                        String URL;
                        if (url.contains(str)) {//Url有问号
                            URL = url + "&username=" + username + "&access_token=" + accessToken + param;
                        } else {
                            URL = url + "?username=" + username + "&access_token=" + accessToken + param;
                        }
                        intent = new Intent(mActivity, MyBrowserActivity.class);
                        intent.putExtra(MyBrowserActivity.KEY_URL, URL);
                        mActivity.startActivity(intent);
                    }

                    @Override
                    public void onFailed(String Message) {
                        ToastFactory.showToast(mActivity, Message);
                    }
                });
            } else {
                LinkParseUtil.parse(mActivity, url, "");
            }
        } else {
            LinkParseUtil.parse(mActivity, url, "");
        }
    }
}
