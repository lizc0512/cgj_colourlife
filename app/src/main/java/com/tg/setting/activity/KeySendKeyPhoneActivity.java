package com.tg.setting.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.DateUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.setting.adapter.KeyPhoneAdapter;
import com.tg.setting.entity.KeyIdentityEntity;
import com.tg.setting.view.KeyRoomPopWindowView;
import com.tg.setting.view.KeyStringPopWindowView;
import com.tg.setting.view.KeyTimePopWindowView;
import com.tg.user.model.UserModel;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.youmai.hxsdk.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 乐开-手机号送钥匙
 *
 * @author hxg 2019.07.18
 */
public class KeySendKeyPhoneActivity extends BaseActivity implements HttpResponse {
    public static final String DOOR_ID = "door_id";
    public static String COMMUNITY_UUID = "community_uuid";
    public static String KEY_NAME = "key_name";

    private SwipeRecyclerView rv_phone;
    private TextView tv_name;
    private EditText et_phone;
    private ImageView iv_add;
    private ImageView iv_contacts;
    private TextView tv_identity;
    private TextView tv_room;
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
    private LinearLayout ll_room;
    private LinearLayout ll_time;
    private KeyPhoneAdapter adapter;

    private int check = 0;
    private int keyType = 0;
    private String communityUuid = "";
    private String identityId = "";
    private long doorId;
    private String startTime = "";
    private String endTime = "";
    private String startTimeCustom = "";
    private String endTimeCustom = "";
    private String keyName;
    private UserModel userModel;
    private List<KeyIdentityEntity.ContentBean> identityList = new ArrayList<>();
    private List<String> identityStringList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userModel = new UserModel(this);
        initView();
        initData();
    }

    private void initView() {
        List<String> list = new ArrayList<>();
        rv_phone = findViewById(R.id.rv_phone);
        iv_add = findViewById(R.id.iv_add);
        iv_contacts = findViewById(R.id.iv_contacts);
        tv_name = findViewById(R.id.tv_name);
        et_phone = findViewById(R.id.et_phone);
        tv_identity = findViewById(R.id.tv_identity);
        tv_room = findViewById(R.id.tv_room);
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
        ll_room = findViewById(R.id.ll_room);
        ll_time = findViewById(R.id.ll_time);

        iv_add.setOnClickListener(singleListener);
        iv_contacts.setOnClickListener(singleListener);
        tv_identity.setOnClickListener(singleListener);
        tv_room.setOnClickListener(singleListener);
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

        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setSubmitBg();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        doorId = getIntent().getLongExtra(DOOR_ID, 0);
        communityUuid = getIntent().getStringExtra(COMMUNITY_UUID);
        keyName = getIntent().getStringExtra(KEY_NAME);
        tv_name.setText(keyName);
        userModel.getIdentity(1, communityUuid, this);
    }

    public void delete(int position) {
        adapter.list.remove(position);
        adapter.notifyDataSetChanged();
        setSubmitBg();
    }

    public void setChangeListener() {
        setSubmitBg();
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_key_send_key_phone, null);
    }

    @Override
    public String getHeadTitle() {
        return "发送钥匙";
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected boolean handClickEvent(View v) {
        Calendar curr;
        Date date;
        switch (v.getId()) {
            case R.id.iv_add:
                adapter.list.add("");
                adapter.notifyDataSetChanged();
                setSubmitBg();
                break;
            case R.id.iv_contacts://联系人
                break;
            case R.id.tv_identity:
                DisplayUtil.showInput(false, this);
                KeyStringPopWindowView areaPop = new KeyStringPopWindowView(this, identityStringList);
                areaPop.showPopupWindow(contentLayout);
                areaPop.setOnDismissListener(new PopupDismissListener());
                break;
            case R.id.tv_room:
                DisplayUtil.showInput(false, this);
                if (!TextUtils.isEmpty(communityUuid)) {
                    KeyRoomPopWindowView roomPop = new KeyRoomPopWindowView(this, communityUuid);
                    roomPop.setOnDismissListener(new PopupDismissListener());
                    roomPop.showPopupWindow(contentLayout);
                }
                setSubmitBg();
                break;
            case R.id.tv_one_year:
                clearText();
                keyType = 1;
                startTime = DateUtils.getStringDate(new Date(System.currentTimeMillis()), "");
                curr = Calendar.getInstance();
                curr.set(Calendar.YEAR, curr.get(Calendar.YEAR) + 1);
                date = curr.getTime();
                endTime = DateUtils.getStringDate(date, "");

                tv_one_year.setBackgroundResource(R.drawable.shape_key_choose_select_text);
                if (View.VISIBLE == ll_time.getVisibility()) {
                    ll_time.setVisibility(View.GONE);
                }
                setSubmitBg();
                break;
            case R.id.tv_two_year:
                clearText();
                keyType = 2;
                startTime = DateUtils.getStringDate(new Date(System.currentTimeMillis()), "");
                curr = Calendar.getInstance();
                curr.set(Calendar.YEAR, curr.get(Calendar.YEAR) + 2);
                date = curr.getTime();
                endTime = DateUtils.getStringDate(date, "");

                tv_two_year.setBackgroundResource(R.drawable.shape_key_choose_select_text);
                if (View.VISIBLE == ll_time.getVisibility()) {
                    ll_time.setVisibility(View.GONE);
                }
                setSubmitBg();
                break;
            case R.id.tv_three_year:
                clearText();
                keyType = 3;

                startTime = DateUtils.getStringDate(new Date(System.currentTimeMillis()), "");
                curr = Calendar.getInstance();
                curr.set(Calendar.YEAR, curr.get(Calendar.YEAR) + 3);
                date = curr.getTime();
                endTime = DateUtils.getStringDate(date, "");

                tv_three_year.setBackgroundResource(R.drawable.shape_key_choose_select_text);
                if (View.VISIBLE == ll_time.getVisibility()) {
                    ll_time.setVisibility(View.GONE);
                }
                setSubmitBg();
                break;
            case R.id.tv_forever:
                clearText();
                keyType = 4;

                startTime = "";
                endTime = "";

                tv_forever.setBackgroundResource(R.drawable.shape_key_choose_select_text);
                if (View.VISIBLE == ll_time.getVisibility()) {
                    ll_time.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_one_month:
                clearText();
                keyType = 5;

                startTime = DateUtils.getStringDate(new Date(System.currentTimeMillis()), "");
                curr = Calendar.getInstance();
                curr.set(Calendar.MONTH, curr.get(Calendar.MONTH) + 1);
                date = curr.getTime();
                endTime = DateUtils.getStringDate(date, "");

                tv_one_month.setBackgroundResource(R.drawable.shape_key_choose_select_text);
                if (View.VISIBLE == ll_time.getVisibility()) {
                    ll_time.setVisibility(View.GONE);
                }
                setSubmitBg();
                break;
            case R.id.tv_one_week:
                clearText();
                keyType = 6;

                startTime = DateUtils.getStringDate(new Date(System.currentTimeMillis()), "");
                curr = Calendar.getInstance();
                curr.set(Calendar.DAY_OF_MONTH, curr.get(Calendar.DAY_OF_MONTH) + 7);
                date = curr.getTime();
                endTime = DateUtils.getStringDate(date, "");

                tv_one_week.setBackgroundResource(R.drawable.shape_key_choose_select_text);
                if (View.VISIBLE == ll_time.getVisibility()) {
                    ll_time.setVisibility(View.GONE);
                }
                setSubmitBg();
                break;
            case R.id.tv_one_day:
                clearText();
                keyType = 7;

                startTime = DateUtils.getStringDate(new Date(System.currentTimeMillis()), "");
                curr = Calendar.getInstance();
                curr.set(Calendar.DAY_OF_MONTH, curr.get(Calendar.DAY_OF_MONTH) + 1);
                date = curr.getTime();
                endTime = DateUtils.getStringDate(date, "");

                tv_one_day.setBackgroundResource(R.drawable.shape_key_choose_select_text);
                if (View.VISIBLE == ll_time.getVisibility()) {
                    ll_time.setVisibility(View.GONE);
                }
                setSubmitBg();
                break;
            case R.id.tv_custom:
                clearText();
                keyType = 8;
                tv_custom.setBackgroundResource(R.drawable.shape_key_choose_select_text);
                ll_time.setVisibility(View.VISIBLE);

                Calendar nowCalender = Calendar.getInstance();
                int year = nowCalender.get(Calendar.YEAR);
                int month = nowCalender.get(Calendar.MONTH) + 1;
                int day = nowCalender.get(Calendar.DAY_OF_MONTH);
                int hour = nowCalender.get(Calendar.HOUR_OF_DAY);
                int minute = nowCalender.get(Calendar.MINUTE);
                int second = nowCalender.get(Calendar.SECOND);
                startTimeCustom = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
                String nowTime = numFormat(year) + "-" + numFormat(month) + "-" + numFormat(day) + " " + numFormat(hour) + ":" + numFormat(minute) + ":" + numFormat(second);
                tv_time_start.setText(nowTime);

                setSubmitBg();
                break;
            case R.id.tv_time_start:
                DisplayUtil.showInput(false, this);
                KeyTimePopWindowView popWindowView = new KeyTimePopWindowView(this, 1);
                popWindowView.showPopupWindow(contentLayout);
                popWindowView.setOnDismissListener(new PopupDismissListener());
                setSubmitBg();
                break;
            case R.id.tv_time_end:
                DisplayUtil.showInput(false, this);
                KeyTimePopWindowView pop = new KeyTimePopWindowView(this, 2);
                pop.showPopupWindow(contentLayout);
                pop.setOnDismissListener(new PopupDismissListener());
                setSubmitBg();
                break;
            case R.id.tv_time_invalid:
                KeyTimePopWindowView popInvalid = new KeyTimePopWindowView(this, 3);
                popInvalid.showPopupWindow(contentLayout);
                popInvalid.setOnDismissListener(new PopupDismissListener());
                setSubmitBg();
                break;
            case R.id.iv_check:
                setSubmitBg();
                if (0 == check) {
                    check = 1;
                    iv_check.setBackgroundResource(R.drawable.ic_key_send_check);
                } else {
                    check = 0;
                    iv_check.setBackgroundResource(R.drawable.ic_key_send_no_check);
                }
                setSubmitBg();
                break;
            case R.id.tv_send:
                DisplayUtil.showInput(false, this);
                if (!setSubmitBg()) {
                    break;
                }
                StringBuilder phone = new StringBuilder();
                if (!TextUtils.isEmpty(et_phone.getText().toString().trim())) {
                    if (et_phone.getText().toString().trim().length() != 11) {
                        ToastUtil.showShortToast(this, "手机号码格式不正确");
                        break;
                    }
                    phone.append(et_phone.getText().toString().trim()).append(" ");
                }
                for (int i = 0; i < adapter.list.size(); i++) {
                    if (adapter.list.get(i).length() != 11) {
                        ToastUtil.showShortToast(this, "手机号码格式不正确");
                        break;
                    }
                    phone.append(adapter.list.get(i));
                    if (i != adapter.list.size() - 1) {
                        phone.append(" ");
                    }
                }
                String roomName = tv_room.getText().toString().trim();
                userModel.sendKeyByPhone(2, doorId, phone.toString(), keyName,
                        identityId, "", roomName, startTime, endTime, this);
                break;
        }
        return super.handClickEvent(v);
    }

    public void setTimeText(int inputText, String time, int y, int m, int d, int h, int mi) {
        switch (inputText) {
            case 1:
                String nowTime = startTime = DateUtils.getStringDate(new Date(System.currentTimeMillis()), "");
                String selectTime = y + "-" + m + "-" + d + " " + h + ":" + mi + ":" + 0;
                String selectText = numFormat(y) + "-" + numFormat(m) + "-" + numFormat(d) + " " + numFormat(h) + ":" + numFormat(mi) + ":" + "00";
                int status = DateUtils.compareDate(nowTime, selectTime);
                if (1 == status) {
                    ToastUtil.showShortToast(this, "开始时间不能在当前时间之前");
                    break;
                }
                startTimeCustom = selectTime;
                tv_time_start.setText(selectText);
                break;
            case 2:
                String selectDate = y + "-" + m + "-" + d + " " + h + ":" + mi + ":" + 0;
                String text = numFormat(y) + "-" + numFormat(m) + "-" + numFormat(d) + " " + numFormat(h) + ":" + numFormat(mi) + ":" + "00";
                int type = DateUtils.compareDate(startTimeCustom, selectDate);
                if (1 == type) {
                    ToastUtil.showShortToast(this, "结束时间不能在开始时间之前");
                    break;
                }
                endTimeCustom = selectDate;
                tv_time_end.setText(text);
                break;
            case 3:
                tv_time_invalid.setText(time);
                break;
        }
        setSubmitBg();
    }

    private String numFormat(int num) {
        return num < 10 ? "0" + num : "" + num;
    }

    private boolean setSubmitBg() {
        boolean canSubmit = true;
        if (TextUtils.isEmpty(et_phone.getText().toString().trim())) {
            canSubmit = false;
        }
        for (int i = 0; i < adapter.list.size(); i++) {
            if (TextUtils.isEmpty(adapter.list.get(i))) {
                canSubmit = false;
            }
        }
        if (TextUtils.isEmpty(identityId)) {
            canSubmit = false;
        }
        String roomName = tv_room.getText().toString().trim();
        if (View.VISIBLE == ll_room.getVisibility() && TextUtils.isEmpty(roomName)) {
            canSubmit = false;
        }
        if (8 == keyType) {
            if (TextUtils.isEmpty(startTimeCustom)) {
                canSubmit = false;
            } else if (TextUtils.isEmpty(endTimeCustom)) {
                canSubmit = false;
            }
            startTime = startTimeCustom;
            endTime = endTimeCustom;
        }
        if (0 == keyType) {
            canSubmit = false;
        }
        if (canSubmit) {
            tv_send.setBackgroundResource(R.drawable.shape_key_submit_blue_text);
        } else {
            tv_send.setBackgroundResource(R.drawable.shape_key_submit_text);
        }
        return canSubmit;
    }

    public void setIdentity(int position) {
        try {
            tv_identity.setText(identityList.get(position).getIdentityName());
            identityId = identityList.get(position).getId();
            int isPower = identityList.get(position).getIsPower();
            if (0 == isPower) {//0为业主 1为管理
                ll_room.setVisibility(View.VISIBLE);
            } else {
                ll_room.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setSubmitBg();
    }

    public void setRoom(String roomName) {
        tv_room.setText(roomName);
        setSubmitBg();
    }

    /**
     * 还原text
     */
    private void clearText() {
        DisplayUtil.showInput(false, this);
        tv_one_year.setBackgroundResource(R.drawable.shape_key_choose_text);
        tv_two_year.setBackgroundResource(R.drawable.shape_key_choose_text);
        tv_three_year.setBackgroundResource(R.drawable.shape_key_choose_text);
        tv_forever.setBackgroundResource(R.drawable.shape_key_choose_text);
        tv_one_month.setBackgroundResource(R.drawable.shape_key_choose_text);
        tv_one_week.setBackgroundResource(R.drawable.shape_key_choose_text);
        tv_one_day.setBackgroundResource(R.drawable.shape_key_choose_text);
        tv_custom.setBackgroundResource(R.drawable.shape_key_choose_text);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        KeyIdentityEntity entity = GsonUtils.gsonToBean(result, KeyIdentityEntity.class);
                        identityList.addAll(entity.getContent());
                        for (KeyIdentityEntity.ContentBean bean : identityList) {
                            identityStringList.add(bean.getIdentityName());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(result)) {
                    ToastUtil.showShortToast(this, "钥匙发送成功");
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }

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
