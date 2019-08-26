package com.tg.setting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.setting.activity.KeyDoorManagerActivity;
import com.tg.setting.entity.KeyBagsEntity;
import com.tg.setting.util.OnItemClickListener;

import java.util.List;

public class KeyKeyAdapter extends RecyclerView.Adapter<KeyKeyAdapter.DefaultViewHolder> {

    public List<KeyBagsEntity.ContentBeanX.ContentBean> keyList;
    public Context mContext;
    private OnItemClickListener onClickListener;

    public KeyKeyAdapter(Context mContext, List<KeyBagsEntity.ContentBeanX.ContentBean> keyList) {
        this.keyList = keyList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public KeyKeyAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_key_key, parent, false);
        DefaultViewHolder defaultViewHolder = new DefaultViewHolder(view);
        defaultViewHolder.onClickListener = onClickListener;
        return new DefaultViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull KeyKeyAdapter.DefaultViewHolder holder, int position) {
        KeyBagsEntity.ContentBeanX.ContentBean contentBeanX = keyList.get(position);
        String door_name = contentBeanX.getPackageName();
        holder.tv_name.setText(door_name);
        List<KeyBagsEntity.ContentBeanX.ContentBean.AccessListBean> accessListBeanList = contentBeanX.getAccessList();
        StringBuffer stringBuffer = new StringBuffer();
        if (null != accessListBeanList && accessListBeanList.size() > 0) {
            for (KeyBagsEntity.ContentBeanX.ContentBean.AccessListBean accessListBean : accessListBeanList) {
                stringBuffer.append(accessListBean.getAccessName());
                stringBuffer.append(",");
            }
            String allName = stringBuffer.toString();
            holder.door_name.setText(allName.substring(0, allName.length() - 1));
        }
        holder.tv_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContext instanceof KeyDoorManagerActivity) {
                    ((KeyDoorManagerActivity) mContext).toSendPackge(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return keyList == null ? 0 : keyList.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_name;
        TextView door_name;
        TextView tv_todo;
        OnItemClickListener onClickListener;

        DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_name = itemView.findViewById(R.id.tv_name);
            door_name = itemView.findViewById(R.id.door_name);
            tv_todo = itemView.findViewById(R.id.tv_todo);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

}
