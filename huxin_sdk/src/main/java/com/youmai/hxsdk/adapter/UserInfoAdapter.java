package com.youmai.hxsdk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.im.cache.ContactsDetailsBean;
import com.youmai.hxsdk.utils.ListUtils;

/**
 * Created by Gary on 17/3/28 19:08
 * Email aoguoyue@163.com
 */

public class UserInfoAdapter extends RecyclerView.Adapter {
    protected Context mContext;
    protected String[] userInfoTitle;
    protected ContactsDetailsBean contactsDetailsBean;

    public UserInfoAdapter(Context context, ContactsDetailsBean contactsDetailsBean) {
        mContext = context;
        userInfoTitle = mContext.getResources().getStringArray(R.array.user_info);
        this.contactsDetailsBean = contactsDetailsBean;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        long start = System.currentTimeMillis();
        View view = inflater.inflate(R.layout.user_info_layout, parent, false);
        return new UserInfoItemView(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String title = userInfoTitle[position];
        UserInfoItemView itemView = (UserInfoItemView) holder;
        itemView.user_info_title.setText(title);
        switch (position) {
            case 0:
                itemView.user_info_text.setText(contactsDetailsBean.getName());
                break;
            case 1:
                itemView.user_info_text.setText(contactsDetailsBean.getJob());
                break;
            case 2:
                itemView.user_info_text.setText(contactsDetailsBean.getCompany());
                break;
            case 3:
                if (!ListUtils.isEmpty(contactsDetailsBean.getPhone())) {
                    itemView.user_info_text.setText(contactsDetailsBean.getPhone().get(0).getPhone());
                }
                break;
            case 4:
                if (!ListUtils.isEmpty(contactsDetailsBean.getEmail())) {
                    itemView.user_info_text.setText(contactsDetailsBean.getEmail().get(0).getEmail());
                }
                break;
            case 5:
                if (!ListUtils.isEmpty(contactsDetailsBean.getDate())) {
                    itemView.user_info_text.setText(contactsDetailsBean.getDate().get(0).getDate());
                }
                break;
            case 6:
                itemView.user_info_text.setText("");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return userInfoTitle.length;
    }

    protected class UserInfoItemView extends RecyclerView.ViewHolder {
        TextView user_info_title, user_info_text;
        RelativeLayout user_info_layout;

        public UserInfoItemView(View itemView) {
            super(itemView);
            user_info_title = (TextView) itemView.findViewById(R.id.user_info_title);
            user_info_text = (TextView) itemView.findViewById(R.id.user_info_text);
            user_info_layout = (RelativeLayout) itemView.findViewById(R.id.user_info_layout);

        }
    }
}
