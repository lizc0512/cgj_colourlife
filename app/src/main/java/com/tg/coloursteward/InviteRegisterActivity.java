package com.tg.coloursteward;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.updateapk.UpdateManager;
import com.tg.coloursteward.util.StringUtils;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * 邀请同事
 * @author Administrator
 *
 */
public class InviteRegisterActivity extends BaseActivity {
	private WebView mInvite_webview;
	private TextView tvVersion;
	// 邀请同事二维码
	private String url = Contants.URl.URL_ICETEST + "/caiguanjia/qrcode";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*mInvite_webview = (WebView) findViewById(R.id.invite_webview);
		mInvite_webview.loadUrl(url);*/
		tvVersion = (TextView) findViewById(R.id.tv_version);
		/**
		 * 获取版本号
		 */
		String versionShort = UpdateManager.getVersionName(InviteRegisterActivity.this);//本地
		SharedPreferences sharedPreferences= getSharedPreferences("versions",0);
		String versionShortService =sharedPreferences.getString("versionShort", "");//服务器
		if(StringUtils.isNotEmpty(versionShortService)){
			tvVersion.setText("Android: V"+versionShortService+"/IOS：V"+versionShortService);
		}else{
			tvVersion.setText("Android: V"+versionShort+"/IOS：V"+versionShort);
		}


	}

	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_invite_register, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "邀请同事";
	}


}
