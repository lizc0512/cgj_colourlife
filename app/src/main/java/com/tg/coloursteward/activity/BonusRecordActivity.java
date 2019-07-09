package com.tg.coloursteward.activity;

import java.util.ArrayList;

import org.json.JSONArray;

import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.BonusRecordAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.BonusRecordInfo;
import com.tg.coloursteward.info.PunishmentEntityInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.view.PullRefreshListView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
/**
 * 集体奖惩明细
 * @author Administrator
 *
 */
public class BonusRecordActivity extends BaseActivity{
	private PullRefreshListView pullListView;
	private BonusRecordAdapter adapter;
	private ArrayList<BonusRecordInfo> list = new ArrayList<BonusRecordInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		pullListView = (PullRefreshListView)findViewById(R.id.pull_listview);
		adapter = new BonusRecordAdapter(this, list);
		pullListView.setAdapter(adapter);
		pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {

					@Override
					public void refreshData(PullRefreshListView t,
							boolean isLoadMore, Message msg, String response) {
						int code = HttpTools.getCode(response);
						if(code == 0){
							JSONArray content = HttpTools.getContentJsonArray(response);
							if(StringUtils.isNotEmpty(response)){
								ResponseData data = HttpTools.getResponseContent(content);
								if(data.length > 0){
									BonusRecordInfo info ;
									for (int i = 0; i < data.length; i++) {
										info = new BonusRecordInfo();
										info.year = data.getString(i,"year");
										info.month = data.getString(i,"month");
										info.baseBonus = data.getString(i,"jthbfee");//基础奖金包
										info.totalBonus = data.getString(i,"jthbactfee");//总奖金包
										info.decrease = data.getString(i,"jtkk");///扣款总额
										info.chaoys = data.getString(i,"chaoys");//超预算
										info.increase = data.getString(i,"jtjl");//奖励总额
										info.pcbfee = data.getString(i,"pcbfee");//片区总监调剂奖金包
										JSONArray jtkkdata = data.getJSONArray(i,"jtkkdata");
										if(jtkkdata.length() > 0){
											ResponseData jtkk = HttpTools.parseJsonArray(jtkkdata);
											if(jtkk.length > 0 ){
												PunishmentEntityInfo item;
												for (int j = 0; j < jtkk.length; j++) {
													item = new PunishmentEntityInfo();
													item.title = jtkk.getString(j, "title");
													item.flag = jtkk.getString(j, "flag");
													item.money = jtkk.getString(j, "money");
													item.time = jtkk.getString(j, "time");
													info.punishmentList.add(item);
												}
											}
										}
										list.add(info);
									}
									adapter.notifyDataSetChanged();
								}
							}
						}
					}
					@Override
					public void onLoadingMore(PullRefreshListView t,Handler hand, int pagerIndex) {
						RequestConfig config = new RequestConfig(BonusRecordActivity.this, PullRefreshListView.HTTP_MORE_CODE);
						config.handler = hand;
						RequestParams params = new RequestParams();
						params.put("oauser", UserInfo.employeeAccount);
						HttpTools.httpGet(Contants.URl.URL_ICETEST, "/oa/collective",config, params);
					}

					@Override
					public void onLoading(PullRefreshListView t, Handler hand) {
						// TODO Auto-generated method stub
						RequestConfig config = new RequestConfig(BonusRecordActivity.this,PullRefreshListView.HTTP_FRESH_CODE);
						config.handler = hand;
						RequestParams params = new RequestParams();
						params.put("oauser", UserInfo.employeeAccount);
						HttpTools.httpGet(Contants.URl.URL_ICETEST, "/oa/collective",config, params);
					}
				});
		pullListView.performLoading();		
	}

	@Override
	public View getContentView() {
		return getLayoutInflater().inflate(R.layout.activity_bonus_record, null);
	}

	@Override
	public String getHeadTitle() {
		return "集体奖惩明细";
	}

}
