package com.tg.point.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.tg.coloursteward.R;
import com.tg.point.entity.PointReturnEntity;

import java.util.List;


public class PointReturnPlanAdapter extends RecyclerView.Adapter<PointReturnPlanAdapter.PointReturnPlanViewHolder> {
    private List<PointReturnEntity.ContentBean.ListBean> totalBeanList;

    public PointReturnPlanAdapter(List<PointReturnEntity.ContentBean.ListBean> totalBeanList) {
        this.totalBeanList = totalBeanList;
    }


    @NonNull
    @Override
    public PointReturnPlanViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_return_point_plan, viewGroup, false);
        PointReturnPlanViewHolder pointListViewHolder = new PointReturnPlanViewHolder(view);
        return pointListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PointReturnPlanViewHolder viewHolder, int i) {
        PointReturnEntity.ContentBean.ListBean listBean = totalBeanList.get(i);
        viewHolder.tv_return_date.setText(listBean.getTime());
        viewHolder.tv_return_amount.setText("返还" + listBean.getMoney() * 1.0 / 100);
    }

    @Override
    public int getItemCount() {
        return totalBeanList.size();
    }


    class PointReturnPlanViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_return_date;
        private TextView tv_return_amount;


        public PointReturnPlanViewHolder(View itemView) {
            super(itemView);
            tv_return_date = itemView.findViewById(R.id.tv_return_date);
            tv_return_amount = itemView.findViewById(R.id.tv_return_amount);
        }
    }
}
