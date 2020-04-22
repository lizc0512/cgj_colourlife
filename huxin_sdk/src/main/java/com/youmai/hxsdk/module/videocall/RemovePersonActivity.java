package com.youmai.hxsdk.module.videocall;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;

public class RemovePersonActivity extends SdkBaseActivity {
    private static final String TAG = RemovePersonActivity.class.getName();
    private ImageView iv_left_cancel;
    private TextView tv_title;
    private TextView tv_right_sure;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_person);
        initView();
        setHeadTitle();
    }

    private void setHeadTitle() {
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("当前通话成员");
        tv_right_sure = findViewById(R.id.tv_right_sure);
        tv_right_sure.setVisibility(View.INVISIBLE);
        iv_left_cancel = findViewById(R.id.tv_left_cancel);
        iv_left_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
    }
}
