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
import com.tg.setting.view.KeyRoomPopWindowView;
import com.tg.setting.view.KeyUnitPopWindowView;

import java.util.List;

public class KeyAddHorAdapter extends RecyclerView.Adapter<KeyAddHorAdapter.DefaultViewHolder> {

    public List<String> list;
    public Context mContext;
    private PopupWindow pop;
    private boolean isUnit;

    public KeyAddHorAdapter(Context mContext, List<String> list, PopupWindow pop, boolean isUnit) {
        this.list = list;
        this.mContext = mContext;
        this.pop = pop;
        this.isUnit = isUnit;
    }

    @NonNull
    @Override
    public KeyAddHorAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_key_time_hor, parent, false);
        return new DefaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyAddHorAdapter.DefaultViewHolder holder, int position) {
        holder.tv_hor.setText(list.get(position));
        holder.tv_hor.setOnClickListener(v -> {
            if (isUnit) {
                ((KeyUnitPopWindowView) pop).selectHor(position);
            } else {
                ((KeyRoomPopWindowView) pop).selectHor(position);
            }
        });
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
