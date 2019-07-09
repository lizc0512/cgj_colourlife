package com.tg.im.activity;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.phone.BaseUtil;
import com.tg.coloursteward.phone.CharacterParser;
import com.tg.coloursteward.phone.PinyinComparator;
import com.tg.coloursteward.phone.SideBar;
import com.tg.coloursteward.phone.SortAdapter;
import com.tg.coloursteward.phone.SortModel;
import com.tg.coloursteward.phone.SideBar.OnTouchingLetterChangedListener;
import com.tg.coloursteward.view.ClearEditText;
import com.tg.coloursteward.view.dialog.ToastFactory;

/**
 * 获取手机通讯录
 * 
 * @author Administrator
 * 
 */
public class ContactsActivity extends BaseActivity {
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;
	
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;
	
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	private  final int REQUESTPERMISSION = 110;
	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
	}
	private void initViews() {
		//实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		
		pinyinComparator = new PinyinComparator();
		
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		
		//设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			
			@Override
			public void onTouchingLetterChanged(String s) {
				//该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if(position != -1){
					sortListView.setSelection(position);
				}
				
			}
		});
		
		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//这里要利用adapter.getItem(position)来获取当前position所对应的对象
				if(ContextCompat.checkSelfPermission(ContactsActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
					//申请权限
					ActivityCompat.requestPermissions(ContactsActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, REQUESTPERMISSION);
					ToastFactory.showToast(ContactsActivity.this, "请允许权限");
				}else{
					intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+((SortModel)adapter.getItem(position)).getTelnum()));
					startActivity(intent);
				}


			}
		});
		
		SourceDateList = filledData();
		
		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new SortAdapter(this, SourceDateList);
		sortListView.setAdapter(adapter);
		
		
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		
		//根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}


	/**
	 * 为ListView填充数据
	 * @param
	 * @return
	 */
	private List<SortModel> filledData(){
		List<SortModel> mSortList = new ArrayList<SortModel>();
		Cursor cursor = getContentResolver().query(Phone.CONTENT_URI,BaseUtil.PHONES_PROJECTION, null, null, null);
		if(cursor != null){
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				SortModel sortModel = new SortModel();
				sortModel.setName(cursor.getString(0));
				sortModel.setTelnum(cursor.getString(1));

				//汉字转换成拼音
				String pinyin = characterParser.getSelling(cursor.getString(0));
				String sortString = pinyin.substring(0, 1).toUpperCase();

				// 正则表达式，判断首字母是否是英文字母
				if(sortString.matches("[A-Z]")){
					sortModel.setSortLetters(sortString.toUpperCase());
				}else{
					sortModel.setSortLetters("#");
				}

				mSortList.add(sortModel);
			}
		}
		return mSortList;
	}
	
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * @param filterStr
	 */
	private void filterData(String filterStr){
		List<SortModel> filterDateList = new ArrayList<SortModel>();
		
		if(TextUtils.isEmpty(filterStr)){
			filterDateList = SourceDateList;
		}else{
			filterDateList.clear();
			for(SortModel sortModel : SourceDateList){
				String name = sortModel.getName();
				if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
					filterDateList.add(sortModel);
				}
			}
		}
		
		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case REQUESTPERMISSION:
				if (grantResults.length > 0&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

				} else {

				}
				break;
		}
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_contacts, null);
	}

	@Override
	public String getHeadTitle() {
		// TODO Auto-generated method stub
		return "手机通讯录";
	}
}
