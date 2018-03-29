package com.tg.coloursteward.meassage;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
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

import com.tg.coloursteward.R;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.view.chat.emoticon.utils.EmoticonHandler;
import com.youmai.hxsdk.view.chat.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import q.rorbin.badgeview.QBadgeView;

/**
 * Created by youmai on 17/2/14.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = "MessageAdapter";

    public static final int ADAPTER_TYPE_HEADER = 1;
    public static final int ADAPTER_TYPE_NORMAL = 2;

    private final int NEW_MSG_UPDATE = 100;
    private final int NEW_MSG_UPDATE_COUNT = 101;
    private final int UPDATE_MSG_BY_INDEX = 102;
    private final int ITEM_SEARCH_HEADER = 1;
    private final int ITEM_NORMAL = 2;
    private final int HEADER_COUNT = 1;
    private int mAdapterType = ADAPTER_TYPE_NORMAL;

    private boolean mShowRightIcon = true;
    private boolean mShowUnread = true;

    private Context mContext;
    private int pageType;
    private List<ExCacheMsgBean> messageList = new ArrayList();
    private int mUpdateCount = 0;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnLongItemClickListener;
    private OnItemClickListener mOnRightButtonClickListener;
    private OnItemClickListener mOnAvatarButtonClickListener;

    public MessageAdapter(Context context) {
        mContext = context;
    }

    public int getPageType() {
        return pageType;
    }

    public void setPageType(int pageType) {
        this.pageType = pageType;
    }

    public boolean isShowRightIcon() {
        return mShowRightIcon;
    }

    public void setShowRightIcon(boolean show) {
        this.mShowRightIcon = show;
    }

    public void setShowUnread(boolean show) {
        this.mShowUnread = show;
    }

    public List<ExCacheMsgBean> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<ExCacheMsgBean> messageList) {
        this.messageList = messageList;
        notifyDataSetChanged();
    }

    public void addMessageList(List<ExCacheMsgBean> messageList) {
        int from = this.messageList.size();
        this.messageList.addAll(messageList);
        if (from == 0) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeChanged(from, messageList.size());
            //notifyDataSetChanged();
        }
    }

    public void deleteMessage(int pos) {
        messageList.remove(pos);
        notifyDataSetChanged();
        //notifyItemRemoved(pos + getHeaderCount());
    }

    public void clearMessageList() {
        this.messageList.clear();
        //notifyDataSetChanged();
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

        if (viewType == ITEM_NORMAL) {
            View view = inflater.inflate(R.layout.message_item_layout, parent, false);
            MessageViewItem viewItem = new MessageViewItem(view);
            return viewItem;
        } else {
            View view = inflater.inflate(R.layout.message_list_item_header_search, parent, false);
            MessageViewSearchItem viewItem = new MessageViewSearchItem(view);
            return viewItem;
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MessageViewSearchItem) {
            MessageViewSearchItem viewHeader = (MessageViewSearchItem) holder;
            viewHeader.header_item.setTag(position);
            viewHeader.header_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(null, position);
                    }
                }
            });
        } else {
            int currPos = position - getHeaderCount();
            final MessageViewItem itemView = (MessageViewItem) holder;
            final ExCacheMsgBean model = messageList.get(currPos);

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
                        mOnLongItemClickListener.onItemLongClick(v, position - getHeaderCount());
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int count = messageList.size();
        if (mAdapterType == ADAPTER_TYPE_HEADER) {
            return count + HEADER_COUNT;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (mAdapterType == ADAPTER_TYPE_NORMAL) {
            return ITEM_NORMAL;
        } else {
            if (position >= 0 && position < getHeaderCount()) {
                return ITEM_SEARCH_HEADER;
            } else {
                return ITEM_NORMAL;
            }
        }
    }

    public void addTop(ExCacheMsgBean msgBean) {
        String phone = msgBean.getTargetPhone();
        if (messageList.isEmpty()) {
            //没有聊天消息
            messageList.add(0, msgBean);
        } else {
            int i;
            for (i = 0; i < messageList.size(); i++) {
                if (messageList.get(i).getTargetPhone().equals(phone)) {
                    if (i == 0) {
                        messageList.set(0, msgBean);
                    } else {
                        messageList.remove(messageList.get(i));
                        messageList.add(0, msgBean);
                        Comparator comp = new SortComparator();
                        Collections.sort(messageList.subList(0, messageList.size()), comp);
                    }
                    break;
                }
            }
            if (i == messageList.size()) {
                messageList.add(0, msgBean);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 未读消息icon
     */
    private void setUnreadIcon(QBadgeView message_status, boolean show) {

    }

    public int getHeaderCount() {
        if (mAdapterType == ADAPTER_TYPE_HEADER) {
            return HEADER_COUNT;
        }
        return 0;
    }

    public int getAdapterType() {
        return mAdapterType;
    }

    public void setAdapterType(int type) {
        this.mAdapterType = type;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnLongItemClickListener(OnItemLongClickListener listener) {
        this.mOnLongItemClickListener = listener;
    }

    public void setOnRightButtonClickListener(OnItemClickListener listener) {
        this.mOnRightButtonClickListener = listener;
    }

    public void setOnAvatarButtonClickListener(OnItemClickListener listener) {
        this.mOnAvatarButtonClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(ExCacheMsgBean bean, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public class MessageViewSearchItem extends RecyclerView.ViewHolder {
        LinearLayout header_item;

        public MessageViewSearchItem(View itemView) {
            super(itemView);
            header_item = (LinearLayout) itemView.findViewById(R.id.list_item_header_search_root);
        }
    }

    protected class MessageViewItem extends RecyclerView.ViewHolder {
        ImageView message_icon, message_callBtn;
        TextView message_name, message_type, message_time;
        QBadgeView message_status;
        RelativeLayout message_item;

        public MessageViewItem(View itemView) {
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
