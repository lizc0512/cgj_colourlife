package com.youmai.hxsdk.module.groupchat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.Contact;
import com.youmai.hxsdk.utils.GlideRoundTransform;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2018.04.23 19:16
 * 描述：
 */
public class GroupDetailAdapter extends RecyclerView.Adapter {

    final String HEAD_ICON_URL="http://iceapi.colourlife.com:8686/";//头像

    private Context mContext;
    private List<Contact> mDataList = new ArrayList<>();
    private LayoutInflater mLayoutInflater;

    public GroupDetailAdapter(Context context) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setDataList(List list) {
        this.mDataList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.hx_activity_im_chat_group, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemHolder itemHolder = (ItemHolder) holder;
        Contact contact = mDataList.get(position);
        itemHolder.tv_name.setText(contact.getRealname());

        String str = HEAD_ICON_URL + "avatar?uid=" + contact.getUsername();
        Glide.with(mContext)
                .load(str)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .override(120, 120)
                        .transform(new GlideRoundTransform(mContext))
                        .placeholder(R.drawable.color_default_header)
                        .error(R.drawable.color_default_header))
                .into(itemHolder.iv_header);

    }

    @Override
    public int getItemCount() {
        return mDataList.size() > 0 ? mDataList.size() : 0;
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        private TextView tv_name;
        private ImageView iv_header;

        public ItemHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_item_name);
            iv_header = itemView.findViewById(R.id.iv_item_header);
        }
    }
}
