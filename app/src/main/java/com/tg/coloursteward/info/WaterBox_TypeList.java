package com.tg.coloursteward.info;

import java.io.Serializable;

public class WaterBox_TypeList implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6768609638681354620L;
	public String key;
	public String value;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "WaterBox_TypeList [key=" + key + ", value=" + value + "]";
	}
	
}
