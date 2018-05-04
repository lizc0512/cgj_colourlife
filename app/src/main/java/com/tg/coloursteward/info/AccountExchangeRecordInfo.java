package com.tg.coloursteward.info;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/27.
 */

public class AccountExchangeRecordInfo implements Serializable{
    private static final long serialVersionUID = 3088089251507292564L;
    public int id;
    public int split_type;
    public int finance_atid;
    public int type;
    public int state;
    public int arrival_atid;
    public String general_uuid ="";
    public String business_uuid ="";
    public String split_target ="";
    public String finance_pano ="";
    public String finance_cano ="";
    public String amount ="";
    public String create_at ="";
    public String update_at ="";
    public String result ="";
    public String arrival_pano ="";
    public String arrival_cano ="";
    public String arrival_account ="";
    public String finance_no ="";
    public String orderno ="";
    public String app_name ="";
}
