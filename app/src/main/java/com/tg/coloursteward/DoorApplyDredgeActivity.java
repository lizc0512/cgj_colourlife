package com.tg.coloursteward;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * 申请手机开门
 * @author Administrator
 *
 */
public class DoorApplyDredgeActivity extends BaseActivity {
	private Button btn_apply_open;
	private TextView txt_apply_sum;
	private String bid;
	private Gson gson = new Gson();
	private int id;

	private int applyCount;// 申请开通人数
	private String czyid;
	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 prepareView();
	     initDate();
	}

	private void prepareView() {

		// 小区没有开通手机开门的布局
		btn_apply_open = (Button) findViewById(R.id.btn_apply_open);
		btn_apply_open.setOnClickListener(singleListener);
		btn_apply_open.setVisibility(View.GONE);
		txt_apply_sum = (TextView) findViewById(R.id.txt_apply_sum);
	}

	private void initDate() {
		czyid = Tools.getCZYID(this);
		bid = getIntent().getStringExtra("bid");
		isApply();
	}
	@Override
	protected boolean handClickEvent(View v) {
		switch (v.getId()) {
		case R.id.right_layout://右上角弹出框
			intent= new Intent(this,MyBrowserActivity.class);
			intent.putExtra(MyBrowserActivity.KEY_URL,"http://www.colourlife.com/Advertisement/Menjin");
			startActivity(intent);
			break;
		case R.id.btn_apply_open://申请开通
			applyOpen();
			break;

		}
		return super.handClickEvent(v);
	}
	 //是否已申请开通
    private void isApply() {
    	Map map = new HashMap<String, String>();
    	map.put("bid", bid);
    	String bid = new Gson().toJson(map);
    	RequestConfig config = new RequestConfig(this, HttpTools.PSOT_APPLY_INFO);
    	RequestParams params = new RequestParams();
    	params.put("params", bid);
    	params.put("customer_id", czyid);
    	params.put("module", "wetown");
    	params.put("func", "doorapply/isapply");
    	HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newczy/wetown/BusinessAgentRequest", config, params);
    }
    //申请开通人数
    private void applyCount() {
    	Map map = new HashMap<String, String>();
    	map.put("bid", bid);
    	String bid = new Gson().toJson(map);
    	RequestConfig config = new RequestConfig(this, HttpTools.PSOT_COUNT_INFO);
    	RequestParams params = new RequestParams();
    	params.put("params", bid);
    	params.put("customer_id", czyid);
    	params.put("module", "wetown");
    	params.put("func", "doorapply/count");
    	HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newczy/wetown/BusinessAgentRequest", config, params);
    }
    //申请开通
    private void applyOpen() {
    	Map map = new HashMap<String, String>();
    	map.put("bid", bid);
    	String bid = new Gson().toJson(map);
    	RequestConfig config = new RequestConfig(this, HttpTools.PSOT_DOORAPPLY_INFO,"申请开通");
    	RequestParams params = new RequestParams();
    	params.put("params", bid);
    	params.put("customer_id", czyid);
    	params.put("module", "wetown");
    	params.put("func", "doorapply/apply");
    	HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newczy/wetown/BusinessAgentRequest", config, params);
    }
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		String message = HttpTools.getMessageString(jsonString);
		if(msg.arg1 == HttpTools.PSOT_APPLY_INFO){//是否已申请开通
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					String isapply = response.get("isapply").toString();
					applyCount();
					 boolean isapply2 = Boolean.parseBoolean(isapply);
	                   if (isapply2) {
	                        btn_apply_open.setVisibility(View.GONE);
	                   } else {
	                        btn_apply_open.setVisibility(View.VISIBLE);
	                   }
				}else {
					ToastFactory.showToast(DoorApplyDredgeActivity.this, message);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
		}else if(msg.arg1 == HttpTools.PSOT_COUNT_INFO){
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					String count = response.get("count").toString();
					if (StringUtils.isNotEmpty(count)) {
						applyCount = Integer.parseInt(count);
						txt_apply_sum.setText("已有" + count + "位邻居申请开通");
					}
				}else {
					ToastFactory.showToast(DoorApplyDredgeActivity.this, message);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
		}else if(msg.arg1 == HttpTools.PSOT_DOORAPPLY_INFO){ //申请开通
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					btn_apply_open.setVisibility(View.GONE);
					applyCount += 1;
					txt_apply_sum.setText("已有" + applyCount + "位邻居申请开通");
					ToastFactory.showToast(DoorApplyDredgeActivity.this, "申请成功");
				}else {
					ToastFactory.showToast(DoorApplyDredgeActivity.this, message);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
		}
	}
	@Override
	public View getContentView() {
		return getLayoutInflater().inflate(R.layout.activity_door_apply_dredge, null);
	}

	@Override
	public String getHeadTitle() {
		headView.setRightImage(R.drawable.icon_open_help);
		headView.setListenerRight(singleListener);
		return "门禁";
	}

}
