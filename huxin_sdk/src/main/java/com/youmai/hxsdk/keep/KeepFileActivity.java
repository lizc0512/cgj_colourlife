package com.youmai.hxsdk.keep;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.dialog.HxKeepDialog;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.http.DownLoadingListener;
import com.youmai.hxsdk.http.FileAsyncTaskDownload;
import com.youmai.hxsdk.http.HttpConnector;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.im.IMHelper;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.StringUtils;

import java.io.File;
import java.lang.reflect.Method;

/**
 * 作者：create by YW
 * 日期：2016.11.23 16:15
 * 描述：
 */
public class KeepFileActivity extends SdkBaseActivity {

    public static final String IM_FILE_BEAN = "im_file_bean";
    public static final String FULL_VIEW_FILE = "full_view_file";
    public static final String KEEP_ID = "keep_id";

    private ImageView iv_file_logo; //文件logo
    private TextView tv_file_name; //文件name
    private LinearLayout ll_progress_parent;//进度条父布局
    private ProgressBar pb_progress_bar; //进度条
    private TextView tv_open_file; //第三方打开文档
    private TextView downloadingProgressText;//当前下载进度

    private boolean isOpenFile = false;//是否打开文件
    private boolean isExit = false;//是否关闭线程

    private String totalSize;//文件总大小
    private String keepId; //收藏Id

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_keep_filepreview);

        initTitle();
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setShortcutsVisibleInner", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hx_keep_more_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_more) {
            forwardOrDeleteItem();
        }
        return false;
    }

    private void forwardOrDeleteItem() {
        HxKeepDialog hxDialog = new HxKeepDialog(mContext);
        HxKeepDialog.HxCallback callback =
                new HxKeepDialog.HxCallback() {
                    @Override
                    public void onForward() {
                        Intent intent = new Intent();
                        intent.setAction("com.youmai.huxin.recent");
                        intent.putExtra("type", "forward_msg");
                        intent.putExtra("data", cacheMsgBean);
                        mContext.startActivity(intent);
                    }

                    @Override
                    public void onDelete() {
                        delKeep();
                    }
                };
        hxDialog.setHxCollectDialog(callback);
        hxDialog.show();
    }

    private void delKeep() {
        String url = AppConfig.COLLECT_DEL;
        ReqKeepDel del = new ReqKeepDel(this);
        del.setIds(keepId);

        HttpConnector.httpPost(url, del.getParams(), new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespBaseBean baseBean = GsonUtil.parse(response, RespBaseBean.class);
                if (baseBean != null && baseBean.isSuccess()) {
                    Toast.makeText(mContext, R.string.collect_del_success, Toast.LENGTH_SHORT).show();
                    Intent data = new Intent();
                    data.putExtra(KEEP_ID, keepId);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                } else {
                    Toast.makeText(mContext, R.string.collect_del_fail, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("文件");

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {

        keepId = getIntent().getStringExtra(KEEP_ID);

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

        if (null == cacheMsgBean) {
            return;
        }

        cacheMsgFile = (CacheMsgFile) cacheMsgBean.getJsonBodyObj();

        if (null == cacheMsgFile) {
            return;
        }

        boolean isFileExist = new File(FileConfig.getBigFileDownLoadPath(), cacheMsgFile.getFileName()).exists();
        if ((cacheMsgBean.getMsgType() == CacheMsgBean.MSG_TYPE_FILE && isFileExist)) {
            isOpenFile = true;
            ll_progress_parent.setVisibility(View.GONE);
            tv_open_file.setVisibility(View.VISIBLE);
        }

        if (cacheMsgBean.getMsgType() == CacheMsgBean.MSG_TYPE_FILE && cacheMsgBean.isRightUI()) {
            File file = new File(cacheMsgFile.getFilePath());
            if (file.exists()) {
                isFileExist = true;
                isOpenFile = true;
                ll_progress_parent.setVisibility(View.GONE);
                tv_open_file.setVisibility(View.VISIBLE);
            }
        }

        totalSize = IMHelper.convertFileSize(cacheMsgFile.getFileSize());
        downloadingProgressText.setText(String.format(getString(R.string.hx_sdk_downloading2), "0k", totalSize));
        tv_file_name.setText(cacheMsgFile.getFileName());
        iv_file_logo.setImageResource(IMHelper.getFileImgRes(cacheMsgFile.getFileName(), false));

        if (cacheMsgBean.getMsgType() == CacheMsgBean.MSG_TYPE_FILE && !isFileExist) {
            breakDownload(AppConfig.DOWNLOAD_IMAGE + cacheMsgFile.getFid(), cacheMsgFile.getFileName());
        }

    }

    private void initListener() {
        tv_open_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpenFile) {
                    if (StringUtils.isEmpty(cacheMsgFile.getFilePath()) && !(new File(cacheMsgFile.getFilePath()).exists())) {
                        openFile(new File(FileConfig.getBigFileDownLoadPath(), cacheMsgFile.getFileName()));
                    } else {
                        openFile(new File(cacheMsgFile.getFilePath()));
                    }
                }
            }
        });
    }

    private void breakDownload(String path, String fileName) {
        FileAsyncTaskDownload load = new FileAsyncTaskDownload(new DownLoadingListener() {
            @Override
            public void onProgress(int cur, int total) {
                //pb_progress_bar.setProgress(rate);
                pb_progress_bar.setProgress(cur);
                pb_progress_bar.setMax(total);
                downloadingProgressText.setText(String.format(getString(R.string.hx_sdk_downloading2), IMHelper.convertFileSize(cur), IMHelper.convertFileSize(total)));
            }

            @Override
            public void downloadFail(String err) {

            }

            @Override
            public void downloadSuccess(String path) {
                ll_progress_parent.setVisibility(View.GONE);
                tv_open_file.setVisibility(View.VISIBLE);
                isOpenFile = true;
            }
        }, fileName);
        load.setDownloadpath(FileConfig.getBigFileDownLoadPath());

        load.execute(path);
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
            Toast.makeText(getApplicationContext(), getString(R.string.hx_toast_20), Toast.LENGTH_SHORT).show();
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
