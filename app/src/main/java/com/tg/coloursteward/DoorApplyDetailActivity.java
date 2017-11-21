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
/**
 * 申请
 * @author Administrator
 *
 */
public class DoorApplyDetailActivity extends BaseActivity {
	// 当前授权信息
    private AuthorizationListResp authorizationListResp;

    private TextView txt_detail;//申请详情或者授权详情
    private TextView txt_stutas;//状态
    private TextView txt_mobile;//电话号码
    private TextView txt_memo;//备注

    private Button btn_apply;//再次申请

	private String czyid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prepareView();
		getIntentData();
		prepareDate();
	}
	@Override
	protected boolean handClickEvent(View v) {
		switch (v.getId()) {
		case R.id.btn_apply://再次申请
			 if (authorizationListResp != null) {
                 aginApply();
             }
			break;
		}
		return super.handClickEvent(v);
	}
	 /**
     * 获取Intent传过来的数据
     */
    private void getIntentData() {
        Intent intent = getIntent();
        authorizationListResp = (AuthorizationListResp) intent.getSerializableExtra("applyListResp");

    }
	private void prepareView() {
    txt_detail = (TextView) findViewById(R.id.txt_detail);
    txt_stutas = (TextView) findViewById(R.id.txt_stutas);
    txt_mobile = (TextView) findViewById(R.id.txt_mobile);
    txt_memo = (TextView) findViewById(R.id.txt_memo);

    btn_apply = (Button) findViewById(R.id.btn_apply);
    btn_apply.setOnClickListener(singleListener);
	}
	  /**
     * 赋值
     */
    private void prepareDate() {
    	czyid = Tools.getCZYID(this);
        String type = authorizationListResp.getType();
        String isdelete = authorizationListResp.getIsdeleted();
        String statuString = "";
        if ("1".equals(type)) {
            // 默认 未批复
            if ("1".equals(isdelete)) {
                txt_stutas.setTextColor(getResources()
                        .getColor(R.color.black));
                statuString = "拒绝";
                btn_apply.setVisibility(View.VISIBLE);
            } else {
                // 未批复
                txt_stutas.setTextColor(getResources()
                        .getColor(R.color.lightgray));
                statuString = "未批复";
                btn_apply.setVisibility(View.GONE);
            }
        } else if ("2".equals(type)) {

            if ("1".equals(isdelete)) {
                txt_stutas.setTextColor(getResources()
                        .getColor(R.color.black));
                statuString = "已失效";
                btn_apply.setVisibility(View.VISIBLE);
            } else if ("2".equals(type)) {
                // 已批复
                String auType = authorizationListResp.getUsertype();
                // 绿色
                txt_stutas.setTextColor(getResources()
                        .getColor(R.color.lightgreen));
                btn_apply.setVisibility(View.GONE);
                statuString = "通过";

            }
        }
        txt_stutas.setText(statuString);
        txt_mobile.setText(authorizationListResp.getMobile());
        txt_memo.setText(authorizationListResp.getMemo());
    }
    private void aginApply() {
    	String cid = authorizationListResp.getCid();
    	if (cid == null || cid.length() == 0 || cid.equalsIgnoreCase("0")) {
        cid = authorizationListResp.getFromid();
    		}
    	String memo = authorizationListResp.getMemo();
    	RequestConfig config = new RequestConfig(this,HttpTools.POST_APPLY_MOBILE);
    	RequestParams params = new RequestParams();
    	params.put("customer_id", czyid);
    	params.put("account", cid);
    	params.put("memo", memo);
    	HttpTools.httpPost(Contants.URl.URL_ICETEST,"/newczy/wetown/ApplyApply4mobile", config, params); 
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
				ToastFactory.showToast(DoorApplyDetailActivity.this, message);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_door_apply_detail,null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "申请详情";
	}


}
