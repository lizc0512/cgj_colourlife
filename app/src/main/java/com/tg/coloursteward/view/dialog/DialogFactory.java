package com.tg.coloursteward.view.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.util.Tools;
import com.tg.user.callback.CreateDialgCallBack;

/**
 * Dialog工具类
 */
public class DialogFactory {
    private static DialogFactory factory = null;
    private AlertDialog dialog;
    private Activity dialogActivity;

    private TextView tvContent;
    private TextView dialog_title;
    private OnClickListener okListener;
    private CreateDialgCallBack dialgCallBack;
    private OnClickListener cancelListener;

    private Button btnOk;
    private Button btnCancel;

    private DialogFactory() {
    }

    public static DialogFactory getInstance() {
        if (factory == null) {
            factory = new DialogFactory();
        }
        return factory;
    }

    public void showDialog(Activity activity, final OnClickListener okL, final OnClickListener cancelL, String content, String ok, String cancel) {
        this.okListener = okL;
        this.cancelListener = cancelL;
        if (dialog == null || dialogActivity != activity) {
            dialogActivity = activity;
            DisplayMetrics metrics = Tools.getDisplayMetrics(activity);
            dialog = new AlertDialog.Builder(activity).create();
            dialog.setCancelable(false);
            Window window = dialog.getWindow();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            LinearLayout layout = (LinearLayout) LayoutInflater.from(activity)
                    .inflate(R.layout.custom_dialog_layout, null);
            tvContent = (TextView) layout.findViewById(R.id.dialog_msg);
            btnOk = (Button) layout.findViewById(R.id.btn_ok);
            btnCancel = (Button) layout.findViewById(R.id.btn_cancel);
            btnOk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (okListener != null) {
                        okListener.onClick(v);
                    }
                    dialog.dismiss();
                }
            });
            btnCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (cancelListener != null) {
                        cancelListener.onClick(v);
                    }
                    dialog.dismiss();
                }
            });
            window.setContentView(layout);
            WindowManager.LayoutParams p = window.getAttributes();
            p.width = ((int) (metrics.widthPixels - 80 * metrics.density));
            //p.height = (int) (120 * metrics.density);
            window.setAttributes(p);
        }
        tvContent.setText(content);
        if (ok == null) {
            ok = "确定";
        }
        if (cancel == null) {
            cancel = "取消";
        }
        btnOk.setText(ok);
        btnCancel.setText(cancel);
        dialog.show();
    }


    public void showDoorDialog(Activity activity, final OnClickListener okL, final OnClickListener cancelL, int showTitle, String content, String ok, String cancel) {
        this.okListener = okL;
        this.cancelListener = cancelL;
        if (dialog == null || dialogActivity != activity) {
            dialogActivity = activity;
            DisplayMetrics metrics = Tools.getDisplayMetrics(activity);
            dialog = new AlertDialog.Builder(activity).create();
            dialog.setCancelable(false);
            Window window = dialog.getWindow();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            LinearLayout layout = (LinearLayout) LayoutInflater.from(activity)
                    .inflate(R.layout.dialog_door_notice, null);
            TextView dialog_title = layout.findViewById(R.id.dialog_title);
            if (showTitle == 1) {
                dialog_title.setVisibility(View.VISIBLE);
            } else {
                dialog_title.setVisibility(View.GONE);
            }
            tvContent = layout.findViewById(R.id.dialog_msg);
            btnOk = layout.findViewById(R.id.btn_ok);
            btnCancel = layout.findViewById(R.id.btn_cancel);
            btnOk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (okListener != null) {
                        okListener.onClick(v);
                    }
                    dialog.dismiss();
                }
            });
            btnCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (cancelListener != null) {
                        cancelListener.onClick(v);
                    }
                    dialog.dismiss();
                }
            });
            window.setContentView(layout);
            WindowManager.LayoutParams p = window.getAttributes();
            p.width = ((int) (metrics.widthPixels - 80 * metrics.density));
            window.setAttributes(p);
        }
        tvContent.setText(content);
        if (ok == null) {
            ok = "确定";
        }
        if (cancel == null) {
            cancel = "取消";
        }
        btnOk.setText(ok);
        btnCancel.setText(cancel);
        dialog.show();

    }

    public void showCreateDialog(Activity activity, final CreateDialgCallBack okL, final OnClickListener cancelL, String content, String ok, String cancel) {
        this.dialgCallBack = okL;
        this.cancelListener = cancelL;
        if (dialog == null || dialogActivity != activity) {
            dialogActivity = activity;
            DisplayMetrics metrics = Tools.getDisplayMetrics(activity);
            dialog = new AlertDialog.Builder(activity).create();
            Window window = dialog.getWindow();
            dialog.setView(new EditText(activity));
            dialog.show();
            ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(activity)
                    .inflate(R.layout.create_dialog_layout, null);
            tvContent = (TextView) layout.findViewById(R.id.dialog_title);
            btnOk = (Button) layout.findViewById(R.id.btn_ok);
            btnCancel = (Button) layout.findViewById(R.id.btn_cancel);
            EditText editText = layout.findViewById(R.id.et_create_content);
            btnOk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String content = editText.getText().toString().trim();
                    if (!TextUtils.isEmpty(content)) {
                        if (dialgCallBack != null) {
                            dialgCallBack.onClick(v, editText.getText().toString().trim());
                        }
                        dialog.dismiss();
                    } else {
                        ToastUtil.showShortToast(activity, "名称不能为空");
                    }
                }
            });
            btnCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cancelListener != null) {
                        cancelListener.onClick(v);
                    }
                    dialog.dismiss();
                }
            });
            window.setContentView(layout);
            WindowManager.LayoutParams p = window.getAttributes();
            p.width = ((int) (metrics.widthPixels - 80 * metrics.density));
            window.setAttributes(p);
        }
        if (!TextUtils.isEmpty(content)) {
            tvContent.setText(content);
        }
        if (ok == null) {
            ok = "确定";
        }
        if (cancel == null) {
            cancel = "取消";
        }
        btnOk.setText(ok);
        btnCancel.setText(cancel);
        dialog.show();
    }

    /**
     * 单关闭按钮信息框
     *
     * @param activity
     * @param content
     */
    public void showSingleDialog(Activity activity, String title, String content) {
        if (dialog == null || dialogActivity != activity) {
            dialogActivity = activity;
            DisplayMetrics metrics = Tools.getDisplayMetrics(activity);
            dialog = new AlertDialog.Builder(activity).create();
            dialog.setCancelable(false);
            Window window = dialog.getWindow();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            LinearLayout layout = (LinearLayout) LayoutInflater.from(activity)
                    .inflate(R.layout.dialog_income_fee, null);
            tvContent = (TextView) layout.findViewById(R.id.tv_income_msg);
            dialog_title = (TextView) layout.findViewById(R.id.dialog_title);
            TextView tv_dialog_close = (TextView) layout.findViewById(R.id.tv_dialog_close);
            tv_dialog_close.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            window.setContentView(layout);
            WindowManager.LayoutParams p = window.getAttributes();
            p.width = ((int) (metrics.widthPixels - 80 * metrics.density));
            window.setAttributes(p);
        }
        tvContent.setText(content);
        dialog_title.setText(title);
        dialog.show();
    }
}
