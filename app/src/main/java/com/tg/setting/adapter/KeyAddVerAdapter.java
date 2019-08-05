package com.tg.setting.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.setting.entity.KeyPopEntity;
import com.tg.setting.view.KeyRoomPopWindowView;
import com.tg.setting.view.KeyUnitPopWindowView;

import java.util.List;

public class KeyAddVerAdapter extends RecyclerView.Adapter<KeyAddVerAdapter.DefaultViewHolder> {

    public List<KeyPopEntity> list;
    public Context mContext;
    public PopupWindow pop;
    private boolean isUnit;

    public KeyAddVerAdapter(Context mContext, List<KeyPopEntity> list, PopupWindow pop, boolean isUnit) {
        this.list = list;
        this.mContext = mContext;
        this.pop = pop;
        this.isUnit = isUnit;
    }

    @NonNull
    @Override
    public KeyAddVerAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_key_time_ver, parent, false);
        return new DefaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyAddVerAdapter.DefaultViewHolder holder, int position) {
        holder.tv_ver.setText(list.get(position).getName());
        if (list.get(position).isSelect()) {
            Drawable door = mContext.getResources().getDrawable(R.drawable.ic_key_address_select);
            holder.tv_ver.setCompoundDrawablesWithIntrinsicBounds(null, null, door, null);
        } else {
            holder.tv_ver.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        holder.tv_ver.setOnClickListener(v -> {
            if (isUnit) {
                ((KeyUnitPopWindowView) pop).selectVer(position);
            } else {
                ((KeyRoomPopWindowView) pop).selectVer(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder {
        TextView tv_ver;

        DefaultViewHolder(View itemView) {
            super(itemView);
            tv_ver = itemView.findViewById(R.id.tv_ver);
        }
    }

}
