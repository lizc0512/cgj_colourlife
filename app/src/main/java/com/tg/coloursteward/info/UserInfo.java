package com.tg.coloursteward.info;


public class UserInfo {
    /**
     * 账号  （手机号）
     */
    public static String uid = "";
    public static String employeeAccount = "";
    public static String color_token = "";
    public static String job_uuid = "";
    public static String sex = "";
    public static String realname = "";
    public static String password = "";
    public static String cashierpassword = "";
    public static String jobName = "";
    public static String familyName = "";//组织架构名称
    public static String orgId = "";//组织架构ID
    public static String infoorgId = "";//组织架构ID


    public static String corp_id = "";
    public static String salary_level = "";
    public static int is_deleted = 0;
    public static int special = 0;
    public static String email = "";
    public static String mobile = "";
    public static String init_mobile = "";
    public static int czy_id = 0;

    public static void initClear() {
        UserInfo.uid = "";
        UserInfo.employeeAccount = "";
        UserInfo.color_token = "";
        UserInfo.job_uuid = "";
        UserInfo.sex = "";
        UserInfo.realname = "";
        UserInfo.password = "";
        UserInfo.cashierpassword = "";
        UserInfo.jobName = "";
        UserInfo.familyName = "";
        UserInfo.orgId = "";//组织架构ID
        UserInfo.corp_id = "";
        UserInfo.salary_level = "";
        UserInfo.is_deleted = 0;
        UserInfo.special = 0;
        UserInfo.email = "";
        UserInfo.mobile = "";
        UserInfo.czy_id = 0;
    }

}
