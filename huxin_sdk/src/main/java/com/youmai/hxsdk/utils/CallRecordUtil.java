package com.youmai.hxsdk.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CallLog;
import android.text.TextUtils;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.cache.CacheMsgCall;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CallRecordUtil {

    private static final String TAG = "CallRecordUtil";

    public static List<CallLogBean> getCallRecord(Context context) {

        return getCallRecord(context, null);
    }

    public static int getLastCallRecordDuration(Context context, String phoneNumber, boolean isOutgoing) {
        final ContentResolver resolver = context.getContentResolver();
        Cursor c = null;
        String selection = CallLog.Calls.NUMBER + " = " + phoneNumber;
        if (isOutgoing) {
            selection = selection + " and " + CallLog.Calls.TYPE + " = " + CallLog.Calls.OUTGOING_TYPE;
        }
        try {
            c = resolver.query(
                    CallLog.Calls.CONTENT_URI,
                    new String[]{CallLog.Calls.DURATION},
                    selection,
                    null,
                    CallLog.Calls.DEFAULT_SORT_ORDER + " LIMIT 1");
            if (c == null || !c.moveToFirst()) {
                return 0;
            }
            return c.getInt(0);
        } finally {
            if (c != null) c.close();
        }
    }

    public static List<CallLogBean> getCallRecord(Context context, Integer limit) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = android.provider.CallLog.Calls.CONTENT_URI;
        // 查询的列
        String[] projection = {CallLog.Calls.DATE, // 日期
                CallLog.Calls.NUMBER, // 号码
                CallLog.Calls.TYPE, // 类型
                CallLog.Calls.CACHED_NAME, // 名字
                CallLog.Calls._ID, // id
        };
        try {

            Cursor cursor = resolver.query(uri, projection, null, null,
                    "date desc" + (limit == null ? "" : " limit " + limit));

            CallLogBean callLogBean;
            if (cursor != null && cursor.getCount() > 0) {
                List<CallLogBean> callLogs = new ArrayList<>();
                SimpleDateFormat sfd = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.CHINA);
                Date date;
                cursor.moveToFirst(); // 游标移动到第一项
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    date = new Date(cursor.getLong(cursor
                            .getColumnIndex(CallLog.Calls.DATE)));
                    String number = cursor.getString(cursor
                            .getColumnIndex(CallLog.Calls.NUMBER));
                    int type = cursor.getInt(cursor
                            .getColumnIndex(CallLog.Calls.TYPE));
                    String cachedName = cursor.getString(cursor
                            .getColumnIndex(CallLog.Calls.CACHED_NAME));// 缓存的名称与电话号码，如果它的存在
                    int id = cursor.getInt(cursor
                            .getColumnIndex(CallLog.Calls._ID));

                    callLogBean = new CallLogBean();
                    callLogBean.setId(id);
                    callLogBean.setNumber(number);
                    callLogBean.setName(cachedName == null ? "" : cachedName);
                    callLogBean.setType(type);
                    callLogBean.setDate(sfd.format(date));

                    callLogs.add(callLogBean);
                }
                cursor.close();
                return callLogs;
            }
            if (cursor != null)
                cursor.close();
        } catch (Exception e) {
            //Toast.makeText(context, "请打开通话记录权限", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询该电话的通话记录
     * <p>
     * 查询条件：
     * 电话 && 当前时间之前的通话记录（防止超前的电话记录）
     */
    public static CacheMsgCall readCallInfo(Context context, String phone) {
        CacheMsgCall model = new CacheMsgCall();
        Cursor cursor = null;
        boolean isCursor = false;
        try {
            // 查询的列
            String[] projection = {CallLog.Calls.DATE, // 日期
                    CallLog.Calls.NUMBER, // 号码
                    CallLog.Calls.TYPE, // 类型
                    CallLog.Calls.CACHED_NAME, // 名字
                    CallLog.Calls._ID,
                    CallLog.Calls.DURATION

            };
            String t = System.currentTimeMillis() + "";
            String selection = CallLog.Calls.DATE + "<?";
/*            if(phone!=null){
                selection = " and "+CallLog.Calls.NUMBER + "=?";
            }*/
            String[] selectionArgsNoPhone = {t};
            String[] selectionArgs = {t, phone};
            cursor = context.getApplicationContext().getContentResolver().query(
                    CallLog.Calls.CONTENT_URI, projection, selection, /*(phone!=null)?selectionArgs:*/selectionArgsNoPhone, "date desc limit 1");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    //呼叫类型
                    int type;
                    switch (Integer.parseInt(cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE)))) {
                        case CallLog.Calls.INCOMING_TYPE:
                            type = 1;//呼入
                            break;
                        case CallLog.Calls.OUTGOING_TYPE:
                            type = 2;//呼出
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            type = 3;//未接
                            break;
                        default:
                            type = 4;//"挂断";//应该是挂断
                            break;
                    }
                    SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long l = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE)));
                    Date date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE))));
                    //呼叫时间
                    String time = sfd.format(date);
                    //联系人
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME));
                    //通话时间,单位:s
                    long duration = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));
                    cursor.getColumnNames();
                    String s = "type:" + type + "\ttime:" + time + "\tname:" + name + "\tduration:" + duration;
                    LogUtils.w(TAG, s);
                    model.setDuration(duration);
                    model.setType(type);
                    String lastNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                    if (!phone.equals(lastNumber)) {
                        correctCalllog(context, lastNumber, model);
                    }
                    isCursor = true;
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "查询异常:" + e.getMessage());
            model.setDuration(-1);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (!isCursor) {
                model.setDuration(-1);
            }
        }
        return model;
    }

    private static boolean correctCalllog(Context context, String phoneNumber, CacheMsgCall callModel) {
        String callBackPhoneNumberPool = AppUtils.getStringSharedPreferences(context, "voip_config_phone_pool", "");
        if (callBackPhoneNumberPool.contains(phoneNumber)) {
            //1：呼入   2：呼出  3：未接  4：挂断
            callModel.setType(2);
            return true;
        }
        return false;
    }

    /**
     * 读取未接电话数
     */
    public static int readMissCall(Context context) {
        int result = 0;
        Cursor cursor = null;
        try {
            cursor = context.getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.TYPE
            }, " type=? and new=?", new String[]{CallLog.Calls.MISSED_TYPE + "", "1"}, "date desc");
            if (cursor != null) {
                result = cursor.getCount();
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "获取未接电话异常");
            result = -1;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    /**
     * 未接来电设为已读
     */
    public static void clearMissedCalls(final Context context, final String number) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Clear the list of new missed calls from the call log.
                ContentValues values = new ContentValues();
                values.put(CallLog.Calls.NEW, 0);
                values.put(CallLog.Calls.IS_READ, 1);
                StringBuilder where = new StringBuilder();
                where.append(CallLog.Calls.NUMBER);
                where.append(" = ").append(number).append(" AND ");
                where.append(CallLog.Calls.NEW);
                where.append(" = 1 AND ");
                where.append(CallLog.Calls.TYPE);
                where.append(" = ?");
                try {
                    context.getContentResolver().update(CallLog.Calls.CONTENT_URI, values,
                            where.toString(), new String[]{Integer.toString(CallLog.Calls.MISSED_TYPE)});
                } catch (Exception e) {
                    LogUtils.w("contact", "未接来电设为已读异常---ContactsProvider update command failed:" + e.getMessage());
                }
            }
        });
    }

    public static String toTalkTime(Context context, long talkTime) {
        int hours = 0;
        int minute = 0;
        int second = 0;
        int time = (int) (talkTime / 1000);

        if (time < 0) {
            time = 0;
        }

        if (time >= 3600) {
            hours = time / 3600;
            minute = time % 3600 / 60;
            second = time % 3600 % 60;
        } else if (time >= 60) {
            minute = time / 60;
            second = time % 60;
        } else {
            second = time % 60;
        }

        String timeStr;
        if (hours > 0) {
            timeStr = hours + context.getString(R.string.hx_hook_strategy_hour) + minute + context.getString(R.string.hx_hook_strategy_minute) + second + context.getString(R.string.hx_hook_strategy_second);
        } else if (minute > 0 && hours == 0) {
            timeStr = minute + context.getString(R.string.hx_hook_strategy_minute) + second + context.getString(R.string.hx_hook_strategy_second);
        } else {
            timeStr = time + context.getString(R.string.hx_hook_strategy_second);
        }
        return timeStr;
    }


    /**
     * 本次通话记录的保存
     * 1：呼入   2：呼出  3：未接  4：挂断
     *
     * @param context
     * @param isMOCall
     * @param targetNumber
     * @param duration
     */
    public static void saveCallInfo(Context context, String targetNumber, int duration, boolean isMOCall) {
        String selfPhone = HuxinSdkManager.instance().getPhoneNum();
        if(TextUtils.isEmpty(selfPhone)){
            return;
        }

        targetNumber = PhoneNumTypes.formatPhoneNumber(targetNumber);
        
        CacheMsgCall model = CallRecordUtil.readCallInfo(context, targetNumber);
        
        if (model == null) {
            model = new CacheMsgCall();
        }
        int userId = HuxinSdkManager.instance().getUserId();
        int type = model.getType();
        if (type == 0) {
            //没有通话记录权限，获取不到数据,即采用本应用监听结果(mTalkTime属于毫秒级)
            if (isMOCall) {//本机拨打
                model.setType(2);
                if (duration > 0) {
                    model.setDuration(duration);
                }
            } else {
                if (duration > 0) {
                    model.setType(1);
                    model.setDuration(duration);
                } else {
                    model.setType(3);
                }
            }
        }

        long d = model.getDuration();
        if ((d + "").endsWith("000")) {
            model.setDuration(d / 1000);
        }

        type = model.getType();
        
        String senderPhone;
        String receiverPhone;
        if (model.getType() == 1 || model.getType() == 3) {
            senderPhone = targetNumber;
            receiverPhone = selfPhone;
        } else {
            senderPhone = selfPhone;
            receiverPhone = targetNumber;
        }
        
        CacheMsgBean cacheMsgBean = new CacheMsgBean()
                .setMsgTime(System.currentTimeMillis())
                .setSend_flag(0)
                .setSenderPhone(senderPhone)
                .setSenderUserId(userId)
                .setReceiverPhone(receiverPhone)
                .setMsgType(CacheMsgBean.MSG_TYPE_CALL)
                .setJsonBodyObj(new CacheMsgCall().setDuration(model.getDuration()).setType(model.getType()));
        if (type == 1 || type == 3) {
            cacheMsgBean.setRightUI(false);
        } else {
            cacheMsgBean.setRightUI(true);
        }

        //missed call add red point
        if (type == 3) {
            cacheMsgBean.setIs_read(CacheMsgBean.MSG_UNREAD_STATUS);
        }

        if (type == 1 || type == 2 || type == 3 || type == 4) {
            //add to db
            if (isUnread(context, targetNumber)) {
                cacheMsgBean.setIs_read(CacheMsgBean.MSG_UNREAD_STATUS);
            }
            CacheMsgHelper.instance(context).insertOrUpdate(cacheMsgBean);
            IMMsgManager.getInstance().addCacheMsgBean(cacheMsgBean);
        }
    }

    /**
     * 本次通话记录的保存
     * 1：呼入   2：呼出  3：未接  4：挂断
     *
     * @param context
     * @param targetNumber
     * @param duration
     */
    public static void saveOutGoingCallInfo(Context context, String targetNumber, int duration) {
        String senderPhone = HuxinSdkManager.instance().getPhoneNum();
        if(TextUtils.isEmpty(senderPhone)){
            return;
        }

        targetNumber = PhoneNumTypes.formatPhoneNumber(targetNumber);
        
        CacheMsgBean cacheMsgBean = new CacheMsgBean()
                .setMsgTime(System.currentTimeMillis())
                .setSend_flag(0)
                .setSenderPhone(senderPhone)
                .setSenderUserId(HuxinSdkManager.instance().getUserId())
                .setReceiverPhone(targetNumber)
                .setMsgType(CacheMsgBean.MSG_TYPE_CALL)
                .setJsonBodyObj(new CacheMsgCall().setDuration(duration).setType(2));

        //add to db
        if (isUnread(context, targetNumber)) {
            cacheMsgBean.setIs_read(CacheMsgBean.MSG_UNREAD_STATUS);
        }
        CacheMsgHelper.instance(context).insertOrUpdate(cacheMsgBean);
        IMMsgManager.getInstance().addCacheMsgBean(cacheMsgBean);
    }

    /**
     * 是否有未读,为了通话记录不会覆盖未读消息
     *
     * @param context
     * @param phoneNumber
     * @return
     */
    private static boolean isUnread(Context context, String phoneNumber) {
        List<CacheMsgBean> receiveList = CacheMsgHelper.instance(context).toQueryUnreadAscById(
                HuxinSdkManager.instance().getPhoneNum(), phoneNumber);
        return receiveList.size() > 0;
    }
}
