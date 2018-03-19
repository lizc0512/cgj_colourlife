package com.youmai.hxsdk.view.full;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Handler;

import com.youmai.hxsdk.HuxinLocationManager;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.HookStrategyActivity;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.bean.ChatMsg;
import com.youmai.hxsdk.chat.ContentLocation;
import com.youmai.hxsdk.chat.ContentText;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.db.dao.ChatMsgDao;
import com.youmai.hxsdk.db.helper.HxUsersHelper;
import com.youmai.hxsdk.db.manager.GreenDbManager;
import com.youmai.hxsdk.entity.CallInfo;
import com.youmai.hxsdk.entity.EmoInfo;
import com.youmai.hxsdk.db.bean.ShowData;
import com.youmai.hxsdk.entity.UserShow;
import com.youmai.hxsdk.im.IMHelper;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.im.cache.CacheMsgJoke;
import com.youmai.hxsdk.interfaces.IFileReceiveListener;
import com.youmai.hxsdk.interfaces.IShowDataListener;
import com.youmai.hxsdk.interfaces.OnChatMsg;
import com.youmai.hxsdk.interfaces.bean.FileBean;
import com.youmai.hxsdk.interfaces.impl.FileReceiveListenerImpl;
import com.youmai.hxsdk.popup.half.MEmoPopWindow;
import com.youmai.hxsdk.service.HuxinService;
import com.youmai.hxsdk.socket.IMContentType;
import com.youmai.hxsdk.sp.SPDataUtil;
import com.youmai.hxsdk.utils.CallUtils;
import com.youmai.hxsdk.utils.CommonUtils;
import com.youmai.hxsdk.utils.FloatLogoUtil;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 * 作者：create by YW
 * 日期：2016.07.25 18:45
 * 描述：
 */
public class FloatViewUtil {

    private static final int[] ITEM_DRAWABLES = {R.drawable.hx_btn_shake, R.drawable.hx_btn_local,
            R.drawable.hx_btn_picture, R.drawable.hx_btn_phiz};

    private Context mContext;

    private CallFullView fullView;   //全屏

    private String mDstPhone; // 对方的手机号码
    private final ArrayList<ChatMsg> mChatMsgs = new ArrayList<>();

    private ArrayList<ChatMsg> mMessagesList = new ArrayList<>(); //todo 聊天IM统计,另跳界面展示

    private ChatMsg mCurShow = null;  //todo 当前秀变量

    private static FloatViewUtil instance = null;

    private ShowData showData;

    private long outgoing_call_time;

    /***************************************************************/
    /** 以下为tcp默认监听tcp协议方法**/
    /***************************************************************/

    /**
     * 收到消息
     */
    private OnChatMsg onChatMsg = new OnChatMsg() {
        @Override
        public void onCallback(ChatMsg msg) {
            IFileReceiveListener listener = FileReceiveListenerImpl.getReceiveListener();

            if (!msg.getSrcPhone().equalsIgnoreCase(mDstPhone)) {
                return;
            }

            if (null != listener && msg.getMsgType().ordinal() != ChatMsg.MsgType.AUDIO.ordinal()) { //语音不显示接收进度
                listener.onProgress(msg.getMsgType().ordinal(), 0.45);
            }

            if (msg.getMsgType() != ChatMsg.MsgType.TEXT && msg.getMsgType() != ChatMsg.MsgType.BIG_FILE) { // 暂不处理文件和表情
                ChatMsgDao chatMsgDao = GreenDbManager.instance(mContext).getChatMsgDao();
                chatMsgDao.insertOrReplace(msg);
            }

            if (msg.getMsgType() == ChatMsg.MsgType.TEXT
                    || msg.getMsgType() == ChatMsg.MsgType.EMO_TEXT
                    || msg.getMsgType() == ChatMsg.MsgType.JOKE_TEXT) {
                if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_HALF ||
                        HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_FULL) {

                    MEmoPopWindow emoPopWindow = null;
                    ContentText text = msg.getMsgContent().getText();
                    String con = text.getContent();

                    if (new EmoInfo(mContext).isEmotion(con)) {
                        List<EmoInfo.EmoItem> dataList = new EmoInfo(mContext).getEmoList();
                        for (EmoInfo.EmoItem emoItem : dataList) {

                            String emoStr = emoItem.getEmoStr();
                            if (emoStr.equals(con)) {
                                try {
                                    /*if (halfView != null && HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_HALF) {

                                        emoPopWindow = new MEmoPopWindow(mContext, emoItem.getEmoRes());
                                        emoPopWindow.showAtLocation(halfView, Gravity.TOP, 0, 0);
                                        HuxinSdkManager.instance().playSound(emoItem.getSoundId());

                                    } else */
                                    if (fullView != null && HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_FULL) {

                                        FileBean fileBean = new FileBean().setUserId(msg.getSrcUsrId())
                                                .setFileMsgType(msg.getMsgType().ordinal())
                                                .setDstPhone(msg.getTargetPhone())
                                                .setTextContent(con);
                                        if (null != listener) {
                                            LogUtils.w("push", "thread:" + Thread.currentThread().getName());
                                            listener.onImSuccess(msg.getMsgType().ordinal(), fileBean);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                msg.setMsgType(ChatMsg.MsgType.EMO_TEXT);
                                mMessagesList.add(0, msg);
                                break;
                            }
                        }
                    } else if (con.startsWith(CacheMsgJoke.JOKES)) {
                        FileBean fileBean = new FileBean().setUserId(msg.getSrcUsrId())
                                .setFileMsgType(msg.getMsgType().ordinal())
                                .setDstPhone(msg.getTargetPhone())
                                .setTextContent(con);
                        if (null != listener) {
                            listener.onImSuccess(ChatMsg.MsgType.JOKE_TEXT.ordinal(), fileBean);
                        }
                        msg.setMsgType(ChatMsg.MsgType.JOKE_TEXT);
                        mMessagesList.add(0, msg);
                    } else if (con.startsWith("/")) {
                        FileBean fileBean = new FileBean().setUserId(msg.getSrcUsrId())
                                .setFileMsgType(msg.getMsgType().ordinal())
                                .setDstPhone(msg.getTargetPhone())
                                .setTextContent(con);
                        if (null != listener) {
                            listener.onImSuccess(ChatMsg.MsgType.EMO_TEXT.ordinal(), fileBean);
                        }
                        msg.setMsgType(ChatMsg.MsgType.EMO_TEXT);
                        mMessagesList.add(0, msg);
                    } else {
                        msg.setMsgType(ChatMsg.MsgType.TEXT);
                        mMessagesList.add(0, msg);
                    }
                }

            } else if (msg.getMsgType() == ChatMsg.MsgType.BIG_FILE) { //文件

                if (fullView != null && HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_FULL) {

                    mMessagesList.add(0, msg);
                    String jsonBody = msg.getJsonBoby();
                    try {
                        JSONArray jsonArray = new JSONArray(jsonBody);
                        if (jsonArray.length() > 0) {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String fid = jsonObject.optString(IMContentType.CONTENT_FILE.toString());
                            String fileName = jsonObject.optString(IMContentType.CONTENT_FILE_NAME.toString());
                            String fileSize = jsonObject.optString(IMContentType.CONTENT_FILE_SIZE.toString());

                            //todo_k: 文件
                            final CacheMsgFile cacheMsgFile = new CacheMsgFile()
                                    .setFileSize(Long.parseLong(fileSize))
                                    .setFileName(fileName)
                                    .setFileUrl(AppConfig.DOWNLOAD_IMAGE + fid)
                                    .setFileRes(IMHelper.getFileImgRes(fileName, false));

                            final CacheMsgBean cacheMsgBean = new CacheMsgBean()
                                    .setMsgTime(System.currentTimeMillis())
                                    .setSend_flag(-1)
                                    .setSenderPhone(HuxinSdkManager.instance().getPhoneNum())
                                    .setSenderUserId(HuxinSdkManager.instance().getUserId())
                                    .setReceiverPhone(mDstPhone)
                                    .setMsgType(CacheMsgBean.MSG_TYPE_FILE)
                                    .setJsonBodyObj(cacheMsgFile)
                                    .setRightUI(false)
                                    .setMsgId(msg.getMsgId());

                            FileBean fileBean = new FileBean()
                                    .setFileMsgType(msg.getMsgType().ordinal())
                                    .setUserId(msg.getSrcUsrId())
                                    .setDstPhone(msg.getTargetPhone())
                                    .setFileUrl(AppConfig.DOWNLOAD_IMAGE + fid)
                                    .setFileName(fileName)
                                    .setFileLength(fileSize)
                                    .setFileRes(IMHelper.getFileImgRes(fileName, true))
                                    .setCacheMsgBean(cacheMsgBean);
                            if (null != listener) {
                                listener.onImSuccess(msg.getMsgType().ordinal(), fileBean);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else if (msg.getMsgType() == ChatMsg.MsgType.AUDIO) {

                mMessagesList.add(0, msg);

            } else if (msg.getMsgType() == ChatMsg.MsgType.PICTURE) {

                mMessagesList.add(0, msg);

                FileBean fileBean = new FileBean().setUserId(msg.getSrcUsrId())
                        .setFileMsgType(msg.getMsgType().ordinal())
                        .setDstPhone(msg.getTargetPhone())
                        .setOriginPath(AppConfig.DOWNLOAD_IMAGE + msg.getMsgContent().getPicture().getPicUrl());
                if (null != listener) {
                    listener.onImSuccess(msg.getMsgType().PICTURE.ordinal(), fileBean);
                }
            } else if (msg.getMsgType() == ChatMsg.MsgType.LOCATION) {

                mMessagesList.add(0, msg);

                ContentLocation mLocation = msg.getMsgContent().getLocation();
                String mLabelAddress = mLocation.getLabelStr();

                final String url = "http://restapi.amap.com/v3/staticmap?location="
                        + mLocation.getLongitudeStr() + "," + mLocation.getLatitudeStr() + "&zoom=" + 17
                        + "&scale=2&size=720*550&traffic=1&markers=mid,0xff0000,A:" + mLocation.getLongitudeStr()
                        + "," + mLocation.getLatitudeStr() + "&key=" + AppConfig.staticMapKey;

                FileBean fileBean = new FileBean().setUserId(msg.getSrcUsrId())
                        .setFileMsgType(msg.getMsgType().ordinal())
                        .setDstPhone(msg.getTargetPhone())
                        .setLongitude(Double.valueOf(mLocation.getLongitudeStr()))
                        .setLatitude(Double.valueOf(mLocation.getLatitudeStr()))
                        .setAddress(mLabelAddress)
                        .setMapUrl(url);
                if (null != listener) {
                    listener.onImSuccess(msg.getMsgType().ordinal(), fileBean);
                }
            } else if (msg.getMsgType() == ChatMsg.MsgType.VIDEO) {

                mMessagesList.add(0, msg);

                FileBean fileBean = new FileBean().setUserId(msg.getSrcUsrId())
                        .setFileMsgType(msg.getMsgType().ordinal())
                        .setDstPhone(msg.getTargetPhone())
                        .setVideoPFidUrl(AppConfig.DOWNLOAD_IMAGE + msg.getMsgContent().getVideo().getFrameId())
                        .setVideoFidUrl(AppConfig.DOWNLOAD_IMAGE + msg.getMsgContent().getVideo().getVideoId());

                if (null != listener) {
                    listener.onImSuccess(msg.getMsgType().VIDEO.ordinal(), fileBean);
                }
            }

            //if (msg.getMsgType() != ChatMsg.MsgType.TEXT && msg.getMsgType() != ChatMsg.MsgType.BIG_FILE) { // 文本IM消息不刷新
            switch (HuxinSdkManager.instance().getFloatType()) {
                case HuxinService.MODEL_TYPE_FULL:
                    if (fullView == null) {
                        return;
                    }

                    //fullView.updateCallMsg(mChatMsgs);
                    fullView.historyCallMsg(mMessagesList, true);
                    break;

                case HuxinService.MODEL_TYPE_HALF:
                    break;

                case HuxinService.MODEL_TYPE_Q:
                    break;
            }

        }
    };


    /***************************************************************/
    /** 以上为tcp默认监听tcp协议方法**/
    /***************************************************************/

    public static FloatViewUtil instance() {
        if (instance == null) {
            instance = new FloatViewUtil();
        }
        return instance;
    }


    private FloatViewUtil() {

    }

    public void setDefaultListener() {
        IMMsgManager.getInstance().registerChatMsg(onChatMsg);
    }

    public void clearDefaultListener() {
        IMMsgManager.getInstance().unregisterChatMsg(onChatMsg);
    }


    /**
     * 开始通话记时
     */
    public void startCallTime() {
        long mills;
        String brand = Build.MANUFACTURER.toLowerCase();
        switch (brand) {
            case "huawei":
                mills = 1;
                break;
            case "oppo":
                mills = 300;
                break;
            default:
                mills = 1;
                break;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (fullView != null) {
                    if (CallInfo.IsMOCalling()) {
                        fullView.startCallTimeDelay();
                    } else {
                        fullView.startCallTime();
                    }
                }
            }
        }, mills);

    }

    /**
     * 触发告警定位
     */
    public void touchAlarmLocation(final String dstPhone) {
        SharedPreferences preferences = mContext.getSharedPreferences("alarm_numbers", Context.MODE_PRIVATE);
        String regularEx = "#";
        String str = preferences.getString("numbers", "");
        String[] numbers = str.split(regularEx);
        List<String> list = Arrays.asList(numbers);
        if (list.contains(dstPhone)) {

            LogUtils.d("", "触发告警：对方号码 " + dstPhone);

            Location location = HuxinLocationManager.instance().requestCache();
            if (location != null) {
                HuxinSdkManager.instance().touchAlarm(dstPhone, location);
            }

            if (CommonUtils.isNetworkAvailable(mContext)) {
                HuxinLocationManager.instance().requestNetwork(new HuxinLocationManager.BetterLocationListener() {
                    @Override
                    public void result(Location location) {
                        if (location != null) {
                            LogUtils.d("", "Wifi定位成功!");
                            HuxinSdkManager.instance().touchAlarm(dstPhone, location);
                        }
                    }
                });
            } else {
                HuxinLocationManager.instance().requestGPS(new HuxinLocationManager.BetterLocationListener() {
                    @Override
                    public void result(Location location) {
                        if (location != null) {
                            LogUtils.d("", "GPS定位成功!");
                            HuxinSdkManager.instance().touchAlarm(dstPhone, location);
                        }
                    }
                });
            }
        }
    }

    /**
     * 针对oppo R9m 弹屏被遮盖
     *
     * @param context
     */
    public void showFloatViewDelay(final Context context) {
        long mills;
        String brand = Build.MANUFACTURER.toLowerCase();
        switch (brand) {
            case "huawei":
                mills = 1;
                break;
            case "oppo":
                mills = 300;
                break;
            default:
                mills = 1;
                break;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showFloatView(context);
            }
        }, mills);
    }


    /**
     * 针对oppo R9m 弹屏被遮盖
     *
     * @param context
     */
    public void showFloatViewDelay(final Context context, final String dstPhone) {
        long mills;
        String brand = Build.MANUFACTURER.toLowerCase();
        switch (brand) {
            case "huawei":
                mills = 1;
                break;
            case "oppo":
                mills = 300;
                break;
            default:
                mills = 1;
                break;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showFloatView(context, dstPhone);
            }
        }, mills);
    }


    /**
     * 显示弹出框
     */
    public void showFloatView(Context context) {
        this.showFloatView(context, mDstPhone);
    }


    /**
     * 显示弹出框
     */
    public void showFloatView(Context context, String dstPhone) {
        mContext = context.getApplicationContext();

        if (StringUtils.isEmpty(dstPhone)) {
            return;
        } else {
            this.mDstPhone = dstPhone;
        }

        if (!HuxinSdkManager.instance().isCallFloatView()) {
            return;
        }

        HuxinSdkManager.instance().getStackAct().finishAllActivity();

        //非主叫状态 并且 不支持呼信接通
        if (CallInfo.IsMTCalling() && !CallUtils.isSupportMTBefore(mContext)) {
            return;
        }


        //用于通话保持中的第三方再来电
        if (FloatLogoUtil.instance().isShow()) {
            if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_FULL) {
                if (fullView != null && !StringUtils.isEmpty(dstPhone)) {
                    fullView.setShowInfo(dstPhone, null);
                }
            }
            return;
        }


        if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_FULL) {

            if (fullView == null) {
                fullView = new CallFullView(context);

                if (CallInfo.isCountFloatView()) {
                    CallInfo.setIsCountFloatView(false);
                }

                IShowDataListener listener = new IShowDataListener() {
                    @Override
                    public void loadShowSuccess(ShowData show) {
                        if (show == null || fullView == null) {
                            return;
                        }

                        if (showData == null || !showData.getFid().equals(show.getFid())) {
                            ChatMsg msg = new ChatMsg(show);
                            if (!ListUtils.isEmpty(mChatMsgs)) {//清除默认秀
                                mChatMsgs.clear();
                            }
                            mChatMsgs.add(msg);
                            //todo 当前秀
                            mCurShow = msg;
                            ChatMsgDao chatMsgDao = GreenDbManager.instance(mContext).getChatMsgDao();
                            chatMsgDao.insertOrReplace(msg);
                            //todo_k: 秀
                            //srsm 3.6 版本已经去掉聊天界面秀的显示 不要轻易打开，否则会造成显示混乱
                            // CacheMsgHelper.saveShowData2DBCacheMsgBean(mContext, mDstPhone, show);

                            fullView.updateCallMsg(mChatMsgs);

                            if (show != null) {
                                fullView.setShowInfo(null, show.getName());
                            }
                        }
                    }

                    @Override
                    public void loadUISuccess(List<UserShow.DBean.SectionsBean> list) {
                        if (fullView != null) {
                            fullView.setListenerFormShow(list);
                        }
                    }
                };

                showData = HuxinSdkManager.instance().getShowData(dstPhone, listener);

                if (!StringUtils.isEmpty(dstPhone)) {
                    String showName = null;
                    if (showData != null) {
                        showName = showData.getName();
                    }
                    fullView.setShowInfo(dstPhone, showName);
                }

                // 检测告警触发
                touchAlarmLocation(dstPhone);

                if (showData == null) {
                    if (!SPDataUtil.getFirstShowFid(mContext).equals("-1")
                            && dstPhone.equals(HuxinSdkManager.instance().getPhoneNum())
                            && !SPDataUtil.getFirstShowPFid(mContext).equals("")) { //TODO: 第一次引导
                        ShowData sd = new ShowData();
                        sd.setFile_type("1");
                        sd.setFid(SPDataUtil.getFirstShowFid(mContext));
                        sd.setPfid(SPDataUtil.getFirstShowPFid(mContext));
                        ChatMsg msg = new ChatMsg(sd);
                        msg.setMsgType(ChatMsg.MsgType.SHOW_VIDEO);

                        //todo 当前秀
                        mCurShow = msg;
                        mChatMsgs.add(msg);
                    } else {
                        ChatMsg msg = new ChatMsg();
                        msg.setMsgType(ChatMsg.MsgType.SHOW_PICTURE);
                        msg.file_type = "0";

                        //todo 当前秀
                        mCurShow = msg;
                        mChatMsgs.add(msg);
                    }

                } else {
                    ChatMsg msg = new ChatMsg(showData);
                    mChatMsgs.add(msg);

                    //todo 当前秀
                    mCurShow = msg;

                    ChatMsgDao chatMsgDao = GreenDbManager.instance(mContext).getChatMsgDao();
                    chatMsgDao.insertOrReplace(msg);

                    //todo_k: 秀
                    //srsm 3.6 版本已经去掉聊天界面秀的显示 不要轻易打开，否则会造成显示混乱
                    // CacheMsgHelper.saveShowData2DBCacheMsgBean(mContext, mDstPhone, showData);
                }

                fullView.updateCallMsg(mChatMsgs);

            }

            fullView.addView();

        } else if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_Q) {
        } else if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_HALF) {

        }


        HxUsersHelper.dsPhoneCard(mContext, dstPhone);//号码入库
    }

    public void hideFloatViewDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideFloatView();
            }
        }, 300);
    }

    public void hideFloatViewDelay(long delayMillis) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideFloatView();
            }
        }, delayMillis);
    }


    /**
     * 隐藏弹出框
     */
    public void hideFloatView() {
        if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_FULL) {
            if (fullView != null) {
                fullView.removeView();
                if (!CallInfo.IsCalling()) {
                    fullView = null;
                }
            }
        } else if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_Q) {
        } else if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_HALF) {
        }

        if (!CallInfo.IsCalling()) {
            mChatMsgs.clear();
            mMessagesList.clear();
        }
    }

    public void setOutgoingCallTime(long time) {
        this.outgoing_call_time = time;
    }

    /**
     * 隐藏弹出框
     *
     * @param isMOCall 是否主叫
     */
    public void startCallHook(boolean isMOCall) {
        if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_FULL) {
            if (fullView != null) {
                long time = System.currentTimeMillis() - outgoing_call_time;
                boolean isShowHook = time > 3 * 1000;

                if (CallInfo.IsCalling() && isShowHook) {
                    Intent intent = new Intent(mContext, HookStrategyActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(HookStrategyActivity.CURRENT_SHOW, mCurShow);
                    intent.putExtra(HookStrategyActivity.IS_MOCALL, isMOCall);
                    intent.putExtra(HookStrategyActivity.TALK_TIME, fullView.getCallTime());
                    intent.putExtra(HookStrategyActivity.DST_PHONE, mDstPhone);
                    intent.putExtra(HookStrategyActivity.SHOW_NAME, fullView.getShowName());

                    intent.putExtra(HookStrategyActivity.PROVINCE, fullView.getProvince());
                    intent.putExtra(HookStrategyActivity.CITY, fullView.getCity());

                    intent.putParcelableArrayListExtra(HookStrategyActivity.REC_PICTURE_CLASSIFY, fullView.getRecPic());
                    intent.putParcelableArrayListExtra(HookStrategyActivity.REC_LOCATION_CLASSIFY, fullView.getRecLocation());
                    intent.putParcelableArrayListExtra(HookStrategyActivity.REC_FILE_CLASSIFY, fullView.getRecFile());
                    mContext.startActivity(intent);
                }
            }
        }

        if (CallInfo.IsCalling()) {
            mChatMsgs.clear();
            mMessagesList.clear();
        }

    }

    /**
     * 隐藏弹出框
     *
     * @param context
     * @param phoneNum
     */
    public void startCallHook(Context context, String phoneNum) {
        if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_FULL) {
            Intent intent = new Intent(context, HookStrategyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(HookStrategyActivity.DST_PHONE, phoneNum);
            intent.putExtra(HookStrategyActivity.TALK_TIME, 0L);
            intent.putExtra(HookStrategyActivity.IS_MOCALL, false);
            context.startActivity(intent);
        }
    }


    /**
     * 是否弹屏
     */
    public boolean isFloatViewShow() {
        boolean res = false;
        if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_FULL) {
            if (fullView != null && fullView.getParent() != null) {
                res = true;
            }
        } else if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_Q) {
        } else if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_HALF) {
        }

        return res;
    }


    /**
     * 随机取段子
     *
     * @return 段子内容
     */
    public String getJokesRandom() {
        String[] jokesArray = mContext.getResources().getStringArray(R.array.hx_jokes);
        int index = new Random().nextInt(jokesArray.length);
        return jokesArray[index];
    }


    /*public void parseJsonToUI(String infoJson) {
        CallShowModel callShowModel = GsonUtil.parse(infoJson, CallShowModel.class);
        mChatMsgs.add(new ChatMsg(callShowModel));

        if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_FULL) {
            // fullView
            if (fullView == null) {
                return;
            }
            fullView.updateCallMsg(mChatMsgs);

        } else if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_Q) {
            //arcMenu
            if (arcMenu == null) {
                return;
            }
            arcMenu.updateArcMsg(mChatMsgs);
        } else if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_HALF) {
            // fullView
            if (halfView == null) {
                return;
            }
            halfView.updateHalfCallMsg(mChatMsgs);
        }
    }*/

    /* 监听home键广播 start by 2016.8.22 */
    private final BroadcastReceiver homeListenerReceiver = new BroadcastReceiver() {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null && reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    if (CallInfo.IsCalling()) {
                        // 处理自己的逻辑
                        FloatViewUtil.instance().hideFloatView();
                        FloatLogoUtil.instance().hideFloat();
                        FloatLogoUtil.instance().showFloat(context.getApplicationContext(), HuxinService.MODEL_TYPE_FULL, false);

                        HuxinSdkManager.instance().getStackAct().finishAllActivity();
                    }
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
    /* 监听home键广播 end by 2016.8.22 */

    public CallFullView getFullView() {
        return fullView;
    }

}
