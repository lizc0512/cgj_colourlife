package com.tg.delivery.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.setting.adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @name 盘点明细
 * @class name：com.tg.delivery.activity
 * @class describe
 * @anthor QQ:510906433
 * @time 2020/6/17 11:55
 * @change
 * @chang time
 * @class describe
 */
public class InventoryDetailActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_base_back;
    private TextView tv_base_title;
    private TabLayout tl_detail_type;
    private ViewPager vp_detail;
    private NormalDeliveryFragment normalDeliveryFragment;
    private ErrorDeliveryFragment errorDeliveryFragment;
    private List<Fragment> fragmentList = new ArrayList<>();
    private String dataType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_detail);
        initView();
        initData();
    }

    private void initData() {
    }

    private void initView() {
        dataType = getIntent().getStringExtra("dataType");
        iv_base_back = findViewById(R.id.iv_base_back);
        tv_base_title = findViewById(R.id.tv_base_title);
        tl_detail_type = findViewById(R.id.tl_detail_type);
        vp_detail = findViewById(R.id.vp_detail);
        tv_base_title.setText("盘点明细");
        iv_base_back.setOnClickListener(this);

        String[] tabTitleArray = {"正常滞留", "异常运单"};
        for (int i = 0; i < tabTitleArray.length; i++) {
            tl_detail_type.addTab(tl_detail_type.newTab().setText(tabTitleArray[i]));
        }
        normalDeliveryFragment = new NormalDeliveryFragment();
        errorDeliveryFragment = new ErrorDeliveryFragment();
        fragmentList.add(normalDeliveryFragment);
        fragmentList.add(errorDeliveryFragment);
        tl_detail_type.setTabIndicatorFullWidth(false);
        tl_detail_type.setSelectedTabIndicatorColor(Color.parseColor("#597EF7"));
        tl_detail_type.setTabTextColors(Color.parseColor("#8D9299"), Color.parseColor("#597EF7"));
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, tabTitleArray);
        vp_detail.setAdapter(adapter);
        vp_detail.setOffscreenPageLimit(fragmentList.size());
        tl_detail_type.setupWithViewPager(vp_detail);
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_base_back:
                finish();
                break;
        }
    }
}