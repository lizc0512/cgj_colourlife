
package com.youmai.hxsdk.push.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.youmai.hxsdk.R;

public class RemindMsgContentActivity extends AppCompatActivity {
    private static final String TAG = RemindMsgContentActivity.class.getSimpleName();


    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TabFragmentPagerAdapter mAdapter;
    private FragmentManager fragmentManager;

    private TextView tv_title;

    boolean isDel = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_remind_main);
        initTitle();
        initView();
    }


    private void initTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        tv_title = (TextView) findViewById(R.id.tv_title);
        setSupportActionBar(toolbar);

        int type = getIntent().getIntExtra("type", 0);
        String title = null;
        if (type == 100) {
            title = "呼信提醒";
        }
        tv_title.setText(title);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void initView() {

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        fragmentManager = getSupportFragmentManager();
        mAdapter = new TabFragmentPagerAdapter(fragmentManager);

        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public void onBackPressed() {
        if (isDel) {
            setResult(Activity.RESULT_OK);
        }
        finish();
    }

    private class TabFragmentPagerAdapter extends FragmentStatePagerAdapter {

        //private Context mContext;
        private String[] tabTitle = {"已提醒", "即将提醒"};

        private TabFragmentPagerAdapter(FragmentManager fm/*, Context context*/) {
            super(fm);
            //mContext = context;
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
                    ft = new RemindMsgAcceptFragment();
                    break;
                case 1:
                    ft = new RemindMsgFutureFragment();
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
        public int getItemPosition(Object object) {
            // 系统默认返回     POSITION_UNCHANGED，未改变
            return POSITION_NONE;   // 返回发生改变，让系统重新加载
        }

        /*private View getTabView(int position) {
            // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
            View v = LayoutInflater.from(mContext).inflate(R.layout.custom_tab, null);
            TextView tv_tab = (TextView) v.findViewById(R.id.tv_tab);
            tv_tab.setText(tabTitle[position]);
            TextView tv_count = (TextView) v.findViewById(R.id.tv_count);
            tv_count.setText(tabCount[position]);

            return v;
        }*/


        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitle[position];
        }

    }


}
