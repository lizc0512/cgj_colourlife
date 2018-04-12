package com.youmai.hxsdk.module.localcontacts;

import android.os.Bundle;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;

/**
 * 作者：create by YW
 * 日期：2018.04.12 13:57
 * 描述：本地手机通讯录
 */

public class LocalContactsActivity extends SdkBaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_contacts_activity);
    }
}
