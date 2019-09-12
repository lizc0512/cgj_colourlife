package com.tg.setting.view;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.tg.coloursteward.R;


/**
 * Created by HX_CHEN on 2016/1/18.
 */
public class UpdateVerSionDialog {
    public Dialog mDialog;
    public Button ok;
    public ImageView cancel;
    public RecyclerView listView;

    public UpdateVerSionDialog(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_update, null);
        mDialog = new Dialog(context, R.style.custom_dialog_theme);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);
        ok = view.findViewById(R.id.tv_ok);
        cancel = view.findViewById(R.id.iv_close);
        listView = view.findViewById(R.id.list_update);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(layoutManager);

    }

    public void show() {
        try {
            mDialog.show();
        } catch (Exception e) {

        }

    }

    public void dismiss() {
        try {
            mDialog.dismiss();
        } catch (Exception e) {

        }

    }

}
