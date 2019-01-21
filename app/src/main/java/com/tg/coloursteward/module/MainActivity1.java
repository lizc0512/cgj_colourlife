package com.tg.coloursteward.module;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.AddEntityRequest;
import com.baidu.trace.api.entity.AddEntityResponse;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.entity.SearchRequest;
import com.baidu.trace.api.entity.SearchResponse;
import com.baidu.trace.api.entity.UpdateEntityRequest;
import com.baidu.trace.api.entity.UpdateEntityResponse;
import com.baidu.trace.model.LocationMode;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.tg.coloursteward.InviteRegisterActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.application.CityPropertyApplication;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.database.SharedPreferencesTools;
import com.tg.coloursteward.entity.SingleDeviceLogin;
import com.tg.coloursteward.entity.SingleDeviceLogout;
import com.tg.coloursteward.fragment.FragmentManagement1;
import com.tg.coloursteward.fragment.FragmentMine;
import com.tg.coloursteward.info.GridViewInfo;
import com.tg.coloursteward.info.HomeDeskTopInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.Oauth2CallBack;
import com.tg.coloursteward.log.Logger;
import com.tg.coloursteward.module.contact.ContactsFragment;
import com.tg.coloursteward.module.meassage.MsgListFragment;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.AppAuthService;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.serice.OAuth2ServiceUpdate;
import com.tg.coloursteward.updateapk.ApkInfo;
import com.tg.coloursteward.updateapk.UpdateManager;
import com.tg.coloursteward.util.AuthTimeUtils;
import com.tg.coloursteward.util.ExampleUtil;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.LinkParseUtil;
import com.tg.coloursteward.util.TokenUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.PopWindowView;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.http.OkHttpConnector;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.view.camera.util.FileUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import q.rorbin.badgeview.QBadgeView;

import static com.tg.coloursteward.application.CityPropertyApplication.lbsTraceClient;
import static com.tg.coloursteward.application.CityPropertyApplication.serviceId;
import static com.tg.coloursteward.application.CityPropertyApplication.trace;
import static com.tg.coloursteward.constant.Contants.URl.cqj_appid;
import static com.tg.coloursteward.constant.Contants.URl.environment;


/**
 * Created by colin on 2018/3/15.
 */

public class MainActivity1 extends BaseActivity implements MessageHandler.ResponseListener, View.OnClickListener {

    private static final String TAG = MainActivity1.class.getSimpleName();

    public static final String ACTION_FRESH_USERINFO = "com.tg.coloursteward.ACTION_FRESH_USERINFO";
    public static final String ACTION_TICKET_INFO = "com.tg.coloursteward.ACTION_TICKET_INFO";
    public static final String ACTION_HOME_DELETEINFO = "com.tg.coloursteward.ACTION_HOME_DELETEINFO";
    public static final String ACTION_ACCOUNT_INFO = "com.tg.coloursteward.ACTION_ACCOUNT_INFO";
    public static final String ACTION_READ_MESSAGEINFO = "com.tg.coloursteward.ACTION_READ_MESSAGEINFO";
    public static final String ACTION_UPDATE_PUSHINFO = "com.tg.coloursteward.ACTION_UPDATE_PUSHINFO";
    public static final String JUMPOTHERURL = "jumpotherurl";
    public static final String KEY_NEDD_FRESH = "need_fresh";
    public static final String KEY_SKIN_CODE = "skin_code";
    public static final String KEY_EXTRAS = "extras";
    public static final String FROM_LOGIN = "from_login";
    public static final String FROM_AD = "from_ad";
    public static final String FROM_AUTH_TYPE = "from_autn_type";
    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;

    private final int REQUESTPERMISSION = 110;

    private ImageView img_scan;
    private TextView tv_title;
    private ImageView img_add;

    private ViewPager mViewPager;
    private BottomNavigationViewEx navigation;
    //private TabLayout mTabLayout;
    private QBadgeView badgeView;

    private TabFragmentPagerAdapter mAdapter;
    private FragmentManager fragmentManager;


    private Context mContext;
    private NormalHandler mHandler;
    private AuthTimeUtils mAuthTimeUtils;
    private MessageHandler msgHand;

    private AuthAppService authAppService;//2.0授权
    private AppAuthService appAuthService;//1.0授权

    private HomeService homeService;

    private boolean needGetUserInfo = true;

    private String skin_code = "101";//  101 彩生活  100 通用  102 中住
    private String extras;
    private Boolean form_login = false;
    private String urlAd = "";
    private String urlauth_type = "";
    public static String auth_type = "";
    public static String url_ad;

    private BroadcastReceiver freshReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_HOME_DELETEINFO)) {//首页删除消息
                HomeDeskTopInfo info = (HomeDeskTopInfo) intent.getSerializableExtra("info");
                String source_id = info.source_id;
                String comefrom = info.comefrom;
                postDeleteListItem(source_id, comefrom);
            } else if (action.equals(ACTION_ACCOUNT_INFO)) {//刷新即时分成金额
                getAccountInfo();
            } else if (action.equals(ACTION_READ_MESSAGEINFO)) {//首页列表消息设置为已读
                String client_code = intent.getStringExtra("messageId");
                getReadMessageInfo(client_code);
            } else if (action.equals(ACTION_UPDATE_PUSHINFO)) {//更新推送首页消息列表
                getUpdatePushInfo();
            }
        }
    };
    private UpdateEntityRequest updateEntityRequest;
    private AddEntityRequest addEntityRequest;
    private SearchRequest searchRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        mContext = this;
        mHandler = new NormalHandler(this);
        windowPermission();
        String key = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.key);
        String secret = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.secret);
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(secret)) {
            getKeyAndSecret();
        }
        Intent data = getIntent();
        if (data != null) {
            urlAd = data.getStringExtra(FROM_AD);
            urlauth_type = data.getStringExtra(FROM_AUTH_TYPE);
            needGetUserInfo = data.getBooleanExtra(KEY_NEDD_FRESH, true);
            skin_code = data.getStringExtra(KEY_SKIN_CODE);
            extras = data.getStringExtra(KEY_EXTRAS);
            form_login = data.getBooleanExtra(FROM_LOGIN, false);
            String urlFromOther = data.getStringExtra(JUMPOTHERURL);
            if (!TextUtils.isEmpty(urlFromOther)) {
                LinkParseUtil.parse(MainActivity1.this, urlFromOther, "");
            }
            if (!TextUtils.isEmpty(urlAd)) {
                AuthTimeUtils authTimeUtils = new AuthTimeUtils();
                authTimeUtils.IsAuthTime(MainActivity1.this, urlAd, "", urlauth_type, "", "");
            }
        }
        msgHand = new MessageHandler(this);
        msgHand.setResponseListener(this);
        initTitle();
        initView();
        getTokenInfo();
        initPush();

        initProto();

        reqSearchList();
        HuxinSdkManager.instance().getStackAct().addActivity(this);
        if (Contants.URl.environment.equals("release")) {
            RequestConfig config = new RequestConfig(this, HttpTools.GET_YINGYAN, "");
            Map<String, Object> map = new HashMap<>();
            Map<String, String> params = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(this, map));
            HttpTools.httpGet_Map(Contants.URl.URL_NEW, "app/home/utility/getEagleJuge", config, (HashMap) params);
        }
        initAd();
    }

    private void initAd() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_AD, "");
        Map<String, Object> map = new HashMap<>();
        Map<String, String> params = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(this, map));
        HttpTools.httpGet_Map(Contants.URl.URL_NEW, "app/home/utility/startPage", config, (HashMap) params);
    }

    private void initYingYan() {
        //初始化鹰眼SDK
        trace = new Trace(serviceId, UserInfo.uid, false);
        lbsTraceClient = new LBSTraceClient(getApplicationContext());
        int gatherInterval = 60;
        int packInterval = 60;
        lbsTraceClient.setInterval(gatherInterval, packInterval);
        lbsTraceClient.setLocationMode(LocationMode.High_Accuracy);
        updateEntityRequest = new UpdateEntityRequest();
        updateEntityRequest.setEntityName(UserInfo.uid);
        updateEntityRequest.setServiceId(serviceId);
        updateEntityRequest.setEntityDesc(UserInfo.jobName + "-" + UserInfo.realname + "-" + UserInfo.employeeAccount);//岗位-姓名-oa账号
        Map<String, String> map = new HashMap<>();
        map.put("realname", UserInfo.realname);
        map.put("oa_username", UserInfo.employeeAccount);
        map.put("mobile", UserInfo.mobile);
        map.put("gender", UserInfo.sex);
        updateEntityRequest.setColumns(map);
        OnTraceListener onTraceListener = new OnTraceListener() {
            @Override
            public void onBindServiceCallback(int i, String s) {
                String mes = s;
                if (i == 0) {
                    lbsTraceClient.startGather(null);
                }
            }

            @Override
            public void onStartTraceCallback(int i, String s) {
                String mes = s;

            }

            @Override
            public void onStopTraceCallback(int i, String s) {
                String mes = s;
            }

            @Override
            public void onStartGatherCallback(int i, String s) {
                String mes = s;
            }

            @Override
            public void onStopGatherCallback(int i, String s) {
                String mes = s;
            }

            @Override
            public void onPushCallback(byte b, PushMessage pushMessage) {
                String mes = String.valueOf(b);
            }

            @Override
            public void onInitBOSCallback(int i, String s) {
                String mes = s;
            }
        };
        lbsTraceClient.setOnTraceListener(onTraceListener);
        lbsTraceClient.startTrace(trace, null);

        addEntityRequest = new AddEntityRequest();
        addEntityRequest.setEntityName(UserInfo.uid);
        addEntityRequest.setServiceId(serviceId);
        addEntityRequest.setEntityDesc(UserInfo.jobName + "-" + UserInfo.realname + "-" + UserInfo.employeeAccount);//岗位-姓名-oa账号
        addEntityRequest.setColumns(map);

        searchRequest = new SearchRequest();
        searchRequest.setKeyword(UserInfo.uid);
        searchRequest.setServiceId(serviceId);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != lbsTraceClient) {
                    lbsTraceClient.searchEntity(searchRequest, new OnEntityListener() {
                        @Override
                        public void onSearchEntityCallback(SearchResponse searchResponse) {
                            super.onSearchEntityCallback(searchResponse);
                            if (searchResponse.getTotal() == 0) {
                                lbsTraceClient.addEntity(addEntityRequest, new OnEntityListener() {
                                    @Override
                                    public void onAddEntityCallback(AddEntityResponse addEntityResponse) {
                                        super.onAddEntityCallback(addEntityResponse);
                                    }
                                });
                            } else {
                                lbsTraceClient.updateEntity(updateEntityRequest, new OnEntityListener() {
                                    @Override
                                    public void onUpdateEntityCallback(UpdateEntityResponse updateEntityResponse) {
                                        super.onUpdateEntityCallback(updateEntityResponse);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }, 4000);

    }

    private void initGetToken() {
        OAuth2ServiceUpdate oAuth2ServiceUpdate = null;
        if (null == oAuth2ServiceUpdate) {
            oAuth2ServiceUpdate = new OAuth2ServiceUpdate(MainActivity1.this);
        }
        oAuth2ServiceUpdate.getOAuth2Service(UserInfo.employeeAccount, Tools.getPassWordMD5(MainActivity1.this), new Oauth2CallBack() {
            @Override
            public void onData(String access_token) {

            }
        });
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }


    public void windowPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, Activity.RESULT_FIRST_USER);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (null != intent) {
            String urlFromOther = intent.getStringExtra(JUMPOTHERURL);
            if (!TextUtils.isEmpty(urlFromOther)) {
                LinkParseUtil.parse(MainActivity1.this, urlFromOther, "");
            }
            String urlAd = intent.getStringExtra(FROM_AD);
            if (!TextUtils.isEmpty(urlAd)) {
                AuthTimeUtils authTimeUtils = new AuthTimeUtils();
                authTimeUtils.IsAuthTime(MainActivity1.this, urlAd, "", urlauth_type, "", "");
            }
        }
        String extras = intent.getStringExtra(KEY_EXTRAS);
        if (extras != null) {
            try {
                JSONObject jsonObject = new JSONObject(extras);
                String client_code = jsonObject.getString("client_code");
                String msgid = jsonObject.getString("msgid");
                String auth_type = jsonObject.getString("auth_type");
                String msgtype = jsonObject.getString("msgtype");
                String url = jsonObject.getString("url");
                mAuthTimeUtils = new AuthTimeUtils();
                mAuthTimeUtils.IsAuthTime(this, url, client_code, auth_type, client_code, "");
                intent = new Intent(ACTION_READ_MESSAGEINFO);
                intent.putExtra("messageId", client_code);
                sendBroadcast(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        initGetToken();
        refreshUnReadCount();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(freshReceiver);

        HuxinSdkManager.instance().getStackAct().finishActivity(this);
    }

    private void getKeyAndSecret() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_KEYSECERT, null);
        HttpTools.httpPost(Contants.URl.URL_CPMOBILE, "/1.0/auth", config, null);
    }

    public void refreshUnReadCount() {
        int unreadCount = IMMsgManager.instance().getAllBadgeCount();
        if (unreadCount > 0) {
            if (unreadCount > 99) {
                badgeView.setBadgeText("...");
            } else {
                badgeView.setBadgeNumber(unreadCount);
            }

        } else {
            badgeView.hide(true);
        }
    }

    private void initTitle() {

    }


    @Override
    public void onRequestStart(Message msg, String hintString) {

    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        int code = HttpTools.getCode(jsonString);
        if (msg.arg1 == HttpTools.GET_VERSION_INFO) {//版本更新
            JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
            String content = HttpTools.getContentString(jsonString);
            if (code == 0 && null != jsonObject) {
                try {
                    int apkCode = jsonObject.getInt("result");
                    ResponseData data = HttpTools.getResponseKey(content, "info");
                    JSONArray func = data.getJSONArray(0, "func");
                    String apkVersion = data.getString(0, "version");
                    String apkSize = data.getString(0, "size");
                    String downloadUrl = data.getString(0, "download_url");
                    String apkLog = "";
                    if (func != null) {
                        for (int i = 0; i < func.length(); i++) {
                            apkLog += func.get(i) + "\n";
                        }
                    }
                    ApkInfo apkinfo = new ApkInfo(downloadUrl, apkVersion, apkSize, apkCode, "", apkLog);
                    if (apkinfo != null) {
                        SharedPreferences mySharedPreferences = getSharedPreferences("versions", 0);
                        SharedPreferences.Editor editor = mySharedPreferences.edit();
                        editor.putString("versionShort", apkVersion);
                        editor.commit();
                        UpdateManager manager = new UpdateManager(this, true);
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            //申请权限
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTPERMISSION);
                            ToastFactory.showToast(this, "请允许权限进行下载安装");
                        } else {
                            manager.checkUpdate(apkinfo, false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {

            }
        } else if (msg.arg1 == HttpTools.POST_SINGLEDEVICE) {
            if (code == 0) {
                try {
                    SingleDeviceLogin singleDeviceLogin = GsonUtils.gsonToBean(jsonString, SingleDeviceLogin.class);
                    String device_token = singleDeviceLogin.getContent().getDevice_token();
                    Tools.saveStringValue(this, Contants.storage.DEVICE_TOKEN, device_token);
                    if (!TextUtils.isEmpty(device_token)) {
                        Log.d("lizc", TAG + "单设备登录OK");
                    }
                } catch (Exception e) {
                }
            }
        } else if (msg.arg1 == HttpTools.POST_LOGOUTDEVICE) {
            if (code == 0) {
                try {
                    SingleDeviceLogout singleDeviceLogout = GsonUtils.gsonToBean(jsonString, SingleDeviceLogout.class);
                    String jsonObject = singleDeviceLogout.getContent().getResult();
                    if ("1".equals(jsonObject)) {
                        Log.d("lizc", TAG + "单设备退出OK");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (msg.arg1 == HttpTools.GET_ORG_TYPE) {//得到个人orgtype
            if (code == 0) {
                JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
                if (jsonObject.length() > 0) {
                    try {
                        String orgType = jsonObject.getString("orgType");
                        Tools.saveStringValue(MainActivity1.this, Contants.storage.ORGTYPE, orgType);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
            }
        } else if (msg.arg1 == HttpTools.GET_KEYSECERT) {
            if (code == 0) {
                try {
                    String jsonObject = HttpTools.getContentString(jsonString);
                    JSONObject sonJon = new JSONObject(jsonObject);
                    String key = sonJon.optString("key");
                    String secret = sonJon.optString("secret");
                    Tools.saveStringValue(MainActivity1.this, Contants.EMPLOYEE_LOGIN.key, key);
                    Tools.saveStringValue(MainActivity1.this, Contants.EMPLOYEE_LOGIN.secret, secret);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (msg.arg1 == HttpTools.POST_USERSYNC) {
            if (code == 0) {
            }
        } else if (msg.arg1 == HttpTools.GET_YINGYAN) {
            if (code == 0) {
                JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
                try {
                    int isopen = jsonObject.getInt("switch");//1:开启，2关闭
                    if (isopen == 1) {
                        initYingYan();
                    } else if (isopen == 2) {
                        if (null != lbsTraceClient) {
                            lbsTraceClient.stopGather(null);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (msg.arg1 == HttpTools.GET_AD) {
            if (code == 0) {
                JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
                if (null != jsonObject) {
                    Tools.saveStringValue(MainActivity1.this, Contants.storage.HomePageAd, jsonString);
                    String urlImg = "";
                    try {
                        urlImg = jsonObject.getString("adUrl");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (!TextUtils.isEmpty(urlImg)) {
                        download(urlImg);
                    }
                }

            }
        }
    }

    // 保存图片到手机
    public void download(final String url) {
        new AsyncTask<Void, Integer, File>() {

            @Override
            protected File doInBackground(Void... params) {
                File file = null;
                try {
                    FutureTarget<File> future = Glide
                            .with(MainActivity1.this)
                            .load(url)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

                    file = future.get();
                    // 首先保存图片
                    File pictureFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile();
                    File appDir = new File(pictureFolder, "Colourlife");
                    if (!appDir.exists()) {
                        appDir.mkdirs();
                    }
                    String fileName = "";
                    if (url.endsWith(".gif")) {
                        fileName = "colourlifeAd.gif";
                        Tools.saveStringValue(MainActivity1.this, Contants.storage.ImageType, "gif");
                    } else {
                        fileName = "colourlifeAd.png";
                        Tools.saveStringValue(MainActivity1.this, Contants.storage.ImageType, "png");
                    }
                    File destFile = new File(appDir, fileName);
                    FileUtil.copy(file, destFile);

                    // 最后通知图库更新
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.fromFile(new File(destFile.getPath()))));


                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                return file;
            }

            @Override
            protected void onPostExecute(File file) {
//                Toast.makeText(MainActivity1.this, "saved in Pictures/GankBeauty", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }
        }.execute();
    }

    private void initInfoSync() {
        RequestConfig config = new RequestConfig(this, HttpTools.POST_USERSYNC, null);
        RequestParams params = new RequestParams();
        params.put("user_uuid", UserInfo.uid);
        params.put("user_name", UserInfo.employeeAccount);
        params.put("mobile", UserInfo.mobile);
        params.put("area_uuid", UserInfo.orgId);
        params.put("area_name", UserInfo.familyName);
        params.put("os", "android");
        params.put("jpush_alias", "cgj_" + UserInfo.employeeAccount);
        params.put("platform_uuid", "2fe08211ef974089831ccadcd98895ca");
        HttpTools.httpPost(Contants.URl.URL_IMPUSH, "api/app/userSync", config, params);
    }

    @Override
    public void onFail(Message msg, String hintString) {
    }

    private void initView() {
        img_scan = (ImageView) findViewById(R.id.img_scan);
        tv_title = (TextView) findViewById(R.id.tv_title);
        img_add = (ImageView) findViewById(R.id.img_add);

        img_scan.setOnClickListener(this);
        img_add.setOnClickListener(this);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        navigation = (BottomNavigationViewEx) findViewById(R.id.navigation);

        badgeView = new QBadgeView(mContext);
        badgeView.bindTarget(navigation.getBottomNavigationItemView(0));
        badgeView.setBadgeGravity(Gravity.TOP | Gravity.END);
        badgeView.setBadgeTextSize(10f, true);
        badgeView.setBadgeBackgroundColor(ContextCompat.getColor(mContext, R.color.hx_color_red_tag));
        badgeView.setGravityOffset(25, 2, true);
        badgeView.setBadgePadding(5, true);
        badgeView.setShowShadow(false);
        badgeView.hide(false);


        //mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

        fragmentManager = getSupportFragmentManager();
        mAdapter = new TabFragmentPagerAdapter(fragmentManager, navigation.getItemCount());

        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_title.setText(navigation.getMenu().getItem(position).getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        navigation.enableShiftingMode(false); //取消切换动画
        navigation.enableItemShiftingMode(false); //取消文字
        navigation.enableAnimation(false);  //取消选中动画
        navigation.setupWithViewPager(mViewPager);

        navigation.setOnNavigationItemReselectedListener(
                new BottomNavigationView.OnNavigationItemReselectedListener() {
                    @Override
                    public void onNavigationItemReselected(@NonNull MenuItem item) {
                        if (AppUtils.isRepeatClick()) {
                            if (mAdapter.getItem(0) instanceof MsgListFragment) {
                                MsgListFragment fragment = (MsgListFragment) mAdapter.instantiateItem(mViewPager, 0);
                                fragment.scrollToNextUnRead();
                            }
                        }
                    }
                });

        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(3);

        tv_title.setText(navigation.getMenu().getItem(0).getTitle());

    }


    private void initPush() {
        /*
         * 设置极光推送别名与标签
         */
        boolean tags = Tools.getBooleanValue(this, Contants.storage.Tags);
        boolean alias = Tools.getBooleanValue(this, Contants.storage.ALIAS);
        Log.d(TAG, "tags=" + tags);
        Log.d(TAG, "alias=" + alias);
        if (!tags) {
            setTag();
        }
        if (alias) {
            initInfoSync();
        } else {
            setAlias();
        }
        CityPropertyApplication.addActivity(this);
    }

    public Handler getHandler() {
        return msgHand.getHandler();
    }

    private void initProto() {
        if (form_login == false) {
            getSlientLogin();
        }
        getAuthAppInfo();//2.0授权
        getAppAuthInfo();//1.0授权
        /**
         * 版本检测更新
         */
        getVersion();
        initView();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_FRESH_USERINFO);
        filter.addAction(ACTION_TICKET_INFO);
        filter.addAction(ACTION_HOME_DELETEINFO);
        filter.addAction(ACTION_ACCOUNT_INFO);
        filter.addAction(ACTION_READ_MESSAGEINFO);
        filter.addAction(ACTION_UPDATE_PUSHINFO);
        registerReceiver(freshReceiver, filter);
        //推送跳转详情
        pushDetail();
    }


    private void pushDetail() {
        if (extras != null) {
            try {
                JSONObject jsonObject = new JSONObject(extras);
                String client_code = jsonObject.getString("client_code");
                String msgid = jsonObject.getString("msgid");
                String auth_type = jsonObject.getString("auth_type");
                String msgtype = jsonObject.getString("msgtype");
                String url = jsonObject.getString("url");
                mAuthTimeUtils = new AuthTimeUtils();
                mAuthTimeUtils.IsAuthTime(this, url, client_code, auth_type, client_code, "");
                Intent intent = new Intent(ACTION_READ_MESSAGEINFO);
                intent.putExtra("messageId", client_code);
                sendBroadcast(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 单设备登录
     */
    private void singleDevicelogin() {
        RequestConfig config = new RequestConfig(this, HttpTools.POST_SINGLEDEVICE, null);
        RequestParams params = new RequestParams();
        params.put("login_type", "1");//登录方式,1静默和2密码
        params.put("device_type", "1");//登录设备类别，1：安卓，2：IOS
        params.put("version", UpdateManager.getVersionName(MainActivity1.this));//APP版本号
        params.put("device_code", TokenUtils.getUUID(MainActivity1.this));//设备唯一编号
        params.put("device_info", TokenUtils.getDeviceInfor(MainActivity1.this));//设备详细信息（json字符创）
        params.put("device_name", TokenUtils.getDeviceBrand() + TokenUtils.getDeviceType());//设备名称（如三星S9）
        HttpTools.httpPost(Contants.URl.SINGLE_DEVICE, "cgjapp/single/device/login", config, params);
    }

    /**
     * 单设备退出
     */
    private void singleDevicelogout() {
        RequestConfig config = new RequestConfig(this, HttpTools.POST_LOGOUTDEVICE, null);
        RequestParams params = new RequestParams();
        String device_code = Tools.getStringValue(this, Contants.storage.DEVICE_TOKEN);
        params.put("device_code", device_code);//
        HttpTools.httpPost(Contants.URl.SINGLE_DEVICE, "cgjapp/single/device/logout", config, params);
    }

    // 检测版本更新
    private void getVersion() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_VERSION_INFO);
        RequestParams params = new RequestParams();
        String version = UpdateManager.getVersionName(this);
        params.put("version", version);
        params.put("type", "android");
        HttpTools.httpGet(Contants.URl.URL_CPMOBILE, "/1.0/version", config, params);
    }

    // 获取即时分成金额
    private void getAccountInfo() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_ACCOUNT_INFO);
        RequestParams params = new RequestParams();
        params.put("access_token", "1521ac83521b8063e7a9a49dc22e79b0");
        params.put("target_type", "2");
        params.put("target", UserInfo.employeeAccount);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/splitdivide/api/account", config, params);
    }

    // 首页列表消息设置为已读
    private void getReadMessageInfo(String client_code) {
        RequestConfig config = new RequestConfig(this, HttpTools.SET_MSG_READ);
        RequestParams params = new RequestParams();
        params.put("client_code", client_code);
        params.put("username", UserInfo.employeeAccount);
        HttpTools.httpPut(Contants.URl.URL_ICETEST, "/push2/homepush/readhomePush", config, params);
    }

    // 更新首页消息推送列表
    private void getUpdatePushInfo() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_UPDATE_PUSH);
        RequestParams params = new RequestParams();
        params.put("username", UserInfo.employeeAccount);
        params.put("corp_id", Tools.getStringValue(this, Contants.storage.CORPID));
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/push2/homepush/gethomePushBybox", config, params);
    }

    private void postDeleteListItem(String source_id, String comefrom) {
        RequestConfig config = new RequestConfig(this, HttpTools.POST_DELETE_INFO, null);
        RequestParams params = new RequestParams();
        params.put("usernames", UserInfo.employeeAccount);
        params.put("source_id", source_id);
        params.put("comefrom", comefrom);
        HttpTools.httpDelete(Contants.URl.URL_ICETEST, "/homelist", config, params);
    }

    /**
     * 重新静默获取用户信息
     */
    private void getSlientLogin() {
        OAuth2ServiceUpdate auth2ServiceUpdate = new OAuth2ServiceUpdate(MainActivity1.this);
        auth2ServiceUpdate.getOAuth2Service(UserInfo.employeeAccount, Tools.getPassWordMD5(mContext), new Oauth2CallBack() {
            @Override
            public void onData(String access_token) {
                getNetInfo(access_token);
            }
        });
    }

    private void getNetInfo(String access_token) {
        ContentValues header = new ContentValues();
        header.put("Authorization", "Bearer " + access_token);
        OkHttpConnector.httpGet(MainActivity1.this, Contants.URl.URL_OAUTH2 + "/oauth/user", header, null, new IGetListener() {
            @Override
            public void httpReqResult(String jsonString) {
                if (null != jsonString) {
                    try {
                        int code = HttpTools.getCode(jsonString);
                        String message = HttpTools.getMessageString(jsonString);
                        if (code == 0) {
                            String response = HttpTools.getContentString(jsonString);
                            ResponseData data = HttpTools.getResponseContentObject(response);
                            Tools.loadUserInfo(data, jsonString);
                            Tools.savetokenUserInfo(MainActivity1.this, jsonString);
                            int status = data.getInt("status");
                            String corpId = data.getString("corp_id");
                            UserInfo.infoorgId = data.getString("org_uuid");
                            UserInfo.employeeAccount = data.getString("username");
                            Tools.saveOrgId(MainActivity1.this, data.getString("org_uuid"));
                            Tools.saveStringValue(MainActivity1.this, Contants.storage.CORPID, corpId);//租户ID
                            if (status == 0) {//账号正常
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        singleDevicelogin();
                                    }
                                }, 2500);
                                if (("101").equals(skin_code)) {//彩生活
                                    sendBroadcast(new Intent(ACTION_TICKET_INFO));
                                }
                            } else {
                                ToastFactory.showToast(MainActivity1.this, "账号异常，请及时联系管理员");
                                singleDevicelogout();
                                SharedPreferencesTools.clearUserId(MainActivity1.this);
                                //清空缓存
                                SharedPreferencesTools.clearCache(MainActivity1.this);
                                SharedPreferencesTools.clearAllData(MainActivity1.this);
                                CityPropertyApplication.gotoLoginActivity(MainActivity1.this);
                                HuxinSdkManager.instance().loginOut();
                            }
                        } else {
                            ToastFactory.showToast(MainActivity1.this, message);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });
    }

    /**
     * 获取token
     * sectet
     */
    private void getTokenInfo() {
        getAuth("", "case", "0", "case", "");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getAuth("", "case", "1", "case", "");
            }
        }, 300);
    }

    /**
     * 获取token（2.0）
     * sectet
     */
    private void getAuthAppInfo() {
        if (authAppService == null) {
            authAppService = new AuthAppService(this);
        }
        authAppService.getAppAuth(new GetTwoRecordListener<String, String>() {
            @Override
            public void onFinish(String jsonString, String data2, String data3) {
                int code = HttpTools.getCode(jsonString);
                if (code == 0) {
                    JSONObject content = HttpTools.getContentJSONObject(jsonString);
                    if (content.length() > 0) {
                        try {
                            String accessToken = content.getString("accessToken");
                            String expireTime = content.getString("expireTime");
                            Tools.saveStringValue(mContext, Contants.storage.APPAUTH, accessToken);
                            Tools.saveStringValue(mContext, Contants.storage.APPAUTHTIME, expireTime);
                            getOrgType(accessToken);//获取个人OrgType
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }

            @Override
            public void onFailed(String Message) {

            }
        });
    }

    /**
     * 获取token（1.0）
     * sectet
     */
    private void getAppAuthInfo() {
        if (appAuthService == null) {
            appAuthService = new AppAuthService(this);
        }
        appAuthService.getAppAuth(new GetTwoRecordListener<String, String>() {
            @Override
            public void onFinish(String jsonString, String data2, String data3) {
                int code = HttpTools.getCode(jsonString);
                if (code == 0) {
                    JSONObject content = HttpTools.getContentJSONObject(jsonString);
                    if (content.length() > 0) {
                        try {
                            String accessToken = content.getString("access_token");
                            String expireTime = content.getString("expire");
                            Tools.saveStringValue(mContext, Contants.storage.APPAUTH_1, accessToken);
                            Tools.saveStringValue(mContext, Contants.storage.APPAUTHTIME_1, expireTime);

                            HuxinSdkManager.instance().setAccessToken(accessToken);
                            HuxinSdkManager.instance().setExpireTime(Long.parseLong(expireTime));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }

            @Override
            public void onFailed(String Message) {

            }
        });
    }

    /**
     * 获取key
     * sectet
     */
    public void getOrgType(String token) {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_ORG_TYPE, null);
        RequestParams params = new RequestParams();
        String corpId = Tools.getStringValue(MainActivity1.this, Contants.storage.CORPID);
        params.put("token", token);
        params.put("orgUuid", UserInfo.orgId);
        params.put("corpId", corpId);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/orgms/org", config, params);
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
        if (homeService == null) {
            homeService = new HomeService(this);
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
                }

                @Override
                public void onFailed(String Message) {
                    // TODO Auto-generated method stub

                }
            });
        }
    }

    /**
     * 极光推送设置
     */
    private void setTag() {

        Set<String> set = new HashSet<String>();
        String s1 = "cgj";
        String s2 = Tools.getStringValue(this, Contants.storage.CORPID);
        set.add(s1);
        set.add(s2);
        //调用JPush API设置Tag
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAGS, set));

    }

    private void setAlias() {
        String alias = "cgj_" + UserInfo.employeeAccount;
        //调用JPush API设置Alias
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    Logger.logd(TAG, "alias   设置成功   code=" + code);
                    Tools.setBooleanValue(MainActivity1.this, Contants.storage.ALIAS, true);
                    initInfoSync();
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    if (ExampleUtil.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }
        }

    };

    private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    Logger.logd(TAG, "tag   设置成功   code=" + code);
                    Tools.setBooleanValue(MainActivity1.this, Contants.storage.Tags, true);
                    break;
                case 6002:
                    if (ExampleUtil.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }
        }

    };


    private ArrayList<GridViewInfo> gridlistAdd = new ArrayList<>();


    private void reqSearchList() {
        String url = Contants.URl.URL_ICETEST + "/newoa/rights/list";
        String pwd = Tools.getPassWord(this);

        ContentValues params = new ContentValues();
        params.put("user_name", UserInfo.employeeAccount);
        params.put("password", pwd);
        params.put("resource", "app");
        params.put("cate_id", 0);
        ColorsConfig.commonParams(params);

        OkHttpConnector.httpPost(MainActivity1.this, url, params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                String jsonString = HttpTools.getContentString(response);
                if (jsonString != null) {
                    ResponseData app_list = HttpTools.getResponseKey(jsonString, "app_list");
                    if (app_list.length > 0) {
                        Tools.saveCommonInfo(mContext, response);
                        JSONArray jsonArray = app_list.getJSONArray(0, "list");
                        ResponseData data = HttpTools.getResponseKeyJSONArray(jsonArray);
                        gridlistAdd = new ArrayList<>();
                        GridViewInfo item;
                        for (int i = 0; i < data.length; i++) {
                            try {
                                item = new GridViewInfo();
                                item.name = data.getString(i, "name");
                                item.oauthType = data.getString(i, "oauthType");
                                item.developerCode = data.getString(i, "app_code");
                                item.clientCode = data.getString(i, "app_code");
                                item.sso = data.getString(i, "url");
                                JSONObject icon = data.getJSONObject(i, "icon");
                                if (icon != null || icon.length() > 0) {
                                    item.icon = icon.getString("android");
                                }
                                gridlistAdd.add(item);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }

    private void lightoff() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.3f;
        getWindow().setAttributes(lp);
    }


    class PopupDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 1.0f;
            getWindow().setAttributes(lp);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img_scan:
                startActivity(new Intent(this, InviteRegisterActivity.class));
                break;
            case R.id.img_add:
                PopWindowView popWindowView = new PopWindowView(this, gridlistAdd);
                popWindowView.setOnDismissListener(new PopupDismissListener());
                popWindowView.showPopupWindow(img_add);
                lightoff();
                break;

        }
    }

    public static LinkedHashMap<String, String> getPublicParams() {
        LinkedHashMap<String, String> publicParams = new LinkedHashMap<>();
        publicParams.put("appId", cqj_appid);
        publicParams.put("outUserId", UserInfo.uid);
        publicParams.put("phone", UserInfo.mobile);
        return publicParams;
    }

    public static Boolean getEnvironment() {
        Boolean isrelease = false;
        if (environment.equals("debug")) {
            isrelease = false;
        } else {
            isrelease = true;
        }
        return isrelease;
    }

    private static class NormalHandler extends android.os.Handler {
        private final WeakReference<MainActivity1> mTarget;

        NormalHandler(MainActivity1 target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity1 act = mTarget.get();
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    JPushInterface.setAliasAndTags(act.getApplicationContext(), (String) msg.obj, null, act.mAliasCallback);
                    break;

                case MSG_SET_TAGS:
                    JPushInterface.setAliasAndTags(act.getApplicationContext(), null, (Set<String>) msg.obj, act.mTagsCallback);
                    break;
                default:
                    break;
            }
        }
    }

    private class TabFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private int mCount;

        private TabFragmentPagerAdapter(FragmentManager fm, int count) {
            super(fm);
            mCount = count;
        }


        @Override
        public Fragment getItem(int arg0) {
            Fragment ft = null;
            switch (arg0) {
                case 0:
                    ft = new MsgListFragment();
                    break;
                case 1:
                    ft = new ContactsFragment();
                    break;
                case 2:
                    ft = new FragmentManagement1();
                    break;
                case 3:
                    ft = new FragmentMine();
                    break;
                default:
                    break;
            }

            return ft;
        }


        @Override
        public int getCount() {
            return mCount;
        }

    }


}
