package com.tg.coloursteward.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.DataShowInfo;

public class DataShowAdapter extends MyBaseAdapter<DataShowInfo> {
    private ArrayList<DataShowInfo> list;
    private LayoutInflater inflater;
    private DataShowInfo item;
    private Context context;

    public DataShowAdapter(Context con, ArrayList<DataShowInfo> list) {
        super(list);
        this.list = list;
        this.context = con;
        inflater = LayoutInflater.from(con);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.data_show_item,
                    null);
        }
        item = list.get(position);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
        TextView tvContent = (TextView) convertView.findViewById(R.id.tv_content);

        TextView tvTitle1 = (TextView) convertView.findViewById(R.id.tv_title1);
        TextView tvTitle2 = (TextView) convertView.findViewById(R.id.tv_title2);
        TextView tvTitle3 = (TextView) convertView.findViewById(R.id.tv_title3);
        TextView tvContent1 = (TextView) convertView.findViewById(R.id.tv_content1);
        TextView tvContent2 = (TextView) convertView.findViewById(R.id.tv_content2);
        TextView tvContent3 = (TextView) convertView.findViewById(R.id.tv_content3);

        tvTitle.setText(item.title);
        tvContent.setText(item.content);

        /**
         * 发布新版本之前的改动
         */
//        if (tvTitle.getText().toString().equals("小区面积（㎡）")) {
//            tvTitle1.setVisibility(View.VISIBLE);
//            tvTitle2.setVisibility(View.VISIBLE);
//            tvTitle3.setVisibility(View.VISIBLE);
//            tvContent1.setVisibility(View.VISIBLE);
//            tvContent2.setVisibility(View.VISIBLE);
//            tvContent3.setVisibility(View.VISIBLE);
//            tvTitle1.setText(item.title1);
//            tvTitle2.setText(item.title2);
//            tvTitle3.setText(item.title3);
//            tvContent1.setText(item.content1);
//            tvContent2.setText(item.content2);
//            tvContent3.setText(item.content3);
//        }
        return convertView;
    }

}
