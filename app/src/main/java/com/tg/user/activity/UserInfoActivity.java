package com.tg.user.activity;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.module.MainActivity1;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.object.ImageParams;
import com.tg.coloursteward.object.SlideItemObj;
import com.tg.coloursteward.util.GlideCacheUtil;
import com.tg.coloursteward.util.TokenUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.CameraView;
import com.tg.coloursteward.view.CameraView.STATE;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.tg.coloursteward.view.spinnerwheel.SlideSelectorView.OnCompleteListener;
import com.youmai.hxsdk.router.APath;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.tg.user.activity.BindMobileActivity.ISFROMUSER;

/**
 * 个人中心
 */
@Route(path = APath.USER_INFO_ACT)
public class UserInfoActivity extends BaseActivity implements OnClickListener {
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int RESULT_REQUEST_CODE = 2;
    private ArrayList<SlideItemObj> genderList;
    private boolean needPostImage = false;
    private String email = "";
    private String sex = "";
    private ImageView ivIcon;// 头像
    private RelativeLayout rlIcon;
    private String imageName;
    String crop_path = Environment.getExternalStorageDirectory()
            + "/colorholder/";
    private String updatetime;
    private boolean isSaveHeadImg = false;
    private TextView tv_user_name;
    private TextView tv_user_sex;
    private TextView tv_user_part;
    private TextView tv_user_job;
    private TextView tv_user_mobile;
    private EditText et_user_email;
    private RelativeLayout rl_usersex_change;
    private RelativeLayout rl_usermobile_change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        updateView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        sex = UserInfo.sex;
        email = UserInfo.email;
        int size = (int) (50 * Tools.getDisplayMetrics(this).density);
        rlIcon = findViewById(R.id.rl_icon);
        ivIcon = findViewById(R.id.iv_icon);
        rlIcon.setOnClickListener(this);
        ivIcon.getLayoutParams().width = size;
        ivIcon.getLayoutParams().height = size;
        ivIcon.setScaleType(ScaleType.CENTER_CROP);
        tv_user_name = findViewById(R.id.tv_user_name);
        tv_user_sex = findViewById(R.id.tv_user_sex);
        tv_user_part = findViewById(R.id.tv_user_part);
        tv_user_job = findViewById(R.id.tv_user_job);
        tv_user_mobile = findViewById(R.id.tv_user_mobile);
        et_user_email = findViewById(R.id.et_user_email);
        rl_usersex_change = findViewById(R.id.rl_usersex_change);
        rl_usermobile_change = findViewById(R.id.rl_usermobile_change);
        rl_usersex_change.setOnClickListener(this);
        rl_usermobile_change.setOnClickListener(this);

        tv_user_name.setText(UserInfo.realname);
        tv_user_sex.setText(UserInfo.sex);
        tv_user_part.setText(UserInfo.familyName);
        tv_user_job.setText(UserInfo.jobName);
        if (!TextUtils.isEmpty(UserInfo.mobile)) {
            tv_user_mobile.setText(UserInfo.mobile);
        } else {
            tv_user_mobile.setText("未绑定");
        }
        et_user_email.setText(email);
        updatetime = UserInfo.userinfoImg;
        Tools.saveStringValue(UserInfoActivity.this, "updatetime_img", UserInfo.userinfoImg);
        freshImg();
    }

    private void freshImg() {
        String url = Contants.Html5.HEAD_ICON_URL + "avatar?uid=" + UserInfo.employeeAccount;
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
        tv_user_sex.setText(UserInfo.sex);
        et_user_email.setText(UserInfo.email);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        if (msg.arg1 == HttpTools.SET_USER_INFO) {
            int code = HttpTools.getCode(jsonString);
            if (code == 0) {
                String content = HttpTools.getContentString(jsonString);
                if (content.equals("1")) {
                    headView.setRightText("保存");
                    setUserInfo();
                    updateView();
                    ToastFactory.showToast(this, "保存成功");
                    sendBroadcast(new Intent(MainActivity1.ACTION_FRESH_USERINFO));
                    UserInfoActivity.this.finish();
                }
            } else {
                ToastFactory.showToast(this, hintString);
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
            HttpTools.postAnImage(Contants.Html5.HEAD_ICON_URL, mHand, imgParams);
        } else {
            submitUserInfo();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == Activity.RESULT_OK && requestCode == RESULT_REQUEST_CODE) {
                if (data != null) {
                    getImageToView(data);
                }
            } else if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_REQUEST_CODE) {
                Uri uri = data.getData();
                startPhotoZoom(uri);
            } else if (requestCode == 100) {
                String mobile = data.getStringExtra("mobile");
                tv_user_mobile.setText(mobile);
            }
        }
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
        needPostImage = true;
        ivIcon.setImageBitmap(bitmap);
        if (needPostImage) {
            ImageParams imgParams = new ImageParams();
            imgParams.fileName = imageName;
            imgParams.path = crop_path + imageName;
            HttpTools.postAnImage(Contants.Html5.HEAD_ICON_URL, mHand, imgParams);
        } else {
            submitUserInfo();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_layout:
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
                break;
            case R.id.rl_icon:
                showFileChooser();
                break;
            case R.id.right_layout:
                submitUserInfo();
                break;
            case R.id.rl_usersex_change:
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
                                    tv_user_sex.setText(sex);
                                }
                            }
                        }, false);
                break;
            case R.id.rl_usermobile_change:
                Intent intent = new Intent(this, BindMobileActivity.class);
                intent.putExtra(ISFROMUSER, true);
                startActivityForResult(intent, 100);
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
            if (isSaveHeadImg == true) {
                ToastFactory.showToast(UserInfoActivity.this, "头像已保存");
                GlideCacheUtil.getInstance().clearImageDiskCache(UserInfoActivity.this);
                GlideCacheUtil.getInstance().clearImageMemoryCache(UserInfoActivity.this);
            }
            return;
        }
        RequestConfig config = new RequestConfig(UserInfoActivity.this, HttpTools.SET_USER_INFO, "验证中");
        Map<String, Object> validateParams = new HashMap<>();
        if (sex.equals("男")) {
            validateParams.put("gender", "1");//1男，2女;
        } else if (sex.equals("女")) {
            validateParams.put("gender", "2");//1男，2女;
        }
        validateParams.put("email", email);
        validateParams.put("device_uuid", TokenUtils.getUUID(UserInfoActivity.this));
        Map<String, String> stringMap = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(UserInfoActivity.this, validateParams));
        HttpTools.httpPost_Map(Contants.URl.URL_ICESTAFF, "/app/modifyInfo", config, (HashMap) stringMap);


    }

    private boolean hasChanged() {
        sex = tv_user_sex.getText().toString().trim();
        email = et_user_email.getText().toString().trim();
        if (!TextUtils.equals(sex, UserInfo.sex)) {
            return true;
        }
        if (!TextUtils.equals(email, UserInfo.email)) {
            return true;
        }
        return false;
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
