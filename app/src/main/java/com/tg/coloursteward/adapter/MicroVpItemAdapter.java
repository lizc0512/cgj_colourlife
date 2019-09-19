package com.tg.coloursteward.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.entity.MicroDataEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @name ${lizc}
 * @class name：com.tg.coloursteward.adapter
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/7/22 11:17
 * @change
 * @chang time
 * @class 微服务页面适配器
 */
public class MicroVpItemAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<MicroDataEntity.ContentBean> mList = new ArrayList<>();
    private int width;

    public MicroVpItemAdapter(Context context, List<MicroDataEntity.ContentBean> list) {
        this.mContext = context;
        this.mList = list;
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        width = dm.widthPixels / 3;
    }

    public void setData(List<MicroDataEntity.ContentBean> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_micro_viewpager_data_two,
                    parent, false);
            MicroVpViewHolderTwo holderTwo = new MicroVpViewHolderTwo(view);
            return holderTwo;
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_micro_viewpager_data,
                    parent, false);
            MicroVpViewHolder homeDialogViewHolder = new MicroVpViewHolder(view);
            return homeDialogViewHolder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.get(position).getName().equals(" ")) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MicroVpViewHolderTwo) {
            ((MicroVpViewHolderTwo) holder).tv_micro_date_name.setText(" ");
        } else {
            ((MicroVpViewHolder) holder).tv_micro_date_name.setText(mList.get(position).getName());
            ((MicroVpViewHolder) holder).tv_micro_date_content.setText(mList.get(position).getData());
            RelativeLayout.LayoutParams layoutParams =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT);
            layoutParams.width = width;
            ((MicroVpViewHolder) holder).rl_micro_vp_data.setLayoutParams(layoutParams);
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    private static class MicroVpViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_micro_date_name;
        private TextView tv_micro_date_content;
        private RelativeLayout rl_micro_vp_data;

        public MicroVpViewHolder(View itemView) {
            super(itemView);
            tv_micro_date_name = itemView.findViewById(R.id.tv_micro_date_name);
            tv_micro_date_content = itemView.findViewById(R.id.tv_micro_date_content);
            rl_micro_vp_data = itemView.findViewById(R.id.rl_micro_vp_data);
        }
    }

    private static class MicroVpViewHolderTwo extends RecyclerView.ViewHolder {

        private TextView tv_micro_date_name;

        public MicroVpViewHolderTwo(View itemView) {
            super(itemView);
            tv_micro_date_name = itemView.findViewById(R.id.tv_micro_date_name);
        }
    }
}
