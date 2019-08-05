package com.tg.setting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.setting.view.KeyStringPopWindowView;

import java.util.List;


public class KeyStringAdapter extends RecyclerView.Adapter<KeyStringAdapter.DefaultViewHolder> {

    public List<String> list;
    public Context mContext;
    public PopupWindow pop;

    public KeyStringAdapter(Context mContext, List<String> list, PopupWindow pop) {
        this.list = list;
        this.mContext = mContext;
        this.pop = pop;
    }

    @NonNull
    @Override
    public KeyStringAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_key_address, parent, false);
        return new DefaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyStringAdapter.DefaultViewHolder holder, int position) {
        holder.tv_address.setText(list.get(position));
        holder.rl_item.setOnClickListener(v -> ((KeyStringPopWindowView) pop).setIdentity(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder {
        TextView tv_address;
        RelativeLayout rl_item;

        DefaultViewHolder(View itemView) {
            super(itemView);
            rl_item = itemView.findViewById(R.id.rl_item);
            tv_address = itemView.findViewById(R.id.tv_address);
        }
    }

}
