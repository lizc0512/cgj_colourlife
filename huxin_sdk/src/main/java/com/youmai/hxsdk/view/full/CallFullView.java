package com.youmai.hxsdk.view.full;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.qiniu.android.storage.UpProgressHandler;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.IMFilePreviewActivity;
import com.youmai.hxsdk.im.cache.CacheMsgJoke;
import com.youmai.hxsdk.map.LocationActivity;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.SendSmsActivity;
import com.youmai.hxsdk.activity.CropImageActivity;
import com.youmai.hxsdk.activity.CropMapActivity;
import com.youmai.hxsdk.activity.CropVideoActivity;
import com.youmai.hxsdk.activity.HookCameraActivity;
import com.youmai.hxsdk.activity.WebViewActivity;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.bean.ChatMsg;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.db.helper.HxUsersHelper;
import com.youmai.hxsdk.entity.CallInfo;
import com.youmai.hxsdk.entity.EmoInfo;
import com.youmai.hxsdk.db.bean.HxUsers;
import com.youmai.hxsdk.db.bean.UIData;
import com.youmai.hxsdk.entity.UserShow;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.im.IMHelper;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;
import com.youmai.hxsdk.im.cache.CacheMsgMap;
import com.youmai.hxsdk.interfaces.IFileReceiveListener;
import com.youmai.hxsdk.interfaces.IFileSendListener;
import com.youmai.hxsdk.interfaces.bean.FileBean;
import com.youmai.hxsdk.interfaces.impl.FileReceiveListenerImpl;
import com.youmai.hxsdk.interfaces.impl.FileSendListenerImpl;
import com.youmai.hxsdk.module.callmsg.MsgActivity;
import com.youmai.hxsdk.module.callmsg.MsgFragment;
import com.youmai.hxsdk.module.filemanager.PickerManager;
import com.youmai.hxsdk.module.filemanager.activity.FileManagerActivity;
import com.youmai.hxsdk.module.filemanager.constant.FilePickerConst;
import com.youmai.hxsdk.module.filemanager.interfaces.PickerRefreshUI2Listener;
import com.youmai.hxsdk.module.photo.activity.PhotoActivity;
import com.youmai.hxsdk.popup.full.FMEmoPopWindow;
import com.youmai.hxsdk.popup.full.FullWhiteBoardPopWindow;
import com.youmai.hxsdk.popup.full.NoWifiLoadVideoPopWindow;
import com.youmai.hxsdk.popup.half.MEmoPopWindow;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiChat;
import com.youmai.hxsdk.push.http.HttpPushManager;
import com.youmai.hxsdk.recyclerview.CallPageRecycleView;
import com.youmai.hxsdk.recyclerview.page.CallFunctionItem;
import com.youmai.hxsdk.recyclerview.page.CallFunctionViewUtils;
import com.youmai.hxsdk.recyclerview.page.FunctionIndicator;
import com.youmai.hxsdk.recyclerview.page.FunctionPageView;
import com.youmai.hxsdk.service.HuxinService;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.sp.SPDataUtil;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.CallUtils;
import com.youmai.hxsdk.utils.CommonUtils;
import com.youmai.hxsdk.utils.CustomDigitalClock;
import com.youmai.hxsdk.utils.DisplayUtil;
import com.youmai.hxsdk.utils.FloatLogoUtil;
import com.youmai.hxsdk.utils.GlideCircleTransform;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.LogFile;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.SMSUtils;
import com.youmai.hxsdk.utils.ScreenUtils;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.utils.ToastUtil;
import com.youmai.hxsdk.utils.ViewAnimUtils;
import com.youmai.hxsdk.module.photo.animator.AnimatorUtils;
import com.youmai.hxsdk.module.photo.animator.PathPoint;
import com.youmai.hxsdk.view.camera.JCameraView;
import com.youmai.hxsdk.view.full.CallRecyclerAdapter.IDownloadClickListener;
import com.youmai.hxsdk.view.progressbar.RoundProgressBarWithBgProgress;
import com.youmai.hxsdk.view.progressbar.RoundProgressBarWithProgress;
import com.youmai.thirdbiz.INotifyDataChanged;
import com.youmai.thirdbiz.ThirdActivityHelper;
import com.youmai.thirdbiz.ThirdBizHelper;
import com.youmai.thirdbiz.ThirdBizMgr;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by yw on 2016/7/19.
 * 呼信通话弹屏全屏view
 */
public class CallFullView extends FrameLayout implements PickerRefreshUI2Listener, IDownloadClickListener {

    private static final String TYPE_ONE = CallFunctionViewUtils.ITEM_ONE;
    private static final String TYPE_TWO = CallFunctionViewUtils.ITEM_TWO;

    public static final long MAX_SENDER_FILE = 20 * 1024 * 1024;

    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams params;

    private View floatView;//浮屏全屏UI

    private String dstPhone; // 对方的手机号码
    private String showName; // 对方的秀名称

    private String province;
    private String city;

    /**
     * Background service executor
     */
    private ExecutorService mBackgoundExecutor;

    /*********************************
     * That call by first install
     *******************************/
    private int mLineWidth;//线宽
    private int leftWidth;//左偏
    private int emoPosition = -1;//emo标志位置

    /**********************************
     * Handler
     **********************************/
    private static final int HANDLER_REFRESH_NICKNAME = 1;
    private static final int mDelayTime = 1500;

    /********************************
     * 弹屏显示消息通信定义UI
     ********************************/
    private FMEmoPopWindow popWindow;
    private CallPageRecycleView mRecyclerView;
    private CallRecyclerAdapter mRVAdapter;


    /********************************
     * 白板
     ********************************/
    private FullWhiteBoardPopWindow whiteboardWindow;


    /******************************
     * 弹屏显示定义UI属性
     ******************************/
    private FrameLayout fl_user_info_parent;//user info parent
    private TextView tv_nickname;
    private TextView tv_full_locale;
    private CustomDigitalClock tv_call_time;

    private RelativeLayout rl_full_up_close;
    private RelativeLayout rl_full_jump_msg;
    private View full_msg_tip;
    private RoundProgressBarWithBgProgress progress_round_bar;
    private FrameLayout fl_callOrRing;
    private ImageView iv_finger;
    //private FrameLayout fl_function_bg;
    private FrameLayout fl_im_send_progress;

    private LinearLayout rl_full_refuse_reply;
    private ImageView iv_refuse_in;


    /*******************
     * 去电屏显示
     ********************/
    private ImageView iv_voice;
    private boolean flag; //扬声器UI标志

    /**********************************/

    public CallFullView(Context context) {
        this(context, null);
    }

    public CallFullView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    @Override
    protected void onAttachedToWindow() {
        try {
            super.onAttachedToWindow();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally {
            FloatViewUtil.instance().setDefaultListener();
            FloatViewUtil.instance().registerHome(mContext);
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        //PickerManager.getInstance().setRefreshUIListener(null);

        //FloatViewUtil.instance().clearDefaultListener();//弹屏隐藏也需要收到消息
        HuxinSdkManager.instance().unregisterSharkListener();
        FloatViewUtil.instance().unRegisterHome(mContext);

        mBackgoundExecutor.shutdown();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_REFRESH_NICKNAME:
                    String nickName = (String) msg.obj;
                    if (!StringUtils.isEmpty(nickName)) {
                        showName = nickName;
                    } else {
                        if (StringUtils.isEmpty(showName)
                                && !StringUtils.isEmpty(dstPhone)) {
                            showName = dstPhone;
                        }
                    }

                    tv_nickname.setText(showName);
                    //getUserHeader();
                    //ThirdBizMgr.getInstance().handleThirdBiz(mContext);
                    break;
                default:
                    break;
            }
        }
    };

    /*****************************
     * 2016.7.19 start by yw
     *******************************/
    private void init(Context context) {
        mContext = context.getApplicationContext();
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        li.inflate(R.layout.hx_full_phone_call_layout, this);

        FileReceiveListenerImpl.setReceiveListener(receive);
        FileSendListenerImpl.setListener(send);
        PickerManager.getInstance().setRefreshUI2Listener(this);

        initCallMsg();
        initViews();
        initFloatView(context);
    }

    private void initFloatView(Context context) {
        // 获取WindowManager
        mWindowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        params = new WindowManager.LayoutParams();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2
                && Build.VERSION.SDK_INT < 25) {// 4.3以上  // add by chenqy for android 7.1 弹屏被覆盖
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {// 4.3以下
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
        // 备注：使用WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL Lenovo手机不能接电话
        params.flags = WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

                | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

        //屏保服务
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {
            params.flags |= WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
        } else {
            params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        }

        params.format = PixelFormat.RGBA_8888;
        params.gravity = Gravity.START | Gravity.TOP;

        floatView = this;
    }

    private void executorNickName() {
        mBackgoundExecutor = Executors.newSingleThreadExecutor();
        mBackgoundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                initNickName(dstPhone);
            }
        });
    }

    private void initNickName(String phone) {
        String nickName = HuxinSdkManager.instance().getContactName(phone);

        Message msg = handler.obtainMessage();
        msg.what = HANDLER_REFRESH_NICKNAME;
        msg.obj = nickName;
        handler.sendMessage(msg);
    }

    public void addView() {
        if (floatView == null) {
            floatView = this;
        }

        if (floatView.getParent() == null) {
            try {
                mWindowManager.addView(floatView, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        freshSharkUI();
        executorNickName();
        if (ListUtils.isEmpty(mItemList)) {
            setListenerFormDefault();
            ThirdBizMgr.getInstance().handleThirdBiz(mContext);
        }
        historyCallMsg(msgList, false);

        //TODO：这里调用判断展示什么user info
        getUserHeader();
        getUserLocale();

    }

    public void removeView() {

        if (null == floatView) return;

        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
        }

        if (ll_jokes_receive_parent != null && ll_jokes_receive_parent.getVisibility() == View.VISIBLE) {
            ll_jokes_receive_parent.setVisibility(View.GONE);
        }

        if (floatView.getParent() != null) {
            LogUtils.e(Constant.SDK_SCREEN_LOG, "hidePopupWindow");
            try {
                mWindowManager.removeView(floatView);
                //floatView = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!CallInfo.IsCalling()) {
                mRecyclerView.setVisibility(View.GONE);
                CallFunctionViewUtils.instance(mContext).clearItemList();
            }
        }

        FloatLogoUtil.instance().hideFloat();
    }


    public void setShowInfo(String phone, String name) {
        if (!StringUtils.isEmpty(phone)) {
            dstPhone = phone;
        }
        if (!StringUtils.isEmpty(name)) {
            showName = name;
        }

        if (!StringUtils.isEmpty(phone)) {
            ThirdBizMgr.getInstance().setDstPhone(phone);
        }

        if (tv_nickname != null) {
            if (StringUtils.isEmpty(showName)) {
                showName = dstPhone;
            }
            tv_nickname.setText(showName);
        }
    }

    /**
     * 来电计算通话时间
     */
    public void startCallTime() {
        if (tv_call_time != null) {
            tv_call_time.start();
        }
    }

    /**
     * 去电计算通话时间
     */
    public void startCallTimeDelay() {
        if (tv_call_time != null) {
            tv_call_time.startDelay();
        }
    }

    public long getCallTime() {
        long res = 0;
        if (tv_call_time != null) {
            res = tv_call_time.getCountMillSecond();
        }
        return res;
    }


    /**
     * 设置来电或去电的文案
     *
     * @param locale
     */
    public void setLocale(String locale) {
        tv_call_time.setText(locale);
    }

    /**
     * 获取用户信息（头像，姓别）
     */
    public void getUserHeader() {
        if (dstPhone.equals(HuxinSdkManager.instance().getContactName(dstPhone))) {
            iv_hx_header.setImageResource(R.drawable.hx_icon_full_header_normal);
            iv_header_bg.setBackgroundResource(R.drawable.hx_icon_full_header_male_bg);
        } else {
            HxUsersHelper.instance().updateSingleUser(mContext, dstPhone, new HxUsersHelper.IOnUpCompleteListener() {
                @Override
                public void onSuccess(HxUsers users) {
                    updateHeader(users);
                }

                @Override
                public void onFail() {
                    HxUsers user = HxUsersHelper.instance().getSingleUserCache(mContext, dstPhone);
                    if (null != user) {
                        updateHeader(user);
                    }
                }
            });
        }
    }

    /**
     * 刷新头像
     *
     * @param users
     */
    private void updateHeader(HxUsers users) {
        if (iv_hx_header != null && null != users.getIconUrl()) {
            Glide.with(mContext).load(users.getIconUrl())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .placeholder(R.drawable.hx_icon_full_header_normal)
                            .error(R.drawable.hx_icon_full_header_normal).circleCrop())
                    .into(iv_hx_header);
        }
        if (null != users.getSex() && users.getSex().equals("2")) {
            iv_header_bg.setBackgroundResource(R.drawable.hx_icon_full_header_female_bg);
        } else {
            iv_header_bg.setBackgroundResource(R.drawable.hx_icon_full_header_male_bg);
        }
    }

    /**
     * 归属地查询
     */
    public void getUserLocale() {
        Location lastKnownLocation = AppUtils.getLastKnownLocation(mContext);
        String j = null;
        String w = null;
        if (lastKnownLocation != null) {
            //获取到位置信息
            j = lastKnownLocation.getLongitude() + "";
            w = lastKnownLocation.getLatitude() + "";
        }

        if (!StringUtils.isEmpty(province) || !StringUtils.isEmpty(city)) {
            return;
        }

        HuxinSdkManager.instance().userLocale(dstPhone, j, w, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                LogUtils.e(Constant.SDK_UI_TAG, "response = " + response);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.optString("s").equals("1")) {
                        JSONObject d = json.getJSONObject("d");
                        province = d.optString("province");
                        city = d.optString("city");
                        tv_full_locale.setText(province + " " + city);
                        tv_full_locale.setVisibility(View.VISIBLE);
                    } else {
                        tv_full_locale.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    tv_full_locale.setVisibility(View.GONE);
                    LogUtils.e(Constant.SDK_UI_TAG, "请求号码归属地失败");
                }
            }

        });
    }

    public String getShowName() {
        return showName;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    private void initCallMsg() {
        mRecyclerView = (CallPageRecycleView) findViewById(R.id.rv_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                mContext, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    public void updateCallMsg(ArrayList<ChatMsg> list) {

        if (list == null || list.size() == 0) {
            return;
        }
        mRecyclerView.setVisibility(View.VISIBLE);
        if (null == mRVAdapter) {
            mRVAdapter = new CallRecyclerAdapter(mContext, dstPhone);
            mRecyclerView.setAdapter(mRVAdapter);
        }
        mRVAdapter.updateDataList(list);
        mRecyclerView.updateTotalPage();
        mRecyclerView.setFocusable(false);
        mRVAdapter.setOnDownloadClickListener(this);
    }

    private ArrayList<ChatMsg> msgList = new ArrayList<>();//IM消息监听

    /**
     * 通话中IM消息界面MsgActivity
     *
     * @param list
     * @param isMsg true: IM消息监听
     */
    public void historyCallMsg(final ArrayList<ChatMsg> list, final boolean isMsg) {

        if (null == list || list.size() <= 0) {
            rl_full_jump_msg.setVisibility(View.GONE);
            return;
        }

        msgList = list;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isMsg) {
                    full_msg_tip.setVisibility(View.VISIBLE);
                }
                rl_full_jump_msg.setVisibility(View.VISIBLE);
            }
        }, 4900);

    }

    /**
     * IM 消息 统计
     */
    private RoundProgressBarWithProgress circleProgress, circleRec;
    private ImageView iv_send_status, iv_send_status_success, iv_rec_status;
    private TextView tv_progress_send, tv_progress_rec;
    private FrameLayout fl_im_receive_progress;
    private RelativeLayout rl_send_pic_Location_parent;
    private LinearLayout ll_send_progress;
    private View vv_send_bg;
    private ImageView iv_rec_pic, iv_send_pic;
    private LinearLayout ll_rec_location, ll_send_location;
    private TextView tv_send_location1, tv_send_location2, tv_rec_location1, tv_rec_location2;
    private GifImageView gf_rec_emo, gf_send_emo;
    private LinearLayout rl_rec_file_parent;
    private LinearLayout rl_send_file_parent;
    private ImageView iv_rec_file, iv_send_file;
    private TextView tv_rec_file_name, tv_rec_file_size, tv_send_file_name, tv_send_file_size;
    private RelativeLayout fl_rec_video_parent;
    private ImageView iv_rec_video, iv_rec_video_play_icon;
    //jokes
    private View ll_jokes_send_parent, rl_send_close, ll_joke_send_function;
    private TextView tv_jokes_send, tv_joke_send_exchange, tv_joke_send_share;
    private View ll_jokes_receive_parent, rl_receive_close;
    private TextView tv_jokes_receive, tv_jokes_receive_from;

    private LinearLayout ll_full_reply_item1, ll_full_reply_item2, ll_full_reply_item3;

    private FrameLayout third_ly;

    /**
     * new UI
     */
    private FrameLayout fl_hook_im_picture;
    private ImageView iv_hook_im_picture;

    private void initViews() {

        //Todo: start by user info
        fl_user_info_parent = (FrameLayout) findViewById(R.id.fl_full_user_info);//用户信息父布局
        initHxHeader();
        third_ly = (FrameLayout) findViewById(R.id.third_ly);

        initHxHeader();
        ThirdBizMgr.getInstance().initThirdBiz(mContext, third_ly, null);
        //Todo: end by user info

        fl_callOrRing = (FrameLayout) findViewById(R.id.fl_full_dial_parent);//判断来去电的父布局
        rl_full_up_close = (RelativeLayout) findViewById(R.id.rl_full_up_close);//收起弹屏按钮

        rl_full_jump_msg = (RelativeLayout) findViewById(R.id.rl_full_jump_msg);//通话中IM消息记录
        full_msg_tip = findViewById(R.id.full_msg_tip);//IM消息红点提示
        progress_round_bar = (RoundProgressBarWithBgProgress) findViewById(R.id.full_progress_round_bar);

        iv_finger = (ImageView) findViewById(R.id.iv_full_finger); //第一次新手引导
        //fl_function_bg = (FrameLayout) findViewById(R.id.fl_full_function_bg);//第一次功能区显示

        rl_full_refuse_reply = (LinearLayout) findViewById(R.id.rl_full_refuse_reply);//挂机键快速回复父布局
        ll_full_reply_item1 = (LinearLayout) findViewById(R.id.ll_full_reply_item1);//挂机键快速回复按钮1
        ll_full_reply_item2 = (LinearLayout) findViewById(R.id.ll_full_reply_item2);//挂机键快速回复按钮2
        ll_full_reply_item3 = (LinearLayout) findViewById(R.id.ll_full_reply_item3);//挂机键快速回复按钮3

        // Todo:start by send
        fl_im_send_progress = (FrameLayout) findViewById(R.id.fl_im_progress);//IM 消息进度更新
        View view = LayoutInflater.from(mContext).inflate(R.layout.hx_im_message_send_progress, null);
        fl_im_send_progress.addView(view);

        circleProgress = (RoundProgressBarWithProgress) view.findViewById(R.id.circle_progress_bar);
        tv_progress_send = (TextView) view.findViewById(R.id.tv_progress_send);
        iv_send_status = (ImageView) view.findViewById(R.id.iv_send_status);
        iv_send_status_success = (ImageView) view.findViewById(R.id.iv_send_status_success);
        iv_send_status.setImageResource(R.drawable.hx_full_rotate_loading);
        iv_send_status_success.setImageResource(R.drawable.hx_full_success);

        rl_send_pic_Location_parent = (RelativeLayout) view.findViewById(R.id.rl_send_pic_parent);//图片地图父布局
        vv_send_bg = view.findViewById(R.id.vv_send_bg);
        ll_send_progress = (LinearLayout) view.findViewById(R.id.ll_send_progress);
        iv_send_pic = (ImageView) view.findViewById(R.id.iv_send_pic);//图片&地图
        ll_send_location = (LinearLayout) view.findViewById(R.id.ll_send_location);//send地图位置
        tv_send_location1 = (TextView) view.findViewById(R.id.tv_send_location1);//send地图位置1
        tv_send_location2 = (TextView) view.findViewById(R.id.tv_send_location2);//send地图位置2
        gf_send_emo = (GifImageView) view.findViewById(R.id.gf_send_emo);//表情
        rl_send_file_parent = (LinearLayout) view.findViewById(R.id.rl_send_file_parent);//文件
        iv_send_file = (ImageView) view.findViewById(R.id.iv_send_file);
        tv_send_file_name = (TextView) view.findViewById(R.id.iv_send_file_name);
        tv_send_file_size = (TextView) view.findViewById(R.id.iv_send_file_size);

        ll_jokes_send_parent = findViewById(R.id.ll_jokes_send_parent); //段子
        ll_joke_send_function = findViewById(R.id.ll_joke_send_function);
        rl_send_close = findViewById(R.id.rl_send_close);
        tv_jokes_send = (TextView) findViewById(R.id.tv_send_jokes);
        tv_joke_send_exchange = (TextView) findViewById(R.id.tv_joke_send_exchange);
        tv_joke_send_share = (TextView) findViewById(R.id.tv_joke_send_share);
        // Todo:end by send

        // Todo:start by receive
        fl_im_receive_progress = (FrameLayout) findViewById(R.id.fl_im_receive_progress);//IM 接收消息进度更新
        View viewRec = LayoutInflater.from(mContext).inflate(R.layout.hx_im_message_receive_progress, null);
        fl_im_receive_progress.addView(viewRec);

        circleRec = (RoundProgressBarWithProgress) viewRec.findViewById(R.id.circle_rec_progress_bar);
        tv_progress_rec = (TextView) viewRec.findViewById(R.id.tv_progress_rec);
        iv_rec_status = (ImageView) viewRec.findViewById(R.id.iv_rec_status);
        iv_rec_status.setImageResource(R.drawable.hx_full_rotate_loading);

        iv_rec_pic = (ImageView) viewRec.findViewById(R.id.iv_rec_pic);//图片&地图
        ll_rec_location = (LinearLayout) viewRec.findViewById(R.id.ll_rec_location);//地图位置
        tv_rec_location1 = (TextView) viewRec.findViewById(R.id.tv_rec_location1);//地图位置1
        tv_rec_location2 = (TextView) viewRec.findViewById(R.id.tv_rec_location2);//地图位置2
        gf_rec_emo = (GifImageView) viewRec.findViewById(R.id.gf_rec_emo);//表情
        rl_rec_file_parent = (LinearLayout) viewRec.findViewById(R.id.rl_rec_file_parent);//文件
        iv_rec_file = (ImageView) viewRec.findViewById(R.id.iv_rec_file);
        tv_rec_file_name = (TextView) viewRec.findViewById(R.id.iv_rec_file_name);
        tv_rec_file_size = (TextView) viewRec.findViewById(R.id.iv_rec_file_size);
        fl_rec_video_parent = (RelativeLayout) viewRec.findViewById(R.id.fl_rec_video_parent);//短视频
        iv_rec_video = (ImageView) viewRec.findViewById(R.id.iv_rec_video);
        iv_rec_video_play_icon = (ImageView) viewRec.findViewById(R.id.iv_rec_video_play_icon);

        ll_jokes_receive_parent = findViewById(R.id.ll_jokes_receive_parent); //段子
        tv_jokes_receive = (TextView) ll_jokes_receive_parent.findViewById(R.id.tv_jokes);
        tv_jokes_receive_from = (TextView) findViewById(R.id.tv_jokes_from);
        rl_receive_close = findViewById(R.id.rl_close);
        rl_receive_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_jokes_receive_parent.setVisibility(View.GONE);
            }
        });

        // Todo:end by receive

        // TODO: start by send picture new UI
        iv_hook_im_picture = (ImageView) findViewById(R.id.iv_hook_im_picture);
        fl_hook_im_picture = (FrameLayout) findViewById(R.id.fl_hook_im_picture);
        // TODO: end by send picture new UI

        //启动发与收信息的动画
        startAnimation(iv_send_status, 500);
        startRecAnimation(iv_rec_status, 500);

        // TODO: start by function
        hook_top_box = (RelativeLayout) findViewById(R.id.hook_top_box);
        recycler_view = (FunctionPageView) findViewById(R.id.hook_recycler_view);
        page_indicator = (FunctionIndicator) findViewById(R.id.card_header_indicator);
        CallFunctionViewUtils.instance(mContext).clearItemList();
        // TODO: end by function
    }

    private ImageView iv_header_bg;//性别背景
    private ImageView iv_hx_header;//用户头像

    /**
     * 添加呼信用户信息区分第三方APP
     */
    private void initHxHeader() {
        fl_user_info_parent.removeAllViews();
        View hxUserView = LayoutInflater.from(mContext).inflate(R.layout.hx_full_user_info, null);
        fl_user_info_parent.addView(hxUserView);
        iv_header_bg = (ImageView) hxUserView.findViewById(R.id.iv_full_header_bg);//用户头像背景
        iv_hx_header = (ImageView) hxUserView.findViewById(R.id.iv_full_header);//用户头像
        tv_nickname = (TextView) hxUserView.findViewById(R.id.tv_full_nickname);//昵称
        tv_full_locale = (TextView) hxUserView.findViewById(R.id.tv_full_locale);//呼信APP的号码归属地
        tv_call_time = (CustomDigitalClock) hxUserView.findViewById(R.id.tv_call_time);//通话时长

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (tv_nickname.getEllipsize() == TextUtils.TruncateAt.MARQUEE) {
                    tv_nickname.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        }, 20 * 1000);
    }

    private void freshSharkUI() {
        if (tv_nickname != null) {
            if (StringUtils.isEmpty(showName)) {
                showName = dstPhone;
            }
            tv_nickname.setText(showName);
        }
        setLayoutContent(CallInfo.IsMTCalling());//设置默认为来电布局
    }

    /**
     * 动态监听定制数据
     */
    public void setListenerFormShow(List<UserShow.DBean.SectionsBean> list) {

        LogUtils.e("YW", "show come here");

        if (!ListUtils.isEmpty(mItemList)) {
            mItemList.clear();
        }
        for (UserShow.DBean.SectionsBean item : list) {
            mItemList.add(new CallFunctionItem(item.getName(), item.getIcon(), item.getNum(), item.getType(), item.getData()));
        }

        if (ListUtils.isEmpty(mItemList)) {
            CallFunctionViewUtils.instance(mContext).addInitFuncList(true);
            mItemList = CallFunctionViewUtils.instance(mContext).getItemList();
        }

        // 设置页间距
        recycler_view.setPageMargin(DisplayUtil.dip2px(mContext, 10));
        // 设置指示器
        recycler_view.setIndicator(page_indicator);
        // 设置数据
        recycler_view.setAdapter(mFunctionAdapter);

        upToClick();
    }


    /**
     * 动态监听定制数据 addView()
     */
    public void setListenerFormDefault() {

        LogUtils.e("YW", "default function item.");

        if (!ListUtils.isEmpty(mItemList)) {
            mItemList.clear();
        }

        List<UIData> uiDataList = HuxinSdkManager.instance().getUIData(dstPhone);

        if (!ListUtils.isEmpty(uiDataList)) {
            for (UIData item : uiDataList) {
                mItemList.add(new CallFunctionItem(item.getName(), item.getIcon(), item.getNum(), item.getType(), item.getData()));
            }
        } else {
            if (HuxinSdkManager.instance().getCPU() != HuxinSdkManager.CPU_MTK) {
                CallFunctionViewUtils.instance(mContext).addInitFuncList(true);
                mItemList = CallFunctionViewUtils.instance(mContext).getItemList();
            } else {
                CallFunctionViewUtils.instance(mContext).addInitFuncList(false);
                mItemList = CallFunctionViewUtils.instance(mContext).getItemList();
            }
        }

        initPager();

        upToClick();
    }

    private void sendReply(final String content) {

        SMSUtils.sendSMS(mContext, dstPhone, content + mContext.getString(R.string.hx_send_text_suffix));
        
        /*LogUtils.e(Constant.SDK_UI_TAG, content);
        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        if (ack.getIsTargetOnline()) {
                            Toast.makeText(mContext, mContext.getString(R.string.hx_toast_31), Toast.LENGTH_SHORT).show();
                        } else {
                            SMSUtils.sendSMS(mContext, dstPhone, content + mContext.getString(R.string.hx_send_text_suffix));
                        }
                    } else {
                        SMSUtils.sendSMS(mContext, dstPhone, content + mContext.getString(R.string.hx_send_text_suffix));
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        };

        YouMaiChat.IMChat_Personal.Builder builder = YouMaiChat.IMChat_Personal.newBuilder();
        builder.setSrcUsrId(HuxinSdkManager.instance().getUserId());
        builder.setTargetPhone(PhoneNumTypes.changePhone(dstPhone, mContext));
        //builder.setTargetPhone(desPhone);

        IMContentUtil imContentUtil = new IMContentUtil();

        final int type = imContentUtil.getContentType(0, YouMaiChat.IM_CONTENT_TYPE.IM_CONTENT_TYPE_TEXT_VALUE);
        builder.setContentType(type);
        imContentUtil.appendText(content);
        builder.setBody(imContentUtil.serializeToString());
        YouMaiChat.IMChat_Personal imData = builder.build();
        HuxinSdkManager.instance().sendProto(imData, callback);*/
    }

    /**
     * 第一次手指引导判断
     */
    private void judgeGuide() {
        //显示手指引导
        if (!SPDataUtil.getIsFirstShowCall(mContext)) {
            twinkleBg();
            iv_finger.setVisibility(View.VISIBLE);
            SPDataUtil.setIsFirstShowCall(mContext, true);
            initTabLineWidth();
            ViewAnimUtils.floatAnim(iv_finger, 400);
        }
    }

    /**
     * first install call by
     */
    private void twinkleBg() {
        if (!SPDataUtil.getIsFirstShowCall(mContext)) {
            //fl_function_bg.setVisibility(View.VISIBLE);
            ViewAnimUtils.alphaAnim(hook_top_box, mDelayTime);
            /*handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fl_function_bg.setVisibility(View.GONE);
                }
            }, mDelayTime * 3);*/
        }
    }

    /**
     * 计算emo位置
     * That call by first install
     */
    private void initTabLineWidth() {
        mLineWidth = mContext.getResources().getDisplayMetrics().widthPixels / 4;
        emoPosition = 2;
        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) iv_finger.getLayoutParams();
        if (emoPosition == 2) {
            leftWidth = mLineWidth * emoPosition + DisplayUtil.dip2px(mContext, 20);
        } else {
            leftWidth = mLineWidth * emoPosition + DisplayUtil.dip2px(mContext, 30);
        }
        lParams.leftMargin = leftWidth;
        iv_finger.setLayoutParams(lParams);
    }

    private void jumpLocation() {
        if (HuxinSdkManager.instance().showSdkLogin()) {
            removeView();
            return;
        }

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("dstPhone", dstPhone);
        intent.setClass(mContext, LocationActivity.class);

        mContext.startActivity(intent);
    }

    private void jumpPicture() {
        if (HuxinSdkManager.instance().showSdkLogin()) {
            removeView();
            return;
        }

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(PhotoActivity.EXTRA_SHOW_CAMERA, false);//显示摄像
        intent.putExtra("dstPhone", dstPhone);
        intent.setClass(mContext, PhotoActivity.class);
        mContext.startActivity(intent);

        /*Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("dstPhone", dstPhone);
        intent.putExtra("type", WrapSendFileActivity.TYPE_PIC);
        intent.setClass(mContext, WrapSendFileActivity.class);
        mContext.startActivity(intent);*/
    }

    /**
     * 拍照
     */
    private void jumpCamera() {
        if (HuxinSdkManager.instance().showSdkLogin()) {
            removeView();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(mContext, HookCameraActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(HookCameraActivity.CAMERA_TYPE, JCameraView.BUTTON_STATE_ONLY_CAPTURE);
                intent.putExtra(HookCameraActivity.TARGET_PHONE, dstPhone);
                mContext.startActivity(intent);
            } else {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA}, 104);
            }
        } else {
            Intent intent = new Intent(mContext, HookCameraActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(HookCameraActivity.CAMERA_TYPE, JCameraView.BUTTON_STATE_ONLY_CAPTURE);
            intent.putExtra(HookCameraActivity.TARGET_PHONE, dstPhone);
            mContext.startActivity(intent);
        }

    }

    /**
     * 跳到文件管理器
     */
    private void jumpFile() {
        if (HuxinSdkManager.instance().showSdkLogin()) {
            removeView();
            return;
        }

//        Intent intent = new Intent();
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("dstPhone", dstPhone);
//        intent.putExtra("type", WrapSendFileActivity.TYPE_FILE);
//        intent.setClass(mContext, WrapSendFileActivity.class);
//        mContext.startActivity(intent);

        PickerManager.getInstance().addDocTypes();
        Intent intent = new Intent(mContext, FileManagerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("dstPhone", dstPhone);
        intent.putExtra(FileManagerActivity.REQUEST_CODE_CALLBACK, FilePickerConst.CALL_REQUEST_CALLBACK);
        mContext.startActivity(intent);

    }

    private void popWhiteBoard() {
        if (HuxinSdkManager.instance().showSdkLogin()) {
            removeView();
            return;
        }
        try {
            if (whiteboardWindow == null) {
                whiteboardWindow = new FullWhiteBoardPopWindow(mContext, dstPhone,
                        DisplayUtil.dip2px(mContext, 300), DisplayUtil.dip2px(mContext, 450));
            }

            if (!whiteboardWindow.isShowing()) {
                whiteboardWindow.showAtLocation(floatView, Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        } catch (Exception e) {
            whiteboardWindow = null;
        }
    }

    /**
     * 展示段子
     */
    private void popJokes() {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
        }
        if (ll_jokes_receive_parent != null && ll_jokes_receive_parent.getVisibility() == View.VISIBLE) {
            ll_jokes_receive_parent.setVisibility(View.GONE);
        }
        ll_joke_send_function.setVisibility(View.VISIBLE);
        ll_jokes_send_parent.setVisibility(View.VISIBLE);
        if (StringUtils.isEmpty(tv_jokes_send.getText().toString())) {
            tv_jokes_send.setText(FloatViewUtil.instance().getJokesRandom());
        }
        rl_send_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_jokes_send_parent.setVisibility(View.GONE);
            }
        });
        tv_joke_send_exchange.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_jokes_send.setText(FloatViewUtil.instance().getJokesRandom());
            }
        });
        tv_joke_send_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HuxinSdkManager.instance().showSdkLogin()) {
                    removeView();
                    return;
                }
                AlphaAnimation animation = new AlphaAnimation(1, 0);
                animation.setDuration(400);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ll_joke_send_function.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                ll_joke_send_function.setAnimation(animation);
                HuxinSdkManager.instance().sendJokesText(tv_jokes_send.getText().toString(), dstPhone, send);
            }
        });
    }

    private void popEmo() {
        if (HuxinSdkManager.instance().showSdkLogin()) {
            removeView();
            return;
        }
        try {
            if (popWindow == null) {
                popWindow = new FMEmoPopWindow(mContext, dstPhone,
                        ViewGroup.LayoutParams.MATCH_PARENT, (int) mContext.getResources().getDimension(R.dimen.hx_full_pop_emo_height));
            }
            if (!popWindow.isShowing()) {
                popWindow.showAtLocation(floatView, Gravity.START | Gravity.TOP, 0, 0);
            }
        } catch (Exception e) {
            popWindow = null;
        }
    }

    /* 收起电话屏、监听向上滑动 start */
    private void upToClick() {

        // TODO: 2017/3/7  start by 挂机键的快速回复
        ll_full_reply_item1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) findViewById(R.id.tv_full_reply_item1);
                sendReply(tv.getText().toString());
                rl_full_refuse_reply.setVisibility(GONE);
                iv_refuse_in.performClick();
            }
        });

        ll_full_reply_item2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) findViewById(R.id.tv_full_reply_item2);
                sendReply(tv.getText().toString());
                rl_full_refuse_reply.setVisibility(GONE);
                iv_refuse_in.performClick();
            }
        });

        ll_full_reply_item3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) findViewById(R.id.tv_full_reply_item3);
                sendReply(tv.getText().toString());
                rl_full_refuse_reply.setVisibility(GONE);
                iv_refuse_in.performClick();
            }
        });
        // TODO: 2017/3/7  end by 挂机键的快速回复

        rl_full_up_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView();
                FloatLogoUtil.instance().showFloat(mContext, HuxinService.MODEL_TYPE_FULL, false);
            }
        });

        rl_full_jump_msg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MsgActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putParcelableArrayListExtra(MsgFragment.MSG_LIST, msgList);
                mContext.startActivity(intent);
                full_msg_tip.setVisibility(View.INVISIBLE);
            }
        });

        rl_full_up_close.setOnTouchListener(onTouch);

        //TODO：监听第三方接入SDK功能按钮回调 2017.10.28
        ThirdBizMgr.getInstance().setNotifyFullCallBack(new INotifyDataChanged<CallFunctionItem>() {
            @Override
            public void notifyData(List<CallFunctionItem> list) {
                if (ListUtils.isEmpty(mItemList)) {
                    mItemList = CallFunctionViewUtils.instance(mContext).getItemList();
                }
                mItemList.addAll(0, list);

                // 设置页间距
                recycler_view.setPageMargin(DisplayUtil.dip2px(mContext, 10));
                // 设置指示器
                recycler_view.setIndicator(page_indicator);
                // 设置数据
                recycler_view.setAdapter(mFunctionAdapter);
            }
        });
    }

    private final OnTouchListener onTouch = new OnTouchListener() {
        int firstY;
        int lastY;
        int paramY;
        int firstX, lastX;

        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    firstY = (int) event.getRawY();
                    lastX = firstX;
                    lastY = firstY;
                    paramY = params.y;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (lastY == (int) event.getRawY())
                        break;
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();

                    int dx = (int) event.getRawX() - firstX;
                    int dy = (int) event.getRawY() - firstY;

                    LogUtils.e(Constant.SDK_UI_TAG, "dx = " + dx + "dy = " + dy);

                    if (Math.abs(dy) > view.getHeight() / 3) {
                        int screenHeight = ScreenUtils.getHeightPixels(mContext);
                        if (screenHeight - lastY < 100) {
                            break;
                        }
                    }

                    params.y = paramY + dy;
                    // 更新悬浮窗位置
                    if (null != mWindowManager && floatView != null) {
                        mWindowManager.updateViewLayout(floatView, params);//没效果
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    LogUtils.e(Constant.SDK_UI_TAG, "event.getRawY() = " + event.getRawY() + "; firstY = " + firstY);
                    if (firstY - event.getRawY() <= 300 && firstY - event.getRawY() >= 50 && CallInfo.IsCalling()) {
                        //return true; //不执行啦
                    } else { // 移动事件
                        removeView();
                        FloatLogoUtil.instance().showFloat(mContext, HuxinService.MODEL_TYPE_FULL, false);
                    }
                    break;
            }
            return false;
        }
    };

    /**
     * @param IsMTCalling true 未接通,false已接通
     *                    来去电布局动态添加
     */
    public void setLayoutContent(boolean IsMTCalling) {
        View view;
        if (IsMTCalling && CallUtils.isSupportMTBefore(mContext)) {
            view = getRingMenuView();//有绿键和红键
        } else {
            view = getCallMenuView();  //只有红键
        }

        fl_callOrRing.removeAllViews();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        fl_callOrRing.addView(view, params);

    }

    private View getRingMenuView() {
        final View ringMenuView = LayoutInflater.from(mContext).inflate(R.layout.hx_full_ring_view, null);
        iv_refuse_in = (ImageView) ringMenuView.findViewById(R.id.iv_refuse_in);
        final ImageView iv_refuse_up = (ImageView) ringMenuView.findViewById(R.id.iv_refuse_up);
        final ImageView iv_dial_in = (ImageView) ringMenuView.findViewById(R.id.iv_dial_in);

        iv_refuse_in.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                CallUtils.endCall(mContext);
            }
        });

        iv_refuse_up.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rl_full_refuse_reply.getVisibility() == VISIBLE) {
                    rl_full_refuse_reply.setVisibility(GONE);
                    iv_refuse_up.setImageResource(R.drawable.hx_full_refuse_up);
                } else {
                    rl_full_refuse_reply.setVisibility(VISIBLE);
                    iv_refuse_up.setImageResource(R.drawable.hx_full_refuse_down);
                }
            }
        });

        iv_dial_in.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                CallUtils.acceptCall(mContext);
                setLayoutContent(false);//设置接通后的布局
            }
        });

        return ringMenuView;

    }

    private View getCallMenuView() {

        View callMenuView = LayoutInflater.from(mContext).inflate(R.layout.hx_full_call_view, null);

        ImageView iv_dial_key = (ImageView) callMenuView.findViewById(R.id.iv_dial_key);
        ImageView iv_refuse_out = (ImageView) callMenuView.findViewById(R.id.iv_refuse_out);
        iv_voice = (ImageView) callMenuView.findViewById(R.id.iv_voice);

        flag = CallUtils.isSpeakerOn(mContext);
        if (flag) {
            iv_voice.setImageResource(R.drawable.hx_icon_full_voice_click);
        } else {
            iv_voice.setImageResource(R.drawable.hx_icon_full_voice_normal);
        }

        iv_dial_key.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                removeView();
                if (CallInfo.IsCalling()) {
                    FloatLogoUtil.instance().showFloat(mContext, HuxinSdkManager.instance().getFloatType(), false);
                }
            }
        });

        iv_refuse_out.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                CallUtils.endCall(mContext);
            }
        });

        iv_voice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = CallUtils.isSpeakerOn(mContext);
                if (!flag) {
                    CallUtils.openSpeaker(mContext);
                    iv_voice.setImageResource(R.drawable.hx_icon_full_voice_click);
                } else {
                    CallUtils.closeSpeaker(mContext);
                    iv_voice.setImageResource(R.drawable.hx_icon_full_voice_normal);
                }
                LogUtils.e(Constant.SDK_UI_TAG, flag + ";" + Build.BRAND + ";" + Build.MODEL);
                if (CallInfo.IsCalling() && !flag) {
                    if (Build.BRAND.toUpperCase().equals("HUAWEI") || Build.BRAND.toUpperCase().equals("HONOR")) {
                        removeView();
                        FloatLogoUtil.instance().showFloat(mContext, HuxinSdkManager.instance().getFloatType(), false);
                    }
                }
            }
        });
        return callMenuView;
    }

    //重写dispatchKeyEvent
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                // 处理自己的逻辑break;
                if (CallInfo.IsCalling()) {
                    removeView();
                    FloatLogoUtil.instance().showFloat(mContext, HuxinService.MODEL_TYPE_FULL, false);
                } else {
                    removeView();
                }
                break;
            default:
                break;
        }
        return true;
    }

    // TODO: IM ****************************************************
    private int mIMCount = 0;//IM去重
    private boolean isIMFirst;//IM第一次
    private boolean isRecComing = false;//收：发送信息时受到IM进来，先发送完再显示接收消息
    private boolean isSendTo = false;//发：判断是否有当前接收IM，如果有发送消息后显示接收界面

    IFileSendListener send = new IFileSendListener() {

        @Override
        public void onProgress(int type, double progress, String path) {

            //TODO:有网络come发送可见
            if (type != ChatMsg.MsgType.TEXT.ordinal()) {
                rl_send_pic_Location_parent.setVisibility(View.GONE);
                rl_send_file_parent.setVisibility(View.GONE);
                fl_im_send_progress.setVisibility(View.VISIBLE);
                isSendTo = true;
            } else if (type != ChatMsg.MsgType.BIG_FILE.ordinal()) {
                rl_send_file_parent.setVisibility(View.VISIBLE);
                rl_send_pic_Location_parent.setVisibility(View.GONE);
            } else if (type != ChatMsg.MsgType.PICTURE.ordinal() || type != ChatMsg.MsgType.LOCATION.ordinal()) {
                rl_send_pic_Location_parent.setVisibility(View.VISIBLE);
                rl_send_file_parent.setVisibility(View.GONE);
            }

            // TODO: 2017/9/9
            if (type == ChatMsg.MsgType.PICTURE.ordinal()) {
                fl_im_send_progress.setVisibility(GONE);
                rl_send_pic_Location_parent.setVisibility(View.GONE);
                ll_send_progress.setVisibility(View.GONE);
                vv_send_bg.setVisibility(View.GONE);

                if (type == ChatMsg.MsgType.PICTURE.ordinal() && ((int) Math.abs(progress * 100)) == 1) {
                    /*fl_im_send_progress.setVisibility(GONE);
                    rl_send_pic_Location_parent.setVisibility(View.GONE);
                    ll_send_progress.setVisibility(View.GONE);
                    vv_send_bg.setVisibility(View.GONE);*/

                    fl_hook_im_picture.setVisibility(View.GONE);
                    fl_hook_im_picture.setVisibility(View.VISIBLE);

                    Glide.with(mContext)
                            .load(path)
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .placeholder(R.drawable.hx_half_pic_moren)
                                    .error(R.drawable.hx_half_pic_moren).centerCrop())
                            .into(iv_hook_im_picture);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AnimatorUtils.startAnimScale(CallFullView.this, fl_hook_im_picture);
                        }
                    }, 1000);

                }
            } else {
                fl_hook_im_picture.setVisibility(View.GONE);

                fl_im_send_progress.setVisibility(VISIBLE);
                ll_send_progress.setVisibility(View.VISIBLE);
                vv_send_bg.setVisibility(View.VISIBLE);
            }

            // TODO: 2017/9/9

            circleProgress.setVisibility(View.VISIBLE);
            circleProgress.setProgress((int) Math.abs(progress * 100));

            tv_progress_send.setText(mContext.getString(R.string.hx_call_full_view_sending));
            iv_send_status.setImageResource(R.drawable.hx_full_rotate_loading);
            iv_send_status.setVisibility(View.VISIBLE);
            iv_send_status_success.setVisibility(View.GONE);
            fl_im_receive_progress.setVisibility(View.GONE);
        }

        @Override
        public void onImSuccess(final int type, final FileBean fileBean) {

            tv_progress_send.setText(mContext.getString(R.string.hx_call_full_view_send_success));

            if (type == ChatMsg.MsgType.BIG_FILE.ordinal()) {

                fl_im_receive_progress.setVisibility(View.GONE);

                fl_im_send_progress.setVisibility(View.VISIBLE);
                iv_send_file.setImageResource(fileBean.getFileRes());
                tv_send_file_name.setText(fileBean.getFileName());
                tv_send_file_size.setText(IMHelper.convertFileSize(Long.valueOf(fileBean.getFileLength())));

                iv_send_pic.setVisibility(View.GONE);
                ll_send_location.setVisibility(View.GONE);
                rl_send_file_parent.setVisibility(View.VISIBLE);

                sendFile.add(fileBean);

            } else if (type == ChatMsg.MsgType.PICTURE.ordinal()) {

                sendPic.add(fileBean);

            } else if (type == ChatMsg.MsgType.LOCATION.ordinal()) {

                fl_im_receive_progress.setVisibility(View.GONE);

                fl_im_send_progress.setVisibility(View.VISIBLE);
                String[] split = fileBean.getAddress().split(":");

                if (split.length > 1) {
                    tv_send_location1.setText(split[0]);
                    tv_send_location2.setText(split[1]);
                } else {
                    tv_send_location1.setText(fileBean.getAddress());
                    tv_send_location2.setVisibility(View.GONE);
                }

                Glide.with(mContext)
                        .load(fileBean.getMapUrl())
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop())
                        .into(iv_send_pic);

                rl_send_pic_Location_parent.setVisibility(View.VISIBLE);
                iv_send_pic.setVisibility(View.VISIBLE);
                ll_send_location.setVisibility(View.VISIBLE);
                rl_send_file_parent.setVisibility(View.GONE);

                sendLocation.add(fileBean);

            } else if (type == ChatMsg.MsgType.TEXT.ordinal()
                    || type == ChatMsg.MsgType.EMO_TEXT.ordinal()) {

                String textContent = fileBean.getTextContent();
                if (new EmoInfo(mContext).isEmotion(textContent)) {
                    List<EmoInfo.EmoItem> dataList = new EmoInfo(mContext).getEmoList();
                    for (EmoInfo.EmoItem emoItem : dataList) {
                        String emoStr = emoItem.getEmoStr();
                        if (emoStr.equals(fileBean.getTextContent())) {
                            try {

                                MEmoPopWindow emoPopWindow = new MEmoPopWindow(mContext, emoItem.getEmoRes(), "-100");
                                emoPopWindow.showAtLocation(floatView, Gravity.CENTER, 0, 0);

                                if (emoPopWindow != null) {
                                    final MEmoPopWindow finalEmoPopWindow = emoPopWindow;
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_FULL) {
                                                if (finalEmoPopWindow.isShowing()
                                                        && floatView != null
                                                        && floatView.getParent() != null) {
                                                    finalEmoPopWindow.dismiss();
                                                }
                                            }
                                        }
                                    }, 2500);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                } else if (fileBean.getTextContent().startsWith("/")) {
                    try {
                        MEmoPopWindow emoPopWindow = new MEmoPopWindow(mContext, -1, textContent);
                        emoPopWindow.showAtLocation(floatView, Gravity.CENTER, 0, 0);

                        if (emoPopWindow != null) {
                            final MEmoPopWindow finalEmoPopWindow = emoPopWindow;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_FULL) {
                                        if (finalEmoPopWindow.isShowing()
                                                && floatView != null
                                                && floatView.getParent() != null) {
                                            finalEmoPopWindow.dismiss();
                                        }
                                    }
                                }
                            }, 3000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (type == ChatMsg.MsgType.JOKE_TEXT.ordinal()) {
                AnimatorUtils.startAnimJoke(CallFullView.this, ll_jokes_send_parent);
                return;
            }

            if (circleProgress.getProgress() == 100) {
                stopAnimation(iv_send_status);
                circleProgress.setVisibility(View.GONE);
                iv_send_status.setVisibility(View.GONE);//发送中
                iv_send_status_success.setVisibility(View.VISIBLE);//发送成功
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (type == ChatMsg.MsgType.TEXT.ordinal()) {
                        return;
                    }
                    fl_im_send_progress.setVisibility(View.GONE);
                    startAnimation(iv_send_status, 500);
                    if (isRecComing) {
                        fl_im_receive_progress.setVisibility(View.VISIBLE);
                        isRecComing = false;
                    }
                }
            }, 3500);

        }

        @Override
        public void onImFail(final int type, final FileBean fileBean) {

            if (null == fileBean) {
                LogUtils.e("xx", "fail send type = " + type + "; fileBean is null!");
            }

            if (type == ChatMsg.MsgType.TEXT.ordinal()) {
                if (popWindow != null) {
                    popWindow.dismiss();
                }
                fl_im_send_progress.setVisibility(View.GONE);
                if (!CommonUtils.isNetworkAvailable(mContext)) {
                    ToastUtil.showToast(mContext, mContext.getString(R.string.hx_toast_50));
                } else {
                    ToastUtil.showToast(mContext, mContext.getString(R.string.hx_toast_70));
                }
            } else {
                fl_im_send_progress.setVisibility(View.VISIBLE);
            }

            if (!CommonUtils.isNetworkAvailable(mContext)) { //网络断开就隐藏
                rl_send_pic_Location_parent.setVisibility(View.GONE);
                rl_send_file_parent.setVisibility(View.GONE);
            }

            iv_send_status.setVisibility(View.VISIBLE);
            iv_send_status_success.setVisibility(View.GONE);
            iv_send_status.setImageResource(R.drawable.hx_full_fail_retry);
            stopAnimation(iv_send_status);
            circleProgress.setVisibility(View.GONE);
            tv_progress_send.setText(mContext.getString(R.string.hx_call_full_view_send_fail));

            iv_send_status.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startAnimation(iv_send_status, 500);
                    if (type == ChatMsg.MsgType.BIG_FILE.ordinal()) {
                        postFile(fileBean);
                    } else if (type == ChatMsg.MsgType.PICTURE.ordinal()) {
                        postPicture(fileBean);
                    } else if (type == ChatMsg.MsgType.LOCATION.ordinal()) {
                        postLocation(fileBean);
                    } else if (type == ChatMsg.MsgType.TEXT.ordinal()) {
                        //表情
                    }
                }
            });

        }

        @Override
        public void onImNotUser(int type, long msgId) {
            fl_im_send_progress.setVisibility(View.GONE);
        }
    };

    private ArrayList<FileBean> sendPic = new ArrayList<>();
    private ArrayList<FileBean> sendLocation = new ArrayList<>();
    private ArrayList<FileBean> sendFile = new ArrayList<>();

    private ArrayList<FileBean> recPic = new ArrayList<>();
    private ArrayList<FileBean> recLocation = new ArrayList<>();
    private ArrayList<FileBean> recFile = new ArrayList<>();

    public ArrayList<FileBean> getSendPic() {
        return sendPic;
    }

    public ArrayList<FileBean> getSendLocation() {
        return sendLocation;
    }

    public ArrayList<FileBean> getSendFile() {
        return sendFile;
    }

    public ArrayList<FileBean> getRecPic() {
        return recPic;
    }

    public ArrayList<FileBean> getRecLocation() {
        return recLocation;
    }

    public ArrayList<FileBean> getRecFile() {
        return recFile;
    }

    IFileReceiveListener receive = new IFileReceiveListener() {
        @Override
        public void onProgress(int type, double progress) {

            if (type != ChatMsg.MsgType.TEXT.ordinal() && type != ChatMsg.MsgType.JOKE_TEXT.ordinal()) {
                fl_im_receive_progress.setVisibility(View.VISIBLE);
            }

            iv_rec_status.setVisibility(View.VISIBLE);
            circleRec.setProgress((int) Math.abs(progress * 100));
            iv_rec_status.setImageResource(R.drawable.hx_full_rotate_loading);

            if (type == ChatMsg.MsgType.BIG_FILE.ordinal()) {
                tv_progress_rec.setText(mContext.getString(R.string.hx_call_full_view_file_receiving));
            } else if (type == ChatMsg.MsgType.PICTURE.ordinal()) {
                tv_progress_rec.setText(mContext.getString(R.string.hx_call_full_view_picture_receiving));
            } else if (type == ChatMsg.MsgType.LOCATION.ordinal()) {
                tv_progress_rec.setText(mContext.getString(R.string.hx_call_full_view_location_receiving));
            } else if (type == ChatMsg.MsgType.VIDEO.ordinal()) {
                tv_progress_rec.setText(mContext.getString(R.string.hx_call_full_view_video_receiving));
            } else if (type == ChatMsg.MsgType.AUDIO.ordinal()) {
                fl_im_receive_progress.setVisibility(View.GONE);
            } else if (type == ChatMsg.MsgType.TEXT.ordinal()) {
                tv_progress_rec.setText(mContext.getString(R.string.hx_call_full_view_receiving));
            } else {
                tv_progress_rec.setText(mContext.getString(R.string.hx_call_full_view_receiving));
            }
        }

        @Override
        public void onImSuccess(final int type, final FileBean bean) {
            tv_progress_rec.setText(mContext.getString(R.string.hx_call_full_view_receive_success));
            circleRec.setProgress(95);
            if (type != ChatMsg.MsgType.TEXT.ordinal()) {
                mIMCount++;
            }

            iv_rec_video_play_icon.setVisibility(View.GONE); //视频播放icon

            if (type == ChatMsg.MsgType.BIG_FILE.ordinal()) {
                fl_im_receive_progress.setVisibility(View.VISIBLE);
                rl_rec_file_parent.setVisibility(View.VISIBLE);
                iv_rec_pic.setVisibility(View.GONE);
                ll_rec_location.setVisibility(View.GONE);
                gf_rec_emo.setVisibility(View.GONE);
                fl_rec_video_parent.setVisibility(View.GONE);

                iv_rec_file.setImageResource(IMHelper.getFileImgRes(bean.getFileName(), true));
                tv_rec_file_name.setText(bean.getFileName());
                tv_rec_file_size.setText(IMHelper.convertFileSize(Long.valueOf(bean.getFileLength())));

                rl_rec_file_parent.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, IMFilePreviewActivity.class);
                        intent.putExtra(IMFilePreviewActivity.IM_FILE_BEAN, bean.getCacheMsgBean());
                        intent.putExtra(IMFilePreviewActivity.FULL_FILE_BEAN, bean);
                        intent.putExtra(IMFilePreviewActivity.FULL_VIEW_FILE, true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        mContext.startActivity(intent);
                    }
                });

                if (!bean.isJumpFile()) {
                    recFile.add(bean);
                }

            } else if (type == ChatMsg.MsgType.PICTURE.ordinal()) {

                //2017.9.9
                ll_send_progress.setVisibility(View.VISIBLE);
                vv_send_bg.setVisibility(View.VISIBLE);

                fl_im_receive_progress.setVisibility(View.VISIBLE);
                iv_rec_pic.setVisibility(View.VISIBLE);
                ll_rec_location.setVisibility(View.GONE);
                rl_rec_file_parent.setVisibility(View.GONE);
                gf_rec_emo.setVisibility(View.GONE);
                fl_rec_video_parent.setVisibility(View.GONE);

                Glide.with(mContext)
                        .load(bean.getOriginPath() + "?imageView2/0/w/400/h/300&t=")
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop())
                        .into(iv_rec_pic);

                iv_rec_pic.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, CropImageActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("isImageUrl", bean.getOriginPath());
                        mContext.startActivity(intent);
                        fl_im_receive_progress.setVisibility(View.GONE);
                    }
                });

                recPic.add(bean);

            } else if (type == ChatMsg.MsgType.LOCATION.ordinal()) {
                fl_im_receive_progress.setVisibility(View.VISIBLE);
                iv_rec_pic.setVisibility(View.VISIBLE);
                ll_rec_location.setVisibility(View.VISIBLE);
                rl_rec_file_parent.setVisibility(View.GONE);
                gf_rec_emo.setVisibility(View.GONE);
                fl_rec_video_parent.setVisibility(View.GONE);

                String[] split = bean.getAddress().split(":");

                if (split.length > 0) {
                    tv_rec_location1.setText(split[0]);
                }
                if (split.length > 1) {
                    tv_rec_location2.setText(split[1]);
                }

                Glide.with(mContext)
                        .load(bean.getMapUrl())
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop())
                        .into(iv_rec_pic);

                final String location = bean.getLongitude() + "," + bean.getLatitude();
                iv_rec_pic.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (null != CropMapActivity.mapActivity) {
                            CropMapActivity.mapActivity.finish();
                        }

                        Intent intent = new Intent();
                        intent.setClass(mContext, CropMapActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("location", location);
                        intent.putExtra("labelAddress", bean.getAddress());
                        mContext.startActivity(intent);
                        fl_im_receive_progress.setVisibility(View.GONE);
                    }
                });

                recLocation.add(bean);

            } else if (type == ChatMsg.MsgType.TEXT.ordinal()
                    || type == ChatMsg.MsgType.EMO_TEXT.ordinal()) {
                fl_im_receive_progress.setVisibility(View.GONE);

                String textContent = bean.getTextContent();
                if (new EmoInfo(mContext).isEmotion(textContent)) {
                    List<EmoInfo.EmoItem> dataList = new EmoInfo(mContext).getEmoList();
                    for (EmoInfo.EmoItem emoItem : dataList) {
                        String emoStr = emoItem.getEmoStr();
                        if (emoStr.equals(bean.getTextContent())) {
                            try {
                                MEmoPopWindow emoPopWindow = new MEmoPopWindow(mContext, emoItem.getEmoRes(), "-100");
                                emoPopWindow.showAtLocation(floatView, Gravity.CENTER, 0, 0);

                                //HuxinSdkManager.instance().playSound(emoItem.getSoundId());
                                Vibrator vibrator = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
                                long[] v = new long[]{0, 300, 500, 700};
                                vibrator.vibrate(v, -1);

                                if (emoPopWindow != null) {
                                    final MEmoPopWindow finalEmoPopWindow = emoPopWindow;
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_FULL) {
                                                if (finalEmoPopWindow.isShowing()
                                                        && floatView != null
                                                        && floatView.getParent() != null) {
                                                    finalEmoPopWindow.dismiss();
                                                }
                                            }
                                        }
                                    }, 2500);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                } else if (bean.getTextContent().startsWith("/")) {
                    try {
                        MEmoPopWindow emoPopWindow = new MEmoPopWindow(mContext, -1, textContent);
                        emoPopWindow.showAtLocation(floatView, Gravity.CENTER, 0, 0);

                        Vibrator vibrator = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
                        long[] v = new long[]{0, 300, 500, 700};
                        vibrator.vibrate(v, -1);

                        if (emoPopWindow != null) {
                            final MEmoPopWindow finalEmoPopWindow = emoPopWindow;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_FULL) {
                                        if (finalEmoPopWindow.isShowing()
                                                && floatView != null
                                                && floatView.getParent() != null) {
                                            finalEmoPopWindow.dismiss();
                                        }
                                    }
                                }
                            }, 3000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (type == ChatMsg.MsgType.VIDEO.ordinal()) {
                fl_rec_video_parent.setVisibility(View.VISIBLE);
                iv_rec_video_play_icon.setVisibility(View.VISIBLE);

                fl_im_receive_progress.setVisibility(View.VISIBLE);
                iv_rec_status.setVisibility(View.GONE);
                tv_progress_rec.setVisibility(GONE);
                rl_rec_file_parent.setVisibility(View.GONE);
                iv_rec_pic.setVisibility(View.GONE);
                ll_rec_location.setVisibility(View.GONE);
                gf_rec_emo.setVisibility(View.GONE);

                Glide.with(mContext)
                        .load(bean.getVideoPFidUrl())
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop())
                        .into(iv_rec_video);

                iv_rec_video.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        intent.setClass(mContext, CropVideoActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("isVideoUrl", bean.getVideoFidUrl());
                        mContext.startActivity(intent);

                        fl_im_receive_progress.setVisibility(View.GONE);
                    }
                });

            } else if (type == ChatMsg.MsgType.AUDIO.ordinal()
                    || type == ChatMsg.MsgType.REMARK.ordinal()
                    || type == ChatMsg.MsgType.BIZCARD.ordinal()) {
                fl_im_receive_progress.setVisibility(View.GONE);
                fl_rec_video_parent.setVisibility(View.GONE);
                rl_rec_file_parent.setVisibility(View.GONE);
                iv_rec_pic.setVisibility(View.GONE);
                ll_rec_location.setVisibility(View.GONE);
                gf_rec_emo.setVisibility(View.GONE);
                return;
            } else if (type == ChatMsg.MsgType.JOKE_TEXT.ordinal()) {
                ll_jokes_receive_parent.setVisibility(View.VISIBLE);
                tv_jokes_receive_from.setText("来自" + HuxinSdkManager.instance().getContactName(dstPhone) + "的段子");
                tv_jokes_receive.setText(bean.getTextContent().replace(CacheMsgJoke.JOKES, ""));
            }

            if (type != ChatMsg.MsgType.TEXT.ordinal()) { //文本
                fl_im_receive_progress.clearAnimation();
                fl_im_receive_progress.setScaleX(1);
                fl_im_receive_progress.setScaleY(1);
                fl_im_receive_progress.setAlpha(1.0f);
                fl_im_receive_progress.setTranslationX(-0.0f);
                fl_im_receive_progress.setTranslationY(0.0f);
                cancelMultiAnim(fl_im_receive_progress);

                new Handler().postDelayed(new Runnable() { //延时3秒启动动画
                    @Override
                    public void run() {
                        ViewAnimUtils.scaleRedTip(full_msg_tip, 1000);
                        if (!isIMFirst) {
                            ViewAnimUtils.alphaAndScaleAnim(rl_full_jump_msg, 1000);
                            isIMFirst = true;
                        }
                        if (type != ChatMsg.MsgType.TEXT.ordinal()) {
                            --mIMCount;
                            if (mIMCount == 0) {
                                multiAnim(mContext, fl_im_receive_progress, 300);
                            }
                        }
                        LogUtils.e("mm", "mIMCount = " + mIMCount);
                    }
                }, 4700);
            }

            circleRec.setProgress(100);
            circleRec.setVisibility(View.GONE);
            if (type == ChatMsg.MsgType.BIG_FILE.ordinal()) { //对文件特殊处理
                boolean isFileExist = new File(FileConfig.getBigFileDownLoadPath(), bean.getFileName()).exists();
                if (isFileExist) {
                    iv_rec_status.setImageResource(R.drawable.hx_full_success);
                    tv_progress_rec.setText(mContext.getString(R.string.hx_call_full_view_receive_file_see));
                } else {
                    iv_rec_status.setImageResource(R.drawable.hx_full_file_download);
                    tv_progress_rec.setText(mContext.getString(R.string.hx_call_full_view_receive_file_download));
                }
            } else {
                iv_rec_status.setImageResource(R.drawable.hx_full_success);
            }

            stopRecAnimation(iv_rec_status);

            if (isSendTo) {
                isRecComing = true;
                isSendTo = false;
            }
        }

        @Override
        public void onImFail(int type, FileBean fileBean) {
            if (null == fileBean) {
                LogUtils.e("xx", "fail receive type = " + type + "; fileBean is null!");
            }
            tv_progress_rec.setText(mContext.getString(R.string.hx_call_full_view_receive_fail));
            fl_im_receive_progress.setVisibility(View.VISIBLE);
            iv_rec_status.setImageResource(R.drawable.hx_full_fail_retry);
            stopRecAnimation(iv_rec_status);
            circleRec.setVisibility(View.GONE);
        }
    };

    /**
     * 重发图片
     *
     * @param fileBean
     */
    private void postPicture(FileBean fileBean) {

        if (!CommonUtils.isNetworkAvailable(mContext)) {
            ToastUtil.showToast(mContext, mContext.getString(R.string.hx_toast_50));
            return;
        }

        if (null == fileBean) {
            Toast.makeText(mContext, mContext.getString(R.string.hx_toast_69), Toast.LENGTH_SHORT).show();
            return;
        }

        HuxinSdkManager.instance().postPicture(fileBean.getUserId(),
                fileBean.getDstPhone(),
                fileBean.getFile(),
                fileBean.getOriginPath(),
                true,
                FileSendListenerImpl.getListener());
    }

    /**
     * 重发发送位置
     */
    public void postLocation(final FileBean fileBean) {

        if (!CommonUtils.isNetworkAvailable(mContext)) {
            ToastUtil.showToast(mContext, mContext.getString(R.string.hx_toast_50));
            return;
        }

        if (null == fileBean) {
            Toast.makeText(mContext, mContext.getString(R.string.hx_toast_69), Toast.LENGTH_SHORT).show();
            return;
        }

        final IFileSendListener listener = FileSendListenerImpl.getListener();

        final String url = "http://restapi.amap.com/v3/staticmap?location="
                + fileBean.getLongitude() + "," + fileBean.getLatitude() + "&zoom=" + fileBean.getZoomLevel()
                + "&size=600*400&traffic=1&markers=mid,0xff0000,A:" + fileBean.getLongitude()
                + "," + fileBean.getLatitude() + "&key=" + AppConfig.staticMapKey;

        final int userId = HuxinSdkManager.instance().getUserId();

        if (null != listener) {
            if (CommonUtils.isNetworkAvailable(mContext)) {
                listener.onProgress(ChatMsg.MsgType.LOCATION.ordinal(), 0.10, "location");
            } else {
                listener.onImFail(ChatMsg.MsgType.LOCATION.ordinal(), fileBean);
            }
        }

        /*************IM*************/

        //todo_k: 地图
        final CacheMsgBean cacheMsgBean = new CacheMsgBean()
                .setMsgTime(System.currentTimeMillis())
                .setSend_flag(-1)
                .setSenderPhone(HuxinSdkManager.instance().getPhoneNum())
                .setSenderUserId(userId)
                .setReceiverPhone(dstPhone)
                .setMsgType(CacheMsgBean.MSG_TYPE_MAP)
                .setJsonBodyObj(new CacheMsgMap().setLocation(fileBean.getLongitude() + "," + fileBean.getLatitude()).setAddress(fileBean.getAddress()).setImgUrl(url))
                .setRightUI(true);

        IMMsgManager.getInstance().addCacheMsgBean(cacheMsgBean);

        if (null != listener) {
            if (CommonUtils.isNetworkAvailable(mContext)) {
                listener.onProgress(ChatMsg.MsgType.LOCATION.ordinal(), 0.45, "location");
            } else {
                listener.onImFail(ChatMsg.MsgType.LOCATION.ordinal(), fileBean);
            }
        }
        final String content = fileBean.getLongitude() + "" + fileBean.getLatitude();

        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                    long msgId = ack.getMsgId();
                    cacheMsgBean.setMsgId(msgId);

                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        if (ack.getIsTargetOnline()) {
                            if (null != listener) {
                                listener.onProgress(ChatMsg.MsgType.LOCATION.ordinal(), 1.00, "location");
                                listener.onImSuccess(ChatMsg.MsgType.LOCATION.ordinal(), fileBean);
                            }
                            //Toast.makeText(mContext, mContext.getString(R.string.hx_toast_30), Toast.LENGTH_SHORT).show();
                            cacheMsgBean.setSend_flag(0);
                            //add to db
                            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                        } else {
                            HttpPushManager.pushMsgForLocation(userId, dstPhone,
                                    fileBean.getLongitude(), fileBean.getLatitude(),
                                    fileBean.getZoomLevel(), fileBean.getAddress(),
                                    new HttpPushManager.PushListener() {
                                        @Override
                                        public void success(String msg) {
                                            LogUtils.w(TAG, msg);
                                            if (null != listener) {
                                                listener.onProgress(ChatMsg.MsgType.LOCATION.ordinal(), 1.00, "location");
                                                listener.onImSuccess(ChatMsg.MsgType.LOCATION.ordinal(), fileBean);
                                            }
                                            //Toast.makeText(mContext, mContext.getString(R.string.hx_toast_30), Toast.LENGTH_SHORT).show();
                                            cacheMsgBean.setSend_flag(0);
                                            //add to db
                                            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                                        }

                                        @Override
                                        public void fail(String msg) {
                                            //add to db
                                            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                                        }
                                    });
                        }
                    } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_NOT_HUXIN_USER) {
                        HuxinSdkManager.instance().showNotHuxinUser2(dstPhone, SendSmsActivity.SEND_LOCATION, msgId, cacheMsgBean);
                        //add to db
                        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                    } else {
                        String log = "ErrerNo:" + ack.getErrerNo();
                        Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
                        LogFile.inStance().toFile(log);

                        if (null != listener) {
                            listener.onImFail(ChatMsg.MsgType.LOCATION.ordinal(), fileBean);
                        }
                        //add to db
                        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        HuxinSdkManager.instance().sendLocation(userId, dstPhone,
                fileBean.getLongitude(), fileBean.getLatitude(),
                fileBean.getZoomLevel(), fileBean.getAddress(), callback);
    }

    /**
     * 重发文件
     *
     * @param fileBean
     */
    private void postFile(final FileBean fileBean) {

        if (!CommonUtils.isNetworkAvailable(mContext)) {
            ToastUtil.showToast(mContext, mContext.getString(R.string.hx_toast_50));
            return;
        }

        if (null == fileBean) {
            Toast.makeText(mContext, mContext.getString(R.string.hx_toast_69), Toast.LENGTH_SHORT).show();
            return;
        }

        final int userId = HuxinSdkManager.instance().getUserId();

        final IFileSendListener listener = FileSendListenerImpl.getListener();

        //todo_k: 文件
        final CacheMsgFile cacheMsgFile = new CacheMsgFile()
                .setFilePath(fileBean.getFile().getAbsolutePath())
                .setFileSize(Long.valueOf(fileBean.getFileLength()))
                .setFileName(fileBean.getFileName())
                .setFileRes(IMHelper.getFileImgRes(fileBean.getFileName(), false));
        final CacheMsgBean cacheMsgBean = new CacheMsgBean()
                .setMsgTime(System.currentTimeMillis())
                .setSend_flag(-1)
                .setSenderPhone(HuxinSdkManager.instance().getPhoneNum())
                .setSenderUserId(userId)
                .setReceiverPhone(dstPhone)
                .setMsgType(CacheMsgBean.MSG_TYPE_FILE)
                .setJsonBodyObj(cacheMsgFile)
                .setRightUI(true);

        //add to db
        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
        IMMsgManager.getInstance().addCacheMsgBean(cacheMsgBean);

        UpProgressHandler upProgressHandler = new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                LogUtils.e("xx", "qiniu key = " + key + " ; file percent = " + percent);
                if (null != listener) {
                    listener.onProgress(ChatMsg.MsgType.BIG_FILE.ordinal(), percent, "file");
                }
            }
        };

        //发送
        HuxinSdkManager.instance().postBigFile(userId, dstPhone, fileBean.getFile(),
                fileBean.getFileName(), fileBean.getFileLength(), upProgressHandler,
                true, listener);
    }

    public void setAnimationScale(PathPoint p) {
        fl_hook_im_picture.setAlpha(1);
        fl_hook_im_picture.setVisibility(View.VISIBLE);
        if (p.getAlpha() > 0.7f) {
            fl_hook_im_picture.setScaleX(p.getAlpha());
            fl_hook_im_picture.setScaleY(p.getAlpha());
        }
    }

    public void setAnimationStart(PathPoint p) {
        fl_hook_im_picture.setTranslationX(p.getX());
        fl_hook_im_picture.setTranslationY(p.getY());
    }

    public void setAnimationReset(PathPoint p) {
        fl_hook_im_picture.setTranslationX(p.getX());
        fl_hook_im_picture.setTranslationY(p.getY());
        fl_hook_im_picture.setScaleX(1);
        fl_hook_im_picture.setScaleY(1);
        fl_hook_im_picture.setVisibility(View.GONE);
    }

    public void setAnimJoke(PathPoint p) {
        ll_jokes_send_parent.setTranslationY(p.getY());
    }

    public void setAnimJokeReset(PathPoint p) {
        ll_jokes_send_parent.setTranslationY(p.getY());
        ll_jokes_send_parent.setVisibility(View.GONE);
    }

    /**
     * CallFullView 聊天界面  trans、alpha、scale - 动画缩进右上角
     * Data: start by 2017.9.7
     *
     * @param context
     * @param view
     * @param delay
     */
    private AnimatorSet set;
    private ObjectAnimator transXAnim, transYAnim, alphaAnim, scaleXAnim, scaleYAnim;

    public void multiAnim(Context mContext, View view, int delay) {
        set = new AnimatorSet();
        if (ScreenUtils.getSmallWidthDPI(mContext) >= 411.0f) {
            transXAnim = ObjectAnimator.ofFloat(view, "translationX", 0f,
                    DisplayUtil.dip2px(mContext, 140.0f), DisplayUtil.dip2px(mContext, 160.0f));
            transYAnim = ObjectAnimator.ofFloat(view, "translationY", 0f,
                    DisplayUtil.dip2px(mContext, -220.0f), DisplayUtil.dip2px(mContext, -240.0f));
        } else {
            transXAnim = ObjectAnimator.ofFloat(view, "translationX", 0f,
                    DisplayUtil.dip2px(mContext, 135.0f), DisplayUtil.dip2px(mContext, 155.0f));//140
            transYAnim = ObjectAnimator.ofFloat(view, "translationY", 0f,
                    DisplayUtil.dip2px(mContext, -205.0f), DisplayUtil.dip2px(mContext, -225.0f));//220
        }
        alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.75f, 0.5f, 0.25f, 0.0f);
        scaleXAnim = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.15f, 0.0f);
        scaleYAnim = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.15f, 0.0f);

        set.playTogether(transXAnim, transYAnim, alphaAnim, scaleXAnim, scaleYAnim);
        set.setDuration(delay);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (fl_im_receive_progress != null) {
                    fl_im_receive_progress.setVisibility(View.GONE); //fix 接收的信息框隐藏不给点击
                }
            }
        });
        set.start();
    }

    public void cancelMultiAnim(View view) {
        if (set == null) {
            return;
        }
        if (transXAnim.isStarted()) {
            transXAnim.end();
            transXAnim.cancel();
        }
        if (transYAnim.isStarted()) {
            transYAnim.end();
            transYAnim.cancel();
        }
        if (alphaAnim.isStarted()) {
            alphaAnim.end();
            alphaAnim.cancel();
        }
        if (scaleXAnim.isStarted()) {
            scaleXAnim.end();
            scaleXAnim.cancel();
        }
        if (scaleYAnim.isStarted()) {
            scaleYAnim.end();
            scaleYAnim.cancel();
        }
        if (set.isStarted()) {
            set.end();
            set.cancel();
        }

        view.setScaleX(1);
        view.setScaleY(1);
        view.setAlpha(1.0f);
        view.setTranslationX(-0.0f);
        view.setTranslationY(0.0f);
        view.clearAnimation();
    }

    /**
     * CallFullView 聊天界面  rotation start by 2017.9.7
     **/
    private float currentValue;
    private ObjectAnimator mRotateAnim;
    private float currentValueRec;
    private ObjectAnimator mRotateAnimRec;

    /**
     * 发送开始动画
     *
     * @param view
     * @param delay
     */
    public void startAnimation(View view, int delay) {
        mRotateAnim = ObjectAnimator.ofFloat(view, "Rotation", currentValue - 360, currentValue);
        mRotateAnim.setDuration(delay);
        mRotateAnim.setRepeatCount(ObjectAnimator.INFINITE);
        mRotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //监听动画执行的位置，以便下次开始时，从当前位置开始
                currentValue = (float) animation.getAnimatedValue();
            }
        });
        mRotateAnim.start();
    }

    /**
     * 停止动画
     */
    public void stopAnimation(View view) {
        mRotateAnim.end();
        mRotateAnim.cancel();
        view.clearAnimation();
        currentValue = 0;// 重置起始位置
    }

    /**
     * 接收开始动画
     *
     * @param view
     * @param delay
     */
    public void startRecAnimation(View view, int delay) {
        mRotateAnimRec = ObjectAnimator.ofFloat(view, "Rotation", currentValueRec - 360, currentValueRec);
        mRotateAnimRec.setDuration(delay);
        mRotateAnimRec.setRepeatCount(ObjectAnimator.INFINITE);
        mRotateAnimRec.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //监听动画执行的位置，以便下次开始时，从当前位置开始
                currentValueRec = (float) animation.getAnimatedValue();
            }
        });
        mRotateAnimRec.start();
    }

    /**
     * 停止动画
     */
    public void stopRecAnimation(View view) {
        mRotateAnimRec.end();
        mRotateAnimRec.cancel();
        view.clearAnimation();
        currentValueRec = 0;// 重置起始位置
    }

    /**
     * 发送文件.
     *
     * @param file
     */
    public void handleSendFile(final File file) {
        final int userId = HuxinSdkManager.instance().getUserId();
        final IFileSendListener listener = FileSendListenerImpl.getListener();
        //发送 userId, desPhone, fileId, fileName, fileSize, receiveListener
        HuxinSdkManager.instance().postBigFile(userId, dstPhone, file,
                file.getName(), file.length() + "",
                new UpProgressHandler() {
                    @Override
                    public void progress(String key, double percent) {
                        if (null != listener) {
                            listener.onProgress(ChatMsg.MsgType.BIG_FILE.ordinal(), percent, "file");
                        }
                    }
                }, true, listener);
    }

    @Override
    public void onRefresh(ArrayList<String> paths, int resultCode) {
        Log.e("YW", "onrefresh" + "\tresultCode: " + resultCode);

        if (paths != null && paths.size() > 0 && resultCode == FilePickerConst.CALL_REQUEST_CALLBACK) {
            final File file = new File(paths.get(0));
            if (file.exists()) {
                if (file.length() > MAX_SENDER_FILE) {
                    Toast.makeText(mContext, R.string.hx_imadapter_file, Toast.LENGTH_SHORT).show();
                } else {
                    if (!CommonUtils.isNetworkAvailable(mContext)) {
                        ToastUtil.showToast(mContext, mContext.getString(R.string.hx_imadapter_wifi_break));
                        return;
                    }

                    if (!IMHelper.isWifi(mContext)) {
                        Toast.makeText(mContext, mContext.getString(R.string.hx_toast_67), Toast.LENGTH_SHORT).show();
                    }
                    handleSendFile(file);
                }
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.hx_toast_22), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //2G/3G/4G网加载视频show回调
    @Override
    public void onLoadClick(final String filePath, final String absolutePath) {

        NoWifiLoadVideoPopWindow popWindow = new NoWifiLoadVideoPopWindow(mContext);
        if (popWindow != null && popWindow.isShowing()) {
            return;
        }
        popWindow.showAtLocation(floatView, Gravity.CENTER, 0, 0);

        popWindow.setLoadListener(new NoWifiLoadVideoPopWindow.OnCompletionListener() {
            @Override
            public void onLoading() {
                if (!CommonUtils.isNetworkAvailable(mContext)) {
                    ToastUtil.showToast(mContext, "下载失败，请检查网络");
                    return;
                }
                progress_round_bar.setVisibility(View.VISIBLE);
                progress_round_bar.setProgress(0);
                progress_round_bar.setTextSize();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mRVAdapter.setReplayIcon();
                        final String path = mRVAdapter.downloadFile(filePath, absolutePath, progress_round_bar);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progress_round_bar.setVisibility(View.GONE);
                                mRVAdapter.setWithMobileConnect(path);
                            }
                        });
                    }
                }).start();
            }
        });
    }

    /**
     * Function view
     */
    private List<CallFunctionItem> mItemList = new ArrayList<>();
    private RelativeLayout hook_top_box;
    private FunctionPageView recycler_view;
    private FunctionIndicator page_indicator;
    private FunctionPageView.PageAdapter mFunctionAdapter;

    private void initPager() {
        mFunctionAdapter = recycler_view.new PageAdapter(mItemList, new FunctionPageView.CallBack() {

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.hx_call_function_item, parent, false);
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                CallFunctionItem item = mItemList.get(position);

                if (item.getIcon().equals(CallFunctionViewUtils.ITEM_ONE)) { //位置
                    ((ViewHolder) holder).tv_name.setText(mContext.getString(R.string.hx_show_full_setting_info_send_location));
                    ((ViewHolder) holder).iv_icon.setImageResource(R.drawable.hx_btn_full_location);
                } else if (item.getIcon().equals(CallFunctionViewUtils.ITEM_TWO)) { //图片
                    ((ViewHolder) holder).tv_name.setText(mContext.getString(R.string.hx_show_full_setting_info_send_pic));
                    ((ViewHolder) holder).iv_icon.setImageResource(R.drawable.hx_btn_full_picture);
                } else if (item.getIcon().equals(CallFunctionViewUtils.ITEM_THREE)) { //表情
                    ((ViewHolder) holder).tv_name.setText(mContext.getString(R.string.hx_show_full_setting_info_send_emoji));
                    ((ViewHolder) holder).iv_icon.setImageResource(R.drawable.hx_btn_full_emo);
                } else if (item.getIcon().equals(CallFunctionViewUtils.ITEM_ELEVEN)) { //文件
                    ((ViewHolder) holder).tv_name.setText(mContext.getString(R.string.hx_show_full_setting_info_send_file));
                    ((ViewHolder) holder).iv_icon.setImageResource(R.drawable.hx_btn_full_file);
                } else if (item.getIcon().equals(CallFunctionViewUtils.ITEM_TWELVE)) { //拍照
                    ((ViewHolder) holder).tv_name.setText(mContext.getString(R.string.hx_show_full_setting_info_send_camera));
                    ((ViewHolder) holder).iv_icon.setImageResource(R.drawable.hx_btn_full_camera);
                } else if (item.getIcon().equals(CallFunctionViewUtils.ITEM_FOUR)) { //段子
                    ((ViewHolder) holder).tv_name.setText(mContext.getString(R.string.hx_show_full_setting_info_shake));
                    ((ViewHolder) holder).iv_icon.setImageResource(R.drawable.hx_btn_full_jokes);
                } else if (item.getIcon().equals(CallFunctionViewUtils.ITEM_WHITE_BOARD)) { //白板
                    ((ViewHolder) holder).tv_name.setText(mContext.getString(R.string.hx_show_full_setting_info_whiteBoard));
                    ((ViewHolder) holder).iv_icon.setImageResource(R.drawable.hx_btn_full_whiteboard);
                } else if (item.getIcon().equals(CallFunctionViewUtils.ITEM_THIRTEEN)) { //背景音
                    ((ViewHolder) holder).tv_name.setText(mContext.getString(R.string.hx_str_full_call_background_sound));
                    ((ViewHolder) holder).iv_icon.setImageResource(R.drawable.hx_btn_full_bgsound);
                } else if (item.getIcon().equals(CallFunctionViewUtils.ITEM_100)) { //扫码开门
                    ((ViewHolder) holder).tv_name.setText(mContext.getString(R.string.open_door));
                    ((ViewHolder) holder).iv_icon.setImageResource(R.drawable.cgj_after_opendoor_selector);
                } else if (item.getIcon().equals(CallFunctionViewUtils.ITEM_101)) { //意见反馈
                    ((ViewHolder) holder).tv_name.setText(mContext.getString(R.string.hx_feedbace));
                    ((ViewHolder) holder).iv_icon.setImageResource(R.drawable.cgj_after_feeback_selector);
                } else if (item.getIcon().equals(CallFunctionViewUtils.ITEM_102)) { //添加业主
                    ((ViewHolder) holder).tv_name.setText(mContext.getString(R.string.add_owner));
                    ((ViewHolder) holder).iv_icon.setImageResource(R.drawable.cgj_after_addowner_selector);
                } else { //H5
                    if (item != null && item.getType().equals(TYPE_TWO)) {
                        ((ViewHolder) holder).tv_name.setText(item.getName());
                        switch (item.getIcon()) {
                            case CallFunctionViewUtils.ITEM_FIVE:
                                ((ViewHolder) holder).iv_icon.setImageResource(R.drawable.hx_full_item_05);
                                break;
                            case CallFunctionViewUtils.ITEM_SIX:
                                ((ViewHolder) holder).iv_icon.setImageResource(R.drawable.hx_full_item_06);
                                break;
                            case CallFunctionViewUtils.ITEM_SEVEN:
                                ((ViewHolder) holder).iv_icon.setImageResource(R.drawable.hx_full_item_07);
                                break;
                            case CallFunctionViewUtils.ITEM_EIGHT:
                                ((ViewHolder) holder).iv_icon.setImageResource(R.drawable.hx_full_item_08);
                                break;
                            case CallFunctionViewUtils.ITEM_NINE:
                                ((ViewHolder) holder).iv_icon.setImageResource(R.drawable.hx_full_item_09);
                                break;
                            case CallFunctionViewUtils.ITEM_TEN:
                                ((ViewHolder) holder).iv_icon.setImageResource(R.drawable.hx_full_item_10);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            @Override
            public void onItemClickListener(View view, int position) {

                CallFunctionItem item = mItemList.get(position);
                if (item.getIcon().equals(CallFunctionViewUtils.ITEM_ONE)) { //位置
                    jumpLocation();
                    iv_finger.setVisibility(View.GONE);
                } else if (item.getIcon().equals(CallFunctionViewUtils.ITEM_TWO)) { //图片
                    jumpPicture();
                    iv_finger.setVisibility(View.GONE);
                } else if (item.getIcon().equals(CallFunctionViewUtils.ITEM_THREE)) { //表情
                    popEmo();
                    iv_finger.setVisibility(View.GONE);
                } else if (item.getIcon().equals(CallFunctionViewUtils.ITEM_ELEVEN)) { //文件
                    jumpFile();
                    iv_finger.setVisibility(View.GONE);
                } else if (item.getIcon().equals(CallFunctionViewUtils.ITEM_TWELVE)) { //拍照
                    jumpCamera();
                    iv_finger.setVisibility(View.GONE);
                } else if (item.getIcon().equals(CallFunctionViewUtils.ITEM_FOUR)) { //段子
                    popJokes();
                    iv_finger.setVisibility(View.GONE);
                } else if (item.getIcon().equals(CallFunctionViewUtils.ITEM_WHITE_BOARD)) { //白板
                    popWhiteBoard();
                    iv_finger.setVisibility(View.GONE);
                } else if (item.getIcon().equals(CallFunctionViewUtils.ITEM_THIRTEEN)) { //背景音
                    iv_finger.setVisibility(View.GONE);
                } else if (item.getIcon().equals(CallFunctionViewUtils.ITEM_100)) { //扫码开门
                    String activityClass = ThirdBizHelper.readThirdBizActivity(item.getData());
                    ThirdActivityHelper.openDoor(mContext, item.getData(), activityClass);
                } else if (item.getIcon().equals(CallFunctionViewUtils.ITEM_101)) { //意见反馈
                    ThirdActivityHelper.feedBackActivity(mContext);
                } else if (item.getIcon().equals(CallFunctionViewUtils.ITEM_102)) { //添加业主
                    ThirdActivityHelper.selectCommunityActivity(mContext, dstPhone);
                } else { //H5
                    //自定义按钮
                    if (item != null && item.getType().equals(TYPE_TWO)) {
                        //外链功能
                        Intent intent = new Intent();
                        intent.putExtra(WebViewActivity.INTENT_TITLE, item.getName());
                        intent.putExtra(WebViewActivity.INTENT_URL, item.getData());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(mContext, WebViewActivity.class);
                        mContext.startActivity(intent);
                    }
                }
            }
        });

        if (mItemList.size() <= 4) {
            page_indicator.setVisibility(View.GONE);
        } else {
            page_indicator.setVisibility(View.VISIBLE);
        }
        recycler_view.setIndicator(page_indicator);
        recycler_view.setPageMargin(DisplayUtil.dip2px(mContext, 10));
        recycler_view.setAdapter(mFunctionAdapter);

        judgeGuide();
        upToClick();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_icon;
        private TextView tv_name;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_item_img);
            tv_name = (TextView) itemView.findViewById(R.id.tv_item_name);
        }
    }


}
