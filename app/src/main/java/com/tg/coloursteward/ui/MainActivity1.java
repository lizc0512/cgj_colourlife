package com.tg.coloursteward.ui;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.tg.coloursteward.R;
import com.tg.coloursteward.application.CityPropertyApplication;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.fragment.FragmentManagement;
import com.tg.coloursteward.fragment.FragmentMine;
import com.tg.coloursteward.info.HomeDeskTopInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.log.Logger;
import com.tg.coloursteward.module.meassage.MsgListFragment;
import com.tg.coloursteward.module.contact.ContactsFragment;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.AppAuthService;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.updateapk.ApkInfo;
import com.tg.coloursteward.updateapk.UpdateManager;
import com.tg.coloursteward.util.AuthTimeUtils;
import com.tg.coloursteward.util.ExampleUtil;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.push.MorePushManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


/**
 * Created by colin on 2018/3/15.
 */

public class MainActivity1 extends AppCompatActivity implements MessageHandler.ResponseListener {

    private static final String TAG = MainActivity1.class.getSimpleName();

    public static final String ACTION_FRESH_USERINFO = "com.tg.coloursteward.ACTION_FRESH_USERINFO";
    public static final String ACTION_TICKET_INFO = "com.tg.coloursteward.ACTION_TICKET_INFO";
    public static final String ACTION_HOME_DELETEINFO = "com.tg.coloursteward.ACTION_HOME_DELETEINFO";
    public static final String ACTION_ACCOUNT_INFO = "com.tg.coloursteward.ACTION_ACCOUNT_INFO";
    public static final String ACTION_READ_MESSAGEINFO = "com.tg.coloursteward.ACTION_READ_MESSAGEINFO";
    public static final String ACTION_UPDATE_PUSHINFO = "com.tg.coloursteward.ACTION_UPDATE_PUSHINFO";
    public static final String KEY_NEDD_FRESH = "need_fresh";
    public static final String KEY_SKIN_CODE = "skin_code";
    public static final String KEY_EXTRAS = "extras";

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;

    private final int REQUESTPERMISSION = 110;

    private ImageView img_left;
    private TextView tv_title;
    private ImageView img_right1;
    private ImageView img_right2;

    private ViewPager mViewPager;
    private BottomNavigationViewEx navigation;
    //private TabLayout mTabLayout;

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

    private int skin_code = 101;//  101 彩生活  100 通用  102 中住
    private String extras;

    private Runnable getUserInfoRunnable = new Runnable() {
        public void run() {
            getUserInfo();
        }
    };

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        mContext = this;
        mHandler = new NormalHandler(this);

        windowPermission();

        Intent data = getIntent();
        if (data != null) {
            needGetUserInfo = data.getBooleanExtra(KEY_NEDD_FRESH, true);
            skin_code = data.getIntExtra(KEY_SKIN_CODE, -1);
            extras = data.getStringExtra(KEY_EXTRAS);
        }

        initTitle();
        initView();

        initPush();

        initProto();

        MorePushManager.connect(this);
    }


    public void windowPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                if (intent != null) {
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
                return;
            } else {
                //执行6.0以上绘制代码
            }
        } else {
            //执行6.0以下绘制代码
        }

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
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(freshReceiver);
    }


    private void initTitle() {

    }


    @Override
    public void onRequestStart(Message msg, String hintString) {

    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.GET_USER_INFO) {
            String response = HttpTools.getContentString(jsonString);
            ResponseData data = HttpTools.getResponseContentObject(response);
            Tools.loadUserInfo(data, jsonString);
            sendBroadcast(new Intent(ACTION_FRESH_USERINFO));
            mHandler.removeCallbacks(getUserInfoRunnable);
            mHandler.postDelayed(getUserInfoRunnable, 10 * 60 * 1000);
        } else if (msg.arg1 == HttpTools.SET_EMPLOYEE_INFO) {
            if (code == 0) {
                JSONObject content = HttpTools.getContentJSONObject(jsonString);
                if (content != null) {
                    try {
                        String key = content.getString("key");
                        String secret = content.getString("secret");
                        //保存key  sectet
                        Tools.saveStringValue(this, Contants.EMPLOYEE_LOGIN.key, key);
                        Tools.saveStringValue(this, Contants.EMPLOYEE_LOGIN.secret, secret);
                        if (skin_code == 101) {//彩生活
                            sendBroadcast(new Intent(ACTION_TICKET_INFO));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                //保存key  sectet
                Tools.saveStringValue(this, Contants.EMPLOYEE_LOGIN.key, "");
                Tools.saveStringValue(this, Contants.EMPLOYEE_LOGIN.secret, "");
            }
        } else if (msg.arg1 == HttpTools.GET_CZY_ID) {//获取彩之云账户id
            if (code == 1) {
                JSONArray jsonArray = HttpTools.getContentJsonArray(jsonString);
                try {
                    JSONObject object = (JSONObject) jsonArray.get(0);
                    String CZY_id = object.getString("id");
                    String community_id = object.getString("community_id");
                    Tools.saveCZYID(this, CZY_id);
                    Tools.saveCZY_Community_ID(this, community_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (msg.arg1 == HttpTools.GET_VERSION_INFO) {//版本更新
            JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
            String content = HttpTools.getContentString(jsonString);
            if (code == 0) {
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
                            manager.checkUpdate(apkinfo);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

            }
        }
    }

    @Override
    public void onFail(Message msg, String hintString) {

    }

    private void initView() {
        img_left = (ImageView) findViewById(R.id.img_left);
        tv_title = (TextView) findViewById(R.id.tv_title);

        img_right1 = (ImageView) findViewById(R.id.img_right1);
        img_right2 = (ImageView) findViewById(R.id.img_right2);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        navigation = (BottomNavigationViewEx) findViewById(R.id.navigation);

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
        if (!alias) {
            setAlias();

        }
        CityPropertyApplication.addActivity(this);
    }

    public Handler getHandler() {
        return msgHand.getHandler();
    }

    private void initProto() {
        msgHand = new MessageHandler(this);
        msgHand.setResponseListener(this);

        getTokenInfo();
        getEmployeeInfo();
        getAuthAppInfo();//2.0授权
        getAppAuthInfo();//1.0授权
        /**
         * 版本检测更新
         */
        getVersion();
        initView();
        if (needGetUserInfo) {
            mHandler.postDelayed(getUserInfoRunnable, 3000);
        }
        if (!needGetUserInfo) {
            OAtoCZY();
        }
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

    private void getUserInfo() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_USER_INFO, null);
        RequestParams params = new RequestParams();
        params.put("uid", UserInfo.uid);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/account", config, params);
    }

    // 检测版本更新
    private void getVersion() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_VERSION_INFO);
        RequestParams params = new RequestParams();
        String version = UpdateManager.getVersionName(this);
        params.put("version", version);
        params.put("type", "android");
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/czywg/version", config, params);
    }


    // 获取扫码开门权限
    private void OAtoCZY() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_CZY_ID);
        RequestParams params = new RequestParams();
        params.put("oa", UserInfo.employeeAccount);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/newczy/customer/infoByOa", config, params);
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
     * 获取key
     * sectet
     */
    public void getEmployeeInfo() {
        String pwd = Tools.getPassWord(this);
        RequestConfig config = new RequestConfig(this, HttpTools.SET_EMPLOYEE_INFO, null);
        RequestParams params = new RequestParams();
        params.put("username", UserInfo.employeeAccount);
        params.put("password", pwd);
        HttpTools.httpPost(Contants.URl.URL_ICETEST, "/czywg/employee/login", config, params);
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
        }, 500);
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


}
