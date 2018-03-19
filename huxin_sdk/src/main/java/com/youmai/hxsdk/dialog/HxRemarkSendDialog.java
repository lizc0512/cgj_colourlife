package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.utils.TextMergeUtils;

/**
 * 备注发送提示窗
 * Created by fylder on 2017/4/17.
 */

public class HxRemarkSendDialog extends Dialog {

    public HxRemarkSendDialog(Context context) {
        super(context);
    }

    public HxRemarkSendDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {

        HxRemarkSendDialog dialog;

        private Context context;

        private String tip;
        private String title;
        private String content;
        private String submit;
        private String cancel;
        private boolean showPreview = false;

        private OnClickListener positiveClickListener;

        private RelativeLayout lay;
        private TextView tipText;
        private TextView titleText;
        private TextView contentText;
        private ImageView previewImg;

        private Button positivebtn;
        private Button cancelBtn;


        public Builder(Context context) {
            this.context = context;
        }

        public HxRemarkSendDialog.Builder setTip(String tip) {
            this.tip = tip;
            return this;
        }

        public HxRemarkSendDialog.Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public HxRemarkSendDialog.Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public HxRemarkSendDialog.Builder setSubmit(String submit) {
            this.submit = submit;
            return this;
        }

        public HxRemarkSendDialog.Builder setSubmit(@StringRes int submit) {
            this.submit = (String) context.getText(submit);
            return this;
        }

        public HxRemarkSendDialog.Builder setCancel(String cancel) {
            this.cancel = cancel;
            return this;
        }

        public HxRemarkSendDialog.Builder setCancel(@StringRes int cancel) {
            this.cancel = (String) context.getText(cancel);
            return this;
        }

        public HxRemarkSendDialog.Builder setOnListener(OnClickListener listener) {
            this.positiveClickListener = listener;
            return this;
        }

        public HxRemarkSendDialog.Builder setShowPreview(boolean showPreview) {
            this.showPreview = showPreview;
            return this;
        }

        public void dismiss() {
            this.dialog.dismiss();
        }


        public HxRemarkSendDialog create() {
            if (dialog == null) {
                dialog = new HxRemarkSendDialog(context, R.style.PhoneDialog);
            }
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.hx_dialog_remark_send_lay, null);
            lay = (RelativeLayout) view.findViewById(R.id.dialog_remark_send_info_lay);
            tipText = (TextView) view.findViewById(R.id.dialog_remark_send_tip);
            titleText = (TextView) view.findViewById(R.id.dialog_remark_send_title);
            contentText = (TextView) view.findViewById(R.id.dialog_remark_send_message);
            positivebtn = (Button) view.findViewById(R.id.dialog_remark_send_submit_btn);
            cancelBtn = (Button) view.findViewById(R.id.dialog_remark_send_cancel_btn);
            previewImg = (ImageView) view.findViewById(R.id.dialog_remark_send_img);

            lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (positiveClickListener != null) {
                        positiveClickListener.onMoreInfoClick(title, content);
                    }
                }
            });
            positivebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (positiveClickListener != null) {
                        positiveClickListener.onSubmitClick(dialog, title, content);
                    }
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (positiveClickListener != null) {
                        positiveClickListener.onCancelClick(dialog);
                    }
                }
            });

            dialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            if (tip != null) {
                tipText.setText(tip);
            }

            if (title != null) {
                titleText.setText(TextMergeUtils.getDefaultTheme(title, content));
            }

            if (content != null) {
                contentText.setText(content);
            }
            if (submit != null) {
                positivebtn.setText(submit);
            }
            if (cancel != null) {
                cancelBtn.setText(cancel);
            }
            if (showPreview) {
                previewImg.setVisibility(View.VISIBLE);
            } else {
                previewImg.setVisibility(View.GONE);
            }

            dialog.setContentView(view);
            return dialog;
        }

    }


    /**
     * 提交回调
     */
    public interface OnClickListener {

        void onSubmitClick(DialogInterface dialog, String titleStr, String contentStr);

        void onCancelClick(DialogInterface dialog);

        void onMoreInfoClick(String titleStr, String contentStr);
    }

}
