package com.tg.delivery.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tg.coloursteward.R;
import com.tg.delivery.entity.DeliverySmsTemplateEntity;
import com.tg.setting.util.OnItemClickListener;

import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.delivery.adapter
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2020/5/15 15:40
 * @change
 * @chang time
 * @class 扫码派件快递单号的adapter
 */
public class DeliveryMsgTemplateAdapter extends RecyclerView.Adapter<DeliveryMsgTemplateAdapter.DefaultViewHolder> {

    private Context activity;
    private List<DeliverySmsTemplateEntity.ContentBean.ListBean> templateMsgList;
    private OnItemClickListener onClickListener;
    private int clickPos=-1;

    public  void  setClickPos(int clickPos){
        this.clickPos=clickPos;
        notifyDataSetChanged();
    }

    public DeliveryMsgTemplateAdapter(Activity activity,List<DeliverySmsTemplateEntity.ContentBean.ListBean> templateMsgList) {
        this.activity = activity;
        this.templateMsgList = templateMsgList;

    }

    @NonNull
    @Override
    public DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_smscode_template, parent, false);
        DefaultViewHolder defaultViewHolder = new DefaultViewHolder(view);
        defaultViewHolder.onClickListener = onClickListener;
        return defaultViewHolder;

    }
    public void setOnItemClickListener(OnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    @Override
    public void onBindViewHolder(@NonNull DefaultViewHolder defaultViewHolder, int position) {
        DeliverySmsTemplateEntity.ContentBean.ListBean  listBean=templateMsgList.get(position);
        defaultViewHolder.tv_sms_title.setText(listBean.getSmsUserUemplatePlace());
        defaultViewHolder.tv_sms_content.setText(listBean.getSmsUserTemplateContent());
        if (clickPos==position){
            defaultViewHolder.iv_sms_choice.setImageResource(R.drawable.sms_template_choice);
        }else{
            defaultViewHolder.iv_sms_choice.setImageResource(R.drawable.sms_template_default);
        }
    }


     class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iv_sms_choice;
        private TextView tv_sms_title;
        private TextView tv_sms_content;

        OnItemClickListener onClickListener;
        public DefaultViewHolder(View itemView) {
            super(itemView);
            iv_sms_choice = itemView.findViewById(R.id.iv_sms_choice);
            tv_sms_title = itemView.findViewById(R.id.tv_sms_title);
            tv_sms_content = itemView.findViewById(R.id.tv_sms_content);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onClickListener != null) {
                onClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

    @Override
    public int getItemCount() {
        return templateMsgList == null ? 0 : templateMsgList.size();
    }
}
