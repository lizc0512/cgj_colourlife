package com.youmai.hxsdk.module.groupchat;

import android.os.Bundle;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;

/**
 * 作者：create by YW
 * 日期：2018.04.12 13:54
 * 描述：群聊列表
 */

public class GroupListActivity extends SdkBaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_list_activity);
    }
}
