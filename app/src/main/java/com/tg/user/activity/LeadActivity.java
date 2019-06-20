package com.tg.user.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.user.adapter.LeadAdapter;
import com.tg.user.view.CirclePageIndicator;

public class LeadActivity extends BaseActivity implements View.OnClickListener {
    private ViewPager leadViewPager;
    private CirclePageIndicator indicator;
    private int[] imageBg = {R.mipmap.zero, R.mipmap.one, R.mipmap.two, R.mipmap.three};
    private TextView tv_lead_jump;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead);
        initView();
        initData();
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }


    public void initView() {
        tv_lead_jump = findViewById(R.id.tv_lead_jump);
        leadViewPager = findViewById(R.id.lead_viewPager);
        indicator = findViewById(R.id.indicator);
        leadViewPager.setAdapter(new LeadAdapter(this, imageBg));
        indicator.setViewPager(leadViewPager, 0);
        tv_lead_jump.setOnClickListener(this);
        leadViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                indicator.setCurrentItem(arg0);
                if (arg0 == imageBg.length - 1) {
                    indicator.setVisibility(View.GONE);
                    tv_lead_jump.setVisibility(View.GONE);
                    tv_lead_jump.setText(getResources().getString(R.string.lead_go_open));
                    tv_lead_jump.setTextColor(getResources().getColor(R.color.blue));
                } else {
                    indicator.setVisibility(View.VISIBLE);
                    tv_lead_jump.setVisibility(View.VISIBLE);
                    tv_lead_jump.setText(getResources().getString(R.string.tv_lead_jump));
                    tv_lead_jump.setTextColor(getResources().getColor(R.color.tab_gray));
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    public void initData() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.READ_CONTACTS,
                            android.Manifest.permission.READ_PHONE_STATE},
                    Activity.DEFAULT_KEYS_SEARCH_LOCAL);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_lead_jump:
                Intent intent = new Intent(this, SplashActivity.class);
                startActivity(intent);
                LeadActivity.this.finish();
                break;
        }
    }
}
