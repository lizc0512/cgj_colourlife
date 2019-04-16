package com.tg.coloursteward;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.application.CityPropertyApplication;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.database.SharedPreferencesTools;
import com.tg.coloursteward.entity.SingleDeviceLogout;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.updateapk.ApkInfo;
import com.tg.coloursteward.updateapk.UpdateManager;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.utils.AppUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;

import static com.tg.coloursteward.application.CityPropertyApplication.lbsTraceClient;

/**
 * 更多设置
 *
 * @author Administrator
 */
public class SettingActivity extends BaseActivity implements OnClickListener {
    private TextView tv_setting_nowver;
    private TextView tv_setting_newver;
    private final int REQUESTPERMISSION = 110;
    private RelativeLayout rl_setting_changepwd;
    private RelativeLayout rl_setting_clearinfo;
    private RelativeLayout rl_setting_aboutus;
    private RelativeLayout rl_setting_update;
    private RelativeLayout rl_setting_imstatus;
    private TextView tv_setting_quit;
    private TextView tv_setting_imstatus;
    private View tv_setting_point;
    private int apkCode;
    private Boolean isCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        isCheck = false;
        getVersion();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        tv_setting_imstatus = findViewById(R.id.tv_setting_imstatus);
        rl_setting_changepwd = findViewById(R.id.rl_setting_changepwd);
        rl_setting_clearinfo = findViewById(R.id.rl_setting_clearinfo);
        rl_setting_aboutus = findViewById(R.id.rl_setting_aboutus);
        rl_setting_update = findViewById(R.id.rl_setting_update);
        rl_setting_imstatus = findViewById(R.id.rl_setting_imstatus);
        tv_setting_quit = findViewById(R.id.tv_setting_quit);
        tv_setting_nowver = findViewById(R.id.tv_setting_nowver);
        tv_setting_newver = findViewById(R.id.tv_setting_newver);
        tv_setting_point = findViewById(R.id.tv_setting_point);
        rl_setting_changepwd.setOnClickListener(this);
        rl_setting_clearinfo.setOnClickListener(this);
        rl_setting_aboutus.setOnClickListener(this);
        rl_setting_update.setOnClickListener(this);
        tv_setting_quit.setOnClickListener(this);
        String versionShort = UpdateManager.getVersionName(SettingActivity.this);
        tv_setting_nowver.setText(versionShort);
        if (!HuxinSdkManager.instance().isConnect()) {
            tv_setting_imstatus.setText("IM通信状态：" + "离线,请点击这里重新登录IM");
        } else {
            tv_setting_imstatus.setText("IM通信状态：" + "在线");
        }
        rl_setting_imstatus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!HuxinSdkManager.instance().isConnect()) {
                    String uuid = HuxinSdkManager.instance().getUuid();
                    if (!TextUtils.isEmpty(uuid)) {
                        final ProgressDialog progressDialog = new ProgressDialog(SettingActivity.this);
                        progressDialog.setMessage("正在重新登录，请稍后...");
                        progressDialog.show();
                        String ip = AppUtils.getStringSharedPreferences(SettingActivity.this, "IP", AppConfig.getSocketHost());
                        int port = AppUtils.getIntSharedPreferences(SettingActivity.this, "PORT", AppConfig.getSocketPort());
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
                                        tv_setting_imstatus.setText("IM通信状态：" + "在线");
                                    }
                                });
                    }
                } else {

                }
            }
        });

    }

    private void StopYingYan() {
        if (null != lbsTraceClient) {
            lbsTraceClient.stopGather(null);
        }
    }

    // 检测版本更新
    private void getVersion() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_VERSION_INFO, "");
        RequestParams params = new RequestParams();
        String version = UpdateManager.getVersionName(SettingActivity.this);
        params.put("version", version);
        params.put("type", "android");
        HttpTools.httpGet(Contants.URl.URL_CPMOBILE, "/1.0/version", config, params);
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

    @Override
    public void onRequestStart(Message msg, String hintString) {
        super.onRequestStart(msg, hintString);
        if (msg.arg1 == HttpTools.GET_VERSION_INFO) {
            if (isCheck == true) {
                DialogFactory.getInstance().showTransitionDialog(this,
                        "正在检查更新", msg.obj, msg.arg1);
            }
        }
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.DETELE_HOME_LIST) {
            if (code == 0) {
                ToastFactory.showToast(this, message);
                Message msghome = new Message();
                msghome.what = Contants.LOGO.CLEAR_HOMELIST;
                EventBus.getDefault().post(msghome);
            }
        } else if (msg.arg1 == HttpTools.GET_VERSION_INFO) {
            JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
            String content = HttpTools.getContentString(jsonString);
            if (code == 0) {
                try {
                    apkCode = jsonObject.getInt("result");
                    ResponseData data = HttpTools.getResponseKey(content, "info");
                    JSONArray func = data.getJSONArray(0, "func");
                    String apkVersion = data.getString(0, "version");
                    String apkSize = data.getString(0, "size");
                    String downloadUrl = data.getString(0, "download_url");
                    String apkLog = "";
                    if (apkCode == 0 || apkCode == -1 || apkCode == -2) {
                        tv_setting_newver.setText("最新版本 " + apkVersion);
                        tv_setting_point.setVisibility(View.VISIBLE);
                    }
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
                        UpdateManager manager = new UpdateManager(SettingActivity.this, false);
                        if (ContextCompat.checkSelfPermission(SettingActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            //申请权限
                            ActivityCompat.requestPermissions(SettingActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTPERMISSION);
                            ToastFactory.showToast(SettingActivity.this, "请允许权限进行下载安装");
                        } else {
                            if (isCheck == true) {
                                manager.checkUpdate(apkinfo, true);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

            }
        } else if (msg.arg1 == HttpTools.POST_LOGOUTDEVICE) {
            if (code == 0) {
                try {
                    SingleDeviceLogout singleDeviceLogout = GsonUtils.gsonToBean(jsonString, SingleDeviceLogout.class);
                    String jsonObject = singleDeviceLogout.getContent().getResult();
                    if ("1".equals(jsonObject)) {
                        android.util.Log.d("lizc", "单设备退出OK");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUESTPERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                break;
        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_setting, null);
    }

    @Override
    public String getHeadTitle() {
        return "更多设置";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_setting_changepwd:
                startActivity(new Intent(this, ModifiedPasswordActivity.class));
                break;
            case R.id.rl_setting_clearinfo:
                DialogFactory.getInstance().showDialog(SettingActivity.this, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RequestConfig config = new RequestConfig(SettingActivity.this, HttpTools.DETELE_HOME_LIST, "清空首页消息列表");
                        RequestParams params = new RequestParams();
                        params.put("username", UserInfo.employeeAccount);
                        params.put("source", 1);
                        HttpTools.httpDelete(Contants.URl.URL_ICETEST, "/push2/homepush/deleteall", config, params);
                    }
                }, null, "是否确定清空首页消息列表？", null, null);
                break;
            case R.id.rl_setting_aboutus:
                startActivity(new Intent(this, AboutUsActivity.class));
                break;
            case R.id.rl_setting_update:
                isCheck = true;
                getVersion();
                break;
            case R.id.tv_setting_quit:
                DialogFactory.getInstance().showDialog(SettingActivity.this, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initClear();
                        StopYingYan();
                        singleDevicelogout();
                        SharedPreferencesTools.clearUserId(SettingActivity.this);
                        //清空缓存
                        SharedPreferencesTools.clearCache(SettingActivity.this);
                        SharedPreferencesTools.clearAllData(SettingActivity.this);
                        CityPropertyApplication.gotoLoginActivity(SettingActivity.this);
                        HuxinSdkManager.instance().loginOut();
                    }
                }, null, "确定要退出账号吗", null, null);
                break;


        }
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
