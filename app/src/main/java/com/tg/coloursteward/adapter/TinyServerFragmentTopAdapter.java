package com.tg.coloursteward.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.entity.TinyFragmentTopEntity;
import com.tg.coloursteward.inter.TinyFragmentCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/29.
 * 微服务页面顶部适配器
 *
 * @Description
 */

public class TinyServerFragmentTopAdapter extends RecyclerView.Adapter<ViewHolder> {
    private Context context;
    private List<TinyFragmentTopEntity.ContentBean> list = new ArrayList<>();
    private TinyFragmentCallBack callBack;

    public void setCallBack(TinyFragmentCallBack mcallBack) {
        this.callBack = mcallBack;
    }

    public void setData(List<TinyFragmentTopEntity.ContentBean> mlist) {
        this.list = mlist;
        notifyDataSetChanged();
    }

    public TinyServerFragmentTopAdapter(Context mcontext, List<TinyFragmentTopEntity.ContentBean> mlist) {
        this.context = mcontext;
        this.list = mlist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tinyserverfragment_top, null);
        TinyServerFragmentViewHolder holder = new TinyServerFragmentViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        TinyServerFragmentViewHolder viewHolder = (TinyServerFragmentViewHolder) holder;
        viewHolder.title.setText(list.get(position).getTitle());
        viewHolder.explain.setText(list.get(position).getExplain());
        viewHolder.showNum.setText(list.get(position).getQuantity());
        if (null != callBack) {
            viewHolder.ll_fragmenttop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.onclick(position, list.get(position).getUrl(), list.get(position).getAuth_type());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    private class TinyServerFragmentViewHolder extends ViewHolder {
        private TextView title;
        private TextView explain;
        private TextView showNum;
        private LinearLayout ll_fragmenttop;

        public TinyServerFragmentViewHolder(View itemView) {
            super(itemView);
            ll_fragmenttop = itemView.findViewById(R.id.ll_fragmenttop);
            title = itemView.findViewById(R.id.tv_fragmenttop_title);
            explain = itemView.findViewById(R.id.tv_fragmenttop_explain);
            showNum = itemView.findViewById(R.id.tv_fragmenttop_num);
        }
    }
}
