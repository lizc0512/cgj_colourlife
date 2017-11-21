package com.tg.coloursteward;

import com.tg.coloursteward.R.color;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
/**
 * 意见反馈
 * @author Administrator
 *
 */
public class OpinionActivity extends BaseActivity {
	private EditText editOpinion;
	private String opinion = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		editOpinion = (EditText)findViewById(R.id.edit_opinion);
	}
	@Override
	protected boolean handClickEvent(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.right_layout://提交
				opinion = editOpinion.getText().toString();
				if(opinion.length() == 0){
					ToastFactory.showToast(this, "请填写反馈内容");
					return false;
				}
				if(opinion.length() <5){
					ToastFactory.showToast(this, "你输入的内容少于5个字，请重新输入");
					return false;
				}
				if(opinion.length() > 100){
					ToastFactory.showToast(this, "反馈内容不能大于100字");
					return false;
				}
				submit();
				break;
		}
		
		return super.handClickEvent(v);
	}
	/**
	 * 提交
	 */
	private void submit() {
		RequestConfig config = new RequestConfig(this,HttpTools.SET_OPINION_INFO,"提交信息");
		RequestParams params = new RequestParams();
		params.put("founder",UserInfo.realname);
		params.put("content",editOpinion.getText().toString());
		HttpTools.httpPost(Contants.URl.URL_ICETEST,"/feedback" ,config, params);
	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		ToastFactory.showToast(this, "您的反馈已收到，感谢您的参与");
		finish();
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_opinion,null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		headView.setRightText("提交");
		headView.setListenerRight(singleListener);
		headView.setRightTextColor(getResources().getColor(color.white));
		return "意见反馈";
	}
}
