package com.tg.coloursteward;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.info.WaterBox_TypeList;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 选择资源类型
 */
public class WaterBoxTypeActivity extends BaseActivity {
    private MyAdapter myAdapter;
    private List<WaterBox_TypeList> TypeList = new ArrayList<WaterBox_TypeList>();
    private String code;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        if (code == null) {
            code = "";
        }
        initView();
        initData();
    }

    private void initView() {
        ListView listView_type = (ListView) findViewById(R.id.listView_type);
        myAdapter = new MyAdapter();
        listView_type.setAdapter(myAdapter);
        listView_type.setOnItemClickListener(new OnItemClickListener() {//根据类型获取跳转地址

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                if (TypeList.size() > 0 && TypeList != null) {
                    String value = TypeList.get(position).getValue();
                    RequestConfig config = new RequestConfig(WaterBoxTypeActivity.this, HttpTools.GET_URL);
                    RequestParams params = new RequestParams();
                    params.put("resource_type", value);
                    params.put("code", code);
                    HttpTools.httpGet(Contants.URl.URL_ICETEST, "/code/url", config, params);
                }
            }
        });
    }

    private void initData() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_TYPE_LIST);
        RequestParams params = new RequestParams();
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/code/typelist", config, params);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.GET_URL) {//根据类型获取跳转地址
            JSONObject resopnse = HttpTools.getContentJSONObject(jsonString);
            if (code == 0) {
                try {
                    JSONObject content = resopnse.getJSONObject("content");
                    String url = content.getString("url");
                    Intent intent = new Intent(WaterBoxTypeActivity.this, MyBrowserActivity.class);
                    intent.putExtra(MyBrowserActivity.KEY_URL, url);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                }
            } else {
                ToastFactory.showToast(WaterBoxTypeActivity.this, message);
            }
        } else if (msg.arg1 == HttpTools.GET_TYPE_LIST) {
            JSONObject response = HttpTools.getContentJSONObject(jsonString);
            if ("0".equals(code)) {
                try {
                    JSONArray content = response.getJSONArray("content");
                    Type type = new TypeToken<List<WaterBox_TypeList>>() {
                    }.getType();
                    List<WaterBox_TypeList> TypeList = gson.fromJson(
                            content.toString(), type);
                } catch (Exception e) {
                }
            } else {
                ToastFactory.showToast(WaterBoxTypeActivity.this, message);
            }
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return TypeList.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View arg1, ViewGroup arg2) {
            TextView textView = new TextView(WaterBoxTypeActivity.this);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(25);
            textView.setTextColor(WaterBoxTypeActivity.this.getResources().getColor(R.color.text_color3));
            textView.setText(position + 1 + "." + TypeList.get(position).getKey());
            return textView;
        }

    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_water_box_type, null);
    }

    @Override
    public String getHeadTitle() {
        return "选择资源类型";
    }
}
