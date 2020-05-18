package com.tg.delivery.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tg.coloursteward.R;
import com.tg.coloursteward.inter.MicroApplicationCallBack;
import com.tg.delivery.entity.DeliveryHomeEntity;

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
public class DeliveryManagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<DeliveryHomeEntity.ContentBean.DataBean> list = new ArrayList<>();
    private final int item_micro_application_title = 1002;//导航标题
    private final int item_micro_application_content = 1003;
    private MicroApplicationCallBack callBack;

    public void setCallBack(MicroApplicationCallBack mcallBack) {
        this.callBack = mcallBack;
    }

    public void setData(List<DeliveryHomeEntity.ContentBean.DataBean> mlist) {
        this.list = mlist;
        notifyDataSetChanged();
    }

    public DeliveryManagerAdapter(Context mcontext, List<DeliveryHomeEntity.ContentBean.DataBean> mlist) {
        this.context = mcontext;
        this.list = mlist;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == item_micro_application_title) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_delivery_title, null);
            DeliveryManagerAdapter.TinyServerFragmentViewHolder_two holder_two = new DeliveryManagerAdapter.TinyServerFragmentViewHolder_two(view);
            return holder_two;
        } else if (viewType == item_micro_application_content) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_delivery_content, null);
            DeliveryManagerAdapter.TinyServerFragmentViewHolder_three holder_three = new DeliveryManagerAdapter.TinyServerFragmentViewHolder_three(view);
            return holder_three;
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (!TextUtils.isEmpty(list.get(position).getItem_name())) {
            return item_micro_application_title;//导航标题
        } else {
            return item_micro_application_content;//常用、其他 应用
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DeliveryManagerAdapter.TinyServerFragmentViewHolder_two) {
            DeliveryManagerAdapter.TinyServerFragmentViewHolder_two holder_two = (DeliveryManagerAdapter.TinyServerFragmentViewHolder_two) holder;
            holder_two.showTitle.setText(list.get(position).getItem_name());
        } else if (holder instanceof DeliveryManagerAdapter.TinyServerFragmentViewHolder_three) {
            DeliveryManagerAdapter.TinyServerFragmentViewHolder_three holder_three = (DeliveryManagerAdapter.TinyServerFragmentViewHolder_three) holder;
            Glide.with(context)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.drawable.placeholder2))
                    .load(list.get(position).getImg_url()).into(holder_three.icon);
            holder_three.showName.setText(list.get(position).getName());
            if (position % 2 == 0) {
                holder_three.view_delivery_mid.setVisibility(View.GONE);
            }


            if (null != callBack) {
                holder_three.rl_fragmentthree.setOnClickListener(v -> callBack.onclick(
                        position, list.get(position).getRedirect_url(), list.get(position).getAuth_type()));
            }
        }
    }

    private class TinyServerFragmentViewHolder_two extends RecyclerView.ViewHolder {
        private TextView showTitle;

        public TinyServerFragmentViewHolder_two(View itemView) {
            super(itemView);
            showTitle = itemView.findViewById(R.id.rv_delivery_title);
        }
    }

    private class TinyServerFragmentViewHolder_three extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView showName;
        private ConstraintLayout rl_fragmentthree;
        private View view_delivery_mid;

        public TinyServerFragmentViewHolder_three(View itemView) {
            super(itemView);
            rl_fragmentthree = itemView.findViewById(R.id.cl_delivery);
            icon = itemView.findViewById(R.id.iv_delivery_icon);
            showName = itemView.findViewById(R.id.iv_delivery_name);
            view_delivery_mid = itemView.findViewById(R.id.view_delivery_mid);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}
