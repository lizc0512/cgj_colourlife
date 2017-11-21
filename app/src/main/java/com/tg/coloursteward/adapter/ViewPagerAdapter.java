package com.tg.coloursteward.adapter;

import java.util.ArrayList;

import com.tg.coloursteward.info.AdvInfo;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class ViewPagerAdapter extends PagerAdapter {
	private ArrayList<AdvInfo> list;
	private ArrayList<View> tempList = new ArrayList<View>();
	private Context con;
	private String[] tabsName;
	protected boolean needCycle = false;
	public ViewPagerAdapter(){
		
	}
	
	public ViewPagerAdapter(ArrayList<AdvInfo> l, Context c, boolean cycle) {
		needCycle = cycle;
		list = l;
		con = c;
		initTempList(); 
	}
	
	public ArrayList<AdvInfo> getList(){
		return list;
	}
	
	public ViewPagerAdapter(ArrayList<View> l, Context c) {
		needCycle = false;
		tempList = l;
		con = c;
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
		for(int i = 0; i < count;i ++){
			img = new ImageView(con);
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
			img.setBackgroundResource(resID);
			tempList.add(img);
		}
	}

	public void setTabsName(String[] names) {
		tabsName = names;
	}

	public String[] getTabsName() {
		return tabsName;
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
		// View v = LayoutInflater.from(con).inflate(R.layout., null);
		View v = null;
		if(position >= 0 && position < tempList.size()){
			v = tempList.get(position);
			container.addView(v);
			
		}
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
}
