package com.youmai.hxsdk.push.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.IMConnectionActivity;
import com.youmai.hxsdk.db.bean.HxUsers;
import com.youmai.hxsdk.db.helper.HxUsersHelper;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;
import com.youmai.hxsdk.keep.RespRemindBean;
import com.youmai.hxsdk.module.remind.RemindItem;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GlideCircleTransform;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.TimeUtils;
import com.youmai.hxsdk.view.tip.TipView;
import com.youmai.hxsdk.view.tip.bean.TipBean;
import com.youmai.hxsdk.view.tip.listener.ItemListener;
import com.youmai.hxsdk.view.tip.tools.TipsType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class RemindFutureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = RemindFutureAdapter.class.getSimpleName();

    private RemindMsgFutureFragment mFragment;
    private Context mContext;
    private List<RespRemindBean> mList;

    private TipView voiceTip;
    private float mVoiceRawX, mVoiceRawY;

    public RemindFutureAdapter(RemindMsgFutureFragment fragment) {
        mFragment = fragment;
        mContext = fragment.getContext();
        mList = new ArrayList<>();
    }

    public void setList(List<RespRemindBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }


    public void clearList() {
        mList.clear();
    }

    public void removeItem(int position) {
        mList.remove(position);

        if (ListUtils.isEmpty(mList)) {
            mFragment.refreshEmpty();
        } else {
            notifyDataSetChanged();
        }
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_future_remind, parent, false);

        return new TextViewHolder(view);
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final RespRemindBean item = mList.get(position);

        final View view = ((TextViewHolder) viewHolder).itemView;

        final View linear_bar = ((TextViewHolder) viewHolder).linear_bar;

        String targetPhone;
        if (HuxinSdkManager.instance().getPhoneNum().equals(item.getSendPhone())) {
            targetPhone = item.getReceivePhone();
        } else {
            targetPhone = item.getSendPhone();
        }
        final String desPhone = targetPhone;
        final String name = HuxinSdkManager.instance().getContactName(desPhone);

        linear_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, IMConnectionActivity.class);
                intent.putExtra(IMConnectionActivity.DST_PHONE, desPhone);
                intent.putExtra(IMConnectionActivity.DST_NAME, name);
                intent.putExtra(IMConnectionActivity.IS_SHOW_AUDIO, false);
                intent.putExtra(IMConnectionActivity.MSG_ID, item.getMsgId());
                mFragment.startActivityForResult(intent, RemindMsgFutureFragment.REVIEW_REMIND);
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mVoiceRawX = event.getRawX();
                    mVoiceRawY = event.getRawY();
                }
                return false;
            }
        });


        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                List<TipBean> tips = getTips();
                voiceTip = new TipView(mContext, tips, mVoiceRawX, mVoiceRawY);
                voiceTip.setListener(new ItemListener() {
                    @Override
                    public void delete() {
                        delItem(item.getMsgId(), item.getRemindTime(), position);
                    }

                    @Override
                    public void more() {
                        //暂时用不着
                    }
                });
                voiceTip.show(view);
                return false;
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

        String time = TimeUtils.getTime(item.getRemindTime(), TimeUtils.MINUTE_FORMAT_DATE);

        String format = String.format("于 %s 提醒", time);
        tv_set_time.setText(format);

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

    //语音文本(已经转过文字的)
    public static List<TipBean> getTips() {
        List<TipBean> beanList = new ArrayList<>();
        beanList.add(new TipBean("删除", TipsType.TIP_DELETE));
        return beanList;
    }


    private void delItem(final long msgId, long remindTime, final int position) {
        HuxinSdkManager.instance().remindDel(msgId, remindTime, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespBaseBean baseBean = GsonUtil.parse(response, RespBaseBean.class);
                if (baseBean != null && baseBean.isSuccess()) {
                    Toast.makeText(mContext, "提醒删除成功!", Toast.LENGTH_SHORT).show();

                    removeItem(position);
                    CacheMsgHelper.instance(mContext).insertOrUpdate(msgId);

                    mFragment.isDel();

                    String savedStr = AppUtils.getStringSharedPreferences(mContext, "last_set_remind", "");
                    if (!TextUtils.isEmpty(savedStr)) {
                        String strs[] = savedStr.split("@");
                        if (strs.length == 3) {
                            try {
                                long saveMsgId = Long.parseLong(strs[2]);
                                if (saveMsgId == msgId) {
                                    AppUtils.setStringSharedPreferences(mContext, "last_set_remind", "");
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            }
        });
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

        ImageView img_head;
        TextView tv_name;
        ImageView img_type;
        TextView tv_remind;
        TextView tv_set_time;

        LinearLayout linear_bar;

        private TextViewHolder(View itemView) {
            super(itemView);
            img_head = (ImageView) itemView.findViewById(R.id.img_head);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            img_type = (ImageView) itemView.findViewById(R.id.img_type);

            tv_remind = (TextView) itemView.findViewById(R.id.tv_remind);
            tv_set_time = (TextView) itemView.findViewById(R.id.tv_set_time);

            linear_bar = (LinearLayout) itemView.findViewById(R.id.linear_bar);
        }

    }


}

