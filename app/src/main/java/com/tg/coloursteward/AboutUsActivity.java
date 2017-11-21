package com.tg.coloursteward;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.updateapk.UpdateManager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
/**
 * 关于app
 * 
 * @author Administrator
 * 
 */
public class AboutUsActivity extends BaseActivity {
	private TextView tvVersionShort;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tvVersionShort=(TextView) findViewById(R.id.tv_versionShort);
		/**
		 * 获取版本号
		 */
		String versionShort = UpdateManager.getVersionName(AboutUsActivity.this);
		tvVersionShort.setText("V "+versionShort);
	}

	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_about_us, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "关于彩管家";
	}

}
