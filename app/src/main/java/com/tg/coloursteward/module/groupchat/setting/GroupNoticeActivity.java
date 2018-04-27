package com.tg.coloursteward.module.groupchat.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tg.coloursteward.R;
import com.tg.coloursteward.module.groupchat.details.ChatGroupDetailsActivity;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.StringUtils;

/**
 * 作者：create by YW
 * 日期：2018.04.26 17:05
 * 描述: 群公告设置
 */
public class GroupNoticeActivity extends Activity {

    public static final String GROUP_ID = "groupId";
    public static final String GROUP_NOTICE = "groupNotice";

    private TextView tv_back, tv_title, tv_title_right;
    private EditText et_user_notice;

    private String notice;
    private int groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_notice_setting);

        initView();
        initData();
        setClickListener();
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_left_cancel);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title_right = (TextView) findViewById(R.id.tv_right_sure);
        et_user_notice = (EditText) findViewById(R.id.et_user_notice);
    }

    private void initData() {
        groupId = getIntent().getIntExtra(GROUP_ID, -1);
        notice = getIntent().getStringExtra(GROUP_NOTICE);
        tv_title.setText("群公告");
        if (!StringUtils.isEmpty(notice)) {
            et_user_notice.setText(notice);
        }
        et_user_notice.setSelection(et_user_notice.getText().length());

    }

    private void setClickListener() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_title_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String groupNotice = et_user_notice.getText().toString().trim();
                InputMethodManager manager = (InputMethodManager) GroupNoticeActivity.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(et_user_notice.getWindowToken(), 0);

                if (StringUtils.isEmpty(groupNotice)) {
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra(GROUP_NOTICE, groupNotice);
                setResult(ChatGroupDetailsActivity.RESULT_CODE, intent);
                finish();

                ReceiveListener receiveListener = new ReceiveListener() {
                    @Override
                    public void OnRec(PduBase pduBase) {
                        try {
                            YouMaiGroup.GroupInfoModifyRsp ack = YouMaiGroup.GroupInfoModifyRsp.parseFrom(pduBase.body);
                            if (ack.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                                Intent intent = new Intent();
                                intent.putExtra(GROUP_NOTICE, groupNotice);
                                setResult(ChatGroupDetailsActivity.RESULT_CODE, intent);
                                finish();
                            }
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                    }
                };

                HuxinSdkManager.instance().reqModifyGroupInfo(
                        groupId, "", groupNotice, "", receiveListener);
            }
        });
    }

}
