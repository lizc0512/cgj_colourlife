package com.tg.coloursteward;

import org.json.JSONException;
import org.json.JSONObject;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.PwdDialog2;
import com.tg.coloursteward.view.dialog.PwdDialog2.ADialogCallback;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 给同事发饭票
 * 
 * @author Administrator
 * 
 */
public class RedpacketsTransferMainActivity extends BaseActivity {
	private Intent intent;
	private String receiver_id;
	private String receiverName;
	private String receiverOA;
	private String receiverMobile;
	/**
	 * 转账金额
	 */
	private String transferAmount;
	/**
	 * 红包余额
	 */
	private Double balance;
	/**
	 * 给同事发红包（colleague）/转账到彩之云（czy）/提现到银行卡（bank）
	 */
	private String transferTo;
	/**
	 * 输入金额EditText
	 */
	private EditText edtAmount;
	/**
	 * 捎一句话EditText
	 */
	private EditText edtMessage;
	/**
	 * 0转账到OA/1转账绑定的彩之云账号
	 */
	private String type;
	/**
	 * 彩之云绑定记录的id
	 */
	private String id;
	
	private TextView tv_head;
	private TextView tv_czy_account;
	private TextView tv_change_bind;
	private TextView tv_ticket;
	private TextView tv_receiver;
	private LinearLayout ll_submit;
	private LinearLayout ll_czy;
	private PwdDialog2 aDialog;
	private ADialogCallback aDialogCallback;
	private String key;
	private String secret;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent = getIntent();
		if(intent != null){
			transferTo =intent.getStringExtra(Contants.PARAMETER.TRANSFERTO);
			balance = intent.getDoubleExtra(Contants.PARAMETER.BALANCE, 0.00);
			receiverName = intent.getStringExtra("name");
			receiverOA = intent.getStringExtra("username");
			receiverMobile = intent.getStringExtra(Contants.PARAMETER.MOBILE);
			receiver_id = intent.getStringExtra(Contants.PARAMETER.USERID);
		}
		initView();
	}

	@Override
	protected boolean handClickEvent(View v) {
		switch (v.getId()) {
		case R.id.tv_change_bind://更改
			// 成功验证密码后执行解绑操作
			aDialogCallback = new ADialogCallback() {
				@Override
				public void callback() {
					// 删除绑定彩之云
					bindColourLifeDelete();
				}
			};
			aDialog = new PwdDialog2(
					RedpacketsTransferMainActivity.this,
					R.style.choice_dialog, "input", aDialogCallback);
			aDialog.show();
			break;
			
		case R.id.ll_submit://确定
			if (check()) {
				aDialogCallback = new ADialogCallback() {
					@Override
					public void callback() {
						Intent intent = new Intent();
						String transferNote = edtMessage.getText().toString();
						intent.setClass(RedpacketsTransferMainActivity.this,RedpacketsWithdrawFinishedActivity.class);
						intent.putExtra(Contants.PARAMETER.USERID,receiver_id);
						intent.putExtra("type", type);
						intent.putExtra(Contants.PARAMETER.WITHDRAW_AMOUNT,transferAmount);
						intent.putExtra(Contants.PARAMETER.TRANSFERNOTE,transferNote);
						if ("colleague".equals(transferTo)) {
							intent.putExtra("name", receiverName);
							intent.putExtra(Contants.PARAMETER.TRANSFERTO,"colleague");
							intent.putExtra("username", receiverOA);
						} else if ("czy".equals(transferTo)) {
							intent.putExtra(Contants.PARAMETER.TRANSFERTO,"czy");
							intent.putExtra(Contants.PARAMETER.MOBILE,receiverMobile);
						}
						startActivity(intent);
						finish();
					}
				};
				aDialog = new PwdDialog2(
						RedpacketsTransferMainActivity.this,
						R.style.choice_dialog, "inputPwd", aDialogCallback);
				aDialog.show();
			}
			break;

		}
		return super.handClickEvent(v);
	}
	/**
	 * 初始化
	 */
	private void initView() {
		key = Tools.getStringValue(this,Contants.EMPLOYEE_LOGIN.key);
        secret = Tools.getStringValue(this,Contants.EMPLOYEE_LOGIN.secret);
		tv_head = (TextView) findViewById(R.id.tv_head);
		tv_czy_account = (TextView) findViewById(R.id.tv_czy_account);
		tv_change_bind = (TextView) findViewById(R.id.tv_change_bind);
		tv_ticket = (TextView) findViewById(R.id.tv_ticket);
		tv_receiver = (TextView) findViewById(R.id.tv_receiver);
		edtAmount= (EditText) findViewById(R.id.edit);
		edtMessage = (EditText) findViewById(R.id.edt_send_message);
		ll_submit = (LinearLayout) findViewById(R.id.ll_submit);
		ll_czy = (LinearLayout) findViewById(R.id.ll_czy);
		tv_change_bind.setOnClickListener(singleListener);
		ll_submit.setOnClickListener(singleListener);
		edtAmount.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 限制小数点后最多两位小数
				if (!Tools.point2(s.toString())) {
					String inputString = s.toString();
					if (Integer.toString(inputString.indexOf(".")) != null) {
						int pointIndex = inputString.indexOf(".");
						if (pointIndex != -2) {
							ToastFactory.showToast(RedpacketsTransferMainActivity.this, "小数点后最多两位数");
						}
					}
				}
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > 2) {
						s = s.toString().subSequence(0,
								s.toString().indexOf(".") + 3);
						edtAmount.setText(s);
						edtAmount.setSelection(s.length());
					}
				}
				if (s.toString().trim().substring(0).equals(".")) {
					s = "0" + s;
					edtAmount.setText(s);
					edtAmount.setSelection(2);
				}

				if (s.toString().startsWith("0")
						&& s.toString().trim().length() > 1) {
					if (!s.toString().substring(1, 2).equals(".")) {
						edtAmount.setText(s.subSequence(0, 1));
						edtAmount.setSelection(1);
						return;
					}
				}
				Double double2;
				if (s.toString().length() > 0) {
					double2 = Double.parseDouble(s.toString());
					if (double2 > 5000) {
						ToastFactory.showToast(RedpacketsTransferMainActivity.this, "输入金额不能超过5000");
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		tv_ticket.setText("可用余额："+balance);
		if("colleague".equals(transferTo)){
			headView.setTitle("给同事发饭票");
			tv_head.setText("正在转账给");
			tv_receiver.setVisibility(View.VISIBLE);
			ll_czy.setVisibility(View.GONE);
			tv_receiver.setText(receiverName+"("+receiverMobile+")");
			type = "0";
		}else{
			headView.setTitle("转到彩之云");
			tv_head.setText("转账到我的彩之云");
			ll_czy.setVisibility(View.VISIBLE);
			tv_receiver.setVisibility(View.GONE);
			tv_czy_account.setText(receiverMobile);
			type = "1";
		}
	}
	
	/**
	 * 删除绑定彩之云
	 */
	private void bindColourLifeDelete() {
		id = getIntent().getStringExtra("id");
		RequestConfig config  = new RequestConfig(this, HttpTools.DELETE_CAY_INFO,"删除彩之云账号");
		RequestParams params = new RequestParams();
		params.put("id",id);
		params.put("key", key);
		params.put("secret",secret);
		HttpTools.httpDelete(Contants.URl.URL_ICETEST,"/hongbao/bindColourLifeDelete/"+id, config, params);
	}
	
	private boolean check() {
		transferAmount = edtAmount.getEditableText().toString();

		if (transferAmount.length() > 0 && Tools.point2(transferAmount)) {
			Double double2 = Double.parseDouble(transferAmount);

			if (double2 > balance) {
				ToastFactory.showToast(RedpacketsTransferMainActivity.this, "超出红包余额");
				return false;
			} else if (double2 > 5000) {
				ToastFactory.showToast(RedpacketsTransferMainActivity.this, "输入金额不能超过5000");
				return false;
			} else if (balance >= double2 && double2 > 0) {
				return true;
			} else {
				ToastFactory.showToast(RedpacketsTransferMainActivity.this, "输入金额有误");
				return false;
			}
		} else {
			ToastFactory.showToast(RedpacketsTransferMainActivity.this, "输入金额有误");
			return false;
		}
	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		int code = HttpTools.getCode(jsonString);
		String message = HttpTools.getMessageString(jsonString);
		if(msg.arg1 == HttpTools.DELETE_CAY_INFO){//删除彩之云账号
			if(code == 0){
				JSONObject object = HttpTools.getContentJSONObject(jsonString);
				String status;
				try {
					status = object.getString("ok");
					if (status != null) {
						if ("1".equals(status)) {
							ToastFactory.showToast(RedpacketsTransferMainActivity.this, "已解除绑定");
							Intent intent = new Intent();
							intent.setClass(RedpacketsTransferMainActivity.this,RedpacketsBindCZYMainActivity.class);
							intent.putExtra(Contants.PARAMETER.BALANCE, balance);
							startActivity(intent);
							finish();
						}else{
							ToastFactory.showToast(RedpacketsTransferMainActivity.this, "解绑失败");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				}else{
					ToastFactory.showToast(RedpacketsTransferMainActivity.this, message);
				}
			}else{
				
			}
		}
	
	@Override
	public View getContentView() {
		return getLayoutInflater().inflate(R.layout.activity_redpackets_transfer_main, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return null;
	}

}
