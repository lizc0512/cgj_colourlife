package com.tg.coloursteward;

import java.util.ArrayList;

import org.json.JSONArray;

import com.tg.coloursteward.adapter.BonusRecordPersonalAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.BonusRecordInfo;
import com.tg.coloursteward.info.EffectEntityInfo;
import com.tg.coloursteward.info.PunishmentEntityInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.view.PullRefreshListView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
/**
 * 奖金包(个人)
 * @author Administrator
 *
 */
public class BonusRecordPersonalActivity extends BaseActivity implements OnItemClickListener {
	private PullRefreshListView pullListView;
	private BonusRecordPersonalAdapter adapter;
	private ArrayList<BonusRecordInfo> list = new ArrayList<BonusRecordInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}
	
	private void initView() {
		pullListView = (PullRefreshListView)findViewById(R.id.pull_listview);
		adapter = new BonusRecordPersonalAdapter(this, list);
		pullListView.setAdapter(adapter);
		pullListView.setDividerHeight(0);
		pullListView.setOnItemClickListener(this);
		pullListView.setMinPageSize(15);
		pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {

					@Override
					public void refreshData(PullRefreshListView t,
							boolean isLoadMore, Message msg, String response) {
						int code = HttpTools.getCode(response);
						if(code == 0){
							JSONArray content = HttpTools.getContentJsonArray(response);
							if(response != null ){
								ResponseData data = HttpTools.getResponseContent(content);
								if(data.length > 0){
									BonusRecordInfo info ;
									for (int i = 0; i < data.length; i++) {
										info = new BonusRecordInfo();
										info.year = data.getString(i,"year");
										info.month = data.getString(i,"month");
										info.increase = data.getString(i,"hbfee");
										info.totalBonus = data.getString(i,"fee");
										info.totaljjbbase = data.getString(i,"totaljjbbase");
										info.jjbbase = data.getString(i,"jjbbase");
										//奖励
										JSONArray hbdata = data.getJSONArray(i,"hbdata");
										if(hbdata.length() > 0){
											ResponseData hb = HttpTools.parseJsonArray(hbdata);
											if(hb.length > 0 ){
												EffectEntityInfo item;
												for (int j = 0; j < hb.length; j++) {
													item = new EffectEntityInfo();
													item.title = hb.getString(j, "title");
													item.pinfen = hb.getString(j, "pinfen");
													item.money = hb.getString(j, "money");
													item.proportion = hb.getString(j, "proportion");
													info.effectList.add(item);
												}
											}
											}
											//惩罚
											JSONArray kkdata = data.getJSONArray(i,"kkdata");
											if(kkdata.length() > 0){
												ResponseData kk = HttpTools.parseJsonArray(kkdata);
												if(kk.length > 0 ){
													PunishmentEntityInfo item;
													for (int j = 0; j < kk.length; j++) {
														item = new PunishmentEntityInfo();
														item.title = kk.getString(j, "title");
														item.flag = kk.getString(j, "flag");
														item.money = kk.getString(j, "money");
														item.time = kk.getString(j, "time");
														info.punishmentList.add(item);
													}
												}
											}
											list.add(info);
										}
								}
							}
						}
					}
					@Override
					public void onLoadingMore(PullRefreshListView t,Handler hand, int pagerIndex) {
						RequestConfig config = new RequestConfig(BonusRecordPersonalActivity.this, PullRefreshListView.HTTP_MORE_CODE);
						config.handler = hand;
						RequestParams params = new RequestParams();
						params.put("oauser", UserInfo.employeeAccount);
						HttpTools.httpGet(Contants.URl.URL_ICETEST, "/oa/personage",config, params);
					}

					@Override
					public void onLoading(PullRefreshListView t, Handler hand) {
						// TODO Auto-generated method stub
						RequestConfig config = new RequestConfig(BonusRecordPersonalActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
						config.handler = hand;
						RequestParams params = new RequestParams();
						params.put("oauser", UserInfo.employeeAccount);
						HttpTools.httpGet(Contants.URl.URL_ICETEST, "/oa/personage",config, params);
					}
				});
		pullListView.performLoading();
		}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		BonusRecordInfo info = list.get(position);
		Intent intent = new Intent(BonusRecordPersonalActivity.this,BonusRecordDetailNewActivity.class);
		intent.putExtra("type", "personal");
		intent.putExtra(BonusRecordDetailNewActivity.BONUS_RECORD_INFO,info);
		startActivity(intent);
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_bonus_record_personal, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "奖金包";
	}
}
