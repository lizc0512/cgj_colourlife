package com.tg.setting.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        holder.tv_card_key.setText(bean.getPackageName());
        String accessId = bean.getId();
        holder.check_card_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!checkIdList.contains(accessId)) {
                    checkIdList.add(accessId);
                    ((CardSenderActivity) mContext).setChoiceKeyNumbers(0, accessId, "", bean.getAccessList());
                } else {
                    checkIdList.remove(accessId);
                    ((CardSenderActivity) mContext).setChoiceKeyNumbers(1, accessId, "", bean.getAccessList());
                }
                notifyDataSetChanged();
            }
        });
        if (checkIdList.contains(accessId)) {
            holder.iv_card_key.setImageResource(R.drawable.icon_checked_key_bag);
        } else {
            holder.iv_card_key.setImageResource(R.drawable.icon_unchecked_key_bag);
        }
    }

    @Override
    public int getItemCount() {
        return keyList == null ? 0 : keyList.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout check_card_layout;
        public TextView tv_card_key;
        public ImageView iv_card_key;

        DefaultViewHolder(View itemView) {
            super(itemView);
            check_card_layout = itemView.findViewById(R.id.check_card_layout);
            tv_card_key = itemView.findViewById(R.id.tv_card_key);
            iv_card_key = itemView.findViewById(R.id.iv_card_key);
        }
    }
}
