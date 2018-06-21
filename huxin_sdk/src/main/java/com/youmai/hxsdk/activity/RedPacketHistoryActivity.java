package com.youmai.hxsdk.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.entity.red.RedPacketHistoryDetail;
import com.youmai.hxsdk.fragment.RedPackageHistoryFragment;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.utils.GsonUtil;

/**
 * 作者：create by YW
 * 日期：2017.06.07 11:42
 * 描述：Red packet
 */
public class RedPacketHistoryActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = RedPacketHistoryActivity.class.getSimpleName();

    private TextView tv_back;
    private TextView tv_title;
    private TextView tv_right;

    private ImageView img_head;
    private TextView tv_name;
    private TextView tv_red_title;
    private TextView tv_money;

    private TextView tv_info;
    private TextView tv_status;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_red_packet_history);
        initView();
        setupViewPager();
        loadRedPacket();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("利是记录");

        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_right.setText("2018年");


        img_head = (ImageView) findViewById(R.id.img_head);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_red_title = (TextView) findViewById(R.id.tv_red_title);
        tv_money = (TextView) findViewById(R.id.tv_money);

        tv_info = (TextView) findViewById(R.id.tv_info);
        tv_status = (TextView) findViewById(R.id.tv_status);


        tv_back.setOnClickListener(this);

    }


    private void loadRedPacket() {
        HuxinSdkManager.instance().redSendPacketDetail("201806", new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RedPacketHistoryDetail bean = GsonUtil.parse(response, RedPacketHistoryDetail.class);
                if (bean != null && bean.isSuccess()) {

                }
            }
        });

        HuxinSdkManager.instance().redReceivePacketDetail("201806", new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RedPacketHistoryDetail bean = GsonUtil.parse(response, RedPacketHistoryDetail.class);
                if (bean != null && bean.isSuccess()) {

                }
            }
        });

    }


    private void setupViewPager() {
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(mViewPager.getAdapter().getCount());
        mTabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_back) {
            onBackPressed();
        } else if (id == R.id.tv_right) {

        }
    }


    private class TabFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private String[] tabTitle;


        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            tabTitle = getResources().getStringArray(R.array.red_title);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int arg0) {
            Fragment ft = null;

            switch (arg0) {
                case 0:
                    ft = new RedPackageHistoryFragment();
                    break;
                case 1:
                    ft = new RedPackageHistoryFragment();
                    break;
                default:
                    break;
            }
            return ft;
        }


        @Override
        public int getCount() {
            return tabTitle.length;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitle[position];
        }

    }
}
