package com.tg.coloursteward.module;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.amap.api.location.AMapLocation;
import com.appsafekb.safekeyboard.NKeyBoardTextField;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.tencent.bugly.crashreport.CrashReport;
import com.tg.coloursteward.R;
import com.tg.coloursteward.application.CityPropertyApplication;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.entity.HomePopWindowEntity;
import com.tg.coloursteward.fragment.ContactsFragment;
import com.tg.coloursteward.fragment.FragmentManagement;
import com.tg.coloursteward.fragment.FragmentMine;
import com.tg.coloursteward.fragment.MsgListFragment;
import com.tg.coloursteward.info.HomeDeskTopInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.Oauth2CallBack;
import com.tg.coloursteward.model.HomeModel;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.AppAuthService;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.serice.OAuth2ServiceUpdate;
import com.tg.coloursteward.serice.UpdateService;
import com.tg.coloursteward.util.AuthTimeUtils;
import com.tg.coloursteward.util.ExampleUtil;
import com.tg.coloursteward.util.GDLocationUtil;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.LinkParseUtil;
import com.tg.coloursteward.util.MicroAuthTimeUtils;
import com.tg.coloursteward.util.PopupScUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.ViewPagerSlide;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.tg.setting.adapter.UpdateAdapter;
import com.tg.setting.entity.VersionEntity;
import com.tg.setting.model.SettingModel;
import com.tg.setting.view.DeleteMsgDialog;
import com.tg.setting.view.UpdateVerSionDialog;
import com.tg.user.model.UserModel;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.view.camera.util.FileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import q.rorbin.badgeview.QBadgeView;

import static com.tg.coloursteward.constant.Contants.URl.cqj_appid;
import static com.tg.coloursteward.constant.Contants.URl.environment;


/**
 * Created by colin on 2018/3/15.
 */

public class MainActivity extends BaseActivity implements HttpResponse {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String ACTION_TICKET_INFO = "com.tg.coloursteward.ACTION_TICKET_INFO";
    public static final String ACTION_HOME_DELETEINFO = "com.tg.coloursteward.ACTION_HOME_DELETEINFO";
    public static final String ACTION_READ_MESSAGEINFO = "com.tg.coloursteward.ACTION_READ_MESSAGEINFO";
    public static final String ACTION_UPDATE_PUSHINFO = "com.tg.coloursteward.ACTION_UPDATE_PUSHINFO";
    public static final String JUMPOTHERURL = "jumpotherurl";
    public static final String KEY_NEDD_FRESH = "need_fresh";
    public static final String KEY_SKIN_CODE = "skin_code";
    public static final String KEY_EXTRAS = "extras";
    public static final String ISSHOWPOP = "isshowpop";
    public static final String FROM_LOGIN = "from_login";
    public static final String FROM_AD = "from_ad";
    public static final String FROM_AUTH_TYPE = "from_autn_type";
    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;


    private ViewPagerSlide mViewPager;
    private BottomNavigationViewEx navigation;
    //private TabLayout mTabLayout;
    private QBadgeView badgeView;

    private TabFragmentPagerAdapter mAdapter;
    private FragmentManager fragmentManager;


    private Context mContext;
    private NormalHandler mHandler;
    private MicroAuthTimeUtils microAuthTimeUtils;
    private AuthTimeUtils mAuthTimeUtils;
    private MessageHandler msgHand;

    private AuthAppService authAppService;//2.0授权
    private AppAuthService appAuthService;//1.0授权

    private HomeService homeService;

    private boolean needGetUserInfo = true;

    private String skin_code = "101";//  101 彩生活  100 通用  102 中住
    private String extras;
    private String isShowPop = "1";
    private Boolean form_login = false;
    private String urlAd = "";
    private String urlauth_type = "";
    public static String auth_type = "";
    public static String url_ad;
    private SettingModel settingModel;
    private HomeModel homeModel;
    private UserModel userModel;
    private List<String> updateList = new ArrayList<>();
    private BroadcastReceiver freshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_HOME_DELETEINFO)) {//首页删除消息
                HomeDeskTopInfo info = (HomeDeskTopInfo) intent.getSerializableExtra("info");
            } else if (action.equals(ACTION_READ_MESSAGEINFO)) {//首页列表消息设置为已读
                String client_code = intent.getStringExtra("messageId");
                getReadMessageInfo(client_code);
            } else if (action.equals(ACTION_UPDATE_PUSHINFO)) {//更新推送首页消息列表
                getUpdatePushInfo();
            }
        }
    };
    private String downUrl;
    private UpdateVerSionDialog updateDialog;
    private String getVersion;
    private boolean otherPopShow = false;
    private boolean exit = false;//是否退出

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int result = NKeyBoardTextField.setNlicenseKey(Contants.APP.LICENSE_KEY).getStatusCode();
        if (1 == result) {
            spUtils.saveBooleanData(SpConstants.UserModel.ISUSERAJM, true);
        } else {
            spUtils.saveBooleanData(SpConstants.UserModel.ISUSERAJM, false);
        }
        mContext = this;
        mHandler = new NormalHandler(this);
        settingModel = new SettingModel(this);
        homeModel = new HomeModel(this);
        userModel = new UserModel(this);
        userModel.getTs(6, this);
        Intent data = getIntent();
        if (data != null) {
            urlAd = data.getStringExtra(FROM_AD);
            urlauth_type = data.getStringExtra(FROM_AUTH_TYPE);
            needGetUserInfo = data.getBooleanExtra(KEY_NEDD_FRESH, true);
            skin_code = data.getStringExtra(KEY_SKIN_CODE);
            extras = data.getStringExtra(KEY_EXTRAS);
            isShowPop = data.getStringExtra(ISSHOWPOP);

            form_login = data.getBooleanExtra(FROM_LOGIN, false);
            String urlFromOther = data.getStringExtra(JUMPOTHERURL);
            if (!TextUtils.isEmpty(urlFromOther)) {
                LinkParseUtil.parse(MainActivity.this, urlFromOther, "");
            }
            if (!TextUtils.isEmpty(urlAd)) {
                AuthTimeUtils authTimeUtils = new AuthTimeUtils();
                authTimeUtils.IsAuthTime(MainActivity.this, urlAd, urlauth_type, "");
            }
        }
        initView();
        initPush();

        initProto();

        HuxinSdkManager.instance().getStackAct().addActivity(this);
        initAd();
        CheckPermission();
        initGetLocation();
        if (!TextUtils.isEmpty(isShowPop)) {
            if (!isShowPop.equals("2")) {
                initData();
            }
        } else {
            initData();
        }
        CrashReport.putUserData(this, "OA", UserInfo.employeeAccount);
        CrashReport.putUserData(this, "PHONE", UserInfo.mobile);
    }

    private void initData() {
        homeModel.getPopWindow(1, this);
    }

    private void CheckPermission() {
        XXPermissions.with(this)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {

                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {

                    }
                });
    }

    private void initGetLocation() {
        GDLocationUtil.getCurrentLocation(new GDLocationUtil.MyLocationListener() {
            @Override
            public void result(AMapLocation aMapLocation) {
                if (null != aMapLocation) {
                    if (aMapLocation.getErrorCode() == 0) {
                        String str_latitude = String.valueOf(aMapLocation.getLatitude());
                        String str_longitude = String.valueOf(aMapLocation.getLongitude());
                        Tools.saveStringValue(getApplication(), Contants.storage.LATITUDE, str_latitude);
                        Tools.saveStringValue(getApplication(), Contants.storage.LONGITUDE, str_longitude);
                    } else {
                        Log.e("AmapErr", "Location ERR:"
                                + aMapLocation.getErrorCode());
                    }
                }
            }
        });
    }

    private void initAd() {
        homeModel.getAdPager(8, this);
    }

    private void initGetToken() {
        OAuth2ServiceUpdate oAuth2ServiceUpdate = null;
        if (null == oAuth2ServiceUpdate) {
            oAuth2ServiceUpdate = new OAuth2ServiceUpdate(MainActivity.this);
        }
        oAuth2ServiceUpdate.getOAuth2Service(UserInfo.employeeAccount, Tools.getPassWordMD5(MainActivity.this), new Oauth2CallBack() {
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (null != intent) {
            String urlFromOther = intent.getStringExtra(JUMPOTHERURL);
            if (!TextUtils.isEmpty(urlFromOther)) {
                LinkParseUtil.parse(MainActivity.this, urlFromOther, "");
            }
            String urlAd = intent.getStringExtra(FROM_AD);
            if (!TextUtils.isEmpty(urlAd)) {
                AuthTimeUtils authTimeUtils = new AuthTimeUtils();
                authTimeUtils.IsAuthTime(MainActivity.this, urlAd, urlauth_type, "");
            }
        }
        String extras = intent.getStringExtra(KEY_EXTRAS);
        if (extras != null) {
            setMsgStatus(extras);
        }
    }

    private void setMsgStatus(String extras) {
        try {
            JSONObject jsonObject = new JSONObject(extras);
            String msg_id = "";
            String auth_type = "";
            if (jsonObject.has("msg_id")) {
                msg_id = jsonObject.getString("msg_id");
            }
            if (jsonObject.has("auth_type")) {
                auth_type = jsonObject.getString("auth_type");
            }
            String url = jsonObject.getString("url");
            if (null == microAuthTimeUtils) {
                microAuthTimeUtils = new MicroAuthTimeUtils();
            }
            microAuthTimeUtils.IsAuthTime(this, url, auth_type, "");
            Intent intent = new Intent(ACTION_READ_MESSAGEINFO);
            intent.putExtra("messageId", msg_id);
            sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initGetToken();
        refreshUnReadCount();
        initCheckImStatus();
    }

    private void initCheckImStatus() {
        if (!HuxinSdkManager.instance().isConnect()) {
            String uuid = HuxinSdkManager.instance().getUuid();
            if (!TextUtils.isEmpty(uuid)) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("正在重新登录，请稍后...");
                progressDialog.show();

                String ip = AppUtils.getStringSharedPreferences(this, "IP", AppConfig.getSocketHost());
                int port = AppUtils.getIntSharedPreferences(this, "PORT", AppConfig.getSocketPort());

                HuxinSdkManager.instance().close();
                InetSocketAddress isa = new InetSocketAddress(ip, port);
                HuxinSdkManager.instance().connectTcp(uuid, isa);
                HuxinSdkManager.instance().setLoginStatusListener(
                        new HuxinSdkManager.LoginStatusListener() {
                            @Override
                            public void onKickOut() {

                            }

                            @Override
                            public void onReLoginSuccess() {
                                progressDialog.dismiss();
                            }
                        });
            }
        } else {

        }
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

    // 保存图片到手机
    public void download(final String url) {
        new AsyncTask<Void, Integer, File>() {

            @Override
            protected File doInBackground(Void... params) {
                File file = null;
                try {
                    FutureTarget<File> future = Glide
                            .with(MainActivity.this)
                            .load(url)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

                    file = future.get();
                    // 首先保存图片
                    File pictureFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile();
                    File appDir = new File(pictureFolder, "Colourlife");
                    if (!appDir.exists()) {
                        appDir.mkdirs();
                    }
                    String fileName = "";
                    if (url.endsWith(".gif")) {
                        fileName = "colourlifeAd.gif";
                        Tools.saveStringValue(MainActivity.this, Contants.storage.ImageType, "gif");
                    } else {
                        fileName = "colourlifeAd.png";
                        Tools.saveStringValue(MainActivity.this, Contants.storage.ImageType, "png");
                    }
                    File destFile = new File(appDir, fileName);
                    FileUtil.copy(file, destFile);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                return file;
            }

            @Override
            protected void onPostExecute(File file) {
//                Toast.makeText(MainActivity.this, "saved in Pictures/GankBeauty", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }
        }.execute();
    }

    private void initInfoSync(String alias) {
        homeModel.postUserSync(5, UserInfo.uid, UserInfo.employeeAccount, UserInfo.mobile, UserInfo.orgId,
                UserInfo.familyName, alias, this);
    }

    private void initView() {
        mViewPager = findViewById(R.id.view_pager);
        navigation = findViewById(R.id.navigation);

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
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        navigation.enableShiftingMode(false); //取消切换动画
        navigation.enableItemShiftingMode(false); //取消文字
        navigation.enableAnimation(false);  //取消选中动画
        navigation.setupWithViewPager(mViewPager);
        navigation.setItemIconTintList(null);
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

    }


    private void initPush() {
        // 设置极光推送别名与标签
        String alias;
        String phone;
        if (!TextUtils.isEmpty(UserInfo.mobile)) {
            phone = UserInfo.mobile;
        } else {
            phone = UserInfo.init_mobile;
        }
        if (environment.equals("release")) {
            alias = "cgj_" + phone;
        } else {
            alias = "test_cgj_" + phone;
        }
        setTag();
        setAlias(alias);
        initInfoSync(alias);
        CityPropertyApplication.addActivity(this);
        Tools.setMainStatus(MainActivity.this, true);
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
        filter.addAction(ACTION_TICKET_INFO);
        filter.addAction(ACTION_HOME_DELETEINFO);
        filter.addAction(ACTION_READ_MESSAGEINFO);
        filter.addAction(ACTION_UPDATE_PUSHINFO);
        registerReceiver(freshReceiver, filter);
        //推送跳转详情
        pushDetail();
    }


    private void pushDetail() {
        if (extras != null) {
            setMsgStatus(extras);
        }
    }

    /**
     * 单设备登录
     */
    private void singleDevicelogin() {
        userModel.postSingleDevice(2, "1", false, this);
    }

    /**
     * 单设备退出
     */
    private void singleDevicelogout() {
        String device_code = spUtils.getStringData(SpConstants.storage.DEVICE_TOKEN, "");
        userModel.postSingleExit(3, device_code, this);
    }

    // 检测版本更新
    private void getVersion() {
        settingModel.getUpdate(0, "1", false, this);
    }

    // 首页列表消息设置为已读
    private void getReadMessageInfo(String msg_id) {
        if (null == homeModel) {
            homeModel = new HomeModel(this);
        }
        homeModel.postSetMsgRead(7, msg_id, false, this);
    }

    // 更新首页消息推送列表
    private void getUpdatePushInfo() {
        if (mAdapter.getItem(0) instanceof MsgListFragment) {
            MsgListFragment fragment = (MsgListFragment) mAdapter.instantiateItem(mViewPager, 0);
            fragment.reqPushMsg();
        }
    }

    /**
     * 重新静默获取用户信息
     */
    private void getSlientLogin() {
        OAuth2ServiceUpdate auth2ServiceUpdate = new OAuth2ServiceUpdate(MainActivity.this);
        auth2ServiceUpdate.getOAuth2Service(UserInfo.employeeAccount, Tools.getPassWordMD5(mContext), new Oauth2CallBack() {
            @Override
            public void onData(String access_token) {
                getNetInfo(access_token);
            }
        });
    }

    private void getNetInfo(String access_token) {
        userModel.getOauthUser(10, access_token, false, this);
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
     * 极光推送设置
     */
    private void setTag() {

        Set<String> set = new HashSet<String>();
        String s1 = "";
        String s2 = Tools.getStringValue(this, Contants.storage.CORPID);
        if (environment.equals("release")) {
            s1 = "release_cgj";
            s2 = "release_" + s2;
        } else {
            s1 = "test_cgj";
            s2 = "test_" + s2;
        }
        set.add(s1);
        set.add(s2);
        //调用JPush API设置Tag
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAGS, set));

    }

    private void setAlias(String alias) {
        //调用JPush API设置Alias
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    Tools.setBooleanValue(MainActivity.this, Contants.storage.ALIAS, true);
//                    initInfoSync();
                    break;

                case 6002:
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
                    Tools.setBooleanValue(MainActivity.this, Contants.storage.Tags, true);
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

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    VersionEntity versionEntity = new VersionEntity();
                    try {
                        versionEntity = GsonUtils.gsonToBean(result, VersionEntity.class);
                        int result_up = versionEntity.getContent().getResult();
                        downUrl = versionEntity.getContent().getInfo().getUrl();
                        getVersion = versionEntity.getContent().getInfo().getVersion();
                        updateList.clear();
                        updateList.add(versionEntity.getContent().getInfo().getFunc());
                        if (result_up == 2) {//1：最新版本，2：介于最新和最低版本之间，3：低于支持的最低版本
                            int type = versionEntity.getContent().getType();
                            if ((type == 1)) {//1：大版本更新，2：小版本更新
                                showUpdateDialog(result_up, versionEntity.getContent().getInfo().getVersion(), downUrl, updateList);
                            }
                        } else if (result_up == 3) {
                            showUpdateDialog(result_up, versionEntity.getContent().getInfo().getVersion(), downUrl, updateList);
                        }
                    } catch (Exception e) {
                        e.toString();
                    }
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    HomePopWindowEntity popWindowEntity = new HomePopWindowEntity();
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String content = jsonObject.getString("content");
                        if (!TextUtils.isEmpty(content)) {
                            popWindowEntity = GsonUtils.gsonToBean(result, HomePopWindowEntity.class);
                            if (null != popWindowEntity.getContent()) {
                                ArrayList<String> imageList = new ArrayList<>();
                                ArrayList<String> urlList = new ArrayList<>();
                                ArrayList<String> descList = new ArrayList<>();
                                imageList.clear();
                                urlList.clear();
                                descList.clear();
                                imageList.add(popWindowEntity.getContent().getImg_url());
                                urlList.add(popWindowEntity.getContent().getHelp_url());
                                descList.add("");
                                intoPopup(urlList, imageList, descList);
                            }
                        }
                    } catch (Exception e) {
                    }
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(result)) {
                    com.tg.user.entity.SingleDeviceLogin singleDeviceLogin = GsonUtils.gsonToBean(result, com.tg.user.entity.SingleDeviceLogin.class);
                    String device_token = singleDeviceLogin.getContent().getDevice_token();
                    spUtils.saveStringData(SpConstants.storage.DEVICE_TOKEN, device_token);
                }
                break;
            case 3:
                if (!TextUtils.isEmpty(result)) {
                    //单设备退出逻辑，暂无
                }
                break;
            case 5:
                if (!TextUtils.isEmpty(result)) {
                    //IM消息同步逻辑，暂无
                }
                break;
            case 6:
                if (!TextUtils.isEmpty(result)) {
                    String difference = HttpTools.getContentString(result);
                    if (!TextUtils.isEmpty(difference)) {
                        spUtils.saveLongData(SpConstants.UserModel.DIFFERENCE, Long.valueOf(difference));
                    }
                }
                break;
            case 7:
                if (!TextUtils.isEmpty(result)) {
                    //消息已读逻辑，暂无
                }
                break;
            case 8:
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = HttpTools.getContentJSONObject(result);
                    if (null != jsonObject) {
                        Tools.saveStringValue(MainActivity.this, Contants.storage.HomePageAd, result);
                        String urlImg = "";
                        long startTime = 0;
                        long endTime = 0;
                        try {
                            urlImg = jsonObject.getString("adUrl");
                            startTime = jsonObject.getLong("startTime");
                            endTime = jsonObject.getLong("endTime");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!TextUtils.isEmpty(urlImg)) {
                            long time = System.currentTimeMillis() / 1000;
                            if (startTime < time && time < endTime) {
                                download(urlImg);
                            } else {
                                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Colourlife";
                                File file = new File(path);
                                deleteFile(file);
                            }
                        }
                    } else {
                        Tools.saveStringValue(MainActivity.this, Contants.storage.HomePageAd, "");
                    }
                }
                break;
            case 10:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        int code = HttpTools.getCode(result);
                        if (code == 0) {
                            String response = HttpTools.getContentString(result);
                            ResponseData data = HttpTools.getResponseContentObject(response);
                            Tools.loadUserInfo(data, result);
                            Tools.savetokenUserInfo(MainActivity.this, result);
                            int status = data.getInt("status");
                            String corpId = data.getString("corp_id");
                            UserInfo.employeeAccount = data.getString("username");
                            Tools.saveStringValue(MainActivity.this, Contants.storage.CORPID, corpId);//租户ID
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
                                ToastFactory.showToast(MainActivity.this, "账号异常，请及时联系管理员");
                                exitClearAllData(false);
                            }
                        }
                    } catch (Exception e) {
                    }
                }
                break;
        }
    }

    public void deleteFile(File file) {
        try {
            if (file.exists()) { // 判断文件是否存在
                if (file.isFile()) { // 判断是否是文件
                    file.delete(); // delete()方法 你应该知道 是删除的意思;
                } else if (file.isDirectory()) { // 否则如果它是一个目录
                    File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                    for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                        this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                    }
                }
                file.delete();
            }
        } catch (Exception e) {
        }
    }

    /**
     * 首页广告弹窗（单图片）
     *
     * @param urlList
     * @param imageList
     * @param descList
     */
    public void intoPopup(ArrayList<String> urlList, ArrayList<String> imageList, ArrayList<String> descList) {
        if (!otherPopShow) {
            try {
                PopupScUtils.getInstance().jump(this, urlList, imageList, descList);
                otherPopShow = true;
            } catch (Exception e) {

            }
        }
    }

    private void showUpdateDialog(int code, String version, String mdownUrl, List<String> updateList) {
        downUrl = mdownUrl;
        updateDialog = new UpdateVerSionDialog(mContext);
        updateDialog.ok.setText("立即更新V" + version + "版本");
        UpdateAdapter updateAdapter = new UpdateAdapter(mContext, updateList);
        updateDialog.listView.setAdapter(updateAdapter);
        switch (code) {
            case 2://可选更新
                String ver = spUtils.getStringData(SpConstants.UserModel.VERSION_NAME, "");
                if (!version.equals(ver)) {
                    updateDialog.show();
                }
                break;
            case 3://强制更新
                updateDialog.cancel.setVisibility(View.GONE);
                updateDialog.mDialog.setCancelable(false);
                updateDialog.show();
                break;
        }
        updateDialog.cancel.setOnClickListener(v -> {
            spUtils.saveStringData(SpConstants.UserModel.VERSION_NAME, version);
            updateDialog.dismiss();
        });
        updateDialog.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (code != 1) {
                    //用户点击更新，跳转到下载更新页面
                    List<String> list = new ArrayList<>();
                    list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    XXPermissions.with(MainActivity.this)
                            .constantRequest()
                            .permission(list)
                            .request(new OnPermission() {
                                @Override
                                public void hasPermission(List<String> granted, boolean isAll) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        if (getPackageManager().canRequestPackageInstalls()) {
                                            startDown();
                                        } else {
                                            DeleteMsgDialog dialog = new DeleteMsgDialog(MainActivity.this, R.style.custom_dialog_theme);
                                            dialog.show();
                                            dialog.setContentText("当前手机系统安装应用需要打开未知来源权限，请去设置中开启权限");
                                            dialog.setrightText("去打开");
                                            dialog.setCanceledOnTouchOutside(false);
                                            dialog.btn_define.setOnClickListener(v1 -> {
                                                dialog.dismiss();
                                                startInstallPermissionSettingActivity();
                                            });
                                            dialog.btn_cancel.setOnClickListener(v1 -> {
                                                dialog.dismiss();
                                            });
                                        }
                                    } else {
                                        startDown();
                                    }
                                }

                                @Override
                                public void noPermission(List<String> denied, boolean quick) {
                                    ToastUtil.showShortToast(MainActivity.this,
                                            "存储权限被禁止，请去开启该权限");
                                }
                            });
                }
            }
        });
    }

    private void startInstallPermissionSettingActivity() {
        Uri packageURI = Uri.parse("package:" + getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        startActivityForResult(intent, 10001);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001) {
            if (getPackageManager().canRequestPackageInstalls()) {
                startDown();
            }
        }
    }

    private void startDown() {
        if (null != updateDialog) {
            updateDialog.dismiss();
        }
        Intent intent = new Intent(MainActivity.this, UpdateService.class);
        intent.putExtra(UpdateService.DOWNLOAD_URL, downUrl);
        intent.putExtra(UpdateService.VERSIONNAME, getVersion);
        MainActivity.this.startService(intent);
        ToastUtil.showShortToast(MainActivity.this, "彩管家已开始下载更新,详细信息可在通知栏查看哟!");
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
        private final WeakReference<MainActivity> mTarget;

        NormalHandler(MainActivity target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity act = mTarget.get();
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
                    ft = new FragmentManagement();
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

    @Override
    public void onBackPressed() {
        backPress();
    }

    /**
     * 退出APP
     */
    private void backPress() {
        if (exit) {
            Tools.setMainStatus(MainActivity.this, false);
            hand.removeCallbacksAndMessages(null);
            CityPropertyApplication.exitApp(this);
        } else {
            exit = true;
            ToastUtil.showShortToast(this, "再按一次退出彩管家");
            hand.postDelayed(run, 2500);
        }
    }

    private Handler hand = new Handler() {
        public void handleMessage(Message msg) {

        }
    };
    Runnable run = new Runnable() {
        @Override
        public void run() {
            exit = false;
        }
    };
}
