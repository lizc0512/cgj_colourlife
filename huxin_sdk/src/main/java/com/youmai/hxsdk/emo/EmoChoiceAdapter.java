package com.youmai.hxsdk.emo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.module.picker.PhotoPreviewActivity;
import com.youmai.hxsdk.module.picker.model.LocalImage;

import java.util.List;


/**
 * Created by colin on 2017/10/17.
 */

public class EmoChoiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<LocalImage> dataList;


    public EmoChoiceAdapter(Context mContext, List<LocalImage> dataList) {
        this.dataList = dataList;
        this.mContext = mContext;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.emo_choice_item, parent, false);

        int rest = mContext.getResources().getDisplayMetrics().widthPixels - PhotoPreviewActivity.DRI_WIDTH * 4;

        int SIDE = rest / PhotoPreviewActivity.GIRD_COUNT;
        contentView.setLayoutParams(new RelativeLayout.LayoutParams(SIDE, SIDE));

        return new ThumbViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final LocalImage item = dataList.get(position);

        final ThumbViewHolder vh = (ThumbViewHolder) holder;

        String url = item.getPath();

        if (url.endsWith(".gif")) {
            Glide.with(mContext)
                    .asGif()
                    .load(url)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(vh.img_item);
        } else {
            Glide.with(mContext)
                    .load(item.getOriginalUri())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .fitCenter()
                            .error(R.drawable.hx_icon_rd))
                    .into(vh.img_item);
        }


        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, EmoPreviewActivity.class);
                intent.putExtra("img_path", item.getPath());
                ((EmoChoiceActivity) mContext).startActivityForResult(intent, PhotoPreviewActivity.REQUEST_CODE_PHOTO);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    private class ThumbViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_item;

        public ThumbViewHolder(View itemView) {
            super(itemView);
            img_item = (ImageView) itemView.findViewById(R.id.img_item);
        }
    }

}
