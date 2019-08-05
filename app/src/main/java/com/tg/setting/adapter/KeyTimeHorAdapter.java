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
import com.tg.setting.view.KeyTimePopWindowView;

import java.util.List;

public class KeyTimeHorAdapter extends RecyclerView.Adapter<KeyTimeHorAdapter.DefaultViewHolder> {

    public List<String> list;
    public Context mContext;
    private PopupWindow pop;

    public KeyTimeHorAdapter(Context mContext, List<String> list, PopupWindow pop) {
        this.list = list;
        this.mContext = mContext;
        this.pop = pop;
    }

    @NonNull
    @Override
    public KeyTimeHorAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_key_time_hor, parent, false);
        return new DefaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyTimeHorAdapter.DefaultViewHolder holder, int position) {
        holder.tv_hor.setText(list.get(position));
        holder.tv_hor.setOnClickListener(v -> ((KeyTimePopWindowView) pop).selectHor(position));
        holder.rl_pop.setBackgroundResource(R.color.white);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder {
        TextView tv_hor;
        private RelativeLayout rl_pop;

        DefaultViewHolder(View itemView) {
            super(itemView);
            tv_hor = itemView.findViewById(R.id.tv_hor);
            rl_pop = itemView.findViewById(R.id.rl_pop);
        }
    }

}
