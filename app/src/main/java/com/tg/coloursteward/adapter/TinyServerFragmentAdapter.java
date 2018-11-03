package com.tg.coloursteward.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private final int item_tinyserverfragment_one = 1001;
    private final int item_tinyserverfragment_two = 1002;//导航标题
    private final int item_tinyserverfragment_three = 1003;
    private final int item_tinyserverfragment_four = 1004;
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
        if (viewType == item_tinyserverfragment_one) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_tinyserverfragment_one, null);
            TinyServerFragmentViewHolder_one holder_one = new TinyServerFragmentViewHolder_one(view);
            return holder_one;
        } else if (viewType == item_tinyserverfragment_two) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_tinyserverfragment_two, null);
            TinyServerFragmentViewHolder_two holder_two = new TinyServerFragmentViewHolder_two(view);
            return holder_two;
        } else if (viewType == item_tinyserverfragment_three) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_tinyserverfragment_three, null);
            TinyServerFragmentViewHolder_three holder_three = new TinyServerFragmentViewHolder_three(view);
            return holder_three;
        } else if (viewType == item_tinyserverfragment_four) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_tinyserverfragment_four, null);
            TinyServerFragmentViewHolder_four holder_four = new TinyServerFragmentViewHolder_four(view);
            return holder_four;
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (!TextUtils.isEmpty(list.get(position).getItem_name()) && list.get(position).getType() != 3) {
            return item_tinyserverfragment_two;
        } else if (TextUtils.isEmpty(list.get(position).getItem_name()) && list.get(position).getType() == 3) {
            return item_tinyserverfragment_one;
        } else {
            return item_tinyserverfragment_three;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        int type = getItemViewType(position);
        if (holder instanceof TinyServerFragmentViewHolder_one) {
            TinyServerFragmentViewHolder_one holder_one = (TinyServerFragmentViewHolder_one) holder;
            if (TextUtils.isEmpty(list.get(position).getQuantity())) {//无审批形式
                holder_one.icon.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .applyDefaultRequestOptions(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .error(R.drawable.placeholder2))
                        .load(list.get(position).getImg()).into(holder_one.icon);
            } else {//有审批数量
                holder_one.showNum.setText(list.get(position).getQuantity());
                holder_one.showNum.setVisibility(View.VISIBLE);
            }
            holder_one.showName.setTextColor(context.getResources().getColor(R.color.text_color1));
            holder_one.showName.setText(list.get(position).getName());
            if (null != callBack) {
                holder_one.ll_fragmentone.setOnClickListener(new View.OnClickListener() {
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
        } else if (holder instanceof TinyServerFragmentViewHolder_two) {
            TinyServerFragmentViewHolder_two holder_two = (TinyServerFragmentViewHolder_two) holder;
            if (position == 0) {
                holder_two.ll_tinyfragment_title.setVisibility(View.GONE);
            } else {
                holder_two.ll_tinyfragment_title.setVisibility(View.VISIBLE);
                holder_two.showTitle.setText(list.get(position).getItem_name());
            }
        } else if (holder instanceof TinyServerFragmentViewHolder_three) {
            TinyServerFragmentViewHolder_three holder_three = (TinyServerFragmentViewHolder_three) holder;
            Glide.with(context)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.drawable.placeholder2))
                    .load(list.get(position).getImg()).into(holder_three.icon);
            holder_three.showName.setText(list.get(position).getName());
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
        } else if (holder instanceof TinyServerFragmentViewHolder_four) {

        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    private class TinyServerFragmentViewHolder_one extends RecyclerView.ViewHolder {
        private TextView showNum;
        private TextView showName;
        private ImageView icon;
        private FrameLayout fl_fragment;
        private LinearLayout ll_fragmentone;

        public TinyServerFragmentViewHolder_one(View itemView) {
            super(itemView);
            showNum = itemView.findViewById(R.id.tv_fragmentone_nums);
            ll_fragmentone = itemView.findViewById(R.id.ll_fragmentone);
            showName = itemView.findViewById(R.id.tv_fragmentone_name);
            icon = itemView.findViewById(R.id.iv_fragmentone_icon);
            fl_fragment = itemView.findViewById(R.id.fl_fragmentone);
        }
    }

    private class TinyServerFragmentViewHolder_two extends RecyclerView.ViewHolder {
        private TextView showTitle;
        private LinearLayout ll_tinyfragment_title;

        public TinyServerFragmentViewHolder_two(View itemView) {
            super(itemView);
            showTitle = itemView.findViewById(R.id.tv_fragmenttwo_title);
            ll_tinyfragment_title = itemView.findViewById(R.id.ll_tinyfragment_title);
        }
    }

    private class TinyServerFragmentViewHolder_three extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView showName;
        private RelativeLayout rl_fragmentthree;

        public TinyServerFragmentViewHolder_three(View itemView) {
            super(itemView);
            rl_fragmentthree = itemView.findViewById(R.id.rl_fragmentthree);
            icon = itemView.findViewById(R.id.iv_fragmentthree_icon);
            showName = itemView.findViewById(R.id.tv_fragmentthree_name);
        }
    }

    private class TinyServerFragmentViewHolder_four extends RecyclerView.ViewHolder {
        private RelativeLayout rl_fragmentfour;

        public TinyServerFragmentViewHolder_four(View itemView) {
            super(itemView);
            rl_fragmentfour = itemView.findViewById(R.id.rl_fragmentfour);
        }
    }
}
