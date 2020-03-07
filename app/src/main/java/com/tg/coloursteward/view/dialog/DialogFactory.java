package com.tg.coloursteward.view.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
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
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.object.SlideItemObj;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.CameraView;
import com.tg.coloursteward.view.RotateProgress;
import com.tg.coloursteward.view.spinnerwheel.SlideSelectorView;
import com.tg.user.callback.CreateDialgCallBack;

import java.util.ArrayList;

/**
 * Dialog工具类
 */
public class DialogFactory {
    private String PHOTO_NAME = "wisdomPark.jpg";
    private String TAKE_PHOTO_PATH = "";
    private static DialogFactory factory = null;
    private boolean isStorageMemory = false;
    private String cropPath;
    private CameraView cameraView;
    private int groupPosition;
    private int childPosition;
    private int position;

    private AlertDialog Loadialog;
    private Activity LoaddialogActivity;

    private AlertDialog dialog;
    private Activity dialogActivity;

    private AlertDialog photoDialog;
    private BaseActivity photoDialogActivity;

    private AlertDialog selectorDialog;
    private Activity selectorDialogActivity;

    private AlertDialog transitionDialog;
    private Activity transitionDialogActivity;

    private AlertDialog msgDialog;
    private Activity msgDialogActivity;

    private TextView msgTvContent;
    private Button msgBtnOk;
    private OnClickListener msgOkListener;

    private TextView tvMsg;
    private RotateProgress progressBar;
    private TextView tvContent;
    private TextView dialog_title;
    private OnClickListener okListener;
    private CreateDialgCallBack dialgCallBack;
    private OnClickListener cancelListener;

    private Button btnOk;
    private Button btnCancel;
    private SlideSelectorView slideSelectorView;

    private DialogFactory() {
    }

    public static DialogFactory getInstance() {
        if (factory == null) {
            factory = new DialogFactory();
        }
        return factory;
    }

    /**
     * 得到自定义的progressDialog
     *
     * @param activity
     * @param msg
     * @return
     */
    public void createLoadingDialog(Activity activity, String msg) {
        if (Loadialog == null || LoaddialogActivity != activity) {
            LoaddialogActivity = activity;
            DisplayMetrics metrics = Tools.getDisplayMetrics(activity);
            Loadialog = new AlertDialog.Builder(activity).create();
            Window window = Loadialog.getWindow();
            Loadialog.show();
            LinearLayout layout = (LinearLayout) LayoutInflater.from(activity)
                    .inflate(R.layout.transition_dialog_layout, null);
            tvMsg = (TextView) layout.findViewById(R.id.dialog_hint);
            progressBar = (RotateProgress) layout.findViewById(R.id.progressBar);
            window.setContentView(layout);
            WindowManager.LayoutParams p = window.getAttributes();
            p.width = ((int) (metrics.widthPixels - 160 * metrics.density));
            p.height = (int) (120 * metrics.density);
            window.setAttributes(p);
        }
        if (TextUtils.isEmpty(msg)) {
            tvMsg.setVisibility(View.GONE);
        } else {
            tvMsg.setText(msg);
            tvMsg.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.VISIBLE);
        Loadialog.show();

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

    public void hideLoadialogDialog() {
        if (LoaddialogActivity == null || LoaddialogActivity.isFinishing()) {
            return;
        }
        if (Loadialog != null && Loadialog.isShowing()) {
            progressBar.setVisibility(View.INVISIBLE);
            try {
                Loadialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void hideTransitionDialog() {
        if (transitionDialogActivity == null || transitionDialogActivity.isFinishing()) {
            return;
        }
        if (transitionDialog != null && transitionDialog.isShowing()) {
            progressBar.setVisibility(View.INVISIBLE);
            try {
                transitionDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showSelectorDialog(final BaseActivity activity, String title,
                                   ArrayList<SlideItemObj> list1, ArrayList<SlideItemObj> list2,
                                   final SlideSelectorView.OnCompleteListener l, boolean sort) {
        if (selectorDialog == null || selectorDialogActivity != activity) {
            selectorDialogActivity = activity;
            selectorDialog = new AlertDialog.Builder(activity).create();
            selectorDialog.setCanceledOnTouchOutside(true);
            selectorDialog.show();
            Window window = selectorDialog.getWindow();
            slideSelectorView = new SlideSelectorView(
                    activity);

            slideSelectorView
                    .setOnCancelListener(new SlideSelectorView.OnCancelListener() {
                        @Override
                        public void onCancel() {
                            selectorDialog.dismiss();
                        }
                    });

            window.setContentView(slideSelectorView);
            WindowManager.LayoutParams p = window.getAttributes();
            DisplayMetrics metrics = Tools.getDisplayMetrics(activity);
            p.width = metrics.widthPixels;
            p.gravity = Gravity.BOTTOM;
            window.setAttributes(p);
        } else {
            selectorDialog.show();
        }
        slideSelectorView.setNeedSort(sort);
        slideSelectorView.setTitle(title);
        slideSelectorView.setList(list1, list2);
        slideSelectorView
                .setOnCompleteListener(new SlideSelectorView.OnCompleteListener() {
                    @Override
                    public void onComplete(SlideItemObj item1,
                                           SlideItemObj item2) {
                        // TODO Auto-generated method stub
                        selectorDialog.dismiss();
                        if (l != null) {
                            l.onComplete(item1, item2);
                        }
                    }
                });
    }

    public void recycleBitmap(Bitmap btm) {
        if (btm != null && !btm.isRecycled()) {
            btm.recycle();
            btm = null;
        }
    }


    public void recycleBitmap(Intent data) {
        if (data != null && data.getExtras() != null) {
            Bitmap btm = data.getExtras().getParcelable("data");
            recycleBitmap(btm);
            btm = null;
        }
    }

    public void showMessageDialog(Activity activity, String content, String ok,
                                  OnClickListener okL) {
        this.msgOkListener = okL;
        if (msgDialog == null || msgDialogActivity != activity) {
            msgDialogActivity = activity;
            DisplayMetrics metrics = Tools.getDisplayMetrics(activity);
            msgDialog = new AlertDialog.Builder(activity).create();
            msgDialog.setCancelable(false);
            Window window = msgDialog.getWindow();
            msgDialog.show();
            LinearLayout layout = (LinearLayout) LayoutInflater.from(activity)
                    .inflate(R.layout.msg_dialog_layout, null);
            msgTvContent = (TextView) layout.findViewById(R.id.dialog_msg);
            msgBtnOk = (Button) layout.findViewById(R.id.btn_ok);
            msgBtnOk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (msgOkListener != null) {
                        msgOkListener.onClick(v);
                    }
                    msgDialog.dismiss();
                }
            });
            window.setContentView(layout);
            WindowManager.LayoutParams p = window.getAttributes();
            p.width = ((int) (metrics.widthPixels - 80 * metrics.density));
            // p.height = (int) (120 * metrics.density);
            window.setAttributes(p);
        }
        if (content != null) {
            msgTvContent.setText(content);
        }
        if (ok != null) {
            msgBtnOk.setText(ok);
        }
        msgDialog.show();
    }

    public void setDialogOnClickListener(CreateDialgCallBack l) {
        this.dialgCallBack = l;
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
