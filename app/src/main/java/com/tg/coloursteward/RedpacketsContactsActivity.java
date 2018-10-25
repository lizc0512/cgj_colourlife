package com.tg.coloursteward;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.phone.BaseUtil;
import com.tg.coloursteward.phone.CharacterParser;
import com.tg.coloursteward.phone.PinyinComparator;
import com.tg.coloursteward.phone.SideBar;
import com.tg.coloursteward.phone.SideBar.OnTouchingLetterChangedListener;
import com.tg.coloursteward.phone.SortAdapter;
import com.tg.coloursteward.phone.SortModel;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                if (Tools.checkTelephoneNumber(phoneNum)) {
                    Intent intent = new Intent();
                    intent.putExtra("phoneNum", phoneNum);
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
        JSONArray contactArray = new JSONArray();
        for (int i = 0; i < mSortList.size(); i++) {
            contactArray.put(mSortList.get(i).getTelnum());
        }
        String key = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.key);
        String secret = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.secret);
        RequestConfig config = new RequestConfig(RedpacketsContactsActivity.this, HttpTools.POST_SEND_PACKET);
        RequestParams params = new RequestParams();
        Log.d("printLog", "contactArray=" + contactArray);
        Log.d("printLog", "key=" + key);
        Log.d("printLog", "secret=" + secret);
        params.put("phone", contactArray + "");
        params.put("key", key);
        params.put("secret", secret);
        HttpTools.httpPost(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/checkSendPacket", config, params);
        return mSortList;
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        JSONObject content = HttpTools.getContentJSONObject(jsonString);
        if (code == 0) {
            if (content != null) {
                try {
                    if ("1".equals(content.getString("ok"))) {
                        JSONArray jsonArr = content.getJSONArray("sendStatus");
                        for (int i = jsonArr.length() - 1; i >= 0; i--) {
                            if (jsonArr.getInt(i) == 0) {
                                SourceDateList.remove(i);
                            }
                        }
                    }
                    if (SourceDateList.size() == 0) {
                        ToastFactory.showToast(RedpacketsContactsActivity.this, "通讯录中未识别到同事");
                        finish();
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            ToastFactory.showToast(RedpacketsContactsActivity.this, message);
        }
    }

    @Override
    public View getContentView() {
        // TODO Auto-generated method stub
        return getLayoutInflater().inflate(R.layout.activity_redpackets_contacts, null);
    }

    @Override
    public String getHeadTitle() {
        // TODO Auto-generated method stub
        return "通讯录同事";
    }

}
