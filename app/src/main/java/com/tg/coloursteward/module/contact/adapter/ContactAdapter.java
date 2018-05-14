package com.tg.coloursteward.module.contact.adapter;

import android.content.Context;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.List;

/**
 * Created by yw on 2018/4/13.
 */
public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        StickyHeaderAdapter<ContactAdapter.HeaderHolder>, MessageHandler.ResponseListener {

    enum TYPE {
        SEARCH, HEADER, DEFAULT
    }

    public static final int mIndexForContact = 5;  //搜索 //组织架构 //我的部门 //手机联系人 //群聊

    private Context mContext;
    private int mCollectIndex;
    private ItemEventListener itemEventListener;
    private final List<CNPinyin<ContactBean>> cnPinyinList;

    public ContactAdapter(Context context, List<CNPinyin<ContactBean>> cnPinyinList, int collectIndex, ItemEventListener listener) {
        this.mContext = context.getApplicationContext();
        this.cnPinyinList = cnPinyinList;
        this.mCollectIndex = collectIndex;
        this.itemEventListener = listener;

        msgHandler = new MessageHandler(context);
        msgHandler.setResponseListener(this);
    }


    @Override
    public int getItemCount() {
        return cnPinyinList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        if (position == 0) {
            type = TYPE.SEARCH.ordinal();
        } else if (position > 0 && position < mCollectIndex) {
            type = TYPE.HEADER.ordinal();
        } else {
            type = TYPE.DEFAULT.ordinal();
        }
        return type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE.SEARCH.ordinal()) {
            return new SearchHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.global_list_item_header_search, parent, false));
        } else if (viewType == TYPE.HEADER.ordinal()) {
            return new ContactHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contacts_fragment_item, parent, false));
        } else {
            return new ContactHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.contacts_fragment_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ContactBean contact = cnPinyinList.get(position).data;

        if (holder instanceof ContactHolder) {
            final ContactHolder contactHolder = (ContactHolder) holder;
            if (position > 0 && position < mCollectIndex) {
                int icon = defaultIcon(position);
                contactHolder.iv_header.setImageResource(icon);
                contactHolder.cb_collect.setVisibility(View.GONE);
            } else {
                contactHolder.cb_collect.setVisibility(View.GONE);
                try {
                    Glide.with(mContext)
                            .load(contact.getAvatar())
                            .apply(new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .centerCrop()
                                    .transform(new GlideRoundTransform())
                                    .placeholder(R.drawable.color_default_header)
                                    .error(R.drawable.color_default_header))
                            .into(contactHolder.iv_header);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != itemEventListener) {
                        itemEventListener.onItemClick(position, contact);
                    }
                }
            });

            if (contact.getRealname().startsWith("↑##@@**") && position <= mCollectIndex) {
                contactHolder.tv_name.setText(contact.getRealname().substring(9));
            } else {
                contactHolder.tv_name.setText(contact.getRealname());
            }

        }
        if (holder instanceof SearchHolder) {
            //搜索框不处理
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != itemEventListener) {
                        itemEventListener.onItemClick(position, contact);
                    }
                }
            });
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != itemEventListener) {
                    //Toast.makeText(mContext, "长按position：" + position, Toast.LENGTH_SHORT).show();
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

    public class SearchHolder extends RecyclerView.ViewHolder {
        public SearchHolder(View itemView) {
            super(itemView);
        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        private TextView tv_header;

        public HeaderHolder(View itemView) {
            super(itemView);
            tv_header = (TextView) itemView.findViewById(R.id.tv_header);
        }
    }


    public class ContactHolder extends RecyclerView.ViewHolder {
        private ImageView iv_header;
        private TextView tv_name;
        private CheckBox cb_collect;

        public ContactHolder(View itemView) {
            super(itemView);
            iv_header = (ImageView) itemView.findViewById(R.id.iv_header);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
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
        void onRequest(MessageHandler msgHand);

        void onSuccess(Message msg, String response);
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
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
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

    }

}
