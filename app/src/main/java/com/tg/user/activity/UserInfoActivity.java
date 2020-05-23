package com.tg.user.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.baseModel.RequestEncryptionUtils;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.util.Tools;
import com.tg.user.model.UserModel;
import com.youmai.hxsdk.router.APath;
import com.youmai.hxsdk.view.pickerview.OptionsPickerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.tg.user.activity.BindMobileActivity.ISFROMUSER;

/**
 * 个人中心
 */
@Route(path = APath.USER_INFO_ACT)
public class UserInfoActivity extends BaseActivity implements OnClickListener, HttpResponse {
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int RESULT_REQUEST_CODE = 2;
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
    private TextView tv_user_company;
    private EditText et_user_email;
    private RelativeLayout rl_usersex_change;
    private RelativeLayout rl_usermobile_change;
    private RelativeLayout rl_user_company;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userModel = new UserModel(this);
        initView();
        updateView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        sex = UserInfo.sex;
        email = UserInfo.email;
        rlIcon = findViewById(R.id.rl_icon);
        ivIcon = findViewById(R.id.iv_icon);
        rlIcon.setOnClickListener(this);
        tv_user_name = findViewById(R.id.tv_user_name);
        tv_user_sex = findViewById(R.id.tv_user_sex);
        tv_user_part = findViewById(R.id.tv_user_part);
        tv_user_job = findViewById(R.id.tv_user_job);
        tv_user_mobile = findViewById(R.id.tv_user_mobile);
        et_user_email = findViewById(R.id.et_user_email);
        rl_usersex_change = findViewById(R.id.rl_usersex_change);
        rl_usermobile_change = findViewById(R.id.rl_usermobile_change);
        tv_user_company = findViewById(R.id.tv_user_company);
        rl_user_company = findViewById(R.id.rl_user_company);
        rl_usersex_change.setOnClickListener(this);
        rl_usermobile_change.setOnClickListener(this);
        rl_user_company.setOnClickListener(this);

        tv_user_name.setText(UserInfo.realname);
        tv_user_sex.setText(UserInfo.sex);
        tv_user_part.setText(UserInfo.familyName);
        tv_user_job.setText(UserInfo.jobName);
        if (!TextUtils.isEmpty(UserInfo.mobile)) {
            tv_user_mobile.setText(UserInfo.mobile);
        } else {
            tv_user_mobile.setText("未绑定");
        }
        if (!TextUtils.isEmpty(email)) {
            et_user_email.setText(email);
        } else {
            et_user_email.setHint("< 未绑定 >");
        }
        String company = spUtils.getStringData(SpConstants.storage.CORPNAME, "");
        tv_user_company.setText(company);
        freshImg();
    }

    private void freshImg() {
        String url = Contants.Html5.HEAD_ICON_URL + "/avatar?uid=" + UserInfo.employeeAccount;
        updatetime = spUtils.getStringData(SpConstants.UserModel.UPDATETIME_IMG, "");
        GlideUtils.loadSignatureImageView(this, url, updatetime, ivIcon);
    }

    /**
     * 更新UI
     */
    private void updateView() {
        tv_user_sex.setText(UserInfo.sex);
        et_user_email.setText(UserInfo.email);
    }

    private void setUserInfo() {
        UserInfo.sex = sex;
        UserInfo.email = email;
        Tools.saveUserInfo(this);
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
            } else if (requestCode == 1001) {
                String name = data.getStringExtra("companyName");
                tv_user_company.setText(name);
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
        Bitmap bitmap = BitmapFactory.decodeFile(crop_path
                + imageName);
        needPostImage = true;
        ivIcon.setImageBitmap(bitmap);
        if (needPostImage) {
            userModel.postUploadImg(1, crop_path + imageName, this);
        } else {
            submitUserInfo();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_layout:
                if (hasChanged()) {//已经修改过信息
                    submitUserInfo();
                }
                this.finish();
                break;
            case R.id.rl_icon:
                showFileChooser();
                break;
            case R.id.right_layout:
                submitUserInfo();
                break;
            case R.id.rl_usersex_change:
                List<String> itemList = new ArrayList<>();
                itemList.add("男");
                itemList.add("女");
                OptionsPickerView pickerView;
                pickerView = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        if (options1 == 0) {
                            sex = "男";
                        } else {
                            sex = "女";
                        }
                        tv_user_sex.setText(sex);
                        submitUserInfo();
                    }
                }).build();
                pickerView.setPicker(itemList);
                pickerView.show();
                break;
            case R.id.rl_usermobile_change:
                Intent intent = new Intent(this, BindMobileActivity.class);
                intent.putExtra(ISFROMUSER, true);
                startActivityForResult(intent, 100);
                break;
            case R.id.rl_user_company:
                Intent it = new Intent(this, ChangeCompanyActivity.class);
                startActivityForResult(it, 1001);
                break;
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
            if (isSaveHeadImg == true) {
                ToastUtil.showShortToast(UserInfoActivity.this, "头像已保存");
            }
            return;
        }
        String gender = "";
        if (sex.equals("男")) {//1男，2女;
            gender = "1";
        } else if (sex.equals("女")) {
            gender = "2";
        }
        userModel.postUpdateInfo(0, gender, email, this);

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
            submitUserInfo();
        }
        UserInfoActivity.this.finish();
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
        headView.setListenerBack(this);
        return "个人资料";
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    String content = RequestEncryptionUtils.getContentString(result);
                    if (content.equals("1")) {
                        setUserInfo();
                        updateView();
                        ToastUtil.showShortToast(this, "保存成功");
                    }
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    needPostImage = false;
                    updatetime = String.valueOf(System.currentTimeMillis());
                    spUtils.saveStringData(SpConstants.UserModel.UPDATETIME_IMG, updatetime);
                    ToastUtil.showShortToast(this, "图片上传成功");
                    isSaveHeadImg = true;
                    submitUserInfo();
                    freshImg();
                }
                break;
        }
    }
}
