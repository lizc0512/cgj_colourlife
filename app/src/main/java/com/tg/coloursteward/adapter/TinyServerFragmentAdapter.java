package com.tg.coloursteward.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tg.coloursteward.R;
import com.tg.coloursteward.entity.TinyServerFragmentEntity;
import com.tg.coloursteward.inter.TinyFragmentCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/29.
 * 微服务页面适配器
 *
 * @Description
 */

public class TinyServerFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<TinyServerFragmentEntity.ContentBean.DataBean> list = new ArrayList<>();
    private final int item_tinyserverfragment_two = 1002;//导航标题
    private final int item_tinyserverfragment_three = 1003;
    private TinyFragmentCallBack callBack;

    public void setCallBack(TinyFragmentCallBack mcallBack) {
        this.callBack = mcallBack;
    }

    public void setData(List<TinyServerFragmentEntity.ContentBean.DataBean> mlist) {
        this.list = mlist;
        notifyDataSetChanged();
    }

    public TinyServerFragmentAdapter(Context mcontext, List<TinyServerFragmentEntity.ContentBean.DataBean> mlist) {
        this.context = mcontext;
        this.list = mlist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == item_tinyserverfragment_two) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_tinyserverfragment_two, null);
            TinyServerFragmentViewHolder_two holder_two = new TinyServerFragmentViewHolder_two(view);
            return holder_two;
        } else if (viewType == item_tinyserverfragment_three) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_tinyserverfragment_three, null);
            TinyServerFragmentViewHolder_three holder_three = new TinyServerFragmentViewHolder_three(view);
            return holder_three;
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (!TextUtils.isEmpty(list.get(position).getItem_name()) && list.get(position).getType() != 3) {
            return item_tinyserverfragment_two;//导航标题
        } else {
            return item_tinyserverfragment_three;//常用、其他 应用
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (holder instanceof TinyServerFragmentViewHolder_two) {
            TinyServerFragmentViewHolder_two holder_two = (TinyServerFragmentViewHolder_two) holder;
            holder_two.showTitle.setText(list.get(position).getItem_name());
        } else if (holder instanceof TinyServerFragmentViewHolder_three) {
            TinyServerFragmentViewHolder_three holder_three = (TinyServerFragmentViewHolder_three) holder;
            try {
                Glide.with(context)
                        .applyDefaultRequestOptions(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .error(R.drawable.placeholder2))
                        .load(list.get(position).getImg()).into(holder_three.icon);
                holder_three.showName.setText(list.get(position).getName());
            } catch (Exception e) {
            }
            if (!TextUtils.isEmpty(list.get(position).getQuantity())) {
                if (Integer.parseInt(list.get(position).getQuantity()) > 0 && Integer.parseInt(list.get(position).getQuantity()) < 100) {
                    holder_three.tv_tinyserverfragment_num.setVisibility(View.VISIBLE);
                    holder_three.tv_tinyserverfragment_num.setText(list.get(position).getQuantity());
                } else if (Integer.parseInt(list.get(position).getQuantity()) > 100) {
                    holder_three.tv_tinyserverfragment_num.setVisibility(View.VISIBLE);
                    holder_three.tv_tinyserverfragment_num.setText("99+");
                }
            }
            if (null != callBack) {
                holder_three.rl_fragmentthree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(String.valueOf(list.get(position).getAuth_type()))) {
                            callBack.onclick(position, list.get(position).getUrl(), 1);
                        } else {
                            callBack.onclick(position, list.get(position).getUrl(), list.get(position).getAuth_type());
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    private class TinyServerFragmentViewHolder_two extends RecyclerView.ViewHolder {
        private TextView showTitle;

        public TinyServerFragmentViewHolder_two(View itemView) {
            super(itemView);
            showTitle = itemView.findViewById(R.id.tv_fragmenttwo_title);
        }
    }

    private class TinyServerFragmentViewHolder_three extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView showName;
        private TextView tv_tinyserverfragment_num;
        private RelativeLayout rl_fragmentthree;

        public TinyServerFragmentViewHolder_three(View itemView) {
            super(itemView);
            rl_fragmentthree = itemView.findViewById(R.id.rl_fragmentthree);
            icon = itemView.findViewById(R.id.iv_fragmentthree_icon);
            showName = itemView.findViewById(R.id.tv_fragmentthree_name);
            tv_tinyserverfragment_num = itemView.findViewById(R.id.tv_tinyserverfragment_num);
        }
    }
}
