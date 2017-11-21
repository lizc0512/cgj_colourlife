package com.tg.coloursteward.info;

import java.io.Serializable;

/**
 * 授权列表
 * 
 * @author liqingjun
 * 
 */
public class AuthorizationListResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7511551751985963546L;
	private String type;// =1表示 申请，=2表示授权
	private String id;// 授权/授权编号
	private String bid;// 小区编号
	private String autype;// 授权列选，0临时，1限时，2永久
	private String usertype;// =0未处理，=1表示 永久，=2表示7天，=3表示1天，=5表示1年，=4表示2小时
	private String fromid;// 授权人编号
	private String fromname;// 授权人姓名
	private String toid;// 被授权人编号
	private String toname;// 授权人姓名
	private String creationtime;// 授权时间
	private String starttime;// 限时开始时间
	private String stoptime;// 限时截止时间
	private String times;// 最大开启次数（目前无意义）
	private String granttype;// 二次授权，0无，1可
	private String memo;// 备注
	private String cid;// 实际授权人编号
	private String isdeleted;// 是否删除，0未，1是
	private String name;// 小区名称
	private String applyid;// 关联申请编号
	private String mobile;// 电话

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public String getAutype() {
		return autype;
	}

	public void setAutype(String autype) {
		this.autype = autype;
	}

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public String getFromid() {
		return fromid;
	}

	public void setFromid(String fromid) {
		this.fromid = fromid;
	}

	public String getFromname() {
		return fromname;
	}

	public void setFromname(String fromname) {
		this.fromname = fromname;
	}

	public String getToid() {
		return toid;
	}

	public void setToid(String toid) {
		this.toid = toid;
	}

	public String getToname() {
		return toname;
	}

	public void setToname(String toname) {
		this.toname = toname;
	}

	public String getCreationtime() {
		return creationtime;
	}

	public void setCreationtime(String creationtime) {
		this.creationtime = creationtime;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getStoptime() {
		return stoptime;
	}

	public void setStoptime(String stoptime) {
		this.stoptime = stoptime;
	}

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}

	public String getGranttype() {
		return granttype;
	}

	public void setGranttype(String granttype) {
		this.granttype = granttype;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getIsdeleted() {
		return isdeleted;
	}

	public void setIsdeleted(String isdeleted) {
		this.isdeleted = isdeleted;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApplyid() {
		return applyid;
	}

	public void setApplyid(String applyid) {
		this.applyid = applyid;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


}
