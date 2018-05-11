package com.youmai.hxsdk.module.remind;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.sp.SPDataUtil;
import com.youmai.hxsdk.utils.AbDateUtil;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.CommonUtils;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.utils.ToastUtil;
import com.youmai.hxsdk.view.pickerview.OptionsPickerView;
import com.youmai.hxsdk.view.pickerview.listener.CustomListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 作者：create by YW
 * 日期：2017.11.22 15:42
 * 描述：设置提醒
 */
public class SetRemindActivity extends SdkBaseActivity implements View.OnClickListener {

    public static final String CACHE_MSG_BEAN = "cache_msg_bean";
    public static final String REMIND_BEAN = "remind_bean";
    public static final String REMIND_NUMBER = "remindNumber";
    public static final int RESULT_REMIND_CODE = 701;
    public static final int RESULT_REMIND_CONTACTS_CODE = 702;

    private Context mContext;

    private TextView tv_finish;
    private FrameLayout fl_remind_logo;
    private ImageView iv_remind_logo;
    private LinearLayout ll_remind_time;
    private OptionsPickerView pvCustomLongTimes;
    private TextView tv_remind_time;
    private EditText et_remind_content;

    private Switch remind_switch;
    private LinearLayout ll_remind_number_parent;
    private EditText et_remind_number;
    private ImageView iv_remind_contacts;

    private CacheMsgBean cacheMsgBean;
    private int iconNumRes = 0;
    private boolean isSwitch = false;

    TextWatcher mPhoneNumWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isSwitch) {
                String remindTime = tv_remind_time.getText().toString();
                String remindNumber = et_remind_number.getText().toString();
                if (!StringUtils.isEmpty(remindTime) && !StringUtils.isEmpty(remindNumber)) {
                    tv_finish.setEnabled(true);
                    tv_finish.setTextColor(ContextCompat.getColor(mContext, R.color.hx_toolbar_font));
                } else {
                    tv_finish.setEnabled(false);
                    tv_finish.setTextColor(ContextCompat.getColor(mContext, R.color.hxs_color_gray20));
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_back) {
            onBackPressed();
        } else if (id == R.id.tv_title_right) {
            remindSave();
        } else if (id == R.id.fl_remind_logo) {
            showPopGuide();
        } else if (id == R.id.ll_remind_time) {
            pvCustomLongTimes.show();
        } else if (id == R.id.iv_remind_close) {
            dismissPop();
        } else if (id == R.id.iv_remind_contacts) {
            jumpContacts();
        } else if (id == R.id.iv_remind_dial) {
            setIconInfo(0);
        } else if (id == R.id.iv_remind_shopping) {
            setIconInfo(1);
        } else if (id == R.id.iv_remind_meeting) {
            setIconInfo(2);
        } else if (id == R.id.iv_remind_travel) {
            setIconInfo(3);
        } else if (id == R.id.iv_remind_dating) {
            setIconInfo(4);
        } else if (id == R.id.iv_remind_hospital) {
            setIconInfo(5);
        } else if (id == R.id.iv_remind_repayment) {
            setIconInfo(6);
        } else if (id == R.id.iv_remind_birthday) {
            setIconInfo(7);
        } else if (id == R.id.iv_remind_fitness) {
            setIconInfo(8);
        }
    }

    private void jumpContacts() {
        Intent intent = new Intent();
        intent.setAction("com.youmai.huxin.forward");
        intent.putExtra("type", "remind_type");
        startActivityForResult(intent, RESULT_REMIND_CONTACTS_CODE);
        hideSoftKey();
    }

    private void setIconInfo(int num) {
        iv_remind_logo.setImageResource(RemindItem.ITEM_DRAWABLES[num]);
        iconNumRes = num;
        dismissPop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getApplicationContext();
        setContentView(R.layout.hx_activity_remind);

        initTitleBar();
        initView();
        initData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideSoftKey();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_REMIND_CONTACTS_CODE && resultCode == 200) { //提醒号码返回
            String remindNumber = data.getStringExtra(REMIND_NUMBER);
            et_remind_number.setText(remindNumber);
        }
    }

    private void initTitleBar() {
        findViewById(R.id.tv_back).setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText("设置提醒");
        tv_finish = (TextView) findViewById(R.id.tv_title_right);
        tv_finish.setText("完成");
        tv_finish.setTextColor(getResources().getColor(R.color.hxs_color_gray20));
        tv_finish.setOnClickListener(this);
    }

    private void initView() {
        fl_remind_logo = (FrameLayout) findViewById(R.id.fl_remind_logo);
        iv_remind_logo = (ImageView) findViewById(R.id.iv_remind_logo);
        ll_remind_time = (LinearLayout) findViewById(R.id.ll_remind_time);
        tv_remind_time = (TextView) findViewById(R.id.tv_remind_time);
        et_remind_content = (EditText) findViewById(R.id.et_remind_content);
        fl_remind_logo.setOnClickListener(this);
        ll_remind_time.setOnClickListener(this);

        remind_switch = (Switch) findViewById(R.id.remind_switch);
        ll_remind_number_parent = (LinearLayout) findViewById(R.id.ll_remind_number_parent);
        et_remind_number = (EditText) findViewById(R.id.et_remind_number);
        et_remind_number.addTextChangedListener(mPhoneNumWatcher);

        iv_remind_contacts = (ImageView) findViewById(R.id.iv_remind_contacts);
        iv_remind_contacts.setOnClickListener(this);

        remind_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ll_remind_number_parent.setVisibility(View.VISIBLE);
                    isSwitch = true;
                } else {
                    ll_remind_number_parent.setVisibility(View.GONE);
                    isSwitch = false;
                }

                String remindTime = tv_remind_time.getText().toString();
                String remindNumber = et_remind_number.getText().toString();
                if (StringUtils.isEmpty(remindTime) || StringUtils.isEmpty(remindNumber)) {
                    tv_finish.setEnabled(true);
                    tv_finish.setTextColor(ContextCompat.getColor(mContext, R.color.hxs_color_gray20));
                }
            }
        });

    }

    private void initData() {
        cacheMsgBean = getIntent().getParcelableExtra(CACHE_MSG_BEAN);
        initCustomTime();
    }

    private void initCustomTime() {
        final ArrayList<String> dateWithYear = new ArrayList<>(366);
        final ArrayList<String> day = new ArrayList<>(366);
        final ArrayList<String> hour = new ArrayList<>(24);
        final ArrayList<String> mins = new ArrayList<>(60);
        Calendar start = Calendar.getInstance();

        start.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH));

        for (int i = 1; i <= 365; i++) {
            Date time = start.getTime();
            dateWithYear.add(AbDateUtil.getStringByFormat(time, "yyyy年MM月dd日"));
            day.add(AbDateUtil.getTimeWithYearDay(time));
            start.add(Calendar.DAY_OF_YEAR, 1);
        }

        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                hour.add("0" + i);
            } else {
                hour.add("" + i);
            }
        }

        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                mins.add("0" + i);
            } else {
                mins.add("" + i);
            }
        }

        int hor = 0;
        int min = 0;
        int[] minAndSec = AbDateUtil.getCurrentMinAndSec();
        if (minAndSec != null) {
            hor = minAndSec[0];
            min = minAndSec[1];
        }

        pvCustomLongTimes = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String date = dateWithYear.get(options1) + " " + hour.get(options2) + ":" + mins.get(options3);
                tv_remind_time.setText(getTime(date));
                if (isSwitch && StringUtils.isEmpty(et_remind_number.getText().toString())) {
                    return;
                }
                tv_finish.setTextColor(getResources().getColor(R.color.hxs_color_white));
            }
        })
                .setLayoutRes(R.layout.hx_pickerview_custom_time_layout, new CustomListener() {
                    TextView tv_current_time;

                    @Override
                    public void customLayout(View v) {
                        tv_current_time = (TextView) v.findViewById(R.id.tv_current_time);
                        TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        TextView tvCancel = (TextView) v.findViewById(R.id.tv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomLongTimes.returnData();
                                pvCustomLongTimes.dismiss();
                            }
                        });
                        tvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomLongTimes.dismiss();
                            }
                        });
                    }

                    @Override
                    public void customCurrentDate(String date) {
                    }

                    @Override
                    public void customOptions(int options1, int options2, int options3) {
                        String date = dateWithYear.get(options1) + " " + hour.get(options2) + ":" + mins.get(options3);
                        if (tv_current_time != null) {
                            tv_current_time.setText(getTime(date));
                        }
                    }
                })

                .setTitleText("")
                .setContentTextSize(16)//设置滚轮文字大小
                .setSelectOptions(0, hor, min)//默认选中项
                .setBgColor(Color.WHITE)
                .setDividerColor(0xFFD8D8D8) //设置分割线的颜色 0xFF24AD9D
                .setLineSpacingMultiplier(2.0f)
                .setTextXOffset(0, 0, 0)
                .setTextColorCenter(getResources().getColor(R.color.hxs_color_black4))
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setLabels("", "", "")
                .setBackgroundId(0xFFFFFF) //设置外部遮罩颜色
                .setOutSideCancelable(false)
                .isDialog(true)
                .setCyclic(false, true, true)
                .build();
        pvCustomLongTimes.setNPicker(day, hour, mins);

    }

    private String getTime(String date) {
        Date dateByFormat = AbDateUtil.getDateByFormat(date, "yyyy年MM月dd日 HH:mm");
        return AbDateUtil.getTimeWithWeekDay(dateByFormat);
    }

    private void remindSave() {
        if (!CommonUtils.isNetworkAvailable(this)) {
            ToastUtil.showToast(this, getString(R.string.hx_network_exception_check));
            return;
        }
        String remindTime = tv_remind_time.getText().toString();

        if (StringUtils.isEmpty(remindTime)) {
            return;
        }

        if (isSwitch && StringUtils.isEmpty(et_remind_number.getText().toString())) {
            Toast.makeText(mContext, "请输入号码", Toast.LENGTH_SHORT).show();
            return;
        }

        final long timeFromWeekday = AbDateUtil.getTimeFromWeekday(remindTime);
        String dateByFormat = AbDateUtil.getStringByFormat(timeFromWeekday, AbDateUtil.dateFormatYMDHM);
        final String dateWithDay = AbDateUtil.getTimeWithWeekDay(dateByFormat);

        String remindNumber = "";
        if (isSwitch) {
            remindNumber = et_remind_number.getText().toString();
        }

        IPostListener listener = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespBaseBean respBaseBean = GsonUtil.parse(response, RespBaseBean.class);
                if (respBaseBean != null && respBaseBean.isSuccess()) {
                    Intent intent = new Intent();

                    long msgId = cacheMsgBean.getMsgId();
                    String remind = et_remind_content.getText().toString();

                    String savedStr = timeFromWeekday + "@" + remind + "@" + msgId;
                    AppUtils.setStringSharedPreferences(mContext, "last_set_remind", savedStr);

                    intent.putExtra("msg_id", cacheMsgBean.getMsgId());
                    intent.putExtra("remind_time", timeFromWeekday);
                    intent.putExtra("remind", remind);
                    intent.putExtra("remindType", iconNumRes);

                    if (SPDataUtil.getFirstRemind(mContext)) {
                        Toast.makeText(mContext, "设置成功", Toast.LENGTH_LONG).show();
                    } else {
                        RemindBean remindBean = new RemindBean(
                                iconNumRes,
                                remind,
                                dateWithDay,
                                cacheMsgBean.getReceiverUserId());
                        intent.putExtra(REMIND_BEAN, remindBean);
                    }

                    setResult(RESULT_REMIND_CODE, intent);
                    finish();
                } else if (respBaseBean != null && respBaseBean.getS().equals("-1")) {
                    if (respBaseBean.getM() != null) {
                        Toast.makeText(mContext, respBaseBean.getM(), Toast.LENGTH_LONG).show();
                    }
                } else if (respBaseBean != null && respBaseBean.getS().equals("-2")) {
                    if (respBaseBean.getM() != null) {
                        Toast.makeText(mContext, "设置的提醒时间已过期", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(mContext, "设置失败", Toast.LENGTH_LONG).show();
                }
            }
        };

        hideSoftKey();
    }

    private void hideSoftKey() {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(et_remind_content.getWindowToken(), 0);
    }

    PopupWindow popupWindow;

    private void showPopGuide() {

        View popupView = LayoutInflater.from(mContext).inflate(R.layout.hx_remind_logo, null);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.update();
        int yOffset = iv_remind_logo.getHeight(); //下移高度
        if (!popupWindow.isShowing()) {
            popupWindow.showAsDropDown(iv_remind_logo, 0, yOffset);
        }

        popupView.findViewById(R.id.iv_remind_close).setOnClickListener(this);
        popupView.findViewById(R.id.iv_remind_dial).setOnClickListener(this);
        popupView.findViewById(R.id.iv_remind_shopping).setOnClickListener(this);
        popupView.findViewById(R.id.iv_remind_meeting).setOnClickListener(this);
        popupView.findViewById(R.id.iv_remind_travel).setOnClickListener(this);
        popupView.findViewById(R.id.iv_remind_dating).setOnClickListener(this);
        popupView.findViewById(R.id.iv_remind_hospital).setOnClickListener(this);
        popupView.findViewById(R.id.iv_remind_repayment).setOnClickListener(this);
        popupView.findViewById(R.id.iv_remind_birthday).setOnClickListener(this);
        popupView.findViewById(R.id.iv_remind_fitness).setOnClickListener(this);

    }

    private void dismissPop() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

}
