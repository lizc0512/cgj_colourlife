package com.tg.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.entity.CropListEntity;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.model.MicroModel;
import com.tg.coloursteward.module.MainActivity;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.ClearEditText;
import com.tg.user.entity.OauthUserEntity;
import com.tg.user.entity.SendCodeEntity;
import com.tg.user.entity.SingleDeviceLogin;
import com.tg.user.model.UserCzyModel;
import com.tg.user.model.UserModel;
import com.tg.user.oauth.OAuth2ServiceUpdate;

import java.util.ArrayList;
import java.util.List;

import static com.tg.user.activity.LoginActivity.USERACCOUNT;
import static com.tg.user.activity.LoginActivity.USERNAME;
import static com.tg.user.activity.LoginActivity.USEROA;

/**
 * @name lizc
 * @class name：com.tg.user.activity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2020/5/21 15:47
 * @change
 * @chang time
 * @class describe
 */
public class BindWeChatActivity extends BaseActivity implements View.OnClickListener {


    private TextView tv_get_code;
    private TextView tv_bindwx_done;
    private ClearEditText et_bindwx_phone;
    private ClearEditText et_bindwx_code;
    private UserCzyModel userCzyModel;
    private String openid;
    private String unionid;
    private MyTimeCount myTimeCount = null;
    private int countStart;
    private String phone;
    private String code;
    private OAuth2ServiceUpdate auth2ServiceUpdate;
    private List<CropListEntity.ContentBean> cropList = new ArrayList<>();
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userCzyModel = new UserCzyModel(this);
        userModel = new UserModel(this);
        initView();
        initData();
    }

    private void initView() {
        et_bindwx_phone = findViewById(R.id.et_bindwx_phone);
        et_bindwx_code = findViewById(R.id.et_bindwx_code);
        tv_get_code = findViewById(R.id.tv_get_code);
        tv_bindwx_done = findViewById(R.id.tv_bindwx_done);
        tv_get_code.setOnClickListener(this);
        tv_bindwx_done.setOnClickListener(this);
        if (null != getIntent()) {
            openid = getIntent().getStringExtra("openid");
            unionid = getIntent().getStringExtra("unionid");
        }
    }

    private void initData() {

    }

    @Override
    public View getContentView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_bind_wechat, null);
    }

    @Override
    public String getHeadTitle() {
        return "绑定手机号码";
    }

    @Override
    public void onClick(View v) {
        phone = et_bindwx_phone.getText().toString().trim();
        code = et_bindwx_code.getText().toString().trim();
        switch (v.getId()) {
            case R.id.tv_get_code:
                if (!TextUtils.isEmpty(phone)) {
                    userCzyModel.getSmsCode(0, phone, 6, 1, true, this);//找回密码获取短信验证码
                } else {
                    ToastUtil.showShortToast(this, "手机号不能为空");
                }
                break;
            case R.id.tv_bindwx_done:
                if (!TextUtils.isEmpty(phone)) {
                    if (!TextUtils.isEmpty(code)) {
                        userCzyModel.postBindWeChat(1, openid, unionid, phone, code, this);
                    } else {
                        ToastUtil.showShortToast(this, "验证码不能为空");
                    }
                } else {
                    ToastUtil.showShortToast(this, "手机号不能为空");
                }
                break;
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                initTimeCount();
                try {
                    SendCodeEntity sendCodeEntity = GsonUtils.gsonToBean(result, SendCodeEntity.class);
                    ToastUtil.showShortToast(BindWeChatActivity.this, sendCodeEntity.getContent().getNotice());
                } catch (Exception e) {
                    ToastUtil.showShortToast(BindWeChatActivity.this, "验证码已发送");
                }
                break;
            case 1:
                ToastUtil.showShortToast(this, "绑定成功，正在登录");
                login(unionid, MD5.getMd5Value(openid).toLowerCase(), "6");
                break;
            case 2:
                if (!TextUtils.isEmpty(result)) {
                    CropListEntity cropListEntity = new CropListEntity();
                    cropListEntity = GsonUtils.gsonToBean(result, CropListEntity.class);
                    cropList = cropListEntity.getContent();
                    for (CropListEntity.ContentBean contentBean : cropList) {
                        if (contentBean.getIs_default().equals("1")) {
                            spUtils.saveStringData(SpConstants.storage.CORPID, contentBean.getUuid());
                            Tools.saveStringValue(this, Contants.storage.CORPID, contentBean.getUuid());//租户ID
                            getNetInfo(contentBean.getUuid());
                            return;
                        }
                    }
                }
                break;
            case 3:
                if (!TextUtils.isEmpty(result)) {
                    String response = HttpTools.getContentString(result);
                    OauthUserEntity oauthUserEntity = new OauthUserEntity();
                    try {
                        oauthUserEntity = GsonUtils.gsonToBean(result, OauthUserEntity.class);
                        int status = oauthUserEntity.getContent().getStatus();
                        if (status == 0) {
                            ResponseData data = HttpTools.getResponseContentObject(response);
                            Tools.loadUserInfo(data, result);
                            String employeeAccount = data.getString("username");
                            String employeeName = data.getString("name");
                            UserInfo.employeeAccount = employeeAccount;
                            SharedPreferencesUtils.saveUserKey(this, USEROA, employeeAccount);
                            SharedPreferencesUtils.saveUserKey(this, USERACCOUNT, phone);
                            SharedPreferencesUtils.saveUserKey(this, USERNAME, employeeName);
                            spUtils.saveStringData(SpConstants.storage.ORG_UUID, data.getString("org_uuid"));
                            spUtils.saveBooleanData(SpConstants.UserModel.ISLOGIN, true);
                            spUtils.saveStringData(SpConstants.UserModel.ACCOUNT_UUID, data.getString("account_uuid"));
                            singleDevicelogin();
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.putExtra(MainActivity.KEY_NEDD_FRESH, false);
                            intent.putExtra(MainActivity.KEY_SKIN_CODE, "");
                            intent.putExtra(MainActivity.KEY_EXTRAS, "");
                            intent.putExtra(MainActivity.FROM_LOGIN, true);
                            startActivity(intent);
                            ToastUtil.showLoginToastCenter(this, "登录成功");
                            this.finish();
                        } else {
                            ToastUtil.showLoginToastCenter(this, "账号异常，请及时联系管理员");
                            spUtils.clearKey(this);
                        }
                    } catch (Exception e) {
                    }
                }
                break;
            case 4:
                if (!TextUtils.isEmpty(result)) {
                    SingleDeviceLogin singleDeviceLogin = GsonUtils.gsonToBean(result, SingleDeviceLogin.class);
                    String device_token = singleDeviceLogin.getContent().getDevice_token();
                    spUtils.saveStringData(SpConstants.storage.DEVICE_TOKEN, device_token);
                }
                break;
        }
    }


    private void singleDevicelogin() {
        userModel.postSingleDevice(4, "1", true, this);
    }

    private void getNetInfo(String corpId) {
        String colorToken = SharedPreferencesUtils.getKey(this, SpConstants.accessToken.accssToken);
        userModel.getUserInfoByCorp(3, corpId, colorToken, true, this);
    }

    private void login(String accout, String pwdMD5, String loginType) {
        exitClearAllData(true);
        if (null == auth2ServiceUpdate) {
            auth2ServiceUpdate = new OAuth2ServiceUpdate(this, loginType);
        } else {
            auth2ServiceUpdate.setLoginType(loginType);
        }
        auth2ServiceUpdate.getOAuth2Service(accout, pwdMD5, access_token ->
                getCorpId()
        );
    }

    /**
     * 获取租户信息接口
     */
    private void getCorpId() {
        MicroModel microModel = new MicroModel(this);
        microModel.getCropList(2, this);
    }

    /***初始化计数器**/
    private void initTimeCount() {
        cancelTimeCount();
        countStart = 1;
        tv_get_code.setEnabled(false);
        myTimeCount = new MyTimeCount(60000, 1000);
        myTimeCount.start();
    }

    private void cancelTimeCount() {
        if (myTimeCount != null) {
            myTimeCount.cancel();
            myTimeCount = null;
        }
    }

    class MyTimeCount extends CountDownTimer {
        public MyTimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            String account = et_bindwx_phone.getText().toString().trim();
            if (TextUtils.isEmpty(account)) {
                tv_get_code.setTextColor(getResources().getColor(R.color.color_6D7278));
                tv_get_code.setEnabled(false);
            } else {
                tv_get_code.setTextColor(getResources().getColor(R.color.color_3A85E9));
                tv_get_code.setEnabled(true);
            }
            countStart = 0;
            tv_get_code.requestFocus();
            tv_get_code.setText("重新获取");
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            long currentSecond = millisUntilFinished / 1000;
            tv_get_code.setTextColor(getResources().getColor(R.color.color_3A85E9));
            tv_get_code.setText(currentSecond + "S");
        }
    }
}
