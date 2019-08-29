package com.tg.setting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.setting.entity.KeyDoorEntity;
import com.tg.setting.entity.KeyDoorOpenLogsEntity;

import java.util.List;

/***
 *
 *  用户的对某个门的操作记录
 */

public class KeyDoorUserRecordAdapter extends RecyclerView.Adapter<KeyDoorUserRecordAdapter.DefaultViewHolder> {

    public List<KeyDoorOpenLogsEntity.ContentBean.DataBean> mList;
    public Context mContext;

    public KeyDoorUserRecordAdapter(Context mContext, List<KeyDoorOpenLogsEntity.ContentBean.DataBean> list) {
        this.mList = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public KeyDoorUserRecordAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_key_door_record, parent, false);
        return new DefaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyDoorUserRecordAdapter.DefaultViewHolder holder, int position) {
        KeyDoorOpenLogsEntity.ContentBean.DataBean contentBean = mList.get(position);
        holder.tv_opendoor_time.setText(contentBean.getCreateTime());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder {

        TextView tv_opendoor_time;
        TextView tv_door_desc;

        DefaultViewHolder(View itemView) {
            super(itemView);
            tv_opendoor_time = itemView.findViewById(R.id.tv_opendoor_time);
            tv_door_desc = itemView.findViewById(R.id.tv_door_desc);
        }
    }

}
