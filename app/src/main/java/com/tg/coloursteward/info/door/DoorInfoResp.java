package com.tg.coloursteward.info.door;

import java.io.Serializable;

/**
 * 门信息
 * 
 * @author liqingjun
 * 
 */
public class DoorInfoResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7430394672274344923L;
	private String id;// 门禁编号;
	private String qrcode;// 二维码;
	private String bid;// 小区编号;
	private int unitid;// 所在单元编号;
	private String name;// 名称;
	private String status;// 状态，1有效，0无效;
	private String address;// 地址;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQrcode() {
		return qrcode;
	}

	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public int getUnitid() {
		return unitid;
	}

	public void setUnitid(int unitid) {
		this.unitid = unitid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
