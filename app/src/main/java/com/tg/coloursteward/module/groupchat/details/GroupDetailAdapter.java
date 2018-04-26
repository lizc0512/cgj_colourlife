package com.tg.coloursteward.module.groupchat.details;

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
import com.tg.coloursteward.constant.Contants;
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

    final String HEAD_ICON_URL = Contants.URl.HEAD_ICON_URL; //头像

    enum TYPE {
        ADD_DELETE, DEFAULT
    }

    private Context mContext;
    private List<Contact> mDataList = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private ItemEventListener itemEventListener;
    private int mType = 1; //1: 不是群主  2：是群主

    public GroupDetailAdapter(Context context, ItemEventListener listener) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.itemEventListener = listener;
    }

    public void setDataList(List list) {
        this.mDataList = list;
        notifyDataSetChanged();
    }

    public void setType(int type) {
        this.mType = type;
    }

    @Override
    public int getItemCount() {
        return mDataList.size() > 0 ? mDataList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        if (mType == 1) {
            if (position < mDataList.size() - 1) {
                type = TYPE.DEFAULT.ordinal();
            } else {
                type = TYPE.ADD_DELETE.ordinal();
            }
        } else {
            if (position < mDataList.size() - 2) {
                type = TYPE.DEFAULT.ordinal();
            } else {
                type = TYPE.ADD_DELETE.ordinal();
            }
        }
        return type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE.ADD_DELETE.ordinal()) {
            View view = mLayoutInflater.inflate(R.layout.hx_im_group_add_item, parent, false);
            return new AddItemHolder(view);
        } else {
            View view = mLayoutInflater.inflate(R.layout.hx_im_group_item, parent, false);
            return new ItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final Contact contact = mDataList.get(position);
        if (holder instanceof ItemHolder) {
            ItemHolder itemHolder = (ItemHolder) holder;
            itemHolder.tv_name.setText(contact.getRealname());

            Glide.with(mContext)
                    .load(contact.getAvatar())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .centerCrop()
                            .override(120, 120)
                            .transform(new GlideRoundTransform())
                            .placeholder(R.drawable.color_default_header)
                            .error(R.drawable.color_default_header))
                    .into(itemHolder.iv_header);
        } else {
            AddItemHolder mHolder = (AddItemHolder) holder;
            String realname = contact.getRealname();
            if (realname.equals("+")) {
                mHolder.iv_more.setImageResource(R.drawable.hx_add_group_member);
            } else if (realname.equals("-")) {
                mHolder.iv_more.setImageResource(R.drawable.hx_delete_group_member);
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
    }

    private class AddItemHolder extends RecyclerView.ViewHolder {
        private ImageView iv_more;

        public AddItemHolder(View itemView) {
            super(itemView);
            iv_more = itemView.findViewById(R.id.iv_add_more);
        }
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

    public interface ItemEventListener {
        void onItemClick(int pos, Contact contact);
    }
}
