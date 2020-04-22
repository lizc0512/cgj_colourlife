package com.tg.point.adapter;

import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.point.entity.PointHistoryEntity;
import com.tg.setting.util.OnItemClickListener;

import java.util.List;


public class PointTransactionAdapter extends RecyclerView.Adapter<PointTransactionAdapter.PointTransactionViewHolder> {

    private List<PointHistoryEntity.ContentBean> totalListBean;
    public OnItemClickListener onClickListener;

    public PointTransactionAdapter(List<PointHistoryEntity.ContentBean> totalListBean) {
        this.totalListBean = totalListBean;
    }

    public void setOnItemClickListener(OnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public PointTransactionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_point_transaction, viewGroup, false);
        PointTransactionViewHolder pointTransactionViewHolder = new PointTransactionViewHolder(view);
        pointTransactionViewHolder.onClickListener = onClickListener;
        return pointTransactionViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PointTransactionViewHolder viewHolder, int i) {
        PointHistoryEntity.ContentBean listBean = totalListBean.get(i);
        viewHolder.tv_transaction_name.setText(listBean.getTrans_name());
        viewHolder.tv_transaction_date.setText(listBean.getCreate_time());
        int org_money = Integer.valueOf(listBean.getOrg_money());
        String type = listBean.getType();
        if ("1".equals(type)) {
            viewHolder.tv_transaction_amout.setText("+" + org_money * 1.0f / 100);
            viewHolder.tv_transaction_amout.setTextColor(Color.parseColor("#F24724"));
        } else {
            viewHolder.tv_transaction_amout.setTextColor(Color.parseColor("#25282E"));
            viewHolder.tv_transaction_amout.setText("-" + org_money * 1.0f / 100);
        }
        GlideUtils.loadImageView(viewHolder.itemView.getContext(), listBean.getLogo(), viewHolder.iv_transaction_type);
    }

    @Override
    public int getItemCount() {
        return totalListBean == null ? 0 : totalListBean.size();
    }


    class PointTransactionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iv_transaction_type;
        private TextView tv_transaction_name;
        private TextView tv_transaction_date;
        private TextView tv_transaction_amout;
        private OnItemClickListener onClickListener;

        public PointTransactionViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this::onClick);
            iv_transaction_type = itemView.findViewById(R.id.iv_transaction_type);
            tv_transaction_name = itemView.findViewById(R.id.tv_transaction_name);
            tv_transaction_date = itemView.findViewById(R.id.tv_transaction_date);
            tv_transaction_amout = itemView.findViewById(R.id.tv_transaction_amout);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
