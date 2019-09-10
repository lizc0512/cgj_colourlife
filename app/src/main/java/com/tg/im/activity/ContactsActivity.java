package com.tg.im.activity;


import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.phone.BaseUtil;
import com.tg.coloursteward.phone.CharacterParser;
import com.tg.coloursteward.phone.PinyinComparator;
import com.tg.coloursteward.phone.SideBar;
import com.tg.coloursteward.phone.SortAdapter;
import com.tg.coloursteward.phone.SortModel;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.ClearEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 获取手机通讯录
 *
 * @author Administrator
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        sideBar = findViewById(R.id.sidrbar);
        dialog = findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(s -> {
            //该字母首次出现的位置
            int position = adapter.getPositionForSection(s.charAt(0));
            if (position != -1) {
                sortListView.setSelection(position);
            }
        });

        sortListView = findViewById(R.id.country_lvcountry);
        sortListView.setOnItemClickListener((parent, view, position, id) -> {
            if (XXPermissions.isHasPermission(ContactsActivity.this, Manifest.permission.CALL_PHONE)) {
                callPhone(((SortModel) adapter.getItem(position)).getTelnum());
            } else {
                XXPermissions.with(ContactsActivity.this)
                        .permission(Manifest.permission.CALL_PHONE)
                        .request(new OnPermission() {
                            @Override
                            public void hasPermission(List<String> granted, boolean isAll) {
                                callPhone(((SortModel) adapter.getItem(position)).getTelnum());
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean quick) {
                                ToastUtil.showShortToast(ContactsActivity.this, "请打开拨打电话权限");
                            }
                        });
            }
        });

        SourceDateList = filledData();

        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new SortAdapter(this, SourceDateList);
        sortListView.setAdapter(adapter);


        mClearEditText = findViewById(R.id.filter_edit);
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

    private void callPhone(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        startActivity(intent);
    }


    /**
     * 为ListView填充数据
     *
     * @param
     * @return
     */
    private List<SortModel> filledData() {
        List<SortModel> mSortList = new ArrayList<SortModel>();
        Cursor cursor = getContentResolver().query(Phone.CONTENT_URI, BaseUtil.PHONES_PROJECTION, null, null, null);
        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                SortModel sortModel = new SortModel();
                sortModel.setName(cursor.getString(0));
                sortModel.setTelnum(cursor.getString(1));

                //汉字转换成拼音
                String pinyin = characterParser.getSelling(cursor.getString(0));
                String sortString = pinyin.substring(0, 1).toUpperCase();

                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    sortModel.setSortLetters(sortString.toUpperCase());
                } else {
                    sortModel.setSortLetters("#");
                }

                mSortList.add(sortModel);
            }
        }
        return mSortList;
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }


    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_contacts, null);
    }

    @Override
    public String getHeadTitle() {
        return "手机通讯录";
    }
}
