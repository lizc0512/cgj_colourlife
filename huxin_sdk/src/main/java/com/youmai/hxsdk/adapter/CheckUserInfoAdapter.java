package com.youmai.hxsdk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.im.cache.ContactsDetailsBean;
import com.youmai.hxsdk.utils.ListUtils;

/**
 * Created by Gary on 17/3/30 16:40
 * Email aoguoyue@163.com
 */

public class CheckUserInfoAdapter extends UserInfoAdapter {
    private boolean isMore =false;
    public CheckUserInfoAdapter(Context context, ContactsDetailsBean contactsDetailsBean)
    {
        super(context, contactsDetailsBean);
        userInfoTitle = mContext.getResources().getStringArray(R.array.user_info_check);
    }

    public void setMore(boolean more) {
        isMore = more;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //super.onBindViewHolder(holder, position);
        String title = userInfoTitle[position];
        UserInfoItemView itemView = (UserInfoItemView) holder;
        itemView.user_info_title.setText(title);
        switch (position)
        {

            case 0:
                if (!ListUtils.isEmpty(contactsDetailsBean.getPhone()))
                {
                    itemView.user_info_text.setText(contactsDetailsBean.getPhone().get(0).getPhone());
                }
                break;
            case 1:
                if (!ListUtils.isEmpty(contactsDetailsBean.getEmail())){
                    itemView.user_info_text.setText(contactsDetailsBean.getEmail().get(0).getEmail());
                }
                break;
            case 2:
                if (!ListUtils.isEmpty(contactsDetailsBean.getDate())){
                    itemView.user_info_text.setText(contactsDetailsBean.getDate().get(0).getDate());
                }
                break;
            case 3:
                itemView.user_info_text.setText("");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return isMore?userInfoTitle.length:2;
    }
}
