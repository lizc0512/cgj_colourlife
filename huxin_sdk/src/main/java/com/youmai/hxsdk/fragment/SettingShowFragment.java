package com.youmai.hxsdk.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.SettingShowActivity;
import com.youmai.hxsdk.activity.ShowResultActivity;
import com.youmai.hxsdk.adapter.SettingShowAdapter;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.crop.Crop;
import com.youmai.hxsdk.entity.ShowModel;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.module.movierecord.MediaStoreUtils;
import com.youmai.hxsdk.module.movierecord.MovieRecodeActivity;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ImageUtils;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.utils.ToastUtil;
import com.youmai.hxsdk.view.refresh.HeaderSpanSizeLookup;
import com.youmai.hxsdk.view.refresh.OnNextPageListener;
import com.youmai.hxsdk.view.refresh.RefreshRecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


/**
 * Created by colin on 2016/9/12.
 * 用户秀选择素材
 */
public class SettingShowFragment extends BaseFragment {

    public static final String TAG = SettingShowFragment.class.getSimpleName();

    private static final int FROM_CAMERA = 1;// 拍照
    public final static int FROM_PHOTO = 2;  //相册
    public final static int IMAGE_CUT = 3;   //剪裁
    private File mFile;

    private static final int columns = 3;
    private RefreshRecyclerView mPullToRefreshView;
    private SettingShowAdapter mAdapter;

    // 是否刷新状态
    //private boolean isPullRefresh = false;
    private int mPageIndex = 1; // 加载page索引

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hx_fragment_show_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh();

        //首次进入显示刷新动画
        if (mPageIndex == 1) {
            mPullToRefreshView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRefreshing(true);
                }
            }, 5);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mFile && mFile.exists()) { // FIXME: 2017/1/16 删除拍摄的图片
            mFile.delete();
        }
    }

    private void initView(View view) {
        TextView tv_back = (TextView) view.findViewById(R.id.tv_back);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.hx_show_title);


        mPullToRefreshView = (RefreshRecyclerView) view.findViewById(R.id.recycle_refresh);

        mPullToRefreshView.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mAdapter = new SettingShowAdapter(mAct, this);
        mPullToRefreshView.setAdapter(mAdapter);


        GridLayoutManager manager = new GridLayoutManager(mAct, columns);

        HeaderSpanSizeLookup lookup = new HeaderSpanSizeLookup(
                mPullToRefreshView.getHeaderAdapter(), manager.getSpanCount());
        manager.setSpanSizeLookup(lookup); //设置hear and foot view 居中显示
        mPullToRefreshView.setLayoutManager(manager);

        //设置下拉刷新回调
        mPullToRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /*if (isPullRefresh) {// 如果正在刷新就返回
                    return;
                }
                isPullRefresh = true;*/
                SettingShowFragment.this.onRefresh();
            }
        });
        //设置上拉刷新回调
        mPullToRefreshView.setOnNextPageListener(new OnNextPageListener() {
            @Override
            public void onNextPage() {
                SettingShowFragment.this.onRefresh();
            }
        });


        int paddingItem = getResources().getDimensionPixelOffset(R.dimen.hx_show_item_spacing);
        mPullToRefreshView.addItemDecoration(new PaddingItemDecoration(paddingItem));

    }


    private void onRefresh() {
        IGetListener listener = new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                refreshComplete();
                if (!isAdded()) {
                    return;
                }
                ShowModel resp = GsonUtil.parse(response, ShowModel.class);
                if (resp == null) {
                    if (!isAdded()) {
                        return;
                    }
                    refreshComplete();
                    Toast.makeText(getActivity(), getString(R.string.hx_toast_34), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (resp.isSucess()) {
                    List<ShowModel.DBean.ModelsBean> list = resp.getD().getModels();
                    ShowModel.DBean.QueryResultBean resultBean = resp.getD().getQueryResult();

                    if (resultBean.getPageCount() == mPageIndex) {
                        setRefreshEnable(false);
                    } else {
                        mPageIndex++;
                        setRefreshEnable(true);

                    }

                    mAdapter.setList(list);
                    //isPullRefresh = false;
                }
            }
        };

        HuxinSdkManager.instance().showTemplet(mPageIndex, listener);

    }


    /**
     * 显示正在下拉刷新动画
     *
     * @param b false:关闭 ,ture：打开
     */
    private void setRefreshing(boolean b) {
        mPullToRefreshView.setRefreshEnable(b); //是否关闭下拉刷新
        mPullToRefreshView.setRefreshing(b);  //是否关闭下拉刷新动画
    }


    /**
     * 关闭正在刷新动画
     */
    private void refreshComplete() {
        mPullToRefreshView.setRefreshing(false);  //关闭下拉刷新动画
        mPullToRefreshView.refreshComplete();//关闭上拉刷新动画
    }


    /**
     * 设置刷新是否可用
     *
     * @param b false:关闭 ,ture：打开
     */
    private void setRefreshEnable(boolean b) {
        mPullToRefreshView.setRefreshEnable(b); //是否关闭下拉刷新
        mPullToRefreshView.setLoadMoreEnable(b);  //是否关闭上拉刷新
    }


    /**
     * 相机拍照
     */
    public void fromCamera() {
        Camera camera = null;
        try {
            camera = Camera.open(0);
            camera.setPreviewDisplay(null);
            camera.startPreview();
            camera.unlock();

            // 拍照事件
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                mFile = ImageUtils.getOutputMediaFile();
                Uri fileUri = Uri.fromFile(mFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(cameraIntent, FROM_CAMERA);
            }
        } catch (RuntimeException e) {
            LogUtils.e(Constant.SDK_UI_TAG, e.toString());
            ToastUtil.showToast(getActivity(), getActivity().getResources().getString(R.string.hx_camera_record_tip));
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

    /**
     * 调取系统相册
     */
    public void fromPhoto() {
        if (Build.VERSION.SDK_INT >= 19) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            String[] mimetypes = {"image/*", "video/*"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
            startActivityForResult(intent, FROM_PHOTO);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.hx_chat_fragment_choose_pic)), FROM_PHOTO);
        }
    }

    /**
     * 相机拍视频
     */
    public void fromMovie() {
        LogUtils.e(Constant.SDK_DATA_TAG, "录制短视频");
        Intent intent = new Intent();
        intent.setClass(getActivity(), MovieRecodeActivity.class);
        startActivity(intent);
    }


    /**
     * 使用系统剪裁图片
     *
     * @param uri 图片资源地址
     */
    public void cutPicture(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");//可以选择图片类型，如果是*表明所有类型的图片
        intent.putExtra("scale", true);//裁剪时是否保留图片的比例
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 10);
        intent.putExtra("aspectY", 16);

        // outputX outputY 是裁剪图片宽高
        //intent.putExtra("outputX", 800);
        //intent.putExtra("outputY", 560);

        intent.putExtra("return-data", true); //   是否将数据保留在Bitmap中返回

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//设置输出的格式
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        startActivityForResult(intent, IMAGE_CUT);
    }

    /**
     * 自定义剪裁图片
     *
     * @param source 图片资源地址
     */
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        //Crop.of(source, destination).asSquare().start(this);
        Crop.of(source, destination).withAspect(10, 16).start(getActivity(), this);
    }


    /**
     * 剪裁结果处理
     *
     * @param result
     */
    private void handleCrop(Intent result) {
        Uri uri = Crop.getOutput(result);
        String path = "";
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
            File file = ImageUtils.saveBitmapToJpg(bitmap);
            path = file.getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            File f = new File(uri.getPath());
            if (null != f && f.exists()) {
                f.delete();
            }
        }

        Intent intent = new Intent(getActivity(), ShowResultActivity.class);
        intent.putExtra("path", path);

        getActivity().startActivityForResult(intent, SettingShowActivity.REQ_SHOW_DETAIL);

        deletePhotoGraph(mCachePath);
    }


    /**
     * 用户图片uri
     *
     * @param file
     * @return uri
     */
    private Uri uriProvider(File file) {
       /* if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Uri photoURI = FileProvider.getUriForFile(getActivity(), authority, file);
            String authority = getActivity().getPackageName() + ".fileprovider";
            return photoURI;*/
        //  } else {
        return Uri.fromFile(file);
        //  }
    }

    private String mCachePath;//拍照原图 --> 删除

    private void deletePhotoGraph(String cachePath) {
        if (null == cachePath) {
            return;
        }
        File file = new File(cachePath);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case FROM_CAMERA:// 当选择拍照时调用
                    String model = android.os.Build.MODEL;
                    switch (model) {
                        case "SM-C5000":  //三星C5000，照相旋转90°
                            rotateImage(mFile);
                            break;
                    }
                    mCachePath = mFile.getAbsolutePath();
                    //cutPictrue(uriProvider(mFile));
                    beginCrop(Uri.fromFile(mFile));
                    break;
                case FROM_PHOTO:// 当选择从本地获取图片时

                    //String mediaUrl = data.getStringExtra("Media");
                    String mediaUrl = AppUtils.getPath(getContext(), data.getData());
                    if (StringUtils.isEmpty(mediaUrl)) {
                        ToastUtil.showBottomToast(getContext(), "文件不存在");
                        return;
                    }
                    if (mediaUrl.toLowerCase().contains(".mp4")
                            || mediaUrl.toLowerCase().contains(".3pg")) {

                        String mVTime = MediaStoreUtils.getVideoParams(mediaUrl)[0];
                        Log.e("YW", "mVTime: " + mVTime);
                        if (StringUtils.isEmpty(mVTime) || Float.parseFloat(mVTime.trim()) / 1000 > 16) {
                            ToastUtil.showToast(getContext(), "时间超长，请选择小于15秒的视频");
                            return;
                        }
                        long size = new File(mediaUrl).length();
                        if (size > 50 * 1024 * 1024) {
                            ToastUtil.showToast(getContext(), "文件过大， 请选择小于50M");
                            return;
                        }

                        Intent intent = new Intent(getActivity(), ShowResultActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("isLocal", true);
                        intent.putExtra(ShowResultActivity.RECORD_MOVIE_PATH, mediaUrl);//录制路径
                        startActivity(intent);
                    } else if (mediaUrl.toLowerCase().contains(".jpeg")
                            || mediaUrl.toLowerCase().contains(".jpg")
                            || mediaUrl.toLowerCase().contains(".png")) {
                        beginCrop(data.getData());
                    } else if (mediaUrl.toLowerCase().contains(".bmp")
                            || (mediaUrl.toLowerCase().contains(".gif"))) {
                        cutPicture(uriProvider(new File(mediaUrl)));
                    } else {
                        ToastUtil.showBottomToast(getContext(), "暂不支持该文件格式");
                    }

                    //File urifile = new File(media);
                    //cutPicture(uriProvider(urifile));
                    break;
                case IMAGE_CUT:// 返回的结果
                    Bitmap bitmap;
                    bitmap = data.getParcelableExtra("data");
                    if (bitmap == null) {
                        Uri uri = data.getData();
                        try {
                            bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    File file = ImageUtils.saveBitmapToJpg(bitmap);
                    String path = file.getPath();
                    Intent intent = new Intent(getActivity(), ShowResultActivity.class);
                    intent.putExtra("path", path);
                    startActivity(intent);
                    getActivity().finish();
                    break;
                case Crop.REQUEST_PICK:
                    break;
                case Crop.REQUEST_CROP:
                    handleCrop(data);
                    break;
                case Crop.RESULT_ERROR:
                    Toast.makeText(getActivity(), Crop.getError(data).getMessage(), Toast.LENGTH_SHORT).show();
                    break;

            }
        }

    }


    /**
     * 照片旋转90°
     */
    private void rotateImage(File file) {
        String path = file.getAbsolutePath();
        Bitmap bmp = BitmapFactory.decodeFile(path);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(mFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class PaddingItemDecoration extends RecyclerView.ItemDecoration {

        private int mSpace;

        private PaddingItemDecoration(int space) {
            mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = mSpace;
            outRect.top = mSpace;
            outRect.right = mSpace;
            outRect.bottom = mSpace;
        }
    }


}
