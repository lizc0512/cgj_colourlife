package com.youmai.hxsdk.module.callmsg;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.IMFilePreviewActivity;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.CropImageActivity;
import com.youmai.hxsdk.activity.CropMapActivity;
import com.youmai.hxsdk.activity.CropVideoActivity;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.chat.ContentVideo;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.bean.ChatMsg;
import com.youmai.hxsdk.chat.ContentLocation;
import com.youmai.hxsdk.chat.ContentText;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.entity.EmoInfo;
import com.youmai.hxsdk.im.IMHelper;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.im.cache.CacheMsgJoke;
import com.youmai.hxsdk.im.voice.manager.MediaManager;
import com.youmai.hxsdk.service.HuxinService;
import com.youmai.hxsdk.socket.IMContentType;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.FloatLogoUtil;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.utils.TimeUtils;
import com.youmai.hxsdk.view.full.FloatViewUtil;
import com.youmai.hxsdk.view.full.MapViewUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * 作者：create by YW
 * 日期：2017.02.06 10:32
 * 描述：通话接收到的聊天消息
 */
public class MsgRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = MsgRecyclerAdapter.class.getSimpleName();

    public enum UI_TYPE {
        TEXT_NORMAL, TEXT_GIF, IMAGE, LOCATION, VOICE, FILE, VIDEO
    }

    private Context mContext;
    private String mDstPhone;
    private List<ChatMsg> mList;

    public MsgRecyclerAdapter(Context context, String dstPhone) {
        mContext = context;
        mDstPhone = dstPhone;
        mList = new ArrayList<>();
    }

    public void updateDataList(List<ChatMsg> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {  //给Item划分类别

        int type = UI_TYPE.TEXT_NORMAL.ordinal();
        ChatMsg msg = mList.get(position);

        if (msg.getMsgType() == ChatMsg.MsgType.TEXT) {// 普通文本
            ContentText text = msg.getMsgContent().getText();
            String con = text.getContent();
            if (new EmoInfo(mContext).isEmotion(con) || con.startsWith("/")) {
                type = UI_TYPE.TEXT_GIF.ordinal();
            } else {
                type = UI_TYPE.TEXT_NORMAL.ordinal();
            }
        } else if (msg.getMsgType() == ChatMsg.MsgType.EMO_TEXT) { // GIF图

            type = UI_TYPE.TEXT_GIF.ordinal();

        } else if (msg.getMsgType() == ChatMsg.MsgType.PICTURE) {// 图片

            type = UI_TYPE.IMAGE.ordinal();

        } else if (msg.getMsgType() == ChatMsg.MsgType.LOCATION) {// 位置

            type = UI_TYPE.LOCATION.ordinal();

        } else if (msg.getMsgType() == ChatMsg.MsgType.AUDIO) {// 语音

            type = UI_TYPE.VOICE.ordinal();

        } else if (msg.getMsgType() == ChatMsg.MsgType.BIG_FILE) {// 文件

            type = UI_TYPE.FILE.ordinal();

        } else if (msg.getMsgType() == ChatMsg.MsgType.VIDEO) {// 视频

            type = UI_TYPE.VIDEO.ordinal();

        }
        return type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = null;
        RecyclerView.ViewHolder holder = null;

        switch (UI_TYPE.values()[viewType]) {   //根据Item类别不同，选择不同的Item布局

            case IMAGE:
                view = inflater.inflate(R.layout.hx_msg_image, parent, false);
                holder = new ImageViewHolder(view);
                break;
            case LOCATION:
                view = inflater.inflate(R.layout.hx_msg_map, parent, false);
                holder = new MapIMGViewHolder(view);
                break;
            case TEXT_NORMAL:
                view = inflater.inflate(R.layout.hx_msg_text, parent, false);
                holder = new TxtViewHolder(view);
                break;
            case TEXT_GIF:
                view = inflater.inflate(R.layout.hx_msg_emo, parent, false);
                holder = new EmoViewHolder(view);
                break;
            case VOICE:
                view = inflater.inflate(R.layout.hx_msg_voice, parent, false);
                holder = new VoiceViewHolder(view);
                break;
            case FILE:
                view = inflater.inflate(R.layout.hx_msg_file, parent, false);
                holder = new FileViewHolder(view);
                break;
            case VIDEO:
                view = inflater.inflate(R.layout.hx_msg_video, parent, false);
                holder = new VideoViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder == null) {
            return;
        }

        if (holder instanceof ImageViewHolder) { //图片
            onBindPic((ImageViewHolder) holder, position);
        } else if (holder instanceof MapIMGViewHolder) { //地图
            onBindMap((MapIMGViewHolder) holder, position);
        } else if (holder instanceof EmoViewHolder) { //表情
            onBindEmotion((EmoViewHolder) holder, position);
        } else if (holder instanceof TxtViewHolder) { //文字
            onBindTxt((TxtViewHolder) holder, position);
        } else if (holder instanceof VoiceViewHolder) { //语音
            onBindVoice((VoiceViewHolder) holder, position);
        } else if (holder instanceof FileViewHolder) { //文件
            onBindFile((FileViewHolder) holder, position);
        } else if (holder instanceof VideoViewHolder) { //视频
            onBindVideo((VideoViewHolder) holder, position);
        }
    }

    /********************************************
     * /**
     * 显示表情
     *
     * @param emotionViewHolder
     * @param position
     */
    private void onBindEmotion(final EmoViewHolder emotionViewHolder, int position) {

        ChatMsg chatMsg = mList.get(position);
        String con = chatMsg.getMsgContent().getText().getContent();
        if (new EmoInfo(mContext).isEmotion(con)) {
            List<EmoInfo.EmoItem> dataList = new EmoInfo(mContext).getEmoList();
            for (EmoInfo.EmoItem emoItem : dataList) {
                if (emoItem.getEmoStr().equals(con)) {
                    emotionViewHolder.gv_gifView.setImageResource(emoItem.getEmoRes());
                    //HuxinSdkManager.instance().playSound(emoItem.getSoundId());
                    break;
                }
            }
        } else if (con.startsWith("/")) {
            String path = AppConfig.DOWNLOAD_IMAGE + con + "?imageView2/0/w/250/h/250";
            Glide.with(mContext)
                    .load(path)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).fitCenter())
                    .into(emotionViewHolder.gv_gifView);
        }
    }

    /**
     * 显示文字
     *
     * @param txtViewHolder
     * @param position
     */
    private void onBindTxt(TxtViewHolder txtViewHolder, int position) {

        ContentText text = mList.get(position).getMsgContent().getText();

        txtViewHolder.contentTV.setText(text.getContent().replace(CacheMsgJoke.JOKES, ""));
    }

    private boolean isCanPlay = true;

    /**
     * 显示声音
     *
     * @param voiceViewHolder
     * @param position
     */
    private void onBindVoice(final VoiceViewHolder voiceViewHolder, int position) {

        final ChatMsg msgItem = mList.get(position);

        String fid = msgItem.getMsgContent().getAudio().getAudioId();
        final String url = AppConfig.DOWNLOAD_IMAGE + fid;
        String seconds = msgItem.getMsgContent().getAudio().getBarTime();

        voiceViewHolder.msg_sender_time.setText(getFormateVoiceTime(seconds));
        voiceViewHolder.msg_read_iv.setVisibility(msgItem.getMsgContent().getAudio().isPlay() ? View.INVISIBLE : View.INVISIBLE);//先隐藏红点

        voiceViewHolder.viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCanPlay) {
                    return;
                }

                isCanPlay = false;

                if (!(msgItem.getMsgContent().getAudio().isPlay())) { //红点判断
                    msgItem.getMsgContent().getAudio().setPlay(true);
                }

                voiceViewHolder.msg_read_iv.setVisibility(View.INVISIBLE);

                voiceViewHolder.msg_voice_iv.setImageResource(R.drawable.hx_im_voice_left_anim);

                final AnimationDrawable anim = (AnimationDrawable) voiceViewHolder.msg_voice_iv.getDrawable();
                anim.start();

                //播放声音
                MediaManager.playSound(url, new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {

                        isCanPlay = true;
                        anim.stop();
                        voiceViewHolder.msg_voice_iv.setImageResource(R.drawable.hx_im_left_anim_v3);
                    }
                });
            }
        });
    }

    /**
     * 获取语音格式化时间.
     *
     * @param voiceTime
     * @return
     */
    private String getFormateVoiceTime(String voiceTime) {
        try {
            float voiceF = Float.valueOf(voiceTime);
            return Math.round(voiceF) + "\"";
        } catch (Exception e) {
            return voiceTime;
        }
    }

    /**
     * 显示图片.
     *
     * @param imgHolder
     * @param position
     */
    private void onBindPic(ImageViewHolder imgHolder, int position) {

        ChatMsg msgItem = mList.get(position);

        String fid = msgItem.getMsgContent().getPicture().getPicUrl();

        final String imgUrl = AppConfig.getImageUrl(mContext, fid);

        Glide.with(mContext.getApplicationContext())
                .load(imgUrl)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).placeholder(R.drawable.hxm_call_msg_item_default_bg)
                        .error(R.drawable.hxm_call_msg_item_default_bg).override(800, 800))
                .into(imgHolder.msg_image_iv);

        if (!StringUtils.isEmpty(imgUrl)) {
            imgHolder.clickView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof MsgActivity) {
                        MsgActivity act = (MsgActivity) mContext;
                        act.setFloatView(false);
                    }

                    Intent intent = new Intent();
                    intent.setClass(mContext, CropImageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(SdkBaseActivity.SHOW_FLOAT_VIEW, false);
                    intent.putExtra("isImageUrl", imgUrl);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    /**
     * 显示文件.
     *
     * @param fileViewHolder
     * @param position
     */
    private void onBindFile(FileViewHolder fileViewHolder, int position) {

        final ChatMsg msgItem = mList.get(position);
        String jsonBody = msgItem.getJsonBoby();
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
                        .setRightUI(false);

                fileViewHolder.msg_file_iv.setImageResource(IMHelper.getFileImgRes(fileName, false));/*cacheMsgFile.getFileRes()*/
                fileViewHolder.msg_file_name.setText(fileName);
                fileViewHolder.msg_file_size.setText(IMHelper.convertFileSize(Long.parseLong(fileSize)));

                fileViewHolder.btnView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mContext instanceof MsgActivity) {
                            MsgActivity act = (MsgActivity) mContext;
                            act.setFloatView(false);
                        }

                        Intent intent = new Intent(mContext, IMFilePreviewActivity.class);
                        intent.putExtra(SdkBaseActivity.SHOW_FLOAT_VIEW, false);
                        intent.putExtra(IMFilePreviewActivity.IM_FILE_BEAN, cacheMsgBean);
                        mContext.startActivity(intent);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 显示地图.
     *
     * @param mapHolder
     * @param position
     */
    private void onBindMap(MapIMGViewHolder mapHolder, int position) {

        final ChatMsg msgItem = mList.get(position);

        final ContentLocation mLocation = msgItem.getMsgContent().getLocation();
        final String location = mLocation.getLongitudeStr() + "," + mLocation.getLatitudeStr();//经纬度
        final String mLabelAddress = mLocation.getLabelStr();//地址

        if (!StringUtils.isEmpty(mLabelAddress)) {

            try {
                if (mLabelAddress.contains(":")) {
                    mapHolder.msg_map_tv.setVisibility(View.VISIBLE);
                    mapHolder.msg_map_tv.setText(mLabelAddress.split(":")[1]);
                } else {
                    mapHolder.msg_map_tv.setVisibility(View.VISIBLE);
                    mapHolder.msg_map_tv.setText(mLabelAddress);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mapHolder.msg_map_tv.setVisibility(View.VISIBLE);
                mapHolder.msg_map_tv.setText(mLabelAddress);
            }
        }

        if (!AppUtils.isGooglePlay(mContext)) {
            final MapViewUtil mMapViewUtil = new MapViewUtil(mContext, null);
            mMapViewUtil.setLocation(location);//标志物
        }

        final String url = "http://restapi.amap.com/v3/staticmap?location="
                + mLocation.getLongitudeStr() + "," + mLocation.getLatitudeStr() + "&zoom=" + 17
                + "&scale=2&size=720*550&traffic=1&markers=mid,0xff0000,A:" + mLocation.getLongitudeStr()
                + "," + mLocation.getLatitudeStr() + "&key=" + AppConfig.staticMapKey;

        Glide.with(mContext)
                .load(url)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).placeholder(R.drawable.hxm_call_msg_item_default_bg)
                        .error(R.drawable.hxm_call_msg_item_default_bg).override(800, 800))
                .into(mapHolder.msg_map_iv);

        mapHolder.clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                if (mContext instanceof MsgActivity) {
                    MsgActivity act = (MsgActivity) mContext;
                    act.setFloatView(false);
                }

                intent.setClass(mContext, CropMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(SdkBaseActivity.SHOW_FLOAT_VIEW, false);
                intent.putExtra("location", location);
                intent.putExtra("labelAddress", mLabelAddress);
                mContext.startActivity(intent);
            }
        });
    }

    private void onBindVideo(VideoViewHolder holder, int position) {

        ChatMsg msgItem = mList.get(position);
        final ContentVideo video = msgItem.getMsgContent().getVideo();

        long time;
        try {
            time = Long.valueOf(video.getBarTime());
            Glide.with(mContext)
                    .load(AppConfig.DOWNLOAD_IMAGE + video.getFrameId())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).centerCrop())
                    .into(holder.item_video_sender_img);

        } catch (Exception e) {
            time = 0L;
        }

        holder.item_video_time_text.setText(TimeUtils.getTimeFromMillisecond(time));
        holder.video_item_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof MsgActivity) {
                    MsgActivity act = (MsgActivity) mContext;
                    act.setFloatView(false);
                }

                Intent intent = new Intent();
                intent.setClass(mContext, CropVideoActivity.class);
                intent.putExtra(SdkBaseActivity.SHOW_FLOAT_VIEW, false);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("isVideoUrl", AppConfig.DOWNLOAD_IMAGE + video.getVideoId());
                mContext.startActivity(intent);
            }
        });
    }


    /********************************************
     * /**
     * 表情
     */
    class EmoViewHolder extends RecyclerView.ViewHolder {
        private GifImageView gv_gifView;

        public EmoViewHolder(View itemView) {
            super(itemView);
            gv_gifView = (GifImageView) itemView.findViewById(R.id.msg_emo_iv);
        }
    }

    /**
     * 文字.
     */
    private class TxtViewHolder extends RecyclerView.ViewHolder {
        private TextView contentTV;

        public TxtViewHolder(View itemView) {
            super(itemView);
            contentTV = (TextView) itemView.findViewById(R.id.content_tv);
        }
    }

    /**
     * 图片.
     */
    private class ImageViewHolder extends RecyclerView.ViewHolder {

        private View clickView;
        private ImageView msg_image_iv;

        public ImageViewHolder(View itemView) {
            super(itemView);
            msg_image_iv = (ImageView) itemView.findViewById(R.id.msg_image_iv);
            clickView = itemView.findViewById(R.id.msg_image_iv_bg);
        }
    }

    /**
     * 地图图片.
     */
    private class MapIMGViewHolder extends RecyclerView.ViewHolder {

        protected TextView msg_map_tv;
        protected ImageView msg_map_iv;
        protected View clickView;

        public MapIMGViewHolder(View itemView) {
            super(itemView);
            msg_map_tv = (TextView) itemView.findViewById(R.id.msg_map_tv);
            msg_map_iv = (ImageView) itemView.findViewById(R.id.msg_map_iv);
            clickView = itemView.findViewById(R.id.msg_map_iv_bg);
        }

    }

    class GoogleMapViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_map;
        private ImageView iv_navigate;
        private TextView tv_location_address;

        public GoogleMapViewHolder(View itemView) {
            super(itemView);
            img_map = (ImageView) itemView.findViewById(R.id.img_map);
            iv_navigate = (ImageView) itemView.findViewById(R.id.iv_navigate);
            tv_location_address = (TextView) itemView.findViewById(R.id.tv_location_address);
        }
    }

    /**
     * 声音.
     */
    private class VoiceViewHolder extends RecyclerView.ViewHolder {
        private ImageView msg_voice_iv;
        private TextView msg_sender_time;
        private View viewBtn;
        private View msg_read_iv;

        public VoiceViewHolder(View itemView) {
            super(itemView);
            msg_voice_iv = (ImageView) itemView.findViewById(R.id.msg_voice_iv);
            msg_read_iv = itemView.findViewById(R.id.msg_read_iv);
            msg_sender_time = (TextView) itemView.findViewById(R.id.msg_sender_time);
            viewBtn = itemView.findViewById(R.id.msg_voice_iv_bg);
        }
    }

    /**
     * 文件.
     */
    private class FileViewHolder extends RecyclerView.ViewHolder {

        private ImageView msg_file_iv;
        private TextView msg_file_size;
        private TextView msg_file_name;
        private View btnView;

        public FileViewHolder(View itemView) {
            super(itemView);
            btnView = itemView.findViewById(R.id.msg_file_iv_bg);
            msg_file_iv = (ImageView) itemView.findViewById(R.id.msg_file_iv);
            msg_file_size = (TextView) itemView.findViewById(R.id.msg_file_size);
            msg_file_name = (TextView) itemView.findViewById(R.id.msg_file_name);
        }
    }

    /**
     * 视频.
     */
    private class VideoViewHolder extends RecyclerView.ViewHolder {

        private View video_item_btn;
        private ImageView item_video_sender_img, item_video_play_img;
        private TextView item_video_time_text;

        public VideoViewHolder(View itemView) {
            super(itemView);
            video_item_btn = itemView.findViewById(R.id.video_item_btn);
            item_video_sender_img = (ImageView) itemView.findViewById(R.id.item_video_sender_img);
            item_video_play_img = (ImageView) itemView.findViewById(R.id.item_video_play_img);
            item_video_time_text = (TextView) itemView.findViewById(R.id.item_video_time_text);
        }
    }

    private void hideFloat() {
        switch (HuxinSdkManager.instance().getFloatType()) {
            case HuxinService.MODEL_TYPE_FULL: {
                FloatViewUtil.instance().hideFloatView();
                FloatLogoUtil.instance().showFloat(mContext, HuxinService.MODEL_TYPE_FULL, false);
                break;
            }
            case HuxinService.MODEL_TYPE_Q: {
                FloatViewUtil.instance().hideFloatView();
                FloatLogoUtil.instance().showFloat(mContext, HuxinService.MODEL_TYPE_Q, false);
                break;
            }
            case HuxinService.MODEL_TYPE_HALF: {
                FloatViewUtil.instance().hideFloatView();
                break;
            }
        }
    }
}
