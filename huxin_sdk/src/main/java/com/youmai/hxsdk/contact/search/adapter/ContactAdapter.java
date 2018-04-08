package com.youmai.hxsdk.contact.search.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.contact.search.cn.CNPinyin;
import com.youmai.hxsdk.contact.search.stickyheader.StickyHeaderAdapter;
import com.youmai.hxsdk.contact.search.cn.Contact;

import java.util.List;

/**
 * Created by you on 2017/9/11.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> implements StickyHeaderAdapter<ContactAdapter.HeaderHolder> {

    private final List<CNPinyin<Contact>> cnPinyinList;

    public ContactAdapter(List<CNPinyin<Contact>> cnPinyinList) {
        this.cnPinyinList = cnPinyinList;
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
    public void onBindViewHolder(ContactHolder holder, int position) {
        Contact contact = cnPinyinList.get(position).data;
        holder.iv_header.setImageResource(contact.getIconUrl());
        holder.tv_name.setText(contact.getDisplayName());
    }

    @Override
    public long getHeaderId(int childAdapterPosition) {
        return cnPinyinList.get(childAdapterPosition).getFirstChar();
    }

    @Override
    public boolean specialIndex(int childAdapterPosition) {
        CNPinyin<Contact> contactCNPinyin = cnPinyinList.get(childAdapterPosition);
        return contactCNPinyin.getHeaderFilter().contains(contactCNPinyin.data.getDisplayName());
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

}
