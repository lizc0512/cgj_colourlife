package com.tg.setting.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.tg.coloursteward.R;
import com.tg.setting.activity.CardSenderActivity;
import com.tg.setting.entity.KeyBagsEntity;

import java.util.List;

/**
 * 发卡器下面对应的门禁
 */
public class CardKeysBagAdapter extends RecyclerView.Adapter<CardKeysBagAdapter.DefaultViewHolder> {

    public List<KeyBagsEntity.ContentBeanX.ContentBean> keyList;
    public Context mContext;

    private List<String> checkIdList;

    public CardKeysBagAdapter(Context mContext, List<KeyBagsEntity.ContentBeanX.ContentBean> keyList) {
        this.keyList = keyList;
        this.mContext = mContext;
    }

    public void setCheckIdList(List<String> checkIdList) {
        this.checkIdList = checkIdList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CardKeysBagAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_card_key, parent, false);
        return new CardKeysBagAdapter.DefaultViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CardKeysBagAdapter.DefaultViewHolder holder, int position) {
        final KeyBagsEntity.ContentBeanX.ContentBean bean = keyList.get(position);
        holder.cb_card_key.setText(bean.getPackageName());
        holder.cb_card_key.setButtonDrawable(R.drawable.shape_key_select_card);
        String accessId = bean.getId();
        holder.cb_card_key.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkIdList.add(accessId);
                    ((CardSenderActivity) mContext).setChoiceKeyNumbers(0,accessId,"",bean.getAccessList());
                } else {
                    checkIdList.remove(accessId);
                    ((CardSenderActivity) mContext).setChoiceKeyNumbers(1, accessId,"",bean.getAccessList());
                }

            }
        });
        if (checkIdList.contains(accessId)) {
            holder.cb_card_key.setChecked(true);
        } else {
            holder.cb_card_key.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return keyList == null ? 0 : keyList.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder {
        public CheckBox cb_card_key;

        DefaultViewHolder(View itemView) {
            super(itemView);
            cb_card_key = itemView.findViewById(R.id.cb_card_key);
        }
    }
}
