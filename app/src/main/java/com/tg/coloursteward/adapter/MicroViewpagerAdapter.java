package com.tg.coloursteward.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.entity.CropLayoutEntity;

import java.util.List;


/**
 * 微服务页面
 */
public class MicroViewpagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<CropLayoutEntity.ContentBeanX.ContentBean.DataBean> mList;

    public MicroViewpagerAdapter(Context context, List<CropLayoutEntity.ContentBeanX.ContentBean.DataBean> views) {
        this.mContext = context;
        this.mList = views;

    }


    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View viewLayout = LayoutInflater.from(mContext).inflate(R.layout.item_micro_viewpager, container, false);
        TextView tv_micro_vp_title = viewLayout.findViewById(R.id.tv_micro_vp_title);
        TextView tv_micro_vp_content = viewLayout.findViewById(R.id.tv_micro_vp_content);
        tv_micro_vp_title.setText(mList.get(position).getItem_name());
        tv_micro_vp_content.setText(mList.get(position).getData());
        container.addView(viewLayout);
        return viewLayout;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
