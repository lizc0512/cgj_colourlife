package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.rd.PageIndicatorView;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.adapter.PagerGuideAdapter;
import com.youmai.hxsdk.adapter.PagerIndicatorAdapter;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.entity.GuideType;
import com.youmai.hxsdk.utils.GlideRoundTransform;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2016.11.08 15:48
 * 描述：推送通知界面
 */
public class HxGuideDialog extends Dialog implements View.OnClickListener {

    private Context mContext;

    private ViewPager viewPager;
    private PageIndicatorView indicatorView;
    private ImageView imgClose;
    private TextView tv_title;

    private List<GuideType> list;


    public HxGuideDialog(Context context, List<GuideType> list) {
        super(context, R.style.hx_sdk_dialog);
        setCanceledOnTouchOutside(false);
        mContext = context;
        this.list = list;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_guide_dialog);
        initView();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tv_title = (TextView) findViewById(R.id.tv_title);
        if (!ListUtils.isEmpty(list)) {
            tv_title.setText(list.get(0).getTitle());
        }


        indicatorView = (PageIndicatorView) findViewById(R.id.page_indicator_view);
        imgClose = (ImageView) findViewById(R.id.iv_notify_close);
        imgClose.setOnClickListener(this);

        final List<View> listView = new ArrayList<>();
        LayoutInflater mInflater = getLayoutInflater();


        if (list != null && list.size() > 0) {
            for (final GuideType item : list) {
                String url = AppConfig.getImageUrl(mContext, item.getFid());

                View view = mInflater.inflate(R.layout.hxm_guide_item, null);

                ImageView imageView = (ImageView) view.findViewById(R.id.img_content);

                listView.add(view);


                Glide.with(mContext)
                        .load(url)
                        .apply(new RequestOptions().fitCenter()
                                .error(R.drawable.hx_icon_rd))
                        .into(imageView);
            }
        }

        PagerGuideAdapter adapter = new PagerGuideAdapter(mContext, listView);
        viewPager.setAdapter(adapter);
        //indicatorView.setViewPager(viewPager);

        indicatorView.setCount(listView.size());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {/*empty*/}

            @Override
            public void onPageSelected(int position) {
                indicatorView.setSelection(position);
                tv_title.setText(list.get(position).getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {/*empty*/}
        });


    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_notify_close) {
            dismiss();
        }
    }
}
