package com.tg.coloursteward.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tg.coloursteward.BuildConfig;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.entity.H5OauthEntity;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.Oauth2CallBack;
import com.tg.coloursteward.model.H5OauthModel;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.serice.OAuth2ServiceUpdate;
import com.tg.coloursteward.util.FileSizeUtil;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.Helper;
import com.tg.coloursteward.util.LinkParseUtil;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.util.Utils;
import com.tg.coloursteward.view.X5WebView;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import static com.tg.coloursteward.activity.RedpacketsTransferToColleagueH5Activity.Failed_MESSAGE;

/**
 * 浏览器BaseActivity
 *
 * @author Administrator
 */
public class MyBrowserActivity extends BaseActivity implements OnClickListener, AMapLocationListener, HttpResponse {
    public static final String ACTION_FRESH_PAYINFO = "com.tg.coloursteward.ACTION_FRESH_PAYINFO";
    public static final String PAY_STATE = "pay_state";
    public static final int PIC_PHOTO_BY_CAMERA = 1010;
    private final String TAG = "MyBrowserActivity";
    public static final String KEY_HIDE_TITLE = "hide";
    public static final String KEY_TITLE = "title";
    public static final String KEY_HTML_TEXT = "text";
    public static final String KEY_URL = "url";
    public static final String WEBDOMAIN = "webdomain";
    public static final String THRIDSOURCE = "thridsource";
    public static final String OAUTH2_0 = "oauth2";
    private String oauth2_0 = "";
    private String domainName;
    private String TAKE_PHOTO_PATH = "";
    private Uri uri;
    protected X5WebView webView;
    private RelativeLayout rlHeadContent;
    private RelativeLayout rlRollback;
    private RelativeLayout rlRefresh;
    private RelativeLayout rlClose;
    protected String htmlText;
    private TextView tvTitle;
    private String url;
    private final static int STOP = 0x10000;
    private final static int NEXT = 0x10001;
    private int count = 0;
    private ProgressBar bar;
    private String title;
    private Intent data;
    private Boolean hideTitle;
    private ProgressDialog mDialog;
    private ValueCallback<Uri> uploadFile;
    private ValueCallback<Uri[]> uploadFiles;
    public static boolean forepriority = false;
    private GetDeviceIdReceiver deviceIdReceiver;
    private static final int OLD_FILE_SELECT_CODE = 6;
    private static final int FILE_SELECT_CODE = 4;
    private String imeis;
    private String urlFromA;
    private Map<String, String> headerMap;
    private File updateFile;
    private String color_token;
    private View customView;
    private FrameLayout fullscreenContainer;
    private IX5WebChromeClient.CustomViewCallback customViewCallback;
    private String response_type;
    private String app_id;
    private H5OauthModel h5OauthModel;
    private AlertDialog dialog;
    private boolean isOauth2Show = false;
    private boolean isJSOauthShow = false;
    private RelativeLayout rl_wechat;
    private RelativeLayout rl_pyq;
    private FrameLayout webview_frame_share;
    private TextView tv_web_cancel;
    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_FRESH_PAYINFO)) {
                int state = intent.getIntExtra(PAY_STATE, -1);
                if (state != -1) {
                    String url = "";
                    webView.loadUrl(url, headerMap);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        forepriority = true;
        h5OauthModel = new H5OauthModel(this);
        registScanResultReceiver();
        registGetDeviceIDReceiver();
        data = getIntent();
        if (data != null) {
            hideTitle = data.getBooleanExtra(KEY_HIDE_TITLE, false);
            htmlText = data.getStringExtra(KEY_HTML_TEXT);
            url = data.getStringExtra(KEY_URL);
            urlFromA = data.getStringExtra(Failed_MESSAGE);//未支付成功的返回信息
            domainName = data.getStringExtra(WEBDOMAIN);
            oauth2_0 = data.getStringExtra(OAUTH2_0);
        }
        if (!TextUtils.isEmpty(oauth2_0)) {
            if (oauth2_0.equals("3")) {
                isOauth2Show = true;
            } else if (oauth2_0.equals("4")) {
                isJSOauthShow = true;
            } else if (oauth2_0.equals("5")) {
                isJSOauthShow = true;
                isOauth2Show = true;
            } else {
                isJSOauthShow = true;
                isOauth2Show = true;
            }
        } else {
            isJSOauthShow = true;
            isOauth2Show = true;
        }
        /**
         * 初始化控件
         */
        prepareView();
        // 初始化定位
        initLocation();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_FRESH_PAYINFO);
        registerReceiver(receiver, filter);
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }

    private void prepareView() {
        tv_web_cancel = findViewById(R.id.tv_web_cancel);
        webview_frame_share = findViewById(R.id.webview_frame_share);
        rl_pyq = findViewById(R.id.rl_pyq);
        rl_wechat = findViewById(R.id.rl_wechat);
        webView = (X5WebView) findViewById(R.id.webView);
        bar = (ProgressBar) findViewById(R.id.myProgressBar);
        rlHeadContent = (RelativeLayout) findViewById(R.id.head_content);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        rlRollback = (RelativeLayout) findViewById(R.id.rl_rollback);
        rlRefresh = (RelativeLayout) findViewById(R.id.rl_refresh);
        rlClose = (RelativeLayout) findViewById(R.id.rl_close);
        rlRollback.setOnClickListener(this);
        rlRefresh.setOnClickListener(this);
        rlClose.setOnClickListener(this);
        rl_pyq.setOnClickListener(this);
        rl_wechat.setOnClickListener(this);
        tv_web_cancel.setOnClickListener(this);
        if (hideTitle) {
            rlHeadContent.setVisibility(View.GONE);
        } else {
            title = data.getStringExtra(KEY_TITLE);
            if (!TextUtils.isEmpty(title)) {
                tvTitle.setText(title);
            }
        }
        /**
         * 调用原生界面
         */
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                if (url.startsWith("tel:")) {//打电话
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                } else {
                    view.loadUrl(url, headerMap);
                }
                if (uri.getScheme().equals("js")) {
                    if (uri.getAuthority().equals("GetReturn")) {//返回
                        backPress();
                    } else if (uri.getAuthority().equals("GetRefresh")) {//刷新
                        getRefresh();
                    } else if (uri.getAuthority().equals("GetFinish")) {//关闭
                        finish();
                    }
                    return true;
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                tvTitle.setText(view.getTitle());
            }
        });

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setPluginsEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);// 设置允许访问文件数据
        settings.setLoadsImagesAutomatically(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        try {
            @SuppressLint("MissingPermission")
            String imei = ((TelephonyManager) this.getSystemService(TELEPHONY_SERVICE)).getDeviceId();
            imeis = MD5.getMd5Value(imei).toUpperCase();
            if (TextUtils.isEmpty(imei)) {
                imeis = MD5.getMd5Value(Tools.macAddress()).toLowerCase();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String ua = settings.getUserAgentString();
        settings.setUserAgentString(ua + imeis + "/newHousekeeper");
        webView.setDownloadListener(new MyWebViewDownLoadListener());
        if (!TextUtils.isEmpty(htmlText)) {
            webView.loadDataWithBaseURL(null, htmlText, "text/html", "utf-8",
                    null);
        } else if (!TextUtils.isEmpty(url)) {
            // 设置setWebChromeClient对象
            webView.setWebChromeClient(new XHSWebChromeClient());
            //android调用js
            headerMap = new HashMap<>();
            OAuth2ServiceUpdate serviceUpdate = new OAuth2ServiceUpdate(MyBrowserActivity.this);
            serviceUpdate.getOAuth2Service(UserInfo.employeeAccount, Tools.getPassWordMD5(MyBrowserActivity.this), new Oauth2CallBack() {
                @Override
                public void onData(String access_token) {

                }
            });
            color_token = Tools.getAccess_token2(MyBrowserActivity.this);
            if (isOauth2Show) {
                headerMap.put("color-token", color_token);
            }
            //定义js调用android
            webView.addJavascriptInterface(new JsInteration(), "js");
            webView.addJavascriptInterface(new JsInteration(), "myjava");
            webView.loadUrl(url, headerMap);
        } else if (!TextUtils.isEmpty(urlFromA)) {//信息不为空做处理
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    H5OauthEntity h5OauthEntity = new H5OauthEntity();
                    h5OauthEntity = GsonUtils.gsonToBean(result, H5OauthEntity.class);
                    int oauth_pop = h5OauthEntity.getContent().getOauth_pop();
                    if (1 == oauth_pop) {//1：弹，2：不弹
                        createDialog(h5OauthEntity);
                    } else if (2 == oauth_pop) {
                        String domain = Uri.parse(url).getHost();
                        h5OauthModel.getApplicationOauth(1, app_id, response_type, domain, this);
                    }
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    String dataString = "";
                    JSONObject object = new JSONObject();
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String content = jsonObject.getString("content");
                        JSONObject data = new JSONObject(content);
                        if (response_type.equals("access_token")) {
                            dataString = data.getString("access_token");
                            object.put("access_token", dataString);
                        } else if (response_type.equals("code")) {
                            dataString = data.getString("code");
                            object.put("code", dataString);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data = object.toString();
                    webView.loadUrl("javascript:postResponseType('" + data + "')");
                }
                break;
        }
    }

    private void createDialog(final H5OauthEntity h5OauthEntity) {
        if (dialog == null) {
            Resources resources = getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            dialog = new AlertDialog.Builder(this).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            Window window = dialog.getWindow();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            RelativeLayout layout = (RelativeLayout) LayoutInflater.from(this)
                    .inflate(R.layout.dialog_webview_oauth, null);
            window.setContentView(layout);
            WindowManager.LayoutParams p = window.getAttributes();
            p.width = ((int) (dm.widthPixels - 80 * dm.density));
            window.setAttributes(p);
            ImageView iv_webview_icon = layout.findViewById(R.id.iv_webview_icon);
            ImageView iv_webview_close = layout.findViewById(R.id.iv_webview_close);
            TextView tv_webivew_content = layout.findViewById(R.id.tv_webivew_content);
            CheckBox cb_webview_agree = layout.findViewById(R.id.cb_webview_agree);
            final Button btn_webview_confirm = layout.findViewById(R.id.btn_webview_confirm);
            TextView tv_webivew_agreeurl = layout.findViewById(R.id.tv_webivew_agreeurl);
            String img = h5OauthEntity.getContent().getApplication_icon();
            if (!TextUtils.isEmpty(img)) {
                GlideUtils.loadImageView(MyBrowserActivity.this, h5OauthEntity.getContent().getApplication_icon(), iv_webview_icon);
            } else {
                Glide.with(MyBrowserActivity.this).load(R.drawable.logo).apply(new RequestOptions()).into(iv_webview_icon);
            }
            tv_webivew_content.setText("该服务由" + h5OauthEntity.getContent().getApplication_name() + "提供,向其提供以下权限即可继续操作");
            if (cb_webview_agree.isChecked()) {
                btn_webview_confirm.setBackgroundResource(R.drawable.btn_vwebview_blue);
                btn_webview_confirm.setEnabled(true);
            }
            cb_webview_agree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        btn_webview_confirm.setBackgroundResource(R.drawable.btn_vwebview_blue);
                        btn_webview_confirm.setEnabled(true);
                    } else {
                        btn_webview_confirm.setBackgroundResource(R.drawable.btn_vwebview_gray);
                        btn_webview_confirm.setEnabled(false);
                    }
                }
            });
            iv_webview_close.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    MyBrowserActivity.this.finish();
                }
            });
            btn_webview_confirm.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String domain = Uri.parse(url).getHost();
                    h5OauthModel.getApplicationOauth(1, app_id, response_type, domain, MyBrowserActivity.this);
                    dialog.dismiss();
                }
            });
            tv_webivew_agreeurl.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinkParseUtil.parse(MyBrowserActivity.this, h5OauthEntity.getContent().getAgreement_url(), "");
                }
            });
        }
        dialog.show();

    }

    /**
     * 利用谷歌webView做交互
     */
    public class XHSWebChromeClient extends WebChromeClient {

        @Override
        public View getVideoLoadingProgressView() {
            FrameLayout frameLayout = new FrameLayout(MyBrowserActivity.this);
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            return frameLayout;

        }

        @Override
        public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
            showCustomView(view, customViewCallback);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//播放时横屏幕，如果需要改变横竖屏，只需该参数就行了
        }

        @Override
        public void onHideCustomView() {
            hideCustomView();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//不播放时竖屏
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }

        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String s, GeolocationPermissionsCallback geolocationPermissionsCallback) {
            geolocationPermissionsCallback.invoke(s, true, false);
            super.onGeolocationPermissionsShowPrompt(s, geolocationPermissionsCallback);
        }

        @Override
        public void onProgressChanged(WebView view, final int newProgress) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (newProgress == 0) {
                        if (newProgress < 100) {
                            for (int i = 0; i < 100; i++) {
                                try {
                                    count = i;
                                    Thread.sleep(20);
                                    Message msg = new Message();
                                    msg.what = NEXT;
                                    handler.sendMessage(msg);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else if (newProgress == 100) {
                        count = newProgress;
                        Message msg = new Message();
                        msg.what = STOP;
                        handler.sendMessage(msg);
                    }
                }
            });
            t.start();
            super.onProgressChanged(view, newProgress);
        }

        /**
         * 图片选择器
         *
         * @param uploadMsg
         * @param acceptType
         */
        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            Log.i("test", "openFileChooser 1");
            MyBrowserActivity.this.uploadFile = uploadFile;
            showPhotoSelector();
        }

        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsgs) {
            Log.i("test", "openFileChooser 2");
            MyBrowserActivity.this.uploadFile = uploadFile;
            showPhotoSelector();
        }

        // For Android  > 4.1.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            Log.i("test", "openFileChooser 3");
            MyBrowserActivity.this.uploadFile = uploadFile;
            showPhotoSelector();
        }

        // For Android  >= 5.0
        public boolean onShowFileChooser(com.tencent.smtt.sdk.WebView webView,
                                         ValueCallback<Uri[]> filePathCallback,
                                         WebChromeClient.FileChooserParams fileChooserParams) {
            Log.i("test", "openFileChooser 4:" + filePathCallback.toString());
            MyBrowserActivity.this.uploadFiles = filePathCallback;
            showPhotoSelector();
            return true;
        }

    }

    /**
     * 视频播放全屏
     **/
    private void showCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden();
            return;
        }

        MyBrowserActivity.this.getWindow().getDecorView();

        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        fullscreenContainer = new FullscreenHolder(MyBrowserActivity.this);
        fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
        decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
        customView = view;
        setStatusBarVisibility(false);
        customViewCallback = callback;
    }

    /**
     * 隐藏视频全屏
     */
    private void hideCustomView() {
        if (customView == null) {
            return;
        }
        setStatusBarVisibility(true);
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        decor.removeView(fullscreenContainer);
        fullscreenContainer = null;
        customView = null;
        customViewCallback.onCustomViewHidden();
        webView.setVisibility(View.VISIBLE);
    }

    /**
     * 全屏容器界面
     */
    static class FullscreenHolder extends FrameLayout {
        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }

    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (customView != null) {
                hideCustomView();
            } else if (webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    /**
     * 广播 浏览回调
     */
    private CallBackScanResultReceiver backScanResultReceiver;

    /**
     * 注册服务
     */
    private void registScanResultReceiver() {
        backScanResultReceiver = new CallBackScanResultReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Contants.PARAMETER.CALLBACKSCANRESULT);
        registerReceiver(backScanResultReceiver, filter);
    }

    /**
     * 注册服务
     */
    private void registGetDeviceIDReceiver() {
        deviceIdReceiver = new GetDeviceIdReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Contants.Html5.CALLBACKDeviceID);
        registerReceiver(deviceIdReceiver, filter);

    }

    /**
     * 广播  注册id
     */
    class GetDeviceIdReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String registrationId = Tools.getUUID(MyBrowserActivity.this);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("code", "0");
                jsonObject.put("message", "deviceId");
                JSONObject content = new JSONObject();
                content.put("registrationId", registrationId);
                jsonObject.put("content", content);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            webView.loadUrl("javascript:RegistrationIdCallBack('"
                    + jsonObject.toString() + "')");
        }
    }

    /**
     * 广播  浏览回调
     */
    class CallBackScanResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra("result");
            JSONObject jsonObject = new JSONObject();
            try {
                JSONObject content = new JSONObject();
                content.put("result", result);
                jsonObject.put("content", content);
                jsonObject.put("code", "0");
                jsonObject.put("message", "scan");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            webView.loadUrl("javascript:ScanCallBack('" + jsonObject.toString()
                    + "')");
        }
    }

    /**
     * H5调用原生
     * 并不是在主线程执行
     *
     * @author Administrator
     */
    public class JsInteration {
        @JavascriptInterface
        public void GetWebTitle(String title) {
            tvTitle.setText(title);
        }

        @JavascriptInterface
        public void uploadFile(String json) {
            Helper.setParams(json);
            showOldFileChooser();
        }

        @JavascriptInterface
        public void GetLatLngCallBack() {
            startLocation();
        }

        @JavascriptInterface
        public void GetRegistrationId() {
            Intent intent = new Intent();
            intent.setAction(Contants.Html5.CALLBACKDeviceID);
            sendBroadcast(intent);
        }

        @JavascriptInterface
        public void Upload() {
            showFileChooser();
        }

        @JavascriptInterface
        public void GetShareWechat(String Url, String name) {
            webview_frame_share.setVisibility(View.VISIBLE);
        }

        @JavascriptInterface
        public void GetFinish() {
            finish();
        }

        @JavascriptInterface
        public void GetReturn() {
            backPress();
        }

        @JavascriptInterface
        public void GetRefresh() {
            webView.reload();
        }

        /**
         * 支付页面
         *
         * @param oa_username
         */
        @JavascriptInterface
        public void PostTransfer(String oa_username) {
            Intent intent = new Intent(MyBrowserActivity.this, RedpacketsTransferToColleagueH5Activity.class);
            intent.putExtra(RedpacketsTransferToColleagueH5Activity.OA_USERNAME, oa_username);
            startActivity(intent);
        }

        /**
         * 调起第三方授权登录
         *
         * @param json
         */
        @JavascriptInterface
        public void getResponseType(String json) {
            if (!TextUtils.isEmpty(json) && isJSOauthShow) {
                try {
                    JSONObject jsonObjec = new JSONObject(json);
                    app_id = jsonObjec.getString("app_id");
                    response_type = jsonObjec.getString("response_type");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                initGetInfo(app_id, response_type);
            }
        }


    }

    private void initGetInfo(String app_id, String response_type) {
        if (!TextUtils.isEmpty(app_id)) {
            String domain = Uri.parse(url).getHost();
            h5OauthModel.getAppInfo(0, app_id, response_type, domain, this);
        }
    }

    private void showOldFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"),
                    OLD_FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MyBrowserActivity.this, "请安装文件管理器", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MyBrowserActivity.this, "请安装文件管理器", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * 调用相册
     */
    private void openFileChooseProcess() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "相册"), 0);
    }

    /**
     * 调用相机
     */
    private void openFileChooseCamera() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        //M兆
        int maxMemorySize = maxMemory / (1024 * 1024);
        if (android.os.Build.VERSION.SDK_INT <= 10 || maxMemorySize <= 32) {
            Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraintent,
                    PIC_PHOTO_BY_CAMERA);
        } else {
            if (null != updateFile && updateFile.exists()) {
                updateFile.delete();
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                TAKE_PHOTO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + System.currentTimeMillis() + ".jpg";
                updateFile = new File(TAKE_PHOTO_PATH);
                uri = FileProvider.getUriForFile(MyBrowserActivity.this, BuildConfig.APPLICATION_ID + ".fileProvider", updateFile);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                {
                    TAKE_PHOTO_PATH = Environment.getExternalStorageDirectory() +
                            File.separator + System.currentTimeMillis() + ".jpg";
                    updateFile = new File(TAKE_PHOTO_PATH);
                    uri = Uri.fromFile(updateFile);
                }
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, PIC_PHOTO_BY_CAMERA);
        }
    }

    /**
     * encodeBase64File:(将文件转成base64 字符串).
     *
     * @param path 文件路径
     * @return
     * @throws Exception
     * @author guhaizhou@126.com
     * @since JDK 1.6
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK
                && requestCode == FILE_SELECT_CODE) {
            Uri uri = data.getData();
            String fileUrl = Helper.getFileAbsolutePath(
                    MyBrowserActivity.this, uri);
            Log.e(TAG, "fileUrl = " + fileUrl);
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1, fileUrl.length());
            Log.e(TAG, "fileName = " + fileName);

            try {
                // 选择文件
                String encodeBase64File = encodeBase64File(fileUrl);
                double fileOrFilesSize = FileSizeUtil.getFileOrFilesSize(fileUrl, FileSizeUtil.SIZETYPE_B);
                final JSONObject jsonObject = new JSONObject();
                JSONObject content = new JSONObject();
                content.put("fileName", fileName);
                content.put("encodeBase64File", encodeBase64File);
                jsonObject.put("content", content);
                jsonObject.put("code", "0");
                jsonObject.put("message", "file");
                jsonObject.put("length", fileOrFilesSize);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "jsonObject.toString()=" + jsonObject.toString());
                        webView.loadUrl("javascript:UploadCallBack('"
                                + jsonObject.toString() + "')");
                    }
                });
                Log.e(TAG, jsonObject.toString());
            } catch (Exception e) {
                ToastFactory.showToast(MyBrowserActivity.this, e.getMessage());
                e.printStackTrace();
            }
        } else if (requestCode == OLD_FILE_SELECT_CODE
                && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String file = Helper.getFileAbsolutePath(MyBrowserActivity.this,
                    uri);
            Helper.uploadFile(file, webView);
            ToastFactory.showToast(getApplicationContext(), "后台上传中！");
        } else if (requestCode == 0
                && resultCode == Activity.RESULT_OK) {
            if (null != uploadFile) {
                Uri result = data == null || resultCode != RESULT_OK ? null
                        : data.getData();
                uploadFile.onReceiveValue(result);
                uploadFile = null;
            }
            if (null != uploadFiles) {
                Uri result = data == null || resultCode != RESULT_OK ? null
                        : data.getData();
                uploadFiles.onReceiveValue(new Uri[]{result});
                uploadFiles = null;
            }
        } else if (requestCode == PIC_PHOTO_BY_CAMERA && resultCode == Activity.RESULT_OK) {
            if (null != uploadFile) {
                if (data != null) {
                    Uri result = data == null || resultCode != RESULT_OK ? null
                            : data.getData();
                    uploadFile.onReceiveValue(result);
                    uploadFile = null;
                } else {
                    uploadFile.onReceiveValue(uri);
                    uploadFile = null;
                }
            }
            if (null != uploadFiles) {
                if (data != null) {
                    Uri result = data == null || resultCode != RESULT_OK ? null
                            : data.getData();
                    uploadFiles.onReceiveValue(new Uri[]{result});
                    uploadFiles = null;
                } else {
                    uploadFiles.onReceiveValue(new Uri[]{uri});
                    uploadFiles = null;
                }


            }
        } else {
            if (uploadFile != null) {    //xie ：直接点击取消时，ValueCallback回调会被挂起，需要手动结束掉回调，否则再次点击选择照片无响应
                uploadFile.onReceiveValue(null);
                uploadFile = null;
            }
            if (uploadFiles != null) {    //xie ：直接点击取消时，ValueCallback回调会被挂起，需要手动结束掉回调，否则再次点击选择照片无响应
                uploadFiles.onReceiveValue(null);
                uploadFiles = null;
            }
        }
    }

	/*@Subscribe
    public void onEvent(Object event) {
		final String result = (String) event;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				webView.loadUrl("javascript:uploadCallback('" + result
						+ "')");
				ToastFactory.showToast(MyBrowserActivity.this,"上传成功！");
			}
		});
	}*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_rollback:// 返回
                if (webView.canGoBack()) {
                    if (webView.getUrl().equals(url)) {
                        super.onBackPressed();
                    } else {
                        webView.goBack();
                    }
                } else {
                    finish();
                }
                break;
            case R.id.rl_refresh:// 刷新
                webView.reload();
                break;
            case R.id.rl_close:// 关闭
                MyBrowserActivity.this.finish();
                break;
            case R.id.rl_wechat:
                showShare(Wechat.NAME);
                closeShareLayout();
                break;
            case R.id.rl_pyq:
                showShare(WechatMoments.NAME);
                closeShareLayout();
                break;
            case R.id.tv_web_cancel:
                closeShareLayout();
                break;
        }
    }

    /**
     * 关闭分享布局
     */
    private void closeShareLayout() {
        webview_frame_share.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //EventBus.getDefault().unregister(this);
        if (backScanResultReceiver != null) {
            unregisterReceiver(backScanResultReceiver);
        }
        if (deviceIdReceiver != null) {
            unregisterReceiver(deviceIdReceiver);
        }
        forepriority = false;
        CookieSyncManager.createInstance(getApplicationContext());  //Create a singleton CookieSyncManager within a context
        CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
        cookieManager.removeAllCookie();// Removes all cookies.
        CookieSyncManager.getInstance().sync(); // forces sync manager to sync now

        webView.clearHistory();
        ((ViewGroup) webView.getParent()).removeView(webView);
        webView.stopLoading();
        webView.setWebChromeClient(null);
        webView.setWebViewClient(null);
        webView.destroy();
        webView = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }


    @Override
    protected void onResume() {
        if (webView != null) {
            webView.onResume();
        }
        /*if (!EventBus.getDefault().isRegistered(this))
        {
			EventBus.getDefault().register(this);
		}*/
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        backPress();
    }


    /**
     * 刷新
     */
    private void getRefresh() {
        String url = webView.getUrl().replace("js://GetRefresh?", "");
        webView.loadUrl(url, headerMap);
    }

    /**
     * 返回
     */
    private void backPress() {
        if (webView.canGoBack()) {
            if (webView.getUrl().equals(url)) {
                super.onBackPressed();
            } else {
                webView.goBack();
            }
        } else {
            finish();
        }
    }

    private Handler handler = new Handler() {
        @SuppressWarnings("static-access")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NEXT:
                    if (!Thread.currentThread().interrupted()) {
                        if (count < 100) {
                            bar.setProgress(count);
                        }
                    }
                    break;
                case STOP:
                    bar.setVisibility(View.GONE);
                    break;
            }
        }
    };
    public AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient;

    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void initLocation() {
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
//		mLocationOption.setInterval(2000);
        mLocationOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
    }


    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocation() {
        // 设置定位参数
        //mlocationClient.setLocationOption(mLocationOption);
        // 启动定位
        mlocationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        // 停止定位
        mlocationClient.stopLocation();
    }

    /**
     * 定位监听
     */
    /*AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
		public void onLocationChanged(AMapLocation loc) {
			if (null != loc) {
				//解析定位结果
				String result = Utils.getLocationStr(loc);
				if(result != null){
					//Log.d("print","result="+result);
					stopLocation();
					String url = "javascript:LatLngCallBack('" +result+ "')";
					webView.loadUrl(url);
				}
			}else {

			}
		}
	};*/
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (null != aMapLocation) {
            //解析定位结果
            String result = Utils.getLocationStr(aMapLocation);
            if (result != null) {
                String url = "javascript:LatLngCallBack('" + result + "')";
                webView.loadUrl(url, headerMap);
            }
        } else {
            Log.d("TAG", "定位失败");
        }
    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void destroyLocation() {
        if (null != mlocationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mlocationClient.onDestroy();
            mlocationClient = null;
            mLocationOption = null;
        }

    }

    /**
     * 下载帮助类
     */
    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent,
                                    String contentDisposition, String mimetype, long contentLength) {
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                ToastFactory.showToast(MyBrowserActivity.this, "需要SD卡。");
                return;
            }
            DownloaderTask task;
            if (contentDisposition.contains("fileName=")) {
                String fileName = getName(contentDisposition, "fileName=");
                task = new DownloaderTask(fileName);
            } else if (contentDisposition.contains("filename=")) {
                String fileName = getName(contentDisposition, "filename=");
                task = new DownloaderTask(fileName);
            } else {
                task = new DownloaderTask();
            }
            task.execute(url);
        }

        private String getName(String contentDisposition, String label) {
            int lastIndexOf = contentDisposition.lastIndexOf(label);
            return contentDisposition.substring(lastIndexOf + 9,
                    contentDisposition.length());
        }
    }

    /**
     * 异步下载文件
     *
     * @author Administrator
     */
    private class DownloaderTask extends AsyncTask<String, Void, String> {

        private String fileName;

        public DownloaderTask() {

        }

        public DownloaderTask(String fileName) {
            this.fileName = fileName;
        }

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            if (fileName == null || fileName.equals("")) {
                fileName = Tools.getReallyFileName(url);
            }
            if (fileName.contains("/")) {
                int lastIndexOf = fileName.lastIndexOf("/");
                fileName = fileName.substring(lastIndexOf + 1,
                        fileName.length());
            }
            // UrlDecode解码后文件名
            String DecoderFileName = "";
            try {
                DecoderFileName = URLDecoder.decode(fileName, "UTF-8");
                if (DecoderFileName.contains("=")) {
                    int lastIndexOf2 = DecoderFileName.lastIndexOf("=");
                    DecoderFileName = DecoderFileName.substring(
                            lastIndexOf2 + 1, DecoderFileName.length());
                }
                if (DecoderFileName.contains("UTF-8''")) {
                    DecoderFileName = DecoderFileName.replaceAll("UTF-8''", "");
                }
            } catch (UnsupportedEncodingException e1) {
                // 解码失败后将Url中文件名赋值
                DecoderFileName = fileName;
                e1.printStackTrace();
            }
            InputStream input = null;
            try {
                URL fileUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) fileUrl
                        .openConnection();
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    // 错误提示
                    return null;
                }
                input = conn.getInputStream();
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    File file = new File(Contants.DOWN.DOWNLOAD_DIRECT);
                    if (!file.exists()) {
                        // 创建文件夹
                        file.mkdirs();
                    }
                    if (DecoderFileName.contains("\"")) {
                        DecoderFileName = DecoderFileName.replaceAll("\"", "");
                    }
                    File DownloadDirectory = new File(Contants.DOWN.DOWNLOAD_DIRECT,
                            getNewName(DecoderFileName, 1));
                    if (!DownloadDirectory.exists()) {
                        DownloadDirectory.createNewFile();
                    }

                    try {
                        FileOutputStream fos = new FileOutputStream(
                                DownloadDirectory);
                        byte[] b = new byte[2048];
                        int j = 0;
                        while ((j = input.read(b)) != -1) {
                            fos.write(b, 0, j);
                        }
                        fos.flush();
                        fos.close();
                        try {
                            Intent fileIntent = Tools.getFileIntent(DownloadDirectory);
                            startActivity(fileIntent);
                            Intent.createChooser(fileIntent, "请选择对应的软件打开该附件！");
                        } catch (ActivityNotFoundException e) {
                            // TODO: handle exception
                            ToastFactory.showToast(MyBrowserActivity.this, "附件不能打开，请下载相关软件！");
                        }
                           /* Intent fileIntent = Tools.getFileIntent(DownloadDirectory);
                            startActivity(fileIntent);*/
                        return "下载成功";
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return "下载失败，文件不存在";
                    } catch (IOException e) {
                        Log.e("tag", "IOException:" + e.getMessage());
                        e.printStackTrace();
                        return "下载失败";
                    }
                } else {
                    Log.e("tag", "NO SDCard.");
                }
                return null;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            closeProgressDialog();
            try {
                if (result == null || "下载失败".equals(result)) {
                    ToastFactory.showToast(MyBrowserActivity.this, "连接错误！请稍后再试！");
                    String callback = "下载失败";
                    webView.loadUrl("javascript:TakePhotoCallBack('" + callback
                            + "')");
                    return;
                } else if ("下载成功".equals(result)) {
                    ToastFactory.showToast(MyBrowserActivity.this, "下载成功，已保存到SD卡。");
                    String callback = "下载成功!";
                    webView.loadUrl("javascript:TakePhotoCallBack('" + callback
                            + "')");
                    return;
                } else if ("下载失败，文件不存在".equals(result)) {
                    ToastFactory.showToast(MyBrowserActivity.this, "下载失败，文件不存在！");
                    String callback = "下载失败，文件不存在!";
                    webView.loadUrl("javascript:TakePhotoCallBack('" + callback
                            + "')");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
        }

    }

    private void showProgressDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(MyBrowserActivity.this);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
            mDialog.setMessage("正在下载 ，请等待...");
            mDialog.setIndeterminate(false);// 设置进度条是否为不明确
            mDialog.setCancelable(true);// 设置进度条是否可以按退回键取消
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setOnDismissListener(new OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    mDialog = null;
                }
            });
            mDialog.show();
        }
    }

    private void closeProgressDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    private static String getNewName(String fileName, int i) {
        String end = fileName.substring(fileName.lastIndexOf(".") + 1)
                .toLowerCase();
        String name = fileName.substring(0, fileName.lastIndexOf("."));
        if (1 == i) {
            return name + "." + end;
        }
        String newFile = name + "(" + i + ")." + end;
        File directory = new File(Contants.DOWN.DOWNLOAD_DIRECT);
        File f = new File(directory + newFile);
        if (f.exists()) {
            i++;
            return getNewName(fileName, i);
        } else {
            return newFile;
        }
    }

    /**
     * 分享
     */
    private void showShare(String platform) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setPlatform(platform);
        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle("分享好友");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("快来加入彩管家吧");
        oks.setImageUrl("http://newcgjios.oss-cn-shenzhen.aliyuncs.com/pictures/cgj_logo.png");
        // url在微信、微博，Facebook等平台中使用
        oks.setUrl("http://mapp.colourlife.com/mgj.html");
        MyBrowserActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                oks.setCallback(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        ToastUtil.showShortToast(MyBrowserActivity.this, "分享成功");
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                        ToastUtil.showShortToast(MyBrowserActivity.this, "分享失败");
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        ToastUtil.showShortToast(MyBrowserActivity.this, "取消分享");
                    }
                });
            }
        });
        oks.show(this);
    }

    /**
     * 选择拍照和相册
     */
    AlertDialog photoDialog;

    public void showPhotoSelector() {
        XXPermissions.with(this)
                .permission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        openCarema();
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        ToastUtil.showShortToast(MyBrowserActivity.this, "请到程序设置中打开彩管家拍照权限");
                    }
                });

    }

    private void openCarema() {
        if (photoDialog == null) {
            photoDialog = new AlertDialog.Builder(MyBrowserActivity.this).create();
            photoDialog.show();
            View v = LayoutInflater.from(MyBrowserActivity.this).inflate(
                    R.layout.set_photo_dialog_layout, null);
            v.findViewById(R.id.take_photo).setOnClickListener(
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            openFileChooseCamera();
                            photoDialog.dismiss();
                        }
                    });
            v.findViewById(R.id.choose_photo).setOnClickListener(
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            openFileChooseProcess();
                            photoDialog.dismiss();
                        }
                    });
            v.findViewById(R.id.cancel).setOnClickListener(
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (uploadFile != null) {    //xie ：直接点击取消时，ValueCallback回调会被挂起，需要手动结束掉回调，否则再次点击选择照片无响应
                                uploadFile.onReceiveValue(null);
                                uploadFile = null;
                            }
                            if (uploadFiles != null) {    //xie ：直接点击取消时，ValueCallback回调会被挂起，需要手动结束掉回调，否则再次点击选择照片无响应
                                uploadFiles.onReceiveValue(null);
                                uploadFiles = null;
                            }
                            photoDialog.dismiss();
                        }
                    });
            photoDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (uploadFile != null) {    //xie ：直接点击取消时，ValueCallback回调会被挂起，需要手动结束掉回调，否则再次点击选择照片无响应
                        uploadFile.onReceiveValue(null);
                        uploadFile = null;
                    }
                    if (uploadFiles != null) {    //xie ：直接点击取消时，ValueCallback回调会被挂起，需要手动结束掉回调，否则再次点击选择照片无响应
                        uploadFiles.onReceiveValue(null);
                        uploadFiles = null;
                    }
                }
            });
            Window window = photoDialog.getWindow();
            WindowManager.LayoutParams p = window.getAttributes();
            DisplayMetrics metrics = Tools.getDisplayMetrics(MyBrowserActivity.this);
            p.width = metrics.widthPixels;
            p.gravity = Gravity.BOTTOM;
            window.setAttributes(p);
            window.setContentView(v);
        }
        photoDialog.show();
    }
}
