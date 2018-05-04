package com.tg.coloursteward;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.tg.coloursteward.adapter.AddWifiListAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.database.SharedPreferencesTools;
import com.tg.coloursteward.info.WiFiDetail;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 考勤设置
 * Created by prince70 on 2018/3/28.
 */

public class SignInSettingActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private TextView tv_go_setting_time;
    private TextView tv_go_setting_wifi;
    private TextView tv_have_setting;

    private List<WiFiDetail> addwifiDetails = new ArrayList<>();
    private AddWifiListAdapter addAdapter;
    private ListView lv_add_wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sign_in_setting);
        initView();
    }

    private void initView() {
        tv_go_setting_time = (TextView) findViewById(R.id.tv_go_setting_time);
        tv_go_setting_wifi = (TextView) findViewById(R.id.tv_go_setting_wifi);
        tv_go_setting_time.setOnClickListener(this);
        tv_go_setting_wifi.setOnClickListener(this);
        tv_have_setting = (TextView) findViewById(R.id.tv_have_setting);

        String workTime = SharedPreferencesTools.getSysMapStringValue(this, "workTime");
        String breakTime = SharedPreferencesTools.getSysMapStringValue(this, "breakTime");
        if (!workTime.equals("") && !breakTime.equals("")) {
            tv_have_setting.setText(workTime + "-" + breakTime);
        }
//
//        找到保存的  add 进去
        List<WiFiDetail> all = DataSupport.findAll(WiFiDetail.class);
        for (WiFiDetail detail : all) {
            addwifiDetails.add(detail);
        }
        addAdapter = new AddWifiListAdapter(this, addwifiDetails);
        lv_add_wifi = (ListView) findViewById(R.id.lv_add_wifi);
        lv_add_wifi.setAdapter(addAdapter);
//        lv_add_wifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                int selectPosition = addAdapter.getSelectPosition();
//                WiFiDetail wiFiDetail = addwifiDetails.get(selectPosition);
//                ToastFactory.showToast(SignInSettingActivity.this, wiFiDetail.getSSID());
//            }
//        });
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_sign_in_setting, null);
    }

    @Override
    public String getHeadTitle() {
        return "考勤设置";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_go_setting_time:
                startActivity(new Intent(this, SettingTimeActivity.class));
                break;
            case R.id.tv_go_setting_wifi:
                startActivity(new Intent(this, SettingWiFiActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String workTime = SharedPreferencesTools.getSysMapStringValue(this, "workTime");
        String breakTime = SharedPreferencesTools.getSysMapStringValue(this, "breakTime");
        if (!workTime.equals("") && !breakTime.equals("")) {
            tv_have_setting.setText(workTime + "-" + breakTime);
        }
    }

    //设置ListView的点击事件,实现CheckBox联动效果.
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AddWifiListAdapter.ViewHolder holder = (AddWifiListAdapter.ViewHolder) view.getTag();
        holder.checkBox.toggle();
//        holder.checkBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int selectPosition = addAdapter.getSelectPosition();
//                ToastFactory.showToast(SignInSettingActivity.this, selectPosition + "位置");
//            }
//        });
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ToastFactory.showToast(SignInSettingActivity.this, "++++++++++");
            }
        });


    }
}
