package com.tg.setting.adapter;

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

import com.tg.coloursteward.R;
import com.tg.setting.activity.KeySendKeyPhoneActivity;

import java.util.List;

public class KeyPhoneAdapter extends RecyclerView.Adapter<KeyPhoneAdapter.DefaultViewHolder> {

    public List<String> list;
    public Context mContext;

    public KeyPhoneAdapter(Context mContext, List<String> list) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public KeyPhoneAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_key_phone, parent, false);
        return new DefaultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyPhoneAdapter.DefaultViewHolder holder, int position) {
        holder.et_phone.setText(list.get(position));
        holder.iv_delete.setOnClickListener(v -> ((KeySendKeyPhoneActivity) mContext).delete(position));

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
                if (holder.et_phone.hasFocus()) {//判断当前EditText是否有焦点在
                    list.set(position, s.toString());
                }
            }
        };

        //监听焦点
        holder.et_phone.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                holder.et_phone.addTextChangedListener(textWatcher);
            } else {
                holder.et_phone.removeTextChangedListener(textWatcher);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder {
        EditText et_phone;
        ImageView iv_delete;

        DefaultViewHolder(View itemView) {
            super(itemView);
            et_phone = itemView.findViewById(R.id.et_phone);
            iv_delete = itemView.findViewById(R.id.iv_delete);
        }
    }

}
