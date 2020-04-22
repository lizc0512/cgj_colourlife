package com.tg.im.adapter;

import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.tg.coloursteward.R;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.IsTodayutil;
import com.tg.coloursteward.util.Tools;
import com.tg.im.entity.DeskTopEntity;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.im.adapter
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2019/11/4 11:04
 * @change
 * @chang time
 * @class 消息列表页面适配器
 */
public class DeskTopAdapter extends BaseQuickAdapter<DeskTopEntity.ContentBean.DataBean, BaseViewHolder> {

    public DeskTopAdapter(int layoutResId, @Nullable List<DeskTopEntity.ContentBean.DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeskTopEntity.ContentBean.DataBean item) {
        if (null == item) {
            return;
        }
        TextView tvTime = helper.getView(R.id.tv_time);
        if (item.getIsShowCheck() != 1) {
            helper.getView(R.id.iv_desktop).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.iv_desktop).setVisibility(View.VISIBLE);
        }
        if (item.getIsread().equals("0")) {
            helper.getView(R.id.tv_notread).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.tv_notread).setVisibility(View.GONE);
        }
        helper.setText(R.id.tv_title, item.getApp_name());
        helper.setText(R.id.tv_name, "[" + item.getOwner_name() + "]");
        helper.setText(R.id.tv_content, item.getTitle());
        GlideUtils.loadImageView(mContext, item.getApp_logo(), helper.getView(R.id.iv_headimg));
        ImageView iv_desktop = helper.getView(R.id.iv_desktop);
        helper.addOnClickListener(R.id.iv_desktop);
        if (item.getIsCheckBox() == 1) {
            iv_desktop.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_key_check_box_select));
        } else {
            iv_desktop.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_key_check_box));
        }
        if (item.getHomePushTime() != null) {
            boolean isToday;
            IsTodayutil isTodayUtil = new IsTodayutil();
            try {
                isToday = isTodayUtil.IsToday(item.getHomePushTime());
                if (isToday) {//表示消息时间是今天（只显示几时几分或者刚刚）
                    Date dt = new Date();
                    Long time = dt.getTime();//当前时间
                    long servicetime = Tools.dateString2Millis(item.getHomePushTime());//获取到的时间
                    long timestamp = time - servicetime;//当前时间和服务时间差
                    long minutes = 10 * 60 * 1000;//10分钟转换为多少毫秒
                    if (timestamp > minutes) {
                        String nowTime = Tools.getSecondToString(servicetime);
                        tvTime.setText(nowTime);
                    } else {
                        tvTime.setText("刚刚");
                    }
                } else {
                    Date dt = new Date();
                    Long time = dt.getTime();//当前时间
                    String newYear = Tools.getSimpleDateToString(time);//
                    String serviceYear = item.getHomePushTime().substring(0, item.getHomePushTime().indexOf(" "));
                    if (newYear.substring(0, 4).equals(serviceYear.substring(0, 4))) {//表示消息时间是今年
                        tvTime.setText(serviceYear.substring(5, serviceYear.length()));
                    } else {
                        tvTime.setText(serviceYear);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }
}