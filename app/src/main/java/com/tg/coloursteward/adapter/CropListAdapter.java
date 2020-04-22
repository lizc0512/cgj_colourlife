package com.tg.coloursteward.adapter;

import android.content.Context;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tg.coloursteward.R;
import com.tg.coloursteward.entity.CropListEntity;

import java.util.List;

/**
 * @name ${lizc}
 * @class name：com.patrol.adapter
 * @class 巡逻防控适配器
 * @anthor ${lizc} QQ:510906433
 * @time 2019/1/18 21:39
 * @change
 * @chang time
 * @class describe
 */
public class CropListAdapter extends BaseQuickAdapter<CropListEntity.ContentBean, BaseViewHolder> {
    private Context mContext;
    private List<CropListEntity.ContentBean> list;

    public CropListAdapter(Context context, int layoutResId, @Nullable List<CropListEntity.ContentBean> data) {
        super(layoutResId, data);
        mContext = context;
        list = data;
    }

    @Override
    protected void convert(BaseViewHolder helper, CropListEntity.ContentBean item) {
        helper.setText(R.id.tv_croplist_name, item.getName());
        TextView textView = helper.getView(R.id.tv_croplist_name);
        if (item.getIs_default().equals("1")) {
            textView.setTextColor(mContext.getResources().getColor(R.color.color_1890ff));
            helper.setImageResource(R.id.iv_croplist_check, R.drawable.cx_croplist_ok);
        } else {
            textView.setTextColor(mContext.getResources().getColor(R.color.color_445056));
            helper.getView(R.id.iv_croplist_check).setVisibility(View.GONE);
        }
    }
}
