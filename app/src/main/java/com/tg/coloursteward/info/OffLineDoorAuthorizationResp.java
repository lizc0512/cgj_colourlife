package com.tg.coloursteward.info;

import java.io.Serializable;

/**
 * 脱机门禁授权实体
 * 
 * @author chengyun
 * 
 */
public class OffLineDoorAuthorizationResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5596415120727718474L;
	private String doorid;// 门ID
	private String qrcode;// 门编码
	private String doorcode;// 厂商编号
	private String stoptime;// 到期时间 0表示永久授权
	private String doortype;// 门禁类型0普通门禁，1脱机门禁，2脱机梯控
	private String extra;// / 梯控，楼层号 # 分隔
	private String name;// 门禁地址，全名
	private String wificode;// 蓝牙的mac地址
	private String wifienable;// 1表示普通门禁支持蓝牙
	private String address;

	public String getWifienable() {
		return wifienable;
	}

	public void setWifienable(String wifienable) {
		this.wifienable = wifienable;
	}

	public String getWificode() {
		return wificode;
	}

	public void setWificode(String wificode) {
		this.wificode = wificode;
	}

	public String getStoptime() {
		return stoptime;
	}

	public void setStoptime(String stoptime) {
		this.stoptime = stoptime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDoorid() {
		return doorid;
	}

	public void setDoorid(String doorid) {
		this.doorid = doorid;
	}

	public String getQrcode() {
		return qrcode;
	}

	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}

	public String getDoorcode() {
		return doorcode;
	}

	public void setDoorcode(String doorcode) {
		this.doorcode = doorcode;
	}

	public String getDoortype() {
		return doortype;
	}

	public void setDoortype(String doortype) {
		this.doortype = doortype;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
