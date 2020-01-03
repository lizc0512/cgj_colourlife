package com.tg.point.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.util.SoftKeyboardUtils;
import com.tg.point.gridpasswordview.GridPasswordView;


/**
 * 积分或饭票的描述
 */
public class PasswordDialogListener {

    public Dialog mDialog;
    private GridPasswordView grid_pay_pawd;
    private ImageView iv_close_dialog;
    public pwdDialogListener listener;

    public PasswordDialogListener(Activity activity, pwdDialogListener mListener) {
        this.listener = mListener;
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.dialog_point_password, null);
        mDialog = new Dialog(activity, R.style.custom_dialog_theme);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        grid_pay_pawd = view.findViewById(R.id.grid_pay_pawd);
        iv_close_dialog = view.findViewById(R.id.iv_close_dialog);
        grid_pay_pawd.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String psw) {

            }

            @Override
            public void onInputFinish(String psw) {
                if (null != listener) {
                    listener.result(psw);
                }
                dismiss();
            }
        });
        iv_close_dialog.setOnClickListener(v -> dismiss());
        mDialog.setOnDismissListener(dialog -> SoftKeyboardUtils.hideSoftKeyboard(activity, iv_close_dialog));
    }

    public void show() {
        if (null != mDialog && !mDialog.isShowing()) {
            mDialog.show();
            grid_pay_pawd.clearPassword();
            new Handler().postDelayed(() -> {
                grid_pay_pawd.performClick();
            }, 300);
        }

    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public interface pwdDialogListener {
        void result(String pwd);
    }

}