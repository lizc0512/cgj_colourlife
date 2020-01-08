package com.tg.point.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.tg.coloursteward.util.SoftKeyboardUtils;
import com.tg.point.gridpasswordview.GridPasswordView;
import com.tg.point.gridpasswordview.NormalGridPasswordView;

import org.greenrobot.eventbus.EventBus;

import static com.tg.coloursteward.constant.UserMessageConstant.POINT_INPUT_PAYPAWD;


/**
 * 积分或饭票的描述
 */
public class PointPasswordDialog {

    public Dialog mDialog;
    private GridPasswordView grid_pay_pawd;
    private NormalGridPasswordView nor_grid_pay_pawd;
    private ImageView iv_close_dialog;
    private boolean isAjm;


    public PointPasswordDialog(Activity activity) {
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
                Message message = Message.obtain();
                message.what = POINT_INPUT_PAYPAWD;
                message.obj = psw;
                EventBus.getDefault().post(message);
                dismiss();
            }
        });
        nor_grid_pay_pawd.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String psw) {

            }

            @Override
            public void onInputFinish(String psw) {
                Message message = Message.obtain();
                message.what = POINT_INPUT_PAYPAWD;
                message.obj = psw;
                EventBus.getDefault().post(message);
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

}