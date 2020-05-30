package com.tg.coloursteward.info;

import java.io.Serializable;

public class FamilyInfo implements Serializable {

	private static final long serialVersionUID = 5608795672002147086L;
	/**
	 * 
	 */
	public String id = "";
	public String type = "";
	public String name ="";//姓名
	public String username ="";//oa
	public String avatar ="";//头像
	public String mobile ="";
	public String email ="";
	public String sex ="";
	public String orgName ="";//部门
	public String jobName ="";//岗位
	public String sortLetters ="";//显示数据拼音的首字母
	public String orgType ="";//自定义orgType（数据看板处可用，其余无效）
}
