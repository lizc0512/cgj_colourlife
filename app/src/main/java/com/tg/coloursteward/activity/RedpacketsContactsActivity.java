package com.tg.coloursteward.activity;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.tg.coloursteward.phone.SideBar.OnTouchingLetterChangedListener;
import com.tg.coloursteward.phone.SortAdapter;
import com.tg.coloursteward.phone.SortModel;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 通讯录同事
 *
 * @author Administrator
 */
public class RedpacketsContactsActivity extends BaseActivity {

    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;

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
        initPermission();
    }

    private void initPermission() {
        if (XXPermissions.isHasPermission(this, Manifest.permission.READ_CONTACTS)) {
            initViews();
        } else {
            XXPermissions.with(this)
                    .permission(Manifest.permission.READ_CONTACTS)
                    .request(new OnPermission() {
                        @Override
                        public void hasPermission(List<String> granted, boolean isAll) {
                            initViews();
                        }

                        @Override
                        public void noPermission(List<String> denied, boolean quick) {
                            ToastUtil.showShortToast(RedpacketsContactsActivity.this, "请打开通讯录权限才能使用该功能");
                        }
                    });
        }
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
                if (position != -1) {
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
                String phoneNum = ((SortModel) adapter.getItem(position)).getTelnum();
                String phone = phoneNum.replaceAll(" ", "");
                if (Tools.checkTelephoneNumber(phone)) {
                    Intent intent = new Intent();
                    intent.putExtra("phoneNum", phone);
                    setResult(1002, intent);
                    finish();
                } else {
                    ToastFactory.showToast(RedpacketsContactsActivity.this, "手机号码格式不对");
                }
            }
        });

        SourceDateList = filledData();

        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new SortAdapter(this, SourceDateList);
        sortListView.setAdapter(adapter);

    }


    /**
     * 为ListView填充数据
     *
     * @param
     * @return
     */
    private List<SortModel> filledData() {
        List<SortModel> mSortList = new ArrayList<SortModel>();
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(Phone.CONTENT_URI, BaseUtil.PHONES_PROJECTION, null, null, null);
        } catch (Exception e) {

        }
        if (null != cursor) {
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

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_redpackets_contacts, null);
    }

    @Override
    public String getHeadTitle() {
        return "通讯录同事";
    }

}
