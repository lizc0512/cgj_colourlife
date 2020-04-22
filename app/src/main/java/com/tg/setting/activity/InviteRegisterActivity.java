package com.tg.setting.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;

import com.tg.coloursteward.BuildConfig;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.serice.UpdateService;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.setting.adapter.UpdateAdapter;
import com.tg.setting.entity.ShareInfoEntity;
import com.tg.setting.entity.VersionEntity;
import com.tg.setting.model.SettingModel;
import com.tg.setting.view.DeleteMsgDialog;
import com.tg.setting.view.UpdateVerSionDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 邀请同事
 *
 * @author Administrator
 */
public class InviteRegisterActivity extends BaseActivity implements HttpResponse, View.OnClickListener {
    private TextView tv_invite_version;
    private TextView tv_invite_lastver;
    private TextView tv_invite_update;
    private ImageView iv_invite_wechat;
    private ImageView iv_invite_wechatmoment;

    private SettingModel settingModel;
    private String getVersion;
    private String downUrl;
    private List<String> updateList = new ArrayList<>();
    private UpdateVerSionDialog updateDialog;
    private String title = "邀请好友";
    private String content = "邀请好友加入彩管家";
    private String img = "https://pics-czy-cdn.colourlife.com/dev-5d1034ffccd90146486.png";
    private String url = "http://mapp.colourlife.com/mgj.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        iv_invite_wechat = findViewById(R.id.iv_invite_wechat);
        iv_invite_wechatmoment = findViewById(R.id.iv_invite_wechatmoment);
        tv_invite_version = findViewById(R.id.tv_invite_version);
        tv_invite_lastver = findViewById(R.id.tv_invite_lastver);
        tv_invite_update = findViewById(R.id.tv_invite_update);
        tv_invite_version.setText(getString(R.string.setting_invite_version) + BuildConfig.VERSION_NAME);
        iv_invite_wechat.setOnClickListener(this);
        tv_invite_update.setOnClickListener(this);
        iv_invite_wechatmoment.setOnClickListener(this);

    }

    private void initData() {
        settingModel.getUpdate(0, "1", false, this);
        settingModel.getShareInfo(1, this);
    }

    private void initView() {
        settingModel = new SettingModel(this);

    }

    private void showShare(String platform) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setPlatform(platform);
        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle(title);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        oks.setImageUrl(img);
        // url在微信、微博，Facebook等平台中使用
        oks.setUrl(url);
        InviteRegisterActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                oks.setCallback(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        ToastUtil.showShortToast(InviteRegisterActivity.this, "分享成功");
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                        ToastUtil.showShortToast(InviteRegisterActivity.this, "分享失败");
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        ToastUtil.showShortToast(InviteRegisterActivity.this, "取消分享");
                    }
                });
            }
        });
        oks.show(this);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_invite_register, null);
    }

    @Override
    public String getHeadTitle() {
        return "邀请同事";
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    VersionEntity versionEntity = new VersionEntity();
                    try {
                        versionEntity = GsonUtils.gsonToBean(result, VersionEntity.class);
                        int result_up = versionEntity.getContent().getResult();
                        getVersion = versionEntity.getContent().getInfo().getVersion();
                        downUrl = versionEntity.getContent().getInfo().getUrl();
                        updateList.clear();
                        updateList.add(versionEntity.getContent().getInfo().getFunc());
                        if (result_up == 2) {//1：最新版本，2：介于最新和最低版本之间，3：低于支持的最低版本
                            tv_invite_lastver.setText(getString(R.string.setting_invite_lastver) + getVersion);
                            tv_invite_update.setVisibility(View.VISIBLE);

                        } else if (result_up == 1) {
                            tv_invite_lastver.setText(getString(R.string.setting_invite_lastver) + BuildConfig.VERSION_NAME);
                        }
                    } catch (Exception e) {
                    }
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    ShareInfoEntity shareInfoEntity = new ShareInfoEntity();
                    shareInfoEntity = GsonUtils.gsonToBean(result, ShareInfoEntity.class);
                    title = shareInfoEntity.getContent().getTitle();
                    content = shareInfoEntity.getContent().getContent();
                    img = shareInfoEntity.getContent().getImg();
                    url = shareInfoEntity.getContent().getUrl();
                }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_invite_wechatmoment:
                showShare(WechatMoments.NAME);
                break;
            case R.id.iv_invite_wechat:
                showShare(Wechat.NAME);
                break;
            case R.id.tv_invite_update:
                showUpdateDialog();
                break;
        }
    }

    private void showUpdateDialog() {
        updateDialog = new UpdateVerSionDialog(InviteRegisterActivity.this);
        updateDialog.ok.setText("更新至V" + getVersion + "版本");
        updateDialog.cancel.setVisibility(View.VISIBLE);
        UpdateAdapter updateAdapter = new UpdateAdapter(this, updateList);
        updateDialog.listView.setAdapter(updateAdapter);
        updateDialog.show();
        updateDialog.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XXPermissions.with(InviteRegisterActivity.this)
                        .constantRequest()
                        .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .request(new OnPermission() {
                            @Override
                            public void hasPermission(List<String> granted, boolean isAll) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    if (getPackageManager().canRequestPackageInstalls()) {
                                        startDown();
                                    } else {
                                        DeleteMsgDialog dialog = new DeleteMsgDialog(InviteRegisterActivity.this, R.style.custom_dialog_theme);
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
                                ToastUtil.showShortToast(InviteRegisterActivity.this,
                                        "存储权限被禁止，请去开启该权限");
                            }
                        });
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
        Intent intent = new Intent(InviteRegisterActivity.this, UpdateService.class);
        intent.putExtra(UpdateService.DOWNLOAD_URL, downUrl);
        intent.putExtra(UpdateService.VERSIONNAME, getVersion);
        InviteRegisterActivity.this.startService(intent);
        ToastUtil.showShortToast(InviteRegisterActivity.this, "彩管家已开始下载更新,详细信息可在通知栏查看哟!");
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
