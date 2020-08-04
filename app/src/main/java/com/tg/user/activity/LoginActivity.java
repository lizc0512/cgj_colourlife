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
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.geetest.sdk.GT3ConfigBean;
import com.geetest.sdk.GT3ErrorBean;
import com.geetest.sdk.GT3GeetestUtils;
import com.geetest.sdk.GT3Listener;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.tg.coloursteward.BuildConfig;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.baseModel.RequestEncryptionUtils;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.entity.CropListEntity;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.model.MicroModel;
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
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.CircleImageView;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.user.entity.JiYanTwoCheckEntity;
import com.tg.user.entity.OauthUserEntity;
import com.tg.user.entity.SendCodeEntity;
import com.tg.user.entity.SingleDeviceLogin;
import com.tg.user.model.UserLoginModel;
import com.tg.user.model.UserModel;
import com.tg.user.oauth.OAuth2ServiceUpdate;
import com.tg.user.view.CustomDialog;
import com.youmai.hxsdk.router.APath;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 登录页面
 */
@Route(path = APath.RE_LOGIN)
public class LoginActivity extends BaseActivity implements View.OnClickListener, HttpResponse, TextWatcher {
    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
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
    private ImageView iv_login_byczy;
    private UserModel userModel;
    private UserLoginModel userLoginModel;
    private String account;
    private String password;
    private OAuth2ServiceUpdate auth2ServiceUpdate;
    private String extras;
    private String loginType = "1";//1：账号密码登录，2：短信验证码登录，3：手机号码密码登录，4：彩之云授权登录，5：彩之云colour-token登录(暂无该类型)，6：微信授权登录
    private String user_type;//1：oa账号，2：彩之云账号
    private String czyAccessToken;//彩之云授权token
    private CustomDialog reviewDialog;
    private String hotLine = "1010-1778";
    private final float mLogoScale = 0.5f;//logo 缩放比例
    private final int mAnimTime = 300;//动画时间
    private boolean isShow = false;
    private TextView tv_register;
    private MicroModel microModel;
    private List<CropListEntity.ContentBean> cropList = new ArrayList<>();
    private ImageView iv_wx_login;
    private String openid;
    private String unionid;
    private GT3GeetestUtils gt3GeetestUtils;
    private GT3ConfigBean gt3ConfigBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_login);
        userModel = new UserModel(this);
        userLoginModel = new UserLoginModel(this);
        microModel = new MicroModel(this);
        tintManager.setStatusBarTintColor(this.getResources().getColor(R.color.transparent)); //设置状态栏的颜色
        gt3GeetestUtils = new GT3GeetestUtils(this);
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
        iv_wx_login = findViewById(R.id.iv_wx_login);
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
        iv_login_byczy = findViewById(R.id.iv_login_byczy);

        tv_forget_pawd.setOnClickListener(this);
        tv_login_smscode.setOnClickListener(this);
        btn_get_code.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        iv_wx_login.setOnClickListener(this);

        iv_login_byczy.setOnClickListener(this);
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
                exitClearAllData(LoginActivity.this,true);
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

    private void setPassword(String pwd) {
        edit_password.setText(pwd);
        edit_password.setSelection(pwd.length());
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
                setLogin();
                break;
            case R.id.iv_login_byczy:
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
                    edit_account.setHint("请输入手机号");
                    sms_login_layout.setVisibility(View.VISIBLE);
                    sms_login_layout.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this, R.anim.push_right_alpha));
                    tv_login_smscode.setText("账号密码登录");
                    edit_account.setInputType(InputType.TYPE_CLASS_NUMBER); //输入类型
                    edit_account.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                } else {
                    sms_login_layout.setVisibility(View.GONE);
                    edit_password.setVisibility(View.VISIBLE);
                    edit_account.setHint("请输入手机号/OA账号");
                    edit_password.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this, R.anim.push_right_alpha));
                    tv_login_smscode.setText("短信验证码登录");
                    edit_account.setInputType(InputType.TYPE_CLASS_TEXT);
                    edit_account.setFilters(new InputFilter[]{new InputFilter.LengthFilter(24)});
                }
                break;
            case R.id.btn_get_code:
                userLoginModel.getSmsCode(6, account, 3, 1, true, this);//找回密码获取短信验证码
                break;
            case R.id.tv_register:
                Intent register = new Intent(this, RegisterActivity.class);
                startActivityForResult(register, 4000);
                break;
            case R.id.iv_wx_login:
                Platform plat = ShareSDK.getPlatform(Wechat.NAME);
                if (!plat.isClientValid()) {
                    ToastUtil.showShortToast(this, "您尚未安装微信");
                    return;
                }
                ShareSDK.setActivity(this);
                plat.setPlatformActionListener(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        Iterator ite = hashMap.entrySet().iterator();
                        while (ite.hasNext()) {
                            Map.Entry entry = (Map.Entry) ite.next();
                            if ("openid".equals(entry.getKey())) {
                                openid = entry.getValue().toString();
                            }
                            if ("unionid".equals(entry.getKey())) {
                                unionid = entry.getValue().toString();
                            }
                        }
                        isBindWx(openid, unionid);
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(Platform platform, int i) {

                    }
                });
                plat.showUser(null);
                break;
        }
    }

    private void setLogin() {
        if (isSmsLogin) {
            loginType = "2";
            password = edit_smscode.getText().toString().trim();
            SoftKeyboardUtils.hideSoftKeyboard(LoginActivity.this, edit_account);
            login(account, password, loginType);
        } else {
            password = edit_password.getText().toString().trim();
            account = edit_account.getText().toString().trim();
            SoftKeyboardUtils.hideSoftKeyboard(LoginActivity.this, edit_account);
            if (6 > password.length()) {
                ToastUtil.showLoginToastCenter(this, "请输入不少于6位数的密码");
                return;
            }
            userModel.getUserType(8, account, true, this);
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
        String account = intent.getStringExtra(ACCOUNT);
        if (!TextUtils.isEmpty(account)) {
            setAccount(account);
        }
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
        if (null != gt3GeetestUtils) {
            gt3GeetestUtils.destory();
        }
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
        // 配置bean文件，也可在oncreate初始化
        gt3ConfigBean = new GT3ConfigBean();
        // 设置验证模式，1：bind，2：unbind
        gt3ConfigBean.setPattern(1);
        // 设置点击灰色区域是否消失，默认不消失
        gt3ConfigBean.setCanceledOnTouchOutside(true);
        // 设置语言，如果为null则使用系统默认语言
        gt3ConfigBean.setLang(null);
        // 设置加载webview超时时间，单位毫秒，默认10000，仅且webview加载静态文件超时，不包括之前的http请求
        gt3ConfigBean.setTimeout(10000);
        // 设置webview请求超时(用户点选或滑动完成，前端请求后端接口)，单位毫秒，默认10000
        gt3ConfigBean.setWebviewTimeout(10000);
        gt3ConfigBean.setListener(new GT3Listener() {
            /**
             * 验证码加载完成
             * @param s 加载时间和版本等信息，为json格式
             */
            @Override
            public void onStatistics(String s) {
                Log.e("TAG", "GT3BaseListener-->onDialogReady-->" + s);
            }

            @Override
            public void onClosed(int i) {
                Log.e("TAG", "GT3BaseListener-->onDialogReady-->" + i);
            }

            /**
             * 验证结果
             * @param s
             */
            @Override
            public void onDialogResult(String s) {
                super.onDialogResult(s);
                JSONObject res_json = null;
                try {
                    res_json = new JSONObject(s);
                    userLoginModel.postGeetVerify(17, res_json.getString("geetest_challenge"), res_json.getString("geetest_validate")
                            , res_json.getString("geetest_seccode"), LoginActivity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            /**
             *  验证成功回调
             * @param s
             */
            @Override
            public void onSuccess(String s) {
                Log.e("TAG", "GT3BaseListener-->onSuccess-->" + s);
            }

            @Override
            public void onFailed(GT3ErrorBean gt3ErrorBean) {
                Log.e("TAG", "GT3BaseListener-->onFailed-->" + gt3ErrorBean.toString());
            }

            /**
             *  api1回调
             */
            @Override
            public void onButtonClick() {
                userLoginModel.getGeetStart(16, LoginActivity.this);
            }
        });
        gt3GeetestUtils.init(gt3ConfigBean);
        // 开启验证
        gt3GeetestUtils.startCustomFlow();
    }

    private void login(String accout, String pwdMD5, String loginType) {
        SoftKeyboardUtils.hideSoftKeyboard(LoginActivity.this, edit_account);
        exitClearAllData(LoginActivity.this,true);
        if (null == auth2ServiceUpdate) {
            auth2ServiceUpdate = new OAuth2ServiceUpdate(LoginActivity.this, loginType);
        } else {
            auth2ServiceUpdate.setLoginType(loginType);
        }
        auth2ServiceUpdate.getOAuth2Service(accout, pwdMD5, access_token ->
                getCorpId()
        );
    }

    /**
     * 通过租户获取用户信息
     *
     * @param corpId
     */
    private void getNetInfo(String corpId) {
        String colorToken = SharedPreferencesUtils.getKey(this, SpConstants.accessToken.accssToken);
        userModel.getUserInfoByCorp(14, corpId, colorToken, true, this);
    }

    /**
     * 获取租户信息接口
     */
    private void getCorpId() {
        microModel.getCropList(13, true, this);
    }

    /**
     * 查询微信是否绑定
     *
     * @param openid
     * @param unionid
     */
    private void isBindWx(String openid, String unionid) {
        loginType = "6";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                userModel.getIsBindWechat(15, openid, unionid, true, LoginActivity.this);
            }
        });
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
            case 3:
                if (!TextUtils.isEmpty(result)) {
                    SingleDeviceLogin singleDeviceLogin = GsonUtils.gsonToBean(result, SingleDeviceLogin.class);
                    String device_token = singleDeviceLogin.getContent().getDevice_token();
                    spUtils.saveStringData(SpConstants.storage.DEVICE_TOKEN, device_token);
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
                        if ("1".equals(user_type)) {//1：oa账号，2：手机号，3：未注册
                            loginType = "1";
                            loginGt();
                        } else if ("2".equals(user_type)) {
                            loginType = "3";
                            loginGt();
                        } else {
                            if (NumberUtils.IsPhoneNumber(account)) {
                                Intent intent = new Intent(this, RegisterActivity.class);
                                intent.putExtra("phone", account);
                                startActivity(intent);
                            } else {
                                ToastUtil.showShortToast(this, "该账号" + account + "尚未注册");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 11:
                if (!TextUtils.isEmpty(result)) {
                    czyAccessToken = result;
                    login(account, getPawdMD5(), loginType);
                }
                break;
            case 13:
                if (!TextUtils.isEmpty(result)) {
                    CropListEntity cropListEntity = new CropListEntity();
                    cropListEntity = GsonUtils.gsonToBean(result, CropListEntity.class);
                    cropList = cropListEntity.getContent();
                    for (CropListEntity.ContentBean contentBean : cropList) {
                        if (contentBean.getIs_default().equals("1")) {
                            spUtils.saveStringData(SpConstants.storage.CORPID, contentBean.getUuid());
                            spUtils.saveStringData(SpConstants.storage.CORPNAME, contentBean.getName());
                            Tools.saveStringValue(LoginActivity.this, Contants.storage.CORPID, contentBean.getUuid());//租户ID
                            getNetInfo(contentBean.getUuid());
                            return;
                        }
                    }
                }
                break;
            case 14:
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
                            String employeeAccount = data.getString("username");
                            String employeeName = data.getString("name");
                            String password = data.getString("password");
                            if (TextUtils.isEmpty(password) && !"4".equals(loginType)) {
                                spUtils.saveBooleanData(SpConstants.UserModel.NoHAVEPWD, true);
                            } else {
                                spUtils.saveBooleanData(SpConstants.UserModel.NoHAVEPWD, false);
                            }
                            UserInfo.employeeAccount = employeeAccount;
                            SharedPreferencesUtils.saveUserKey(this, USEROA, employeeAccount);
                            if (!"4".equals(loginType)) {
                                SharedPreferencesUtils.saveUserKey(this, USERACCOUNT, account);
                            }
                            SharedPreferencesUtils.saveUserKey(this, USERNAME, employeeName);
                            spUtils.saveStringData(SpConstants.storage.ORG_UUID, data.getString("org_uuid"));
                            spUtils.saveBooleanData(SpConstants.UserModel.ISLOGIN, true);
                            spUtils.saveStringData(SpConstants.UserModel.ACCOUNT_UUID, data.getString("account_uuid"));
                            singleDevicelogin();
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
            case 15:
                if (!TextUtils.isEmpty(result)) {
                    String response = HttpTools.getContentString(result);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String is_bind = jsonObject.getString("is_bind");//1：已绑定，2：未绑定
                        if ("1".equals(is_bind)) {
                            login(unionid, MD5.getMd5Value(openid).toLowerCase(), loginType);
                        } else {
                            Intent it = new Intent(this, BindWeChatActivity.class);
                            it.putExtra("unionid", unionid);
                            it.putExtra("openid", openid);
                            startActivityForResult(it, 3000);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 16:
                JSONObject jsonObject = null;
                if (!TextUtils.isEmpty(result)) {
                    try {
                        jsonObject = new JSONObject(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                gt3ConfigBean.setApi1Json(jsonObject);
                gt3GeetestUtils.getGeetest();
                break;
            case 17:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JiYanTwoCheckEntity entity = new JiYanTwoCheckEntity();
                        entity = GsonUtils.gsonToBean(result, JiYanTwoCheckEntity.class);
                        if (entity.getContent().getStatus() == 1) {//验证通过
                            gt3GeetestUtils.showSuccessDialog();
                            login(account, getPawdMD5(), loginType);
                        } else {
                            gt3GeetestUtils.showFailedDialog();
                            ToastUtil.showShortToast(LoginActivity.this, "极验验证失败,请稍后重试");
                        }
                    } catch (Exception e) {
                        gt3GeetestUtils.showSuccessDialog();
                        login(account, getPawdMD5(), loginType);
                    }
                } else {
                    gt3GeetestUtils.showFailedDialog();
                    ToastUtil.showShortToast(LoginActivity.this, "极验获取数据异常,请稍后重试");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3000) {
            if (resultCode == 3001) {
                String account = data.getStringExtra(ACCOUNT);
                String pwd = data.getStringExtra(PASSWORD);
                loginType = "6";
                ToastUtil.showShortToast(this, "绑定成功，正在登录");
                login(account, pwd, loginType);
            }
        } else if (requestCode == 4000) {
            if (resultCode == 4001) {
                String account = data.getStringExtra(ACCOUNT);
                String registerPassword = data.getStringExtra(PASSWORD);
                if (!TextUtils.isEmpty(account)) {
                    setAccount(account);
                }
                if (!TextUtils.isEmpty(registerPassword)) {
                    setPassword(registerPassword);
                }
                setLogin();
            }
        }
    }

    private void singleDevicelogin() {
        userModel.postSingleDevice(3, "1", true, this);
    }

}
