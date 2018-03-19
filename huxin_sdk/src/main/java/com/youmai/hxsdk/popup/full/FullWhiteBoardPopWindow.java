package com.youmai.hxsdk.popup.full;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.interfaces.impl.FileSendListenerImpl;
import com.youmai.hxsdk.view.PaletteView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class FullWhiteBoardPopWindow extends PopupWindow implements View.OnClickListener, PaletteView.Callback, Handler.Callback,PopupWindow.OnDismissListener {
    private static final int MSG_SEND_SUCCESS = 1;
    private static final int MSG_SEND_FAILED = 2;
    private static final int MSG_SENDING = 3;

    private View mRedPen;
    private View mBlackPen;
    private View mUndoView;
    private View mRedoView;
    private View mPenView;
    private View mEraserView;
    private View mClearView;
    private View mSendView;
    private PaletteView mPaletteView;

    private RelativeLayout mRootContentView;
    private Context mContext;
    private String dstPhone; // 对方的手机号码
    private Handler mHandler;
    //private PopupWindow mSaveProgressDlg;

    public FullWhiteBoardPopWindow(Context context, String phone, int width, int height) {
        super(context);
        mContext = context;
        dstPhone = phone;

        initView(context);
        initAttr(width, height);

        mHandler = new Handler(this);
    }

    private static void scanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }

    private String saveImage(Bitmap bmp, int quality) {
        if (bmp == null) {
            return null;
        }
        File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (appDir == null) {
            return null;
        }
        String fileName = "huxin_wb_" + System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void initAttr(int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
        this.setFocusable(true);// 设置弹出窗体可点击
        ColorDrawable dw = new ColorDrawable(0x000000);
        this.setBackgroundDrawable(dw);
        this.update();
    }

    private void initView(Context context) {
        mRootContentView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.hx_white_board_view, null);
        setContentView(mRootContentView);

        mPaletteView = (PaletteView) mRootContentView.findViewById(R.id.wb_palette);
        mPaletteView.clear();
        
        mRedPen = mRootContentView.findViewById(R.id.wb_btn_red);
        mRedPen.setOnClickListener(this);
        mBlackPen = mRootContentView.findViewById(R.id.wb_btn_black);
        mBlackPen.setSelected(true);
        mBlackPen.setOnClickListener(this);
        mUndoView = mRootContentView.findViewById(R.id.wb_btn_undo);
        mUndoView.setOnClickListener(this);
        mRedoView = mRootContentView.findViewById(R.id.wb_btn_redo);
        mRedoView.setOnClickListener(this);
        mPenView = mRootContentView.findViewById(R.id.wb_btn_pen);
        mPenView.setOnClickListener(this);
        mEraserView = mRootContentView.findViewById(R.id.wb_btn_eraser);
        mEraserView.setOnClickListener(this);
        mClearView = mRootContentView.findViewById(R.id.wb_btn_clear);
        mClearView.setOnClickListener(this);
        mSendView = mRootContentView.findViewById(R.id.wb_btn_send);
        mSendView.setOnClickListener(this);

        Animation anim = AnimationUtils.loadAnimation(context, R.anim.hx_pop_alpha_anim_in);
        mRootContentView.setAnimation(anim);// 设置弹出窗体动画效果

        setOnDismissListener(this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_SEND_SUCCESS:
                //mSaveProgressDlg.dismiss();
                sendWb((String) msg.obj);
                break;
            case MSG_SEND_FAILED:
                //mSaveProgressDlg.dismiss();
                Toast.makeText(mContext, "发送失败！", Toast.LENGTH_SHORT).show();
                break;
            case MSG_SENDING:
                Toast.makeText(mContext, "发送...", Toast.LENGTH_SHORT).show();
/*                if(mSaveProgressDlg==null){
                    initSaveProgressDlg();
                }
                mSaveProgressDlg.show();*/
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bm = mPaletteView.buildBitmap();
                        String savedFile = saveImage(bm, 100);
                        if (savedFile != null) {
                            scanFile(mContext, savedFile);
                            mHandler.obtainMessage(MSG_SEND_SUCCESS, savedFile).sendToTarget();
                        } else {
                            mHandler.obtainMessage(MSG_SEND_FAILED).sendToTarget();
                        }
                    }
                }).start();
                break;
        }
        return true;
    }
    
    
    @Override
    public void onUndoRedoStatusChanged() {
        mUndoView.setEnabled(mPaletteView.canUndo());
        mRedoView.setEnabled(mPaletteView.canRedo());
    }

    private void sendWb(String imgPath) {
        File file = new File(imgPath);
        if (!file.exists()) {
            return;
        }
        int userId = HuxinSdkManager.instance().getUserId();
        HuxinSdkManager.instance().postPicture(userId, dstPhone, file, imgPath,
                true, FileSendListenerImpl.getListener());
        dismiss();
    }

    @Override
    public void onClick(View v) {
        if (mRedPen == v) {
            mRedPen.setSelected(true);
            mBlackPen.setSelected(false);
            mPaletteView.setPenColor(0xffff0000);
        } else if (mBlackPen == v) {
            mBlackPen.setSelected(true);
            mRedPen.setSelected(false);
            mPaletteView.setPenColor(0xff000000);
        } else if (mUndoView == v) {
            mPaletteView.undo();
        } else if (mRedoView == v) {
            mPaletteView.redo();
        } else if (mPenView == v) {
            mPenView.setSelected(true);
            mEraserView.setSelected(false);
            mPaletteView.setMode(PaletteView.Mode.DRAW);
        } else if (mEraserView == v) {
            mEraserView.setSelected(true);
            mPenView.setSelected(false);
            mPaletteView.setMode(PaletteView.Mode.ERASER);
        } else if (mClearView == v) {
            mPenView.setSelected(true);
            mEraserView.setSelected(false);
            mPaletteView.clear();
            mPaletteView.setMode(PaletteView.Mode.DRAW);
        } else if (mSendView == v) {
            mHandler.removeMessages(MSG_SENDING);
            mHandler.sendEmptyMessageDelayed(MSG_SENDING, 500);
        }
    }


    @Override
    public void onDismiss() {
        if(mPaletteView!=null){
            mPaletteView.clear();
        }
    }
}
