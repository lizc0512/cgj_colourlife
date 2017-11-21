package com.tg.coloursteward;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.LocationSource;
import com.githang.statusbar.StatusBarCompat;
import com.tg.coloursteward.application.CityPropertyApplication;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.fragment.FragmentCentre;
import com.tg.coloursteward.fragment.FragmentCommunicate;
import com.tg.coloursteward.fragment.FragmentDeskTop;
import com.tg.coloursteward.fragment.FragmentHome;
import com.tg.coloursteward.fragment.FragmentManagement;
import com.tg.coloursteward.fragment.FragmentMine;
import com.tg.coloursteward.info.AdvInfo;
import com.tg.coloursteward.info.HomeDeskTopInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.log.Logger;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.net.MessageHandler.ResponseListener;
import com.tg.coloursteward.serice.AppAuthService;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.serice.MessageService;
import com.tg.coloursteward.serice.killSelfService;
import com.tg.coloursteward.updateapk.ApkInfo;
import com.tg.coloursteward.updateapk.UpdateManager;
import com.tg.coloursteward.util.AuthTimeUtils;
import com.tg.coloursteward.util.ExampleUtil;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.util.Utils;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.HuxinSdkManager;

import android.Manifest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

public class MainActivity extends FragmentActivity implements ResponseListener, OnTabChangeListener, AMapLocationListener,LocationSource {
	private static final String TAG = "JPush";
	public static final String ACTION_FRESH_USERINFO = "com.tg.coloursteward.ACTION_FRESH_USERINFO";
	public static final String ACTION_TICKET_INFO = "com.tg.coloursteward.ACTION_TICKET_INFO";
	public static final String ACTION_HOME_DELETEINFO = "com.tg.coloursteward.ACTION_HOME_DELETEINFO";
	public static final String ACTION_ACCOUNT_INFO = "com.tg.coloursteward.ACTION_ACCOUNT_INFO";
	public static final String ACTION_READ_MESSAGEINFO = "com.tg.coloursteward.ACTION_READ_MESSAGEINFO";
	public static final String ACTION_UPDATE_PUSHINFO = "com.tg.coloursteward.ACTION_UPDATE_PUSHINFO";
	public static final String KEY_NEDD_FRESH = "need_fresh";
	public static final String KEY_SKIN_CODE = "skin_code";
	public static final String KEY_EXTRAS = "extras";
	private TabHost mTabHost;
	private boolean exit = false;//是否退出
	private boolean needGetUserInfo = true;
	private Fragment fragments[] = { new FragmentDeskTop(),new FragmentCommunicate(), new FragmentManagement(), new FragmentMine() };
	private Fragment fragmentsCityProperty[] = { new FragmentHome(),new FragmentCommunicate(), new FragmentManagement(), new FragmentMine() };
	private Fragment fragmentsCentreLive[] = { new FragmentCentre(),new FragmentCommunicate(), new FragmentManagement(), new FragmentMine() };
	private String tabTexts[] = { "首页", "联系人", "工作", "我的" };
	private MessageHandler msgHand;
	private  final int REQUESTPERMISSION = 110;
	private ImageView iv_home_unreadnum;
	private HomeService homeService;
	private AuthAppService authAppService;//2.0授权
	private AppAuthService appAuthService;//1.0授权
	public AMapLocationClient mlocationClient;
	private AuthTimeUtils mAuthTimeUtils;
	private Intent data;
	private String extras;
	private ArrayList<AdvInfo> listadv = new ArrayList<AdvInfo>();
	private LocationSource.OnLocationChangedListener mListener;
	private int skin_code = 101 ;//  101 彩生活  100 通用  102 中住
	private BroadcastReceiver freshReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(ACTION_FRESH_USERINFO)) {
				FragmentManager frgManager = getSupportFragmentManager();
				Fragment fragment = frgManager.findFragmentByTag(tabTexts[3]);
				if (fragment != null) {
					FragmentMine mineFrag = (FragmentMine) fragment;
					mineFrag.freshUI();
				}
				if(skin_code == 100){//通用版
					if (fragment != null) {
						fragment = frgManager.findFragmentByTag(tabTexts[0]);
						FragmentHome homeFrag = (FragmentHome) fragment;
						homeFrag.freshUI();
					}
				}else if(skin_code == 102){//中住
					if (fragment != null) {
						fragment = frgManager.findFragmentByTag(tabTexts[0]);
						FragmentCentre homeFrag = (FragmentCentre) fragment;
						homeFrag.freshUI();
					}
				}
			}else if(action.equals(ACTION_HOME_DELETEINFO)){//首页删除消息
				HomeDeskTopInfo info = (HomeDeskTopInfo) intent.getSerializableExtra("info");
				String source_id = info.source_id;
				String comefrom = info.comefrom;
				postDeleteListItem(source_id, comefrom);
			}else if(action.equals(ACTION_ACCOUNT_INFO)){//刷新即时分成金额
				getAccountInfo();
			}else if(action.equals(ACTION_TICKET_INFO)){//饭票刷新
				if(skin_code == 101){//彩生活
					FragmentManager frgManager = getSupportFragmentManager();
					Fragment fragment = frgManager.findFragmentByTag(tabTexts[0]);
					if (fragment != null) {
						FragmentDeskTop homeFrag = (FragmentDeskTop) fragment;
						homeFrag.freshUI();
					}
				}
			}else if(action.equals(ACTION_READ_MESSAGEINFO)){//首页列表消息设置为已读
				String client_code  =  intent.getStringExtra("messageId");
				getReadMessageInfo(client_code);
			}else if(action.equals(ACTION_UPDATE_PUSHINFO)){//更新推送首页消息列表
				getUpdatePushInfo();
			}
		}
	};

	private Runnable getUserInfoRunnable = new Runnable() {
		public void run() {
			getUserInfo();
		}
	};
	private Runnable getAdRunnable = new Runnable() {
		public void run() {
			getAdInfo();
		}
	};
	private Handler hand = new Handler() {
		public void handleMessage(Message msg) {

		}
	};
	
	public Handler getHandler() {
		return msgHand.getHandler();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * 开启服务
		 */
		//startService(new Intent(MainActivity.this, MessageService.class));
		/**
		 * 设置极光推送别名与标签
		 */
		Boolean tags = Tools.getBooleanValue(MainActivity.this,Contants.storage.Tags);
		Boolean alias = Tools.getBooleanValue(MainActivity.this,Contants.storage.ALIAS);
		Log.d(TAG,"tags="+tags);
		Log.d(TAG,"alias="+alias);
		if(tags == false){
			setTag();
		}
		if(alias == false){
			setAlias();

		}
		CityPropertyApplication.addActivity(this);
		msgHand = new MessageHandler(this);
		msgHand.setResponseListener(this);
		setContentView(R.layout.activity_main);
		StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.home_fill), false);
		Tools.setMainStatus(MainActivity.this,true);
		huxinInit();
		windowPermission();
		data = getIntent();
		if (data != null) {
			needGetUserInfo = data.getBooleanExtra(KEY_NEDD_FRESH, true);
			skin_code = data.getIntExtra(KEY_SKIN_CODE, -1);
			extras  = data.getStringExtra(KEY_EXTRAS);
		}
		getAdInfo();
		getTokenInfo();
		getEmployeeInfo();
		getAuthAppInfo();//2.0授权
		getAppAuthInfo();//1.0授权
		/**
		 * 版本检测更新
		 */
		getVersion();
		initView();
		if (needGetUserInfo) {
			hand.postDelayed(getUserInfoRunnable, 3000);
		}
		if(!needGetUserInfo){
			OAtoCZY();
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_FRESH_USERINFO);
		filter.addAction(ACTION_TICKET_INFO);
		filter.addAction(ACTION_HOME_DELETEINFO);
		filter.addAction(ACTION_ACCOUNT_INFO);
		filter.addAction(ACTION_READ_MESSAGEINFO);
		filter.addAction(ACTION_UPDATE_PUSHINFO);
		registerReceiver(freshReceiver, filter);
		//推送跳转详情
		pushDetail();
	}
	private void pushDetail() {
		if (extras != null) {
			try {
				JSONObject jsonObject = new JSONObject(extras);
				String client_code = jsonObject.getString("client_code");
				String msgid = jsonObject.getString("msgid");
				String auth_type = jsonObject.getString("auth_type");
				String msgtype = jsonObject.getString("msgtype");
				String url = jsonObject.getString("url");
				mAuthTimeUtils = new AuthTimeUtils();
				mAuthTimeUtils.IsAuthTime(MainActivity.this,url, client_code, auth_type, client_code,"");
				Intent intent = new  Intent(ACTION_READ_MESSAGEINFO);
				intent.putExtra("messageId", client_code);
				sendBroadcast(intent);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String extras = intent.getStringExtra(KEY_EXTRAS);
		if (extras != null) {
			try {
				JSONObject jsonObject = new JSONObject(extras);
				String client_code = jsonObject.getString("client_code");
				String msgid = jsonObject.getString("msgid");
				String auth_type = jsonObject.getString("auth_type");
				String msgtype = jsonObject.getString("msgtype");
				String url = jsonObject.getString("url");
				mAuthTimeUtils = new AuthTimeUtils();
				mAuthTimeUtils.IsAuthTime(MainActivity.this,url, client_code, auth_type, client_code,"");
				intent = new  Intent(ACTION_READ_MESSAGEINFO);
				intent.putExtra("messageId", client_code);
				sendBroadcast(intent);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	   public void windowPermission() {
	        if (Build.VERSION.SDK_INT >= 23) {
	            if (!Settings.canDrawOverlays(getApplicationContext())) {
	                //启动Activity让用户授权
					Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
					if (intent != null){
						intent.setData(Uri.parse("package:" + getPackageName()));
						startActivity(intent);
					}
	                return;
	            } else {
	                //执行6.0以上绘制代码
	            }
	        } else {
	            //执行6.0以下绘制代码
	        }

	        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
	                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
	                != PackageManager.PERMISSION_GRANTED) {
	            ActivityCompat.requestPermissions(this,
	                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, Activity.RESULT_FIRST_USER);
	        }

	    }

	private void getUserInfo() {
		RequestConfig config = new RequestConfig(this, HttpTools.GET_USER_INFO,null);
		RequestParams params = new RequestParams();
		params.put("uid", UserInfo.uid);
		HttpTools.httpGet(Contants.URl.URL_ICETEST,"/account", config, params);
	}
	 // 检测版本更新
    private void getVersion() {
		RequestConfig config = new RequestConfig(this, HttpTools.GET_VERSION_INFO);
		RequestParams params = new RequestParams();
		String version = UpdateManager.getVersionName(MainActivity.this);
		params.put("version",version);
		params.put("type", "android");
		HttpTools.httpGet(Contants.URl.URL_ICETEST,"/czywg/version", config, params);
    }
	//轮播图
	private void getAdInfo() {
		RequestParams params = new RequestParams();
		params.put("corp_id",Tools.getStringValue(MainActivity.this,Contants.storage.CORPID));
		params.put("plate_code","100301");
		HttpTools.httpPost(Contants.URl.URL_ICETEST,"/newoa/banner/list", new RequestConfig(this,
				HttpTools.GET_AD_LIST, null),params);
	}
    // 获取扫码开门权限
    private void OAtoCZY() {
    	RequestConfig config = new RequestConfig(this, HttpTools.GET_CZY_ID);
    	RequestParams params = new RequestParams();
    	params.put("oa", UserInfo.employeeAccount);
		HttpTools.httpGet(Contants.URl.URL_ICETEST,"/newczy/customer/infoByOa", config, params);
    }
    // 获取即时分成金额
    private void getAccountInfo() {
    	RequestConfig config = new RequestConfig(this, HttpTools.GET_ACCOUNT_INFO);
		RequestParams params = new RequestParams();
		params.put("access_token", "1521ac83521b8063e7a9a49dc22e79b0");
		params.put("target_type", "2");
		params.put("target", UserInfo.employeeAccount);
		HttpTools.httpGet(Contants.URl.URL_ICETEST, "/splitdivide/api/account",config, params);
    }
	// 首页列表消息设置为已读
	private void getReadMessageInfo(String client_code) {
		RequestConfig config = new RequestConfig(this, HttpTools.SET_MSG_READ);
		RequestParams params = new RequestParams();
		params.put("client_code",client_code);
		params.put("username",UserInfo.employeeAccount);
		HttpTools.httpPut(Contants.URl.URL_ICETEST, "/push2/homepush/readhomePush",config, params);
	}
	// 更新首页消息推送列表
	private void getUpdatePushInfo() {
		RequestConfig config = new RequestConfig(this, HttpTools.GET_UPDATE_PUSH);
		RequestParams params = new RequestParams();
		params.put("username", UserInfo.employeeAccount);
		params.put("corp_id", Tools.getStringValue(MainActivity.this,Contants.storage.CORPID));
		HttpTools.httpGet(Contants.URl.URL_ICETEST, "/push2/homepush/gethomePushBybox",config, params);
	}
	private void postDeleteListItem(String source_id,String comefrom) {
		RequestConfig config = new RequestConfig(this, HttpTools.POST_DELETE_INFO,null);
		RequestParams params = new RequestParams();
		params.put("usernames", UserInfo.employeeAccount);
		params.put("source_id",source_id);
		params.put("comefrom",comefrom);
		HttpTools.httpDelete(Contants.URl.URL_ICETEST,"/homelist", config, params);
	}

	/**
	 * 获取key
	 * sectet
	 */
	public void getEmployeeInfo() {
		String pwd = Tools.getPassWord(this);
		RequestConfig config = new RequestConfig(this, HttpTools.SET_EMPLOYEE_INFO,null);
		RequestParams params = new RequestParams();
		params.put("username", UserInfo.employeeAccount);
		params.put("password",pwd);
		HttpTools.httpPost(Contants.URl.URL_ICETEST,"/czywg/employee/login", config, params);
	}
	/**
	 * 获取token
	 * sectet
	 */
	private void getTokenInfo() {
		getAuth("","case","0","case","");
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				getAuth("","case","1","case","");
			}
		},500);
	}
	/**
	 * 获取token（2.0）
	 * sectet
	 */
	private void getAuthAppInfo() {
		if(authAppService == null){
			authAppService = new AuthAppService(MainActivity.this);
		}
		authAppService.getAppAuth(new GetTwoRecordListener<String, String>() {
			@Override
			public void onFinish(String jsonString, String data2,String data3) {
				int code = HttpTools.getCode(jsonString);
				if(code == 0){
					JSONObject content = HttpTools.getContentJSONObject(jsonString);
					if(content.length() > 0){
						try {
							String accessToken = content.getString("accessToken");
							String expireTime = content.getString("expireTime");
							Tools.saveStringValue(MainActivity.this,Contants.storage.APPAUTH,accessToken);
							Tools.saveStringValue(MainActivity.this,Contants.storage.APPAUTHTIME,expireTime);
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

				}
			}

			@Override
			public void onFailed(String Message) {

			}
		});
	}
	/**
	 * 获取token（1.0）
	 * sectet
	 */
	private void getAppAuthInfo() {
		if(appAuthService == null){
			appAuthService = new AppAuthService(MainActivity.this);
		}
		appAuthService.getAppAuth(new GetTwoRecordListener<String, String>() {
			@Override
			public void onFinish(String jsonString, String data2,String data3) {
				int code = HttpTools.getCode(jsonString);
				if(code == 0){
					JSONObject content = HttpTools.getContentJSONObject(jsonString);
					if(content.length() > 0){
						try {
							String accessToken = content.getString("access_token");
							String expireTime = content.getString("expire");
							Tools.saveStringValue(MainActivity.this,Contants.storage.APPAUTH_1,accessToken);
							Tools.saveStringValue(MainActivity.this,Contants.storage.APPAUTHTIME_1,expireTime);
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

				}
			}

			@Override
			public void onFailed(String Message) {

			}
		});
	}
	private void initView() {
		initLocation();
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		TabHost.TabSpec tab;
		int[] resIDs = { R.drawable.tab1_selector, R.drawable.tab2_selector,
				R.drawable.tab3_selector, R.drawable.tab4_selector };
		for (int i = 0; i < tabTexts.length; i++) {
			if(i == 0){
				tab = mTabHost.newTabSpec(tabTexts[i]).setIndicator(getTabViewDot(resIDs[i], tabTexts[i])).setContent(android.R.id.tabcontent);
			}else{
				tab = mTabHost.newTabSpec(tabTexts[i]).setIndicator(getTabView(resIDs[i], tabTexts[i])).setContent(android.R.id.tabcontent);
			}
			mTabHost.addTab(tab);
		}
		mTabHost.setOnTabChangedListener(this);
		mTabHost.setCurrentTabByTag(tabTexts[0]);
		showFragment(tabTexts[0]);
	}
	private void huxinInit() {
        HuxinSdkManager.instance().setCgjOpenDoorActivty(DoorActivity.class);
	}
	/**
	 * 更新应用消息轮播图
	 */
	public void freshAdInfo() {
		hand.removeCallbacks(getAdRunnable);
		hand.post(getAdRunnable);
	}
	@Override
	public void onRequestStart(Message msg, String hintString) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		// TODO Auto-generated method stub
		int code = HttpTools.getCode(jsonString);
		String message = HttpTools.getMessageString(jsonString);
		if (msg.arg1 == HttpTools.GET_USER_INFO) {
			String response = HttpTools.getContentString(jsonString);
			ResponseData data = HttpTools.getResponseContentObject(response);
			Tools.loadUserInfo(data,jsonString);
			sendBroadcast(new Intent(ACTION_FRESH_USERINFO));
			hand.removeCallbacks(getUserInfoRunnable);
			hand.postDelayed(getUserInfoRunnable, 10 * 60 * 1000);
		}else if(msg.arg1 == HttpTools.SET_EMPLOYEE_INFO){
			if(code == 0){
				JSONObject content = HttpTools.getContentJSONObject(jsonString);
				if(content != null){
					try {
						String key = content.getString("key");
						String secret = content.getString("secret");
						//保存key  sectet
						 Tools.saveStringValue(MainActivity.this, Contants.EMPLOYEE_LOGIN.key,key);
						 Tools.saveStringValue(MainActivity.this, Contants.EMPLOYEE_LOGIN.secret,secret);
						if(skin_code == 101){//彩生活
							sendBroadcast(new Intent(ACTION_TICKET_INFO));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}else{
				//保存key  sectet
				Tools.saveStringValue(MainActivity.this, Contants.EMPLOYEE_LOGIN.key,"");
				Tools.saveStringValue(MainActivity.this, Contants.EMPLOYEE_LOGIN.secret,"");
			}
		}else if(msg.arg1 == HttpTools.POST_DELETE_INFO){//删除首页消息
			if(code == 0){
				ToastFactory.showToast(MainActivity.this,message);
				if(skin_code == 101){//彩生活
					FragmentManager frgManager = getSupportFragmentManager();
					Fragment fragment = frgManager.findFragmentByTag(tabTexts[0]);
					if (fragment != null) {
						FragmentDeskTop homeFrag = (FragmentDeskTop) fragment;
						homeFrag.freshUIDelete();
					}
				}
			}
		}else if(msg.arg1 == HttpTools.GET_CZY_ID){//获取彩之云账户id
			if(code == 1){
				JSONArray jsonArray =HttpTools.getContentJsonArray(jsonString);
				try {
					JSONObject object = (JSONObject) jsonArray.get(0);
					String CZY_id = object.getString("id");
					String community_id = object.getString("community_id");
					Tools.saveCZYID(MainActivity.this, CZY_id);
					Tools.saveCZY_Community_ID(MainActivity.this, community_id);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else if(msg.arg1 == HttpTools.GET_VERSION_INFO){//版本更新
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
						UpdateManager manager = new UpdateManager(MainActivity.this,true);
						if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
							//申请权限
							ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTPERMISSION);
							ToastFactory.showToast(MainActivity.this, "请允许权限进行下载安装");
						}else{
							manager.checkUpdate(apkinfo);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}else{

			}
		}else if(msg.arg1 == HttpTools.GET_ACCOUNT_INFO){//即时分成
			if(code == 0){
				JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
				try {
				String	account = jsonObject.getString("total_balance");
					if( StringUtils.isNotEmpty(account)){
						if(skin_code == 101){//彩生活
							FragmentManager frgManager = getSupportFragmentManager();
							Fragment fragment = frgManager.findFragmentByTag(tabTexts[0]);
							if (fragment != null) {
								FragmentDeskTop homeFrag = (FragmentDeskTop) fragment;
								homeFrag.freshUIAccount(account);
							}
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else if(msg.arg1 == HttpTools.GET_AD_LIST){//工作页轮播图
			hand.removeCallbacks(getAdRunnable);
			hand.postDelayed(getAdRunnable, 60 * 1000 * 60 * 2);
			if(code == 0){
				Tools.saveStringValue(MainActivity.this,Contants.storage.ADVLIST,jsonString);
				JSONObject content = HttpTools.getContentJSONObject(jsonString);
				listadv.clear();
				try {
					JSONObject list = content.getJSONObject("list");
					JSONArray jarray = list.getJSONArray("100301");
					ResponseData data = HttpTools.parseJsonArray(jarray);
					AdvInfo adInfo;
					for (int i = 0; i < data.length; i++) {
						adInfo = new AdvInfo();
						adInfo.pid = data.getInt(i, "plate_code");
						adInfo.pName = data.getString(i, "name");
						adInfo.imgUrl = data.getString(i, "img_path");
						adInfo.url = data.getString(i, "url");
						listadv.add(adInfo);
					}

					setAdvData();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}else if(msg.arg1 == HttpTools.SET_MSG_READ){//首页消息已读未读
			if(code == 0){
				if(skin_code == 101){//彩生活
					FragmentManager frgManager = getSupportFragmentManager();
					Fragment fragment = frgManager.findFragmentByTag(tabTexts[0]);
					if (fragment != null) {
						FragmentDeskTop homeFrag = (FragmentDeskTop) fragment;
						homeFrag.freshUIListType();

					}
				}else if (skin_code == 100 ){
					FragmentManager frgManager = getSupportFragmentManager();
					Fragment fragment = frgManager.findFragmentByTag(tabTexts[0]);
					if (fragment != null) {
						FragmentHome homeFrag = (FragmentHome) fragment;
						homeFrag.freshUIListType();
					}
				}else if(skin_code == 102){
					FragmentManager frgManager = getSupportFragmentManager();
					Fragment fragment = frgManager.findFragmentByTag(tabTexts[0]);
					if (fragment != null) {
						FragmentCentre homeFrag = (FragmentCentre) fragment;
						homeFrag.freshUIListType();
					}
				}
			}else{
				//ToastFactory.showToast(MainActivity.this,message);
			}
		}else if(msg.arg1 == HttpTools.GET_UPDATE_PUSH){//更新首页推送消息
			if(code == 0){
				ArrayList<HomeDeskTopInfo> list = new ArrayList<HomeDeskTopInfo>();
				String response = HttpTools.getContentString(jsonString);
				if (StringUtils.isNotEmpty(response)) {
					Tools.saveHomeList(MainActivity.this,jsonString);
					ResponseData data = HttpTools.getResponseData(response);
					HomeDeskTopInfo item;
					for (int i = 0; i < data.length; i++) {
						item = new HomeDeskTopInfo();
						item.id = data.getInt(i, "id");
						item.auth_type = data.getInt(i, "auth_type");
						item.icon = data.getString(i, "ICON");
						item.owner_name = data.getString(i, "owner_name");
						item.owner_avatar = data.getString(i, "owner_avatar");
						item.modify_time = data.getString(i, "homePushTime");
						item.title = data.getString(i, "title");
						item.source_id = data.getString(i, "source_id");
						item.comefrom = data.getString(i, "comefrom");
						item.url = data.getString(i, "url");
						item.client_code = data.getString(i, "client_code");
						item.notread = data.getInt(i, "notread");
						list.add(item);
					}
				}
				if(skin_code == 101){//彩生活
					FragmentManager frgManager = getSupportFragmentManager();
					Fragment fragment = frgManager.findFragmentByTag(tabTexts[0]);
					if (fragment != null) {
						FragmentDeskTop homeFrag = (FragmentDeskTop) fragment;
						homeFrag.freshPushUIListType(list);
					}
				}else if (skin_code == 100 ){
					FragmentManager frgManager = getSupportFragmentManager();
					Fragment fragment = frgManager.findFragmentByTag(tabTexts[0]);
					if (fragment != null) {
						FragmentHome homeFrag = (FragmentHome) fragment;
						homeFrag.freshPushUIListType(list);
					}
				}else if(skin_code == 102){
					FragmentManager frgManager = getSupportFragmentManager();
					Fragment fragment = frgManager.findFragmentByTag(tabTexts[0]);
					if (fragment != null) {
						FragmentCentre homeFrag = (FragmentCentre) fragment;
						homeFrag.freshPushUIListType(list);
					}
				}
			}else{
				ToastFactory.showToast(MainActivity.this,message);
			}
		}
	}

	@Override
	public void onFail(Message msg, String hintString) {
		// TODO Auto-generated method stub
		if (msg.arg1 == HttpTools.GET_USER_INFO) {
			hand.removeCallbacks(getUserInfoRunnable);
			hand.postDelayed(getUserInfoRunnable, 60 * 1000);
		}else if (msg.arg1 == HttpTools.GET_AD_LIST) {// 广告
			hand.removeCallbacks(getAdRunnable);
			hand.postDelayed(getAdRunnable, 60 * 1000);
		}
	}
	/**
	 * 应用列表轮播图
	 */
	public void setAdvData() {
		if (listadv.size() == 0) {
			return;
		}
		FragmentManager frgManager = getSupportFragmentManager();
		Fragment fragment = frgManager.findFragmentByTag(tabTexts[2]);
		if (fragment != null) {
			FragmentManagement homeFrag = (FragmentManagement) fragment;
			homeFrag.setAdvList(listadv);
		}
	}
	public View getTabViewDot(int resId, String tab) {
		LayoutInflater layoutInflater = getLayoutInflater();
		View v = layoutInflater.inflate(R.layout.tab_layout, null);
		ImageView img = (ImageView) v.findViewById(R.id.tab_img);
		iv_home_unreadnum = (ImageView) v.findViewById(R.id.tv_new_xinxi);
		TextView tabText = (TextView) v.findViewById(R.id.tab_text);
		iv_home_unreadnum.setVisibility(View.VISIBLE);
		tabText.setText(tab);
		img.setImageResource(resId);
		return v;
	}
	public View getTabView(int resId, String tab) {
		LayoutInflater layoutInflater = getLayoutInflater();
		View v = layoutInflater.inflate(R.layout.tab_layout, null);
		ImageView img = (ImageView) v.findViewById(R.id.tab_img);
		TextView tabText = (TextView) v.findViewById(R.id.tab_text);
		tabText.setText(tab);
		img.setImageResource(resId);
		return v;
	}
	
	@Override
	public void onTabChanged(String tabId) {
		showFragment(tabId);
	}
	private void showFragment(String tabId) {
		Fragment fragment;
		FragmentManager frgManager = getSupportFragmentManager();
		FragmentTransaction transaction = frgManager.beginTransaction();
		for (int i = 0; i < tabTexts.length; i++) {
			fragment = frgManager.findFragmentByTag(tabTexts[i]);
			if (tabId.equals(tabTexts[i])) {
				if (fragment == null) {
					if(skin_code == 102){//中住
						transaction.add(R.id.contentLayout, fragmentsCentreLive[i],tabTexts[i]);
						fragment = fragmentsCentreLive[i];
					}else if(skin_code == 100){//（通用）
						transaction.add(R.id.contentLayout, fragmentsCityProperty[i],tabTexts[i]);
						fragment = fragmentsCityProperty[i];
					}else{//4.0
						transaction.add(R.id.contentLayout, fragments[i],tabTexts[i]);
						fragment = fragments[i];
					}
				}
				transaction.show(fragment);
			} else {
				if (fragment != null) {
					transaction.hide(fragment);
				}
			}
		}
		transaction.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	public void onBackPressed() {
		backPress();
	}
	
	Runnable run = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			exit = false;
		}
	};
	
	/**
	 * 退出程序
	 */
	private void backPress() {
		if (exit) {
			Tools.setMainStatus(MainActivity.this,false);
			Intent intent = new Intent();
	        // 关闭该Service
			stopService(new Intent(this, MessageService.class));
			hand.removeCallbacksAndMessages(null);
			CityPropertyApplication.exitApp(this);
		} else {
			exit = true;
			ToastFactory.showBottomToast(this, "再按一次退出程序");
			hand.postDelayed(run, 2500);
		}
	}

	public void UpdataUnreadNum(int UnreadNum) {
		if (iv_home_unreadnum != null) {
			if (UnreadNum <= 0) {
				iv_home_unreadnum.setVisibility(View.GONE);
			} else {
				iv_home_unreadnum.setVisibility(View.VISIBLE);
			}
            HuxinSdkManager.instance().setCGJNoticeData(MainActivity.this, UnreadNum + "");
            HuxinSdkManager.instance().setCgjNoticeActivty(MainActivity.class);
		}
	}
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Activity.RESULT_FIRST_USER:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                	DialogFactory.getInstance().showDialog(MainActivity.this, new OnClickListener() {
						
						@Override
						public void onClick(View v) {
	                         restartAPP(MainActivity.this, 500);
						}
					}, null, "已成功获取到您设备的存储权限,", "重启", "稍后");
                } else {
                }
                break;
            case REQUESTPERMISSION:
            	if (grantResults.length > 0&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            		
            	} else {
            		
            	}
            	break;
        }
    }
    
    /**
     * 重启整个APP
     *
     * @param context
     * @param Delayed 延迟多少毫秒
     */
    public void restartAPP(Context context, long Delayed) {

        /**开启一个新的服务，用来重启本APP*/
        Intent intent1 = new Intent(context, killSelfService.class);
        intent1.putExtra("PackageName", context.getPackageName());
        intent1.putExtra("Delayed", Delayed);
        context.startService(intent1);

        /**杀死整个进程**/
        android.os.Process.killProcess(android.os.Process.myPid());
    }
	/**
	 * 应用授权
	 *
	 * @param url
	 * @param clientCode
	 * @param oauthType
	 * @param developerCode
	 */
	private void getAuth(final String url,
						 String clientCode, String oauthType, String developerCode,final String param) {
		if (homeService == null) {
			homeService = new HomeService(MainActivity.this);
		}
		if("0".equals(oauthType)  ||  oauthType==null)//oauth1认证
		{
			homeService.getAuth(clientCode, new GetTwoRecordListener<String, String>() {

				@Override
				public void onFinish(String openID, String accessToken,String Expire) {
					Date dt = new Date();
					Long time = dt.getTime();
					Tools.saveOpenID(MainActivity.this, openID);
					Tools.saveAccessToken(MainActivity.this, accessToken);
					Tools.saveCurrentTime(MainActivity.this, time);

				}

				@Override
				public void onFailed(String Message) {
					// TODO Auto-generated method stub

				}
			});
		}else{//oauth2认证
			homeService.getAuth2(developerCode, new GetTwoRecordListener<String, String>() {

				@Override
				public void onFinish(String username, String accessToken,String Expire) {
					Date dt = new Date();
					Long time = dt.getTime();
					Tools.saveAccess_token(MainActivity.this,accessToken);
					Tools.saveCurrentTime2(MainActivity.this, time);
					Tools.saveExpiresTime2(MainActivity.this,Long.parseLong(Expire));
				}

				@Override
				public void onFailed(String Message) {
					// TODO Auto-generated method stub

				}
			});
		}
	}
	/**
	 * 激活定位
	 */
	@Override
	public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener)
	{
		mListener = onLocationChangedListener;
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate()
	{
		mListener = null;
		if (mlocationClient != null)
		{
			mlocationClient.stopLocation();
			mlocationClient.onDestroy();
		}
		mlocationClient = null;
	}

	private void initLocation()
	{
		if (mlocationClient == null)
		{
			//初始化client
			mlocationClient = new AMapLocationClient(getApplicationContext());
			//设置定位参数
			mlocationClient.setLocationOption(getDefaultOption());
			// 设置定位监听
			mlocationClient.setLocationListener(this);

			mlocationClient.startLocation();

		}
	}

	private AMapLocationClientOption getDefaultOption()
	{
		AMapLocationClientOption mOption = new AMapLocationClientOption();
		mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
		mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
		mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
		mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是ture
		mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
		return mOption;
	}

	@Override
	public void onLocationChanged(AMapLocation aMapLocation) {
		if (null != aMapLocation) {
			deactivate();
			//解析定位结果
			String result = Utils.getLocationStr(aMapLocation);
			if(result != null){
				//Log.d("printLog","result="+result);
				Tools.saveStringValue(MainActivity.this,Contants.MAP.ADDRESS,result);
			}
		}else {
			Log.d("TAG","定位失败");
		}
	}
	/**
	 * 极光推送设置
	 */
	private void setTag(){

		Set<String> set = new HashSet<String>();
		String s1 = "cgj";
		String s2 =Tools.getStringValue(MainActivity.this,Contants.storage.CORPID);
		set.add(s1);
		set.add(s2);
		//调用JPush API设置Tag
		mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAGS, set));

	}

	private void setAlias(){
		String alias  = "cgj_"+UserInfo.employeeAccount;
		//调用JPush API设置Alias
		mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
	}
	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs ;
			switch (code) {
				case 0:
					Logger.logd(TAG,"alias   设置成功   code="+code);
					Tools.setBooleanValue(MainActivity.this,Contants.storage.ALIAS,true);
					break;

				case 6002:
					logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
					if (ExampleUtil.isConnected(getApplicationContext())) {
						mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
					} else {
						Log.i(TAG, "No network");
					}
					break;

				default:
					logs = "Failed with errorCode = " + code;
					Log.e(TAG, logs);
			}
		}

	};

	private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs ;
			switch (code) {
				case 0:
					Logger.logd(TAG,"tag   设置成功   code="+code);
					Tools.setBooleanValue(MainActivity.this,Contants.storage.Tags,true);
					break;
				case 6002:
					if (ExampleUtil.isConnected(getApplicationContext())) {
						mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
					} else {
						Log.i(TAG, "No network");
					}
					break;

				default:
					logs = "Failed with errorCode = " + code;
					Log.e(TAG, logs);
			}
		}

	};

	private static final int MSG_SET_ALIAS = 1001;
	private static final int MSG_SET_TAGS = 1002;



	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case MSG_SET_ALIAS:
					JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
					break;

				case MSG_SET_TAGS:
					JPushInterface.setAliasAndTags(getApplicationContext(), null, (Set<String>) msg.obj, mTagsCallback);
					break;

				default:
			}
		}
	};
}
