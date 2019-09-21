package com.tg.user.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.tg.coloursteward.R;


/**
 * 自定义对话框
 * Created by chenql on 16/1/5.
 * migrate from v2.3
 */
public class CustomDialog extends Dialog {
    Context context;

    public TextView dialog_content;
    public TextView dialog_title;

    public Button dialog_button_ok;
    public Button dialog_button_cancel;
    public View dialog_line;

    public CustomDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.custom_dialog);

        dialog_content = (TextView) findViewById(R.id.dialog_content);
        dialog_title = (TextView) findViewById(R.id.dialog_title);

        dialog_line = findViewById(R.id.dialog_line);
        dialog_button_ok = (Button) findViewById(R.id.dialog_button_ok);
        dialog_button_cancel = (Button) findViewById(R.id.dialog_button_cancel);

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
}
