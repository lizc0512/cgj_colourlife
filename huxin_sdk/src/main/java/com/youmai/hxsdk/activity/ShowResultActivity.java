package com.youmai.hxsdk.activity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.SettingShowActivity;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.db.bean.HxUsers;
import com.youmai.hxsdk.db.helper.HxUsersHelper;
import com.youmai.hxsdk.dialog.HxTipDialog;
import com.youmai.hxsdk.entity.UploadFile;
import com.youmai.hxsdk.entity.UserInfo;
import com.youmai.hxsdk.entity.UserShowResult;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.module.movierecord.MediaStoreUtils;
import com.youmai.hxsdk.utils.AbFileUtil;
import com.youmai.hxsdk.utils.AbImageUtil;
import com.youmai.hxsdk.utils.CommonUtils;
import com.youmai.hxsdk.utils.DeviceUtils;
import com.youmai.hxsdk.utils.FileUtils;
import com.youmai.hxsdk.utils.GlideCircleTransform;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.LanguageUtil;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.PhoneImsi;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.utils.ToastUtil;
import com.youmai.hxsdk.utils.VirtualKeyUtils;
import com.youmai.hxsdk.view.progressbar.RoundProgressBarWithArrows;
import com.youmai.smallvideorecord.JianXiCamera;
import com.youmai.smallvideorecord.LocalMediaCompress;
import com.youmai.smallvideorecord.model.AutoVBRMode;
import com.youmai.smallvideorecord.model.BaseMediaBitrateConfig;
import com.youmai.smallvideorecord.model.LocalMediaConfig;
import com.youmai.smallvideorecord.model.OnlyCompressOverBean;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * 作者：create by YW
 * 日期：2016.08.23 19:54
 * 描述：
 */
public class ShowResultActivity extends SdkBaseActivity implements View.OnClickListener {

    public static final String RECORD_MOVIE_PATH = "recordMoviePath";//视频路径path
    public static final String VIDEO_SCREENSHOT = "video_screenshot";

    private ProgressDialog mProgressDialog;
    private TextView tvSetup;
    private LinearLayout mLlParentProgress;
    private RoundProgressBarWithArrows mRpbProgress;
    private TextView mTvProgress;
    private FrameLayout fl_show_parent;

    private String url;
    private String path;
    private String moviePath;
    private int id;

    private File mThumbFile;//视频封面图
    private String mVTime;//视频时长

    private Context mContext;
    private boolean isLocal;//本地选择的视频
    private ShowHandler showHandler = new ShowHandler(this);

    private File newVideoFile, newPicFile;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_setting_show_fragment);
        mContext = this;

        url = getIntent().getStringExtra("url");
        path = getIntent().getStringExtra("path");
        id = getIntent().getIntExtra("id", -1);
        isLocal = getIntent().getBooleanExtra("isLocal", false);
        moviePath = getIntent().getStringExtra(RECORD_MOVIE_PATH);
        LogUtils.e(Constant.SDK_DATA_TAG, "视频录制path = " + moviePath);

        TextView tvBack = (TextView) findViewById(R.id.tv_back);
        tvBack.setOnClickListener(this);
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(R.string.hx_toolbar_preview);

        tvSetup = (TextView) findViewById(R.id.tv_setup);
        tvSetup.setOnClickListener(this);

        mLlParentProgress = (LinearLayout) findViewById(R.id.ll_parent_progress);
        mRpbProgress = (RoundProgressBarWithArrows) findViewById(R.id.rpb_progress);
        mTvProgress = (TextView) findViewById(R.id.tv_progress);

        fl_show_parent = (FrameLayout) findViewById(R.id.fl_show_parent);

        ImageView ivShowHeader = (ImageView) findViewById(R.id.iv_show_header);
        String phone = HuxinSdkManager.instance().getPhoneNum();
        HxUsers user = HxUsersHelper.getHxUser(this, phone);
        if (user != null && user.getIconUrl() != null) {
            Glide.with(this)
                    .load(user.getIconUrl())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .circleCrop()
                            .error(R.drawable.hx_show_preview_header_bg))
                    .into(ivShowHeader);
        }


        final ImageView imgShow = (ImageView) findViewById(R.id.img_show);
        if (!StringUtils.isEmpty(url)) { //模板
            Glide.with(mContext)
                    .load(url)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).centerCrop()
                            .placeholder(R.drawable.hx_show_default_full)  //占位图片
                            .error(R.drawable.hx_show_default_full))
                    .into(imgShow);

            fl_show_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShowResultActivity.this, ShowPreviewActivity.class);
                    intent.putExtra(ShowPreviewActivity.FID, "");
                    intent.putExtra(ShowPreviewActivity.PBL_PHONE, HuxinSdkManager.instance().getPhoneNum());
                    intent.putExtra(ShowPreviewActivity.FILE_TYPE, "0");
                    intent.putExtra(ShowPreviewActivity.LOCAL_URL, url);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                                ShowResultActivity.this, fl_show_parent, "ishow").toBundle());
                    } else {
                        startActivity(intent);
                    }
                }
            });
        } else if (!StringUtils.isEmpty(path)) { //本地图片(拍照或选择)
            Glide.with(mContext)
                    .load(new File(path))
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).centerCrop()
                            .placeholder(R.drawable.hx_show_default_full)  //占位图片
                            .error(R.drawable.hx_show_default_full))
                    .into(imgShow);

            fl_show_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShowResultActivity.this, ShowPreviewActivity.class);
                    intent.putExtra(ShowPreviewActivity.FID, "");
                    intent.putExtra(ShowPreviewActivity.PBL_PHONE, HuxinSdkManager.instance().getPhoneNum());
                    intent.putExtra(ShowPreviewActivity.FILE_TYPE, "0");
                    intent.putExtra(ShowPreviewActivity.LOCAL_URL, path);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                                ShowResultActivity.this, fl_show_parent, "ishow").toBundle());
                    } else {
                        startActivity(intent);
                    }
                }
            });
        } else if (!StringUtils.isEmpty(moviePath)) { //视频

            if (isLocal) {

                initSmallVideo();

                BaseMediaBitrateConfig compressMode = new AutoVBRMode();
                String sRate = ""; //视频帧率（默认为原视频）
                String scale = "2.5"; //缩放视频比例，为浮点型，大于1有效
                int iRate = 0;
                float fScale = 0;
                if (!TextUtils.isEmpty(sRate)) {
                    iRate = Integer.valueOf(sRate);
                }
                if (!TextUtils.isEmpty(scale)) {
                    fScale = Float.valueOf(scale);
                }
                LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
                final LocalMediaConfig config = buidler
                        .setVideoPath(moviePath)
                        .captureThumbnailsTime(1)
                        .doH264Compress(compressMode)
                        .setFramerate(iRate)
                        .setScale(fScale)
                        .build();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("YW", "开始压缩");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showProgress("", "视频压缩中...", -1);
                            }
                        });

                        final OnlyCompressOverBean mOnlyCompressOverBean = new LocalMediaCompress(config).startCompress(false);

                        newVideoFile = AbFileUtil.renameVideoTo(mOnlyCompressOverBean.getVideoPath());
                        newPicFile = AbFileUtil.renamePicTo(mOnlyCompressOverBean.getPicPath());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgress();
                                VideoView vv_video_show = (VideoView) findViewById(R.id.vv_video_show);
                                imgShow.setVisibility(View.GONE);
                                vv_video_show.setVisibility(View.VISIBLE);
                                vv_video_show.setVideoPath(newVideoFile.getAbsolutePath());
                                vv_video_show.start();

                                mVTime = MediaStoreUtils.getVideoParams(newVideoFile.getAbsolutePath())[0];

                                vv_video_show.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mp.start();
                                        mp.setLooping(true);
                                    }
                                });

                                fl_show_parent.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ShowResultActivity.this, ShowPreviewActivity.class);
                                        intent.putExtra(ShowPreviewActivity.FID, "");
                                        intent.putExtra(ShowPreviewActivity.PBL_PHONE, HuxinSdkManager.instance().getPhoneNum());
                                        intent.putExtra(ShowPreviewActivity.FILE_TYPE, "1");
                                        intent.putExtra(ShowPreviewActivity.LOCAL_URL, newVideoFile.getAbsolutePath());
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                                                    ShowResultActivity.this, fl_show_parent, "ishow").toBundle());
                                        } else {
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }
                        });
                    }
                }).start();
            } else {
                VideoView vv_video_show = (VideoView) findViewById(R.id.vv_video_show);
                imgShow.setVisibility(View.GONE);
                vv_video_show.setVisibility(View.VISIBLE);
                vv_video_show.setVideoPath(moviePath);
                vv_video_show.start();

                vv_video_show.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        mp.setLooping(true);
                    }
                });

                //TODO: 拿视频封面图 && 视频总时间长
                //获取视频时长
                mVTime = MediaStoreUtils.getVideoParams(moviePath)[0];
                //生成缩略图
                mThumbFile = getCreateThumb(new File(moviePath));

                fl_show_parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ShowResultActivity.this, ShowPreviewActivity.class);
                        intent.putExtra(ShowPreviewActivity.FID, "");
                        intent.putExtra(ShowPreviewActivity.PBL_PHONE, HuxinSdkManager.instance().getPhoneNum());
                        intent.putExtra(ShowPreviewActivity.FILE_TYPE, "1");
                        intent.putExtra(ShowPreviewActivity.LOCAL_URL, moviePath);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(
                                    ShowResultActivity.this, fl_show_parent, "ishow").toBundle());
                        } else {
                            startActivity(intent);
                        }
                    }
                });
            }

        }

        /***********************适配show***********************/
        boolean hasVirtualKey = VirtualKeyUtils.hasSoftKeys((WindowManager) getSystemService(Context.WINDOW_SERVICE));
        RelativeLayout rl_show_parent = (RelativeLayout) findViewById(R.id.rl_show_parent);
        if (hasVirtualKey) {
            rl_show_parent.setLayoutParams(new LinearLayout.LayoutParams(-2, (int) getResources().getDimension(R.dimen.hx_setting_show_height_virtual_key)));
        } else {
            rl_show_parent.setLayoutParams(new LinearLayout.LayoutParams(-2, (int) getResources().getDimension(R.dimen.hx_setting_show_height)));
        }
        /*****************************************************/

        HuxinSdkManager.instance().getStackAct().addActivity(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (!StringUtils.isEmpty(path)) { // FIXME: 2017/1/16 删除SD卡缓存图片秀
            File picFile = new File(path);
            if (null != picFile && picFile.exists()) {
                picFile.delete();
            }
        }
        if (null != mThumbFile && mThumbFile.exists()) { // FIXME: 2017/1/16 删除SD卡的视频封面
            mThumbFile.delete();
        }
        if (!StringUtils.isEmpty(moviePath) && !isLocal) { // FIXME: 2017/1/16 删除SD卡的缓存视频秀
            File movieFile = new File(moviePath);
            if (null != movieFile && movieFile.exists()) {
                movieFile.delete();
            }
        }
        if (isLocal && newVideoFile != null) { // FIXME: 2017/1/16 本地删除SD卡的缓存视频秀
            File videoFile = newVideoFile;
            File picFile = newPicFile;
            if (videoFile != null) {
                videoFile.delete();
            }
            if (picFile != null) {
                picFile.delete();
            }
            File patentFile = new File(videoFile.getParent());
            if (patentFile != null) {
                patentFile.delete();
            }
        }

        isLocal = false;

        HuxinSdkManager.instance().getStackAct().finishActivity(this);

    }

    private void setEnabled(boolean b) {
        if (b) {
            tvSetup.setText(R.string.hx_show_setup);
        } else {
            tvSetup.setText(R.string.hx_show_used);
        }
        tvSetup.setEnabled(b);
    }

    /**
     * 生成缩略图.
     *
     * @param file
     * @return
     */
    private File getCreateThumb(File file) {
        File thumbFile = new File(file.getParent(), FileUtils.getNameByType(file.getName(), "jpg"));
        //获取视频第一帧
        Bitmap bitmap = MediaStoreUtils.getBitmapFromVideo(file.getAbsolutePath());

        if (bitmap != null) {
            //保存到新路径
            AbImageUtil.saveJPEGBmp(bitmap, thumbFile.getAbsolutePath());
            bitmap.recycle();
        }
        return thumbFile;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_setup) {
            reqShowData("0");
        } else if (id == R.id.tv_back) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*if (tvSetup.getText().equals("应用中")) { //返回应用成功
            if (!StringUtils.isEmpty(url) || !StringUtils.isEmpty(path)) {
                setResult(SettingShowActivity.RES_SHOW_APPLY_SUCCESS);
            } else if (!StringUtils.isEmpty(moviePath)) {
                SPDataUtil.setVideoRefresh(ShowResultActivity.this, true);
            }
        }*/
        finish();
    }

    static class ShowHandler extends Handler {
        WeakReference<ShowResultActivity> weakReference;

        public ShowHandler(ShowResultActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ShowResultActivity showResultActivity = weakReference.get();
            if (showResultActivity == null) {
                return;
            }
            if (msg.what == 0) {
                showResultActivity.setResult(SettingShowActivity.RES_SHOW_APPLY_SUCCESS);
                showResultActivity.finish();
                boolean has = HuxinSdkManager.instance().getStackAct().hasActivity("com.youmai.hxsdk.SettingShowActivity");
                if (has) {
                    HuxinSdkManager.instance().getStackAct().finishActivity(SettingShowActivity.class);
                }
                ToastUtil.showPublicToast(showResultActivity, "设置成功", R.layout.hx_public_toast_tip, R.id.public_toast_message);
            } else if (msg.what == 1) {
                //fail restore all view
                showResultActivity.mLlParentProgress.setVisibility(View.GONE);
            }
        }
    }

    private void setModelUserShow() {

        int id = getIntent().getIntExtra("id", 0);
        final String fid = getIntent().getStringExtra("fid");
        int fileType = getIntent().getIntExtra("fileType", 0);
        final String phoneNum = HuxinSdkManager.instance().getPhoneNum();
        String imei = DeviceUtils.getIMEI(this);

        ContentValues header = new ContentValues();
        header.put("id", id);
        header.put("fid", fid);

        header.put("msisdn", phoneNum);
        header.put("termid", imei);
        header.put("sign", AppConfig.appSign(phoneNum, imei));

        header.put("fileType", fileType);
        header.put("v", AppConfig.V);

        header.put("mcc", PhoneImsi.getMCC(mContext));
        if (!LanguageUtil.isCN(mContext)) {
            header.put("lang", "en");
        }

        mRpbProgress.setProgress(95);
        mTvProgress.setText("95%");
        mLlParentProgress.setVisibility(View.VISIBLE);

        IGetListener listener = new IGetListener() {
            @Override
            public void httpReqResult(String response) {

                UserShowResult resp = GsonUtil.parse(response, UserShowResult.class);
                if (resp == null) {
                    showHandler.sendEmptyMessage(1);
                }
                if (resp != null && resp.isSucess()) {
                    setEnabled(false);

                    /*if (!SPDataUtil.getIsFirstShowGuideDial(ShowResultActivity.this)) { //打给自己
                        ShowTipTwoDialog dialog = new ShowTipTwoDialog(mContext);
                        ShowTipTwoDialog.HxCallback callback = new ShowTipTwoDialog.HxCallback() {
                            @Override
                            public void onJumpToDial() {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                                        + HuxinSdkManager.instance().getPhoneNum()));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        };
                        dialog.setHxSetShowListener(callback);
                        dialog.show();
                        SPDataUtil.setIsFirstShowGuideDial(ShowResultActivity.this, true);
                        SPDataUtil.setFirstShowFid(ShowResultActivity.this, fid);
                        SPDataUtil.setFirstShowPFid(ShowResultActivity.this, "");
                    } else*/
//                    {
//                        //返回应用成功
//                        setResult(SettingShowActivity.RES_SHOW_APPLY_SUCCESS);
//                        finish();
//                    }


                    mRpbProgress.setProgress(100);
                    mTvProgress.setText("100%");
                    showHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mLlParentProgress.setVisibility(View.GONE);
                            showHandler.sendEmptyMessage(0);
                        }
                    }, 1200);
                } else if (resp != null && resp.isFailOrNull()) {
                    ToastUtil.showToast(ShowResultActivity.this, resp.getM());
                    showHandler.sendEmptyMessage(1);
                } else if (resp != null && resp.equals("-200")) {
                    Toast.makeText(ShowResultActivity.this, resp.getM(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        HuxinSdkManager.instance().setShowModel(id, fid, fileType, listener);
    }

    private void setMakeUserShow() {
        UpProgressHandler progressHandler = new UpProgressHandler() {
            @Override
            public void progress(String key, double progress) {
                LogUtils.e(Constant.SDK_UI_TAG, "progressHandler percent = " + progress);

                mRpbProgress.setProgress((int) Math.abs(progress * 100));
                mTvProgress.setText((int) Math.abs(progress * 100) + "%");

                if ((int) Math.abs(progress * 100) == 100) {
                    showHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mLlParentProgress.setVisibility(View.GONE);
                            showHandler.sendEmptyMessage(0);
                        }
                    }, 1200);
                } else {
                    mLlParentProgress.setVisibility(View.VISIBLE);
                }
            }
        };

        UpCompletionHandler completionHandler = new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (response == null) {
                    return;
                }
                UploadFile resp = GsonUtil.parse(response.toString(), UploadFile.class);
                if (resp == null) {
                    return;
                }
                if (resp.isSucess()) {
                    final String fileid = resp.getD().getFileid();

                    IGetListener listener = new IGetListener() {
                        @Override
                        public void httpReqResult(String response) {
                            UserShowResult resp = GsonUtil.parse(response, UserShowResult.class);
                            if (resp == null) {
                                showHandler.sendEmptyMessage(1);
                            }

                            if (resp != null && resp.isSucess()) {
                                setEnabled(false);

                                /*if (!SPDataUtil.getIsFirstShowGuideDial(ShowResultActivity.this)) { //打给自己
                                    ShowTipTwoDialog dialog = new ShowTipTwoDialog(mContext);
                                    ShowTipTwoDialog.HxCallback callback = new ShowTipTwoDialog.HxCallback() {
                                        @Override
                                        public void onJumpToDial() {
                                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                                                    + HuxinSdkManager.instance().getPhoneNum()));
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    };
                                    dialog.setHxSetShowListener(callback);
                                    dialog.show();
                                    SPDataUtil.setIsFirstShowGuideDial(ShowResultActivity.this, true);
                                    SPDataUtil.setFirstShowFid(ShowResultActivity.this, fileid);
                                    SPDataUtil.setFirstShowPFid(ShowResultActivity.this, "");
                                } else*/
                                //{
                                //返回应用成功
                                //setResult(SettingShowActivity.RES_SHOW_APPLY_SUCCESS);
                                //finish();
                                //}
                                //return;
                            } else if (resp != null && resp.isFailOrNull()) {
                                ToastUtil.showToast(ShowResultActivity.this, resp.getM());
                                showHandler.sendEmptyMessage(1);
                            } else if (resp.equals("-200")) {
                                Toast.makeText(ShowResultActivity.this, resp.getM(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    };
                    HuxinSdkManager.instance().setShowPic(fileid, listener);

                } else {
                    showHandler.sendEmptyMessage(1);
                    ToastUtil.showToast(ShowResultActivity.this, resp.getM());
                }
            }
        };
        HuxinSdkManager.instance().postShow(new File(path), completionHandler, progressHandler);
    }

    private void setMakeMovieShow() {

        mRpbProgress.setProgress(1);
        mTvProgress.setText("1%");
        mLlParentProgress.setVisibility(View.VISIBLE);

        UpCompletionHandler completionHandler = new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (response == null) {
                    showHandler.sendEmptyMessage(1);
                    Toast.makeText(ShowResultActivity.this, R.string.hx_apply_show_fail, Toast.LENGTH_SHORT).show();
                    return;
                }

                UploadFile resp = GsonUtil.parse(response.toString(), UploadFile.class);
                if (resp == null) {
                    showHandler.sendEmptyMessage(1);
                    return;
                }
                if (resp.isSucess()) {
                    final int pFid = Integer.parseInt(resp.getD().getFileid()); //拿取封面图fid

                    UpProgressHandler progressHandler = new UpProgressHandler() {
                        @Override
                        public void progress(String key, double progress) {
                            Log.e(Constant.SDK_UI_TAG, "progressHandler percent = " + progress);

                            mRpbProgress.setProgress((int) Math.abs(progress * 100));
                            mTvProgress.setText((int) Math.abs(progress * 100) + "%");

                            if ((int) Math.abs(progress * 100) == 100) {
                                showHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mLlParentProgress.setVisibility(View.GONE);
                                        showHandler.sendEmptyMessage(0);
                                    }
                                }, 1200);
                            } else {
                                mLlParentProgress.setVisibility(View.VISIBLE);
                            }
                        }
                    };

                    UpCompletionHandler mHandler = new UpCompletionHandler() { //设置应用视频秀
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject res) {
                            if (res == null) {
                                showHandler.sendEmptyMessage(1);
                                Toast.makeText(ShowResultActivity.this, R.string.hx_apply_show_fail, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            UploadFile resp = GsonUtil.parse(res.toString(), UploadFile.class);
                            if (resp == null) {
                                showHandler.sendEmptyMessage(1);
                                return;
                            }
                            if (resp.isSucess()) {
                                final String fid = resp.getD().getFileid();
                                IGetListener listener = new IGetListener() {
                                    @Override
                                    public void httpReqResult(String response) {
                                        UserShowResult resp = GsonUtil.parse(response, UserShowResult.class);
                                        if (resp == null) {
                                            showHandler.sendEmptyMessage(1);
                                        }

                                        if (resp != null && resp.isSucess()) {
                                            setEnabled(false);
                                            /*if (!SPDataUtil.getIsFirstShowGuideDial(ShowResultActivity.this)) { //打给自己
                                                ShowTipTwoDialog dialog = new ShowTipTwoDialog(mContext);
                                                ShowTipTwoDialog.HxCallback callback = new ShowTipTwoDialog.HxCallback() {
                                                    @Override
                                                    public void onJumpToDial() {
                                                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                                                                + HuxinSdkManager.instance().getPhoneNum()));
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                    }
                                                };
                                                dialog.setHxSetShowListener(callback);
                                                dialog.show();
                                                SPDataUtil.setIsFirstShowGuideDial(ShowResultActivity.this, true);
                                                SPDataUtil.setFirstShowFid(ShowResultActivity.this, pFid + "");//fid
                                                //SPDataUtil.setFirstShowPFid(ShowResultActivity.this, pFid + "");
                                            } else*/
                                            //{
                                            //返回应用成功
                                            //setResult(SettingShowActivity.RES_SHOW_APPLY_SUCCESS);
                                            //finish();
                                            //}
                                            //return;
                                        } else if (resp != null && resp.getS().equals("0")) {
                                            ToastUtil.showToast(ShowResultActivity.this, resp.getM());
                                            showHandler.sendEmptyMessage(1);
                                        } else if (resp.equals("-200")) {
                                            Toast.makeText(ShowResultActivity.this, resp.getM(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                };

                                HuxinSdkManager.instance().setShowVideo(fid, pFid, mVTime, listener);

                            } else {
                                showHandler.sendEmptyMessage(1);
                                ToastUtil.showToast(ShowResultActivity.this, resp.getM());
                            }
                        }
                    };
                    HuxinSdkManager.instance().postShow((isLocal ? new File(newVideoFile.getAbsolutePath()) : new File(moviePath)), mHandler, progressHandler);
                }
            }
        };
        HuxinSdkManager.instance().postShow((isLocal ? new File(newPicFile.getAbsolutePath()) : mThumbFile), completionHandler, null);
    }


    private void reqShowData(String version) {
        if (!CommonUtils.isNetworkAvailable(this)) {
            ToastUtil.showToast(this, mContext.getString(R.string.hx_network_exception_check));
            return;
        }

        if (isLocal && newVideoFile == null) {
            ToastUtil.showToast(this, "视频文件压缩中...");
            return;
        }

        if (mLlParentProgress.getVisibility() == View.VISIBLE) {
            return;
        }

        final String phone = HuxinSdkManager.instance().getPhoneNum();
        IPostListener listener = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                UserInfo userShow = GsonUtil.parse(response, UserInfo.class);
                if (userShow == null) {
                    if (!StringUtils.isEmpty(url)) {
                        setModelUserShow();
                    } else if (!StringUtils.isEmpty(path)) {
                        setMakeUserShow();
                    } else if (!StringUtils.isEmpty(moviePath)) {
                        setMakeMovieShow();
                    }
                }

                if (userShow != null && userShow.isSuccess()) {
                    UserInfo.DBean userInfo = userShow.getD();
                    String showType = userInfo.getShowType();

                    if (showType.equals("2")) { //商家秀
                        final HxTipDialog hxExitDialog = new HxTipDialog(ShowResultActivity.this);
                        hxExitDialog.show();
                        hxExitDialog.setSureBtnClickListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (!StringUtils.isEmpty(url)) {
                                    setModelUserShow();
                                } else if (!StringUtils.isEmpty(path)) {
                                    //图片应用事件
                                    setMakeUserShow();
                                } else if (!StringUtils.isEmpty(moviePath)) {
                                    //视频应用事件
                                    setMakeMovieShow();
                                }
                            }
                        }).setCancelBtnClickListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //取消
                            }
                        }).setMessage(getResources().getString(R.string.hx_tip_setting_show_text02));

                        hxExitDialog.getCancelBtn().setText(R.string.hx_represent_cancel);
                        hxExitDialog.getSureBtn().setText(R.string.hx_show_setup);
                    } else if (showType.equals("3")) { //代言
                        final HxTipDialog hxExitDialog = new HxTipDialog(ShowResultActivity.this);
                        hxExitDialog.show();
                        hxExitDialog.setSureBtnClickListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (!StringUtils.isEmpty(url)) {
                                    setModelUserShow();
                                } else if (!StringUtils.isEmpty(path)) {
                                    setMakeUserShow();
                                } else if (!StringUtils.isEmpty(moviePath)) {
                                    setMakeMovieShow();
                                }
                            }
                        }).setCancelBtnClickListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //取消
                            }
                        }).setMessage(getResources().getString(R.string.hx_tip_setting_show_text));

                        hxExitDialog.getCancelBtn().setText(R.string.hx_represent_cancel);
                        hxExitDialog.getSureBtn().setText(R.string.hx_show_setup);
                    } else {
                        if (!StringUtils.isEmpty(url)) {
                            setModelUserShow();
                        } else if (!StringUtils.isEmpty(path)) {
                            //图片应用事件
                            setMakeUserShow();
                        } else if (!StringUtils.isEmpty(moviePath)) {
                            //视频应用事件
                            setMakeMovieShow();
                        }
                    }
                } else {
                    if (!StringUtils.isEmpty(url)) {
                        setModelUserShow();
                    } else if (!StringUtils.isEmpty(path)) {
                        setMakeUserShow();
                    } else if (!StringUtils.isEmpty(moviePath)) {
                        setMakeMovieShow();
                    }
                }
            }

        };
        HuxinSdkManager.instance().userInfo(phone, listener);
    }


    public static void initSmallVideo() {
        // 设置拍摄视频缓存路径
        File dcim = Environment.getExternalStorageDirectory();
        if (com.youmai.smallvideorecord.utils.DeviceUtils.isZte()) {
            if (dcim.exists()) {
                JianXiCamera.setVideoCachePath(dcim + "/HuXin/zero/");
            } else {
                JianXiCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/",
                        "/sdcard-ext/") + "/HuXin/zero/");
            }
        } else {
            JianXiCamera.setVideoCachePath(dcim + "/HuXin/zero/");
        }
        // 初始化拍摄
        JianXiCamera.initialize(false, null);
    }

    private void showProgress(String title, String message, int theme) {
        if (mProgressDialog == null) {
            if (theme > 0)
                mProgressDialog = new ProgressDialog(this, theme);
            else
                mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
        }

        if (!com.youmai.smallvideorecord.utils.StringUtils.isEmpty(title))
            mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    private void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

}
