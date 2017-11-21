package com.tg.coloursteward;

import org.json.JSONException;
import org.json.JSONObject;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
/**
 * 获取临时密码
 * @author Administrator
 *
 */
public class TemporaryPassWordActivity extends BaseActivity {
private TextView tvTemporaryPwd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RequestParams params = new RequestParams();
		params.put("username", UserInfo.employeeAccount);
		params.put("password", Tools.getPassWordMD5(getApplicationContext()));
		HttpTools.httpGet(Contants.URl.URL_ICETEST,"/account/temp", 
				new RequestConfig(this, HttpTools.GET_ACCOUNT_TEMP), params);
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		tvTemporaryPwd = (TextView) findViewById(R.id.tv_temporary_pwd);
	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		int code = HttpTools.getCode(jsonString);
		if(code == 0){
			JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
			if(jsonObject != null){
				try {
					String password = jsonObject.getString("password");
					if(password != null){
						tvTemporaryPwd.setText(password);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}else{
			String message = HttpTools.getMessageString(jsonString);
			ToastFactory.showToast(TemporaryPassWordActivity.this,message);
			tvTemporaryPwd.setText(message);
		}
	}
	@Override
	public View getContentView() {
		return getLayoutInflater().inflate(R.layout.activity_temporary_pass_word, null);
	}

	@Override
	public String getHeadTitle() {
		return "获取临时密码";
	}


}
