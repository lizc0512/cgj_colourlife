package com.tg.coloursteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.AppDetailsGridViewInfo;

import java.util.ArrayList;

/**
 *
 */

public class AppDetailsGridViewAdapter extends MyBaseAdapter<AppDetailsGridViewInfo> {

    private ArrayList<AppDetailsGridViewInfo> list;
    private LayoutInflater inflater;
    private AppDetailsGridViewInfo item;
    private Context context;
    public AppDetailsGridViewAdapter(Context con, ArrayList<AppDetailsGridViewInfo> list) {
        super(list);
        this.list = list;
        this.context = con;
        inflater = LayoutInflater.from(con);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.app_details_grid_item, null);
        }
        item = list.get(position);
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_grid_item);
        tvName.setText(item.name);
        return convertView;
    }
}

