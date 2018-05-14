package com.tg.coloursteward;

import com.tg.coloursteward.base.BaseActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

/**
 * 扫描结果
 */
public class ScanResultActivity extends BaseActivity {
    private final static String TAG = "ScanResultActivity";


    private TextView tv_result;

    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        prepareView();
        prepareData();

    }

    private void init() {
        Intent intent = getIntent();
        result = intent.getStringExtra("result");
    }

    private void prepareView() {
        tv_result = (TextView) findViewById(R.id.tv_result);
    }

    private void prepareData() {
        showResult(result);
    }

    // 更新信息
    public void showResult(String result) {

        tv_result.setText(result);

    }

    @Override
    public View getContentView() {
        // TODO Auto-generated method stub
        return getLayoutInflater().inflate(R.layout.activity_scan_result, null);
    }

    @Override
    public String getHeadTitle() {
        // TODO Auto-generated method stub
        return "扫描结果";
    }

}
