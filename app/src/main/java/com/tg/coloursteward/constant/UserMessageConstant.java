package com.tg.coloursteward.constant;


public class UserMessageConstant {
    public static final int USER_MESSAGE_BASE = 1000;
    public static final int SIGN_IN_SUCCESS = USER_MESSAGE_BASE + 0; //登录成功
    public static final int SIGN_IN_FAIL = USER_MESSAGE_BASE + 100;  //登录失败
    public static final int APP_INSTANCE_MSG = USER_MESSAGE_BASE + 102;//首页各大应用获取app的推送消息
    public static final int GUANGCAI_PAY_MSG = USER_MESSAGE_BASE + 103;//光彩支付的
    public static final int WEIXIN_PAY_MSG = USER_MESSAGE_BASE + 104;//微信支付的
    public static final int GET_SINGLE_FRIINFOR = USER_MESSAGE_BASE + 105;//好友同意申请的回调
    public static final int GET_APPLY_NUMBER = USER_MESSAGE_BASE + 106;//好友同意的记录
    public static final int COMMUNITY_MANAGER_NOTICE = USER_MESSAGE_BASE + 108;//社群审核的通知
    public static final int NET_CONN_CHANGE = USER_MESSAGE_BASE + 109;//网络改变
    public static final int CHANGE_DIFF_LANG = USER_MESSAGE_BASE + 110;// 国际化切换语言
    public static final int REGISTER_TYPE_FIAL = USER_MESSAGE_BASE + 1;//注册失败
    public static final int CREATE_FEED = USER_MESSAGE_BASE + 13;
    public static final int CHANGE_COMMUNITY = USER_MESSAGE_BASE + 16;
    public static final int DELETE_FEED = USER_MESSAGE_BASE + 18;
    public static final int DELETE_COMMENT = USER_MESSAGE_BASE + 19;
    public static final int COMMUNITY_REFRESH = USER_MESSAGE_BASE + 20;
    public static final int LOGOUT = USER_MESSAGE_BASE + 24;
    public static final int SHAREDOKMESSAGE = USER_MESSAGE_BASE + 56;
    public static final int SUREBTNCHECKET = USER_MESSAGE_BASE + 75;
    public static final int SQUEEZE_OUT = USER_MESSAGE_BASE + 76;
    public static final int WEB_OUT = USER_MESSAGE_BASE + 77;
    public static final int AUDIT_PASS_OUT = USER_MESSAGE_BASE + 78;
    public static final int UPLOAD_PAGE_TIME = USER_MESSAGE_BASE + 79;//上传页面的停留时间
    public static final int UPDATE_APP_CLICK = USER_MESSAGE_BASE + 80;//更新应用的点击事件
    public static final int BLUETOOTH_OPEN_DOOR = USER_MESSAGE_BASE + 81;//蓝牙开门成功的提示
    public static final int BLUETOOTH_CLOSE_DIALOG = USER_MESSAGE_BASE + 82;//蓝牙列表页开门成功关闭首页的
    public static final int UPDATE_DOOR = USER_MESSAGE_BASE + 83;//更新门禁
    public static final int POINT_SUCCESS_RETURN = USER_MESSAGE_BASE + 84;//积分或饭票赠送成功返回首页
    public static final int POINT_CONTINUE_GIVEN= USER_MESSAGE_BASE + 85;//积分或饭票赠送成功返回首页
    public static final int POINT_INPUT_PAYPAWD = USER_MESSAGE_BASE + 86;//输入支付密码
    public static final int POINT_SET_PAYPAWD= USER_MESSAGE_BASE + 87;//设置支付密码成功
    public static final int POINT_CHANGE_PAYPAWD= USER_MESSAGE_BASE + 88;//修改或忘记密码成功


}
