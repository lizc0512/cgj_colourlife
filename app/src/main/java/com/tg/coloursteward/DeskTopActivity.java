package com.tg.coloursteward;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.tg.coloursteward.adapter.DeskTopItemAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.HomeDeskTopInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.OnItemDeleteListener;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.util.AuthTimeUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.PullRefreshListView;
import com.tg.coloursteward.view.PullRefreshListViewFind;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.entity.MsgConfig;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * 消息推送消息列表
 *
 * @author Administrator
 */
public class DeskTopActivity extends BaseActivity implements OnItemClickListener, OnItemDeleteListener {
    public static final String DESKTOP_WEIAPPCODE = "weiappcode";
    public static final String DESKTOP_POSTION = "desktop_postion";
    private PullRefreshListView pullListView;
    private DeskTopItemAdapter adapter;
    private MsgConfig.ContentBean.DataBean item;
    private HomeService homeService;
    private Intent intent;
    private int deletePosition;
    private boolean isEditable = false;
    private int readPosition;
    private int homePosition;
    private ArrayList<HomeDeskTopInfo> list = new ArrayList<HomeDeskTopInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        if (intent != null) {
            item = intent.getParcelableExtra(DESKTOP_WEIAPPCODE);
            homePosition = intent.getIntExtra(DESKTOP_POSTION, -1);
        }
        if (item == null) {
            ToastFactory.showToast(DeskTopActivity.this, "参数错误");
            finish();
        }
        if (TextUtils.isEmpty(item.getComefrom())) {
            headView.setTitle(item.getComefrom());
        }
        initView();
    }

    @Override
    protected boolean handClickEvent(View v) {
        // TODO Auto-generated method stub
        if (adapter.getCount() == 0) {
            headView.setRightText("编辑");
            pullListView.setEnablePullRefresh(true);
            isEditable = false;
            return false;
        }
        if (isEditable) {
            headView.setRightText("编辑");
            pullListView.setEnablePullRefresh(true);
            isEditable = false;
        } else {
            headView.setRightText("完成");
            isEditable = true;
            pullListView.setEnablePullRefresh(false);
        }
        adapter.showDeleteButton(isEditable);
        return false;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        pullListView = (PullRefreshListView) findViewById(R.id.pull_listview);
        adapter = new DeskTopItemAdapter(DeskTopActivity.this, list);
        adapter.setOnItemDeleteListener(this);
        pullListView.setAdapter(adapter);
        pullListView.setOnItemClickListener(this);
        pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
            @Override
            public void refreshData(PullRefreshListView t, boolean isLoadMore, Message msg, String response) {
                String jsonString = HttpTools.getContentString(response);
                if (StringUtils.isNotEmpty(jsonString)) {
                    Message msghome = new Message();
                    msghome.what = Contants.LOGO.UPDATE_HOMELIST;
                    msghome.arg1 = homePosition;
                    EventBus.getDefault().post(msghome);
                    ResponseData data = HttpTools.getResponseData(jsonString);
                    HomeDeskTopInfo item;
                    for (int i = 0; i < data.length; i++) {
                        item = new HomeDeskTopInfo();
                        item.id = data.getInt(i, "id");
                        item.auth_type = data.getInt(i, "auth_type");
                        item.icon = data.getString(i, "ICON");
                        item.owner_name = data.getString(i, "owner_name");
                        item.owner_avatar = data.getString(i, "owner_avatar");
                        item.modify_time = data.getString(i, "homePushTime");
                        item.title = data.getString(i, "title");
                        item.source_id = data.getString(i, "source_id");
                        item.comefrom = data.getString(i, "comefrom");
                        item.url = data.getString(i, "url");
                        item.client_code = data.getString(i, "client_code");
                        item.notread = data.getInt(i, "notread");
                        list.add(item);
                    }
                }
            }

            @Override
            public void onLoadingMore(PullRefreshListView t, Handler hand, int pageIndex) {
                RequestConfig config = new RequestConfig(DeskTopActivity.this, PullRefreshListView.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("username", UserInfo.employeeAccount);
                params.put("client_code", item.getClient_code());
                params.put("showType", 1);
                params.put("page", pageIndex);
                params.put("pagesize", PullRefreshListViewFind.PAGER_SIZE);
                params.put("corp_id", Tools.getStringValue(DeskTopActivity.this, Contants.storage.CORPID));
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/push2/homepush", config, params);
            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                RequestConfig config = new RequestConfig(DeskTopActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
                config.handler = hand;

                RequestParams params = new RequestParams();
                params.put("username", UserInfo.employeeAccount);
                params.put("showType", 1);
                params.put("client_code", item.getClient_code());
                params.put("page", 1);
                params.put("pagesize", PullRefreshListViewFind.PAGER_SIZE);
                params.put("corp_id", Tools.getStringValue(DeskTopActivity.this, Contants.storage.CORPID));
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/push2/homepush", config, params);
            }
        });
        pullListView.performLoading();
    }

    /**
     * 设置为已读
     *
     * @param Position
     */
    private void readMsg(int Position) {
        readPosition = Position;
        RequestConfig config = new RequestConfig(this, HttpTools.SET_MSG_READ);
        RequestParams params = new RequestParams();
        params.put("client_code", list.get(Position).client_code);
        params.put("username", UserInfo.employeeAccount);
        HttpTools.httpPut(Contants.URl.URL_ICETEST, "/push2/homepush/readhomePush", config, params);
        list.get(readPosition).notread = 1;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != list && list.size() > 0) {
            HomeDeskTopInfo info = list.get(position);
            if (info.notread == 0) {
                readMsg((int) parent.getAdapter().getItemId(position));
            }
            AuthTimeUtils mAuthTimeUtils = new AuthTimeUtils();
            mAuthTimeUtils.IsAuthTime(DeskTopActivity.this, info.url, info.client_code, String.valueOf(info.auth_type), info.client_code, "");
        }
    }

    @Override
    public void onItemDelete(int position) {
        deletePosition = position;
        HomeDeskTopInfo info = list.get(position);
        RequestConfig config = new RequestConfig(this, HttpTools.POST_DELETE_INFO, null);
        RequestParams params = new RequestParams();
        params.put("msgid", info.id);
        HttpTools.httpDelete(Contants.URl.URL_ICETEST, "/push2/homepush", config, params);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        // TODO Auto-generated method stub
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        if (msg.arg1 == HttpTools.POST_DELETE_INFO) {
            String message = HttpTools.getMessageString(jsonString);
            if (code == 0) {
                list.remove(deletePosition);
                adapter.notifyDataSetChanged();
            } else {
                ToastFactory.showToast(DeskTopActivity.this, message);
            }
        } else {
            if (code == 0) {
                list.get(readPosition).notread = 1;
                adapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (isEditable) {
            headView.setRightText("编辑");
            pullListView.setEnablePullRefresh(true);
            isEditable = false;
            adapter.hideDeleteButton();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_desk_top, null);
    }

    @Override
    public String getHeadTitle() {
        headView.setRightText("编辑");
        headView.setRightTextColor(getResources().getColor(R.color.white));
        headView.setListenerRight(singleListener);
        return null;
    }
}
