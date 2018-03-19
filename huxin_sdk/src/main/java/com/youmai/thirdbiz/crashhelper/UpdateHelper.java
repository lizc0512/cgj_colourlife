package com.youmai.thirdbiz.crashhelper;

import android.app.Activity;
import android.app.Dialog;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Update Helper.
 * @version   Revision History
 * <pre>
 * Author      Version       Date        Changes
 * Kevin Feng   1.0        2014年10月23日       Created
 *
 * </pre>
 * @since 1.
 */
public class UpdateHelper {

    private static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

    private Activity mAct;

    private String mUrl;

    private IUpdateCallback updateCallback;

    private static final int DOWN_ERROR = 1;

    private static final int SET_PROGREEESSBAR_MAX = 2; //设置进度条最大值

    private static final int UPDATE_PROGRESSBAR_TEXT = 3; //更新进度条信息

    private static final int DISMISS_PROGRESSDIALOG = 4; //关闭对话框

    private ExecutorService service;

    private ProgressBar mProgressBar;

    private TextView mTextView;

    private int maxSize = 0;

    private int nowSize = 0;

    public interface IUpdateCallback {
        void onCallBack(boolean isFinish);
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case DOWN_ERROR:
                //下载apk失败
                Toast.makeText(mAct, "下载失败", Toast.LENGTH_SHORT).show();
                ((Dialog) msg.obj).dismiss();
                updateCallback.onCallBack(false);
                break;
            case SET_PROGREEESSBAR_MAX: //设置进度条最大值
                mProgressBar.setMax(maxSize);

                break;
            case UPDATE_PROGRESSBAR_TEXT: //更新进度条信息
                mProgressBar.setProgress(nowSize);
                //                float tag = (float) (nowSize / (maxSize * 1.00));
                mTextView.setText(SizeUtil.convertFileSize(nowSize) + "/" + SizeUtil.convertFileSize(maxSize));
                break;
            case DISMISS_PROGRESSDIALOG: //关闭进度条
                ((Dialog) msg.obj).dismiss();
                updateCallback.onCallBack(true);
                break;
            }
        }
    };

    public UpdateHelper(Activity act, String url, IUpdateCallback updateCallback) {
        this.mAct = act;
        this.mUrl = url;
        this.updateCallback = updateCallback;
        service = Executors.newSingleThreadExecutor();
    }

    /**
     * 从服务器下载.
     * @throws Exception
     */
    private File getFileFromServer() throws Exception {
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(mUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小 
            maxSize = conn.getContentLength();
            handler.sendEmptyMessage(SET_PROGREEESSBAR_MAX);
            InputStream is = conn.getInputStream();
            File file = new File(Environment.getExternalStorageDirectory(), "ubantmark_updata.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                //获取当前下载量
                nowSize = total;
                handler.sendEmptyMessage(UPDATE_PROGRESSBAR_TEXT);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }

    /**
     * 从服务器中下载APK
     */
    public void downLoadApk() {
        final Dialog dlg = new Dialog(mAct, R.style.up_dialog);
        dlg.setContentView(R.layout.cgj_update_dlg);
        dlg.setTitle("正在下载中");
        dlg.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
        mTextView = (TextView) dlg.findViewById(R.id.tv);
        mProgressBar = (ProgressBar) dlg.findViewById(R.id.pbar);
        Button btnCancel = (Button) dlg.findViewById(R.id.btn_1);
        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.dismiss();
                service.shutdown();
                handler.removeMessages(1);
                handler.removeMessages(2);
                handler.removeMessages(3);
                handler.removeMessages(4);
                //删除临时apk
                updateCallback.onCallBack(false);
            }
        });
        dlg.setCancelable(false);
        dlg.show();
        service.execute(new Runnable() {

            @Override
            public void run() {
                File file = null;
                try {
                    file = getFileFromServer();

                    Thread.sleep(300);
                } catch (Exception e) {
                    handler.sendMessage(handler.obtainMessage(DOWN_ERROR, dlg));
                    return;

                }

                if (!service.isShutdown()) {
                        handler.sendMessage(handler.obtainMessage(DISMISS_PROGRESSDIALOG, dlg));
                        ToolHelper.installApk(mAct, file);
                }

            }
        });
    }



}
