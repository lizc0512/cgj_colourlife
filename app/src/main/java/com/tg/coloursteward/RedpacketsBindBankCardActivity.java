package com.tg.coloursteward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.inter.NetworkRequestListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.object.SlideItemObj;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.NetWorkListDialog;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.tg.coloursteward.view.spinnerwheel.WheelVerticalView;
import com.tg.coloursteward.view.spinnerwheel.SlideSelectorView.OnCompleteListener;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 绑定银行卡
 * @author Administrator
 *
 */
public class RedpacketsBindBankCardActivity extends BaseActivity {
	/**
	 * 选择银行
	 */
	private RelativeLayout rl_bank;

	private EditText et_cardholder;

	private EditText et_card_number;

	private TextView tv_bank;

	private RelativeLayout btn_add_bank_card;
	
	private EditText et_id_number;
	String[] aCity = new String[]{ "11", "12", "13", "14", "15", "21", "22", "23", 
			"31", "32", "33", "34", "35", "36", "37", "41", "42", 
			"43", "44", "45", "46", "50", "51","52", "53", "54",
			"61", "62", "63", "64", "65", "71", "81", "82"};
	//将身份证号码前面的17位数分别乘以不同的系数。从第一位到第十七位的系数分别为：7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2  
	int[] intArr = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
	// 余数只可能有0 1 2 3 4 5 6 7 8 9 10这11个数字。其分别对应的最后一位身份证的号码为1 0 X 9 8 7 6 5 4 3 2。 
	int[] intArr2 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };  
	int[] intArr3 = { 1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2 }; 

	/**
	 * 银行id
	 */
	private String bankId;

	/**
	 * 银行名称
	 */
	private String bankName;


	private Double balance;


	private String rate;
	
	private NetWorkListDialog typeDialog;
	private String key;
	private String secret;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		key = Tools.getStringValue(this,Contants.EMPLOYEE_LOGIN.key);
        secret = Tools.getStringValue(this,Contants.EMPLOYEE_LOGIN.secret);
		et_cardholder = (EditText) findViewById(R.id.et_cardholder);
		et_card_number = (EditText) findViewById(R.id.et_card_number);
		et_id_number = (EditText) findViewById(R.id.et_id_number);
		tv_bank = (TextView) findViewById(R.id.tv_bank);
		rl_bank = (RelativeLayout) findViewById(R.id.rl_bank);
		btn_add_bank_card = (RelativeLayout) findViewById(R.id.btn_add_bank_card);
		rl_bank.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showtypeDialog();
			}
		});
		btn_add_bank_card.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (check()) {
					// 员工绑定银行卡
					bindBank();
				}
			}
		});
		// 银行卡号每隔四位插入一个空格
			et_card_number.addTextChangedListener(new TextWatcher() {
					int beforeTextLength = 0;
					int onTextLength = 0;
					boolean isChanged = false;

					int location = 0;// 记录光标的位置
					private char[] tempChar;
					private StringBuffer buffer = new StringBuffer();
					int konggeNumberB = 0;

					@Override
					public void onTextChanged(CharSequence s, int start, int before,
							int count) {
						onTextLength = s.length();
						buffer.append(s.toString());
						if (onTextLength == beforeTextLength || onTextLength <= 3
								|| isChanged) {
							isChanged = false;
							return;
						}
						isChanged = true;
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
						beforeTextLength = s.length();
						if (buffer.length() > 0) {
							buffer.delete(0, buffer.length());
						}
						konggeNumberB = 0;
						for (int i = 0; i < s.length(); i++) {
							if (s.charAt(i) == ' ') {
								konggeNumberB++;
							}
						}
					}

					@Override
					public void afterTextChanged(Editable s) {
						if (isChanged) {
							location = et_card_number.getSelectionEnd();
							int index = 0;
							while (index < buffer.length()) {
								if (buffer.charAt(index) == ' ') {
									buffer.deleteCharAt(index);
								} else {
									index++;
								}
							}

							index = 0;
							int konggeNumberC = 0;
							while (index < buffer.length()) {
								if ((index == 4 || index == 9 || index == 14 || index == 19)) {
									buffer.insert(index, ' ');
									konggeNumberC++;
								}
								index++;
							}

							if (konggeNumberC > konggeNumberB) {
								location += (konggeNumberC - konggeNumberB);
							}

							tempChar = new char[buffer.length()];
							buffer.getChars(0, buffer.length(), tempChar, 0);
							String str = buffer.toString();
							if (location > str.length()) {
								location = str.length();
							} else if (location < 0) {
								location = 0;
							}

							et_card_number.setText(str);
							Editable etable = et_card_number.getText();
							Selection.setSelection(etable, location);
							isChanged = false;
						}
					}
				});
			
	}
	/**
	 * 选择类型
	 */
	private void showtypeDialog() {
		if (typeDialog == null) {
			typeDialog = new NetWorkListDialog(this);
			typeDialog.setNetworkListener(new NetworkRequestListener() {
				@Override
				public void onSuccess(WheelVerticalView wheelView, Message msg,
						String response) {
					int code = HttpTools.getCode(response);
					if(code == 0){
						JSONArray jsonString = HttpTools.getContentJsonArray(response);
						if (jsonString != null) {
							ResponseData data = HttpTools.getResponseContent(jsonString);
							ArrayList<SlideItemObj> list = wheelView.getList();
							list.clear();
							for (int i = 0; i < data.length; i++) {
								list.add(new SlideItemObj(
										data.getString(i, "name"), 
										data.getString(i, "id")));
							}
							typeDialog.notifyDataInvalidated();
						}
					}
				}

				@Override
				public void onRequest(MessageHandler msgHand) {
					// TODO Auto-generated method stub
					RequestConfig config = new RequestConfig(RedpacketsBindBankCardActivity.this, 0);
					config.handler = msgHand.getHandler();
					RequestParams params = new RequestParams();
					params.put("key", key);
					params.put("secret", secret);
					HttpTools.httpGet(Contants.URl.URL_ICETEST, "/hongbao/bankList", config, params);
				}

				@Override
				public void onFail(Message msg, String message) {
					// TODO Auto-generated method stub

				}
			});
			typeDialog.setOnCompleteClickListener(new OnCompleteListener() {

				@Override
				public void onComplete(SlideItemObj item1, SlideItemObj item2) {
					// TODO Auto-generated method stub
					if (item1 != null) {
						tv_bank.setText(item1.name);
						bankId = item1.id;
					}
				}
			});
		}
		typeDialog.show("银行列表", false);

	}
	/**
	 * 员工绑定银行卡
	 */
	private void bindBank() {
		String cardholder = et_cardholder.getEditableText().toString();
		String cardNum = et_card_number.getEditableText().toString();
		String identity_Id = et_id_number.getEditableText().toString();
		
		RequestConfig config = new RequestConfig(this, HttpTools.POST_BAND_BANK,"获取银行卡列表");
		RequestParams params = new RequestParams();
		params.put("card_holder", cardholder);
		params.put("bank_id", bankId);
		params.put("card_num", cardNum);
		params.put("ide_num", identity_Id);
		params.put("key", key);
		params.put("secret", secret);
		HttpTools.httpPost(Contants.URl.URL_ICETEST, "/hongbao/bindBank", config, params);
	}
	private boolean check() {
		if (TextUtils.isEmpty(et_cardholder.getEditableText().toString())) {
			ToastFactory.showToast(this, "请输入持卡人姓名");
			return false;
		}
		if (TextUtils.isEmpty(tv_bank.getText())) {
			ToastFactory.showToast(this, "请选择银行卡发卡行");
			return false;
		}
		if (TextUtils.isEmpty(et_card_number.getEditableText().toString())) {
			ToastFactory.showToast(this, "请输入银行卡卡号");
			return false;
		} 
		if (TextUtils.isEmpty(et_id_number.getEditableText().toString())) {
			ToastFactory.showToast(this, "请输入身份证号");
			return false;
		} 
		else if ((et_card_number.getEditableText().length() != 19 && et_card_number
				.getEditableText().length() != 23)) {
			ToastFactory.showToast(this, "卡号不正确");
			return false;
		}
		if (!checkid(et_id_number.getEditableText().toString())) {
			ToastFactory.showToast(this, "身份证号格式不正确");
			return false;
		}
		if(et_id_number.getEditableText().toString().length() == 18)
		{
			if (!checkLastone(et_id_number.getEditableText().toString())) {
				ToastFactory.showToast(this, "身份证号格式不正确");
				return false;
			}
		}
		return true;
	}
	private boolean checkid(String id) {
		return checkID(id);
	}
	
	public boolean checkID(String identityno) {
		String checkID = "";
		if(identityno.length() == 15)
		{
			checkID="^(\\d{6})([3-9][0-9][01][0-9][0-3])(\\d{4})$";
		}
		else if(identityno.length() == 18)
		{
			checkID="^(\\d{6})([12][09][3-9][0-9][01][0-9][0-3])(\\d{4})(\\d|[xX])$";
			
		}
		Pattern pattern = Pattern.compile(checkID);
		Matcher matcher = pattern.matcher(identityno);
	
		return matcher.matches();
	}
	private boolean checkLastone(String id) {
		id=id.toUpperCase();
		int sum = 0;  
        for (int i = 0; i < intArr.length; i++) {  
            sum += Character.digit(id.charAt(i), 10) * intArr[i];  
        }  
        int mod = sum % 11;  
        String matchDigit = "";  
        for (int i = 0; i < intArr2.length; i++) {  
            int j = intArr2[i];  
            if (j == mod) {  
                matchDigit = String.valueOf(intArr3[i]);  
                if (intArr3[i] > 57) {  
                    matchDigit = String.valueOf((char) intArr3[i]);  
                }  
            }  
        }  
        return matchDigit.equals(id.substring(id.length() - 1));
	}
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		// TODO Auto-generated method stub
		super.onSuccess(msg, jsonString, hintString);
		int code = HttpTools.getCode(jsonString);
		String message = HttpTools.getMessageString(jsonString);
		if(code == 0){
			JSONObject object = HttpTools.getContentJSONObject(jsonString);
			String state;
			try {
				state = object.getString("Status");
				String bankCardId = object.getString("id");
				if ("ok".equals(state)) {
				String rate = object.getString("rate");
				String desc = getIntent().getStringExtra("desc");
				String cardholder = et_cardholder.getEditableText().toString();
				String bankName = tv_bank.getText().toString();
				String cardNum = et_card_number.getEditableText().toString();
				Intent intent = new Intent(RedpacketsBindBankCardActivity.this,RedpacketsWithdrawMainActivity.class);
				intent.putExtra(Contants.PARAMETER.BANK_CARD_ID,bankCardId);
				intent.putExtra(Contants.PARAMETER.BANK_ID, bankId);
				intent.putExtra(Contants.PARAMETER.BALANCE, balance);
				intent.putExtra(Contants.PARAMETER.CARDHOLDER,cardholder);
				intent.putExtra(Contants.PARAMETER.BANK_NAME, bankName);
				intent.putExtra(Contants.PARAMETER.CARD_NUMBER,cardNum);
				intent.putExtra("desc", desc);
				intent.putExtra("rate", rate);
				startActivity(intent);
				finish();
				}else{
					ToastFactory.showToast(RedpacketsBindBankCardActivity.this,message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			ToastFactory.showToast(RedpacketsBindBankCardActivity.this,message);
		}
	}
	@Override
	public View getContentView() {
		return getLayoutInflater().inflate(R.layout.activity_redpackets_bind_bank_card, null);
	}

	@Override
	public String getHeadTitle() {
		return "绑定银行卡";
	}


}
