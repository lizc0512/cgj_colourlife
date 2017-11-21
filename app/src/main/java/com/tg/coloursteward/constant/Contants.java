package com.tg.coloursteward.constant;

import android.os.Environment;

public interface Contants {
	/**
	 * 下载安装包保存目录
	 */
	public static String downloadDir = "app/download/";
	public interface First{
		public static  String IS_FIRST_TIME_STARTING = "firt_time";
	}
	public interface URl{
		/**
		 *正式地址
		 */
	final String URL_ICETEST="http://iceapi.colourlife.com:8081/v1";
	/**
	 * 测试地址
	 */
	//final String URL_ICETEST="http://openapi.test.colourlife.com/v1";
	//final String URL_ICETEST="https://openapi.colourlife.com/v1";


	final String SeaHealthApiUrl="http://183.136.184.209:80/api/serviceApi/userauth/gettoken";//海康api
	final String CZY_BINDCUSTOMER="http://www.colourlife.com/bindCustomer";//绑定彩之云
	final String URL_H5_LEAVE="http://eqd.backyard.colourlife.com/cailife/leave/index?";//请假
	final String HUXIN_H5_HELP="http://www.colourlife.com/Introduction/CgjCall";//呼信(帮助)
	final String HEAD_ICON_URL="http://iceapi.colourlife.com:8686/";//头像
	final String Yj_Url="http://emailsso.colourlife.net/login.aspx";//邮件
	final String Sp_Url="http://spsso.colourlife.net/login.aspx/";//审批
	final String SeaHealthApkUrl="http://spsso.colourlife.net/login.aspx/";//海康客户端下载
	}
	public interface APP{
		final String SeaHealthPackageName = "com.hikvi.ivms8700.hd.colorfulLife";
	}
	
	public interface EMPLOYEE_LOGIN{
		final String key = "EmployeeLogin_key";
		final String secret = "EmployeeLogin_secret";
	}
	public interface LOGO{
		 /*
	     * 门禁模块
	     */
	    public static final int DOOREDIT_LOGCLICK = 20;

	    public static final int DOOREDIT_MOVE = 21;

	    public static final int DOOREDIT_ADD = 22;

	    public static final int DOOREDIT_ADD_REFRESH = 23;

	    public static final int DOOREDIT_DELETE = 24;

	    public static final int DOOREDIT_DOOR_NAME = 25;

		/**
		 * 首页消息更新
		 */
		public static final int CLEAR_HOMELIST = 30;

		public static final int UPDATE_HOMELIST = 35;
	}
	
	public interface PARAMETER{
		/**
		 * 红包余额
		 */
		final String BALANCE = "balance";
		/**
		 * 给同事发红包（colleague）/转账到彩之云（czy）
		 */
		public static final String TRANSFERTO = "transferTo";
		/**
		 * OaID
		 * 
		 */
		public static final String USERID = "OaID";
		/**
		 * mobile
		 * 
		 */
		public static final String MOBILE = "mobile";
		/**
		 * 提现金额
		 */
		public static final String WITHDRAW_AMOUNT = "withdrawAmount";
		/**
		 * 发红包捎一句话
		 */
		public static final String TRANSFERNOTE = "transferNote";
		/**
		 * 彩之云小区
		 */
		public static final String COMMUNITY = "community";
		/**
		 * 持卡人
		 */
		public static final String CARDHOLDER = "cardholder";

		/**
		 * 银行名称
		 */
		public static final String BANK_NAME = "bankName";

		/**
		 * 银行卡号
		 */
		public static final String CARD_NUMBER = "cardNumber";
		/**
		 * 银行ID号
		 */
		public static final String BANK_ID = "bankId";

		/**
		 * 用户的银行卡ID
		 */
		public static final String BANK_CARD_ID = "bankCardId";
		public static final String CALLBACKSCANRESULT = "CallBackScanResult";
	}
	public interface  Html5{
		public static final String CALLBACKDeviceID = "CALLBACKDeviceID";
		public static final String YJ = "http://mail.oa.colourlife.com:40060/login";
		public static final String SP = "http://spsso.colourlife.net/login.aspx";
		public static final String CASE = "http://iceapi.colourlife.com:4600/home";
		public static final String QIANDAO = "http://eqd.oa.colourlife.com/cailife/sign/main";
	}
	public interface storage{
		public static final String TICKET = "ticket";
		public static final String ACCOUNT = "account";
		public static final String AREAHOME = "area_home";
		public static final String STOCKHOME = "stock_home";
		public static final String TICKETHOME = "ticket_home";
		public static final String COMMUNITYHOME = "community_home";
		public static final String PERFORMANCEHOME = "performance_home";
		public static final String ACCOUNTHOME = "account_home";
		public static final String SKINCODE = "skin_code";//皮肤包
		public static final String CORPID = "corp_id";//租户Id
		public static final String APPAUTH = "app_auth";//appAuth
		public static final String APPAUTHTIME = "app_auth_time";//appAuthtime
		public static final String APPAUTH_1 = "app_aut_1";//appAuth  1.0
		public static final String APPAUTHTIME_1 = "app_auth_time_1";//appAuthtime  1.0
		public static final String ORGNAME = "org_name";//OrgName
		public static final String ORGID = "org_id";//Orgid
		public static final String ADVLIST = "advlist";//轮播图
		public static final String ALIAS = "alias";//ALIAS
		public static final String Tags = "tags";//Tags
	}
	public interface DOWN{
		 // 下载文件保存路径
	    String DOWNLOAD_DIRECT = Environment.getExternalStorageDirectory()
	            + "/colourlife/download/";
	    String LOG_DIRECT = Environment.getExternalStorageDirectory()
	            + "/colourlife/log/";
	}
	public interface MAP{
		 // 下载文件保存路径
	    String ADDRESS = "map_address";
	}
	
	
}
