package com.tg.coloursteward.info;

import java.io.Serializable;

public class CommunityResp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7713877371477063142L;
	private String name;// 小区名称
	private String cardnum;// 会员卡编号
	private String cid;// 用户编号
	private String householder;// 是否房主 0 不是 1 是
	private String bid;// 小区编号（对应卡卡兔的biz）
	private String thirdid;// 三方id
	private String address;// 小区地址
	private String colorid;// 彩之云编号
	private String memoname;// 当前用户所在小区备注名
	private String provinceid;//
	private int districtid;//
	private int cityid;// 城市编号

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCardnum() {
		return cardnum;
	}

	public void setCardnum(String cardnum) {
		this.cardnum = cardnum;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getHouseholder() {
		return householder;
	}

	public void setHouseholder(String householder) {
		this.householder = householder;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public String getThirdid() {
		return thirdid;
	}

	public void setThirdid(String thirdid) {
		this.thirdid = thirdid;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getColorid() {
		return colorid;
	}

	public void setColorid(String colorid) {
		this.colorid = colorid;
	}

	public String getMemoname() {
		return memoname;
	}

	public void setMemoname(String memoname) {
		this.memoname = memoname;
	}

	public String getProvinceid() {
		return provinceid;
	}

	public void setProvinceid(String provinceid) {
		this.provinceid = provinceid;
	}

	public int getDistrictid() {
		return districtid;
	}

	public void setDistrictid(int districtid) {
		this.districtid = districtid;
	}

	public int getCityid() {
		return cityid;
	}

	public void setCityid(int cityid) {
		this.cityid = cityid;
	}

}

