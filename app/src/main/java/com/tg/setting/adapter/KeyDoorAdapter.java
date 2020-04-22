package com.tg.setting.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.setting.activity.KeyDoorManagerActivity;
import com.tg.setting.entity.KeyDoorEntity;

import java.util.List;


public class KeyDoorAdapter extends RecyclerView.Adapter<KeyDoorAdapter.DefaultViewHolder> {

    public List<KeyDoorEntity.ContentBeanX.ContentBean> mList;
    public Context mContext;

    public KeyDoorAdapter(Context mContext, List<KeyDoorEntity.ContentBeanX.ContentBean> list) {
        this.mList = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public KeyDoorAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_key_door, parent, false);
        return new DefaultViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull KeyDoorAdapter.DefaultViewHolder holder, int position) {
        final KeyDoorEntity.ContentBeanX.ContentBean bean = mList.get(position);
        holder.tv_door.setText(bean.getAccessName());

        if (1 == bean.getStatus()) {//1 已绑定
            holder.tv_bind.setVisibility(View.GONE);
            holder.tv_send.setVisibility(View.VISIBLE);
            holder.rl_item.setBackgroundResource(R.drawable.bg_key_normal);
            holder.tv_send.setOnClickListener(v -> ((KeyDoorManagerActivity) mContext).todo(position, bean.getStatus()));
            holder.tv_key_num.setText("钥匙数 " + bean.getKeynum());
        } else { //0 没绑定 可以直接删除
            holder.tv_send.setVisibility(View.GONE);
            holder.tv_bind.setVisibility(View.VISIBLE);
            holder.rl_item.setBackgroundResource(R.drawable.bg_key_no_bind);
            holder.tv_bind.setOnClickListener(v -> ((KeyDoorManagerActivity) mContext).todo(position, bean.getStatus()));
            holder.tv_key_num.setText("未绑定");
        }
        holder.rl_item.setOnClickListener(view -> ((KeyDoorManagerActivity) mContext).toKeyInfor(position, bean.getStatus()));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder {
        TextView tv_door;
        TextView tv_status;
        TextView tv_key_num;
        TextView tv_send;
        TextView tv_bind;
        RelativeLayout rl_item;

        DefaultViewHolder(View itemView) {
            super(itemView);
            tv_door = itemView.findViewById(R.id.tv_door);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_key_num = itemView.findViewById(R.id.tv_key_num);
            tv_send = itemView.findViewById(R.id.tv_send);
            tv_bind = itemView.findViewById(R.id.tv_bind);
            rl_item = itemView.findViewById(R.id.rl_item);
        }
    }

}
