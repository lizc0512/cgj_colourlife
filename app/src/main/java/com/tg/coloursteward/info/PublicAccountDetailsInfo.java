package com.tg.coloursteward.info;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/6.
 */

public class PublicAccountDetailsInfo implements Serializable{
    private static final long serialVersionUID = 3982082719290718080L;
    public String tno;
    public String transtype;
    public String typeid;
    public String thirdno;
    public String orderno;
    public String orgmoney;
    public String destmoney;
    public String creationtime;
    public String status;
    public String detail;
    public String content;
    public String orgplatform;
    public String destplatform;
    public String orgbiz;
    public String destbiz;
    public String orgclient;
    public String destclient;
    public String orgcccount;
    public String destcccount;
    public String type;//0：转入1：转出
}
