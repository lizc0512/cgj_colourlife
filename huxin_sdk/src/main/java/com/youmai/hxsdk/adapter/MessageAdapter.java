package com.youmai.hxsdk.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

/**
 * iewPager适配器
 * Created by colin on 2016/6/30.
 */
public class MessageAdapter extends PagerAdapter {


    private List<View> mViews;

    public MessageAdapter(List<View> list) {
        mViews = list;
    }


    @Override
    public int getCount() {
        return mViews.size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViews.get(position), 0);
        return mViews.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == (obj);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
    }
}