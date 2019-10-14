package com.tg.setting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.tg.coloursteward.R;
import com.tg.setting.entity.KeyCardKeysEntity;

import java.util.List;

/***
 *
 *门禁卡下面对应的要死列表
 */

public class KeyCardKeysRecordAdapter extends RecyclerView.Adapter<KeyCardKeysRecordAdapter.DefaultViewHolder> {

    public List<KeyCardKeysEntity.ContentBeanX.ContentBean> mList;
    public Context mContext;

    public KeyCardKeysRecordAdapter(Context mContext, List<KeyCardKeysEntity.ContentBeanX.ContentBean> list) {
        this.mList = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public KeyCardKeysRecordAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_card_key, parent, false);
        return new DefaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyCardKeysRecordAdapter.DefaultViewHolder holder, int position) {
        KeyCardKeysEntity.ContentBeanX.ContentBean contentBean = mList.get(position);
        holder.cb_card_key.setText(contentBean.getAccessName());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder {

        CheckBox cb_card_key;

        DefaultViewHolder(View itemView) {
            super(itemView);
            cb_card_key = itemView.findViewById(R.id.cb_card_key);
        }
    }

}
