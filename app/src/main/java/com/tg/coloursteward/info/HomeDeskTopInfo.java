package com.tg.coloursteward.info;

import java.io.Serializable;

public class HomeDeskTopInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2098547178773841247L;
	public int id;
	public int auth_type;
	public String icon=""; 
	public String owner_name=""; 
	public String owner_avatar=""; 
	public String modify_time=""; 
	public String title=""; 
	public String source_id=""; 
	public String client_code=""; 
	public String comefrom=""; 
	public String url=""; 
	public String msg_id="";
	public int notread;
}
