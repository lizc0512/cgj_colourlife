package com.youmai.hxsdk;


import android.content.Intent;
import android.os.Bundle;

import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.sp.SPDataUtil;


/**
 * Created by colin on 2016/9/12.
 * 用户秀选择素材
 */
public class SettingShowActivity extends SdkBaseActivity {

    /*
     * Const.
     */
    public static final int REQ_SHOW_DETAIL = 1; //秀详细请求

    public static final int RES_SHOW_APPLY_SUCCESS = 2; //秀设置成功

    @Override
    public void onResume() {
        super.onResume();
        /*if(SPDataUtil.getVideoRefresh(this)) {//设置完视频秀后销毁此界面
         overridePendingTransition(0, 0);
         finish();
         }
         SPDataUtil.setVideoRefresh(this, false);*/
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_show_setting);

        HuxinSdkManager.instance().getStackAct().addActivity(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //todo_k:
        if (REQ_SHOW_DETAIL == requestCode && RES_SHOW_APPLY_SUCCESS == resultCode) {
            overridePendingTransition(0, 0);
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HuxinSdkManager.instance().getStackAct().finishActivity(this);
    }
}
