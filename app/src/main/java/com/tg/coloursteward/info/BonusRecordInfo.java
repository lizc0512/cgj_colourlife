package com.tg.coloursteward.info;

import java.io.Serializable;
import java.util.ArrayList;

public class BonusRecordInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8062181465664480220L;
	/**
	 * 年月
	 */

	public String year;
	public String month;

	/**
	 * 基础奖金包
	 */
	public String baseBonus;
	/**
	 * 总奖金包
	 */
	public String totalBonus;

	/**
	 * 奖励总额
	 */
	public String increase;

	/**
	 * 扣款
	 */
	public String decrease;
	/**
	 * 片区总监调剂奖金包
	 */
	public String pcbfee;
	/**
	 * 超预算
	 */
	public String chaoys;

	public String id;
	// 集体系数之和
	public String totaljjbbase;
	// 个人系数
	public String jjbbase;
	
	public ArrayList<EffectEntityInfo> effectList = new ArrayList<EffectEntityInfo>();

	public ArrayList<PunishmentEntityInfo> punishmentList = new ArrayList<PunishmentEntityInfo>();
	

}
