package com.youmai.hxsdk;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.http.DownloadListener;
import com.youmai.hxsdk.http.FileAsyncTaskDownload;
import com.youmai.hxsdk.im.IMHelper;
import com.youmai.hxsdk.im.cache.CacheMsgFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 作者：create by YW
 * 日期：2016.11.23 16:15
 * 描述：
 */
public class IMFilePreviewActivity extends SdkBaseActivity {

    public static final String IM_FILE_BEAN = "im_file_bean";
    public static final String FULL_FILE_BEAN = "full_file_bean";
    public static final String FULL_VIEW_FILE = "full_view_file";

    private static final int MSG_UPDATE = 0x01;

    private ImageView tv_preview_back; //返回键
    private ImageView iv_file_logo; //文件logo
    private TextView tv_file_name; //文件name
    private LinearLayout ll_progress_parent;//进度条父布局
    private ProgressBar pb_progress_bar; //进度条
    private TextView tv_open_file; //第三方打开文档
    private TextView downloadingProgressText;//当前下载进度

    private boolean isOpenFile = false;//是否打开文件
    public boolean isFullViewFile = false;//是否弹屏处过来打开的
    private boolean isExit = false;//是否关闭线程

    private String totalSize;//文件总大小

//    private Handler downloadHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//
//            /*switch (msg.arg1) {
//                case MSG_UPDATE: //更新进度
//                    LogUtils.e(Constant.SDK_UI_TAG, "currentProgress = " + currentProgress + " ; length = " + length);
//                    if (currentProgress == length) {
//                        SystemClock.sleep(500);
//                        ll_progress_parent.setVisibility(View.GONE);
//                        tv_open_file.setVisibility(View.VISIBLE);
//                        isOpenFile = true;
//                    }
//                    break;
//            }*/
//        }
//    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im_filepreview);

        initView();
        initDownload();

    }

    @Override
    public void onResume() {
        super.onResume();
        isExit = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        isExit = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initView() {

        tv_preview_back = (ImageView) findViewById(R.id.tv_im_preview_back);
        iv_file_logo = (ImageView) findViewById(R.id.iv_im_file_type);
        tv_file_name = (TextView) findViewById(R.id.tv_im_file_name);
        ll_progress_parent = (LinearLayout) findViewById(R.id.ll_im_progress_parent);
        pb_progress_bar = (ProgressBar) findViewById(R.id.pb_im_progress_bar);
        tv_open_file = (TextView) findViewById(R.id.tv_im_open_file);
        downloadingProgressText = (TextView) findViewById(R.id.downloading_progress_text);
        initListener();

    }

    private CacheMsgBean cacheMsgBean;
    private CacheMsgFile cacheMsgFile;

    private void initDownload() {

        cacheMsgBean = getIntent().getParcelableExtra(IM_FILE_BEAN);
        isFullViewFile = getIntent().getBooleanExtra(FULL_VIEW_FILE, false);

        if (null == cacheMsgBean) {
            return;
        }

        cacheMsgFile = (CacheMsgFile) cacheMsgBean.getJsonBodyObj();

        if (null == cacheMsgFile) {
            return;
        }

        boolean isFileExist = new File(FileConfig.getBigFileDownLoadPath(), cacheMsgFile.getFileName()).exists();
        if ((cacheMsgBean.getMsgType() == CacheMsgBean.RECEIVE_FILE && !cacheMsgBean.isRightUI()&& isFileExist)
                || (cacheMsgBean.getMsgType() == CacheMsgBean.RECEIVE_FILE && cacheMsgBean.isRightUI())) {
            isOpenFile = true;
            ll_progress_parent.setVisibility(View.GONE);
            tv_open_file.setVisibility(View.VISIBLE);
        }

        totalSize = IMHelper.convertFileSize(cacheMsgFile.getFileSize());
        downloadingProgressText.setText(String.format(getString(R.string.hx_sdk_downloading2), "0k", totalSize));
        tv_file_name.setText(cacheMsgFile.getFileName());
        iv_file_logo.setImageResource(IMHelper.getFileImgRes(cacheMsgFile.getFileName(), false));

        if (cacheMsgBean.getMsgType() == CacheMsgBean.RECEIVE_FILE && !isFileExist) {
            //new Thread(new DownloadFile(cacheMsgFile.getFileUrl(), cacheMsgFile.getFileName())).start();
            breakDownload(cacheMsgFile.getFileUrl(), cacheMsgFile.getFileName());
        }

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
                } else {
                    breakDownload(cacheMsgFile.getFileUrl(), cacheMsgFile.getFileName());
                }
            }
        });
    }

    private void breakDownload(String path, String fileName) {
        FileAsyncTaskDownload load = new FileAsyncTaskDownload(new DownloadListener() {
            @Override
            public void onProgress(int cur, int total) {
                pb_progress_bar.setProgress(cur);
                pb_progress_bar.setMax(total);
                downloadingProgressText.setText(String.format(getString(R.string.hx_sdk_downloading2), IMHelper.convertFileSize(cur), IMHelper.convertFileSize(total)));
            }

            @Override
            public void onFail(String err) {
                isOpenFile = false;
                tv_open_file.setVisibility(View.VISIBLE);
                tv_open_file.setText("重新下载");
            }

            @Override
            public void onSuccess(String path) {
                ll_progress_parent.setVisibility(View.GONE);
                tv_open_file.setText(R.string.hx_sdk_open_other_app);
                tv_open_file.setVisibility(View.VISIBLE);
                isOpenFile = true;
            }
        }, fileName);
        load.setDownloadpath(FileConfig.getBigFileDownLoadPath());

        load.execute(path);
    }

    private final static int THREAD_COUNT = 1;
    private int finishedThread = 0;
    private int length = 0;
    private int mCurrentProgress;//当前进度

    private class DownLoadThread implements Runnable {
        int startIndex;
        int endIndex;
        int threadId;
        private String mDownloadUrl;
        private String mFileName;

        public DownLoadThread(int startIndex, int endIndex, int threadId, String downloadUrl, String name) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.threadId = threadId;
            this.mDownloadUrl = downloadUrl;
            this.mFileName = name;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            File progressFile = null;
            try {
                progressFile = new File(FileConfig.getBigFileDownLoadPath(), mFileName + "-" + threadId + ".txt");
                //判断进度临时文件是否存在
                if (progressFile.exists()) {
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(progressFile);
                        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                        //从进度临时文件中读取出上一次下载的总进度，然后与原本的开始位置相加，得到新的开始位置
                        int lastProgress = Integer.parseInt(br.readLine());
                        startIndex += lastProgress;

                        //把上次下载的进度显示至进度条
                        mCurrentProgress += lastProgress;
                        pb_progress_bar.setProgress(mCurrentProgress);

                        //downloadHandler.sendEmptyMessage(1);//发送消息，让主线程刷新UI进度

                    } catch (Exception e) {
                        if (null != progressFile && progressFile.exists()) {
                            progressFile.delete();
                        }
                        e.printStackTrace();
                    } finally {
                        if (null != fis) {
                            fis.close();
                        }
                    }
                }

                URL url = new URL(mDownloadUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10 * 1000);
                conn.setReadTimeout(10 * 1000);
                //设置本次http请求所请求的数据的区间
                conn.setRequestProperty("Range", "bytes=" + startIndex + "-" + endIndex);

                if (conn.getResponseCode() == 206) {
                    //流里此时只有1/threadCount原文件的数据
                    InputStream is = conn.getInputStream();
                    byte[] buf = new byte[1024 * 2];
                    int len = -1;
                    int total = 0;
                    //拿到临时文件的输出流
                    File file = new File(FileConfig.getBigFileDownLoadPath(), mFileName + ".tmp");
                    raf = new RandomAccessFile(file, "rwd");
                    //把文件的写入位置移动至startIndex
                    raf.seek(startIndex);
                    while ((len = is.read(buf)) != -1 && !isExit) {

                        raf.write(buf, 0, len);//每次读取流里数据之后，同步把数据写入临时文件
                        total += len;

                        //每次读取流里数据之后，把本次读取的数据的长度显示至进度条
                        mCurrentProgress += len;
                        pb_progress_bar.setProgress(mCurrentProgress);

                        //downloadHandler.sendEmptyMessage(1);//发送消息，让主线程刷新UI进度
                        //LogUtils.e(Constant.SDK_UI_TAG, "file progress : " + mCurrentProgress);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                downloadingProgressText.setText(String.format(getString(R.string.hx_sdk_downloading2), IMHelper.convertFileSize(mCurrentProgress), totalSize));
                            }
                        });

                        //生成一个专门用来记录下载进度的临时文件
                        RandomAccessFile progressRaf = new RandomAccessFile(progressFile, "rwd");
                        //每次读取流里数据之后，同步把当前线程下载的总进度写入进度临时文件中
                        progressRaf.write((total + "").getBytes());
                        progressRaf.close();
                    }

                    //LogUtils.e(Constant.SDK_UI_TAG, "线程" + threadId + "下载完毕--------------YW参上！");

                    finishedThread++;
                    synchronized (mDownloadUrl) {
                        if (finishedThread == THREAD_COUNT && !isExit) {
                            for (int i = 0; i < THREAD_COUNT; i++) {
                                File f = new File(FileConfig.getBigFileDownLoadPath(), mFileName + "-" + i + ".txt");
                                f.delete();
                                if (i == THREAD_COUNT - 1) {
                                    file.renameTo(new File(FileConfig.getBigFileDownLoadPath(), mFileName));
                                    IMFilePreviewActivity.this.runOnUiThread(new Runnable() { //刷UI
                                        @Override
                                        public void run() {
                                            ll_progress_parent.setVisibility(View.GONE);
                                            tv_open_file.setVisibility(View.VISIBLE);
                                            isOpenFile = true;
                                        }
                                    });
                                }
                            }
                            finishedThread = 0;
                        }
                    }
                } else {
                    if (null != progressFile && progressFile.exists()) {
                        progressFile.delete();
                    }
                }
            } catch (Exception e) {
                if (null != progressFile && progressFile.exists()) {
                    progressFile.delete();
                }
                e.printStackTrace();
            } finally {
                if (null != raf) {
                    try {
                        raf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (null != conn) {
                    conn.disconnect();
                }
            }
        }
    }

//    public class DownloadFile implements Runnable {
//
//        private String mDownloadUrl;
//        private String mFileName;
//
//        public DownloadFile(String downloadUrl, String name) {
//            this.mDownloadUrl = downloadUrl;
//            this.mFileName = name;
//        }
//
//        @Override
//        public void run() {
//            HttpURLConnection conn = null;
//            File file = null;
//            File tmpFile = null;
//            InputStream is = null;
//            OutputStream os = null;
//            int mFileTotalLength = 0;
//            Message msg;
//            try {
//                URL url = new URL(mDownloadUrl);
//                conn = (HttpURLConnection) url.openConnection();
//                conn.setConnectTimeout(30 * 1000);
//                conn.setReadTimeout(30 * 1000);
//                conn.setRequestMethod("GET");
//
//                file = new File(FileConfig.getBigFileDownLoadPath(), mFileName);
//                if (!file.exists()) {
//                    String downLoadFileTmpName = mFileName + ".tmp"; // 设置下载的临时文件名
//                    tmpFile = new File(FileConfig.getBigFileDownLoadPath(), downLoadFileTmpName);
//                } else {
//                    return;
//                }
//
//                long startPosition = tmpFile.length(); // 已下载的文件长度
//                String start = "bytes=" + startPosition + "-";
//
//                conn.setRequestProperty("RANGE", start); //支持断点续传
//                conn.connect();
//
//                // this will be useful so that you can show a typical 0-100% progress bar
//                mFileTotalLength = conn.getContentLength(); // -1
//                // download the file
//                is = new BufferedInputStream(conn.getInputStream());
//                os = new FileOutputStream(tmpFile);
//
//                byte data[] = new byte[1024 * 4];
//                long currentTotalSize = startPosition;
//                int readSize = -1;
//
//
//                final long finalCurrentTotalSize1 = currentTotalSize;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        tv_download_progress.setText(IMHelper.convertFileSize(finalCurrentTotalSize1));
//                    }
//                });
//
//                while ((readSize = is.read(data)) != -1) {
//
//                    os.write(data, 0, readSize);
//                    currentTotalSize += readSize;
//
//                    // publishing the progress....
//                    long startTime = System.currentTimeMillis();
//                    do {
//                        msg = downloadHandler.obtainMessage();
//                        msg.arg1 = MSG_UPDATE;
//                        msg.arg2 = (int) currentTotalSize;
//                        msg.what = (int) (currentTotalSize * 100 / mFileTotalLength);
//                        LogUtils.e(Constant.SDK_UI_TAG, "file progress : " + (currentTotalSize * 100 / mFileTotalLength));
//                        downloadHandler.sendMessage(msg);
//
//                        final long finalCurrentTotalSize = currentTotalSize;
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                tv_download_progress.setText(IMHelper.convertFileSize(finalCurrentTotalSize));
//                            }
//                        });
//
//                    } while (System.currentTimeMillis() - startTime > 300);
//
//                    os.flush();
//                }
//
//            } catch (IOException e) {
//                if (tmpFile.exists()) {
//                    tmpFile.delete();
//                }
//                e.printStackTrace();
//            } finally {
//                if (is != null) {
//                    try {
//                        is.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                if (os != null) {
//                    try {
//                        os.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                if (conn != null) {
//                    conn.disconnect();
//                }
//
//                try {
//                    if (tmpFile.length() != mFileTotalLength) {
//                        return;
//                    }
//                    tmpFile.renameTo(file);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    /**
     * 打开文件
     *
     * @param file
     */
    private void openFile(File file) {

//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        //设置intent的Action属性
//        intent.setAction(Intent.ACTION_VIEW);
//        //获取文件file的MIME类型
//        String type = getMIMEType(file);
//        //设置intent的data和Type属性。
//        intent.setDataAndType(Uri.fromFile(file), type);
//        //跳转
//        startActivity(intent);

        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            //设置intent的Action属性
            intent.setAction(Intent.ACTION_VIEW);
            //获取文件file的MIME类型
            String type = getMIMEType(file);
            //设置intent的data和Type属性。
            intent.setDataAndType(Uri.fromFile(file), type);
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
