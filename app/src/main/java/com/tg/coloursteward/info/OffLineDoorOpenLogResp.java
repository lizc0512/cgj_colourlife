package com.tg.coloursteward.info;

import java.io.Serializable;

/**
 * 脱机门禁开门日志实体
 * 
 * @author chengyun
 * 
 */
public class OffLineDoorOpenLogResp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2025053258141944422L;
	private String doorid;// 门id
	private String opentime;// 开门时间
	private String actiontime;// 连接时间
	private String responsetime;// 开门时间
	private String sendtime;// 暂时保留，留空
	private String result;// 开门结果，0 成功，1失败
	private String timer;// 暂时保留，留空
	private String server;// 暂时保留，留空

	public String getDoorid() {
		return doorid;
	}

	public void setDoorid(String doorid) {
		this.doorid = doorid;
	}

	public String getOpentime() {
		return opentime;
	}

	public void setOpentime(String opentime) {
		this.opentime = opentime;
	}

	public String getActiontime() {
		return actiontime;
	}

	public void setActiontime(String actiontime) {
		this.actiontime = actiontime;
	}

	public String getResponsetime() {
		return responsetime;
	}

	public void setResponsetime(String responsetime) {
		this.responsetime = responsetime;
	}

	public String getSendtime() {
		return sendtime;
	}

	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getTimer() {
		return timer;
	}

	public void setTimer(String timer) {
		this.timer = timer;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

}
