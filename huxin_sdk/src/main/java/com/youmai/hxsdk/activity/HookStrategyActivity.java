package com.youmai.hxsdk.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.map.LocationActivity;
import com.youmai.hxsdk.interfaces.IFileSendListener;
import com.youmai.hxsdk.interfaces.impl.FileSendListenerImpl;
import com.youmai.hxsdk.module.filemanager.PickerManager;
import com.youmai.hxsdk.module.filemanager.activity.FileManagerActivity;
import com.youmai.hxsdk.module.filemanager.constant.FilePickerConst;
import com.youmai.hxsdk.module.photo.activity.PhotoActivity;
import com.youmai.hxsdk.sp.SPDataUtil;
import com.youmai.hxsdk.utils.AndroidBottomBarUtil;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.chat.ContentText;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.bean.ChatMsg;
import com.youmai.hxsdk.db.bean.HxUsers;
import com.youmai.hxsdk.db.bean.ShowData;
import com.youmai.hxsdk.db.bean.UIData;
import com.youmai.hxsdk.db.helper.HxUsersHelper;
import com.youmai.hxsdk.entity.CallInfo;
import com.youmai.hxsdk.entity.StrategyModel;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.cache.CacheMsgCall;
import com.youmai.hxsdk.interfaces.OnChatMsg;
import com.youmai.hxsdk.interfaces.bean.FileBean;
import com.youmai.hxsdk.recyclerview.page.FunctionIndicator;
import com.youmai.hxsdk.recyclerview.page.FunctionItem;
import com.youmai.hxsdk.recyclerview.page.FunctionPageView;
import com.youmai.hxsdk.recyclerview.page.FunctionViewUtils;
import com.youmai.hxsdk.recyclerview.page.StrategyAdapter;
import com.youmai.hxsdk.utils.AndroidBugWorkaround;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.CallRecordUtil;
import com.youmai.hxsdk.utils.DisplayUtil;
import com.youmai.hxsdk.utils.GlideCircleTransform;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.IntentQueryUtil;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.PhoneNumTypes;
import com.youmai.hxsdk.utils.RingUtils;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.utils.TimeUtils;
import com.youmai.hxsdk.view.CountImageView;
import com.youmai.hxsdk.view.camera.JCameraView;
import com.youmai.hxsdk.view.full.FloatViewUtil;
import com.youmai.hxsdk.view.headerview.HeaderScrollHelper;
import com.youmai.hxsdk.view.headerview.HeaderViewPager;
import com.youmai.thirdbiz.INotifyDataChanged;
import com.youmai.thirdbiz.ThirdActivityHelper;
import com.youmai.thirdbiz.ThirdBizHelper;
import com.youmai.thirdbiz.ThirdBizMgr;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2016.11.08 15:48
 * 描述：挂机策略
 */
public class HookStrategyActivity extends AppCompatActivity implements View.OnClickListener {

    public enum MsgType {
        TEXT, GIF_TEXT, PICTURE, AUDIO, VIDEO, URL, LOCATION, BEGIN_LOCATION, SHOW_PICTURE, SHOW_VIDEO, BIG_FILE, BIZCARD, REMARK
    }

    public static final String TAG = HookStrategyActivity.class.getSimpleName();
    public static final String IS_MOCALL = "IS_MOCALL";
    public static final String TALK_TIME = "TALK_TIME";
    public static final String DST_PHONE = "DST_PHONE";

    public static final String SHOW_NAME = "SHOW_NAME";
    public static final String CURRENT_SHOW = "CURRENT_SHOW"; //todo 当前秀
    public static final String REC_PICTURE_CLASSIFY = "rec_picture_classify";//rec picture map classify
    public static final String REC_LOCATION_CLASSIFY = "rec_location_classify";//rec location map classify
    public static final String REC_FILE_CLASSIFY = "rec_file_classify";//rec msg file classify

    public static final String PROVINCE = "province";//归属地 省份
    public static final String CITY = "city";//归属地 城市

    //private static final int HANDLER_REFRESH_NICKNAME = 10000;
    private static final int MSG_REC_SMS = 1002;//rec Handler sms classify

    private static final int REQUEST_CODE_CAMERA = 1003; //跳转拍摄的请求码
    private static final int GET_PERMISSION_REQUEST = 1004; //权限申请自定义码

    private boolean hasContact = false;//是否保存联系人 -- 是否交换名片
    private boolean hasCallInfo = false;

    private Context mContext;

    //type header info
    private ImageView mPhoneView;
    private TextView tv_hook_number;
    private TextView tv_hook_location;
    private TextView tv_hook_call_time;
    private FrameLayout fl_back_close;
    private CountImageView civ_message;
    private TextView tv_hook_finish_tip;

    //type show info
    private RelativeLayout rl_hook_top_box;//第三方

    //Activity最外层的Layout视图
    CountImageView messageImg;
    //private TextView noAnswerText;
    private ImageView headImg;
    private TextView tvItemCallTip;

    private ChatMsg mCurShow;//通话屏秀Bean
    private String mShowName;//通话屏名字
    private String province;
    private String city;

    private boolean isMOCall;//是否主叫
    private String mDSPhone; //对方的电话号码
    private long mTalkTime; //通话时长
    private CacheMsgCall callModel;

    private AppBarLayout hook_appbar;//appbar
    private LinearLayout hook_header;//appbar

    private String showNickName;
    private int contact_id = 0;

    private int unreadCount = 0;//留言未读消息
    private BroadcastReceiver mReceiveSmsMsg;

    private MSGHandler mMSGHandler;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.hook_common_head_img) {
        } else if (id == R.id.fl_back_close) {
            // 跳呼信主界面
            onBackPressed();
        } else if (id == R.id.civ_message) {
            if (HuxinSdkManager.instance().showSdkLogin()) {
                return;
            }

            //IM消息
            Intent intent = new Intent();
            intent.setClass(mContext, IMConnectionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(IMConnectionActivity.DST_PHONE, mDSPhone);
            intent.putExtra(IMConnectionActivity.DST_SHOW, mCurShow);
            intent.putExtra(IMConnectionActivity.DST_NAME, showNickName);
            intent.putExtra(IMConnectionActivity.DST_CONTACT_ID, contact_id);
            intent.putExtra(IMConnectionActivity.IS_SHOW_AUDIO, false);
            intent.putExtra(IMConnectionActivity.ACTIVITY_COME_TYPE, 1);
            mContext.startActivity(intent);
            unreadCount = 0;
            civ_message.setCount(0);
            civ_message.setBackgroundResource(R.drawable.hx_call_after_messenger);
            tvItemCallTip.setVisibility(View.INVISIBLE);
            finish();
        }
    }


    private class MSGHandler extends Handler {
        private final WeakReference<HookStrategyActivity> mTarget;

        MSGHandler(HookStrategyActivity target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            HookStrategyActivity act = mTarget.get();
            LogUtils.e(Constant.SDK_UI_TAG, "消息：" + msg.toString());
            if (msg.what == MsgType.TEXT.ordinal()) { //文本
                // do nothing
            } else if (msg.what == MsgType.GIF_TEXT.ordinal()) { // gif
                // do nothing
            } else if (msg.what == MsgType.PICTURE.ordinal()) { //图片
                act.mPicCount = act.mPicCount + 1;
            } else if (msg.what == MsgType.LOCATION.ordinal()) { //地图
                act.mLocationCount = act.mLocationCount + 1;
            } else if (msg.what == MsgType.AUDIO.ordinal()) { //语音
                act.mAudioCount = act.mAudioCount + 1;
            } else if (msg.what == MsgType.BIG_FILE.ordinal()) { //大文件
                act.mFileCount = act.mFileCount + 1;
            } else if (msg.what == MsgType.BIZCARD.ordinal()) {
                // do nothing
            } else if (msg.what == MsgType.REMARK.ordinal()) {
                //do nothing
            } else if (msg.what == MSG_REC_SMS) {
                //do nothing
            } /*else if (msg.what == HANDLER_REFRESH_NICKNAME) {
                String nickName = (String) msg.obj;
                if (!StringUtils.isEmpty(nickName)) {
                    mShowName = nickName;
                    tv_hook_number.setText(mShowName);
                } else if (mCurShow != null
                        && !StringUtils.isEmpty(mCurShow.getName())) {
                    mShowName = mCurShow.getName();
                    tv_hook_number.setText(mShowName);
                }
            }*/
            act.setCount(++act.unreadCount, true);  //消息统计加1
        }
    }

    /**
     * 收到消息
     */
    private OnChatMsg onChatMsg = new OnChatMsg() {
        @Override
        public void onCallback(ChatMsg msg) {
            //IMMsgManager.getInstance().notifyMsg(msg);
            //IMMsgManager.getInstance().parseCharMsg(msg);

            Message message = mMSGHandler.obtainMessage();
            LogUtils.e(Constant.SDK_UI_TAG, "挂机消息来了：" + msg.toString());

            //拦截非当前通话号码发送来的消息
            String phoneNow = msg.getSrcPhone();
            if (phoneNow.equals(mDSPhone)) {
                if (msg.getMsgType() == ChatMsg.MsgType.TEXT) {

                    message.what = MsgType.TEXT.ordinal();
                    mMSGHandler.sendMessage(message);
                } else if (msg.getMsgType() == ChatMsg.MsgType.EMO_TEXT) {

                    message.what = MsgType.GIF_TEXT.ordinal();
                    mMSGHandler.sendMessage(message);
                } else if (msg.getMsgType() == ChatMsg.MsgType.PICTURE) {

                    message.what = MsgType.PICTURE.ordinal();
                    mMSGHandler.sendMessage(message);
                } else if (msg.getMsgType() == ChatMsg.MsgType.LOCATION) {

                    message.what = MsgType.LOCATION.ordinal();
                    mMSGHandler.sendMessage(message);
                } else if (msg.getMsgType() == ChatMsg.MsgType.AUDIO) {

                    message.what = MsgType.AUDIO.ordinal();
                    mMSGHandler.sendMessage(message);
                } else if (msg.getMsgType() == ChatMsg.MsgType.BIG_FILE) {

                    message.what = MsgType.BIG_FILE.ordinal();
                    mMSGHandler.sendMessage(message);
                } else if (msg.getMsgType() == ChatMsg.MsgType.BIZCARD) {
                    ContentText contentText = msg.getMsgContent().getText();
                    String cardStr = contentText.getContent();//获取vCard字符串内容
                    message.what = MsgType.BIZCARD.ordinal();
                    message.obj = cardStr;
                    mMSGHandler.sendMessage(message);
                } else if (msg.getMsgType() == ChatMsg.MsgType.REMARK) {
                    message.what = MsgType.REMARK.ordinal();
                    mMSGHandler.sendMessage(message);
                }
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT); //也可以设置成灰色透明的，比较符合Material Design的风格
        }

        setContentView(R.layout.hx_activity_hook_layout);
        mContext = this;
        mMSGHandler = new MSGHandler(this);

        registerHome(this);
        registerAddContactsOnBack();
        AndroidBugWorkaround.assistActivity(this);
        FunctionViewUtils.instance(mContext).clearItemList();

        initView();
        initShow();

        //loadRedPacket();
        setClickListener();
        initReceiver();

        HuxinSdkManager.instance().getStackAct().addActivity(this);
        ThirdBizMgr.getInstance().handleAferFloat(this, rl_hook_top_box, null);

        FileSendListenerImpl.setListener(send);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FloatViewUtil.instance().hideFloatView();

        mMSGHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    initData();
                    //setHeadImg();
                }
            }
        }, 300);

        //读取通话记录，以获取未接来电数,600ms确保弹屏已经关掉，防止个别手机系统会被弹屏界面遮住弹出权限询问窗（Vivo反面教材）
        if (!hasCallInfo) {
            mMSGHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!FloatViewUtil.instance().isFloatViewShow()) {
                        showCallInfo();
                        hasCallInfo = true;
                    }
                }
            }, 600);
        }

        moveFocus(headImg);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStart() {
        super.onStart();
        IMMsgManager.getInstance().registerChatMsg(onChatMsg);
    }

    @Override
    protected void onStop() {
        super.onStop();
        IMMsgManager.getInstance().unregisterChatMsg(onChatMsg);


        if (CallInfo.IsCalling()) {
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterHome(this);
        unRegisterAddContactsOnBack();
        HuxinSdkManager.instance().getStackAct().finishActivity(this);
        unregisterReceiver(mReceiveSmsMsg);  //短信刷新
        mMSGHandler.removeCallbacksAndMessages(null);
        if (!ListUtils.isEmpty(mItemList)) { //功能item
            mItemList.clear();
            FunctionViewUtils.instance(mContext).clearItemList();
        }
        //退出去掉回调
        ThirdBizMgr.getInstance().setNotifyChanged(null);
        //FileSendListenerImpl.setListener(null);
        System.gc();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA) {
            //拍照回来后
            if (resultCode == 101) {
                //图片
                String path = data.getStringExtra("filePath");
                sendCameraPic(path);
            } else if (resultCode == 102) {
                //视频
                String framePath = data.getStringExtra("framePath");
                String path = data.getStringExtra("filePath");
                long millisecond = data.getLongExtra("millisecond", 0L);
                sendCameraVideo(path, framePath, millisecond);
            } else if (resultCode == 103) {
                Toast.makeText(this, "相机有误，请返回重试!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendCameraPic(String path) {
        int userId = HuxinSdkManager.instance().getUserId();
        HuxinSdkManager.instance().postPicture(
                userId,
                mDSPhone,
                new File(path),
                path,
                true,
                send);
    }

    private void sendCameraVideo(String filePath, String framePath, long millisecond) {
        int userId = HuxinSdkManager.instance().getUserId();
        HuxinSdkManager.instance().postVideo(
                userId,
                mDSPhone,
                new File(filePath),
                filePath,
                framePath,
                millisecond,
                true,
                send);
    }

    /***
     * IM 消息 统计
     */
    private ArrayList<FileBean> recPicList = new ArrayList<>();
    private ArrayList<FileBean> recLocationList = new ArrayList<>();
    private ArrayList<FileBean> recFileList = new ArrayList<>();
    private int mPicCount, mLocationCount, mAudioCount, mFileCount;//统计

    private void initView() {
        isMOCall = getIntent().getBooleanExtra(IS_MOCALL, true);
        mTalkTime = getIntent().getLongExtra(TALK_TIME, 0L);
        mDSPhone = getIntent().getStringExtra(DST_PHONE);
        mDSPhone = PhoneNumTypes.formatPhoneNumber(mDSPhone);
        mShowName = getIntent().getStringExtra(SHOW_NAME);
        province = getIntent().getStringExtra(PROVINCE);
        city = getIntent().getStringExtra(CITY);
        recLocationList = getIntent().getParcelableArrayListExtra(REC_LOCATION_CLASSIFY);
        recPicList = getIntent().getParcelableArrayListExtra(REC_PICTURE_CLASSIFY);
        recFileList = getIntent().getParcelableArrayListExtra(REC_FILE_CLASSIFY);
        mCurShow = getIntent().getParcelableExtra(CURRENT_SHOW);
    }

    /**
     * 初始化show && Function添加
     */
    private void initShow() {
        if (mCurShow == null) {
            ShowData show = HuxinSdkManager.instance().getShowDataFromDb(mDSPhone);
            if (show != null) {
                mCurShow = new ChatMsg(show);
            }
        }

        boolean isPersonShow = false; //是否个人show

        if (mCurShow == null) {
            FunctionViewUtils.instance(mContext).addFuncList(true);
        } else {
            ShowData showData = mCurShow.getNewShowData();
            List<UIData> uiDataList = HuxinSdkManager.instance().getUIData(mDSPhone);
            if (null == uiDataList || uiDataList.size() == 0 || null == showData ||
                    showData.getType().equals("1") || showData.getType().equals("3")) {
                //个人
                FunctionViewUtils.instance(mContext).addFuncList(true);
            } else {
                //商家
                FunctionViewUtils.instance(mContext).addFuncList(false);
                isPersonShow = true;
            }
        }

        mItemList = FunctionViewUtils.instance(mContext).getItemList();

        ThirdBizMgr.getInstance().setNotifyChanged(new INotifyDataChanged<FunctionItem>() {
            @Override
            public void notifyData(List<FunctionItem> list) {
                if (ListUtils.isEmpty(mItemList)) {
                    mItemList = FunctionViewUtils.instance(mContext).getItemList();
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

        initMsgUI();//新增 msg UI

        if (!isPersonShow) {
            civ_message.setVisibility(View.VISIBLE);
        } else {
            civ_message.setVisibility(View.GONE);
        }

    }

    private void reqCurDetail() {
        String date = TimeUtils.getDate(System.currentTimeMillis());
        String key = date + "red_package_call_count";

        int count = AppUtils.getIntSharedPreferences(mContext, key, 1);
        //当天的3,6,9...才发起请求
        if (count % 3 == 0) {
            StrategyModel.DBean.ModelsBean item = new StrategyModel.DBean.ModelsBean();
            item.setType("2");
            item.setFunType("2");
            item.setTitle(getString(R.string.hx_sdk_hook_layout_27));
            item.setRemark(getString(R.string.hx_sdk_hook_layout_28));
            item.setContent("com.youmai.hxsdk.activity.RedPacketActivity");
            mAdapter.setItem(item);
        }
        count++;
        AppUtils.setIntSharedPreferences(mContext, key, count);

    }

    private boolean isOutOfTenTimes() {
        String key = TimeUtils.getDate(System.currentTimeMillis()) + HuxinSdkManager.instance().getPhoneNum();
        return AppUtils.getIntSharedPreferences(mContext, key, 0) > 9;
    }

    private void coutRedPacket() {
        String key = TimeUtils.getDate(System.currentTimeMillis()) + HuxinSdkManager.instance().getPhoneNum();
        int times = AppUtils.getIntSharedPreferences(mContext, key, 0);
        times++;
        AppUtils.setIntSharedPreferences(mContext, key, times);
    }

    private void loadRedPacket() {
        /*final SnowView redPacketRain = (SnowView) findViewById(R.id.sv_redpacket_rain);
        if (isOutOfTenTimes()) {
            return;
        }

        IPostListener iPostListener = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                LogUtils.e("YW", "red response = " + response);
                try {
                    final JSONObject json = new JSONObject(response);
                    if (json.optString("s").equals("1")) {
                        final RedPacketModel model = GsonUtil.parse(response, RedPacketModel.class);
                        if (model != null && model.getD() != null) {
                            coutRedPacket();

                            redPacketRain.setVisibility(View.VISIBLE);
                            redPacketRain.setFocusable(true);
                            redPacketRain.setFocusableInTouchMode(true);
                            redPacketRain.setClickable(true);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        redPacketRain.setVisibility(View.GONE);
                                        redPacketRain.setFocusable(false);
                                        redPacketRain.setFocusableInTouchMode(false);
                                        redPacketRain.setClickable(false);
                                        RedRainPopWindow popRain = new RedRainPopWindow(mContext, model.getD());
                                        //popRain.showAtLocation(scrollable_layout, Gravity.CENTER, 0, 0);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 3000);

                            if (model.getD().getSurplusHxCoin().compareTo("0") > 0) {
                                reqCurDetail();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        HuxinSdkManager.instance().repRedPacketAward(iPostListener);*/
    }

    /**
     * 未读消息的广播监听
     */
    private void initReceiver() {
        mReceiveSmsMsg = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                CacheMsgBean cacheMsgBean = intent.getParcelableExtra("CacheMsgBean");
                if (cacheMsgBean != null) {
                    Message message = mMSGHandler.obtainMessage();
                    message.what = MSG_REC_SMS;
                    mMSGHandler.sendMessage(message);
                }
            }
        };
        IntentFilter homeFilter1 = new IntentFilter("com.youmai.hxsdk.receiveSmsMsg");
        registerReceiver(mReceiveSmsMsg, homeFilter1);
        //针对锁屏后的消息保留
        unreadCount = IMMsgManager.getInstance().genUnreadCacheMsgBeanListCount(mContext, mDSPhone);
    }

    private void initMsgUI() {
        AndroidBottomBarUtil.assistActivity(findViewById(R.id.ll_hook_parent));

        //todo_yw: header
        mPhoneView = (ImageView) findViewById(R.id.pv_full_hook);//背景秀
        mPhoneView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        fl_back_close = (FrameLayout) findViewById(R.id.fl_back_close);//关闭
        fl_back_close.setOnClickListener(this);
        civ_message = (CountImageView) findViewById(R.id.civ_message);
        civ_message.setOnClickListener(this);
        tv_hook_finish_tip = (TextView) findViewById(R.id.hook_common_finish_text);//通话结束
        headImg = (ImageView) findViewById(R.id.hook_common_head_img);//用户头像
        tv_hook_number = (TextView) findViewById(R.id.hook_common_name);//用户名字
        tv_hook_location = (TextView) findViewById(R.id.hook_common_call_info);//地址
        tv_hook_call_time = (TextView) findViewById(R.id.hook_call_time);//通话时长
        rl_hook_top_box = (RelativeLayout) findViewById(R.id.hook_top_box);

        //todo_yw: RecyclerView
        hook_appbar = (AppBarLayout) findViewById(R.id.hook_appbar);
        hook_header = (LinearLayout) findViewById(R.id.hook_header);

        // TODO_YW: function
        recycler_view = (FunctionPageView) findViewById(R.id.hook_recycler_view);
        page_indicator = (FunctionIndicator) findViewById(R.id.card_header_indicator);

        //todo_yw: card_type_parent
        tv_no_message = (TextView) findViewById(R.id.tv_no_message);
        mStrategyRecycler = (RecyclerView) findViewById(R.id.card_strategy_recycler);
        mStrategyRecycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new StrategyAdapter(mContext);
        mStrategyRecycler.setAdapter(mAdapter);

        initScrollHeader();
        loadShowConfig(); //todo_yw: show
        loadDataConfig();
        initPager();
        initMsgInfo();

        setHeadImg(); //设置用户相关信息
    }

    /**
     * 滑动的头部 Parent
     */
    private void initScrollHeader() {
        scrollable_layout = (HeaderViewPager) findViewById(R.id.scrollable_layout);
        scrollable_layout.setCurrentScrollableContainer(new HeaderScrollHelper.ScrollableContainer() {
            @Override
            public View getScrollableView() {
                return mStrategyRecycler;
            }
        });

        hook_appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                hook_header.setAlpha(1 - Math.abs(verticalOffset * 1.0f / appBarLayout.getTotalScrollRange()));
                appBarLayout.setBackgroundColor(0x00000000);
            }
        });

        scrollable_layout.setOnScrollListener(new HeaderViewPager.OnScrollListener() {
            @Override
            public void onScroll(int currentY, int maxY) {
                //让头部具有差速动画,如果不需要,可以不用设置
                //hook_appbar.setTranslationY(currentY / 2);
                //动态改变标题栏的透明度,注意转化为浮点型
                //float alpha = 1.0f * currentY / maxY;
                //hook_appbar.setAlpha(1 - alpha);
            }
        });
    }

    private void loadShowConfig() {

        if (mCurShow == null) {
            return;
        }

        if (null == mCurShow.getNewShowData()) {
            //blurBg(R.drawable.hx_show_default_full, mPhoneView);
        } else {
            // show
            String url;
            if (mCurShow.getMsgType().equals(ChatMsg.MsgType.SHOW_PICTURE)) {
                url = AppConfig.getImageUrl(this, mCurShow.getNewShowData().getFid());
            } else {
                url = AppConfig.getImageUrl(this, mCurShow.getNewShowData().getPfid());
            }

            if (!StringUtils.isEmpty(mCurShow.getNewShowData().getDetailurl())
                    && (!StringUtils.isEmpty(mCurShow.getNewShowData().getFid())
                    || !StringUtils.isEmpty(mCurShow.getNewShowData().getPfid()))) {

                StrategyModel.DBean.ModelsBean item = new StrategyModel.DBean.ModelsBean();
                item.setType("0");
                item.setFunType("1");
                if (mCurShow.getNewShowData().getTitle() != null && !mCurShow.getNewShowData().getTitle().equals("")) {
                    item.setTitle(mCurShow.getNewShowData().getTitle());
                } else {
                    item.setTitle(mCurShow.getNewShowData().getName());
                }
                item.setRemark(url);
                item.setContent(mCurShow.getNewShowData().getDetailurl());
                mAdapter.setItem(item);

                tv_no_message.setVisibility(View.GONE);
                mStrategyRecycler.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 拉取消息配置列表
     */
    private void loadDataConfig() {

        HuxinSdkManager.instance().strategyList(new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                if (isCheckDestroyed()) {
                    return;
                }

                try {
                    if (!isFinishing()) {
                        StrategyModel model = GsonUtil.parse(response, StrategyModel.class);
                        if (model != null && model.getD() != null) {
                            StrategyModel.DBean dBean = model.getD();
                            String repId = dBean.getRepresentId();
                            String gift = dBean.getHasGift();
                            List<StrategyModel.DBean.ModelsBean> list = dBean.getModels();

                            List<StrategyModel.DBean.ModelsBean> newList = new ArrayList<>();
                            if (!ListUtils.isEmpty(list)) {
                                newList.addAll(list);
                            }

                            /*if (!StringUtils.isEmpty(repId) && !repId.equals("0") && !StringUtils.isEmpty(gift) && gift.equals("1")) {
                                StrategyModel.DBean.ModelsBean item = new StrategyModel.DBean.ModelsBean();
                                item.setType("2");
                                item.setFunType("2");
                                item.setRepId(repId);
                                item.setTitle(getString(R.string.hx_sdk_hook_layout_25));
                                item.setRemark(getString(R.string.hx_sdk_hook_layout_26));
                                item.setContent("com.youmai.hxsdk.activity.RepDetailsActivity");
                                newList.add(0, item);
                            }*/

                            strategyDataConfig(newList);

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void strategyDataConfig(List<StrategyModel.DBean.ModelsBean> modelsList) {

        if (!isCheckDestroyed() && !ListUtils.isEmpty(modelsList)) {
            mAdapter.setList(modelsList);

            tv_no_message.setVisibility(View.GONE);
            mStrategyRecycler.setVisibility(View.VISIBLE);

            HxUsersHelper.instance().updateSingleUser(mContext, HuxinSdkManager.instance().getPhoneNum(),
                    new HxUsersHelper.IOnUpCompleteListener() {
                        @Override
                        public void onSuccess(HxUsers users) {
                            if (users.getShowType().equals("3")) {
                                mAdapter.removeItemByContent("com.youmai.hxsdk.activity.RepresentActivity");
                            } else if (users.getShowType().equals("1")) {
                                mAdapter.removeItemByContent("com.youmai.hxsdk.SettingShowActivity");
                            }
                        }

                        @Override
                        public void onFail() {

                        }
                    });
        }
    }

    /**
     * Function view
     */
    private HeaderViewPager scrollable_layout;
    private RecyclerView mStrategyRecycler;
    private StrategyAdapter mAdapter;
    private TextView tv_no_message;
    private List<FunctionItem> mItemList;
    private FunctionPageView recycler_view;
    private FunctionIndicator page_indicator;
    private FunctionPageView.PageAdapter mFunctionAdapter;

    private void initPager() {
        mFunctionAdapter = recycler_view.new PageAdapter(mItemList, new FunctionPageView.CallBack() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.hx_function_item, parent, false);
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                FunctionItem item = mItemList.get(position);
                ((ViewHolder) holder).img.setImageResource(item.getDrawableRes());
                ((ViewHolder) holder).tv_name.setText(item.getNameStr());
                if (item.getNameStr().equals(mContext.getString(FunctionViewUtils.ITEM_STRS[0]))) { //重拨
                    //显示未接电话,区分主叫和被叫的区别
                    if (isMOCall) {
                        ((ViewHolder) holder).tv_name.setText(R.string.hx_sdk_hook_layout_11);
                    } else {
                        ((ViewHolder) holder).tv_name.setText(R.string.hx_sdk_hook_layout_18);
                    }
                    tvItemCallTip = ((ViewHolder) holder).getTvItemCallTip();
                } else if (item.getNameStr().equals(mContext.getString(FunctionViewUtils.ITEM_STRS[2]))) {
                    messageImg = ((ViewHolder) holder).getMsgImg();
                    setCount(unreadCount, false);
                    ((ViewHolder) holder).img.setVisibility(View.GONE);
                    ((ViewHolder) holder).msgImg.setVisibility(View.VISIBLE);
                    ((ViewHolder) holder).msgImg.setImageResource(item.getDrawableRes());
                }
            }

            @Override
            public void onItemClickListener(View view, int position) {

                FunctionItem item = mItemList.get(position);
                if (item.getNameStr().equals(mContext.getString(FunctionViewUtils.ITEM_STRS[0]))) { //重拨
                    call(mDSPhone, true);
                    if (isMOCall) {

                    } else {
                        tvItemCallTip.setVisibility(View.INVISIBLE);
                    }
                } else if (item.getNameStr().equals(mContext.getString(FunctionViewUtils.ITEM_STRS[1]))) { //拨号
                    //跳转联系人
                    Intent intent = new Intent();
                    intent.setAction("com.youmai.huxin.Dials");
                    startActivityForResult(intent, 3);
                } else if (item.getNameStr().equals(mContext.getString(FunctionViewUtils.ITEM_STRS[2]))) { //留言
                    if (HuxinSdkManager.instance().showSdkLogin()) {
                        return;
                    }

                    Intent intent = new Intent();
                    intent.setClass(mContext, IMConnectionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(IMConnectionActivity.DST_PHONE, mDSPhone);
                    intent.putExtra(IMConnectionActivity.DST_SHOW, mCurShow);
                    intent.putExtra(IMConnectionActivity.DST_NAME, showNickName);
                    intent.putExtra(IMConnectionActivity.DST_CONTACT_ID, contact_id);
                    intent.putExtra(IMConnectionActivity.IS_SHOW_AUDIO, true);
                    mContext.startActivity(intent);
                    unreadCount = 0;
                    messageImg.setCount(0);//消息数归零
                    tvItemCallTip.setVisibility(View.INVISIBLE);
                } else if (item.getNameStr().equals(mContext.getString(FunctionViewUtils.ITEM_STRS[3]))) { //递名片
                    setSwapCard();
                } else if (item.getNameStr().equals(mContext.getString(FunctionViewUtils.ITEM_STRS[4]))) { //开门
                    String activityClass = ThirdBizHelper.readThirdBizActivity(item.getPackageName());
                    ThirdActivityHelper.openDoor(mContext, item.getPackageName(), activityClass);
                } else if (item.getNameStr().equals(mContext.getString(FunctionViewUtils.ITEM_STRS[5]))) { //意见反馈
                    ThirdActivityHelper.feedBackActivity(mContext);
                } else if (item.getNameStr().equals(mContext.getString(FunctionViewUtils.ITEM_STRS[6]))) { //设为业主
                    ThirdActivityHelper.selectCommunityActivity(mContext, mDSPhone);
                } else if (item.getNameStr().equals(mContext.getString(FunctionViewUtils.ITEM_STRS[7]))) { //图片
                    jumpPicture();
                } else if (item.getNameStr().equals(mContext.getString(FunctionViewUtils.ITEM_STRS[8]))) { //位置
                    jumpLocation();
                } else if (item.getNameStr().equals(mContext.getString(FunctionViewUtils.ITEM_STRS[9]))) { //文件
                    jumpFile();
                } else if (item.getNameStr().equals(mContext.getString(FunctionViewUtils.ITEM_STRS[10]))) { //备注
                    jumpRemark();
                } else if (item.getNameStr().equals(mContext.getString(FunctionViewUtils.ITEM_STRS[11]))) { //添加
                    jumpAdd();
                } else if (item.getNameStr().equals(mContext.getString(FunctionViewUtils.ITEM_STRS[12]))) { //拍照
                    jumpCamera();
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
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img;
        private TextView tv_name;
        private CountImageView msgImg;
        private TextView tv_item_call_tip;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.iv_item_img);
            tv_name = (TextView) itemView.findViewById(R.id.tv_item_name);
            msgImg = (CountImageView) itemView.findViewById(R.id.iv_item_count);
            tv_item_call_tip = (TextView) itemView.findViewById(R.id.tv_item_call_tip);
        }

        public CountImageView getMsgImg() {
            return msgImg;
        }

        public TextView getTvItemCallTip() {
            return tv_item_call_tip;
        }
    }

    private void initMsgInfo() {
        if (!StringUtils.isEmpty(mShowName)) {
            tv_hook_number.setText(mShowName);
            showNickName = mShowName;
        } else {
            showNickName = HuxinSdkManager.instance().getContactName(mDSPhone);
            tv_hook_number.setText(showNickName);
        }

        tv_hook_finish_tip.setText(getString(R.string.hx_sdk_hook_layout_01));
    }

    /**
     * 焦点转移
     */
    private void moveFocus(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    /**
     * 消息统计数显示
     *
     * @param count     消息数
     * @param isReceive 是否正在接收
     */
    private void setCount(int count, boolean isReceive) {
        if (isReceive && HuxinSdkManager.instance().isNotifySound(mContext)) {
            RingUtils.playRingTone(mContext, RingtoneManager.TYPE_NOTIFICATION);//播放消息提示音
        }
        if (messageImg != null) {
            messageImg.setCount(count);
        }
        if (civ_message != null) {
            civ_message.setCount(count);
            civ_message.setBackgroundResource(R.drawable.hx_call_after_message);
        }
    }

    private void initData() {

        if (StringUtils.isEmpty(province) && StringUtils.isEmpty(city)) {
            getPhoneInfo(mDSPhone);
        } else {
            calculateTime(province, city, mTalkTime);
            tv_hook_location.setText(province + " " + city);
        }

        if (null != recPicList && recPicList.size() > 0) {
            mPicCount += recPicList.size();
            recPicList.clear();
        }
        if (null != recLocationList && recLocationList.size() > 0) {
            mLocationCount += recLocationList.size();
            recLocationList.clear();
        }
        if (null != recFileList && recFileList.size() > 0) {
            mFileCount += recFileList.size();
            recFileList.clear();
        }

        unreadCount = IMMsgManager.getInstance().genUnreadCacheMsgBeanListCount(mContext, mDSPhone);

        if (mCurShow == null) {
            mPhoneView.setImageResource(R.drawable.hx_show_default_full);
            return;
        }
    }

    //本次通话信息，能读取系统通话时间优先，不能读取则用自带统计talkTime
    private void calculateTime(final String province, final String city, final long talkTime) {
        long duration = talkTime;
        String timeStr;

        if (duration != 0) { // 用talkTime区分来电与去电
            duration = getDurTime();
        }

        String info;

        if ((duration + "").endsWith("000") || duration == -1) { // 区分权限
            timeStr = CallRecordUtil.toTalkTime(mContext, talkTime);
        } else {
            timeStr = CallRecordUtil.toTalkTime(mContext, duration * 1000);
        }

        if (duration > 0 && !CallInfo.IsMTCalling()) { //用IsMTCalling区分来电与去电 的已接与未接
            info = String.format("%1$s  %2$s", getString(R.string.hx_sdk_hook_layout_23), timeStr);
            clearCallInfo();
        } else {
            long dur = getDurTime();
            if (dur == 0 || dur == -1) {
                info = String.format("%1$s  %2$s", getString(R.string.hx_sdk_hook_layout_22), duration == 0 ? "" : timeStr);//暂时拿不到响铃时间
            } else {
                info = String.format("%1$s  %2$s", getString(R.string.hx_sdk_hook_layout_22), CallRecordUtil.toTalkTime(mContext, dur * 1000));//暂时拿不到响铃时间
            }
        }
        tv_hook_call_time.setText(info);
    }

    private long getDurTime() {
        if (callModel == null) {
            callModel = CallRecordUtil.readCallInfo(mContext, mDSPhone);
        }
        return callModel.getDuration();
    }

    private void setClickListener() {
        if (headImg != null) {
            headImg.setOnClickListener(this);
        }
    }

    private void setHeadImg() {
        boolean contact = HuxinSdkManager.instance().isContact(mDSPhone);
        if (contact) { //非通讯录
            if (mDSPhone.equals("4000")) {
                showNickName = mContext.getString(R.string.hx_sdk_feadback_service_name);
            } else {
                showNickName = mDSPhone;
                addNotContactsFunctionItem();
            }
            headImg.setImageResource(R.drawable.hx_icon_full_header_normal);
            headImg.setBackgroundResource(R.drawable.hx_icon_full_header_male_bg);
        } else {
            HxUsers user = HxUsersHelper.instance().getSingleUserCache(mContext, mDSPhone);
            if (null != user && null != user.getIconUrl()) {
                try {
                    Glide.with(mContext).load(user.getIconUrl())
                            .apply(new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .circleCrop()
                                    .error(R.drawable.hx_icon_full_header_normal))
                            .into(headImg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null != user.getSex() && user.getSex().equals("2")) { //女
                    headImg.setBackgroundResource(R.drawable.hx_icon_full_header_female_bg);
                } else {
                    headImg.setBackgroundResource(R.drawable.hx_icon_full_header_male_bg);
                }
            }
        }
    }

    /**
     * 非联系人
     */
    private void addNotContactsFunctionItem() {
        mItemList.addAll(4, FunctionViewUtils.instance(mContext).addNotContactsItem());
        // 设置页间距
        recycler_view.setPageMargin(DisplayUtil.dip2px(mContext, 10));
        // 设置指示器
        recycler_view.setIndicator(page_indicator);
        // 设置数据
        recycler_view.setAdapter(mFunctionAdapter);
    }

    /**
     * 获取手机号归属地
     *
     * @param dstPhone 手机号
     */
    private void getPhoneInfo(String dstPhone) {
        HuxinSdkManager.instance().userLocale(dstPhone, null, null, new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                LogUtils.e(Constant.SDK_UI_TAG, "response = " + response);
                if (Build.VERSION.SDK_INT >= 17 && isDestroyed()) {
                    return;
                }
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.optString("s").equals("1")) {
                        JSONObject d = json.getJSONObject("d");
                        String province = d.optString("province");
                        String city = d.optString("city");
                        tv_hook_location.setText(province + " " + city);
                        calculateTime(province, city, mTalkTime);
                    } else {
                        calculateTime(null, null, mTalkTime);
                    }
                } catch (Exception e) {
                    calculateTime(null, null, mTalkTime);
                    LogUtils.e(Constant.SDK_UI_TAG, "请求号码归属地失败");
                    e.printStackTrace();
                }
            }

        });
    }

    /* 监听home键广播 start by 2016.11.10 */
    private final BroadcastReceiver homeListenerReceiver = new BroadcastReceiver() {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null && reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    // 处理自己的逻辑
                    //isFinish = false;
                    finish();
                }
            }
        }
    };

    //在创建View时注册Receiver
    public void registerHome(Context context) {
        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.registerReceiver(homeListenerReceiver, homeFilter);
    }


    //在View消失时反注册Receive 反注册home监听
    public void unRegisterHome(Context context) {
        try {
            context.unregisterReceiver(homeListenerReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
    /* 监听home键广播 end by 2016.11.10 */


    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    private void registerAddContactsOnBack() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(broadcastReceiver, new IntentFilter("NewContactsActivity_onBack"));
    }

    private void unRegisterAddContactsOnBack() {
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //增加手动退出事件
        if (!TextUtils.isEmpty(HuxinSdkManager.instance().getPhoneNum())) {

            Intent backHome = new Intent(Intent.ACTION_MAIN);
            backHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            backHome.addCategory(Intent.CATEGORY_HOME);

            Intent huxinAppIntent = new Intent(Intent.ACTION_MAIN);
            huxinAppIntent.setClassName("com.youmai.huxin", "com.youmai.huxin.app.activity.MainAct");
            huxinAppIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            huxinAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            if (IntentQueryUtil.isExist(this, huxinAppIntent)) {
                startActivity(huxinAppIntent);
            } else {
                startActivity(backHome);
            }

        }

        finish();
    }


    /**
     * 拨打电话
     *
     * @param isCallBack 是否回拨
     */
    private void call(final String phone, boolean isCallBack) {

        Intent intent;
        if (isCallBack) {
            Uri uri;
            if (TextUtils.isEmpty(phone)) {
                uri = Uri.parse("tel:");
                intent = new Intent(Intent.ACTION_DIAL, uri);//获取不到手机号则跳到拨号界面
                startActivity(intent);
                HuxinSdkManager.instance().getStackAct().finishAllActivity();
            } else {
                int voipTime = SPDataUtil.getVoipDialogTimestamp(mContext);
                String combo = SPDataUtil.getComboEnd(mContext);
                if (HuxinSdkManager.instance().getPhoneNum() == null || HuxinSdkManager.instance().getPhoneNum().isEmpty() ||
                        (TextUtils.isEmpty(combo) && voipTime != 0 && (TimeUtils.getNightTimestamp() - voipTime < 86400))) {
                    // 调系统拨号
                    try {
                        intent = new Intent();
                        intent.setAction(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + phone));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getBaseContext(), R.string.hx_permissions_call_tip, Toast.LENGTH_SHORT).show();
                    }
                }
                HuxinSdkManager.instance().getStackAct().finishAllActivity();
            }
            CallRecordUtil.clearMissedCalls(mContext, mDSPhone);//清除未接来电数 Ps:会直接影响通话记录的数据库
            //直接拔打电话 需要权限
        } else {
            Uri uri;
            if (TextUtils.isEmpty(phone)) {
                uri = Uri.parse("tel:");
            } else {
                uri = Uri.parse("tel:" + phone);
            }
            //跳至拨号界面，并填写上号码不需要权限
            intent = new Intent(Intent.ACTION_DIAL, uri);
            startActivity(intent);
            HuxinSdkManager.instance().getStackAct().finishAllActivity();
        }
    }

    /**
     * 显示未接来电信息,只有获取通话记录权限才有显示未接来电
     */
    private void showCallInfo() {
        int c = CallRecordUtil.readMissCall(mContext);
        if (callModel == null) {
            callModel = CallRecordUtil.readCallInfo(mContext, mDSPhone);
        }
        if (isMOCall) {
            tv_hook_finish_tip.setText(getString(R.string.hx_sdk_hook_layout_01));
            tvItemCallTip.setVisibility(View.INVISIBLE);
        } else {
            if (c > 0) {
                if (callModel != null && callModel.getType() == 3) {
                    tv_hook_finish_tip.setText(c + " 个未接来电");
                    tvItemCallTip.setVisibility(View.VISIBLE);
                    tvItemCallTip.setText("回电给Ta");
                }
            } else {
                tv_hook_finish_tip.setText(getString(R.string.hx_sdk_hook_layout_01));
                tvItemCallTip.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 设置未接来电已读
     */
    private void clearCallInfo() {
        tv_hook_finish_tip.setText(getString(R.string.hx_sdk_hook_layout_01));
        tvItemCallTip.setVisibility(View.INVISIBLE);
        CallRecordUtil.clearMissedCalls(mContext, mDSPhone);
    }

    private void jumpLocation() {
        if (HuxinSdkManager.instance().showSdkLogin()) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("dstPhone", mDSPhone);

        intent.setClass(mContext, LocationActivity.class);

        startActivity(intent);
    }


    private void jumpPicture() {
        if (HuxinSdkManager.instance().showSdkLogin()) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(PhotoActivity.EXTRA_SHOW_CAMERA, false);//显示摄像
        intent.putExtra("dstPhone", mDSPhone);
        intent.setClass(mContext, PhotoActivity.class);

        startActivity(intent);
    }

    /**
     * 跳到文件管理器
     */
    private void jumpFile() {
        if (HuxinSdkManager.instance().showSdkLogin()) {
            return;
        }

        PickerManager.getInstance().addDocTypes();
        Intent intent = new Intent(mContext, FileManagerActivity.class);
        intent.putExtra("dstPhone", mDSPhone);
        intent.putExtra(FileManagerActivity.REQUEST_CODE_CALLBACK, FilePickerConst.CALL_REQUEST_CALLBACK);
        startActivity(intent);
    }

    /**
     * 跳到沟通并打开备注
     */
    private void jumpRemark() {
        if (HuxinSdkManager.instance().showSdkLogin()) {
            return;
        }

        Intent intent = new Intent();
        intent.setClass(mContext, IMConnectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(IMConnectionActivity.DST_PHONE, mDSPhone);
        intent.putExtra(IMConnectionActivity.DST_SHOW, mCurShow);
        intent.putExtra(IMConnectionActivity.DST_NAME, showNickName);
        intent.putExtra(IMConnectionActivity.DST_CONTACT_ID, contact_id);
        intent.putExtra(IMConnectionActivity.IS_SHOW_AUDIO, false);
        intent.putExtra(IMConnectionActivity.IS_OPEN_REMARK, true);
        mContext.startActivity(intent);
        unreadCount = 0;
        civ_message.setCount(0);//消息数归零
        if (messageImg != null) {
            messageImg.setCount(0);//消息数归零
        }
        tvItemCallTip.setVisibility(View.INVISIBLE);
    }

    /**
     * 跳转到添加联系人
     */
    private void jumpAdd() {
        if (HuxinSdkManager.instance().showSdkLogin()) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("contacts_operate", 2);
        intent.putExtra("contacts_phone", mDSPhone);
        intent.putExtra(IMConnectionActivity.ACTIVITY_COME_TYPE, 1);
        intent.setClassName("com.youmai.huxin", "com.youmai.huxincontacts.activity.NewContactsActivity");
        startActivity(intent);
    }

    /**
     * 跳转到自定义拍摄
     */
    private void jumpCamera() {
        if (HuxinSdkManager.instance().showSdkLogin()) {
            return;
        }
        useCamera();
    }

    //拍照
    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, HookCameraActivity.class);
                intent.putExtra(HookCameraActivity.CAMERA_TYPE, JCameraView.BUTTON_STATE_BOTH);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            } else {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA}, GET_PERMISSION_REQUEST);
            }
        } else {
            Intent intent = new Intent(this, HookCameraActivity.class);
            intent.putExtra(HookCameraActivity.CAMERA_TYPE, JCameraView.BUTTON_STATE_BOTH);
            startActivityForResult(intent, REQUEST_CODE_CAMERA);
        }
    }

    //跳转编辑名片页面
    public void setSwapCard() {
        if (HuxinSdkManager.instance().showSdkLogin()) {
            return;
        }

        Intent intent = new Intent();
        intent.setClass(mContext, IMConnectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(IMConnectionActivity.DST_PHONE, mDSPhone);
        intent.putExtra(IMConnectionActivity.DST_SHOW, mCurShow);
        intent.putExtra(IMConnectionActivity.DST_NAME, showNickName);
        intent.putExtra(IMConnectionActivity.DST_CONTACT_ID, contact_id);
        intent.putExtra(IMConnectionActivity.IS_SHOW_AUDIO, false);

        intent.putExtra(IMConnectionActivity.FROM_TO, IMConnectionActivity.FROM_HOOK_STRATE);

        mContext.startActivity(intent);
        unreadCount = 0;
        civ_message.setCount(0);//消息数归零
        tvItemCallTip.setVisibility(View.INVISIBLE);
    }


    private ProgressDialog dialog;

    IFileSendListener send = new IFileSendListener() {

        @Override
        public void onProgress(int type, double progress, String path) {
            if (Build.VERSION.SDK_INT >= 17 && !isDestroyed()) {
                if (dialog == null) {
                    HuxinSdkManager.instance().getStackAct().finishActivity(PhotoActivity.class);
                    dialog = new ProgressDialog(mContext);
                    dialog.setMessage("正在发送...");
                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    dialog.setProgress(0);
                    dialog.setMax(100);
                }
                if (!dialog.isShowing()) {
                    dialog.show();
                }
                dialog.setProgress((int) Math.abs(progress * 100));
            }
        }

        @Override
        public void onImSuccess(int type, FileBean bean) {
            if (Build.VERSION.SDK_INT >= 17 && !isDestroyed()) {
                if (dialog != null && dialog.isShowing()) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            Toast.makeText(mContext, "发送成功", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent();
            intent.setClass(mContext, IMConnectionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(IMConnectionActivity.DST_PHONE, mDSPhone);
            intent.putExtra(IMConnectionActivity.DST_SHOW, mCurShow);
            intent.putExtra(IMConnectionActivity.DST_NAME, showNickName);
            intent.putExtra(IMConnectionActivity.DST_CONTACT_ID, contact_id);
            intent.putExtra(IMConnectionActivity.IS_SHOW_AUDIO, false);
            mContext.startActivity(intent);
            unreadCount = 0;
            civ_message.setCount(0);//消息数归零
            tvItemCallTip.setVisibility(View.INVISIBLE);

            HuxinSdkManager.instance().getStackAct().finishActivity(PhotoActivity.class);
            finish();
        }

        @Override
        public void onImFail(int type, FileBean fileBean) {
            if (Build.VERSION.SDK_INT >= 17 && !isDestroyed()) {
                if (dialog != null && dialog.isShowing()) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            Toast.makeText(mContext, "发送失败", Toast.LENGTH_SHORT).show();
            HuxinSdkManager.instance().getStackAct().finishActivity(PhotoActivity.class);
        }

        @Override
        public void onImNotUser(int type, long msgId) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            HuxinSdkManager.instance().getStackAct().finishActivity(PhotoActivity.class);
        }
    };

    public boolean isCheckDestroyed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (isDestroyed() || isFinishing()) {
                return true;
            }
        } else {
            if (isFinishing()) {
                return true;
            }
        }
        return false;
    }

}
