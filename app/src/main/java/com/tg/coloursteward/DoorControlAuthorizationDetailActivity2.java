package com.tg.coloursteward;

import org.json.JSONException;
import org.json.JSONObject;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.AuthorizationListResp;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DoorControlAuthorizationDetailActivity2 extends BaseActivity {
	  // 跳转到再次授权
    private final static int INTENT_TO_ACTION_AUTHORIZATION_APPROVE_PASS = 1;
    private TextView txt_stutas;//状态
    private TextView txt_autor_time;//授权时间
    private TextView txt_apply_time;//申请时间
    private TextView txt_memo;//备注
    private Button btn_autor;//取消或者再次授权
    private AuthorizationListResp authorizationListResp;

	private String czyid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prepareView();
		getIntentData();
		prepareData();
	}
	@Override
	protected boolean handClickEvent(View v) {
		switch (v.getId()) {
        case R.id.btn_autor:
            if (authorizationListResp != null) {

                if ("取消授权".equals(btn_autor.getText())) {
                    cancelAutor();
                } else if ("再次授权".equals(btn_autor.getText())) {
                    Intent intent = new Intent(
                            DoorControlAuthorizationDetailActivity2.this,
                            DoorAuthorizationApproveActivity.class);
                    intent.putExtra("authorListResp",
                            authorizationListResp);
                    intent.putExtra("refuse", false);
                    startActivityForResult(intent,
                            INTENT_TO_ACTION_AUTHORIZATION_APPROVE_PASS);
                }
            }
            break;
    }
		return super.handClickEvent(v);
	}
	 private void prepareView() {

     txt_stutas = (TextView) findViewById(R.id.txt_stutas);
     txt_autor_time = (TextView) findViewById(R.id.txt_autor_time);
     txt_apply_time = (TextView) findViewById(R.id.txt_apply_time);
     txt_memo = (TextView) findViewById(R.id.txt_memo);

     btn_autor = (Button) findViewById(R.id.btn_autor);
     btn_autor.setOnClickListener(singleListener);
 }

	/**
	 * 获取Intent传过来的数据
	 */
	private void getIntentData() {

		Intent intent = getIntent();
		authorizationListResp = (AuthorizationListResp) intent
				.getSerializableExtra("authorizationListResp");
	}

	private void prepareData() {
		czyid = Tools.getCZYID(this);
		if (authorizationListResp != null) {
         // 小区
         if ("0".equals(authorizationListResp.getIsdeleted())) {
             txt_stutas.setTextColor(getResources()
                     .getColor(R.color.lightgreen));
             txt_stutas.setText("通过");
             btn_autor.setText("取消授权");
         } else if ("1".equals(authorizationListResp.getIsdeleted())) {
             txt_stutas.setText("已失效");
             txt_stutas.setTextColor(getResources()
                     .getColor(R.color.black));
             btn_autor.setText("再次授权");
         }

         // 授权类型
         String type = authorizationListResp.getUsertype();
         if ("1".equals(type)) {
             txt_autor_time.setText("永久");
         } else if ("2".equals(type)) {
             txt_autor_time.setText("7天");
         } else if ("3".equals(type)) {
             txt_autor_time.setText("1天");
         } else if ("4".equals(type)) {
             txt_autor_time.setText("2小时");
         } else if ("5".equals(type)) {
             txt_autor_time.setText("1年");
         }

         // 授权时间
         txt_apply_time.setText(DoorAuthorizationActivity
                 .phpToTimeString(authorizationListResp.getCreationtime()));
         txt_memo.setText(authorizationListResp.getMemo());
     }
 }
	//取消授权
    private void cancelAutor() {
    	String aid = authorizationListResp.getId();
    	RequestConfig config = new RequestConfig(this,HttpTools.POST_AUTHOR_INFO);
 		RequestParams params = new RequestParams();
 		 params.put("customer_id", czyid);
         params.put("aid", aid);
 		HttpTools.httpPost(Contants.URl.URL_ICETEST,"/newczy/wetown/AuthorizationUnAuthorize", config, params);
    }
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		String message = HttpTools.getMessageString(jsonString);
		try {
			JSONObject response = new JSONObject(jsonString);
			String result = response.get("result").toString();
			String reason = response.get("reason").toString();
			if ("0".equals(result)) {
				 setResult(RESULT_OK);
	             finish();
			}else {
				ToastFactory.showToast(DoorControlAuthorizationDetailActivity2.this, message);
			}
			} catch (JSONException e) {
				e.printStackTrace();
			}
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_door_control_authorization_detail_activity2, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "授权详情";
	}
}
