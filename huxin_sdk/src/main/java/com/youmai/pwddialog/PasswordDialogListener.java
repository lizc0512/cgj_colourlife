package com.youmai.pwddialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.pwddialog.gridpasswordview.GridPasswordView;
import com.youmai.pwddialog.gridpasswordview.NormalGridPasswordView;


/**
 * 积分或饭票的描述
 */
public class PasswordDialogListener {

    public Dialog mDialog;
    private GridPasswordView grid_pay_pawd;
    private NormalGridPasswordView nor_grid_pay_pawd;
    private ImageView iv_close_dialog;
    private TextView tv_dialog_forget;
    public pwdDialogListener listener;
    private boolean isAjm;
    private String FILE_NAME = "wisdomPark_map";

    public PasswordDialogListener(Activity activity, pwdDialogListener mListener) {
        this.listener = mListener;
        LayoutInflater inflater = LayoutInflater.from(activity);
        SharedPreferences sharedPreferences = activity.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        isAjm = sharedPreferences.getBoolean("userajm", false);
        View view = inflater.inflate(R.layout.hx_dialog_point_password, null);
        tv_dialog_forget = view.findViewById(R.id.tv_dialog_forget);
        grid_pay_pawd = view.findViewById(R.id.grid_pay_pawd);
        nor_grid_pay_pawd = view.findViewById(R.id.nor_grid_pay_pawd);
        if (isAjm) {
            nor_grid_pay_pawd.setVisibility(View.GONE);
            grid_pay_pawd.setVisibility(View.VISIBLE);
        } else {
            nor_grid_pay_pawd.setVisibility(View.VISIBLE);
            grid_pay_pawd.setVisibility(View.GONE);
        }
        mDialog = new Dialog(activity, R.style.custom_dialog_theme);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
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
                dismiss(activity);
            }
        });
        nor_grid_pay_pawd.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String psw) {

            }

            @Override
            public void onInputFinish(String psw) {
                if (null != listener) {
                    listener.result(psw);
                }
                dismiss(activity);
            }
        });
        iv_close_dialog.setOnClickListener(v -> {
            dismiss(activity);
        });
        tv_dialog_forget.setOnClickListener(v -> {
            if (null != listener) {
                listener.forgetPassWord();
            }
        });
        mDialog.setOnDismissListener(dialog -> {
                    dismiss(activity);
                }
        );

    }

    public void show() {
        if (null != mDialog && !mDialog.isShowing()) {
            mDialog.show();
            if (isAjm) {
                grid_pay_pawd.clearPassword();
                grid_pay_pawd.showKeyWorld();
                new Handler().postDelayed(() -> {
                    grid_pay_pawd.performClick();
                }, 300);
            } else {
                nor_grid_pay_pawd.clearPassword();
                new Handler().postDelayed(() -> {
                    nor_grid_pay_pawd.performClick();
                }, 300);
            }
        }

    }

    public void dismiss(Activity activity) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        grid_pay_pawd.hideKeyWorld();
        hideSoftKeyboard(activity, iv_close_dialog);
    }

    public interface pwdDialogListener {
        void result(String pwd);

        default void forgetPassWord() {
        }
    }

    private void hideSoftKeyboard(Activity activity, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
        } catch (Exception ignored) {
        }
    }

}