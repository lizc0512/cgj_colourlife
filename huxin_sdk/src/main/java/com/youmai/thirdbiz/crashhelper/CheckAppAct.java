package com.youmai.thirdbiz.crashhelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.ToastUtil;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2017-02-22 15:25
 * Description:
 */
public class CheckAppAct extends Activity {

   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new AlertDialog.Builder(this).setMessage(R.string.cgj_crash_title).setPositiveButton(R.string.cgj_btn_sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                appUpdateDownLoad();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setCancelable(false).show();
    }


    public void appUpdateDownLoad() {
        CacheHelper cacheHelper = new CacheHelper(this);

        String url = cacheHelper.getDownloadUrl();
        if (TextUtils.isEmpty(url))
        {
            ToastUtil.showToast(CheckAppAct.this, "没有下载地址");
            return;
        }
        LogUtils.e("xx",url);
        UpdateHelper updateHelper = new UpdateHelper(this, url, new UpdateHelper.IUpdateCallback() {
            @Override
            public void onCallBack(boolean isFinish) {

                if (isFinish){
                    ToastUtil.showToast(CheckAppAct.this, "下载成功");
                }else
                {
                    ToastUtil.showToast(CheckAppAct.this, "下载失败");
                }

            }
        });
        cacheHelper.cleanErrorNum();

        updateHelper.downLoadApk();


    }

}

