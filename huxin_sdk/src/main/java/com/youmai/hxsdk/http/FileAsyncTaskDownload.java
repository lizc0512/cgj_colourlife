package com.youmai.hxsdk.http;


import android.os.AsyncTask;
import android.util.Log;

import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class FileAsyncTaskDownload extends AsyncTask<String, Integer, String> {

    private static final String TAG = FileAsyncTaskDownload.class.getSimpleName();

    private volatile boolean running = true;

    private String md5;//下载文件的md5
    private DownLoadingListener mProgressListener;

    private String downloadpath; //下载文件保存目录
    private String mFileName; //下载完成保存的文件名


    public FileAsyncTaskDownload() {
        this(null, null);
    }

    public FileAsyncTaskDownload(DownLoadingListener listener) {
        this(listener, null);
    }


    public FileAsyncTaskDownload(DownLoadingListener listener, String filename) {
        super();
        mProgressListener = listener;
        mFileName = filename;
    }


    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public void setProgressListener(DownLoadingListener mProgressListener) {
        this.mProgressListener = mProgressListener;
    }

    public void setDownloadpath(String downloadpath) {
        this.downloadpath = downloadpath;
    }

    public void setDownLoadFileName(String mFileName) {
        this.mFileName = mFileName;
    }


    /**
     * 从url解析出fileName
     */
    public static String getFileNameFromUrl(String strUrl) {
        String fileName = null;
        if (strUrl != null) {
            String[] tmpStrArray = strUrl.replace("=", "/").replace("&", "/").split("/");
            for (String item : tmpStrArray) {
                if (item.endsWith(".apk")) {
                    fileName = item;
                    break;
                }
            }

            if (fileName == null) {
                int index = tmpStrArray.length - 1;
                fileName = tmpStrArray[index] + ".apk";
            }
        }

        return fileName;
    }

    @Override
    protected void onCancelled() {
        running = false;
    }

    /**
     * 传入参数第一个,下载 url
     * 传入参数第二个,下载文件md5校验码
     * 传入参数第三个,下载文件保存的文件夹
     */
    @Override
    protected String doInBackground(String... params) {
        while (running) {
            // does the hard work
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;

            String httpUrl = params[0];
            Log.v(TAG, "down http url=" + httpUrl);
            initDownLoadFile(httpUrl);

            File tmpFile;
            File downloadFile = new File(downloadpath, mFileName);

            if (downloadFile.exists()) {
                // 已经存在 不用下载
                Log.v(TAG, "file is exist,don't download");
                return downloadFile.getPath();
            } else {
                String downLoadFileTmpName = mFileName + ".tmp"; // 设置下载的临时文件名
                tmpFile = new File(downloadpath, downLoadFileTmpName);
            }

            try {
                URL url = new URL(httpUrl);
                connection = (HttpURLConnection) url.openConnection();

                long startPosition = tmpFile.length(); // 已下载的文件长度
                String start = "bytes=" + startPosition + "-";
                connection.setRequestProperty("RANGE", start); //支持断点续传
                connection.setRequestProperty("Accept-Encoding", "identity");
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                /*if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }*/

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(tmpFile, true);

                byte data[] = new byte[1024 * 2];
                long total = 0;
                int count;

                if (connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL
                        || connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled()) {
                            input.close();
                            return null;
                        }
                        total += count;

                        int rate = (int) (total * 100 / fileLength);
                        if (rate % 5 == 0) {
                            // publishing the progress....
                            publishProgress((int) total, fileLength);
                        }
                        output.write(data, 0, count);
                    }
                }

            } catch (Exception e) {
                mProgressListener.downloadFail(e.toString());
                if (tmpFile.exists()) {
                    tmpFile.delete();
                }
                return null;
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException e) {
                }

                if (connection != null)
                    connection.disconnect();
            }

            if (!StringUtils.isEmpty(md5)) {
                // 下载文件MD5检测
                if (AppUtils.checkFileMd5(tmpFile, md5)) {
                    Log.v(TAG, "download file md5 check success");
                    tmpFile.renameTo(downloadFile);
                } else {
                    Log.e(TAG, "download file md5 check fail");
                    tmpFile.delete();
                    return null;

                }
            } else {
                Log.v(TAG, "download file md5 check success");
                tmpFile.renameTo(downloadFile);
            }


            return downloadFile.getPath();
        }

        return null;

    }


    @Override
    protected void onPostExecute(String strPath) {
        super.onPostExecute(strPath);
        if (strPath != null) {
            mProgressListener.downloadSuccess(strPath);
        }
    }


    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        int cur = progress[0].intValue();
        int total = progress[1].intValue();

        mProgressListener.onProgress(cur, total);

    }


    /**
     * 生产下载的文件名
     *
     * @param httpUrl
     * @return File
     */
    private void initDownLoadFile(String httpUrl) {
        if (StringUtils.isEmpty(downloadpath)) {
            downloadpath = FileConfig.getFileDownLoadPath();
        }

        if (StringUtils.isEmpty(mFileName)) {
            mFileName = getFileNameFromUrl(httpUrl);
        }
    }

    public static File getFileByUrl(String httpUrl) {
        String path = FileConfig.getFileDownLoadPath();
        String fileName = getFileNameFromUrl(httpUrl);
        return new File(path, fileName);
    }

}
