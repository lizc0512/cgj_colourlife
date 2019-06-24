package com.tg.setting.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.tg.coloursteward.R;


/**
 * 创建时间 : 2017/3/29.
 * 编写人 :  ${yuansk}
 * 功能描述:
 * 版本:
 */

public class DeleteMsgDialog extends Dialog {
    Context context;

    public Button btn_define;
    public Button btn_cancel;
    private TextView textView;

    public DeleteMsgDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.delete_msg_notice);
        btn_define = (Button) findViewById(R.id.btn_define);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        textView = findViewById(R.id.tv_desc);
        textView.setText("确认删除消息吗？");
        Window window = getWindow();
        window.setBackgroundDrawableResource(R.color.transparent);
        WindowManager.LayoutParams params = window.getAttributes();
        int density = getWidthPixels(context);
        params.width = density - 40;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        this.setCanceledOnTouchOutside(false);
    }

    private int getWidthPixels(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    public void setContentText(String content) {
        textView.setText(content);
    }

    public void setleftText(String content) {
        btn_cancel.setText(content);
    }

    public void setrightText(String content) {
        btn_define.setText(content);
    }

    public void setBtn_defineColor(int color) {
        btn_define.setTextColor(context.getResources().getColor(color));
    }

    public void setBtn_CancelColor(int color) {
        btn_cancel.setTextColor(context.getResources().getColor(color));
    }
}