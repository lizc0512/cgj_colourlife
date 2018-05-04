package com.tg.coloursteward;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tg.coloursteward.adapter.HomeContactSearchAdapter;
import com.tg.coloursteward.adapter.HomeHomeListAdapter;
import com.tg.coloursteward.adapter.SearchManageMentAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.FindContactInfo;
import com.tg.coloursteward.info.FindHomeListInfo;
import com.tg.coloursteward.info.GridViewInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.util.AuthTimeUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
/**
 * 通讯录搜索
 * @author Administrator
 *
 */
public class HomeContactSearchActivity extends BaseActivity {
	// 返回取消
	private Button btnBack;
	// 搜索結果
	private ArrayList<FindContactInfo> contactDatas ;
	private ArrayList<FindHomeListInfo> homelistDatas ;
	private ArrayList<GridViewInfo> managementDatas ;
	private HomeContactSearchAdapter contactAdapter;
	private HomeHomeListAdapter homeListAdapter;
	private SearchManageMentAdapter managementAdapter;
	private ListView listview_homelist;
	private ListView listview_contact;
	private ListView listview_manage;
	private EditText et_content;
	private View division;
	private View division_manage;
    private int contactheadheight = 0;
    private int contactfootheight = 0;
    private int homelistheadheight = 0;
    private int homelistfootheight = 0;
    private int managementheadheight = 0;
    private int managementfootheight = 0;
	private Intent intent;
	private String keyword;
	private HomeService homeService;

	private String skincode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getPopup(true);
		setContentView(R.layout.activity_home_contact_search);
		contactDatas = new ArrayList<FindContactInfo>();//消息
		homelistDatas = new ArrayList<FindHomeListInfo>();//联系人
		managementDatas = new ArrayList<GridViewInfo>();//应用
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		skincode = Tools.getStringValue(HomeContactSearchActivity.this,Contants.storage.SKINCODE);
		// 返回取消
		btnBack = (Button) findViewById(R.id.btnBack);
		division = findViewById(R.id.division);
		division_manage = findViewById(R.id.division_manage);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				keyword = et_content.getText().toString();
				if(StringUtils.isNotEmpty(keyword)){
					getHomeListResult(keyword);
					getContactSearchResult(keyword);
					getManageMentResult(keyword);
				}else{
					ToastFactory.showToast(HomeContactSearchActivity.this,"搜索内容不能为空");
				}
			}
		});
		et_content = (EditText) findViewById(R.id.edit_search);
		et_content.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				//getSearchResult(s.toString());//请求接口进行搜索
			}
		});
		et_content.setOnEditorActionListener(new OnEditorActionListener() {//触发键盘

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					keyword= et_content.getText().toString();
					getHomeListResult(keyword);
					getContactSearchResult(keyword);
					getManageMentResult(keyword);
				}
				return false;
			}

		});
		// 搜索結果
		/**
		 * 首页消息列表
		 */
		listview_homelist = (ListView) findViewById(R.id.listview_homelist);//首页消息
		homeListAdapter = new HomeHomeListAdapter(HomeContactSearchActivity.this, homelistDatas);
		/**
		 * list添加头部
		 */
		addHeadHomeList();
		listview_homelist.setAdapter(homeListAdapter);
		listview_homelist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				if((int) parent.getAdapter().getItemId(position) != -1){
					FindHomeListInfo info = homelistDatas.get((int) parent.getAdapter().getItemId(position));
					AuthTimeUtils mAuthTimeUtils  = new AuthTimeUtils();
					mAuthTimeUtils.IsAuthTime(HomeContactSearchActivity.this,info.url, info.client_code, String.valueOf(info.auth_type), info.client_code,"");
				}else{
					return;
				}
			}
		});
		/**
		 * 通讯录
		 */
		listview_contact = (ListView) findViewById(R.id.listview_contact);//通讯录
		contactAdapter = new HomeContactSearchAdapter(HomeContactSearchActivity.this, contactDatas);
		/**
		 * list添加头部
		 */
		addHeadContact();
		listview_contact.setAdapter(contactAdapter);
		listview_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if((int) parent.getAdapter().getItemId(position) != -1){
					FindContactInfo item = contactDatas.get((int) parent.getAdapter().getItemId(position));
					Intent intent = new Intent(HomeContactSearchActivity.this,EmployeeDataActivity.class);
					intent.putExtra(EmployeeDataActivity.CONTACTS_ID,item.username);
					startActivity(intent);
				}else{
					return;
				}
			}
		});
		/**
		 * 应用
		 */
		listview_manage = (ListView) findViewById(R.id.listview_manage);
		managementAdapter = new SearchManageMentAdapter(HomeContactSearchActivity.this,managementDatas);
		/**
		 * list添加头部
		 */
		addHeadManageMent();
		listview_manage.setAdapter(managementAdapter);
		listview_manage.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				if((int) parent.getAdapter().getItemId(position) != -1){
					GridViewInfo info = managementDatas.get((int) parent.getAdapter().getItemId(position));
					AuthTimeUtils mAuthTimeUtils  = new AuthTimeUtils();
					mAuthTimeUtils.IsAuthTime(HomeContactSearchActivity.this,info.sso, info.clientCode, info.oauthType, info.developerCode,"");
				}else{
					return;
				}
			}
		});
	}

	/**
	 * 首页消息列表头部
	 */
	private void addHeadHomeList(){
		final LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View headView = inflater.inflate(R.layout.contact_search_item, null);
		TextView tvTitle = (TextView) headView.findViewById(R.id.tv_head_title);
		tvTitle.setText("消息");
        View footView = inflater.inflate(R.layout.contact_search_item_foot, null);
		LinearLayout ll_foot = (LinearLayout) footView.findViewById(R.id.ll_foot);
		ll_foot.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(HomeContactSearchActivity.this,SearchHomeListActivity.class);
				intent.putExtra(SearchHomeListActivity.KEYWORD,keyword);
				startActivity(intent);
			}
		});
        TextView tvFoot = (TextView) footView.findViewById(R.id.tv_head_foot);
        tvFoot.setText("查看更多消息");
		listview_homelist.addHeaderView(headView);
		listview_homelist.addFooterView(footView);
        headView.measure(0, 0);
        homelistheadheight=headView.getMeasuredHeight();
        footView.measure(0, 0);
        homelistfootheight = footView.getMeasuredHeight();
	}

	/**
	 *通讯录列表头部
	 */
	private void addHeadContact(){
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View headView = inflater.inflate(R.layout.contact_search_item, null);
		TextView tvTitle = (TextView) headView.findViewById(R.id.tv_head_title);
		tvTitle.setText("联系人");
        View footView = inflater.inflate(R.layout.contact_search_item_foot, null);
        LinearLayout ll_foot = (LinearLayout) footView.findViewById(R.id.ll_foot);
        ll_foot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				intent = new Intent(HomeContactSearchActivity.this,SearchContactListActivity.class);
				intent.putExtra(SearchContactListActivity.KEYWORD,keyword);
				startActivity(intent);
            }
        });
        TextView tvFoot = (TextView) footView.findViewById(R.id.tv_head_foot);
        tvFoot.setText("查看更多联系人");
		listview_contact.addHeaderView(headView);
		listview_contact.addFooterView(footView);
        headView.measure(0, 0);
        contactheadheight=headView.getMeasuredHeight();
        footView.measure(0, 0);
        contactfootheight=footView.getMeasuredHeight();
	}
	/**
	 *应用列表头部
	 */
	private void addHeadManageMent(){
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View headView = inflater.inflate(R.layout.contact_search_item, null);
		TextView tvTitle = (TextView) headView.findViewById(R.id.tv_head_title);
		tvTitle.setText("应用");
        View footView = inflater.inflate(R.layout.contact_search_item_foot, null);
        LinearLayout ll_foot = (LinearLayout) footView.findViewById(R.id.ll_foot);
        ll_foot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				/*intent = new Intent(HomeContactSearchActivity.this,SearchManageMentListActivity.class);
				intent.putExtra(SearchManageMentListActivity.KEYWORD,keyword);
				startActivity(intent);*/
            }
        });
        TextView tvFoot = (TextView) footView.findViewById(R.id.tv_head_foot);
        tvFoot.setText("查看更多应用");
		listview_manage.addHeaderView(headView);
		//listview_manage.addFooterView(footView);
        headView.measure(0, 0);
		managementheadheight=headView.getMeasuredHeight();
        footView.measure(0, 0);
		managementfootheight=footView.getMeasuredHeight();
	}
	/**
	 * 根据关键字请求接口进行搜索处理(首页消息列表)
	 */
	private void getHomeListResult(String str) {
		if(StringUtils.isNotEmpty(str)){
			try {
				String keyword = URLEncoder.encode(str,"UTF-8");
				RequestConfig config = new RequestConfig(this,HttpTools.GET_HOME_LSIT);
				RequestParams params = new RequestParams();
				params.put("username", UserInfo.employeeAccount);
				params.put("keyword",keyword);
				params.put("page", 1);
				params.put("pagesize",3);
				HttpTools.httpGet(Contants.URl.URL_ICETEST, "/push2/homepush",config, params);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据关键字请求接口进行搜索处理(通讯录)
	 */
	private void getContactSearchResult(String str) {
		if(StringUtils.isNotEmpty(str)){
			try {
				String keyword = URLEncoder.encode(str,"UTF-8");
				RequestConfig config = new RequestConfig(this,HttpTools.SET_REPAIR_INFO);
				RequestParams params = new RequestParams();
				params.put("keyword",keyword);
				HttpTools.httpGet(Contants.URl.URL_ICETEST,"/phonebook/search" ,config, params);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据关键字请求接口进行搜索处理(应用)
	 */
	private void getManageMentResult(String filterStr) {
		List<GridViewInfo> gridlistDatas = new ArrayList<GridViewInfo>();
		gridlistDatas.clear();
		String commonjsonStr = Tools.getCommonName(HomeContactSearchActivity.this);
		String 	elsejsonStr = Tools.getElseName(HomeContactSearchActivity.this);
		/**
		 * 常用应用
		 */
		String  jsonString = HttpTools.getContentString(commonjsonStr);
		if (jsonString != null) {
			ResponseData app_list = HttpTools.getResponseKey(jsonString,"app_list");
			if (app_list.length > 0) {
				JSONArray jsonArray = app_list.getJSONArray(0,"list");
				ResponseData data = HttpTools.getResponseKeyJSONArray(jsonArray);
				List<GridViewInfo> gridlist1 = new ArrayList<GridViewInfo>();
				GridViewInfo item = null;
				if(data.length > 0){
					for (int i = 0; i < data.length; i++) {
						try {
							item = new GridViewInfo();
							item.name = data.getString(i, "name");
							item.oauthType = data.getString(i, "oauthType");
							item.developerCode = data.getString(i, "app_code");
							item.clientCode = data.getString(i, "app_code");
							item.sso = data.getString(i,"url");
							JSONObject icon = data.getJSONObject(i,"icon");
							if(icon != null || icon.length() > 0){
								item.icon = icon.getString("android");
							}
							gridlist1.add(item);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					gridlistDatas.addAll(gridlist1);
				}
			}
		}
		/**
		 * 其他应用
		 */
		String  jsonelse = HttpTools.getContentString(elsejsonStr);
		if (jsonelse != null) {
			ResponseData app_list = HttpTools.getResponseKey(jsonelse,"app_list");
			if (app_list.length > 0) {
				JSONArray jsonArray = app_list.getJSONArray(1,"list");
				ResponseData data = HttpTools.getResponseKeyJSONArray(jsonArray);
				List<GridViewInfo> gridlist2 = new ArrayList<GridViewInfo>();
				GridViewInfo item = null;
				if(data.length > 0){
					for (int i = 0; i < data.length; i++) {
						try {
							item = new GridViewInfo();
							item.name = data.getString(i, "name");
							item.oauthType = data.getString(i, "oauthType");
							item.developerCode = data.getString(i, "app_code");
							item.clientCode = data.getString(i, "app_code");
							item.sso = data.getString(i,"url");
							JSONObject icon = data.getJSONObject(i,"icon");
							if(icon != null || icon.length() > 0){
								item.icon = icon.getString("android");
							}
							gridlist2.add(item);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					gridlistDatas.addAll(gridlist2);
				}
			}
		}
		/**
		 * 进行搜索
		 */
		managementDatas.clear();
		for (int i = 0 ;i < gridlistDatas.size() ;i++){
			String name = gridlistDatas.get(i).name;
			if(name.indexOf(filterStr) != -1){
				managementDatas.add(gridlistDatas.get(i));
			}
		}
		if(managementDatas.size() > 0 ){
			division_manage.setVisibility(View.VISIBLE);
			listview_manage.setVisibility(View.VISIBLE);
		}else{
			division_manage.setVisibility(View.GONE);
			listview_manage.setVisibility(View.GONE);
		}
		managementAdapter.notifyDataSetChanged();
		setContactListViewHeightManageBasedOnChildren(managementAdapter,listview_manage);
	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		int code = HttpTools.getCode(jsonString);
		if(msg.arg1 == HttpTools.SET_REPAIR_INFO){//通讯录
			if(code == 0){
				listview_contact.setVisibility(View.VISIBLE);
				List<FindContactInfo> list1 =  new ArrayList<>();
				JSONArray jsonArray = HttpTools.getContentJsonArray(jsonString);
				if(jsonArray != null){
					ResponseData  data = HttpTools.getResponseContent(jsonArray);
					if(data.length > 0){
						division.setVisibility(View.VISIBLE);
						FindContactInfo info ;
						if(data.length >= 3){
							for (int i = 0; i < 3; i++) {
								info = new FindContactInfo();
								info.username = data.getString(i, "username");
								info.realname = data.getString(i, "realname");
								info.avatar = data.getString(i, "avatar");
								info.org_name = data.getString(i, "org_name");
								info.job_name = data.getString(i, "job_name");
								list1.add(info);
							}
						}else{
							for (int i = 0; i < data.length; i++) {
								info = new FindContactInfo();
								info.username = data.getString(i, "username");
								info.realname = data.getString(i, "realname");
								info.avatar = data.getString(i, "avatar");
								info.org_name = data.getString(i, "org_name");
								info.job_name = data.getString(i, "job_name");
								list1.add(info);
							}
						}

						contactDatas.clear();
						contactDatas.addAll(list1);
						contactAdapter.notifyDataSetChanged();
						setContactListViewHeightContactBasedOnChildren(contactAdapter,listview_contact);

					}else{
						division.setVisibility(View.GONE);
						listview_contact.setVisibility(View.GONE);
					}
				}
			}
		}else if(msg.arg1 == HttpTools.GET_HOME_LSIT){//首页消息列表
			if(code == 0){
				List<FindHomeListInfo> list2 = new ArrayList<>();
				String response = HttpTools.getContentString(jsonString);
				if (StringUtils.isNotEmpty(response)) {
					listview_homelist.setVisibility(View.VISIBLE);
					ResponseData data = HttpTools.getResponseData(response);
					FindHomeListInfo item;
					for (int i = 0; i < data.length; i++) {
						item = new FindHomeListInfo();
						item.id = data.getInt(i, "id");
						item.auth_type = data.getInt(i, "auth_type");
						item.icon = data.getString(i, "ICON");
						item.owner_name = data.getString(i, "owner_name");
						item.owner_avatar = data.getString(i, "owner_avatar");
						item.modify_time = data.getString(i, "homePushTime");
						item.title = data.getString(i, "title");
						item.source_id = data.getString(i, "source_id");
						item.comefrom = data.getString(i, "comefrom");
						item.url = data.getString(i, "url");
						item.client_code = data.getString(i, "client_code");
						list2.add(item);
					}
					homelistDatas.clear();
					homelistDatas.addAll(list2);
					homeListAdapter.notifyDataSetChanged();
					setContactListViewHeightHomeListBasedOnChildren(homeListAdapter,listview_homelist);
				}else{
					listview_homelist.setVisibility(View.GONE);
				}
			}else{
				listview_homelist.setVisibility(View.GONE);
			}

		}
	}

	/**
	 * 适配listview高度(通讯录)
	 * @param listView
	 */
	public void setContactListViewHeightContactBasedOnChildren(BaseAdapter adapter ,ListView listView) {
		// 获取ListView的Adapter
		//HomeContactSearchAdapter listAdapter = (HomeContactSearchAdapter) listView.getAdapter();
		if (adapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0, len = adapter.getCount(); i < len; i++) {
			// listAdapter.getCount()返回数据项的数目
			View listItem = adapter.getView(i, null, listView);
			// 计算item View 的宽高
			listItem.measure(0, 0);
			// 统计所有item的总高度
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight+ (listView.getDividerHeight() * (adapter.getCount() - 1)) +contactheadheight+contactfootheight;
		listView.setLayoutParams(params);
	}
	/**
	 * 适配listview高度(首页消息)
	 * @param listView
	 */
	public void setContactListViewHeightHomeListBasedOnChildren(BaseAdapter adapter ,ListView listView) {
		// 获取ListView的Adapter
		//HomeContactSearchAdapter listAdapter = (HomeContactSearchAdapter) listView.getAdapter();
		if (adapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0, len = adapter.getCount(); i < len; i++) {
			// listAdapter.getCount()返回数据项的数目
			View listItem = adapter.getView(i, null, listView);
			// 计算item View 的宽高
			listItem.measure(0, 0);
			// 统计所有item的总高度
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight+ (listView.getDividerHeight() * (adapter.getCount() - 1)) +homelistheadheight+homelistfootheight;
		listView.setLayoutParams(params);
	}
	/**
	 * 适配listview高度(应用)
	 * @param listView
	 */
	public void setContactListViewHeightManageBasedOnChildren(BaseAdapter adapter ,ListView listView) {
		// 获取ListView的Adapter
		//HomeContactSearchAdapter listAdapter = (HomeContactSearchAdapter) listView.getAdapter();
		if (adapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0, len = adapter.getCount(); i < len; i++) {
			// listAdapter.getCount()返回数据项的数目
			View listItem = adapter.getView(i, null, listView);
			// 计算item View 的宽高
			listItem.measure(0, 0);
			// 统计所有item的总高度
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight+ (listView.getDividerHeight() * (adapter.getCount() - 1)) +managementheadheight+managementfootheight;
		listView.setLayoutParams(params);
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return null;
	}

}
