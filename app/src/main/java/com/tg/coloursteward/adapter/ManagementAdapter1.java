package com.tg.coloursteward.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tg.coloursteward.R;
import com.tg.coloursteward.info.GridViewInfo;

import java.util.ArrayList;

public class ManagementAdapter1 extends RecyclerView.Adapter {

    private ArrayList<GridViewInfo> mList;
    private Context mContext;
    private ItemEventListener itemEventListener;

    public interface ItemEventListener {
        void onItemClick(GridViewInfo contact);
    }

    public ManagementAdapter1(Context context, ArrayList<GridViewInfo> list) {
        this.mList = list;
        this.mContext = context;
    }


    public void setItemEventListener(ItemEventListener itemEventListener) {
        this.itemEventListener = itemEventListener;
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item1, parent, false);
        return new ItemHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final GridViewInfo item = mList.get(position);

        if (holder instanceof ItemHolder) {
            TextView tvName = ((ItemHolder) holder).tvName;
            ImageView ivIcon = ((ItemHolder) holder).ivIcon;
            tvName.setText(item.name);
            if (!TextUtils.isEmpty(item.icon)) {
                String icon;
                if (item.icon.contains("-100-100.jpg")) {
                    icon = item.icon.replaceAll("-100-100.jpg", "");
                } else {
                    icon = item.icon;
                }

                Glide.with(mContext).load(icon)
                        .apply(new RequestOptions()
                                .error(R.drawable.zhanwei)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                        .into(ivIcon);
            } else {
                ivIcon.setImageResource(R.drawable.zhanwei);
            }
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != itemEventListener) {
                    itemEventListener.onItemClick(item);
                }
            }
        });
    }


    private class ItemHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivIcon;

        ItemHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_grid_item);
            ivIcon = itemView.findViewById(R.id.iv_grid_item);
        }
    }


}
