package com.tg.coloursteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.List;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BeeBaseAdapter;
import com.tg.coloursteward.info.AuthorizationListResp;

public class DoorAutorAdapter extends BeeBaseAdapter {
    LayoutInflater mInflater = null;

    public DoorAutorAdapter(Context context, List<AuthorizationListResp> list) {
        this.mContext = context;
        this.dataList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    protected BeeCellHolder createCellHolder(View cellView) {
        Holder holder = new Holder();
        holder.txt_mobile = (TextView) cellView.findViewById(R.id.txt_mobile);
        holder.txt_status = (TextView) cellView.findViewById(R.id.txt_status);
        holder.txt_date = (TextView) cellView.findViewById(R.id.txt_date);
        holder.txt_name = (TextView) cellView.findViewById(R.id.txt_name);
        holder.rl_name = (RelativeLayout) cellView.findViewById(R.id.rl_name);
        return holder;
    }

    @Override
    protected View bindData(int position, View cellView, ViewGroup parent, BeeCellHolder h) {
        AuthorizationListResp authorizationListResp = (AuthorizationListResp) dataList.get(position);
        Holder holder = (Holder) h;

        String name = authorizationListResp.getToname();
        String statuString = "";


        String type = authorizationListResp.getType();
        String isdelete = authorizationListResp.getIsdeleted();
        if ("1".equals(type)) {

            // 默认 未批复
            if ("1".equals(isdelete)) {
                holder.txt_status.setTextColor(mContext.getResources()
                        .getColor(R.color.black));
                statuString = "拒绝";
            } else {
                // 未批复
                holder.txt_status.setTextColor(mContext.getResources()
                        .getColor(R.color.red));
                statuString = "未批复";
            }
        } else if ("2".equals(type)) {

            if ("1".equals(isdelete)) {
                // 已失效
                holder.txt_status.setTextColor(mContext.getResources()
                        .getColor(R.color.black));
                statuString = "已失效";
            } else if ("2".equals(type)) {
                // 已批复
                String auType = authorizationListResp.getUsertype();
                // 绿色
                holder.txt_status.setTextColor(mContext.getResources()
                        .getColor(R.color.lightgreen));

                statuString = "通过";

            }
        }
        String date = authorizationListResp.getCreationtime() + "000";
        String dateTime = "";
        try {
            Long dateLong = Long.parseLong(date);

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            dateTime = df.format(dateLong);
        } catch (Exception e) {
        }

        holder.txt_mobile.setText(name);
        holder.txt_status.setText(statuString);
        holder.txt_date.setText(dateTime);
        holder.txt_name.setText(authorizationListResp.getName());
        if (authorizationListResp.getName() == null) {
            holder.rl_name.setVisibility(View.GONE);
        } else {
            holder.rl_name.setVisibility(View.VISIBLE);
        }

        return cellView;
    }


    @Override
    public View createCellView() {
        return mInflater.inflate(R.layout.door_author_item, null);
    }

    public class Holder extends BeeCellHolder {
        public TextView txt_mobile;//电话号码
        public TextView txt_status;//状态
        public TextView txt_date;//时间
        private TextView txt_name;//小区
        private RelativeLayout rl_name;
    }
}
