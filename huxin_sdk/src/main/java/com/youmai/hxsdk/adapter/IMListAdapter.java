package com.youmai.hxsdk.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.IMFilePreviewActivity;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.CropMapActivity;
import com.youmai.hxsdk.activity.IMConnectionActivity;
import com.youmai.hxsdk.activity.PictureIndicatorActivity;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.im.IMHelper;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.cache.CacheMsgCall;
import com.youmai.hxsdk.im.cache.CacheMsgEmotion;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.im.cache.CacheMsgJoke;
import com.youmai.hxsdk.im.cache.CacheMsgLShare;
import com.youmai.hxsdk.im.cache.CacheMsgMap;
import com.youmai.hxsdk.im.cache.CacheMsgRemark;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.im.cache.CacheMsgVideo;
import com.youmai.hxsdk.im.cache.CacheMsgVoice;
import com.youmai.hxsdk.im.voice.manager.MediaManager;
import com.youmai.hxsdk.module.map.AbstractStartOrQuit;
import com.youmai.hxsdk.module.map.ActualLocation;
import com.youmai.hxsdk.module.map.ActualLocationFragment;
import com.youmai.hxsdk.module.map.AnswerOrReject;
import com.youmai.hxsdk.module.remind.SetRemindActivity;
import com.youmai.hxsdk.service.SendMsgService;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.QiniuUrl;
import com.youmai.hxsdk.utils.TextMergeUtils;
import com.youmai.hxsdk.utils.TimeUtils;
import com.youmai.hxsdk.utils.ToastUtil;
import com.youmai.hxsdk.view.LinearLayoutManagerWithSmoothScroller;
import com.youmai.hxsdk.view.chat.emoticon.utils.EmoticonHandler;
import com.youmai.hxsdk.view.chat.utils.Utils;
import com.youmai.hxsdk.view.progressbar.CircleProgressView;
import com.youmai.hxsdk.view.text.CopeTextView;
import com.youmai.hxsdk.view.text.ItemTextView;
import com.youmai.hxsdk.view.tip.TipView;
import com.youmai.hxsdk.view.tip.bean.TipBean;
import com.youmai.hxsdk.view.tip.listener.ItemListener;
import com.youmai.hxsdk.view.tip.tools.TipsType;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.wasabeef.glide.transformations.MaskTransformation;
import pl.droidsonroids.gif.GifImageView;


/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-11-07 14:17
 * Description:
 */
public class IMListAdapter extends RecyclerView.Adapter {

    private static final String TAG = IMListAdapter.class.getSimpleName();

    private static final int IMG_LEFT = 0;  //图片左

    private static final int EMOTION_LEFT = 1; //表情左

    private static final int MAP_LEFT = 2; //地图左

    private static final int VOICE_LEFT = 3; //声音左

    private static final int TXT_LEFT = 4; //文字左

    private static final int IMG_RIGHT = 5; //图片右

    private static final int EMOTION_RIGHT = 6; //表情右

    private static final int MAP_RIGHT = 7; //地图右

    private static final int VOICE_RIGHT = 8; //声音右

    private static final int TXT_RIGHT = 9; //文字右

    private static final int FILE_LEFT = 10; //文件左

    private static final int FILE_RIGHT = 11;//文件右

    private static final int CALL_RIGHT = 12;//通话右

    private static final int CALL_LEFT = 13;//通话左

    private static final int SHOW_LEFT = 14;//秀左

    private static final int SHOW_RIGHT = 15;//秀右

    private static final int VCARD_LEFT = 16;//vcard左

    private static final int VCARD_RIGHT = 17;//vcard右

    private static final int REMARK_LEFT = 18;//备注左

    private static final int REMARK_RIGHT = 19;//备注右

    private static final int VIDEO_LEFT = 20;//视频左

    private static final int VIDEO_RIGHT = 21;//视频右

    private static final int L_SHARE_LEFT = 22;//位置共享左

    private static final int L_SHARE_RIGHT = 23;//位置共享右

    private static final int CARD_HEAD_TAIL_FLAG = 0x8800;// 卡片头，包含尾,左一位代表头部，左二位代表尾部

    private static final int CARD_HEAD_FLAG = 0x8000;// 卡片头

    private static final int CARD_TAIL_FLAG = 0x0800;// 卡片尾

    private static final int CARD_MASK_FLAG = 0x00ff;// 校验消息类型

    private static final int CARD_MENU_CREATE_REMARK = 1;

    private static final int CARD_MENU_SEND_REMARK = 2;

    private static final int CARD_MENU_ALARM = 3;

    private static final int CARD_MENU_CALENDER = 4;


    private Context mContext;
    private IMConnectionActivity mIMConnectActivity;
    private Fragment fragment;
    private RecyclerView mRecyclerView;
    private String mDstPhone, mDstName;
    private String mSelfPhone = HuxinSdkManager.instance().getPhoneNum();
    private String theme;
    private List<CacheMsgBean> mImBeanList = new ArrayList<>();
    private List<Integer> showIndexList;
    public int mThemeIndex = -1;

    private boolean isCanPlay = true;
    private boolean hasItemClick = false;

    private OnListener listener;

    private Toast mGolbalToast;

    private long t = 0L;

    private final int REMARK_UNEDIT_STATE = -99;
    private int mEditingRemarkPos = REMARK_UNEDIT_STATE;
    private String mCurrThemeEdit;
    private String mCurrRemarkEdit;

    private boolean hasOpenRemark = false;//是否打开备注

    private boolean isIMType = false;//是否沟通界面 默认true:沟通 false:问题反馈

    public boolean isShowSelect = false;//用于控制显示更多的选项框
    private TreeMap<Integer, CacheMsgBean> selectMsg = new TreeMap<>();
    private OnClickMoreListener moreListener;

    TipView tipView;
    public float mRawX;
    public float mRawY;

    public IMListAdapter(IMConnectionActivity act, RecyclerView recyclerView, String dstPhone, String dstName, boolean isIM) {
        mIMConnectActivity = act;
        mContext = act;
        mRecyclerView = recyclerView;
        mDstPhone = dstPhone;
        mDstName = dstName;
        //srsm add
        mImBeanList.addAll(IMMsgManager.getInstance().genCacheMsgBeanList(mContext, mDstPhone, true));
        defaultVoice(mImBeanList);

        mRecyclerView.getItemAnimator().setChangeDuration(0);
        mRecyclerView.getItemAnimator().setAddDuration(0);
        mRecyclerView.getItemAnimator().setRemoveDuration(0);
        mRecyclerView.getItemAnimator().setMoveDuration(0);
        this.isIMType = isIM;
    }

    public List<CacheMsgBean> getmImBeanList() {
        return mImBeanList;
    }

    public TreeMap<Integer, CacheMsgBean> getSelectMsg() {
        return selectMsg;
    }

    private void showToast(int resId, int duration) {
        if (mGolbalToast == null) {
            mGolbalToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        }

        mGolbalToast.setText(resId);
        mGolbalToast.setDuration(duration);
        mGolbalToast.show();
    }

    public void setListener(OnListener listener) {
        this.listener = listener;
    }

    public void setMoreListener(OnClickMoreListener moreListener) {
        this.moreListener = moreListener;
    }

    public void setHasItemClick(boolean click) {
        hasItemClick = click;
    }

    public boolean getHasItemClick() {
        return hasItemClick;
    }

    public void setHasOpenRemark(boolean hasOpenRemark) {
        this.hasOpenRemark = hasOpenRemark;
        if (getItemCount() > 0) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public void setRemind(long msgId, long remindTime, String remind, int iconNumRes) {
        for (CacheMsgBean item : mImBeanList) {
            Long id = item.getMsgId();

            if (id != null && id == msgId) {
                item.setRemindTime(remindTime);
                item.setRemind(remind);
                item.setRemindType(iconNumRes);
                CacheMsgHelper.instance(mContext).insertOrUpdate(item);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public int getItemPositionByMsgId(long id) {
        int index = 0;
        for (int i = 0; i < mImBeanList.size(); i++) {
            Long msgId = mImBeanList.get(i).getMsgId();
            if (msgId != null && msgId == id) {
                index = i;
                break;
            }
        }
        return index;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        RecyclerView.ViewHolder holder = null;
        int oriType = (viewType & CARD_MASK_FLAG);//校验后八位，获取消息类型
        switch (oriType) {
            case IMG_LEFT:
                view = inflater.inflate(R.layout.hx_fragment_im_left_img_item, parent, false);
                holder = new ImgViewHolder(view);
                break;
            case EMOTION_LEFT:
                view = inflater.inflate(R.layout.hx_fragment_im_left_emotion_item, parent, false);
                holder = new EmotionViewHolder(view);
                break;
            case MAP_LEFT:
                view = inflater.inflate(R.layout.hx_fragment_im_left_map_item, parent, false);
                holder = new MapViewHolder(view);
                break;
            case VOICE_LEFT:
                view = inflater.inflate(R.layout.hx_fragment_im_left_voice_item, parent, false);
                holder = new VoiceViewHolder(view);
                break;
            case TXT_LEFT:
                view = inflater.inflate(R.layout.hx_fragment_im_left_txt_item, parent, false);
                holder = new TxtViewHolder(view);
                break;
            case IMG_RIGHT:
                view = inflater.inflate(R.layout.hx_fragment_im_right_img_item, parent, false);
                holder = new ImgViewHolder(view);
                break;
            case EMOTION_RIGHT:
                view = inflater.inflate(R.layout.hx_fragment_im_right_emotion_item, parent, false);
                holder = new EmotionViewHolder(view);
                break;
            case MAP_RIGHT:
                view = inflater.inflate(R.layout.hx_fragment_im_right_map_item, parent, false);
                holder = new MapViewHolder(view);
                break;
            case VOICE_RIGHT:
                view = inflater.inflate(R.layout.hx_fragment_im_right_voice_item, parent, false);
                holder = new VoiceViewHolder(view);
                break;
            case TXT_RIGHT:
                view = inflater.inflate(R.layout.hx_fragment_im_right_txt_item, parent, false);
                holder = new TxtViewHolder(view);
                break;
            case FILE_RIGHT:
                view = inflater.inflate(R.layout.hx_fragment_im_right_file_item, parent, false);
                holder = new FileViewHolder(view);
                break;
            case FILE_LEFT:
                view = inflater.inflate(R.layout.hx_fragment_im_left_file_item, parent, false);
                holder = new FileViewHolder(view);
                break;

            case CALL_RIGHT:
                view = inflater.inflate(R.layout.hx_fragment_im_right_call_item, parent, false);
                holder = new CallViewHolder(view);
                break;
            case CALL_LEFT:
                view = inflater.inflate(R.layout.hx_fragment_im_left_call_item, parent, false);
                holder = new CallViewHolder(view);
                break;
            case SHOW_LEFT:
                view = inflater.inflate(R.layout.hx_fragment_im_left_show_item, parent, false);
                holder = new ShowViewHolder(view);
                break;
            case SHOW_RIGHT:
                view = inflater.inflate(R.layout.hx_fragment_im_right_show_item, parent, false);
                holder = new ShowViewHolder(view);
                break;
            case VCARD_LEFT:
                view = inflater.inflate(R.layout.hx_fragment_im_left_vcard_item, parent, false);
                holder = new BIZCardViewHolder(view);
                break;
            case VCARD_RIGHT:
                view = inflater.inflate(R.layout.hx_fragment_im_right_vcard_item, parent, false);
                holder = new BIZCardViewHolder(view);
                break;
            case REMARK_LEFT:
                view = inflater.inflate(R.layout.hx_fragment_im_left_remark_item, parent, false);
                holder = new RemarkViewHolder(view);
                break;
            case REMARK_RIGHT:
                view = inflater.inflate(R.layout.hx_fragment_im_right_remark_item, parent, false);
                holder = new RemarkViewHolder(view);
                break;
            case VIDEO_LEFT:
                view = inflater.inflate(R.layout.hx_fragment_im_left_video_item, parent, false);
                holder = new VideoViewHolder(view);
                break;
            case VIDEO_RIGHT:
                view = inflater.inflate(R.layout.hx_fragment_im_right_video_item, parent, false);
                holder = new VideoViewHolder(view);
                break;
            case L_SHARE_LEFT:
                view = inflater.inflate(R.layout.hx_fragment_im_left_location_share_item, parent, false);
                holder = new LShareHolder(view);
                break;
            case L_SHARE_RIGHT:
                view = inflater.inflate(R.layout.hx_fragment_im_right_location_share_item, parent, false);
                holder = new LShareHolder(view);
                break;
            default:
                //默认视图，用于解析错误的消息
                view = inflater.inflate(R.layout.hx_fragment_im_left_txt_item, parent, false);
                holder = new BaseViewHolder(view);
        }
        BaseViewHolder baseViewHolder = (BaseViewHolder) holder;
        baseViewHolder.mItemViewType = viewType;
        return holder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindCommon((BaseViewHolder) holder, position);
        if (holder instanceof ImgViewHolder) { //图片
            onBindPic((ImgViewHolder) holder, position);
        } else if (holder instanceof TxtViewHolder) {  //文字
            onBindTxt((TxtViewHolder) holder, position);
        } else if (holder instanceof VoiceViewHolder) {  //声音
            onBindVoice((VoiceViewHolder) holder, position);
        } else if (holder instanceof MapViewHolder) { //地图
            onBindMap((MapViewHolder) holder, position);
        } else if (holder instanceof EmotionViewHolder) { //表情
            onBindEmotion((EmotionViewHolder) holder, position);
        } else if (holder instanceof FileViewHolder) { //文件
            onBindFile((FileViewHolder) holder, position);
        } else if (holder instanceof CallViewHolder) { //通话
            onBindCall((CallViewHolder) holder, position);
        } else if (holder instanceof ShowViewHolder) { //秀
            //onBindCall((ShowViewHolder) holder, position);
        } else if (holder instanceof BIZCardViewHolder) { //卡片
        } else if (holder instanceof RemarkViewHolder) {//备注
            onBindRemark((RemarkViewHolder) holder, position);
        } else if (holder instanceof VideoViewHolder) {//视频
            onBindVideo((VideoViewHolder) holder, position);
        } else if (holder instanceof LShareHolder) { //共享位置
            onBindLShare((LShareHolder) holder, position);
        }
    }



    @Override
    public int getItemViewType(int position) {
        CacheMsgBean cacheMsgBean = mImBeanList.get(position);
        int oriType = -1;
        switch (cacheMsgBean.getMsgType()) {
            case CacheMsgBean.MSG_TYPE_TXT:
            case CacheMsgBean.MSG_TYPE_JOKE:
                oriType = cacheMsgBean.isRightUI() ? TXT_RIGHT : TXT_LEFT;
                break;
            case CacheMsgBean.MSG_TYPE_EMOTION:
                oriType = cacheMsgBean.isRightUI() ? EMOTION_RIGHT : EMOTION_LEFT;
                break;
            case CacheMsgBean.MSG_TYPE_VOICE:
                oriType = cacheMsgBean.isRightUI() ? VOICE_RIGHT : VOICE_LEFT;
                break;
            case CacheMsgBean.MSG_TYPE_IMG:
                oriType = cacheMsgBean.isRightUI() ? IMG_RIGHT : IMG_LEFT;
                break;
            case CacheMsgBean.MSG_TYPE_MAP:
                oriType = cacheMsgBean.isRightUI() ? MAP_RIGHT : MAP_LEFT;
                break;
            case CacheMsgBean.MSG_TYPE_FILE:
                oriType = cacheMsgBean.isRightUI() ? FILE_RIGHT : FILE_LEFT;
                break;
            case CacheMsgBean.MSG_TYPE_CALL:
                oriType = cacheMsgBean.isRightUI() ? CALL_RIGHT : CALL_LEFT;
                break;
            case CacheMsgBean.MSG_TYPE_SHOW:
                oriType = cacheMsgBean.isRightUI() ? SHOW_RIGHT : SHOW_LEFT;
                break;
            case CacheMsgBean.MSG_TYPE_BIZCARD:
                oriType = cacheMsgBean.isRightUI() ? VCARD_RIGHT : VCARD_LEFT;
                break;
            case CacheMsgBean.MSG_TYPE_REMARK:
                oriType = cacheMsgBean.isRightUI() ? REMARK_RIGHT : REMARK_LEFT;
                break;
            case CacheMsgBean.MSG_TYPE_VIDEO:
                oriType = cacheMsgBean.isRightUI() ? VIDEO_RIGHT : VIDEO_LEFT;
                break;
            case CacheMsgBean.MSG_TYPE_LOCATION_SHARE:
                oriType = cacheMsgBean.isRightUI() ? L_SHARE_RIGHT : L_SHARE_LEFT;
                break;
        }
        return oriType;
    }

    /**
     * 文件
     *
     * @param holder
     * @param position
     */
    private void onBindFile(final FileViewHolder holder, final int position) { //文件
        final CacheMsgBean cacheMsgBean = mImBeanList.get(position);
        final CacheMsgFile cacheMsgFile = (CacheMsgFile) cacheMsgBean.getJsonBodyObj();

        holder.rl.setTag(cacheMsgBean.getId());
        Glide.with(mContext)
                .load(cacheMsgFile.getFileRes())
                .apply(new RequestOptions()
                        .error(R.drawable.hx_half_pic_moren)        //下载失败
                        .placeholder(R.drawable.hx_half_pic_moren))
                .into(holder.fileIV);

        holder.fileNameTV.setText(cacheMsgFile.getFileName());
        holder.fileSizeTV.setText(IMHelper.convertFileSize(cacheMsgFile.getFileSize()));

        showSendStart(holder, cacheMsgBean.getSend_flag(), cacheMsgBean, position);

        if (cacheMsgBean.isRightUI()) {
            if (cacheMsgBean.getSend_flag() == 0) {
                holder.fileSizeTV.setVisibility(View.VISIBLE);
                holder.filePbar.setVisibility(View.GONE);
            } else {
                holder.fileSizeTV.setVisibility(View.GONE);
                holder.filePbar.setVisibility(View.VISIBLE);
            }
        } else {
            holder.fileSizeTV.setVisibility(View.VISIBLE);
            holder.filePbar.setVisibility(View.GONE);
        }

        holder.fileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasItemClick = true;
                Intent intent = new Intent(mContext, IMFilePreviewActivity.class);
                intent.putExtra(IMFilePreviewActivity.IM_FILE_BEAN, cacheMsgBean);
                mContext.startActivity(intent);
            }
        });

    }

    /**
     * 图片
     *
     * @param imgViewHolder
     * @param position
     */
    private void onBindPic(final ImgViewHolder imgViewHolder, final int position) {
        final CacheMsgBean cacheMsgBean = mImBeanList.get(position);
        final CacheMsgImage cacheMsgImage = (CacheMsgImage) cacheMsgBean.getJsonBodyObj();
        String leftUrl;
        switch (cacheMsgImage.getOriginalType()) {
            case CacheMsgImage.SEND_IS_ORI_RECV_NOT_ORI:
                leftUrl = QiniuUrl.getThumbImageUrl(mContext, cacheMsgImage.getFid(), QiniuUrl.SCALE);
                break;
            default:
                leftUrl = AppConfig.getImageUrl(mContext, cacheMsgImage.getFid());
                break;
        }
        final File imgFile = new File(cacheMsgImage.getFilePath());
        String rightUrl = TextUtils.isEmpty(cacheMsgImage.getFilePath()) ? leftUrl : cacheMsgImage.getFilePath();

        showSendStart(imgViewHolder, cacheMsgBean.getSend_flag(), cacheMsgBean, position);

        Glide.with(mContext)
                .load(cacheMsgBean.isRightUI() ? rightUrl : leftUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .error(R.drawable.hx_half_pic_moren)        //下载失败
                        .placeholder(R.drawable.hx_half_pic_moren)
                        .transform(new MaskTransformation(cacheMsgBean.isRightUI() ? R.drawable.hx_im_voice_bg_right : R.drawable.hx_im_voice_bg_left)))
                .into(imgViewHolder.senderImg);

        final String finalLeftUrl = leftUrl;
        imgViewHolder.imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*hasItemClick = true;
                Intent intent = new Intent();
                intent.setClass(mContext, CropImageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("isImageUrl", cacheMsgBean.isRightUI() ? cacheMsgImage.getFilePath() : imgUrl);
                mContext.startActivity(intent);*/

                //List<String> list = new ArrayList<>();
                int index = 0;
                ArrayList<CacheMsgBean> beanList = new ArrayList<>();
                for (CacheMsgBean item : mImBeanList) {
                    if (CacheMsgBean.MSG_TYPE_IMG == item.getMsgType()) { //图片
                        beanList.add(item);
                        final CacheMsgImage cacheImage = (CacheMsgImage) item.getJsonBodyObj();
                        String fid = cacheImage.getFid();
                        if (!TextUtils.isEmpty(fid)) {
                            if (finalLeftUrl.equals(AppConfig.getImageUrl(mContext, fid))
                                    || finalLeftUrl.equals(QiniuUrl.getThumbImageUrl(mContext, fid, QiniuUrl.SCALE))) {
                                index = beanList.indexOf(item);
                            }
                            //list.add(AppConfig.getImageUrl(mContext, fid));
                        } else {
                            if (imgFile.getAbsolutePath().equals(cacheImage.getFilePath())) {
                                index = beanList.indexOf(item);
                            }
                            //list.add(cacheImage.getFilePath());
                        }
                    }
                }

                /*final String[] array = list.toArray(new String[list.size()]);
                int index = 0;
                for (int i = 0; i < array.length; i++) {
                    if (imgUrl.equals(array[i]) || (imgFile != null && imgFile.getAbsolutePath().equals(array[i]))) {
                        index = i;
                        break;
                    }
                }*/

                Intent intent = new Intent(mContext, PictureIndicatorActivity.class);
                //intent.putExtra("image", array);
                intent.putExtra("index", index);
                intent.putParcelableArrayListExtra("beanList", beanList);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                    // back twice
                    // mContext.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(mIMConnectActivity, imgViewHolder.senderImg, "imimage").toBundle());
                    mContext.startActivity(intent);
                } else {
                    mContext.startActivity(intent);
                }

            }
        });
    }

    /**
     * 视频数据
     */
    private void onBindVideo(final VideoViewHolder videoViewHolder, final int position) {
        final CacheMsgBean cacheMsgBean = mImBeanList.get(position);
        final CacheMsgVideo cacheMsgVideo = (CacheMsgVideo) cacheMsgBean.getJsonBodyObj();
        showSendStart(videoViewHolder, cacheMsgBean.getSend_flag(), cacheMsgBean, position);
        final long mid = cacheMsgBean.getId();
        final String dstPhone = cacheMsgBean.getSenderPhone();

        final String videoPath = cacheMsgVideo.getVideoPath();//本地视频
        final String framePath = cacheMsgVideo.getFramePath();//本地视频首帧
        final long time = cacheMsgVideo.getTime();//视频时长(毫秒)
        final String videoUrl = AppConfig.getImageUrl(mContext, cacheMsgVideo.getVideoId());    //上传视频Url
        String leftUrl = AppConfig.getImageUrl(mContext, cacheMsgVideo.getFrameId());     //上传视频首帧Url
        String rightUrl = framePath;
        if (rightUrl == null || !new File(rightUrl).exists()) {
            rightUrl = AppConfig.getImageUrl(mContext, cacheMsgVideo.getFrameId());
        }

        videoViewHolder.timeText.setText(TimeUtils.getTimeFromMillisecond(time));
        Glide.with(mContext)
                .load(cacheMsgBean.isRightUI() ? rightUrl : leftUrl)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).placeholder(R.drawable.hx_im_default_img)
                        .transform(new MaskTransformation(cacheMsgBean.isRightUI() ? R.drawable.hx_im_voice_bg_right : R.drawable.hx_im_voice_bg_left)))
                .into(videoViewHolder.videoImg);

        if (TextUtils.isEmpty(videoPath) && cacheMsgVideo.getProgress() != 0) {
            videoViewHolder.videoPlayImg.setVisibility(View.GONE);
            videoViewHolder.videoCircleProgressView.setVisibility(View.VISIBLE);
            videoViewHolder.videoCircleProgressView.setProgress(cacheMsgVideo.getProgress());
            videoViewHolder.videoImg.setEnabled(false);
        } else if (TextUtils.isEmpty(videoPath) && cacheMsgVideo.getProgress() == -1) {
            //下载失败的显示
            videoViewHolder.videoPlayImg.setVisibility(View.VISIBLE);
            videoViewHolder.videoCircleProgressView.setVisibility(View.GONE);
            videoViewHolder.lay.setEnabled(true);
        } else {
            videoViewHolder.videoPlayImg.setVisibility(View.VISIBLE);
            videoViewHolder.videoCircleProgressView.setVisibility(View.GONE);
            videoViewHolder.lay.setEnabled(true);
        }
        videoViewHolder.lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }


    /**
     * 文字
     *
     * @param txtViewHolder
     * @param position
     */
    private void onBindTxt(TxtViewHolder txtViewHolder, final int position) {
        final CacheMsgBean cacheMsgBean = mImBeanList.get(position);

        String txtContent = "";
        if (cacheMsgBean.getJsonBodyObj() instanceof CacheMsgJoke) {
            CacheMsgJoke cacheMsgJoke = (CacheMsgJoke) cacheMsgBean.getJsonBodyObj();
            txtContent = cacheMsgJoke.getMsgJoke().replace(CacheMsgJoke.JOKES, "");
        } else {
            CacheMsgTxt cacheMsgTxt = (CacheMsgTxt) cacheMsgBean.getJsonBodyObj();
            txtContent = cacheMsgTxt.getMsgTxt();
        }

        showSendStart(txtViewHolder, cacheMsgBean.getSend_flag(), cacheMsgBean, position);
        if (txtContent != null) {
            txtViewHolder.senderTV.setAdapter(this);
            SpannableString msgSpan = new SpannableString(txtContent);
            msgSpan = EmoticonHandler.getInstance(mContext).getTextFace(txtContent, msgSpan, 0,
                    Utils.getFontSize(txtViewHolder.senderTV.getTextSize()));
            txtViewHolder.senderTV.setText(msgSpan);
            if (theme == null) {
                txtViewHolder.senderTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MediaManager.isPlaying()) {
                            MediaManager.release();
                            //关闭本身的语音状态
                            if (voicePlayAnim != null && voicePlayAnim.isRunning()) {
                                voicePlayAnim.stop();
                            }
                            CacheMsgBean cacheMsgBean1 = mImBeanList.get(mPlayVoicePosition);
                            cacheMsgBean1.setSend_flag(0);
                            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean1);
                            mImBeanList.set(mPlayVoicePosition, cacheMsgBean1);
                            notifyItemChanged(mPlayVoicePosition);
                        }
                    }
                });
                txtViewHolder.senderTV.setOnClickLis(new CopeTextView.OnCopeListener() {
                    @Override
                    public void copeText() {
                        Toast.makeText(mContext, R.string.hx_im_card_cope_tip, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void forwardText(CharSequence s) {
                        //点击转发事件
                        if (mImBeanList.get(position).getSend_flag() == 0
                                || mImBeanList.get(position).getSend_flag() == 2
                                || mImBeanList.get(position).getSend_flag() == 4) {
                            Intent intent = new Intent();
                            intent.setAction("com.youmai.huxin.recent");
                            intent.putExtra("type", "forward_msg");
                            intent.putExtra("data", mImBeanList.get(position));
                            mIMConnectActivity.startActivityForResult(intent, 300);
                        } else {
                            Toast.makeText(mContext, "转发失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void collect() {
                        //收藏操作
                    }

                    @Override
                    public void read() {

                    }

                    @Override
                    public void remind() {
                        if (cacheMsgBean.getMsgId() == null) {
                            Toast.makeText(mContext, "消息没发送成功，不能设置提醒", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(mContext, SetRemindActivity.class);
                        intent.putExtra(SetRemindActivity.CACHE_MSG_BEAN, cacheMsgBean);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        mIMConnectActivity.startActivityForResult(intent, IMConnectionActivity.REQUEST_REMIND_CODE);
                    }

                    @Override
                    public void delete() {
                        //删除消息的操作
                        deleteMsg(cacheMsgBean, position, true);
                    }

                    @Override
                    public void more() {
                        moreAction(position);
                    }
                });
            } else {
                //不显示删除和复制
                txtViewHolder.senderTV.setCanShow(false);
            }
        }
    }


    /**
     * 备注
     */
    private void onBindRemark(RemarkViewHolder remarkViewHolder, final int position) {
        final CacheMsgBean cacheMsgBean = mImBeanList.get(position);
        CacheMsgRemark cacheMsgRemark = (CacheMsgRemark) cacheMsgBean.getJsonBodyObj();
        showSendStart(remarkViewHolder, cacheMsgBean.getSend_flag(), cacheMsgBean, position);
        String themeStr = cacheMsgRemark.getTheme();
        String remarkStr = cacheMsgRemark.getRemark();
        int type = cacheMsgRemark.getType();

        remarkViewHolder.themeText.setText(TextMergeUtils.getDefaultTheme(themeStr, remarkStr));
        remarkViewHolder.remarkText.setText(remarkStr);
        if (!cacheMsgBean.isRightUI()) {
            if (type == CacheMsgRemark.TYPE_UPDATE) {
                remarkViewHolder.typeText.setText(R.string.hx_im_msg_remark_tip);
            } else {
                remarkViewHolder.typeText.setText(R.string.hx_im_msg_remark_tip2);
            }
        }
    }

    /**
     * 通话
     */
    private void onBindCall(CallViewHolder callViewHolder, int position) {
        CacheMsgBean cacheMsgBean = mImBeanList.get(position);

        CacheMsgCall cacheMsgCall = (CacheMsgCall) cacheMsgBean.getJsonBodyObj();

//        showSendStart(callViewHolder, cacheMsgBean.getSend_flag(), cacheMsgBean, position);

        long time = cacheMsgCall.getDuration();
        int type = cacheMsgCall.getType();

        String content = "";
        //1：呼入   2：呼出  3：未接  4：挂断
        if (type == 1) {
            callViewHolder.senderCallImg.setImageResource(R.drawable.hx_im_call_in_icon);
            callViewHolder.senderTV.setTextColor(ContextCompat.getColor(mContext, R.color.card_call_font));
            content = mContext.getString(R.string.call_message_1) + TimeUtils.getTimeFromMillisecond((int) time * 1000);
        } else if (type == 2) {
            if (time == 0) {
                callViewHolder.senderCallImg.setImageResource(R.drawable.hx_im_call_out2_icon);
                callViewHolder.senderTV.setTextColor(ContextCompat.getColor(mContext, R.color.card_call_font));
                content = mContext.getString(R.string.call_message_2);
            } else {
                callViewHolder.senderCallImg.setImageResource(R.drawable.hx_im_call_out_icon);
                callViewHolder.senderTV.setTextColor(ContextCompat.getColor(mContext, R.color.card_call_font));
                content = mContext.getString(R.string.call_message_3) + TimeUtils.getTimeFromMillisecond((int) time * 1000);
            }
        } else if (type == 3) {
            callViewHolder.senderCallImg.setImageResource(R.drawable.hx_im_call_in2_icon);
            callViewHolder.senderTV.setTextColor(ContextCompat.getColor(mContext, R.color.card_call_font));
            content = mContext.getString(R.string.call_message_4);
        } else if (type == 4) {
            callViewHolder.senderCallImg.setImageResource(R.drawable.hx_im_call_out_icon);
            callViewHolder.senderTV.setTextColor(ContextCompat.getColor(mContext, R.color.card_call_font));
            content = mContext.getString(R.string.call_message_5);
        }

        callViewHolder.senderTV.setText(content);
    }

    /**
     * 地图
     *
     * @param mapViewHolder
     * @param position
     */
    private void onBindMap(final MapViewHolder mapViewHolder, final int position) {
        final CacheMsgBean cacheMsgBean = mImBeanList.get(position);
        CacheMsgMap cacheMsgMap = (CacheMsgMap) cacheMsgBean.getJsonBodyObj();

        String mapUrl = cacheMsgMap.getImgUrl();
        final String mapAddr = cacheMsgMap.getAddress();
        final String mapLocation = cacheMsgMap.getLocation();

        showSendStart(mapViewHolder, cacheMsgBean.getSend_flag(), cacheMsgBean, position);

        Glide.with(mContext)
                .load(mapUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .transform(new MaskTransformation(cacheMsgBean.isRightUI() ? R.drawable.hx_im_card_map_right : R.drawable.hx_im_card_map_left)))
                .into(mapViewHolder.senderMap);

        mapViewHolder.senderAddr.setText(mapAddr);

        mapViewHolder.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasItemClick = true;
                Intent intent = new Intent();

                intent.setClass(mContext, CropMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("location", mapLocation);
                intent.putExtra("labelAddress", mapAddr);
                mContext.startActivity(intent);
            }
        });
    }

    /**
     * 共享位置
     *
     * @param holder
     * @param position
     */
    private void onBindLShare(final LShareHolder holder, final int position) {
        final CacheMsgBean cacheMsgBean = mImBeanList.get(position);

        CacheMsgLShare cache = (CacheMsgLShare) cacheMsgBean.getJsonBodyObj();
        holder.senderTV.setText("我发起了位置共享");

        showSendStart(holder, cacheMsgBean.getSend_flag(), cacheMsgBean, position);

        if (cache.isEndOver()) {
            holder.tvShareOver.setVisibility(View.VISIBLE);
        } else {
            holder.tvShareOver.setVisibility(View.GONE);
        }

        holder.senderTV.setAdapter(this);
        holder.senderTV.setOnClickLis(new ItemTextView.OnCopeListener() {
            @Override
            public void copeText() {
            }

            @Override
            public void forwardText(CharSequence s) {
            }

            @Override
            public void collect() {
                //收藏操作
            }

            @Override
            public void read() {
            }

            @Override
            public void remind() {
                if (cacheMsgBean.getMsgId() == null) {
                    Toast.makeText(mContext, "消息没发送成功，不能设置提醒", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(mContext, SetRemindActivity.class);
                intent.putExtra(SetRemindActivity.CACHE_MSG_BEAN, cacheMsgBean);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mIMConnectActivity.startActivityForResult(intent, IMConnectionActivity.REQUEST_REMIND_CODE);
            }

            @Override
            public void delete() {
                //删除消息的操作
                deleteMsg(cacheMsgBean, position, true);
            }

            @Override
            public void more() {
                moreAction(position);
            }
        });

        final CacheMsgLShare cacheFinal = cache;

        holder.senderTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cacheFinal != null && !cacheFinal.isEndOver() && !cacheMsgBean.isRightUI()) {
                    ActualLocation.setStatus(ActualLocationFragment.LShareStatus.ANSWER.ordinal());
                    ActualLocation.setLSharePhone(mDstPhone);
                    ActualLocation.setAnswerCacheMsgBean(cacheMsgBean);
                    mIMConnectActivity.showFragment();
                }
            }
        });

        //
        IMMsgManager.getInstance().setOnRefreshListener(new AnswerOrReject() {
            @Override
            public void onQuit() {
                cacheMsgBean.setSend_flag(0)
                        .setJsonBodyObj(cacheFinal.setEndOver(true));
                mImBeanList.set(position, cacheMsgBean);
                CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                notifyDataSetChanged();

                mIMConnectActivity.removeFragment();
                mIMConnectActivity.hideLSView();
            }
        });

        //
        mIMConnectActivity.setOnEndRefreshUIListener(new AbstractStartOrQuit() {
            @Override
            public void onQuit(CacheMsgBean bean) {
                cacheMsgBean.setSend_flag(0)
                        .setJsonBodyObj(cacheFinal.setEndOver(true));
                mImBeanList.set(position, cacheMsgBean);
                CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                notifyDataSetChanged();

                mIMConnectActivity.removeFragment();
                mIMConnectActivity.hideLSView();
            }
        });

        ActualLocationFragment.setOnStartRefreshUIListener(new AbstractStartOrQuit() {
            @Override
            public void onQuit(CacheMsgBean cacheMsgBean) {
                mImBeanList.set(position, cacheMsgBean);
                notifyDataSetChanged();
                focusBottom(true);

                mIMConnectActivity.removeFragment();
                mIMConnectActivity.hideLSView();
            }

            // 发送邀请成功后刷UI
            @Override
            public void onRefreshUi(CacheMsgBean cacheMsgBean) {
                mImBeanList.set(position, cacheMsgBean);
                notifyDataSetChanged();
            }
        });

    }

    /**
     * 声音
     *
     * @param voiceViewHolder
     * @param position
     */
    AnimationDrawable voicePlayAnim;
    private int mPlayVoicePosition;
    private ImageView mPlayVoiceIV;

    private TipView voiceTip;
    private float mVoiceRawX, mVoiceRawY;

    private void onBindVoice(final VoiceViewHolder voiceViewHolder, final int position) {
        final CacheMsgBean cacheMsgBean = mImBeanList.get(position);
        final CacheMsgVoice cacheMsgVoice = (CacheMsgVoice) cacheMsgBean.getJsonBodyObj();

        String voiceTime = cacheMsgVoice.getVoiceTime();

        showSendStart(voiceViewHolder, cacheMsgBean.getSend_flag(), cacheMsgBean, position);

        if (voiceViewHolder.readIV != null) {
            if (cacheMsgVoice.isHasLoad()) { //到达
                voiceViewHolder.readIV.setVisibility(View.INVISIBLE);
            } else {
                voiceViewHolder.readIV.setVisibility(View.VISIBLE);
            }
        }
        if (cacheMsgVoice.getForwardCount() > 0) {
            voiceViewHolder.voiceTypeText.setVisibility(View.VISIBLE);
            if (TextUtils.equals(cacheMsgVoice.getSourcePhone(), mSelfPhone)) {
                voiceViewHolder.voiceTypeText.setText("来自本人语音");
            } else {
                voiceViewHolder.voiceTypeText.setText("来自他人语音");
            }
        } else {
            voiceViewHolder.voiceTypeText.setVisibility(View.GONE);
        }

        voiceViewHolder.senderTime.setText(getFormateVoiceTime(voiceTime));

        if (cacheMsgVoice.isShowText()) {
            voiceViewHolder.textLay.setVisibility(View.VISIBLE);
            String contentStr = cacheMsgVoice.getVoiceText();
            if (TextUtils.isEmpty(contentStr)) {
                voiceViewHolder.contentText.setVisibility(View.GONE);
                voiceViewHolder.closeImg.setRotation(180);
            } else {
                voiceViewHolder.contentText.setVisibility(View.VISIBLE);
                voiceViewHolder.contentText.setText(cacheMsgVoice.getVoiceText());
                voiceViewHolder.closeImg.setRotation(0);
            }
        } else {
            voiceViewHolder.textLay.setVisibility(View.GONE);
        }

        voiceViewHolder.closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceViewHolder.textLay.setVisibility(View.GONE);
                cacheMsgVoice.setShowText(false);
                cacheMsgBean.setJsonBodyObj(cacheMsgVoice);
                CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                mImBeanList.set(position, cacheMsgBean);
//                if (voiceViewHolder.contentText.getVisibility() == View.GONE) {
//                    voiceViewHolder.contentText.setVisibility(View.VISIBLE);
//                    voiceViewHolder.closeImg.setRotation(0);
//                } else {
//                    voiceViewHolder.contentText.setVisibility(View.GONE);
//                    voiceViewHolder.closeImg.setRotation(180);
//                }
            }
        });

        voiceViewHolder.contentText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mVoiceRawX = event.getRawX();
                    mVoiceRawY = event.getRawY();
                }
                return false;
            }
        });

        voiceViewHolder.contentText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                List<TipBean> tips = TipsType.getVoiceType3();
                voiceTip = new TipView(mContext, tips, mVoiceRawX, mVoiceRawY);
                voiceTip.setListener(new ItemListener() {
                    @Override
                    public void delete() {
                        deleteMsg(cacheMsgBean, position, true);
                    }

                    @Override
                    public void copy() {
                        ClipboardManager cpb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        if (cpb != null) {
                            cpb.setPrimaryClip(ClipData.newPlainText(null, voiceViewHolder.contentText.getText().toString()));//加入剪贴板
                            Toast.makeText(mContext, R.string.hx_im_card_cope_tip, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void collect() {
                        //收藏操作
                    }

                    @Override
                    public void forward() {
                        //转发操作
                        if (mImBeanList.get(position).getSend_flag() == 0
                                || mImBeanList.get(position).getSend_flag() == 2
                                || mImBeanList.get(position).getSend_flag() == 4) {
                            CacheMsgBean bean = mImBeanList.get(position);
                            //语音设置是谁发的源头
                            CacheMsgVoice cacheMsgVoice = (CacheMsgVoice) bean.getJsonBodyObj();
                            if (cacheMsgVoice.getForwardCount() == 0 && TextUtils.isEmpty(cacheMsgVoice.getSourcePhone())) {
                                cacheMsgVoice.setSourcePhone(bean.isRightUI() ? bean.getReceiverPhone() : bean.getSenderPhone());
                                bean.setJsonBodyObj(cacheMsgVoice);
                            }
                            Intent intent = new Intent();
                            intent.setAction("com.youmai.huxin.recent");
                            intent.putExtra("type", "forward_msg");
                            intent.putExtra("data", bean);
                            mIMConnectActivity.startActivityForResult(intent, 300);
                        } else {
                            Toast.makeText(mContext, "转发失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void remind() {
                        Toast.makeText(mContext, "click:提醒", Toast.LENGTH_SHORT).show();
                    }

                });
                voiceTip.show(voiceViewHolder.contentText);
                return false;
            }
        });

        voiceViewHolder.voiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean isRightUi = cacheMsgBean.isRightUI();

                if (voicePlayAnim != null && voicePlayAnim.isRunning()) {
                    voicePlayAnim.stop();
                    if (mPlayVoiceIV != null) {
                        mPlayVoiceIV.setImageResource(mImBeanList.get(mPlayVoicePosition).isRightUI()
                                ? R.drawable.hx_im_right_anim_v3 : R.drawable.hx_im_left_anim_v3);
                    }
                }

                if (MediaManager.isPlaying()) {
                    MediaManager.release();
                    if (mPlayVoicePosition == position) {
                        return;
                    } else {
                        //停止文本朗读
                        CacheMsgBean cacheMsgBean1 = mImBeanList.get(mPlayVoicePosition);
                        if (cacheMsgBean1.getJsonBodyObj() instanceof CacheMsgTxt) {
                            if (voicePlayAnim != null && voicePlayAnim.isRunning()) {
                                voicePlayAnim.stop();
                            }
                            cacheMsgBean1.setSend_flag(0);
                            CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean1);
                            mImBeanList.set(mPlayVoicePosition, cacheMsgBean1);
                            notifyItemChanged(mPlayVoicePosition);
                        }
                    }
                }

                mPlayVoicePosition = position;
                mPlayVoiceIV = voiceViewHolder.voiceIV;

                if (!isRightUi) {
                    if (!cacheMsgVoice.isHasLoad()) {
                        //add to db
                        cacheMsgVoice.setHasLoad(true);
                        cacheMsgBean.setJsonBodyObj(cacheMsgVoice);
                        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                    }
                    voiceViewHolder.readIV.setVisibility(View.INVISIBLE);
                }

                voiceViewHolder.voiceIV.setImageResource(isRightUi ? R.drawable.hx_im_voice_right_anim : R.drawable.hx_im_voice_left_anim);
                voicePlayAnim = (AnimationDrawable) voiceViewHolder.voiceIV.getDrawable();
                voicePlayAnim.start();

                //播放声音
                MediaManager.playSound(cacheMsgVoice.getVoicePath(), new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        voicePlayAnim.stop();
                        voiceViewHolder.voiceIV.setImageResource(isRightUi ? R.drawable.hx_im_right_anim_v3 : R.drawable.hx_im_left_anim_v3);
                    }
                });
            }
        });

    }

    public String getFormateVoiceTime(String voiceTime) {
        try {
            float voiceF = Float.valueOf(voiceTime);
            return Math.round(voiceF) + "\"";
        } catch (Exception e) {
            return voiceTime;
        }
    }

    /**
     * 表情
     *
     * @param emotionViewHolder
     * @param position
     */
    private void onBindEmotion(EmotionViewHolder emotionViewHolder, int position) {
        CacheMsgBean cacheMsgBean = mImBeanList.get(position);
        CacheMsgEmotion cacheMsgEmotion = (CacheMsgEmotion) cacheMsgBean.getJsonBodyObj();
        showSendStart(emotionViewHolder, cacheMsgBean.getSend_flag(), cacheMsgBean, position);

        try {
            if (cacheMsgEmotion.getEmotionRes() != -1) {
                emotionViewHolder.senderEmotion.setImageResource(cacheMsgEmotion.getEmotionRes());
            } else {
                Glide.with(mContext)
                        .load(AppConfig.DOWNLOAD_IMAGE + cacheMsgEmotion.getEmotionContent() + "?imageView2/0/w/250/h/250")
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .fitCenter())
                        .into(emotionViewHolder.senderEmotion);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }


    //支持失败重传
    //根据flag显示发送状态 0:发送成功  -1:正在发送  2:短信发送  4:发送错误  5:文本播放语音 6:提醒状态
    private void showSendStart(final BaseViewHolder viewHolder, int flag, final CacheMsgBean bean, final int position) {

        if (viewHolder.progressBar != null) {
            viewHolder.smsImg.setBackgroundDrawable(null);
            viewHolder.smsImg.setOnClickListener(null);
            if (flag == 0) {
                //显示到达状态
                viewHolder.progressBar.setVisibility(View.INVISIBLE);
                viewHolder.smsImg.setVisibility(View.GONE);
            } else if (flag == 2) {
                //显示短信状态
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.smsImg.setVisibility(View.VISIBLE);
                viewHolder.smsImg.setImageResource(bean.isRightUI() ? R.drawable.hx_im_send_sms_icon : R.drawable.ic_sms_grey_left24dp);
            } else if (flag == 4) {
                //显示发送失败状态
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.smsImg.setVisibility(View.VISIBLE);
                viewHolder.smsImg.setImageResource(R.drawable.hx_im_send_error2_icon);
                viewHolder.smsImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (bean.getJsonBodyObj() instanceof CacheMsgLShare) {
                            resendLShare(bean);
                            return;
                        }
                        //重传
                        bean.setSend_flag(-1);
                        updateSendStatus(bean, position);
                        Intent intent = new Intent(mContext, SendMsgService.class);
                        intent.putExtra("data", bean);
                        intent.putExtra("data_from", SendMsgService.FROM_IM);
                        mContext.startService(intent);
                    }
                });
            } else if (flag == 5) {
                //显示文本播放语音状态
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.smsImg.setVisibility(View.VISIBLE);
                viewHolder.smsImg.setBackgroundResource(R.drawable.hx_card_voice_play_bg);
                viewHolder.smsImg.setImageResource(R.drawable.hx_im_voice_left_white_anim);
                viewHolder.smsImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopTextVoice();
                        notifyItemChanged(position);
                    }
                });
                voicePlayAnim = (AnimationDrawable) viewHolder.smsImg.getDrawable();
                voicePlayAnim.start();
                Log.w("123", "voicePlayAnim.start() flag:5");
                mPlayVoiceIV = viewHolder.smsImg;
            } else if (flag == 6) {
                //显示短信状态
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.smsImg.setVisibility(View.GONE);
                viewHolder.smsImg.setImageResource(R.drawable.hx_ic_reminding);
            } else {
                //正在发送状态
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                viewHolder.smsImg.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 重发位置共享
     *
     * @param bean
     */
    void resendLShare(CacheMsgBean bean) {
        ActualLocation.setStatus(ActualLocationFragment.LShareStatus.RESEND.ordinal());
        ActualLocation.setLSharePhone(mDstPhone);
        ActualLocation.setResendCacheMsgBean(bean);
        mIMConnectActivity.showFragment();
    }

    void updateSendStatus(CacheMsgBean cacheMsgBean, int position) {
        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);//更新数据库
        if (position < mImBeanList.size()) {
            mImBeanList.get(position).setSend_flag(cacheMsgBean.getSend_flag());//更新列表显示数据
            notifyItemChanged(position);
        }
    }


    @Override
    public int getItemCount() {
        return mImBeanList.size();
    }


    public int getItemPosition(long id) {
        int index;
        for (index = 0; index < mImBeanList.size(); index++) {
            if (mImBeanList.get(index).getId() == id) {
                return index;
            }
        }
        return index;
    }

    private class BIZCardViewHolder extends BaseViewHolder {

        View lay;
        ImageView headImg;
        TextView nickNameText;
        TextView jobText;
        TextView companyText;
        TextView phoneText;
        TextView emailText;
        TextView addressText;

        public BIZCardViewHolder(View itemView) {
            super(itemView);
            lay = itemView.findViewById(R.id.item_btn);
            headImg = (ImageView) itemView.findViewById(R.id.item_card_head_img);
            nickNameText = (TextView) itemView.findViewById(R.id.item_card_nickname_text);
            jobText = (TextView) itemView.findViewById(R.id.item_card_job_text);
            companyText = (TextView) itemView.findViewById(R.id.item_card_company_text);
            phoneText = (TextView) itemView.findViewById(R.id.item_card_phone_text);
            emailText = (TextView) itemView.findViewById(R.id.item_card_email_text);
            addressText = (TextView) itemView.findViewById(R.id.item_card_address_text);
        }
    }

    private class TxtViewHolder extends BaseViewHolder {

        protected CopeTextView senderTV;

        public TxtViewHolder(View itemView) {
            super(itemView);
            senderTV = (CopeTextView) itemView.findViewById(R.id.sender_tv);
        }
    }

    private class VoiceViewHolder extends BaseViewHolder {

        protected TextView senderTime;
        protected View voiceBtn;
        protected ImageView voiceIV;

        protected View readIV;
        private TextView voiceTypeText;

        private View textLay;
        private TextView contentText;
        private ImageView closeImg;

        public VoiceViewHolder(View itemView) {
            super(itemView);
            senderTime = (TextView) itemView.findViewById(R.id.sender_time);
            voiceBtn = itemView.findViewById(R.id.item_btn);
            voiceIV = (ImageView) itemView.findViewById(R.id.voice_iv);
            readIV = itemView.findViewById(R.id.read_iv);
            voiceTypeText = (TextView) itemView.findViewById(R.id.item_voice_send_type_text);
            textLay = itemView.findViewById(R.id.item_voice_text_lay);
            contentText = (TextView) itemView.findViewById(R.id.item_voice_text);
            closeImg = (ImageView) itemView.findViewById(R.id.item_voice_text_close);
        }
    }

    private class MapViewHolder extends BaseViewHolder {

        protected ImageView senderMap;
        protected TextView senderAddr;
        protected View btnMap;

        public MapViewHolder(View itemView) {
            super(itemView);
            senderMap = (ImageView) itemView.findViewById(R.id.sender_map);
            senderAddr = (TextView) itemView.findViewById(R.id.sender_addr);
            btnMap = itemView.findViewById(R.id.item_btn);
        }
    }

    private class LShareHolder extends BaseViewHolder {

        protected ItemTextView senderTV;
        protected TextView tvShareOver;

        public LShareHolder(View itemView) {
            super(itemView);
            senderTV = (ItemTextView) itemView.findViewById(R.id.sender_tv);
            tvShareOver = (TextView) itemView.findViewById(R.id.tv_share_over);
        }
    }

    private class EmotionViewHolder extends BaseViewHolder {

        protected GifImageView senderEmotion;

        public EmotionViewHolder(View itemView) {
            super(itemView);
            senderEmotion = (GifImageView) itemView.findViewById(R.id.sender_emotion);
        }
    }

    private class ImgViewHolder extends BaseViewHolder {

        protected ImageView senderImg;
        protected View imgBtn;

        public ImgViewHolder(View itemView) {
            super(itemView);
            senderImg = (ImageView) itemView.findViewById(R.id.sender_img);
            imgBtn = itemView.findViewById(R.id.item_btn);
        }
    }

    public class FileViewHolder extends BaseViewHolder {
        protected View rl;
        public ProgressBar filePbar;
        private View fileBtn;
        public ImageView fileIV;
        public TextView fileNameTV;
        public TextView fileSizeTV;


        FileViewHolder(View itemView) {
            super(itemView);
            rl = itemView.findViewById(R.id.rl);
            filePbar = (ProgressBar) itemView.findViewById(R.id.file_pbar);
            fileBtn = itemView.findViewById(R.id.item_btn);
            fileNameTV = (TextView) itemView.findViewById(R.id.file_name);
            fileSizeTV = (TextView) itemView.findViewById(R.id.file_size);
            fileIV = (ImageView) itemView.findViewById(R.id.file_iv);
        }
    }

    private class CallViewHolder extends BaseViewHolder {

        ImageView senderCallImg;
        protected TextView senderTV;

        public CallViewHolder(View itemView) {
            super(itemView);
            senderCallImg = (ImageView) itemView.findViewById(R.id.sender_call_status);
            senderTV = (TextView) itemView.findViewById(R.id.sender_tv);
        }
    }

    private class ShowViewHolder extends BaseViewHolder {

        public ShowViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class RemarkViewHolder extends BaseViewHolder {

        View lay;
        TextView themeText;
        TextView remarkText;
        TextView typeText;

        public RemarkViewHolder(View itemView) {
            super(itemView);
            lay = itemView.findViewById(R.id.item_btn);
            themeText = (TextView) itemView.findViewById(R.id.sender_theme_text);
            remarkText = (TextView) itemView.findViewById(R.id.sender_remark_text);
            typeText = (TextView) itemView.findViewById(R.id.sender_status_text);
        }
    }

    private class VideoViewHolder extends BaseViewHolder {

        View lay;
        ImageView videoImg;
        TextView timeText;
        ImageView videoPlayImg;
        CircleProgressView videoCircleProgressView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            lay = itemView.findViewById(R.id.item_btn);
            videoImg = (ImageView) itemView.findViewById(R.id.sender_img);
            timeText = (TextView) itemView.findViewById(R.id.item_video_time_text);
            videoPlayImg = (ImageView) itemView.findViewById(R.id.item_video_play_img);
            videoCircleProgressView = (CircleProgressView) itemView.findViewById(R.id.item_video_pro);
        }
    }

    private Bitmap mTargetBitmap = null;
    private Bitmap mSelfBitmap = null;

    private void genIcon(String targetPhone, String selfPhone, boolean inContact) {

        if (!isIMType && !selfPhone.equals("4000")) {
            Glide.with(mContext)
                    .asBitmap()
                    .load(R.drawable.hx_ic_launcher)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .circleCrop()
                            .placeholder(R.drawable.hx_ic_launcher))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            mTargetBitmap = resource;
                            notifyDataSetChanged();
                        }
                    });
        } else {
            String urlOther = "";// = AppConfig.DOWNLOAD_IMAGE + targetPhone;
            if (!urlOther.isEmpty()) {
                Glide.with(mContext)
                        .asBitmap()
                        .load(urlOther)
                        .apply(new RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .circleCrop()
                                .placeholder(R.drawable.hx_index_head01))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                mTargetBitmap = resource;
                                notifyDataSetChanged();
                            }
                        });
            }
        }

    }

    private void onBindCommon(final BaseViewHolder baseViewHolder, final int position) {
        int viewType = baseViewHolder.mItemViewType;
        int cardTag = 0;
        if (((viewType & CARD_HEAD_TAIL_FLAG) == CARD_HEAD_TAIL_FLAG)) {
            cardTag = CARD_HEAD_TAIL_FLAG;
        } else if (((viewType & CARD_HEAD_FLAG) == CARD_HEAD_FLAG)) {
            cardTag = CARD_HEAD_FLAG;
        } else if (((viewType & CARD_TAIL_FLAG) == CARD_TAIL_FLAG)) {
            cardTag = CARD_TAIL_FLAG;
        }

        if (cardTag == CARD_HEAD_FLAG
                || cardTag == CARD_HEAD_TAIL_FLAG) {
            //头部处理
            onBindHead(baseViewHolder, position);
        }
        if (cardTag == CARD_TAIL_FLAG
                || cardTag == CARD_HEAD_TAIL_FLAG) {
            //尾部处理
            onBindTail(baseViewHolder, position);
        }
        if (!(baseViewHolder instanceof ShowViewHolder)) {
            //头像
            if (mImBeanList.get(position).isRightUI()) {
                if (mSelfBitmap != null) {
                    baseViewHolder.senderIV.setImageBitmap(mSelfBitmap);
                } else {
                    baseViewHolder.senderIV.setImageResource(R.drawable.hx_index_head01);
                }
            } else {
                if (mTargetBitmap != null) {
                    baseViewHolder.senderIV.setImageBitmap(mTargetBitmap);
                } else {
                    baseViewHolder.senderIV.setImageResource(R.drawable.hx_index_head01);
                }
                baseViewHolder.senderIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null && !isShowSelect) {
                            listener.onHandleAvatarClick();
                        }
                    }
                });
            }

            //时间  累计超10分钟显示
            //首条不显示，将于日期一同显示   2017-3-22 14:35:42
            baseViewHolder.senderDateTV.setText(TimeUtils.getFormateDate3(mImBeanList.get(position).getMsgTime()));//TimeUtils.getFormateDate(mImBeanList.get(position).getMsgTime()));
            if ((cardTag == CARD_HEAD_FLAG) || (cardTag == CARD_HEAD_TAIL_FLAG)
                    || ((position - 1 >= 0)
                    && ((mImBeanList.get(position).getMsgTime() - mImBeanList.get(position - 1).getMsgTime()) / 1000) > (10 * 60))) {
                if ((cardTag == CARD_HEAD_FLAG) || (cardTag == CARD_HEAD_TAIL_FLAG)) {
                    baseViewHolder.senderDateTV.setVisibility(View.GONE);
                } else {
                    baseViewHolder.senderDateTV.setVisibility(View.VISIBLE);
                }
            } else {
                baseViewHolder.senderDateTV.setVisibility(View.GONE);
            }
            final CacheMsgBean cacheMsgBean = mImBeanList.get(position);

            //删除消息
            //TxTViewHolder类型另外处理，TextView添加autoLink属性后会拦截ViewGroup的事件分发,删除消息的提示窗放到CopeTextView里处理
            if (theme == null && !(baseViewHolder instanceof TxtViewHolder)) {
                baseViewHolder.itemBtn.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            mRawX = event.getRawX();
                            mRawY = event.getRawY();
                        }
                        return false;
                    }
                });
                baseViewHolder.itemBtn.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (isShowSelect) {
                            //批量操作不可见
                            return true;
                        }
                        List<TipBean> tips = null;
                        if (baseViewHolder instanceof EmotionViewHolder) {
                            if (mImBeanList.get(position).isRightUI()) {
                                tips = TipsType.getSelfEmotionType();
                            } else {
                                tips = TipsType.getRecEmotionType();
                            }
                        } else if (baseViewHolder instanceof VoiceViewHolder) {
                            CacheMsgVoice cacheMsgVoice = (CacheMsgVoice) mImBeanList.get(position).getJsonBodyObj();
                            if (!cacheMsgVoice.isShowText()) {
                                tips = TipsType.getVoiceType();
                            } else {
                                tips = TipsType.getVoiceType2();
                            }
                        } else if (baseViewHolder instanceof ImgViewHolder
                                || baseViewHolder instanceof VideoViewHolder
                                || baseViewHolder instanceof MapViewHolder
                                || baseViewHolder instanceof FileViewHolder
                                || baseViewHolder instanceof BIZCardViewHolder) {
                            tips = TipsType.getOtherType();
                        } else if (baseViewHolder instanceof RemarkViewHolder) {
                            tips = TipsType.getRemarkType();
                        } else if (baseViewHolder instanceof CallViewHolder) {
                            tips = TipsType.getCallType();
                        }
                        if (tips != null) {
                            tipView = new TipView(mContext, tips, mRawX, mRawY);
                            tipView.setListener(new ItemListener() {
                                @Override
                                public void delete() {
                                    deleteMsg(cacheMsgBean, position, true);
                                }

                                @Override
                                public void copy() {
//                                    Toast.makeText(mContext, "click:复制", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void collect() {
                                    //收藏操作
                                }

                                @Override
                                public void forward() {
                                    //转发操作
                                    if (mImBeanList.get(position).getSend_flag() == 0
                                            || mImBeanList.get(position).getSend_flag() == 2
                                            || mImBeanList.get(position).getSend_flag() == 4) {
                                        CacheMsgBean bean = mImBeanList.get(position);
                                        //语音设置是谁发的源头
                                        if (baseViewHolder instanceof VoiceViewHolder) {
                                            CacheMsgVoice cacheMsgVoice = (CacheMsgVoice) bean.getJsonBodyObj();
                                            if (cacheMsgVoice.getForwardCount() == 0 && TextUtils.isEmpty(cacheMsgVoice.getSourcePhone())) {
                                                cacheMsgVoice.setSourcePhone(bean.isRightUI() ? bean.getReceiverPhone() : bean.getSenderPhone());
                                                bean.setJsonBodyObj(cacheMsgVoice);
                                            }
                                        }

                                        Intent intent = new Intent();
                                        intent.setAction("com.youmai.huxin.recent");
                                        intent.putExtra("type", "forward_msg");
                                        intent.putExtra("data", bean);
                                        mIMConnectActivity.startActivityForResult(intent, 300);
                                    } else {
                                        Toast.makeText(mContext, "转发失败", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void read() {

                                }

                                @Override
                                public void remind() {
                                    if (cacheMsgBean.getMsgId() == null) {
                                        Toast.makeText(mContext, "消息没发送成功，不能设置提醒", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Intent intent = new Intent(mContext, SetRemindActivity.class);
                                    intent.putExtra(SetRemindActivity.CACHE_MSG_BEAN, cacheMsgBean);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    mIMConnectActivity.startActivityForResult(intent, IMConnectionActivity.REQUEST_REMIND_CODE);
                                }

                                @Override
                                public void turnText() {

                                }

                                @Override
                                public void more() {
                                    moreAction(position);
                                }

                                @Override
                                public void emoKeep() {
                                    CacheMsgBean cacheMsgBean = mImBeanList.get(position);
                                    CacheMsgEmotion cacheMsgEmotion = (CacheMsgEmotion) cacheMsgBean.getJsonBodyObj();
                                    try {
                                        if (cacheMsgEmotion.getEmotionRes() == -1) {
                                            String fid = cacheMsgEmotion.getEmotionContent().replace("/", "");
                                            setEmoKeep(fid);
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, e.toString());
                                    }
                                }
                            });
                            tipView.show(view);
                        }
                        return true;
                    }
                });
            }
            if (baseViewHolder.contentLay != null) {
                if (TextUtils.isEmpty(theme)) {
                    baseViewHolder.contentLay.setVisibility(View.VISIBLE);
                } else {
                    if (showIndexList.get(position) == 1) {
                        baseViewHolder.contentLay.setVisibility(View.VISIBLE);
                    } else {
                        baseViewHolder.contentLay.setVisibility(View.GONE);
                    }
                }
            }
        }

        final long remindTime = mImBeanList.get(position).getRemindTime();
        if (remindTime != 0) {
            if (!isShowSelect) {
                //非批量处理情况下
                baseViewHolder.imgRemind.setVisibility(View.VISIBLE);
                final boolean isRemind = remindTime > System.currentTimeMillis();

                if (isRemind) {  //未提醒
                    baseViewHolder.imgRemind.setImageResource(R.drawable.hx_ic_reminding);
                } else { //已提醒
                    baseViewHolder.imgRemind.setImageResource(R.drawable.hx_ic_reminded);
                }

            } else {
                if (baseViewHolder.imgRemind != null)
                    baseViewHolder.imgRemind.setVisibility(View.GONE);
            }
        } else {
            if (baseViewHolder.imgRemind != null)
                baseViewHolder.imgRemind.setVisibility(View.GONE);
        }
        moreAction(baseViewHolder, position);
    }


    public void setEmoKeep(String fid) {

    }


    /**
     * 收藏
     */
    IPostListener iCollectListener = new IPostListener() {
        @Override
        public void httpReqResult(String response) {
            RespBaseBean respBaseBean = GsonUtil.parse(response, RespBaseBean.class);
            if (respBaseBean != null && respBaseBean.isSuccess()) {
                ToastUtil.showToast(mContext, "收藏成功");
            }
        }
    };

    /**
     * 删除单条消息
     *
     * @param cacheMsgBean
     * @param position
     */
    void deleteMsg(CacheMsgBean cacheMsgBean, int position, boolean refreshUI) {
        if (isEditingRemark()) {
            Toast.makeText(mContext, "正在编辑备注。", Toast.LENGTH_SHORT).show();
            return;
        }

        //如果是删除最新的那个或是最后一个   通知主页刷新
        if (position == (mImBeanList.size() - 1)) {
            int type = 1;
            if (mImBeanList.size() == 1) {
                type = 2;
            }
            listener.deleteMsgCallback(type);
        }

        //删除本地
        mImBeanList.remove(cacheMsgBean);
        IMMsgManager.getInstance().removeCacheMsgBean(cacheMsgBean);
        if (refreshUI) {
            notifyDataSetChanged();
        }
    }

    public void multiAscDeleteMsg() {
        Iterator iter = selectMsg.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            int key = (int) entry.getKey();
            CacheMsgBean val = (CacheMsgBean) entry.getValue();
            deleteMsg(val, key, false);
            iter.remove();
        }
        notifyDataSetChanged();
    }

    public void multiDescDeleteMsg() {
        Iterator iter = selectMsg.descendingKeySet().iterator();
        while (iter.hasNext()) {
            int key = (int) iter.next();
            CacheMsgBean val = selectMsg.get(key);
            //LogUtils.e("lee","multiDescDeleteMsg---"+key);
            deleteMsg(val, key, false);
            iter.remove();
        }
        cancelMoreStat();
    }

    /**
     * 头部事件数据处理
     */
    private void onBindHead(final BaseViewHolder baseViewHolder, final int position) {


    }

    //备注ui的显示或编辑
    private void showRemark(BaseViewHolder baseViewHolder, boolean isDisplay, String remarkTheme, String remarkContent, int position) {
        if (isDisplay) {
            baseViewHolder.displayLay.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(remarkTheme) || !TextUtils.isEmpty(remarkContent)) {
                baseViewHolder.displayInfoLay.setVisibility(View.VISIBLE);
            } else {
                baseViewHolder.displayInfoLay.setVisibility(View.GONE);
            }
            baseViewHolder.editLay.setVisibility(View.GONE);
        } else {
            baseViewHolder.displayLay.setVisibility(View.GONE);
            baseViewHolder.editLay.setVisibility(View.VISIBLE);
            if (listener != null) {
                listener.smoothScroll(position, 500, false);
                listener.hasEdit(true);
            }
        }
    }

    //主题备注内容
    private void setRemarkStr(BaseViewHolder baseViewHolder, String remarkTheme, String remarkContent) {
        String themeDefaultStr;
        if (!TextUtils.isEmpty(remarkContent) && TextUtils.isEmpty(remarkTheme)) {
            //只保存了备注内容的情况下，主题自动填充备注内容前5个字，超出部分用省略号表示
            themeDefaultStr = remarkContent.length() > 5 ? remarkContent.substring(0, 5) + "..." : remarkContent;
        } else {
            themeDefaultStr = remarkTheme;
        }
        baseViewHolder.themeText.setText(themeDefaultStr);
        if (remarkContent != null) {
            baseViewHolder.remarkText.setText(remarkContent);
        }
    }

    //预览主题备注，真实显示数据，不做无主题以备注代替
    private void showPreviewView(String themePreStr, String remarkPreStr, int type) {
        if (TextUtils.isEmpty(themePreStr)) {
            themePreStr = mContext.getString(R.string.hx_im_card_preview_theme_default);
        }
        if (TextUtils.isEmpty(remarkPreStr)) {
            remarkPreStr = mContext.getString(R.string.hx_im_card_tail_no_remark);
        }

        final Dialog dialog = new Dialog(mContext, R.style.popupDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View popupRemark = inflater.inflate(R.layout.im_card_popup_remark, null);
        ImageView typeImg = (ImageView) popupRemark.findViewById(R.id.im_card_popup_type_img);

        TextView popupThemeText = (TextView) popupRemark.findViewById(R.id.im_card_popup_theme_text);
        TextView popupRemarkText = (TextView) popupRemark.findViewById(R.id.im_card_popup_remark_text);
        popupThemeText.setText(themePreStr);
        popupRemarkText.setText(remarkPreStr);
        popupRemark.findViewById(R.id.im_card_popup_lay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(popupRemark);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lay = dialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        Activity activity;
        if (mIMConnectActivity == null) {
            activity = fragment.getActivity();
        } else {
            activity = mIMConnectActivity;
        }
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Rect rect = new Rect();
        View view;
        view = activity.getWindow().getDecorView();//decorView是window中的最顶层view，可以从window中获取到decorView
        view.getWindowVisibleDisplayFrame(rect);
        lay.height = dm.heightPixels - rect.top;
        lay.width = dm.widthPixels;

        dialog.show();
    }

    private void onBindTail(final BaseViewHolder baseViewHolder, final int position) {

    }


    //离开编辑备注
    private void quitEditRemark() {
        mEditingRemarkPos = REMARK_UNEDIT_STATE;
        mCurrThemeEdit = null;
        mCurrRemarkEdit = null;
    }

    private void handleRemark(final BaseViewHolder baseViewHolder, final int position, final long headId) {
    }


    public int getEditingRemarkPos() {
        return mEditingRemarkPos;
    }

    public boolean isEditingRemark() {
        return mEditingRemarkPos != REMARK_UNEDIT_STATE;
    }


    class BaseViewHolder extends RecyclerView.ViewHolder {
        int mItemViewType;

        TextView senderDateTV;
        ImageView senderIV;
        View itemBtn;
        public ProgressBar progressBar;
        ImageView smsImg;
        ImageView imgRemind;

        View contentLay;

        View displayLay, displayInfoLay;
        View editLay;
        TextView themeText, remarkText;

        CheckBox selectbox;

        BaseViewHolder(View itemView) {
            super(itemView);
            senderDateTV = (TextView) itemView.findViewById(R.id.sender_date);
            senderDateTV.setTextSize(12);
            senderIV = (ImageView) itemView.findViewById(R.id.sender_iv);
            itemBtn = itemView.findViewById(R.id.item_btn);
            contentLay = itemView.findViewById(R.id.img_content_lay);// 中间背景
            progressBar = (ProgressBar) itemView.findViewById(R.id.pbar);
            smsImg = (ImageView) itemView.findViewById(R.id.im_sms_img);
            imgRemind = (ImageView) itemView.findViewById(R.id.imgRemind);

            selectbox = (CheckBox) itemView.findViewById(R.id.sender_select_checkbox);
        }
    }

    //更多选项
    private void moreAction(final BaseViewHolder baseViewHolder, final int position) {
        if (baseViewHolder.selectbox != null) {
            baseViewHolder.selectbox.setVisibility(isShowSelect ? View.VISIBLE : View.GONE);
            if (isShowSelect) {
                //在更多选项的界面
                baseViewHolder.selectbox.setTag(position);
                baseViewHolder.selectbox.setChecked(selectMsg.containsKey(position));//复用容错判断
            }
            baseViewHolder.selectbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (moreListener != null) {
                        if (selectMsg.containsKey(position)) {
                            selectMsg.remove(position);
                            moreListener.hasSelectMsg(selectMsg.size() != 0);
                        } else {
                            selectMsg.put(position, mImBeanList.get(position));
                            moreListener.hasSelectMsg(true);
                        }
                    }
                }
            });
        }
    }

    //开启批量处理
    private void moreAction(int position) {
        isShowSelect = true;
        selectMsg.clear();
        selectMsg.put(position, mImBeanList.get(position));
        stopTextVoiceUi(true);
        notifyDataSetChanged();
        if (moreListener != null) {
            moreListener.showMore(true);
        }
    }

    //隐藏批量多选框
    public void cancelMoreStat() {
        isShowSelect = false;
        stopTextVoiceUi(false);//若仍在朗读状态，恢复该图标
        notifyDataSetChanged();
    }

    //srsm add start
    public void onStop() {
        stopTextVoice();
        //WindowLeaked
        if (tipView != null && tipView.isShowing()) {
            tipView.dismiss();
        }
        if (voiceTip != null && voiceTip.isShowing()) {
            voiceTip.dismiss();
        }
    }

    //停止语音
    public void stopTextVoice() {
        if (MediaManager.isPlaying()) {
            MediaManager.release();
        }
        //重置语音ui状态
        if (voicePlayAnim != null && voicePlayAnim.isRunning()) {
            voicePlayAnim.stop();
            if (mPlayVoiceIV != null) {
                mPlayVoiceIV.setImageResource(mImBeanList.get(mPlayVoicePosition).isRightUI() ? R.drawable.hx_im_right_anim_v3 : R.drawable.hx_im_left_anim_v3);
            }
        }
        //重置文本语音的状态
        if (mImBeanList != null && mImBeanList.size() > mPlayVoicePosition) {
            CacheMsgBean cacheMsgBean = mImBeanList.get(mPlayVoicePosition);
            if (cacheMsgBean.getJsonBodyObj() instanceof CacheMsgTxt
                    || cacheMsgBean.getJsonBodyObj() instanceof CacheMsgJoke) {
                if (cacheMsgBean.getSend_flag() == 5) {
                    cacheMsgBean.setSend_flag(0);
                    CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                    notifyItemChanged(mPlayVoicePosition);
                }
            }
        }
    }

    //只改界面
    public void stopTextVoiceUi(boolean isHide) {
        //重置文本语音的状态
        if (!isHide && !MediaManager.isPlaying()) {
            //需要显示朗读的图标，但朗读的语音已经结束
            return;
        }
        if (mImBeanList != null && mImBeanList.size() > mPlayVoicePosition) {
            CacheMsgBean cacheMsgBean = mImBeanList.get(mPlayVoicePosition);
            if (cacheMsgBean.getJsonBodyObj() instanceof CacheMsgTxt
                    || cacheMsgBean.getJsonBodyObj() instanceof CacheMsgJoke) {
                if (isHide) {
                    if (cacheMsgBean.getSend_flag() == 5) {
                        cacheMsgBean.setSend_flag(0);
                        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                        notifyItemChanged(mPlayVoicePosition);
                    }
                } else {
                    if (cacheMsgBean.getSend_flag() == 0) {
                        cacheMsgBean.setSend_flag(5);
                        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
                        notifyItemChanged(mPlayVoicePosition);
                    }
                }
            }
        }
    }

    public void resume(boolean inContact) {
        genIcon(mDstPhone, mSelfPhone, inContact);
        loadAfterList();
    }

    public void loadAfterList() {
        long startId = -1;
        if (getItemCount() > 0) {
            startId = mImBeanList.get(getItemCount() - 1).getId();
        }

        List<CacheMsgBean> unReadList = IMMsgManager.getInstance().getCacheMsgBeanListFromStartIndex(
                mDstPhone, true, startId);

        if (unReadList.size() > 0) {
            if (getItemCount() > 0) {
                CacheMsgBean lastMsgBean = mImBeanList.get(getItemCount() - 1);
                List<CacheMsgBean> lastListBean = CacheMsgHelper.instance(mContext).toQueryDescById(lastMsgBean.getId());

                if (lastListBean.size() > 0) {
                    //重新处理最后一条的沟通卡状态
                    mImBeanList.set(getItemCount() - 1, lastListBean.get(0));
                }
                notifyItemChanged(getItemCount() - 1);
            }
            mImBeanList.addAll(unReadList);
            focusBottom(false);
        }
    }
    //srsm add end

    //发送消息的刷新
    public void addAndRefreshUI(CacheMsgBean cacheMsgBean) {
        //add to db
        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
        IMMsgManager.getInstance().addCacheMsgBean(cacheMsgBean);
        mImBeanList.add(cacheMsgBean);
        if (getItemCount() > 1) {
            notifyItemRangeChanged(getItemCount() - 2, 2);//需要更新上一条
        } else {
            notifyItemChanged(getItemCount() - 1);
        }
        if (mEditingRemarkPos == REMARK_UNEDIT_STATE) {
            focusBottom(false);
        }
    }

    /**
     * 收到消息的刷新
     *
     * @param cacheMsgBean
     * @param isV2T        语音消息是否转文本
     */
    public void refreshIncomingMsgUI(CacheMsgBean cacheMsgBean, boolean isV2T) {
        //closeMenuDialog();//关闭菜单视图
        cacheMsgBean.setIs_read(CacheMsgBean.MSG_READ_STATUS);
        addAndRefreshUI(cacheMsgBean);
    }

    //刷新单个item
    public void refreshItemUI(CacheMsgBean cacheMsgBean) {
        long mid = cacheMsgBean.getId();
        int index = -1;
        if (mImBeanList.size() > 0) {
            for (int i = mImBeanList.size() - 1; i >= 0; i--) {
                if (mImBeanList.get(i).getId() == mid) {
                    index = i;
                    break;
                }
            }
            if (index != -1 && index < mImBeanList.size()) {
                //tag 标识迁移
                CacheMsgBean oldBean = mImBeanList.get(index);
                cacheMsgBean.setCardTag(oldBean.getCardTag());
                mImBeanList.set(index, cacheMsgBean);//更新数据
                notifyItemChanged(index);
            }
        }
    }

    //获取item的位置
    public int getPos(CacheMsgBean cacheMsgBean) {
        long mid = cacheMsgBean.getId();
        int index = -1;
        if (mImBeanList.size() > 0) {
            for (int i = mImBeanList.size() - 1; i >= 0; i--) {
                if (mImBeanList.get(i).getId() == mid) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    //刷新单个item的进度
    public void refreshItemUI(long mid, int p) {
        int index = -1;
        if (mImBeanList.size() > 0) {
            for (int i = mImBeanList.size() - 1; i >= 0; i--) {
                if (mImBeanList.get(i).getId() == mid) {
                    index = i;
                    break;
                }
            }
            if (index != -1 && index < mImBeanList.size()) {
                CacheMsgBean cacheMsgBean = mImBeanList.get(index);
                if (cacheMsgBean.getJsonBodyObj() instanceof CacheMsgVideo) {
                    CacheMsgVideo cacheMsgVideo = (CacheMsgVideo) cacheMsgBean.getJsonBodyObj();
                    cacheMsgVideo.setProgress(p);
                    cacheMsgBean.setJsonBodyObj(cacheMsgVideo);
                    mImBeanList.set(index, cacheMsgBean);//更新数据
                    notifyItemChanged(index);
                }
            }
        }
    }


    public void update(CacheMsgBean cacheMsgBean, int position) {
        mImBeanList.set(position, cacheMsgBean);
    }

    public void focusBottom(final boolean smoothScroll) {
        focusBottom(smoothScroll, 300);
    }

    public void focusBottom(final boolean smoothScroll, int delayMillis) {

        if (mIMConnectActivity != null
                && mRecyclerView != null
                && mRecyclerView.getLayoutManager() != null
                && getItemCount() > 0) {

            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                            && mIMConnectActivity.isDestroyed()) {
                        return;
                    }

                    if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManagerWithSmoothScroller) {
                        ((LinearLayoutManagerWithSmoothScroller) mRecyclerView.getLayoutManager()).setScrollerTop(false);
                    }
                    if (!mRecyclerView.canScrollVertically(1)) {
                        //到底了,不用滑动
                        return;
                    }
                    if (smoothScroll) {
                        mRecyclerView.smoothScrollToPosition(getItemCount() - 1);
                    } else {
                        int c = mRecyclerView.getLayoutManager().getItemCount() - 1;
                        int lastPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
                        if (c - lastPosition < 3) {
                            //已经显示最后一个,移动少的用滑动
                            mRecyclerView.smoothScrollToPosition(c);
                        } else {
                            mRecyclerView.scrollToPosition(c);
                        }
                    }
                }
            }, delayMillis);
        }
    }

    //默认设置语音转文字不显示
    private void defaultVoice(List<CacheMsgBean> datas) {
        //TODO 数据库在读取CacheMsgVoice的showText有缓存
        if (datas != null && datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                CacheMsgBean msg = datas.get(i);
                if (msg.getMsgType() == CacheMsgBean.MSG_TYPE_VOICE) {
                    CacheMsgVoice msgVoice = (CacheMsgVoice) msg.getJsonBodyObj();
                    if (msgVoice.isShowText()) {
                        msgVoice.setShowText(false);
                        msg.setJsonBodyObj(msgVoice);
                        datas.set(i, msg);
                    }
                }
            }
        }
    }



    public interface OnListener {
        /**
         * @param position      滚动的位置
         * @param delayMillis   滚动触发时间
         * @param isScrollerTop true:滚动到该item的顶部    false:滚动到item底部
         */
        void smoothScroll(int position, long delayMillis, boolean isScrollerTop);

        /**
         * @param hasEdit 在编辑状态下不允许滑动最底
         */
        void hasEdit(boolean hasEdit);

        void hidden(boolean hidden);

        /**
         * 删除记录回调，1 删除最新的那条，2 全部删除，记录为空
         *
         * @param type
         */
        void deleteMsgCallback(int type);

        /**
         * 头像点击事件
         */
        void onHandleAvatarClick();
    }

    class RemakeTextWatcher implements TextWatcher {

        public static final int TYPE_THEME = 1;
        public static final int TYPE_REMAKE = 2;
        private Context mContext;
        private long mHeadId;
        private EditText mEditText;
        private TextView mSaveButton;
        private int mType;

        public RemakeTextWatcher(Context ctx, long headId, EditText edit, TextView text, int type) {
            this.mContext = ctx;
            this.mHeadId = headId;
            this.mEditText = edit;
            this.mSaveButton = text;
            this.mType = type;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    public interface OnClickMoreListener {
        void showMore(boolean isShow);

        void hasSelectMsg(boolean selected);
    }

}
