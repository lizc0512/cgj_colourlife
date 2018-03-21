package com.youmai.hxsdk.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.adapter.IMListAdapter;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.bean.ChatMsg;
import com.youmai.hxsdk.dialog.HxDialog;
import com.youmai.hxsdk.entity.CallInfo;
import com.youmai.hxsdk.im.IMHelper;
import com.youmai.hxsdk.im.IMMsgCallback;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.cache.CacheMsgEmotion;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.im.cache.CacheMsgLShare;
import com.youmai.hxsdk.im.cache.CacheMsgMap;
import com.youmai.hxsdk.im.cache.CacheMsgRemark;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.im.cache.CacheMsgVideo;
import com.youmai.hxsdk.im.cache.CacheMsgVoice;
import com.youmai.hxsdk.im.cache.ContactsDetailsBean;
import com.youmai.hxsdk.im.voice.manager.DrivingModeMediaManager;
import com.youmai.hxsdk.im.voice.manager.MediaManager;
import com.youmai.hxsdk.interfaces.OnChatMsg;
import com.youmai.hxsdk.map.LocationActivity;
import com.youmai.hxsdk.module.filemanager.PickerManager;
import com.youmai.hxsdk.module.filemanager.activity.FileManagerActivity;
import com.youmai.hxsdk.module.filemanager.constant.FilePickerConst;
import com.youmai.hxsdk.module.filemanager.interfaces.PickerRefreshUIListener;
import com.youmai.hxsdk.module.map.AbstractStartOrQuit;
import com.youmai.hxsdk.module.map.ActualLocation;
import com.youmai.hxsdk.module.map.ActualLocationFragment;
import com.youmai.hxsdk.module.map.IReceiveStartListener;
import com.youmai.hxsdk.module.movierecord.MediaStoreUtils;
import com.youmai.hxsdk.module.picker.PhotoPickerManager;
import com.youmai.hxsdk.module.picker.PhotoPreviewActivity;
import com.youmai.hxsdk.module.remind.HxFirstRemindDialog;
import com.youmai.hxsdk.module.remind.RemindBean;
import com.youmai.hxsdk.module.remind.SetRemindActivity;
import com.youmai.hxsdk.service.SendMsgService;
import com.youmai.hxsdk.service.download.bean.FileQueue;
import com.youmai.hxsdk.service.sendmsg.SendMsg;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.sp.SPDataUtil;
import com.youmai.hxsdk.utils.AbFileUtil;
import com.youmai.hxsdk.utils.CallRecordUtil;
import com.youmai.hxsdk.utils.CommonUtils;
import com.youmai.hxsdk.utils.CompressImage;
import com.youmai.hxsdk.utils.CompressVideo;
import com.youmai.hxsdk.utils.DisplayUtil;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.SmsManager;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.utils.TimeUtils;
import com.youmai.hxsdk.utils.ToastUtil;
import com.youmai.hxsdk.utils.VideoUtils;
import com.youmai.hxsdk.view.LinearLayoutManagerWithSmoothScroller;
import com.youmai.hxsdk.view.chat.InputMessageLay;
import com.youmai.smallvideorecord.model.OnlyCompressOverBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-11-07 10:31
 * Description:  im 界面
 */
public class IMConnectionActivity extends SdkBaseActivity implements
        IMMsgCallback, InputMessageLay.KeyBoardBarViewListener,
        PickerRefreshUIListener {


    /*
     * Const.
     */
    public static final String TAG = IMConnectionActivity.class.getSimpleName();

    public static final String DST_PHONE = "DST_PHONE";
    //srsm add @20170214
    public static final String DST_NAME = "DST_NAME";
    public static final String DST_PAGE_TYPE = "PAGE_TYPE";
    public static final String DST_CONTACT_ID = "DST_CONTACT_ID";
    public static final String EXTRA_IS_UNREAD = "EXTRA_IS_UNREAD";
    public static final String EXTRA_SCROLL_POSITION = "EXTRA_SCROLL_POSITION";
    public static final String DST_SHOW = "DST_SHOW";
    public static final String IS_SHOW_AUDIO = "IS_SHOW_AUDIO";
    public static final String IS_OPEN_REMARK = "IS_OPEN_REMARK";
    public static final String FROM_TO = "from_to";
    public static final String MSG_ID = "msg_id";

    public static final String IS_IM_TYPE = "IS_IM_TYPE";
    public static final String ACTIVITY_COME_TYPE = "ACTIVITY_COME_TYPE";

    //sync app MyMsgListFragment INTENT_REQUEST_FOR_UPDATE_UI
    public static final int INTENT_REQUEST_FOR_UPDATE_UI = 101;
    private static final int SWAP_CARD_EQUEST = 102;
    public static final int SAVE_CARD_EQUEST = 103;
    public static final long MAX_SENDER_FILE = 50 * 1024 * 1024;

    public static final int FROM_HOOK_STRATE = 100;

    public static final int REQUEST_CODE_CAMERA = 400;
    public static final int REQUEST_CODE_CARD = 600;

    public static final int REQUEST_CODE_ADDEMO = 1000;

    private final int GET_PERMISSION_REQUEST = 500; //权限申请自定义码
    public static final int REQUEST_REMIND_CODE = 700;
    public static final int REQUEST_CODE_FORWAED = 800;
    public static final int REQUEST_CODE_CARD_UPDATE = 900;


    //UI
    private RecyclerView recyclerView;
    LinearLayoutManagerWithSmoothScroller manager;
    public InputMessageLay keyboardLay;

    //Data.
    private IMListAdapter imListAdapter;

    private TextView tvTitle;
    private ImageView ivMore;
    private ImageView tvTitleRightImg;
    private String targetPhone = "";
    private String contactName;
    private int contactID;
    private long msgId;

    private int pageType;
    private boolean isOpenAudio = false;
    private boolean isOpenRemark = false;

    private boolean isPauseOut = false;
    private boolean isOpenEmotion = false;

    private boolean isSmoothBottom = true;//是否滑动完成
    private boolean canSliding = true;//是否正可以滑动最底

    private boolean isIMType = true;
    private int comeType;//分类界面 1: hook界面的IM图像跳过来的 2：
    private long mScrollPosition;

    private boolean isRemind = false;

    private BroadcastReceiver mUpdateContactNameReceiver;
    private BroadcastReceiver mReceiveSmsMsg;

    private boolean isDrivingMode = false;
    private boolean isMeetingMode = false;

    private BroadcastReceiver mUpdateImageStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("update_CacheMsgBean".equals(intent.getAction())) {
                CacheMsgBean cacheMsgBean = intent.getParcelableExtra("CacheMsgBean");
                List<CacheMsgBean> cacheMsgBeanList = imListAdapter.getmImBeanList();
                int size = cacheMsgBeanList.size();
                for (int i = 0; i < size; i++) {
                    CacheMsgBean oriMsgBean = cacheMsgBeanList.get(i);
                    if (cacheMsgBean.getId().equals(oriMsgBean.getId())) {
                        CacheMsgImage cacheImage = (CacheMsgImage) oriMsgBean.getJsonBodyObj();
                        cacheImage.setOriginalType(CacheMsgImage.SEND_IS_ORI_RECV_IS_ORI);
                        oriMsgBean.setJsonBodyObj(cacheImage);

                        //imListAdapter.update(oriMsgBean, i);
                        //imListAdapter.refreshItemUI(oriMsgBean);
                        CacheMsgHelper.instance(mContext).insertOrUpdate(oriMsgBean);
                        break;
                    }
                }
            }
        }
    };

    private LocalBroadcastManager localBroadcastManager;
    private DownloadBroadcastReceiver downloadBroadcastReceiver;

    private Context mContext;

    private int fromTo;
    private boolean isOriginal = false;

    private OnChatMsg onChatMsg = new OnChatMsg() {
        @Override
        public void onCallback(ChatMsg msg) {
            // 目前按监听器的机制，只用于控制通知栏
        }
    };

    private static final int MSG_GET_CONTACT_ID = 1000;

    private NormalHandler mHandler;

    private static class NormalHandler extends Handler {
        private final WeakReference<IMConnectionActivity> mTarget;

        NormalHandler(IMConnectionActivity target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            IMConnectionActivity act = mTarget.get();
            switch (msg.what) {
                case MSG_GET_CONTACT_ID:
                    act.contactID = act.getContactID(act.targetPhone, act.contactName);
                    break;
                default:
                    break;
            }
        }
    }


    public void setRemind(boolean remind) {
        isRemind = remind;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_fragment_im_main);
        mContext = this;

        mHandler = new NormalHandler(this);

        Intent fromIntent = getIntent();
        targetPhone = fromIntent.getStringExtra(DST_PHONE);
        isOpenAudio = fromIntent.getBooleanExtra(IS_SHOW_AUDIO, false);
        isOpenRemark = fromIntent.getBooleanExtra(IS_OPEN_REMARK, false);
        pageType = fromIntent.getIntExtra(DST_PAGE_TYPE, 0);
        isIMType = fromIntent.getBooleanExtra(IS_IM_TYPE, true);//是否沟通界面 默认true:沟通 false:问题反馈
        comeType = fromIntent.getIntExtra(ACTIVITY_COME_TYPE, -1);

        if (StringUtils.isEmpty(targetPhone)) {
            targetPhone = HuxinSdkManager.instance().getPhoneNum();
        }

        contactID = fromIntent.getIntExtra(DST_CONTACT_ID, 0);
        CallRecordUtil.clearMissedCalls(getApplicationContext(), targetPhone);//归零未接通话记录

        mScrollPosition = fromIntent.getLongExtra(EXTRA_SCROLL_POSITION, 0);

        mUpdateContactNameReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getStringExtra("action");
                if ("insert".equals(action)) {
                    ContactsDetailsBean contactsDetailsBean = intent.getParcelableExtra("bean");
                    if (contactsDetailsBean != null) {
                        for (ContactsDetailsBean.Phone phone : contactsDetailsBean.getPhone()) {
                            if (targetPhone.equals(phone.getPhone())) {
                                contactName = intent.getStringExtra("contactName");
                                contactID = intent.getIntExtra("contactID", contactID);
                                if (contactName != null && !TextUtils.isEmpty(contactName)) {
                                    if (tvTitle != null) {
                                        tvTitle.setText(contactName);
                                    }
                                }
                                break;
                            }
                        }
                    }
                } else if ("delete".equals(action)) {
                    int delectId = intent.getIntExtra("contactID", 0);
                    if (contactID == delectId) {
                        contactID = 0;
                        if (tvTitle != null) {
                            tvTitle.setText(targetPhone);
                        }
                    }
                } else {
                    contactName = intent.getStringExtra("contactName");
                    if (contactName != null && !TextUtils.isEmpty(contactName)) {
                        if (tvTitle != null) {
                            tvTitle.setText(contactName);
                        }
                    }
                }

            }
        };
        IntentFilter homeFilter = new IntentFilter("com.youmai.hxsdk.updatecontact");
        registerReceiver(mUpdateContactNameReceiver, homeFilter);

        mReceiveSmsMsg = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                CacheMsgBean cacheMsgBean = intent.getParcelableExtra("CacheMsgBean");
                if (cacheMsgBean != null) {
                    if (cacheMsgBean.getSenderPhone().equals(targetPhone)) {
                        imListAdapter.refreshIncomingMsgUI(cacheMsgBean, false);
                    }
                }

            }
        };
        IntentFilter homeFilter1 = new IntentFilter("com.youmai.hxsdk.receiveSmsMsg");
        registerReceiver(mReceiveSmsMsg, homeFilter1);

        //注册视频文件下载广播接收器
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        downloadBroadcastReceiver = new DownloadBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("download.service.video");
        filter.addAction("service.send.msg");
        filter.addAction("com.youmai.huxin.driving");
        localBroadcastManager.registerReceiver(downloadBroadcastReceiver, filter);
        localBroadcastManager.registerReceiver(mUpdateImageStateReceiver, new IntentFilter("update_CacheMsgBean"));

        if (fromIntent != null &&
                fromIntent.getBooleanExtra(IMConnectionActivity.EXTRA_IS_UNREAD, false)) {
            Intent intent = new Intent();
            intent.putExtra("updatePhone", targetPhone);
            setResult(Activity.RESULT_OK, intent);
        }

        initView();
        initData();
        IMMsgManager.getInstance().setImMsgCallback(this);

        HuxinSdkManager.instance().getStackAct().addActivity(this);

        fromTo = fromIntent.getIntExtra(FROM_TO, 0);
        //IMMsgManager.getInstance().stopDrivingMode();

        initFragment();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);

        /*fromTo = intent.getIntExtra(FROM_TO, 0);
        if (fromTo == FROM_HOOK_STRATE) {
            setSwapCard(1);
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();

        isDrivingMode = com.youmai.hxsdk.utils.AppUtils.getBooleanSharedPreferences(mContext, "huxin_driving_mode", false);
        isMeetingMode = com.youmai.hxsdk.utils.AppUtils.getBooleanSharedPreferences(mContext, "huxin_meeting_mode", false);
        keyboardLay.showDrivingLay(isDrivingMode);

        IMMsgManager.getInstance().setDrivingModePhone(targetPhone);

        //srsm add start
        imListAdapter.resume(contactID == 0);
        IMMsgManager.getInstance().setImMsgCallback(this);
        //srsm add end

        MediaManager.resume();
        isPauseOut = false;
        /*if (imListAdapter != null && !imListAdapter.getHasItemClick() && !isPauseOut) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mScrollPosition != 0) {
                        recyclerView.scrollToPosition(imListAdapter.getItemPosition(mScrollPosition));
                    } else {
                        imListAdapter.focusBottom(true);
                    }
                }
            }, 100);
            isPauseOut = false;
        }*/
        imListAdapter.setHasItemClick(false);
        if (isOpenAudio) {
            //只生效一次
            keyboardLay.showVoice(true);//开启录音状态
            isOpenAudio = false;
        }

        PickerManager.getInstance().setRefreshUIListener(this);
        IMMsgManager.getInstance().removeBadge(targetPhone);

        initLSListener();
    }

    public void handleIntent(Intent intent) {
        targetPhone = intent.getStringExtra(DST_PHONE);
        contactName = intent.getStringExtra(DST_NAME);

        if (!TextUtils.isEmpty(contactName)) {
            tvTitle.setText(contactName);
        } else if (TextUtils.isEmpty(targetPhone)) {
            tvTitle.setText(targetPhone);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (isOpenEmotion) {
            isOpenEmotion = false;
        } else {
            keyboardLay.hideAutoView();
            keyboardLay.setEditableState(false);
            keyboardLay.setMoreState(true);
        }
        MediaManager.pause();
        isPauseOut = true;
        //srsm add
        IMMsgManager.getInstance().removeImMsgCallback(this);

        // FIXME: 2017/1/9  三方电话时号码重置
        if (CallInfo.IsCalling()) {
            finish();
        }
        imListAdapter.toastHidden();
        imListAdapter.stopTextVoice();
    }

    @Override
    public void onStart() {
        super.onStart();
        isCancelled = false;
        // 目前按监听器的机制，只用于控制通知栏
        IMMsgManager.getInstance().registerChatMsg(onChatMsg);
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GET_PERMISSION_REQUEST) {
            int size = 0;
            if (grantResults.length >= 1) {
                int writeResult = grantResults[0];
                //读写内存权限
                boolean writeGranted = writeResult == PackageManager.PERMISSION_GRANTED;//读写内存权限
                if (!writeGranted) {
                    size++;
                }
                //录音权限
                int recordPermissionResult = grantResults[1];
                boolean recordPermissionGranted = recordPermissionResult == PackageManager.PERMISSION_GRANTED;
                if (!recordPermissionGranted) {
                    size++;
                }
                //相机权限
                int cameraPermissionResult = grantResults[2];
                boolean cameraPermissionGranted = cameraPermissionResult == PackageManager.PERMISSION_GRANTED;
                if (!cameraPermissionGranted) {
                    size++;
                }
                if (size == 0) {
                    startActivityForResult(new Intent(this, CameraActivity.class), 100);
                } else {
                    Toast.makeText(this, "请到设置-权限管理中开启", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //拦截适配器里更多状态,恢复原始消息状态
            if (imListAdapter != null && imListAdapter.isShowSelect) {
                imListAdapter.cancelMoreStat();
                setRightUi(false);
                return true;
            }
            closeTip();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {

        if (tvShareLocation.getVisibility() == View.VISIBLE && mFragment.isAdded()) {
            if (mFragment.isHidden()) {
                mFragment.initDialog(mContext);
            } else {
                hideFragment();
            }
            return;
        } else {
            if (mStopRefreshUIListener != null) {
                mStopRefreshUIListener.onQuit(null);
                endActualLocationShared();
            }
        }

        List<CacheMsgBean> lShareList = IMMsgManager.getInstance().getLShareList();
        for (CacheMsgBean bean : lShareList) {
            if (bean.getSenderPhone().equals(targetPhone)) {
                lShareList.remove(bean);
                break;
            }
        }

        if (isRemind) {
            setResult(Activity.RESULT_OK);
        }

        finish();

        if (!CallInfo.IsCalling()) {
            if (!HuxinSdkManager.instance().getStackAct().hasActivity("com.youmai.huxin.app.activity.MainAct")
                    || comeType == 1) {
                Intent huxinAppIntent = new Intent(Intent.ACTION_MAIN);
                huxinAppIntent.setClassName("com.youmai.huxin", "com.youmai.huxin.app.activity.MainAct");
                huxinAppIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                huxinAppIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(huxinAppIntent);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        MediaManager.release();
        imListAdapter.onStop();
        isCancelled = true;
        IMMsgManager.getInstance().unregisterChatMsg(onChatMsg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUpdateContactNameReceiver);
        unregisterReceiver(mReceiveSmsMsg);

        localBroadcastManager.unregisterReceiver(mUpdateImageStateReceiver);
        localBroadcastManager.unregisterReceiver(downloadBroadcastReceiver);
        localBroadcastManager = null;

        if (imListAdapter != null) {
            imListAdapter.unregisterAdapterDataObserver(mEmptyRecyclerViewDataObserver);
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.removeMessages(MSG_GET_CONTACT_ID);
        }

        PickerManager.getInstance().setRefreshUIListener(null);
        IMMsgManager.getInstance().setReceiveListener(null);

        //任务完成后，可以销毁消息服务
        Intent intent = new Intent(this, SendMsgService.class);
        intent.putExtra("flag", false);
        startService(intent);
        HuxinSdkManager.instance().getStackAct().finishActivity(this);

        PhotoPickerManager.getInstance().clearMap();

        IMMsgManager.getInstance().setDrivingModePhone("");
        //IMMsgManager.getInstance().stopDrivingMode();
    }


    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        if (tvTitle != null) {
            tvTitle.setText(targetPhone);
        }

        //srsm add start
        contactName = getIntent().getStringExtra(DST_NAME);
        if (contactName != null && !TextUtils.isEmpty(contactName)) {
            tvTitle.setText(contactName);
        }
        mHandler.sendEmptyMessageDelayed(MSG_GET_CONTACT_ID, 100);
        //srsm add end

        TextView tvBack = (TextView) findViewById(R.id.tv_back);
        if (tvBack != null) {
            //tvBack.setVisibility(View.GONE);
            tvBack.setText("");
            tvBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imListAdapter != null && imListAdapter.isShowSelect) {
                        imListAdapter.cancelMoreStat();
                        setRightUi(false);
                        return;
                    }
                    closeTip();
                }
            });
        }

        ivMore = (ImageView) findViewById(R.id.tv_title_more_img);
        if (ivMore != null) {
            ivMore.setVisibility(View.VISIBLE);
            ivMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent contactIntent = new Intent();
                    contactIntent.setAction("com.youmai.huxin.CotactDetails");
                    final PackageManager packageManager = getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(contactIntent, 0);
                    if (activities.size() > 0) {
                        showPopUp(ivMore);
                    }
                }
            });
        }

        tvTitleRightImg = (ImageView) findViewById(R.id.tv_title_right_img);
        if (tvTitleRightImg != null) {
            tvTitleRightImg.setVisibility(View.VISIBLE);
            //tvTitleRightImg.setText(R.string.hx_imadapter_call);
            tvTitleRightImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //HuxinSdkManager.instance().getStackAct().finishActivity(HookStrategyActivity.class);

                    int voipTime = SPDataUtil.getVoipDialogTimestamp(mContext);
                    String combo = SPDataUtil.getComboEnd(mContext);
                    if (TextUtils.isEmpty(combo) && voipTime != 0 && (TimeUtils.getNightTimestamp() - voipTime < 86400)) {
                        // 调系统拨号
                        try {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + targetPhone));
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getBaseContext(), R.string.hx_permissions_call_tip, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        keyboardLay = (InputMessageLay) findViewById(R.id.keyboard_lay);
        keyboardLay.setOnKeyBoardBarViewListener(this);
        keyboardLay.setEditableState(false);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isSmoothBottom) {
                        keyboardLay.hideAutoView();
                    }
                }
                return false;
            }
        });
        showToolsBar();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case SCROLL_STATE_IDLE:
                        if (!isSmoothBottom) {
                            isSmoothBottom = true;
                            if (imListAdapter.mThemeIndex != -1) {
                                imListAdapter.notifyItemChanged(imListAdapter.mThemeIndex);
                            }
//                            recyclerView.setOnTouchListener(new View.OnTouchListener() {
//                                @Override
//                                public boolean onTouch(View v, MotionEvent event) {
//                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                                        keyboardLay.hideAutoView();
//                                    }
//                                    return false;
//                                }
//                            });
                        }
//                        if (this != null)
//                            Glide.with(getActivity()).resumeRequests();
                        break;
                    case SCROLL_STATE_DRAGGING:
                    case SCROLL_STATE_SETTLING:
//                        if (this != null)
//                            Glide.with(getActivity()).pauseRequests();
                        break;
                }
            }
        });
    }

    private void initData() {
        manager = new LinearLayoutManagerWithSmoothScroller(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        imListAdapter = new IMListAdapter(this, recyclerView, targetPhone, contactName, isIMType);
        imListAdapter.setMoreListener(new IMListAdapter.OnClickMoreListener() {
            @Override
            public void showMore(boolean isShow) {
                setRightUi(isShow);
            }

            @Override
            public void hasSelectMsg(boolean selected) {
                if (selected) {
                    keyboardLay.changeMoreAction(true);
                } else {
                    //置灰
                    keyboardLay.changeMoreAction(false);
                }
            }
        });
        imListAdapter.registerAdapterDataObserver(mEmptyRecyclerViewDataObserver);
        recyclerView.setAdapter(imListAdapter);
        //打开备注
        if (isOpenRemark) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    imListAdapter.setHasOpenRemark(true);
                }
            }, 100);
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showNewGuide();
            }
        }, 300);

        msgId = getIntent().getLongExtra(MSG_ID, 0);

        if (mScrollPosition != 0) {
            recyclerView.scrollToPosition(imListAdapter.getItemPosition(mScrollPosition));
        } else if (msgId > 0) {
            int position = imListAdapter.getItemPositionByMsgId(msgId);
            if (position > 0) {
                recyclerView.scrollToPosition(position);
            } else {
                recyclerView.scrollToPosition(imListAdapter.getItemCount() - 1); // scroll to bottom
                imListAdapter.focusBottom(false, 30);
            }

        } else {
            recyclerView.scrollToPosition(imListAdapter.getItemCount() - 1); // scroll to bottom
            imListAdapter.focusBottom(false, 30);//scrollToPosition在item超长时，不能滑到最底，这里补救
        }
        imListAdapter.setListener(new IMListAdapter.OnListener() {
            @Override
            public void smoothScroll(final int position, long delayMillis, final boolean isScrollerTop) {
                //滑动冲突
//                recyclerView.setOnTouchListener(new View.OnTouchListener() {
//
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        return false;
//                    }
//                });
                isSmoothBottom = false;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (recyclerView.getLayoutManager() instanceof LinearLayoutManagerWithSmoothScroller) {
                            ((LinearLayoutManagerWithSmoothScroller) recyclerView.getLayoutManager()).setScrollerTop(false);
                        }
                        recyclerView.smoothScrollToPosition(position);
                    }
                }, delayMillis);
            }

            @Override
            public void hasEdit(boolean hasEdit) {
                canSliding = !hasEdit;
            }

            @Override
            public void hidden(final boolean hidden) {
                if (keyboardLay != null) {
                    keyboardLay.hideAutoView();
                }
            }

            @Override
            public void deleteMsgCallback(int type) {
                if (!isFinishing()) {
                    Intent intent = new Intent();
                    intent.putExtra("updatePhone", targetPhone);
                    intent.putExtra("isDeleteMsgType", type);
                    setResult(Activity.RESULT_OK, intent);
                }
            }

            @Override
            public void onHandleAvatarClick() {
                handlerContact();
            }
        });
    }

    PopupWindow popupWindow;
    long i = 0;

    private void handlerContact() {
        Intent intent = new Intent();
        if (contactID == 0) {
            //intent.setClassName(this, "com.youmai.huxin.app.activity.NewContactsActivity");
            intent.setAction("com.youmai.huxincontacts.action.newcontacts");
            intent.putExtra("contacts_operate", 2);
            intent.putExtra("contacts_phone", targetPhone);
        } else {
            intent.setAction("com.youmai.huxin.CotactDetails");
            intent.putExtra("contactID", contactID);
            intent.putExtra("phone", targetPhone);
            intent.putExtra("name", contactName);
        }
        startActivity(intent);
    }


    private void showPopUp(View v) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.hx_im_more_lay, null);
        if (popupWindow == null) {
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(v);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.im_more_contact_lay);
        View view1 = view.findViewById(R.id.im_more_line);
        View popupWindowView = popupWindow.getContentView();
        TextView contactText = (TextView) popupWindowView.findViewById(R.id.im_more_contact_text);
        if (contactID == 0) {
            contactText.setText(R.string.hx_im_pop_contact_add);
        } else {
            contactText.setText(R.string.hx_im_pop_contact_information);
        }
        if (1 == pageType) {
            layout.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.VISIBLE);
            view1.setVisibility(View.VISIBLE);
        }
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i + 2000 < System.currentTimeMillis()) {
                    i = System.currentTimeMillis();
                    //跳转联系人
                    handlerContact();
                    popupWindow.dismiss();
                }

            }
        });

        //驾驶模式
        TextView drivingModeButton = (TextView) popupWindowView.findViewById(R.id.im_driving_mode_button);
        ImageView drivingModeIcon = (ImageView) popupWindowView.findViewById(R.id.im_driving_mode_icon);
        isDrivingMode = com.youmai.hxsdk.utils.AppUtils.getBooleanSharedPreferences(mContext, "huxin_driving_mode", false);
        if (isDrivingMode) {
            drivingModeButton.setText(R.string.hx_meeting_mode_off);
            drivingModeIcon.setImageResource(R.drawable.im_more_icon_driving_mode_on);
        } else {
            drivingModeButton.setText(R.string.hx_meeting_mode_on);
            drivingModeIcon.setImageResource(R.drawable.im_more_icon_driving_mode_off);
        }
        view.findViewById(R.id.im_driving_mode_lay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDrivingMode) {
                    keyboardLay.showDrivingLay(true);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollBy(0, DisplayUtil.dip2px(mContext, 150), new DecelerateInterpolator());
                        }
                    }, 300);

                    isDrivingMode = true;
                    isMeetingMode = false;
                    Intent intent = new Intent("com.youmai.huxin.drivingmode.open");
                    sendBroadcast(intent);

                } else {
                    keyboardLay.showDrivingLay(false);
                    ToastUtil.showPublicToast2(getBaseContext(), R.string.hx_im_driving_exit_tip, R.drawable.hx_driving_exit_ic);
                    isDrivingMode = false;
                    Intent intent = new Intent("com.youmai.huxin.drivingmode.close");
                    sendBroadcast(intent);
                }
                popupWindow.dismiss();
            }
        });


        //会议模式
        TextView meetingModeButton = (TextView) popupWindowView.findViewById(R.id.im_meeting_mode_button);
        ImageView meetingModeIcon = (ImageView) popupWindowView.findViewById(R.id.im_meeting_mode_icon);
        isMeetingMode = com.youmai.hxsdk.utils.AppUtils.getBooleanSharedPreferences(mContext, "huxin_meeting_mode", false);
        if (isMeetingMode) {
            meetingModeButton.setText(R.string.hx_im_pop_close_meeting_mode);
            meetingModeIcon.setImageResource(R.drawable.im_more_icon_meeting_mode_on);
        } else {
            meetingModeButton.setText(R.string.hx_im_pop_open_meeting_mode);
            meetingModeIcon.setImageResource(R.drawable.im_more_icon_meeting_mode_off);
        }
        view.findViewById(R.id.im_meeting_mode_lay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启会议模式
                if (isMeetingMode) {
                    isMeetingMode = false;
                    ToastUtil.showPublicToast2(getBaseContext(), R.string.hx_im_pop_close_meeting_mode, R.drawable.hx_meeting_exit_ic);
                    //驾驶模式和会议模式是互斥的
                    com.youmai.hxsdk.utils.AppUtils.setBooleanSharedPreferences(mContext, "huxin_meeting_mode", false);
                } else {
                    isMeetingMode = true;
                    isDrivingMode = false;
                    //驾驶模式和会议模式是互斥的
                    com.youmai.hxsdk.utils.AppUtils.setBooleanSharedPreferences(mContext, "huxin_meeting_mode", true);
                    Intent intent = new Intent("com.youmai.huxin.drivingmode.close");
                    intent.putExtra("type", "close_driving_ui");
                    sendBroadcast(intent);
                    IMMsgManager.getInstance().stopDrivingMode();

                }
                popupWindow.dismiss();
            }
        });
    }


    private ArrayList<String> docPaths = new ArrayList<>();

    /**
     * 文件选择器
     */
    private void showFileChooser() {
//        docPaths.clear();
//        String[] docs = {".txt", ".docx", ".doc", ".ppt", ".pptx", ".xls", ".xlsx", ".pdf"};
//        String[] videos = {".mp4"/*, ".rmvb", ".avi", ".3gp"*/};
//        String[] audios = {".mp3"};
//        String[] zips = {".zip", ".rar"};
//        //String[] apks = {".apk"};
//
//        FilePickerBuilder.getInstance().setMaxCount(1)
//                .setSelectedFiles(docPaths)
//                .setActivityTheme(R.style.HxSdkTheme)
//                .addFileSupport(getString(R.string.doc), docs)
//                .addFileSupport(getString(R.string.video), videos)
//                .addFileSupport(getString(R.string.audio), audios)
//                .addFileSupport(getString(R.string.zip), zips)
//                //.addFileSupport(getString(R.string.apk), apks)
//                .enableDocSupport(false)
//                .pickFile(this);

        docPaths.clear();
        PickerManager.getInstance().addDocTypes();
        Intent intent = new Intent(this, FileManagerActivity.class);
        intent.putExtra("dstPhone", targetPhone);
        startActivity(intent);
    }


    //发送消息(启动后台消息服务)
    private void sendMsg(CacheMsgBean msg) {
        Intent intent = new Intent(this, SendMsgService.class);
        intent.putExtra("data", msg);
        intent.putExtra("data_from", SendMsgService.FROM_IM);
        startService(intent);
    }

    private CacheMsgBean getBaseMsg() {
        return new CacheMsgBean()
                .setMsgTime(System.currentTimeMillis())
                .setSend_flag(-1)
                .setSenderPhone(HuxinSdkManager.instance().getPhoneNum())
                .setSenderUserId(HuxinSdkManager.instance().getUserId())
                .setReceiverPhone(targetPhone)
                .setRightUI(true);
    }

    /**
     * 发送文字(或表情)
     */
    private void sendTxt(final String content, int refContent, boolean isInput) {
        CacheMsgBean cacheMsgBean = getBaseMsg();
        if (isInput) {
            cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_TXT).setJsonBodyObj(new CacheMsgTxt().setMsgTxt(content));
        } else {
            cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_EMOTION).setJsonBodyObj(new CacheMsgEmotion().setEmotion(content, refContent));
        }

        imListAdapter.addAndRefreshUI(cacheMsgBean);
        sendMsg(cacheMsgBean);
    }


    /**
     * 发送位置
     */
    public void sendMap(Intent data) {
        final String url = data.getStringExtra("url");
        final double longitude = data.getDoubleExtra("longitude", 0);
        final double latitude = data.getDoubleExtra("latitude", 0);
        final int zoomLevel = data.getIntExtra("zoom_level", 0);
        final String address = data.getStringExtra("address");

        CacheMsgBean cacheMsgBean = getBaseMsg();
        cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_MAP)
                .setJsonBodyObj(new CacheMsgMap()
                        .setLocation(longitude + "," + latitude)
                        .setAddress(address)
                        .setImgUrl(url));

        imListAdapter.addAndRefreshUI(cacheMsgBean);
        sendMsg(cacheMsgBean);
    }

    /**
     * 发送语音
     *
     * @param seconds
     * @param filePath
     */
    private void sendVoice(float seconds, String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return;
        }
        CacheMsgBean cacheMsgBean = getBaseMsg();
        cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_VOICE)
                .setJsonBodyObj(new CacheMsgVoice().setVoiceTime(seconds + "").setVoicePath(filePath));

        imListAdapter.addAndRefreshUI(cacheMsgBean);
        sendMsg(cacheMsgBean);
    }

    /**
     * 相册选择回调或拍照回调
     *
     * @param path
     */
    private void sendTakenPic(String path, boolean isOriginal) {
        CacheMsgBean cacheMsgBean = getBaseMsg();
        cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_IMG)
                .setJsonBodyObj(new CacheMsgImage()
                        .setFilePath(path)
                        .setOriginalType(isOriginal ? CacheMsgImage.SEND_IS_ORI : CacheMsgImage.SEND_NOT_ORI));

        imListAdapter.addAndRefreshUI(cacheMsgBean);
        sendMsg(cacheMsgBean);
    }

    /**
     * 发送文件.
     *
     * @param file
     */

    public void sendFile(File file) {
        CacheMsgBean cacheMsgBean = getBaseMsg();
        cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_FILE)
                .setJsonBodyObj(new CacheMsgFile()
                        .setFilePath(file.getAbsolutePath())
                        .setFileSize(file.length())
                        .setFileName(file.getName())
                        .setFileRes(IMHelper.getFileImgRes(file.getName(), false)));

        imListAdapter.addAndRefreshUI(cacheMsgBean);
        sendMsg(cacheMsgBean);
    }

    /**
     * 发送视频
     */
    public void sendVideo(String filePath, String framePath, long millisecond) {
        CacheMsgBean cacheMsgBean = getBaseMsg();
        cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_VIDEO)
                .setJsonBodyObj(new CacheMsgVideo()
                        .setVideoPath(filePath)
                        .setFramePath(framePath)
                        .setTime(millisecond));

        imListAdapter.addAndRefreshUI(cacheMsgBean);
        sendMsg(cacheMsgBean);
    }

    /**
     * 发送分享名片
     */
    public void sendCard(final ContactsDetailsBean cardModel) {

        if (cardModel != null) {
            CacheMsgBean cacheMsgBean = getBaseMsg();
            cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_BIZCARD).setJsonBodyObj(cardModel);

            imListAdapter.addAndRefreshUI(cacheMsgBean);
            sendMsg(cacheMsgBean);
        } else {
            ToastUtil.showToast(this, getString(R.string.swap_card_failure));
        }
    }

    /**
     * 发送备注.
     */
    public void sendRemark(final String theme, String remark, long time) {
        CacheMsgBean cacheMsgBean = getBaseMsg();
        CacheMsgRemark cacheMsgRemark = new CacheMsgRemark().setTheme(theme).setRemark(remark).setTimestamp(time).setType(0);
        cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_REMARK).setJsonBodyObj(cacheMsgRemark);

        imListAdapter.addAndRefreshUI(cacheMsgBean);
        sendMsg(cacheMsgBean);
    }


    //拍照
    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager
                    .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager
                            .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager
                            .PERMISSION_GRANTED) {
                startActivityForResult(new Intent(this, CameraActivity.class), REQUEST_CODE_CAMERA);
            } else {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA}, GET_PERMISSION_REQUEST);
            }
        } else {
            startActivityForResult(new Intent(this, CameraActivity.class), REQUEST_CODE_CAMERA);
        }
    }


    /**
     * 发位置
     */
    private void sendLocation() {
        Intent intent = new Intent();
        intent.putExtra("is_user_by_im", true);
        //intent.setClass(this, LocationActivity.class);
        intent.setClass(this, LocationActivity.class);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PhotoPreviewActivity.REQUEST_CODE_PHOTO) { //图片
            ArrayList<String> photoPaths = new ArrayList<>();
            if (data == null) {
                isOriginal = PhotoPickerManager.getInstance().isOriginal();
                photoPaths.addAll(PhotoPickerManager.getInstance().getPaths());
            } else {
                isOriginal = data.getBooleanExtra(FilePickerConst.KEY_IS_ORIGINAL, false);
                photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
            }
            PhotoPickerManager.getInstance().clear();

            if (photoPaths == null || photoPaths.size() == 0) {
                //ToastUtil.showToast(this, getString(R.string.hx_toast_70));
                return;
            }

            String path = photoPaths.get(0);
            if (path.toLowerCase().endsWith(".mp4") || path.toLowerCase().endsWith(".rmvb")
                    || path.toLowerCase().endsWith(".avi") || path.toLowerCase().endsWith(".3gp")) {
                new CompressVideoAsyncTask().execute(path);
            } else {
                if (isOriginal) {
                    for (String item : photoPaths) {
                        sendTakenPic(item, isOriginal);
                    }
                } else {
                    //final ArrayList<String> thumbList = new ArrayList<>();
                    final int size = photoPaths.size();
                    for (int i = 0; i < size; i++) {
                        //final int index = i;
                        Luban.with(mContext)
                                .load(photoPaths.get(i))
                                .ignoreBy(100)
                                .setTargetDir(FileConfig.getThumbImagePaths())
                                .setCompressListener(new OnCompressListener() {
                                    @Override
                                    public void onStart() {

                                    }

                                    @Override
                                    public void onSuccess(File file) {
                                        LogUtils.d("wul", "getImageThumb --- onSuccess:" + file.getAbsolutePath());
                                        /*thumbList.add(file.getAbsolutePath());
                                        if (index == (size - 1)) {
                                            new CompressImageAsyncTask().execute(thumbList.toArray(new String[thumbList.size()]));
                                        }*/
                                        sendTakenPic(file.getAbsolutePath(), isOriginal);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        LogUtils.d("wul", "getImageThumb --- onError:" + e.getMessage());
                                    }
                                }).launch();
                    }
                }
            }

        } else if (requestCode == 1 && resultCode == 1) { //图片
            final String filePath = data.getStringExtra("path");
            sendTakenPic(filePath, isOriginal);
        } else if (requestCode == 2 && resultCode == 1) {  //地图
            sendMap(data);
        } else if (requestCode == FilePickerConst.REQUEST_CODE_DOC && resultCode == Activity.RESULT_OK) {
            //交换名片编辑名片返回 发送(用不到)
        } else if (requestCode == SWAP_CARD_EQUEST && resultCode == Activity.RESULT_OK) {
            ContactsDetailsBean contactsDetailsBean = data.getParcelableExtra("ContactsDetailsBean");
            LogUtils.e("ContactsDetailsBean", contactsDetailsBean.toString());
            sendCard(contactsDetailsBean);
        } else if (requestCode == SAVE_CARD_EQUEST && resultCode == Activity.RESULT_OK) {
            //刷新界面
            ContactsDetailsBean contactsDetailsBean = data.getParcelableExtra("ContactsDetailsBean");
            int beanPos = data.getIntExtra("MsgBeanPosition", -1);
            imListAdapter.refreshItemUI(beanPos, contactsDetailsBean);
            //TODO 去掉名片交换提示窗 2017-10-27

        } else if (requestCode == SWAP_CARD_EQUEST && resultCode == Activity.RESULT_CANCELED) {
            if (fromTo == FROM_HOOK_STRATE) {
                finish();
            }
        } else if (requestCode == REQUEST_CODE_CAMERA) {
            //拍照回来后
            if (resultCode == 101) {
                //图片
                String path = data.getStringExtra("filePath");
                sendTakenPic(path, isOriginal);
            } else if (resultCode == 102) {
                //视频
                String framePath = data.getStringExtra("framePath");
                String path = data.getStringExtra("filePath");
                long millisecond = VideoUtils.instance(getBaseContext()).getVideoTime(path);
                if (millisecond == 0L) {
                    //拿不到系统读取时间，再用自定义计算时间
                    millisecond = data.getLongExtra("millisecond", 0L);
                }
                sendVideo(path, framePath, millisecond);
            } else if (resultCode == 103) {
                Toast.makeText(this, "相机有误，请返回重试!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_CARD) {
            if (resultCode == 200) {
                ContactsDetailsBean bean = data.getParcelableExtra("data");
                showDialog(bean);
            }
        } else if (requestCode == REQUEST_REMIND_CODE) {
            if (resultCode == SetRemindActivity.RESULT_REMIND_CODE) {
                long msgId = data.getLongExtra("msg_id", 0);
                long remindTime = data.getLongExtra("remind_time", 0);
                String remind = data.getStringExtra("remind");
                int iconNumRes = data.getIntExtra("remindType", 0);

                setRemind(true);
                imListAdapter.setRemind(msgId, remindTime, remind, iconNumRes);

                RemindBean remindBean = data.getParcelableExtra(SetRemindActivity.REMIND_BEAN);
                if (!SPDataUtil.getFirstRemind(mContext)) {
                    HxFirstRemindDialog dialog = new HxFirstRemindDialog(mContext);
                    dialog.show();
                    dialog.setMessage(remindBean.getTime())
                            .setSureClickListener(new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    SPDataUtil.setFirstRemind(mContext, true);
                }
            }
        } else if (requestCode == REQUEST_CODE_FORWAED && resultCode == 200) {
            //批量转发后的回调
            imListAdapter.cancelMoreStat();

            setRightUi(false);
        } else if (requestCode == REQUEST_CODE_CARD_UPDATE && resultCode == 200) {

        } else if (requestCode == REQUEST_CODE_ADDEMO && resultCode == Activity.RESULT_OK) {
            //colin
            keyboardLay.refreshEmotion();
        }
    }


    /**
     * 发送名片的弹窗
     *
     * @param data 名片的数据
     */
    void showDialog(final ContactsDetailsBean data) {

    }

    //获取号码归属地
    void getPhoneAddress(String phone) {

    }

    //返回事件
    void closeTip() {
        if (isSend) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCancelable(false)// 屏蔽返回键
                    .setTitle(getString(R.string.hx_prompt))
                    .setMessage(getString(R.string.hx_upload_file));

            builder.setPositiveButton(getString(R.string.hx_imadapter_sure),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0,
                                            int arg1) {
                            arg0.dismiss();
                            finish();
                        }
                    });

            builder.setNegativeButton(getString(R.string.hx_imadapter_cancle),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0,
                                            int arg1) {
                            arg0.dismiss();
                        }
                    });
            builder.show();
        } else if (imListAdapter.isEditingRemark()) {
            keyboardLay.hideAutoView();
        } else {
            onBackPressed();
        }
    }

    @Override
    public void onRefresh(ArrayList<String> paths, int resultCode) {
        Log.e("YW", "resultCode: " + resultCode);
        if (paths != null && paths.size() > 0 && resultCode == FilePickerConst.IM_REQUEST_CALLBACK) {
            final File file = new File(paths.get(0));
            if (file.exists()) {
                if (file.length() > MAX_SENDER_FILE) {
                    Toast.makeText(this, R.string.hx_imadapter_file, Toast.LENGTH_SHORT).show();
                } else {
                    if (!CommonUtils.isNetworkAvailable(this)) {
                        ToastUtil.showToast(this, getString(R.string.hx_imadapter_wifi_break));
                        return;
                    }
                    sendFile(file);
                }
            } else {
                Toast.makeText(this, getString(R.string.hx_toast_22), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCallback(CacheMsgBean cacheMsgBean) {
        //刷新界面
        if (this != null && !isFinishing()) {
            if (cacheMsgBean.getSenderPhone().equals(targetPhone))
                imListAdapter.refreshIncomingMsgUI(cacheMsgBean, isMeetingMode);
        }
    }

    // 初始化、执行上传
    private volatile boolean isCancelled = false;
    private volatile boolean isSend = false;


    /**
     * 消息广播
     * 下载视频文件本地广播接收器
     */
    private class DownloadBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("download.service.video".equals(action)) {
                //视频下载的通知
                FileQueue fileQueue = intent.getParcelableExtra("data");
                String phone = fileQueue.getPhone();
                int pro = fileQueue.getPro();
                if (TextUtils.equals(targetPhone, phone)) {
                    if (pro == 100) {
                        //下载完成,刷新ui
                        refreshFinishVideo(fileQueue);
                    } else if (pro == -1) {
                        //下载失败
                        Toast.makeText(context, "下载失败，请重新尝试", Toast.LENGTH_SHORT).show();
                        imListAdapter.refreshItemUI(fileQueue.getMid(), fileQueue.getPro());//不用查数据库
                    } else {
                        imListAdapter.refreshItemUI(fileQueue.getMid(), fileQueue.getPro());//不用查数据库
                    }
                }
            } else if ("service.send.msg".equals(action)) {
                //消息发送的通知
                SendMsg sendMsg = intent.getParcelableExtra("data");
                CacheMsgBean cacheMsgBean = sendMsg.getMsg();
                imListAdapter.refreshItemUI(cacheMsgBean);

                if (!isPauseOut && TextUtils.equals(sendMsg.getFrom(), SendMsgService.FROM_IM)) {
                    String type = intent.hasExtra("type") ? intent.getStringExtra("type") : null;
                    if (TextUtils.equals(type, SendMsgService.NOT_NETWORK)) {
                        //无网络状态下
                        sendSms2(cacheMsgBean, imListAdapter.getPos(cacheMsgBean));
                    } else if (TextUtils.equals(type, SendMsgService.NOT_HUXIN_USER)) {
                        //非呼信用户
                        sendSms(cacheMsgBean, imListAdapter.getPos(cacheMsgBean));
                    } else if (TextUtils.equals(type, SendMsgService.NOT_TCP_CONNECT)) {
                        //tcp尚未连接
                        showTcpTipDialog();
                    }
                }
            } else if ("com.youmai.huxin.driving".equals(action)) {
                //退出
                keyboardLay.showDrivingLay(false);
            }
        }
    }

    //发送短信(发送失败状态)
    private void sendSms(CacheMsgBean cacheMsgBean, int position) {
        int msgType = cacheMsgBean.getMsgType();
        if (msgType == CacheMsgBean.MSG_TYPE_TXT
                || msgType == CacheMsgBean.MSG_TYPE_MAP
                || msgType == CacheMsgBean.MSG_TYPE_IMG
                || msgType == CacheMsgBean.MSG_TYPE_VOICE
                || msgType == CacheMsgBean.MSG_TYPE_VIDEO
                || msgType == CacheMsgBean.MSG_TYPE_FILE
                || msgType == CacheMsgBean.MSG_TYPE_BIZCARD) {
            SmsManager smsManager = new SmsManager(IMConnectionActivity.this, new SmsManager.Listener() {
                @Override
                public void sendNotify(int position) {
                    imListAdapter.notifyItemChanged(position);
                }
            });
            smsManager.showNotHuxinUserDialog(cacheMsgBean, position);
        } else {
            Toast.makeText(mContext, "对方非呼信用户", Toast.LENGTH_SHORT).show();
        }
    }

    //无网络发送短信(发送失败状态)
    private void sendSms2(CacheMsgBean cacheMsgBean, int position) {
        int msgType = cacheMsgBean.getMsgType();
        if (msgType == CacheMsgBean.MSG_TYPE_TXT) {
            SmsManager smsManager = new SmsManager(IMConnectionActivity.this, new SmsManager.Listener() {
                @Override
                public void sendNotify(int position) {
                    imListAdapter.notifyItemChanged(position);
                }
            });
            smsManager.showNotNetHuxinUserDialog(cacheMsgBean, position);
        } else {
            Toast.makeText(mContext, "无网络状态下", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTcpTipDialog() {
    }

    private void refreshFinishVideo(FileQueue fileQueue) {
        long mid = fileQueue.getMid();
        CacheMsgBean cacheMsgBean = CacheMsgHelper.instance(this).queryByID(mid);
        if (cacheMsgBean.getJsonBodyObj() instanceof CacheMsgVideo) {
            CacheMsgVideo cacheMsgVideo = (CacheMsgVideo) cacheMsgBean.getJsonBodyObj();
            cacheMsgVideo.setProgress(fileQueue.getPro());
            cacheMsgBean.setJsonBodyObj(cacheMsgVideo);
            imListAdapter.refreshItemUI(cacheMsgBean);
        }
    }


    /**
     * 监听软件的状态
     */
    @Override
    public void OnKeyBoardStateChange(int state, int height) {
        if (state == InputMessageLay.KEYBOARD_STATE_NONE) {
            //键盘隐藏
        } else if (state == InputMessageLay.KEYBOARD_STATE_FUNC) {
            //输入布局显示
            if (isSmoothBottom && canSliding) {
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        imListAdapter.focusBottom(false);
                    }
                });
            }
        } else {
            //键盘显示
            if (isSmoothBottom && canSliding) {
                if (height > 0) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    imListAdapter.focusBottom(false);
                                }
                            }, 300);
                        }
                    });
                } else if (height == -1 && keyboardLay.keyHasFocus()) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    imListAdapter.focusBottom(false);
                                }
                            }, 300);
                        }
                    });
                }
            }

        }
    }

    /**
     * 输入框点击发送语音
     */
    @Override
    public void onKeyBoardVoice(String voiceFile, int time) {
        sendVoice(time, voiceFile);
    }

    /**
     * 输入框点击发送信息
     *
     * @param msg 输入的内容
     */
    @Override
    public void onKeyBoardSendMsg(String msg) {
        sendTxt(msg, -1, true);
    }

    /**
     * 输入框点击添加表情
     */
    @Override
    public void onKeyBoardAddEmotion() {
    }

    /**
     * 输入框点击发送表情
     */
    @Override
    public void onKeyBoardEmotion(String content, int refContent) {
        sendTxt(content, refContent, false);
    }

    @Override
    public void onScroll() {
        imListAdapter.focusBottom(false);
    }

    /**
     * 输入框点击更多菜单的点击事件
     */
    @Override
    public void onKeyBoardMore(int type) {
        if (type == InputMessageLay.TYPE_PHOTO) {
            PhotoPreviewActivity.start(this);
        } else if (type == InputMessageLay.TYPE_CAMERA) {
            useCamera();
        } else if (type == InputMessageLay.TYPE_LOCATION) {

            HxDialog hxDialog = new HxDialog(mContext);
            HxDialog.HxCallback callback =
                    new HxDialog.HxCallback() {
                        @Override
                        public void onItemOne() {
                            sendLocation();
                        }

                        @Override
                        public void onItemTwo() {
                            if (mFragment != null && !mFragment.isAdded()) {
                                CacheMsgBean cacheMsgBean = new CacheMsgBean()
                                        .setMsgTime(System.currentTimeMillis())
                                        .setSend_flag(-1)
                                        .setSenderPhone(HuxinSdkManager.instance().getPhoneNum())
                                        .setSenderUserId(HuxinSdkManager.instance().getUserId())
                                        .setReceiverPhone(targetPhone)
                                        .setMsgType(CacheMsgBean.MSG_TYPE_LOCATION_SHARE)
                                        .setJsonBodyObj(new CacheMsgLShare()
                                                .setTargetId(System.currentTimeMillis())
                                                .setReceivePhone(targetPhone))
                                        .setRightUI(true);

                                ActualLocation.setStatus(ActualLocationFragment.LShareStatus.INVITE.ordinal());
                                ActualLocation.setLSharePhone(targetPhone);
                                ActualLocation.setInviteCacheMsgBean(cacheMsgBean);
                                imListAdapter.addAndRefreshUI(cacheMsgBean);
                            }

                            showFragment();
                            keyboardLay.hideAutoView();
                        }
                    };
            hxDialog.setItemOneString("发送位置")
                    .setItemTwoString("共享实时位置", targetPhone.equals(HuxinSdkManager.instance().getPhoneNum()))
                    .setHxDialog(callback);
            hxDialog.show();

        } else if (type == InputMessageLay.TYPE_FILE) {
            showFileChooser();
        } else if (type == InputMessageLay.TYPE_CARD) {
            //分享名片
            try {
                Intent intent = new Intent();
                intent.setAction("com.youmai.huxin.select.card");
                intent.putExtra("disName", tvTitle.getText().toString());
                intent.putExtra("targetPhone", targetPhone);
                startActivityForResult(intent, REQUEST_CODE_CARD);
            } catch (Exception e) {
                Toast.makeText(mContext, "tan90", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onClickVoice() {
        //点击录音，停止语音播放
        imListAdapter.stopTextVoice();
        //驾驶模式停止语音播放
        DrivingModeMediaManager.release();
    }

    @Override
    public void onMoreForward() {
        TreeMap<Integer, CacheMsgBean> selectMsg = imListAdapter.getSelectMsg();
        ArrayList<CacheMsgBean> msgList = new ArrayList<>();
        for (Object o : selectMsg.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            CacheMsgBean val = (CacheMsgBean) entry.getValue();
            msgList.add(val);
        }

        Intent intent = new Intent();
        intent.setAction("com.youmai.huxin.recent");
        intent.putExtra("type", "forward_msg_list");
        intent.putParcelableArrayListExtra("dataList", msgList);
        startActivityForResult(intent, REQUEST_CODE_FORWAED);
    }

    @Override
    public void onMoreGarbage() {

    }

    @Override
    public void onDrivingExit() {
        ToastUtil.showPublicToast2(getBaseContext(), R.string.hx_im_driving_exit_tip, R.drawable.hx_driving_exit_ic);
        Intent intent = new Intent("com.youmai.huxin.drivingmode.close");
        sendBroadcast(intent);
    }


    /**
     * 是否显示标题栏右边的按钮
     * 问题反馈不需要显示
     */
    private void showToolsBar() {
        if (isIMType) {
            ivMore.setVisibility(View.VISIBLE);
            tvTitleRightImg.setVisibility(View.VISIBLE);
        } else {
            ivMore.setVisibility(View.GONE);
            tvTitleRightImg.setVisibility(View.GONE);
        }
    }

    private EmptyRecyclerViewDataObserver mEmptyRecyclerViewDataObserver = new EmptyRecyclerViewDataObserver();

    private void showNewGuide() {

    }

    /**
     * 从通讯录获取ID
     *
     * @param phone
     * @return
     */
    public int getContactID(String phone, String name) {
        String nickName = name;
        int contactId = 0;
        ContentResolver resolver = getContentResolver();
        Cursor cursor = null;
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        try {
            cursor = resolver.query(
                    uri,
                    projection,
                    "replace(replace(replace(" + ContactsContract.CommonDataKinds.Phone.NUMBER + ",' ', '') ,'-',''),'+86','')" + "='" + phone + "'",
                    null,
                    null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    nickName = cursor.getString(2);
                    if (nickName.trim().equals(name.trim())) {
                        contactId = cursor.getInt(0);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return contactId;
    }


    private class EmptyRecyclerViewDataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            showNewGuide();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            showNewGuide();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            showNewGuide();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {

        }

    }


    /**
     * 压缩图片
     */
    private class CompressImageAsyncTask extends AsyncTask<String[], Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String[]... params) {
            ArrayList<String> res = new ArrayList<>();
            for (String item : params[0]) {
                res.add(CompressImage.compressImage(item));

                if (!isOriginal) {
                    File file = new File(item);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
            return res;
        }

        @Override
        protected void onPostExecute(ArrayList<String> res) {
            for (String item : res) {
                if (TextUtils.isEmpty(item)) {
                    Toast.makeText(mContext, "未找到文件", Toast.LENGTH_SHORT).show();
                } else {
                    sendTakenPic(item, isOriginal);
                }

            }
        }
    }

    /**
     * 压缩视频
     */
    private class CompressVideoAsyncTask extends AsyncTask<String, Void, OnlyCompressOverBean> {
        @Override
        protected void onPreExecute() {
            showProgress("", "视频压缩中...", -1);
        }

        @Override
        protected OnlyCompressOverBean doInBackground(String... params) {
            String url = params[0];
            if (TextUtils.isEmpty(url)) {
                return null;
            } else {
                File video = new File(url);
                if (!video.exists()) {
                    return null;
                }

                OnlyCompressOverBean bean = CompressVideo.compressVideo(params[0]);
                String videoPath = AbFileUtil.renameVideoTo2(bean.getVideoPath());
                String picPath = AbFileUtil.renamePicTo2(bean.getPicPath());

                if (videoPath.toLowerCase().endsWith(".m")) {
                    try {
                        String[] videoParams = MediaStoreUtils.getVideoParams(videoPath);
                        long time = Long.parseLong(videoParams[0]);
                        bean.setVideoTime(time);
                        bean.setVideoPath(videoPath);
                        bean.setPicPath(picPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return bean;
            }
        }

        @Override
        protected void onPostExecute(OnlyCompressOverBean res) {
            hideProgress();
            if (res == null) {
                Toast.makeText(mContext, "未找到文件", Toast.LENGTH_SHORT).show();
            } else {
                sendVideo(res.getVideoPath(), res.getPicPath(), res.getVideoTime());
            }

        }
    }

    private ProgressDialog mProgressDialog;

    private void showProgress(String title, String message, int theme) {
        if (mProgressDialog == null) {
            if (theme > 0)
                mProgressDialog = new ProgressDialog(this, theme);
            else
                mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
        }

        if (!TextUtils.isEmpty(title))
            mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        if (Build.VERSION.SDK_INT >= 17 && !isDestroyed() && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    /**
     * 显示更多后的界面变化
     *
     * @param isShow 更多选择:true
     */
    private void setRightUi(boolean isShow) {
        tvTitleRightImg.setVisibility(isShow ? View.GONE : View.VISIBLE);
        ivMore.setVisibility(isShow ? View.GONE : View.VISIBLE);
        keyboardLay.changeMoreLay(isShow);
    }

    //*****************start
    private ActualLocationFragment mFragment;
    private TextView tvShareLocation;
    private AbstractStartOrQuit mStopRefreshUIListener;

    private void initFragment() {
        tvShareLocation = (TextView) findViewById(R.id.tv_share_location);
        mFragment = ActualLocationFragment.newInstance();
        ActualLocation.setLSharePhone(targetPhone);

        tvShareLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActualLocation.getStatus() == ActualLocationFragment.LShareStatus.INVITE.ordinal()) {

                } else if (ActualLocation.getStatus() == ActualLocationFragment.LShareStatus.ANSWER.ordinal()) {

                }
                ActualLocation.setLSharePhone(targetPhone);
                showFragment();
            }
        });

        List<CacheMsgBean> lShareList = IMMsgManager.getInstance().getLShareList();
        for (int i = 0; i < lShareList.size(); i++) {
            CacheMsgBean cacheMsgBean = lShareList.get(i);
            if (cacheMsgBean.getSenderPhone().equals(targetPhone)) {
                showLSView();
                ActualLocation.setLSharePhone(targetPhone);
                ActualLocation.setAnswerCacheMsgBean(cacheMsgBean);
                ActualLocation.setStatus(ActualLocationFragment.LShareStatus.ANSWER.ordinal());
                break;
            }
        }
    }

    private void initLSListener() {
        IMMsgManager.getInstance().setReceiveListener(new IReceiveStartListener() {
            @Override
            public void onStartLShare(CacheMsgBean cacheMsgBean) {
                // IM 位置共享过来
                if (cacheMsgBean.getSenderPhone().equals(targetPhone)) {
                    showLSView();
                    ActualLocation.setLSharePhone(targetPhone);
                    ActualLocation.setAnswerCacheMsgBean(cacheMsgBean);
                    ActualLocation.setStatus(ActualLocationFragment.LShareStatus.ANSWER.ordinal());
                }
            }
        });
    }

    public void showFragment() {
        if (notNull()) {
            return;
        }
        FragmentTransaction showTransaction = getSupportFragmentManager().beginTransaction();
        if (!mFragment.isHidden()) {
            showTransaction.add(R.id.frag_container, mFragment).commit();
        } else {
            showTransaction.show(mFragment).commit();
        }
        showLSView();
        keyboardLay.hide();
    }

    public void hideFragment() {
        if (notNull()) {
            return;
        }
        FragmentTransaction hideTransaction = getSupportFragmentManager().beginTransaction();
        if (mFragment.isAdded()) {
            hideTransaction.hide(mFragment).commit();
        }
        keyboardLay.initLay();
    }

    public void showLSView() {
        tvShareLocation.setVisibility(View.VISIBLE);
    }

    public void hideLSView() {
        tvShareLocation.setVisibility(View.GONE);
    }

    public void removeFragment() {
        if (notNull()) {
            return;
        }
        FragmentTransaction removeTransaction = getSupportFragmentManager().beginTransaction();
        if (mFragment.isAdded()) {
            removeTransaction.remove(mFragment).commit();
        }
        keyboardLay.initLay();
    }

    private boolean notNull() {
        return mFragment == null;
    }

    // 结束主动
    public void setOnEndRefreshUIListener(AbstractStartOrQuit listener) {
        mStopRefreshUIListener = listener;
    }

    public void endActualLocationShared() {
        int userId = HuxinSdkManager.instance().getUserId();
        ReceiveListener receiveListener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                if (mContext == null || ((IMConnectionActivity) mContext).isFinishing()) {
                    return;
                }
                Toast.makeText(mContext, "位置共享结束", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int errCode) {
            }
        };
        HuxinSdkManager.instance().endLocation(userId, targetPhone, receiveListener);
    }
    //*****************end

}
