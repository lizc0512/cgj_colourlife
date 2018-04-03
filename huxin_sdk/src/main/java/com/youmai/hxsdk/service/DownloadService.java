package com.youmai.hxsdk.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseArray;

import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.http.okhttp.DownloadProListener;
import com.youmai.hxsdk.http.okhttp.OkHttpDownload;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;
import com.youmai.hxsdk.im.cache.CacheMsgVideo;
import com.youmai.hxsdk.service.download.DownloadStatus;
import com.youmai.hxsdk.service.download.bean.FileQueue;
import com.youmai.hxsdk.service.download.bean.DownloadServiceData;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 视频下载
 * Queue基于队列的下载模式
 * <p>
 * 添加下载文件任务：
 * startService()开始本服务时传递FileQueue
 * <p>
 * ui：
 * 通过接收本地广播action：download.service.video
 * 得到FileQueue
 * <p>
 * Created by fylder on 2017/4/27.
 */
@SuppressLint("Registered")
public class DownloadService extends Service {

    private static final String TAG = DownloadService.class.getName();

    private static final int RUNNING_MAX = 1;//最大同时下载数
    private static int running = 0;
    private Queue<FileQueue> fileQueues = new LinkedList<>();//存放下载的队列
    private static SparseArray<FileQueue> datas = new SparseArray<>();//用于保存下载队列的进度
    private boolean startStat = true;
    private boolean hasRun = false;

    Executor executor = new ThreadPoolExecutor(5, 5, 10,
            TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(5));

    private static Context appContext;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        runThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.hasExtra("dataList")) {
                DownloadServiceData data = intent.getParcelableExtra("dataList");
                List<FileQueue> fileQueues = data.getFileQueues();
                for (FileQueue f : fileQueues) {
                    int index = datas.size() + 1;
                    datas.put(index, f);
                    sendFile(f);//下载文件放入队列
                }
            }
            if (intent.hasExtra("data")) {
                FileQueue fileQueue = intent.getParcelableExtra("data");
                int index = datas.size() + 1;
                datas.put(index, fileQueue);
                sendFile(fileQueue);//下载文件放入队列
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 发送文件
     *
     * @param fileQueue
     */
    public void sendFile(FileQueue fileQueue) {
        if (!fileQueues.contains(fileQueue)) {
            fileQueues.offer(fileQueue);
        }
    }

    private Thread runThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (startStat) {
                try {
                    if (fileQueues.size() > 0) {
                        Thread.sleep(100);
                    } else {
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runDownload();
            }
            stopSelf();
            Log.w(TAG, "stopSelf");
            DownloadStatus response = new DownloadStatus();
            response.setStatus(2);
//        HermesEventBus.getDefault().post(response);
        }
    });

    /**
     * 控制下载
     */
    private void runDownload() {
        if (fileQueues.size() > 0) {
            if (running < RUNNING_MAX) {
                FileQueue fileQueue = fileQueues.poll();
                if (fileQueue != null) {
                    download(fileQueue);
                }
                hasRun = true;
            }
        } else if (hasRun && running == 0) {
            startStat = false;
        }
    }

    /**
     * 下载文件
     */

    private void download(FileQueue fileQueue) {
        DownloadStatus response = new DownloadStatus();
        response.setStatus(1);
        new DownloadTask(fileQueue).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);//系统异步多线程
//        new DownloadTask(fileQueue).executeOnExecutor(executor);//自定义异步多线程
    }

    /**
     * 下载任务
     */
    private static class DownloadTask extends AsyncTask<Void, Void, String> {

        FileQueue fileQueue;
        int key = 0;//当前下载文件的位置
        long updateTime = 0;

        DownloadTask(FileQueue fileQueue) {
            this.fileQueue = fileQueue;
            for (int i = 0; i < datas.size(); i++) {
                if (datas.valueAt(i).getMid().longValue() == fileQueue.getMid().longValue()) {
                    key = datas.keyAt(i);
                    break;
                }
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            running++;
            //下载文件业务
            updateTime = System.currentTimeMillis();
            //更新开始UI下载进度
            datas.get(key).setPro(1);
            updateUI(datas.get(key));

            OkHttpDownload okhttpDownload = new OkHttpDownload();
            String path = okhttpDownload.downloadFile(fileQueue.getPath(), new DownloadProListener() {
                @Override
                public void onProgress(int p) {
                    if (p > datas.get(key).getPro() && p < 100) {
                        long nowTime = System.currentTimeMillis();
                        //超过500ms再更新
                        if (nowTime - updateTime > 500) {
                            updateTime = nowTime;
                            //更新UI下载进度
                            Log.w(TAG, "进度:" + p);
                            datas.get(key).setPro(p);
                            updateUI(datas.get(key));
                        }
                    }
                }
            });
            return path;
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            if (path == null) {
                //下载失败
                Log.w(TAG, "下载失败");
                datas.get(key).setPro(-1);
                updateUI(datas.get(key));
            } else {
                //下载成功
                datas.get(key).setPro(100);
                updateUI(datas.get(key));
                downFileFinish(datas.get(key), path);
            }
            running--;
        }
    }

    /**
     * 完成下载一个文件
     *
     * @param filePath 下载得到的本地文件
     */
    static void downFileFinish(FileQueue fileQueue, String filePath) {
        //保存文件到数据库
        Long id = fileQueue.getMid();
        CacheMsgBean cacheMsgBean = CacheMsgHelper.instance(appContext).queryByID(id);
        if (cacheMsgBean.getJsonBodyObj() instanceof CacheMsgVideo) {
            CacheMsgVideo cacheMsgVideo = (CacheMsgVideo) cacheMsgBean.getJsonBodyObj();
            cacheMsgVideo.setVideoPath(filePath);
            cacheMsgVideo.setProgress(100);
            cacheMsgBean.setJsonBodyObj(cacheMsgVideo);
            CacheMsgHelper.instance(appContext).insertOrUpdate(cacheMsgBean);//保存数据库
        }
    }

    /**
     * 发送本地广播通知更新ui
     *
     * @param fileQueue
     */
    static void updateUI(FileQueue fileQueue) {
        Intent intent = new Intent("download.service.video");
        intent.putExtra("data", fileQueue);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(appContext);
        localBroadcastManager.sendBroadcast(intent);
    }

//    //通知获取数据
////    @Subscribe(threadMode = ThreadMode.MAIN)
//    protected void getServiceData(DownloadStatus data) {
//        if (data.getStatus() == 100) {
//
//            List<FileQueue> fileQueues = new ArrayList<>();
//            for (int i = 0; i < datas.size(); i++) {
//                fileQueues.add(datas.valueAt(i));
//            }
//
//            DownloadStatus response = new DownloadStatus();
//            response.setStatus(200);
//            response.setDatas(fileQueues);
////            HermesEventBus.getDefault().post(response);//返回数据
//        }
//    }
}
