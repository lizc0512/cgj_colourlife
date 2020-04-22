package com.youmai.hxsdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.entity.cn.CNPinyin;
import com.youmai.hxsdk.group.adapter.SearchContactAdapter;
import com.youmai.hxsdk.stickyheader.StickyHeaderAdapter;
import com.youmai.hxsdk.utils.GlideRoundTransform;

import java.util.List;

/**
 * Created by yw on 2018/4/13.
 */
public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        StickyHeaderAdapter<ContactAdapter.HeaderHolder> {

    public interface ItemEventListener {
        void onItemClick(int pos, ContactBean contact);

        void onLongClick(int pos, ContactBean contact);
    }

    private Context mContext;
    private int mCollectIndex;
    private ItemEventListener itemEventListener;
    private final List<CNPinyin<ContactBean>> cnPinyinList;
    public String org_name;

    public ContactAdapter(Context context, List<CNPinyin<ContactBean>> cnPinyinList, int collectIndex, ItemEventListener listener) {
        this.mContext = context.getApplicationContext();
        this.cnPinyinList = cnPinyinList;
        this.mCollectIndex = collectIndex;
        this.itemEventListener = listener;
    }

    public void removeItem(int position) {
        cnPinyinList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cnPinyinList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_fragment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final ContactBean contact = cnPinyinList.get(position).data;

        if (holder instanceof ContactHolder) {
            final ContactHolder contactHolder = (ContactHolder) holder;
            if (contact.getUiType() != SearchContactAdapter.TYPE.CONTACT_TYPE.ordinal()) {
                Glide.with(mContext).load(contact.getResId()).apply(new RequestOptions()
                        .placeholder(R.drawable.color_default_header)
                        .error(R.drawable.color_default_header))
                        .into(contactHolder.iv_header);
                contactHolder.cb_collect.setVisibility(View.GONE);
                contactHolder.tv_user_name.setVisibility(View.VISIBLE);
                contactHolder.tv_user_name.setText(contact.getOrgName());
            } else {
                contactHolder.cb_collect.setVisibility(View.GONE);
                contactHolder.tv_user_name.setVisibility(View.GONE);
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
            if (contact.getRealname().startsWith("↑##@@**") && position < mCollectIndex) {
                contactHolder.tv_name.setText(contact.getRealname().substring(9));
            } else {
                contactHolder.tv_name.setText(contact.getRealname());
            }

        }

        //搜索框不处理
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
                    itemEventListener.onLongClick(position, contact);
                }
                return true;
            }
        });
    }

    @Override
    public long getHeaderId(int childAdapterPosition) {
        if (childAdapterPosition < mCollectIndex) {
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


    class HeaderHolder extends RecyclerView.ViewHolder {
        private TextView tv_header;

        HeaderHolder(View itemView) {
            super(itemView);
            tv_header = (TextView) itemView.findViewById(R.id.tv_header);
        }
    }


    class ContactHolder extends RecyclerView.ViewHolder {
        private ImageView iv_header;
        private TextView tv_name;
        private TextView tv_user_name;
        private CheckBox cb_collect;

        ContactHolder(View itemView) {
            super(itemView);
            iv_header = (ImageView) itemView.findViewById(R.id.iv_header);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_user_name = (TextView) itemView.findViewById(R.id.tv_user_name);
            cb_collect = itemView.findViewById(R.id.cb_collect);
        }
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


}
