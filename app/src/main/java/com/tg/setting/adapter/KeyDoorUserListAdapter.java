package com.tg.setting.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.view.CircleImageView;
import com.tg.setting.activity.KeyDoorUserListActivity;
import com.tg.setting.entity.KeyByAccessEntity;

import java.util.List;


public class KeyDoorUserListAdapter extends RecyclerView.Adapter<KeyDoorUserListAdapter.DefaultViewHolder> {

    public List<KeyByAccessEntity.ContentBeanX.ContentBean> mList;
    public Context mContext;
    public String accessName;

    public KeyDoorUserListAdapter(Context mContext, List<KeyByAccessEntity.ContentBeanX.ContentBean> list, String accessName) {
        this.mList = list;
        this.mContext = mContext;
        this.accessName = accessName;
    }

    @NonNull
    @Override
    public KeyDoorUserListAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_key_door_user, parent, false);
        return new DefaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyDoorUserListAdapter.DefaultViewHolder holder, int position) {
        KeyByAccessEntity.ContentBeanX.ContentBean contentBean = mList.get(position);
        GlideUtils.loadImageView(mContext, contentBean.getAvatar(), holder.door_user_photo);
        String updateTime = contentBean.getUpdateTime();
        StringBuffer sb = new StringBuffer();
        sb.append(updateTime);
        switch (contentBean.getStatus()) {
            case "1":
                holder.tv_door_status.setText("正常");
                holder.tv_door_status.setTextColor(Color.parseColor("#1DA1F4"));
                sb.append("领取");
                sb.append(accessName);
                sb.append("钥匙");
                break;
            case "2":
                holder.tv_door_status.setText("被冻结钥匙");
                holder.tv_door_status.setTextColor(Color.parseColor("#EF7E33"));
                sb.append("被冻结");
                sb.append(accessName);
                sb.append("钥匙");
                break;
            case "3":
                holder.tv_door_status.setText("被删除钥匙");
                holder.tv_door_status.setTextColor(Color.parseColor("#F7667C"));
                sb.append("被删除");
                sb.append(accessName);
                sb.append("钥匙");
                break;
            case "4":
                holder.tv_door_status.setText("失效");
                holder.tv_door_status.setTextColor(Color.parseColor("#999FAA"));
                sb.append(accessName);
                sb.append("钥匙到期");
                break;
        }
        holder.tv_door_username.setText(contentBean.getScreenName());
        holder.tv_door_identify.setText(contentBean.getIdentityName());
        holder.tv_door_phone.setText(StringUtils.getHandlePhone(contentBean.getPhoneNumber()));
        holder.tv_door_desc.setText(sb.toString());
        holder.itemView.setOnClickListener(view -> ((KeyDoorUserListActivity) mContext).goUserDetailsPage(position));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder {
        CircleImageView door_user_photo;
        TextView tv_door_status;
        TextView tv_door_username;
        TextView tv_door_identify;
        TextView tv_door_phone;
        TextView tv_door_desc;

        DefaultViewHolder(View itemView) {
            super(itemView);
            door_user_photo = itemView.findViewById(R.id.door_user_photo);
            tv_door_status = itemView.findViewById(R.id.tv_door_status);
            tv_door_username = itemView.findViewById(R.id.tv_door_username);
            tv_door_identify = itemView.findViewById(R.id.tv_door_identify);
            tv_door_phone = itemView.findViewById(R.id.tv_door_phone);
            tv_door_desc = itemView.findViewById(R.id.tv_door_desc);
        }
    }

}
