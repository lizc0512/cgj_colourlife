package com.youmai.hxsdk.group.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.group.AddContactsCreateGroupActivity;
import com.youmai.hxsdk.utils.GlideRoundTransform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：create by YW
 * 日期：2018.04.20 18:03
 * 描述：搜索 - 我的部门
 */
public class DepartAdapter extends RecyclerView.Adapter {


    public enum TYPE {
        ORG_TYPE, MEMBER_TYPE
    }


    private Context mContext;
    private List<ContactBean> mDataList;
    private Map<String, ContactBean> mTotalMap = new HashMap<>();
    private Map<String, ContactBean> groupMap = new HashMap<>();

    private ItemEventListener callback;

    public DepartAdapter(Context context) {
        this.mContext = context;
    }

    public void setDataList(List<ContactBean> dataList) {
        this.mDataList = dataList;
        notifyDataSetChanged();
    }

    public List<ContactBean> getDataList() {
        return mDataList;
    }

    public void setCacheMap(Map<String, ContactBean> map) {
        mTotalMap = map;
        notifyDataSetChanged();
    }

    public void setGroupMap(Map<String, ContactBean> map) {
        this.groupMap = map;
    }

    @Override
    public int getItemViewType(int position) {
        final ContactBean contact = mDataList.get(position);
        if (contact.getOrgType().equals("org")) {
            return TYPE.ORG_TYPE.ordinal();
        }
        return TYPE.MEMBER_TYPE.ordinal();
    }

    public void setCallback(ItemEventListener callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == TYPE.ORG_TYPE.ordinal()) {
            View view = inflater.inflate(R.layout.contacts_fragment_item, parent, false);
            return new OrgHolder(view);
        } else {
            View view = inflater.inflate(R.layout.group_search_item_layout, parent, false);
            return new MemberHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final ContactBean contact = mDataList.get(position);
        if (holder instanceof OrgHolder) {
            ((OrgHolder) holder).cb_collect.setVisibility(View.GONE);
            ((OrgHolder) holder).iv_header.setImageResource(R.drawable.contacts_department);
            ((OrgHolder) holder).tv_name.setText(contact.getRealname());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        callback.onItemClick(holder.getAdapterPosition(), contact);
                    }

                }
            });


        } else if (holder instanceof MemberHolder) {
            if (null != groupMap && null != groupMap.get(contact.getUuid())) {
                ((MemberHolder) holder).cb_collect.setButtonDrawable(R.drawable.contact_select_def);
                ((MemberHolder) holder).search_name.setText(contact.getRealname());
            } else {
                ((MemberHolder) holder).search_name.setText(contact.getRealname());
                ((MemberHolder) holder).cb_collect.setButtonDrawable(R.drawable.contacts_select_selector);
                if (mTotalMap.get(contact.getUuid()) != null) {
                    ((MemberHolder) holder).cb_collect.setChecked(true);
                } else {
                    ((MemberHolder) holder).cb_collect.setChecked(false);
                }
            }

            try {
                int size = mContext.getResources().getDimensionPixelOffset(com.youmai.hxsdk.R.dimen.card_head);
                Glide.with(mContext)
                        .load(contact.getAvatar())
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop()
                                .override(size, size)
                                .transform(new GlideRoundTransform())
                                .placeholder(R.drawable.color_default_header)
                                .error(R.drawable.color_default_header))
                        .into(((MemberHolder) holder).search_icon);

            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mTotalMap) {
                        if (null != groupMap && null != groupMap.get(contact.getUuid())) {
                            return;
                        }
                        if (mTotalMap.get(contact.getUuid()) != null) {
                            ((MemberHolder) holder).cb_collect.setChecked(false);
                        } else {
                            ((MemberHolder) holder).cb_collect.setChecked(true);
                        }
                        Intent intent = new Intent(AddContactsCreateGroupActivity.BROADCAST_FILTER);
                        intent.putExtra(AddContactsCreateGroupActivity.ACTION, AddContactsCreateGroupActivity.DEPART_CONTACT);
                        intent.putExtra("bean", contact);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    }
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    class MemberHolder extends RecyclerView.ViewHolder {
        ImageView search_icon;
        TextView search_name;
        RelativeLayout search_item;
        CheckBox cb_collect;

        MemberHolder(View itemView) {
            super(itemView);
            cb_collect = itemView.findViewById(R.id.cb_collect);
            search_icon = itemView.findViewById(R.id.global_search_icon);
            search_name = itemView.findViewById(R.id.global_search_name);
            search_item = itemView.findViewById(R.id.global_search_item);
        }
    }

    class OrgHolder extends RecyclerView.ViewHolder {
        private ImageView iv_header;
        private TextView tv_name;
        private CheckBox cb_collect;

        OrgHolder(View itemView) {
            super(itemView);
            iv_header = itemView.findViewById(com.youmai.hxsdk.R.id.iv_header);
            tv_name = itemView.findViewById(com.youmai.hxsdk.R.id.tv_name);
            cb_collect = itemView.findViewById(com.youmai.hxsdk.R.id.cb_collect);
        }
    }

    public interface ItemEventListener {
        void onItemClick(int pos, ContactBean contact);
    }


}
