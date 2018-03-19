package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.utils.TextMergeUtils;

/**
 * 合并或替换
 * Created by fylder on 2017/4/18.
 */

public class HxRemarkMergeDialog extends Dialog {

    public HxRemarkMergeDialog(Context context) {
        super(context);
    }

    public HxRemarkMergeDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {

        HxRemarkMergeDialog dialog;

        private Context context;

        private String tip;

        private String mergeTitle;
        private String mergeContent;
        private String replaceTitle;
        private String replaceContent;

        private String submit;
        private String cancel;

        private HxRemarkMergeDialog.OnClickListener positiveClickListener;

        private RelativeLayout mergeLay;
        private RelativeLayout replaceLay;
        private TextView mergeTipText;

        private TextView mergeTitleText;
        private TextView mergeContentText;
        private TextView replaceTitleText;
        private TextView replaceContentText;

        private Button mergeBtn;
        private Button replaceBtn;
        private Button cancelBtn;


        public Builder(Context context) {
            this.context = context;
        }

        public HxRemarkMergeDialog.Builder setTip(String tip) {
            this.tip = tip;
            return this;
        }

        public HxRemarkMergeDialog.Builder setMergeTitle(String mergeTitle) {
            this.mergeTitle = mergeTitle;
            return this;
        }

        public HxRemarkMergeDialog.Builder setMergeContent(String mergeContent) {
            this.mergeContent = mergeContent;
            return this;
        }

        public HxRemarkMergeDialog.Builder setReplaceTitle(String replaceTitle) {
            this.replaceTitle = replaceTitle;
            return this;
        }

        public HxRemarkMergeDialog.Builder setReplaceContent(String replaceContent) {
            this.replaceContent = replaceContent;
            return this;
        }

        public HxRemarkMergeDialog.Builder setSubmit(String submit) {
            this.submit = submit;
            return this;
        }

        public HxRemarkMergeDialog.Builder setSubmit(@StringRes int submit) {
            this.submit = (String) context.getText(submit);
            return this;
        }

        public HxRemarkMergeDialog.Builder setCancel(String cancel) {
            this.cancel = cancel;
            return this;
        }

        public HxRemarkMergeDialog.Builder setCancel(@StringRes int cancel) {
            this.cancel = (String) context.getText(cancel);
            return this;
        }

        public HxRemarkMergeDialog.Builder setOnListener(HxRemarkMergeDialog.OnClickListener listener) {
            this.positiveClickListener = listener;
            return this;
        }


        public void dismiss() {
            this.dialog.dismiss();
        }


        public HxRemarkMergeDialog create() {
            if (dialog == null) {
                dialog = new HxRemarkMergeDialog(context, R.style.PhoneDialog);
            }
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.hx_dialog_remark_merge_lay, null);
            mergeLay = (RelativeLayout) view.findViewById(R.id.dialog_remark_merge_info_lay);
            replaceLay = (RelativeLayout) view.findViewById(R.id.dialog_remark_merge_replace_info_lay);
            mergeTipText = (TextView) view.findViewById(R.id.dialog_remark_merge_tip);
            mergeTitleText = (TextView) view.findViewById(R.id.dialog_remark_merge_title);
            mergeContentText = (TextView) view.findViewById(R.id.dialog_remark_merge_message);
            replaceTitleText = (TextView) view.findViewById(R.id.dialog_remark_merge_replace_title);
            replaceContentText = (TextView) view.findViewById(R.id.dialog_remark_merge_replace_message);
            mergeBtn = (Button) view.findViewById(R.id.dialog_remark_merge_btn);
            replaceBtn = (Button) view.findViewById(R.id.dialog_remark_merge_replace_btn);
            cancelBtn = (Button) view.findViewById(R.id.dialog_remark_merge_cancel_btn);

            mergeLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (positiveClickListener != null) {
                        positiveClickListener.onMoreInfoClick(1, mergeTitle, mergeContent);
                    }
                }
            });
            replaceLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (positiveClickListener != null) {
                        positiveClickListener.onMoreInfoClick(2, replaceTitle, replaceContent);
                    }
                }
            });
            mergeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (positiveClickListener != null) {
                        positiveClickListener.onMergeClick(dialog, mergeTitle, mergeContent);
                    }
                }
            });
            replaceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (positiveClickListener != null) {
                        positiveClickListener.onReplaceClick(dialog, replaceTitle, replaceContent);
                    }
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            dialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            if (tip != null) {
                mergeTipText.setText(tip);
            }

            if (mergeTitle != null) {
                mergeTitleText.setText(mergeTitle);
            }

            if (mergeContent != null) {
                mergeContentText.setText(mergeContent);
            }
            if (replaceTitle != null) {
                replaceTitleText.setText(TextMergeUtils.getDefaultTheme(replaceTitle, replaceContent));
            }

            if (replaceContent != null) {
                replaceContentText.setText(replaceContent);
            }
            if (submit != null) {
                mergeBtn.setText(submit);
            }
            if (cancel != null) {
                cancelBtn.setText(cancel);
            }
            dialog.setContentView(view);
            return dialog;
        }
    }


    /**
     * 提交回调
     */
    public interface OnClickListener {

        void onMergeClick(DialogInterface dialog, String mergeTheme, String mergeRemark);

        void onReplaceClick(DialogInterface dialog, String replaceTheme, String replaceRemark);

        void onMoreInfoClick(int type, String theme, String remark);
    }

}
