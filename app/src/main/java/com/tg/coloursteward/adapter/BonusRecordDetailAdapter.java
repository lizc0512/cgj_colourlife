package com.tg.coloursteward.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.entity.BonusRecordDetailEntity;

import java.util.List;

/**
 * @name ${lizc}
 * @class name：com.tg.coloursteward.adapter
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/7/4 10:09
 * @change
 * @chang time
 * @class describe 奖金包详情适配器
 */
public class BonusRecordDetailAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<BonusRecordDetailEntity.ContentBean.ListBean> list;

    private BonusCallBack callBack;

    public void setBonusCallBack(BonusCallBack mCallBack) {
        this.callBack = mCallBack;
    }

    public BonusRecordDetailAdapter(Context context, List<BonusRecordDetailEntity.ContentBean.ListBean> mlist) {
        this.mContext = context;
        this.list = mlist;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.bouns_record_detail_item, parent, false);
        BonusRecordDetailViewHolder viewHolde = new BonusRecordDetailViewHolder(view);
        return viewHolde;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BonusRecordDetailViewHolder bonusHolder = (BonusRecordDetailViewHolder) holder;
        bonusHolder.title.setText(list.get(position).getTitle());
        bonusHolder.value.setText(list.get(position).getValue());
        String url = list.get(position).getUrl();
        if (!TextUtils.isEmpty(url)) {
            bonusHolder.imageView.setVisibility(View.VISIBLE);
            bonusHolder.rl_bouns.setOnClickListener(v -> {
                if (null != callBack) {
                    callBack.result(url);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class BonusRecordDetailViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView value;
        private ImageView imageView;
        private RelativeLayout rl_bouns;

        public BonusRecordDetailViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.TextView_group);
            value = itemView.findViewById(R.id.tv_bonus_groupnum);
            imageView = itemView.findViewById(R.id.iv_next_group);
            rl_bouns = itemView.findViewById(R.id.rl_bouns);
        }
    }

    public interface BonusCallBack {
        void result(String url);
    }
}
