package com.tg.coloursteward.serice;

import android.content.Context;

import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.HashMap;

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

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(md5_pwd) ) {
            ToastFactory.showToast(context, "参数错误，请稍后重试");
            return;
        }
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", md5_pwd);
        params.put("clientCode", "case");
        params.put("getExpire", "1");
        String url = null;
        HashMap<String, Object> paramsStr = null;
        if (params != null) {
            paramsStr = params.toHashMap();
        } else {
            paramsStr = null;
        }
        try {
            url = Contants.URl.URL_ICETEST + HttpTools.GetUrl(Contants.URl.URL_ICETEST, "/auth", paramsStr);
            AuthThread authThead = new AuthThread(url, listener);
            authThead.start();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 获取用户应用权限（Oauth2）  access_token
     */
    public void getAuth2(String developerCode, final GetTwoRecordListener<String, String> listener) {
        String username = UserInfo.employeeAccount;
        String md5_pwd = Tools.getPassWordMD5(context);

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(md5_pwd) ) {
            ToastFactory.showToast(context, "参数错误，请稍后重试");
            return;
        }
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
            Auth2Thread auth2Thread = new Auth2Thread(url, listener);
            auth2Thread.start();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Auth 1.0
     *
     * @author Administrator
     */
    class AuthThread extends Thread {
        String url;
        GetTwoRecordListener<String, String> listener;

        public AuthThread(String url, GetTwoRecordListener<String, String> listener) {
            this.url = url;
            this.listener = listener;
        }

        public void run() {
            HttpGet getMethod = new HttpGet(url);//将URL与参数拼接
            HttpClient httpClient = MyHttpClient.getNewHttpClient();
            String response = "";
            try {
                HttpResponse mHttpResponse = httpClient.execute(getMethod); //发起GET请求
                int code = mHttpResponse.getStatusLine().getStatusCode();
                if (code == 200) {
                    response = EntityUtils.toString(mHttpResponse.getEntity(), "utf-8");
                    int codeInt = HttpTools.getCode(response);
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
                            listener.onFailed("获取认证参数失败！");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    /**
     * Auth  2.0
     *
     * @author Administrator
     */
    class Auth2Thread extends Thread {
        String url;
        GetTwoRecordListener<String, String> listener;

        public Auth2Thread(String url, GetTwoRecordListener<String, String> listener) {
            this.url = url;
            this.listener = listener;
        }

        public void run() {
            HttpGet getMethod = new HttpGet(url);//将URL与参数拼接
            HttpClient httpClient = MyHttpClient.getNewHttpClient();
            String response = "";
            try {
                HttpResponse mHttpResponse = httpClient.execute(getMethod); //发起GET请求
                int code = mHttpResponse.getStatusLine().getStatusCode();
                if (code == 200) {
                    response = EntityUtils.toString(mHttpResponse.getEntity(), "utf-8");
                    int codeInt = HttpTools.getCode(response);
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
                            listener.onFailed("获取认证参数失败！");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

}