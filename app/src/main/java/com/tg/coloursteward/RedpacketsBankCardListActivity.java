package com.tg.coloursteward;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.tg.coloursteward.adapter.SwipeAdapter;
import com.tg.coloursteward.adapter.SwipeAdapter.IOnItemRightClickListener;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.BankCardInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.SwipeListView;
import com.tg.coloursteward.view.dialog.PwdDialog2;
import com.tg.coloursteward.view.dialog.PwdDialog2.ADialogCallback;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
/**
 * 选择银行卡
 * @author Administrator
 *
 */
public class RedpacketsBankCardListActivity extends BaseActivity {

	private SwipeListView cardListView;
	private ArrayList<BankCardInfo> bankCardList = new ArrayList<BankCardInfo>();
	private SwipeAdapter adapter;

	/**
	 * 添加银行卡
	 */
	private RelativeLayout addBankCard;

	/**
	 * 标记被操作的银行卡的位置
	 */
	private int bankCardPosition;

	private Double balance;


	private PwdDialog2 aDialog;
	private ADialogCallback aDialogCallback;

	/**
	 * 实际到账金额描述
	 */
	private String desc;

	private String rate;

	private String key;
	private String secret;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}

	@Override
	protected boolean handClickEvent(View v) {
		/**
		 * 提现明细
		 */
		startActivity(new Intent(RedpacketsBankCardListActivity.this,RedpacketsWithdrawRecordActivity.class));
		return super.handClickEvent(v);
	}
	private void initData() {
		cardListView = (SwipeListView) findViewById(R.id.lv_bank_card);
		addBankCard = (RelativeLayout) findViewById(R.id.rl_add_bank_card);
		addBankCard.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				aDialogCallback = new ADialogCallback() {
					@Override
					public void callback() {
						Intent intent = new Intent(RedpacketsBankCardListActivity.this,RedpacketsBindBankCardActivity.class);
						intent.putExtra(Contants.PARAMETER.BALANCE, balance);
						intent.putExtra("desc", desc);
						intent.putExtra("rate", rate);
						startActivity(intent);
						finish();
					}
				};
				aDialog = new PwdDialog2(RedpacketsBankCardListActivity.this,
						R.style.choice_dialog, "input", aDialogCallback);
				aDialog.show();
			}
		});
		balance = getIntent().getDoubleExtra(Contants.PARAMETER.BALANCE, 0.00);
		desc = getIntent().getStringExtra("desc");
		rate = getIntent().getStringExtra("rate");
		key = Tools.getStringValue(this,Contants.EMPLOYEE_LOGIN.key);
        secret = Tools.getStringValue(this,Contants.EMPLOYEE_LOGIN.secret);
		// 获取绑定银行卡
		bindBankList();
	}
	/**
	 * 获取绑定银行卡
	 */
	private void bindBankList() {
		RequestConfig config = new RequestConfig(this, HttpTools.GET_BANK_LIST,"获取银行卡列表");
		RequestParams params = new RequestParams();
		params.put("key", key);
		params.put("secret", secret);
		params.put("page", 1);
		params.put("pagesize", Integer.MAX_VALUE);
		HttpTools.httpGet(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/bindBankList", config, params);
	}
	/**
	 * 删除绑定银行卡
	 */
	private void bankDelete(String bankCardId) {
		RequestConfig config = new RequestConfig(this, HttpTools.DETELE_BANK_ITEM,"获取银行卡列表");
		RequestParams params = new RequestParams();
		params.put("key", key);
		params.put("secret", secret);
		params.put("id",bankCardId);
		HttpTools.httpDelete(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/bankDelete/"+bankCardId, config, params);
	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		int code = HttpTools.getCode(jsonString);
		String message = HttpTools.getMessageString(jsonString);
		if(msg.arg1 == HttpTools.GET_BANK_LIST){
		if(code == 0){
			String content = HttpTools.getContentString(jsonString);
			if(content.length() > 0){
				ResponseData data = HttpTools.getResponseKey(content, "bankCardList");
				if(data.length > 0 ){
					cardListView.setVisibility(View.VISIBLE);
					BankCardInfo info;
					for (int i = 0; i < data.length; i++) {
						info = new BankCardInfo();
						info.bankCardId = data.getInt(i, "id");
						info.bankCode = data.getInt(i, "bank_id");
						info.bankName = data.getString(i, "bankName");
						info.cardNo = data.getString(i, "card_num");
						info.userName = data.getString(i, "card_holder");
						bankCardList.add(info);
					}
					if (adapter == null) {
						adapter = new SwipeAdapter(this, cardListView.getRightViewWidth(),new IOnItemRightClickListener() {
							
							@Override
							public void onRightClick(View v, int position) {
								bankCardPosition = position;
								final String bankCardId = String.valueOf(bankCardList.get(bankCardPosition).bankCardId);
								aDialogCallback = new ADialogCallback() {
									@Override
									public void callback() {
										// 删除绑定银行卡
										bankDelete(bankCardId);
									}
								};
								aDialog = new PwdDialog2(
										RedpacketsBankCardListActivity.this,
										R.style.choice_dialog,
										"input",
										aDialogCallback);
								aDialog.show();
							}
						}, bankCardList);
						cardListView.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,View view, int position,long id) {
								BankCardInfo bankCard = bankCardList.get(position);
								if (bankCard != null) {
									String cardNo = bankCard.cardNo;
									String bankName = bankCard.bankName;
									String cardholder = bankCard.userName;
									int bankId = bankCard.bankCode;
									String bankCardId = String.valueOf(bankCard.bankCardId);
									Intent intent = new Intent(RedpacketsBankCardListActivity.this,RedpacketsWithdrawMainActivity.class);
									intent.putExtra(Contants.PARAMETER.BANK_CARD_ID,bankCardId);
									intent.putExtra(Contants.PARAMETER.CARD_NUMBER,cardNo);
									intent.putExtra(Contants.PARAMETER.BANK_NAME,bankName);
									intent.putExtra(Contants.PARAMETER.BALANCE,balance);
									intent.putExtra(Contants.PARAMETER.CARDHOLDER,cardholder);
									intent.putExtra(Contants.PARAMETER.BANK_ID,String.valueOf(bankId));
									intent.putExtra("desc",desc);
									intent.putExtra("rate",rate);
									startActivity(intent);
									finish();
								}
							}
						});
					}
					cardListView.setAdapter(adapter);
				}else{
					cardListView.setVisibility(View.GONE);
				}
			}
		}else{
			ToastFactory.showToast(RedpacketsBankCardListActivity.this,message);
		}
		}else if(msg.arg1 == HttpTools.DETELE_BANK_ITEM){
			if(code == 0){
				JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
				String status;
				try {
					status = jsonObject.getString("ok");
					if (status != null) {
						if ("1".equals(status)) {
							bankCardList.remove(bankCardPosition);
							adapter.notifyDataSetChanged();
							cardListView.setAdapter(adapter);
						}else{
							ToastFactory.showToast(RedpacketsBankCardListActivity.this,message);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}else{
				ToastFactory.showToast(RedpacketsBankCardListActivity.this,message);
			}
		}
	}
	
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_redpackets_bank_card_list, null);
	}

	@Override
	public String getHeadTitle() {
		headView.setRightText("提现明细");
		headView.setRightTextColor(getResources().getColor(R.color.white));
		headView.setListenerRight(singleListener);
		return "选择银行卡";
	}


}
