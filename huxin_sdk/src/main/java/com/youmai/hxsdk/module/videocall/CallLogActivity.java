package com.youmai.hxsdk.module.videocall;

import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;

public class CallLogActivity extends SdkBaseActivity {
    private static final String TAG = CallLogActivity.class.getName();
    private ImageView iv_left_cancel;
    private TextView tv_title;
    private TextView tv_right_sure;
    private TextView tv_look_video;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);
        setHeadTitle();
        ivitView();
    }

    private void ivitView() {
        tv_look_video = findViewById(R.id.tv_look_video);
        tv_look_video.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    private void setHeadTitle() {
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("通话记录");
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
}
