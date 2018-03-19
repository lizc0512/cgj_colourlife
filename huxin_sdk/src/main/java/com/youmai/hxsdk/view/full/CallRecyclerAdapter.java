package com.youmai.hxsdk.view.full;

import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.CropImageActivity;
import com.youmai.hxsdk.activity.CropMapActivity;
import com.youmai.hxsdk.activity.CropVideoActivity;
import com.youmai.hxsdk.activity.WebViewActivity;
import com.youmai.hxsdk.db.bean.ChatMsg;
import com.youmai.hxsdk.chat.ContentLocation;
import com.youmai.hxsdk.chat.ContentText;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.entity.EmoInfo;
import com.youmai.hxsdk.db.bean.ShowData;
import com.youmai.hxsdk.interfaces.IFileReceiveListener;
import com.youmai.hxsdk.interfaces.bean.FileBean;
import com.youmai.hxsdk.interfaces.impl.FileReceiveListenerImpl;
import com.youmai.hxsdk.module.videoplayer.utils.NetworkUtils;
import com.youmai.hxsdk.service.HuxinService;
import com.youmai.hxsdk.sp.SPDataUtil;
import com.youmai.hxsdk.utils.AbFileUtil;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.CommonUtils;
import com.youmai.hxsdk.utils.FloatLogoUtil;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.utils.ViewAnimUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * 作者：create by YW
 * 日期：2016.08.10 09:40
 * 描述：
 */
public class CallRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements TextureView.SurfaceTextureListener {

    public enum UI_TYPE {
        TEXT_NORMAL, TEXT_GIF, IMAGE_NORMAL, LOCATION, UI_TYPE, SHOW_PICTURE, SHOW_VIDEO
    }

    private Context mContext;
    private String mDstPhone;
    private List<ChatMsg> mList;

    public CallRecyclerAdapter(Context context, String dstPhone) {
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

        if (msg.getMsgType() == ChatMsg.MsgType.TEXT) {// 普通文本 && GIF图

            type = UI_TYPE.TEXT_GIF.ordinal();

        } else if (msg.getMsgType() == ChatMsg.MsgType.PICTURE) {// 图片

            type = UI_TYPE.IMAGE_NORMAL.ordinal();

        } else if (msg.getMsgType() == ChatMsg.MsgType.LOCATION) {// 位置

            type = UI_TYPE.LOCATION.ordinal();

        } else if (msg.getMsgType() == ChatMsg.MsgType.SHOW_PICTURE) {//图片show

            type = UI_TYPE.SHOW_PICTURE.ordinal();

        } else if (msg.getMsgType() == ChatMsg.MsgType.SHOW_VIDEO) {// 视频show

            type = UI_TYPE.SHOW_VIDEO.ordinal();

        }
        return type;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = null;
        RecyclerView.ViewHolder holder = null;

        switch (UI_TYPE.values()[viewType]) {   //根据Item类别不同，选择不同的Item布局
            case TEXT_GIF: {
                view = inflater.inflate(R.layout.hx_call_msg_gif_layout, parent, false);
                holder = new GIFViewHolder(view);
            }
            break;
            case IMAGE_NORMAL: {
                view = inflater.inflate(R.layout.hx_call_msg_image_layout, parent, false);
                holder = new ImageViewHolder(view);
            }
            break;
            case LOCATION: {
                if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_HALF) {
                    view = inflater.inflate(R.layout.hx_call_msg_local_pic_layout, parent, false);
                    holder = new MapIMGViewHolder(view);
                } else {
                    view = inflater.inflate(R.layout.hx_call_msg_location_layout, parent, false);
                    holder = new MapViewHolder(view);
                }

            }
            break;
            case SHOW_PICTURE: {
                view = inflater.inflate(R.layout.hx_call_msg_image_layout, parent, false);
                holder = new ShowImageHolder(view);
            }
            break;
            case SHOW_VIDEO: {
                view = inflater.inflate(R.layout.hx_call_video_show, parent, false);
                holder = new VideoViewHolder(view);
            }
            break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        IFileReceiveListener listener = FileReceiveListenerImpl.getReceiveListener();

        final ChatMsg item = mList.get(position);

        if (viewHolder instanceof GIFViewHolder) {
            ContentText test = item.getMsgContent().getText();
            String con = test.getContent();
            List<EmoInfo.EmoItem> dataList = new EmoInfo(mContext).getEmoList();
            for (EmoInfo.EmoItem emoItem : dataList) {
                if (emoItem.getEmoStr().equals(con)) {
                    ((GIFViewHolder) viewHolder).gv_gifView.setImageResource(emoItem.getEmoRes());
                    HuxinSdkManager.instance().playSound(emoItem.getSoundId());
                    if (null != mList && mList.size() > 0 && position == mList.size() - 1) {
                        FileBean fileBean = new FileBean().setUserId(item.getSrcUsrId())
                                .setDstPhone(item.getTargetPhone())
                                .setTextContent(con);
                        listener.onImSuccess(item.getMsgType().ordinal(), fileBean);
                    }
                    break;
                }
            }
        } else if (viewHolder instanceof ImageViewHolder) {
            ImageView img = ((ImageViewHolder) viewHolder).iv_msg_view;
            String fid = item.getMsgContent().getPicture().getPicUrl();
            if (StringUtils.isEmpty(fid)) {
                return;
            }
            String url = AppConfig.DOWNLOAD_IMAGE + fid;

            Glide.with(mContext)
                    .load(url)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(img);

            if (null != mList && mList.size() > 0 && position == mList.size() - 1) {
                FileBean fileBean = new FileBean().setUserId(item.getSrcUsrId())
                        .setDstPhone(item.getTargetPhone())
                        .setOriginPath(url);
                listener.onImSuccess(item.getMsgType().ordinal(), fileBean);
            }

            final String finalUrl = url;

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, CropImageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("isImageUrl", finalUrl);
                    mContext.startActivity(intent);
                    hideFloat();
                }
            });

        } else if (viewHolder instanceof MapViewHolder) {
            LogUtils.e(Constant.SDK_UI_TAG, "地图位置");

            ContentLocation mLocation = item.getMsgContent().getLocation();
            String location = mLocation.getLongitudeStr() + "," + mLocation.getLatitudeStr();
            String mLabelAddress = mLocation.getLabelStr();
            final LatLng latLng = new LatLng(
                    Double.parseDouble(mLocation.getLatitudeStr()),
                    Double.parseDouble(mLocation.getLongitudeStr()));

            MapView mMapView = ((MapViewHolder) viewHolder).fm_msg_map;
            final MapViewUtil mMapViewUtil = new MapViewUtil(mContext, mMapView);
            mMapViewUtil.onCreate(null);
            mMapViewUtil.setLocation(location);//标志物

            if (!StringUtils.isEmpty(mLabelAddress)
                    && HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_Q) {
                try {
                    ((MapViewHolder) viewHolder).tv_location_address.setVisibility(View.VISIBLE);
                    ((MapViewHolder) viewHolder).tv_location_address.setText(mLabelAddress.split(":")[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                    ((MapViewHolder) viewHolder).tv_location_address.setText(mLabelAddress);
                }
            }

            final String url = "http://restapi.amap.com/v3/staticmap?location="
                    + mLocation.getLongitudeStr() + "," + mLocation.getLatitudeStr() + "&zoom=" + 17
                    + "&scale=2&size=720*550&traffic=1&markers=mid,0xff0000,A:" + mLocation.getLongitudeStr()
                    + "," + mLocation.getLatitudeStr() + "&key=" + AppConfig.staticMapKey;

            if (null != mList && mList.size() > 0 && position == mList.size() - 1) {
                FileBean fileBean = new FileBean().setUserId(item.getSrcUsrId())
                        .setDstPhone(item.getTargetPhone())
                        .setLongitude(Double.valueOf(mLocation.getLongitudeStr()))
                        .setLatitude(Double.valueOf(mLocation.getLatitudeStr()))
                        .setAddress(mLabelAddress)
                        .setMapUrl(url);
                listener.onImSuccess(item.getMsgType().ordinal(), fileBean);
            }

            ((MapViewHolder) viewHolder).iv_navigate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//导航
                    mMapViewUtil.toDaoHang(latLng);
                    hideFloat();
                }
            });

        } else if (viewHolder instanceof MapIMGViewHolder) {
            ContentLocation mLocation = item.getMsgContent().getLocation();
            final String location = mLocation.getLongitudeStr() + "," + mLocation.getLatitudeStr();
            final String mLabelAddress = mLocation.getLabelStr();
            final LatLng latLng = new LatLng(
                    Double.parseDouble(mLocation.getLatitudeStr()),
                    Double.parseDouble(mLocation.getLongitudeStr()));

            LogUtils.e(Constant.SDK_UI_TAG, "location pic = " + location);

            if (!StringUtils.isEmpty(mLabelAddress)) {

                try {
                    if (mLabelAddress.contains(":")) {
                        ((MapIMGViewHolder) viewHolder).tv_location_address.setVisibility(View.VISIBLE);
                        ((MapIMGViewHolder) viewHolder).tv_location_address.setText(mLabelAddress.split(":")[0]);
                    } else {
                        ((MapIMGViewHolder) viewHolder).tv_location_address.setVisibility(View.VISIBLE);
                        ((MapIMGViewHolder) viewHolder).tv_location_address.setText(mLabelAddress);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ((MapIMGViewHolder) viewHolder).tv_location_address.setVisibility(View.VISIBLE);
                    ((MapIMGViewHolder) viewHolder).tv_location_address.setText(mLabelAddress);
                }
            }


            final MapViewUtil mMapViewUtil = new MapViewUtil(mContext, null);
            mMapViewUtil.setLocation(location);//标志物

            ((MapIMGViewHolder) viewHolder).iv_navigate.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {//导航
                    mMapViewUtil.toDaoHang(latLng);
                }
            });

            if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_HALF) {
                final String url = "http://restapi.amap.com/v3/staticmap?location="
                        + mLocation.getLongitudeStr() + "," + mLocation.getLatitudeStr() + "&zoom=" + 17
                        + "&scale=2&size=720*550&traffic=1&markers=mid,0xff0000,A:" + mLocation.getLongitudeStr()
                        + "," + mLocation.getLatitudeStr() + "&key=" + AppConfig.staticMapKey;
                try {
                    Glide.with(mContext)
                            .load(url)
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .centerCrop()
                                    .placeholder(R.drawable.hx_show_default_full)  //占位图片
                                    .error(R.drawable.hx_show_default_full))        //下载失败)
                            .into(((MapIMGViewHolder) viewHolder).iv_msg_map);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ((MapIMGViewHolder) viewHolder).iv_msg_map.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, CropMapActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("location", location);
                        intent.putExtra("labelAddress", mLabelAddress);
                        mContext.startActivity(intent);
                        hideFloat();
                    }
                });
            }
        } else if (viewHolder instanceof GoogleMapViewHolder) {
            LogUtils.e(Constant.SDK_UI_TAG, "google map");
            final ContentLocation location = item.getMsgContent().getLocation();

            final String labelAddress = location.getLabelStr();
            if (!StringUtils.isEmpty(labelAddress)) {
                try {
                    if (labelAddress.contains(":")) {
                        ((GoogleMapViewHolder) viewHolder).tv_location_address.setVisibility(View.VISIBLE);
                        ((GoogleMapViewHolder) viewHolder).tv_location_address.setText(labelAddress.split(":")[0]);
                    } else {
                        ((GoogleMapViewHolder) viewHolder).tv_location_address.setVisibility(View.VISIBLE);
                        ((GoogleMapViewHolder) viewHolder).tv_location_address.setText(labelAddress);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ((GoogleMapViewHolder) viewHolder).tv_location_address.setVisibility(View.VISIBLE);
                    ((GoogleMapViewHolder) viewHolder).tv_location_address.setText(labelAddress);
                }
            }

            ((GoogleMapViewHolder) viewHolder).iv_navigate.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {//导航
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);

                    // 从当前地点导航到location
                    Uri uri = Uri.parse("http://maps.google.com/maps?daddr=" + location.getLongitudeStr()
                            + "," + location.getLatitudeStr());
                    intent.setData(uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });

            final String url = "https://maps.googleapis.com/maps/api/staticmap?center="
                    + location.getLongitudeStr() + "," + location.getLatitudeStr() + "&zoom=" + 17
                    + "&scale=2&size=720*550&markers=color:blue%7Clabel:S%7C" + location.getLongitudeStr()
                    + "," + location.getLatitudeStr() + "&key=" + AppConfig.googleMapKey;

            try {
                Glide.with(mContext)
                        .load(url)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop())
                        .into(((GoogleMapViewHolder) viewHolder).img_map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (viewHolder instanceof ShowImageHolder) {
            try {
                ImageView imgShow = ((ShowImageHolder) viewHolder).img_show;
                ImageView imgWgw = ((ShowImageHolder) viewHolder).img_wgw;
                RelativeLayout rl_full_wgw = ((ShowImageHolder) viewHolder).tv_full_wgw;

                ViewAnimUtils.scaleAnim(rl_full_wgw, 500);

                final ShowData showInfo = item.getNewShowData();
                if (showInfo == null) {
                    if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_HALF) {
                        imgShow.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imgShow.setImageResource(R.drawable.hx_half_pic_moren);
                    } else {
                        if (!SPDataUtil.getFirstShowFid(mContext).equals("-1")
                                && mDstPhone.equals(HuxinSdkManager.instance().getPhoneNum())) { //TODO: 第一次引导
                            final String url = AppConfig.getImageUrl(mContext, SPDataUtil.getFirstShowFid(mContext));
                            Glide.with(mContext)
                                    .load(url)
                                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                            .placeholder(R.drawable.hx_half_pic_moren)
                                            .error(R.drawable.hx_half_pic_moren))
                                    .into(imgShow);
                            SPDataUtil.setFirstShowFid(mContext, "-1");
                        } else { //默认的
                            imgShow.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            imgShow.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
                            imgShow.setImageResource(R.drawable.hx_show_default_full);
                        }
                    }
                } else {
                    final String url = AppConfig.getImageUrl(mContext, showInfo.getFid());
                    if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_HALF) {
                        Glide.with(mContext)
                                .load(url)
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                        .placeholder(R.drawable.hx_half_pic_moren)
                                        .error(R.drawable.hx_half_pic_moren))
                                .into(imgShow);
                    } else {
                        Glide.with(mContext)
                                .load(url)
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                        .placeholder(R.drawable.hx_show_default_full)
                                        .error(R.drawable.hx_show_default_full))
                                .into(imgShow);
                    }

                    if (!StringUtils.isEmpty(url) && HuxinSdkManager.instance().getFloatType()
                            == HuxinService.MODEL_TYPE_HALF) { // 半屏点击看大图
                        imgShow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setClass(mContext, CropImageActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("isImageUrl", url);
                                mContext.startActivity(intent);
                            }
                        });
                    }

                    //TODO：根据秀类型判断执行 type：1->个人 2->商家 3->代言 4->广告
                    if (showInfo.getType().equals("1")) {// 个人秀
                        imgWgw.setVisibility(View.GONE);
                        rl_full_wgw.setVisibility(View.GONE);
                    } else if (showInfo.getType().equals("2") || showInfo.getType().equals("3")
                            || showInfo.getType().equals("4")) {// 商家秀 -- 广告
                        String detailUrl = showInfo.getDetailurl();
                        if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_HALF) {//半屏
                            if (StringUtils.isEmpty(detailUrl)) {  //无商家外链
                                imgWgw.setVisibility(View.GONE);
                            } else {//有商家外链
                                imgWgw.setVisibility(View.VISIBLE);
                                imgWgw.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //外链 H5
                                        Intent intent = new Intent();
                                        intent.putExtra(WebViewActivity.INTENT_TITLE, showInfo.getName());
                                        intent.putExtra(WebViewActivity.INTENT_URL, showInfo.getDetailurl());
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.setClass(mContext, WebViewActivity.class);
                                        mContext.startActivity(intent);
                                    }
                                });
                            }
                        } else if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_FULL ||
                                HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_Q) { //全屏
                            if (!StringUtils.isEmpty(detailUrl)) { //外链
                                rl_full_wgw.setVisibility(View.VISIBLE);
                                rl_full_wgw.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //外链 H5
                                        Intent intent = new Intent();
                                        intent.putExtra(WebViewActivity.INTENT_TITLE, showInfo.getName());
                                        intent.putExtra(WebViewActivity.INTENT_URL, showInfo.getDetailurl());
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.setClass(mContext, WebViewActivity.class);
                                        mContext.startActivity(intent);
                                    }
                                });
                            } else {
                                rl_full_wgw.setVisibility(View.GONE);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (viewHolder instanceof VideoViewHolder) {
            try {
                mVideoView = ((VideoViewHolder) viewHolder).videoView;
                iv_half_replay = ((VideoViewHolder) viewHolder).iv_half_replay;
                iv_half_replay.setVisibility(View.GONE);
                iv_shade_bg = ((VideoViewHolder) viewHolder).iv_shade_bg;
                tv_video_show_tip = ((VideoViewHolder) viewHolder).tv_video_show_tip;

                String url = AppConfig.getImageUrl(mContext, item.getNewShowData().getFid());
                //http://192.168.0.42:8088/file/download/user/show?uphone=15016732932&fid=282601
                //TODO: 2016/8/24
                loadVideo(item.getNewShowData());

                if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_HALF) {//半屏看全屏视频
                    final String finalUrl = url;
                    ((VideoViewHolder) viewHolder).rl_half_crop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(mContext, CropVideoActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("isVideoUrl", finalUrl);
                            mContext.startActivity(intent);
                            hideFloat();
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ShowImageHolder extends RecyclerView.ViewHolder {
        private ImageView img_show;
        private ImageView img_wgw;
        private RelativeLayout tv_full_wgw;

        public ShowImageHolder(View itemView) {
            super(itemView);
            img_show = (ImageView) itemView.findViewById(R.id.img_show);
            img_wgw = (ImageView) itemView.findViewById(R.id.img_wgw);
            tv_full_wgw = (RelativeLayout) itemView.findViewById(R.id.rl_full_h5_detail);
        }
    }


    class VideoViewHolder extends RecyclerView.ViewHolder {
        private TextureView videoView;
        private RelativeLayout rl_half_crop;
        private ImageView iv_half_replay, iv_shade_bg;
        private TextView tv_video_show_tip;

        public VideoViewHolder(View itemView) {
            super(itemView);
            videoView = (TextureView) itemView.findViewById(R.id.vv_show);
            rl_half_crop = (RelativeLayout) itemView.findViewById(R.id.rl_half_crop);
            iv_half_replay = (ImageView) itemView.findViewById(R.id.iv_half_replay);
            iv_shade_bg = (ImageView) itemView.findViewById(R.id.iv_shade_bg);
            tv_video_show_tip = (TextView) itemView.findViewById(R.id.tv_video_show_tip);
        }
    }

    class GIFViewHolder extends RecyclerView.ViewHolder {
        private GifImageView gv_gifView;

        public GIFViewHolder(View itemView) {
            super(itemView);
            gv_gifView = (GifImageView) itemView.findViewById(R.id.gv_gif_view);
        }
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_msg_view;

        public ImageViewHolder(View itemView) {
            super(itemView);
            iv_msg_view = (ImageView) itemView.findViewById(R.id.img_show);
        }
    }

    class MapViewHolder extends RecyclerView.ViewHolder {
        private MapView fm_msg_map;
        private ImageView iv_navigate;
        private TextView tv_location_address;

        public MapViewHolder(View itemView) {
            super(itemView);
            fm_msg_map = (MapView) itemView.findViewById(R.id.fm_msg_map);
            iv_navigate = (ImageView) itemView.findViewById(R.id.iv_navigate);
            tv_location_address = (TextView) itemView.findViewById(R.id.tv_location_address);
        }
    }

    class MapIMGViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_msg_map;
        private ImageView iv_navigate;
        private TextView tv_location_address;

        public MapIMGViewHolder(View itemView) {
            super(itemView);
            iv_msg_map = (ImageView) itemView.findViewById(R.id.iv_msg_map);
            iv_navigate = (ImageView) itemView.findViewById(R.id.iv_navigate);
            tv_location_address = (TextView) itemView.findViewById(R.id.tv_location_address);
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

    /* 视频秀 start by 2016.8.12 */
    private TextureView mVideoView;
    private ImageView iv_half_replay;
    private ImageView iv_shade_bg;
    private TextView tv_video_show_tip;

    private MediaPlayer mMediaPlayer;
    private Surface surface;

    public void setListener(final String filePath) {

        LogUtils.e(Constant.SDK_DATA_TAG, "filePath = " + filePath);

        iv_shade_bg.setVisibility(View.GONE);
        iv_half_replay.setVisibility(View.GONE);
        if (!CommonUtils.isNetworkAvailable(mContext) || NetworkUtils.isMobileConnected(mContext)) {
            tv_video_show_tip.setVisibility(View.VISIBLE);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (tv_video_show_tip != null) {
                        tv_video_show_tip.setVisibility(View.GONE);
                    }
                }
            }, 3000);
        }

        mVideoView.setSurfaceTextureListener(this);

        if (mVideoView.isAvailable()) {
            hasFile = filePath;
            onSurfaceTextureAvailable(mVideoView.getSurfaceTexture(), mVideoView.getWidth(), mVideoView.getHeight());
        }
    }

    public void setWithMobileConnect(final String filePath) {

        LogUtils.e(Constant.SDK_DATA_TAG, "filePath = " + filePath);

        iv_shade_bg.setVisibility(View.GONE);
        iv_half_replay.setVisibility(View.GONE);

        mVideoView.setSurfaceTextureListener(this);

        if (mVideoView.isAvailable()) {
            hasFile = filePath;
            onSurfaceTextureAvailable(mVideoView.getSurfaceTexture(), mVideoView.getWidth(), mVideoView.getHeight());
        }
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        System.out.println("onSurfaceTextureAvailable");
        surface = new Surface(surfaceTexture);
        new Thread(new PlayerVideo(hasFile)).start();//开启一个线程去播放视频
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        System.out.println("onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        System.out.println("onSurfaceTextureDestroyed");

        LogUtils.e(Constant.SDK_DATA_TAG, "onSurfaceTextureDestroyed = ");
        if (surfaceTexture != null) {
            surfaceTexture = null;
        }
        if (surface != null) {
            surface = null;
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        System.out.println("onSurfaceTextureUpdated");
    }

    private class PlayerVideo implements Runnable {

        private String mFilePath;

        public PlayerVideo(String fileName) {
            this.mFilePath = fileName;
        }

        @Override
        public void run() {
            Message msg = handler.obtainMessage();
            msg.obj = mFilePath;
            handler.sendMessage(msg);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            final String filePath = (String) msg.obj;
            if (null != filePath) {
                try {
                    mMediaPlayer = new MediaPlayer();
                    mMediaPlayer.setDataSource(filePath);
                    mMediaPlayer.setSurface(surface);

                    mMediaPlayer.prepare();

                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {

                            //全屏特殊处理
                            if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_FULL) {
                                mp.setLooping(true);
                            }
                            iv_shade_bg.setVisibility(View.GONE);
                            mMediaPlayer.start();
                        }
                    });

                    mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            if (filePath != null) {
                                File file = new File(filePath);
                                file.deleteOnExit();
                            }
                            return true;
                        }
                    });

                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            //全屏特殊处理
                            /*if (HuxinSdkManager.instance().getFloatType() == HuxinService.MODEL_TYPE_FULL) {
                                iv_half_replay.setVisibility(View.GONE);
                            } else {
                                iv_half_replay.setVisibility(View.VISIBLE);
                            }
                            iv_half_replay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMediaPlayer.start();
                                    iv_half_replay.setVisibility(View.GONE);
                                }
                            });*/
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private String hasFile;//视频本地路径

    private void loadVideo(final ShowData mShowModel) {

        final String filePath = AppConfig.getImageUrl(mContext, mShowModel.getFid());
        final String absolutePath = FileConfig.getVideoDownLoadPath();
        hasFile = AbFileUtil.hasFilePath(filePath /*+ ".mp4"*/, absolutePath);

        if (AbFileUtil.isEmptyString(hasFile)) {
            if (!AppUtils.isWifi(mContext)/*!CommonUtils.isNetworkAvailable(mContext)*/) {
                //setDefaultShow();//播放默认视频秀
                try {
                    Glide.with(mContext)
                            .load(AppConfig.getImageUrl(mContext, mShowModel.getPfid()))
                            .into(iv_shade_bg);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                iv_half_replay.setVisibility(View.VISIBLE);
                iv_half_replay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mClickListener != null) {
                            mClickListener.onLoadClick(filePath, absolutePath);
                        }
                    }
                });

            } else {
                AsyncTask<Object, Object, String> task = new AsyncTask<Object, Object, String>() {
                    String lastDownload = "";
                    long lastTime = 0;

                    @Override
                    protected String doInBackground(Object... params) {
                        long now = System.currentTimeMillis();
                        if (now - lastTime < 10 * 1000 && lastDownload.equals(filePath)) {
                            return null;
                        }
                        lastTime = now;
                        lastDownload = filePath;
                        return AbFileUtil.downloadFile(filePath /*+ ".mp4"*/, absolutePath);
                    }

                    @Override
                    protected void onPostExecute(String path) {
                        super.onPostExecute(path);

                        if (path != null) {
                            hasFile = path;
                            setListener(hasFile);
                        }
                    }
                };
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else {
            setListener(hasFile);
        }
    }

    public String downloadFile(String url, String dirPath, ProgressBar progress_round_bar) {
        InputStream in = null;
        FileOutputStream fileOutputStream = null;
        HttpURLConnection connection = null;
        String downFilePath = null;
        File file = null;
        try {
            if (!AbFileUtil.isCanUseSD()) {
                return null;
            }
            // 先判断SD卡中有没有这个文件，不比较后缀部分比较
            String fileNameNoMIME = AbFileUtil.getCacheFileNameFromUrl(url);
            File parentFile = new File(dirPath);
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            File[] files = parentFile.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; ++i) {
                    String fileName = files[i].getName();
                    if (files[i].isDirectory()) { //不兼容文件夹目录
                        files[i].delete();
                        continue;
                    }
                    String name = fileName.substring(0, fileName.lastIndexOf("."));
                    if (name.equals(fileNameNoMIME)) {
                        // 文件已存在
                        return files[i].getPath();
                    }
                }
            }

            URL mUrl = new URL(url);
            connection = (HttpURLConnection) mUrl.openConnection();
            connection.connect();
            int fileLength = connection.getContentLength();//文件大小
            // 获取文件名，下载文件
            String realFileName = AbFileUtil.getCacheFileNameFromUrl(url, connection);
            String tempFileName = System.currentTimeMillis() + "_" + realFileName;

            File tempFile = new File(dirPath, tempFileName);
            File realFile = new File(dirPath, realFileName);
            downFilePath = realFile.getPath();
            if (!realFile.exists()) {
                tempFile.deleteOnExit();
                tempFile.createNewFile();
            } else {
                // 文件已存在
                return realFile.getPath();
            }
            in = connection.getInputStream();
            fileOutputStream = new FileOutputStream(tempFile);
            byte[] b = new byte[1024];
            int temp = 0;
            long total = 0;
            while ((temp = in.read(b)) != -1) {
                total += temp;

                int rate = (int) (total * 100 / fileLength);
                if (rate % 5 == 0) {
                    // publishing the progress....
                    progress_round_bar.setProgress(rate);
                    Log.d("YW", "rate: " + rate);
                }
                fileOutputStream.write(b, 0, temp);
            }

            tempFile.renameTo(realFile);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(AbFileUtil.class.getName(), "有文件下载出错了,已删除");
            // 检查文件大小,如果文件为0B说明网络不好没有下载成功，要将建立的空文件删除
            if (file != null) {
                file.delete();
            }
            file = null;
            downFilePath = null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (file != null) {
                    file.delete();
                }
            }
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (file != null) {
                    file.delete();
                }
            }
            try {
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (file != null) {
                    file.delete();
                }
            }
        }
        return downFilePath;
    }

    /* 设置默认视频 */
    private void setDefaultShow() {
        onLoadLocalVideo();
    }

    /* 拿本地的默认视频 */
    private void onLoadLocalVideo() {
        String assetsVideo = getRawVideo(mContext, "sample_video_");
        if (assetsVideo != null) {
            hasFile = assetsVideo;
            setListener(hasFile);
            return;
        }
    }

    private String getRawVideo(Context context, String pt) {
        Class<?> clsR = null;
        String className = context.getPackageName() + ".R";
        try {
            Class<?> cls = Class.forName(className);
            for (Class<?> childClass : cls.getClasses()) {
                String simple = childClass.getSimpleName();
                if (simple.equals("raw")) {
                    clsR = childClass;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (clsR == null)
            return null;

        Field[] fields = clsR.getDeclaredFields();
        int rawId;
        String rawName;
        ArrayList<Integer> videoList = new ArrayList<Integer>();
        for (int i = 0; i < fields.length; i++) {
            try {
                rawId = fields[i].getInt(clsR);
                rawName = fields[i].getName();
                if (rawName.startsWith(pt)) {
                    videoList.add(rawId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (videoList.size() > 0) {
            int rand = (int) (Math.random() * videoList.size());
            rawId = videoList.get(rand);
            return "android.resource://" + context.getPackageName() + "/" + rawId;
        }
        return null;
    }

    /* 视频秀 end by 2016.8.12 */

    public interface IDownloadClickListener {
        void onLoadClick(String filePath, String absolutePath);
    }

    private IDownloadClickListener mClickListener;

    public void setOnDownloadClickListener(IDownloadClickListener listener) {
        mClickListener = listener;
    }

    public void setReplayIcon() {
        if (iv_half_replay != null) {
            iv_half_replay.setVisibility(View.GONE);
        }
    }

}
