package com.tg.coloursteward.adapter;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.util.GlideUtils;

import java.util.List;

/**
 * 作者: zhudewei
 * 时间： 2016/12/18 10:26
 * God bless the program never bug
 */

public class ImagePagerAdapter extends PagerAdapter {
    private List<String> imageList;
    private List<String> urlList;
    private List<String> nameList;
    private Context context;

    public ImagePagerAdapter(Context context, List<String> imageList,
                             List<String> urlList,
                             List<String> nameList) {
        this.context = context;
        this.imageList = imageList;
        this.urlList = urlList;
        this.nameList = nameList;
    }

    @Override
    public int getCount() {
        return urlList == null ? 0 : urlList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.viewpager_item, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.iv_item);
        final String url = urlList.get(position);
        final String name = nameList.get(position);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && url != null) {
                    listener.OnImgClick(position, url, name);
                }
            }
        });
        GlideUtils.loadImageView(context, imageList.get(position), imageView);
        container.addView(rootView);
        return rootView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    private OnImgClickListener listener;

    public void setOnImgClickListener(OnImgClickListener listener) {
        this.listener = listener;
    }

    public interface OnImgClickListener {
        void OnImgClick(int pos, String url, String name);
    }
}
