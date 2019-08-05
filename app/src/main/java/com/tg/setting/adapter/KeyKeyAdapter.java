package com.tg.setting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;

import java.util.List;

public class KeyKeyAdapter extends RecyclerView.Adapter<KeyKeyAdapter.DefaultViewHolder> {

    public List<String> addBeanList;
    public Context mContext;

    public KeyKeyAdapter(Context mContext, List<String> addBeanList) {
        this.addBeanList = addBeanList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public KeyKeyAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_key_key, parent, false);
        return new DefaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyKeyAdapter.DefaultViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return addBeanList == null ? 0 : addBeanList.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_doors;
        TextView tv_todo;

        DefaultViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_doors = itemView.findViewById(R.id.tv_doors);
            tv_todo = itemView.findViewById(R.id.tv_todo);
        }
    }

}
