package com.youmai.hxsdk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.ChatMsg;
import com.youmai.hxsdk.chat.ContentLocation;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.view.full.MapViewUtil;

import java.util.ArrayList;
import java.util.List;


public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public enum UI_Type {
        TEXT_LEFT, TEXT_RIGHT, IMAGE_LEFT, IMAGE_RIGHT, MAP_LEFT, MAP_RIGHT
    }

    private Context mContext;
    private List<ChatMsg> mList;

    public ChatRecyclerAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public void setDataList(List<ChatMsg> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void setItem(ChatMsg item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {  //给Item划分类别
        int type = UI_Type.IMAGE_LEFT.ordinal();
        ChatMsg msg = mList.get(position);
        if (msg.getMsgType() == ChatMsg.MsgType.TEXT) {
            if (msg.isOwner()) {
                type = UI_Type.TEXT_RIGHT.ordinal();
            } else {
                type = UI_Type.TEXT_LEFT.ordinal();
            }
        } else if (msg.getMsgType() == ChatMsg.MsgType.PICTURE) {
            if (msg.isOwner()) {
                type = UI_Type.IMAGE_RIGHT.ordinal();
            } else {
                type = UI_Type.IMAGE_LEFT.ordinal();
            }

        } else if (msg.getMsgType() == ChatMsg.MsgType.LOCATION) {
            if (msg.isOwner()) {
                type = UI_Type.MAP_RIGHT.ordinal();
            } else {
                type = UI_Type.MAP_LEFT.ordinal();
            }

        } else if (msg.getMsgType() == ChatMsg.MsgType.BIZCARD) {
            if (msg.isOwner()) {
                type = UI_Type.TEXT_RIGHT.ordinal();
            } else {
                type = UI_Type.TEXT_LEFT.ordinal();
            }
        }
        return type;

    }

    public int getLastPosition() {
        return getItemCount() - 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        RecyclerView.ViewHolder holder = null;


        switch (UI_Type.values()[viewType]) {   //根据Item类别不同，选择不同的Item布局
            case TEXT_LEFT: {
                view = inflater.inflate(R.layout.hx_text_left, parent, false);
                holder = new TextViewHolder(view);
            }
            break;
            case TEXT_RIGHT: {
                view = inflater.inflate(R.layout.hx_text_right, parent, false);
                holder = new TextViewHolder(view);
            }
            break;
            case IMAGE_LEFT: {
                view = inflater.inflate(R.layout.hx_image_left, parent, false);
                holder = new ImageViewHolder(view);
            }
            break;
            case IMAGE_RIGHT: {
                view = inflater.inflate(R.layout.hx_image_right, parent, false);
                holder = new ImageViewHolder(view);
            }
            break;
            case MAP_LEFT: {
                view = inflater.inflate(R.layout.hx_call_msg_location_layout, parent, false);
                holder = new MapViewHolder(view);
            }
            break;
            case MAP_RIGHT: {
                view = inflater.inflate(R.layout.hx_image_right, parent, false);
                holder = new ImageViewHolder(view);
            }
            break;
        }
        return holder;

    }

    // 数据绑定
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        ChatMsg item = mList.get(position);

        if (viewHolder instanceof TextViewHolder) {
            String content = item.getMsgContent().getText().getContent();
            TextView textView = ((TextViewHolder) viewHolder).textView;
            textView.setText(content);
        } else if (viewHolder instanceof ImageViewHolder) {
            if (item.getMsgType() == ChatMsg.MsgType.PICTURE) {

                ImageView imageView = ((ImageViewHolder) viewHolder).imageView;

                String fid = item.getMsgContent().getPicture().getPicUrl();
                String url = AppConfig.getImageUrl(mContext, fid);

                Glide.with(mContext)
                        .load(url)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop())
                        .into(imageView);
            } else if (item.getMsgType() == ChatMsg.MsgType.LOCATION) {

            }

        } else if (viewHolder instanceof MapViewHolder) {

            ContentLocation mLocation = item.getMsgContent().getLocation();
            String location = mLocation.getLongitudeStr() + "," + mLocation.getLatitudeStr();
            final LatLng latLng = new LatLng(
                    Double.parseDouble(mLocation.getLongitudeStr()),
                    Double.parseDouble(mLocation.getLatitudeStr()));

            LogUtils.e(Constant.SDK_UI_TAG, "location = " + location);

            MapView mMapView = ((MapViewHolder) viewHolder).fm_msg_map;
            final MapViewUtil mMapViewUtil = new MapViewUtil(mContext, mMapView);
            mMapViewUtil.onCreate(null);
            mMapViewUtil.setLocation(location);//标志物
        }


    }

    class TextViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView headimgView;

        public TextViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_content);
            headimgView = (ImageView) itemView.findViewById(R.id.img_head);
        }
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView headimgView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_content);
            headimgView = (ImageView) itemView.findViewById(R.id.img_head);
        }

    }


    class MapViewHolder extends RecyclerView.ViewHolder {
        private MapView fm_msg_map;
        private ImageView iv_navigate;

        public MapViewHolder(View itemView) {
            super(itemView);
            fm_msg_map = (MapView) itemView.findViewById(R.id.fm_msg_map);
            iv_navigate = (ImageView) itemView.findViewById(R.id.iv_navigate);
        }
    }


}

