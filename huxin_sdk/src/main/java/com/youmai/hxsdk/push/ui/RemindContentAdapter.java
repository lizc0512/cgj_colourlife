package com.youmai.hxsdk.push.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.IMConnectionActivity;
import com.youmai.hxsdk.db.bean.HxUsers;
import com.youmai.hxsdk.db.bean.RemindMsg;
import com.youmai.hxsdk.db.helper.HxUsersHelper;
import com.youmai.hxsdk.module.remind.RemindItem;
import com.youmai.hxsdk.utils.GlideCircleTransform;
import com.youmai.hxsdk.utils.TimeUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class RemindContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = RemindContentAdapter.class.getSimpleName();

    private Context mContext;
    private List<RemindMsg> mList;

    public RemindContentAdapter(Context context) {
        mContext = context;
    }

    public void setList(List<RemindMsg> list) {
        mList = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_remind_msg_content, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final RemindMsg item = mList.get(position);

        View view = ((TextViewHolder) viewHolder).itemView;

        String targetPhone;
        if (HuxinSdkManager.instance().getPhoneNum().equals(item.getSendPhone())) {
            targetPhone = item.getReceivePhone();
        } else {
            targetPhone = item.getSendPhone();
        }
        final String desPhone = targetPhone;
        final String name = HuxinSdkManager.instance().getContactName(desPhone);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, IMConnectionActivity.class);
                intent.putExtra(IMConnectionActivity.DST_PHONE, desPhone);
                intent.putExtra(IMConnectionActivity.DST_NAME, name);
                intent.putExtra(IMConnectionActivity.IS_SHOW_AUDIO, false);
                intent.putExtra(IMConnectionActivity.MSG_ID, item.getMsgId().longValue());
                mContext.startActivity(intent);
            }
        });

        ImageView img_head = ((TextViewHolder) viewHolder).img_head;
        loadIcon(targetPhone, img_head);

        TextView tv_name = ((TextViewHolder) viewHolder).tv_name;
        tv_name.setText(name);

        ImageView img_type = ((TextViewHolder) viewHolder).img_type;
        int msgIcon = item.getMsgIcon();
        if (msgIcon < RemindItem.ITEM_DRAWABLES.length) {
            img_type.setImageResource(RemindItem.ITEM_DRAWABLES[msgIcon]);
        }

        TextView tv_remind = ((TextViewHolder) viewHolder).tv_remind;
        tv_remind.setText(item.getRemark());

        TextView tv_set_time = ((TextViewHolder) viewHolder).tv_set_time;

        String time = TimeUtils.getTime(item.getCreateTime(), TimeUtils.MINUTE_FORMAT_DATE);

        String format = String.format("于 %s 设置提醒", time);
        tv_set_time.setText(format);

        TextView tv_date = ((TextViewHolder) viewHolder).tv_date;
        long rec_time = item.getRecTime();
        Date curDate = new Date(System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(rec_time);
        Date recDate = calendar.getTime();

        if (isSameDay(curDate, recDate)) {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);

            StringBuilder sb = new StringBuilder();
            sb.append(hour).append(":");
            if (min < 10) {
                sb.append(0).append(min);
            } else {
                sb.append(min);
            }

            tv_date.setText(sb.toString());

        } else if (isYesterDay(curDate, recDate)) {
            tv_date.setText("昨天");
        } else if (isBeforeYesterDay(curDate, recDate)) {
            tv_date.setText("前天");
        } else {
            String date = TimeUtils.getDate(rec_time);
            tv_date.setText(date);
        }

    }

    private void loadIcon(String collectPhone, final ImageView imageView) {
        HxUsers hxUsers = HxUsersHelper.instance().getHxUser(mContext, collectPhone);
        if (hxUsers != null && hxUsers.getIconUrl() != null) {
            Glide.with(mContext).load(hxUsers.getIconUrl())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .placeholder(R.drawable.hx_index_head01)
                            .circleCrop())
                    .into(imageView);
        } else {
            HxUsersHelper.instance().updateSingleUser(mContext, collectPhone, new HxUsersHelper.IOnUpCompleteListener() {
                @Override
                public void onSuccess(HxUsers users) {
                    if (users != null && users.getIconUrl() != null) {
                        Glide.with(mContext).load(users.getIconUrl())
                                .apply(new RequestOptions()
                                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                        .placeholder(R.drawable.hx_index_head01)
                                        .circleCrop())
                                .into(imageView);
                    }
                }

                @Override
                public void onFail() {
                }
            });
        }
    }


    private static boolean isSameDay(Date dateA, Date dateB) {
        Calendar calDateA = Calendar.getInstance();
        calDateA.setTime(dateA);

        Calendar calDateB = Calendar.getInstance();
        calDateB.setTime(dateB);

        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                && calDateA.get(Calendar.DAY_OF_MONTH) == calDateB.get(Calendar.DAY_OF_MONTH);
    }


    private static boolean isYesterDay(Date dateA, Date dateB) {
        Calendar calDateA = Calendar.getInstance();
        calDateA.setTime(dateA);
        calDateA.add(Calendar.DAY_OF_YEAR, -1); // yesterday

        Calendar calDateB = Calendar.getInstance();
        calDateB.setTime(dateB);

        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                && calDateA.get(Calendar.DAY_OF_MONTH) == calDateB.get(Calendar.DAY_OF_MONTH);

    }

    private static boolean isBeforeYesterDay(Date dateA, Date dateB) {
        Calendar calDateA = Calendar.getInstance();
        calDateA.setTime(dateA);
        calDateA.add(Calendar.DAY_OF_YEAR, -2); // yesterday

        Calendar calDateB = Calendar.getInstance();
        calDateB.setTime(dateB);

        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                && calDateA.get(Calendar.DAY_OF_MONTH) == calDateB.get(Calendar.DAY_OF_MONTH);

    }


    private class TextViewHolder extends RecyclerView.ViewHolder {

        TextView tv_date;

        ImageView img_head;
        TextView tv_name;
        ImageView img_type;
        TextView tv_remind;
        TextView tv_set_time;

        LinearLayout linear_bar;

        private TextViewHolder(View itemView) {
            super(itemView);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);

            img_head = (ImageView) itemView.findViewById(R.id.img_head);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            img_type = (ImageView) itemView.findViewById(R.id.img_type);

            tv_remind = (TextView) itemView.findViewById(R.id.tv_remind);
            tv_set_time = (TextView) itemView.findViewById(R.id.tv_set_time);

            linear_bar = (LinearLayout) itemView.findViewById(R.id.linear_bar);
        }

    }


}

