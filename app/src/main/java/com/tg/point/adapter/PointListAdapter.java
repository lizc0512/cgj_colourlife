package com.tg.point.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.money.activity.WithDrawalActivity;
import com.tg.point.activity.GivenPointAmountActivity;
import com.tg.point.activity.GivenPointMobileActivity;
import com.tg.point.activity.GivenUserTypeActivity;
import com.tg.point.activity.PointTransactionListActivity;
import com.tg.point.entity.PointAccountListEntity;

import java.util.List;

import static com.tg.money.activity.WithDrawalActivity.DRAWALTYPE;
import static com.tg.money.activity.WithDrawalActivity.DRAWALTax;
import static com.tg.money.activity.WithDrawalActivity.FPMONEY;
import static com.tg.money.activity.WithDrawalActivity.PANO;
import static com.tg.point.activity.PointTransactionListActivity.POINTTITLE;
import static com.tg.point.activity.PointTransactionListActivity.POINTTPANO;


public class PointListAdapter extends RecyclerView.Adapter<PointListAdapter.PointListViewHolder> {
    private List<PointAccountListEntity.ContentBean.ListBean> listBeanList;
    private String mobilePhone;


    public PointListAdapter(List<PointAccountListEntity.ContentBean.ListBean> listBeanList) {
        this.listBeanList = listBeanList;
    }

    public void setUserInfor(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    @NonNull
    @Override
    public PointListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_point_type, viewGroup, false);
        PointListViewHolder pointListViewHolder = new PointListViewHolder(view);
        return pointListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PointListViewHolder viewHolder, int i) {
        PointAccountListEntity.ContentBean.ListBean listBean = listBeanList.get(i);
        if ("1".equals(listBean.getPano_type())) {
            viewHolder.tv_point_return.setVisibility(View.GONE);
            viewHolder.layout_point_bg.setBackgroundResource(R.drawable.point_one_balance_bg);
        } else {
            viewHolder.tv_point_return.setVisibility(View.GONE);
            viewHolder.layout_point_bg.setBackgroundResource(R.drawable.point_two_balance_bg);
        }
        if ("1".equals(listBean.getWithdrawal())) {//是否支持提现,1：支持，2：不支持
            viewHolder.tv_point_whthdrawal.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tv_point_whthdrawal.setVisibility(View.GONE);
        }
        String pointTitle = listBean.getName();
        viewHolder.tv_point_title.setText(pointTitle);
        viewHolder.tv_point_total.setText(String.valueOf(listBean.getBalance() * 1.0f / 100));
        viewHolder.tv_point_details.setOnClickListener(v -> {
            Context mContext = viewHolder.itemView.getContext();
            Intent intent = new Intent(mContext, PointTransactionListActivity.class);
            intent.putExtra(POINTTITLE, pointTitle);
            intent.putExtra(POINTTPANO, listBean.getPano());
            mContext.startActivity(intent);

        });
        viewHolder.tv_point_given.setOnClickListener(v -> {
            if (!"1".equals(listBean.getCgj_fp())) {
                Context mContext = viewHolder.itemView.getContext();
                Intent it = new Intent(mContext, GivenPointMobileActivity.class);
                it.putExtra(POINTTPANO, listBean.getPano());
                it.putExtra(GivenPointAmountActivity.TYPE, "cgj-czy");
                it.putExtra(GivenPointMobileActivity.GIVENNAME, listBean.getName());
                mContext.startActivity(it);
            } else {
                Context mContext = viewHolder.itemView.getContext();
                Intent intent = new Intent(mContext, GivenUserTypeActivity.class);
                intent.putExtra(GivenPointAmountActivity.GIVENMOBILE, mobilePhone);
                intent.putExtra(POINTTPANO, listBean.getPano());
                intent.putExtra("givenname", listBean.getName());
                mContext.startActivity(intent);
            }
        });
        viewHolder.tv_point_whthdrawal.setOnClickListener(v -> {
            Context mContext = viewHolder.itemView.getContext();
            Intent intent = new Intent(mContext, WithDrawalActivity.class);
            intent.putExtra(POINTTPANO, listBean.getPano());
            intent.putExtra(DRAWALTYPE, "point");
            intent.putExtra(PANO, listBean.getPano());
            intent.putExtra(DRAWALTax, Float.valueOf(listBean.getWithdrawal_rate()));
            intent.putExtra(FPMONEY, String.valueOf(listBean.getBalance() * 1.0f / 100));
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listBeanList == null ? 0 : listBeanList.size();
    }


    class PointListViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout layout_point_bg;
        private TextView tv_point_title;
        private TextView tv_point_total;
        private TextView tv_point_details;
        private TextView tv_point_return;
        private TextView tv_point_whthdrawal;
        private TextView tv_point_given;


        public PointListViewHolder(View itemView) {
            super(itemView);
            layout_point_bg = itemView.findViewById(R.id.layout_point_bg);
            tv_point_title = itemView.findViewById(R.id.tv_point_title);
            tv_point_total = itemView.findViewById(R.id.tv_point_total);
            tv_point_details = itemView.findViewById(R.id.tv_point_details);
            tv_point_return = itemView.findViewById(R.id.tv_point_return);
            tv_point_whthdrawal = itemView.findViewById(R.id.tv_point_whthdrawal);
            tv_point_given = itemView.findViewById(R.id.tv_point_given);
        }
    }
}
