package com.tg.point.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tg.coloursteward.R;
import com.tg.point.entity.UserIdInforEntity;

import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.point.adapter
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/12/10 11:26
 * @change
 * @chang time
 * @class describe
 */
public class MultiCollegeAdapter extends BaseQuickAdapter<UserIdInforEntity.ContentBean, BaseViewHolder> {
    public MultiCollegeAdapter(int layoutResId, List<UserIdInforEntity.ContentBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserIdInforEntity.ContentBean item) {
        helper.setText(R.id.tv_name, item.getName());
        helper.setText(R.id.tv_oa, "OA:" + item.getUsername());
        helper.setText(R.id.tv_job, item.getJob_name() + "-" + item.getOrg_name());
    }

}
