package com.tg.delivery.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tg.coloursteward.R;
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
public class DeliveryAddressListAdapter extends RecyclerView.Adapter<DeliveryAddressListAdapter.DefaultViewHolder> {

    private Context activity;
    private List<String> addressList;
    private OnItemClickListener onClickListener;
    private int clickPos=-1;

    public  void  setClickPos(int clickPos){
        this.clickPos=clickPos;
        notifyDataSetChanged();
    }

    public DeliveryAddressListAdapter(Activity activity, List<String> addressList) {
        this.activity = activity;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_delivery_address, parent, false);
        DefaultViewHolder defaultViewHolder = new DefaultViewHolder(view);
        defaultViewHolder.onClickListener = onClickListener;
        return defaultViewHolder;

    }
    public void setOnItemClickListener(OnItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    @Override
    public void onBindViewHolder(@NonNull DefaultViewHolder defaultViewHolder, int position) {
        defaultViewHolder.tv_address_name.setText(addressList.get(position));
        if (clickPos==position){
            defaultViewHolder.tv_address_name.setBackgroundResource(R.drawable.bg_delivery_address_select);
            defaultViewHolder.tv_address_name.setTextColor(Color.parseColor("#597EF7"));
        }else{
            defaultViewHolder.tv_address_name.setBackgroundResource(R.drawable.bg_delivery_address_default);
            defaultViewHolder.tv_address_name.setTextColor(Color.parseColor("#a9afb8"));
        }
    }


     class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tv_address_name;
        OnItemClickListener onClickListener;
        public DefaultViewHolder(View itemView) {
            super(itemView);
            tv_address_name = itemView.findViewById(R.id.tv_address_name);
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
        return addressList == null ? 0 : addressList.size();
    }
}
