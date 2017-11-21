package com.tg.coloursteward;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.githang.statusbar.StatusBarCompat;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.util.FileSizeUtil;
import com.tg.coloursteward.util.Helper;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.util.Utils;
import com.tg.coloursteward.view.X5WebView;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.JavascriptInterface;
import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 浏览器BaseActivity
 * @author Administrator
 *
 */
public class MyBrowserActivity extends Activity implements OnClickListener, AMapLocationListener {
	private final String TAG = "MyBrowserActivity";
	public static final String KEY_HIDE_TITLE = "hide";
	public static final String KEY_TITLE = "title";
	public static final String KEY_HTML_TEXT = "text";
	public static final String KEY_URL = "url";
	protected X5WebView webView;
	private RelativeLayout rlHeadContent;
	private RelativeLayout rlRollback;
	private RelativeLayout rlRefresh;
	private RelativeLayout rlClose;
	protected String htmlText;
	private TextView tvTitle;
	private String url;
	private final static int STOP = 0x10000;
	private final static int NEXT = 0x10001;
	private int count = 0;
	private ProgressBar bar;
	private String title;
	private String uuid;
	private Intent data;
	private Boolean hideTitle;
	private int isShowLoading = 0;
	private ProgressDialog mDialog;
    private String mCameraPhotoPath;
	private ValueCallback<Uri> uploadFile;
	private ValueCallback<Uri[]> uploadFiles;
    public static boolean forepriority = false;
	public static final int REQUEST_CODE_LOLIPOP = 1;
	private final static int RESULT_CODE_ICE_CREAM = 2;
	private GetDeviceIdReceiver deviceIdReceiver;
	private static final int OLD_FILE_SELECT_CODE = 6;
	private static final int FILE_SELECT_CODE = 4;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);
		StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.home_fill), false);
		 forepriority = true;
		registScanResultReceiver();
		registGetDeviceIDReceiver();
		 data = getIntent();
		if (data != null) {
			hideTitle = data.getBooleanExtra(KEY_HIDE_TITLE, false);
			htmlText = data.getStringExtra(KEY_HTML_TEXT);
			url = data.getStringExtra(KEY_URL);
		}
		/**
		 * 初始化控件
		 */
		prepareView();
		// 初始化定位
		initLocation();
	}

	private void prepareView() {
		webView = (X5WebView) findViewById(R.id.webView);
		bar = (ProgressBar)findViewById(R.id.myProgressBar);
		rlHeadContent = (RelativeLayout) findViewById(R.id.head_content);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		rlRollback = (RelativeLayout) findViewById(R.id.rl_rollback);
		rlRefresh = (RelativeLayout) findViewById(R.id.rl_refresh);
		rlClose = (RelativeLayout) findViewById(R.id.rl_close);
		rlRollback.setOnClickListener(this);
		rlRefresh.setOnClickListener(this);
		rlClose.setOnClickListener(this);
		if (hideTitle) {
			rlHeadContent.setVisibility(View.GONE);
		} else {
			title = data.getStringExtra(KEY_TITLE);
			if (!TextUtils.isEmpty(title)) {
				tvTitle.setText(title);
			}
		}
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				if (url.startsWith("tel:")) {
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
					startActivity(intent);
				}else{
					view.loadUrl(url);
				}
				return true;
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				tvTitle.setText(view.getTitle());
				DialogFactory.getInstance().hideLoadialogDialog();
			}


			@Override
			public void onPageStarted(WebView view, String url,
									  Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				if (isShowLoading == 0) {
					isShowLoading = 1;
					DialogFactory.getInstance().createLoadingDialog(MyBrowserActivity.this, "加载中...");
				}

			}
		});
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setAllowFileAccess(true);// 设置允许访问文件数据
		settings.setLoadsImagesAutomatically(true);
		settings.setUseWideViewPort(true);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		settings.setLoadWithOverviewMode(true);
		settings.setDomStorageEnabled(true);
		webView.setDownloadListener(new MyWebViewDownLoadListener());
		if (!TextUtils.isEmpty(htmlText)) {
			webView.loadDataWithBaseURL(null, htmlText, "text/html", "utf-8",
					null);
		} else if (!TextUtils.isEmpty(url)) {
			// 设置setWebChromeClient对象
			webView.setWebChromeClient(new XHSWebChromeClient());
			webView.loadUrl(url);
			webView.addJavascriptInterface(new JsInteration(),"js");
			webView.addJavascriptInterface(new JsInteration(),"myjava");
		}
	}
	public class XHSWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
								 JsResult result) {
			return super.onJsAlert(view, url, message, result);
		}

		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
			handler.proceed();
		}
		@Override
		public void onGeolocationPermissionsShowPrompt(String s, GeolocationPermissionsCallback geolocationPermissionsCallback) {
			geolocationPermissionsCallback.invoke(s, true, false);
			super.onGeolocationPermissionsShowPrompt(s, geolocationPermissionsCallback);
		}
		@Override
		public void onProgressChanged(WebView view, final int newProgress) {
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(newProgress == 0){
						if (newProgress < 100) {
							for (int i = 0; i < 100; i++) {
								try {
									count = i;
									Thread.sleep(20);
									Message msg = new Message();
									msg.what = NEXT;
									handler.sendMessage(msg);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}else if(newProgress == 100){
						count = newProgress;
						Message msg = new Message();
						msg.what = STOP;
						handler.sendMessage(msg);
					}
				}
			});
			t.start();
			super.onProgressChanged(view, newProgress);
		}
		// For Android 3.0+
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
			Log.i("test", "openFileChooser 1");
			MyBrowserActivity.this.uploadFile = uploadFile;
			openFileChooseProcess();
		}

		// For Android < 3.0
		public void openFileChooser(ValueCallback<Uri> uploadMsgs) {
			Log.i("test", "openFileChooser 2");
			MyBrowserActivity.this.uploadFile = uploadFile;
			openFileChooseProcess();
		}

		// For Android  > 4.1.1
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
			Log.i("test", "openFileChooser 3");
			MyBrowserActivity.this.uploadFile = uploadFile;
			openFileChooseProcess();
		}

		// For Android  >= 5.0
		public boolean onShowFileChooser(com.tencent.smtt.sdk.WebView webView,
										 ValueCallback<Uri[]> filePathCallback,
										 WebChromeClient.FileChooserParams fileChooserParams) {
			Log.i("test", "openFileChooser 4:" + filePathCallback.toString());
			MyBrowserActivity.this.uploadFiles = filePathCallback;
			openFileChooseProcess();
			return true;
		}

	}

	private CallBackScanResultReceiver backScanResultReceiver;
	private void registScanResultReceiver() {
		backScanResultReceiver = new CallBackScanResultReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Contants.PARAMETER.CALLBACKSCANRESULT);
		registerReceiver(backScanResultReceiver, filter);
	}
	private void registGetDeviceIDReceiver() {
		deviceIdReceiver = new GetDeviceIdReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Contants.Html5.CALLBACKDeviceID);
		registerReceiver(deviceIdReceiver, filter);

	}
	class GetDeviceIdReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String registrationId = Tools.getUUID(MyBrowserActivity.this);
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("code", "0");
				jsonObject.put("message", "deviceId");
				JSONObject content = new JSONObject();
				content.put("registrationId", registrationId);
				jsonObject.put("content", content);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			webView.loadUrl("javascript:RegistrationIdCallBack('"
					+ jsonObject.toString() + "')");
		}
	}

	class CallBackScanResultReceiver extends BroadcastReceiver {

	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String result = intent.getStringExtra("result");
	            JSONObject jsonObject = new JSONObject();
	            try {
	                JSONObject content = new JSONObject();
	                content.put("result", result);
	                jsonObject.put("content", content);
	                jsonObject.put("code", "0");
	                jsonObject.put("message", "scan");
	            } catch (JSONException e) {
	                e.printStackTrace();
	            }
	            webView.loadUrl("javascript:ScanCallBack('" + jsonObject.toString()
	                    + "')");
	        }
	    }
	/**
	 * H5调用原生
	 * @author Administrator
	 *
	 */
	public class  JsInteration{
		@JavascriptInterface
		public void uploadFile(String json) {
			Helper.setParams(json);
			showOldFileChooser();
		}
		@JavascriptInterface
	    public void GetLatLngCallBack() {
			startLocation();
	    }

		@JavascriptInterface
        public void GetRegistrationId() {
			Intent intent = new Intent();
			intent.setAction(Contants.Html5.CALLBACKDeviceID);
			sendBroadcast(intent);
        }
		@JavascriptInterface
		public void Upload() {
			showFileChooser();
		}
		@JavascriptInterface
		public void GetShareWechat(String Url,String name) {
			Log.d("printLog","Url="+Url);
			Log.d("printLog","name="+name);
			showShare(Url,name);
		}
	}
	private void showOldFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"),
					OLD_FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(MyBrowserActivity.this, "请安装文件管理器", Toast.LENGTH_SHORT)
					.show();
		}
	}
	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"),
					FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(MyBrowserActivity.this, "请安装文件管理器", Toast.LENGTH_SHORT)
					.show();
		}
	}
	private void openFileChooseProcess() {
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		i.setType("*/*");
		startActivityForResult(Intent.createChooser(i, "test"), 0);
	}
	/**
	 * encodeBase64File:(将文件转成base64 字符串).
	 *
	 * @param path 文件路径
	 * @return
	 * @throws Exception
	 * @author guhaizhou@126.com
	 * @since JDK 1.6
	 */
	public static String encodeBase64File(String path) throws Exception {
		File file = new File(path);
		FileInputStream inputFile = new FileInputStream(file);
		byte[] buffer = new byte[(int) file.length()];
		inputFile.read(buffer);
		inputFile.close();
		return Base64.encodeToString(buffer, Base64.DEFAULT);
	}
	 @Override
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
		        super.onActivityResult(requestCode, resultCode, data);
		 if (resultCode == Activity.RESULT_OK
				 && requestCode == FILE_SELECT_CODE) {
			 Uri uri = data.getData();
			 String fileUrl = Helper.getFileAbsolutePath(
					 MyBrowserActivity.this, uri);
			 Log.e(TAG, "fileUrl = " + fileUrl);
			 String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1, fileUrl.length());
			 Log.e(TAG, "fileName = " + fileName);

			 try {
				 // 选择文件
				 String encodeBase64File = encodeBase64File(fileUrl);
				 double fileOrFilesSize = FileSizeUtil.getFileOrFilesSize(fileUrl, FileSizeUtil.SIZETYPE_B);
				 final JSONObject jsonObject = new JSONObject();
				 JSONObject content = new JSONObject();
				 content.put("fileName", fileName);
				 content.put("encodeBase64File", encodeBase64File);
				 jsonObject.put("content", content);
				 jsonObject.put("code", "0");
				 jsonObject.put("message", "file");
				 jsonObject.put("length", fileOrFilesSize);
				 runOnUiThread(new Runnable() {
					 @Override
					 public void run() {
						 Log.d(TAG,"jsonObject.toString()="+jsonObject.toString());
						 webView.loadUrl("javascript:UploadCallBack('"
								 + jsonObject.toString() + "')");
					 }
				 });
				 Log.e(TAG, jsonObject.toString());
			 } catch (Exception e) {
				ToastFactory.showToast(MyBrowserActivity.this,e.getMessage());
				 e.printStackTrace();
			 }
		 }else if (requestCode == OLD_FILE_SELECT_CODE
				 && resultCode == Activity.RESULT_OK) {
			 Uri uri = data.getData();
			 String file = Helper.getFileAbsolutePath(MyBrowserActivity.this,
					 uri);
			 Helper.uploadFile(file, webView);
			 ToastFactory.showToast(getApplicationContext(), "后台上传中！");
		 }else if (requestCode == 0
				 && resultCode == Activity.RESULT_OK) {
			 if (null != uploadFile) {
				 Uri result = data == null || resultCode != RESULT_OK ? null
						 : data.getData();
				 uploadFile.onReceiveValue(result);
				 uploadFile = null;
			 }
			 if (null != uploadFiles) {
				 Uri result = data == null || resultCode != RESULT_OK ? null
						 : data.getData();
				 uploadFiles.onReceiveValue(new Uri[]{result});
				 uploadFiles = null;
			 }
		 }
		    }

	/*@Subscribe
	public void onEvent(Object event) {
		final String result = (String) event;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				webView.loadUrl("javascript:uploadCallback('" + result
						+ "')");
				ToastFactory.showToast(MyBrowserActivity.this,"上传成功！");
			}
		});
	}*/

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_rollback:// 返回
			if (webView.canGoBack()) {
				if (webView.getUrl().equals(url)) {
					super.onBackPressed();
				} else {
					webView.goBack();
				}
			}else {
				finish();
			}
			break;
		case R.id.rl_refresh:// 刷新
			webView.reload();
			break;
		case R.id.rl_close:// 关闭
			finish();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//EventBus.getDefault().unregister(this);
		if(backScanResultReceiver!=null){
			unregisterReceiver(backScanResultReceiver);
		}
		if(deviceIdReceiver!=null){
			unregisterReceiver(deviceIdReceiver);
		}
		forepriority = false;
		CookieSyncManager.createInstance(getApplicationContext());  //Create a singleton CookieSyncManager within a context
		CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
		cookieManager.removeAllCookie();// Removes all cookies.
		CookieSyncManager.getInstance().sync(); // forces sync manager to sync now

		webView.clearHistory();
		((ViewGroup) webView.getParent()).removeView(webView);
		webView.loadUrl("about:blank");
		webView.stopLoading();
		webView.setWebChromeClient(null);
		webView.setWebViewClient(null);
		webView.destroy();
		webView = null;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (webView != null) {
			webView.onPause();
		}
	}


	@Override
	protected void onResume() {
		if (webView != null) {
			webView.onResume();
		}
		/*if (!EventBus.getDefault().isRegistered(this))
		{
			EventBus.getDefault().register(this);
		}*/
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		backPress();
	}

	private void backPress() {
		if (webView.canGoBack()) {
			if (webView.getUrl().equals(url)) {
				super.onBackPressed();
			} else {
				webView.goBack();
			}
		}else{
			DialogFactory.getInstance().hideLoadialogDialog();
			finish();
		}
	}

	private Handler handler = new Handler() {
		@SuppressWarnings("static-access")
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case NEXT:
					if (!Thread.currentThread().interrupted()) {
						if(count < 100){
							bar.setProgress(count);
						}
					}
					break;
				case STOP:
					bar.setVisibility(View.GONE);
					break;
			}
		}
	};
	public AMapLocationClientOption mLocationOption = null;
	private AMapLocationClient mlocationClient;
	/**
	 * 初始化定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 * 
	 */
	private void initLocation() {
		mlocationClient = new AMapLocationClient(this);
		//初始化定位参数
		mLocationOption = new AMapLocationClientOption();
		//设置定位监听
		mlocationClient.setLocationListener(this);
		//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		//设置定位间隔,单位毫秒,默认为2000ms
//		mLocationOption.setInterval(2000);
		mLocationOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
		//设置定位参数
		mlocationClient.setLocationOption(mLocationOption);
	}

	
	/**
	 * 开始定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void startLocation(){
		// 设置定位参数
		//mlocationClient.setLocationOption(mLocationOption);
		// 启动定位
		mlocationClient.startLocation();
	}

	/**
	 * 停止定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void stopLocation(){
		// 停止定位
		mlocationClient.stopLocation();
	}
	
	/**
	 * 定位监听
	 */
	/*AMapLocationListener locationListener = new AMapLocationListener() {
		@Override
		public void onLocationChanged(AMapLocation loc) {
			if (null != loc) {
				//解析定位结果
				String result = Utils.getLocationStr(loc);
				if(result != null){
					//Log.d("print","result="+result);
					stopLocation();
					String url = "javascript:LatLngCallBack('" +result+ "')"; 
					webView.loadUrl(url);
				}
			}else {
				
			}
		}
	};*/

	@Override
	public void onLocationChanged(AMapLocation aMapLocation) {
		if (null != aMapLocation) {
			//解析定位结果
			String result = Utils.getLocationStr(aMapLocation);
			if(result != null){
				//Log.d("printLog","2    result="+result);
			//	stopLocation();
				String url = "javascript:LatLngCallBack('" +result+ "')";
				webView.loadUrl(url);
			}
		}else {
			Log.d("TAG","定位失败");
		}
	}

	/**
	 * 销毁定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void destroyLocation(){
		if (null != mlocationClient) {
			/**
			 * 如果AMapLocationClient是在当前Activity实例化的，
			 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
			 */
			mlocationClient.onDestroy();
			mlocationClient = null;
			mLocationOption = null;
		}

	}

	/**
	 * 下载帮助类
	 */
	private class MyWebViewDownLoadListener implements DownloadListener {

		 @Override
	        public void onDownloadStart(String url, String userAgent,
	                                    String contentDisposition, String mimetype, long contentLength) {
	            if (!Environment.getExternalStorageState().equals(
	                    Environment.MEDIA_MOUNTED)) {
	                ToastFactory.showToast(MyBrowserActivity.this, "需要SD卡。");
	                return;
	            }
	            DownloaderTask task;
	            if (contentDisposition.contains("fileName=")) {
	                String fileName = getName(contentDisposition,"fileName=");
	                task = new DownloaderTask(fileName);
	            } else if(contentDisposition.contains("filename=")){
	                String fileName = getName(contentDisposition,"filename=");
	                task = new DownloaderTask(fileName);
	            }
	            else {
	                task = new DownloaderTask();
	            }
	            task.execute(url);
		}
		 
		 private String getName(String contentDisposition,String label) {
	            int lastIndexOf = contentDisposition.lastIndexOf(label);
	            return contentDisposition.substring(lastIndexOf + 9,
	                    contentDisposition.length());
	        }
	}

	/**
	 * 异步下载文件
	 * @author Administrator
	 *
	 */
	  private class DownloaderTask extends AsyncTask<String, Void, String> {

	        private String fileName;

	        public DownloaderTask() {

	        }

	        public DownloaderTask(String fileName) {
	            this.fileName = fileName;
	        }

	        @Override
	        protected String doInBackground(String... params) {
	            String url = params[0];
	            if (fileName == null || fileName.equals("")) {
	                fileName = Tools.getReallyFileName(url);
	            }
	            if (fileName.contains("/")) {
	                int lastIndexOf = fileName.lastIndexOf("/");
	                fileName = fileName.substring(lastIndexOf + 1,
	                        fileName.length());
	            }
	            // UrlDecode解码后文件名
	            String DecoderFileName = "";
	            try {
	                DecoderFileName = URLDecoder.decode(fileName, "UTF-8");
	                if (DecoderFileName.contains("=")) {
	                    int lastIndexOf2 = DecoderFileName.lastIndexOf("=");
	                    DecoderFileName = DecoderFileName.substring(
	                            lastIndexOf2 + 1, DecoderFileName.length());
	                }
	                if(DecoderFileName.contains("UTF-8''")){
						DecoderFileName = DecoderFileName.replaceAll("UTF-8''","");
					}
	            } catch (UnsupportedEncodingException e1) {
	                // 解码失败后将Url中文件名赋值
	                DecoderFileName = fileName;
	                e1.printStackTrace();
	            }
	            InputStream input = null;
	            try {
	                URL fileUrl = new URL(url);
	                HttpURLConnection conn = (HttpURLConnection) fileUrl
	                        .openConnection();
	                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
	                    // 错误提示
	                    return null;
	                }
	                input = conn.getInputStream();
	                if (Environment.getExternalStorageState().equals(
	                        Environment.MEDIA_MOUNTED)) {
	                    File file = new File(Contants.DOWN.DOWNLOAD_DIRECT);
	                    if (!file.exists()) {
	                        // 创建文件夹
	                        file.mkdirs();
	                    }
	                    if(DecoderFileName.contains("\"")){
	                        DecoderFileName = DecoderFileName.replaceAll("\"","");
	                    }
	                    File DownloadDirectory = new File(Contants.DOWN.DOWNLOAD_DIRECT,
	                            getNewName(DecoderFileName, 1));
	                    if (!DownloadDirectory.exists()) {
	                        DownloadDirectory.createNewFile();
	                    }

	                    try {
	                        FileOutputStream fos = new FileOutputStream(
	                                DownloadDirectory);
	                        byte[] b = new byte[2048];
	                        int j = 0;
	                        while ((j = input.read(b)) != -1) {
	                            fos.write(b, 0, j);
	                        }
	                        fos.flush();
	                        fos.close();
							try{
								Intent fileIntent = Tools.getFileIntent(DownloadDirectory);
								startActivity(fileIntent);
								Intent.createChooser(fileIntent, "请选择对应的软件打开该附件！");
							}catch (ActivityNotFoundException e) {
								// TODO: handle exception
								ToastFactory.showToast(MyBrowserActivity.this,"附件不能打开，请下载相关软件！");
							}
	                       /* Intent fileIntent = Tools.getFileIntent(DownloadDirectory);
	                        startActivity(fileIntent);*/
	                        return "下载成功";
	                    } catch (FileNotFoundException e) {
	                        e.printStackTrace();
	                        return "下载失败，文件不存在";
	                    } catch (IOException e) {
	                        Log.e("tag", "IOException:" + e.getMessage());
	                        e.printStackTrace();
	                        return "下载失败";
	                    }
	                } else {
	                    Log.e("tag", "NO SDCard.");
	                }
	                return null;

	            } catch (Exception e) {
	                e.printStackTrace();
	                return null;
	            } finally {
	                if (input != null) {
	                    try {
	                        input.close();
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
	                }
	            }
	        }

	        @Override
	        protected void onCancelled() {
	            // TODO Auto-generated method stub
	            super.onCancelled();
	        }

	        @Override
	        protected void onPostExecute(String result) {
	            // TODO Auto-generated method stub
	            super.onPostExecute(result);
	            closeProgressDialog();
	            if (result == null || "下载失败".equals(result)) {
	               ToastFactory.showToast(MyBrowserActivity.this,"连接错误！请稍后再试！");
	                String callback = "下载失败";
	                webView.loadUrl("javascript:TakePhotoCallBack('" + callback
	                        + "')");
	                return;
	            } else if ("下载成功".equals(result)) {
	            	ToastFactory.showToast(MyBrowserActivity.this,"下载成功，已保存到SD卡。");
	                String callback = "下载成功！";
	                webView.loadUrl("javascript:TakePhotoCallBack('" + callback
	                        + "')");
	                return;
	            } else if ("下载失败，文件不存在".equals(result)) {
	            	ToastFactory.showToast(MyBrowserActivity.this,"下载失败，文件不存在！");
	                String callback = "下载失败，文件不存在！";
	                webView.loadUrl("javascript:TakePhotoCallBack('" + callback
	                        + "')");
	                return;
	            }

	        }

	        @Override
	        protected void onPreExecute() {
	            // TODO Auto-generated method stub
	            super.onPreExecute();
	            showProgressDialog();
	        }

	        @Override
	        protected void onProgressUpdate(Void... values) {
	            // TODO Auto-generated method stub
	            super.onProgressUpdate(values);
	        }

	    }
	  
	  private void showProgressDialog() {
	        if (mDialog == null) {
	            mDialog = new ProgressDialog(MyBrowserActivity.this);
	            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
	            mDialog.setMessage("正在下载 ，请等待...");
	            mDialog.setIndeterminate(false);// 设置进度条是否为不明确
	            mDialog.setCancelable(true);// 设置进度条是否可以按退回键取消
	            mDialog.setCanceledOnTouchOutside(false);
	            mDialog.setOnDismissListener(new OnDismissListener() {

	                @Override
	                public void onDismiss(DialogInterface dialog) {
	                    // TODO Auto-generated method stub
	                    mDialog = null;
	                }
	            });
	            mDialog.show();
	        }
	    }
	  
	  private void closeProgressDialog() {
	        if (mDialog != null) {
	            mDialog.dismiss();
	            mDialog = null;
	        }
	    }
	  private static String getNewName(String fileName, int i) {
	        String end = fileName.substring(fileName.lastIndexOf(".") + 1)
	                .toLowerCase();
	        String name = fileName.substring(0, fileName.lastIndexOf("."));
	        if (1 == i) {
	            return name + "." + end;
	        }
	        String newFile = name + "(" + i + ")." + end;
	        File directory = new File(Contants.DOWN.DOWNLOAD_DIRECT);
	        File f = new File(directory + newFile);
	        if (f.exists()) {
	            i++;
	            return getNewName(fileName, i);
	        } else {
	            return newFile;
	        }
	    }

	/**
	 * 分享
	 */
	private void showShare(String url,String name) {
		OnekeyShare oks = new OnekeyShare();
		//关闭sso授权
		oks.disableSSOWhenAuthorize();
		// title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
		oks.setTitle(getResources().getString(R.string.app_name));
		// titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
		oks.setTitleUrl(url);
		// text是分享文本，所有平台都需要这个字段
		oks.setText(name);
		//分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
		// oks.setImageUrl("22");
		oks.setImageUrl("http://pic6.nipic.com/20091207/3337900_161732052452_2.jpg");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		//oks.setImagePath("file:///android_asset/test.png");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(url);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment(name);
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite("ShareSDK");
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(url);

		// 启动分享GUI
		oks.show(this);

	}
}
