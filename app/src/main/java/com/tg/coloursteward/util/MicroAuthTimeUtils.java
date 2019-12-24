package com.tg.coloursteward.util;

import android.app.Activity;
import android.text.TextUtils;

import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.serice.HomeService;

/**
 * 应用授权判断时间是否过期
 */

public class MicroAuthTimeUtils {
    private Activity mActivity;
    private HomeService homeService;

    public void IsAuthTime(Activity mActivity, final String url, String oauthType, final String param) {
        this.mActivity = mActivity;
        if (url.startsWith("http")) {
            getAuth(url, oauthType, param);
        } else {
            LinkParseUtil.parse(mActivity, url, "");
        }
    }

    /**
     * 应用授权
     *
     * @param url
     * @param oauthType 0：无授权，1：auth1，2：auth2，3：oauth2.0授权，4：原生js授权，5：支持所有授权方式
     */
    private void getAuth(final String url, String oauthType, final String param) {
        if (homeService == null) {
            homeService = new HomeService(mActivity);
        }
        if (!TextUtils.isEmpty(oauthType)) {
            if ("1".equals(oauthType)) {//oauth1认证
                homeService.getAuth(new GetTwoRecordListener<String, String>() {

                    @Override
                    public void onFinish(String openID, String accessToken, String Expire) {
                        String str = "?";
                        String URL;
                        if (url.contains(str)) {//Url有问号
                            URL = url + "&openID=" + openID + "&accessToken=" + accessToken + param;
                        } else {
                            URL = url + "?openID=" + openID + "&accessToken=" + accessToken + param;
                        }
                        LinkParseUtil.parse(mActivity, oauthType, URL, "");
                    }

                    @Override
                    public void onFailed(String Message) {
                    }
                });
            } else if ("0".equals(oauthType)) {//0：无授权
                LinkParseUtil.parse(mActivity, oauthType, url, "");
            } else if ("2".equals(oauthType)) {//2：auth2
                homeService.getAuth2(new GetTwoRecordListener<String, String>() {

                    @Override
                    public void onFinish(String username, String accessToken, String Expire) {
                        String str = "?";
                        String URL;
                        if (url.contains(str)) {//Url有问号
                            URL = url + "&username=" + username + "&access_token=" + accessToken + param;
                        } else {
                            URL = url + "?username=" + username + "&access_token=" + accessToken + param;
                        }
                        LinkParseUtil.parse(mActivity, oauthType, URL, "");
                    }

                    @Override
                    public void onFailed(String Message) {
                    }
                });
            } else if ("3".equals(oauthType)) {//3：oauth2.0授权，
                LinkParseUtil.parse(mActivity, oauthType, url, "");
            } else if ("4".equals(oauthType)) {//4：原生js授权
                LinkParseUtil.parse(mActivity, oauthType, url, "");
            } else if ("5".equals(oauthType)) {//5：支持所有授权方式
                homeService.getAuth2(new GetTwoRecordListener<String, String>() {

                    @Override
                    public void onFinish(String username, String accessToken, String Expire) {
                        String str = "?";
                        String URL;
                        if (url.contains(str)) {//Url有问号
                            URL = url + "&username=" + username + "&access_token=" + accessToken + param;
                        } else {
                            URL = url + "?username=" + username + "&access_token=" + accessToken + param;
                        }
                        LinkParseUtil.parse(mActivity, oauthType, URL, "");
                    }

                    @Override
                    public void onFailed(String Message) {
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
