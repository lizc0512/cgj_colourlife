package com.tg.coloursteward.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tg.coloursteward.R;
import com.tg.coloursteward.entity.MicroDataEntity;

import java.util.List;

/**
 * @name ${lizc}
 * @class name：com.tg.coloursteward.adapter
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/7/22 11:17
 * @change
 * @chang time
 * @class 微服务页面适配器
 */
public class MicroVpAdapter extends BaseQuickAdapter<MicroDataEntity.ContentBean, BaseViewHolder> {
    private Context mContext;

    public MicroVpAdapter(Context context, int layoutResId, @Nullable List<MicroDataEntity.ContentBean> data) {
        super(layoutResId, data);
        this.mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, MicroDataEntity.ContentBean item) {
        RelativeLayout rl_micro_vp_data = helper.getView(R.id.rl_micro_vp_data);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        layoutParams.width = dm.widthPixels / 3;
        rl_micro_vp_data.setLayoutParams(layoutParams);
        helper.setText(R.id.tv_micro_date_name, item.getName());
        helper.setText(R.id.tv_micro_date_content, item.getData());
    }
}
