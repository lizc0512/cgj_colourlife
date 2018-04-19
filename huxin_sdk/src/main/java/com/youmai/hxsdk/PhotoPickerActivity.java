package com.youmai.hxsdk;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.adapter.PhotoAdapter;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.config.FileConfig;
import com.youmai.hxsdk.photopicker.beans.Photo;
import com.youmai.hxsdk.photopicker.beans.PhotoFolder;
import com.youmai.hxsdk.photopicker.widgets.PhotoPickerView;
import com.youmai.hxsdk.photopicker.widgets.PpTabbarView;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.CompressImage;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Class: PhotoPickerActivity
 * @Description: 照片选择界面
 * @author:
 * @Date: 2015/11/4
 */
public class PhotoPickerActivity extends SdkBaseActivity implements PhotoAdapter.PhotoClickCallBack {

    public final static String TAG = PhotoPickerActivity.class.getSimpleName();

    public final static int REQUEST_CAMERA = 1;
    /**
     * 是否直接进入相机
     */
    public final static String EXTRA_USE_CAMERA = "is_use_camera";
    /**
     * 是否显示相机
     */
    public final static String EXTRA_SHOW_CAMERA = "is_show_camera";
    /**
     * 照片选择模式
     */
    public final static String EXTRA_SELECT_MODE = "select_mode";
    /**
     * 最大选择数量
     */
    public final static String EXTRA_MAX_MUN = "max_num";
    /**
     * 单选
     */
    public final static int MODE_SINGLE = 0;
    /**
     * 多选
     */
    public final static int MODE_MULTI = 1;
    /**
     * 默认最大选择数量
     */
    public final static int DEFAULT_NUM = 9;

    private final static String ALL_PHOTO = "所有图片";//打开时，默认读取哪个文件夹的图片
    //private final static String ALL_PHOTO = "Camera";

    /**
     * 是否显示相机，默认不显示
     */
    private boolean mIsShowCamera = false;
    /**
     * 是否仅使用相机，默认不用
     */
    private boolean isUserCamera = false;
    /**
     * 照片选择模式，默认是单选模式
     */
    private int mSelectMode = 0;
    /**
     * 最大选择数量，仅多选模式有用
     */
    private int mMaxNum;

    private GridView mGridView;
    //private List<Photo> mPhotoLists = new ArrayList<>();
    //private ArrayList<String> mSelectList = new ArrayList<>();
    private PhotoAdapter mPhotoAdapter;
    private ProgressDialog mProgressDialog;

    private int mPosition = -1;//默认

    private Button mCommitBtn;


    /**
     * 拍照时存储拍照结果的临时文件
     */
    private File mTmpFile;

    private PhotoPickerView pickerView;
    private String dstUuid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickerView = new PhotoPickerView(this);
        setContentView(pickerView);

        dstUuid = getIntent().getStringExtra("dstPhone");

        initIntentParams();
        initView();
        if (!AppUtils.isExtSdcard()) {
            Toast.makeText(mContext, "No SD card!", Toast.LENGTH_SHORT).show();
            return;
        }
        new PhotosAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        HuxinSdkManager.instance().getStackAct().addActivity(this);

        isUserCamera = getIntent().getBooleanExtra(EXTRA_USE_CAMERA, false);
        if (isUserCamera) {
            //直接进入相机
            showCamera();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            mProgressDialog = null;
        }
        HuxinSdkManager.instance().getStackAct().finishActivity(this);
    }


    private void initView() {
        mGridView = (GridView) pickerView.findViewWithTag(PhotoPickerView.photo_gridview_tag);
        //设置单选模式
        mGridView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);

        //back event
        pickerView.findViewWithTag(PpTabbarView.btn_back_tag).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView top_tv_ph = (TextView) pickerView.findViewWithTag(PpTabbarView.bottom_tv_ph_tag);
        top_tv_ph.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mPhotoAdapter == null) {
                    ToastUtil.showToast(mContext, "图片还没加载完成哦");
                } else {
                    selectPhoto(mPhotoAdapter.getItem(mPosition));
                }
            }
        });
    }


    /**
     * 初始化选项参数
     */
    private void initIntentParams() {
        mIsShowCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, false);
        mSelectMode = getIntent().getIntExtra(EXTRA_SELECT_MODE, MODE_SINGLE);
        mMaxNum = getIntent().getIntExtra(EXTRA_MAX_MUN, DEFAULT_NUM);
        if (mSelectMode == MODE_MULTI) {
            //如果是多选模式，需要将确定按钮初始化以及绑定事件
            mCommitBtn = (Button) pickerView.findViewWithTag(PpTabbarView.commit_tag);//(Button) findViewById(R.id.commit);
            mCommitBtn.setVisibility(View.VISIBLE);
            mCommitBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String path = mPhotoAdapter.getSelectedPhotos().get(0);
                    returnData(path, false);
                }
            });
        }
    }

    private void getPhotosSuccess(Map<String, PhotoFolder> result) {
        Set<String> keys1 = result.keySet();
        List<Photo> photoLists = new ArrayList<>();

        for (String key : keys1) {
            PhotoFolder folder = result.get(key);
            if (folder.getName().equals(ALL_PHOTO)) {//所有图片  ： 选择模式文件夹   "Camera"
                folder.setIsSelected(true);
                photoLists.addAll(folder.getPhotoList());
            }
        }

//            if (photoLists.size() == 0) {
//                Photo photo = new Photo("a");
//                photoLists.add(photo);
//            }


        mPhotoAdapter = new PhotoAdapter(this, photoLists);
        mPhotoAdapter.setIsShowCamera(mIsShowCamera);
        mPhotoAdapter.setSelectMode(mSelectMode);
        mPhotoAdapter.setMaxNum(mMaxNum);
        mPhotoAdapter.setPhotoClickCallBack(this);
        mGridView.setAdapter(mPhotoAdapter);
        Set<String> keys = result.keySet();
        List<PhotoFolder> folders = new ArrayList<>();

        for (String key : keys) {
            if (ALL_PHOTO.equals(key)) {
                PhotoFolder folder = result.get(key);
                folders.add(0, folder);
            } else {
                folders.add(result.get(key));
            }
        }

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (mPhotoAdapter.isShowCamera() && position == 0) {
                    showCamera();
                    return;
                }
                mPosition = position;
            }
        });
    }

    /**
     * 点击选择某张照片
     *
     * @param photo
     */
    private void selectPhoto(Photo photo) {
        LogUtils.e(TAG, "selectPhoto");
        if (photo == null) {
            ToastUtil.showToast(mContext, "未选择发送图片");
            return;
        }
        String path = photo.getPath();
        if (mSelectMode == MODE_SINGLE) {
            returnData(path, false);
        }
    }

    @Override
    public void onPhotoClick() {
        LogUtils.e(TAG, "onPhotoClick");
        List<String> list = mPhotoAdapter.getSelectedPhotos();
        if (list != null && list.size() > 0) {
            mCommitBtn.setEnabled(true);
            mCommitBtn.setText(String.format(getString(R.string.hx_picture_fragment_unit), list.size()));
        } else {
            mCommitBtn.setEnabled(false);
            mCommitBtn.setText(R.string.hx_confirm);
        }
    }

    /**
     * 返回选择图片的路径
     */
    private void returnData(String path, boolean photograph) {

        String imagePath = CompressImage.compressImage(path);
        LogUtils.e(Constant.SDK_UI_TAG, "imagePath = " + imagePath);

        if (photograph) { // FIXME: 2017/2/24 拍照后原图删除
            File photo = new File(path);
            if (photo.exists()) {
                photo.delete();
            }
        }

        if (imagePath == null || !new File(imagePath).exists()) {
            Log.w("123", "imagePath is null");
            Toast.makeText(this, "该照片不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        // im使用
        boolean isUserByIM = getIntent().getBooleanExtra("is_user_by_im", false);
        if (!isUserByIM) {
            HuxinSdkManager.instance().postPicture(dstUuid, imagePath, imagePath,
                    true, false);
            if (Build.MODEL != null && Build.MODEL.startsWith("OPPO R9m")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 200);
            } else {
                finish();
            }

        } else {
            Intent it = new Intent();
            it.putExtra("path", imagePath);
            setResult(1, it);
            finish();
        }
        //finish();
    }


    private class PhotosAsyncTask extends AsyncTask<Void, Void, Map<String, PhotoFolder>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(mContext, null, "loading...");
        }


        @Override
        protected Map<String, PhotoFolder> doInBackground(Void... params) {
            return getPhotos(mContext);
        }

        @Override
        protected void onPostExecute(Map<String, PhotoFolder> result) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if (result != null) {
                getPhotosSuccess(result);
            }
        }
    }


    /**
     * 选择相机
     */
    private void showCamera() {

        Camera camera = null;
        try {
            camera = Camera.open(0);
            camera.setPreviewDisplay(null);
            camera.startPreview();
            camera.unlock();

            // 跳转到系统照相机
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                // 设置系统相机拍照后的输出路径
                // 创建临时文件
                mTmpFile = new File(FileConfig.getPicDownLoadPath(), System.currentTimeMillis() + ".jpg");
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
                startActivityForResult(cameraIntent, REQUEST_CAMERA);

                setFloatView(false);
            } else {
                Toast.makeText(getApplicationContext(), "没找到摄像头", Toast.LENGTH_SHORT).show();
            }

        } catch (RuntimeException e) {
            LogUtils.e(Constant.SDK_UI_TAG, e.toString());
            ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.hx_camera_record_tip));
            if (camera != null) {
                try {
                    camera.setPreviewCallback(null);
                    camera.stopPreview();
                    camera.lock();
                    camera.release();
                } catch (Exception e1) {
                    LogUtils.e(Constant.SDK_UI_TAG, "freeCameraResource=" + e.getMessage().toString());
                } finally {
                    camera = null;
                }
            }
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (camera != null) {
                try {
                    camera.setPreviewCallback(null);
                    camera.stopPreview();
                    camera.lock();
                    camera.release();
                } catch (Exception e) {
                    LogUtils.e(Constant.SDK_UI_TAG, "freeCameraResource=" + e.getMessage().toString());
                } finally {
                    camera = null;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 相机拍照完成后，返回图片路径
        if (requestCode == REQUEST_CAMERA) {
            setFloatView(true);

            if (resultCode == Activity.RESULT_OK) {
                if (mTmpFile != null) {
                    returnData(mTmpFile.getAbsolutePath(), true);
                }
            } else {
                if (mTmpFile != null && mTmpFile.exists()) {
                    mTmpFile.delete();
                }
            }
            if (isUserCamera) {
                //只用相机的情况
                finish();
            }
        }
    }


    private Map<String, PhotoFolder> getPhotos(Context context) {

        Map<String, PhotoFolder> folderMap = new HashMap<>();

        String allPhotosKey = "所有图片";
        PhotoFolder allFolder = new PhotoFolder();
        allFolder.setName(allPhotosKey);
        allFolder.setDirPath(allPhotosKey);
        allFolder.setPhotoList(new ArrayList<Photo>());
        folderMap.put(allPhotosKey, allFolder);

        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();

        // 只查询jpeg和png的图片
        Cursor cursor = contentResolver.query(imageUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED + " desc");

        if (cursor == null)
            return folderMap;

        while (cursor.moveToNext()) {
            // 获取图片的路径
            String path = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));

            // 获取该图片的父路径名
            File parentFile = new File(path).getParentFile();
            if (parentFile == null) {
                continue;
            }
            if (parentFile.getName().equalsIgnoreCase("Pic")) {
                continue;
            }
            if (parentFile.getName().equalsIgnoreCase("Head")) {
                continue;
            }

            String dirPath = parentFile.getAbsolutePath();
            if (folderMap.containsKey(dirPath)) {
                Photo photo = new Photo(path);
                PhotoFolder photoFolder = folderMap.get(dirPath);
                photoFolder.getPhotoList().add(photo);
                folderMap.get(allPhotosKey).getPhotoList().add(photo);
                continue;
            } else {
                // 初始化imageFolder
                PhotoFolder photoFolder = new PhotoFolder();
                List<Photo> photoList = new ArrayList<Photo>();
                Photo photo = new Photo(path);
                photoList.add(photo);
                photoFolder.setPhotoList(photoList);
                photoFolder.setDirPath(dirPath);
                photoFolder.setName(dirPath.substring(dirPath.lastIndexOf(File.separator) + 1, dirPath.length()));
                folderMap.put(dirPath, photoFolder);
                folderMap.get(allPhotosKey).getPhotoList().add(photo);
            }

        }
        cursor.close();
        return folderMap;
    }

}
