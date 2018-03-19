package com.youmai.hxsdk.popup.half;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.WrapIMFilePreviewActivity;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.im.IMHelper;
import com.youmai.hxsdk.im.cache.CacheMsgFile;

/**
 * Created by Kevin on 2016/12/5.
 */

public class MFilePopWindow extends PopupWindow {

    private View rl;
    private Context mContext;
    private CacheMsgBean mCacheMsgBean;
    private CacheMsgFile mCacheMsgFile;

    private TextView fileNameTV;
    private TextView fileSizeTV;
    private ImageView fileIconIV;
    private View fontLy;
    private View btnClose;

    public MFilePopWindow(Context context, CacheMsgBean cacheMsgBean) {
        super(context);
        mContext = context;
        mCacheMsgBean = cacheMsgBean;
        mCacheMsgFile = (CacheMsgFile) mCacheMsgBean.getJsonBodyObj();
        initView(context);
        initAttr(context);
    }

    private void initAttr(Context context) {
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        this.setFocusable(true);// 设置弹出窗体可点击
        ColorDrawable dw = new ColorDrawable(0x000000);
        this.setBackgroundDrawable(dw);
        this.update();
    }

    private void initView(final Context context) {
        rl = LayoutInflater.from(context).inflate(R.layout.hx_m_file_view, null);
        fileNameTV = (TextView) rl.findViewById(R.id.file_name);
        fileSizeTV = (TextView) rl.findViewById(R.id.file_size);
        fileIconIV = (ImageView) rl.findViewById(R.id.file_iv);
        fontLy = rl.findViewById(R.id.font_ly);
        setContentView(rl);

        fileSizeTV.setText(IMHelper.convertFileSize(mCacheMsgFile.getFileSize()));
        fileNameTV.setText(mCacheMsgFile.getFileName());
        try {
            fileIconIV.setImageResource(mCacheMsgFile.getFileRes());
        } catch (Exception e) {
            fileIconIV.setImageResource(R.drawable.hx_im_file_iv);
        }

        btnClose = rl.findViewById(R.id.rl_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        fontLy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WrapIMFilePreviewActivity.class);
                intent.putExtra(WrapIMFilePreviewActivity.IM_FILE_BEAN, mCacheMsgBean);
                intent.putExtra(WrapIMFilePreviewActivity.IM_IS_FLOAT, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(intent);
            }
        });

    }
}
