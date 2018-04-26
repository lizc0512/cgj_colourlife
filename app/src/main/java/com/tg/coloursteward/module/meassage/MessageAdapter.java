package com.tg.coloursteward.module.meassage;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tg.coloursteward.R;
import com.tg.coloursteward.util.StringUtils;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.utils.GlideRoundTransform;
import com.youmai.hxsdk.utils.TimeUtils;
import com.youmai.hxsdk.view.chat.emoticon.utils.EmoticonHandler;
import com.youmai.hxsdk.view.chat.utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import q.rorbin.badgeview.QBadgeView;

/**
 * Created by youmai on 17/2/14.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = "MessageAdapter";

    public static final int ADAPTER_TYPE_SERACH = 1;  //搜索
    public static final int ADAPTER_TYPE_PUSHMSG = 2; //ICE push msg
    public static final int ADAPTER_TYPE_NORMAL = 3;  //消息


    private Context mContext;
    private List<ExCacheMsgBean> messageList;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnLongItemClickListener;

    public MessageAdapter(Context context) {
        mContext = context;
        messageList = new ArrayList<>();
        messageList.add(new ExCacheMsgBean());
    }


    public List<ExCacheMsgBean> getMessageList() {
        return messageList;
    }

    public void addHeadItem(ExCacheMsgBean item) {
        if (messageList.size() > 0) {
            messageList.add(1, item);
            notifyDataSetChanged();
        }
    }


    public void addMessageList(List<ExCacheMsgBean> messageList) {
        int from = this.messageList.size();
        this.messageList.addAll(messageList);
        if (from == 0) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeChanged(from, messageList.size());
        }
    }


    public void changeMessageList(List<ExCacheMsgBean> oldList, List<ExCacheMsgBean> newList) {
        messageList.removeAll(oldList);
        messageList.addAll(newList);
        notifyDataSetChanged();
    }


    public void deleteMessage(int pos) {
        messageList.remove(pos);
        notifyDataSetChanged();
        //notifyItemRemoved(pos + getHeaderCount());
    }


    public ExCacheMsgBean getTop() {
        if (messageList.size() > 0) {
            return messageList.get(0);
        }

        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == ADAPTER_TYPE_SERACH) {
            View view = inflater.inflate(R.layout.message_list_item_header_search, parent, false);
            return new MsgItemSearch(view);
        } else if (viewType == ADAPTER_TYPE_PUSHMSG) {
            View view = inflater.inflate(R.layout.message_item_layout, parent, false);
            return new MsgItemPush(view);
        } else {
            View view = inflater.inflate(R.layout.message_item_layout, parent, false);
            return new MsgItemChat(view);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ExCacheMsgBean model = messageList.get(position);
        if (holder instanceof MsgItemSearch) {
            MsgItemSearch viewHeader = (MsgItemSearch) holder;
            viewHeader.header_item.setTag(position);

        } else if (holder instanceof MsgItemPush) {
            final MsgItemPush itemView = (MsgItemPush) holder;
            itemView.message_status.hide(false);

            String comefrom = model.getPushMsg().getComefrom();
            String pushTime = model.getPushMsg().getHomePushTime();
            try {
                Calendar calendar = TimeUtils.parseDate(pushTime, TimeUtils.DEFAULT_DATE_FORMAT);
                long millis = calendar.getTimeInMillis();

                String time = TimeFormatUtil.convertTimeMillli(mContext, millis);
                itemView.message_time.setText(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            itemView.message_name.setText(comefrom);
            itemView.message_type.setText(model.getPushMsg().getTitle());

            if (comefrom.equals("审批")) {//审批
                itemView.message_icon.setImageResource(R.drawable.sp);
            } else if (comefrom.equals("邮件")) {//邮件
                itemView.message_icon.setImageResource(R.drawable.yj);
            } else if (comefrom.equals("蜜蜂协同")) {//蜜蜂协同
                itemView.message_icon.setImageResource(R.drawable.case_home);
            } else if (comefrom.equals("通知") || comefrom.equals("公告") || comefrom.equals("通知公告")) {//公告通知
                itemView.message_icon.setImageResource(R.drawable.ggtz);
            } else {
                String url = model.getPushMsg().getICON();
                if (StringUtils.isNotEmpty(url)) {
                    Glide.with(mContext).load(url)
                            .apply(new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                            .into(itemView.message_icon);
                }
            }

        } else if (holder instanceof MsgItemChat) {
            final MsgItemChat itemView = (MsgItemChat) holder;
            itemView.message_item.setTag(position);
            itemView.message_time.setText(TimeFormatUtil.convertTimeMillli(mContext, model.getMsgTime()));
            itemView.message_name.setText(model.getDisplayName());

            switch (model.getMsgType()) {
                case CacheMsgBean.SEND_EMOTION:
                case CacheMsgBean.RECEIVE_EMOTION:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_1));
                    break;
                case CacheMsgBean.SEND_TEXT:
                case CacheMsgBean.RECEIVE_TEXT:
                    CacheMsgTxt textM = (CacheMsgTxt) model.getJsonBodyObj();
                    SpannableString msgSpan = new SpannableString(textM.getMsgTxt());
                    msgSpan = EmoticonHandler.getInstance(mContext.getApplicationContext()).getTextFace(
                            textM.getMsgTxt(), msgSpan, 0, Utils.getFontSize(itemView.message_type.getTextSize()));
                    itemView.message_type.setText(msgSpan);
                    break;
                case CacheMsgBean.SEND_IMAGE:
                case CacheMsgBean.RECEIVE_IMAGE:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_3));
                    break;
                case CacheMsgBean.SEND_LOCATION:
                case CacheMsgBean.RECEIVE_LOCATION:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_4));
                    break;
                case CacheMsgBean.SEND_VIDEO:
                case CacheMsgBean.RECEIVE_VIDEO:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_5));
                    break;
                case CacheMsgBean.SEND_VOICE:
                case CacheMsgBean.RECEIVE_VOICE:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_sounds));
                    break;
                case CacheMsgBean.SEND_FILE:
                case CacheMsgBean.RECEIVE_FILE:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_file));
                    break;
                default:
                    itemView.message_type.setText(mContext.getString(R.string.message_type));
            }

            //沟通列表
            int unreadCount = IMMsgManager.instance().getBadeCount(model.getTargetUuid());
            if (unreadCount > 0) {
                itemView.message_status.setBadgeNumber(unreadCount);
                itemView.message_status.setGravityOffset(0.5f, 0.5f, true);
                itemView.message_status.setBadgePadding(1.0f, true);
                itemView.message_status.setVisibility(View.VISIBLE);
            } else {
                itemView.message_status.setVisibility(View.GONE);
            }

            if (model.getGroupId() > 0) {
                itemView.message_icon.setImageResource(R.drawable.contacts_groupchat);
            } else {
                String avatar = model.getTargetAvatar();
                Glide.with(mContext).load(avatar)
                        .apply(new RequestOptions()
                                .transform(new GlideRoundTransform())
                                .placeholder(R.drawable.color_default_header)
                                .error(R.drawable.color_default_header)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                        .into(itemView.message_icon);
            }

        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(model, position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnLongItemClickListener != null) {
                    mOnLongItemClickListener.onItemLongClick(v, model, position);
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getUiType();
    }

    public void addTop(ExCacheMsgBean msgBean) {
        String uuid = msgBean.getTargetUuid();
        for (int i = 0; i < messageList.size(); i++) {
            ExCacheMsgBean item = messageList.get(i);
            if (item.getUiType() != MessageAdapter.ADAPTER_TYPE_NORMAL) {
                continue;
            }
            if (item.getTargetUuid().equals(uuid)) {
                messageList.remove(item);
                break;
            }
        }

        messageList.add(1, msgBean);
        SortComparator comp = new SortComparator();
        Collections.sort(messageList.subList(1, messageList.size()), comp);

        notifyDataSetChanged();
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnLongItemClickListener(OnItemLongClickListener listener) {
        this.mOnLongItemClickListener = listener;
    }


    public interface OnItemClickListener {
        void onItemClick(ExCacheMsgBean bean, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View v, ExCacheMsgBean bean, int position);
    }

    public class MsgItemSearch extends RecyclerView.ViewHolder {
        LinearLayout header_item;

        public MsgItemSearch(View itemView) {
            super(itemView);
            header_item = (LinearLayout) itemView.findViewById(R.id.list_item_header_search_root);
        }
    }


    protected class MsgItemPush extends RecyclerView.ViewHolder {
        ImageView message_icon, message_callBtn;
        TextView message_name, message_type, message_time;
        QBadgeView message_status;
        RelativeLayout message_item;

        public MsgItemPush(View itemView) {
            super(itemView);
            message_item = (RelativeLayout) itemView.findViewById(R.id.message_itme);
            message_icon = (ImageView) itemView.findViewById(R.id.message_icon);
            message_callBtn = (ImageView) itemView.findViewById(R.id.message_call_btn);
            message_name = (TextView) itemView.findViewById(R.id.message_name);
            message_type = (TextView) itemView.findViewById(R.id.message_type);
            message_time = (TextView) itemView.findViewById(R.id.message_time);
            message_status = new QBadgeView(mContext);
            message_status.bindTarget(message_icon);
            message_status.setBadgeGravity(Gravity.TOP | Gravity.END);
            message_status.setBadgeTextSize(10f, true);
            message_status.setBadgeBackgroundColor(ContextCompat.getColor(mContext, R.color.hx_color_red_tag));
            message_status.setShowShadow(false);
        }
    }


    protected class MsgItemChat extends RecyclerView.ViewHolder {
        ImageView message_icon, message_callBtn;
        TextView message_name, message_type, message_time;
        QBadgeView message_status;
        RelativeLayout message_item;

        public MsgItemChat(View itemView) {
            super(itemView);
            message_item = (RelativeLayout) itemView.findViewById(R.id.message_itme);
            message_icon = (ImageView) itemView.findViewById(R.id.message_icon);
            message_callBtn = (ImageView) itemView.findViewById(R.id.message_call_btn);
            message_name = (TextView) itemView.findViewById(R.id.message_name);
            message_type = (TextView) itemView.findViewById(R.id.message_type);
            message_time = (TextView) itemView.findViewById(R.id.message_time);
            message_status = new QBadgeView(mContext);
            message_status.bindTarget(message_icon);
            message_status.setBadgeGravity(Gravity.TOP | Gravity.END);
            message_status.setBadgeTextSize(10f, true);
            message_status.setBadgeBackgroundColor(ContextCompat.getColor(mContext, R.color.hx_color_red_tag));
            message_status.setShowShadow(false);
        }
    }
}
