package com.tg.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
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
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.LinkParseUtil;
import com.tg.coloursteward.util.MicroAuthTimeUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.util.TokenUtils;
import com.tg.coloursteward.view.PullRefreshListView;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.tg.im.adapter.DeskTopAdapter;
import com.tg.im.entity.DeskTopEntity;
import com.youmai.hxsdk.entity.MsgConfig;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private int page = 1;
    private DeskTopAdapter deskTopAdapter;
    private List<DeskTopEntity.ContentBean.DataBean> mList = new ArrayList<>();
    private RecyclerView rv_desktop;
    private int totalAllNum;
    private SwipeRefreshLayout sr_desktop;
    private RelativeLayout rl_desktop_bottom;
    private TextView tv_desktop_all;
    private TextView tv_desktop_read;
    private TextView tv_desktop_delete;
    private boolean isCheckAll = false;
    private List<Integer> delPosition = new ArrayList<>();
    private List<Integer> readedPosition = new ArrayList<>();

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
        initData(page, true);
    }

    private void initData(int page, boolean isLoading) {
        homeModel.getHomeMsgDetail(2, item.getApp_id(), page, isLoading, this);
    }

    @Override
    protected boolean handClickEvent(View v) {
//        if (adapter.getCount() == 0) {
//            headView.setRightText("编辑");
//            pullListView.setEnablePullRefresh(true);
//            isEditable = false;
//            return false;
//        }
        if (isEditable) {
            headView.setRightText("编辑");
//            pullListView.setEnablePullRefresh(true);
            for (int i = 0; i < mList.size(); i++) {
                mList.get(i).setIsShowCheck(0);
                mList.get(i).setIsCheckBox(0);
            }
            if (null != deskTopAdapter) {
                deskTopAdapter.notifyDataSetChanged();
                rl_desktop_bottom.setVisibility(View.GONE);
            }
            isEditable = false;
        } else {
            headView.setRightText("完成");
            isEditable = true;
            for (int i = 0; i < mList.size(); i++) {
                mList.get(i).setIsShowCheck(1);
            }
            if (null != deskTopAdapter) {
                deskTopAdapter.notifyDataSetChanged();
                rl_desktop_bottom.setVisibility(View.VISIBLE);
                if (item.getApp_id().equals("tgzz")) {
                    tv_desktop_delete.setVisibility(View.GONE);
                }
            }
//            pullListView.setEnablePullRefresh(false);
        }
//        adapter.showDeleteButton(isEditable);

        return false;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        pullListView = (PullRefreshListView) findViewById(R.id.pull_listview);
        rv_desktop = findViewById(R.id.rv_desktop);
        sr_desktop = findViewById(R.id.sr_desktop);
        rl_desktop_bottom = findViewById(R.id.rl_desktop_bottom);
        tv_desktop_all = findViewById(R.id.tv_desktop_all);
        tv_desktop_read = findViewById(R.id.tv_desktop_read);
        tv_desktop_delete = findViewById(R.id.tv_desktop_delete);
        tv_desktop_all.setOnClickListener(this);
        tv_desktop_read.setOnClickListener(this);
        tv_desktop_delete.setOnClickListener(this);
        sr_desktop.setColorSchemeResources(R.color.alipay_bg_color);
        sr_desktop.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                initData(page, true);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_desktop.setLayoutManager(layoutManager);
        adapter = new DeskTopItemAdapter(DeskTopActivity.this, list);
//        adapter.setOnItemDeleteListener(this);
//        pullListView.setAdapter(adapter);
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
                        item.comefrom = data.getString(i, "app_name");
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
//                HttpTools.httpGet_Map(Contants.URl.URL_NEW, "/app/home/getMsgDetailList", config, (HashMap) params);
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
//                HttpTools.httpGet_Map(Contants.URl.URL_NEW, "/app/home/getMsgDetailList", config, (HashMap) params);
            }
        });
//        pullListView.performLoading();
    }

    /**
     * 设置为已读
     *
     * @param Position
     */
    private void readMsg(int Position) {
        readPosition = Position;
        homeModel.postSetMsgRead(0, list.get(Position).msg_id, true, this);
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
                if (!TextUtils.isEmpty(result)) {//点击消息，默认调用已读接口

                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    list.remove(deletePosition);
                    adapter.notifyDataSetChanged();
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(result)) {
                    initAdapter(result);
                }
                break;
            case 3:
                if (!TextUtils.isEmpty(result)) {//设为已读
                    for (int i = 0; i < readedPosition.size(); i++) {
                        mList.get(readedPosition.get(i)).setIsread("1");
                        deskTopAdapter.notifyDataSetChanged();
                    }
                    ToastUtil.showShortToast(this, "已读成功");
                }
                break;
            case 4:
                if (!TextUtils.isEmpty(result)) {//标记删除
                    for (int i = 0; i < delPosition.size(); i++) {
                        deskTopAdapter.remove(delPosition.get(i));
                    }
                    ToastUtil.showShortToast(this, "删除成功");
                }
                break;

        }
    }

    private void initAdapter(String result) {
        String jsonString = HttpTools.getContentString(result);
        if (TextUtils.isEmpty(jsonString)) {
            return;
        }
        if (sr_desktop.isRefreshing()) {
            sr_desktop.setRefreshing(false);
        }
        DeskTopEntity deskTopEntity = new DeskTopEntity();
        deskTopEntity = GsonUtils.gsonToBean(result, DeskTopEntity.class);
        totalAllNum = deskTopEntity.getContent().getTotal();
        if (page == 1) {
            mList.clear();
        }
        mList.addAll(deskTopEntity.getContent().getData());
        for (int i = 0; i < mList.size(); i++) {
            if (isCheckAll) {
                mList.get(i).setIsShowCheck(1);
            } else {
                mList.get(i).setIsShowCheck(0);
            }
        }
        if (null == deskTopAdapter) {
            deskTopAdapter = new DeskTopAdapter(R.layout.home_listview, mList);
            rv_desktop.setAdapter(deskTopAdapter);
        } else {
            deskTopAdapter.notifyDataSetChanged();
        }
        deskTopAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mList.size() >= totalAllNum) {
                            deskTopAdapter.loadMoreEnd();
                        } else {
                            page++;
                            initData(page, false);
                            deskTopAdapter.loadMoreComplete();
                        }
                    }
                }, 1500);
            }
        }, rv_desktop);
        deskTopAdapter.disableLoadMoreIfNotFullPage();
        deskTopAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_desktop:
                        if (mList.get(position).getIsCheckBox() == 1) {
                            mList.get(position).setIsCheckBox(0);
                        } else {
                            mList.get(position).setIsCheckBox(1);
                        }
                        deskTopAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });
        deskTopAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (null != mList && mList.size() > 0) {
                    if (mList.get(position).getIsread().equals("0")) {
                        mList.get(position).setIsread("1");
                        deskTopAdapter.notifyItemChanged(position);
                        homeModel.postSetMsgRead(0, mList.get(position).getMsg_id(), false, DeskTopActivity.this);
                    }
                    String url = mList.get(position).getUrl();
                    String auth_type = mList.get(position).getAuth_type();
                    if (url.startsWith("https") || url.startsWith("http")) {
                        if (null == mMicroAuthTimeUtils) {
                            mMicroAuthTimeUtils = new MicroAuthTimeUtils();
                        }
                        mMicroAuthTimeUtils.IsAuthTime(DeskTopActivity.this, url,
                                "", auth_type, "", "");
                    } else {
                        LinkParseUtil.parse(DeskTopActivity.this, url, "");
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_layout:
                setResult(3001);
                this.finish();
                break;
            case R.id.tv_desktop_all:
                if (isCheckAll) {
                    tv_desktop_all.setText("全选");
                    for (int i = 0; i < mList.size(); i++) {
                        mList.get(i).setIsCheckBox(0);
                    }
                    if (null != deskTopAdapter) {
                        deskTopAdapter.notifyDataSetChanged();
                    }
                    isCheckAll = false;
                } else {
                    tv_desktop_all.setText("取消全选");
                    for (int i = 0; i < mList.size(); i++) {
                        mList.get(i).setIsCheckBox(1);
                    }
                    if (null != deskTopAdapter) {
                        deskTopAdapter.notifyDataSetChanged();
                    }
                    isCheckAll = true;
                }

                break;
            case R.id.tv_desktop_read:
                readedPosition.clear();
                StringBuffer stringBuffer = new StringBuffer();
                StringBuffer sbll = new StringBuffer();
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).getIsCheckBox() == 1) {
                        sbll.append(mList.get(i).getMsg_id() + ",");
                        if (!mList.get(i).getIsread().equals("1")) {
                            readedPosition.add(i);
                            stringBuffer.append(mList.get(i).getMsg_id() + ",");
                        }
                    }
                }
                String content = stringBuffer.toString();
                if (!TextUtils.isEmpty(sbll)) {
                    if (!TextUtils.isEmpty(content)) {
                        content = content.substring(0, content.length() - 1);
                        homeModel.postSetMsgRead(3, content, true, this);
                    } else {
                        ToastUtil.showShortToast(this, "已读成功");
                    }
                } else {
                    ToastUtil.showShortToast(this, "请选择设为已读的消息");
                }
                break;
            case R.id.tv_desktop_delete:
                delPosition.clear();
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).getIsCheckBox() == 1) {
                        delPosition.add(i);
                        sb.append(mList.get(i).getMsg_id() + ",");
                    }
                }
                String con = sb.toString();
                if (!TextUtils.isEmpty(con)) {
                    content = con.substring(0, con.length() - 1);
                    homeModel.postDelMsg(4, content, this);
                } else {
                    ToastUtil.showShortToast(this, "请选择需要删除的消息");
                }
                break;
        }
    }
}
