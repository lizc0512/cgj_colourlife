package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.youmai.hxsdk.R;

public class HxApplyVideoDialog extends Dialog {
    private Context context;
    private Button btn_yes, btn_cacel;
    private String titleStr;//从外界设置的title文本
    private TextView titleTv;
    private String firstText;
    private String secondText;
    private String threeText;
    private View.OnClickListener firstListenner, secondListenner, threeListenner;
    private Button btn_apply;

    private HxApplyVideoDialog(Build build) {
        super(build.context, R.style.qiniu_common_dialog);
        this.titleStr = build.contentText;
        this.firstText = build.btnFirstText;
        this.secondText = build.btnSecondText;
        this.threeText = build.btnThreeText;
        this.firstListenner = build.firstClick;
        this.secondListenner = build.secondClick;
        this.threeListenner = build.threeClick;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_dialog_apply_video);
        //按空白处不能取消
        setCanceledOnTouchOutside(true);
        setWindow();
        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();

    }

    private void setWindow() {
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        // 设置宽度为屏宽、靠近屏幕底部。
        wlp.gravity = Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //  window.setWindowAnimations(R.style.DialogOutAndInStyle);
        window.setAttributes(wlp);
    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {

        if (secondListenner != null) {
            btn_apply.setOnClickListener(secondListenner);
        }
        if (firstListenner != null) {
            btn_yes.setOnClickListener(firstListenner);
        }
        if (threeListenner != null) {
            btn_cacel.setOnClickListener(threeListenner);
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

        if (!TextUtils.isEmpty(firstText)) {
            btn_yes.setText(firstText);
        }
        if (!TextUtils.isEmpty(secondText)) {
            btn_apply.setText(secondText);
        }
        if (!TextUtils.isEmpty(threeText)) {
            btn_cacel.setText(threeText);
        }
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        btn_yes = (Button) findViewById(R.id.yes);
        btn_cacel = (Button) findViewById(R.id.btn_cacel);
        titleTv = (TextView) findViewById(R.id.title);
        btn_apply = findViewById(R.id.btn_apply);
    }


    public static final class Build {
        private Context context;
        private String contentText;
        private View.OnClickListener firstClick;
        private View.OnClickListener secondClick;
        private String btnFirstText;
        private String btnSecondText;
        private String btnThreeText;
        private View.OnClickListener threeClick;


        public Build(Context context) {
            this.context = context;
        }

        public Build textContent(String str) {
            this.contentText = str;
            return this;
        }

        public Build setFirstClick(String str, View.OnClickListener listener) {
            this.btnFirstText = str;
            this.firstClick = listener;
            return this;
        }


        public Build setSecondClick(String str, View.OnClickListener listener) {
            this.btnSecondText = str;
            this.secondClick = listener;
            return this;
        }

        public Build setThreeClick(String str, View.OnClickListener listener) {
            this.btnThreeText = str;
            this.threeClick = listener;
            return this;
        }

        public HxApplyVideoDialog build() {
            return new HxApplyVideoDialog(this);
        }
    }
}
