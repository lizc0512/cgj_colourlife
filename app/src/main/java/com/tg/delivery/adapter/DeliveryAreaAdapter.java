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
import com.tg.delivery.entity.DeliveryUserInfoEntitiy;

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
public class DeliveryAreaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<DeliveryUserInfoEntitiy.ContentBean.CommunityBean> list = new ArrayList<>();
    private MicroApplicationCallBack callBack;


    public void setCallBack(MicroApplicationCallBack mcallBack) {
        this.callBack = mcallBack;
    }

    public void setData(List<DeliveryUserInfoEntitiy.ContentBean.CommunityBean> mlist) {
        this.list = mlist;
        notifyDataSetChanged();
    }

    public DeliveryAreaAdapter(Context mcontext, List<DeliveryUserInfoEntitiy.ContentBean.CommunityBean> mlist) {
        this.context = mcontext;
        this.list = mlist;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_delivery_area, null);
        WareHouseViewHolder holder = new WareHouseViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WareHouseViewHolder) {
            WareHouseViewHolder mHolder = (WareHouseViewHolder) holder;
            mHolder.tv_delivery_area.setText(list.get(position).getCommunityName());

            mHolder.rl_delivery_area.setOnClickListener(v -> {
                if (callBack != null) {
                    callBack.onclick(position, "", "");
                }
            });

        }
    }

    private class WareHouseViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_delivery_area;
        private RelativeLayout rl_delivery_area;

        public WareHouseViewHolder(View itemView) {
            super(itemView);
            tv_delivery_area = itemView.findViewById(R.id.tv_delivery_area);
            rl_delivery_area = itemView.findViewById(R.id.rl_delivery_area);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}
