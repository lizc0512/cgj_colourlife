package com.youmai.hxsdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.dialog.HxCardDialog;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.im.cache.ContactsDetailsBean;

import java.lang.ref.WeakReference;

/**
 * 失败消息后提示短信发送
 * Created by fylder on 2017/10/25.
 */

public class SmsManager {

    private WeakReference<Activity> activityWeakReference;
    private Context context;
    private Listener listener;

    public SmsManager(Activity activity, Listener listener) {
        this.activityWeakReference = new WeakReference<>(activity);
        this.context = activityWeakReference.get().getBaseContext();
        this.listener = listener;
    }

    /**
     * 无网络情况下，仅文字
     */
    public void showNotNetHuxinUserDialog(CacheMsgBean msgBean, int pos) {
        String desPhone = msgBean.getReceiverPhone();
        String text = "";
        if (msgBean.getJsonBodyObj() instanceof CacheMsgTxt) {
            text = ((CacheMsgTxt) msgBean.getJsonBodyObj()).getMsgTxt();
        }
        String textMsg = context.getString(R.string.hx_send_text);
        textMsg = textMsg + text;
        showNetDialog(desPhone, textMsg, msgBean, pos);
    }

    public void showNotHuxinUserDialog(CacheMsgBean msgBean, int pos) {
        String text = "";
        long msgId = msgBean.getMsgId();
        String desPhone = msgBean.getReceiverPhone();
        String textMsg = "";
        int msgType = msgBean.getMsgType();
        if (msgType == CacheMsgBean.MSG_TYPE_IMG) {
            textMsg = context.getString(R.string.hx_send_picture);
        } else if (msgType == CacheMsgBean.MSG_TYPE_MAP) {
            textMsg = context.getString(R.string.hx_send_location);
        } else if (msgType == CacheMsgBean.MSG_TYPE_VOICE) {
            textMsg = context.getString(R.string.hx_send_audio);
        } else if (msgType == CacheMsgBean.MSG_TYPE_FILE) {
            textMsg = context.getString(R.string.hx_send_big_file);
        } else if (msgType == CacheMsgBean.MSG_TYPE_TXT) {
            textMsg = context.getString(R.string.hx_send_text);
            CacheMsgTxt cacheMsgTxt = new CacheMsgTxt().fromJson(msgBean.getContentJsonBody());
            text = cacheMsgTxt.getMsgTxt();
        } else if (msgType == CacheMsgBean.MSG_TYPE_BIZCARD) {
            textMsg = context.getString(R.string.hx_send_card);
        } else if (msgType == CacheMsgBean.MSG_TYPE_VIDEO) {
            textMsg = context.getString(R.string.hx_send_video);
        }

        if (msgType == CacheMsgBean.MSG_TYPE_BIZCARD) {
            ContactsDetailsBean cardModel = (ContactsDetailsBean) msgBean.getJsonBodyObj();
            String content = '\n' + context.getString(R.string.contact_name) + cardModel.getName();
            if (!ListUtils.isEmpty(cardModel.getPhone())) {
                content += '\n' + context.getString(R.string.contacts_phone) + cardModel.getPhone().get(0).getPhone();
            }
            if (cardModel.getCompany() != null) {
                content += '\n' + context.getString(R.string.contact_company) + cardModel.getCompany();
            }
            if (cardModel.getJob() != null) {
                content += '\n' + context.getString(R.string.contact_job) + cardModel.getJob();
            }

            if (!ListUtils.isEmpty(cardModel.getEmail())) {
                if (cardModel.getEmail().get(0).getEmail() != null) {
                    content += '\n' + context.getString(R.string.contact_email) + cardModel.getEmail().get(0).getEmail();
                }
            }
            if (!ListUtils.isEmpty(cardModel.getDate())) {
                if (cardModel.getDate().get(0).getDate() != null) {
                    content += '\n' + context.getString(R.string.contact_birthday) + cardModel.getDate().get(0).getDate();
                }
            }
            if (!ListUtils.isEmpty(cardModel.getIm())) {
                String qqStr = cardModel.getIm().get(0).getIm();
                if (qqStr != null) {
                    content += '\n' + context.getString(R.string.contact_qq) + qqStr;
                }
            }
            if (!ListUtils.isEmpty(cardModel.getWebsite())) {
                String webStr = cardModel.getWebsite().get(0).getWebsite_num();
                if (webStr != null) {
                    content += '\n' + context.getString(R.string.contact_web) + webStr;
                }
            }
            text = content;
            if (text != null) {
                textMsg = textMsg + text;
            }
        } else if (msgType != CacheMsgBean.MSG_TYPE_TXT) {
            if (AppUtils.isGooglePlay(context)) {
                textMsg = textMsg + AppConfig.SMS_HTTP_HOST_EN + "im/" + CodingUtils.getMsgId(msgId);
            } else {
                textMsg = textMsg + AppConfig.SMS_HTTP_HOST + "im/" + CodingUtils.getMsgId(msgId);
            }
        } else {
            if (text != null) {
                textMsg = textMsg + text;
            }
        }
        showDialog(desPhone, textMsg, msgBean, pos);
    }

    //发送短息提示窗
    private void showDialog(final String desPhone, final String textMsg, final CacheMsgBean msgBean, final int pos) {
        if (activityWeakReference.get() == null || activityWeakReference.get().isFinishing()) {
            return;
        }
        HxCardDialog.Builder builder = new HxCardDialog.Builder(activityWeakReference.get())
                .setOutSide(false)
                .setTitle(R.string.hx_prompt)
                .setContent(R.string.hx_not_user_info)
                .setSubmit(R.string.hx_confirm)
                .setCancel(R.string.hx_cancel)
                .setOnListener(new HxCardDialog.OnClickListener() {
                    @Override
                    public void onSubmitClick(DialogInterface dialog) {
                        SMSUtils.sendSMS(context, desPhone, textMsg);
                        handlerHuxinUserDialog(pos, msgBean, CacheMsgBean.MSG_SEND_FLAG_SEND_BY_SMS);
                    }

                    @Override
                    public void onCancelClick(DialogInterface dialog) {
                        handlerHuxinUserDialog(pos, msgBean, CacheMsgBean.MSG_SEND_FLAG_FAIL);
                    }
                });
        builder.create().show();
    }

    //无网络发送短息提示
    private void showNetDialog(final String desPhone, final String textMsg, final CacheMsgBean msgBean, final int pos) {
        if (activityWeakReference.get() == null || activityWeakReference.get().isFinishing()) {
            return;
        }
        HxCardDialog.Builder builder = new HxCardDialog.Builder(activityWeakReference.get())
                .setOutSide(false)
                .setTitle(R.string.hx_prompt)
                .setContent(R.string.hx_im_msg_sms_tip)
                .setSubmit(R.string.hx_im_msg_sms_send)
                .setCancel(R.string.hx_cancel)
                .setOnListener(new HxCardDialog.OnClickListener() {
                    @Override
                    public void onSubmitClick(DialogInterface dialog) {
                        SMSUtils.sendSMS(context, desPhone, textMsg);
                        handlerHuxinUserDialog(pos, msgBean, CacheMsgBean.MSG_SEND_FLAG_SEND_BY_SMS);
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelClick(DialogInterface dialog) {
                        handlerHuxinUserDialog(pos, msgBean, CacheMsgBean.MSG_SEND_FLAG_FAIL);
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void handlerHuxinUserDialog(int pos, CacheMsgBean msgBean, int sendFlag) {
        msgBean.setSend_flag(sendFlag);
        CacheMsgHelper.instance(context).insertOrUpdate(msgBean);
        if (listener != null) {
            listener.sendNotify(pos);
        }
    }

    public interface Listener {
        //通知更新列表UI
        void sendNotify(int position);
    }
}
