package com.tg.coloursteward.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.BonusRecordPersonalActivity;
import com.tg.coloursteward.GroupAccountDetailsActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.entity.RedPacketEntity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizc on 2018/6/15.
 *
 * @Description 我的饭票页面适配器
 */

public class RedPacketBonusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<RedPacketEntity.ContentBean> redPacketEntityList = new ArrayList<>();

    public RedPacketBonusAdapter(Context mContext, List<RedPacketEntity.ContentBean> list) {
        this.context = mContext;
        this.redPacketEntityList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myfp, parent, false);
        RedPacketBounsViewHolder viewHolder = new RedPacketBounsViewHolder(view);
        return viewHolder;
    }

    public void setData(List<RedPacketEntity.ContentBean> list) {
        this.redPacketEntityList = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((RedPacketBounsViewHolder) holder).tv_item_myfp_name.setText(redPacketEntityList.get(position).getOrgname());
        DecimalFormat df = new DecimalFormat("0.00");
        ((RedPacketBounsViewHolder) holder).tv_team_bonus_balance.setText(df.format(redPacketEntityList.get(position).getSummoney()));
        ((RedPacketBounsViewHolder) holder).rl_team_bonus_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (redPacketEntityList != null && redPacketEntityList.size() > 0) {
                    Intent intent = new Intent(context.getApplicationContext(), GroupAccountDetailsActivity.class);
                    intent.putExtra("familyUuid", redPacketEntityList.get(position).getOrguuid());
                    intent.putExtra("useruuid", redPacketEntityList.get(position).getUseruuid());
                    GroupAccountDetailsActivity.list_item = redPacketEntityList.get(position).getDbzhdata();
                    context.startActivity(intent);
                }
            }
        });
        ((RedPacketBounsViewHolder) holder).rl_personal_bonus_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), BonusRecordPersonalActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return redPacketEntityList == null ? 0 : redPacketEntityList.size();
    }

    private static class RedPacketBounsViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_item_myfp_name;
        private TextView tv_team_bonus_balance;
        private RelativeLayout rl_team_bonus_detail;
        private RelativeLayout rl_personal_bonus_detail;

        public RedPacketBounsViewHolder(View itemView) {
            super(itemView);
            tv_item_myfp_name = itemView.findViewById(R.id.tv_item_myfp_name);
            tv_team_bonus_balance = itemView.findViewById(R.id.tv_team_bonus_balance);
            rl_team_bonus_detail = itemView.findViewById(R.id.rl_team_bonus_detail);
            rl_personal_bonus_detail = itemView.findViewById(R.id.rl_personal_bonus_detail);
        }
    }
}
