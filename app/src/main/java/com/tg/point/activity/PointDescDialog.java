package com.tg.point.activity;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.util.SharedPreferencesUtils;


/**
 * 积分或饭票的描述
 */
public class PointDescDialog {

    public Dialog mDialog;
    private ImageView iv_close_dialog;

    public PointDescDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_point_desc, null);
        mDialog = new Dialog(context, R.style.custom_dialog_theme);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(true);
        iv_close_dialog = view.findViewById(R.id.iv_close_dialog);
        iv_close_dialog.setOnClickListener(v -> dismiss());
        TextView tv_honor_point = view.findViewById(R.id.tv_honor_point);
        TextView tv_honor_point_desc = view.findViewById(R.id.tv_honor_point_desc);
        TextView tv_normal_point = view.findViewById(R.id.tv_normal_point);
        TextView tv_normal_point_desc = view.findViewById(R.id.tv_normal_point_desc);
        String keyword_sign = SharedPreferencesUtils.getInstance().getStringData(SpConstants.storage.COLOUR_WALLET_KEYWORD_SIGN, "积分");
        String honor_point_title = String.format(context.getResources().getString(R.string.point_honor_title), keyword_sign);
        String honor_point_desc = String.format(context.getResources().getString(R.string.point_honor_desc), keyword_sign, keyword_sign);
        String honor_normal_title = String.format(context.getResources().getString(R.string.point_normal_title), keyword_sign);
        String honor_normal_desc = String.format(context.getResources().getString(R.string.point_normal_desc), keyword_sign, keyword_sign);
        tv_honor_point.setText(honor_point_title);
        tv_honor_point_desc.setText(honor_point_desc);
        tv_normal_point.setText(honor_normal_title);
        tv_normal_point_desc.setText(honor_normal_desc);
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