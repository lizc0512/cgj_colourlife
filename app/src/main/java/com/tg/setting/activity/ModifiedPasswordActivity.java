package com.tg.setting.activity;

import android.R.color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.util.SoftKeyboardUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.tg.user.model.UserModel;


/**
 * 修改密码
 *
 * @author Administrator
 */
public class ModifiedPasswordActivity extends BaseActivity implements HttpResponse {
    private EditText editPwd1;
    private EditText editPwd2;
    private EditText editPwd3;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userModel = new UserModel(this);
        editPwd1 = findViewById(R.id.edit_password);
        editPwd2 = findViewById(R.id.edit_password2);
        editPwd3 = findViewById(R.id.edit_password3);
    }

    @Override
    protected boolean handClickEvent(View v) {
        String pwd1;
        String pwd2;
        String pwd3;
        pwd1 = editPwd1.getText().toString();
        pwd2 = editPwd2.getText().toString();
        pwd3 = editPwd3.getText().toString();
        if (v.getId() == R.id.right_layout) {
            SoftKeyboardUtils.hideSoftKeyboard(this, editPwd1);
            if (TextUtils.isEmpty(pwd1)) {
                ToastFactory.showToast(this, "请输入旧密码");
                return false;
            }
            if (!StringUtils.checkPwdType(pwd2)) {
                ToastUtil.showShortToast(this, "请输入8-18位字母+数字新密码");
                return false;
            }
            if (!StringUtils.checkPwdType(pwd3)) {
                ToastUtil.showShortToast(this, "请输入8-18位字母+数字确认密码");
                return false;
            }
            if (!pwd2.equals(pwd3)) {
                ToastUtil.showShortToast(this, "确认密码和密码不一致");
                return false;
            }
            try {
                String pwdold = MD5.getMd5Value(pwd1).toLowerCase();
                String pwdnew = MD5.getMd5Value(pwd2).toLowerCase();
                userModel.putChangePwd(1, UserInfo.employeeAccount, pwdold, pwdnew, this::OnHttpResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return super.handClickEvent(v);
    }

    /**
     * 单设备退出
     */
    private void singleDevicelogout() {
        String device_code = spUtils.getStringData(SpConstants.storage.DEVICE_TOKEN, "");
        userModel.postSingleExit(0, device_code, this);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_modified_password,
                null);
    }

    @Override
    public String getHeadTitle() {
        headView.setRightText("提交");
        headView.setRightTextColor(getResources().getColor(color.white));
        headView.setListenerRight(singleListener);
        return "修改登录密码";
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    exitClearAllData(ModifiedPasswordActivity.this,false);
                    String message = HttpTools.getMessageString(result);
                    ToastFactory.showToast(ModifiedPasswordActivity.this, message);
                    ModifiedPasswordActivity.this.finish();
                }
                break;
        }
    }
}
