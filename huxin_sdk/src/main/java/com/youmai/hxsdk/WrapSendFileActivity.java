package com.youmai.hxsdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.qiniu.android.storage.UpProgressHandler;
import com.youmai.hxsdk.db.bean.ChatMsg;
import com.youmai.hxsdk.im.IMHelper;
import com.youmai.hxsdk.interfaces.IFileSendListener;
import com.youmai.hxsdk.interfaces.impl.FileSendListenerImpl;
import com.youmai.hxsdk.module.filemanager.PickerManager;
import com.youmai.hxsdk.module.filemanager.interfaces.PickerRefreshUIListener;
import com.youmai.hxsdk.module.filemanager.activity.FileManagerActivity;
import com.youmai.hxsdk.picker.FilePickerBuilder;
import com.youmai.hxsdk.module.filemanager.constant.FilePickerConst;
import com.youmai.hxsdk.utils.CommonUtils;
import com.youmai.hxsdk.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;

import static com.youmai.hxsdk.activity.IMConnectionActivity.MAX_SENDER_FILE;


/**
 * Created by Kevin on 2016/12/5.
 */

public class WrapSendFileActivity extends FragmentActivity implements PickerRefreshUIListener {
    public static final int TYPE_PIC = 0;
    public static final int TYPE_FILE = 1;

    private String dstPhone;

    private ArrayList<String> docPaths = new ArrayList<>();
    private ArrayList<String> photoPaths = new ArrayList<>();

    private int type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dstPhone = getIntent().getStringExtra("dstPhone");
        type = getIntent().getIntExtra("type", TYPE_PIC);

        HuxinSdkManager.instance().getStackAct().addActivity(this);
        if (type == TYPE_PIC) {
            showPicChooser();
        } else if (type == TYPE_FILE) {
            showFileChooser();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PickerManager.getInstance().setRefreshUIListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HuxinSdkManager.instance().getStackAct().finishActivity(this);
        //PickerManager.getInstance().setRefreshUIListener(null);
    }

    /**
     * 发送图片
     */
    private void showPicChooser() {
        photoPaths.clear();
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setSelectedFiles(photoPaths)
                .setActivityTheme(R.style.HxSdkTheme)
                //.addVideoPicker()
                .enableCameraSupport(true)
                //.showGifs(true)
                .showFolderView(true)
                .pickPhoto(this);
    }


    private void showFileChooser() {
        docPaths.clear();

//        String[] docs = {".txt", ".docx", ".doc", ".ppt", ".pptx", ".xls", ".xlsx", ".pdf"};
//        String[] videos = {".mp4"/*, ".rmvb", ".avi", ".3gp"*/};
//        String[] audios = {".mp3"};
//        String[] zips = {".zip", ".rar"};
//        //String[] apks = {".apk"};
//
//
//        FilePickerBuilder.getInstance().setMaxCount(1)
//                .setSelectedFiles(docPaths)
//                .setActivityTheme(R.style.HxSdkTheme)
//                .addFileSupport(getString(R.string.doc), docs)
//                .addFileSupport(getString(R.string.video), videos)
//                .addFileSupport(getString(R.string.audio), audios)
//                .addFileSupport(getString(R.string.zip), zips)
//                //.addFileSupport(getString(R.string.apk), apks)
//                .enableDocSupport(false)
//                .pickFile(this);

        PickerManager.getInstance().addDocTypes();
        Intent intent = new Intent(this, FileManagerActivity.class);
        intent.putExtra("dstPhone", dstPhone);
        intent.putExtra(FileManagerActivity.REQUEST_CODE_CALLBACK, FilePickerConst.CALL_REQUEST_CALLBACK);
        startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {

//            case FilePickerConst.REQUEST_CODE_PHOTO:
//                if (resultCode == Activity.RESULT_OK && data != null) {
//
//                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
//
//                    if (photoPaths != null && photoPaths.size() > 0) {
//                        String path = photoPaths.get(0);
//                        String imagePath = CompressImage.compressImage(path);
//
//                        File file = new File(imagePath);
//                        if (!file.exists()) {
//                            return;
//                        }
//
//                        int userId = HuxinSdkManager.instance().getUserId();
//                        HuxinSdkManager.instance().postPicture(userId, dstPhone, file, imagePath,
//                                true, FileSendListenerImpl.getListener());
//                    }
//                }
//                break;
//            case FilePickerConst.REQUEST_CODE_DOC:
//                if (resultCode == Activity.RESULT_OK && data != null) {
//                    finish();
////                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
////                    if (docPaths != null && docPaths.size() > 0) {
////                        final File file = new File(docPaths.get(0));
////                        if (file.exists()) {
////                            if (file.length() > MAX_SENDER_FILE) {
////                                Toast.makeText(getApplicationContext(), R.string.hx_imadapter_file, Toast.LENGTH_SHORT).show();
////                            } else {
////                                if (!CommonUtils.isNetworkAvailable(getApplicationContext())) {
////                                    ToastUtil.showToast(this, getString(R.string.hx_imadapter_wifi_break));
////                                    finish();
////                                    return;
////                                }
////
////                                if (!IMHelper.isWifi(getApplicationContext())) {
////                                    Toast.makeText(getApplicationContext(), getString(R.string.hx_toast_67), Toast.LENGTH_SHORT).show();
////                                }
////                                handleSendFile(file);
////                            }
////                        } else {
////                            Toast.makeText(getApplicationContext(), getString(R.string.hx_toast_22), Toast.LENGTH_SHORT).show();
////                        }
////                    }
//                }
//                break;
//        }
//
//
//        FloatViewUtil.instance().showFloatViewDelay(getApplicationContext());
//        finish();
    }


    /**
     * 发送文件.
     *
     * @param file
     */
    public void handleSendFile(final File file) {
        final int userId = HuxinSdkManager.instance().getUserId();
        final IFileSendListener listener = FileSendListenerImpl.getListener();
        //发送 userId, desPhone, fileId, fileName, fileSize, receiveListener
        HuxinSdkManager.instance().postBigFile(userId, dstPhone, file,
                file.getName(), file.length() + "",
                new UpProgressHandler() {
                    @Override
                    public void progress(String key, double percent) {
                        listener.onProgress(ChatMsg.MsgType.BIG_FILE.ordinal(), percent, "file");
                    }
                }, true, listener);
        finish();
    }

    @Override
    public void onRefresh(ArrayList<String> paths, int resultCode) {
        Log.e("YW", "onrefresh" + "\tresultCode: " + resultCode);

        if (paths != null && paths.size() > 0 && resultCode == FilePickerConst.CALL_REQUEST_CALLBACK) {
            final File file = new File(paths.get(0));
            if (file.exists()) {
                if (file.length() > MAX_SENDER_FILE) {
                    Toast.makeText(getApplicationContext(), R.string.hx_imadapter_file, Toast.LENGTH_SHORT).show();
                } else {
                    if (!CommonUtils.isNetworkAvailable(getApplicationContext())) {
                        ToastUtil.showToast(this, getString(R.string.hx_imadapter_wifi_break));
                        finish();
                        return;
                    }

                    if (!IMHelper.isWifi(getApplicationContext())) {
                        Toast.makeText(getApplicationContext(), getString(R.string.hx_toast_67), Toast.LENGTH_SHORT).show();
                    }
                    handleSendFile(file);
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.hx_toast_22), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
