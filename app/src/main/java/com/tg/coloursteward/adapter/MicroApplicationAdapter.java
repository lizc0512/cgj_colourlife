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
import com.tg.coloursteward.entity.CropLayoutEntity;
import com.tg.coloursteward.inter.MicroApplicationCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/07/17.
 * 微服务页面适配器
 *
 * @Description
 */

public class MicroApplicationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<CropLayoutEntity.ContentBeanX.ContentBean.DataBean> list = new ArrayList<>();
    private final int item_micro_application_title = 1002;//导航标题
    private final int item_micro_application_content = 1003;
    private MicroApplicationCallBack callBack;

    public void setCallBack(MicroApplicationCallBack mcallBack) {
        this.callBack = mcallBack;
    }

    public void setData(List<CropLayoutEntity.ContentBeanX.ContentBean.DataBean> mlist) {
        this.list = mlist;
        notifyDataSetChanged();
    }

    public MicroApplicationAdapter(Context mcontext, List<CropLayoutEntity.ContentBeanX.ContentBean.DataBean> mlist) {
        this.context = mcontext;
        this.list = mlist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == item_micro_application_title) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_micro_application_title, null);
            TinyServerFragmentViewHolder_two holder_two = new TinyServerFragmentViewHolder_two(view);
            return holder_two;
        } else if (viewType == item_micro_application_content) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_micro_application_content, null);
            TinyServerFragmentViewHolder_three holder_three = new TinyServerFragmentViewHolder_three(view);
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
                        .load(list.get(position).getImg_url()).into(holder_three.icon);
                holder_three.showName.setText(list.get(position).getName());
            } catch (Exception e) {
            }
            if (!TextUtils.isEmpty(list.get(position).getQuantity())) {
                if (Integer.parseInt(list.get(position).getQuantity()) > 0 && Integer.parseInt(list.get(position).getQuantity()) < 100) {
                    holder_three.tv_tinyserverfragment_num.setVisibility(View.VISIBLE);
                    holder_three.tv_tinyserverfragment_num.setText(list.get(position).getQuantity());
                    holder_three.tv_tinyserverfragment_num.setWidth(20);
                } else if (Integer.parseInt(list.get(position).getQuantity()) > 100) {
                    holder_three.tv_tinyserverfragment_num.setVisibility(View.VISIBLE);
                    holder_three.tv_tinyserverfragment_num.setText("99+");
                }
            }
            if (null != callBack) {
                holder_three.rl_fragmentthree.setOnClickListener(v -> callBack.onclick(
                        position, list.get(position).getRedirect_url(), list.get(position).getAuth_type()));
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
