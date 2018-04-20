package com.tg.coloursteward.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.GridViewInfo;
import com.tg.coloursteward.net.image.VolleyUtils;
import com.tg.coloursteward.view.RoundImageView;

import java.util.ArrayList;

public class SearchManageMentAdapter  extends MyBaseAdapter<GridViewInfo> {
    private ArrayList<GridViewInfo> list;
    private LayoutInflater inflater;
    private GridViewInfo item;
    private Context context;

    public SearchManageMentAdapter(Context con, ArrayList<GridViewInfo> list) {
        super(list);
        this.list = list;
        this.context = con;
        inflater = LayoutInflater.from(con);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.home_management_search_item, null);
        }
        item = list.get(position);
        RoundImageView rivHead = (RoundImageView) convertView.findViewById(R.id.riv_head);
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        tvName.setText(item.name);

        Glide.with(context).load(item.icon)
                .apply(new RequestOptions()
                        .error(R.drawable.moren_geren)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(rivHead);
        //VolleyUtils.getImage(context, item.icon,rivHead,R.drawable.moren_geren);
        return convertView;
    }
}