package com.tg.coloursteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.DataShowInfo;

import java.util.ArrayList;

public class DataShowTenantAdapter extends MyBaseAdapter<DataShowInfo> {
    private ArrayList<DataShowInfo> list;
    private LayoutInflater inflater;
    private DataShowInfo item;
    private Context context;

    public DataShowTenantAdapter(Context con, ArrayList<DataShowInfo> list) {
        super(list);
        this.list = list;
        this.context = con;
        inflater = LayoutInflater.from(con);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.data_show_tenant_item,
                    null);
        }
        item = list.get(position);
        TextView mainTitle = (TextView) convertView.findViewById(R.id.tv_mainTitle);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
        TextView tvContent = (TextView) convertView.findViewById(R.id.tv_content);

        TextView tvTitle1 = (TextView) convertView.findViewById(R.id.tv_title1);
        TextView tvTitle2 = (TextView) convertView.findViewById(R.id.tv_title2);
        TextView tvTitle3 = (TextView) convertView.findViewById(R.id.tv_title3);
        TextView tvTitle4 = (TextView) convertView.findViewById(R.id.tv_title4);
        TextView tvTitle5 = (TextView) convertView.findViewById(R.id.tv_title5);
        TextView tvContent1 = (TextView) convertView.findViewById(R.id.tv_content1);
        TextView tvContent2 = (TextView) convertView.findViewById(R.id.tv_content2);
        TextView tvContent3 = (TextView) convertView.findViewById(R.id.tv_content3);
        TextView tvContent4 = (TextView) convertView.findViewById(R.id.tv_content4);
        TextView tvContent5 = (TextView) convertView.findViewById(R.id.tv_content5);

        mainTitle.setText(item.mainTitle);
        tvTitle.setText(item.title);
        tvContent.setText(item.content);
        tvTitle4.setText(item.title4);
        tvContent4.setText(item.content4);
        tvTitle5.setText(item.title5);
        tvContent5.setText(item.content5);


        if (tvTitle.getText().toString().equals("小区面积（㎡）")) {
            tvTitle1.setVisibility(View.VISIBLE);
            tvTitle2.setVisibility(View.VISIBLE);
            tvTitle3.setVisibility(View.VISIBLE);
            tvContent1.setVisibility(View.VISIBLE);
            tvContent2.setVisibility(View.VISIBLE);
            tvContent3.setVisibility(View.VISIBLE);
            tvTitle1.setText(item.title1);
            tvTitle2.setText(item.title2);
            tvTitle3.setText(item.title3);
            tvContent1.setText(item.content1);
            tvContent2.setText(item.content2);
            tvContent3.setText(item.content3);
        }
        return convertView;
    }

}
