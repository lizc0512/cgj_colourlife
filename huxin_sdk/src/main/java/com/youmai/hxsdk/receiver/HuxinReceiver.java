package com.youmai.hxsdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.db.bean.PushMsg;
import com.youmai.hxsdk.db.bean.RemindMsg;
import com.youmai.hxsdk.db.dao.PushMsgDao;
import com.youmai.hxsdk.db.dao.RemindMsgDao;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;
import com.youmai.hxsdk.fragment.LoginFragment;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.service.HuxinService;
import com.youmai.hxsdk.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HuxinReceiver extends BroadcastReceiver {
    private static final String TAG = HuxinReceiver.class.getSimpleName();

    public static final String ACTION_START_SERVICE = "huxin.intent.action.START_SERVER";
    public static final String SHOW_FLOAT_VIEW = "huxin.intent.action.SHOW_FLOAT_VIEW";
    public static final String HIDE_FLOAT_VIEW = "huxin.intent.action.HIDE_FLOAT_VIEW";

    public static final String ACTION_PUSH_MSG = "huxin.intent.action.PUSH_MSG";
    public static final String ACTION_REMIND_MSG = "huxin.intent.action.REMIND_MSG";

    private static final String SEND_SMS_NUMBER1 = "10690895037128969322";  //发送短信验证码号码
    private static final String SEND_SMS_NUMBER2 = "10655025196509693226";  //发送短信验证码号码

    private static final int CODE_LEN = 4;   //短信验证码长度


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            Intent in = new Intent(context, HuxinService.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(in);//启动服务

        } else if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            if (!StringUtils.isEmpty(phoneNumber)) {
                Intent in = new Intent(context, HuxinService.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                in.putExtra("phoneNumber", phoneNumber);
                in.setAction(HuxinService.NEW_OUTGOING_CALL);
                context.startService(in);//启动服务
            }
        } else if (action.equals(ACTION_START_SERVICE)
                || action.equals(Intent.ACTION_BOOT_COMPLETED)
                || action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            HuxinSdkManager.instance().init(context);

            Intent in = new Intent(context, HuxinService.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            in.setAction(HuxinService.BOOT_SERVICE);
            context.startService(in);//启动服务
        } else if (action.equals(SHOW_FLOAT_VIEW)) {
            Intent in = new Intent(context, HuxinService.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            in.setAction(HuxinService.SHOW_FLOAT_VIEW);
            context.startService(in);
        } else if (action.equals(HIDE_FLOAT_VIEW)) {
            Intent in = new Intent(context, HuxinService.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            in.setAction(HuxinService.HIDE_FLOAT_VIEW);
            context.startService(in);
        } else if (action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdu_Objects = (Object[]) bundle.get("pdus");
                if (pdu_Objects != null) {
                    for (Object aObject : pdu_Objects) {
                        SmsMessage currentSMS = getIncomingMessage(aObject, bundle);
                        String senderNo = currentSMS.getDisplayOriginatingAddress();
                        //tring senderNo = currentSMS.getOriginatingAddress();

                        //if (senderNo.equals(SEND_SMS_NUMBER1)
                        //     || senderNo.equals(SEND_SMS_NUMBER2)) {
                        String message = currentSMS.getDisplayMessageBody();

                        String valid = patternCode(message);
                        if (!StringUtils.isEmpty(valid)) {
                            broadValid(context, valid);
                            //abortBroadcast();
                            break;
                        }
                        //}
                    }
                }
            }

        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();

            IPostListener listener = new IPostListener() {
                @Override
                public void httpReqResult(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("s").equals("1")) {
                            // TODO: 2017/6/16 success
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            HuxinSdkManager.instance().appInsert(0, packageName, listener);
        } else if (intent.getAction().equals(ACTION_PUSH_MSG)) {

            /*Intent resultIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            if (resultIntent == null) {
                Log.e(TAG, "handleMessage(): cannot find app: " + context.getPackageName());
            } else {
                resultIntent.setPackage(context.getPackageName());
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
            context.startActivity(resultIntent);*/


            PushMsg pushMsg = intent.getParcelableExtra("push_msg");
            if (pushMsg != null) {
                pushMsg.setIs_click(true);
            }
            PushMsgDao pushMsgDao = GreenDBIMManager.instance(context).getPushMsgDao();
            pushMsgDao.update(pushMsg);


            Intent realIntent = intent.getParcelableExtra("realIntent");
            if (realIntent != null) {
                context.startActivity(realIntent);
            }
        } else if (intent.getAction().equals(ACTION_REMIND_MSG)) {
            RemindMsg remindMsg = intent.getParcelableExtra("remind_msg");
            if (remindMsg != null) {
                remindMsg.setIsRead(true);
            }
            RemindMsgDao remindMsgDao = GreenDBIMManager.instance(context).getRemindMsgDao();
            remindMsgDao.update(remindMsg);

            Intent realIntent = intent.getParcelableExtra("realIntent");
            if (realIntent != null) {
                context.startActivity(realIntent);
            }
        }
    }


    private void broadValid(Context context, String valid) {
        Intent in = new Intent(LoginFragment.SMS_RECEIVED_ACTION);
        in.putExtra(LoginFragment.VALID, valid);
        context.sendBroadcast(in);
    }


    /**
     * 匹配短信中间的验证码
     *
     * @param message
     * @return
     */
    private String patternCode(String message) {
        String res = "";
        /* 正则匹配验证码 */
        String patternCoder = "(?<!\\d)\\d{" + CODE_LEN + "}(?!\\d)";
        if (StringUtils.isEmpty(message)) {
            return res;
        }

        Pattern p = Pattern.compile(patternCoder);
        Matcher matcher = p.matcher(message);
        if (matcher.find()) {
            res = matcher.group();
        }
        return res;
    }

    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        //for Build Tool 22,  Build.VERSION_CODES.M build error
        if (Build.VERSION.SDK_INT >= 23/*Build.VERSION_CODES.M*/) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSMS;
    }

}