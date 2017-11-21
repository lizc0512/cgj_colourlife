package com.tg.coloursteward;

import java.util.ArrayList;
import java.util.Calendar;

import com.tg.coloursteward.adapter.DataShowAdapter;
import com.tg.coloursteward.adapter.ViewPagerAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.DataShowInfo;
import com.tg.coloursteward.info.FamilyInfo;
import com.tg.coloursteward.info.MapDataResp;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.MyViewPager;
import com.tg.coloursteward.view.PullRefreshListViewFind;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.os.Bundle;
import android.content.Intent;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 数据看板
 * 
 * @author Administrator
 * 
 */
public class DataShowActivity extends BaseActivity implements OnCheckedChangeListener, OnPageChangeListener {
	private DataShowAdapter mAdapter1;
	private DataShowAdapter mAdapter2;
	public final static String BRANCH = "branch";
	private String branch;
	private TextView tvOrgId;
	private Intent intent;
	private MyViewPager viewPager;
	private RadioGroup radioGroup;
	private ViewPagerAdapter pagerAdapter;
	private ArrayList<View> pagerList = new ArrayList<View>();
	private ListView mListView1;
	private ListView mListView2;
	private RelativeLayout  rlOrgId;
	private ArrayList<DataShowInfo> list1 = new ArrayList<DataShowInfo>();
	private ArrayList<DataShowInfo> list2 = new ArrayList<DataShowInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent = getIntent();
		if(intent != null){
            branch =intent.getStringExtra(BRANCH);
		}
		if(branch == null){
			ToastFactory.showToast(this,"参数错误");
			finish();
			return;
		}
		getData();
		initView();
		radioGroup.setOnCheckedChangeListener(this);
		viewPager.setOnPageChangeListener(this);
	}

	private void initView() {
		rlOrgId = (RelativeLayout) findViewById(R.id.rl_orgId);
		rlOrgId.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FamilyInfo info = new FamilyInfo();
				info.id = "9959f117-df60-4d1b-a354-776c20ffb8c7";
				info.type = "org";
				info.name = "彩生活服务集团";
				intent = new Intent(DataShowActivity.this,BranchActivity.class);
				intent.putExtra(BranchActivity.FAMILY_INFO, info);
				startActivityForResult(intent,1);
			}
		});
		tvOrgId = (TextView) findViewById(R.id.tv_orgId);
		tvOrgId.setText(UserInfo.familyName);
		radioGroup = (RadioGroup)findViewById(R.id.radio_group);
		viewPager = (MyViewPager)findViewById(R.id.viewPager);
		RadioButton btn1 = (RadioButton)findViewById(R.id.rb_noticBtn);
		RadioButton btn2 = (RadioButton)findViewById(R.id.rb_notifiicationBtn);
		btn1.setText("管理类");
		btn2.setText("经营类");
		mListView1 = new ListView(this);
		mAdapter1 = new DataShowAdapter(this, list1);
		mListView1.setAdapter(mAdapter1);
		pagerList.add(mListView1);
		
		
		mListView2 = new ListView(this);
		mAdapter2= new DataShowAdapter(this, list2);
		mListView2.setAdapter(mAdapter2);
		pagerList.add(mListView2);
		pagerAdapter = new ViewPagerAdapter(pagerList,this);
		viewPager.setAdapter(pagerAdapter);
	}

	/**
	 * 获取数据
	 */
	private void getData(){
		RequestConfig config = new RequestConfig(DataShowActivity.this,HttpTools.GET_STATISTICS_INFO);
		RequestParams params = new RequestParams();
		params.put("id",branch);
		HttpTools.httpGet(Contants.URl.URL_ICETEST, "/resource/statistics",config, params);

		Calendar c = Calendar.getInstance();//
		int mYear = c.get(Calendar.YEAR); // 获取当前年份
		config = new RequestConfig(DataShowActivity.this,HttpTools.GET_KPI_INFO);
		params = new RequestParams();
		params.put("uid",UserInfo.orgId);
		params.put("branch",branch);
		params.put("year",mYear);
		HttpTools.httpGet(Contants.URl.URL_ICETEST, "/bigdata/kpi",config, params);
	}
	/**
	 * 管理类添加数据
	 */
	private void getDataMagment(MapDataResp info) {
		DataShowInfo item = new DataShowInfo();
		item.title = "小区面积（㎡）";
		String area = info.floorArea;
		if(StringUtils.isNotEmpty(area)){
			item.content = Tools.formatTosepara(Float.valueOf(area));
		}
		list1.add(item);
		
		item = new DataShowInfo();
		item.title = "小区数";
		item.content = info.communityCount;
		list1.add(item);
		
		item = new DataShowInfo();
		item.title = "车位数量";
		item.content = info.parkingCount;
		list1.add(item);
		
		item = new DataShowInfo();
		item.title = "APP安装数量";
		item.content = info.appCount;
		list1.add(item);
		
		item = new DataShowInfo();
		item.title = "上线小区";
		item.content = info.join_smallarea_num;
		list1.add(item);
	}
	
	/**
	 * 经营类添加数据
	 */
	private void getDataBusiness(MapDataResp info) {
		DataShowInfo item = new DataShowInfo();
		item.title = "应收";
		item.content = info.normalFee;
		list2.add(item);
		
		item = new DataShowInfo();
		item.title = "实收";
		item.content = info.receivedFee;
		list2.add(item);
		
		item = new DataShowInfo();
		item.title = "收费率";
		item.content = info.feeRate;
		list2.add(item);

		item = new DataShowInfo();
		item.title = "业主投诉数";
		item.content = info.complainCount;
		list2.add(item);
	}

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
		if(msg.arg1 == HttpTools.GET_STATISTICS_INFO){
			if(code == 0){
				JSONArray jsonArray = HttpTools.getContentJsonArray(jsonString);
				ResponseData data  = HttpTools.getResponseContent(jsonArray);
				if(data.length > 0){
					MapDataResp info = new MapDataResp();
					info.communityCount = data.getString("smallarea_num");
					info.appCount = data.getString("app_num");
					info.parkingCount = data.getString("parking_space");
					info.floorArea = data.getString("const_area");
					info.join_smallarea_num = data.getString("join_smallarea_num");
					getDataMagment(info);
					mAdapter1.notifyDataSetChanged();
				}
			}else{
				ToastFactory.showToast(DataShowActivity.this,message);
			}
		}else{
			if(code == 0){
				JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
				if(jsonObject.length() > 0){
					MapDataResp info = new MapDataResp();
					try {
						info.normalFee = jsonObject.getString("normalFee");
						info.receivedFee = jsonObject.getString("receivedFee");
						info.feeRate = jsonObject.getString("feeRate");
						info.complainCount = jsonObject.getString("complainCount");
						getDataBusiness(info);
						mAdapter2.notifyDataSetChanged();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}else{
				ToastFactory.showToast(DataShowActivity.this,message);
			}
		}
	}

    @Override
    public void onFail(Message msg, String hintString) {
        super.onFail(msg, hintString);
        ToastFactory.showToast(DataShowActivity.this,hintString);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
			list1.clear();
			list2.clear();
            branch = data.getStringExtra("id");
            String name = data.getStringExtra("name");
            tvOrgId.setText(name);
            getData();
        }
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int position) {
		if(position == 0){
			radioGroup.check(R.id.rb_noticBtn);
		}else{
			radioGroup.check(R.id.rb_notifiicationBtn);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(checkedId == R.id.rb_noticBtn){
			if(viewPager.getCurrentItem() != 0){
				viewPager.setCurrentItem(0);
				//listViewReceive.performLoading();
			}
		}else{
			if(viewPager.getCurrentItem() != 1){
				viewPager.setCurrentItem(1);
				//listViewExpend.performLoading();
			}
		}
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_data_show, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "数据看板";
	}

}
