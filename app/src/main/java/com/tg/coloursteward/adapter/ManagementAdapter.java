package com.tg.coloursteward.adapter;

import java.util.ArrayList;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.GridViewInfo;
import com.tg.coloursteward.net.image.VolleyUtils;

public class ManagementAdapter extends MyBaseAdapter<GridViewInfo> {

	private ArrayList<GridViewInfo> list;
	private LayoutInflater inflater;
	private GridViewInfo item;
	private Context context;
	public ManagementAdapter(Context con, ArrayList<GridViewInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = inflater.inflate(R.layout.grid_item, null);
		}
		item = list.get(position);
		TextView tvName = (TextView) convertView.findViewById(R.id.tv_grid_item);
		ImageView ivIcon = (ImageView) convertView.findViewById(R.id.iv_grid_item);
		tvName.setText(item.name);
		if(item.icon != ""){
			String icon ;
			if(item.icon.contains("-100-100.jpg")){
				 icon = item.icon.replaceAll("-100-100.jpg","");
			}else{
				icon = item.icon;
			}

            Glide.with(context).load(icon)
                    .apply(new RequestOptions()
                            .error(R.drawable.zhanwei)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(ivIcon);

			//VolleyUtils.getImage(context,icon,ivIcon);
		}else {
			ivIcon.setImageResource(R.drawable.zhanwei);
		}
		return convertView;
	}
}
