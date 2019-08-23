package com.tg.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.DeskTopItemAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.HomeDeskTopInfo;
import com.tg.coloursteward.inter.OnItemDeleteListener;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.model.HomeModel;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.util.LinkParseUtil;
import com.tg.coloursteward.util.MicroAuthTimeUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.TokenUtils;
import com.tg.coloursteward.view.PullRefreshListView;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.entity.MsgConfig;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息推送消息列表
 *
 * @author Administrator
 */
public class DeskTopActivity extends BaseActivity implements OnItemClickListener, OnItemDeleteListener,
        HttpResponse, View.OnClickListener {
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
    private HomeModel homeModel;
    private MicroAuthTimeUtils mMicroAuthTimeUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        homeModel = new HomeModel(this);
        if (intent != null) {
            item = intent.getParcelableExtra(DESKTOP_WEIAPPCODE);
            homePosition = intent.getIntExtra(DESKTOP_POSTION, -1);
        }
        if (item == null) {
            item = new MsgConfig.ContentBean.DataBean();
        }
        if (!TextUtils.isEmpty(item.getComefrom())) {
            headView.setTitle(item.getComefrom());
        }
        initView();
    }

    @Override
    protected boolean handClickEvent(View v) {
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
                        item.id = data.getInt(i, "msg_id");
                        item.msg_id = data.getString(i, "msg_id");
                        item.auth_type = data.getInt(i, "auth_type");
                        item.icon = data.getString(i, "app_logo");
                        item.owner_name = data.getString(i, "owner_name");
                        item.owner_avatar = data.getString(i, "owner_avatar");
                        item.modify_time = data.getString(i, "homePushTime");
                        item.title = data.getString(i, "title");
                        item.source_id = data.getString(i, "source_id");
                        item.comefrom = data.getString(i, "comefrom");
                        item.url = data.getString(i, "url");
                        item.client_code = data.getString(i, "client_code");
                        item.notread = data.getInt(i, "isread");
                        list.add(item);
                    }
                }
            }

            @Override
            public void onLoadingMore(PullRefreshListView t, Handler hand, int pageIndex) {
                RequestConfig config = new RequestConfig(DeskTopActivity.this, PullRefreshListView.HTTP_MORE_CODE);
                config.handler = hand;
                Map<String, Object> map = new HashMap();
                if (TextUtils.isEmpty(item.getApp_id())) {
                    map.put("app_id", "");
                } else {
                    map.put("app_id", item.getApp_id());
                }
                map.put("page", pageIndex);
                Map<String, String> params = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(
                        DeskTopActivity.this, map));
                HttpTools.httpGet_Map(Contants.URl.URL_NEW, "/app/home/getMsgDetailList", config, (HashMap) params);
            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                RequestConfig config = new RequestConfig(DeskTopActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
                config.handler = hand;
                Map<String, Object> map = new HashMap();
                if (TextUtils.isEmpty(item.getApp_id())) {
                    map.put("app_id", "");
                } else {
                    map.put("app_id", item.getApp_id());
                }
                map.put("page", 1);
                Map<String, String> params = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(
                        DeskTopActivity.this, map));
                HttpTools.httpGet_Map(Contants.URl.URL_NEW, "/app/home/getMsgDetailList", config, (HashMap) params);
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
        homeModel.postSetMsgRead(0, list.get(Position).msg_id, this);
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
            if (info.url.startsWith("https") || info.url.startsWith("http")) {
                if (null == mMicroAuthTimeUtils) {
                    mMicroAuthTimeUtils = new MicroAuthTimeUtils();
                }
                mMicroAuthTimeUtils.IsAuthTime(this, info.url,
                        info.client_code, String.valueOf(info.auth_type), info.client_code, "");
            } else {
                LinkParseUtil.parse(DeskTopActivity.this, info.url, "");
            }
        }
    }

    @Override
    public void onItemDelete(int position) {
        deletePosition = position;
        homeModel.postDelMsg(1, list.get(position).msg_id, this);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
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
        }

    }

    @Override
    public void onBackPressed() {
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                setResult(3001);
                this.finish();
                return false;//拦截事件
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
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
        headView.setListenerBack(this);
        return null;
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    list.get(readPosition).notread = 1;
                    adapter.notifyDataSetChanged();
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    list.remove(deletePosition);
                    adapter.notifyDataSetChanged();
                }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_layout:
                setResult(3001);
                this.finish();
                break;
        }
    }
}
