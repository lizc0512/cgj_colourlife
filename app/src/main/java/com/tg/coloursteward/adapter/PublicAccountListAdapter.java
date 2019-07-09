package com.tg.coloursteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.PublicAccountInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/9/5.
 */

public class PublicAccountListAdapter extends MyBaseAdapter<PublicAccountInfo> {
    private LayoutInflater inflater;
    private PublicAccountInfo item;
    private Context context;

    public PublicAccountListAdapter(Context con, ArrayList<PublicAccountInfo> list) {
        super(list);
        this.list = list;
        this.context = con;
        inflater = LayoutInflater.from(con);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.public_account_list_item, null);
        }
        item = list.get(position);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
        TextView tvAno = (TextView) convertView.findViewById(R.id.tv_ano);
        TextView tvTypeName = (TextView) convertView.findViewById(R.id.tv_typeName);
        tvTitle.setText(item.title);
        tvAno.setText(item.ano);
        tvTypeName.setText(item.typeName);
        return convertView;
    }

}

