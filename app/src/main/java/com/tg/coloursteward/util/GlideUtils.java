package com.tg.coloursteward.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

/**
 * @name ${lizc}
 * @class name：com.tg.coloursteward.util
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/7/10 11:24
 * @change
 * @chang time
 * @class describe
 */
public class GlideUtils {
    public GlideUtils() {
    }

    /**
     * @param mContext
     * @param path
     * @param mImageView 默认加载
     */
    public static void loadImageView(Context mContext, String path, ImageView mImageView) {
        try {
            if (null != mContext) {
                Glide.with(mContext).load(path).apply((new RequestOptions()).diskCacheStrategy(DiskCacheStrategy.ALL)).into(mImageView);
            }
        } catch (Exception var4) {
            ;
        }

    }

    //设置加载中以及加载失败图片
    public static void loadImageDefaultDisplay(Context mContext, String path, ImageView mImageView, int lodingImage, int errorImageView) {
        try {
            Glide.with(mContext).load(path).apply(new RequestOptions().placeholder(lodingImage).error(errorImageView).diskCacheStrategy(DiskCacheStrategy.ALL)).into(mImageView);
        } catch (Exception e) {

        }
    }

    //设置加载圆角图片
    public static void loadRoundImageDisplay(Context mContext, Object path, int round, ImageView mImageView, int lodingImage, int errorImageView) {
        try {
            RoundedCorners roundedCorners = new RoundedCorners(round);//数字为圆角度数
            RequestOptions coverRequestOptions = new RequestOptions()
                    .transforms(new CenterCrop(), roundedCorners)
                    .error(errorImageView)
                    .centerCrop()
                    .dontAnimate()
                    .placeholder(lodingImage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(mContext).load(path).apply(coverRequestOptions).into(mImageView);
        } catch (Exception e) {

        }
    }

    public static void loadHeadPhoto(Context mContext, String path, ImageView mImageView) {
        try {
            if (null != mContext) {
                Glide.with(mContext).load(path).apply((new RequestOptions()).diskCacheStrategy(DiskCacheStrategy.ALL)).into(mImageView);
            }
        } catch (Exception var4) {
            ;
        }

    }

    public static void loadImageViewSize(Context mContext, String path, int width, int height, ImageView mImageView) {
        try {
            if (mContext != null) {
                Glide.with(mContext).asBitmap().load(new File(path)).apply((new RequestOptions()).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.RESOURCE).override(width, height).centerCrop().format(DecodeFormat.PREFER_RGB_565)).thumbnail(0.5F).into(mImageView);
            }
        } catch (Exception var6) {
            ;
        }

    }

    public static void loadImageViewLoding(Context mContext, String path, ImageView mImageView, int lodingImage, int errorImageView) {
        try {
            if (mContext != null) {
                Glide.with(mContext).load(path).apply((new RequestOptions()).placeholder(lodingImage).error(errorImageView)).into(mImageView);
            }
        } catch (Exception var6) {
            ;
        }

    }

    public static void clearImagView(Context mContext, ImageView mImageView) {
        try {
            if (mContext != null) {
                Glide.with(mContext).clear(mImageView);
            }
        } catch (Exception var3) {
            ;
        }

    }
}
