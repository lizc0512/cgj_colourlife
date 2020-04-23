package com.tg.user.activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.geetest.gt3unbindsdk.Bind.GT3GeetestBindListener;
import com.geetest.gt3unbindsdk.Bind.GT3GeetestUtilsBind;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.tg.coloursteward.BuildConfig;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.baseModel.RequestEncryptionUtils;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.module.MainActivity;
import com.tg.coloursteward.net.DES;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.NumberUtils;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.tg.coloursteward.util.SoftKeyboardUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.util.TokenUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.CircleImageView;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.user.entity.CheckWhiteEntity;
import com.tg.user.entity.JiYanTwoCheckEntity;
import com.tg.user.entity.OauthUserEntity;
import com.tg.user.entity.SendCodeEntity;
import com.tg.user.entity.SingleDeviceLogin;
import com.tg.user.model.UserCzyModel;
import com.tg.user.model.UserModel;
import com.tg.user.oauth.OAuth2ServiceUpdate;
import com.tg.user.view.CustomDialog;
import com.youmai.hxsdk.router.APath;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录页面
 */
@Route(path = APath.RE_LOGIN)
public class LoginActivity extends BaseActivity implements View.OnClickListener, HttpResponse, TextWatcher {
    public static final String ACCOUNT = "account";
    public static final String CZY_CODE = "czy_code";
    public static final String USERACCOUNT = "user_account";
    public static final String USEROA = "user_oa";
    public static final String USERNAME = "user_name";
    private ConstraintLayout constrant_layout;
    private View distance_view;
    private CircleImageView iv_head_pic;
    private TextView tv_welcome;
    private EditText edit_account;
    private EditText edit_password;
    private LinearLayout sms_login_layout;
    private EditText edit_smscode;
    private Button btn_get_code;
    private TextView tv_login_smscode;
    private TextView tv_forget_pawd;
    private Button btn_login;
    private TextView tv_login_byczy;
    private GT3GeetestUtilsBind gt3GeetestUtils;
    private UserModel userModel;
    private UserCzyModel userCzyModel;
    private String account;
    private String password;
    private OAuth2ServiceUpdate auth2ServiceUpdate;
    private String corpId;
    private String extras;
    private String loginType = "1";//1：账号密码登录，2：短信验证码登录，3：手机号码密码登录，4：彩之云授权登录 5：彩之云color-token登录
    private String user_type;//1：oa账号，2：彩之云账号
    private String czyAccessToken;//彩之云授权token
    private CustomDialog reviewDialog;
    private String hotLine = "1010-1778";
    private final float mLogoScale = 0.5f;//logo 缩放比例
    private final int mAnimTime = 300;//动画时间
    private boolean isShow = false;
    private TextView tv_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_login);
        userModel = new UserModel(this);
        userCzyModel = new UserCzyModel(this);
        tintManager.setStatusBarTintColor(this.getResources().getColor(R.color.transparent)); //设置状态栏的颜色
        gt3GeetestUtils = new GT3GeetestUtilsBind(LoginActivity.this);
        initView();
        initPermission();
        userModel.getTs(5, this);

    }

    private void initPermission() {
        XXPermissions.with(this)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {

                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {

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


    private void initView() {
        tv_register = findViewById(R.id.tv_register);
        constrant_layout = findViewById(R.id.constrant_layout);
        distance_view = findViewById(R.id.distance_view);
        iv_head_pic = findViewById(R.id.iv_head_pic);
        tv_welcome = findViewById(R.id.tv_welcome);
        edit_account = findViewById(R.id.edit_account);
        edit_password = findViewById(R.id.edit_password);
        sms_login_layout = findViewById(R.id.sms_login_layout);
        edit_smscode = findViewById(R.id.edit_smscode);
        btn_get_code = findViewById(R.id.btn_get_code);
        tv_login_smscode = findViewById(R.id.tv_login_smscode);
        tv_forget_pawd = findViewById(R.id.tv_forget_pawd);
        btn_login = findViewById(R.id.btn_login);
        tv_login_byczy = findViewById(R.id.tv_login_byczy);

        tv_forget_pawd.setOnClickListener(this);
        tv_login_smscode.setOnClickListener(this);
        btn_get_code.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);

        tv_login_byczy.setOnClickListener(this);
        edit_account.addTextChangedListener(this);
        edit_password.addTextChangedListener(this);
        edit_smscode.addTextChangedListener(this);
        btn_get_code.setEnabled(false);
        btn_login.setEnabled(false);
        Intent intent = getIntent();
        if (null != intent) {
            boolean loginOut = intent.getBooleanExtra("login_out", false);
            extras = intent.getStringExtra(MainActivity.KEY_EXTRAS);
            if (loginOut) {
                exitClearAllData(true);
            }

            String account = intent.getStringExtra(ACCOUNT);
            if (!TextUtils.isEmpty(account)) {
                setAccount(account);
            }
        }
        String account = SharedPreferencesUtils.getUserKey(this, USERACCOUNT);
        if (!TextUtils.isEmpty(account)) {
            setAccount(account);
        }
        setHeadPic();
        softInputListener();
    }

    private void softInputListener() {
        constrant_layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                constrant_layout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //判断现在软键盘的开关状态
                        if (SoftKeyboardUtils.isSoftShowing(LoginActivity.this)) {
                            if (!isShow) {
                                distance_view.setVisibility(View.GONE);
                                isShow = true;
                                // 执行缩小动画
                                iv_head_pic.setPivotX(iv_head_pic.getWidth() / 2f);
                                iv_head_pic.setPivotY(iv_head_pic.getHeight());
                                AnimatorSet animatorSet = new AnimatorSet();
                                ObjectAnimator scaleX = ObjectAnimator.ofFloat(iv_head_pic, "scaleX", 1.0f, mLogoScale);
                                ObjectAnimator scaleY = ObjectAnimator.ofFloat(iv_head_pic, "scaleY", 1.0f, mLogoScale);
                                ObjectAnimator translationY = ObjectAnimator.ofFloat(iv_head_pic, "translationY", 0.0f, 1);
                                animatorSet.play(translationY).with(scaleX).with(scaleY);
                                animatorSet.setDuration(mAnimTime);
                                animatorSet.start();
                            }
                        } else {
                            if (isShow) {
                                distance_view.setVisibility(View.VISIBLE);
                                iv_head_pic.setPivotX(iv_head_pic.getWidth() * 2f);
                                iv_head_pic.setPivotY(iv_head_pic.getHeight());
                                AnimatorSet animatorSet = new AnimatorSet();
                                ObjectAnimator scaleX = ObjectAnimator.ofFloat(iv_head_pic, "scaleX", 1.0f, 1.0f);
                                ObjectAnimator scaleY = ObjectAnimator.ofFloat(iv_head_pic, "scaleY", 1.0f, 1.0f);
                                ObjectAnimator translationY = ObjectAnimator.ofFloat(iv_head_pic, "translationY", iv_head_pic.getTranslationY(), 0);
                                animatorSet.play(translationY).with(scaleX).with(scaleY);
                                animatorSet.setDuration(mAnimTime);
                                animatorSet.start();
                                isShow = false;
                            }

                        }
                    }
                }, 100L);
            }
        });
    }

    private void setHeadPic() {
        String userOa = SharedPreferencesUtils.getUserKey(this, USEROA);
        if (!TextUtils.isEmpty(userOa)) {
            String headIcon = Contants.Html5.HEAD_ICON_URL + "/avatar?uid=" + userOa;
            GlideUtils.loadImageView(LoginActivity.this, headIcon, iv_head_pic);
            tv_welcome.setText("欢迎回来");
        }
    }

    private void setAccount(String account) {
        edit_account.setText(account);
        edit_account.setSelection(account.length());
    }

    private void singleDevicelogout() {
        String device_code = spUtils.getStringData(SpConstants.storage.DEVICE_TOKEN, "");
        userModel.postSingleExit(7, device_code, this);
    }

    private void ThirdLogin(String code) {
        try {
            loginType = "4";
            login(code, MD5.getMd5Value(code).toLowerCase(), loginType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLoginButton() {
        account = edit_account.getText().toString().trim();
        if (isSmsLogin) {
            password = edit_smscode.getText().toString().trim();
        } else {
            password = edit_password.getText().toString().trim();
        }
        if (NumberUtils.IsPhoneNumber(account) && !isSmsLogin) {
            userModel.getUserType(12, account, false, this);
        } else {
            tv_forget_pawd.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
            btn_login.setBackground(getResources().getDrawable(R.drawable.login_button_default));
            btn_login.setTextColor(getResources().getColor(R.color.white));
            btn_login.setEnabled(false);
        } else {
            btn_login.setBackground(getResources().getDrawable(R.drawable.login_button_click));
            btn_login.setTextColor(getResources().getColor(R.color.color_1082ff));
            btn_login.setEnabled(true);
        }
    }

    private void setCodeBtn() {
        account = edit_account.getText().toString().trim();
        if (countStart == 0) {
            if (11 == account.length()) {
                btn_get_code.setTextColor(getResources().getColor(R.color.color_1890ff));
                btn_get_code.setBackgroundResource(R.drawable.sms_button_click);
                btn_get_code.setEnabled(true);
            } else {
                btn_get_code.setTextColor(getResources().getColor(R.color.white));
                btn_get_code.setBackgroundResource(R.drawable.sms_button_default);
                btn_get_code.setEnabled(false);
            }
        }
        if (account.equals(SharedPreferencesUtils.getUserKey(this, USERACCOUNT))) {
            setHeadPic();
        } else {
            iv_head_pic.setImageResource(R.drawable.login_logo);
            tv_welcome.setText("欢迎使用彩管家");
        }
    }

    private boolean isSmsLogin = false;

    @Override
    public void onClick(View v) {
        account = edit_account.getText().toString().trim();
        switch (v.getId()) {
            case R.id.btn_login:
                if (isSmsLogin) {
                    loginType = "5";
                    password = edit_smscode.getText().toString().trim();
                    SoftKeyboardUtils.hideSoftKeyboard(LoginActivity.this, edit_account);
                    userCzyModel.getAuthToken(11, account, password, "3", this);
                } else {
                    password = edit_password.getText().toString().trim();
                    SoftKeyboardUtils.hideSoftKeyboard(LoginActivity.this, edit_account);
                    if (6 > password.length()) {
                        ToastUtil.showLoginToastCenter(this, "请输入不少于6位数的密码");
                        return;
                    }
                    if (NumberUtils.IsPhoneNumber(account)) {
                        userModel.getUserType(8, account, true, this);
                        return;
                    } else {
                        loginType = "1";
                    }
                    loginGt();
                }
                break;
            case R.id.tv_login_byczy:
                czyLogin();
                break;
            case R.id.tv_forget_pawd:
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                if (!TextUtils.isEmpty(account) && 11 == account.length() && NumberUtils.IsPhoneNumber(account)) {
                    intent.putExtra(ACCOUNT, account);
                } else {
                    intent.putExtra(ACCOUNT, "");
                }
                startActivity(intent);// 忘记密码
                break;
            case R.id.tv_login_smscode:
                isSmsLogin = !isSmsLogin;
                if (isSmsLogin) { //短信验证码登录
                    if (!NumberUtils.IsPhoneNumber(account)) {
                        edit_account.setText("");
                    }
                    edit_password.setVisibility(View.GONE);
                    edit_account.setHint("请输入彩之云账号");
                    sms_login_layout.setVisibility(View.VISIBLE);
                    sms_login_layout.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this, R.anim.push_right_alpha));
                    tv_login_smscode.setText("账号密码登录");
                    edit_account.setInputType(InputType.TYPE_CLASS_NUMBER); //输入类型
                    edit_account.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                } else {
                    sms_login_layout.setVisibility(View.GONE);
                    edit_password.setVisibility(View.VISIBLE);
                    edit_account.setHint("请输入彩之云账号/OA账号");
                    edit_password.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this, R.anim.push_right_alpha));
                    tv_login_smscode.setText("短信验证码登录");
                    edit_account.setInputType(InputType.TYPE_CLASS_TEXT);
                    edit_account.setFilters(new InputFilter[]{new InputFilter.LengthFilter(24)});
                }
                break;
            case R.id.btn_get_code:
                userCzyModel.getSmsCode(6, account, 5, 1, true, this);//找回密码获取短信验证码
                break;
            case R.id.tv_register:
                Intent register = new Intent(this, CompanyInfoActivity.class);
                startActivity(register);
                break;
        }
    }

    /**
     * 彩之云授权登录
     */
    private void czyLogin() {
        ToastUtil.showLoginToastCenter(LoginActivity.this, "正在调起中...");
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
            } else {
                DialogFactory.getInstance().showDialog(LoginActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://mapp.colourlife.com/m.html"));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(Intent.createChooser(intent, "请选择浏览器"));
                        } else {
                            ToastUtil.showLoginToastCenter(LoginActivity.this, "请下载浏览器");
                        }
                    }
                }, null, "你的手机未安装彩之云客户端", "下载", null);
            }
        } catch (Exception e) {
            ToastUtil.showLoginToastCenter(LoginActivity.this, "请安装最新版彩之云APP");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String code = intent.getStringExtra(CZY_CODE);
        if (!TextUtils.isEmpty(code)) {
            ThirdLogin(code);
            spUtils.saveStringData(SpConstants.storage.THRID_CODE, code);
        }
    }

    private MyTimeCount myTimeCount = null;


    /***初始化计数器**/
    private void initTimeCount() {
        cancelTimeCount();
        countStart = 1;
        btn_get_code.setEnabled(false);
        myTimeCount = new MyTimeCount(60000, 1000);
        myTimeCount.start();
    }

    private void cancelTimeCount() {
        if (myTimeCount != null) {
            myTimeCount.cancel();
            myTimeCount = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimeCount();
    }

    private int countStart = 0;//计时器是否工作

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        setCodeBtn();
        setLoginButton();
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
            account = edit_account.getText().toString().trim();
            if (TextUtils.isEmpty(account)) {
                btn_get_code.setTextColor(getResources().getColor(R.color.white));
                btn_get_code.setBackgroundResource(R.drawable.sms_button_default);
                btn_get_code.setEnabled(false);
            } else {
                btn_get_code.setTextColor(getResources().getColor(R.color.color_1890ff));
                btn_get_code.setBackgroundResource(R.drawable.sms_button_click);
                btn_get_code.setEnabled(true);
            }
            countStart = 0;
            btn_get_code.requestFocus();
            btn_get_code.setText("重新获取");
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            long currentSecond = millisUntilFinished / 1000;
            btn_get_code.setTextColor(getResources().getColor(R.color.white));
            btn_get_code.setBackgroundResource(R.drawable.sms_button_default);
            btn_get_code.setText(currentSecond + "S");
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
                if ("205".equals(s)) {
                    gt3GeetestUtils.gt3TestFinish();
                    login(account, getPawdMD5(), loginType);
                } else {
                    gt3GeetestUtils.cancelAllTask();
                }
            }
        });
        //设置是否可以点击屏幕边缘关闭验证码
        gt3GeetestUtils.setDialogTouch(true);
    }

    private void login(String accout, String pwdMD5, String loginType) {
        SoftKeyboardUtils.hideSoftKeyboard(LoginActivity.this, edit_account);
        exitClearAllData(true);
        if (null == auth2ServiceUpdate) {
            auth2ServiceUpdate = new OAuth2ServiceUpdate(LoginActivity.this, loginType);
        } else {
            auth2ServiceUpdate.setLoginType(loginType);
        }
        if ("5".equals(loginType)) {
            auth2ServiceUpdate.setCzyAccessToken(czyAccessToken);
        }
        auth2ServiceUpdate.getOAuth2Service(accout, pwdMD5, this::getNetInfo);
    }

    private void getNetInfo(String access_token) {
        userModel.getOauthUser(2, access_token, true, this);
    }


    private String getPawdMD5() {
        String pawdMd5 = "";
        try {
            pawdMd5 = MD5.getMd5Value(password).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pawdMd5;
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
                            login(account, getPawdMD5(), loginType);
                        } else {
                            gt3GeetestUtils.gt3TestClose();
                            ToastUtil.showLoginToastCenter(LoginActivity.this, "极验验证失败,请稍后重试");
                        }
                    } catch (Exception e) {
                        gt3GeetestUtils.gt3TestFinish();
                        login(account, getPawdMD5(), loginType);
                    }
                } else {
                    gt3GeetestUtils.gt3TestClose();
                    ToastUtil.showLoginToastCenter(LoginActivity.this, "极验获取数据异常,请稍后重试");
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(result)) {
                    String response = HttpTools.getContentString(result);
                    OauthUserEntity oauthUserEntity = new OauthUserEntity();
                    try {
                        oauthUserEntity = GsonUtils.gsonToBean(result, OauthUserEntity.class);
                        int status = oauthUserEntity.getContent().getStatus();
                        if (status == 0) {
                            ResponseData data = HttpTools.getResponseContentObject(response);
                            Tools.savePassWordMD5(LoginActivity.this, getPawdMD5());//保存密码(MD5加密后)
                            Tools.loadUserInfo(data, result);
                            corpId = oauthUserEntity.getContent().getCorp_id();
                            UserInfo.infoorgId = data.getString("org_uuid");
                            if (loginType.equals("4") || loginType.equals("5")) {
                                Tools.savePassWordMD5(LoginActivity.this, data.getString("password"));//保存密码(MD5加密后)
                            }
                            String employeeAccount = data.getString("username");
                            String employeeName = data.getString("name");
                            UserInfo.employeeAccount = employeeAccount;
                            SharedPreferencesUtils.saveUserKey(this, USEROA, employeeAccount);
                            if (!"4".equals(loginType)) {
                                SharedPreferencesUtils.saveUserKey(this, USERACCOUNT, account);
                            }
                            SharedPreferencesUtils.saveUserKey(this, USERNAME, employeeName);
                            Tools.saveOrgId(LoginActivity.this, data.getString("org_uuid"));
                            Tools.saveStringValue(LoginActivity.this, Contants.storage.CORPID, corpId);//租户ID
                            spUtils.saveStringData(SpConstants.storage.CORPID, corpId);
                            spUtils.saveStringData(SpConstants.storage.ORG_UUID, data.getString("org_uuid"));
                            spUtils.saveBooleanData(SpConstants.UserModel.ISLOGIN, true);
                            spUtils.saveStringData(SpConstants.UserModel.ACCOUNT_UUID, data.getString("account_uuid"));
                            singleDevicelogin();
                            getSkin(corpId);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra(MainActivity.KEY_NEDD_FRESH, false);
                            intent.putExtra(MainActivity.KEY_SKIN_CODE, "");
                            intent.putExtra(MainActivity.KEY_EXTRAS, extras);
                            intent.putExtra(MainActivity.FROM_LOGIN, true);
                            startActivity(intent);
                            ToastUtil.showLoginToastCenter(this, "登录成功");
                            LoginActivity.this.finish();
                        } else {
                            ToastUtil.showLoginToastCenter(this, "账号异常，请及时联系管理员");
                            spUtils.clearKey(this);
                        }
                    } catch (Exception e) {
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
                initTimeCount();
                try {
                    SendCodeEntity sendCodeEntity = GsonUtils.gsonToBean(result, SendCodeEntity.class);
                    ToastUtil.showShortToast(LoginActivity.this, sendCodeEntity.getContent().getNotice());
                } catch (Exception e) {
                    ToastUtil.showShortToast(LoginActivity.this, "验证码已发送");
                }
                break;
            case 8:
                if (!TextUtils.isEmpty(result)) {
                    String content = RequestEncryptionUtils.getContentString(result);
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        user_type = jsonObject.getString("user_type");
                        if ("1".equals(user_type)) {//1：oa账号，2：彩之云账号
                            loginType = "1";
                            loginGt();
                            tv_forget_pawd.setVisibility(View.VISIBLE);
                        } else if ("2".equals(user_type)) {
                            loginType = "5";
                            tv_forget_pawd.setVisibility(View.GONE);
                            userCzyModel.getCheckWhite(10, account, 1, this);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    loginType = "5";
                    tv_forget_pawd.setVisibility(View.GONE);
                    userCzyModel.getCheckWhite(10, account, 1, this);
                }
                break;
            case 9:
                if (!TextUtils.isEmpty(result)) {
                    czyAccessToken = result;
                    loginGt();
                }
                break;
            case 10:
                if (!TextUtils.isEmpty(result)) {
                    CheckWhiteEntity checkWhiteEntity = new CheckWhiteEntity();
                    checkWhiteEntity = GsonUtils.gsonToBean(result, CheckWhiteEntity.class);
                    String isWhite = checkWhiteEntity.getContent().getIs_white();
                    hotLine = checkWhiteEntity.getContent().getHotLine();
                    if (TextUtils.isEmpty(hotLine)) {
                        hotLine = "1010-1778";
                    }
                    if ("1".equals(isWhite) || "0".equals(isWhite)) {
                        userCzyModel.getAuthToken(9, account, password, "1", this);
                    } else if ("5".equals(isWhite)) {
                        showReviewDialog("更换手机号审核中");
                    } else {
                        isSmsLogin = !isSmsLogin;
                        ToastUtil.showShortToast(LoginActivity.this, "您的账号长时间未登录，请使用验证码登录");
                        if (!NumberUtils.IsPhoneNumber(account)) {
                            edit_account.setText("");
                        }
                        edit_password.setVisibility(View.GONE);
                        edit_account.setHint("请输入手机号码");
                        sms_login_layout.setVisibility(View.VISIBLE);
                        sms_login_layout.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this, R.anim.push_right_alpha));
                        tv_login_smscode.setText("账号密码登录");
                        edit_account.setInputType(InputType.TYPE_CLASS_NUMBER); //输入类型
                        edit_account.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                    }
                }
                break;
            case 11:
                if (!TextUtils.isEmpty(result)) {
                    czyAccessToken = result;
                    login(account, getPawdMD5(), loginType);
                }
                break;
            case 12:
                if (!TextUtils.isEmpty(result)) {
                    String content = RequestEncryptionUtils.getContentString(result);
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        user_type = jsonObject.getString("user_type");
                        if ("1".equals(user_type)) {//1：oa账号，2：彩之云账号
                            tv_forget_pawd.setVisibility(View.VISIBLE);
                        } else if ("2".equals(user_type)) {
                            tv_forget_pawd.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    tv_forget_pawd.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void showReviewDialog(String tips) {
        reviewDialog = new CustomDialog(LoginActivity.this, R.style.custom_dialog_theme);
        reviewDialog.show();
        reviewDialog.setCancelable(false);
        reviewDialog.dialog_content.setText(tips);
        reviewDialog.dialog_line.setVisibility(View.VISIBLE);
        reviewDialog.dialog_button_ok.setGravity(Gravity.CENTER_HORIZONTAL);
        reviewDialog.dialog_button_cancel.setText(getResources().getString(R.string.message_define));
        reviewDialog.dialog_button_ok.setText(getResources().getString(R.string.user_contact_service));
        reviewDialog.dialog_button_cancel.setOnClickListener(v -> reviewDialog.dismiss());
        reviewDialog.dialog_button_ok.setOnClickListener(v -> {
            XXPermissions.with(LoginActivity.this)
                    .permission(Manifest.permission.CALL_PHONE)
                    .request(new OnPermission() {
                        @Override
                        public void hasPermission(List<String> granted, boolean isAll) {
                            Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + hotLine));//跳转到拨号界面，同时传递电话号码
                            startActivity(dialIntent);
                        }

                        @Override
                        public void noPermission(List<String> denied, boolean quick) {
                            ToastUtil.showShortToast(LoginActivity.this, "需要您到设置里打开拨号权限");
                        }
                    });
            reviewDialog.dismiss();
        });
    }

    private void getSkin(String corpId) {
        userModel.postSkin(4, corpId, this);
    }

    private void singleDevicelogin() {
        userModel.postSingleDevice(3, "2", true, this);
    }

}
