package com.tg.im.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.model.HomeModel;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.LinkParseUtil;
import com.tg.coloursteward.util.MicroAuthTimeUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.im.adapter.DeskTopAdapter;
import com.tg.im.entity.DeskTopEntity;
import com.youmai.hxsdk.entity.MsgConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息推送消息列表
 *
 * @author Administrator
 */
public class DeskTopActivity extends BaseActivity implements HttpResponse, View.OnClickListener {
    public static final String DESKTOP_WEIAPPCODE = "weiappcode";
    private MsgConfig.ContentBean.DataBean item;
    private Intent intent;
    private boolean isEditable = false;
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
        if (isEditable) {
            headView.setRightText("编辑");
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
        }
        return false;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        rv_desktop = findViewById(R.id.rv_desktop);
        sr_desktop = findViewById(R.id.sr_desktop);
        rl_desktop_bottom = findViewById(R.id.rl_desktop_bottom);
        tv_desktop_all = findViewById(R.id.tv_desktop_all);
        tv_desktop_read = findViewById(R.id.tv_desktop_read);
        tv_desktop_delete = findViewById(R.id.tv_desktop_delete);
        tv_desktop_all.setOnClickListener(this);
        tv_desktop_read.setOnClickListener(this);
        tv_desktop_delete.setOnClickListener(this);
        sr_desktop.setColorSchemeResources(R.color.blue_text_color);
        sr_desktop.setOnRefreshListener(() -> {
            page = 1;
            initData(page, true);
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_desktop.setLayoutManager(layoutManager);
    }

    @Override
    public void onBackPressed() {
        if (isEditable) {
            headView.setRightText("编辑");
            isEditable = false;
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
            case 2:
                if (!TextUtils.isEmpty(result)) {
                    initAdapter(result);
                }
                break;
            case 3:
                if (!TextUtils.isEmpty(result)) {//手动设置为已读
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
        deskTopAdapter.setOnLoadMoreListener(() -> new Handler().postDelayed(() -> {
            if (mList.size() >= totalAllNum) {
                deskTopAdapter.loadMoreEnd();
            } else {
                page++;
                initData(page, false);
                deskTopAdapter.loadMoreComplete();
            }
        }, 1500), rv_desktop);
        deskTopAdapter.disableLoadMoreIfNotFullPage();
        deskTopAdapter.setOnItemChildClickListener((adapter, view, position) -> {
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
        });
        deskTopAdapter.setOnItemClickListener((adapter, view, position) -> {
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
                            auth_type, "");
                } else {
                    LinkParseUtil.parse(DeskTopActivity.this, url, "");
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
