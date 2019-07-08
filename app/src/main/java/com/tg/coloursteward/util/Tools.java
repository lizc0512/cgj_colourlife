package com.tg.coloursteward.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.content.FileProvider;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tg.coloursteward.BuildConfig;
import com.tg.coloursteward.R;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.database.SharedPreferencesTools;
import com.tg.coloursteward.info.PublicAccountInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.log.Logger;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.ResponseData;
import com.youmai.hxsdk.HuxinSdkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {
    private static int TYPE_PDF = 0;
    private static int TYPE_AUDIO = 1;
    private static int TYPE_VIDEO = 2;
    private static int TYPE_IMAGE = 3;
    private static int TYPE_APP = 4;
    private static int TYPE_OTHERS = 9;
    public static final String PREFERENCES_NAME = "park_cache_map";
    private static String[] propertys = {"ro.boot.serialno", "ro.serialno"};
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_TOKEN = "user_token";
    public static final String KEY_GPS_CITY_NAME = "gps_city";
    public static final String KEY_GPS_PROVINCE_NAME = "gps_province";
    public static final String KEY_TIME_STAMP = "time_stamp";
    public static final String KEY_JPUSH_ALIAS = "jpush_alias";
    private static DisplayMetrics metrics;
    private static Animation progressAnim;
    private static String deviceSN = null;
    public static Context mContext;
    public static boolean setAlias = false;
    public static int userHeadSize;
    //缓存常用门禁列表
    public final static String CommonDoorList = "CommonDoorList";
    //缓存开门记录
    public final static String OpenLogList = "OpenLogList";
    //缓存开门记录
    public final static String CZY_CommunityList = "CZY_CommunityList";
    // 保存JPush信息条数
    public final static String Jpush_num = "JPUSH_NUM";
    public final static String SHAREPREFENCENAME = "WeiTown";

    public static DisplayMetrics getDisplayMetrics(Context context) {
        if (metrics == null) {
            metrics = context.getResources().getDisplayMetrics();
        }
        return metrics;
    }


    public static int getStatusBarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    public static void initProgressAnim(Context con) {
        progressAnim = AnimationUtils.loadAnimation(con, R.anim.progess_anim);
    }

    public static Animation getProgressAnim(Context con) {
        if (progressAnim == null) {
            initProgressAnim(con);
        }
        return progressAnim;
    }

    public static String getRootPath(Context con) {
        return con.getFilesDir() + "/park";
    }

    public static void call(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }

    public static String getDateToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        return sf.format(d);
    }

    public static String getSecondToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        return sf.format(d);
    }

    public static String getSimpleDateToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(d);
    }

    public static long getCurrentMillis() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static long dateString2Millis(String date) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d = sf.parse(date);
            return d.getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.PNG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {   //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos  
            image.compress(CompressFormat.PNG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10  
        }
        if (image != null && !image.isRecycled()) {
            image.recycle();
            image = null;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream�?  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;
    }

    public static Bitmap getSmallBitmap(String path) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = mContext.getResources().getDimensionPixelSize(R.dimen.margin_90);
        float ww = mContext.getResources().getDimensionPixelSize(R.dimen.margin_90);
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放   
        if (w >= h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) {
            be = 1;
        }
        newOpts.inSampleSize = be;//设置缩放比例  
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
        bitmap = BitmapFactory.decodeFile(path, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩  
    }

    public static void compressImage(Context con, Bitmap image, String path) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.PNG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        int len = baos.toByteArray().length;
        while (len > 300 * 1024) {  //循环判断如果压缩后图片是否大于300kb,大于继续压缩
            len = baos.toByteArray().length;
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            if (options < 0) {
                options = 0;
            }
            image.compress(CompressFormat.PNG, options, baos);
        }
        if (image != null && !image.isRecycled()) {
            image.recycle();
            image = null;
        }
        FileOutputStream stream = null;
        try {
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            stream = new FileOutputStream(file);
            baos.writeTo(stream);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            baos.reset();
            baos.close();
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void saveImageToPath(Context con, String sPath, String oPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(sPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        Logger.logd("w =" + w + " h = " + h);
        float hh = 500f;
        float ww = 500f;
        int be = 1;
        if (w >= h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) {
            be = 1;
        }
        newOpts.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(sPath, newOpts);
        int newW = newOpts.outWidth;
        int newH = newOpts.outHeight;
        Logger.logd("getByteCount = " + bitmap.getHeight() * bitmap.getRowBytes() + "  newW = " + newW + " newH = " + newH + "   inPreferredConfig = " + newOpts.inPreferredConfig);
        compressImage(con, bitmap, oPath);//压缩好比例大小后再进行质量压缩
    }

    public static void saveUserInfo(Context context) {
        if (UserInfo.uid == null) {
            return;
        }
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("uid", UserInfo.uid);
            jsonObj.put("employeeAccount", UserInfo.employeeAccount);
            jsonObj.put("job_uuid", UserInfo.job_uuid);
            jsonObj.put("sex", UserInfo.sex);
            jsonObj.put("realname", UserInfo.realname);
            jsonObj.put("jobName", UserInfo.jobName);
            jsonObj.put("familyName", UserInfo.familyName);
            jsonObj.put("orgId", UserInfo.orgId);
            jsonObj.put("corp_id", UserInfo.corp_id);
            jsonObj.put("salary_level", UserInfo.salary_level);
            jsonObj.put("is_deleted", UserInfo.is_deleted);
            jsonObj.put("special", UserInfo.special);
            jsonObj.put("mobile", UserInfo.mobile);
            jsonObj.put("czy_id", UserInfo.czy_id);
            jsonObj.put("email", UserInfo.email);

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        SharedPreferencesTools.saveUserInfoJson(context, jsonObj);
    }

    public static boolean loadUserInfo(ResponseData data, String response) {
        if (!TextUtils.isEmpty(response)) {
            JSONObject jsonObj = HttpTools.getContentJSONObject(response);
            if (jsonObj != null) {
                SharedPreferencesTools.saveUserInfoJson(Tools.mContext, jsonObj);
            }
        }
        String uuid = "";
        String employeeAccount = "";
        String realName = "";
        int gender = 0;
        String sex = "";
        String mobile = "";
        String mail = "";
        String corp_id = "";
        int is_deleted = 0;
        int special = 0;
        String salary_level = "";
        int czy_id = 0;
        String jobName = "";
        String job_uuid = "";
        String orgId = "";
        String familyName = "";
        boolean changed = false;
        if (data.getHasString("account_uuid") && data.getHasString("name") && data.getHasString("username")) {//新版本个人信息接口
            uuid = data.getString("account_uuid");//uuid
            if (uuid == null) {
                return false;
            }
            employeeAccount = data.getString("username");//OA
            realName = data.getString("name");//真实姓名
            gender = data.getInt("gender");//性别：1男2女,
            if (gender == 1) {
                sex = "男";
            } else if (gender == 2) {
                sex = "女";
            }
            mobile = data.getString("bind_mobile");//手机号
            mail = data.getString("email");//邮箱号
            corp_id = data.getString("corp_id");//租户ID
            is_deleted = data.getInt("is_deleted");//是否删除
            special = data.getInt("special");//是否特殊员工1是，0否;
            salary_level = data.getString("salary_level");//工资等级
            czy_id = data.getInt("czy_id");//彩之云ID
            jobName = data.getString("job_type");//职位
            job_uuid = data.getString("job_uuid");//职位UUID
            orgId = data.getString("org_uuid");//组织UUID
            familyName = data.getString("org_name");//部门名称
        } else {//旧版本个人接口
            uuid = data.getString("uuid");//uuid
            if (uuid == null) {
                return false;
            }
            employeeAccount = data.getString("employeeAccount");//OA
            realName = data.getString("realname");//真实姓名
            sex = data.getString("sex");//性别：1男2女,
            mobile = data.getString("bind_mobile");//手机号
            mail = data.getString("mail");//邮箱号
            czy_id = data.getInt("czyId");//彩之云ID
            jobName = data.getString("jobName");//职位
            job_uuid = data.getString("jobId");//职位UUID
            orgId = data.getString("orgId");//组织UUID
            familyName = data.getString("familyName");//部门名称
        }
        String avatar = Contants.Html5.HEAD_ICON_URL + "avatar?uid=" + employeeAccount;
        com.youmai.hxsdk.UserInfo userInfo = new com.youmai.hxsdk.UserInfo();
        userInfo.setUuid(uuid);
        userInfo.setAvatar(avatar);
        userInfo.setRealName(realName);
        userInfo.setPhoneNum(mobile);
        userInfo.setSex(sex);
        userInfo.setUserName(employeeAccount);
        userInfo.setOrgId(orgId);
        userInfo.setOrgName(familyName);
        HuxinSdkManager.instance().setUserInfo(userInfo);

        Tools.saveUserName(Tools.mContext, UserInfo.employeeAccount);

        initInfo:
        {
            if (!TextUtils.equals(UserInfo.uid, uuid)) {
                changed = true;
                break initInfo;
            }
            if (!TextUtils.equals(UserInfo.employeeAccount, employeeAccount)) {
                changed = true;
                break initInfo;
            }
            if (!TextUtils.equals(UserInfo.sex, sex)) {
                changed = true;
                break initInfo;
            }
            if (!TextUtils.equals(UserInfo.realname, realName)) {
                changed = true;
                break initInfo;
            }
            if (!TextUtils.equals(UserInfo.job_uuid, job_uuid)) {
                changed = true;
                break initInfo;
            }
            if (!TextUtils.equals(UserInfo.jobName, jobName)) {
                changed = true;
                break initInfo;
            }
            if (!TextUtils.equals(UserInfo.familyName, familyName)) {
                changed = true;
                break initInfo;
            }
            if (!TextUtils.equals(UserInfo.orgId, orgId)) {
                changed = true;
                break initInfo;
            }
            if (!TextUtils.equals(UserInfo.corp_id, corp_id)) {
                changed = true;
                break initInfo;
            }
            if (!TextUtils.equals(UserInfo.salary_level, salary_level)) {
                changed = true;
                break initInfo;
            }
            if (UserInfo.is_deleted != is_deleted) {
                changed = true;
                break initInfo;
            }
            if (UserInfo.special != special) {
                changed = true;
                break initInfo;
            }
            if (!TextUtils.equals(UserInfo.email, mail)) {
                changed = true;
                break initInfo;
            }
            if (!TextUtils.equals(UserInfo.mobile, mobile)) {
                changed = true;
                break initInfo;
            }
            if (UserInfo.czy_id != czy_id) {
                changed = true;
                break initInfo;
            }
        }
        UserInfo.uid = uuid;
        UserInfo.employeeAccount = employeeAccount;
        UserInfo.realname = realName;
        UserInfo.job_uuid = job_uuid;
        UserInfo.sex = sex;
        UserInfo.jobName = jobName;
        UserInfo.familyName = familyName;
        UserInfo.orgId = orgId;
        UserInfo.corp_id = corp_id;
        UserInfo.salary_level = salary_level;
        UserInfo.is_deleted = is_deleted;
        UserInfo.special = special;
        UserInfo.email = mail;
        UserInfo.mobile = mobile;
        UserInfo.czy_id = czy_id;
        return changed;
    }

    public static String getPathByDocumentUri(Context context, Uri contentUri) {
        String filePath = null;
        String wholeID = contentUri.getLastPathSegment();
        String ids[] = wholeID.split(":");
        String id = null;
        if (ids != null && ids.length == 2) {
            id = ids[1];
            String[] column = {MediaColumns.DATA};
            String sel = BaseColumns._ID + "=?";
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
                    sel, new String[]{id}, null);
            if (cursor == null) {
                return null;
            }
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return filePath;
    }

    public static String getPathByGeneralUri(Context context, Uri contentUri) {
        String filePath = null;
        String[] projection = {MediaColumns.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor == null) {
            return null;
        }
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(column_index);
        }
        cursor.close();
        return filePath;
    }

    public static String getPathByUri(Context context, Uri contentUri) {
        //file:///storage/emulated/0/DCIM/Camera/IMG20150524000853.jpg
        //content://com.android.providers.media.documents/document/image:3951
        //content://media/external/images/media/3951
        String filePath = null;
        Logger.logd("contentUri = " + contentUri);
        String scheme = contentUri.getScheme();
        if ("file".equals(scheme)) {
            filePath = contentUri.getPath();
        } else {
            filePath = getPathByGeneralUri(context, contentUri);
            if (filePath == null) {
                filePath = getPathByDocumentUri(context, contentUri);
            }
        }
        return filePath;
    }

    /**
     * 隐藏键盘
     *
     * @param v
     */
    public static void hideKeyboard(final View v) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
            }
        }, 10);
    }

    /**
     * 判断是否是正确手机号
     *
     * @param mobiles
     * @return
     */
    public static boolean checkTelephoneNumber(String mobiles) {
         /*
        移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
	    联通：130、131、132、152、155、156、185、186 
	    电信：133、153、180、189、（1349卫通） 
	    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9 
	    */
        String telRegex = "[1][23456789]\\d{9}";//"[1]"代表第1位为数字1，"[34578]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            return mobiles.matches(telRegex);
        }
    }

    /**
     * 得到全局唯一UUID
     */
    public static String getUUID(Context context) {
        SharedPreferences mShare = getSysShare(context);
        String uuid = null;
        if (mShare != null) {
            uuid = mShare.getString("uuid", "");
        }
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            uuid = uuid.replaceAll("-", "_").replaceAll(":", "_");
            saveSysMap(context, "uuid", uuid);
        }
        return uuid;
    }

    public static SharedPreferences getSysShare(Context con) {
        return con.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static String getSysMapStringValue(Context con, String key) {
        return getSysShare(con).getString(key, "");
    }

    public static boolean getSysMapBooleanValue(Context con, String key, boolean defValue) {
        return getSysShare(con).getBoolean(key, defValue);
    }

    public static void saveUserName(Context con, String userName) {
        getSysShare(con).edit().putString(KEY_USER_NAME, userName).commit();
    }

    public static String getUserName(Context con) {
        return getSysShare(con).getString(KEY_USER_NAME, "");
    }

    public static void saveSysMap(Context con, String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        getSysShare(con).edit().putString(key, value).commit();
    }

    public static void saveDateInfo(Context con, String time) {
        getSysShare(con).edit().
                putString("time", time).commit();
    }

    public static String getDateName(Context con) {
        return getSysShare(con).getString("time", "");
    }

    public static String getElseName(Context con) {
        return getSysShare(con).getString("else", "");
    }

    public static void saveElseInfo(Context con, String time) {
        getSysShare(con).edit().
                putString("else", time).commit();
    }

    public static Boolean setBooleanValue(Context context, String key,
                                          boolean value) {

        if (context == null) {
            return false;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SHAREPREFENCENAME, Activity.MODE_PRIVATE);//允许夸应用访问
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static Boolean getBooleanValue(Context context, String key) {

        if (context == null) {
            return false;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SHAREPREFENCENAME, Activity.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * 保存MainActivity存在状态
     */
    public static Boolean setMainStatus(Context context, boolean isMaincreate) {
        if (context == null) {
            return false;
        }
        return Tools.setBooleanValue(context, "isMaincreate",
                isMaincreate);
    }

    /**
     * 获取MainActivity存在状态
     */
    public static Boolean getMainStatus(Context context) {
        return Tools.getBooleanValue(context, "isMaincreate");
    }

    /**
     * 保存首页消息列表
     *
     * @param con
     * @return
     */
    public static String getHomeList(Context con) {
        return getSysShare(con).getString("home_list", "");
    }

    public static void saveHomeList(Context con, String time) {
        getSysShare(con).edit().
                putString("home_list", time).commit();
    }

    /**
     * 保存收藏联系人列表
     *
     * @param con
     * @return
     */
    public static String getLinkManList(Context con) {
        return getSysShare(con).getString("link_man", "");
    }

    public static void saveLinkManList(Context con, String time) {
        getSysShare(con).edit().
                putString("link_man", time).commit();
    }

    /**
     * 保存CurrentTime
     *
     * @param con
     * @return
     */
    public static long getCurrentTime(Context con) {
        return getSysShare(con).getLong("CurrentTime", -1);
    }

    public static void saveCurrentTime(Context con, long time) {
        getSysShare(con).edit().
                putLong("CurrentTime", time).commit();
    }

    /**
     * 保存有效期（auth2.0）
     *
     * @param con
     * @return
     */
    public static long getExpiresTime2(Context con) {
        return getSysShare(con).getLong("ExpiresTime2", -1);
    }

    public static void saveExpiresTime2(Context con, long time) {
        getSysShare(con).edit().
                putLong("ExpiresTime2", time).commit();
    }

    /**
     * 保存CurrentTime2.0
     *
     * @param con
     * @return
     */
    public static long getCurrentTime2(Context con) {
        return getSysShare(con).getLong("CurrentTime2", -1);
    }

    public static void saveCurrentTime2(Context con, long time) {
        getSysShare(con).edit().
                putLong("CurrentTime2", time).commit();
    }

    /**
     * 保存OpenID值
     *
     * @param con
     * @return
     */
    public static String getOpenID(Context con) {
        return getSysShare(con).getString("OpenID", "");
    }

    public static void saveOpenID(Context con, String time) {
        getSysShare(con).edit().
                putString("OpenID", time).commit();
    }

    /**
     * 保存AccessToken值
     *
     * @param con
     */
    public static String getAccessToken(Context con) {
        return getSysShare(con).getString("AccessToken", "");
    }

    public static void saveAccessToken(Context con, String time) {
        getSysShare(con).edit().
                putString("AccessToken", time).commit();
    }

    /**
     * 保存Access_token值
     *
     * @param con
     */
    public static String getAccess_token(Context con) {
        return getSysShare(con).getString("Access_token", "");
    }

    public static void saveAccess_token(Context con, String time) {
        getSysShare(con).edit().
                putString("Access_token", time).commit();
    }

    public static String getCommonName(Context con) {
        return getSysShare(con).getString("common", "");
    }

    public static void saveCommonInfo(Context con, String time) {
        getSysShare(con).edit().
                putString("common", time).commit();
    }

    /**
     * 保存登录密码(加密前)
     *
     * @param con
     */
    public static void savePassWord(Context con, String pwd) {
        getSysShare(con).edit().
                putString("pwd", pwd).commit();
    }

    public static String getPassWord(Context con) {
        return getSysShare(con).getString("pwd", "");
    }

    /**
     * 保存支付密码(加密前)
     *
     * @param con
     */
    public static void saveCaierPassWord(Context con, String pwd) {
        getSysShare(con).edit().
                putString("caierpwd", pwd).commit();
    }

    public static String getCaierPassWord(Context con) {
        return getSysShare(con).getString("caierpwd", "");
    }

    /**
     * 保存登录密码(加密后)
     *
     * @param con
     */
    public static void savePassWordMD5(Context con, String pwd) {
        getSysShare(con).edit().
                putString("password", pwd).commit();
    }

    public static String getPassWordMD5(Context con) {
        return getSysShare(con).getString("password", "");
    }

    /**
     * 保存orgid
     *
     * @param con
     */
    public static void saveOrgId(Context con, String orgId) {
        getSysShare(con).edit().
                putString("orgId_new", orgId).commit();
    }

    public static String getOrgId(Context con) {
        return getSysShare(con).getString("orgId_new", "");
    }

    /**
     * 保存饭票明细数据
     *
     * @param con
     */
    public static void saveFpDetails(Context con, String FpDetails) {
        getSysShare(con).edit().
                putString("FpDetails", FpDetails).commit();
    }

    public static String getFpDetails(Context con) {
        return getSysShare(con).getString("FpDetails", "");
    }

    /**
     * 保存key
     *
     * @param con
     */
    public static void saveStringValue(Context con, String key,
                                       String value) {
        getSysShare(con).edit().
                putString(key, value).commit();
    }

    public static String getStringValue(Context con, String key) {
        return getSysShare(con).getString(key, "");
    }

    /**
     * 保存token_type
     *
     * @param con
     */
    public static void saveToken_type(Context con, String token_type2) {
        getSysShare(con).edit().
                putString("token_type2", token_type2).commit();
    }

    public static String getToken_type(Context con) {
        return getSysShare(con).getString("token_type2", "");
    }

    /**
     * 保存expires_in
     *
     * @param con
     */
    public static void saveExpires_in(Context con, Long expires_in2) {
        getSysShare(con).edit().
                putLong("expires_in2", expires_in2).commit();
    }

    public static Long getExpires_in(Context con) {
        return getSysShare(con).getLong("expires_in2", 7200);
    }

    /**
     * 保存access_token
     *
     * @param con
     */
    public static void saveAccess_token2(Context con, String access_token) {
        getSysShare(con).edit().
                putString("access_token2", access_token).commit();
    }

    public static String getAccess_token2(Context con) {
        return getSysShare(con).getString("access_token2", "");
    }

    /**
     * 保存refresh_token
     *
     * @param con
     */
    public static void saveRefresh_token2(Context con, String refresh_token) {
        getSysShare(con).edit().
                putString("refresh_token2", refresh_token).commit();
    }

    public static String getRefresh_token2(Context con) {
        return getSysShare(con).getString("refresh_token2", "");
    }

    /**
     * 保存refresh_token获取的用户信息
     *
     * @param con
     */
    public static void savetokenUserInfo(Context con, String tokenUserInfo) {
        getSysShare(con).edit().
                putString("tokenuserinfo", tokenUserInfo).commit();
    }

    public static String gettokenUserInfo(Context con) {
        return getSysShare(con).getString("tokenuserinfo", "");
    }

    /**
     * 保存refresh_token获取时间
     *
     * @param con
     */
    public static void saveRefresh_token2Time(Context con, Long refresh_token) {
        getSysShare(con).edit().
                putLong("refresh_token2time", refresh_token).commit();
    }

    public static Long getRefresh_token2Time(Context con) {
        return getSysShare(con).getLong("refresh_token2time", System.currentTimeMillis());
    }

    /**
     * 保存彩之云用户ID
     *
     * @return 是否保存成功
     */
    public static boolean saveCZYID(Context context,
                                    String ID) {
        getSysShare(context).edit().
                putString("CZYID", ID).commit();
        return true;

    }

    /**
     * 获取彩之云用户ID
     *
     * @param context
     * @return 记住实体
     */
    public static String getCZYID(Context context) {
        return getSysShare(context).getString("CZYID", "");
    }

    /**
     * 保存彩之云小区ID
     *
     * @return 是否保存成功
     */
    public static boolean saveCZY_Community_ID(Context context,
                                               String ID) {
        getSysShare(context).edit().
                putString("CZY_Community_ID", ID).commit();
        return true;

    }

    /**
     * 验证数字
     *
     * @return 如果是符合格式的字符串, 返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean IsIntNumber(String str) {
        String regex = "^\\+?[0-9][0-9]*$";
        return str.matches(regex);
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String getReallyFileName(String url) {
        String filename = "";
        URL myURL;
        HttpURLConnection conn = null;
        if (url == null || url.length() < 1) {
            return null;
        }

        try {
            myURL = new URL(url);
            conn = (HttpURLConnection) myURL.openConnection();
            conn.connect();
            conn.getResponseCode();
            URL absUrl = conn.getURL();// 获得真实Url
            filename = conn.getHeaderField("Content-Disposition");// 通过Content-Disposition获取文件名，这点跟服务器有关，需要灵活变通
            if (filename == null || filename.length() < 1) {
                filename = absUrl.getFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }

        return filename;
    }

    public static Intent getFileIntent(File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            uri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", file);//7.0
        } else {
            uri = Uri.fromFile(file);//7.0以下
        }
        String type = getMIMEType(file);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, type);
        return intent;
    }

    public static String getFileType(File f) {
        String type = "";
        String fName = f.getName();
        /* 取得扩展名 */
        String end = fName
                .substring(fName.lastIndexOf(".") + 1, fName.length())
                .toLowerCase();

        /* 依扩展名的类型决定MimeType */
        if (end.equals("pdf")) {
            type = "application/pdf";//
        } else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio/*";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video/*";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            type = "image/*";
        } else if (end.equals("apk")) {
            /* android.permission.INSTALL_PACKAGES */
            type = "application/vnd.android.package-archive";
        } else {
            // /*如果无法直接打开，就跳出软件列表给用户选择 */
            type = "*/*";
        }
        return type;
    }

    private static String getMIMEType(File file) {

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

    private static String[][] MIME_MapTable = {
            //{后缀名，MIME类型}
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

    /**
     * 删除指定文件夹下的所有文件
     *
     * @param path
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean success = false;
        File file = new File(path);
        if (!file.exists()) {
            return success;
        }
        if (!file.isDirectory()) {
            file.delete();
            success = true;
            return success;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                long lastModeify = temp.lastModified();
                long current = new Date().getTime();
                Log.e("文件创建时间", "current=" + current + "  lastModeify="
                        + lastModeify);
                // 超过3天
                if (current - lastModeify > (1000 * 60 * 60 * 24 * 3)) {
                    temp.delete();
                }
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                success = true;
            }
        }
        return success;
    }

    public static int getFileIntType(File f) {
        String type = getFileType(f);
        if (type.equals("application/pdf")) {
            return TYPE_PDF;
        } else if (type.equals("audio/*")) {
            return TYPE_AUDIO;
        } else if (type.equals("video/*")) {
            return TYPE_VIDEO;
        } else if (type.equals("image/*")) {
            return TYPE_IMAGE;
        } else if (type.equals("application/vnd.android.package-archive")) {
            return TYPE_APP;
        } else if (type.equals("*/*")) {
            return TYPE_OTHERS;
        }
        return 0;
    }

    /**
     * 判断字符串是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9|.]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    // 数字是否两位小数
    public static boolean point2(String str) {
        Pattern pattern = Pattern.compile("[0-9]*(.[0-9]{0,2})?");
        Matcher ispoint2 = pattern.matcher(str);
        return ispoint2.matches();
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dpValue （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpValue, context.getResources().getDisplayMetrics());
    }

    // 手机号码格式
    public static String setPhoneNumberFormat(String s) {
        s = s.replaceAll("[\\s\\t\\r]", "");

        int len = s.length();
        int[] ints = new int[]{3, 8};// 手入空格的位置
        for (int i : ints) {

            if (len > i) {
                s = s.substring(0, i) + " " + s.substring(i, len);
            }
            len = s.length();
        }

        return s;
    }

    public static String macAddress() throws SocketException {
        String address = null;
        // 把当前机器上的访问网络接口的存入 Enumeration集合中
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface netWork = interfaces.nextElement();
            // 如果存在硬件地址并可以使用给定的当前权限访问，则返回该硬件地址（通常是 MAC）。
            byte[] by = netWork.getHardwareAddress();
            if (by == null || by.length == 0) {
                continue;
            }
            StringBuilder builder = new StringBuilder();
            for (byte b : by) {
                builder.append(String.format("%02X:", b));
            }
            if (builder.length() > 0) {
                builder.deleteCharAt(builder.length() - 1);
            }
            String mac = builder.toString();
            Log.d("mac", "interfaceName=" + netWork.getName() + ", mac=" + mac);
            // 从路由器上在线设备的MAC地址列表，可以印证设备Wifi的 name 是 wlan0
            if (netWork.getName().equals("wlan0")) {
                Log.d("mac", " interfaceName =" + netWork.getName() + ", mac=" + mac);
                address = mac;
            }
        }
        return address;
    }
}

