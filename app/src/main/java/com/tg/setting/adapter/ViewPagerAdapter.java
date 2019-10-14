package com.tg.setting.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String[] tabTitleArray;
    private List<Fragment> fragmentList;


    public ViewPagerAdapter(FragmentManager fm, List fragmentList, String[] tabTitleArray) {
        super(fm);

        this.tabTitleArray = tabTitleArray;
        this.fragmentList = fragmentList;
    }


    /* 重写与TabLayout配合 */

    @Override
    public int getCount() {
        return tabTitleArray.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitleArray[position % tabTitleArray.length];
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }
}