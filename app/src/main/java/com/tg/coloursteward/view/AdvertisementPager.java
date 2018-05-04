package com.tg.coloursteward.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.AdvPagerAdapter;
import com.tg.coloursteward.info.AdvInfo;
import com.tg.coloursteward.inter.PagerChangeListener;
import com.tg.coloursteward.log.Logger;
import com.tg.coloursteward.util.Tools;

import java.util.ArrayList;

/**
 * 轮播图控件
 */
public class AdvertisementPager extends FrameLayout implements OnPageChangeListener,
OnClickListener{
	public static final int MSG_NEXT_PAGE = 0x111;
	public static final int DELAY_TIME = 20 * 1000;
	public static final int MAX_DOT_NUM = 10;
	public static final int HALF_TRANSPARENT = 0x01000000;
	private int childCount;
	private Context con; 
	private ViewPager pager;
	private ImageView[] pointImg;
	private View[] contentView;
	private LinearLayout pointLayout;
	private int defaultImgRes = R.drawable.ad_dot;
	private int highlightImgRes = R.drawable.ad_dot_h;
	private int currentPage = 0;
	private int totalViewCount;
	private int currentIndex = 1;
	private boolean needCycle = false;
	private PagerChangeListener changeListener;
	private ArrayList<AdvInfo> list;
	private Handler hand = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == MSG_NEXT_PAGE){
				nextPager();
			}
		}
	};
	
	public void nextPager(){
		currentIndex ++;
		if(currentIndex >= totalViewCount){
			currentIndex = 0;
		}
		pager.setCurrentItem(currentIndex, true);
	}
	
	public AdvertisementPager(Context context) {
		super(context);
		initViewPager(context);
	}
	public AdvertisementPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViewPager(context);
	}
	public void initViewPager(Context c){
		con = c;
		LayoutInflater.from(con).inflate(R.layout.ad_pager_layout, this);
		pager = (ViewPager)findViewById(R.id.ad_viewpager);
		pager.setOnPageChangeListener(this);
	}
	
	public void setPagerChangeListener(PagerChangeListener chang){
		changeListener = chang;
	}
	
	public void notifyDataSetChanged(){
		pager.getAdapter().notifyDataSetChanged();
		list = adapter.getList();
		hand.removeMessages(MSG_NEXT_PAGE);
		needCycle = adapter.isCycle();
		childCount = adapter.getChildSize();
		totalViewCount = adapter.getCount();
		
		initSmallbutton(childCount);
		//contentView = new View[childCount];
		/*for(int i = 0 ;i < childCount; i ++){
			contentView[i] = adapter.getViewByIndex(i);
			contentView[i].setOnClickListener(this); 
		}*/
		setPosition(0);
		if(needCycle && childCount >= 2){
			hand.removeMessages(MSG_NEXT_PAGE);
			hand.sendEmptyMessageDelayed(MSG_NEXT_PAGE, DELAY_TIME);
		}
	}
	private AdvPagerAdapter adapter;
	public void setAdapter(AdvPagerAdapter adap){
		if(adap == null){
			return;
		}
		adapter = adap;
		list = adapter.getList();
		hand.removeMessages(MSG_NEXT_PAGE);
		needCycle = adapter.isCycle();
		childCount = adapter.getChildSize();
		totalViewCount = adapter.getCount();
		pager.setAdapter(adapter);
		initSmallbutton(childCount);
		//contentView = new View[childCount];
		/*for(int i = 0 ;i < childCount; i ++){
			contentView[i] = adapter.getViewByIndex(i);
			contentView[i].setOnClickListener(this); 
		}*/
		if(needCycle && childCount >= 2){
			hand.removeMessages(MSG_NEXT_PAGE);
			hand.sendEmptyMessageDelayed(MSG_NEXT_PAGE, DELAY_TIME);
		}
		if(needCycle){
			pager.setCurrentItem(1);
		}else{
			pager.setCurrentItem(0);
			setPosition(0);
		}
	}
	
	public void setCurrentItem(int position){
		if(needCycle){
			pager.setCurrentItem(position+1);
		}else{
			pager.setCurrentItem(position);
		}
	}
	
	public void setPagerDotResource(int selectedRes , int defaultRes ){
		highlightImgRes = selectedRes;
		defaultImgRes = defaultRes;
	}
	public int getCurrentPage(){
		return pager.getCurrentItem();
	}
	
	public void setContentImgEnabled(boolean enable){
		for(int i = 0 ;i < childCount; i ++){
			contentView[i].setEnabled(enable);
		}
	}
	
	public void setPosition(int index){
		if(pointLayout == null){
			return;
		}
		if(index < 0){
			index = 0;
		}else if(index > pointImg.length - 1){
			index = pointImg.length -1;
		}
		for(int i = 0; i < pointImg.length; i++){
			if(i == index){
				pointImg[i].setBackgroundResource(highlightImgRes);
			}else{
				pointImg[i].setBackgroundResource(defaultImgRes);
			}
		}
	}
	
	public void initSmallbutton(int count){
		Logger.logd("count = "+count);
		if(pointLayout != null ){
			removeView(pointLayout);
		}
		int padding = (int)(Tools.getDisplayMetrics(con).density * 10);
		pointLayout = new LinearLayout(con);
		pointLayout.setOrientation(LinearLayout.HORIZONTAL);
		pointLayout.setBackgroundColor(HALF_TRANSPARENT);//默认颜色
		//pointLayout.setBackgroundResource(R.drawable.test);//默认图片
		pointLayout.setGravity(Gravity.CENTER);
		pointLayout.setPadding(padding, padding, padding, padding);
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		//lp.topMargin = 50;
		lp.gravity = Gravity.BOTTOM;
		if(count > MAX_DOT_NUM){
			count = MAX_DOT_NUM;
		}
		
		pointImg = new ImageView[count];
		LinearLayout.LayoutParams p ;
		int leftMargin = (int)(Tools.getDisplayMetrics(con).density * 5);
		for(int i = 0; i < count; i ++){
			p = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			p.leftMargin = leftMargin;
			if(i == 0){
				p.leftMargin = 0;
			}
			pointImg[i] = new ImageView(con);
			pointLayout.addView(pointImg[i],p);
		}
		addView(pointLayout,lp);
	}
	
	public void setGravityCenter(){
		if(pointLayout == null){
			return;
		}
		LayoutParams lp = (LayoutParams)pointLayout.getLayoutParams();
		lp.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
		pointLayout.setLayoutParams(lp);
	}
	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		// TODO Auto-generated method stub
		if(needCycle){
			if(position == 0 && positionOffsetPixels == 0){
				pager.setCurrentItem(childCount ,false);
			}else if(position == totalViewCount -1 && positionOffsetPixels == 0){
				pager.setCurrentItem(1,false);
			}
		}
	}
	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		currentIndex = position;
		if(needCycle){
			if(position == 0){
				currentPage = childCount -1;
				setPosition(currentPage);
			}else if(position == totalViewCount -1){
				currentPage = 0;
				setPosition(currentPage);
			}else{
				currentPage = position -1;
				setPosition(currentPage);
				if(childCount >= 2){
					hand.removeMessages(MSG_NEXT_PAGE);
					hand.sendEmptyMessageDelayed(MSG_NEXT_PAGE, DELAY_TIME);
				}
			}
		}else{
			setPosition(currentIndex);
		}
		if(changeListener != null){
			changeListener.onPagerChange(currentIndex, childCount);
		}
	}
	
	@Override
	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub
		if(needCycle){
			if(state == 0){
				if(childCount >= 2){
					hand.removeMessages(MSG_NEXT_PAGE);
					hand.sendEmptyMessageDelayed(MSG_NEXT_PAGE, DELAY_TIME);
				}
			}else{
				if(childCount >= 2){
					hand.removeMessages(MSG_NEXT_PAGE);
				}
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}
}
