package com.youmai.hxsdk.keep;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.CropImageActivity;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.bean.HxUsers;
import com.youmai.hxsdk.db.helper.HxUsersHelper;
import com.youmai.hxsdk.dialog.HxKeepDialog;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.http.HttpConnector;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.im.IMHelper;
import com.youmai.hxsdk.im.cache.CacheMsgCall;
import com.youmai.hxsdk.im.cache.CacheMsgEmotion;
import com.youmai.hxsdk.im.cache.CacheMsgFile;
import com.youmai.hxsdk.im.cache.CacheMsgImage;
import com.youmai.hxsdk.im.cache.CacheMsgJoke;
import com.youmai.hxsdk.im.cache.CacheMsgMap;
import com.youmai.hxsdk.im.cache.CacheMsgRemark;
import com.youmai.hxsdk.im.cache.CacheMsgShow;
import com.youmai.hxsdk.im.cache.CacheMsgTxt;
import com.youmai.hxsdk.im.cache.CacheMsgVideo;
import com.youmai.hxsdk.im.cache.CacheMsgVoice;
import com.youmai.hxsdk.im.cache.ContactsDetailsBean;
import com.youmai.hxsdk.im.cache.JsonFormate;
import com.youmai.hxsdk.module.videoplayer.VideoPlayerActivity;
import com.youmai.hxsdk.module.videoplayer.bean.VideoDetailInfo;
import com.youmai.hxsdk.utils.AbDateUtil;
import com.youmai.hxsdk.utils.GlideCircleTransform;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.utils.TimeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_BIZCARD;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_CALL;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_EMOTION;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_FILE;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_IMG;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_JOKE;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_MAP;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_REMARK;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_SHOW;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_TXT;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_VIDEO;
import static com.youmai.hxsdk.db.bean.CacheMsgBean.MSG_TYPE_VOICE;


public class KeepAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum UI_TYPE {
        KEEP_TEXT, KEEP_IMAGE, KEEP_VIDEO, KEEP_VOICE, KEEP_LOCATION, KEEP_FILE, KEEP_CARD, DEFAULT
    }

    public static final String CACHE_MSG_BEAN = "cache_msg_bean";

    private Context mContext;
    private List<RespKeepBean> mList;

    public KeepAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public void setList(List<RespKeepBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void removeItem(String ids) {
        for (RespKeepBean item : mList) {
            if (item.getId().equals(ids)) {
                mList.remove(item);
                notifyDataSetChanged();
                if (ListUtils.isEmpty(mList) && mEmptyListener != null) {
                    mEmptyListener.onRefresh();
                }
                break;
            }
        }
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public int getItemViewType(int position) {  //给Item划分类别
        int res;
        int type = 0;
        if (mList.size() > 0) {
            type = mList.get(position).getMsgType();
        }
        switch (type) {   //根据Item类别不同，选择不同的Item布局
            case MSG_TYPE_TXT:
            case MSG_TYPE_JOKE:
                res = UI_TYPE.KEEP_TEXT.ordinal();
                break;
            case MSG_TYPE_IMG:
                res = UI_TYPE.KEEP_IMAGE.ordinal();
                break;
            case MSG_TYPE_VIDEO:
                res = UI_TYPE.KEEP_VIDEO.ordinal();
                break;
            case MSG_TYPE_VOICE:
                res = UI_TYPE.KEEP_VOICE.ordinal();
                break;
            case MSG_TYPE_MAP:
                res = UI_TYPE.KEEP_LOCATION.ordinal();
                break;
            case MSG_TYPE_FILE:
                res = UI_TYPE.KEEP_FILE.ordinal();
                break;
            case MSG_TYPE_BIZCARD:
                res = UI_TYPE.KEEP_CARD.ordinal();
                break;
            default:
                res = UI_TYPE.DEFAULT.ordinal();
                break;
        }

        return res;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        RecyclerView.ViewHolder holder = null;
        switch (UI_TYPE.values()[viewType]) {   //根据Item类别不同，选择不同的Item布局
            case KEEP_TEXT: {
                view = inflater.inflate(R.layout.hx_item_keep_text, parent, false);
                holder = new TextViewHolder(view);
            }
            break;
            case KEEP_IMAGE: {
                view = inflater.inflate(R.layout.hx_item_keep_picture, parent, false);
                holder = new ImageViewHolder(view);
            }
            break;
            case KEEP_VIDEO: {
                view = inflater.inflate(R.layout.hx_item_keep_video, parent, false);
                holder = new VideoViewHolder(view);
            }
            break;
            case KEEP_VOICE: {
                view = inflater.inflate(R.layout.hx_item_keep_voice, parent, false);
                holder = new VoiceViewHolder(view);
            }
            break;
            case KEEP_LOCATION: {
                view = inflater.inflate(R.layout.hx_item_keep_map, parent, false);
                holder = new MapIMGViewHolder(view);
            }
            break;
            case KEEP_FILE: {
                view = inflater.inflate(R.layout.hx_item_keep_file, parent, false);
                holder = new FileViewHolder(view);
            }
            break;
            case KEEP_CARD: {
                view = inflater.inflate(R.layout.hx_item_keep_card, parent, false);
                holder = new CardViewHolder(view);
            }
            break;
            case DEFAULT: {
                view = new View(mContext);
                holder = new RecyclerView.ViewHolder(view) {
                };
            }
            break;
        }
        return holder;
    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder == null) {
            return;
        }
        if (holder instanceof TextViewHolder) { //文字
            onBindText((TextViewHolder) holder, position);
        } else if (holder instanceof ImageViewHolder) { //图片
            onBindPic((ImageViewHolder) holder, position);
        } else if (holder instanceof VideoViewHolder) { //视频
            onBindVideo((VideoViewHolder) holder, position);
        } else if (holder instanceof VoiceViewHolder) { //语音
            onBindVoice((VoiceViewHolder) holder, position);
        } else if (holder instanceof MapIMGViewHolder) { //地图
            onBindMap((MapIMGViewHolder) holder, position);
        } else if (holder instanceof FileViewHolder) { //文件
            onBindFile((FileViewHolder) holder, position);
        } else if (holder instanceof CardViewHolder) { //名片
            onBindCard((CardViewHolder) holder, position);
        }

    }

    private void onBindText(final TextViewHolder holder, final int position) {
        RespKeepBean dataBean = mList.get(position);
        JsonFormate jsonBodyObj = getJsonBodyObj(dataBean.getMsgType(), dataBean.getMsgContent());

        String msgContent;
        if (jsonBodyObj instanceof CacheMsgJoke) {
            msgContent = ((CacheMsgJoke) jsonBodyObj).getMsgJoke().replace(CacheMsgJoke.JOKES, "");
        } else {
            msgContent = ((CacheMsgTxt) jsonBodyObj).getMsgTxt();
        }

        String collectPhone = dataBean.getSendPhone();
        final String id = dataBean.getId();
        final String finalMsgTxt = msgContent;

        final CacheMsgBean cacheMsgBean = new CacheMsgBean()
                .setSenderPhone(collectPhone)
                .setJsonBodyObj(jsonBodyObj)
                .setMsgType(dataBean.getMsgType());

        holder.tv_name.setText(HuxinSdkManager.instance().getContactName(collectPhone));
        holder.tv_date.setText(AbDateUtil.convertTimeMillis(dataBean.getMsgTime()));

        holder.tv_content.setText(finalMsgTxt);

        loadIcon(collectPhone, holder.iv_header);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                forwardOrDeleteItem(cacheMsgBean, id);
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, KeepTextActivity.class);
                intent.putExtra(KeepTextActivity.KEEP_ID, id);
                intent.putExtra(KeepTextActivity.KEEP_TEXT_CONTENT, finalMsgTxt);
                intent.putExtra(CACHE_MSG_BEAN, cacheMsgBean);

                if (mContext instanceof KeepActivity) {
                    KeepActivity act = (KeepActivity) mContext;
                    act.startForResult(intent, KeepActivity.DEL_CODE);
                }
            }
        });


    }

    private void onBindPic(ImageViewHolder holder, final int position) {
        RespKeepBean dataBean = mList.get(position);
        CacheMsgImage jsonBodyObj = (CacheMsgImage) getJsonBodyObj(dataBean.getMsgType(), dataBean.getMsgContent());

        final String id = dataBean.getId();
        final String msgFid = jsonBodyObj.getFid();

        final CacheMsgBean cacheMsgBean = new CacheMsgBean()
                .setSenderPhone(dataBean.getSendPhone())
                .setJsonBodyObj(jsonBodyObj)
                .setMsgType(dataBean.getMsgType());

        holder.tv_name.setText(HuxinSdkManager.instance().getContactName(dataBean.getSendPhone()));
        holder.tv_date.setText(AbDateUtil.convertTimeMillis(dataBean.getMsgTime()));

        loadIcon(dataBean.getSendPhone(), holder.iv_header);

        Glide.with(mContext).load(AppConfig.DOWNLOAD_IMAGE + jsonBodyObj.getFid()).into(holder.img_content);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                forwardOrDeleteItem(cacheMsgBean, id);
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CropImageActivity.class);
                intent.putExtra("isImageUrl", AppConfig.DOWNLOAD_IMAGE + msgFid);
                intent.putExtra(CACHE_MSG_BEAN, cacheMsgBean);
                mContext.startActivity(intent);
            }
        });

    }

    private void onBindVideo(VideoViewHolder holder, final int position) {
        RespKeepBean dataBean = mList.get(position);
        CacheMsgVideo jsonBodyObj = (CacheMsgVideo) getJsonBodyObj(dataBean.getMsgType(), dataBean.getMsgContent());

        final String id = dataBean.getId();
        String videoPath = jsonBodyObj.getVideoPath();
        if (StringUtils.isEmpty(videoPath) || !new File(videoPath).exists()) {
            videoPath = AppConfig.getImageUrl(mContext, jsonBodyObj.getVideoId());    //上传视频Url
        }

        final CacheMsgBean cacheMsgBean = new CacheMsgBean()
                .setSenderPhone(dataBean.getSendPhone())
                .setJsonBodyObj(jsonBodyObj)
                .setMsgType(dataBean.getMsgType());

        holder.tv_name.setText(HuxinSdkManager.instance().getContactName(dataBean.getSendPhone()));
        holder.tv_date.setText(AbDateUtil.convertTimeMillis(dataBean.getMsgTime()));

        loadIcon(dataBean.getSendPhone(), holder.iv_header);

        Glide.with(mContext).load(AppConfig.DOWNLOAD_IMAGE + jsonBodyObj.getFrameId()).into(holder.iv_video_cover);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                forwardOrDeleteItem(cacheMsgBean, id);
                return false;
            }
        });

        final String finalVideoPath = videoPath;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoDetailInfo info = new VideoDetailInfo();
                info.setVideoPath(finalVideoPath); //视频路径
                Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                intent.putExtra("info", info);
                mContext.startActivity(intent);
            }
        });

    }

    private void onBindVoice(VoiceViewHolder holder, int position) {
        RespKeepBean msg = mList.get(position);
        JsonFormate formate = getJsonBodyObj(msg.getMsgType(), msg.getMsgContent());

        if (formate instanceof CacheMsgVoice) {
            CacheMsgVoice msgVoice = (CacheMsgVoice) formate;

            final String ids = msg.getId();

            String time = msg.getCreateTime();
            final String fid = msgVoice.getFid();
            final String voiceTime = msgVoice.getVoiceTime();
            final String path = msgVoice.getVoicePath();
            final String sendPhone = msg.getSendPhone();

            final CacheMsgBean cacheMsgBean = new CacheMsgBean()
                    .setSenderPhone(msg.getSendPhone())
                    .setJsonBodyObj(msgVoice)
                    .setMsgType(msg.getMsgType());

            holder.tv_name.setText(HuxinSdkManager.instance().getContactName(sendPhone));
            holder.tv_date.setText(AbDateUtil.convertTimeMillis(msg.getMsgTime()));

            try {
                long mil = (long) (Float.parseFloat(voiceTime) * 1000);
                String voiceStr = TimeUtils.getTimeSeconds(mContext, mil);
                holder.tv_play_time.setText(voiceStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            CacheMsgVoice cacheMsgVoice = (CacheMsgVoice) cacheMsgBean.getJsonBodyObj();
            if (cacheMsgVoice.getForwardCount() > 0) {
                holder.tv_voice_type.setText("转发的语音");
            } else {
                holder.tv_voice_type.setText("语音");
            }

            loadIcon(msg.getSendPhone(), holder.iv_header);

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    forwardOrDeleteItem(cacheMsgBean, ids);
                    return false;
                }
            });

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, KeepVoiceActivity.class);
                    intent.putExtra(KeepVoiceActivity.KEEP_ID, ids);
                    intent.putExtra(KeepVoiceActivity.FID, fid);
                    intent.putExtra(KeepVoiceActivity.VOICE_TIME, voiceTime);
                    intent.putExtra(KeepVoiceActivity.PATH, path);
                    intent.putExtra(KeepVoiceActivity.SEND_PHONE, sendPhone);
                    intent.putExtra(CACHE_MSG_BEAN, cacheMsgBean);

                    if (mContext instanceof KeepActivity) {
                        KeepActivity act = (KeepActivity) mContext;
                        act.startForResult(intent, KeepActivity.DEL_CODE);
                    }
                }
            });
        }

    }

    private void onBindMap(MapIMGViewHolder holder, int position) {
        RespKeepBean msg = mList.get(position);

        JsonFormate formate = getJsonBodyObj(msg.getMsgType(), msg.getMsgContent());

        if (formate instanceof CacheMsgMap) {
            CacheMsgMap msgMap = (CacheMsgMap) formate;

            final String ids = msg.getId();

            String sendPhone = msg.getSendPhone();
            String time = msg.getCreateTime();
            final String address = msgMap.getAddress();
            String imgUrl = msgMap.getImgUrl();
            final String location = msgMap.getLocation();

            final CacheMsgBean cacheMsgBean = new CacheMsgBean()
                    .setSenderPhone(msg.getSendPhone())
                    .setJsonBodyObj(msgMap)
                    .setMsgType(msg.getMsgType());

            holder.tv_name.setText(HuxinSdkManager.instance().getContactName(sendPhone));
            holder.tv_date.setText(AbDateUtil.convertTimeMillis(msg.getMsgTime()));
            holder.tv_address.setText(address);

            loadIcon(msg.getSendPhone(), holder.iv_header);

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    forwardOrDeleteItem(cacheMsgBean, ids);
                    return false;
                }
            });

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, KeepMapActivity.class);
                    intent.putExtra(KeepMapActivity.KEEP_ID, ids);
                    intent.putExtra("location", location);
                    intent.putExtra("labelAddress", address);
                    intent.putExtra(CACHE_MSG_BEAN, cacheMsgBean);

                    if (mContext instanceof KeepActivity) {
                        KeepActivity act = (KeepActivity) mContext;
                        act.startForResult(intent, KeepActivity.DEL_CODE);
                    }

                }
            });

        }
    }

    private void onBindFile(FileViewHolder holder, final int position) {
        RespKeepBean dataBean = mList.get(position);
        CacheMsgFile jsonBodyObj = (CacheMsgFile) getJsonBodyObj(dataBean.getMsgType(), dataBean.getMsgContent());

        final String id = dataBean.getId();

        final CacheMsgBean cacheMsgBean = new CacheMsgBean()
                .setSenderPhone(dataBean.getSendPhone())
                .setMsgType(dataBean.getMsgType())
                .setJsonBodyObj(jsonBodyObj);


        holder.tv_name.setText(HuxinSdkManager.instance().getContactName(dataBean.getSendPhone()));
        holder.tv_date.setText(AbDateUtil.convertTimeMillis(dataBean.getMsgTime()));

        holder.tv_file_name.setText(jsonBodyObj.getFileName());
        holder.tv_file_size.setText(IMHelper.convertFileSize(jsonBodyObj.getFileSize()));

        loadIcon(dataBean.getSendPhone(), holder.iv_header);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                forwardOrDeleteItem(cacheMsgBean, id);
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, KeepFileActivity.class);
                intent.putExtra(KeepFileActivity.IM_FILE_BEAN, cacheMsgBean);
                intent.putExtra(KeepFileActivity.KEEP_ID, id);
                KeepActivity act = (KeepActivity) mContext;
                act.startActivityForResult(intent, KeepActivity.DEL_CODE);
            }
        });

    }

    private void onBindCard(CardViewHolder holder, int position) {

        RespKeepBean msg = mList.get(position);

        JsonFormate formate = getJsonBodyObj(msg.getMsgType(), msg.getMsgContent());

        if (formate instanceof ContactsDetailsBean) {
            final ContactsDetailsBean msgCard = (ContactsDetailsBean) formate;

            final String ids = msg.getId();

            String sendPhone = msg.getSendPhone();
            String time = msg.getCreateTime();

            String name = msgCard.getName();
            String job = msgCard.getJob();
            String com = msgCard.getCompany();

            String phone = "";
            List<ContactsDetailsBean.Phone> phones = msgCard.getPhone();
            if (!ListUtils.isEmpty(phones)) {
                phone = phones.get(0).getPhone();
            }

            if (TextUtils.isEmpty(name)) {
                name = HuxinSdkManager.instance().getContactName(sendPhone);
            }


            String address = msgCard.getCard().getData2();
            final CacheMsgBean cacheMsgBean = new CacheMsgBean()
                    .setSenderPhone(msg.getSendPhone())
                    .setJsonBodyObj(msgCard)
                    .setMsgType(msg.getMsgType());

            loadIcon(msg.getSendPhone(), holder.iv_header);

            holder.tv_name.setText(HuxinSdkManager.instance().getContactName(sendPhone));
            holder.tv_date.setText(AbDateUtil.convertTimeMillis(msg.getMsgTime()));
            //holder.item_card_head_img
            holder.item_card_nickname_text.setText(name);
            holder.item_card_job_text.setText(job);
            holder.item_card_company_text.setText(com);
            holder.item_card_phone_text.setText(phone);
            holder.item_card_address_text.setText(address);

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    forwardOrDeleteItem(cacheMsgBean, ids);
                    return false;
                }
            });

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    /**
     * 文本
     */
    private class TextViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_header;
        TextView tv_name;
        TextView tv_date;

        TextView tv_content;

        private TextViewHolder(View itemView) {
            super(itemView);
            tv_date = (TextView) itemView.findViewById(R.id.tv_time);
            tv_name = (TextView) itemView.findViewById(R.id.tv_phone);
            iv_header = (ImageView) itemView.findViewById(R.id.iv_header);

            tv_content = (TextView) itemView.findViewById(R.id.tv_text);
        }
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_header;
        TextView tv_name;
        TextView tv_date;

        ImageView img_content;

        private ImageViewHolder(View itemView) {
            super(itemView);
            tv_date = (TextView) itemView.findViewById(R.id.tv_time);
            tv_name = (TextView) itemView.findViewById(R.id.tv_phone);
            iv_header = (ImageView) itemView.findViewById(R.id.iv_header);

            img_content = (ImageView) itemView.findViewById(R.id.img_content);
        }
    }

    private class VideoViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_header;
        TextView tv_name;
        TextView tv_date;

        ImageView iv_video_cover;
        ImageView img_play;

        private VideoViewHolder(View itemView) {
            super(itemView);
            tv_date = (TextView) itemView.findViewById(R.id.tv_time);
            tv_name = (TextView) itemView.findViewById(R.id.tv_phone);
            iv_header = (ImageView) itemView.findViewById(R.id.iv_header);

            iv_video_cover = (ImageView) itemView.findViewById(R.id.iv_video_cover);
            img_play = (ImageView) itemView.findViewById(R.id.img_play);
        }
    }


    /**
     * 地图图片.
     */
    private class MapIMGViewHolder extends RecyclerView.ViewHolder {

        View view;

        ImageView iv_header;
        TextView tv_name;
        TextView tv_date;

        TextView tv_address;


        public MapIMGViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            tv_date = (TextView) itemView.findViewById(R.id.tv_time);
            tv_name = (TextView) itemView.findViewById(R.id.tv_phone);
            iv_header = (ImageView) itemView.findViewById(R.id.iv_header);

            tv_address = (TextView) itemView.findViewById(R.id.tv_address);
        }

    }

    /**
     * 声音.
     */
    private class VoiceViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView iv_header;
        TextView tv_name;
        TextView tv_date;

        TextView tv_voice_type;
        TextView tv_play_time;

        public VoiceViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_date = (TextView) itemView.findViewById(R.id.tv_time);
            tv_name = (TextView) itemView.findViewById(R.id.tv_phone);
            iv_header = (ImageView) itemView.findViewById(R.id.iv_header);

            tv_voice_type = (TextView) itemView.findViewById(R.id.tv_name);
            tv_play_time = (TextView) itemView.findViewById(R.id.tv_play_time);
        }
    }

    /**
     * 文件.
     */
    private class FileViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_header;
        TextView tv_name;
        TextView tv_date;

        TextView tv_file_name;
        TextView tv_file_size;

        public FileViewHolder(View itemView) {
            super(itemView);
            tv_date = (TextView) itemView.findViewById(R.id.tv_time);
            tv_name = (TextView) itemView.findViewById(R.id.tv_phone);
            iv_header = (ImageView) itemView.findViewById(R.id.iv_header);

            tv_file_name = (TextView) itemView.findViewById(R.id.tv_file_name);
            tv_file_size = (TextView) itemView.findViewById(R.id.tv_file_size);
        }
    }


    /**
     * 名片.
     */
    private class CardViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView iv_header;
        TextView tv_name;
        TextView tv_date;

        ImageView item_card_head_img;
        TextView item_card_nickname_text;
        TextView item_card_job_text;
        TextView item_card_company_text;
        TextView item_card_phone_text;
        TextView item_card_address_text;


        public CardViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            tv_date = (TextView) itemView.findViewById(R.id.tv_time);
            iv_header = (ImageView) itemView.findViewById(R.id.iv_header);
            tv_name = (TextView) itemView.findViewById(R.id.tv_phone);

            item_card_head_img = (ImageView) itemView.findViewById(R.id.item_card_head_img);

            item_card_nickname_text = (TextView) itemView.findViewById(R.id.item_card_nickname_text);
            item_card_job_text = (TextView) itemView.findViewById(R.id.item_card_job_text);
            item_card_company_text = (TextView) itemView.findViewById(R.id.item_card_company_text);
            item_card_phone_text = (TextView) itemView.findViewById(R.id.item_card_phone_text);
            item_card_address_text = (TextView) itemView.findViewById(R.id.item_card_address_text);
        }
    }

    private JsonFormate getJsonBodyObj(int msgType, String contentJsonBody) {
        JsonFormate jsonBodyObj = null;
        switch (msgType) {
            case MSG_TYPE_TXT:
                jsonBodyObj = new CacheMsgTxt().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_JOKE:
                jsonBodyObj = new CacheMsgJoke().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_IMG:
                jsonBodyObj = new CacheMsgImage().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_MAP:
                jsonBodyObj = new CacheMsgMap().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_VOICE:
                jsonBodyObj = new CacheMsgVoice().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_EMOTION:
                jsonBodyObj = new CacheMsgEmotion().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_FILE:
                jsonBodyObj = new CacheMsgFile().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_SHOW:
                jsonBodyObj = new CacheMsgShow().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_CALL:
                jsonBodyObj = new CacheMsgCall().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_BIZCARD:
                jsonBodyObj = new ContactsDetailsBean().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_REMARK:
                jsonBodyObj = new CacheMsgRemark().fromJson(contentJsonBody);
                break;
            case MSG_TYPE_VIDEO:
                jsonBodyObj = new CacheMsgVideo().fromJson(contentJsonBody);
                break;
        }
        return jsonBodyObj;
    }


    private void forwardOrDeleteItem(final CacheMsgBean cacheMsgBean, final String id) {
        HxKeepDialog hxDialog = new HxKeepDialog(mContext);
        HxKeepDialog.HxCallback callback = new HxKeepDialog.HxCallback() {
            @Override
            public void onForward() {
                Intent intent = new Intent();
                intent.setAction("com.youmai.huxin.recent");
                intent.putExtra("type", "forward_msg");
                intent.putExtra("data", cacheMsgBean);
                mContext.startActivity(intent);
            }

            @Override
            public void onDelete() {
                ReqKeepDel reqKeepDel = new ReqKeepDel(mContext);
                reqKeepDel.setIds(id);
                HttpConnector.httpPost(AppConfig.COLLECT_DEL, reqKeepDel.getParams(), new IPostListener() {
                    @Override
                    public void httpReqResult(String response) {
                        RespBaseBean baseBean = GsonUtil.parse(response, RespBaseBean.class);
                        if (baseBean != null && baseBean.isSuccess()) {
                            Toast.makeText(mContext, R.string.collect_del_success, Toast.LENGTH_SHORT).show();
                            removeItem(id);
                        } else {
                            Toast.makeText(mContext, R.string.collect_del_fail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        hxDialog.setHxCollectDialog(callback);
        hxDialog.show();
    }

    private void loadIcon(String collectPhone, final ImageView imageView) {
        HxUsers hxUsers = HxUsersHelper.instance().getHxUser(mContext, collectPhone);
        if (hxUsers != null && hxUsers.getIconUrl() != null) {
            Glide.with(mContext).load(hxUsers.getIconUrl())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).placeholder(R.drawable.hx_index_head01).circleCrop())
                    .into(imageView);
        } else {
            HxUsersHelper.instance().updateSingleUser(mContext, collectPhone, new HxUsersHelper.IOnUpCompleteListener() {
                @Override
                public void onSuccess(HxUsers users) {
                    if (users != null && users.getIconUrl() != null) {
                        Glide.with(mContext).load(users.getIconUrl())
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).placeholder(R.drawable.hx_index_head01).circleCrop())
                                .into(imageView);
                    }
                }

                @Override
                public void onFail() {
                }
            });
        }
    }

    private IEmptyCollect mEmptyListener;

    public interface IEmptyCollect {
        void onRefresh();
    }

    public void setOnEmptyCollectListener(IEmptyCollect listener) {
        mEmptyListener = listener;
    }

}

