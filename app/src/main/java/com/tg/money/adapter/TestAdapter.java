package com.tg.money.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.money.entity.DistributionRecordEntity;

import java.util.List;

/**
 * 发卡器下面对应的门禁
 */
public class TestAdapter extends RecyclerView.Adapter<TestAdapter.DefaultViewHolder> {

    public List<DistributionRecordEntity.ContentBean.ListBean> keyList;
    public Context mContext;


    public TestAdapter(Context mContext, List<DistributionRecordEntity.ContentBean.ListBean> keyList) {
        this.keyList = keyList;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public TestAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_distribution_record, parent, false);
        return new TestAdapter.DefaultViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TestAdapter.DefaultViewHolder holder, int position) {
        final DistributionRecordEntity.ContentBean.ListBean bean = keyList.get(position);
        holder.tv_distribution_name.setText(bean.getGeneral_name());
    }

    @Override
    public int getItemCount() {
        return keyList == null ? 0 : keyList.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_distribution_name;

        DefaultViewHolder(View itemView) {
            super(itemView);
            tv_distribution_name = itemView.findViewById(R.id.tv_distribution_name);
        }
    }
}
