package com.tg.setting.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tg.coloursteward.BuildConfig;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;

/**
 * 邀请同事
 *
 * @author Administrator
 */
public class InviteRegisterActivity extends BaseActivity {
    private TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvVersion = findViewById(R.id.tv_version);
        String versionShort = BuildConfig.VERSION_NAME;
        tvVersion.setText("Android: V" + versionShort + "/IOS：V" + versionShort);

    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_invite_register, null);
    }

    @Override
    public String getHeadTitle() {
        return "邀请同事";
    }


}
