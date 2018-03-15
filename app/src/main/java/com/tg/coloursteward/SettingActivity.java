package com.tg.coloursteward;


import java.util.ArrayList;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tg.coloursteward.application.CityPropertyApplication;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.database.SharedPreferencesTools;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.object.ViewConfig;
import com.tg.coloursteward.updateapk.ApkInfo;
import com.tg.coloursteward.updateapk.UpdateManager;
import com.tg.coloursteward.view.MessageArrowView;
import com.tg.coloursteward.view.MessageArrowView.ItemClickListener;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.coloursteward.view.dialog.ToastFactory;
//import com.youmai.hxsdk.HuxinSdkManager;

import android.*;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 更多设置
 * @author Administrator
 *
 */
public class SettingActivity extends BaseActivity implements ItemClickListener {
	private MessageArrowView mineInfoZone;
	private LinearLayout llExit;
	private RelativeLayout rlUpApk;
	private TextView tvVersion;
	private  final int REQUESTPERMISSION = 110;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}
	/**
	 * 初始化控件
	 */
	private void initView() {
		mineInfoZone=(MessageArrowView) findViewById(R.id.mine_info_zone);
		llExit = (LinearLayout) findViewById(R.id.exit);
		rlUpApk = (RelativeLayout) findViewById(R.id.rl_upApk);
		tvVersion = (TextView) findViewById(R.id.tv_version);
		/**
		 * 获取版本号
		 */
		String versionShort = UpdateManager.getVersionName(SettingActivity.this);
		tvVersion.setText("V "+versionShort);
		rlUpApk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*
				 * 版本检测更新
				 */
				getVersion();
			}
		});
		llExit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {// 退出登录
				DialogFactory.getInstance().showDialog(SettingActivity.this, new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						SharedPreferencesTools.clearUserId(SettingActivity.this);
						//清空缓存
						SharedPreferencesTools.clearCache(SettingActivity.this);
						CityPropertyApplication.gotoLoginActivity(SettingActivity.this);
					}
				}, null, "确定要退出账号吗", null, null);
				
			}
		});
		mineInfoZone.setItemClickListener(this);
		ArrayList<ViewConfig> list = new ArrayList<ViewConfig>();
		ViewConfig viewConfig = new ViewConfig("关于彩管家", "", true);
		list.add(viewConfig);
		viewConfig = new ViewConfig("修改密码", "", true);
		list.add(viewConfig);
		viewConfig = new ViewConfig("清空首页消息列表", "", true);
		list.add(viewConfig);
		viewConfig = new ViewConfig("通话设置", "", true);
		list.add(viewConfig);
		mineInfoZone.setData(list);
	}

	// 检测版本更新
	private void getVersion() {
		RequestConfig config = new RequestConfig(this, HttpTools.GET_VERSION_INFO,"检测更新");
		RequestParams params = new RequestParams();
		String version = UpdateManager.getVersionName(SettingActivity.this);
		params.put("version",version);
		params.put("type", "android");
		HttpTools.httpGet(Contants.URl.URL_ICETEST,"/czywg/version", config, params);
	}
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		int code = HttpTools.getCode(jsonString);
		String message = HttpTools.getMessageString(jsonString);
		if(msg.arg1 == HttpTools.DETELE_HOME_LIST){
			if(code == 0){
				ToastFactory.showToast(this,message);
				Message msghome = new Message();
				msghome.what = Contants.LOGO.CLEAR_HOMELIST;
				EventBus.getDefault().post(msghome);
			}
		}else if(msg.arg1 == HttpTools.GET_VERSION_INFO){
			JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
			String content = HttpTools.getContentString(jsonString);
			if(code == 0){
				try {
					int apkCode = jsonObject.getInt("result");
					ResponseData data = HttpTools.getResponseKey(content,"info");
					JSONArray func = data.getJSONArray(0,"func");
					String apkVersion = data.getString(0,"version");
					String apkSize = data.getString(0,"size");
					String downloadUrl = data.getString(0,"download_url");
					String apkLog = "";
					if(func != null){
						for (int i = 0 ; i < func.length() ; i++){
							apkLog += func.get(i)+"\n";
						}
					}
					ApkInfo apkinfo = new ApkInfo(downloadUrl, apkVersion,apkSize, apkCode, "", apkLog);
					if (apkinfo != null) {
						SharedPreferences mySharedPreferences= getSharedPreferences("versions",0);
						SharedPreferences.Editor editor = mySharedPreferences.edit();
						editor.putString("versionShort", apkVersion);
						editor.commit();
						UpdateManager manager = new UpdateManager(SettingActivity.this,false);
						if(ContextCompat.checkSelfPermission(SettingActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
							//申请权限
							ActivityCompat.requestPermissions(SettingActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTPERMISSION);
							ToastFactory.showToast(SettingActivity.this, "请允许权限进行下载安装");
						}else{
							manager.checkUpdate(apkinfo);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}else{

			}
		}

		
	}
	@Override
	public void onItemClick(MessageArrowView mv, View v, int position) {
		// TODO Auto-generated method stub
		if (mv == mineInfoZone) {
			if (position == 0) {// 关于app
				startActivity(new Intent(this,AboutUsActivity.class));
				//showShare();
			}else if(position == 1){// 修改密码
				startActivity(new Intent(this,ModifiedPasswordActivity.class));
			}else if(position == 2){// 清空首页消息列表
				DialogFactory.getInstance().showDialog(SettingActivity.this, new OnClickListener() {
					@Override
					public void onClick(View v) {
						RequestConfig config = new RequestConfig(SettingActivity.this, HttpTools.DETELE_HOME_LIST,"清空首页消息列表");
						RequestParams params = new RequestParams();
						params.put("username", UserInfo.employeeAccount);
						params.put("source", 1);
						HttpTools.httpDelete(Contants.URl.URL_ICETEST,"/push2/homepush/deleteall", config, params);
					}
				}, null, "是否确定清空首页消息列表？", null, null);
			}else if(position == 3){
				//HuxinSdkManager.instance().setCallSetting();
			}
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case REQUESTPERMISSION:
				if (grantResults.length > 0&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

				} else {

				}
				break;
		}
	}

	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_setting, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "更多设置";
	}
	private void showShare() {
		final String url = "http://a.app.qq.com/o/simple.jsp?pkgname=com.park.wisdom#opened";
		OnekeyShare oks = new OnekeyShare();
		//关闭sso授权
		oks.disableSSOWhenAuthorize();
		// title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
		oks.setTitle("标题");
		// titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("我是分享文本");
		//分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
		// oks.setImageUrl("22");
		oks.setImageUrl("http://pic6.nipic.com/20091207/3337900_161732052452_2.jpg");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		//oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite("ShareSDK");
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");

		// 启动分享GUI
		oks.show(this);
	}
}
