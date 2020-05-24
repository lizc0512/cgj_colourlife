package com.tg.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.entity.CropListEntity;
import com.tg.coloursteward.inter.TransferCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.user.adapter
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2020/5/23 10:06
 * @change
 * @chang time
 * @class describe
 */
public class ChangeCompanyAdapter extends RecyclerView.Adapter<ChangeCompanyAdapter.Holder> {

    private List<CropListEntity.ContentBean> list = new ArrayList<>();
    private Context context;
    private TransferCallBack transferCallBack;

    public ChangeCompanyAdapter(Context mContext, List<CropListEntity.ContentBean> list) {
        this.list = list;
        this.context = mContext;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_change_company, null);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.tv_title.setText(list.get(position).getName());
        holder.callBack = transferCallBack;
        if (list.get(position).getIs_default().equals("1")) {
            holder.iv_show_done.setVisibility(View.VISIBLE);
        } else {
            holder.iv_show_done.setVisibility(View.GONE);
        }
    }

    public void setCallBack(TransferCallBack back) {
        this.transferCallBack = back;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tv_title;
        private TransferCallBack callBack;
        private ImageView iv_show_done;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_company);
            iv_show_done = itemView.findViewById(R.id.iv_show_done);
            tv_title.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != callBack) {
                callBack.onclick(getAdapterPosition());
            }
        }
    }
}
