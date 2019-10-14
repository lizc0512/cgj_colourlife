package com.tg.setting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.setting.activity.KeyDoorManagerActivity;
import com.tg.setting.entity.KeyBagsEntity;
import com.tg.setting.entity.KeyCardEntity;
import com.tg.setting.util.OnItemClickListener;

import java.util.List;

public class KeyCardAdapter extends RecyclerView.Adapter<KeyCardAdapter.DefaultViewHolder> {

    public List<KeyCardEntity.ContentBeanX.ContentBean> keyCardList;
    public Context mContext;
    private OnItemClickListener onClickListener;

    public KeyCardAdapter(Context mContext, List<KeyCardEntity.ContentBeanX.ContentBean> keyCardList) {
        this.keyCardList = keyCardList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public KeyCardAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_card_device, parent, false);
        DefaultViewHolder defaultViewHolder = new DefaultViewHolder(view);
        defaultViewHolder.onClickListener = onClickListener;
        return new DefaultViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull KeyCardAdapter.DefaultViewHolder holder, int position) {
        KeyCardEntity.ContentBeanX.ContentBean contentBean = keyCardList.get(position);
        holder.card_infor_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof KeyDoorManagerActivity) {
                    ((KeyDoorManagerActivity) mContext).enterCardList(contentBean.getId());
                }
            }
        });
        holder.tv_delete_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((KeyDoorManagerActivity) mContext).delDeviceCard(contentBean.getId(), position);
            }
        });
        String macAddress=contentBean.getMac();
        holder.tv_connect_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof KeyDoorManagerActivity) {
                    ((KeyDoorManagerActivity) mContext).connectSendCard(macAddress,contentBean.getId());
                }
            }
        });
        holder.tv_card_name.setText(contentBean.getModel());
        holder.tv_card_mac.setText("MAC  " + macAddress);
        holder.tv_card_number.setText("门禁卡:" + contentBean.getCardCount());
    }

    @Override
    public int getItemCount() {
        return keyCardList == null ? 0 : keyCardList.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout card_infor_layout;
        TextView tv_card_name;
        TextView tv_card_mac;
        TextView tv_card_number;
        TextView tv_connect_device;
        TextView tv_delete_device;
        OnItemClickListener onClickListener;

        DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            card_infor_layout = itemView.findViewById(R.id.card_infor_layout);
            tv_card_name = itemView.findViewById(R.id.tv_card_name);
            tv_card_mac = itemView.findViewById(R.id.tv_card_mac);
            tv_card_number = itemView.findViewById(R.id.tv_card_number);
            tv_connect_device = itemView.findViewById(R.id.tv_connect_device);
            tv_delete_device = itemView.findViewById(R.id.tv_delete_device);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
