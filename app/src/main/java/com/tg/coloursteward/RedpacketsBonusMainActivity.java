package com.tg.coloursteward;

import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.PublicAccountInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.RoundImageView;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 我的饭票
 * @author Administrator
 *
 */
public class RedpacketsBonusMainActivity extends BaseActivity {
	private RelativeLayout rl_team_bonus_detail;//集体奖惩明细
	private RelativeLayout rl_personal_bonus_detail;//我的奖金包明细
	private RelativeLayout rl_ticket_details;//饭票明细
	private RelativeLayout rl_submit;//转账提现
	private RoundImageView rivHead;
	private TextView tvRealName;
	private TextView tv_team_bonus_balance;//奖金红包
	private TextView tv_balance;//饭票余额
	private DisplayImageOptions options;
	private double balance = 0.00d;
	private double teamBonus = 0.00d;
	private String mDept;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getPopup(true);
		initView();
		initOptions();
		initData();
	}
	/**
	 * 根据用户获取该用户的红包发放列表情况
	 * @param secret 
	 * @param key 
	 * 
	 */
	private void getHBUserList(String key, String secret) {
		RequestConfig config = new RequestConfig(this, HttpTools.GET_HBUSER_LIST);
		RequestParams params = new RequestParams();
		params.put("key", key);
		params.put("secret",secret);
		HttpTools.httpGet(Contants.URl.URL_ICETEST,"/hongbao/getHBUserList",config, params);
	}
	private void getEmployeeInfo() {
		String pwd = Tools.getPassWord(this);
		RequestConfig config = new RequestConfig(this, HttpTools.SET_EMPLOYEE_INFO,null);
		RequestParams params = new RequestParams();
		params.put("username", UserInfo.employeeAccount);
		params.put("password",pwd);
		HttpTools.httpPost(Contants.URl.URL_ICETEST,"/czywg/employee/login", config, params);
	}
	
	@Override
	protected boolean handClickEvent(View v) {
		switch (v.getId()) {
		case R.id.rl_team_bonus_detail:// 集体奖惩明细
			startActivity(new Intent(this, BonusRecordActivity.class));
			break;
		case R.id.rl_personal_bonus_detail:// 我的奖金包明细
			startActivity(new Intent(this, BonusRecordPersonalActivity.class));
			break;
		case R.id.rl_ticket_details:// 饭票明细
			submit();
			//startActivity(new Intent(this, RedpacketsRecordActivity.class));
			break;
		case R.id.rl_submit:// 转账提现
			startActivity(new Intent(this, RedpacketsMainActivity.class));
			break;
		}
		return super.handClickEvent(v);
	}

	/**
	 * 请求OA金融平台数据
	 */
	private void submit() {
		RequestParams params = new RequestParams();
		params.put("oa_username", UserInfo.employeeAccount);
		HttpTools.httpGet(Contants.URl.URL_ICETEST,"/newczy/employee/getFinanceByOa",
				new RequestConfig(this, HttpTools.GET_USER_INFO,"获取员工金融信息"), params);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		rivHead = (RoundImageView) findViewById(R.id.riv_head);
		tvRealName = (TextView) findViewById(R.id.tv_real_Name);
		tv_team_bonus_balance = (TextView) findViewById(R.id.tv_team_bonus_balance);
		tv_balance = (TextView) findViewById(R.id.tv_balance);
		rl_team_bonus_detail = (RelativeLayout) findViewById(R.id.rl_team_bonus_detail);
		rl_personal_bonus_detail = (RelativeLayout) findViewById(R.id.rl_personal_bonus_detail);
		rl_ticket_details = (RelativeLayout) findViewById(R.id.rl_ticket_details);
		rl_submit = (RelativeLayout) findViewById(R.id.rl_submit);
		rl_team_bonus_detail.setOnClickListener(singleListener);
		rl_personal_bonus_detail.setOnClickListener(singleListener);
		rl_ticket_details.setOnClickListener(singleListener);
		rl_submit.setOnClickListener(singleListener);
		String jsonStr = Tools.getStringValue(RedpacketsBonusMainActivity.this,Contants.storage.TICKET);
		if(StringUtils.isNotEmpty(jsonStr)){
			JSONObject jsonObject = HttpTools.getContentJSONObject(jsonStr);
			if(jsonObject != null ){
				try {
					mDept = jsonObject.getString("place");
					balance = jsonObject.getDouble("balance");
					teamBonus = jsonObject.getDouble("fee");
					setData();
				} catch (JSONException e) {
					e.printStackTrace();
				}
		}
	}
	}
	private void initOptions()
	{
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.placeholder2)
				.showImageForEmptyUri(R.drawable.placeholder2)
				.showImageOnFail(R.drawable.placeholder2).cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true)
				// .displayer(new RoundedBitmapDisplayer(20))
				.build();
	}
	public void initData(){
		String str = Contants.URl.HEAD_ICON_URL +"avatar?uid=" + UserInfo.employeeAccount;
		ImageLoader.getInstance().clearMemoryCache();
		ImageLoader.getInstance().clearDiskCache();
		ImageLoader.getInstance().displayImage(str, rivHead, options);
	}
	
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		int code = HttpTools.getCode(jsonString);
		String message = HttpTools.getMessageString(jsonString);
		if(msg.arg1 ==HttpTools.GET_USER_INFO){//请求金融平台数据
			if(code == 0){
				JSONObject content = HttpTools.getContentJSONObject(jsonString);
				try {
					String cano = content.getString("cano");
					String pano = content.getString("pano");
					String cno = content.getString("cno");
					Intent intent = new Intent(RedpacketsBonusMainActivity.this, RedpacketsDetailsActivity.class);
					intent.putExtra(RedpacketsDetailsActivity.PUBLICACCOUNT_CANO,cano);
					intent.putExtra(RedpacketsDetailsActivity.PUBLICACCOUNT_PANO,pano);
					intent.putExtra(RedpacketsDetailsActivity.PUBLICACCOUNT_CNO,cno);
					startActivity(intent);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else {
				ToastFactory.showToast(RedpacketsBonusMainActivity.this,message);
			}
		}else if(msg.arg1 == HttpTools.SET_EMPLOYEE_INFO){
			if(code == 0){
				JSONObject content = HttpTools.getContentJSONObject(jsonString);
				if(content != null){
					try {
						String key = content.getString("key");
						String secret = content.getString("secret");
						//保存key  sectet
						Tools.saveStringValue(RedpacketsBonusMainActivity.this, Contants.EMPLOYEE_LOGIN.key,key);
						Tools.saveStringValue(RedpacketsBonusMainActivity.this, Contants.EMPLOYEE_LOGIN.secret,secret);
						getHBUserList(key, secret);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}else{
				ToastFactory.showToast(RedpacketsBonusMainActivity.this,message);
			}
		}else{
			if(code == 0){
				Tools.saveStringValue(RedpacketsBonusMainActivity.this,Contants.storage.TICKET,jsonString);
				JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
				if(jsonObject != null ){
					try {
						mDept = jsonObject.getString("place");
						balance = jsonObject.getDouble("balance");
						teamBonus = jsonObject.getDouble("fee");
						setData();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}else{
				ToastFactory.showToast(RedpacketsBonusMainActivity.this, message);
			}
		}
	}
	
	private void setData() {
		DecimalFormat df = new DecimalFormat("0.00");
		tvRealName.setText(UserInfo.realname+"("+mDept+")");
		tv_balance.setText(df.format(balance));
		tv_team_bonus_balance.setText(df.format(teamBonus));
	}
	@Override
	protected void onResume() {
		super.onResume();
		String key = Tools.getStringValue(this,Contants.EMPLOYEE_LOGIN.key);
        String secret = Tools.getStringValue(this,Contants.EMPLOYEE_LOGIN.secret);
		if(StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(secret)){
			getHBUserList(key, secret);
		}else{
			getEmployeeInfo();
		}

	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_redpackets_bonus_main,null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "我的饭票";
	}

}
