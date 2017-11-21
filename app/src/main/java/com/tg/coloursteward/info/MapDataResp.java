package com.tg.coloursteward.info;

import java.io.Serializable;

public class MapDataResp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7904892322283622195L;
	public String year;
	public String communityCount;
	public String appCount;// App安装数
	public String complainCount;// 业主投诉数
	public String normalFee;// 实收
	public String receivedFee;// 应收
	public String feeRate;// 收缴率
	public String satisfaction;// 满意度
	public String parkingCount;// 停车位
	public String floorArea;// 物管面积
	public String qrcode2open;// 二维码开门次数
	public String parkingFee;// 停车场累计收费总额
	public String cameraStatus;// 海康摄像头在线情况
	public String guardStatus;// 门禁在线情况
	public String barrierStatus;// 格美特道闸系统在线情况
	public String join_smallarea_num;// 上线小区
}
