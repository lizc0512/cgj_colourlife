package com.tg.coloursteward.module.groupchat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;

import java.util.List;

import q.rorbin.badgeview.QBadgeView;

/**
 * 作者：create by YW
 * 日期：2018.04.14 14:53
 * 描述：
 */
public class GroupListAdapter extends RecyclerView.Adapter {

    public static final int ADAPTER_TYPE_HEADER = 1;
    public static final int ADAPTER_TYPE_NORMAL = 2;
    private final int HEADER_COUNT = 1;
    private final int ITEM_NORMAL = 2;
    private int mAdapterType = ADAPTER_TYPE_NORMAL;

    private Context mContext;
    private List<Group> mGroupList;

    public GroupListAdapter(Context context) {
        this.mContext = context;
    }

    public void setGroupList(List<Group> list) {
        this.mGroupList = list;
        notifyDataSetChanged();
    }

    public List<Group> getMessageList() {
        return mGroupList;
    }

    public void deleteMessage(int position) {
        mGroupList.remove(position);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (viewType == ITEM_NORMAL) {
            View view = inflater.inflate(R.layout.message_item_layout, parent, false);
            GroupViewHolder viewItem = new GroupViewHolder(view);
            return viewItem;
        } else {
            View view = inflater.inflate(R.layout.message_list_item_header_search, parent, false);
            GroupViewSearchItem viewItem = new GroupViewSearchItem(view);
            return viewItem;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof GroupViewSearchItem) {
            GroupViewSearchItem viewHeader = (GroupViewSearchItem) holder;
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
            int currPos = position - getHeaderCount(); //实际的位置
            GroupViewHolder itemView = (GroupViewHolder) holder;
        }
    }

    @Override
    public int getItemCount() {
        int count = mGroupList.size();
        if (mAdapterType == ADAPTER_TYPE_HEADER) {
            return count + HEADER_COUNT;
        }
        return count;
    }

    public int getHeaderCount() {
        if (mAdapterType == ADAPTER_TYPE_HEADER) {
            return HEADER_COUNT;
        }
        return 0;
    }

    public class GroupViewSearchItem extends RecyclerView.ViewHolder {
        LinearLayout header_item;

        public GroupViewSearchItem(View itemView) {
            super(itemView);
            header_item = (LinearLayout) itemView.findViewById(R.id.list_item_header_search_root);
        }
    }

    protected class GroupViewHolder extends RecyclerView.ViewHolder {
        ImageView message_icon, message_callBtn;
        TextView message_name, message_type, message_time;
        RelativeLayout message_item;

        public GroupViewHolder(View itemView) {
            super(itemView);
            message_item = (RelativeLayout) itemView.findViewById(R.id.message_itme);
            message_icon = (ImageView) itemView.findViewById(R.id.message_icon);
            message_callBtn = (ImageView) itemView.findViewById(R.id.message_call_btn);
            message_name = (TextView) itemView.findViewById(R.id.message_name);
            message_type = (TextView) itemView.findViewById(R.id.message_type);
            message_time = (TextView) itemView.findViewById(R.id.message_time);
        }
    }

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnLongItemClickListener;

    public void setAdapterType(int type) {
        this.mAdapterType = type;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnLongItemClickListener(OnItemLongClickListener listener) {
        this.mOnLongItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Group bean, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
}