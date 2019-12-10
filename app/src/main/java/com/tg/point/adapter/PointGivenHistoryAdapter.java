package com.tg.point.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.point.entity.PointHistoryEntity;
import com.tg.setting.util.OnItemClickListener;

import java.util.List;

public class PointGivenHistoryAdapter extends RecyclerView.Adapter<PointGivenHistoryAdapter.PointGivenHistoryViewHolder> {

    private List<PointHistoryEntity.ContentBean> totalContentBeanList;
    public OnItemClickListener onClickListener;

    public PointGivenHistoryAdapter(List<PointHistoryEntity.ContentBean> totalContentBeanList) {
        this.totalContentBeanList = totalContentBeanList;
    }

    public void setOnItemClickListener(OnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public PointGivenHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_given_point_history, viewGroup, false);
        PointGivenHistoryViewHolder pointListViewHolder = new PointGivenHistoryViewHolder(view);
        pointListViewHolder.onClickListener = onClickListener;
        return pointListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PointGivenHistoryViewHolder viewHolder, int i) {
        PointHistoryEntity.ContentBean listBean = totalContentBeanList.get(i);
        viewHolder.tv_given_date.setText(listBean.getCreate_time());
        String mobile = listBean.getMobile();
        if (TextUtils.isEmpty(mobile) || "null".equalsIgnoreCase(mobile)) {
            mobile = "";
        }
        viewHolder.tv_given_username.setText(listBean.getDest_client() + " " + mobile);
        viewHolder.tv_given_amount.setTextColor(Color.parseColor("#F24724"));
        viewHolder.tv_given_amount.setText(String.valueOf(Double.valueOf(listBean.getDest_money()) * 1.0f / 100));
    }

    @Override
    public int getItemCount() {
        return totalContentBeanList == null ? 0 : totalContentBeanList.size();
    }


    class PointGivenHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tv_given_username;
        private TextView tv_given_date;
        private TextView tv_given_amount;
        private OnItemClickListener onClickListener;

        public PointGivenHistoryViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this::onClick);
            tv_given_username = itemView.findViewById(R.id.tv_given_username);
            tv_given_date = itemView.findViewById(R.id.tv_given_date);
            tv_given_amount = itemView.findViewById(R.id.tv_given_amount);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
