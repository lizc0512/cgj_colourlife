package com.tg.setting.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.setting.entity.KeyCommunityListEntity;
import com.tg.setting.view.KeyCommunityPopWindowView;

import java.util.List;


public class KeyCommunityAdapter extends RecyclerView.Adapter<KeyCommunityAdapter.DefaultViewHolder> {

    public List<KeyCommunityListEntity.ContentBeanX.ContentBean> list;
    public Context mContext;
    public PopupWindow mPop;
    public int selectPosition;

    public KeyCommunityAdapter(Context mContext, List<KeyCommunityListEntity.ContentBeanX.ContentBean> list, PopupWindow pop, int selectPosition) {
        this.mContext = mContext;
        this.list = list;
        this.mPop = pop;
        this.selectPosition = selectPosition;
    }

    @NonNull
    @Override
    public KeyCommunityAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_key_address, parent, false);
        return new DefaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyCommunityAdapter.DefaultViewHolder holder, int position) {
        holder.tv_address.setText(list.get(position).getName());
        if (selectPosition == position) {
            Drawable door = mContext.getResources().getDrawable(R.drawable.ic_key_address_select);
            holder.tv_address.setCompoundDrawablesWithIntrinsicBounds(null, null, door, null);
        } else {
            holder.tv_address.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        holder.rl_item.setOnClickListener(v -> ((KeyCommunityPopWindowView) mPop).selectCommunity(position, list.get(position).getName(), list.get(position).getCommunityUuid()));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl_item;
        TextView tv_address;

        DefaultViewHolder(View itemView) {
            super(itemView);
            rl_item = itemView.findViewById(R.id.rl_item);
            tv_address = itemView.findViewById(R.id.tv_address);
        }
    }

}
