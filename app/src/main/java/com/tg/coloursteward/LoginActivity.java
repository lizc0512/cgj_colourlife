package com.tg.coloursteward;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.amap.api.location.AMapLocation;
import com.geetest.gt3unbindsdk.Bind.GT3GeetestBindListener;
import com.geetest.gt3unbindsdk.Bind.GT3GeetestUtilsBind;
import com.tg.coloursteward.application.CityPropertyApplication;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.database.SharedPreferencesTools;
import com.tg.coloursteward.entity.CzyOauthEntity;
import com.tg.coloursteward.entity.JiYanTwoCheckEntity;
import com.tg.coloursteward.entity.SingleDeviceLogin;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.Oauth2CallBack;
import com.tg.coloursteward.module.MainActivity1;
import com.tg.coloursteward.net.DES;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.OAuth2ServiceUpdate;
import com.tg.coloursteward.updateapk.UpdateManager;
import com.tg.coloursteward.util.GDLocationUtil;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.TokenUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.http.OkHttpConnector;
import com.youmai.hxsdk.router.APath;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static com.tg.coloursteward.application.CityPropertyApplication.lbsTraceClient;

/**
 * 登录页面
 *
 * @author Administrator
 */
@Route(path = APath.RE_LOGIN)
public class LoginActivity extends BaseActivity implements AnimationListener {
    private static final String TAG = "LoginActivity";
    private String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Colourlife/colourlifeAd.gif";
    private EditText editUser;
    private EditText editPassword;
    private View startLayout;
    private View contentLayout;
    private Animation outAnim;
    private Animation inAnim;
    private String newPhone = "";
    private String corpId;
    private String password;
    private String extras;
    private GT3GeetestUtilsBind gt3GeetestUtils;
    private RelativeLayout submit;
    private OAuth2ServiceUpdate auth2ServiceUpdate;
    private String code;
    private RelativeLayout rl_czy_login;
    private TextView tv_login_loginbuttom;
    private ImageView iv_login_deloa;
    private ImageView iv_login_delpwd;
    private GifImageView gif_login;
    private TextView tv_login_cancel;
    private RelativeLayout rl_login_ad;
    private int duration;
    private String urlAd;
    private String auth_type;
    private String passwordMD5;

    @Override
    public View getContentView() {
//        headView.setBackgroundColor(getResources().getColor(R.color.white));
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }


    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.submit:
                submit.setBackground(getResources().getDrawable(R.drawable.login_button_select));
                newPhone = editUser.getText().toString();
                if (newPhone.length() <= 0) {
                    ToastFactory.showToast(this, "请输入账号");
                    return false;
                }
                password = editPassword.getText().toString();
                if (password.length() < 6) {
                    ToastFactory.showToast(this, "请输入不少于6位的密码");
                    return false;
                }
                loginGt();// 极验登录
                break;
            case R.id.forget_pwd:
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));// 忘记密码
                break;
            case R.id.rl_czy_login://彩之云授权登录
                ToastFactory.showToast(LoginActivity.this, "正在调起中...");
                String package_name = "cn.net.cyberway";
                String activity_path = "cn.net.cyberway.OauthWebviewActivity";
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("applicationId", DES.APP_ID);
                bundle.putString("packgename", "com.tg.coloursteward");
                intent.putExtras(bundle);
                ComponentName cn = new ComponentName(package_name, activity_path);
                intent.setComponent(cn);
                try {
                    if (intent.resolveActivityInfo(getPackageManager(), PackageManager.MATCH_DEFAULT_ONLY) != null) {
                        startActivity(intent);
                    } else {
                        ToastFactory.showToast(LoginActivity.this, "未检测到手机有安装彩之云APP");
                    }
                } catch (Exception e) {
                    ToastFactory.showToast(LoginActivity.this, "请安装最新版彩之云APP");
                }
                break;
            case R.id.iv_login_deloa:
                if (!TextUtils.isEmpty(editUser.getText().toString().trim())) {
                    editUser.setText("");
                }
                break;
            case R.id.iv_login_delpwd:
                if (!TextUtils.isEmpty(editPassword.getText().toString().trim())) {
                    editPassword.setText("");
                }
                break;
        }
        return super.handClickEvent(v);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent getintent = getIntent();
        if (null != getintent) {
            boolean loginOut = getintent.getBooleanExtra("login_out", false);
            extras = getintent.getStringExtra(MainActivity.KEY_EXTRAS);
            code = getintent.getStringExtra("czy_code");
            if (loginOut) {
                initClear();
                StopYingYan();
                singleDevicelogout();
                SharedPreferencesTools.clearUserId(LoginActivity.this);
                //清空缓存
                SharedPreferencesTools.clearCache(LoginActivity.this);
                SharedPreferencesTools.clearAllData(LoginActivity.this);
                CityPropertyApplication.gotoLoginActivity(LoginActivity.this);
            }
        }
        if (!TextUtils.isEmpty(code)) {
            ThridLogin(code);
            Tools.saveStringValue(LoginActivity.this, Contants.storage.THRID_CODE, code);
        }
        initView();
        showAd();
        initGetTS();
        CheckPermission();
        /**
         * 初始化*
         * 务必放在onCreate方法里面执行
         */
        gt3GeetestUtils = new GT3GeetestUtilsBind(LoginActivity.this);
    }

    /**
     * 单设备退出
     */
    private void singleDevicelogout() {
        RequestConfig config = new RequestConfig(this, HttpTools.POST_LOGOUTDEVICE, null);
        RequestParams params = new RequestParams();
        String device_code = Tools.getStringValue(this, Contants.storage.DEVICE_TOKEN);
        params.put("device_code", device_code);
        HttpTools.httpPost(Contants.URl.SINGLE_DEVICE, "cgjapp/single/device/logout", config, params);
    }

    private void StopYingYan() {
        if (null != lbsTraceClient) {
            lbsTraceClient.stopGather(null);
        }
    }

    private void showAd() {
        String CacheAd = Tools.getStringValue(LoginActivity.this, Contants.storage.HomePageAd);
        if (!TextUtils.isEmpty(CacheAd)) {
            JSONObject jsonObject = HttpTools.getContentJSONObject(CacheAd);
            long startTime = 0;
            long endTime = 0;
            duration = 3;
            urlAd = "";
            try {
                startTime = jsonObject.getLong("startTime");
                endTime = jsonObject.getLong("endTime");
                duration = jsonObject.getInt("duration");
                urlAd = jsonObject.getString("openUrl");
                auth_type = jsonObject.getString("auth_type");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tv_login_cancel.setText("跳过(" + duration-- + "s)");
            long time = System.currentTimeMillis() / 1000;
//            Boolean isshow = Tools.getBooleanValue(LoginActivity.this, Contants.storage.ISSHOWAD);
            if (startTime < time && time < endTime) {
                rl_login_ad.setVisibility(View.VISIBLE);
                String imageType = Tools.getStringValue(LoginActivity.this, Contants.storage.ImageType);
                if (imageType.equals("gif")) {
                    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Colourlife/colourlifeAd.gif";
                } else if (imageType.equals("png")) {
                    path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Colourlife/colourlifeAd.png";
                }
                File mFile = new File(path);
                if (mFile.canRead()) {
                    //若该文件存在
                    if (mFile.exists()) {
                        if (path.endsWith("gif")) {
                            try {
                                GifDrawable gifFromPath = new GifDrawable(path);
                                gif_login.setImageDrawable(gifFromPath);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (path.endsWith("png")) {
                            Bitmap bitmap = BitmapFactory.decodeFile(path);
                            gif_login.setImageBitmap(bitmap);
                        }
                    }
                }
                initTimeCount(duration);
                rl_login_ad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(urlAd)) {
                            cancelTimeCount();
                            MainActivity1.url_ad = urlAd;
                            MainActivity1.auth_type = auth_type;
                            ResponseData userInfoData = SharedPreferencesTools.getUserInfo(LoginActivity.this);
                            String skin_code = Tools.getStringValue(LoginActivity.this, Contants.storage.SKINCODE);
                            if (userInfoData.length > 0) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity1.class);
                                intent.putExtra(MainActivity1.KEY_SKIN_CODE, skin_code);
                                intent.putExtra(MainActivity1.KEY_EXTRAS, extras);
                                intent.putExtra(MainActivity1.FROM_AD, urlAd);
                                intent.putExtra(MainActivity1.FROM_AUTH_TYPE, auth_type);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            } else {
                                showContentView();
                            }
                        }
                    }
                });
            } else {
                showStartPager();
            }
        } else {
            showStartPager();
        }
    }

    private MyTimeCount myTimeCount = null;

    /***初始化计数器**/
    private void initTimeCount(int duration) {
        cancelTimeCount();
        myTimeCount = new MyTimeCount(duration * 1000, 1000);
        myTimeCount.start();
    }

    private void cancelTimeCount() {
        if (myTimeCount != null) {
            myTimeCount.cancel();
            myTimeCount = null;
        }
    }

    /**
     * 定义一个倒计时的内部类
     */
    class MyTimeCount extends CountDownTimer {
        public MyTimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            ResponseData userInfoData = SharedPreferencesTools.getUserInfo(LoginActivity.this);
            String skin_code = Tools.getStringValue(LoginActivity.this, Contants.storage.SKINCODE);
            if (userInfoData.length > 0) {
                Intent intent = new Intent(LoginActivity.this, MainActivity1.class);
                intent.putExtra(MainActivity1.KEY_SKIN_CODE, skin_code);
                intent.putExtra(MainActivity1.KEY_EXTRAS, extras);
                intent.putExtra(MainActivity1.FROM_LOGIN, false);
                startActivity(intent);
                LoginActivity.this.finish();
            } else {
                showContentView();
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            long currentSecond = millisUntilFinished / 1000;
            tv_login_cancel.setText("跳过(" + currentSecond-- + "s)");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimeCount();
    }

    private void ThridLogin(String code) {
        RequestConfig config = new RequestConfig(LoginActivity.this, HttpTools.GET_CZYOAUTH, "登录中");
        Map<String, Object> validateParams = new HashMap<>();
        validateParams.put("code", code);
        validateParams.put("token", DES.TOKEN);
        validateParams.put("app_id", DES.APP_ID);
        validateParams.put("client_secret", DES.TOKEN);
        validateParams.put("device_uuid", TokenUtils.getUUID(LoginActivity.this));
        Map<String, String> stringMap = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(LoginActivity.this, validateParams));
        HttpTools.httpGet_Map(Contants.URl.URL_ICESTAFF, "app/authUser", config, (HashMap) stringMap);
    }

    private void initView() {
        gif_login = findViewById(R.id.gif_login);
        rl_login_ad = findViewById(R.id.rl_login_ad);
        tv_login_cancel = findViewById(R.id.tv_login_cancel);
        tv_login_cancel.getBackground().setAlpha(100);
        rl_czy_login = findViewById(R.id.rl_czy_login);
        tv_login_loginbuttom = findViewById(R.id.tv_login_loginbuttom);
        iv_login_deloa = findViewById(R.id.iv_login_deloa);
        iv_login_delpwd = findViewById(R.id.iv_login_delpwd);
        contentLayout = findViewById(R.id.login_content);
        editUser = (EditText) findViewById(R.id.edit_user);
        startLayout = findViewById(R.id.start_layout);
        editPassword = (EditText) findViewById(R.id.edit_password);
        rl_czy_login.setOnClickListener(singleListener);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(singleListener);
        submit.setBackground(getResources().getDrawable(R.drawable.bg_login_button));
        iv_login_deloa.setOnClickListener(singleListener);
        iv_login_delpwd.setOnClickListener(singleListener);
        findViewById(R.id.forget_pwd).setOnClickListener(singleListener);
        tv_login_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResponseData userInfoData = SharedPreferencesTools.getUserInfo(LoginActivity.this);
                String skin_code = Tools.getStringValue(LoginActivity.this, Contants.storage.SKINCODE);
                if (userInfoData.length > 0) {
                    cancelTimeCount();
                    Intent intent = new Intent(LoginActivity.this, MainActivity1.class);
                    intent.putExtra(MainActivity1.KEY_SKIN_CODE, skin_code);
                    intent.putExtra(MainActivity1.KEY_EXTRAS, extras);
                    intent.putExtra(MainActivity1.FROM_LOGIN, false);
                    startActivity(intent);
                    LoginActivity.this.finish();
                } else {
                    showContentView();
                }
            }
        });
        editUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    iv_login_deloa.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(editPassword.getText().toString().trim())) {
                        submit.setBackground(getResources().getDrawable(R.drawable.bg_login_button_blue));
                        tv_login_loginbuttom.setTextColor(getResources().getColor(R.color.white));
                        submit.setClickable(true);
                    }
                } else {
                    iv_login_deloa.setVisibility(View.GONE);
                    submit.setBackground(getResources().getDrawable(R.drawable.bg_login_button));
                    tv_login_loginbuttom.setTextColor(getResources().getColor(R.color.line_login_button));
                    submit.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    iv_login_delpwd.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(editUser.getText().toString().trim())) {
                        submit.setBackground(getResources().getDrawable(R.drawable.bg_login_button_blue));
                        tv_login_loginbuttom.setTextColor(getResources().getColor(R.color.white));
                        submit.setClickable(true);
                    }
                } else {
                    iv_login_delpwd.setVisibility(View.GONE);
                    submit.setBackground(getResources().getDrawable(R.drawable.bg_login_button));
                    tv_login_loginbuttom.setTextColor(getResources().getColor(R.color.line_login_button));
                    submit.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initGetTS() {
        RequestParams params = new RequestParams();
        RequestConfig config = new RequestConfig(this, HttpTools.GET_TS, "");
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/timestamp", config, params);
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

    private void CheckPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.READ_CONTACTS,
                            android.Manifest.permission.READ_PHONE_STATE},
                    Activity.DEFAULT_KEYS_SEARCH_LOCAL);
        }
    }

    private void showStartPager() {
        startLayout.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);
        initGetLocation();
        mHand.postDelayed(new Runnable() {
            @Override
            public void run() {
                ResponseData userInfoData = SharedPreferencesTools.getUserInfo(LoginActivity.this);
                String skin_code = Tools.getStringValue(LoginActivity.this, Contants.storage.SKINCODE);
                if (userInfoData.length > 0) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity1.class);
                    intent.putExtra(MainActivity1.KEY_SKIN_CODE, skin_code);
                    intent.putExtra(MainActivity1.KEY_EXTRAS, extras);
                    intent.putExtra(MainActivity1.FROM_LOGIN, false);
                    startActivity(intent);
                    LoginActivity.this.finish();
                } else {
                    showContentView();
                }
            }
        }, 2000);
    }

    private void showContentView() {
        if (outAnim == null || inAnim == null) {
            outAnim = AnimationUtils.loadAnimation(this,
                    android.R.anim.fade_out);
            inAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            outAnim.setDuration(500);
            inAnim.setDuration(500);
            outAnim.setAnimationListener(this);
            inAnim.setAnimationListener(this);
        }
        startLayout.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.VISIBLE);
        startLayout.startAnimation(outAnim);
        contentLayout.startAnimation(inAnim);
    }

    /**
     * 极验验证码
     */
    private void loginGt() {
        gt3GeetestUtils.getGeetest(LoginActivity.this, Contants.APP.captchaURL, Contants.APP.validateURL, null, new GT3GeetestBindListener() {
            /**
             * num 1 点击验证码的关闭按钮来关闭验证码
             * num 2 点击屏幕关闭验证码
             * num 3 点击返回键关闭验证码
             */
            @Override
            public void gt3CloseDialog(int num) {
            }

            /**
             * 验证码加载准备完成
             * 此时弹出验证码
             */
            @Override
            public void gt3DialogReady() {
            }

            /**
             * 拿到第一个url（API1）返回的数据
             */
            @Override
            public void gt3FirstResult(JSONObject jsonObject) {
            }

            /**
             * 往API1请求中添加参数
             * 添加数据为Map集合
             * 添加的数据以get形式提交
             */
            @Override
            public Map<String, String> gt3CaptchaApi1() {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("device_uuid", TokenUtils.getUUID(LoginActivity.this));
                Map<String, String> stringMap = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(LoginActivity.this, map));
                return stringMap;
            }

            /**
             * 设置是否自定义第二次验证ture为是 默认为false(不自定义)
             * 如果为false这边的的完成走gt3GetDialogResult(String result)
             * 如果为true这边的的完成走gt3GetDialogResult(boolean a, String result)
             * result为二次验证所需要的数据
             */
            @Override
            public boolean gt3SetIsCustom() {
                return true;
            }

            /**
             * 拿到二次验证需要的数据
             */
            @Override
            public void gt3GetDialogResult(String result) {
            }

            /**
             * 自定义二次验证，当gtSetIsCustom为ture时执行这里面的代码
             */
            @Override
            public void gt3GetDialogResult(boolean status, String result) {
                if (status) {
                    //利用异步进行解析这result进行二次验证，结果成功后调用gt3GeetestUtils.gt3TestFinish()方法调用成功后的动画，然后在gt3DialogSuccess执行成功之后的结果
                    JSONObject res_json = null;
                    Map<String, Object> validateParams = new HashMap<>();
                    try {
                        res_json = new JSONObject(result);
                        validateParams.put("device_uuid", TokenUtils.getUUID(LoginActivity.this));
                        validateParams.put("geetest_challenge", res_json.getString("geetest_challenge"));
                        validateParams.put("geetest_validate", res_json.getString("geetest_validate"));
                        validateParams.put("geetest_seccode", res_json.getString("geetest_seccode"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Map<String, String> stringMap = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(LoginActivity.this, validateParams));
                    RequestConfig config = new RequestConfig(LoginActivity.this, HttpTools.POST_TWOJIYAN, "");
                    HttpTools.httpPost_Map(Contants.URl.URL_NEW, "app/home/login/verify", config, (HashMap) stringMap);

                }
            }


            /**
             * 需要做验证统计的可以打印此处的JSON数据
             * JSON数据包含了极验每一步的运行状态和结果
             */
            @Override
            public void gt3GeetestStatisticsJson(JSONObject jsonObject) {
            }

            /**
             * 往二次验证里面put数据
             * put类型是map类型
             * 注意map的键名不能是以下三个：geetest_challenge，geetest_validate，geetest_seccode
             */
            @Override
            public Map<String, String> gt3SecondResult() {
                Map<String, Object> objectMap = new HashMap<String, Object>();
                objectMap.put("device_uuid", TokenUtils.getUUID(LoginActivity.this));
                Map<String, String> stringMap = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(LoginActivity.this, objectMap));
                return stringMap;

            }

            /**
             * 二次验证完成的回调
             * result为验证后的数据
             * 根据二次验证返回的数据判断此次验证是否成功
             * 二次验证成功调用 gt3GeetestUtils.gt3TestFinish();
             * 二次验证失败调用 gt3GeetestUtils.gt3TestClose();
             */
            @Override
            public void gt3DialogSuccessResult(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject jobj = new JSONObject(result);
                        String content = jobj.getString("content");
                        JSONObject jsonObject = new JSONObject(content);
                        int code = jsonObject.getInt("status");
                        if (code == 1) {
                            gt3GeetestUtils.gt3TestFinish();
                        } else {
                            gt3GeetestUtils.gt3TestClose();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    gt3GeetestUtils.gt3TestClose();
                }
            }

            /**
             * 验证过程错误
             * 返回的错误码为判断错误类型的依据
             */

            @Override
            public void gt3DialogOnError(String error) {
                gt3GeetestUtils.cancelAllTask();
            }
        });
        //设置是否可以点击屏幕边缘关闭验证码
        gt3GeetestUtils.setDialogTouch(true);
    }

    /**
     * 登录
     */
    public void login(String username, String pwd, String pwdMD5) {
        Tools.hideKeyboard(editUser);
        passwordMD5 = "";
        if (!TextUtils.isEmpty(pwd)) {
            try {
                passwordMD5 = MD5.getMd5Value(pwd).toLowerCase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            passwordMD5 = pwdMD5;
        }
        getKeyAndSecret();
        if (null == auth2ServiceUpdate) {
            auth2ServiceUpdate = new OAuth2ServiceUpdate(LoginActivity.this);
        }
        auth2ServiceUpdate.getOAuth2Service(username, passwordMD5, new Oauth2CallBack() {
            @Override
            public void onData(String access_token) {
                getNetInfo(access_token);
            }
        });
    }

    /**
     * 获取用户信息
     *
     * @param access_token
     */
    private void getNetInfo(String access_token) {
        ContentValues header = new ContentValues();
        header.put("Authorization", "Bearer " + access_token);
        OkHttpConnector.httpGet(LoginActivity.this, Contants.URl.URL_OAUTH2 + "/oauth/user", header, null, new IGetListener() {
            @Override
            public void httpReqResult(String jsonString) {
                if (null != jsonString) {
                    try {
                        int code = HttpTools.getCode(jsonString);
                        String message = HttpTools.getMessageString(jsonString);
                        if (code == 0) {
                            UserInfo.employeeAccount = newPhone;
                            Tools.savePassWord(LoginActivity.this, password);//保存密码
                            Tools.savePassWordMD5(LoginActivity.this, passwordMD5);//保存密码(MD5加密后)
                            String response = HttpTools.getContentString(jsonString);
                            ResponseData data = HttpTools.getResponseContentObject(response);
                            int status = data.getInt("status");
                            if (status == 0) {
                                Tools.loadUserInfo(data, jsonString);
                                Tools.savetokenUserInfo(LoginActivity.this, jsonString);
                                corpId = data.getString("corp_id");
                                UserInfo.infoorgId = data.getString("org_uuid");
                                Tools.saveOrgId(LoginActivity.this, data.getString("org_uuid"));
                                Tools.saveStringValue(LoginActivity.this, Contants.storage.CORPID, corpId);//租户ID
                            }
                            if (status == 0) {//账号正常
                                singleDevicelogin();
                                getSkin(corpId);
                                Intent intent = new Intent(LoginActivity.this, MainActivity1.class);
                                intent.putExtra(MainActivity.KEY_NEDD_FRESH, false);
                                intent.putExtra(MainActivity.KEY_SKIN_CODE, "");
                                intent.putExtra(MainActivity1.FROM_LOGIN, true);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            } else {
                                ToastFactory.showToast(LoginActivity.this, "账号异常，请及时联系管理员");
                            }
                        } else {
                            ToastFactory.showToast(LoginActivity.this, message);
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });
    }

    private void getSkin(String corp_id) {
        RequestParams params = new RequestParams();
        params.put("corp_id", corp_id);
        RequestConfig config = new RequestConfig(this, HttpTools.GET_SKIN_INFO);
        config.hintString = "获取皮肤包";
        HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newoa/config/skin", config, params);
    }

    private void getKeyAndSecret() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_KEYSECERT);
        config.hintString = "获取key";
        HttpTools.httpPost(Contants.URl.URL_CPMOBILE, "/1.0/auth", config, null);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.GET_TS) {
            if (code == 0) {
                String difference = HttpTools.getContentString(jsonString);
                if (difference != null) {
                    SharedPreferences.Editor sharedata = CityPropertyApplication.getInstance().getSharedPreferences("APP_TS", 0).edit();
                    sharedata.putLong(HttpTools.DIFFERENCE, Long.parseLong(difference));
                    sharedata.commit();
                }
            }
        } else if (msg.arg1 == HttpTools.GET_SKIN_INFO) {//获取皮肤包
            if (code == 0) {
                JSONObject json = HttpTools.getContentJSONObject(jsonString);
                if (json.length() > 0) {
                    try {
                        int skin_code = json.getInt("skin_code");
                        Tools.saveStringValue(LoginActivity.this, Contants.storage.SKINCODE, String.valueOf(skin_code));//保存皮肤包
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                ToastFactory.showToast(LoginActivity.this, message);
            }
        } else if (msg.arg1 == HttpTools.GET_KEYSECERT) {
            if (code == 0) {
                try {
                    String jsonObject = HttpTools.getContentString(jsonString);
                    JSONObject sonJon = new JSONObject(jsonObject);
                    String key = sonJon.optString("key");
                    String secret = sonJon.optString("secret");
                    Tools.saveStringValue(LoginActivity.this, Contants.EMPLOYEE_LOGIN.key, key);
                    Tools.saveStringValue(LoginActivity.this, Contants.EMPLOYEE_LOGIN.secret, secret);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (msg.arg1 == HttpTools.POST_TWOJIYAN) {
            if (code == 0) {
                try {
                    JiYanTwoCheckEntity entity = new JiYanTwoCheckEntity();
                    entity = GsonUtils.gsonToBean(jsonString, JiYanTwoCheckEntity.class);
                    if (entity.getContent().getStatus() == 1) {//验证通过
                        gt3GeetestUtils.gt3TestFinish();
                        login(newPhone, password, "");
                    } else {
                        gt3GeetestUtils.gt3TestClose();
                        ToastFactory.showToast(LoginActivity.this, "极验验证失败,请稍后重试");
                    }
                } catch (Exception e) {
                    gt3GeetestUtils.gt3TestFinish();
                    login(newPhone, password, "");
                }
            } else {
                gt3GeetestUtils.gt3TestClose();
                ToastFactory.showToast(LoginActivity.this, "极验获取数据异常,请稍后重试");
            }

        } else if (msg.arg1 == HttpTools.GET_CZYOAUTH) {
            if (code == 0) {
                try {
                    CzyOauthEntity czyOauthEntity = new CzyOauthEntity();
                    czyOauthEntity = GsonUtils.gsonToBean(jsonString, CzyOauthEntity.class);
                    login(czyOauthEntity.getContent().getUsername(), "", czyOauthEntity.getContent().getPasswordMD5());
                } catch (Exception e) {
                }
            } else {
                ToastFactory.showToast(LoginActivity.this, message);
            }

        }
    }

    /**
     * 单设备登录
     */
    private void singleDevicelogin() {
        ContentValues params = new ContentValues();
        params.put("login_type", "2");//登录方式,1静默和2密码
        params.put("device_type", "1");//登录设备类别，1：安卓，2：IOS
        params.put("version", UpdateManager.getVersionName(LoginActivity.this));//APP版本号
        params.put("device_code", TokenUtils.getUUID(LoginActivity.this));//设备唯一编号
        params.put("device_info", TokenUtils.getDeviceInfor(LoginActivity.this));//设备详细信息（json字符创）
        params.put("device_name", TokenUtils.getDeviceBrand() + TokenUtils.getDeviceType());//设备名称（如三星S9）
        OkHttpConnector.httpPost(LoginActivity.this, Contants.URl.SINGLE_DEVICE + "cgjapp/single/device/login", params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                try {
                    SingleDeviceLogin singleDeviceLogin = GsonUtils.gsonToBean(response, SingleDeviceLogin.class);
                    String device_token = singleDeviceLogin.getContent().getDevice_token();
                    Tools.saveStringValue(LoginActivity.this, Contants.storage.DEVICE_TOKEN, device_token);
                    if (!TextUtils.isEmpty(device_token)) {
                        Log.d("lizc", TAG + "单设备登录OK::");
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == outAnim) {
            startLayout.setVisibility(View.GONE);
            rl_login_ad.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
        } else {

        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onFail(Message msg, String hintString) {
        super.onFail(msg, hintString);
    }

    @Override
    public void onCancel(Object tag, int requestCode) {
        super.onCancel(tag, requestCode);
        if (requestCode == HttpTools.GET_USER_INFO) {
            showContentView();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            CityPropertyApplication.finishOtherActivity(LoginActivity.class);
            LoginActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initClear() {
        UserInfo.uid = "";
        UserInfo.employeeAccount = "";
        UserInfo.color_token = "";
        UserInfo.job_uuid = "";
        UserInfo.sex = "";
        UserInfo.realname = "";
        UserInfo.password = "";
        UserInfo.cashierpassword = "";
        UserInfo.jobName = "";
        UserInfo.familyName = "";
        UserInfo.orgId = "";//组织架构ID
        UserInfo.infoorgId = "";//组织架构ID
        UserInfo.userinfoImg = "";//
        UserInfo.corp_id = "";
        UserInfo.salary_level = "";
        UserInfo.is_deleted = 0;
        UserInfo.special = 0;
        UserInfo.email = "";
        UserInfo.mobile = "";
        UserInfo.czy_id = 0;
    }
}

