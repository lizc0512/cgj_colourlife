package com.youmai.hxsdk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.entity.RedStatusItem;

import java.util.List;


public class RedStatusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<RedStatusItem> mList;

    public RedStatusAdapter(Context context) {
        mContext = context;
    }

    public void setList(List<RedStatusItem> list) {
        mList = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.hx_red_item, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        RedStatusItem item = mList.get(position);

        ImageView img_head = ((TextViewHolder) viewHolder).img_head;
        TextView tv_name = ((TextViewHolder) viewHolder).tv_name;
        TextView tv_time = ((TextViewHolder) viewHolder).tv_time;
        TextView tv_money = ((TextViewHolder) viewHolder).tv_money;


    }


    private class TextViewHolder extends RecyclerView.ViewHolder {
        ImageView img_head;
        TextView tv_name;
        TextView tv_time;
        TextView tv_money;

        private TextViewHolder(View itemView) {
            super(itemView);
            img_head = (ImageView) itemView.findViewById(R.id.img_head);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_money = (TextView) itemView.findViewById(R.id.tv_money);
        }

    }


}

