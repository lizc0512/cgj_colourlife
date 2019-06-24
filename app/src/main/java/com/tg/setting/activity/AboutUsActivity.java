package com.tg.setting.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tg.coloursteward.BuildConfig;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;

/**
 * 关于app页面
 *
 * @author Administrator
 */
public class AboutUsActivity extends BaseActivity {
    private TextView tvVersionShort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvVersionShort = findViewById(R.id.tv_versionShort);
        String versionShort = BuildConfig.VERSION_NAME;
        tvVersionShort.setText("当前版本 V" + versionShort);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_about_us, null);
    }

    @Override
    public String getHeadTitle() {
        return "关于彩管家";
    }

}
