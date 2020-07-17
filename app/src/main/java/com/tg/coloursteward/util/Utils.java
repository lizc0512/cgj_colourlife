/**
 * 
 */
package com.tg.coloursteward.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.tg.coloursteward.info.GPSInfo;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 辅助工具类(高德定位)
 * 创建时间： 2015年11月24日 上午11:46:50
 * 项目名称： AMapLocationDemo2.x
 * author hongming.wang
 * 文件名称: Utils.java
 * 类型名称: Utils
 */
public class Utils {
	private static JSONObject object;  
	private static JSONObject object2;
	private static String jsonString;
	private static List<GPSInfo> message; //保存数据的集合
	/**
	 *  开始定位
	 */
	public final static int MSG_LOCATION_START = 0;
	/**
	 * 定位完成
	 */
	public final static int MSG_LOCATION_FINISH = 1;
	/**
	 * 停止定位
	 */
	public final static int MSG_LOCATION_STOP= 2;
	
	public final static String KEY_URL = "URL";
	public final static String URL_H5LOCATION = "file:///android_asset/location.html";
	
	/**
	 * 默认的定位参数
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	public static AMapLocationClientOption getDefaultOption(){
		AMapLocationClientOption mOption = new AMapLocationClientOption();
		mOption.setLocationMode(AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
		mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
		mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
		mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
		mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
		mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
		return mOption;
	}
	/**
	 * 根据定位结果返回定位信息的字符串
	 * @param location
	 * @return
	 */
	public synchronized static String getLocationStr(AMapLocation location){
		if(null == location){
			return null;
		}
		message = new ArrayList<GPSInfo>();
		//errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
		if(location.getErrorCode() == 0){
			
			GPSInfo info = new GPSInfo();
			info.setID("province");
			info.setName(location.getProvince());
			message.add(info);
			
			info = new GPSInfo();
			info.setID("longitude");//经    度
			info.setName(location.getLongitude()+"");
			message.add(info);
			
			info = new GPSInfo();
			info.setID("latitude");//纬    度 
			info.setName(location.getLatitude()+"");
			message.add(info);
			
			info = new GPSInfo();
			info.setID("address");
			info.setName(location.getAddress()+"");
			message.add(info);
			
			info = new GPSInfo();
			info.setID("district");//区 
			info.setName(location.getDistrict()+"");
			message.add(info);
			
			info = new GPSInfo();
			info.setID("city");
			info.setName(location.getCity()+"");
			message.add(info);
			
		}
		if(message.size() > 0){
			return changeNotArrayDateToJson();
		}
		return null;
	}
	
	private static SimpleDateFormat sdf = null;
	public synchronized static String formatUTC(long l, String strPattern) {
		if (TextUtils.isEmpty(strPattern)) {
			strPattern = "yyyy-MM-dd HH:mm:ss";
		}
		if (sdf == null) {
			try {
				sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
			} catch (Throwable e) {
			}
		} else {
			sdf.applyPattern(strPattern);
		}
		return sdf == null ? "NULL" : sdf.format(l);
	}
	/**
	 * 转换为json数据
	 */
	private static String changeNotArrayDateToJson() {
		object=null;
		object2=null;
		object=new JSONObject();
		object2=new JSONObject();
		try {
			for (int i = 0; i < message.size(); i++) {
				object2.put(message.get(i).getID(),message.get(i).getName()); //把数据加入JSONObject对象即可，"userid"相当于map里面的key,1即为value的值。
			}
			object.put("message", "coordinate");
			object.put("content", object2);
			object.put("code","0");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		jsonString=null;
		jsonString = object.toString();//把JSONObject转换成json格式的字符串
		return jsonString;
	}
	/**
	 * 转换为json数据
	 */
	public  static String changeUUIDToJson(String uuid) {
		object=null;
		object2=null;
		object=new JSONObject();
		object2=new JSONObject();
		try {
			object2.put("registrationId",uuid); 
			object.put("message", "deviceId");
			object.put("content", object2);
			object.put("code","0");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		jsonString=null;
		jsonString = object.toString();//把JSONObject转换成json格式的字符串
		return jsonString;
	}
	/** 获取屏幕的宽度 */
	public final static int getWindowsWidth(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	/**
	 * dip转为 px
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 *  px 转为 dip
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	/**
	 * 隐藏键盘
	 *
	 * @param v
	 */
	public static void hideKeyboard(final View v) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
				}
			}
		}, 10);
	}
}
