package com.tg.setting.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.setting.entity.KeyPopEntity;
import com.tg.setting.view.KeyTimePopWindowView;

import java.util.List;

public class KeyTimeVerAdapter extends RecyclerView.Adapter<KeyTimeVerAdapter.DefaultViewHolder> {

    public List<KeyPopEntity> list;
    public Context mContext;
    public PopupWindow pop;

    public KeyTimeVerAdapter(Context mContext, List<KeyPopEntity> list, PopupWindow pop) {
        this.list = list;
        this.mContext = mContext;
        this.pop = pop;
    }

    @NonNull
    @Override
    public KeyTimeVerAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_key_time_ver, parent, false);
        return new DefaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyTimeVerAdapter.DefaultViewHolder holder, int position) {
        holder.tv_ver.setText(list.get(position).getName());
        if (list.get(position).isSelect()) {
            Drawable door = mContext.getResources().getDrawable(R.drawable.ic_key_address_select);
            holder.tv_ver.setCompoundDrawablesWithIntrinsicBounds(null, null, door, null);
        } else {
            holder.tv_ver.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        holder.rl_item.setOnClickListener(v -> ((KeyTimePopWindowView) pop).selectVer(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl_item;
        TextView tv_ver;

        DefaultViewHolder(View itemView) {
            super(itemView);
            rl_item = itemView.findViewById(R.id.rl_item);
            tv_ver = itemView.findViewById(R.id.tv_ver);
        }
    }

}
