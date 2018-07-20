package com.youmai.hxsdk.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
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
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.data.ExCacheMsgBean;
import com.youmai.hxsdk.data.SortComparator;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.entity.MsgConfig;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GlideRoundTransform;
import com.youmai.hxsdk.utils.TimeUtils;
import com.youmai.hxsdk.view.chat.emoticon.utils.EmoticonHandler;
import com.youmai.hxsdk.view.chat.utils.Utils;
import com.youmai.hxsdk.view.group.TeamHeadView;

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

    public static final int ADAPTER_TYPE_SEARCH = 1;  //搜索
    public static final int ADAPTER_TYPE_PUSHMSG = 2; //ICE push msg
    public static final int ADAPTER_TYPE_SINGLE = 3;  //单聊消息
    public static final int ADAPTER_TYPE_GROUP = 4;  //群聊消息


    private Context mContext;
    private List<ExCacheMsgBean> messageList;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnLongItemClickListener;

    public MessageAdapter(Context context) {
        mContext = context;
        messageList = new ArrayList<>();
        init(messageList);
    }


    private void init(List<ExCacheMsgBean> list) {

        long curTime = System.currentTimeMillis();

        long xt = AppUtils.getLongSharedPreferences(mContext, "case", 0);
        long yj = AppUtils.getLongSharedPreferences(mContext, "yj", 0);
        long ggtz = AppUtils.getLongSharedPreferences(mContext, "ggtz", 0);
        long sp = AppUtils.getLongSharedPreferences(mContext, "sp", 0);

        MsgConfig.ContentBean.DataBean item1 = new MsgConfig.ContentBean.DataBean();
        item1.setComefrom("蜜蜂协同");
        item1.setTitle("暂无新消息");
        item1.setOwner_name("暂无");
        item1.setClient_code("case");
        ExCacheMsgBean bean1 = new ExCacheMsgBean(item1);
        if (xt == 0) {
            bean1.setMsgTime(curTime);
            AppUtils.setLongSharedPreferences(mContext, "case", curTime);
            curTime++;
        } else {
            bean1.setMsgTime(xt);
        }

        MsgConfig.ContentBean.DataBean item2 = new MsgConfig.ContentBean.DataBean();
        item2.setComefrom("邮件");
        item2.setTitle("暂无新消息");
        item2.setOwner_name("暂无");
        item2.setClient_code("yj");
        ExCacheMsgBean bean2 = new ExCacheMsgBean(item2);
        if (yj == 0) {
            bean2.setMsgTime(curTime);
            AppUtils.setLongSharedPreferences(mContext, "yj", curTime);
            curTime++;
        } else {
            bean2.setMsgTime(yj);
        }

        MsgConfig.ContentBean.DataBean item3 = new MsgConfig.ContentBean.DataBean();
        item3.setComefrom("公告");
        item3.setTitle("暂无新消息");
        item3.setOwner_name("暂无");
        item3.setClient_code("ggtz");
        ExCacheMsgBean bean3 = new ExCacheMsgBean(item3);
        if (ggtz == 0) {
            bean2.setMsgTime(curTime);
            AppUtils.setLongSharedPreferences(mContext, "ggtz", curTime);
            curTime++;
        } else {
            bean3.setMsgTime(ggtz);
        }

        MsgConfig.ContentBean.DataBean item4 = new MsgConfig.ContentBean.DataBean();
        item4.setComefrom("审批");
        item4.setTitle("暂无新消息");
        item4.setOwner_name("暂无");
        item4.setClient_code("sp");
        ExCacheMsgBean bean4 = new ExCacheMsgBean(item4);
        if (sp == 0) {
            bean2.setMsgTime(curTime);
            AppUtils.setLongSharedPreferences(mContext, "sp", curTime);
        } else {
            bean4.setMsgTime(sp);
        }

        list.add(new ExCacheMsgBean());
        list.add(bean1);
        list.add(bean2);
        list.add(bean3);
        list.add(bean4);

    }

    public void addPushMsgItem(ExCacheMsgBean msgBean) {
        if (msgBean.getUiType() == MessageAdapter.ADAPTER_TYPE_PUSHMSG) {
            MsgConfig.ContentBean.DataBean bean = msgBean.getPushMsg();
            String clientCode = bean.getClient_code();

            ExCacheMsgBean item = null;
            for (int i = 0; i < messageList.size(); i++) {
                ExCacheMsgBean index = messageList.get(i);

                if (index.getUiType() == MessageAdapter.ADAPTER_TYPE_PUSHMSG
                        && clientCode.equals(index.getPushMsg().getClient_code())) {
                    item = index;
                    break;
                }
            }
            if (item != null) {
                boolean isChanged = Collections.replaceAll(messageList, item, msgBean);
                if (isChanged) {
                    SortComparator comp = new SortComparator();
                    Collections.sort(messageList.subList(1, messageList.size()), comp);
                    notifyDataSetChanged();
                }
            } else {
                messageList.add(1, msgBean);
                SortComparator comp = new SortComparator();
                Collections.sort(messageList.subList(1, messageList.size()), comp);
                notifyDataSetChanged();
            }
        }
    }

    public void addTop(ExCacheMsgBean msgBean) {
        String uuid = msgBean.getTargetUuid();
        for (int i = 0; i < messageList.size(); i++) {
            ExCacheMsgBean item = messageList.get(i);
            if (item.getUiType() == MessageAdapter.ADAPTER_TYPE_SEARCH
                    || item.getUiType() == MessageAdapter.ADAPTER_TYPE_PUSHMSG) {
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


    public void changeMessageList(List<ExCacheMsgBean> newList) {
        List<ExCacheMsgBean> oldList = new ArrayList<>();
        for (ExCacheMsgBean item : messageList) {
            if (item.getUiType() == MessageAdapter.ADAPTER_TYPE_SINGLE
                    || item.getUiType() == MessageAdapter.ADAPTER_TYPE_GROUP) {
                oldList.add(item);
            }
        }
        messageList.removeAll(oldList);
        messageList.addAll(newList);

        SortComparator comp = new SortComparator();
        Collections.sort(messageList.subList(1, messageList.size()), comp);
        notifyDataSetChanged();
    }


    public void deleteMessage(String targetUuid) {
        for (ExCacheMsgBean item : messageList) {
            if (item.getTargetUuid() != null
                    && item.getTargetUuid().equals(targetUuid)) {
                messageList.remove(item);
                break;
            }
        }
        notifyDataSetChanged();
    }


    public List<ExCacheMsgBean> getMsgList() {
        return messageList;
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
        if (viewType == ADAPTER_TYPE_SEARCH) {
            View view = inflater.inflate(R.layout.message_list_item_header_search, parent, false);
            return new MsgItemSearch(view);
        } else if (viewType == ADAPTER_TYPE_PUSHMSG) {
            View view = inflater.inflate(R.layout.push_message_item_layout, parent, false);
            return new MsgItemPush(view);
        } else if (viewType == ADAPTER_TYPE_SINGLE) {
            View view = inflater.inflate(R.layout.single_message_item_layout, parent, false);
            return new MsgItemChat(view);
        } else if (viewType == ADAPTER_TYPE_GROUP) {
            View view = inflater.inflate(R.layout.group_message_item_layout, parent, false);
            return new MsgItemGroup(view);
        } else {
            View view = inflater.inflate(R.layout.single_message_item_layout, parent, false);
            return new MsgItemChat(view);
        }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ExCacheMsgBean model = messageList.get(position);
        if (holder instanceof MsgItemSearch) {
            MsgItemSearch viewHeader = (MsgItemSearch) holder;
            viewHeader.header_item.setTag(position);

        } else if (holder instanceof MsgItemPush) {
            final MsgItemPush itemView = (MsgItemPush) holder;
            itemView.message_status.hide(false);

            String comefrom = model.getPushMsg().getComefrom();
            String pushTime = model.getPushMsg().getHomePushTime();
            String owner_name = model.getPushMsg().getOwner_name();
            if (!TextUtils.isEmpty(pushTime)) {
                try {
                    Calendar calendar = TimeUtils.parseDate(pushTime, TimeUtils.DEFAULT_DATE_FORMAT);
                    long millis = calendar.getTimeInMillis();

                    String time = TimeUtils.dateFormat(millis);
                    itemView.message_time.setText(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            itemView.message_name.setText(comefrom);

            if (!TextUtils.isEmpty(owner_name)) {
                String format = mContext.getResources().getString(R.string.group_item_info);
                itemView.message_info.setText(String.format(format, owner_name));
            }

            if (model.getPushMsg().getNotread() > 0) {
                itemView.message_status.setBadgeNumber(-1);
            } else {
                itemView.message_status.setBadgeNumber(0);
            }

            itemView.message_type.setText(model.getPushMsg().getTitle());

            String clientCode = model.getPushMsg().getClient_code();

            if (clientCode.contains("sp")) {//审批
                itemView.message_icon.setImageResource(R.drawable.sp);
            } else if (clientCode.contains("yj")) {//邮件
                itemView.message_icon.setImageResource(R.drawable.yj);
            } else if (clientCode.contains("case")) {//蜜蜂协同
                itemView.message_icon.setImageResource(R.drawable.case_home);
            } else if (clientCode.contains("ggtz")) {//公告通知
                itemView.message_icon.setImageResource(R.drawable.ggtz);
            } else {
                String url = model.getPushMsg().getICON();
                if (!TextUtils.isEmpty(url)) {
                    Glide.with(mContext).load(url)
                            .apply(new RequestOptions()
                                    .error(R.drawable.color_default_header)
                                    .placeholder(R.drawable.color_default_header)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                            .into(itemView.message_icon);
                }
            }

        } else if (holder instanceof MsgItemChat) {
            final MsgItemChat itemView = (MsgItemChat) holder;
            itemView.message_item.setTag(position);
            itemView.message_time.setText(TimeUtils.dateFormat(model.getMsgTime()));

            String displayName = model.getDisplayName();
            boolean contains = displayName.contains(ColorsConfig.GROUP_DEFAULT_NAME);
            if (contains) {
                displayName = displayName.replace(ColorsConfig.GROUP_DEFAULT_NAME, "");
            }
            itemView.message_name.setText(displayName);

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
                case CacheMsgBean.SEND_REDPACKAGE:
                case CacheMsgBean.RECEIVE_REDPACKAGE:
                case CacheMsgBean.OPEN_REDPACKET:
                    itemView.message_type.setText(mContext.getString(R.string.message_red_package));
                    break;
                case CacheMsgBean.RECEIVE_PACKET_OPENED:
                    itemView.message_type.setText(mContext.getString(R.string.message_red_package_open));
                    break;
                case CacheMsgBean.PACKET_OPENED_SUCCESS:
                    itemView.message_type.setText(mContext.getString(R.string.message_open_red_packet_success));
                    break;
                default:
                    itemView.message_type.setText("");
            }

            //沟通列表
            int unreadCount = IMMsgManager.instance().getBadeCount(model.getTargetUuid());
            itemView.message_status.setBadgeNumber(unreadCount);


            int size = mContext.getResources().getDimensionPixelOffset(R.dimen.card_head);

            String avatar = model.getTargetAvatar();
            Glide.with(mContext).load(avatar)
                    .apply(new RequestOptions()
                            .transform(new GlideRoundTransform())
                            .override(size, size)
                            .placeholder(R.drawable.color_default_header)
                            .error(R.drawable.color_default_header)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(itemView.message_icon);

        } else if (holder instanceof MsgItemGroup) {//群组
            final MsgItemGroup itemView = (MsgItemGroup) holder;
            itemView.message_item.setTag(position);
            itemView.message_time.setText(TimeUtils.dateFormat(model.getMsgTime()));

            String displayName = model.getDisplayName();
            if (!TextUtils.isEmpty(displayName)
                    && displayName.contains(ColorsConfig.GROUP_DEFAULT_NAME)) {
                displayName = displayName.replace(ColorsConfig.GROUP_DEFAULT_NAME, "");
            }
            itemView.message_name.setText(displayName);

            String keyword = "";
            if (IMMsgManager.instance().isMeInGroup(model.getGroupId())) {
                keyword = "[有人@我]";
            }


            switch (model.getMsgType()) {
                case CacheMsgBean.SEND_EMOTION:
                case CacheMsgBean.RECEIVE_EMOTION:
                    String context = keyword + mContext.getString(R.string.message_type_1);
                    itemView.message_type.setText(context);
                    setAtText(keyword, context, itemView.message_type);
                    break;
                case CacheMsgBean.SEND_TEXT:
                case CacheMsgBean.RECEIVE_TEXT:
                    CacheMsgTxt textM = (CacheMsgTxt) model.getJsonBodyObj();
                    SpannableString msgSpan = new SpannableString(textM.getMsgTxt());
                    msgSpan = EmoticonHandler.getInstance(mContext.getApplicationContext()).getTextFace(
                            textM.getMsgTxt(), msgSpan, 0, Utils.getFontSize(itemView.message_type.getTextSize()));

                    String content = keyword + msgSpan;
                    itemView.message_type.setText(content);
                    setAtText(keyword, content, itemView.message_type);

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
                case CacheMsgBean.GROUP_MEMBER_CHANGED:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_member_changed));
                    break;
                case CacheMsgBean.GROUP_NAME_CHANGED:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_name_changed));
                    break;
                case CacheMsgBean.GROUP_TRANSFER_OWNER:
                    itemView.message_type.setText(mContext.getString(R.string.message_type_owner_changed));
                    break;
                case CacheMsgBean.SEND_REDPACKAGE:
                case CacheMsgBean.RECEIVE_REDPACKAGE:
                case CacheMsgBean.OPEN_REDPACKET:
                    itemView.message_type.setText(mContext.getString(R.string.message_red_package));
                    break;
                case CacheMsgBean.RECEIVE_PACKET_OPENED:
                    itemView.message_type.setText(mContext.getString(R.string.message_red_package_open));
                    break;
                case CacheMsgBean.PACKET_OPENED_SUCCESS:
                    itemView.message_type.setText(mContext.getString(R.string.message_open_red_packet_success));
                    break;
                default:
                    itemView.message_type.setText("");
            }

            //沟通列表
            int unreadCount = IMMsgManager.instance().getBadeCount(model.getTargetUuid());
            itemView.message_status.setBadgeNumber(unreadCount);

        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder instanceof MsgItemPush) {
                    MsgItemPush itemView = (MsgItemPush) holder;
                    itemView.message_status.setBadgeNumber(0);
                }

                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(model, position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnLongItemClickListener != null) {
                    mOnLongItemClickListener.onItemLongClick(v, model);
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


    private void setAtText(String keyword, String content, TextView view) {
        if (TextUtils.isEmpty(keyword) || TextUtils.isEmpty(content)) {
            view.setText(content);
        } else {
            int start = content.indexOf(keyword);
            if (start == -1) {
                view.setText(content);
            } else {
                int length = keyword.length();
                SpannableStringBuilder style = new SpannableStringBuilder(content);
                style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, com.youmai.hxsdk.R.color.hx_color_red_tag)),
                        start, start + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                view.setText(style);
            }
        }

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
        void onItemLongClick(View v, ExCacheMsgBean bean);
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
        TextView message_name, message_info, message_type, message_time;
        QBadgeView message_status;
        RelativeLayout message_item;

        public MsgItemPush(View itemView) {
            super(itemView);
            message_item = (RelativeLayout) itemView.findViewById(R.id.message_itme);
            message_icon = (ImageView) itemView.findViewById(R.id.message_icon);
            message_callBtn = (ImageView) itemView.findViewById(R.id.message_call_btn);
            message_name = (TextView) itemView.findViewById(R.id.message_name);
            message_type = (TextView) itemView.findViewById(R.id.message_type);
            message_info = (TextView) itemView.findViewById(R.id.message_info);
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
        RelativeLayout icon_layout;

        public MsgItemChat(View itemView) {
            super(itemView);
            message_item = (RelativeLayout) itemView.findViewById(R.id.message_itme);
            icon_layout = (RelativeLayout) itemView.findViewById(R.id.icon_layout);
            message_icon = (ImageView) itemView.findViewById(R.id.message_icon);
            message_callBtn = (ImageView) itemView.findViewById(R.id.message_call_btn);
            message_name = (TextView) itemView.findViewById(R.id.message_name);
            message_type = (TextView) itemView.findViewById(R.id.message_type);
            message_time = (TextView) itemView.findViewById(R.id.message_time);
            message_status = new QBadgeView(mContext);
            message_status.bindTarget(icon_layout);
            message_status.setBadgeGravity(Gravity.TOP | Gravity.END);
            message_status.setBadgeTextSize(10f, true);
            message_status.setBadgeBackgroundColor(ContextCompat.getColor(mContext, R.color.hx_color_red_tag));
            message_status.setShowShadow(false);
        }
    }

    protected class MsgItemGroup extends RecyclerView.ViewHolder {
        TeamHeadView message_icon;
        ImageView message_callBtn;
        TextView message_name, message_type, message_time;
        QBadgeView message_status;
        RelativeLayout message_item;
        RelativeLayout icon_layout;

        public MsgItemGroup(View itemView) {
            super(itemView);
            message_item = (RelativeLayout) itemView.findViewById(R.id.message_itme);
            icon_layout = (RelativeLayout) itemView.findViewById(R.id.icon_layout);
            message_icon = (TeamHeadView) itemView.findViewById(R.id.message_icon);
            message_callBtn = (ImageView) itemView.findViewById(R.id.message_call_btn);
            message_name = (TextView) itemView.findViewById(R.id.message_name);
            message_type = (TextView) itemView.findViewById(R.id.message_type);
            message_time = (TextView) itemView.findViewById(R.id.message_time);
            message_status = new QBadgeView(mContext);
            message_status.bindTarget(icon_layout);
            message_status.setBadgeGravity(Gravity.TOP | Gravity.END);
            message_status.setBadgeTextSize(10f, true);
            message_status.setBadgeBackgroundColor(ContextCompat.getColor(mContext, R.color.hx_color_red_tag));
            message_status.setShowShadow(false);
        }
    }
}
