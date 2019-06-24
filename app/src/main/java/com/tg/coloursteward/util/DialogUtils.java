package com.tg.coloursteward.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.youmai.hxsdk.utils.AppUtils;

/**
 * @name ${lizc}
 * @class name：com.tg.coloursteward.util
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/6/22 11:20
 * @change
 * @chang time
 * @class describe
 */
public class DialogUtils {

    /**
     * 权限提示框
     *
     * @param context
     */
    public static void showPermissionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(context.getString(com.youmai.hxsdk.R.string.permission_title))
                .setMessage(context.getString(com.youmai.hxsdk.R.string.permission_content));

        builder.setPositiveButton(context.getString(com.youmai.hxsdk.R.string.hx_confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        AppUtils.startAppSettings(context);
                        arg0.dismiss();
                    }
                });

        builder.setNegativeButton(context.getString(com.youmai.hxsdk.R.string.hx_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
        builder.show();
    }
}
