package com.tg.coloursteward.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.Collections;
import java.util.List;

import org.greenrobot.eventbus.EventBus;

import com.tg.coloursteward.R;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.door.DoorFixedResp;
import com.tg.coloursteward.util.ScreenManager;

public class DragAdapter extends BaseAdapter implements DragGridBaseAdapter {
    public List<DoorFixedResp> list;
    private LayoutInflater mInflater;
    private int mHidePosition = -1;
    private Context context;
    private int selectposition = -1;

    public DragAdapter(Context context, List<DoorFixedResp> list) {
        this.list = list;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.griddrag_item, null);
        FrameLayout fl_layout = (FrameLayout) convertView.findViewById(R.id.fl_layout);
        FrameLayout fl_left = (FrameLayout) convertView.findViewById(R.id.fl_left);
        FrameLayout fl_ritgh = (FrameLayout) convertView.findViewById(R.id.fl_ritgh);
        ImageView img_icon = (ImageView) convertView.findViewById(R.id.img_grid_icon);
        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_icon_name);
        ImageView img_write = (ImageView) convertView.findViewById(R.id.img_callwrite);
        ImageView img_add_del = (ImageView) convertView.findViewById(R.id.img_add_del);
        img_add_del.setImageResource(R.drawable.icon_deletesmall);
        tv_name.setText(list.get(position).getName());
        Log.e("常用", ""+list.get(position).getType());
        if (list.get(position).getType().equals("1")) {
            img_icon.setImageResource(R.drawable.icon_home);
        } else if (list.get(position).getType().equals("2")) {
            img_icon.setImageResource(R.drawable.icon_bussine);
        }
        LinearLayout ll_select = (LinearLayout) convertView.findViewById(R.id.ll_select);
        if (selectposition == position) {
            ll_select.setBackgroundResource(R.color.base_color);
            fl_layout.setBackgroundResource(R.color.base_color);
            img_add_del.setVisibility(View.VISIBLE);
            img_write.setVisibility(View.VISIBLE);
            fl_left.setVisibility(View.VISIBLE);
            fl_ritgh.setVisibility(View.VISIBLE);
        } else {
            ll_select.setBackgroundResource(R.color.white);
            fl_layout.setBackgroundResource(R.color.white);
            img_add_del.setVisibility(View.INVISIBLE);
            img_write.setVisibility(View.INVISIBLE);
            fl_left.setVisibility(View.VISIBLE);
            fl_ritgh.setVisibility(View.VISIBLE);
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ScreenManager.getScreenWitdh(context) / 10, (ScreenManager.getScreenWitdh(context) / 10));
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        img_icon.setLayoutParams(layoutParams);

        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(ScreenManager.getScreenWitdh(context) / 25, (ScreenManager.getScreenWitdh(context) / 25));
        img_write.setLayoutParams(layoutParams2);
        FrameLayout.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(ScreenManager.getScreenWitdh(context) / 25, (ScreenManager.getScreenWitdh(context) / 25));
        layoutParams3.gravity = Gravity.RIGHT;
        img_add_del.setLayoutParams(layoutParams3);

        if (position == mHidePosition) {
            convertView.setVisibility(View.INVISIBLE);
        }
        fl_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.what = Contants.LOGO.DOOREDIT_DOOR_NAME;
                msg.obj = list.get(position);
                EventBus.getDefault().post(msg);
            }
        });
        fl_ritgh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.what =  Contants.LOGO.DOOREDIT_DELETE;
                msg.obj = list.get(position);
                EventBus.getDefault().post(msg);
                list.remove(position); 
                setItemBg(-1);

            }
        });
        return convertView;
    }


    @Override
    public void reorderItems(int oldPosition, int newPosition) {
        DoorFixedResp temp = list.get(oldPosition);
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        } else if (oldPosition > newPosition) {
            for (int i = oldPosition; i > newPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        }
        Message msg = new Message();
        msg.what = Contants.LOGO.DOOREDIT_MOVE;
        EventBus.getDefault().post(msg);
        list.set(newPosition, temp);
        setItemBg(-1);
    }

    @Override
    public void setItemBg(int position) {
        selectposition = position;
        notifyDataSetChanged();
    }

    @Override
    public void setHideItem(int hidePosition) {
        this.mHidePosition = hidePosition;
        notifyDataSetChanged();
    }
}
