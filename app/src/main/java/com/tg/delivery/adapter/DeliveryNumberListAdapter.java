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
import com.tg.delivery.activity.DeliveryScannerActivity;
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
public class DeliveryNumberListAdapter extends RecyclerView.Adapter<DeliveryNumberListAdapter.DefaultViewHolder> {

    private Context activity;
    private List<String> deliveryList;
    private List<String> phoneList;
    private OnItemClickListener onClickListener;
    private int editPos=-1;

    public  void  setEditStatus(int editPos){
        this.editPos=editPos;
        notifyDataSetChanged();
    }

    public DeliveryNumberListAdapter(Activity activity, List<String> deliveryList, List<String> phoneList) {
        this.activity = activity;
        this.deliveryList = deliveryList;
        this.phoneList = phoneList;
    }

    @NonNull
    @Override
    public DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_scancode_content, parent, false);
        DefaultViewHolder defaultViewHolder = new DefaultViewHolder(view);
        defaultViewHolder.onClickListener = onClickListener;
        return defaultViewHolder;

    }
    public void setOnItemClickListener(OnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    @Override
    public void onBindViewHolder(@NonNull DefaultViewHolder defaultViewHolder, int position) {
        defaultViewHolder.tv_tracking_nonum.setText(deliveryList.get(position));
        defaultViewHolder.tv_tracking_phonenum.setText(phoneList.get(position));
        if (editPos==position){
            defaultViewHolder.itemView.setBackgroundResource(R.drawable.bg_delivery_item_select);
            defaultViewHolder.iv_tracking_del.setVisibility(View.GONE);
            defaultViewHolder.iv_tracking_edit.setVisibility(View.GONE);
            defaultViewHolder.iv_tracking_cancel.setVisibility(View.VISIBLE);
        }else{
            defaultViewHolder.itemView.setBackgroundResource(R.drawable.bg_delivery_item_default);
            defaultViewHolder.iv_tracking_del.setVisibility(View.VISIBLE);
            defaultViewHolder.iv_tracking_edit.setVisibility(View.VISIBLE);
            defaultViewHolder.iv_tracking_cancel.setVisibility(View.GONE);
        }
        defaultViewHolder.iv_tracking_del.setOnClickListener(view -> {
            ((DeliveryScannerActivity) activity).delDeliveryItem(position);
        });
        defaultViewHolder.iv_tracking_cancel.setOnClickListener(view -> {
            ((DeliveryScannerActivity) activity).cancelDeliveryItem();
        });
        defaultViewHolder.iv_tracking_edit.setOnClickListener(view -> {
            ((DeliveryScannerActivity) activity).editDeliveryItem(position);
        });
    }


     class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iv_tracking_del;
        private ImageView iv_tracking_edit;
        private TextView iv_tracking_cancel;
        private TextView tv_delivery_name;
        private TextView tv_tracking_nonum;
        private TextView tv_tracking_phonenum;
        OnItemClickListener onClickListener;
        public DefaultViewHolder(View itemView) {
            super(itemView);
            iv_tracking_del = itemView.findViewById(R.id.iv_tracking_del);
            iv_tracking_edit = itemView.findViewById(R.id.iv_tracking_edit);
            iv_tracking_cancel = itemView.findViewById(R.id.iv_tracking_cancel);
            tv_delivery_name = itemView.findViewById(R.id.tv_delivery_name);
            tv_tracking_nonum = itemView.findViewById(R.id.tv_tracking_nonum);
            tv_tracking_phonenum = itemView.findViewById(R.id.tv_tracking_phonenum);
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
        return deliveryList == null ? 0 : deliveryList.size();
    }
}
