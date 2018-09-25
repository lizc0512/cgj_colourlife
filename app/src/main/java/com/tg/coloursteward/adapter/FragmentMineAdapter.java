package com.tg.coloursteward.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import com.tg.coloursteward.entity.FragmentMineEntity;
import com.tg.coloursteward.inter.FragmentMineCallBack;

import java.util.List;

/**
 * Created by Administrator on 2018/9/18.
 * 我的页面选项适配器
 *
 * @Description
 */

public class FragmentMineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<FragmentMineEntity.ContentBean.DataBean> list;
    private FragmentMineCallBack fragmentMineCallBack;

    public FragmentMineAdapter(Context context, List<FragmentMineEntity.ContentBean.DataBean> mList) {
        this.mContext = context;
        this.list = mList;
    }

    public void setFragmentMineCallBack(FragmentMineCallBack callBack) {
        this.fragmentMineCallBack = callBack;

    }

    public void setData(List<FragmentMineEntity.ContentBean.DataBean> mList) {
        this.list = mList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_fragmentmine, parent, false);
        FragmentMineViewHolder fragmentMineViewHolder = new FragmentMineViewHolder(view);
        return fragmentMineViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        String name = list.get(position).getName();
        ((FragmentMineViewHolder) holder).tv_name.setText(name);
        Glide.with(mContext).load(list.get(position).getImg())
                .apply(new RequestOptions()
                        .error(R.drawable.moren_geren)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(((FragmentMineViewHolder) holder).iv_logo);
        ((FragmentMineViewHolder) holder).rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != fragmentMineCallBack) {
                    fragmentMineCallBack.getData("", position);
                }
            }
        });
        int currentStr = list.get(position).getGroup_id();//获取ID
        int previewStr = (position - 1) >= 0 ? (list.get(position - 1).getGroup_id()) : 0;
        if (previewStr != (currentStr)) {
            ((FragmentMineViewHolder) holder).tv_fragmentmineline.setVisibility(View.VISIBLE);
            ((FragmentMineViewHolder) holder).view_fragmentmine.setVisibility(View.GONE);
        } else {
            ((FragmentMineViewHolder) holder).tv_fragmentmineline.setVisibility(View.GONE);//是，隐藏起来
            ((FragmentMineViewHolder) holder).view_fragmentmine.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    private static class FragmentMineViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_logo;
        private TextView tv_name;
        private TextView tv_fragmentmineline;
        private View view_fragmentmine;
        private RelativeLayout rl_item;

        public FragmentMineViewHolder(View itemView) {
            super(itemView);
            rl_item = (RelativeLayout) itemView.findViewById(R.id.rl_fragmentmine);
            iv_logo = (ImageView) itemView.findViewById(R.id.iv_fragmentmine);
            tv_name = (TextView) itemView.findViewById(R.id.tv_fragmentmine);
            view_fragmentmine = itemView.findViewById(R.id.view_fragmentmine);
            tv_fragmentmineline = (TextView) itemView.findViewById(R.id.tv_fragmentmineline);
        }
    }
}
