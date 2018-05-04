package com.tg.coloursteward;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.tg.coloursteward.adapter.HistoryTransferAdapter;
import com.tg.coloursteward.adapter.ThreeElementsAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.HistoryTransferInfo;
import com.tg.coloursteward.info.RedpacketsInfo;
import com.tg.coloursteward.info.ThreeElementsInfo;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.updateapk.UpdateManager;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.PullRefreshListView;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 给同事发饭票
 * @author Administrator
 *
 */
public class RedpacketsShareMainActivity extends BaseActivity {
	private static final String TAG = "RedpacketsShareMainActi";
	/**
	 * 同事OA账号或手机号码
	 */
	private EditText edtColleagueInfo;

	/**
	 * 下一步
	 */
	private RelativeLayout rlSubmit;

	/**
	 * 历史记录ListView
	 */
	private PullRefreshListView pullListView;

	/**
	 * 获取到的员工oa信息
	 */
	private LinearLayout lloaInfo;

	/**
	 * 获取到的员工oa姓名
	 */
	private TextView tvOaName;

	/**
	 * 获取到的员工oa用户名
	 */
	private TextView tvOaUsername;

	/**
	 * 获取到的员工oa手机号
	 */
	private TextView tvOaMobile;

	/**
	 * oa账号或手机号
	 */
	private String colleagueInfo;
	
	private String receiver_id;
	private String receiverName;
	private String receiverOA;
	private String receiverMobile;


	/**
	 * 给同事发红包（colleague）/转账到彩之云（czy）/提现到银行卡（bank）
	 */
	private String transferTo;

	/**
	 * 红包余额
	 */
	private Double balance;


	
	private ImageButton btnNext;

	/**
	 * oa发红包记录的Adapter
	 */
	private HistoryTransferAdapter adapter;
	private String key;
	private String secret;
	private Intent intent;
	private int OA;
	final ArrayList<RedpacketsInfo> list = new ArrayList<RedpacketsInfo>();
	private ArrayList<HistoryTransferInfo> pageInfoList = new ArrayList<HistoryTransferInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getPopup(false);
		transferTo = getIntent().getStringExtra("colleague");
		colleagueInfo = getIntent().getStringExtra(Contants.PARAMETER.OA);
		balance = getIntent().getDoubleExtra(Contants.PARAMETER.BALANCE, 0.00);
		key = Tools.getStringValue(this,Contants.EMPLOYEE_LOGIN.key);
        secret = Tools.getStringValue(this,Contants.EMPLOYEE_LOGIN.secret);
		initView();
		if(StringUtils.isNotEmpty(colleagueInfo)){
			edtColleagueInfo.setText(colleagueInfo);
			check();
		}
	}
	@Override
	protected boolean handClickEvent(View v) {
		switch (v.getId()) {
		case R.id.imgbtn_contact://通讯录
			intent = new Intent(RedpacketsShareMainActivity.this,RedpacketsContactsActivity.class);
			startActivityForResult(intent, 1001);
			break;
		case R.id.rl_submit://下一步
			check();
			break;
		}
		return super.handClickEvent(v);
	}
	private void initView() {
		edtColleagueInfo = (EditText) findViewById(R.id.edt_colleague_info);
		lloaInfo = (LinearLayout) findViewById(R.id.ll_oa_info);
		tvOaName = (TextView) findViewById(R.id.tv_oa_name);
		tvOaUsername = (TextView) findViewById(R.id.tv_oa_username);
		tvOaMobile = (TextView) findViewById(R.id.tv_oa_mobile);
		rlSubmit = (RelativeLayout) findViewById(R.id.rl_submit);
		btnNext = (ImageButton) findViewById(R.id.imgbtn_contact);
		rlSubmit.setOnClickListener(singleListener);
		btnNext.setOnClickListener(singleListener);
		lloaInfo.setVisibility(View.GONE);

		edtColleagueInfo.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				/*if (s.length() == 11 && Tools.isNumeric(s.toString())) {
					// 通过手机号或者OA账号读取同事信息
					getEmployeeInfo(edtColleagueInfo.getText().toString());
					OA = 0;
				} else if (View.VISIBLE == lloaInfo.getVisibility()) {
					lloaInfo.setVisibility(View.GONE);
				}*/
			}
		});
		pullListView = (PullRefreshListView) findViewById(R.id.pull_listview);
		pullListView.setDividerHeight(0);
		adapter = new HistoryTransferAdapter(this,pageInfoList);
		pullListView.setAdapter(adapter);
		pullListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HistoryTransferInfo info = pageInfoList.get(position);
				Intent intent = new Intent(RedpacketsShareMainActivity.this,
						RedpacketsTransferMainActivity.class);
				intent.putExtra(Contants.PARAMETER.TRANSFERTO,"colleague");
				intent.putExtra(Contants.PARAMETER.BALANCE,balance);
				intent.putExtra("name", info.receiverName);
				intent.putExtra("username", info.receiverOA);
				intent.putExtra(Contants.PARAMETER.MOBILE,info.receiverMobile);
				intent.putExtra(Contants.PARAMETER.USERID,info.receiver_id);
				startActivity(intent);
//				finish();
			}
		});
		pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
			
			@Override
			public void refreshData(PullRefreshListView t, boolean isLoadMore,
					Message msg, String response) {
				int code  = HttpTools.getCode(response);
				if(code == 0){
					JSONObject content = HttpTools.getContentJSONObject(response);
					String sponse = HttpTools.getContentString(response);
					if(content != null){
						try {
							if (content.getJSONArray("CarryJsonList") != null) {
								ResponseData data = HttpTools.getResponseKey(sponse, "CarryJsonList");
								HistoryTransferInfo info ;
								for (int i = 0; i < data.length; i++) {
									info = new HistoryTransferInfo();
									info.receiver_id = data.getString(i, "receiver_id");
									info.receiverName = data.getString(i, "receiverName");
									info.receiverOA = data.getString(i, "receiverOA");
									info.receiverMobile = data.getString(i, "receiverMobile");
									pageInfoList.add(info);
								}
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						}
					}
			}
			
			@Override
			public void onLoadingMore(PullRefreshListView t, Handler hand, int pageIndex) {
				RequestConfig config = new RequestConfig(RedpacketsShareMainActivity.this, PullRefreshListView.HTTP_MORE_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				params.put("key", key);
				params.put("secret",secret);
				params.put("page",pageIndex);
				params.put("pagesize", PullRefreshListView.PAGER_SIZE);
				HttpTools.httpGet(Contants.URl.URL_ICETEST, "/hongbao/carryList",config, params);
				
			}
			
			@Override
			public void onLoading(PullRefreshListView t, Handler hand) {
				RequestConfig config = new RequestConfig(RedpacketsShareMainActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				params.put("key", key);
				params.put("secret",secret);
				params.put("page", 1);
				params.put("pagesize", PullRefreshListView.PAGER_SIZE);
				HttpTools.httpGet(Contants.URl.URL_ICETEST, "/hongbao/carryList",config, params);
			}
		});
		pullListView.performLoading();
	}
	
	/**
	 * 根据输入手机号获oa账号搜索
	 * @param username
	 */
	private  void getEmployeeInfo(String username) {
		/**
		 * 获取版本号
		 */
		String versionShort = UpdateManager.getVersionName(this);
		String key = Tools.getStringValue(this,Contants.EMPLOYEE_LOGIN.key);
        String secret = Tools.getStringValue(this,Contants.EMPLOYEE_LOGIN.secret);
		Log.e(TAG, "getEmployeeInfo:key   "+key );
		Log.e(TAG, "getEmployeeInfo:secret   "+secret );
		RequestConfig config = new RequestConfig(RedpacketsShareMainActivity.this, HttpTools.GET_EMPLOYEE_INFO,"查询");
		RequestParams params = new RequestParams();
		params.put("username", username);
		params.put("version", versionShort);
		params.put("key", key);
		params.put("secret",secret);
		HttpTools.httpPost(Contants.URl.URL_ICETEST, "/hongbao/getEmployeeInfo", config, params);
//		这个接口改造过
	}
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		// TODO Auto-generated method stub
		super.onSuccess(msg, jsonString, hintString);
		int code = HttpTools.getCode(jsonString);
		String message = HttpTools.getMessageString(jsonString);
		if (code == 0) {
			JSONArray content = HttpTools.getContentJsonArray(jsonString);
			if(content != null){
				ResponseData data = HttpTools.getResponseContent(content);
				if(data.length >0){
					RedpacketsInfo info ;
					for(int i = 0; i < data.length; i++) {
						info = new RedpacketsInfo();
						info.receiver_id = data.getString(i,"id");
						info.receiverName = data.getString(i,"name");
						info.receiverOA = data.getString(i,"username");
						info.receiverMobile = data.getString(i,"mobile");
						list.add(info);
					}
				}
			}
			if(list.size() > 0){
				if(list.size() == 1){
					intent = new Intent(RedpacketsShareMainActivity.this,RedpacketsTransferMainActivity.class);
					intent.putExtra(Contants.PARAMETER.TRANSFERTO,"colleague");
					intent.putExtra(Contants.PARAMETER.BALANCE,balance);
					intent.putExtra("name",list.get(0).receiverName);
					intent.putExtra("username",list.get(0).receiverOA);
					intent.putExtra(Contants.PARAMETER.MOBILE, list.get(0).receiverMobile);
					intent.putExtra(Contants.PARAMETER.USERID,list.get(0).receiver_id);
					startActivity(intent);
					list.clear();

//					finish();
				}else {
					intent = new Intent(RedpacketsShareMainActivity.this,RedpacketsAccountListActivity.class);
					intent.putExtra(Contants.PARAMETER.BALANCE,balance);
					//intent.putExtra(RedpacketsAccountListActivity.REDPACKETS_LIST,list);
					Bundle bundleObject = new Bundle();
					bundleObject.putSerializable(RedpacketsAccountListActivity.REDPACKETS_LIST, list);
					intent.putExtras(bundleObject);
					startActivity(intent);
					list.clear();
//					finish();
				}
			}else {
				ToastFactory.showToast(RedpacketsShareMainActivity.this, "你输入的账号有误！");
			}
		} else {
			ToastFactory.showToast(RedpacketsShareMainActivity.this, message);
		}
		/*if(code == 0){
			JSONObject content = HttpTools.getContentJSONObject(jsonString);
			if(content != null){
				try {
					if(content.getInt("ok") == 1){
						JSONObject object = content.getJSONObject("employeeInfo");
						receiver_id = object.getString("id");
						receiverName = object.getString("name");
						receiverOA = object.getString("username");
						receiverMobile = object.getString("mobile");
					}
					lloaInfo.setVisibility(View.VISIBLE);
					tvOaName.setText(receiverName);
					tvOaUsername.setText(receiverOA);
					if (receiverMobile.length() > 0) {
						tvOaMobile.setText(receiverMobile);
					} else {
						tvOaMobile.setText(" ");
					}
					if(OA == 1){//搜索oa（不是手机号），直接跳到下一个页面
						intent = new Intent(RedpacketsShareMainActivity.this,RedpacketsTransferMainActivity.class);
						intent.putExtra(Contants.PARAMETER.TRANSFERTO,"colleague");
						intent.putExtra(Contants.PARAMETER.BALANCE,balance);
						intent.putExtra("name",receiverName);
						intent.putExtra("username",receiverOA);
						intent.putExtra(Contants.PARAMETER.MOBILE, receiverMobile);
						intent.putExtra(Contants.PARAMETER.USERID,receiver_id);
						startActivity(intent);
						finish();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}else{
			ToastFactory.showToast(RedpacketsShareMainActivity.this,message);
		}
		*/
	}
	
	/**
	 * 下一步
	 */
	public void check() {
		if (edtColleagueInfo.getText().length() == 0) {
			ToastFactory.showToast(this, "请输入同事信息");
			return;
		}
		getEmployeeInfo(edtColleagueInfo.getText().toString());
		/*if (View.VISIBLE == lloaInfo.getVisibility()) {
			intent = new Intent(RedpacketsShareMainActivity.this,RedpacketsTransferMainActivity.class);
			intent.putExtra(Contants.PARAMETER.TRANSFERTO,"colleague");
			intent.putExtra(Contants.PARAMETER.BALANCE,balance);
			intent.putExtra("name", receiverName);
			intent.putExtra("username",receiverOA);
			intent.putExtra(Contants.PARAMETER.USERID,receiver_id);
			intent.putExtra(Contants.PARAMETER.MOBILE,receiverMobile);
			startActivity(intent);
			finish();
		} else {
			// 通过手机号或者OA账号读取同事信息
			getEmployeeInfo(edtColleagueInfo.getText().toString());
			OA = 1;
		}*/
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 1002:
			colleagueInfo = data.getStringExtra("phoneNum");
			edtColleagueInfo.setText(colleagueInfo);
			break;
		default:
			break;
		}
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_redpackets_share_main, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "给同事发饭票";
	}

}
