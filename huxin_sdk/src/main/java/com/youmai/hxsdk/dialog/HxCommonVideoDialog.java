package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.youmai.hxsdk.R;

public class HxCommonVideoDialog extends Dialog {
    private Context context;
    private Button btn_right;//确定按钮
    private Button btn_left;//取消按钮
    private String titleStr;//从外界设置的title文本
    private TextView titleTv;
    private String leftText, rightText;
    private View.OnClickListener rightListenner, leftListenner;

    private HxCommonVideoDialog(Build build) {
        super(build.context, R.style.qiniu_common_dialog);
        this.titleStr = build.contentText;
        this.context = build.context;
        this.rightListenner = build.okClick;
        this.leftListenner = build.cacel;
        this.leftText=build.leftText;
        this.rightText=build.rightText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_dialog_common_video);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        if (rightListenner != null) {
            btn_right.setOnClickListener(rightListenner);
        }
        if (leftListenner != null) {
            btn_left.setOnClickListener(leftListenner);
        }
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        //如果用户自定了title和message
        if (!TextUtils.isEmpty(titleStr)) {
            titleTv.setText(titleStr);
        }
        if (!TextUtils.isEmpty(leftText)) {
            btn_left.setText(leftText);
        }
        if (!TextUtils.isEmpty(rightText)) {
            btn_right.setText(rightText);
        }
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        btn_right = (Button) findViewById(R.id.yes);
        btn_left = (Button) findViewById(R.id.no);
        titleTv = (TextView) findViewById(R.id.title);
    }


    public static final class Build {
        private Context context;
        private String contentText;
        private View.OnClickListener okClick;
        private View.OnClickListener cacel;
        private String leftText, rightText;

        public Build(Context context) {
            this.context = context;
        }

        public Build textContent(String str) {
            this.contentText = str;
            return this;
        }

        public Build leftText(String str) {
            this.leftText = str;
            return this;
        }

        public Build rightText(String str) {
            this.rightText = str;
            return this;
        }

        public Build setOk(View.OnClickListener listener) {
            this.okClick = listener;
            return this;
        }

        public Build setCacel(View.OnClickListener listener) {
            this.cacel = listener;
            return this;
        }

        public HxCommonVideoDialog build() {
            return new HxCommonVideoDialog(this);
        }
    }
}
