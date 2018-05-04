package com.tg.coloursteward.info;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/11.
 */

public class AccountExchangeDetailInfo implements Serializable{
    private static final long serialVersionUID = 8390170169959000783L;
    public int result_id;
    public int split_type;
    public String general_uuid ="";
    public String general_name ="";
    public String tag_uuid ="";
    public String tag_name ="";
    public String community_uuid ="";
    public String community_name ="";
    public String split_target ="";
    public String finance_cano ="";
    public String split_account_amount ="";
    public String out_trade_no ="";
    public String orderno ="";
    public String time_at ="";
}
