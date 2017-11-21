package com.tg.coloursteward;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tg.coloursteward.adapter.DoorAutorAdapter;
import com.tg.coloursteward.adapter.SelectCommunityAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.AuthorizationListResp;
import com.tg.coloursteward.info.CommunityResp;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.coloursteward.view.dialog.SelectCommunityDialog;
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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
/**
 * 门禁授权
 * @author Administrator
 *
 */
public class DoorAuthorizationActivity extends BaseActivity {
	    private ListView listview;

	    private DoorAutorAdapter adapter;
	    private List<AuthorizationListResp> authorList = new ArrayList<AuthorizationListResp>();
	    LayoutInflater mInflater = null;
	    // 倒数计时器
	    private CountDownTimer countDownTimer;
	    private long time;
	    private TextView txt_time;
	    private Button btn_hour,//2小时
	            btn_one_day,//一天
	            btn_seven_days,//7天
	            btn_years,//一年
	            btn_permanent,//永久;
	            btn_commit;//授权确定
	    private EditText edit_mobile,//电话号码
	            edit_memo;//备注
	    private TextView edit_community;//小区名称
	    // 小区列表
	    private List<CommunityResp> communityList = new ArrayList<CommunityResp>();
	    // 当前小区
	    private CommunityResp communityResp;
	    // 保存小区选中状态 哪一个小区被选中
	    private int whichCommunitySel = 0;

	    private String usertype = "4";
	    // app内通知
	    private LocalBroadcastManager mLocalBroadcastManager;
	    private BroadcastReceiver mReceiver;
	    // 跳转到授权详情（批复授权和取消授权）
	    private final static int INTENT_ACTION_OPEN_AUTHORIZATION_DETAIL = 1;

	    // 接受到通知，用户点击通知栏，是否应从main进入系统通知
	    public static boolean jumpToDoorAuthMessageCenter = false;
		private String czyid;
		private Gson gson =new Gson();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		jumpToDoorAuthMessageCenter = false;
		czyid = Tools.getCZYID(this);
		prepareView();
		getAuthorList();
	}
	@Override
	protected boolean handClickEvent(View v) {
		switch (v.getId()) {
		case R.id.btn_hour:
            setChooseBtnSelector(1);
            break;
        case R.id.btn_one_day:
            setChooseBtnSelector(2);
            break;
        case R.id.btn_seven_days:
            setChooseBtnSelector(3);
            break;
        case R.id.btn_years:
            setChooseBtnSelector(4);
            break;
        case R.id.btn_permanent:
            setChooseBtnSelector(5);
            break;
        case R.id.edit_community:
            //选择小区
            if (communityList.size() > 1) {
                selectCommunity();
            }
            break;
        case R.id.btn_autor:
            setAutor();
            break;

		}
	return super.handClickEvent(v);
	}
	 
	private void prepareView() {
	        mInflater = LayoutInflater.from(DoorAuthorizationActivity.this);
	        View view = mInflater.inflate(R.layout.activity_door_authorization_header, null);

	        btn_hour = (Button) view.findViewById(R.id.btn_hour);
	        btn_hour.setOnClickListener(singleListener);
	        btn_hour.setSelected(true);
	        btn_one_day = (Button) view.findViewById(R.id.btn_one_day);
	        btn_one_day.setOnClickListener(singleListener);
	        btn_seven_days = (Button) view.findViewById(R.id.btn_seven_days);
	        btn_seven_days.setOnClickListener(singleListener);
	        btn_years = (Button) view.findViewById(R.id.btn_years);
	        btn_years.setOnClickListener(singleListener);
	        btn_permanent = (Button) view.findViewById(R.id.btn_permanent);
	        btn_permanent.setOnClickListener(singleListener);

	        edit_mobile = (EditText) view.findViewById(R.id.edit_mobile);
	        edit_community = (TextView) view.findViewById(R.id.edit_community);
	        edit_community.setOnClickListener(singleListener);
	        edit_memo = (EditText) view.findViewById(R.id.edit_memo);

	        btn_commit = (Button) view.findViewById(R.id.btn_autor);
	        btn_commit.setOnClickListener(singleListener);
	        adapter = new DoorAutorAdapter(DoorAuthorizationActivity.this, authorList);
	        listview = (ListView) findViewById(R.id.listview);
	        listview.addHeaderView(view);
	        listview.setAdapter(adapter);
	        listview.setVerticalScrollBarEnabled(false);
	        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	                AuthorizationListResp authorizationListResp = authorList.get(position - 1);
	                if (authorizationListResp == null) {
	                    return;
	                }
	                if ("1".equals(authorizationListResp.getType())) {
	                    //批复界面
	                    Intent intent = new Intent(DoorAuthorizationActivity.this, DoorAuthorizationApproveActivity.class);
	                    intent.putExtra("authorListResp", authorizationListResp);
	                    intent.putExtra("refuse", true);
	                    startActivityForResult(intent,
	                            INTENT_ACTION_OPEN_AUTHORIZATION_DETAIL);
	                } else {
	                    //通过 已失效
	                    Intent authorizerIntent = new Intent(DoorAuthorizationActivity.this,DoorControlAuthorizationDetailActivity2.class);
	                    authorizerIntent.putExtra("authorizationListResp",
	                            authorizationListResp);
	                    startActivityForResult(authorizerIntent,
	                            INTENT_ACTION_OPEN_AUTHORIZATION_DETAIL);
	                }
	            }
	        });
	        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
	            @Override
	            public void onScrollStateChanged(AbsListView view, int scrollState) {

	                //滑动隐藏键盘
	                if (AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL == scrollState) {
//	                    dismissSoftKeyboard(DoorAuthorizationActivity.this);
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

	    }

	/**
	 * 选中状态设置
	 */
	 private void setChooseBtnSelector(int index) {
	        btn_hour.setSelected(false);
	        btn_one_day.setSelected(false);
	        btn_seven_days.setSelected(false);
	        btn_years.setSelected(false);
	        btn_permanent.setSelected(false);
	        switch (index) {
	            case 1:
	                usertype = "4";
	                btn_hour.setSelected(true);
	                break;
	            case 2:
	                usertype = "3";
	                btn_one_day.setSelected(true);
	                break;
	            case 3:
	                usertype = "2";
	                btn_seven_days.setSelected(true);
	                break;
	            case 4:
	                usertype = "5";
	                btn_years.setSelected(true);
	                break;
	            case 5:
	                usertype = "1";
	                btn_permanent.setSelected(true);
	                break;
	        }
	    }
	   /**
	     * 获取授权,小区列表
	     */
	   private void getAuthorList() {
		   RequestConfig config = new RequestConfig(this,HttpTools.GET_AUTOR_INFO);
		   RequestParams params = new RequestParams();
		   params.put("customer_id", czyid);
		   HttpTools.httpGet(Contants.URl.URL_ICETEST,"/newczy/wetown/AuthorizationGetList4top", config, params);
	    }
	   /**
	    * 获取用户拥有住户权限的小区列表
	    */
	   private void getCommunityList()  {
		   RequestConfig config = new RequestConfig(this,HttpTools.GET_COMMUNITY_INFO);
		   RequestParams params = new RequestParams();
		   params.put("customer_id", czyid);
		   HttpTools.httpGet(Contants.URl.URL_ICETEST,"/newczy/wetown/UserCommunityList", config, params);
	   }
	 /**
	  * 主动授权
	  */
	    private void setAutor() {
	        String mobile = edit_mobile.getText().toString();
	        long currentTime = System.currentTimeMillis() / 1000;
	        long stop = 0;
	        // 二次授权，0没有，1有
	        String granttype = "";
	        // 授权类型，0临时，1限时，2永久
	        String autype = "";
	        String starttime = currentTime + "";
	        String stoptime = "";

	        if ("1".equals(usertype)) {
	            starttime = "0";
	            stoptime = "0";
	            granttype = "1";
	            autype = "2";
	        } else {
	            autype = "1";
	            granttype = "0";

	            if ("2".equals(usertype)) {

	                stop = currentTime + 3600 * 24 * 7;

	            } else if ("3".equals(usertype)) {

	                stop = currentTime + 3600 * 24;

	            } else if ("4".equals(usertype)) {

	                stop = currentTime + 3600 * 2;

	            } else if ("5".equals(usertype)) {
	                stop = currentTime + 3600 * 24 * 365;
	            }

	            starttime = phpToTimeString(currentTime + "");
	            stoptime = phpToTimeString(stop + "");
	        }
	        String memo = edit_memo.getText().toString();

	        if (mobile == null || mobile.length() < 11) {
	        	ToastFactory.showToast(DoorAuthorizationActivity.this,"请输入至少11位电话号码查询");
	            return;
	        }
	        String bid = "";
	        if (communityResp != null) {
	            bid = communityResp.getBid();
	        } else {
	        	ToastFactory.showToast(DoorAuthorizationActivity.this,"请选择小区");
	            return;
	        }
		RequestConfig config = new RequestConfig(this,HttpTools.POST_AUTOR_INFO);
		RequestParams params = new RequestParams();
		params.put("customer_id", czyid);
		params.put("mobile", mobile);
		params.put("bid", bid);
		params.put("usertype", usertype);
		params.put("granttype", granttype);
		params.put("autype", autype);
		if (!"1".equals(usertype))
		{
			params.put("starttime", starttime);
			params.put("stoptime", stoptime);
		}
		params.put("memo", memo);
		HttpTools.httpPost(Contants.URl.URL_ICETEST,"/newczy/wetown/AuthorizationAuthorize4mobile", config, params);
	   }
	    
	    public static String phpToTimeString(String phpDate) {

	        String date = phpDate + "000";
	        String dateTime = "";
	        try {
	            Long dateLong = Long.parseLong(date);

	            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	            dateTime = df.format(dateLong);
	        } catch (Exception e) {
	        }

	        return dateTime;

	    }
	    
	 @Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		String message = HttpTools.getMessageString(jsonString);
		if(msg.arg1 == HttpTools.POST_AUTOR_INFO){//主动授权
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					successAuthorBack("授权成功");
				}else {
					ToastFactory.showToast(DoorAuthorizationActivity.this, message);
				}
				} catch (JSONException e) {
					e.printStackTrace();
				}
		}else if(msg.arg1 == HttpTools.GET_AUTOR_INFO){// 获取授权,小区列表
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					JSONArray list = response.getJSONArray("list");
					Type type = new TypeToken<List<AuthorizationListResp>>() {
					}.getType();
					List<AuthorizationListResp> data = gson.fromJson(list.toString(), type);
					authorList.clear();
		            authorList.addAll(data);
		            if (authorList.size() > 0) {
		                listview.setDividerHeight(1);
		            }
		            adapter.notifyDataSetChanged();
		            getCommunityList();
				}else {
					ToastFactory.showToast(DoorAuthorizationActivity.this, message);
				}
				} catch (JSONException e) {
					e.printStackTrace();
				}
		}else if(msg.arg1 == HttpTools.GET_COMMUNITY_INFO){//获取用户拥有住户权限的小区列表
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					JSONArray list = response.getJSONArray("communitylist");
					Type type = new TypeToken<List<CommunityResp>>() {
					}.getType();
					List<CommunityResp> data = gson.fromJson(
							list.toString(), type);
					if (data != null) {
		                communityResp = data.get(0);
		                communityList.clear();
		                communityList.addAll(data);
		                edit_community.setText(data.get(0).getName());
		                Tools.saveCZY_CommunityList(DoorAuthorizationActivity.this, data, czyid);
		            }
				}else {
					ToastFactory.showToast(DoorAuthorizationActivity.this, message);
				}
				} catch (JSONException e) {
					e.printStackTrace();
				}
		}
		
	}
	 /**
	     * 申请返回成功结束页面
	     */
	   private void successAuthorBack(String reason) {
		   DialogFactory.getInstance().showDialog(DoorAuthorizationActivity.this, new OnClickListener() {
			   
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
	 /**
	     * 选择小区弹窗
	     */
	private void selectCommunity() {

        final SelectCommunityDialog dialog = new SelectCommunityDialog(
                DoorAuthorizationActivity.this,
                R.style.selectorDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        // 添加选项名称
        TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
        tv_title.setText("选择小区");

        ListView listView = (ListView) dialog.findViewById(R.id.listview);

        final SelectCommunityAdapter adapter = new SelectCommunityAdapter(
                DoorAuthorizationActivity.this, communityList,
                whichCommunitySel);

        listView.setAdapter(adapter);

        if (communityList != null && communityList.size() > 3) {
            setListViewHeightBasedOnChildren(listView);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                CommunityResp data = communityList.get(position);

                if (data != null) {
                    communityResp = data;
                    whichCommunitySel = position;
                    adapter.setWhichCommunitySel(whichCommunitySel);
                    edit_community.setText(communityResp.getName());
                }
                dialog.dismiss();
            }
        });
	    }
	/**
     * 动态计算设置AbsListView高度
     *
     * @param absListView
     * @函数名 setAbsListViewHeightBasedOnChildren
     * @功能 TODO
     * @备注 <其它说明>
     */
    private void setListViewHeightBasedOnChildren(ListView absListView) {

        ListAdapter listAdapter = absListView.getAdapter();
        if (listAdapter != null && listAdapter.getCount() > 0) {

            View view = listAdapter.getView(0, null, absListView);
            view.measure(0, 0);
            int totalHeight = 0;

            totalHeight = view.getMeasuredHeight() * 3;


            ViewGroup.LayoutParams params = absListView.getLayoutParams();
            params.height = totalHeight;
            absListView.setLayoutParams(params);
        }
    }
   
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mLocalBroadcastManager != null && mReceiver != null) {

            mLocalBroadcastManager.unregisterReceiver(mReceiver);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_ACTION_OPEN_AUTHORIZATION_DETAIL) {
            // 跳转到授权详情（批复授权和取消授权）
            if (resultCode == RESULT_OK) {
                // 需要刷新授权列表
//                authorModel.getAuthorList(this, true);
            		getAuthorList();
            }
        }
    }
    @Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_door_authorization, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "授权";
	}


}
