package com.tg.coloursteward.updateapk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tg.coloursteward.BuildConfig;
import com.tg.coloursteward.R;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.util.Tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * @author coolszy
 * @date 2012-4-26
 * @blog http://blog.92coding.com
 */

public class UpdateManager {
    private final static int DOWNLOAD_COMPLETE = 0;
    private final static int DOWNLOAD_FAIL = 1;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;
    /* 保存解析的XML信息 */
    private ApkInfo apkInfo;
    private Context mContext;
    private boolean isHome;
    /* 更新进度条 */
    private ProgressBar mProgress;
    // 下载目录
    private File updateDir = null;
    private File updateFile = null;
    // .apk下载地址
    private String clientUrlPath = "";
    private String appName;
    private Thread thread;

    /**
     * 弹出框
     *
     * @param context
     * @param isHome
     */
    private AlertDialog ProgressBarDialog;
    private AlertDialog dialog;
    private TextView tvContent;
    private Button btnOk;
    private Button btnCancel;

    public UpdateManager(Context context, boolean isHome) {
        this.mContext = context;
        this.isHome = isHome;
    }

    /**
     * 检查软件是否有更新版本
     *
     * @return
     */

    public void checkUpdate(ApkInfo apkinfo, Boolean isshow) {
        // 获取当前软件版本
        int versionCode = getVersionCode(mContext);
        if (apkinfo != null) {
            apkInfo = apkinfo;
        }
        // 获取服务器版本
        int result = apkinfo.getApkCode();
        if (result == 0) {
            showNoticeDialog();//非强制更新
        } else if (result == -1) {
            showNoticeDialogMust();//强制更新
        } else if (result == -2 && isshow == true) {
            showNoticeDialog();//小版本，非强制更新
        } else {//提示最新版本
            if (!isHome) {
                Toast.makeText(mContext, R.string.soft_update_no, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager().getPackageInfo(
                    "com.tg.coloursteward", 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionName
            versionName = context.getPackageManager().getPackageInfo(
                    "com.tg.coloursteward", 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 显示软件更新对话框(非强制更新)
     */
    private void showNoticeDialog() {
        if (dialog == null) {
            DisplayMetrics metrics = Tools.getDisplayMetrics(mContext);
            dialog = new AlertDialog.Builder(mContext).create();
            dialog.setCancelable(false);
            Window window = dialog.getWindow();
            dialog.show();
            LinearLayout layout = (LinearLayout) LayoutInflater.from(mContext)
                    .inflate(R.layout.update_dialog_layout, null);
            tvContent = (TextView) layout.findViewById(R.id.dialog_msg);
            btnOk = (Button) layout.findViewById(R.id.btn_ok);
            btnCancel = (Button) layout.findViewById(R.id.btn_cancel);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//立即更新
                    dialog.dismiss();
                    // 显示下载对话框
                    showDownloadDialog("");
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//稍后更新
                    dialog.dismiss();
                }
            });
            window.setContentView(layout);
            WindowManager.LayoutParams p = window.getAttributes();
            p.width = ((int) (metrics.widthPixels - 80 * metrics.density));
            window.setAttributes(p);
        }
        tvContent.setText(apkInfo.getApkLog());
        dialog.show();
    }

    /**
     * 显示软件更新对话框(强制更新)
     */
    private void showNoticeDialogMust() {
        if (dialog == null) {
            DisplayMetrics metrics = Tools.getDisplayMetrics(mContext);
            dialog = new AlertDialog.Builder(mContext).create();
            dialog.setCancelable(false);
            Window window = dialog.getWindow();
            dialog.show();
            LinearLayout layout = (LinearLayout) LayoutInflater.from(mContext)
                    .inflate(R.layout.update_dialog_layout, null);
            tvContent = (TextView) layout.findViewById(R.id.dialog_msg);
            btnOk = (Button) layout.findViewById(R.id.btn_ok);
            btnCancel = (Button) layout.findViewById(R.id.btn_cancel);
            ImageView ivLine = (ImageView) layout.findViewById(R.id.iv_line);
            btnCancel.setVisibility(View.GONE);
            ivLine.setVisibility(View.GONE);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//立即更新
                    dialog.dismiss();
                    // 显示下载对话框
                    showDownloadDialog("");
                }
            });
            window.setContentView(layout);
            WindowManager.LayoutParams p = window.getAttributes();
            p.width = ((int) (metrics.widthPixels - 80 * metrics.density));
            window.setAttributes(p);
        }
        tvContent.setText(apkInfo.getApkLog());
        dialog.show();
    }

    /**
     * 显示软件下载进度框
     */
    public void showDownloadDialog(String downUrl) {
        if (ProgressBarDialog == null) {
            DisplayMetrics metrics = Tools.getDisplayMetrics(mContext);
            ProgressBarDialog = new AlertDialog.Builder(mContext).create();
            ProgressBarDialog.setCanceledOnTouchOutside(false);
            Window window = ProgressBarDialog.getWindow();
            ProgressBarDialog.show();
            LinearLayout layout = (LinearLayout) LayoutInflater.from(mContext)
                    .inflate(R.layout.softupdate_progress, null);
            mProgress = (ProgressBar) layout.findViewById(R.id.update_progress);
            Button btn_cancel = (Button) layout.findViewById(R.id.btn_cancel);
            // 取消更新
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProgressBarDialog.dismiss();
                    // 设置取消状态
                    cancelUpdate = true;
                }
            });
            window.setContentView(layout);
            WindowManager.LayoutParams p = window.getAttributes();
            p.width = ((int) (metrics.widthPixels - 80 * metrics.density));
            window.setAttributes(p);
        }
        // 下载文件
        downloadApk(downUrl);

    }

    /**
     * 启动线程下载apk
     */
    private void downloadApk(String downUrl) {
        if (!"".equals(downUrl)) {
            clientUrlPath = downUrl;
        }
        appName = mContext.getString(R.string.app_name);
        // 判断是否有外部存储
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            Date date = new Date();
            String dateString = "" + (date.getMonth() + 1) + date.getDay()
                    + date.getHours() + date.getMinutes() + date.getSeconds();
            updateDir = new File(Environment.getExternalStorageDirectory(),
                    Contants.downloadDir);
            updateFile = new File(updateDir.getPath(), "WeiTown" + dateString
                    + ".apk");
        }

        // 使用新线程去下载

        thread = new Thread(new updateRunnable());
        thread.start();
    }

    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD_COMPLETE:
                    if (ProgressBarDialog != null) {
                        ProgressBarDialog.dismiss();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            installApk();
                        }
                    }, 300);
                    break;
                case DOWNLOAD_FAIL:
                    // 下载失败处理
                    if (ProgressBarDialog != null) {
                        ProgressBarDialog.dismiss();
                    }
                    break;
            }

        }
    };

    class updateRunnable implements Runnable {
        Message message = updateHandler.obtainMessage();

        @Override
        public void run() {

            message.what = DOWNLOAD_COMPLETE;
            try {
                if (!updateDir.exists()) {
                    updateDir.mkdirs();
                }
                if (!updateFile.exists()) {
                    updateFile.createNewFile();
                }

                long downloadSize = downloadUpdateFile(clientUrlPath,
                        updateFile);
                if (downloadSize > 0) {
                    // 下载完毕通知
                    updateHandler.sendMessage(message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();

                message.what = DOWNLOAD_FAIL;
                // 下载失败通知
                updateHandler.sendMessage(message);
            }

        }

    }


    /**
     * 安装APK文件
     */
    private void installApk() {
        if (!updateFile.exists()) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT > 23) { //判读版本是否在7.0含以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri contentUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", updateFile);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.parse("file://" + updateFile.toString()), "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
        //android.os.Process.killProcess(android.os.Process.myPid());// 如果不加上这句的话在apk安装完成之后点击单开会崩溃


    }

    // 下载文件
    public long downloadUpdateFile(String downloadUrl, File saveFile) throws IOException {
        int downloadCount = 0;
        int currentSize = 0;
        long totalSize = 0;
        int updateTotalSize = 0;

        HttpURLConnection httpConnection = null;
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            URL url = new URL(downloadUrl);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection
                    .setRequestProperty("User-Agent", "PacificHttpClient");
            if (currentSize > 0) {
                httpConnection.setRequestProperty("RANGE", "bytes="
                        + currentSize + "-");
            }
            httpConnection.setConnectTimeout(10000);
            httpConnection.setReadTimeout(20000);
            updateTotalSize = httpConnection.getContentLength();
            if (httpConnection.getResponseCode() == 404) {
                throw new Exception("fail!");
            }
            is = httpConnection.getInputStream();
            fos = new FileOutputStream(saveFile, false);
            byte buffer[] = new byte[1024];
            int readsize = 0;
            while ((readsize = is.read(buffer)) > 0) {
                fos.write(buffer, 0, readsize);
                totalSize += readsize;
                // 更新下载进度 eg:10%
                if ((downloadCount == 0)
                        || (int) (totalSize * 100 / updateTotalSize) - 1 > downloadCount) {
                    downloadCount += 1;
                    mProgress.setProgress(downloadCount);
                    if (cancelUpdate) {
//                        httpConnection.disconnect();
                    }
                }
            }
        } catch (Exception e) {
            String log = e.toString();
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
        return totalSize;
    }

    public String getMIMEType(File var0) {
        String var1 = "";
        String var2 = var0.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
    }
}
