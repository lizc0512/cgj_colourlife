package com.tg.delivery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    public void setDelCallBack(MicroApplicationCallBack mcallBack) {
        this.delCallBack = mcallBack;
    }

    public void setEditCallBack(MicroApplicationCallBack mcallBack) {
        this.editCallBack = mcallBack;
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
            mHolder.iv_warehouse_item_del.setOnClickListener(v -> {
                if (delCallBack != null) {
                    delCallBack.onclick(position, "", "");
                }
            });
            mHolder.iv_warehouse_item_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editCallBack != null) {
                        editCallBack.onclick(position, "", "");
                    }
                }
            });

        }
    }

    private class WareHouseViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_warehouse_nonum;
        private TextView tv_warehouse_phonenum;
        private ImageView iv_warehouse_item_del;
        private ImageView iv_warehouse_item_edit;

        public WareHouseViewHolder(View itemView) {
            super(itemView);
            tv_warehouse_nonum = itemView.findViewById(R.id.tv_warehouse_nonum);
            tv_warehouse_phonenum = itemView.findViewById(R.id.tv_warehouse_phonenum);
            iv_warehouse_item_del = itemView.findViewById(R.id.iv_warehouse_item_del);
            iv_warehouse_item_edit = itemView.findViewById(R.id.iv_warehouse_item_edit);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}
