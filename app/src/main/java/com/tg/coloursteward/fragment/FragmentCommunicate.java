package com.tg.coloursteward.fragment;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tg.coloursteward.ContactsActivity;
import com.tg.coloursteward.EmployeeDataActivity;
import com.tg.coloursteward.HomeContactOrgActivity;
import com.tg.coloursteward.HomeContactSearchActivity;
import com.tg.coloursteward.MainActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.CollectLinkmanAdapter;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.FamilyInfo;
import com.tg.coloursteward.info.HomeDeskTopInfo;
import com.tg.coloursteward.info.LinkManInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.inter.SingleClickListener;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.HomeRelativeLayout;
import com.tg.coloursteward.view.HomeRelativeLayout.NetRelativeRequestListener;
import com.tg.coloursteward.view.PullRefreshListView;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
/**
 * 通讯录
 * @author Administrator
 *
 */
public class FragmentCommunicate extends Fragment implements OnItemClickListener {
	private MainActivity mActivity;
	private static final int ISTREAD=1;
	private View mView;
	private TextView tvSection;
	private TextView tvOrgName;
	private PullRefreshListView pullListView;
	private HomeRelativeLayout rlOrganization;
	private RelativeLayout rlNulllinkman,rlDepartment,rlContacts;
	private ArrayList<LinkManInfo> linkManList = new ArrayList<LinkManInfo>();
	private CollectLinkmanAdapter adapter;
	private LinearLayout llSearch;
	private AuthAppService authAppService;
	private String accessToken;
	private String skincode;
	private String orgName;
	private String orgId;
	private  final int REQUESTPERMISSION = 110;
	private String LinkManListCache;
	private ArrayList<FamilyInfo> familyList = new ArrayList<FamilyInfo>();
	private SingleClickListener singleListener = new SingleClickListener() {
		
		@Override
		public void onSingleClick(View v) {
			Intent intent;
			FamilyInfo info;
			switch(v.getId()){
			case R.id.rl_organization://组织架构
				if(familyList.size()  > 0){
					if(skincode.equals("101")){//101 彩生活
						for (int i = 0; i < familyList.size(); i++){
							if(familyList.get(i).name.equals("彩生活服务集团")){
								info = new FamilyInfo();
								info.id = familyList.get(i).id;
								info.type = "org";
								info.name = familyList.get(i).name;
								intent = new Intent(mActivity,HomeContactOrgActivity.class);
								intent.putExtra(HomeContactOrgActivity.FAMILY_INFO, info);
								startActivity(intent);
							}
						}
					}else{
						info = new FamilyInfo();
						info.id = familyList.get(0).id;
						info.type = "org";
						info.name = familyList.get(0).name;
						intent = new Intent(mActivity,HomeContactOrgActivity.class);
						intent.putExtra(HomeContactOrgActivity.FAMILY_INFO, info);
						startActivity(intent);
					}

				}else{
					if(StringUtils.isNotEmpty(orgId) && StringUtils.isNotEmpty(orgName)){
						info = new FamilyInfo();
						info.id = orgId;
						info.type = "org";
						info.name = orgName;
						intent = new Intent(mActivity,HomeContactOrgActivity.class);
						intent.putExtra(HomeContactOrgActivity.FAMILY_INFO, info);
						startActivity(intent);
					}else{
						ToastFactory.showToast(mActivity,"正在获取组织架构，请稍后...");
					}
				}

				break;
			case R.id.rl_department://部门
				info = new FamilyInfo();
				info.id = UserInfo.orgId;
				info.type = "org";
				info.name = UserInfo.familyName;
				intent = new Intent(mActivity,HomeContactOrgActivity.class);
				intent.putExtra(HomeContactOrgActivity.FAMILY_INFO, info);
				startActivity(intent);
				break;
			case R.id.rl_contacts://手机通讯录
				if(ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
					//申请权限
					ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_CONTACTS}, REQUESTPERMISSION);
					ToastFactory.showToast(mActivity, "请允许权限");
				}else{
					startActivity(new Intent(mActivity,ContactsActivity.class));
				}

				break;
			case R.id.ll_search://搜索框
				startActivity(new Intent(mActivity, HomeContactSearchActivity.class));
				break;
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.fragment_communicate_layout,container, false);
		initView();
		return mView;
	}


	/**
	 * 初始化
	 */
	private void initView() {
		skincode = Tools.getStringValue(mActivity,Contants.storage.SKINCODE);
		orgName = Tools.getStringValue(mActivity,Contants.storage.ORGNAME);
		orgId = Tools.getStringValue(mActivity,Contants.storage.ORGID);
		rlNulllinkman = (RelativeLayout) mView.findViewById(R.id.rl_nulllinkman);//无联系人提示
		rlOrganization = (HomeRelativeLayout) mView.findViewById(R.id.rl_organization);//组织架构
		rlDepartment = (RelativeLayout) mView.findViewById(R.id.rl_department);//我的部门
		rlContacts = (RelativeLayout) mView.findViewById(R.id.rl_contacts);//手机通讯录
		tvSection = (TextView) mView.findViewById(R.id.tv_section);
		tvOrgName = (TextView) mView.findViewById(R.id.tv_orgName);
		tvSection.setText(UserInfo.familyName);
		if(StringUtils.isNotEmpty(orgName)){
			tvOrgName.setText(orgName);
		}
		/**
		 * 搜索框
		 */
		llSearch = (LinearLayout) mView.findViewById(R.id.ll_search);
		llSearch.setOnClickListener(singleListener);
		rlOrganization.setOnClickListener(singleListener);
		rlDepartment.setOnClickListener(singleListener);
		rlContacts.setOnClickListener(singleListener);

		rlOrganization.setNetworkRequestListener(new NetRelativeRequestListener() {

			@Override
			public void onSuccess(HomeRelativeLayout magLearLayout, Message msg,String response) {
				JSONArray jsonString = HttpTools.getContentJsonArray(response);
				if (jsonString != null) {
					ResponseData data = HttpTools.getResponseContent(jsonString);
					FamilyInfo item;
					for (int i = 0; i < data.length; i++) {
						item = new FamilyInfo();
						item.id = data.getString(i, "orgUuid");
						item.name = data.getString(i, "name");
						familyList.add(item);
					}
					if(skincode.equals("101")){//101 彩生活
						for (int i = 0; i < familyList.size(); i++){
							if(familyList.get(i).name.equals("彩生活服务集团")){
								tvOrgName.setText(familyList.get(i).name);
								Tools.saveStringValue(mActivity,Contants.storage.ORGNAME,familyList.get(i).name);
								Tools.saveStringValue(mActivity,Contants.storage.ORGID,familyList.get(i).id);
							}
						}
					}else {
						if(StringUtils.isNotEmpty(familyList.get(0).name)){
							tvOrgName.setText(familyList.get(0).name);
							Tools.saveStringValue(mActivity,Contants.storage.ORGNAME,familyList.get(0).name);
							Tools.saveStringValue(mActivity,Contants.storage.ORGID,familyList.get(0).id);
						}
					}

				}
			}

			@Override
			public void onRequest(MessageHandler msgHand) {
				RequestConfig config = new RequestConfig(mActivity,0);
				config.handler = msgHand.getHandler();
				RequestParams params = new RequestParams();
				params.put("token",accessToken);
				params.put("parentId", "0");//0：取根目录
				params.put("corpId", Tools.getStringValue(mActivity,Contants.storage.CORPID));
				HttpTools.httpGet(Contants.URl.URL_ICETEST, "/orgms/org/batch",config, params);
			}
		});
		pullListView = (PullRefreshListView) mView.findViewById(R.id.pull_listview);
		pullListView.setEnableMoreButton(false);
		pullListView.setOnItemClickListener(this);
		pullListView.setDividerHeight(0);
		//读取本地缓存列表
		getCacheList();
		pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
			
			@Override
			public void refreshData(PullRefreshListView t, boolean isLoadMore,
					Message msg, String response) {
				// TODO Auto-generated method stub
				JSONArray jsonString = HttpTools.getContentJsonArray(response);
				if (jsonString != null) {
					Tools.saveLinkManList(mActivity,response);
					ResponseData data = HttpTools.getResponseContent(jsonString);
					if(jsonString.length() > 0){
						rlNulllinkman.setVisibility(View.GONE);
					}
					LinkManInfo item;
					for (int i = 0; i < data.length; i++) {
						item = new LinkManInfo();
						item.username = data.getString(i, "username");
						item.realname = data.getString(i, "realname");
						item.icon = data.getString(i, "avatar");
						item.job_name = data.getString(i,"jobName");
						item.orgName = data.getString(i, "orgName");
						linkManList.add(item);
					}
				}
			}
			
			@Override
			public void onLoadingMore(PullRefreshListView t, Handler hand, int pageIndex) {
				// TODO Auto-generated method stub
				RequestConfig config = new RequestConfig(mActivity,PullRefreshListView.HTTP_MORE_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				params.put("uid", UserInfo.employeeAccount);
				HttpTools.httpGet(Contants.URl.URL_ICETEST, "/phonebook/frequentContacts",config, params);
				
			}
			
			@Override
			public void onLoading(PullRefreshListView t, Handler hand) {
				// TODO Auto-generated method stub
				RequestConfig config = new RequestConfig(mActivity,PullRefreshListView.HTTP_FRESH_CODE);
				config.handler = hand;
				RequestParams params = new RequestParams();
				params.put("uid", UserInfo.employeeAccount);
				HttpTools.httpGet(Contants.URl.URL_ICETEST, "/phonebook/frequentContacts",config, params);
				
			}
		});
		pullListView.setAdapter(adapter);
		pullListView.performLoading();

		Date dt = new Date();
		Long time = dt.getTime();
		String expireTime = Tools.getStringValue(mActivity,Contants.storage.APPAUTHTIME);
		accessToken = Tools.getStringValue(mActivity,Contants.storage.APPAUTH);
		/**
		 * 获取组织架构根目录
		 */
		if(StringUtils.isNotEmpty(expireTime)){
			if(Long.parseLong(expireTime) <= time) {//token过期
				getAuthAppInfo();
			}else{
				requestData();
			}
		}else{
			getAuthAppInfo();
		}

	}
	/**
	 * 获取首页缓存列表
	 */
	private void getCacheList() {
		LinkManListCache = Tools.getLinkManList(mActivity);
		if(StringUtils.isNotEmpty(LinkManListCache)){
			JSONArray jsonString = HttpTools.getContentJsonArray(LinkManListCache);
			if (jsonString != null) {
				ResponseData data = HttpTools.getResponseContent(jsonString);
				if(jsonString.length() > 0){
					rlNulllinkman.setVisibility(View.GONE);
				}
				LinkManInfo item;
				for (int i = 0; i < data.length; i++) {
					item = new LinkManInfo();
					item.username = data.getString(i, "username");
					item.realname = data.getString(i, "realname");
					item.icon = data.getString(i, "avatar");
					item.job_name = data.getString(i,"jobName");
					item.orgName = data.getString(i, "orgName");
					linkManList.add(item);
				}
			}
			pullListView.setAdapter(adapter);
		}
	}

	/**
	 * 获取token
	 * sectet
	 */
	private void getAuthAppInfo() {
		if(authAppService == null){
			authAppService = new AuthAppService(mActivity);
		}
		authAppService.getAppAuth(new GetTwoRecordListener<String, String>() {
			@Override
			public void onFinish(String jsonString, String data2,String data3) {
				int code = HttpTools.getCode(jsonString);
				if(code == 0){
					JSONObject content = HttpTools.getContentJSONObject(jsonString);
					if(content.length() > 0){
						try {
							accessToken = content.getString("accessToken");
							String expireTime = content.getString("expireTime");
							Tools.saveStringValue(mActivity,Contants.storage.APPAUTH,accessToken);
							Tools.saveStringValue(mActivity,Contants.storage.APPAUTHTIME,expireTime);
							requestData();
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

				}
			}

			@Override
			public void onFailed(String Message) {

			}
		});
	}
	/**
	 * 开启加载数据
	 */
	private void requestData() {
		rlOrganization.loaddingData();
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = (MainActivity)activity;
		adapter=new CollectLinkmanAdapter(mActivity, linkManList);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case REQUESTPERMISSION:
				if (grantResults.length > 0&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					startActivity(new Intent(mActivity,ContactsActivity.class));
				} else {

				}
				break;
		}
	}

	/**
	 * 点击ListView item
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		LinkManInfo item  = linkManList.get(position);
		Intent intent = new Intent(mActivity,EmployeeDataActivity.class);
		intent.putExtra(EmployeeDataActivity.CONTACTS_ID,item.username);
		startActivityForResult(intent,ISTREAD);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==ISTREAD){
			pullListView.performLoading();
		}
	}
}
