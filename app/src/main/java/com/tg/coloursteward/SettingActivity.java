package com.tg.coloursteward;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
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
import com.tg.coloursteward.object.ViewConfig;
import com.tg.coloursteward.updateapk.ApkInfo;
import com.tg.coloursteward.updateapk.UpdateManager;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.MessageArrowView;
import com.tg.coloursteward.view.MessageArrowView.ItemClickListener;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.HuxinSdkManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.tg.coloursteward.application.CityPropertyApplication.lbsTraceClient;

/**
 * 更多设置
 *
 * @author Administrator
 */
public class SettingActivity extends BaseActivity implements ItemClickListener {
    private MessageArrowView mineInfoZone;
    private LinearLayout llExit;
    private RelativeLayout rlUpApk;
    private TextView tvVersion;
    private final int REQUESTPERMISSION = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mineInfoZone = (MessageArrowView) findViewById(R.id.mine_info_zone);
        llExit = (LinearLayout) findViewById(R.id.exit);
        rlUpApk = (RelativeLayout) findViewById(R.id.rl_upApk);
        tvVersion = (TextView) findViewById(R.id.tv_version);
        /**
         * 获取版本号
         */
        String versionShort = UpdateManager.getVersionName(SettingActivity.this);
        tvVersion.setText("V " + versionShort);
        rlUpApk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                /*
                 * 版本检测更新
				 */
                getVersion();
            }
        });
        llExit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {// 退出登录
                DialogFactory.getInstance().showDialog(SettingActivity.this, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
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

            }
        });
        mineInfoZone.setItemClickListener(this);
        ArrayList<ViewConfig> list = new ArrayList<ViewConfig>();
        ViewConfig viewConfig = new ViewConfig("关于彩管家", "", true);
        list.add(viewConfig);
        viewConfig = new ViewConfig("修改密码", "", true);
        list.add(viewConfig);
        viewConfig = new ViewConfig("清空首页消息列表", "", true);
        list.add(viewConfig);
        /*viewConfig = new ViewConfig("通话设置", "", true);
        list.add(viewConfig);*/
        mineInfoZone.setData(list);
    }

    private void StopYingYan() {
        if (null != lbsTraceClient) {
            lbsTraceClient.stopGather(null);
        }
    }

    // 检测版本更新
    private void getVersion() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_VERSION_INFO, "检查更新");
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
                        UpdateManager manager = new UpdateManager(SettingActivity.this, false);
                        if (ContextCompat.checkSelfPermission(SettingActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            //申请权限
                            ActivityCompat.requestPermissions(SettingActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTPERMISSION);
                            ToastFactory.showToast(SettingActivity.this, "请允许权限进行下载安装");
                        } else {
                            manager.checkUpdate(apkinfo, true);
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
    public void onItemClick(MessageArrowView mv, View v, int position) {
        // TODO Auto-generated method stub
        if (mv == mineInfoZone) {
            if (position == 0) {// 关于app
                startActivity(new Intent(this, AboutUsActivity.class));
//				showShare();
            } else if (position == 1) {// 修改密码
                startActivity(new Intent(this, ModifiedPasswordActivity.class));
            } else if (position == 2) {// 清空首页消息列表
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
            } else if (position == 3) {
                //HuxinSdkManager.instance().setCallSetting();
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
        // TODO Auto-generated method stub
        return getLayoutInflater().inflate(R.layout.activity_setting, null);
    }

    @Override
    public String getHeadTitle() {
        // TODO Auto-generated method stub
        return "更多设置";
    }

}
