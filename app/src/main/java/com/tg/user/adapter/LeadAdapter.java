package com.tg.user.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.tg.coloursteward.R;
import com.tg.user.activity.SplashActivity;


/**
 * 引导页adater
 */
public class LeadAdapter extends PagerAdapter {

    private int[] imageBg;
    private LayoutInflater mInflater;
    private Context mContext;

    public LeadAdapter(Context context, int[] imageBg) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.imageBg = imageBg;

    }

    @Override
    public int getCount() {
        return imageBg.length;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = mInflater.inflate(R.layout.adapter_lead_item, null);
        ImageView image = imageLayout.findViewById(R.id.lead_item);
        Button start = imageLayout.findViewById(R.id.start_expexperience);
        image.setImageResource(imageBg[position]);
        (view).addView(imageLayout, 0);
        /**
         * 最后一页显示开始按钮 点击跳转到主界面
         */
        if (imageBg.length - 1 == position) {
            start.setVisibility(View.VISIBLE);
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SplashActivity.class);
                    (mContext).startActivity(intent);
                    ((Activity) mContext).finish();
                }
            });
        } else {
            start.setVisibility(View.GONE);
        }
        return imageLayout;
    }


}
