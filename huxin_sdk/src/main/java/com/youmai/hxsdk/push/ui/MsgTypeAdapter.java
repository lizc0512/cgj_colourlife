package com.youmai.hxsdk.push.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.PushMsg;
import com.youmai.hxsdk.db.bean.RemindMsg;
import com.youmai.hxsdk.db.dao.PushMsgDao;
import com.youmai.hxsdk.db.dao.RemindMsgDao;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.TimeUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class MsgTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<MsgType> mList;

    public MsgTypeAdapter(Context context) {
        mContext = context;
    }

    public void setList(List<MsgType> list) {
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
        View view = inflater.inflate(R.layout.item_msg_type, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final MsgType item = mList.get(position);
        ImageView img_icon = ((TextViewHolder) viewHolder).img_icon;
        TextView tv_date = ((TextViewHolder) viewHolder).tv_date;
        TextView tv_title = ((TextViewHolder) viewHolder).tv_title;
        TextView tv_content = ((TextViewHolder) viewHolder).tv_content;

        final int msgType = item.getMsgType();

        final Badge badge = new QBadgeView(mContext).bindTarget(img_icon);
        badge.setBadgeTextSize(0.5f, true);
        badge.setBadgeGravity(Gravity.TOP | Gravity.START);
        //badge.setBadgePadding(6, true);
        badge.setGravityOffset(2, 2, true);
        badge.setBadgeTextColor(ContextCompat.getColor(mContext, R.color.hx_color_red_tag));
        badge.setBadgeBackgroundColor(ContextCompat.getColor(mContext, R.color.hx_color_red_tag));

        final PushMsgDao pushMsgDao = GreenDBIMManager.instance(mContext).getPushMsgDao();
        final List<PushMsg> pushMsgList = pushMsgDao.queryBuilder()
                .where(PushMsgDao.Properties.Is_click.eq(false), PushMsgDao.Properties.Msg_type.eq(msgType))
                .list();

        final RemindMsgDao remindMsgDao = GreenDBIMManager.instance(mContext).getRemindMsgDao();
        final List<RemindMsg> remindMsgList = remindMsgDao.queryBuilder()
                .where(RemindMsgDao.Properties.IsRead.eq(false))
                .orderAsc(RemindMsgDao.Properties.RecTime)
                .list();

        if (msgType < 4) {
            if (!ListUtils.isEmpty(pushMsgList)) {
                badge.setBadgeNumber(-1);
            } else {
                badge.hide(false);
            }
        } else if (msgType == 100) {
            if (!ListUtils.isEmpty(remindMsgList)) {
                badge.setBadgeNumber(remindMsgList.size());
            } else {
                badge.hide(false);
            }
        }


        View view = ((TextViewHolder) viewHolder).view;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgType < 4) {
                    Intent intent = new Intent(mContext, PushMsgContentActivity.class);
                    intent.putExtra("type", item.getMsgType());
                    mContext.startActivity(intent);
                    badge.hide(false);

                    for (PushMsg item : pushMsgList) {
                        item.setIs_click(true);
                    }
                    pushMsgDao.insertOrReplaceInTx(pushMsgList);
                } else if (msgType == 100) {

                }


            }
        });


        String type = null;
        int res = R.drawable.img_push0;
        if (msgType == 0) {
            type = "系统公告";
            res = R.drawable.img_push0;
        } else if (msgType == 1) {
            type = "精选活动";
            res = R.drawable.img_push1;
        } else if (msgType == 2) {
            type = "玩转呼信";
            res = R.drawable.img_push2;
        } else if (msgType == 3) {
            type = "行业资讯";
            res = R.drawable.img_push3;
        } else if (msgType == 100) {
            type = "呼信提醒";
            res = R.drawable.hx_ic_remind;
        }


        tv_title.setText(type);
        img_icon.setImageResource(res);

        tv_content.setText(item.getTitle());

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
            if (rec_time != 0) {
                String date = TimeUtils.getDate(rec_time);
                tv_date.setText(date);
                tv_date.setVisibility(View.VISIBLE);
            } else {
                tv_date.setVisibility(View.INVISIBLE);
            }
        }

    }


    public static boolean isSameDay(Date dateA, Date dateB) {
        Calendar calDateA = Calendar.getInstance();
        calDateA.setTime(dateA);

        Calendar calDateB = Calendar.getInstance();
        calDateB.setTime(dateB);

        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                && calDateA.get(Calendar.DAY_OF_MONTH) == calDateB.get(Calendar.DAY_OF_MONTH);
    }


    public static boolean isYesterDay(Date dateA, Date dateB) {
        Calendar calDateA = Calendar.getInstance();
        calDateA.setTime(dateA);
        calDateA.add(Calendar.DAY_OF_YEAR, -1); // yesterday

        Calendar calDateB = Calendar.getInstance();
        calDateB.setTime(dateB);

        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                && calDateA.get(Calendar.DAY_OF_MONTH) == calDateB.get(Calendar.DAY_OF_MONTH);

    }

    public static boolean isBeforeYesterDay(Date dateA, Date dateB) {
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

        View view;
        ImageView img_icon;
        TextView tv_date;
        TextView tv_title;
        TextView tv_content;

        private TextViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            img_icon = (ImageView) itemView.findViewById(R.id.img_icon);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
        }

    }


}

