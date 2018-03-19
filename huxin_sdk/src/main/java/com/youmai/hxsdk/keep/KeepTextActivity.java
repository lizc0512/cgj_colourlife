package com.youmai.hxsdk.keep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.dialog.HxKeepDialog;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.http.HttpConnector;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.utils.GsonUtil;

/**
 * 作者：create by YW
 * 日期：2017.11.16 17:18
 * 描述：
 */
public class KeepTextActivity extends SdkBaseActivity {

    public static final String KEEP_ID = "keep_id";
    public static final String KEEP_TEXT_CONTENT = "keep_text_context";

    private String keepId;
    private CacheMsgBean cacheMsgBean;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hx_keep_text_view);

        initTitle();

        initView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hx_keep_more_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_more) {
            forwardOrDeleteItem();
        }
        return false;
    }

    private void forwardOrDeleteItem() {
        HxKeepDialog hxDialog = new HxKeepDialog(mContext);
        HxKeepDialog.HxCallback callback =
                new HxKeepDialog.HxCallback() {
                    @Override
                    public void onForward() {
                        Intent intent = new Intent();
                        intent.setAction("com.youmai.huxin.recent");
                        intent.putExtra("type", "forward_msg");
                        intent.putExtra("data", cacheMsgBean);
                        mContext.startActivity(intent);
                    }

                    @Override
                    public void onDelete() {
                        delKeep();
                    }
                };
        hxDialog.setHxCollectDialog(callback);
        hxDialog.show();
    }

    private void initTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("详情");

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        keepId = getIntent().getStringExtra(KEEP_ID);
        cacheMsgBean = getIntent().getParcelableExtra(KeepAdapter.CACHE_MSG_BEAN);
        String content = getIntent().getStringExtra(KEEP_TEXT_CONTENT);

        TextView tv_text_content = (TextView) findViewById(R.id.tv_text_content);
        tv_text_content.setText(content);
    }

    private void delKeep() {
        String url = AppConfig.COLLECT_DEL;
        ReqKeepDel del = new ReqKeepDel(this);
        del.setIds(keepId);

        HttpConnector.httpPost(url, del.getParams(), new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespBaseBean baseBean = GsonUtil.parse(response, RespBaseBean.class);
                if (baseBean != null && baseBean.isSuccess()) {
                    Toast.makeText(mContext, R.string.collect_del_success, Toast.LENGTH_SHORT).show();
                    Intent data = new Intent();
                    data.putExtra(KEEP_ID, keepId);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                } else {
                    Toast.makeText(mContext, R.string.collect_del_fail, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
