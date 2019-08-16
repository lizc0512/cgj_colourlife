package com.tg.coloursteward.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.entity.CropLayoutEntity;
import com.tg.coloursteward.inter.MicroApplicationCallBack;

import java.util.List;


/**
 * 微服务页面
 */
public class MicroViewpagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<CropLayoutEntity.ContentBeanX.ContentBean.DataBean> mList;
    private MicroApplicationCallBack callBack;

    public void setCallBack(MicroApplicationCallBack mcallBack) {
        this.callBack = mcallBack;
    }

    public MicroViewpagerAdapter(Context context, List<CropLayoutEntity.ContentBeanX.ContentBean.DataBean> views) {
        this.mContext = context;
        this.mList = views;

    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size() * 20;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View viewLayout = LayoutInflater.from(mContext).inflate(R.layout.item_micro_viewpager, container, false);
        TextView tv_micro_vp_title = viewLayout.findViewById(R.id.tv_micro_vp_title);
        TextView tv_micro_vp_content = viewLayout.findViewById(R.id.tv_micro_vp_content);
        RelativeLayout rl_micro_viewpager = viewLayout.findViewById(R.id.rl_micro_viewpager);
        int pos = position % mList.size();
        tv_micro_vp_title.setText(mList.get(pos).getName());
        tv_micro_vp_content.setText(mList.get(pos).getData());
        if ("1".equals(mList.get(pos).getIsShow())) {
            tv_micro_vp_title.setTextColor(mContext.getResources().getColor(R.color.white));
            tv_micro_vp_content.setTextColor(mContext.getResources().getColor(R.color.white));
            rl_micro_viewpager.setBackground(mContext.getResources().getDrawable(R.drawable.pic_bg_data_select_s3));
        } else {
            rl_micro_viewpager.setBackground(mContext.getResources().getDrawable(R.drawable.pic_bg_data_select_n3));
        }
        if (null != callBack) {
            rl_micro_viewpager.setOnClickListener(v -> callBack.onclick(
                    pos, mList.get(pos).getRedirect_url(), mList.get(pos).getAuth_type()));
        }

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
