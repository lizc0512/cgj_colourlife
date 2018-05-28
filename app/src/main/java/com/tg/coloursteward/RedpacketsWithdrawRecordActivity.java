package com.tg.coloursteward;

import java.util.ArrayList;


import com.tg.coloursteward.adapter.RedpacketsWithdrawRecordAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.RedpacketsWithdrawRecordInfo;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.PullRefreshListView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 提现记录
 */
public class RedpacketsWithdrawRecordActivity extends BaseActivity {
	private PullRefreshListView pullListView;
	private RedpacketsWithdrawRecordAdapter adapter;
	private String key ;
	private String secret ;
	private ArrayList<RedpacketsWithdrawRecordInfo> list = new ArrayList<RedpacketsWithdrawRecordInfo>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}
	private void initView() {
		key = Tools.getStringValue(this,Contants.EMPLOYEE_LOGIN.key);
        secret = Tools.getStringValue(this,Contants.EMPLOYEE_LOGIN.secret);
		pullListView = (PullRefreshListView)findViewById(R.id.pull_listview);
		adapter = new RedpacketsWithdrawRecordAdapter(this, list);
		pullListView.setAdapter(adapter);
		pullListView.setMinPageSize(15);
		pullListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				RedpacketsWithdrawRecordInfo info = list.get(position);
				Intent intent = new Intent(RedpacketsWithdrawRecordActivity.this,RedpacketsWithdrawRecordDetailActivity.class);
				intent.putExtra("orderSn", info.orderSn);
				intent.putExtra("payAmount",info.payAmount);
				intent.putExtra("payTime", info.payTime);
				intent.putExtra("statusName", info.statusName);
				intent.putExtra("bank_name", info.bank_name);
				intent.putExtra("card_holder", info.card_holder);
				intent.putExtra("card_num", info.card_num);
				intent.putExtra("real_money", info.real_money);
				startActivity(intent);
				
			}
		});
		pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {

					@Override
					public void refreshData(PullRefreshListView t,
							boolean isLoadMore, Message msg, String response) {
						int code = HttpTools.getCode(response);
						if(code == 0){
							String content = HttpTools.getContentString(response);
							if(content != null ){
								ResponseData data = HttpTools.getResponseKey(content, "orderList");
								if(data.length > 0){
									RedpacketsWithdrawRecordInfo info ;
									for (int i = 0; i < data.length; i++) {
										info = new RedpacketsWithdrawRecordInfo();
										info.payTime = data.getString(i,"payTime");
										info.orderSn = data.getString(i,"orderSn");
										info.payAmount = data.getString(i,"payAmount");
										info.statusName = data.getString(i,"statusName");
										info.bank_name = data.getString(i,"bank_name");
										info.card_holder = data.getString(i,"card_holder");
										info.card_num = data.getString(i,"card_num");
										info.real_money = data.getString(i,"real_money");
										list.add(info);
									}
								}
							}
						}
					}
					@Override
					public void onLoadingMore(PullRefreshListView t,Handler hand, int pagerIndex) {
						RequestConfig config = new RequestConfig(RedpacketsWithdrawRecordActivity.this, PullRefreshListView.HTTP_MORE_CODE);
						config.handler = hand;
						RequestParams params = new RequestParams();
						params.put("key",key);
						params.put("secret",secret);
						params.put("page",pagerIndex);
						params.put("pagesize",PullRefreshListView.PAGER_SIZE);
						HttpTools.httpGet(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/tiXianOrderList",config, params);
					}

					@Override
					public void onLoading(PullRefreshListView t, Handler hand) {
						// TODO Auto-generated method stub
						RequestConfig config = new RequestConfig(RedpacketsWithdrawRecordActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
						config.handler = hand;
						RequestParams params = new RequestParams();
						params.put("key",key);
						params.put("secret",secret);
						params.put("page",1);
						params.put("pagesize",PullRefreshListView.PAGER_SIZE);
						HttpTools.httpGet(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/tiXianOrderList",config, params);
					}
				});
		pullListView.performLoading();
		}
	
	@Override
	public View getContentView() {
		return getLayoutInflater().inflate(R.layout.activity_redpackets_withdraw_record, null);
	}

	@Override
	public String getHeadTitle() {
		return "提现记录";
	}

}
