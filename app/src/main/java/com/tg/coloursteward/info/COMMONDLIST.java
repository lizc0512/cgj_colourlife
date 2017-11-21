package com.tg.coloursteward.info;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class COMMONDLIST {
	  public String doorid; //门禁编号

	  public String qrcode; //门禁二维码

	  public String doortype; //门禁类型

	  public String conntype; //连接类型

	  public String name; //门禁名称

	  public String position; //门禁位置

	  public String type; //小区类型 1住宅 2写字楼

	  public void fromJson(JSONObject jsonObject) throws JSONException
	  {
	    if( null == jsonObject ) {
	      return ;
	    }

	    JSONArray subItemArray = new JSONArray();

	    this.doorid = jsonObject.optString("doorid");
	    this.qrcode = jsonObject.optString("qrcode");
	    this.doortype = jsonObject.optString("doortype");
	    this.conntype = jsonObject.optString("conntype");
	    this.name = jsonObject.optString("name");
	    this.position = jsonObject.optString("position");
	    this.type = jsonObject.optString("type");

	    return;
	  }

	  public JSONObject toJson() throws JSONException 
	  {
	    JSONObject localItemObject = new JSONObject();
	    JSONArray itemJSONArray = new JSONArray();
	    localItemObject.put("doorid", doorid);
	    localItemObject.put("qrcode", qrcode);
	    localItemObject.put("doortype", doortype);
	    localItemObject.put("conntype", conntype);
	    localItemObject.put("name", name);
	    localItemObject.put("position", position);
	    localItemObject.put("type", type);
	    return localItemObject;
	  }
}
