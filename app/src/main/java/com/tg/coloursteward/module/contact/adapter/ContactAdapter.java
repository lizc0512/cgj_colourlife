package com.tg.coloursteward.module.contact.adapter;

import android.content.Context;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.youmai.hxsdk.db.bean.Contact;
import com.youmai.hxsdk.entity.cn.CNPinyin;
import com.youmai.hxsdk.utils.GlideRoundTransform;

import java.util.List;

/**
 * Created by yw on 2018/4/13.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> implements
        StickyHeaderAdapter<ContactAdapter.HeaderHolder>, MessageHandler.ResponseListener {

    private Context mContext;
    private ItemEventListener itemEventListener;
    private final List<CNPinyin<Contact>> cnPinyinList;

    public ContactAdapter(Context context, List<CNPinyin<Contact>> cnPinyinList, ItemEventListener listener) {
        this.mContext = context.getApplicationContext();
        this.cnPinyinList = cnPinyinList;
        this.itemEventListener = listener;

        msgHandler = new MessageHandler(context);
        msgHandler.setResponseListener(this);
    }

    @Override
    public int getItemCount() {
        return cnPinyinList.size();
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_fragment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ContactHolder holder, final int position) {
        final Contact contact = cnPinyinList.get(position).data;

        if (position < 4) {
            int icon = defaultIcon(position);
            holder.iv_header.setImageResource(icon);
        } else {
            RequestOptions options = new RequestOptions();
            options.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .transform(new GlideRoundTransform(mContext))
                    .centerCrop()
                    .error(R.drawable.contacts_common_default_user_bg);
            Glide.with(mContext)
                    .load(contact.getAvatar())
                    .apply(options)
                    .into(holder.iv_header);
        }

        if (contact.getNick_name().startsWith("↑##@@**") && position < 4) {
            holder.tv_name.setText(contact.getNick_name().substring(9));
        } else {
            holder.tv_name.setText(contact.getNick_name());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != itemEventListener) {
                    itemEventListener.onItemClick(position, contact);
                }
            }
        });

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
        return cnPinyinList.get(childAdapterPosition).getFirstChar();
    }

    @Override
    public boolean specialIndex(int childAdapterPosition) {
        CNPinyin<Contact> contactCNPinyin = cnPinyinList.get(childAdapterPosition);
        return contactCNPinyin.getHeaderFilter().contains(contactCNPinyin.data.getNick_name());
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
        public final TextView tv_header;

        public HeaderHolder(View itemView) {
            super(itemView);
            tv_header = (TextView) itemView.findViewById(R.id.tv_header);
        }
    }

    public class ContactHolder extends RecyclerView.ViewHolder {
        public final ImageView iv_header;
        public final TextView tv_name;

        public ContactHolder(View itemView) {
            super(itemView);
            iv_header = (ImageView) itemView.findViewById(R.id.iv_header);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }

    public interface ItemEventListener {
        void onItemClick(int pos, Contact contact);

        void onLongClick(int pos);
    }

    /**
     * 默认功能的头像
     * @param position
     * @return
     */
    int defaultIcon(int position) {
        int icon = -1;
        switch (position) {
            case 0:
                icon = R.drawable.contacts_org;
                break;
            case 1:
                icon = R.drawable.contacts_department;
                break;
            case 2:
                icon = R.drawable.contacts_phone_list;
                break;
            case 3:
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
        isLoadding= false;
    }

    @Override
    public void onFail(Message msg, String hintString) {
        // TODO Auto-generated method stub
    }

}
