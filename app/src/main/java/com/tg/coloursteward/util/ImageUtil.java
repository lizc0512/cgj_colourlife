package com.tg.coloursteward.util;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by prince70 on 2018/3/28.
 */

public class ImageUtil {

    /**
     * 改变图片尺寸
     *
     * @param bm        所要转换的bitmap
     * @param newWidth  新的宽
     * @param newHeight 新的高
     * @return 指定宽高的bitmap
     */
    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 按照比例缩放图片
     */
    public static Bitmap zoomImgScale(Bitmap bm, int newWidth, int newHeight) {
        if (null == bm) return null;

        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        float scale = scaleWidth < scaleHeight ? scaleWidth : scaleHeight;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * 获得压缩后的图片
     */
    public static Bitmap compressImageFromFile(String srcPath, float width, float height) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容
        Bitmap bitmap;
        BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //采样率
        int be = 1;
        if (w > h && w > width) {
            be = (int) (w / width);
        } else if (w < h && h > height) {
            be = (int) (h / height);
        }
        newOpts.inSampleSize = be;//设置采样率

        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
//      return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
        //其实是无效的,大家尽管尝试
        return bitmap;
    }

    /**
     * @param bitmap     原图
     * @param edgeLength 图片的边长
     * @return 缩放截取正中部分后的位图。
     */
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
        if (null == bitmap || edgeLength <= 0) {
            return null;
        }

        //按正方形裁剪图片
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int edge = width < height ? width : height; //取宽和高中的最小者来作为正方形的边长
        int retX = width > height ? (width - height) / 2 : 0;
        int retY = width > height ? 0 : (height - width) / 2;
        bitmap = Bitmap.createBitmap(bitmap, retX, retY, edge, edge, null, false);

        //缩放图片
        bitmap = Bitmap.createScaledBitmap(bitmap, edgeLength, edgeLength, true);

        return bitmap;
    }

    /**
     * 从Uri加载Bitmap
     */
    public static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    /**
     * 获取加载失败的图片
     */
//    public static int getFailLoadPic(){
//        return R.mipmap.ic_fail_pic;
//    }

    /**
     * 获取加载失败的图片
     */
//    public static Bitmap getFailLoadPic(Context context){
//        if (context == null) return null;
//        return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_fail_pic);
//    }

    /**
     * 获取加载中的图片
     */
//    public static int getLoadingPic(){
//        return  R.mipmap.ic_loding;
//    }

    /**
     * 获取加载中的图片
     */
//    public static Bitmap getLoadingPic(Context context){
//        return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_loding);
//    }

    /**
     * 获取经过用户裁剪的图片
     * 使用了UCrop开源库
     * @param sourceUri 源位置
     * @param destinationUri 目标位置
     */
//    public static void cropImg(Activity activity, Uri sourceUri, Uri destinationUri, int w, int h){
//        UCrop.Options options = new UCrop.Options();
//        options.setToolbarTitle("裁剪");
//        UCrop.of(sourceUri, destinationUri)
//                .withAspectRatio(1, 1)
//                .withOptions(options)
//                .withMaxResultSize(w, h)
//                .start(activity);
//    }

    /**
     * 保存Bitmap为本地文件
     */
    public static void saveImage(Bitmap bitmap, String path) {
        File file = new File(path);
        if (file != null) {
            try {
                //创建文件
                file.createNewFile();
                //获取文件输出流
                FileOutputStream fos = new FileOutputStream(file);
                //将Bitmap存储为jpg格式的图片
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                //刷新文件流
                fos.flush();
                //关闭文件
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void viewSaveToImage(Context context, View view) {
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheBackgroundColor(Color.WHITE);
        // 把一个View转换成图片
        Bitmap cachebmp = loadBitmapFromView(view);
        FileOutputStream fos;
        String imagePath = "";
        if (XXPermissions.isHasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            try {
                // 判断手机设备是否有SD卡
                boolean isHasSDCard = Environment.getExternalStorageState().equals(
                        android.os.Environment.MEDIA_MOUNTED);
                File file = null;
                if (isHasSDCard) {
                    // SD卡根目录
                    File sdRoot = Environment.getExternalStorageDirectory();
                    file = new File(sdRoot, Calendar.getInstance().getTimeInMillis() + ".png");
                    fos = new FileOutputStream(file);
                    imagePath = file.getAbsolutePath();
                } else
                    throw new Exception("创建文件失败!");
                cachebmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), "code", null);
                // 最后通知图库更新
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
                        + file)));
                ToastUtil.showLongToastCenter(context, "已成功保存到手机");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            view.destroyDrawingCache();
        }
    }

    private static Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */
        v.layout(0, 0, w, h);
        v.draw(c);
        return bmp;
    }


    /**
     * 根据url获取缓存图片
     */
//    public static Bitmap decodeUrl(String url){
//        String filename =  PathUtil.getCachePath() + PathUtil.url2Filename(url);
//        Bitmap bitmap = null;
//        //获取本地缓存的图片
//        if (FileUtils.isFileExists(filename)) {
//            bitmap = BitmapFactory.decodeFile(filename);
//        }
//        return bitmap;
//    }

    /**
     * 根据url获取图片，实现了本地缓存
     */
//    public static Bitmap loadBitmap(String url){
//        return loadBitmap(url, -1, -1);
//    }

//    public static Bitmap loadBitmap(String url, int w, int h){
//        if (url == null || url.isEmpty()) return null;
//
//        String filename =  PathUtil.getCachePath() + PathUtil.url2Filename(url);
//        Bitmap bitmap = null;
//        //获取本地缓存的图片
//        if (FileUtils.isFileExists(filename)) {
//            if (w > 0 && h > 0) {
//                bitmap = compressImageFromFile(filename, w, h);
//            } else {
//                bitmap = BitmapFactory.decodeFile(filename);
//            }
//        }
//        //本地没有缓存，下载图片
//        if (bitmap == null){
//            bitmap = downloadImage(url);
//            //下载成功，保存到本地缓存
//            if (bitmap != null){
//                saveImage(bitmap, filename);
//            }
//        }
//        return bitmap;
//    }
}
