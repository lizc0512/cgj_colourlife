package com.youmai.hxsdk.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.entity.MccCode;
import com.youmai.hxsdk.view.MaterialRippleLayout;

import java.util.List;

/**
 * 区号适配器
 * Created by fylder on 2017/1/5.
 */
public class MccAdapter extends RecyclerView.Adapter {

    OnClickListener listener;

    List<MccCode> mccCodes;
    Context context;

    public MccAdapter(Context context, List<MccCode> mccCodes, OnClickListener listener) {
        this.context = context;
        this.mccCodes = mccCodes;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.hxm_login_spinner_item_lay, parent, false);
        return new MccViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MccCode data = mccCodes.get(position);
        MccViewHolder viewHolder = (MccViewHolder) holder;
        viewHolder.codeText.setText(mccCodes.get(position).getCode());
        viewHolder.countryText.setText(mccCodes.get(position).getCountry());
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = data.getCode();
                listener.onClick(code);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mccCodes == null ? 0 : mccCodes.size();
    }

    class MccViewHolder extends RecyclerView.ViewHolder {

        MaterialRippleLayout cardView;
        TextView countryText;
        TextView codeText;

        public MccViewHolder(View itemView) {
            super(itemView);
            cardView = (MaterialRippleLayout) itemView.findViewById(R.id.item_mcc_lay);
            countryText = (TextView) itemView.findViewById(R.id.item_mcc_country);
            codeText = (TextView) itemView.findViewById(R.id.item_mcc_code);
        }
    }

    public interface OnClickListener {
        void onClick(String code);
    }
}
