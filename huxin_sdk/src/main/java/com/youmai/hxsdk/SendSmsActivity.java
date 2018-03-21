package com.youmai.hxsdk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.CodingUtils;
import com.youmai.hxsdk.utils.SMSUtils;


/**
 * Created by Administrator on 2016/7/19.
 * 发送短信界面
 */
public class SendSmsActivity extends SdkBaseActivity implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;

    public static final int SEND_PICTURE = 1;
    public static final int SEND_LOCATION = 2;

    public static final int SEND_AUDIO = 3;
    public static final int SEND_FILE = 4;

    public static final int SEND_TEXT = 5;

    public static final int SEND_CARD = 6;
    public static final int SEND_REMARK = 7;

    public static final int SEND_VIDEO = 8;

    public static final int SEND_JOKES = 9;

    private String desPhone;
    private String textMsg;

    private CacheMsgBean cacheMsgBean;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        //setFinishOnTouchOutside(false);
        setContentView(R.layout.hx_activity_send_sms);

        desPhone = getIntent().getStringExtra("desPhone");
        long msgId = getIntent().getLongExtra("msgId", 0);
        String text = getIntent().getStringExtra("text");
        cacheMsgBean = getIntent().hasExtra("bean") ? (CacheMsgBean) getIntent().getParcelableExtra("bean") : null;

        int type = getIntent().getIntExtra("type", 0);
        if (type == SEND_PICTURE) {
            textMsg = getString(R.string.hx_send_picture);
        } else if (type == SEND_LOCATION) {
            textMsg = getString(R.string.hx_send_location);
        } else if (type == SEND_AUDIO) {
            textMsg = getString(R.string.hx_send_audio);
        } else if (type == SEND_FILE) {
            textMsg = getString(R.string.hx_send_big_file);
        } else if (type == SEND_TEXT) {
            textMsg = getString(R.string.hx_send_text);
        } else if (type == SEND_CARD) {
            textMsg = getString(R.string.hx_send_card);
        } else if (type == SEND_VIDEO) {
            textMsg = getString(R.string.hx_send_video);
        } else if (type == SEND_JOKES) {
            textMsg = getString(R.string.hx_send_text_jokes);
        }

        if (type != SEND_TEXT && type != SEND_JOKES) {
            if (AppUtils.isGooglePlay(mContext)) {
                textMsg = textMsg + AppConfig.SMS_HTTP_HOST_EN + "im/"
                        + CodingUtils.getMsgId(msgId);
            } else {
                textMsg = textMsg + AppConfig.SMS_HTTP_HOST + "im/"
                        + CodingUtils.getMsgId(msgId);
            }
        } else {
            textMsg = textMsg + text;
        }

        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.close_btn).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        findViewById(R.id.rel_content).setOnClickListener(this);
    }

    /**
     * 在该声明周期,检查权限申请情况
     */
/*    @Override
    public void onStart() {
        super.onStart();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }

    }*/

//    /**
//     * 请求权限检查完后回调的结果
//     *
//     * @param requestCode  .
//     * @param permissions  所请求的权限
//     * @param grantResults .
//     */
//    @TargetApi(Build.VERSION_CODES.M)
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    showPermissionDialog(mContext);
//                }
//            }
//            break;
//        }
//    }


    /**
     * 显示登录提示框
     *
     * @param context
     */
    private void showPermissionDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.permission_title))
                .setMessage(context.getString(R.string.send_sms_content));

        builder.setPositiveButton(context.getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                        AppUtils.startAppSettings(mContext);
                        arg0.dismiss();
                        if (cacheMsgBean != null) {
                            cacheMsgBean.setSend_flag(2);
                            //add to db
                            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                            IMMsgManager.getInstance().addCacheMsgBean(cacheMsgBean);
                        }
                    }
                });

        builder.setNegativeButton(context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0,
                                        int arg1) {
                        finish();
                        arg0.dismiss();
                        if (cacheMsgBean != null) {
                            cacheMsgBean.setSend_flag(4);
                            //add to db
                            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                            IMMsgManager.getInstance().addCacheMsgBean(cacheMsgBean);
                        }
                    }
                });
        builder.show();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_cancel
                || id == R.id.close_btn) {
            setResult(RESULT_CANCELED);
            if (cacheMsgBean != null) {
                cacheMsgBean.setSend_flag(4);
                //add to db
                CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                IMMsgManager.getInstance().addCacheMsgBean(cacheMsgBean);
            }
            onBackPressed();
        } else if (id == R.id.btn_confirm) {
            SMSUtils.sendSMS(mContext, desPhone, textMsg);
            setResult(RESULT_OK);
            if (cacheMsgBean != null) {
                cacheMsgBean.setSend_flag(2);
                //add to db
                CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                IMMsgManager.getInstance().addCacheMsgBean(cacheMsgBean);
            }
            onBackPressed();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

}
