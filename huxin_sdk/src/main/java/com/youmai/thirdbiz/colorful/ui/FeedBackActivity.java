package com.youmai.thirdbiz.colorful.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.thirdbiz.colorful.net.ColorsConfig;
import com.youmai.thirdbiz.colorful.net.ColorsUtil;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-07-20 17:59
 * Description:
 * 意见反馈界面
 */
public class FeedBackActivity extends SdkBaseActivity implements TextWatcher, View.OnClickListener {

    private EditText mEditext;

    private CharSequence temp;
    private int selectionStart;
    private int selectionEnd;
    private TextView mTextView;
    private Button mBtnCommit;
    private String content;
    private int number;
    private ProgressDialog progressDialog;
    private RelativeLayout re_title_feedback;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cgj_activity_color_feedback);
        initView();
        initData();

    }


    protected void initView() {
        TextView tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView tv_title_right = (TextView) findViewById(R.id.tv_title_right);
        tv_title_right.setVisibility(View.GONE);

        //主题的长按事件
        re_title_feedback = (RelativeLayout) findViewById(R.id.re_title_feedback);
        re_title_feedback.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(FeedBackActivity.this, ColorsConfig.COLOR_BUILD_VERSION, Toast.LENGTH_LONG).show();
                return true;
            }
        });
        mEditext = (EditText) findViewById(R.id.et_bianji);
        mTextView = (TextView) findViewById(R.id.tv_curren);
        mBtnCommit = (Button) findViewById(R.id.btn_commit);
        mBtnCommit.setEnabled(false);
        mBtnCommit.setClickable(false);
    }


    protected void initData() {
        mEditext.addTextChangedListener(this);
        mBtnCommit.setOnClickListener(this);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        temp = s;
    }

    /**
     * 编辑之后的字数的监控
     *
     * @param s
     */

    @Override
    public void afterTextChanged(Editable s) {
        number = 100 - s.length();
        mTextView.setText("" + number);
        selectionStart = mEditext.getSelectionStart();
        selectionEnd = mEditext.getSelectionEnd();

        if (temp.length() > 0) {
            //mBtnCommit.setBackgroundResource(R.drawable.hxm_tbmine_feedback_submit_selector);
            mBtnCommit.setEnabled(true);
            mBtnCommit.setClickable(true);
        } else {
            //mBtnCommit.setBackgroundResource(R.mipmap.hxm_app_tbmine_bg_feedback_disabled);
            mBtnCommit.setClickable(false);
        }
        if (temp.length() > 100) {
            s.delete(selectionStart - 1, selectionEnd);
            int tempSelection = selectionEnd;
            mEditext.setText(s);
            mEditext.setSelection(tempSelection);//设置光标在最后
        }
    }

    /**
     * 点击提交
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        content = mEditext.getText().toString();
        LogUtils.e(Constant.SDK_UI_TAG, "content======" + content);

        if (AppUtils.isNetworkConnected(FeedBackActivity.this)) {

            String phoneNum = HuxinSdkManager.instance().getPhoneNum();
            String userid = HuxinSdkManager.instance().getCgjUserId(); //"dageda";

            ShowProgress();

            ColorsUtil.addFeedbackInfo("name", phoneNum, content, userid, new IPostListener() {
                @Override
                public void httpReqResult(String response) {
                    progressDialog.dismiss();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);
                        int code = jsonObject.optInt("code");
                        if (code == 0) {
                            Toast.makeText(FeedBackActivity.this.getApplicationContext(), "提交成功", Toast.LENGTH_SHORT).show();
                            mEditext.setText(null);
                            //mBtnCommit.setTextColor(R.color.hxb_color_white);
                            mTextView.setText("" + 0);
                            LogUtils.e("xx", response);
                            setResult(1);
                            finish();
                        } else {
                            Toast.makeText(FeedBackActivity.this.getApplicationContext(), "提交失败", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            ToastUtil.showToast(FeedBackActivity.this, "当前网络异常");
        }

    }


    IPostListener callback = new IPostListener() {
        @Override
        public void httpReqResult(String response) {
            Toast.makeText(FeedBackActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
            LogUtils.e(Constant.SDK_UI_TAG, "res======" + response);
            //提交成功将输入框内容置为空
            mEditext.setText(null);
            //mBtnCommit.setTextColor(R.color.hxb_color_white);
            mTextView.setText("" + 0);
        }
    };

    /**
     * 显示进度条
     */
    public void ShowProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在上传数据");
        progressDialog.show();
    }


}
