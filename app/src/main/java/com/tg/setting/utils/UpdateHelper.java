package com.tg.setting.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.inter.ResultCallBack;
import com.tg.coloursteward.updateapk.UpdateManager;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.setting.adapter.UpdateAdapter;
import com.tg.setting.view.DeleteMsgDialog;
import com.tg.setting.view.UpdateVerSionDialog;

import java.util.List;


/**
 * @name ${lizc}
 * @class name：com.tg.setting.utils
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/6/26 11:22
 * @change
 * @chang time
 * @class 升级帮助类
 */
public class UpdateHelper implements ResultCallBack {
    private static final int RESULT_CODE = 100;
    private Context mContext;
    private UpdateVerSionDialog updateDialog;
    private UpdateAdapter updateAdapter;
    private BaseActivity baseActivity;
    private String downUrl;

    public UpdateHelper(Context context) {
        this.mContext = context;
    }

    public void showUpdateDialog(int code, String version, String mdownUrl, List<String> updateList) {
        downUrl = mdownUrl;
        updateDialog = new UpdateVerSionDialog(mContext);
        updateDialog.ok.setText("更新至V" + version + "版本");
        updateAdapter = new UpdateAdapter(mContext, updateList);
        updateDialog.listView.setAdapter(updateAdapter);
        switch (code) {
            case 2://可选更新
                updateDialog.show();
                break;
            case 3://强制更新
                updateDialog.cancel.setVisibility(View.GONE);
                updateDialog.mDialog.setCancelable(false);
                updateDialog.show();
                break;
        }
        updateDialog.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (code != 1) {
                    updateDialog.dismiss();
                    checkStartDown(downUrl);
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

    public void checkStartDown(String appDownUrl) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mContext.getPackageManager().canRequestPackageInstalls()) {
                startDown(appDownUrl);
            } else {
                DeleteMsgDialog dialog = new DeleteMsgDialog(mContext, R.style.custom_dialog_theme);
                dialog.show();
                dialog.setContentText("当前手机系统安装应用需要打开未知来源权限，请去设置中开启权限");
                dialog.setrightText("去打开");
                dialog.setCanceledOnTouchOutside(false);
                dialog.btn_define.setOnClickListener(v1 -> {
                    dialog.dismiss();
                    startInstallPermissionSettingActivity();
                });
                dialog.btn_cancel.setOnClickListener(v1 -> {
                    dialog.dismiss();
                });
            }
        } else {
            startDown(appDownUrl);
        }
    }

    private void startInstallPermissionSettingActivity() {
        baseActivity = (BaseActivity) mContext;
        if (baseActivity != null) {
            baseActivity.setResultCallBack(this);
        }
        Uri packageURI = Uri.parse("package:" + mContext.getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        baseActivity.startActivityForResult(intent, RESULT_CODE);
    }


    private void startDown(String url) {
        ToastUtil.showShortToast(mContext,
                "开始下载");
        UpdateManager updateManager = new UpdateManager(mContext, true);
        updateManager.showDownloadDialog(url);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_CODE:
                try {
                    if (mContext.getPackageManager().canRequestPackageInstalls()) {
                        startDown(downUrl);
                    } else {

                    }
                } catch (Exception e) {
                }
                break;
        }
    }
}
