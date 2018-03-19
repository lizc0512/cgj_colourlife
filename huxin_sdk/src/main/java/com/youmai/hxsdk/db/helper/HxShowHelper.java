package com.youmai.hxsdk.db.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.dao.CacheMsgBeanDao;
import com.youmai.hxsdk.db.dao.PhoneCardsDao;
import com.youmai.hxsdk.db.dao.ShowDataDao;
import com.youmai.hxsdk.db.dao.UIDataDao;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;
import com.youmai.hxsdk.db.manager.GreenDbManager;
import com.youmai.hxsdk.entity.HxAllShow;
import com.youmai.hxsdk.db.bean.PhoneCards;
import com.youmai.hxsdk.db.bean.ShowData;
import com.youmai.hxsdk.db.bean.UIData;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.im.IMHelper;
import com.youmai.hxsdk.utils.AbFileUtil;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.StringUtils;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 作者：create by YW
 * 日期：2017.02.21 20:05
 * 描述：呼信用户秀表
 */
public class HxShowHelper {

    private static final String SAVE_TIME = "show_save_time";

    private static HxShowHelper instance;


    public static HxShowHelper instance() {
        if (instance == null) {
            instance = new HxShowHelper();
        }
        return instance;
    }

    private HxShowHelper() {
    }


    public boolean isEnable(Context context) {
        boolean res = false;

        int unit = BackGroundJob.instance().getShowCfgUnit(context);
        int interval = BackGroundJob.instance().getShowCfgInterval(context);
        long timeInterval = BackGroundJob.instance().getTimeDelay(unit, interval);


        long saveTime = AppUtils.getLongSharedPreferences(context, SAVE_TIME, 0);
        long curTime = System.currentTimeMillis();
        if ((curTime - saveTime > timeInterval)) {
            AppUtils.setLongSharedPreferences(context, SAVE_TIME, curTime);
            res = true;
        }
        return res;
    }


    /**
     * 拼接号码和版本
     *
     * @param context
     * @return content = "4000@1,13788609508@2,13687325901@1"
     */
    public String showsContent(Context context) {

        int limit = BackGroundJob.instance().getShowCfgLimits(context);
        StringBuilder sb = new StringBuilder();

        ShowDataDao showDataDao = GreenDbManager.instance(context).getShowDataDao();
        PhoneCardsDao phoneCardsDao = GreenDbManager.instance(context).getPhoneCardsDao();

        List<PhoneCards> phoneList = phoneCardsDao.loadAll();
        int size = phoneList.size() > limit ? limit : phoneList.size();
        for (int i = 0; i < size; i++) {
            String version = "0";
            String phone = phoneList.get(i).getMsisdn();

            List<ShowData> showDataList = showDataDao.queryRaw("where msisdn = ?", phone);
            if (!ListUtils.isEmpty(showDataList)) {
                ShowData showData = showDataList.get(0);
                version = showData.getVersion();
            }
            sb.append(phone);
            sb.append("@");
            sb.append(version);
            if (size - 1 == i) {
                continue;
            }
            sb.append(",");
        }

        return sb.toString();
    }


    public void updateAllShow(final Context context) {
        if (!isEnable(context)) {
            return;
        }

        String content = showsContent(context);
        if (StringUtils.isEmpty(content)) {
            return;
        }

        HuxinSdkManager.instance().allHxShowInfo(content, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                HxAllShow hxAllShow = GsonUtil.parse(response, HxAllShow.class);
                if (null != hxAllShow && hxAllShow.isSucess() && hxAllShow.getD() != null) {

                    List<HxAllShow.DBean.ItemsBean> list = hxAllShow.getD().getItems();
                    List<String> phoneList = hxAllShow.getD().getpItems();
                    if (ListUtils.isEmpty(list)
                            || ListUtils.isEmpty(phoneList)
                            || list.size() != phoneList.size()) {
                        return;
                    }

                    for (int i = 0; i < list.size(); i++) {
                        HxAllShow.DBean.ItemsBean item = list.get(i);
                        String phone = phoneList.get(i);
                        
                        ShowData showData = item.getShow();
                        String ver = item.getVersion();

                        showData.setVersion(ver);
                        showData.setMphone(phone);
                        showData.setMsisdn(phone);

                        ShowDataDao showDataDao = GreenDbManager.instance(context).getShowDataDao();

                        List<ShowData> showDataList = showDataDao.queryRaw("where msisdn = ?", phone);
                        if (!ListUtils.isEmpty(showDataList)) {
                            showData.setId(showDataList.get(0).getId());
                            showDataDao.update(showData);
                        } else {
                            showDataDao.insert(showData);
                        }

                        List<UIData> uiDatas = item.getSections();
                        if (!ListUtils.isEmpty(uiDatas)) {
                            UIDataDao uiDataDao = GreenDbManager.instance(context).getUIDataDao();
                            List<UIData> uiDataList = uiDataDao.queryBuilder().where(UIDataDao.Properties.Msisdn.eq(phone)).list();
                            uiDataDao.deleteInTx(uiDataList);
                            for (UIData uiData : uiDatas) {
                                uiData.setMsisdn(phone);
                                uiData.setMphone(phone);
                                uiDataDao.insert(uiData);
                            }
                        }
                    }
                }
            }
        });
    }

    public void loadUserShowList(final Context context) {

        ShowDataDao showDataDao = GreenDbManager.instance(context).getShowDataDao();
        CacheMsgBeanDao cacheMsgBeanDao = GreenDBIMManager.instance(context).getCacheMsgDao();
        List<CacheMsgBean> cacheMsgList = cacheMsgBeanDao.loadAll();
        if (cacheMsgList == null) {
            return;
        }
        Collections.reverse(cacheMsgList);
        int size = cacheMsgList.size() >= 20 ? 20 : cacheMsgList.size();
        cacheMsgList = cacheMsgList.subList(0, size);
        Log.d("YW", "cacheMsgList: " + cacheMsgList.toString());

        List<String> phoneList = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            String phone = cacheMsgList.get(i).getReceiverPhone();

            boolean contains = phoneList.contains(phone);
            if (contains) {
                continue;
            }
            phoneList.add(phone);
            Log.d("YW", "phoneList: " + phoneList.toString());
        }

        for (int i = 0; i < phoneList.size(); i++) {
            String version = "0";
            String phone = phoneList.get(i);
            List<ShowData> showDataList = showDataDao.queryRaw("where msisdn = ?", phone);
            if (!ListUtils.isEmpty(showDataList)) {
                ShowData showData = showDataList.get(0);
                version = showData.getVersion();
            }
            sb.append(phone);
            sb.append("@");
            sb.append(version);
            if (size - 1 == i) {
                continue;
            }
            sb.append(",");
        }

        HuxinSdkManager.instance().allHxShowInfo(sb.toString(), new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                HxAllShow hxAllShow = GsonUtil.parse(response, HxAllShow.class);
                if (null != hxAllShow && hxAllShow.isSucess() && hxAllShow.getD() != null) {

                    List<HxAllShow.DBean.ItemsBean> list = hxAllShow.getD().getItems();
                    List<String> phoneList = hxAllShow.getD().getpItems();
                    if (ListUtils.isEmpty(list)
                            || ListUtils.isEmpty(phoneList)
                            || list.size() != phoneList.size()) {
                        return;
                    }

                    for (int i = 0; i < list.size(); i++) {
                        HxAllShow.DBean.ItemsBean item = list.get(i);
                        String phone = phoneList.get(i);

                        ShowData showData = item.getShow();
                        String ver = item.getVersion();

                        showData.setVersion(ver);
                        showData.setMphone(phone);
                        showData.setMsisdn(phone);

                        ShowDataDao showDataDao = GreenDbManager.instance(context).getShowDataDao();

                        List<ShowData> showDataList = showDataDao.queryRaw("where msisdn = ?", phone);
                        if (!ListUtils.isEmpty(showDataList)) {
                            showData.setId(showDataList.get(0).getId());
                            showDataDao.update(showData);
                        } else {
                            showDataDao.insert(showData);
                        }

                        List<UIData> uiDatas = item.getSections();
                        if (!ListUtils.isEmpty(uiDatas)) {
                            UIDataDao uiDataDao = GreenDbManager.instance(context).getUIDataDao();
                            List<UIData> uiDataList = uiDataDao.queryBuilder().where(UIDataDao.Properties.Msisdn.eq(phone)).list();
                            uiDataDao.deleteInTx(uiDataList);
                            for (UIData uiData : uiDatas) {
                                uiData.setMsisdn(phone);
                                uiData.setMphone(phone);
                                uiDataDao.insert(uiData);
                            }
                        }
                    }
                }
                loadShowToLocal(context);
            }
        });
    }

    private void loadShowToLocal(final Context context) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                Database database = null;
                try {
                    ShowDataDao showDataDao = GreenDbManager.instance(context).getShowDataDao();
                    database = showDataDao.getDatabase();
                    List<ShowData> showDataList = showDataDao.loadAll();
                    if (showDataList == null) {
                        return;
                    }
                    Collections.reverse(showDataList);
                    int size = showDataList.size() >= 10 ? 10 : showDataList.size();
                    database.beginTransaction();
                    for (int i = 0; i < size; i++) {
                        ShowData showData = showDataList.get(i);
                        if (showData.getFile_type().equals("1") && IMHelper.isWifi(context)) {
                            final String filePath = AppConfig.getImageUrl(context, showData.getFid());
                            final String absolutePath = FileConfig.getVideoDownLoadPath();
                            String hasFile = AbFileUtil.hasFilePath(filePath, absolutePath);

                            if (AbFileUtil.isEmptyString(hasFile)) {
                                AsyncTask<Object, Object, String> task = new AsyncTask<Object, Object, String>() {
                                    String lastDownload = "";
                                    long lastTime = 0;

                                    @Override
                                    protected String doInBackground(Object... params) {
                                        long now = System.currentTimeMillis();
                                        if (now - lastTime < 10 * 1000 && lastDownload.equals(filePath)) {
                                            return null;
                                        }
                                        lastTime = now;
                                        lastDownload = filePath;
                                        return AbFileUtil.downloadFile(filePath, absolutePath);
                                    }

                                    @Override
                                    protected void onPostExecute(String path) {
                                        Log.d("YW", "path: " + path);
                                    }
                                };
                                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        } else if (showData.getFile_type().equals("0")) {
                            //图片show
                        }
                    }
                    database.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (database != null) {
                        database.endTransaction();
                    }
                }
            }
        });
        executor.shutdown();
    }

}
