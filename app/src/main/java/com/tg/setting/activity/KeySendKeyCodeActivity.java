package com.tg.setting.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.setting.adapter.KeyPhoneAdapter;
import com.tg.setting.view.KeyUnitPopWindowView;
import com.tg.setting.view.KeyStringPopWindowView;
import com.tg.setting.view.KeyTimePopWindowView;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 乐开-二维码发送钥匙
 *
 * @author hxg 2019.07.18
 */
public class KeySendKeyCodeActivity extends BaseActivity {
    public static String COMMUNITY_UUID = "communityUuid";
    public static final String DOOR_ID = "door_id";

    private SwipeRecyclerView rv_phone;
    private EditText et_name;
    private EditText et_phone;
    private ImageView iv_add;
    private ImageView iv_contacts;
    private TextView tv_identity;
    private TextView tv_address;
    private TextView tv_one_year;
    private TextView tv_two_year;
    private TextView tv_three_year;
    private TextView tv_forever;
    private TextView tv_one_month;
    private TextView tv_one_week;
    private TextView tv_one_day;
    private TextView tv_custom;
    private TextView tv_time_start;
    private TextView tv_time_end;
    private TextView tv_time_invalid;
    private ImageView iv_check;
    private TextView tv_send;
    private KeyPhoneAdapter adapter;

    private int check = 0;
    private int keyType = 0;
    private String communityUuid = "";
    private long doorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initView() {
        List<String> list = new ArrayList<>();
        rv_phone = findViewById(R.id.rv_phone);
        iv_add = findViewById(R.id.iv_add);
        iv_contacts = findViewById(R.id.iv_contacts);
        et_name = findViewById(R.id.et_name);
        et_phone = findViewById(R.id.et_phone);
        tv_identity = findViewById(R.id.tv_identity);
        tv_address = findViewById(R.id.tv_address);
        tv_one_year = findViewById(R.id.tv_one_year);
        tv_two_year = findViewById(R.id.tv_two_year);
        tv_three_year = findViewById(R.id.tv_three_year);
        tv_forever = findViewById(R.id.tv_forever);
        tv_one_month = findViewById(R.id.tv_one_month);
        tv_one_week = findViewById(R.id.tv_one_week);
        tv_one_day = findViewById(R.id.tv_one_day);
        tv_custom = findViewById(R.id.tv_custom);
        tv_time_start = findViewById(R.id.tv_time_start);
        tv_time_end = findViewById(R.id.tv_time_end);
        tv_time_invalid = findViewById(R.id.tv_time_invalid);
        iv_check = findViewById(R.id.iv_check);
        tv_send = findViewById(R.id.tv_send);

        iv_add.setOnClickListener(singleListener);
        iv_contacts.setOnClickListener(singleListener);
        tv_identity.setOnClickListener(singleListener);
        tv_address.setOnClickListener(singleListener);
        tv_one_year.setOnClickListener(singleListener);
        tv_two_year.setOnClickListener(singleListener);
        tv_three_year.setOnClickListener(singleListener);
        tv_forever.setOnClickListener(singleListener);
        tv_one_month.setOnClickListener(singleListener);
        tv_one_week.setOnClickListener(singleListener);
        tv_one_day.setOnClickListener(singleListener);
        tv_custom.setOnClickListener(singleListener);
        tv_time_start.setOnClickListener(singleListener);
        tv_time_end.setOnClickListener(singleListener);
        tv_time_invalid.setOnClickListener(singleListener);
        iv_check.setOnClickListener(singleListener);
        tv_send.setOnClickListener(singleListener);

        rv_phone.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new KeyPhoneAdapter(this, list);
        rv_phone.setAdapter(adapter);
    }

    private void initData() {
        doorId = getIntent().getLongExtra(DOOR_ID, 0);
        communityUuid = getIntent().getStringExtra(COMMUNITY_UUID);
    }

    public void delete(int position) {
        adapter.list.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_key_send_key_phone, null);
    }

    @Override
    public String getHeadTitle() {
        return "发送钥匙";
    }

    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.iv_add:
                adapter.list.add("");
                adapter.notifyDataSetChanged();
                break;
            case R.id.iv_contacts://联系人
                break;
            case R.id.tv_identity:
                List<String> list = new ArrayList<>();
                list.add("业主钥匙");
                list.add("管理员");
                list.add("普通用户");
                list.add("临时用户");

                KeyStringPopWindowView areaPop = new KeyStringPopWindowView(this, list);
                areaPop.showPopupWindow(contentLayout);
                areaPop.setOnDismissListener(new PopupDismissListener());
                break;
            case R.id.tv_address:
                if (!TextUtils.isEmpty(communityUuid)) {
                    KeyUnitPopWindowView addressPop = new KeyUnitPopWindowView(this, communityUuid, false);
                    addressPop.setOnDismissListener(new PopupDismissListener());
                    addressPop.showPopupWindow(contentLayout);
                }
                break;
            case R.id.tv_one_year:
                clearText();
                keyType = 1;
                tv_one_year.setBackgroundResource(R.drawable.shape_key_choose_select_text);
                break;
            case R.id.tv_two_year:
                clearText();
                keyType = 2;
                tv_two_year.setBackgroundResource(R.drawable.shape_key_choose_select_text);
                break;
            case R.id.tv_three_year:
                clearText();
                keyType = 3;
                tv_three_year.setBackgroundResource(R.drawable.shape_key_choose_select_text);
                break;
            case R.id.tv_forever:
                clearText();
                keyType = 4;
                tv_forever.setBackgroundResource(R.drawable.shape_key_choose_select_text);
                break;
            case R.id.tv_one_month:
                clearText();
                keyType = 5;
                tv_one_month.setBackgroundResource(R.drawable.shape_key_choose_select_text);
                break;
            case R.id.tv_one_week:
                clearText();
                keyType = 6;
                tv_one_week.setBackgroundResource(R.drawable.shape_key_choose_select_text);
                break;
            case R.id.tv_one_day:
                clearText();
                keyType = 7;
                tv_one_day.setBackgroundResource(R.drawable.shape_key_choose_select_text);
                break;
            case R.id.tv_custom:
                clearText();
                keyType = 8;
                tv_custom.setBackgroundResource(R.drawable.shape_key_choose_select_text);
                break;
            case R.id.tv_time_start:
                KeyTimePopWindowView popWindowView = new KeyTimePopWindowView(this, 1);
                popWindowView.showPopupWindow(contentLayout);
                popWindowView.setOnDismissListener(new PopupDismissListener());
                break;
            case R.id.tv_time_end:
                KeyTimePopWindowView pop = new KeyTimePopWindowView(this, 2);
                pop.showPopupWindow(contentLayout);
                pop.setOnDismissListener(new PopupDismissListener());
                break;
            case R.id.tv_time_invalid:
                KeyTimePopWindowView popInvalid = new KeyTimePopWindowView(this, 3);
                popInvalid.showPopupWindow(contentLayout);
                popInvalid.setOnDismissListener(new PopupDismissListener());
                break;
            case R.id.iv_check:
                if (0 == check) {
                    check = 1;
                    iv_check.setBackgroundResource(R.drawable.ic_key_send_check);
                } else {
                    check = 0;
                    iv_check.setBackgroundResource(R.drawable.ic_key_send_no_check);
                }
                break;
            case R.id.tv_send:

                break;
        }
        return super.handClickEvent(v);
    }

    public void setTimeText(int inputText, String time, int y, int m, int d, int h, int mi) {
        switch (inputText) {
            case 1:
                tv_time_start.setText(time);
                break;
            case 2:
                tv_time_end.setText(time);
                break;
            case 3:
                tv_time_invalid.setText(time);
                break;
        }
    }

    /**
     * 还原text
     */
    private void clearText() {
        tv_one_year.setBackgroundResource(R.drawable.shape_key_choose_text);
        tv_two_year.setBackgroundResource(R.drawable.shape_key_choose_text);
        tv_three_year.setBackgroundResource(R.drawable.shape_key_choose_text);
        tv_forever.setBackgroundResource(R.drawable.shape_key_choose_text);
        tv_one_month.setBackgroundResource(R.drawable.shape_key_choose_text);
        tv_one_week.setBackgroundResource(R.drawable.shape_key_choose_text);
        tv_one_day.setBackgroundResource(R.drawable.shape_key_choose_text);
        tv_custom.setBackgroundResource(R.drawable.shape_key_choose_text);
    }

    class PopupDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 1.0f;
            getWindow().setAttributes(lp);
        }
    }

}
