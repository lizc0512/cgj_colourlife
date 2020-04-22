package com.tg.setting.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;

import java.util.List;

/***
 *
 *  开门统计
 */

public class KeyDoorStatisticsAdapter extends RecyclerView.Adapter<KeyDoorStatisticsAdapter.DefaultViewHolder> {

    public List<String> mNameList;
    public List<String> mValuesList;
    public Context mContext;

    public KeyDoorStatisticsAdapter(Context mContext, List<String> mNameList, List<String> mValuesList) {
        this.mNameList = mNameList;
        this.mValuesList = mValuesList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public KeyDoorStatisticsAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_key_statistics, parent, false);
        return new DefaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyDoorStatisticsAdapter.DefaultViewHolder holder, int position) {
        String values = mValuesList.get(position);
        String name = mNameList.get(position);
        holder.tv_statistics_data.setText(values);
        holder.tv_statistics_name.setText(name);

    }

    @Override
    public int getItemCount() {
        return mValuesList == null ? 0 : mValuesList.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder {

        TextView tv_statistics_data;
        TextView tv_statistics_name;

        DefaultViewHolder(View itemView) {
            super(itemView);
            tv_statistics_data = itemView.findViewById(R.id.tv_statistics_data);
            tv_statistics_name = itemView.findViewById(R.id.tv_statistics_name);
        }
    }

}
