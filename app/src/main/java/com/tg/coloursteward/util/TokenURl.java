package com.tg.coloursteward.util;

import java.util.HashMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.application.CityPropertyApplication;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.view.RotateProgress;

public class  TokenURl{
	// 获取消息线程
	private  static MessageThread messageThread = null;
	private static long currentTime;
	private static TextView tvMsg;
	private static RotateProgress progressBar;
	private static AlertDialog transitionDialog;

	public static  String getTokenURl(Activity context,String URl,String oauthType,String developerCode,String clientCode){
		showTransitionDialog(context,"正在授权. . .");
		messageThread = new MessageThread(oauthType,developerCode,clientCode);
		messageThread.start();
		while (messageThread.accessToken == null)
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		String str = "?";
		if(URl.contains(str)){//Url有问号
			if(oauthType.equals("0")){
				transitionDialog.dismiss();
				return URl+"&openID="+messageThread.openID+""+"&accessToken="+messageThread.accessToken;
			}else{
				transitionDialog.dismiss();
				return URl+"&username="+UserInfo.employeeAccount+"&access_token="+messageThread.accessToken;
			}
		}else{//没有问号
			if(oauthType.equals("0")){
				transitionDialog.dismiss();
				return URl+"?openID="+messageThread.openID+""+"&accessToken="+messageThread.accessToken;
			}else{
				transitionDialog.dismiss();
				return URl+"?username="+UserInfo.employeeAccount+"&access_token="+messageThread.accessToken;
			}
		}
	}
	private static void showTransitionDialog(final Activity activity, String text) {
		if (transitionDialog == null) {
			DisplayMetrics metrics = Tools.getDisplayMetrics(activity);
			transitionDialog = new AlertDialog.Builder(activity).create();
			transitionDialog.setCanceledOnTouchOutside(false);
			transitionDialog.setCancelable(true);
			transitionDialog.show();
			Window window = transitionDialog.getWindow();
			LinearLayout layout = (LinearLayout) LayoutInflater.from(activity)
					.inflate(R.layout.transition_dialog_layout, null);
			tvMsg = (TextView) layout.findViewById(R.id.dialog_hint);
			progressBar = (RotateProgress) layout.findViewById(R.id.progressBar);
			window.setContentView(layout);
			WindowManager.LayoutParams p = window.getAttributes();
			p.width = ((int) (metrics.widthPixels - 80 * metrics.density));
			p.height = (int) (120 * metrics.density);
			window.setAttributes(p);
		}
		if(TextUtils.isEmpty(text)){
			tvMsg.setVisibility(View.GONE);
		}else{
			tvMsg.setText(text);
			tvMsg.setVisibility(View.VISIBLE);
		}
		progressBar.setVisibility(View.VISIBLE);
		transitionDialog.show();
	}
	/**
	 * 从服务器端获取消息
	 * 
	 */
	static class MessageThread extends Thread {
		private String type;
		private String developerCode;
		private String clientCode;
		private String openID;
		private String accessToken;
		public MessageThread(String type,String developerCode,String clientCode) {
			this.type = type;
			this.developerCode = developerCode;
			this.clientCode = clientCode;
		}
		public void run() {
				/**
				 *  获取数据
				 */
				String  jsonString = getToken(type,developerCode,clientCode);
				int code = HttpTools.getCode(jsonString);
				if(code == 0){
					String jsonObject = HttpTools.getContentString(jsonString);
					if(jsonObject != null){
						ResponseData data = HttpTools.getResponseContentObject(jsonObject);
						if(type.equals("0")){
							openID = data.getString("openID");
							accessToken = data.getString("accessToken");
						}else if(type.equals("1")){
							accessToken = data.getString("access_token");
						}
					}
				}
		}

		/**
		 * 这里以此方法为服务器Demo，仅作示例
		 * 
		 * @return 返回服务器要推送的消息，否则如果为空的话
		 */
		public  String getToken(String oauthType,String developerCode,String clientCode) {
			try {
				RequestParams params = new RequestParams();
				params.put("username", UserInfo.employeeAccount);
				params.put("password",  Tools.getPassWordMD5(CityPropertyApplication.getInstance()));
				if(oauthType.equals("0")){
					params.put("clientCode",clientCode);
				}else if(oauthType.equals("1")){
					params.put("developerCode", developerCode);
					params.put("accountType", "cgj");
				}
				
				HashMap<String, Object> paramsStr = null;
				if (params == null) {
					paramsStr = null;
				} else {
					paramsStr = params.toHashMap();
				}
				String baseUrl = null;
				if(oauthType.equals("0")){
					baseUrl = Contants.URl.URL_ICETEST+HttpTools.GetUrl(Contants.URl.URL_ICETEST,"/auth",paramsStr);
				}else if(oauthType.equals("1")){
					baseUrl = Contants.URl.URL_ICETEST+HttpTools.GetUrl(Contants.URl.URL_ICETEST,"/auth2",paramsStr);
				}
				HttpGet getMethod = new HttpGet(baseUrl);// 将URL与参数拼接
				HttpClient httpClient = new DefaultHttpClient();
				HttpResponse response = httpClient.execute(getMethod); // 发起GET请求
				int code = response.getStatusLine().getStatusCode(); // 获取响应码
				if (code == 200) {
					String jsonObject = EntityUtils.toString(response.getEntity(),"utf-8");// 获取服务器响应内容
					if (jsonObject != null) {
						return jsonObject;
					}
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		}
}
	 }
