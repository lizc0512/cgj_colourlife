package com.youmai.hxsdk.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.IMFilePreviewActivity;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.CropMapActivity;
import com.youmai.hxsdk.activity.IMConnectionActivity;
import com.youmai.hxsdk.activity.PictureIndicatorActivity;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.http.DownloadListener;
import com.youmai.hxsdk.http.FileAsyncTaskDownload;
import com.youmai.hxsdk.im.IMHelper;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.im.cache.CacheMsgMap;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.im.cache.CacheMsgVideo;
import com.youmai.hxsdk.im.cache.CacheMsgVoice;
import com.youmai.hxsdk.im.voice.manager.MediaManager;
import com.youmai.hxsdk.module.remind.SetRemindActivity;
import com.youmai.hxsdk.module.videoplayer.VideoPlayerActivity;
import com.youmai.hxsdk.module.videoplayer.bean.VideoDetailInfo;
import com.youmai.hxsdk.proto.YouMaiMsg;
import com.youmai.hxsdk.service.SendMsgService;
import com.youmai.hxsdk.utils.QiniuUrl;
import com.youmai.hxsdk.utils.TimeUtils;
import com.youmai.hxsdk.view.LinearLayoutManagerWithSmoothScroller;
import com.youmai.hxsdk.view.chat.emoticon.utils.EmoticonHandler;
import com.youmai.hxsdk.view.chat.utils.Utils;
import com.youmai.hxsdk.view.progressbar.CircleProgressView;
import com.youmai.hxsdk.view.text.CopeTextView;
import com.youmai.hxsdk.view.tip.TipView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import jp.wasabeef.glide.transformations.MaskTransformation;


/**
 * Created by colin on 2018/3/23.
 * im message adapter
 */
public class IMGroupAdapter extends RecyclerView.Adapter {

    private static final String TAG = IMGroupAdapter.class.getSimpleName();

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


    private static final int HANDLER_REFRESH_PROGREE = 0;

    private Activity mAct;
    private RecyclerView mRecyclerView;
    private String mDstUuid;
    private List<CacheMsgBean> mImBeanList;
    public int mThemeIndex = -1;

    private OnListener listener;

    public boolean isShowSelect = false;//用于控制显示更多的选项框

    private TreeMap<Integer, CacheMsgBean> selectMsg = new TreeMap<>();

    private OnClickMoreListener moreListener;
    private UIHandler mHandler;

    public IMGroupAdapter(Activity act, RecyclerView recyclerView, String dstUuid) {
        mAct = act;
        mHandler = new UIHandler(this);

        mRecyclerView = recyclerView;
        mDstUuid = dstUuid;
        //srsm add

        mImBeanList = CacheMsgHelper.instance().toQueryCacheMsgListAndSetRead(mAct, dstUuid, true);

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


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mAct);
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
            case CacheMsgBean.SEND_TEXT:
                oriType = TXT_RIGHT;
                break;
            case CacheMsgBean.RECEIVE_TEXT:
            case 0:
                oriType = TXT_LEFT;
                break;
            case CacheMsgBean.SEND_VOICE:
                oriType = VOICE_RIGHT;
                break;
            case CacheMsgBean.RECEIVE_VOICE:
                oriType = VOICE_LEFT;
                break;
            case CacheMsgBean.SEND_IMAGE:
                oriType = IMG_RIGHT;
                break;
            case CacheMsgBean.RECEIVE_IMAGE:
                oriType = IMG_LEFT;
                break;
            case CacheMsgBean.SEND_LOCATION:
                oriType = MAP_RIGHT;
                break;
            case CacheMsgBean.RECEIVE_LOCATION:
                oriType = MAP_LEFT;
                break;
            case CacheMsgBean.SEND_FILE:
                oriType = FILE_RIGHT;
                break;
            case CacheMsgBean.RECEIVE_FILE:
                oriType = FILE_LEFT;
                break;
            case CacheMsgBean.SEND_VIDEO:
                oriType = VIDEO_RIGHT;
                break;
            case CacheMsgBean.RECEIVE_VIDEO:
                oriType = VIDEO_LEFT;
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

        Glide.with(mAct)
                .load(cacheMsgFile.getFileRes())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(holder.fileIV);

        holder.fileNameTV.setText(cacheMsgFile.getFileName());
        holder.fileSizeTV.setText(IMHelper.convertFileSize(cacheMsgFile.getFileSize()));

        showSendStart(holder, cacheMsgBean.getMsgStatus(), cacheMsgBean, position);

        if (cacheMsgBean.isRightUI()) {
            if (cacheMsgBean.getMsgType() == CacheMsgBean.SEND_SUCCEED) {
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
                Intent intent = new Intent(mAct, IMFilePreviewActivity.class);
                intent.putExtra(IMFilePreviewActivity.IM_FILE_BEAN, cacheMsgBean);
                mAct.startActivity(intent);
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
                leftUrl = QiniuUrl.getThumbImageUrl(mAct, cacheMsgImage.getFid(), QiniuUrl.SCALE);
                break;
            default:
                leftUrl = AppConfig.getImageUrl(mAct, cacheMsgImage.getFid());
                break;
        }
        final File imgFile = new File(cacheMsgImage.getFilePath());
        String rightUrl = TextUtils.isEmpty(cacheMsgImage.getFilePath()) ? leftUrl : cacheMsgImage.getFilePath();

        showSendStart(imgViewHolder, cacheMsgBean.getMsgStatus(), cacheMsgBean, position);

        showMsgTime(position, imgViewHolder.senderDateTV, cacheMsgBean.getMsgTime());

        Glide.with(mAct)
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
                    if (CacheMsgBean.SEND_IMAGE == item.getMsgType()
                            || CacheMsgBean.RECEIVE_IMAGE == item.getMsgType()) { //图片
                        beanList.add(item);
                        final CacheMsgImage cacheImage = (CacheMsgImage) item.getJsonBodyObj();
                        String fid = cacheImage.getFid();
                        if (!TextUtils.isEmpty(fid)) {
                            if (finalLeftUrl.equals(AppConfig.getImageUrl(mAct, fid))
                                    || finalLeftUrl.equals(QiniuUrl.getThumbImageUrl(mAct, fid, QiniuUrl.SCALE))) {
                                index = beanList.indexOf(item);
                            }
                        } else {
                            if (imgFile.getAbsolutePath().equals(cacheImage.getFilePath())) {
                                index = beanList.indexOf(item);
                            }
                        }
                    }
                }

                Intent intent = new Intent(mAct, PictureIndicatorActivity.class);
                //intent.putExtra("image", array);
                intent.putExtra("index", index);
                intent.putParcelableArrayListExtra("beanList", beanList);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                    mAct.startActivity(intent);
                } else {
                    mAct.startActivity(intent);
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
        showSendStart(videoViewHolder, cacheMsgBean.getMsgStatus(), cacheMsgBean, position);

        final String videoPath = cacheMsgVideo.getVideoPath();//本地视频
        final String framePath = cacheMsgVideo.getFramePath();//本地视频首帧
        final long time = cacheMsgVideo.getTime();//视频时长(毫秒)
        final String videoUrl = AppConfig.getImageUrl(mAct, cacheMsgVideo.getVideoId());    //上传视频Url
        String leftUrl = AppConfig.getImageUrl(mAct, cacheMsgVideo.getFrameId());     //上传视频首帧Url
        String rightUrl = framePath;
        if (rightUrl == null || !new File(rightUrl).exists()) {
            rightUrl = AppConfig.getImageUrl(mAct, cacheMsgVideo.getFrameId());
        }

        videoViewHolder.timeText.setText(TimeUtils.getTimeFromMillisecond(time));

        showMsgTime(position, videoViewHolder.senderDateTV, cacheMsgBean.getMsgTime());

        Glide.with(mAct)
                .load(cacheMsgBean.isRightUI() ? rightUrl : leftUrl)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).placeholder(R.drawable.hx_im_default_img)
                        .transform(new MaskTransformation(cacheMsgBean.isRightUI() ? R.drawable.hx_im_voice_bg_right : R.drawable.hx_im_voice_bg_left)))
                .into(videoViewHolder.videoImg);

        if (TextUtils.isEmpty(videoPath) && cacheMsgBean.getProgress() != 0) {
            videoViewHolder.videoPlayImg.setVisibility(View.GONE);
            videoViewHolder.videoCircleProgressView.setVisibility(View.VISIBLE);
            videoViewHolder.videoCircleProgressView.setProgress(cacheMsgBean.getProgress());
            videoViewHolder.videoImg.setEnabled(false);
        } else if (TextUtils.isEmpty(videoPath) && cacheMsgBean.getProgress() == -1) {
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
                if (TextUtils.isEmpty(videoPath)) {
                    downVideo(position, videoUrl, cacheMsgBean);
                } else {
                    File videoFile = new File(videoPath);
                    if (videoFile.exists()) {
                        VideoDetailInfo info = new VideoDetailInfo();
                        info.setVideoPath(cacheMsgBean.isRightUI() ? videoPath : videoPath); //视频路径
                        Intent intent = new Intent(mAct, VideoPlayerActivity.class);
                        intent.putExtra("info", info);
                        mAct.startActivity(intent);
                    } else {
                        //文件被删掉，重新下载
                        cacheMsgVideo.setVideoPath("");
                        cacheMsgBean.setJsonBodyObj(cacheMsgVideo);
                        cacheMsgBean.setProgress(0);
                        CacheMsgHelper.instance().insertOrUpdate(mAct, cacheMsgBean);
                        downVideo(position, videoUrl, cacheMsgBean);
                    }
                }
            }
        });
    }

    private void downVideo(final int position, String path, final CacheMsgBean cacheMsgBean) {
        FileAsyncTaskDownload load = new FileAsyncTaskDownload(new DownloadListener() {
            @Override
            public void onProgress(int cur, int total) {
                if (cacheMsgBean.getJsonBodyObj() instanceof CacheMsgVideo) {
                    cacheMsgBean.setProgress(cur * 100 / total);

                    Message msg = mHandler.obtainMessage(HANDLER_REFRESH_PROGREE);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("item", cacheMsgBean);
                    msg.setData(bundle);
                    mHandler.handleMessage(msg);
                }

            }

            @Override
            public void onFail(String err) {
                cacheMsgBean.setProgress(-1);
                mImBeanList.set(position, cacheMsgBean);//更新数据
                CacheMsgHelper.instance().insertOrUpdate(mAct, cacheMsgBean);
                refreshItemUI(cacheMsgBean);
            }

            @Override
            public void onSuccess(String path) {
                if (cacheMsgBean.getJsonBodyObj() instanceof CacheMsgVideo) {
                    cacheMsgBean.setProgress(100);

                    CacheMsgVideo cacheMsgVideo = (CacheMsgVideo) cacheMsgBean.getJsonBodyObj();
                    cacheMsgVideo.setVideoPath(path);
                    cacheMsgBean.setJsonBodyObj(cacheMsgVideo);

                    CacheMsgHelper.instance().insertOrUpdate(mAct, cacheMsgBean);
                    refreshItemUI(cacheMsgBean);

                    File videoFile = new File(path);
                    if (videoFile.exists()) {
                        VideoDetailInfo info = new VideoDetailInfo();
                        info.setVideoPath(path); //视频路径
                        Intent intent = new Intent(mAct, VideoPlayerActivity.class);
                        intent.putExtra("info", info);
                        mAct.startActivity(intent);
                    }
                }
            }
        }, "video_" + System.currentTimeMillis() + ".jv");
        load.setDownloadpath(FileConfig.getBigFileDownLoadPath());

        load.execute(path);
    }


    /**
     * 文字
     *
     * @param txtViewHolder
     * @param position
     */
    private void onBindTxt(TxtViewHolder txtViewHolder, final int position) {
        final CacheMsgBean cacheMsgBean = mImBeanList.get(position);

        CacheMsgTxt cacheMsgTxt = (CacheMsgTxt) cacheMsgBean.getJsonBodyObj();
        String txtContent = cacheMsgTxt.getMsgTxt();

        showSendStart(txtViewHolder, cacheMsgBean.getMsgStatus(), cacheMsgBean, position);

        showMsgTime(position, txtViewHolder.senderDateTV, cacheMsgBean.getMsgTime());

        if (txtContent != null) {
            SpannableString msgSpan = new SpannableString(txtContent);
            msgSpan = EmoticonHandler.getInstance(mAct).getTextFace(txtContent, msgSpan, 0,
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
                        cacheMsgBean1.setMsgStatus(CacheMsgBean.SEND_SUCCEED);
                        CacheMsgHelper.instance().insertOrUpdate(mAct, cacheMsgBean1);
                        mImBeanList.set(mPlayVoicePosition, cacheMsgBean1);
                        notifyItemChanged(mPlayVoicePosition);
                    }
                }
            });
            txtViewHolder.senderTV.setOnClickLis(new CopeTextView.OnCopeListener() {
                @Override
                public void copeText() {
                    Toast.makeText(mAct, R.string.hx_im_card_cope_tip, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void forwardText(CharSequence s) {
                    //点击转发事件
                    int status = mImBeanList.get(position).getMsgType();
                    if (status == CacheMsgBean.SEND_SUCCEED
                            || status == CacheMsgBean.SEND_FAILED
                            || status == CacheMsgBean.RECEIVE_READ) {
                        Intent intent = new Intent();
                        intent.setAction("com.youmai.huxin.recent");
                        intent.putExtra("type", "forward_msg");
                        intent.putExtra("data", mImBeanList.get(position));
                        mAct.startActivityForResult(intent, 300);
                    } else {
                        Toast.makeText(mAct, "转发失败", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(mAct, "消息没发送成功，不能设置提醒", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(mAct, SetRemindActivity.class);
                    intent.putExtra(SetRemindActivity.CACHE_MSG_BEAN, cacheMsgBean);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    mAct.startActivityForResult(intent, IMConnectionActivity.REQUEST_REMIND_CODE);
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
        final double latitude = cacheMsgMap.getLatitude();
        final double longitude = cacheMsgMap.getLongitude();

        showSendStart(mapViewHolder, cacheMsgBean.getMsgStatus(), cacheMsgBean, position);

        showMsgTime(position, mapViewHolder.senderDateTV, cacheMsgBean.getMsgTime());

        Glide.with(mAct)
                .load(mapUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .transform(new MaskTransformation(cacheMsgBean.isRightUI() ? R.drawable.hx_im_card_map_right : R.drawable.hx_im_card_map_left)))
                .into(mapViewHolder.senderMap);

        mapViewHolder.senderAddr.setText(mapAddr);

        mapViewHolder.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.setClass(mAct, CropMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("labelAddress", mapAddr);
                mAct.startActivity(intent);
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

    private void onBindVoice(final VoiceViewHolder voiceViewHolder, final int position) {
        final CacheMsgBean cacheMsgBean = mImBeanList.get(position);
        final CacheMsgVoice cacheMsgVoice = (CacheMsgVoice) cacheMsgBean.getJsonBodyObj();

        String voiceTime = cacheMsgVoice.getVoiceTime();

        showSendStart(voiceViewHolder, cacheMsgBean.getMsgStatus(), cacheMsgBean, position);

        showMsgTime(position, voiceViewHolder.senderDateTV, cacheMsgBean.getMsgTime());

        if (voiceViewHolder.readIV != null) {
            if (cacheMsgVoice.isHasLoad()) { //到达
                voiceViewHolder.readIV.setVisibility(View.INVISIBLE);
            } else {
                voiceViewHolder.readIV.setVisibility(View.VISIBLE);
            }
        }

        voiceViewHolder.senderTime.setText(getFormateVoiceTime(voiceTime));

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
                        if (cacheMsgBean1.getMsgType() == YouMaiMsg.IM_CONTENT_TYPE.IM_CONTENT_TYPE_TEXT_VALUE) {
                            if (voicePlayAnim != null && voicePlayAnim.isRunning()) {
                                voicePlayAnim.stop();
                            }
                            cacheMsgBean1.setMsgStatus(CacheMsgBean.SEND_SUCCEED);
                            CacheMsgHelper.instance().insertOrUpdate(mAct, cacheMsgBean1);
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
                        CacheMsgHelper.instance().insertOrUpdate(mAct, cacheMsgBean);
                    }
                    voiceViewHolder.readIV.setVisibility(View.INVISIBLE);
                }

                voiceViewHolder.voiceIV.setImageResource(isRightUi ? R.drawable.hx_im_voice_right_anim : R.drawable.hx_im_voice_left_anim);
                voicePlayAnim = (AnimationDrawable) voiceViewHolder.voiceIV.getDrawable();
                voicePlayAnim.start();

                String path;
                if (TextUtils.isEmpty(cacheMsgVoice.getVoicePath())) {
                    path = cacheMsgVoice.getVoiceUrl();
                } else {
                    path = cacheMsgVoice.getVoicePath();
                }

                //播放声音
                MediaManager.playSound(path, new MediaPlayer.OnCompletionListener() {
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


    private void showMsgTime(int position, TextView textView, long curTime) {
        textView.setText(TimeUtils.getTime(curTime));

        if (position >= 1) {
            final CacheMsgBean cacheMsgBean = mImBeanList.get(position - 1);
            long lastTime = cacheMsgBean.getMsgTime();

            if (curTime - lastTime < 30 * 1000) {
                textView.setVisibility(View.GONE);
            } else {
                textView.setVisibility(View.VISIBLE);
            }
        }

    }


    //支持失败重传
    //根据flag显示发送状态 0:发送成功  -1:正在发送  2:短信发送  4:发送错误  5:文本播放语音 6:提醒状态
    private void showSendStart(final BaseViewHolder viewHolder, int flag, final CacheMsgBean bean, final int position) {

        if (viewHolder.progressBar != null) {
            if (flag == CacheMsgBean.SEND_SUCCEED
                    || flag == CacheMsgBean.RECEIVE_READ) {
                //显示到达状态
                viewHolder.progressBar.setVisibility(View.INVISIBLE);
            } else if (flag == CacheMsgBean.SEND_FAILED) {
                //显示发送失败状态
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.smsImg.setVisibility(View.VISIBLE);
                viewHolder.smsImg.setImageResource(R.drawable.hx_im_send_error2_icon);
                viewHolder.smsImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //重传
                        bean.setMsgStatus(CacheMsgBean.SEND_GOING);
                        updateSendStatus(bean, position);
                        Intent intent = new Intent(mAct, SendMsgService.class);
                        intent.putExtra("data", bean);
                        intent.putExtra("data_from", SendMsgService.FROM_IM);
                        mAct.startService(intent);
                    }
                });
            } else {
                //正在发送状态
                viewHolder.progressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    void updateSendStatus(CacheMsgBean cacheMsgBean, int position) {
        CacheMsgHelper.instance().insertOrUpdate(mAct, cacheMsgBean);//更新数据库
        if (position < mImBeanList.size()) {
            mImBeanList.get(position).setMsgStatus(cacheMsgBean.getMsgStatus());//更新列表显示数据
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


        public VoiceViewHolder(View itemView) {
            super(itemView);
            senderTime = (TextView) itemView.findViewById(R.id.sender_time);
            voiceBtn = itemView.findViewById(R.id.item_btn);
            voiceIV = (ImageView) itemView.findViewById(R.id.voice_iv);
            readIV = itemView.findViewById(R.id.read_iv);
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
        public ProgressBar filePbar;
        private View fileBtn;
        public ImageView fileIV;
        public TextView fileNameTV;
        public TextView fileSizeTV;


        FileViewHolder(View itemView) {
            super(itemView);
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
        if (refreshUI) {
            notifyDataSetChanged();
        }
    }


    class BaseViewHolder extends RecyclerView.ViewHolder {
        int mItemViewType;
        TextView senderDateTV;
        ImageView senderIV;
        View itemBtn;
        ProgressBar progressBar;
        ImageView smsImg;
        View contentLay;


        BaseViewHolder(View itemView) {
            super(itemView);
            senderDateTV = (TextView) itemView.findViewById(R.id.sender_date);
            senderDateTV.setTextSize(12);
            senderIV = (ImageView) itemView.findViewById(R.id.sender_iv);
            itemBtn = itemView.findViewById(R.id.item_btn);
            contentLay = itemView.findViewById(R.id.img_content_lay);// 中间背景
            progressBar = (ProgressBar) itemView.findViewById(R.id.pbar);
            smsImg = (ImageView) itemView.findViewById(R.id.im_sms_img);
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
        if (voiceTip != null && voiceTip.isShowing()) {
            voiceTip.dismiss();
        }
    }


    public void loadAfterList() {
        long startId = -1;
        if (getItemCount() > 0) {
            startId = mImBeanList.get(getItemCount() - 1).getId();
        }

        List<CacheMsgBean> unReadList = CacheMsgHelper.instance().getCacheMsgBeanListFromStartIndex(
                mAct, startId, mDstUuid, true);

        if (unReadList.size() > 0) {
            if (getItemCount() > 0) {
                CacheMsgBean lastMsgBean = mImBeanList.get(getItemCount() - 1);
                List<CacheMsgBean> lastListBean = CacheMsgHelper.instance().toQueryDescById(mAct, lastMsgBean.getId());

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

    //发送消息的刷新
    public void addAndRefreshUI(CacheMsgBean cacheMsgBean) {
        //add to db
        CacheMsgHelper.instance().insertOrUpdate(mAct, cacheMsgBean);

        mImBeanList.add(cacheMsgBean);
        if (getItemCount() > 1) {
            notifyItemRangeChanged(getItemCount() - 2, 2);//需要更新上一条
        } else {
            notifyItemChanged(getItemCount() - 1);
        }

        focusBottom(false);
    }

    /**
     * 收到消息的刷新
     *
     * @param cacheMsgBean
     */
    public void refreshIncomingMsgUI(CacheMsgBean cacheMsgBean) {
        //closeMenuDialog();//关闭菜单视图
        cacheMsgBean.setMsgStatus(CacheMsgBean.RECEIVE_READ);
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
                    cacheMsgBean.setJsonBodyObj(cacheMsgVideo);
                    cacheMsgBean.setProgress(p);
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

        if (mAct != null
                && mRecyclerView != null
                && mRecyclerView.getLayoutManager() != null
                && getItemCount() > 0) {

            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                            && mAct.isDestroyed()) {
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


    private static class UIHandler extends Handler {
        private final WeakReference<IMGroupAdapter> mTarget;

        UIHandler(IMGroupAdapter target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            final IMGroupAdapter adapter = mTarget.get();

            switch (msg.what) {
                case HANDLER_REFRESH_PROGREE:
                    Bundle bundle = msg.getData();
                    CacheMsgBean cacheMsgBean = bundle.getParcelable("item");
                    adapter.refreshItemUI(cacheMsgBean);
                    break;
                default:
                    break;
            }
        }

    }

}
