package com.youmai.hxsdk.db.helper;

import android.content.Context;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.db.dao.HxUsersDao;
import com.youmai.hxsdk.db.dao.PhoneCardsDao;
import com.youmai.hxsdk.db.bean.HxUsers;
import com.youmai.hxsdk.db.bean.PhoneCards;
import com.youmai.hxsdk.db.manager.GreenDbManager;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.StringUtils;

import org.greenrobot.greendao.database.Database;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 作者：create by YW
 * 日期：2017.02.21 20:05
 * 描述：呼信用户信息表
 */

public class HxUsersHelper {

    private static final String SAVE_TIME = "user_save_time";

    private static HxUsersHelper instance;
    private List<HxUsers> mCacheUserList = null;

    public static HxUsersHelper instance() {
        if (instance == null) {
            instance = new HxUsersHelper();
        }
        return instance;
    }

    private HxUsersHelper() {
    }

    public interface IOnUpCompleteListener {
        void onSuccess(HxUsers users);

        void onFail();
    }

    public boolean isEnable(Context context) {
        boolean res = false;

        int unit = BackGroundJob.instance().getContCfgUnit(context);
        int interval = BackGroundJob.instance().getContCfgInterval(context);
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
     * 获取上传所有号码的用户信息
     *
     * @param context
     */
    public void updateAllUser(final Context context) {
        if (!isEnable(context)) {
            return;
        }

        String content = usersContent(context);
        if (StringUtils.isEmpty(content)) {
            return;
        }
        HuxinSdkManager.instance().allHxUserInfo(content, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                LogUtils.e(Constant.SDK_DATA_TAG, "update user db = " + response);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.optString("s").equals("1")) {
                        String dString = json.optString("d");
                        if (null != dString) {
                            JSONObject dJson = new JSONObject(dString);
                            JSONArray itemString = dJson.optJSONArray("items");
                            List<HxUsers> usersList = new ArrayList<>();
                            for (int i = 0; i < itemString.length(); i++) {
                                HxUsers users = new HxUsers();
                                JSONObject obj = (JSONObject) itemString.get(i);
                                users.setUserId(obj.getInt("id"));
                                users.setSex(obj.getInt("sex") + "");
                                users.setNname(obj.getString("nname"));
                                users.setMsisdn(obj.getString("msisdn"));
                                users.setIconUrl(obj.getString("iconUrl"));
                                users.setType(obj.getInt("type") + "");
                                users.setVersion(obj.getString("version"));
                                users.setShowType(obj.getString("showType"));
                                usersList.add(users);
                            }
                            updateUserDb(usersList, context);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void updateUserDb(final List<HxUsers> usersList, final Context context) {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                HxUsersDao hxUsersDao = GreenDbManager.instance(context).getHxUsersDao();
                Database database = hxUsersDao.getDatabase();
                try {
                    database.beginTransaction();
                    for (int i = 0; i < usersList.size(); i++) {
                        HxUsers userBean = usersList.get(i);
                        List<HxUsers> list = hxUsersDao.queryBuilder()
                                .where(HxUsersDao.Properties.Msisdn.eq(userBean.getMsisdn())).list();
                        if (null != list && list.size() > 0) {
                            userBean.setId(list.get(0).getId());
                            hxUsersDao.update(userBean);
                        } else {
                            hxUsersDao.insertOrReplace(userBean);
                        }
                    }
                    database.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    database.endTransaction();
                }
            }
        });
        executorService.shutdown();
    }

    /**
     * 获取上传单个号码的用户信息
     *
     * @param context
     */
    public void updateSingleUser(final Context context, String phone, final IOnUpCompleteListener listener) {
        HuxinSdkManager.instance().allHxUserInfo(phone + "@0", new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                LogUtils.e(Constant.SDK_DATA_TAG, "update single user db = " + response);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.optString("s").equals("1")) {
                        String dString = json.optString("d");
                        if (null != dString) {
                            JSONObject dJson = new JSONObject(dString);
                            JSONArray itemString = dJson.optJSONArray("items");
                            if (null != itemString && itemString.length() > 0) {
                                JSONObject obj = (JSONObject) itemString.get(0);
                                HxUsers users = new HxUsers();
                                users.setUserId(obj.getInt("id"));
                                users.setSex(obj.getInt("sex") + "");
                                users.setNname(obj.getString("nname"));
                                users.setMsisdn(obj.getString("msisdn"));
                                users.setIconUrl(obj.getString("iconUrl"));
                                users.setType(obj.getInt("type") + "");
                                users.setVersion(obj.getString("version"));
                                users.setShowType(obj.getString("showType"));
                                updateSingleUserDb(context, users);
                                if (null != listener) {
                                    listener.onSuccess(users);
                                    clearAllUserCache();
                                }
                            } else {
                                if (null != listener) {
                                    listener.onFail();
                                }
                            }
                        }
                    } else {
                        if (null != listener) {
                            listener.onFail();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (null != listener) {
                        listener.onFail();
                    }
                }
            }

        });
    }

    private void updateSingleUserDb(final Context context, final HxUsers user) {
        HxUsersDao hxUsersDao = GreenDbManager.instance(context).getHxUsersDao();
        List<HxUsers> list = hxUsersDao.queryBuilder()
                .where(HxUsersDao.Properties.Msisdn.eq(user.getMsisdn())).list();
        if (null != list && list.size() > 0) {
            user.setId(list.get(0).getId());
            hxUsersDao.update(user);
        } else {
            hxUsersDao.insert(user);
        }
    }

    /**
     * 单条数据更新
     *
     * @param context
     * @param userBean
     */
    public static void updateUserInfo(Context context, HxUsers userBean) {
        HxUsersDao hxUsersDao = GreenDbManager.instance(context).getHxUsersDao();
        List<HxUsers> list = hxUsersDao.queryRaw("where msisdn = ?", userBean.getMsisdn());
        if (null != list && list.size() > 0) {
            userBean.setId(list.get(0).getId());
            hxUsersDao.update(userBean);
        } else {
            hxUsersDao.insert(userBean);
        }
        HxUsersHelper.instance().clearAllUserCache();
        HxUsersHelper.instance().getAllUserCache(context);
    }

    /**
     * 拼接号码和版本
     *
     * @param context
     * @return content = "4000@1,13788609508@2,13687325901@1"
     */
    public String usersContent(Context context) {

        int limit = BackGroundJob.instance().getContCfgLimits(context);
        StringBuilder sb = new StringBuilder();

        HxUsersDao hxUsersDao = GreenDbManager.instance(context).getHxUsersDao();
        PhoneCardsDao phoneCardsDao = GreenDbManager.instance(context).getPhoneCardsDao();

        List<PhoneCards> phoneList = phoneCardsDao.loadAll();
        int size = phoneList.size() > limit ? limit : phoneList.size();
        for (int i = 0; i < size; i++) {
            String version = "0";
            List<HxUsers> hxUsersList = hxUsersDao.queryRaw("where msisdn = ?", phoneList.get(i).getMsisdn());
            if (null != hxUsersList && hxUsersList.size() > 0) {
                HxUsers user = hxUsersList.get(0);
                version = user.getVersion();
            }
            sb.append(phoneList.get(i).getMsisdn());
            sb.append("@");
            sb.append(version);
            if (size - 1 == i) {
                continue;
            }
            sb.append(",");
        }

        return sb.toString();
    }

    /**
     * 来电&去电 号码入库
     *
     * @param context
     * @param dstPhone
     */
    public static void dsPhoneCard(Context context, String dstPhone) {
        PhoneCardsDao phoneCardsDao = GreenDbManager.instance(context).getPhoneCardsDao();
        List<PhoneCards> phoneList = phoneCardsDao.queryRaw("where msisdn = ?", dstPhone);
        if (null == phoneList || phoneList.size() <= 0) {
            PhoneCards phoneCards = new PhoneCards();
            phoneCards.setMsisdn(dstPhone);
            phoneCardsDao.insertOrReplace(phoneCards);
        }
    }

    /**
     * 通信录 号码入库
     *
     * @param context
     * @param bookList
     */
    public static void addressBook(final Context context, List<PhoneCards> bookList) {
        if (bookList.size() > 300) {
            bookList = bookList.subList(0, 300);
        }
        final List<PhoneCards> finalBookList = bookList;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                PhoneCardsDao phoneCardsDao = GreenDbManager.instance(context).getPhoneCardsDao();
                Database database = phoneCardsDao.getDatabase();
                try {
                    database.beginTransaction();
                    for (int i = 0; i < finalBookList.size(); i++) {
                        PhoneCards phoneCard = finalBookList.get(i);
                        List<PhoneCards> phoneList = phoneCardsDao.queryBuilder()
                                .where(PhoneCardsDao.Properties.Msisdn.eq(phoneCard.getMsisdn())).list();
                        if (null == phoneList || phoneList.size() <= 0) {
                            phoneCardsDao.insertOrReplace(phoneCard);
                            LogUtils.e(Constant.SDK_DATA_TAG, "insert = 1; " + phoneCard);
                        }
                    }
                    database.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    database.endTransaction();
                }
            }
        });
        executor.shutdown();
    }

    /**
     * 根据号码拉取指定用户缓存
     *
     * @param context
     * @param phone
     * @return
     */
    public static HxUsers getHxUser(Context context, String phone) {
        if (StringUtils.isEmpty(phone)) {
            return null;
        }
        HxUsersDao hxUsersDao = GreenDbManager.instance(context).getHxUsersDao();
        List<HxUsers> hxUsersList = hxUsersDao.queryBuilder()
                .where(HxUsersDao.Properties.Msisdn.eq(phone)).list();
        if (ListUtils.isEmpty(hxUsersList)) {
            return null;
        }
        return hxUsersList.get(0);
    }

    /**
     * 拉取DB所有用户缓存
     *
     * @param context
     * @return
     */
    public List<HxUsers> getAllUserCache(Context context) {
        if (null == mCacheUserList) {
            HxUsersDao hxUsersDao = GreenDbManager.instance(context).getHxUsersDao();
            mCacheUserList = hxUsersDao.loadAll();
        }
        return mCacheUserList;
    }

    /**
     * 清理List用户缓存
     */
    private void clearAllUserCache() {
        if (null != mCacheUserList) {
            mCacheUserList.clear();
            mCacheUserList = null;
        }
    }

    /**
     * 获取用户缓存信息
     *
     * @param context
     * @param phone
     * @return
     */
    public HxUsers getSingleUserCache(Context context, String phone) {
        try {
            if (null == mCacheUserList) {
                mCacheUserList = getAllUserCache(context);
                if (null == mCacheUserList) {
                    return null;
                }
            }
            for (int i = 0; i < mCacheUserList.size(); i++) {
                if (mCacheUserList.get(i).getMsisdn().equals(phone)) {
                    return mCacheUserList.get(i);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

}
