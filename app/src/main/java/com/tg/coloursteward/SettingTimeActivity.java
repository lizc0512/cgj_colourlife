package com.tg.coloursteward;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.database.SharedPreferencesTools;
import com.tg.coloursteward.view.ActivityHeaderView;
import com.tg.coloursteward.view.TimeChoosePopWindow;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.coloursteward.view.dialog.ToastFactory;

import java.util.Calendar;

/**
 * 设置考勤时间
 * Created by prince70 on 2018/3/28.
 */

public class SettingTimeActivity extends BaseActivity implements View.OnClickListener {
    private ActivityHeaderView headerView;
    private LinearLayout ll_time_s;
    private LinearLayout ll_time_x;
    private Calendar startCalendar;
    private TextView tv_time_s;
    private TextView tv_time_x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_time);
        initView();
    }

    private void initView() {
        headerView = (ActivityHeaderView) findViewById(R.id.header_setting_time);
        headerView.setTitle("设置考勤时间");
        headerView.setRightText("保存");
        headerView.setRightTextColor(Color.WHITE);
        headerView.setListenerRight(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesTools.saveSysMap(SettingTimeActivity.this, "workTime", tv_time_s.getText().toString());
                SharedPreferencesTools.saveSysMap(SettingTimeActivity.this, "breakTime", tv_time_x.getText().toString());
                finish();
//                ToastFactory.showToast(SettingTimeActivity.this, "时间保存成功");
            }
        });
        ll_time_s = (LinearLayout) findViewById(R.id.ll_time_s);
        ll_time_s.setOnClickListener(this);
        ll_time_x = (LinearLayout) findViewById(R.id.ll_time_x);
        ll_time_x.setOnClickListener(this);
        startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.HOUR_OF_DAY, 9);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        tv_time_s = (TextView) findViewById(R.id.tv_time_s);
        tv_time_x = (TextView) findViewById(R.id.tv_time_x);
    }

    @Override
    public View getContentView() {
//        return getLayoutInflater().inflate(R.layout.activity_setting_time,null);
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_time_s:
                TimeChoosePopWindow timeChoosePopWindow = new TimeChoosePopWindow(this, startCalendar);
                timeChoosePopWindow.setTimeChooseListener(new TimeChoosePopWindow.TimeChooseListener() {
                    @Override
                    public void timeChoose(int hour, int minute) {
                        tv_time_s.setText((hour > 10 ? hour : "0" + hour) + ":" + (minute > 10 ? minute : "0" + minute));
                    }
                });
                timeChoosePopWindow.show();
                break;
            case R.id.ll_time_x:
                TimeChoosePopWindow timeChoosePopWindow2 = new TimeChoosePopWindow(this, startCalendar);
                timeChoosePopWindow2.setTimeChooseListener(new TimeChoosePopWindow.TimeChooseListener() {
                    @Override
                    public void timeChoose(int hour, int minute) {
                        tv_time_x.setText((hour > 10 ? hour : "0" + hour) + ":" + (minute > 10 ? minute : "0" + minute));
                    }
                });
                timeChoosePopWindow2.show();
                break;
            default:
                break;
        }
    }
}
