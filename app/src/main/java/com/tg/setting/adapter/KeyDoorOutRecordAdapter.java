package com.tg.setting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.view.CircleImageView;
import com.tg.setting.entity.KeyDoorOpenLogsEntity;

import java.util.List;


public class KeyDoorOutRecordAdapter extends RecyclerView.Adapter<KeyDoorOutRecordAdapter.DefaultViewHolder> {

    public List<KeyDoorOpenLogsEntity.ContentBean.DataBean> mList;
    public String accessName;
    public Context mContext;

    public KeyDoorOutRecordAdapter(Context mContext, List<KeyDoorOpenLogsEntity.ContentBean.DataBean> list, String accessName) {
        this.mList = list;
        this.mContext = mContext;
        this.accessName = accessName;
    }

    @NonNull
    @Override
    public KeyDoorOutRecordAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_key_door_user, parent, false);
        return new DefaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyDoorOutRecordAdapter.DefaultViewHolder holder, int position) {
        KeyDoorOpenLogsEntity.ContentBean.DataBean contentBean = mList.get(position);
        String avater = contentBean.getAvatar();
        if (!TextUtils.isEmpty(avater)) {
            GlideUtils.loadImageView(mContext, avater, holder.door_user_photo);
        }
        String screenName = contentBean.getScreenName();
        if (!TextUtils.isEmpty(screenName)) {
            holder.tv_door_username.setText(screenName);
        }
        String identityName = contentBean.getIdentityName();
        if (!TextUtils.isEmpty(identityName)) {
            holder.tv_door_identify.setText(identityName);
        } else {
            holder.tv_door_identify.setText("业主");
        }
        holder.tv_door_username.setText(StringUtils.getHandlePhone(contentBean.getMobile()));
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(contentBean.getCreateTime());
        stringBuffer.append(accessName);
        stringBuffer.append("开门");
        holder.tv_door_desc.setText(stringBuffer.toString());
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
            tv_door_status.setVisibility(View.GONE);
        }
    }

}
