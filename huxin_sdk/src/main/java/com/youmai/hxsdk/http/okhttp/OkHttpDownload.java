package com.youmai.hxsdk.http.okhttp;

import android.support.annotation.NonNull;
import android.util.Log;

import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.http.DownLoadingListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * okhttp的文件同步下载
 * <p>
 * Created by fylder on 2017/10/23.
 */

public class OkHttpDownload {

    private final String DOWNLOAD_PATH = FileConfig.getVideoDownLoadPath();


    public String downloadFile(final String url, final DownloadProListener listener) {
        String downloadFilePath = null;
        OkHttpClient okHttpClient = new OkHttpClient()
                .newBuilder()
                .build();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            InputStream is = null;
            byte[] buf = new byte[2048];
            int len = 0;
            FileOutputStream fos = null;
            // 储存下载文件的目录
            String savePath = isExistDir(DOWNLOAD_PATH);
            try {
                is = response.body().byteStream();
                long total = response.body().contentLength();
                File file = new File(savePath, getFileName());
                fos = new FileOutputStream(file);
                long sum = 0;
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                    sum += len;
                    int progress = (int) (sum * 1.0f / total * 100);
                    // 下载中
                    listener.onProgress(progress);
                }
                fos.flush();
                // 下载完成
                downloadFilePath = file.getAbsolutePath();
            } catch (Exception e) {
                Log.w("download", e.getMessage());
            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                    Log.w("download", e.getMessage());
                }
                try {
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {
                    Log.w("download", e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.w("download", e.getMessage());
        }
        return downloadFilePath;
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException 判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }


    @NonNull
    private String getFileName() {
        return "video_" + System.currentTimeMillis() + ".jv";
    }


}
