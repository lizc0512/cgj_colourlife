package com.tg.delivery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.inter.MicroApplicationCallBack;

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
public class DeliveryCompanyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<String> list = new ArrayList<>();
    private MicroApplicationCallBack callBack;

    public void setDelCallBack(MicroApplicationCallBack mcallBack) {
        this.callBack = mcallBack;
    }

    public void setData(List<String> mlist) {
        this.list = mlist;
        notifyDataSetChanged();
    }

    public DeliveryCompanyAdapter(Context mcontext, List<String> mlist) {
        this.context = mcontext;
        this.list = mlist;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_warehouse_company, null);
        WareHouseViewHolder holder = new WareHouseViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WareHouseViewHolder) {
            WareHouseViewHolder mHolder = (WareHouseViewHolder) holder;
            mHolder.tv_warehouse_company.setText(list.get(position));
            mHolder.rl_warehouse_company.setOnClickListener(v -> {
                if (callBack != null) {
                    callBack.onclick(position, "", "");
                }
            });

        }
    }

    private class WareHouseViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_warehouse_company;
        private RelativeLayout rl_warehouse_company;

        public WareHouseViewHolder(View itemView) {
            super(itemView);
            tv_warehouse_company = itemView.findViewById(R.id.tv_warehouse_company);
            rl_warehouse_company = itemView.findViewById(R.id.rl_warehouse_company);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}
