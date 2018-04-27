package com.tg.coloursteward;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tg.coloursteward.application.CityPropertyApplication;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.database.SharedPreferencesTools;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.log.Logger;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.object.ImageParams;
import com.tg.coloursteward.ui.MainActivity1;
import com.tg.coloursteward.util.NetWorkUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;
//import com.youmai.hxsdk.HuxinSdkManager;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * 登录页面
 *
 * @author Administrator
 */
public class LoginActivity extends BaseActivity implements AnimationListener {
    private EditText editUser;
    private EditText editPassword;
    private ImageView ivClose;
    private View startLayout;
    private View contentLayout;
    private Animation outAnim;
    private Animation inAnim;
    private String newPhone = "";
    private boolean isAllowHuxin = false;
    private String corpId;
    private String password;
    private String extras;

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
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.submit:
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_PHONE_STATE}, Activity.RESULT_FIRST_USER);
                } else {
                    login();// 登录
                }
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

        extras = getintent.getStringExtra(MainActivity.KEY_EXTRAS);
        contentLayout = findViewById(R.id.login_content);
        editUser = (EditText) findViewById(R.id.edit_user);
        startLayout = findViewById(R.id.start_layout);
        editPassword = (EditText) findViewById(R.id.edit_password);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        ivClose.setOnClickListener(singleListener);
        findViewById(R.id.submit).setOnClickListener(singleListener);
        findViewById(R.id.forget_pwd).setOnClickListener(singleListener);
        RequestParams params = new RequestParams();
        RequestConfig config = new RequestConfig(this, HttpTools.GET_TS,"");
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/timestamp", config, params);
       // editUser.setText(Tools.getUserName(this));
        showStartPager();
        CheckPermission();
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.READ_CONTACTS}, Activity.DEFAULT_KEYS_SEARCH_LOCAL);
        }
    }

    private void showStartPager() {
        startLayout.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);
        mHand.postDelayed(new Runnable() {
            @Override
            public void run() {
                ResponseData userInfoData = SharedPreferencesTools.getUserInfo(LoginActivity.this);
                String skin_code = Tools.getStringValue(LoginActivity.this, Contants.storage.SKINCODE);
                String corp_id = Tools.getStringValue(LoginActivity.this, Contants.storage.CORPID);
                if (userInfoData.length > 0 && StringUtils.isNotEmpty(skin_code) && StringUtils.isNotEmpty(corp_id)) {
                    Tools.loadUserInfo(userInfoData, null);
                    //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    Intent intent = new Intent(LoginActivity.this, MainActivity1.class);
                    intent.putExtra(MainActivity.KEY_SKIN_CODE, Integer.valueOf(skin_code));
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
        startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
    }

    /**
     * 登录
     */
    public void login() {
        newPhone = editUser.getText().toString();
        if (newPhone.length() <= 0) {
            ToastFactory.showToast(this, "请输入账号");
            return;
        }
        password  = editPassword.getText().toString();
        if (password.length() < 6) {
            ToastFactory.showToast(this, "请输入不少于6位的密码");
            return;
        }
        try {
            String passwordMD5 = MD5.getMd5Value(password).toLowerCase();
            Log.d("TAG", "passwordMD5=" + passwordMD5);
            Tools.savePassWordMD5(getApplicationContext(), passwordMD5);//保存密码(加密后)
            Tools.savePassWord(getApplicationContext(), password);//保存密码
            Tools.hideKeyboard(editUser);
            RequestParams params = new RequestParams();
            params.put("username", newPhone);
            params.put("password", passwordMD5);
            RequestConfig config = new RequestConfig(this, HttpTools.GET_LOGIN);
            config.hintString = "登录";
            HttpTools.httpPost(Contants.URl.URL_ICETEST, "/orgms/loginAccount", config, params);
        } catch (Exception e) {
            // TODO Auto-generated catch block
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

    /**
     * 获取个人信息
     */
    private void getUserInfo(String accountUuid) {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_USER_INFO,null);
        config.hintString = "加载个人信息";
        RequestParams params = new RequestParams();
        params.put("uid", accountUuid);
        HttpTools.httpGet(Contants.URl.URL_ICETEST,"/account", config, params);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        String jsonObject = HttpTools.getContentString(jsonString);
        ResponseData data = HttpTools.getResponseContentObject(jsonObject);
        if (msg.arg1 == HttpTools.POST_IMAG) {
            ToastFactory.showToast(this, hintString);
            Logger.logd("POST_IMAG" + jsonString);
            ImageParams params = msg.getData().getParcelable(HttpTools.KEY_IMAGE_PARAMS);
            Logger.logd("path = " + params.path);
            Logger.logd("fileName =" + params.fileName);
            Logger.logd("position =" + params.position);
        } else if (msg.arg1 == HttpTools.GET_LOGIN) {
            if(code == 0){
                Date dt = new Date();
                Long time = dt.getTime();
                String date = Tools.getDateToString(time);
                Tools.saveDateInfo(this, date);
                JSONObject content = HttpTools.getContentJSONObject(jsonString);
                try {
                    corpId = content.getString("corpId");
                    int status = content.getInt("status");
                    if(status > 0){
                        ToastFactory.showToast(LoginActivity.this,"账号异常，请及时联系管理员");
                    }else{
                        Tools.saveStringValue(LoginActivity.this, Contants.storage.CORPID,corpId);
                        String mobile = content.getString("mobile");
                        String accountUuid = content.getString("accountUuid");
                        if (isAllowHuxin) {
                            if (StringUtils.isNotEmpty(mobile)) {
                                //HuxinSdkManager.instance().setPhoneNumber(mobile, null);
                                //HuxinSdkManager.instance().setCgjUserId(newPhone);
                            }
                        }
                        getUserInfo(accountUuid);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                ToastFactory.showToast(LoginActivity.this, message);
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
                        Tools.saveStringValue(LoginActivity.this, Contants.storage.SKINCODE, String.valueOf(skin_code));//保存皮肤包
                        //Intent intent = new Intent(this, MainActivity.class);
                        Intent intent = new Intent(this, MainActivity1.class);
                        intent.putExtra(MainActivity.KEY_NEDD_FRESH, false);
                        intent.putExtra(MainActivity.KEY_SKIN_CODE, skin_code);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                ToastFactory.showToast(LoginActivity.this, message);
                SharedPreferencesTools.clearUserId(LoginActivity.this);
            }
        }else if(msg.arg1 == HttpTools.GET_USER_INFO){
            if(code == 0){
                Tools.loadUserInfo(data,jsonString);
                getSkin(corpId);
            }else{
                ToastFactory.showToast(LoginActivity.this,"加载个人信息失败，请重新登录");
            }

        }
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
        // TODO Auto-generated method stub
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
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Activity.RESULT_FIRST_USER: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    isAllowHuxin = true;
                } else {
                    ToastFactory.showToast(LoginActivity.this, "拒绝该权限则使用不了呼信功能");
                }
                login();
                break;
            }
        }
    }
}

