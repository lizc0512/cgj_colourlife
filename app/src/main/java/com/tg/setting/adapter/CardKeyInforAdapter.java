package com.tg.setting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.view.CircleImageView;
import com.tg.setting.entity.KeyCardInforEntity;
import com.tg.setting.util.OnItemClickListener;

import java.util.List;


public class CardKeyInforAdapter extends RecyclerView.Adapter<CardKeyInforAdapter.DefaultViewHolder> {

    public List<KeyCardInforEntity.ContentBeanX.ContentBean> mList;
    public Context mContext;
    private OnItemClickListener onClickListener;

    public CardKeyInforAdapter(Context mContext, List<KeyCardInforEntity.ContentBeanX.ContentBean> list) {
        this.mList = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public CardKeyInforAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_device_card_key, parent, false);
        DefaultViewHolder defaultViewHolder=new DefaultViewHolder(view);
        defaultViewHolder.onClickListener = onClickListener;
        return defaultViewHolder;
    }

    public void setOnItemClickListener(OnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull CardKeyInforAdapter.DefaultViewHolder holder, int position) {
        KeyCardInforEntity.ContentBeanX.ContentBean contentBean = mList.get(position);
        String avater = contentBean.getAvatar();
        if (!TextUtils.isEmpty(avater)) {
            GlideUtils.loadImageView(mContext, avater, holder.card_user_photo);
        }
        String screenName = contentBean.getScreenName();
        if (!TextUtils.isEmpty(screenName)) {
            holder.tv_card_username.setText(screenName);
        }
        String identityName = contentBean.getIdentityName();
        if (!TextUtils.isEmpty(identityName)) {
            holder.tv_card_identify.setText(identityName);
        } else {
            holder.tv_card_identify.setText("业主");
        }
        holder.tv_card_phone.setText(StringUtils.getHandlePhone(contentBean.getPhoneNumber()));

        holder.tv_card_date.setText(contentBean.getCreateTime());
        holder.tv_card_number.setText("卡号：" + contentBean.getCardNumber());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        CircleImageView card_user_photo;
        TextView tv_card_number;
        TextView tv_card_username;
        TextView tv_card_identify;
        TextView tv_card_phone;
        TextView tv_card_date;
        OnItemClickListener onClickListener;

        DefaultViewHolder(View itemView) {
            super(itemView);
            card_user_photo = itemView.findViewById(R.id.card_user_photo);
            tv_card_number = itemView.findViewById(R.id.tv_card_number);
            tv_card_username = itemView.findViewById(R.id.tv_card_username);
            tv_card_identify = itemView.findViewById(R.id.tv_card_identify);
            tv_card_phone = itemView.findViewById(R.id.tv_card_phone);
            tv_card_date = itemView.findViewById(R.id.tv_card_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

}
