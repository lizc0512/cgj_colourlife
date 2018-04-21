package com.tg.coloursteward.module.groupchat.addcontact;

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
import com.tg.coloursteward.R;
import com.tg.coloursteward.module.groupchat.AddContactsCreateGroupActivity;
import com.youmai.hxsdk.db.bean.Contact;
import com.youmai.hxsdk.utils.GlideRoundTransform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：create by YW
 * 日期：2018.04.20 18:03
 * 描述：
 */
public class DepartAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<Contact> mDataList;
    private Map<String, Contact> mTotalMap = new HashMap<>();

    public DepartAdapter(Context context) {
        this.mContext = context;
        mDataList = new ArrayList<>();
    }

    public void setDataList(List<Contact> dataList) {
        this.mDataList = dataList;
        notifyDataSetChanged();
    }

    public List<Contact> getDataList() {
        return mDataList;
    }

    public void setCacheMap(Map<String, Contact> map) {
        mTotalMap = map;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(com.youmai.hxsdk.R.layout.group_search_item_layout, parent, false);
        return new SearchItem(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final SearchItem searchItemHolder = (SearchItem) holder;
        final Contact contact = mDataList.get(position);
        searchItemHolder.search_name.setText(contact.getRealname());

        if (mTotalMap.get(contact.getUuid()) != null) {
            searchItemHolder.cb_collect.setChecked(true);
        } else {
            searchItemHolder.cb_collect.setChecked(false);
        }

        try {
            Glide.with(mContext)
                    .load(contact.getAvatar())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .centerCrop()
                            .override(60, 60)
                            .transform(new GlideRoundTransform(mContext))
                            .placeholder(R.drawable.color_default_header)
                            .error(R.drawable.color_default_header))
                    .into(searchItemHolder.search_icon);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mTotalMap) {
                    if (mTotalMap.get(contact.getUuid()) != null) {
                        searchItemHolder.cb_collect.setChecked(false);
                    } else {
                        searchItemHolder.cb_collect.setChecked(true);
                    }
                    Intent intent = new Intent(AddContactsCreateGroupActivity.BROADCAST_FILTER);
                    intent.putExtra(AddContactsCreateGroupActivity.ACTION, AddContactsCreateGroupActivity.DEPART_CONTACT);
                    intent.putExtra("bean", contact);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class SearchItem extends RecyclerView.ViewHolder {
        ImageView search_icon;
        TextView search_name;
        RelativeLayout search_item;
        CheckBox cb_collect;

        public SearchItem(View itemView) {
            super(itemView);
            cb_collect = itemView.findViewById(com.youmai.hxsdk.R.id.cb_collect);
            search_icon = itemView.findViewById(com.youmai.hxsdk.R.id.global_search_icon);
            search_name = itemView.findViewById(com.youmai.hxsdk.R.id.global_search_name);
            search_item = itemView.findViewById(com.youmai.hxsdk.R.id.global_search_item);
        }
    }

}
