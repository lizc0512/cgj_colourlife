package com.youmai.hxsdk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.entity.red.SendRedPacketList;
import com.youmai.hxsdk.utils.GlideRoundTransform;

import java.util.ArrayList;
import java.util.List;


public class SendRedPackageRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<SendRedPacketList.ContentBean> mList;

    public SendRedPackageRecordAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }


    public void setList(List<SendRedPacketList.ContentBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_send_history, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final SendRedPacketList.ContentBean item = mList.get(position);

        ImageView img_head = ((TextViewHolder) viewHolder).img_head;
        TextView tv_name = ((TextViewHolder) viewHolder).tv_name;
        TextView tv_time = ((TextViewHolder) viewHolder).tv_time;
        ImageView img_tag = ((TextViewHolder) viewHolder).img_tag;
        TextView tv_money = ((TextViewHolder) viewHolder).tv_money;
        TextView tv_best = ((TextViewHolder) viewHolder).tv_best;

        String avatar = item.getSenderHeadImgUrl();
        String sendName = item.getSenderName();
        String time = item.getTimeAllowWithdraw();
        String money = item.getMoneyDraw();
        int type = item.getLsType();

        int size = mContext.getResources().getDimensionPixelOffset(R.dimen.card_head);
        Glide.with(mContext).load(avatar)
                .apply(new RequestOptions()
                        .transform(new GlideRoundTransform())
                        .override(size, size)
                        .placeholder(R.drawable.color_default_header)
                        .error(R.drawable.color_default_header)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(img_head);

        tv_name.setText(sendName);
        tv_time.setText(time);

        String format2 = mContext.getResources().getString(R.string.red_packet_unit2);
        tv_money.setText(String.format(format2, String.valueOf(money)));

        if (type == 2) {
            img_tag.setVisibility(View.VISIBLE);
        } else {
            img_tag.setVisibility(View.INVISIBLE);
        }

    }


    private class TextViewHolder extends RecyclerView.ViewHolder {

        ImageView img_head;
        TextView tv_name;
        TextView tv_time;
        ImageView img_tag;
        TextView tv_money;
        TextView tv_best;


        private TextViewHolder(View itemView) {
            super(itemView);
            img_head = (ImageView) itemView.findViewById(R.id.img_head);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            img_tag = (ImageView) itemView.findViewById(R.id.img_tag);
            tv_money = (TextView) itemView.findViewById(R.id.tv_money);
            tv_best = (TextView) itemView.findViewById(R.id.tv_best);
        }

    }


}

