package com.youmai.hxsdk.db.helper;

import android.content.Context;
import android.os.Build;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.db.dao.AppAuthCfgDao;
import com.youmai.hxsdk.db.dao.AppCfgDao;
import com.youmai.hxsdk.db.dao.ContCfgDao;
import com.youmai.hxsdk.db.dao.ShowCfgDao;
import com.youmai.hxsdk.db.dao.StatsCfgDao;
import com.youmai.hxsdk.db.bean.AppAuthCfg;
import com.youmai.hxsdk.db.bean.AppCfg;
import com.youmai.hxsdk.db.bean.ContCfg;
import com.youmai.hxsdk.db.manager.GreenDbManager;
import com.youmai.hxsdk.entity.HxConfig;
import com.youmai.hxsdk.db.bean.ShowCfg;
import com.youmai.hxsdk.db.bean.StatsCfg;
import com.youmai.hxsdk.entity.RespFloatView;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.utils.TimeUtils;

import java.util.List;

/**
 * Created by colin on 2017/1/16.
 */
public class BackGroundJob {

    private static final long TIME_INTERVAL = 1000 * 60 * 60 * 12;

    private static final String SAVE_TIME = "bg_save_time";
    private static final String STATS_VER = "stats_ver";
    private static final String ALLOW_CODE = "allow_code";

    private static final String HUAWEI = "huawei";
    private static final String XIAOMI = "xiaomi";
    private static final String MEIZU = "meizu";
    private static final String OPPO = "oppo";
    private static final String VIVO = "vivo";
    private String mBrandProp = "";

    private static final String DEFAULT_VER = "0";  //默认版本
    private static final int DEFAULT_LIMIT = 50;    //默认最大条数
    private static final int DEFAULT_UNIT = 2;    //默认间隔单位
    private static final int DEFAULT_INTERVAL = 1;    //默认间隔

    private static BackGroundJob instance;

    private ShowCfg mShowCfg;
    private ContCfg mContCfg;
    private List<StatsCfg> mStatsList;
    private List<AppCfg> mSppCfgList;
    private List<AppAuthCfg> mAppAuthCfgList;

    public static BackGroundJob instance() {
        if (instance == null) {
            instance = new BackGroundJob();
        }
        return instance;
    }

    private BackGroundJob() {
    }


    public boolean isEnable(Context context) {
        boolean res = false;
        long saveTime = AppUtils.getLongSharedPreferences(context, SAVE_TIME, 0);
        long curTime = System.currentTimeMillis();
        if ((curTime - saveTime > TIME_INTERVAL)) {
            AppUtils.setLongSharedPreferences(context, SAVE_TIME, curTime);
            res = true;
        }
        return res;
    }

    public long getTimeDelay(int unit, int interval) {
        long res = 0;
        switch (unit) {
            case 0:
                res = interval * 1000;
                break;
            case 1:
                res = interval * 1000 * 60;
                break;
            case 2:
                res = interval * 1000 * 60 * 60;
                break;
            case 3:
                res = interval * 1000 * 60 * 60 * 24;
                break;
        }
        return res;

    }


    /**
     * 请求服务器配置信息
     *
     * @param context
     */
    public void reqConfig(final Context context) {
        if (!isEnable(context)) {
            return;
        }

        String brand = Build.MANUFACTURER.toLowerCase();
        String model = Build.MODEL.replace(" ", "_");
        int sdkInt = Build.VERSION.SDK_INT;

        String appCfg = getAppCfgVer(context);
        String showCfg = getShowCfgVer(context);
        String contCfg = getContCfgVer(context);
        String statsCfg = getStatsCfgVer(context);
        String appAuthCfg = getAppAuthCfgVer(context);

        IGetListener listener = new IGetListener() {
            @Override
            public void httpReqResult(String response) {

                HxConfig config = GsonUtil.parse(response, HxConfig.class);
                if (config != null && config.isSuccess() && config.getD() != null) {

                    AppUtils.setIntSharedPreferences(context, ALLOW_CODE, config.getD().getAllow());

                    AppCfg appCfg = config.getD().getAppCfgBean();
                    if (appCfg != null) {
                        AppCfgDao appCfgDao = GreenDbManager.instance(context).getAppCfgDao();
                        appCfgDao.deleteAll();
                        appCfgDao.insertOrReplace(appCfg);
                    }

                    ShowCfg showCfg = config.getD().getShowCfg();
                    if (showCfg != null) {
                        ShowCfgDao showCfgDao = GreenDbManager.instance(context).getShowCfgDao();
                        showCfgDao.deleteAll();
                        showCfgDao.insertOrReplace(showCfg);
                    }

                    ContCfg contCfg = config.getD().getContCfg();
                    if (contCfg != null) {
                        ContCfgDao contCfgDao = GreenDbManager.instance(context).getContCfgDao();
                        contCfgDao.deleteAll();
                        contCfgDao.insertOrReplace(contCfg);
                    }

                    if (config.getD().getStatsCfg() != null) {
                        String ver = config.getD().getStatsCfg().getVersion();
                        if (!StringUtils.isEmpty(ver)) {
                            AppUtils.setStringSharedPreferences(context, STATS_VER, ver);
                        }

                        List<StatsCfg> statsList = config.getD().getStatsCfg().getStatsBeans();
                        if (!ListUtils.isEmpty(statsList)) {
                            StatsCfgDao statsCfgDao = GreenDbManager.instance(context).getStatsCfgDao();
                            statsCfgDao.deleteAll();
                            statsCfgDao.insertOrReplaceInTx(statsList);
                        }
                    }

                    if (config.getD().getAppAuthCfg() != null) {
                        String v = config.getD().getAppAuthCfg().getVersion();
                        List<AppAuthCfg> authList = config.getD().getAppAuthCfg().getAuthBeans();
                        if (!ListUtils.isEmpty(authList)) {
                            AppAuthCfgDao appAuthCfgDao = GreenDbManager.instance(context).getAppAuthCfgDao();
                            appAuthCfgDao.deleteAll();
                            for (AppAuthCfg item : authList) {
                                if (!StringUtils.isEmpty(v)) {
                                    item.setVersion(v);
                                }
                            }
                            appAuthCfgDao.insertOrReplaceInTx(authList);
                        }
                    }
                }
            }
        };

        HuxinSdkManager.instance().reqSdkConfig(brand, model, mBrandProp, sdkInt, appAuthCfg, appCfg, showCfg, contCfg, statsCfg, listener);
    }


    /**
     * 获取弹屏配置开关 和 协议公共参数
     *
     * @param context
     */
    public void reqFloatViewConfig(final Context context, boolean isOnce) {
        /*if (!isEnable(context)) {
            return;
        }*/
        final String key = TimeUtils.getDate(System.currentTimeMillis()) + "float_view";

        if (!isOnce) {
            if (AppUtils.getBooleanSharedPreferences(context, key, false)) {
                return;
            }
        }


        HuxinSdkManager.instance().reqFloatViewConfig(context, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespFloatView bean = GsonUtil.parse(response, RespFloatView.class);
                if (bean != null && bean.isSucess()) {
                    int isOff = bean.getD().getIsOff();  //0:否，不关闭； 1:是，已关闭
                    String ps = bean.getD().getPs();
                    if (isOff == 1) {
                        //HuxinSdkManager.instance().setCallFloatView(false);  //关闭通话中弹屏
                    } else {
                        HuxinSdkManager.instance().setCallFloatView(true);  //打开通话中弹屏
                    }
                    AppUtils.setBooleanSharedPreferences(context, key, true);
                    AppUtils.setStringSharedPreferences(context, "common_param", ps);
                    HuxinSdkManager.instance().setCommonParam(ps);
                }
            }
        });
    }


    public boolean isSupportMTBefore(Context context) {
        int code = AppUtils.getIntSharedPreferences(context, ALLOW_CODE, 0);
        return (0x0001 & code) != 0;
    }


    /**
     * 获取当前版本升级配置版本
     *
     * @param context
     * @return
     */
    public String getAppCfgVer(Context context) {
        String res = DEFAULT_VER;

        if (ListUtils.isEmpty(mSppCfgList)) {
            AppCfgDao appCfgDao = GreenDbManager.instance(context).getAppCfgDao();
            mSppCfgList = appCfgDao.loadAll();
        }

        if (mSppCfgList != null && mSppCfgList.size() == 1) {
            res = mSppCfgList.get(0).getVersion();
        }

        return res;
    }


    /**
     * 获取当前show配置版本
     *
     * @param context
     * @return
     */
    public String getShowCfgVer(Context context) {
        String res = DEFAULT_VER;

        if (mShowCfg == null) {
            ShowCfgDao showCfgDao = GreenDbManager.instance(context).getShowCfgDao();
            List<ShowCfg> showCfgList = showCfgDao.loadAll();
            if (showCfgList != null && showCfgList.size() == 1) {
                mShowCfg = showCfgList.get(0);
            }
        }

        if (mShowCfg != null) {
            res = mShowCfg.getVersion();
        }
        return res;
    }

    /**
     * 获取当前show配置间隔
     *
     * @param context
     * @return
     */
    public int getShowCfgInterval(Context context) {
        int res = DEFAULT_INTERVAL;

        if (mShowCfg == null) {
            ShowCfgDao showCfgDao = GreenDbManager.instance(context).getShowCfgDao();
            List<ShowCfg> showCfgList = showCfgDao.loadAll();
            if (showCfgList != null && showCfgList.size() == 1) {
                mShowCfg = showCfgList.get(0);
            }
        }

        if (mShowCfg != null) {
            res = mShowCfg.getInterval();
        }

        return res;
    }


    /**
     * 获取当前show配置单位
     *
     * @param context
     * @return
     */
    public int getShowCfgUnit(Context context) {
        int res = DEFAULT_UNIT;

        if (mShowCfg == null) {
            ShowCfgDao showCfgDao = GreenDbManager.instance(context).getShowCfgDao();
            List<ShowCfg> showCfgList = showCfgDao.loadAll();
            if (showCfgList != null && showCfgList.size() == 1) {
                mShowCfg = showCfgList.get(0);
            }
        }


        if (mShowCfg != null) {
            res = mShowCfg.getUnit();
        }

        return res;
    }


    /**
     * 获取当前show配置最大条数
     *
     * @param context
     * @return
     */
    public int getShowCfgLimits(Context context) {
        int res = DEFAULT_LIMIT;

        if (mShowCfg == null) {
            ShowCfgDao showCfgDao = GreenDbManager.instance(context).getShowCfgDao();
            List<ShowCfg> showCfgList = showCfgDao.loadAll();
            if (showCfgList != null && showCfgList.size() == 1) {
                mShowCfg = showCfgList.get(0);
            }
        }

        if (mShowCfg != null) {
            res = mShowCfg.getLimits();
        }

        return res;
    }


    /**
     * 获取当前联系人配置版本
     *
     * @param context
     * @return
     */
    public String getContCfgVer(Context context) {
        String res = DEFAULT_VER;

        if (mContCfg == null) {
            ContCfgDao contCfgDao = GreenDbManager.instance(context).getContCfgDao();
            List<ContCfg> contCfgList = contCfgDao.loadAll();
            if (contCfgList != null && contCfgList.size() == 1) {
                mContCfg = contCfgList.get(0);
            }
        }

        if (mContCfg != null) {
            res = mContCfg.getVersion();
        }

        return res;
    }


    /**
     * 获取当前联系人配置间隔
     *
     * @param context
     * @return
     */
    public int getContCfgInterval(Context context) {
        int res = DEFAULT_INTERVAL;

        if (mContCfg == null) {
            ContCfgDao contCfgDao = GreenDbManager.instance(context).getContCfgDao();
            List<ContCfg> contCfgList = contCfgDao.loadAll();
            if (contCfgList != null && contCfgList.size() == 1) {
                mContCfg = contCfgList.get(0);
            }
        }

        if (mContCfg != null) {
            res = mContCfg.getInterval();
        }

        return res;
    }


    /**
     * 获取当前联系人配置单位
     *
     * @param context
     * @return
     */
    public int getContCfgUnit(Context context) {
        int res = DEFAULT_UNIT;

        if (mContCfg == null) {
            ContCfgDao contCfgDao = GreenDbManager.instance(context).getContCfgDao();
            List<ContCfg> contCfgList = contCfgDao.loadAll();
            if (contCfgList != null && contCfgList.size() == 1) {
                mContCfg = contCfgList.get(0);
            }
        }

        if (mContCfg != null) {
            res = mContCfg.getUnit();
        }

        return res;
    }


    /**
     * 获取当前联系人配置最大条数
     *
     * @param context
     * @return
     */
    public int getContCfgLimits(Context context) {
        int res = DEFAULT_LIMIT;

        if (mContCfg == null) {
            ContCfgDao contCfgDao = GreenDbManager.instance(context).getContCfgDao();
            List<ContCfg> contCfgList = contCfgDao.loadAll();
            if (contCfgList != null && contCfgList.size() == 1) {
                mContCfg = contCfgList.get(0);
            }
        }

        if (mContCfg != null) {
            res = mContCfg.getLimits();
        }

        return res;
    }


    /**
     * 获取当前统计汇报配置版本
     *
     * @param context
     * @return
     */
    public String getStatsCfgVer(Context context) {
        return AppUtils.getStringSharedPreferences(context, STATS_VER, "0");
    }

    /**
     * 获取当前权限升级配置版本
     *
     * @param context
     * @return
     */
    public String getAppAuthCfgVer(Context context) {
        String res = DEFAULT_VER;

        if (ListUtils.isEmpty(mAppAuthCfgList)) {
            AppAuthCfgDao appAuthCfgDao = GreenDbManager.instance(context).getAppAuthCfgDao();
            mAppAuthCfgList = appAuthCfgDao.loadAll();
        }

        if (mAppAuthCfgList != null && mAppAuthCfgList.size() >= 1) {
            res = mAppAuthCfgList.get(0).getVersion();
        }

        return res;
    }

    /**
     * 获取统计汇报配置
     *
     * @param context
     * @return
     */
    public List<StatsCfg> getStatsCfg(Context context) {
        if (ListUtils.isEmpty(mStatsList)) {
            StatsCfgDao statsCfgDao = GreenDbManager.instance(context).getStatsCfgDao();
            mStatsList = statsCfgDao.loadAll();
        }

        if (ListUtils.isEmpty(mStatsList)) {
            AppUtils.setStringSharedPreferences(context, STATS_VER, "0");
        }

        return mStatsList;
    }


}