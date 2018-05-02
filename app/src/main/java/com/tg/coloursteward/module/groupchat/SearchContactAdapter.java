package com.tg.coloursteward.module.groupchat;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tg.coloursteward.module.contact.stickyheader.StickyHeaderAdapter;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.entity.cn.CNPinyin;
import com.youmai.hxsdk.utils.GlideRoundTransform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yw on 2018/4/13.
 */
public class SearchContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        StickyHeaderAdapter<SearchContactAdapter.HeaderHolder>, MessageHandler.ResponseListener {

    enum TYPE {
        SEARCH, COLLECT, DEFAULT
    }

    public static final int mIndexForCollect = 1;
    public static final int mIndexForContact = 5;

    private Map<Integer, ContactBean> mCacheMap;
    private Map<String, ContactBean> mTotalMap = new HashMap<>();
    private Map<String, ContactBean> groupMap = new HashMap<>();

    private Context mContext;
    private int mCollectIndex = 5;
    private ItemEventListener itemEventListener;
    private final List<CNPinyin<ContactBean>> cnPinyinList;

    public SearchContactAdapter(Context context, List<CNPinyin<ContactBean>> cnPinyinList, int collectIndex, ItemEventListener listener) {
        this.mContext = context.getApplicationContext();
        this.cnPinyinList = cnPinyinList;
        this.mCollectIndex = collectIndex;
        this.itemEventListener = listener;

        if (mCollectIndex == mIndexForCollect) {
            mCacheMap = new HashMap(cnPinyinList.size());
        }

        msgHandler = new MessageHandler(context);
        msgHandler.setResponseListener(this);
    }

    public Map<Integer, ContactBean> getCacheMap() {
        return mCacheMap;
    }

    //刷Adapter
    public void setCacheMap(Map<String, ContactBean> map) {
        this.mTotalMap = map;
        notifyDataSetChanged();
    }

    //不刷Adapter
    public void setMap(Map<String, ContactBean> map) {
        this.mTotalMap = map;
    }

    public void setGroupMap(Map<String, ContactBean> map) {
        this.groupMap = map;
    }

    @Override
    public int getItemCount() {
        return cnPinyinList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        if (position == mCollectIndex) {
            type = TYPE.COLLECT.ordinal();
        } else {
            type = TYPE.DEFAULT.ordinal();
        }
        return type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE.COLLECT.ordinal()) {
            return new CollectHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.collect_fragment_item, parent, false));
        } else {
            return new ContactHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contacts_fragment_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ContactBean contact = cnPinyinList.get(position).data;

        if (holder instanceof ContactHolder) {
            if (position >= 0 && position < mCollectIndex) {
                int icon = defaultIcon(position);
                ((ContactHolder) holder).iv_header.setImageResource(icon);
                ((ContactHolder) holder).cb_collect.setVisibility(View.GONE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != itemEventListener) {
                            itemEventListener.onItemClick(position, contact);
                        }
                    }
                });
            } else {
                if (null != mCacheMap) {
                    if (mCollectIndex == mIndexForCollect) {
                        ((ContactHolder) holder).cb_collect.setVisibility(View.VISIBLE);
                    } else {
                        ((ContactHolder) holder).cb_collect.setVisibility(View.GONE);
                    }

                    Log.e("YW", "realname: " + contact.getRealname());


                    Log.e("YW", "contact.getUuid(): " + contact.getUuid() + "\tcheck: " + (groupMap.get(contact.getUuid()) == null ?
                            "null" : groupMap.get(contact.getUuid()).getUuid()));

                    for (Map.Entry<String, ContactBean> entry: groupMap.entrySet()) {
                        ContactBean item = entry.getValue();

                        Log.e("YW", "迭代: " + item.getUuid());
                    }

                    if (null != groupMap && null != groupMap.get(contact.getUuid())) {
                        ((ContactHolder) holder).cb_collect.setButtonDrawable(R.drawable.contact_select_def);
                    } else {
                        ((ContactHolder) holder).cb_collect.setButtonDrawable(R.drawable.contacts_select_selector);
                        if (mTotalMap.get(contact.getUuid()) != null) {
                            ((ContactHolder) holder).cb_collect.setChecked(true);
                        } else {
                            ((ContactHolder) holder).cb_collect.setChecked(false);
                        }
                    }
                }

                try {
                    Glide.with(mContext)
                            .load(contact.getAvatar())
                            .apply(new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .centerCrop()
                                    .override(120, 120)
                                    .transform(new GlideRoundTransform())
                                    .placeholder(R.drawable.color_default_header)
                                    .error(R.drawable.color_default_header))
                            .into(((ContactHolder) holder).iv_header);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position > mCollectIndex && null != mCacheMap) {
                            if (null != groupMap && null != groupMap.get(contact.getUuid())) {
                                return;
                            }
                            if (((ContactHolder) holder).cb_collect.isChecked()) {
                                mCacheMap.remove(position);
                                ((ContactHolder) holder).cb_collect.setChecked(false);
                            } else {
                                mCacheMap.put(position, contact);
                                ((ContactHolder) holder).cb_collect.setChecked(true);
                            }
                            Intent intent = new Intent(AddContactsCreateGroupActivity.BROADCAST_FILTER);
                            intent.putExtra(AddContactsCreateGroupActivity.ACTION, AddContactsCreateGroupActivity.ADAPTER_CONTACT);
                            intent.putExtra("bean", contact);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                            if (null != itemEventListener) {
                                itemEventListener.onItemClick(position, contact);
                                itemEventListener.collectCount(mCacheMap.size());
                            }
                        }
                    }
                });
            }

            if (contact.getRealname().startsWith("↑##@@**") && position <= mCollectIndex) {
                ((ContactHolder) holder).tv_name.setText(contact.getRealname().substring(9));
            } else {
                ((ContactHolder) holder).tv_name.setText(contact.getRealname());
            }

        } else if (holder instanceof CollectHolder) {
            ((CollectHolder) holder).itemView.setBackgroundColor(0xF5F5F5);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != itemEventListener) {
                    Toast.makeText(mContext, "长按position：" + position, Toast.LENGTH_SHORT).show();
                    itemEventListener.onLongClick(position);
                }
                return true;
            }
        });
    }

    @Override
    public long getHeaderId(int childAdapterPosition) {
        if (childAdapterPosition <= mCollectIndex) {
            return '↑';
        } else {
            return cnPinyinList.get(childAdapterPosition).getFirstChar();
        }
    }

    @Override
    public boolean specialIndex(int childAdapterPosition) {
        CNPinyin<ContactBean> contactCNPinyin = cnPinyinList.get(childAdapterPosition);
        return contactCNPinyin.getHeaderFilter().contains(contactCNPinyin.data.getRealname());
    }

    @Override
    public String index(int childAdapterPosition) {
        return cnPinyinList.get(childAdapterPosition).getFirstChar() + "";
    }

    @Override
    public void onBindHeaderViewHolder(HeaderHolder holder, int childAdapterPosition) {
        holder.tv_header.setText(String.valueOf(cnPinyinList.get(childAdapterPosition).getFirstChar()));
    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new HeaderHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_header, parent, false));
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        private TextView tv_header;

        public HeaderHolder(View itemView) {
            super(itemView);
            tv_header = itemView.findViewById(R.id.tv_header);
        }
    }

    public class CollectHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;

        public CollectHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }

    public class ContactHolder extends RecyclerView.ViewHolder {
        private ImageView iv_header;
        private TextView tv_name;
        private CheckBox cb_collect;

        public ContactHolder(View itemView) {
            super(itemView);
            iv_header = itemView.findViewById(R.id.iv_header);
            tv_name = itemView.findViewById(R.id.tv_name);
            cb_collect = itemView.findViewById(R.id.cb_collect);
        }
    }

    public interface ItemEventListener {
        void onItemClick(int pos, ContactBean contact);

        void onLongClick(int pos);

        void collectCount(int count);
    }

    /**
     * 默认功能的头像
     *
     * @param position
     * @return
     */
    int defaultIcon(int position) {
        int icon = -1;
        switch (position) {
            case 1:
                icon = R.drawable.contacts_org;
                break;
            case 0:
            case 2:
                icon = R.drawable.contacts_department;
                break;
            case 3:
                icon = R.drawable.contacts_phone_list;
                break;
            case 4:
                icon = R.drawable.contacts_groupchat;
                break;
        }
        return icon;
    }

    //--------------------------------------------------------------
    public interface NetRelativeRequestListener {
        public void onRequest(MessageHandler msgHand);

        public void onSuccess(Message msg, String response);
    }

    private NetRelativeRequestListener requestListener;
    private boolean isLoadding = false;
    private MessageHandler msgHandler;

    public void loadingData() {
        if (!isLoadding) {
            if (requestListener != null) {
                isLoadding = true;
                requestListener.onRequest(msgHandler);
            }
        }
    }

    public void setNetworkRequestListener(NetRelativeRequestListener l) {
        requestListener = l;
    }

    @Override
    public void onRequestStart(Message msg, String hintString) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        // TODO Auto-generated method stub
        int code = HttpTools.getCode(jsonString);
        if (code == 0) {
            if (requestListener != null) {
                requestListener.onSuccess(msg, jsonString);
            }
        }
        isLoadding = false;
    }

    @Override
    public void onFail(Message msg, String hintString) {
        // TODO Auto-generated method stub
    }

}
