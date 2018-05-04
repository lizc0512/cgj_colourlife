package com.tg.coloursteward.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tg.coloursteward.MyBrowserActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.AdvInfo;
import com.tg.coloursteward.net.image.VolleyUtils;
import com.tg.coloursteward.util.AuthTimeUtils;

import java.util.ArrayList;

public class AdvPagerAdapter extends PagerAdapter implements OnClickListener{
	private ArrayList<AdvInfo> list;
	private ArrayList<ImageView> tempList = new ArrayList<ImageView>();
	private Context con;
	private Activity mActivity ;
	private AdvInfo item;
	protected boolean needCycle = false;
	public AdvPagerAdapter(){
		
	}
	
	public AdvPagerAdapter(ArrayList<AdvInfo> l, Context c, boolean cycle) {
		mActivity = (Activity) c;
		needCycle = cycle;
		list = l;
		con = c;
		initTempList(); 
	}
	
	
	public AdvPagerAdapter(ArrayList<ImageView> l, Context c) {
		needCycle = false;
		tempList = l;
		con = c;
	}
	
	public void notifyDataSetChanged(){
		initTempList();
		super.notifyDataSetChanged();
	}
	
	public ArrayList<AdvInfo> getList(){
		return list;
	}
	
	public boolean isCycle(){
		return needCycle;
	}
	
	public void initTempList(){
		if(list == null || list.size() == 0){
			return;
		}
		ImageView img ;
		int resID;
		int count = needCycle? list.size()+2 : list.size();
		tempList.clear();
		for(int i = 0; i < count;i ++){
			img = new ImageView(con);
			img.setScaleType(ScaleType.FIT_XY);
			ViewPager.LayoutParams p = new ViewPager.LayoutParams();
			img.setLayoutParams(p);
			if(needCycle){
				if(i == 0){
					resID =list.get(list.size() - 1).advResId;
				}else if(i == count -1){
					resID = list.get(0).advResId;
				}else{
					resID = list.get(i -1).advResId;
				}
			}else{
				resID = list.get(i).advResId;
			}
			img.setId(i+1);
			img.setImageResource(resID);
			img.setOnClickListener(this);
			tempList.add(img);
		}
	}

	public View getViewByIndex(int index) {
		if (tempList != null && index >= 0 && index <= tempList.size() - 1) {
			if(needCycle){
				return tempList.get(index+1);
			}else{
				return tempList.get(index);
			}
		}
		return null;
	}
	
	public AdvInfo getAdvInfoByIndex(int index) {
		if(needCycle){
			int position = index - 1;
			if(position < 0){
				position = list.size() - 1;
			}else if(position > list.size() - 1){
				position = 0;
			}
			return list.get(position);
		}else{
			return list.get(index);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tempList.size();
	}

	public int getChildSize(){
		return list == null ? tempList.size() : list.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		ImageView v = null;
		item = getAdvInfoByIndex(position);
		if(position >= 0 && position < tempList.size()){
			v = tempList.get(position);
		}else{
			v = new ImageView(con);
		}
		if(item.advResId > 0){
			v.setImageResource(item.advResId);
		}else{
			Glide.with(con).load(item.imgUrl)
					.apply(new RequestOptions()
							.override(1000, 500)
							.diskCacheStrategy(DiskCacheStrategy.RESOURCE))
					.into(v);

			/*VolleyUtils.getImage(con,
					item.imgUrl, 
					v, 
					1000, 
					500, 
					R.color.default_bg);*/
		}
		container.addView(v);
		return v;
	}

    @Override  
    public int getItemPosition(Object object) {  
        return POSITION_NONE;  
    }  
    
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		if(position >= 0 && position < tempList.size()){
			container.removeView(tempList.get(position));
		}
	}
	
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		int position;
		if(isCycle()){
			position = id -1 - 1;
			if(position < 0){
				position = list.size() - 1;
			}else if(position > list.size() - 1){
				position = 0;
			}
		}else{
			position = id - 1;
		}
		
			String url = list.get(position).url;
			if(TextUtils.isEmpty(url)){
				return;
			}
		AuthTimeUtils mAuthTimeUtils = new AuthTimeUtils();
		mAuthTimeUtils.IsAuthTime(mActivity, url, "case", "1", "case","");
		/*Intent intent = new Intent(con,MyBrowserActivity.class);
		intent.putExtra(MyBrowserActivity.KEY_URL, url);
		con.startActivity(intent);*/
		
	}
}
