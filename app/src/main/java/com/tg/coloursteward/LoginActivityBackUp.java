package com.tg.coloursteward;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.LocationSource;
import com.geetest.gt3unbindsdk.Bind.GT3GeetestBindListener;
import com.geetest.gt3unbindsdk.Bind.GT3GeetestUtilsBind;
import com.tg.coloursteward.application.CityPropertyApplication;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.database.SharedPreferencesTools;
import com.tg.coloursteward.entity.AccountEntity;
import com.tg.coloursteward.entity.JiYanTwoCheckEntity;
import com.tg.coloursteward.entity.SingleDeviceLogin;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.log.Logger;
import com.tg.coloursteward.module.MainActivity1;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.object.ImageParams;
import com.tg.coloursteward.serice.OAuth2Service;
import com.tg.coloursteward.updateapk.UpdateManager;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.TokenUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.http.OkHttpConnector;
import com.youmai.hxsdk.router.APath;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.tg.coloursteward.util.Utils.getDefaultOption;

/**
 * 登录页面
 *
 * @author Administrator
 */
@Route(path = APath.RE_LOGIN)
public class LoginActivityBackUp extends BaseActivity implements AnimationListener, AMapLocationListener, LocationSource {
    private static final String TAG = "LoginActivity";
    private EditText editUser;
    private EditText editPassword;
    private ImageView ivClose;
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
    private OAuth2Service oAuth2Service;
    private String str_latitude;
    private String str_longitude;
    public AMapLocationClient mlocationClient;
    private OnLocationChangedListener mListener;
    private String oauth;

    @Override
    public View getContentView() {
        // TODO Auto-generated method stub
        headView.setBackgroundColor(getResources().getColor(R.color.white));
        return null;
    }

    @Override
    public String getHeadTitle() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.submit:
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
                Tools.saveStringValue(LoginActivityBackUp.this, Contants.storage.LOGOIN_PASSWORD, password);
                Tools.saveStringValue(LoginActivityBackUp.this, Contants.storage.LOGOIN_PHONE, newPhone);
                getOauth2();
                loginGt();// 登录
                break;
            case R.id.forget_pwd:
                forgetPassword();// 忘记密码
                break;
            case R.id.iv_close://关闭
                finish();
                break;
        }
        return super.handClickEvent(v);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent getintent = getIntent();
        boolean loginOut = getintent.getBooleanExtra("login_out", false);
        if (loginOut) {
            SharedPreferencesTools.clearUserId(this);
        }
        initLocation();
        extras = getintent.getStringExtra(MainActivity.KEY_EXTRAS);
        contentLayout = findViewById(R.id.login_content);
        editUser = (EditText) findViewById(R.id.edit_user);
        startLayout = findViewById(R.id.start_layout);
        editPassword = (EditText) findViewById(R.id.edit_password);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        ivClose.setOnClickListener(singleListener);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(singleListener);
        findViewById(R.id.forget_pwd).setOnClickListener(singleListener);
        RequestParams params = new RequestParams();
        RequestConfig config = new RequestConfig(this, HttpTools.GET_TS, "");
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/timestamp", config, params);
        // editUser.setText(Tools.getUserName(this));
        showStartPager();
        CheckPermission();
        /**
         * 初始化
         * 务必放在onCreate方法里面执行
         */
        gt3GeetestUtils = new GT3GeetestUtilsBind(LoginActivityBackUp.this);
    }

    private void initLocation() {
        if (mlocationClient == null) {
            //初始化client
            mlocationClient = new AMapLocationClient(getApplicationContext());
            //设置定位参数
            mlocationClient.setLocationOption(getDefaultOption());
            // 设置定位监听
            mlocationClient.setLocationListener(this);

            mlocationClient.startLocation();

        }
    }

    private void CheckPermission() {
           /* if (Build.VERSION.SDK_INT >= 23)
            {
	            if (!Settings.canDrawOverlays(getApplicationContext()))
	            {
	                //启动Activity让用户授权
	                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
	                if (intent!=null){
	                    intent.setData(Uri.parse("package:" + getPackageName()));
	                    startActivity(intent);
	                }
	                return;
	            } else
	            {
	                //执行6.0以上绘制代码

	            }
	        } else
	        {
	            //执行6.0以下绘制代码
	        }
*/
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
        mHand.postDelayed(new Runnable() {
            @Override
            public void run() {
                ResponseData userInfoData = SharedPreferencesTools.getUserInfo(LoginActivityBackUp.this);
                String skin_code = Tools.getStringValue(LoginActivityBackUp.this, Contants.storage.SKINCODE);
                String corp_id = Tools.getStringValue(LoginActivityBackUp.this, Contants.storage.CORPID);
                if (userInfoData.length > 0 && StringUtils.isNotEmpty(skin_code) && StringUtils.isNotEmpty(corp_id)) {
                    Tools.loadUserInfo(userInfoData, null);
                    Intent intent = new Intent(LoginActivityBackUp.this, MainActivity1.class);
                    intent.putExtra(MainActivity.KEY_SKIN_CODE, skin_code);
                    intent.putExtra(MainActivity.KEY_EXTRAS, extras);
                    startActivity(intent);
                    finish();
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
     * 忘记密码
     */
    public void forgetPassword() {
        startActivity(new Intent(LoginActivityBackUp.this, ForgetPasswordActivity.class));
    }

    /**
     * 极验验证码
     */
    private void loginGt() {
        gt3GeetestUtils.getGeetest(LoginActivityBackUp.this, Contants.APP.captchaURL, Contants.APP.validateURL, null, new GT3GeetestBindListener() {
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
                String json = String.valueOf(jsonObject);
                try {
                    JSONObject jsonObject1 = new JSONObject(json);
                    int code = jsonObject1.getInt("success");
                    if (code == 1) {
                        String gt = jsonObject1.getString("gt");
                        String challenge = jsonObject1.getString("challenge");
                        int new_captcha = jsonObject1.getInt("new_captcha");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            /**
             * 往API1请求中添加参数
             * 添加数据为Map集合
             * 添加的数据以get形式提交
             */
            @Override
            public Map<String, String> gt3CaptchaApi1() {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("device_uuid", TokenUtils.getUUID(LoginActivityBackUp.this));
                Map<String, String> stringMap = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(LoginActivityBackUp.this, map));
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
                        validateParams.put("device_uuid", TokenUtils.getUUID(LoginActivityBackUp.this));
                        validateParams.put("geetest_challenge", res_json.getString("geetest_challenge"));
                        validateParams.put("geetest_validate", res_json.getString("geetest_validate"));
                        validateParams.put("geetest_seccode", res_json.getString("geetest_seccode"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Map<String, String> stringMap = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(LoginActivityBackUp.this, validateParams));
                    RequestConfig config = new RequestConfig(LoginActivityBackUp.this, HttpTools.POST_TWOJIYAN, "");
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
                objectMap.put("device_uuid", TokenUtils.getUUID(LoginActivityBackUp.this));
                Map<String, String> stringMap = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(LoginActivityBackUp.this, objectMap));
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
                            login();
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
                Log.i("dsd", "gt3DialogOnError");
                gt3GeetestUtils.cancelAllTask();
            }
        });
        //设置是否可以点击屏幕边缘关闭验证码
        gt3GeetestUtils.setDialogTouch(true);
    }

    /**
     * 登录
     */
    public void login() {

        try {
            String passwordMD5 = MD5.getMd5Value(password).toLowerCase();
            Tools.savePassWordMD5(getApplicationContext(), passwordMD5);//保存密码(MD5加密后)
            Tools.savePassWord(getApplicationContext(), password);//保存密码
            Tools.hideKeyboard(editUser);
            RequestParams params = new RequestParams();
            params.put("username", newPhone);
            params.put("password", passwordMD5);
            RequestConfig config = new RequestConfig(this, HttpTools.GET_LOGIN);
            config.hintString = "登录";
            HttpTools.httpPost(Contants.URl.URL_ICETEST, "/orgms/loginAccount", config, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    /**
     * 获取个人信息
     */
    private void getUserInfo(String accountUuid) {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_USER_INFO, null);
        config.hintString = "加载个人信息";
        RequestParams params = new RequestParams();
        params.put("username", newPhone);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/account", config, params);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.POST_IMAG) {
            ToastFactory.showToast(this, hintString);
            Logger.logd("POST_IMAG" + jsonString);
            ImageParams params = msg.getData().getParcelable(HttpTools.KEY_IMAGE_PARAMS);
            Logger.logd("path = " + params.path);
            Logger.logd("fileName =" + params.fileName);
            Logger.logd("position =" + params.position);
        } else if (msg.arg1 == HttpTools.GET_LOGIN) {
            if (code == 0) {
                Date dt = new Date();
                Long time = dt.getTime();
                String date = Tools.getDateToString(time);
                Tools.saveDateInfo(this, date);
                JSONObject content = HttpTools.getContentJSONObject(jsonString);
                try {
                    corpId = content.getString("corpId");
                    int status = content.getInt("status");
                    UserInfo.employeeAccount = content.getString("username");
                    UserInfo.password = password;
                    if (status > 0) {
                        ToastFactory.showToast(LoginActivityBackUp.this, "账号异常，请及时联系管理员");
                    } else {
                        Tools.saveStringValue(LoginActivityBackUp.this, Contants.storage.CORPID, corpId);
                        String accountUuid = content.getString("accountUuid");
                        getUserInfo(accountUuid);
//                        getKeyAndSecret();
                        getSkin(corpId);
//                        getOauth2();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                ToastFactory.showToast(LoginActivityBackUp.this, message);
            }
        } else if (msg.arg1 == HttpTools.GET_TS) {
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
                        Tools.saveStringValue(LoginActivityBackUp.this, Contants.storage.SKINCODE, String.valueOf(skin_code));//保存皮肤包
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                ToastFactory.showToast(LoginActivityBackUp.this, message);
                SharedPreferencesTools.clearUserId(LoginActivityBackUp.this);
            }
        } else if (msg.arg1 == HttpTools.GET_USER_INFO) {
            if (code == 0) {
                String jsonObject = HttpTools.getContentString(jsonString);
                ResponseData data = HttpTools.getResponseContentObject(jsonObject);
                AccountEntity accountEntity = GsonUtils.gsonToBean(jsonString, AccountEntity.class);
                UserInfo.infoorgId = accountEntity.getContent().getOrgId();
                Tools.saveOrgId(LoginActivityBackUp.this, accountEntity.getContent().getOrgId());
                Tools.saveStringValue(LoginActivityBackUp.this, "org_depart_name", accountEntity.getContent().getFamilyName());
                Tools.loadUserInfo(data, jsonString);
                getKeyAndSecret();
            }
        } else if (msg.arg1 == HttpTools.GET_KEYSECERT) {
            if (code == 0) {
                try {
                    String jsonObject = HttpTools.getContentString(jsonString);
                    JSONObject sonJon = new JSONObject(jsonObject);
                    String key = sonJon.optString("key");
                    String secret = sonJon.optString("secret");
                    Tools.saveStringValue(LoginActivityBackUp.this, Contants.EMPLOYEE_LOGIN.key, key);
                    Tools.saveStringValue(LoginActivityBackUp.this, Contants.EMPLOYEE_LOGIN.secret, secret);
                    getEmployeeInfo(key, secret);
                    if (!TextUtils.isEmpty(oauth) || !TextUtils.isEmpty(Tools.getAccess_token2(LoginActivityBackUp.this))) {
                        singleDevicelogin();
                    }
                    String skin_code = Tools.getStringValue(LoginActivityBackUp.this, Contants.storage.SKINCODE);
                    Intent intent = new Intent(this, MainActivity1.class);
                    intent.putExtra(MainActivity.KEY_NEDD_FRESH, false);
                    intent.putExtra(MainActivity.KEY_SKIN_CODE, skin_code);
                    intent.putExtra(MainActivity1.FROM_LOGIN, true);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                ToastFactory.showToast(LoginActivityBackUp.this, "登录失败，请重新登录");
            }
        } else if (msg.arg1 == HttpTools.SET_EMPLOYEE_INFO) {
            if (code == 0) {
                JSONObject json = HttpTools.getContentJSONObject(jsonString);
                if (json.length() > 0) {
                    Tools.setBooleanValue(LoginActivityBackUp.this, Contants.storage.EMPLOYEE_LOGIN, true);
                }
            }
        } else if (msg.arg1 == HttpTools.POST_TWOJIYAN) {
            if (code == 0) {
                JiYanTwoCheckEntity entity = new JiYanTwoCheckEntity();
                entity = GsonUtils.gsonToBean(jsonString, JiYanTwoCheckEntity.class);
                if (entity.getContent().getStatus() == 1) {//验证通过
                    gt3GeetestUtils.gt3TestFinish();
                    login();
                } else {
                    gt3GeetestUtils.gt3TestClose();
                    ToastFactory.showToast(LoginActivityBackUp.this, "极验验证失败,请稍后重试");
                }
            } else {
                gt3GeetestUtils.gt3TestClose();
                ToastFactory.showToast(LoginActivityBackUp.this, "极验获取数据异常,请稍后重试");
            }

        }
    }

    /**
     * aoutn2.0请求
     */

    private void getOauth2() {
        if (null == oAuth2Service) {
            oAuth2Service = new OAuth2Service(getApplication());
        }
        oAuth2Service.getOAuth2Service("");
        oauth = Tools.getAccess_token2(LoginActivityBackUp.this);
    }

    /**
     * 单设备登录
     */
    private void singleDevicelogin() {
        ContentValues params = new ContentValues();
        params.put("login_type", "2");//登录方式,1静默和2密码
        params.put("device_type", "1");//登录设备类别，1：安卓，2：IOS
        params.put("version", UpdateManager.getVersionName(LoginActivityBackUp.this));//APP版本号
        params.put("device_code", TokenUtils.getUUID(LoginActivityBackUp.this));//设备唯一编号
        params.put("device_info", TokenUtils.getDeviceInfor(LoginActivityBackUp.this));//设备详细信息（json字符创）
        params.put("device_name", TokenUtils.getDeviceBrand() + TokenUtils.getDeviceType());//设备名称（如三星S9）
        OkHttpConnector.httpPost(LoginActivityBackUp.this, Contants.URl.SINGLE_DEVICE + "cgjapp/single/device/login", params, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                try {
                    SingleDeviceLogin singleDeviceLogin = GsonUtils.gsonToBean(response, SingleDeviceLogin.class);
                    String device_token = singleDeviceLogin.getContent().getDevice_token();
                    Tools.saveStringValue(LoginActivityBackUp.this, Contants.storage.DEVICE_TOKEN, device_token);
                    if (!TextUtils.isEmpty(device_token)) {
                        Log.d("lizc", TAG + "单设备登录OK::");
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    /**
     * 调用登录的接口
     */
    public void getEmployeeInfo(String key, String secret) {

        String pwd = Tools.getPassWord(this);
        RequestConfig config = new RequestConfig(this, HttpTools.SET_EMPLOYEE_INFO, null);
        RequestParams params = new RequestParams();
        params.put("username", UserInfo.employeeAccount);
        try {
            params.put("password", MD5.getMd5Value(pwd).toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("key", key);
        params.put("secret", secret);
        HttpTools.httpPost(Contants.URl.URL_CPMOBILE, "/1.0/employee/login", config, params);
    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // TODO Auto-generated method stub
        if (animation == outAnim) {
            startLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
        } else {

        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFail(Message msg, String hintString) {
        super.onFail(msg, hintString);
    }

    @Override
    public void onCancel(Object tag, int requestCode) {
        // TODO Auto-generated method stub
        super.onCancel(tag, requestCode);
        if (requestCode == HttpTools.GET_USER_INFO) {
            showContentView();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (null != aMapLocation) {
            deactivate();
            if (aMapLocation.getErrorCode() == 0) {
                double latitude = aMapLocation.getLatitude();
                double longitude = aMapLocation.getLongitude();
                str_latitude = String.valueOf(latitude);
                str_longitude = String.valueOf(longitude);
                Tools.saveStringValue(getApplication(), Contants.storage.LATITUDE, str_latitude);
                Tools.saveStringValue(getApplication(), Contants.storage.LONGITUDE, str_longitude);
            } else {
                Log.e("AmapErr", "Location ERR:"
                        + aMapLocation.getErrorCode());
            }
        }
    }

    /**
     * 激活定位
     *
     * @param onLocationChangedListener
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
}

