package com.tg.coloursteward.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.tg.coloursteward.R;
import com.youmai.hxsdk.contact.ContactFragment;


/**
 * Created by colin on 2018/3/15.
 */

public class MainActivity1 extends AppCompatActivity {

    private ViewPager mViewPager;
    private BottomNavigationViewEx navigation;
    //private TabLayout mTabLayout;

    private TabFragmentPagerAdapter mAdapter;
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        initTitle();
        initView();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    private void initTitle() {

    }


    private void initView() {

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        navigation = (BottomNavigationViewEx) findViewById(R.id.navigation);

        //mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

        fragmentManager = getSupportFragmentManager();
        mAdapter = new TabFragmentPagerAdapter(fragmentManager, navigation.getItemCount());

        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        navigation.enableShiftingMode(false); //取消切换动画
        navigation.enableItemShiftingMode(false); //取消文字
        navigation.enableAnimation(false);  //取消选中动画
        navigation.setupWithViewPager(mViewPager);

        //mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.setCurrentItem(0);
    }


    private class TabFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private int mCount;

        private TabFragmentPagerAdapter(FragmentManager fm, int count) {
            super(fm);
            mCount = count;
        }


        @Override
        public Fragment getItem(int arg0) {
            Fragment ft = null;
            switch (arg0) {
                case 0:
                    ft = SignCountFragment.newInstance("title1");
                    break;
                case 1:
                    ft = new ContactFragment();
                    break;
                case 2:
                    ft = SignCountFragment.newInstance("title3");
                    break;
                case 3:
                    ft = SignCountFragment.newInstance("title4");
                    break;
                default:
                    break;
            }

            return ft;
        }


        @Override
        public int getCount() {
            return mCount;
        }

    }


}
