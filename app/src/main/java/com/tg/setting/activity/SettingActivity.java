package com.tg.setting.activity;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.tg.coloursteward.BuildConfig;
import com.tg.coloursteward.R;
import com.tg.coloursteward.application.CityPropertyApplication;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.database.SharedPreferencesTools;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.serice.UpdateService;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.setting.adapter.UpdateAdapter;
import com.tg.setting.entity.VersionEntity;
import com.tg.setting.model.SettingModel;
import com.tg.setting.view.DeleteMsgDialog;
import com.tg.setting.view.UpdateVerSionDialog;
import com.tg.user.model.UserModel;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.utils.AppUtils;

import org.greenrobot.eventbus.EventBus;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import static com.tg.coloursteward.application.CityPropertyApplication.lbsTraceClient;

/**
 * 更多设置
 *
 * @author Administrator
 */
public class SettingActivity extends BaseActivity implements OnClickListener, HttpResponse {
    private TextView tv_setting_nowver;
    private TextView tv_setting_newver;
    private RelativeLayout rl_setting_changepwd;
    private RelativeLayout rl_setting_clearinfo;
    private RelativeLayout rl_setting_aboutus;
    private RelativeLayout rl_setting_update;
    private RelativeLayout rl_setting_imstatus;
    private TextView tv_setting_quit;
    private TextView tv_setting_imstatus;
    private View tv_setting_point;
    private SettingModel settingModel;
    private UserModel userModel;
    private List<String> updateList = new ArrayList<>();
    private String downUrl;
    private int result_up;
    private String getVersion;
    private UpdateVerSionDialog updateDialog;
    private UpdateAdapter updateAdapter;
    private boolean isCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingModel = new SettingModel(this);
        userModel = new UserModel(this);
        initView();
        getVersion(false);
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
        String versionShort = BuildConfig.VERSION_NAME;
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
    private void getVersion(Boolean isloading) {
        settingModel.getUpdate(0, "1", isloading, this);
    }

    /**
     * 单设备退出
     */
    private void singleDevicelogout() {
        String device_code = spUtils.getStringData(SpConstants.storage.DEVICE_TOKEN,"");
        userModel.postSingleExit(1, device_code, this);
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
                        settingModel.deleteAllNotice(1, UserInfo.employeeAccount, 1, SettingActivity.this);
                    }
                }, null, "是否确定清空首页消息列表？", null, null);
                break;
            case R.id.rl_setting_aboutus:
                startActivity(new Intent(this, AboutUsActivity.class));
                break;
            case R.id.rl_setting_update:
                isCheck = true;
                getVersion(true);
                break;
            case R.id.tv_setting_quit:
                DialogFactory.getInstance().showDialog(SettingActivity.this, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserInfo.initClear();
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

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    VersionEntity versionEntity = new VersionEntity();
                    try {
                        versionEntity = GsonUtils.gsonToBean(result, VersionEntity.class);
                        result_up = versionEntity.getContent().getResult();
                        downUrl = versionEntity.getContent().getInfo().getUrl();
                        getVersion = versionEntity.getContent().getInfo().getVersion();
                        updateList.clear();
                        updateList.add(versionEntity.getContent().getInfo().getFunc());
                        if (result_up == 2) {//1：最新版本，2：介于最新和最低版本之间，3：低于支持的最低版本
                            tv_setting_newver.setText("最新版本 " + versionEntity.getContent().getInfo().getVersion());
                            tv_setting_point.setVisibility(View.VISIBLE);
                            int type = versionEntity.getContent().getType();
                            if ((type == 2 || type == 1) && isCheck) {//1：大版本更新，2：小版本更新
                                showUpdateDialog(result_up, versionEntity.getContent().getInfo().getVersion(), downUrl);
                            }
                        } else if (result_up == 3) {
                            tv_setting_newver.setText("最新版本 " + versionEntity.getContent().getInfo().getVersion());
                            tv_setting_point.setVisibility(View.VISIBLE);
                            if (isCheck) {
                                showUpdateDialog(result_up, versionEntity.getContent().getInfo().getVersion(), downUrl);
                            }
                        } else if (result_up == 1 && isCheck) {
                            ToastUtil.showShortToast(SettingActivity.this, "已经是最新版本");
                        }
                    } catch (Exception e) {
                    }
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    String message = HttpTools.getMessageString(result);
                    ToastUtil.showShortToast(this, message);
                    Message msghome = new Message();
                    msghome.what = Contants.LOGO.CLEAR_HOMELIST;
                    EventBus.getDefault().post(msghome);
                }
                break;
        }
    }

    /**
     * 版本更新弹窗
     */
    public void showUpdateDialog(int code, String version, String downurl) {
        updateDialog = new UpdateVerSionDialog(SettingActivity.this);
        switch (code) {
            case 2://可选更新
                updateDialog.ok.setText("更新至V" + version + "版本");
                updateDialog.cancel.setVisibility(View.VISIBLE);
                updateAdapter = new UpdateAdapter(this, updateList);
                updateDialog.listView.setAdapter(updateAdapter);
                updateDialog.show();
                break;
            case 3://强制更新
                updateDialog.cancel.setVisibility(View.GONE);
                updateDialog.ok.setText("更新至V" + version + "版本");
                updateAdapter = new UpdateAdapter(this, updateList);
                updateDialog.listView.setAdapter(updateAdapter);
                updateDialog.mDialog.setCancelable(false);
                updateDialog.show();
                break;
        }
        updateDialog.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (code != 1) {
                    XXPermissions.with(SettingActivity.this)
                            .constantRequest()
                            .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .request(new OnPermission() {
                                @Override
                                public void hasPermission(List<String> granted, boolean isAll) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        if (getPackageManager().canRequestPackageInstalls()) {
                                            startDown();
                                        } else {
                                            DeleteMsgDialog dialog = new DeleteMsgDialog(SettingActivity.this, R.style.custom_dialog_theme);
                                            dialog.show();
                                            dialog.setContentText("当前手机系统安装应用需要打开未知来源权限，请去设置中开启权限");
                                            dialog.setrightText("去打开");
                                            dialog.btn_define.setOnClickListener(v1 -> {
                                                dialog.dismiss();
                                                startInstallPermissionSettingActivity();
                                            });
                                            dialog.btn_cancel.setOnClickListener(v1 -> {
                                                dialog.dismiss();
                                            });
                                        }
                                    } else {
                                        startDown();
                                    }

                                }

                                @Override
                                public void noPermission(List<String> denied, boolean quick) {
                                    ToastUtil.showShortToast(SettingActivity.this,
                                            "存储权限被禁止，请去开启该权限");
                                }
                            });
                }

            }
        });
        updateDialog.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDialog.dismiss();
            }
        });
    }

    private void startInstallPermissionSettingActivity() {
        Uri packageURI = Uri.parse("package:" + getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        startActivityForResult(intent, 10001);
    }

    private void startDown() {
        if (null != updateDialog) {
            updateDialog.dismiss();
        }
        Intent intent = new Intent(SettingActivity.this, UpdateService.class);
        intent.putExtra(UpdateService.DOWNLOAD_URL, downUrl);
        intent.putExtra(UpdateService.VERSIONNAME, getVersion);
        SettingActivity.this.startService(intent);
        ToastUtil.showShortToast(SettingActivity.this, "彩管家已开始下载更新,详细信息可在通知栏查看哟!");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001) {
            if (getPackageManager().canRequestPackageInstalls()) {
                startDown();
            }
        }
    }
}
