package com.tg.coloursteward;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tg.coloursteward.adapter.DoorApplyAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.AuthorizationListResp;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 申请门禁
 * @author Administrator
 *
 */
public class DoorApplyActivity extends BaseActivity {
	private final static int INTENT_ACTION_APPLY_FOR_DETAIL = 3;
	private Gson gson = new Gson();
	private EditText edit_mobile;// 电话号码
	private EditText edit_memo;// 备注
	private Button btn_commit;// 确定
	private ListView listview;

	private DoorApplyAdapter adapter;
	private List<AuthorizationListResp> applyList = new ArrayList<AuthorizationListResp>();
	LayoutInflater mInflater = null;

	// 倒数计时器
	private CountDownTimer countDownTimer;
	private long time;
	private TextView txt_time;

	// app内通知
	private LocalBroadcastManager mLocalBroadcastManager;
	private BroadcastReceiver mReceiver;

	// 接受到通知，用户点击通知栏，是否应从main进入系统通知
	public static boolean jumpToDoorApplyMessageCenter = false;
	private String czyid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		jumpToDoorApplyMessageCenter = false;
        czyid = Tools.getCZYID(this);
        prepareView();
        getApplyList();
	}
	@Override
	protected boolean handClickEvent(View v) {
		switch (v.getId()) {
		case R.id.btn_commit://确定
			 setApply();
			break;

		default:
			break;
		}
		return super.handClickEvent(v);
	}
	 private void prepareView() {
	        mInflater = LayoutInflater.from(DoorApplyActivity.this);
	        View view = mInflater.inflate(R.layout.door_apply_header, null);
	        edit_mobile = (EditText) view.findViewById(R.id.edit_mobile);
	        edit_mobile.addTextChangedListener(new TextWatcher() {
	            @Override
	            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	            }

	            @Override
	            public void onTextChanged(CharSequence s, int start, int before, int count) {

	                if (s.toString().length() >= 11) {
	                    btn_commit.setBackgroundResource(R.drawable.btn_shape_pass);
	                } else {
	                    btn_commit.setBackgroundResource(R.drawable.btn_shape_gray);
	                }
	            }

	            @Override
	            public void afterTextChanged(Editable s) {

	            }
	        });
	        edit_memo = (EditText) view.findViewById(R.id.edit_memo);

	        btn_commit = (Button) view.findViewById(R.id.btn_commit);
	        btn_commit.setOnClickListener(singleListener);


	        adapter = new DoorApplyAdapter(DoorApplyActivity.this, applyList);
	        listview = (ListView) findViewById(R.id.listview);
	        listview.addHeaderView(view);
	        listview.setAdapter(adapter);
	        listview.setVerticalScrollBarEnabled(false);
	        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	                if (applyList == null || applyList.size() == 0) {
	                    return;
	                }
	                Intent intent = new Intent(DoorApplyActivity.this, DoorApplyDetailActivity.class);
	                intent.putExtra("applyListResp", applyList.get(position - 1));
	                startActivityForResult(intent, INTENT_ACTION_APPLY_FOR_DETAIL);
	            }
	        });
	        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
	            @Override
	            public void onScrollStateChanged(AbsListView view, int scrollState) {

	                //滑动隐藏键盘
	                if (AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL == scrollState) {
//	                    dismissSoftKeyboard(DoorApplyActivity.this);
	                }
	            }

	            @Override
	            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	            }
	        });


	        countDownTimer = new CountDownTimer(6000, 1000) {

	            @Override
	            public void onTick(long millisUntilFinished) {

	                time = millisUntilFinished / 1000;

	                if (txt_time != null) {
	                    String date = "(提示：此页面将在" + time + "秒内直接关闭，并返回到门禁首页)";
	                    SpannableStringBuilder builder = new SpannableStringBuilder(date);
	                    ForegroundColorSpan redSpan = new ForegroundColorSpan(getResources().getColor(R.color.lightgray));
	                    builder.setSpan(redSpan, 9, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	                    txt_time.setText(builder);
	                }
	            }

	            @Override
	            public void onFinish() {
	                finish();
	            }
	        };
	    }
	 /**
	  * 获取我发出的申请、我拥有的权限列表
	  */
	 private void getApplyList() {
		RequestConfig config = new RequestConfig(this,HttpTools.GET_AUTHORIZATION_LIST);
		RequestParams params = new RequestParams();
		params.put("customer_id", czyid);
		HttpTools.httpGet(Contants.URl.URL_ICETEST,"/newczy/wetown/AuthorizationGetList4topByToID", config, params);
	  }
	 /**
	  * 申请
	  * @param mobile
	  * @param memo
	  */
	 private void setApply(String mobile, String memo) {
		 RequestConfig config = new RequestConfig(this,HttpTools.POST_APPLY_MOBILE);
		 RequestParams params = new RequestParams();
		 params.put("customer_id", czyid);
		 params.put("account", mobile);
		 params.put("memo", memo);
		 HttpTools.httpPost(Contants.URl.URL_ICETEST,"/newczy/wetown/ApplyApply4mobile", config, params); 
	 }
	 private void setApply() {
	        String mobile = edit_mobile.getText().toString();
	        String memo = edit_memo.getText().toString();
	        if (mobile == null || mobile.length() < 11) {
	        	ToastFactory.showToast(DoorApplyActivity.this, "请输入至少11位电话号码查询");
	            return;
	        }
	        setApply(mobile, memo);
	    }
	 /**
	     * 申请返回成功结束页面
	     */
	   private void successApplyBack(String reason) {
		   DialogFactory.getInstance().showDialog(DoorApplyActivity.this, new OnClickListener() {
			   
				@Override
				public void onClick(View v) {
	                countDownTimer.cancel();
	                finish();
				}
			}, new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					countDownTimer.cancel();
	                 finish();
				}
			},reason, null, null);
	    }
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		// TODO Auto-generated method stub
		super.onSuccess(msg, jsonString, hintString);
		String message = HttpTools.getMessageString(jsonString);
		if(msg.arg1 == HttpTools.GET_AUTHORIZATION_LIST){//获取我发出的申请、我拥有的权限列表
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					JSONArray list = response.getJSONArray("list");
					Type type = new TypeToken<List<AuthorizationListResp>>() {}.getType();
					List<AuthorizationListResp> data = gson.fromJson(list.toString(), type);
					applyList.clear();
		            applyList.addAll(data);
		            if (applyList.size() > 0) {
		                listview.setDividerHeight(1);
		            }
		            adapter.notifyDataSetChanged();
				}else {
					ToastFactory.showToast(DoorApplyActivity.this, message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(msg.arg1 == HttpTools.POST_APPLY_MOBILE){//申请
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					 successApplyBack("申请成功");
				}else {
					ToastFactory.showToast(DoorApplyActivity.this, message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_door_apply, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "申请";
	}

}
