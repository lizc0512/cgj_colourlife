package com.tg.point.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.tg.coloursteward.util.SoftKeyboardUtils;
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
    public pwdDialogListener listener;
    private boolean isAjm;

    public PasswordDialogListener(Activity activity, pwdDialogListener mListener) {
        this.listener = mListener;
        LayoutInflater inflater = LayoutInflater.from(activity);
        isAjm = SharedPreferencesUtils.getInstance().getBooleanData(SpConstants.UserModel.ISUSERAJM, false);
        View view = inflater.inflate(R.layout.dialog_point_password, null);
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
                dismiss();
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
                dismiss();
            }
        });
        iv_close_dialog.setOnClickListener(v -> dismiss());
        mDialog.setOnDismissListener(dialog -> SoftKeyboardUtils.hideSoftKeyboard(activity, iv_close_dialog));
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

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public interface pwdDialogListener {
        void result(String pwd);
    }

}