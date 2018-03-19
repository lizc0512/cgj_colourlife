package com.youmai.hxsdk.popup.full;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.ProtocolCallBack;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.bean.ChatMsg;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.entity.EmoInfo;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.push.http.HttpPushManager;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.cache.CacheMsgEmotion;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;
import com.youmai.hxsdk.interfaces.IFileSendListener;
import com.youmai.hxsdk.interfaces.bean.FileBean;
import com.youmai.hxsdk.interfaces.impl.FileSendListenerImpl;
import com.youmai.hxsdk.popup.PushFailPopWindow;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiChat;
import com.youmai.hxsdk.recyclerview.EmoIndicatorView;
import com.youmai.hxsdk.recyclerview.PageRecyclerView;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.CommonUtils;
import com.youmai.hxsdk.utils.LogFile;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.view.full.FloatViewUtil;

import java.util.List;

/**
 * @author yw
 * @data 2016.6.27
 */
public class FullEmoPopWindow extends PopupWindow {

    private static final int columns = 2;

    private RelativeLayout rl_close;
    private RelativeLayout rl_emo_layout;
    private LinearLayout rl_emo;
    private PageRecyclerView mRecyclerView;
    private EmoIndicatorView mCustomIndicator;
    private List<EmoInfo.EmoItem> dataList = null;
    private Context mContext;
    private String dstPhone; // 对方的手机号码


    public FullEmoPopWindow(Context context, String phone, int width, int height) {
        super(context);
        mContext = context;
        dstPhone = phone;

        initView(context);
        initAttr(width, height);
        setAdapter();
        setListener(context);

    }

    private void initAttr(int width, int height) {
        this.setWidth(width);//ViewGroup.LayoutParams.WRAP_CONTENT
        this.setHeight(height);
        //this.setFocusable(true);// 设置弹出窗体可点击
        ColorDrawable dw = new ColorDrawable(0x000000);
        this.setBackgroundDrawable(dw);
        this.update();
    }

    private void initView(Context context) {
        rl_emo = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.hx_emo_view, null);
        setContentView(rl_emo);

        rl_emo_layout = (RelativeLayout) rl_emo.findViewById(R.id.rl_emo_layout);
        rl_close = (RelativeLayout) rl_emo.findViewById(R.id.rl_close);

        mRecyclerView = (PageRecyclerView) rl_emo.findViewById(R.id.recycler_emo_view);
        mCustomIndicator = (EmoIndicatorView) rl_emo.findViewById(R.id.custom_indicator);// 设置指示器

        Animation anim = AnimationUtils.loadAnimation(context, R.anim.hx_pop_alpha_anim_in);
        rl_emo_layout.setAnimation(anim);// 设置弹出窗体动画效果
    }

    private void setAdapter() {

        initData();

        // 设置指示器
        mRecyclerView.setIndicator(mCustomIndicator);
        // 设置行数和列数
        mRecyclerView.setPageSize(columns, columns);
        // 设置页间距
        mRecyclerView.setPageMargin(30);
        // 设置数据
        mRecyclerView.setAdapter(mRecyclerView.new PageAdapter(dataList, new PageRecyclerView.CallBack() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.hx_phiz_item, parent, false);
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((ViewHolder) holder).imgGif.setImageResource(dataList.get(position).getEmoRes());
                ((ViewHolder) holder).tv_emo_name.setText(dataList.get(position).getEmoName());
            }

            @Override
            public void onItemClickListener(View view, int position) {

                final IFileSendListener listener = FileSendListenerImpl.getListener();
                final int userId = HuxinSdkManager.instance().getUserId();
                final String content = dataList.get(position).getEmoStr();

                final FileBean fileBean = new FileBean()
                        .setUserId(userId)
                        .setDstPhone(dstPhone)
                        .setTextContent(content);
                if (null != listener) {
                    if (CommonUtils.isNetworkAvailable(mContext)) {
                        //listener.onProgress(ChatMsg.MsgType.TEXT.ordinal(), 0.05);
                    } else {
                        listener.onImFail(ChatMsg.MsgType.TEXT.ordinal(), fileBean);
                    }
                }

                //todo_k: 表情
                final CacheMsgBean cacheMsgBean = new CacheMsgBean()
                        .setMsgTime(System.currentTimeMillis())
                        .setSend_flag(-1)
                        .setSenderPhone(HuxinSdkManager.instance().getPhoneNum())
                        .setSenderUserId(userId)
                        .setReceiverPhone(dstPhone)
                        .setMsgType(CacheMsgBean.MSG_TYPE_EMOTION)
                        .setJsonBodyObj(new CacheMsgEmotion().setEmotion(content, dataList.get(position).getEmoRes()))
                        .setRightUI(true);
                if (null != listener) {
                    if (CommonUtils.isNetworkAvailable(mContext)) {
                        //listener.onProgress(ChatMsg.MsgType.TEXT.ordinal(), 0.05);
                    } else {
                        listener.onImFail(ChatMsg.MsgType.TEXT.ordinal(), fileBean);
                    }
                }

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
                                            /*final PushFailPopWindow popWindow = new PushFailPopWindow(mContext);
                                            if (FloatViewUtil.instance().getFullView() != null && FloatViewUtil.instance().getArcMenu() == null) {
                                                popWindow.showAtLocation(FloatViewUtil.instance().getFullView(), Gravity.CENTER, 0, 0);
                                            }
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    popWindow.dismiss();
                                                }
                                            }, 1200);*/
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

                if (FullEmoPopWindow.this.isShowing()) {
                    if (res) {
                        if (CommonUtils.isNetworkAvailable(mContext)) {
                            /*final SuccessPopWindow successPopWindow = new SuccessPopWindow(mContext);
                            if (FloatViewUtil.instance().getFullView() != null && FloatViewUtil.instance().getArcMenu() == null) {
                                successPopWindow.showAtLocation(FloatViewUtil.instance().getFullView(), Gravity.CENTER, 0, 0);
                            }*/
                            /*new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {*/
                            AppUtils.showShortToast(mContext, mContext.getString(R.string.hx_toast_71));
                                /*}
                            }, 200);*/
                        } else {
                            AppUtils.showLongToast(mContext, mContext.getString(R.string.hx_toast_57));
                        }
                        FullEmoPopWindow.this.dismiss();
                    }
                }
            }

            @Override
            public void onItemLongClickListener(View view, int position) {
                /*Toast.makeText(MainActivity.this, "删除："
                        + dataList.get(position), Toast.LENGTH_SHORT).show();
                myAdapter.remove(position);*/
            }
        }));

    }

    private void initData() {
        dataList = new EmoInfo(mContext).getEmoList();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgGif;
        private TextView tv_emo_name;

        public ViewHolder(View itemView) {
            super(itemView);
            imgGif = (ImageView) itemView.findViewById(R.id.emo_img_gif);
            tv_emo_name = (TextView) itemView.findViewById(R.id.tv_emo_gif_name);
        }
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

    private IOnEmoCompleteListener listener;

    public interface IOnEmoCompleteListener {
        void onEmoSuccess();
    }

    public void setEmoListener(IOnEmoCompleteListener mListener) {
        this.listener = mListener;
    }

}
