package com.tg.coloursteward;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.updateapk.UpdateManager;

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
        tvVersionShort = (TextView) findViewById(R.id.tv_versionShort);
        String versionShort = UpdateManager.getVersionName(AboutUsActivity.this);
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
