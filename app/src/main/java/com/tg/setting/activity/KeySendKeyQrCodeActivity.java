package com.tg.setting.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
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
import com.tg.setting.entity.KeyIdentityEntity;
import com.tg.setting.entity.KeyQrCodeEntity;
import com.tg.setting.view.KeyStringPopWindowView;
import com.tg.setting.view.KeyTimePopWindowView;
import com.tg.user.model.UserModel;
import com.youmai.hxsdk.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.tg.setting.activity.KeySendKeyListActivity.COMMUNITY_NAME;
import static com.tg.setting.activity.KeySendKeyListActivity.COMMUNITY_UUID;
import static com.tg.setting.activity.KeySendKeyListActivity.DOOR_ID;
import static com.tg.setting.activity.KeySendKeyListActivity.DOOR_QRCODE;
import static com.tg.setting.activity.KeySendKeyListActivity.KEY_CONTENT;

/**
 * 乐开-二维码
 *
 * @author hxg 2019.07.18
 */
public class KeySendKeyQrCodeActivity extends BaseActivity implements HttpResponse {
    private TextView tv_name;
    private TextView tv_identity;
    private TextView tv_title_name;
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
    private LinearLayout ll_time;
    private int check = 0;
    private int keyType = 0;
    private String communityUuid = "";
    private String communityName = "";
    private String identityId = "";
    private String doorId;
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
        tv_name = findViewById(R.id.tv_name);
        tv_title_name = findViewById(R.id.tv_title_name);
        tv_identity = findViewById(R.id.tv_identity);
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
        ll_time = findViewById(R.id.ll_time);
        tv_identity.setOnClickListener(singleListener);
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
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        Intent intent = getIntent();
        doorId = intent.getStringExtra(DOOR_ID);
        communityUuid = intent.getStringExtra(COMMUNITY_UUID);
        keyName = intent.getStringExtra(KEY_CONTENT);
        communityName = intent.getStringExtra(COMMUNITY_NAME);
        tv_name.setText(keyName);
        userModel.getIdentity(1, communityUuid, this);
    }


    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_key_send_key_code, null);
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
            case R.id.tv_identity:
                DisplayUtil.showInput(false, this);
                KeyStringPopWindowView areaPop = new KeyStringPopWindowView(this, identityStringList);
                areaPop.showPopupWindow(contentLayout);
                areaPop.setOnDismissListener(new PopupDismissListener());
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
                ll_time.setVisibility(View.GONE);
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
                ll_time.setVisibility(View.GONE);
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
                ll_time.setVisibility(View.GONE);
                setSubmitBg();
                break;
            case R.id.tv_forever:
                clearText();
                keyType = 4;
                startTime = "";
                endTime = "";
                tv_forever.setBackgroundResource(R.drawable.shape_key_choose_select_text);
                ll_time.setVisibility(View.GONE);
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
                ll_time.setVisibility(View.GONE);
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
                ll_time.setVisibility(View.GONE);
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
                ll_time.setVisibility(View.GONE);
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
                UserModel userModel = new UserModel(KeySendKeyQrCodeActivity.this);
                userModel.sendKeyByQrCode(2, doorId, communityUuid, keyName, communityName + keyName, identityId,
                        tv_time_invalid.getText().toString(), startTime, endTime, this);
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
                String invalidTime = numFormat(y) + "-" + numFormat(m) + "-" + numFormat(d) + " " + numFormat(h) + ":" + numFormat(mi) + ":" + "00";
                tv_time_invalid.setText(invalidTime);
                break;
        }
        setSubmitBg();
    }

    private String numFormat(int num) {
        return num < 10 ? "0" + num : "" + num;
    }

    private boolean setSubmitBg() {
        boolean canSubmit = true;
        if (TextUtils.isEmpty(identityId)) {
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

        String invalidTime = tv_time_invalid.getText().toString();
        if (TextUtils.isEmpty(invalidTime)) {
            canSubmit = false;
        }
        if (canSubmit) {
            tv_send.setBackgroundResource(R.drawable.shape_key_submit_blue_text);
        } else {
            tv_send.setBackgroundResource(R.drawable.shape_key_submit_text);
        }
        return canSubmit;
    }

    private int isPower;

    public void setIdentity(int position) {
        try {
            tv_identity.setText(identityList.get(position).getIdentityName());
            identityId = identityList.get(position).getId();
            isPower = identityList.get(position).getIsPower();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    try {
                        KeyQrCodeEntity keyQrCodeEntity = GsonUtils.gsonToBean(result, KeyQrCodeEntity.class);
                        String content = keyQrCodeEntity.getContent();
                        Intent intent = new Intent(KeySendKeyQrCodeActivity.this, KeySendKeyCodeActivity.class);
                        intent.putExtra(COMMUNITY_NAME, communityName);
                        intent.putExtra(DOOR_QRCODE, content);
                        intent.putExtra(KEY_CONTENT, keyName);
                        startActivityForResult(intent, 1000);
                    } catch (Exception e) {

                    }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == 200) {
                setResult(Activity.RESULT_OK);
                finish();
            } else {
                identityList.clear();
                identityStringList.clear();
                identityId = "";
                startTime = "";
                endTime = "";
                startTimeCustom = "";
                endTimeCustom = "";
                keyType = 0;
                recreate();
            }
        }
    }
}
