package com.tg.coloursteward;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.module.MainActivity1;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.object.ImageParams;
import com.tg.coloursteward.object.SlideItemObj;
import com.tg.coloursteward.object.ViewConfig;
import com.tg.coloursteward.util.GlideCacheUtil;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.CameraView;
import com.tg.coloursteward.view.CameraView.STATE;
import com.tg.coloursteward.view.MessageArrowView;
import com.tg.coloursteward.view.MessageArrowView.ItemClickListener;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.tg.coloursteward.view.spinnerwheel.SlideSelectorView.OnCompleteListener;
import com.youmai.hxsdk.router.APath;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 个人中心
 */
@Route(path = APath.USER_INFO_ACT)
public class UserInfoActivity extends BaseActivity implements ItemClickListener, OnClickListener {
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int RESULT_REQUEST_CODE = 2;
    private MessageArrowView messageView1;
    private MessageArrowView messageView2;
    private ArrayList<SlideItemObj> genderList;
    private ArrayList<ViewConfig> list1 = new ArrayList<ViewConfig>();
    private ArrayList<ViewConfig> list2 = new ArrayList<ViewConfig>();
    private boolean needPostImage = false;
    private String realname = "";
    private String email = "";
    private String sex = "";
    private String headImgPath;
    private ImageView ivIcon;// 头像
    private RelativeLayout rlIcon;
    private String imageName;
    String crop_path = Environment.getExternalStorageDirectory()
            + "/colorholder/";
    private String updatetime;
    private boolean isSaveHeadImg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageView1 = (MessageArrowView) findViewById(R.id.messageView1);
        messageView2 = (MessageArrowView) findViewById(R.id.messageView2);
        messageView1.setItemClickListener(this);
        messageView2.setItemClickListener(this);
        messageView1.setEditable(true);
        messageView2.setEditable(true);
        initView();
        RequestParams params = new RequestParams();
        params.put("uid", UserInfo.uid);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/account",
                new RequestConfig(this, HttpTools.GET_USER_INFO), params);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        realname = UserInfo.realname;
        String oa=UserInfo.employeeAccount;
        sex = UserInfo.sex;
        email = UserInfo.email;
        int size = (int) (50 * Tools.getDisplayMetrics(this).density);
        rlIcon = (RelativeLayout) findViewById(R.id.rl_icon);
        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        rlIcon.setOnClickListener(this);
        ivIcon.getLayoutParams().width = size;
        ivIcon.getLayoutParams().height = size;
        ivIcon.setScaleType(ScaleType.CENTER_CROP);

        list1.clear();
        ViewConfig config = new ViewConfig("姓名", UserInfo.realname, false);
        config.enable = false;
        config.rightEditable = true;
        list1.add(config);

        config = new ViewConfig("性别", UserInfo.sex, true);
        list1.add(config);
        messageView1.setData(list1);


        list2.clear();
        config = new ViewConfig("部门", UserInfo.familyName, false);
        config.rightEditable = false;
        config.enable = false;
        list2.add(config);

        config = new ViewConfig("职位", UserInfo.jobName, false);
        config.rightEditable = false;
        config.enable = false;
        list2.add(config);

        config = new ViewConfig("手机号码", UserInfo.mobile, false);
        config.rightEditable = false;
        config.enable = false;
        list2.add(config);

        config = new ViewConfig("Email", email, false);
        config.enable = false;
        config.rightEditable = true;
        list2.add(config);
        messageView2.setData(list2);

        updatetime = UserInfo.userinfoImg;
        Tools.saveStringValue(UserInfoActivity.this, "updatetime_img", UserInfo.userinfoImg);
        freshImg();
        messageView1.freshView(0);
    }

    private void freshImg() {
        String url = Contants.URl.HEAD_ICON_URL + "avatar?uid=" + UserInfo.employeeAccount;
        Glide.with(this).load(url)
                .apply(new RequestOptions()
                        .signature(new ObjectKey(Tools.getStringValue(UserInfoActivity.this, "updatetime_img")))
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(ivIcon);
    }

    /**
     * 更新UI
     */
    private void updateView() {
        list1.get(0).rightText = UserInfo.realname;
        list1.get(1).rightText = UserInfo.sex;
        list2.get(3).rightText = UserInfo.email;
        messageView1.freshAll();
        messageView2.freshAll();
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        String jsonObject = HttpTools.getContentString(jsonString);
        ResponseData data = HttpTools.getResponseContentObject(jsonObject);
        if (msg.arg1 == HttpTools.SET_USER_INFO) {
            headView.setRightText("保存");
            messageView1.setEditable(true);
            messageView2.setEditable(true);
            setUserInfo();
            updateView();
            ToastFactory.showToast(this, hintString);
            sendBroadcast(new Intent(MainActivity1.ACTION_FRESH_USERINFO));
            finish();
        } else if (msg.arg1 == HttpTools.GET_USER_INFO) {
            if (Tools.loadUserInfo(data, jsonString)) {
                updateView();
            }
        } else if (msg.arg1 == HttpTools.POST_IMAG) {
            DialogFactory.getInstance().hideTransitionDialog();
            needPostImage = false;
            sendBroadcast(new Intent(MainActivity1.ACTION_FRESH_USERINFO));
            ToastFactory.showToast(this, hintString);
            updatetime = String.valueOf(System.currentTimeMillis());
            UserInfo.userinfoImg = updatetime;
            Tools.saveStringValue(UserInfoActivity.this, "updatetime_img", updatetime);
            isSaveHeadImg = true;
            freshImg();
        }
    }

    private void setUserInfo() {
        UserInfo.realname = realname;
        UserInfo.sex = sex;
        UserInfo.email = email;
        Tools.saveUserInfo(this);
    }

    @Override
    public void returnData(CameraView cv, STATE state, int groupPosition,
                           int childPosition, int position, Bitmap bitmap, String path) {
        super.returnData(cv, state, groupPosition, childPosition, position, bitmap,
                path);
        needPostImage = true;
        Drawable rightDrawable = new BitmapDrawable(bitmap);
        ivIcon.setImageDrawable(rightDrawable);
        if (needPostImage) {
            ImageParams imgParams = new ImageParams();
            imgParams.fileName = imageName;
            imgParams.path = headImgPath;
            HttpTools.postAnImage(Contants.URl.HEAD_ICON_URL, mHand, imgParams);
        } else {
            submitUserInfo();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (resultCode == Activity.RESULT_OK && requestCode == RESULT_REQUEST_CODE) {
                if (data != null) {
                    getImageToView(data);
                }
            } else if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_REQUEST_CODE) {
                Uri uri = data.getData();
                startPhotoZoom(uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        File dir = new File(crop_path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", false);

        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(crop_path, imageName)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, RESULT_REQUEST_CODE);

    }

    /**
     * 上传图片
     *
     * @param data
     */
    private void getImageToView(Intent data) {
        final Bitmap bitmap = BitmapFactory.decodeFile(crop_path
                + imageName);
        String fileName = crop_path + imageName;
        needPostImage = true;
        ivIcon.setImageBitmap(bitmap);
        if (needPostImage) {
            ImageParams imgParams = new ImageParams();
            imgParams.fileName = imageName;
            imgParams.path = crop_path + imageName;
            HttpTools.postAnImage(Contants.URl.HEAD_ICON_URL, mHand, imgParams);
        } else {
            submitUserInfo();
        }
    }

    @Override
    public void onItemClick(MessageArrowView mv, View v, int position) {
        // TODO Auto-generated method stub
        if (mv == messageView1) {
            if (position == 1) {
                if (genderList == null) {
                    genderList = new ArrayList<SlideItemObj>();
                    genderList.add(new SlideItemObj("男", "0"));
                    genderList.add(new SlideItemObj("女", "1"));
                }
                DialogFactory.getInstance().showSelectorDialog(this,
                        "选择性别", genderList, null, new OnCompleteListener() {
                            @Override
                            public void onComplete(SlideItemObj item1, SlideItemObj item2) {
                                if (item1 != null) {
                                    sex = item1.name;
                                    list1.get(1).rightText = item1.name;
                                    messageView1.freshView(1);
                                }
                            }
                        }, false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_layout) {
            if (hasChanged()) {//已经修改过信息
                DialogFactory.getInstance().showDialog(UserInfoActivity.this, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }, null, "信息还没保存，确定要返回吗？", null, null);
            } else {
                finish();
            }
        } else if (v.getId() == R.id.rl_icon) {
            showFileChooser();
        } else {
            if (messageView1.isEditable() || messageView2.isEditable()) {
                submitUserInfo();
            } else {
                headView.setRightText("保存");
                messageView1.setEditable(true);
                messageView2.setEditable(true);
            }
        }
    }

    /**
     * 选择图片上传
     */
    private void showFileChooser() {
        imageName = getNowTime() + ".png";
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    private void submitUserInfo() {
        if (!hasChanged()) {
            headView.setRightText("保存");
            messageView1.setEditable(true);
            messageView2.setEditable(true);
            if (isSaveHeadImg == true) {
                ToastFactory.showToast(UserInfoActivity.this, "头像已保存");
                GlideCacheUtil.getInstance().clearImageDiskCache(UserInfoActivity.this);
                GlideCacheUtil.getInstance().clearImageMemoryCache(UserInfoActivity.this);
            }
            return;
        }
        RequestConfig config = new RequestConfig(this, HttpTools.SET_USER_INFO);
        RequestParams params = new RequestParams
                ("employeeAccount", UserInfo.employeeAccount).
                put("realname", realname).
                put("sex", sex).
                put("mail", email);
        config.hintString = "修改个人信息";
        HttpTools.httpPut(Contants.URl.URL_ICETEST, "/account", config, params);
    }

    private boolean hasChanged() {
        realname = messageView1.getRightTextString(0);
        sex = messageView1.getRightTextString(1);
        email = messageView2.getRightTextString(3);
        if (!TextUtils.equals(realname, UserInfo.realname)) {
            return true;
        }
        if (!TextUtils.equals(sex, UserInfo.sex)) {
            return true;
        }
        return !TextUtils.equals(email, UserInfo.email);
    }

    @Override
    public void onBackPressed() {
        backPress();
    }

    protected void backPress() {
        if (hasChanged()) {//已经修改过信息
            DialogFactory.getInstance().showDialog(UserInfoActivity.this, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserInfoActivity.this.finish();
                }
            }, null, "信息还没保存，确定要返回吗？", null, null);
        } else {
            UserInfoActivity.this.finish();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dateFormat.format(date);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_user_info, null);
    }

    @Override
    public String getHeadTitle() {
        headView.setRightText("保存");
        headView.setRightTextColor(getResources().getColor(R.color.white));
        headView.setListenerRight(this);
        headView.setListenerBack(this);
        return "个人资料";
    }
}
