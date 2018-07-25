package com.youmai.hxsdk.router;

/**
 * 作者：create by YW
 * 日期：2018.03.26 10:18
 * 描述：路由分组
 * eg:/com/Activity com:代表组的标识 Activity代表类的标识
 */

public class APath {

    /**
     * 本人资料
     */

    public static final String USER_INFO_ACT = "/color/userInfo";

    /**
     * 员工名片
     */
    public static final String EMPLOYEE_DATA_ACT = "/color/employee";


    /**
     * 好友
     */
    public static final String BUDDY_FRIEND = "/color/friend";


    /**
     * 非好友
     */
    public static final String BUDDY_NOT_FRIEND = "/color/notFriend";


    /**
     * 消息转发
     */
    public static final String MSG_FORWARD = "/module/forward";


    /**
     * 重新登录
     */
    public static final String RE_LOGIN = "/login/reLogin";


}
