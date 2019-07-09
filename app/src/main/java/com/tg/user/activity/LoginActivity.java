package com.tg.user.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.geetest.gt3unbindsdk.Bind.GT3GeetestBindListener;
import com.geetest.gt3unbindsdk.Bind.GT3GeetestUtilsBind;
import com.tg.coloursteward.BuildConfig;
import com.tg.coloursteward.R;
import com.tg.coloursteward.application.CityPropertyApplication;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.database.SharedPreferencesTools;
import com.tg.coloursteward.entity.CzyOauthEntity;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.module.MainActivity1;
import com.tg.coloursteward.net.DES;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.util.TokenUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.util.Utils;
import com.tg.user.callback.Oauth2CallBack;
import com.tg.user.entity.JiYanTwoCheckEntity;
import com.tg.user.entity.OauthUserEntity;
import com.tg.user.entity.SingleDeviceLogin;
import com.tg.user.model.UserModel;
import com.tg.user.oauth.OAuth2ServiceUpdate;
import com.youmai.hxsdk.router.APath;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.tg.coloursteward.application.CityPropertyApplication.lbsTraceClient;

/**
 * 登录页面
 */
@Route(path = APath.RE_LOGIN)
public class LoginActivity extends BaseActivity implements View.OnClickListener, HttpResponse {
    public static final String ACCOUNT = "account";
    public static final String CZY_CODE = "czy_code";
    public static final String USERACCOUNT = "user_account";
    private EditText et_login_phone;
    private EditText et_login_pwd;
    private ImageView iv_login_deloa;
    private ImageView iv_login_delpwd;
    private ImageView tv_login_czy;
    private TextView tv_login_forgetpwd;
    private Button btn_login_login;
    private Button btn_login_czylogin;
    private GT3GeetestUtilsBind gt3GeetestUtils;
    private UserModel userModel;
    private String account;
    private String password;
    private OAuth2ServiceUpdate auth2ServiceUpdate;
    private String corpId;
    private String extras;
    private String passwordMD5 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userModel = new UserModel(this);
        gt3GeetestUtils = new GT3GeetestUtilsBind(LoginActivity.this);
        initView();
        userModel.getTs(5, this);

    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }


    private void initView() {
        et_login_phone = findViewById(R.id.et_login_phone);
        et_login_pwd = findViewById(R.id.et_login_pwd);
        iv_login_deloa = findViewById(R.id.iv_login_deloa);
        iv_login_delpwd = findViewById(R.id.iv_login_delpwd);
        tv_login_czy = findViewById(R.id.tv_login_czy);
        tv_login_forgetpwd = findViewById(R.id.tv_login_forgetpwd);
        btn_login_login = findViewById(R.id.btn_login_login);
        btn_login_czylogin = findViewById(R.id.btn_login_czylogin);
        btn_login_login.setOnClickListener(this);
        btn_login_czylogin.setOnClickListener(this);
        iv_login_deloa.setOnClickListener(this);
        iv_login_delpwd.setOnClickListener(this);
        tv_login_forgetpwd.setOnClickListener(this);
        tv_login_czy.bringToFront();
        et_login_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    iv_login_deloa.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(et_login_pwd.getText().toString().trim())) {
                        btnClick();
                    }
                } else {
                    iv_login_deloa.setVisibility(View.GONE);
                    btnNoClick();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        et_login_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    iv_login_delpwd.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(et_login_phone.getText().toString().trim())) {
                        btnClick();
                    }
                } else {
                    iv_login_delpwd.setVisibility(View.GONE);
                    btnNoClick();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        Intent intent = getIntent();
        if (null != intent) {
            boolean loginOut = intent.getBooleanExtra("login_out", false);
            extras = intent.getStringExtra(MainActivity1.KEY_EXTRAS);
            if (loginOut) {
                UserInfo.initClear();
                StopYingYan();
                singleDevicelogout();
                SharedPreferencesTools.clearUserId(LoginActivity.this);
                //清空缓存
                SharedPreferencesTools.clearCache(LoginActivity.this);
                SharedPreferencesTools.clearAllData(LoginActivity.this);
                CityPropertyApplication.gotoLoginActivity(LoginActivity.this);
            }

            String account = intent.getStringExtra(ACCOUNT);
            String code = intent.getStringExtra(CZY_CODE);
            if (!TextUtils.isEmpty(code)) {
                ThirdLogin(code);
                spUtils.saveStringData(SpConstants.storage.THRID_CODE, code);
            }
            if (!TextUtils.isEmpty(account)) {
                setAccount(account);
            }
        }
        String account = SharedPreferencesUtils.getUserKey(this, USERACCOUNT);
        if (!TextUtils.isEmpty(account)) {
            setAccount(account);
        }
    }

    private void setAccount(String account) {
        et_login_phone.setText(account);
        et_login_phone.setSelection(account.length());
    }

    private void singleDevicelogout() {
        String device_code = Tools.getStringValue(this, Contants.storage.DEVICE_TOKEN);
        userModel.postSingleExit(7, device_code, this);
    }

    private void ThirdLogin(String code) {
        userModel.getCzyLogin(6, code, this);
    }

    private void btnClick() {
        btn_login_login.setBackground(getResources().getDrawable(R.drawable.bg_login_button_blue));
        btn_login_login.setTextColor(getResources().getColor(R.color.white));
    }

    private void btnNoClick() {
        btn_login_login.setTextColor(getResources().getColor(R.color.line_login_button));
    }

    @Override
    public void onClick(View v) {
        account = et_login_phone.getText().toString().trim();
        password = et_login_pwd.getText().toString().trim();
        switch (v.getId()) {
            case R.id.btn_login_login:
                if (TextUtils.isEmpty(account)) {
                    ToastUtil.showShortToast(this, "账号不能为空");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    ToastUtil.showShortToast(this, "密码不能为空");
                    return;
                }
                loginGt();
                break;
            case R.id.btn_login_czylogin:
                czyLogin();
                break;
            case R.id.iv_login_deloa:
                SharedPreferencesUtils.saveUserKey(this, USERACCOUNT, "");
                et_login_phone.getText().clear();
                break;
            case R.id.iv_login_delpwd:
                et_login_pwd.getText().clear();
                break;
            case R.id.tv_login_forgetpwd:
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));// 忘记密码
                break;
        }
    }

    /**
     * 彩之云授权登录
     */
    private void czyLogin() {
        ToastUtil.showShortToast(LoginActivity.this, "正在调起中...");
        String package_name = "cn.net.cyberway";
        String activity_path = "cn.net.cyberway.OauthWebviewActivity";
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("applicationId", DES.APP_ID);
        bundle.putString("packgename", BuildConfig.APPLICATION_ID);
        intent.putExtras(bundle);
        ComponentName cn = new ComponentName(package_name, activity_path);
        intent.setComponent(cn);
        try {
            if (intent.resolveActivityInfo(getPackageManager(), PackageManager.MATCH_DEFAULT_ONLY) != null) {
                startActivity(intent);
                LoginActivity.this.finish();
            } else {
                ToastUtil.showShortToast(LoginActivity.this, "未检测到手机有安装彩之云APP");
            }
        } catch (Exception e) {
            ToastUtil.showShortToast(LoginActivity.this, "请安装最新版彩之云APP");
        }
    }

    /**
     * 极验登录
     */
    private void loginGt() {
        gt3GeetestUtils.getGeetest(LoginActivity.this, Contants.APP.captchaURL, Contants.APP.validateURL, null, new GT3GeetestBindListener() {
            @Override
            public void gt3CloseDialog(int i) {
                super.gt3CloseDialog(i);
            }

            @Override
            public void gt3DialogReady() {
                super.gt3DialogReady();
            }

            @Override
            public void gt3FirstResult(JSONObject jsonObject) {
                super.gt3FirstResult(jsonObject);
            }

            @Override
            public Map<String, String> gt3CaptchaApi1() {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("device_uuid", TokenUtils.getUUID(LoginActivity.this));
                Map<String, String> stringMap = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(LoginActivity.this, map));
                return stringMap;
            }

            @Override
            public boolean gt3SetIsCustom() {
                return true;
            }

            @Override
            public void gt3GetDialogResult(boolean status, String result) {
                if (status) {
                    //利用异步进行解析这result进行二次验证，结果成功后调用gt3GeetestUtils.gt3TestFinish()方法调用成功后的动画，然后在gt3DialogSuccess执行成功之后的结果
                    JSONObject res_json = null;
                    Map<String, Object> validateParams = new HashMap<>();
                    try {
                        res_json = new JSONObject(result);
                        userModel.postGt(0, res_json.getString("geetest_challenge"), res_json.getString("geetest_validate")
                                , res_json.getString("geetest_seccode"), LoginActivity.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public Map<String, String> gt3SecondResult() {
                Map<String, Object> objectMap = new HashMap<String, Object>();
                objectMap.put("device_uuid", TokenUtils.getUUID(LoginActivity.this));
                Map<String, String> stringMap = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(LoginActivity.this, objectMap));
                return stringMap;
            }

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

            @Override
            public void gt3DialogOnError(String s) {
                gt3GeetestUtils.cancelAllTask();
            }
        });
        //设置是否可以点击屏幕边缘关闭验证码
        gt3GeetestUtils.setDialogTouch(true);
    }

    private void login(String accout, String pwd, String pwdMD5) {
        Utils.hideKeyboard(et_login_pwd);
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
        auth2ServiceUpdate.getOAuth2Service(accout, passwordMD5, new Oauth2CallBack() {
            @Override
            public void onData(String access_token) {
                getNetInfo(access_token);
            }
        });
    }

    private void getNetInfo(String access_token) {
        userModel.getOauthUser(2, access_token, this);
    }

    private void getKeyAndSecret() {
        userModel.postKeyAndSecret(1, this);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JiYanTwoCheckEntity entity = new JiYanTwoCheckEntity();
                        entity = GsonUtils.gsonToBean(result, JiYanTwoCheckEntity.class);
                        if (entity.getContent().getStatus() == 1) {//验证通过
                            gt3GeetestUtils.gt3TestFinish();
                            login(account, password, "");
                        } else {
                            gt3GeetestUtils.gt3TestClose();
                            ToastUtil.showShortToast(LoginActivity.this, "极验验证失败,请稍后重试");
                        }
                    } catch (Exception e) {
                        gt3GeetestUtils.gt3TestFinish();
                        login(account, password, "");
                    }
                } else {
                    gt3GeetestUtils.gt3TestClose();
                    ToastUtil.showShortToast(LoginActivity.this, "极验获取数据异常,请稍后重试");
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String key = jsonObject.optString("key");
                        String secret = jsonObject.optString("secret");
                        spUtils.saveStringData(Contants.EMPLOYEE_LOGIN.key, key);
                        spUtils.saveStringData(Contants.EMPLOYEE_LOGIN.secret, secret);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(result)) {
                    String response = HttpTools.getContentString(result);
                    OauthUserEntity oauthUserEntity = new OauthUserEntity();
                    oauthUserEntity = GsonUtils.gsonToBean(result, OauthUserEntity.class);
                    int status = oauthUserEntity.getContent().getStatus();
                    if (status == 0) {
                        ResponseData data = HttpTools.getResponseContentObject(response);
                        UserInfo.employeeAccount = account;
                        SharedPreferencesUtils.saveUserKey(this, USERACCOUNT, account);
                        Tools.savePassWord(LoginActivity.this, password);//保存密码
                        Tools.savePassWordMD5(LoginActivity.this, passwordMD5);//保存密码(MD5加密后)
                        Tools.loadUserInfo(data, result);
                        corpId = oauthUserEntity.getContent().getCorp_id();
                        UserInfo.infoorgId = data.getString("org_uuid");
                        Tools.saveOrgId(LoginActivity.this, data.getString("org_uuid"));
                        Tools.saveStringValue(LoginActivity.this, Contants.storage.CORPID, corpId);//租户ID
                        spUtils.saveStringData(SpConstants.storage.CORPID, corpId);
                        spUtils.saveStringData(SpConstants.storage.ORG_UUID, data.getString("org_uuid"));
                        spUtils.saveBooleanData(SpConstants.UserModel.ISLOGIN, true);
                        singleDevicelogin();
                        getSkin(corpId);
                        Intent intent = new Intent(LoginActivity.this, MainActivity1.class);
                        intent.putExtra(MainActivity1.KEY_NEDD_FRESH, false);
                        intent.putExtra(MainActivity1.KEY_SKIN_CODE, "");
                        intent.putExtra(MainActivity1.KEY_EXTRAS, extras);
                        intent.putExtra(MainActivity1.FROM_LOGIN, true);
                        startActivity(intent);
                        ToastUtil.showShortToast(this, "登录成功");
                        LoginActivity.this.finish();
                    } else {
                        ToastUtil.showShortToast(this, "账号异常，请及时联系管理员");
                        spUtils.clearKey(this);
                    }
                }
                break;
            case 3:
                if (!TextUtils.isEmpty(result)) {
                    SingleDeviceLogin singleDeviceLogin = GsonUtils.gsonToBean(result, SingleDeviceLogin.class);
                    String device_token = singleDeviceLogin.getContent().getDevice_token();
                    spUtils.saveStringData(SpConstants.storage.DEVICE_TOKEN, device_token);
                }
                break;
            case 4:
                if (!TextUtils.isEmpty(result)) {
                    JSONObject json = HttpTools.getContentJSONObject(result);
                    try {
                        int skin_code = json.getInt("skin_code");
                        spUtils.saveStringData(SpConstants.storage.SKINCODE, String.valueOf(skin_code));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 5:
                if (!TextUtils.isEmpty(result)) {
                    String difference = HttpTools.getContentString(result);
                    if (difference != null) {
                        spUtils.saveLongData(SpConstants.UserModel.DIFFERENCE, Long.valueOf(difference));
                    }
                }
                break;
            case 6:
                if (!TextUtils.isEmpty(result)) {
                    CzyOauthEntity czyOauthEntity = new CzyOauthEntity();
                    czyOauthEntity = GsonUtils.gsonToBean(result, CzyOauthEntity.class);
                    login(czyOauthEntity.getContent().getUsername(), "", czyOauthEntity.getContent().getPasswordMD5());
                }
                break;
        }
    }

    private void getSkin(String corpId) {
        userModel.postSkin(4, corpId, this);
    }

    private void singleDevicelogin() {
        userModel.postSingleDevice(3, "2", this);
    }

    private void StopYingYan() {
        if (null != lbsTraceClient) {
            lbsTraceClient.stopGather(null);
        }
    }
}
