package com.tg.coloursteward;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.info.door.DoorInfoResp;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ScanResultDoorOpenActivity extends BaseActivity {
	private final static String TAG = "ScanResultDoorOpenActivity";
	private ImageView img_door;
	private AnimationDrawable _animaition;
	private String qrcode;
	private CountDownTimer countDownTimer;// 计时器
	private long time;
	private TextView tv_name;
	// 门信息
	private DoorInfoResp doorInfoResp;//
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prepareView();
	}
	private void prepareView() {
		img_door = (ImageView) findViewById(R.id.img_door);
		img_door.setBackgroundResource(R.drawable.open_door_ainm);
		_animaition = (AnimationDrawable) img_door.getBackground();
		tv_name = (TextView) findViewById(R.id.tv_name);

		_animaition.start();// 启动
		countDownTimer = new CountDownTimer(15 * 1000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				time = millisUntilFinished;
			}

			@Override
			public void onFinish() {
				ToastFactory.showToast(ScanResultDoorOpenActivity.this,"网络异常，请检查网络连接");

			}
		};

	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_scan_result_door_open,null);
	}
	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return null;
	}
}
