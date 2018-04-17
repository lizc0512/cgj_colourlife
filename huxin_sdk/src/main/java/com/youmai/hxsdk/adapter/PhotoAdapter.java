package com.youmai.hxsdk.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.PhotoPickerActivity;
import com.youmai.hxsdk.photopicker.beans.Photo;
import com.youmai.hxsdk.photopicker.widgets.ItemCameraView;
import com.youmai.hxsdk.photopicker.widgets.PhotoLayoutView;
import com.youmai.hxsdk.utils.ScreenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Class: PhotoAdapter
 * @Description: 图片适配器
 * @author: lling(www.liuling123.com)
 * @Date: 2015/11/4
 */
public class PhotoAdapter extends BaseAdapter {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_PHOTO = 1;

    private List<Photo> mDatas;
    //存放已选中的Photo数据
    private List<String> mSelectedPhotos;
    private Context mContext;
    private int mWidth;
    //是否显示相机，默认不显示
    private boolean mIsShowCamera = false;
    //照片选择模式，默认单选
    private int mSelectMode = PhotoPickerActivity.MODE_SINGLE;
    //图片选择数量
    private int mMaxNum = PhotoPickerActivity.DEFAULT_NUM;

    private View.OnClickListener mOnPhotoClick;
    private PhotoClickCallBack mCallBack;

    public PhotoAdapter(Context context, List<Photo> mDatas) {
        this.mDatas = mDatas;
        this.mContext = context;
        int screenWidth = ScreenUtils.getWidthPixels(mContext);
        mWidth = (screenWidth - ScreenUtils.dipTopx(mContext, 4)) / 3;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mIsShowCamera) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PHOTO;
        }
    }

    @Override
    public int getCount() {
        return mIsShowCamera ? mDatas.size() + 1 : mDatas.size();
    }

    @Override
    public Photo getItem(int position) {
        if (mIsShowCamera) {
            if (position == 0 || position == -1) {
                return null;
            }
            return mDatas.get(position - 1);
        } else {
            if (position == -1) {
                return null;
            }
            return mDatas.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        //无实际意义
        return position;
    }

    public void setDatas(List<Photo> mDatas) {
        this.mDatas = mDatas;
    }

    public void setIsShowCamera(boolean isShowCamera) {
        this.mIsShowCamera = isShowCamera;
    }

    public boolean isShowCamera() {
        return mIsShowCamera;
    }

    public void setMaxNum(int maxNum) {
        this.mMaxNum = maxNum;
    }

    public void setPhotoClickCallBack(PhotoClickCallBack callback) {
        mCallBack = callback;
    }


    /**
     * 获取已选中相片
     *
     * @return
     */
    public List<String> getSelectedPhotos() {
        return mSelectedPhotos;
    }

    public void setSelectMode(int selectMode) {
        this.mSelectMode = selectMode;
        if (mSelectMode == PhotoPickerActivity.MODE_MULTI) {
            initMultiMode();
        }
    }

    /**
     * 初始化多选模式所需要的参数
     */
    private void initMultiMode() {
        mSelectedPhotos = new ArrayList<String>();
        mOnPhotoClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = v.findViewById(PhotoLayoutView.imageview_photo_tag.hashCode()).getTag().toString();
                if (mSelectedPhotos.contains(path)) {
                    v.findViewWithTag(PhotoLayoutView.mask_tag).setVisibility(View.GONE);//findViewById(R.id.mask).setVisibility(View.GONE);
                    //  v.findViewById(R.id.checkmark).setSelected(false);
                    v.findViewById(PhotoLayoutView.checkmark_tag.hashCode()).setSelected(false);
                    mSelectedPhotos.remove(path);
                } else {
                    if (mSelectedPhotos.size() >= mMaxNum) {
                        Toast.makeText(mContext, "已达到照片选择上限",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mSelectedPhotos.add(path);
                    v.findViewWithTag(PhotoLayoutView.mask_tag).setVisibility(View.VISIBLE);
                    v.findViewById(PhotoLayoutView.checkmark_tag.hashCode()).setSelected(true);
                }
                if (mCallBack != null) {
                    mCallBack.onPhotoClick();
                }
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == TYPE_CAMERA) {
            /*convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.pp_item_camera_layout, null);*/
            convertView = new ItemCameraView(mContext);
            convertView.setTag(null);
            //设置高度等于宽度
            GridView.LayoutParams lp = new GridView.LayoutParams(mWidth, mWidth);
            convertView.setLayoutParams(lp);
        } else {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                /*convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.pp_item_photo_layout, null);*/
                convertView = new PhotoLayoutView(mContext);
                holder.photoImageView = (ImageView) convertView.findViewById(PhotoLayoutView.imageview_photo_tag.hashCode());//(R.id.imageview_photo);
                holder.selectView = ((PhotoLayoutView) convertView).getCheckmark();
                holder.maskView = convertView.findViewWithTag(PhotoLayoutView.mask_tag);
                holder.wrapLayout = (FrameLayout) convertView.findViewWithTag(PhotoLayoutView.wrap_layout_tag);//convertView.findViewById(R.id.wrap_layout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.photoImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.hx_pp_ic_photo_loading));//setImageResource(R.drawable.pp_ic_photo_loading);


            Photo photo = getItem(position);
            if (mSelectMode == PhotoPickerActivity.MODE_MULTI) {
                holder.wrapLayout.setOnClickListener(mOnPhotoClick);
                // holder.photoImageView.setTag(photo.getPath());
                holder.selectView.setVisibility(View.VISIBLE);
                if (mSelectedPhotos != null && mSelectedPhotos.contains(photo.getPath())) {
                    holder.selectView.setSelected(true);
                    holder.maskView.setVisibility(View.VISIBLE);
                } else {
                    holder.selectView.setSelected(false);
                    holder.maskView.setVisibility(View.GONE);
                }
            } else {
                holder.selectView.setImageResource(R.drawable.hx_btn_pic_pressed);
                holder.selectView.setBackgroundColor(Color.parseColor("#99000000"));
                holder.selectView.setVisibility(View.VISIBLE);//GONE
            }

//            final ViewHolder finalHolder = holder;
//            Glide.with(mContext).load(new File(photo.getPath())).asBitmap()
//                    .into(new SimpleTarget<Bitmap>(mWidth, mWidth) {
//                        @Override
//                        public void onResourceReady(Bitmap resource,
//                                                    GlideAnimation<? super Bitmap> glideAnimation) {
//                            finalHolder.photoImageView.setImageBitmap(resource);
//                        }
//                    });

            Glide.with(mContext)
                    .load(new File(photo.getPath()))
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .centerCrop())
                    .into(holder.photoImageView);

        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView photoImageView;
        private ImageView selectView;
        private View maskView;
        private FrameLayout wrapLayout;
    }

    /**
     * 多选时，点击相片的回调接口
     */
    public interface PhotoClickCallBack {
        void onPhotoClick();
    }
}
