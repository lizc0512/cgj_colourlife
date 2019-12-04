package com.tg.point.activity;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.tg.coloursteward.R;


/**
 * 积分或饭票的描述
 */
public class PointOldWalletDialog {

    public Dialog mDialog;

    public PointOldWalletDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_oldwallet_enter, null);
        mDialog = new Dialog(context, R.style.custom_dialog_theme);
        mDialog.setContentView(view);
        Window window = mDialog.getWindow();
        window.setGravity(Gravity.TOP | Gravity.RIGHT);
        mDialog.setCanceledOnTouchOutside(true);
        ImageView iv_close_dialog = view.findViewById(R.id.iv_close_dialog);
        iv_close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
}