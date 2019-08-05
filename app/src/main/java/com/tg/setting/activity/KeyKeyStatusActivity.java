package com.tg.setting.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;

/**
 * 乐开-钥匙身份
 *
 * @author hxg 2019.07.18
 */
public class KeyKeyStatusActivity extends BaseActivity {
    private TextView tvVersionShort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvVersionShort = findViewById(R.id.tv_versionShort);
//        String versionShort = BuildConfig.VERSION_NAME;
//        tvVersionShort.setText("当前版本 V" + versionShort);
//        headView.setVisibility(View.GONE);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_key_key_status, null);
    }

    @Override
    public String getHeadTitle() {
        return "钥匙身份";
    }

}
