package com.tg.coloursteward;

import com.tg.coloursteward.base.BaseActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.graphics.Bitmap;

public class SettingPolicyActivity extends BaseActivity {
	private final String TAG = "SettingPolicyActivity";
	private WebView webView;

	private String website;
	private String title;
	private int isShowLoading = 0;// 1为商品列表 不需要loading 0位默认需要loading
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent data = getIntent();
		if (data.getExtras() != null) {
			website = data.getExtras().getString("addr");
			title = data.getExtras().getString("title");
			isShowLoading = data.getExtras().getInt("isloading");

		} else {
			website = "http://www.colourlife.com/help?category_id=30";
			title ="用户协议";// 用户协议
		}

		prepareView();
	}

	private void prepareView() {
		webView = (WebView) findViewById(R.id.webview);
		webView.requestFocusFromTouch();
		webView.setDownloadListener(new MyWebViewDownLoadListener());
		webView.loadUrl(website);

		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}

		});
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setLoadsImagesAutomatically(true);
		settings.setUseWideViewPort(true);
		settings.setBuiltInZoomControls(false);
		settings.setLoadWithOverviewMode(true);

	}

	private class MyWebViewDownLoadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}

	}

	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_setting_policy,null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "日志";
	}


}
