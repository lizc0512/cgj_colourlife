package com.tg.setting.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.setting.activity.KeySendKeyPhoneActivity;

import java.util.List;

public class KeyPhoneAdapter extends RecyclerView.Adapter<KeyPhoneAdapter.DefaultViewHolder> {

    public List<String> list;
    public List<String> roomList;
    public Context mContext;
    private int power;

    public KeyPhoneAdapter(Context mContext, List<String> list, List<String> roomList) {
        this.list = list;
        this.roomList = roomList;
        this.mContext = mContext;
    }

    public void setPower(int power) {
        this.power = power;
    }

    @NonNull
    @Override
    public KeyPhoneAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_key_phone, parent, false);
        return new DefaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyPhoneAdapter.DefaultViewHolder holder, int position) {

        holder.iv_delete.setOnClickListener(v -> ((KeySendKeyPhoneActivity) mContext).delete(position));

        holder.et_phone.setTag(position);
        //处理EditText位置错乱问题
        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (position == (int) holder.et_phone.getTag() && holder.et_phone.hasFocus()) {//判断当前EditText是否有焦点在
                    String inputPhone = s.toString().trim();
                    ((KeySendKeyPhoneActivity) mContext).setChangeListener(position, inputPhone);
                }
            }
        };

        //监听焦点
        holder.et_phone.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && position == (int) holder.et_phone.getTag()) {
                holder.et_phone.addTextChangedListener(textWatcher);
            } else {
                holder.et_phone.removeTextChangedListener(textWatcher);
            }
        });
        holder.choice_room_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContext instanceof Activity) {
                    ((KeySendKeyPhoneActivity) mContext).getRoomData(holder.tv_room, position);
                }
            }
        });
        holder.et_phone.setText(list.get(position));
        holder.tv_room.setText(roomList.get(position));
        if (power == 0) {
            holder.choice_room_layout.setVisibility(View.VISIBLE);
        } else {
            holder.choice_room_layout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder {
        EditText et_phone;
        LinearLayout choice_room_layout;
        TextView tv_room;
        ImageView iv_delete;

        DefaultViewHolder(View itemView) {
            super(itemView);
            et_phone = itemView.findViewById(R.id.et_phone);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            choice_room_layout = itemView.findViewById(R.id.choice_room_layout);
            tv_room = itemView.findViewById(R.id.tv_room);
        }
    }

}
