package com.youmai.hxsdk.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import com.youmai.hxsdk.im.IMHelper;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.im.cache.CacheMsgJoke;
import com.youmai.hxsdk.im.cache.CacheMsgLShare;
import com.youmai.hxsdk.im.cache.CacheMsgMap;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.im.cache.CacheMsgVideo;
import com.youmai.hxsdk.im.cache.CacheMsgVoice;
import com.youmai.hxsdk.im.voice.manager.MediaManager;
import com.youmai.hxsdk.module.remind.SetRemindActivity;
import com.youmai.hxsdk.service.SendMsgService;
import com.youmai.hxsdk.utils.QiniuUrl;
import com.youmai.hxsdk.utils.TimeUtils;
import com.youmai.hxsdk.view.LinearLayoutManagerWithSmoothScroller;
import com.youmai.hxsdk.view.chat.emoticon.utils.EmoticonHandler;
import com.youmai.hxsdk.view.chat.utils.Utils;
import com.youmai.hxsdk.view.progressbar.CircleProgressView;
import com.youmai.hxsdk.view.text.CopeTextView;
import com.youmai.hxsdk.view.tip.TipView;
import com.youmai.hxsdk.view.tip.bean.TipBean;
import com.youmai.hxsdk.view.tip.listener.ItemListener;
import com.youmai.hxsdk.view.tip.tools.TipsType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import jp.wasabeef.glide.transformations.MaskTransformation;


/**
 * Created by colin on 2018/3/23.
 * im message adapter
 */
public class IMListAdapter extends RecyclerView.Adapter {

    private static final String TAG = IMListAdapter.class.getSimpleName();

    private static final int IMG_LEFT = 0;  //图片左

    private static final int MAP_LEFT = 2; //地图左

    private static final int VOICE_LEFT = 3; //声音左

    private static final int TXT_LEFT = 4; //文字左

    private static final int IMG_RIGHT = 5; //图片右

    private static final int MAP_RIGHT = 7; //地图右

    private static final int VOICE_RIGHT = 8; //声音右

    private static final int TXT_RIGHT = 9; //文字右

    private static final int FILE_LEFT = 10; //文件左

    private static final int FILE_RIGHT = 11;//文件右

    private static final int VIDEO_LEFT = 20;//视频左

    private static final int VIDEO_RIGHT = 21;//视频右


    private Context mContext;
    private IMConnectionActivity mIMConnectActivity;
    private RecyclerView mRecyclerView;
    private String mDstPhone;
    private String mSelfPhone = HuxinSdkManager.instance().getPhoneNum();
    private List<CacheMsgBean> mImBeanList = new ArrayList<>();
    public int mThemeIndex = -1;

    private boolean hasItemClick = false;

    private OnListener listener;

    private Toast mGolbalToast;

    private final int REMARK_UNEDIT_STATE = -99;
    private int mEditingRemarkPos = REMARK_UNEDIT_STATE;

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
        //srsm add
        mImBeanList.addAll(IMMsgManager.getInstance().genCacheMsgBeanList(mContext, mDstPhone, true));

        mRecyclerView.getItemAnimator().setChangeDuration(0);
        mRecyclerView.getItemAnimator().setAddDuration(0);
        mRecyclerView.getItemAnimator().setRemoveDuration(0);
        mRecyclerView.getItemAnimator().setMoveDuration(0);
    }

    public List<CacheMsgBean> getmImBeanList() {
        return mImBeanList;
    }

    public TreeMap<Integer, CacheMsgBean> getSelectMsg() {
        return selectMsg;
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


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case IMG_LEFT:
                view = inflater.inflate(R.layout.hx_fragment_im_left_img_item, parent, false);
                holder = new ImgViewHolder(view);
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
            case VIDEO_LEFT:
                view = inflater.inflate(R.layout.hx_fragment_im_left_video_item, parent, false);
                holder = new VideoViewHolder(view);
                break;
            case VIDEO_RIGHT:
                view = inflater.inflate(R.layout.hx_fragment_im_right_video_item, parent, false);
                holder = new VideoViewHolder(view);
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
        } else if (holder instanceof FileViewHolder) { //文件
            onBindFile((FileViewHolder) holder, position);
        } else if (holder instanceof VideoViewHolder) {//视频
            onBindVideo((VideoViewHolder) holder, position);
        }
    }


    @Override
    public int getItemViewType(int position) {
        CacheMsgBean cacheMsgBean = mImBeanList.get(position);
        int oriType = -1;
        switch (cacheMsgBean.getMsgType()) {
            case CacheMsgBean.MSG_TYPE_TXT:
                oriType = cacheMsgBean.isRightUI() ? TXT_RIGHT : TXT_LEFT;
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
            case CacheMsgBean.MSG_TYPE_VIDEO:
                oriType = cacheMsgBean.isRightUI() ? VIDEO_RIGHT : VIDEO_LEFT;
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
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
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
                        .transform(new MaskTransformation(cacheMsgBean.isRightUI() ? R.drawable.hx_im_voice_bg_right : R.drawable.hx_im_voice_bg_left)))
                .into(imgViewHolder.senderImg);

        final String finalLeftUrl = leftUrl;
        imgViewHolder.imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                Intent intent = new Intent(mContext, PictureIndicatorActivity.class);
                //intent.putExtra("image", array);
                intent.putExtra("index", index);
                intent.putParcelableArrayListExtra("beanList", beanList);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
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
        }
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

    private void onBindCommon(final BaseViewHolder baseViewHolder, final int position) {
        moreAction(baseViewHolder, position);
    }


    /**
     * 删除单条消息
     *
     * @param cacheMsgBean
     * @param position
     */
    void deleteMsg(CacheMsgBean cacheMsgBean, int position, boolean refreshUI) {
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


    class BaseViewHolder extends RecyclerView.ViewHolder {
        int mItemViewType;

        TextView senderDateTV;
        ImageView senderIV;
        View itemBtn;
        public ProgressBar progressBar;
        ImageView smsImg;

        View contentLay;

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
        notifyDataSetChanged();
        if (moreListener != null) {
            moreListener.showMore(true);
        }
    }

    //隐藏批量多选框
    public void cancelMoreStat() {
        isShowSelect = false;
        notifyDataSetChanged();
    }

    //srsm add start
    public void onStop() {
        //WindowLeaked
        if (tipView != null && tipView.isShowing()) {
            tipView.dismiss();
        }
        if (voiceTip != null && voiceTip.isShowing()) {
            voiceTip.dismiss();
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
     */
    public void refreshIncomingMsgUI(CacheMsgBean cacheMsgBean) {
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
                mImBeanList.set(index, cacheMsgBean);//更新数据
                notifyItemChanged(index);
            }
        }
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

    public interface OnClickMoreListener {
        void showMore(boolean isShow);

        void hasSelectMsg(boolean selected);
    }

}
