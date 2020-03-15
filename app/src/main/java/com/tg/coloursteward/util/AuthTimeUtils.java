package com.tg.coloursteward.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.tg.coloursteward.activity.MyBrowserActivity;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.view.dialog.ToastFactory;

/**
 * 应用授权判断时间是否过期
 */

public class AuthTimeUtils {
    private Intent intent;
    private Activity mActivity;
    private HomeService homeService;

    public void IsAuthTime(Activity mActivity, final String url, String oauthType, final String param) {
        this.mActivity = mActivity;
        getAuth(url, oauthType, param);

    }

    /**
     * 应用授权
     *
     * @param url
     * @param oauthType
     */
    private void getAuth(final String url, String oauthType, final String param) {
        if (homeService == null) {
            homeService = new HomeService(mActivity);
        }
        if (url.startsWith("colourlife://proto")) {
            LinkParseUtil.parse(mActivity, url, "");
        } else if (!TextUtils.isEmpty(oauthType)) {
            if ("0".equals(oauthType)) {//oauth1认证
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
            } else if ("1".equals(oauthType)) {
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
                        intent = new Intent(mActivity, MyBrowserActivity.class);
                        intent.putExtra(MyBrowserActivity.KEY_URL, URL);
                        mActivity.startActivity(intent);
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
