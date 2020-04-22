package com.tg.coloursteward.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.entity.WebviewRightEntity;
import com.tg.coloursteward.inter.FragmentMineCallBack;
import com.tg.coloursteward.util.GlideUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/9/18.
 * 首页弹窗选项适配器
 *
 * @Description
 */

public class WebviewPopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<WebviewRightEntity.DataBean> list;
    private FragmentMineCallBack fragmentMineCallBack;

    public WebviewPopAdapter(Context context, List<WebviewRightEntity.DataBean> mList) {
        this.mContext = context;
        this.list = mList;
    }

    public void setFragmentMineCallBack(FragmentMineCallBack callBack) {
        this.fragmentMineCallBack = callBack;

    }

    public void setData(List<WebviewRightEntity.DataBean> mList) {
        this.list = mList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_web_pop, parent, false);
        HomeDialogViewHolder homeDialogViewHolder = new HomeDialogViewHolder(view);
        return homeDialogViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        String name = list.get(position).getTitle();
        String img = list.get(position).getImg();
        String url = list.get(position).getUrl();
        ((HomeDialogViewHolder) holder).tv_web_pop.setText(name);
        if ("refresh_web".equals(url)) {
            GlideUtils.loadImageView(mContext, R.drawable.nav_refresh_normal, ((HomeDialogViewHolder) holder).iv_web_pop);
        } else if ("close_web".equals(url)) {
            GlideUtils.loadImageView(mContext, R.drawable.nav_shut_normal, ((HomeDialogViewHolder) holder).iv_web_pop);
        } else {
            GlideUtils.loadImageView(mContext, img, ((HomeDialogViewHolder) holder).iv_web_pop);
        }
        ((HomeDialogViewHolder) holder).rl_web_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != fragmentMineCallBack) {
                    fragmentMineCallBack.getData(url, position);
                }
            }
        });
        if (position == (list.size() - 1)) {
            ((HomeDialogViewHolder) holder).view_line.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    private static class HomeDialogViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_web_pop;
        private ImageView iv_web_pop;
        private View view_line;
        private RelativeLayout rl_web_pop;

        public HomeDialogViewHolder(View itemView) {
            super(itemView);
            tv_web_pop = itemView.findViewById(R.id.tv_web_pop);
            iv_web_pop = itemView.findViewById(R.id.iv_web_pop);
            view_line = itemView.findViewById(R.id.view_line);
            rl_web_pop = itemView.findViewById(R.id.rl_web_pop);
        }
    }
}
