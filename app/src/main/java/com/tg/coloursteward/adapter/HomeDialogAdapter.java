package com.tg.coloursteward.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.entity.HomeDialogEntitiy;
import com.tg.coloursteward.inter.FragmentMineCallBack;

import java.util.List;

/**
 * Created by Administrator on 2018/9/18.
 * 首页弹窗选项适配器
 *
 * @Description
 */

public class HomeDialogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<HomeDialogEntitiy.ContentBean.ButtonBean> list;
    private FragmentMineCallBack fragmentMineCallBack;

    public HomeDialogAdapter(Context context, List<HomeDialogEntitiy.ContentBean.ButtonBean> mList) {
        this.mContext = context;
        this.list = mList;
    }

    public void setFragmentMineCallBack(FragmentMineCallBack callBack) {
        this.fragmentMineCallBack = callBack;

    }

    public void setData(List<HomeDialogEntitiy.ContentBean.ButtonBean> mList) {
        this.list = mList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_home_dialog_layout, parent, false);
        HomeDialogViewHolder homeDialogViewHolder = new HomeDialogViewHolder(view);
        return homeDialogViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        String name = list.get(position).getTitle();
        String color = list.get(position).getColor();
        if (null != list && list.size() > 0 && position == list.size() - 1) {
            ((HomeDialogViewHolder) holder).tv_homedialog_line.setVisibility(View.GONE);
        }
        ((HomeDialogViewHolder) holder).btn_homedialog.setText(name);
        ViewGroup.LayoutParams layoutParams = ((HomeDialogViewHolder) holder).ll_homedialog.getLayoutParams();
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        layoutParams.width = dm.widthPixels / 10 * 7 / (list.size());
        ((HomeDialogViewHolder) holder).ll_homedialog.setLayoutParams(layoutParams);
        ((HomeDialogViewHolder) holder).btn_homedialog.setTextColor(Color.parseColor(color));
        ((HomeDialogViewHolder) holder).btn_homedialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != fragmentMineCallBack) {
                    fragmentMineCallBack.getData("", position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    private static class HomeDialogViewHolder extends RecyclerView.ViewHolder {

        private Button btn_homedialog;
        private TextView tv_homedialog_line;
        private LinearLayout ll_homedialog;

        public HomeDialogViewHolder(View itemView) {
            super(itemView);
            btn_homedialog = (Button) itemView.findViewById(R.id.btn_homedialog);
            tv_homedialog_line = (TextView) itemView.findViewById(R.id.tv_homedialog_line);
            ll_homedialog = (LinearLayout) itemView.findViewById(R.id.ll_homedialog);
        }
    }
}
