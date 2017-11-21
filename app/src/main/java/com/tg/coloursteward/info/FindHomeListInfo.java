package com.tg.coloursteward.info;

import java.io.Serializable;

/**
 * 搜索首页消息列表
 */

public class FindHomeListInfo  implements Serializable{
    private static final long serialVersionUID = -5740663543481410975L;
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
}
