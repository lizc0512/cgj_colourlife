package com.tg.delivery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.inter.MicroApplicationCallBack;
import com.tg.delivery.entity.WareHouseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.delivery.adapter
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2020/5/15 15:40
 * @change
 * @chang time
 * @class describe
 */
public class WareHouseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<WareHouseEntity> list = new ArrayList<>();
    private MicroApplicationCallBack delCallBack;
    private MicroApplicationCallBack editCallBack;
    private MicroApplicationCallBack cancelCallBack;

    private int editPos = -1;

    public void setEditStatus(int editPos) {
        this.editPos = editPos;
        notifyDataSetChanged();
    }

    public void setDelCallBack(MicroApplicationCallBack mcallBack) {
        this.delCallBack = mcallBack;
    }

    public void setEditCallBack(MicroApplicationCallBack mcallBack) {
        this.editCallBack = mcallBack;
    }

    public void setCancelCallBack(MicroApplicationCallBack mcallBack) {
        this.cancelCallBack = mcallBack;
    }

    public void setData(List<WareHouseEntity> mlist) {
        this.list = mlist;
        notifyDataSetChanged();
    }

    public WareHouseAdapter(Context mcontext, List<WareHouseEntity> mlist) {
        this.context = mcontext;
        this.list = mlist;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_warehouse_content, null);
        WareHouseViewHolder holder = new WareHouseViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WareHouseViewHolder) {
            WareHouseViewHolder mHolder = (WareHouseViewHolder) holder;
            mHolder.tv_warehouse_nonum.setText(list.get(position).getCourierNumber());
            mHolder.tv_warehouse_phonenum.setText(list.get(position).getRecipientMobile());
            mHolder.tv_warehouse_name.setText(list.get(position).getCourierCompany());

            if (editPos == position) {
                mHolder.rl_warehouse_item.setBackgroundResource(R.drawable.bg_delivery_item_select);
                mHolder.iv_warehouse_item_del.setVisibility(View.GONE);
                mHolder.iv_warehouse_item_edit.setVisibility(View.GONE);
                mHolder.iv_warehouse_cancel.setVisibility(View.VISIBLE);
            } else {
                mHolder.rl_warehouse_item.setBackgroundResource(R.drawable.bg_delivery_item_default);
                mHolder.iv_warehouse_item_del.setVisibility(View.VISIBLE);
                mHolder.iv_warehouse_item_edit.setVisibility(View.VISIBLE);
                mHolder.iv_warehouse_cancel.setVisibility(View.GONE);
            }

            mHolder.iv_warehouse_item_del.setOnClickListener(v -> {
                if (delCallBack != null) {
                    delCallBack.onclick(position, "", "");
                }
            });
            mHolder.iv_warehouse_item_edit.setOnClickListener(v -> {
                if (editCallBack != null) {
                    editCallBack.onclick(position, "", "");
                }
            });
            mHolder.iv_warehouse_cancel.setOnClickListener(view -> {
                if (cancelCallBack != null) {
                    cancelCallBack.onclick(position, "", "");
                }

            });

        }
    }

    private class WareHouseViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_warehouse_nonum;
        private TextView tv_warehouse_phonenum;
        private TextView iv_warehouse_cancel;
        private TextView tv_warehouse_name;
        private ImageView iv_warehouse_item_del;
        private ImageView iv_warehouse_item_edit;
        private RelativeLayout rl_warehouse_item;

        public WareHouseViewHolder(View itemView) {
            super(itemView);
            tv_warehouse_nonum = itemView.findViewById(R.id.tv_warehouse_nonum);
            tv_warehouse_phonenum = itemView.findViewById(R.id.tv_warehouse_phonenum);
            iv_warehouse_cancel = itemView.findViewById(R.id.iv_warehouse_cancel);
            tv_warehouse_name = itemView.findViewById(R.id.tv_warehouse_name);
            iv_warehouse_item_del = itemView.findViewById(R.id.iv_warehouse_item_del);
            iv_warehouse_item_edit = itemView.findViewById(R.id.iv_warehouse_item_edit);
            rl_warehouse_item = itemView.findViewById(R.id.rl_warehouse_item);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}
