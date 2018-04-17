package com.youmai.hxsdk;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.im.IMHelper;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.utils.LogUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 作者：create by YW
 * 日期：2016.11.23 16:15
 * 描述：
 */
public class WrapIMFilePreviewActivity extends FragmentActivity {

    public static final String IM_FILE_BEAN = "im_file_bean";

    public static final String IM_IS_FLOAT = "im_is_float";

    private static final int MSG_UPDATE = 0x01;

    private ImageView tv_preview_back; //返回键
    private ImageView iv_file_logo; //文件logo
    private TextView tv_file_name; //文件name
    private LinearLayout ll_progress_parent;//进度条父布局
    private ProgressBar pb_progress_bar; //进度条
    private TextView tv_open_file; //第三方打开文档
    private TextView downloadingProgressText;//当前下载进度

    private boolean isOpenFile = false;

    private boolean isFloat = true;

    private String totalSize;

    private Handler downloadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            LogUtils.e(Constant.SDK_UI_TAG, msg.toString() + "; " + msg.what + ";" + msg.arg1);
            switch (msg.arg1) {
                case MSG_UPDATE: //更新进度
                    pb_progress_bar.setProgress(msg.what);
                    if (pb_progress_bar.getProgress() == 100) {
                        SystemClock.sleep(1000);
                        ll_progress_parent.setVisibility(View.GONE);
                        tv_open_file.setVisibility(View.VISIBLE);
                        isOpenFile = true;
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im_filepreview);

        initView();
        initDownload();

    }

    private void initView() {

        tv_preview_back = (ImageView) findViewById(R.id.tv_im_preview_back);
        iv_file_logo = (ImageView) findViewById(R.id.iv_im_file_type);
        tv_file_name = (TextView) findViewById(R.id.tv_im_file_name);
        ll_progress_parent = (LinearLayout) findViewById(R.id.ll_im_progress_parent);
        pb_progress_bar = (ProgressBar) findViewById(R.id.pb_im_progress_bar);
        tv_open_file = (TextView) findViewById(R.id.tv_im_open_file);
        downloadingProgressText = (TextView) findViewById(R.id.downloading_progress_text);
        pb_progress_bar.setMax(100);

        initListener();

    }

    private CacheMsgBean cacheMsgBean;

    private CacheMsgFile cacheMsgFile;

    private void initDownload() {

        cacheMsgBean = getIntent().getParcelableExtra(IM_FILE_BEAN);

        if (null == cacheMsgBean) {
            finish();
            return;
        }

        cacheMsgFile = (CacheMsgFile) cacheMsgBean.getJsonBodyObj();

        if (null == cacheMsgFile) {
            finish();
            return;
        }

        boolean isFileExist = new File(FileConfig.getBigFileDownLoadPath(), cacheMsgFile.getFileName()).exists();
        if ((cacheMsgBean.getMsgType() == CacheMsgBean.SEND_FILE && !cacheMsgBean.isRightUI()
                && isFileExist)
                || (cacheMsgBean.getMsgType() == CacheMsgBean.SEND_FILE && cacheMsgBean.isRightUI())) {
            isOpenFile = true;
            ll_progress_parent.setVisibility(View.GONE);
            tv_open_file.setVisibility(View.VISIBLE);
        }

        totalSize = IMHelper.convertFileSize(cacheMsgFile.getFileSize());
        downloadingProgressText.setText(String.format(getString(R.string.hx_sdk_downloading2), "0k", totalSize));
        tv_file_name.setText(cacheMsgFile.getFileName());
        iv_file_logo.setImageResource(cacheMsgFile.getFileRes());

        if (cacheMsgBean.getMsgType() == CacheMsgBean.SEND_FILE && !isFileExist) {
            new Thread(new DownloadFile(cacheMsgFile.getFileUrl(), cacheMsgFile.getFileName())).start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initListener() {

        tv_preview_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_open_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpenFile) {
                    if (!cacheMsgBean.isRightUI()) {
                        openFile(new File(FileConfig.getBigFileDownLoadPath(), cacheMsgFile.getFileName()));
                    } else {
                        openFile(new File(cacheMsgFile.getFilePath()));
                    }

                }
            }
        });

    }

    public class DownloadFile implements Runnable {

        private String mDownloadUrl;
        private String mFileName;

        public DownloadFile(String downloadUrl, String name) {
            this.mDownloadUrl = downloadUrl;
            this.mFileName = name;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            File file = null;
            InputStream is = null;
            OutputStream os = null;
            int mFileTotalLength = 0;
            Message msg;
            try {
                URL url = new URL(mDownloadUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(60 * 1000);
                conn.setReadTimeout(60 * 1000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("RANGE", "bytes=" + 0 + "-"); //支持断点续传
                conn.connect();

                file = new File(FileConfig.getBigFileDownLoadPath(), mFileName);
                if (!file.exists()) {
                    file.createNewFile();
                } else {
                    return;
                }

                // this will be useful so that you can show a typical 0-100% progress bar
                mFileTotalLength = conn.getContentLength(); // -1
                // download the file
                is = new BufferedInputStream(conn.getInputStream());
                os = new FileOutputStream(file);

                //RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                //raf.seek(0);//把文件的写入位置移动至startIndex

                byte data[] = new byte[1024 * 8];
                long currentTotalSize = 0;
                int readSize = -1;

                while ((readSize = is.read(data)) != -1) {
                    os.write(data, 0, readSize);
                    currentTotalSize += readSize;

                    // publishing the progress....
                    long startTime = System.currentTimeMillis();
                    do {
                        msg = downloadHandler.obtainMessage();
                        msg.arg1 = MSG_UPDATE;
                        msg.arg2 = (int) currentTotalSize;
                        msg.what = (int) (currentTotalSize * 100 / mFileTotalLength);
                        LogUtils.e(Constant.SDK_UI_TAG, (currentTotalSize * 100 / mFileTotalLength) + "");
                        downloadHandler.sendMessage(msg);

                        final long finalCurrentTotalSize = currentTotalSize;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                downloadingProgressText.setText(String.format(getString(R.string.hx_sdk_downloading2), IMHelper.convertFileSize(finalCurrentTotalSize), totalSize));
                            }
                        });

                    } while (System.currentTimeMillis() - startTime > 500);

                    os.flush();
                }

            } catch (IOException e) {
                if (file != null && file.exists()) {
                    file.delete();
                }
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
    }

    /**
     * 打开文件
     *
     * @param file
     */
    private void openFile(File file) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (isFloat) {
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            }


            //设置intent的Action属性
            intent.setAction(Intent.ACTION_VIEW);
            //获取文件file的MIME类型
            String type = getMIMEType(file);
            //设置intent的data和Type属性。
            intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
            //跳转
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "该文件无法打开", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param file
     */
    private String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* 获取文件的后缀名*/
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    private final String[][] MIME_MapTable = { //{后缀名，MIME类型}

            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };
}
