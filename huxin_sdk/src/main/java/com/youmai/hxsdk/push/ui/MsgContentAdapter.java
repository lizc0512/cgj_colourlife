package com.youmai.hxsdk.push.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.activity.WebViewActivity;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.db.bean.PushMsg;
import com.youmai.hxsdk.utils.TimeUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MsgContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = MsgContentAdapter.class.getSimpleName();

    private Context mContext;
    private List<PushMsg> mList;

    public MsgContentAdapter(Context context) {
        mContext = context;
    }

    public void setList(List<PushMsg> list) {
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
        View view = inflater.inflate(R.layout.item_msg_content, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final PushMsg item = mList.get(position);

        View view = ((TextViewHolder) viewHolder).view;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 0 打开APP , 1 打开app activity , ,3 APP webview打开 url ,4 打开APP dialog图片展示 ,5 打开APP dialog文字展示，6 详情展示
                int open_type = item.getOpen_type();
                if (open_type == 1) {  //1 打开app activity
                    openActivity(mContext, item.getActivity(), item.getMsg_type(),
                            item.getTitle(), item.getText(), item.getPublish_date());
                } else if (open_type == 2) { // 浏览器打开url
                    openUrl(mContext, item.getUrl());
                } else if (open_type == 3) {  //APP webview打开 url
                    openAppWebView(mContext, item.getUrl(), item.getTitle());
                } /*else if (open_type == 4) {  //详情打开
                    openAppMsgDetail(mContext, item.getMsg_type(), item.getTitle(), item.getText(),
                            item.getPublish_date());
                } */ else {
                    //launchApp(mContext);
                }
            }
        });

        TextView tv_date = ((TextViewHolder) viewHolder).tv_date;
        TextView tv_title = ((TextViewHolder) viewHolder).tv_title;

        ImageView img_content = ((TextViewHolder) viewHolder).img_content;
        TextView tv_content = ((TextViewHolder) viewHolder).tv_content;

        String fid = item.getH_img();
        if (!TextUtils.isEmpty(fid)) {
            String imgUrl = AppConfig.getImageUrl(mContext, fid);
            img_content.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(imgUrl).into(img_content);
        }

        tv_title.setText(item.getTitle());
        tv_content.setText("    " + item.getText());

        long rec_time = item.getRec_time();
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


    private void launchApp(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (intent == null) {
            Log.e(TAG, "handleMessage(): cannot find app: " + context.getPackageName());
        } else {
            intent.setPackage(context.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Log.d(TAG, "handleMessage(): lunach app: " + context.getPackageName());
        }
    }

    public void openUrl(Context context, String url) {
        if (!TextUtils.isEmpty(url.trim())) {
            Log.d(TAG, "handleMessage(): open url: " + url);
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public void openActivity(Context context, String activity, int type, String title,
                             String content, String time) {
        if (!TextUtils.isEmpty(activity.trim())) {
            Intent intent = new Intent();
            intent.setClassName(context, activity);
            if (activity.equals("com.youmai.hxsdk.push.ui.PushMsgDetailActivity")) {
                intent.putExtra(PushMsgDetailActivity.TYPE, type);
                intent.putExtra(PushMsgDetailActivity.TITLE, title);
                intent.putExtra(PushMsgDetailActivity.CONTENT, content);
                intent.putExtra(PushMsgDetailActivity.TIME, time);
                intent.putExtra(SdkBaseActivity.FROM_PUSH, false);
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    private void openAppWebView(Context context, String url, String title) {
        if (!TextUtils.isEmpty(url.trim())) {
            Intent intent = new Intent();
            intent.putExtra(WebViewActivity.INTENT_TITLE, title);
            intent.putExtra(WebViewActivity.INTENT_URL, url);
            intent.putExtra(SdkBaseActivity.FROM_PUSH, false);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(context, WebViewActivity.class);
            context.startActivity(intent);
        }
    }


    private void openAppMsgDetail(Context context, int type, String title, String content, String time) {
        if (!TextUtils.isEmpty(title.trim()) && !TextUtils.isEmpty(content.trim())) {
            Intent intent = new Intent();
            intent.putExtra(PushMsgDetailActivity.TYPE, type);
            intent.putExtra(PushMsgDetailActivity.TITLE, title);
            intent.putExtra(PushMsgDetailActivity.CONTENT, content);
            intent.putExtra(PushMsgDetailActivity.TIME, time);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(context, PushMsgDetailActivity.class);
            context.startActivity(intent);
        }
    }

    private void showNotifyImageDialog(final Context context, String url, String btn_name,
                                       final String activity) {
        if (!TextUtils.isEmpty(url)) {
            HxNotifyImageDialog dialog = new HxNotifyImageDialog(context);
            dialog.show();

            dialog.setImage(url);
            dialog.setGoTaskClickListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!TextUtils.isEmpty(activity)) {
                        final Intent intent = new Intent();
                        intent.setClassName(context, activity);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }


    private void showNotifyTextDialog(final Context context, int type, String content,
                                      String btn_name, final String activity) {
        if (!TextUtils.isEmpty(content)) {
            HxNotifyTextDialog dialog = new HxNotifyTextDialog(context);
            dialog.show();

            dialog.setType(type);
            dialog.setContent(content);
            dialog.setBtnName(btn_name);

            dialog.setGoTaskClickListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!TextUtils.isEmpty(activity)) {
                        final Intent intent = new Intent();
                        intent.setClassName(context, activity);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
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

        View view;
        TextView tv_date;
        TextView tv_title;

        ImageView img_content;
        TextView tv_content;
        LinearLayout linear_bar;

        private TextViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            img_content = (ImageView) itemView.findViewById(R.id.img_content);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            linear_bar = (LinearLayout) itemView.findViewById(R.id.linear_bar);
        }

    }


}

