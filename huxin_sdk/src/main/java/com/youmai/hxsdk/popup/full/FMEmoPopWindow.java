package com.youmai.hxsdk.popup.full;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.ProtocolCallBack;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.bean.ChatMsg;
import com.youmai.hxsdk.entity.EmoInfo;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.cache.CacheMsgEmotion;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;
import com.youmai.hxsdk.interfaces.IFileSendListener;
import com.youmai.hxsdk.interfaces.bean.FileBean;
import com.youmai.hxsdk.interfaces.impl.FileSendListenerImpl;
import com.youmai.hxsdk.popup.PushFailPopWindow;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiChat;
import com.youmai.hxsdk.push.http.HttpPushManager;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.CommonUtils;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.LogFile;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.view.chat.emoticon.EmoticonLayout;
import com.youmai.hxsdk.view.chat.emoticon.bean.EmoticonBean;
import com.youmai.hxsdk.view.chat.emoticon.bean.EmoticonSetBean;
import com.youmai.hxsdk.view.chat.emoticon.db.EmoticonDBHelper;
import com.youmai.hxsdk.view.chat.emoticon.utils.EmoticonsKeyboardBuilder;

import java.util.ArrayList;

/**
 * 作者：create by YW
 * 日期：2017.12.19 17:02
 * 描述：
 */
public class FMEmoPopWindow extends PopupWindow {

    private RelativeLayout rl_close;
    private RelativeLayout rl_emo_layout;
    private LinearLayout rl_emo;

    private EmoticonLayout emoticon;

    private Context mContext;
    private String dstPhone; // 对方的手机号码


    public FMEmoPopWindow(Context context, String phone, int width, int height) {
        super(context);
        mContext = context;
        dstPhone = phone;

        initView(context);
        initAttr(width, height);

        setListener(context);
        initEmoticon();
    }

    private void initEmoticon() {

        emoticon.setListener(new EmoticonLayout.OnEmoticonListener() {
            @Override
            public void onEmoticonItemClicked(EmoticonBean bean) {
                final IFileSendListener listener = FileSendListenerImpl.getListener();
                final int userId = HuxinSdkManager.instance().getUserId();

                final String content = bean.getTag();
                EmoInfo emoInfo = new EmoInfo(mContext);
                int emoRes = -1;
                if (bean.getEventType() == EmoticonBean.FACE_TYPE_USERDEF) {
                    emoRes = emoInfo.getEmoRes(content);
                } else if (bean.getEventType() == EmoticonBean.FACE_TYPE_SELF_SETTING) {
                    emoRes = -1;
                }

                final FileBean fileBean = new FileBean()
                        .setUserId(userId)
                        .setDstPhone(dstPhone)
                        .setTextContent(content);

                if (null != listener && !CommonUtils.isNetworkAvailable(mContext)) {
                    listener.onImFail(ChatMsg.MsgType.TEXT.ordinal(), fileBean);
                }

                final CacheMsgBean cacheMsgBean = new CacheMsgBean()
                        .setMsgTime(System.currentTimeMillis())
                        .setSend_flag(-1)
                        .setSenderPhone(HuxinSdkManager.instance().getPhoneNum())
                        .setSenderUserId(userId)
                        .setReceiverPhone(dstPhone)
                        .setMsgType(CacheMsgBean.MSG_TYPE_EMOTION)
                        .setJsonBodyObj(new CacheMsgEmotion().setEmotion(content, emoRes))
                        .setRightUI(true);

                //add to db
                CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                IMMsgManager.getInstance().addCacheMsgBean(cacheMsgBean);

                ReceiveListener callback = new ReceiveListener() {
                    @Override
                    public void OnRec(PduBase pduBase) {
                        //发自己处理
                        final CacheMsgBean newMsgBean;
                        if (dstPhone.equals(HuxinSdkManager.instance().getPhoneNum())) {
                            newMsgBean = HuxinSdkManager.instance().getCacheMsgFromDBById(cacheMsgBean.getId());
                        } else {
                            newMsgBean = cacheMsgBean;
                        }
                        try {
                            YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);

                            LogUtils.e(Constant.SDK_DATA_TAG, "ack = " + ack.getErrerNo() + ";  ack.getIsTargetOnline() = " + ack.getIsTargetOnline());
                            long msgId = ack.getMsgId();
                            newMsgBean.setMsgId(msgId);

                            if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                                if (ack.getIsTargetOnline()) {
                                    Toast.makeText(mContext, mContext.getString(R.string.hx_toast_56), Toast.LENGTH_SHORT).show();
                                    newMsgBean.setSend_flag(0);
                                    CacheMsgHelper.instance(mContext).insertOrUpdate(newMsgBean);

                                    if (null != listener) {
                                        listener.onImSuccess(ChatMsg.MsgType.TEXT.ordinal(), fileBean);
                                    }
                                } else {
                                    // TODO: 2017/1/5 推送消息    发送文本 ok
                                    HttpPushManager.pushMsgForText(mContext, userId, dstPhone, content, new HttpPushManager.PushListener() {
                                        @Override
                                        public void success(String msg) {
                                            LogUtils.w(TAG, msg);
                                            Toast.makeText(mContext, mContext.getString(R.string.hx_toast_56), Toast.LENGTH_SHORT).show();
                                            newMsgBean.setSend_flag(0);
                                            CacheMsgHelper.instance(mContext).insertOrUpdate(newMsgBean);

                                            if (null != listener) {
                                                listener.onImSuccess(ChatMsg.MsgType.TEXT.ordinal(), fileBean);
                                            }
                                        }

                                        @Override
                                        public void fail(String msg) {
                                            Toast.makeText(mContext, mContext.getString(R.string.hx_toast_56), Toast.LENGTH_SHORT).show();
                                            newMsgBean.setSend_flag(0);
                                            LogUtils.e(TAG, "推送消息异常:" + msg);
                                        }
                                    });
                                }
                            } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_NOT_HUXIN_USER) {
                                Toast.makeText(mContext, mContext.getString(R.string.hx_toast_72), Toast.LENGTH_SHORT).show();
                            } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_ERR_SESSIONID) {
                                ProtocolCallBack sCallBack = RespBaseBean.getsCallBack();
                                if (sCallBack != null) {
                                    sCallBack.sessionExpire();
                                }
                            } else {
                                LogFile.inStance().toFile("ErrerNo:" + ack.getErrerNo());
                                if (null != listener) {
                                    listener.onImFail(ChatMsg.MsgType.TEXT.ordinal(), fileBean);
                                }
                            }
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(int errCode) {
                        super.onError(errCode);
                        if (null != listener) {
                            listener.onImFail(ChatMsg.MsgType.TEXT.ordinal(), fileBean);
                        }
                    }
                };

                boolean res = HuxinSdkManager.instance().sendText(userId, dstPhone, content, callback);

                if (FMEmoPopWindow.this.isShowing()) {
                    if (res) {
                        if (!CommonUtils.isNetworkAvailable(mContext)) {
                            AppUtils.showLongToast(mContext, mContext.getString(R.string.hx_toast_57));
                        }
                        FMEmoPopWindow.this.dismiss();
                    }
                }
            }
        });
        EmoticonsKeyboardBuilder builder = getBuilder(mContext);
        emoticon.setContents(builder, 0);
        emoticon.setEmoticonPageBg(0xFFFFFFFF);
    }

    private EmoticonsKeyboardBuilder getBuilder(Context context) {
        if (context == null) {
            throw new RuntimeException(" Context is null, cannot create db helper");
        }
        EmoticonDBHelper emoticonDbHelper = new EmoticonDBHelper(context);
        ArrayList<EmoticonSetBean> mEmoticonSetBeanList;
        if (ListUtils.isEmpty(HuxinSdkManager.instance().getEmoList())) {
            mEmoticonSetBeanList = emoticonDbHelper.queryAllEmoticonEmoIj();
        } else {
            mEmoticonSetBeanList = emoticonDbHelper.queryAllEmoticonSelf();
        }
        emoticonDbHelper.cleanup();

        return new EmoticonsKeyboardBuilder.Builder().setEmoticonSetBeanList(mEmoticonSetBeanList).build();
    }

    private void initAttr(int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
        //this.setFocusable(true);// 设置弹出窗体可点击
        ColorDrawable dw = new ColorDrawable(0x000000);
        this.setBackgroundDrawable(dw);
        this.update();
    }

    private void initView(Context context) {
        rl_emo = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.hx_full_emo_view, null);
        setContentView(rl_emo);

        rl_emo_layout = (RelativeLayout) rl_emo.findViewById(R.id.rl_emo_layout);
        rl_close = (RelativeLayout) rl_emo.findViewById(R.id.rl_close);

        emoticon = (EmoticonLayout) rl_emo.findViewById(R.id.emoticon);

        Animation anim = AnimationUtils.loadAnimation(context, R.anim.hx_pop_alpha_anim_in);
        rl_emo_layout.setAnimation(anim);// 设置弹出窗体动画效果
    }

    private void setListener(final Context context) {

        rl_emo.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });

        // 添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        rl_emo.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int bottom = rl_emo.getBottom();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y > bottom) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        rl_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animOut = AnimationUtils.loadAnimation(context, R.anim.hx_pop_alpha_anim_out);
                rl_emo_layout.setAnimation(animOut);
                dismiss();
            }
        });
    }
}
