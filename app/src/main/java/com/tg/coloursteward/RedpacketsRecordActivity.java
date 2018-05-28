package com.tg.coloursteward;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import com.tg.coloursteward.adapter.RedpacketsRecordAdapter;
import com.tg.coloursteward.adapter.ViewPagerAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.RedpacketsRecordInfo;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.MyViewPager;
import com.tg.coloursteward.view.PullRefreshListView;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
/**
 * 饭票收支明细
 * @author Administrator
 *
 */
public class RedpacketsRecordActivity extends BaseActivity implements OnCheckedChangeListener, OnPageChangeListener {
	private RedpacketsRecordAdapter adapterReceive;
	private RedpacketsRecordAdapter adapterExpend;
	private MyViewPager viewPager;
	private RadioGroup radioGroup;
	private ViewPagerAdapter pagerAdapter;
	private ArrayList<View> pagerList = new ArrayList<View>();
	private PullRefreshListView listViewReceive;
	private PullRefreshListView listViewExpend;
	private ArrayList<RedpacketsRecordInfo> listReceive = new ArrayList<RedpacketsRecordInfo>();
	private ArrayList<RedpacketsRecordInfo> listExpend = new ArrayList<RedpacketsRecordInfo>();
	private String key ;
	private String secret ;
	private String totalReceive ;
	private String totalExpend ;
	private TextView tvTotalReceive,tvTotalExpend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		radioGroup.setOnCheckedChangeListener(this);
		viewPager.setOnPageChangeListener(this);
	}
	private void initView(){
		key = Tools.getStringValue(this,Contants.EMPLOYEE_LOGIN.key);
        secret = Tools.getStringValue(this,Contants.EMPLOYEE_LOGIN.secret);
		radioGroup = (RadioGroup)findViewById(R.id.radio_group);
		viewPager = (MyViewPager)findViewById(R.id.viewPager);
		RadioButton btn1 = (RadioButton)findViewById(R.id.rb_noticBtn);
		RadioButton btn2 = (RadioButton)findViewById(R.id.rb_notifiicationBtn);
		btn1.setText("收入");
		btn2.setText("支出");
		listViewReceive = new PullRefreshListView(this);
		listViewReceive.setKeyName("Receive");
		listViewReceive.setDividerHeight(0);
		adapterReceive = new RedpacketsRecordAdapter(this, listReceive,0);
		listViewReceive.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
					@Override
					public void refreshData(PullRefreshListView t, boolean isLoadMore,
							Message msg, String response) {
						int code = HttpTools.getCode(response);
						String message = HttpTools.getMessageString(response);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
						if(code == 0){
							String content = HttpTools.getContentString(response);
							if(content.length() > 0){
								ResponseData data = HttpTools.getResponseKey(content, "receive");
								if(data.length > 0 ){
									RedpacketsRecordInfo info ;
									for (int i = 0; i < data.length; i++) {
										info = new RedpacketsRecordInfo();
										info.id = data.getString(i,"id");
										info.sn = data.getString(i,"sn");
										String create_time = data.getString(i, "create_time");
										info.time = sdf.format(new Date(Long.parseLong(create_time) * 1000));
										info.detail = data.getString(i,"typeName");
										info.money = data.getString(i,"sum");
										info.note = data.getString(i,"note");
										info.remark = data.getString(i,"remark");
										totalReceive = data.getString(i,"total");
										listReceive.add(info);
									}
									tvTotalReceive.setText("收入："+totalReceive);
								}
							}
						}else{
							ToastFactory.showToast(RedpacketsRecordActivity.this, message);
						}
					}
					
					@Override
					public void onLoadingMore(PullRefreshListView t, Handler hand,
							int pagerIndex) {
						// TODO Auto-generated method stub
						RequestConfig config = new RequestConfig(RedpacketsRecordActivity.this, PullRefreshListView.HTTP_MORE_CODE);
						config.handler = hand;
						RequestParams params = new RequestParams();
						params.put("key", key);
						params.put("secret",secret);
						params.put("page", pagerIndex);
						params.put("pagesize", PullRefreshListView.PAGER_SIZE);
						HttpTools.httpGet(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/redPacketReceive",config, params);
					}
					
					@Override
					public void onLoading(PullRefreshListView t, Handler hand) {
						// TODO Auto-generated method stub
						RequestConfig config = new RequestConfig(RedpacketsRecordActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
						config.handler = hand;
						RequestParams params = new RequestParams();
						params.put("key", key);
						params.put("secret",secret);
						params.put("page", 1);
						params.put("pagesize", PullRefreshListView.PAGER_SIZE);
						HttpTools.httpGet(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/redPacketReceive",config, params);
					}
				});
		/**
		 * list添加头部(Receive)
		 */
		addHeadReceive();
		listViewReceive.setAdapter(adapterReceive);
		pagerList.add(listViewReceive);
		
		listViewExpend = new PullRefreshListView(this);
		listViewExpend.setKeyName("Expend");
		listViewExpend.setDividerHeight(0);
		listViewExpend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				RedpacketsRecordInfo info  = listExpend.get(position);
				Intent intent = new Intent(RedpacketsRecordActivity.this,
						RedpacketsRecordDetailActivity.class);
				intent.putExtra("sn", info.sn);
				intent.putExtra("detail", info.detail);
				intent.putExtra("money", info.money);
				startActivity(intent);
			}
		});
		listViewExpend.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
					@Override
					public void refreshData(PullRefreshListView t, boolean isLoadMore,
							Message msg, String response) {
						int code = HttpTools.getCode(response);
						String message = HttpTools.getMessageString(response);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
						if(code == 0){
							String content = HttpTools.getContentString(response);
							if(content.length() > 0){
								ResponseData data = HttpTools.getResponseKey(content, "expend");
								if(data.length > 0 ){
									RedpacketsRecordInfo info ;
									for (int i = 0; i < data.length; i++) {
										info = new RedpacketsRecordInfo();
										info.id = data.getString(i,"id");
										info.sn = data.getString(i,"sn");
										String create_time = data.getString(i, "create_time");
										info.time = sdf.format(new Date(Long.parseLong(create_time) * 1000));
										info.detail = data.getString(i,"typeName");
										info.money = data.getString(i,"sum");
										info.note = data.getString(i,"note");
										info.remark = data.getString(i,"remark");
										totalExpend = data.getString(i,"total");
										listExpend.add(info);
									}
									tvTotalExpend.setText("支出："+totalExpend);
								}
							}
						}else{
							ToastFactory.showToast(RedpacketsRecordActivity.this, message);
						}
					}
					
					@Override
					public void onLoadingMore(PullRefreshListView t, Handler hand,
							int pagerIndex) {
						// TODO Auto-generated method stub
						RequestConfig config = new RequestConfig(RedpacketsRecordActivity.this, PullRefreshListView.HTTP_MORE_CODE);
						config.handler = hand;
						RequestParams params = new RequestParams();
						params.put("key", key);
						params.put("secret",secret);
						params.put("page", pagerIndex);
						params.put("pagesize", PullRefreshListView.PAGER_SIZE);
						HttpTools.httpGet(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/redPacketExpend",config, params);
					}
					
					@Override
					public void onLoading(PullRefreshListView t, Handler hand) {
						// TODO Auto-generated method stub
						RequestConfig config = new RequestConfig(RedpacketsRecordActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
						config.handler = hand;
						RequestParams params = new RequestParams();
						params.put("key", key);
						params.put("secret",secret);
						params.put("page", 1);
						params.put("pagesize", PullRefreshListView.PAGER_SIZE);
						HttpTools.httpGet(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/redPacketExpend",config, params);
					}
				});
		
		adapterExpend = new RedpacketsRecordAdapter(this, listExpend,1);
		/**
		 * list添加头部(Expend)
		 */
		addHeadExpend();
		listViewExpend.setAdapter(adapterExpend);
		pagerList.add(listViewExpend);
		
		pagerAdapter = new ViewPagerAdapter(pagerList,this);
		viewPager.setAdapter(pagerAdapter);
		listViewReceive.performLoading();
	}
	/**
	 * Receive添加头部
	 */
	private void addHeadReceive() {
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View headView = inflater.inflate(R.layout.redpackets_recoed_head, null);
		listViewReceive.addHeaderView(headView);
		tvTotalReceive = (TextView) headView.findViewById(R.id.tv_total);
	}
	/**
	 * Expend添加头部
	 */
	private void addHeadExpend() {
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View headView = inflater.inflate(R.layout.redpackets_recoed_head, null);
		listViewExpend.addHeaderView(headView);
		tvTotalExpend = (TextView) headView.findViewById(R.id.tv_total);
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int position) {
		if(position == 0){
			radioGroup.check(R.id.rb_noticBtn);
		}else{
			radioGroup.check(R.id.rb_notifiicationBtn);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(checkedId == R.id.rb_noticBtn){
			if(viewPager.getCurrentItem() != 0){
				viewPager.setCurrentItem(0);
				listViewReceive.performLoading();
			}
		}else{
			if(viewPager.getCurrentItem() != 1){
				viewPager.setCurrentItem(1);
				listViewExpend.performLoading();
			}
		}
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_redpackets_record,null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "饭票收支明细";
	}

}
