package com.tg.coloursteward.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jcodecraeer.xrecyclerview.progressindicator.AVLoadingIndicatorView;
import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.HomeDialogAdapter;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.entity.HomeDialogEntitiy;
import com.tg.coloursteward.entity.HomeMsgEntity;
import com.tg.coloursteward.entity.ScanCodeTimeEntity;
import com.tg.coloursteward.inter.FragmentMineCallBack;
import com.tg.coloursteward.model.HomeModel;
import com.tg.coloursteward.module.MainActivity;
import com.tg.coloursteward.serice.NetWorkStateReceiver;
import com.tg.coloursteward.util.AuthTimeUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.PopWindowView;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.im.activity.DeskTopActivity;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.ProtoCallback;
import com.youmai.hxsdk.adapter.MessageAdapter;
import com.youmai.hxsdk.chatgroup.IMGroupActivity;
import com.youmai.hxsdk.chatsingle.IMConnectionActivity;
import com.youmai.hxsdk.data.ExCacheMsgBean;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.entity.MsgConfig;
import com.youmai.hxsdk.im.IMMsgCallback;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.search.GlobalSearchActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 主页-沟通
 * A simple {@link Fragment} subclass.
 */
public class MsgListFragment extends Fragment implements IMMsgCallback, View.OnClickListener, HttpResponse {

    private Activity mActivity;
    //重新登录或重新连接
    private static final int RELOGIN_HUXIN_SERVER = 100;

    //progress加载完成显示进度
    private static final int LOAD_PROGRESS_DISMISS = 200;

    private XRecyclerView recyclerView;
    private MessageAdapter mMessageAdapter;
    private Context mContext;
    private Handler mHandler;

    // 换号登录需要去判断改变
    private LinearLayoutManager mLinearLayoutManager;

    private List<Integer> unReadListPosition = new ArrayList<>();
    private int curPostion;

    private ImageView iv_contactfragment_scan;
    private RelativeLayout rl_home_nomsg;
    private AlertDialog dialog;
    private HomeModel homeModel;
    private String dialogUuid;
    private RelativeLayout rl_home_internet;
    private NetWorkStateReceiver netWorkStateReceiver;
    private List<ScanCodeTimeEntity.ScandataBean> scanOffList = new ArrayList<>();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_contactfragment_scan:
                PopWindowView popWindowView = new PopWindowView(mActivity, iv_contactfragment_scan);
                popWindowView.showPopupWindow(iv_contactfragment_scan);
                break;
            case R.id.rl_home_internet:
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        HomeDialogEntitiy homeDialogEntitiy = GsonUtils.gsonToBean(result, HomeDialogEntitiy.class);
                        if (!TextUtils.isEmpty(homeDialogEntitiy.getContent().getContent())) {
                            if (null != homeDialogEntitiy.getContent().getButton() && homeDialogEntitiy.getContent().getButton().size() > 0) {
                                dialogUuid = homeDialogEntitiy.getContent().getPopup_uuid();
                                creatreDialog(homeDialogEntitiy);
                            }
                        }
                    } catch (Exception e) {
                    }
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    SharedPreferencesUtils.getInstance().saveStringData(SpConstants.UserModel.HOMEDATA, result);
                    initSetData(result);
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(result)) {

                }
                break;
            case 3:
                if (!TextUtils.isEmpty(result)) {
                    for (int i = 0; i < scanOffList.size(); i++) {
                        if (result.equals(scanOffList.get(i).getUrl())) {
                            scanOffList.remove(i);
                        }
                    }
                    if (scanOffList.size() == 0) {
                        SharedPreferencesUtils.getInstance().saveStringData(SpConstants.UserModel.SCANCODEOFFDATA, "");
                        return;
                    }
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = null;
                    for (int i = 0; i < scanOffList.size(); i++) {
                        jsonObject = new JSONObject();
                        try {
                            jsonObject.put("url", scanOffList.get(i).getUrl());
                            jsonObject.put("time", scanOffList.get(i).getTime());
                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (jsonArray.length() > 0) {
                        JSONObject js = new JSONObject();
                        try {
                            js.put("scandata", jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferencesUtils.getInstance().saveStringData(SpConstants.UserModel.SCANCODEOFFDATA, js.toString());
                    } else {
                        SharedPreferencesUtils.getInstance().saveStringData(SpConstants.UserModel.SCANCODEOFFDATA, "");
                    }
                }
                break;
        }
    }

    private void initSetData(String result) {
        List<MsgConfig.ContentBean.DataBean> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            String content = jsonObject.getString("content");
            if (TextUtils.isEmpty(content)) {
                return;
            }
            HomeMsgEntity homeMsgEntity = new HomeMsgEntity();
            homeMsgEntity = GsonUtils.gsonToBean(result, HomeMsgEntity.class);
            for (int i = 0; i < homeMsgEntity.getContent().getData().size(); i++) {
                MsgConfig.ContentBean.DataBean dataBean = new MsgConfig.ContentBean.DataBean();
                dataBean.setICON(homeMsgEntity.getContent().getData().get(i).getApp_logo());
                dataBean.setComefrom(homeMsgEntity.getContent().getData().get(i).getApp_name());
                dataBean.setOwner_name(homeMsgEntity.getContent().getData().get(i).getOwner_name());
                dataBean.setTitle(homeMsgEntity.getContent().getData().get(i).getTitle());
                dataBean.setClient_code(homeMsgEntity.getContent().getData().get(i).getClient_code());
                dataBean.setApp_id(homeMsgEntity.getContent().getData().get(i).getApp_id());
                dataBean.setHomePushTime(homeMsgEntity.getContent().getData().get(i).getHomePushTime());
                dataBean.setUrl(homeMsgEntity.getContent().getData().get(i).getUrl());
                if (homeMsgEntity.getContent().getData().get(i).getIsread() > 0) {
                    dataBean.setNotread(0);
                } else {
                    dataBean.setNotread(1);
                }
                dataBean.setApp_uuid(homeMsgEntity.getContent().getData().get(i).getApp_uuid());
                list.add(dataBean);
                dataBean = null;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != list && list.size() > 0) {
            for (MsgConfig.ContentBean.DataBean item : list) {
                ExCacheMsgBean bean = new ExCacheMsgBean(item);
                mMessageAdapter.addPushMsgItem(bean);
            }
            initUnreadList();
        }
    }

    private static class MsgHandler extends Handler {
        WeakReference<MsgListFragment> weakReference;

        public MsgHandler(MsgListFragment fragment) {
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            MsgListFragment fragment = weakReference.get();
            if (fragment == null || fragment.getActivity() == null
                    || fragment.getActivity().isFinishing()) {
                return;
            }
            switch (msg.what) {
                case RELOGIN_HUXIN_SERVER:
                    HuxinSdkManager.instance().imReconnect();
                    break;
                case LOAD_PROGRESS_DISMISS:
                    break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        HuxinSdkManager.instance().chatMsgFromCache(this,
                new ProtoCallback.CacheMsgCallBack() {
                    @Override
                    public void result(List<ExCacheMsgBean> data) {
                        mMessageAdapter.changeMessageList(data);
                        initUnreadList();
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeModel = new HomeModel(mActivity);
        initView(view);
        reqPushMsg();
        initDialog();
        initNet();
        initSetLoginPwd();
//        boolean isClearHomeData = SharedPreferencesUtils.getInstance().getBooleanData(SpConstants.UserModel.HOMEDATACLEAR, false);
//        if (!isClearHomeData) {
//            SharedPreferencesUtils.getInstance().saveStringData(SpConstants.UserModel.HOMEDATA, "");
//            SharedPreferencesUtils.getInstance().saveBooleanData(SpConstants.UserModel.HOMEDATACLEAR, true);
//        }
        String cacheData = SharedPreferencesUtils.getInstance().getStringData(SpConstants.UserModel.HOMEDATA, "");
        if (!TextUtils.isEmpty(cacheData)) {
            initSetData(cacheData);
        }
    }

    private void initSetLoginPwd() {
        boolean isHavePwd = SharedPreferencesUtils.getInstance().getBooleanData(SpConstants.UserModel.NoHAVEPWD, false);
        if (isHavePwd) {//没有设置密码，则每次启动APP提示
            DialogFactory.getInstance().showSingleNoCancelDialog(mActivity, "重要提示", "您的账号尚未设置登录密码," +
                    "为了更好提升您的体验，请前往设置", "前往设置");
        }
    }

    private void initNet() {
        if (null == netWorkStateReceiver) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        netWorkStateReceiver.setmNetStatusListener(status -> {
            if (-1 == status) {
                rl_home_internet.setVisibility(View.VISIBLE);
            } else {
                rl_home_internet.setVisibility(View.GONE);
                initRequestScanData();
            }
        });
    }

    private void initRequestScanData() {
        String scanData = SharedPreferencesUtils.getInstance().getStringData(SpConstants.UserModel.SCANCODEOFFDATA, "");
        if (!TextUtils.isEmpty(scanData)) {
            ScanCodeTimeEntity timeEntity = new ScanCodeTimeEntity();
            timeEntity = GsonUtils.gsonToBean(scanData, ScanCodeTimeEntity.class);
            scanOffList.clear();
            scanOffList.addAll(timeEntity.getScandata());
            for (int i = 0; i < scanOffList.size(); i++) {
                homeModel.postOfflineScan(3, scanOffList.get(i).getUrl(), "", scanOffList.get(i).getTime(),
                        "1", false, this);
            }
        }
    }

    private void initDialog() {
        homeModel.getHomeDialog(0, this);
    }


    private void creatreDialog(HomeDialogEntitiy homeDialogEntitiy) {
        RecyclerView rv_homedialog;
        HomeDialogAdapter dialogAdapter = null;
        if (dialog == null) {
            DisplayMetrics metrics = Tools.getDisplayMetrics(mContext);
            dialog = new AlertDialog.Builder(mContext).create();
            dialog.setCancelable(true);
            Window window = dialog.getWindow();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            LinearLayout layout = (LinearLayout) LayoutInflater.from(mContext)
                    .inflate(R.layout.home_dialog_layout, null);
            TextView dialog_msg = layout.findViewById(R.id.dialog_msg);
            TextView dialog_title = layout.findViewById(R.id.dialog_title);
            dialog_msg.setText(homeDialogEntitiy.getContent().getContent());
            if (!TextUtils.isEmpty(homeDialogEntitiy.getContent().getTitle())) {
                dialog_title.setText(homeDialogEntitiy.getContent().getTitle());
                dialog_title.setVisibility(View.VISIBLE);
            }
            rv_homedialog = layout.findViewById(R.id.rv_homedialog);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rv_homedialog.setLayoutManager(layoutManager);
            if (null == dialogAdapter) {
                dialogAdapter = new HomeDialogAdapter(mContext, homeDialogEntitiy.getContent().getButton());
                rv_homedialog.setAdapter(dialogAdapter);
            } else {
                dialogAdapter.setData(homeDialogEntitiy.getContent().getButton());
            }
            dialogAdapter.setFragmentMineCallBack(new FragmentMineCallBack() {
                @Override
                public void getData(String result, int positon) {
                    try {
                        if (!TextUtils.isEmpty(homeDialogEntitiy.getContent().getButton().get(positon).getUrl())) {
                            AuthTimeUtils authTimeUtils = new AuthTimeUtils();
                            authTimeUtils.IsAuthTime(mActivity, homeDialogEntitiy.getContent().getButton().get(positon).getUrl(),
                                    homeDialogEntitiy.getContent().getButton().get(positon).getAuth_type(), "");
                        }
                    } catch (Exception ignored) {
                    }
                    homeModel.getConfirmDialog(3, dialogUuid, homeDialogEntitiy.getContent().getButton().get(positon).getState(), MsgListFragment.this::OnHttpResponse);
                    dialog.dismiss();
                }
            });
            window.setContentView(layout);
            WindowManager.LayoutParams p = window.getAttributes();
//            p.width = ((int) (metrics.widthPixels - 80 * metrics.density));
            float aaa = metrics.density;
            p.width = ((int) (metrics.widthPixels) / 10 * 7);
            //p.height = (int) (120 * metrics.density);
            window.setAttributes(p);
        }
        dialog.show();
    }


    private void initView(View rootView) {

        View header_item = rootView.findViewById(R.id.list_item_header_search_root);
        iv_contactfragment_scan = rootView.findViewById(R.id.iv_contactfragment_scan);
        rl_home_internet = rootView.findViewById(R.id.rl_home_internet);
        rl_home_nomsg = rootView.findViewById(R.id.rl_home_nomsg);
        iv_contactfragment_scan.setOnClickListener(this);
        rl_home_internet.setOnClickListener(this);
        header_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GlobalSearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });


        mHandler = new MsgHandler(this);
        recyclerView = (XRecyclerView) rootView.findViewById(R.id.message_refresh_recycler);
        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageAdapter = new MessageAdapter(getActivity());
        recyclerView.setRefreshProgressStyle(AVLoadingIndicatorView.BallPulse);
        recyclerView.setAdapter(mMessageAdapter);
        recyclerView.setLoadingMoreEnabled(false);
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mHandler.removeMessages(RELOGIN_HUXIN_SERVER);
                mHandler.sendEmptyMessageDelayed(RELOGIN_HUXIN_SERVER, 1000);
                new Handler().postDelayed(() -> recyclerView.refreshComplete(), 1000);
            }

            @Override
            public void onLoadMore() {
            }
        });
        mMessageAdapter.setOnItemClickListener(new MessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ExCacheMsgBean bean, int position) {
                if (bean.getUiType() == MessageAdapter.ADAPTER_TYPE_PUSHMSG) {
                    Intent intent = new Intent(getActivity(), DeskTopActivity.class);
                    intent.putExtra(DeskTopActivity.DESKTOP_WEIAPPCODE, bean.getPushMsg());
                    startActivityForResult(intent, 3000);
                } else if (bean.getUiType() == MessageAdapter.ADAPTER_TYPE_SINGLE) {//单聊

                    Intent intent = new Intent(getActivity(), IMConnectionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra(IMConnectionActivity.DST_UUID, bean.getTargetUuid());
                    intent.putExtra(IMConnectionActivity.DST_NAME, bean.getDisplayName());
                    intent.putExtra(IMConnectionActivity.DST_USERNAME, bean.getTargetUserName());
                    intent.putExtra(IMConnectionActivity.DST_AVATAR, bean.getTargetAvatar());

                    startActivity(intent);
                } else if (bean.getUiType() == MessageAdapter.ADAPTER_TYPE_GROUP) {   //群聊

                    Intent intent = new Intent(getActivity(), IMGroupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    int groupId = bean.getGroupId();

                    intent.putExtra(IMGroupActivity.DST_UUID, groupId);
                    intent.putExtra(IMGroupActivity.DST_NAME, bean.getDisplayName());

                    startActivity(intent);
                }
            }
        });

        mMessageAdapter.setOnLongItemClickListener(new MessageAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View v, ExCacheMsgBean bean) {
                if (bean.getUiType() == MessageAdapter.ADAPTER_TYPE_SINGLE
                        || bean.getUiType() == MessageAdapter.ADAPTER_TYPE_GROUP) {
                    topPopUp(v, bean, "");
                } else {//公告审批置顶
                    topPopUp(v, bean, bean.getPushMsg().getApp_uuid());
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3000) {
            if (resultCode == 3001) {
                reqPushMsg();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        HuxinSdkManager.instance().setImMsgCallback(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(netWorkStateReceiver, intentFilter);
    }


    @Override
    public void onPause() {
        super.onPause();
        HuxinSdkManager.instance().removeImMsgCallback(this);
        mContext.unregisterReceiver(netWorkStateReceiver);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {//处理dismiss 时崩溃
            mHandler.removeMessages(LOAD_PROGRESS_DISMISS);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void topPopUp(View v, ExCacheMsgBean bean, String app_uuid) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.hx_im_del_lay, null);
        final PopupWindow popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(v, 400, 0);
        TextView tv_del = view.findViewById(R.id.tv_del);
        TextView tv_top = view.findViewById(R.id.tv_top);
        RelativeLayout rl_del = view.findViewById(R.id.rl_del);
        if (null != bean.getPushMsg() && "tzgg".equals(bean.getPushMsg().getApp_id())) {
            rl_del.setVisibility(View.GONE);
        }
        String targetUuid = bean.getTargetUuid();
        boolean isTop = HuxinSdkManager.instance().getMsgTop(targetUuid);

        if (isTop) {
            tv_top.setText("取消置顶");
        } else {
            tv_top.setText("置顶");
        }

        tv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delMsgChat(targetUuid, app_uuid);
                popupWindow.dismiss();
            }
        });

        tv_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topPushMsg(isTop, bean);
                popupWindow.dismiss();
            }
        });

    }


    public void topPushMsg(boolean isTop, ExCacheMsgBean bean) {
        String targetUuid = bean.getTargetUuid();
        if (isTop) {
            HuxinSdkManager.instance().removeMsgTop(targetUuid);
            bean.setTop(false);
        } else {
            HuxinSdkManager.instance().setMsgTop(targetUuid);
            bean.setTop(true);
        }
        mMessageAdapter.refreshTopMsg(bean);
    }


    public void delMsgChat(String targetUuid, String app_uuid) {
        homeModel.postDelAppMsg(2, app_uuid, this);
        CacheMsgHelper.instance().deleteAllMsg(getActivity(), targetUuid);
        //去掉未读消息计数
        IMMsgManager.instance().removeBadge(targetUuid);
        mMessageAdapter.deleteMessage(targetUuid);
    }


    public void reqPushMsg() {
        if (null == homeModel) {
            homeModel = new HomeModel(mActivity);
        }
        homeModel.getHomeMsg(1, this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && null != mActivity) {
            reqPushMsg();
        }
    }

    /**
     * IM消息的入口回调
     *
     * @param imComingMsg
     */
    @Override
    public void onCallback(CacheMsgBean imComingMsg) {
        if (imComingMsg != null) {
            ExCacheMsgBean bean = new ExCacheMsgBean(imComingMsg);

            String targetId = bean.getTargetUuid();

            boolean isTop = HuxinSdkManager.instance().getMsgTop(targetId);
            if (isTop) {
                bean.setTop(true);
            }

            bean.setDisplayName(imComingMsg.getTargetName());
            mMessageAdapter.addTop(bean);

            if (getActivity() instanceof MainActivity) {
                MainActivity act = (MainActivity) getActivity();
                act.refreshUnReadCount();
            }
            initUnreadList();
        }
    }

    private void initUnreadList() {
        List<ExCacheMsgBean> list = mMessageAdapter.getMsgList();
        if (list.size() > 0) {
            rl_home_nomsg.setVisibility(View.GONE);
        } else {
            rl_home_nomsg.setVisibility(View.VISIBLE);
        }
        unReadListPosition.clear();
        for (int i = 0; i < list.size(); i++) {
            ExCacheMsgBean item = list.get(i);
            int count = IMMsgManager.instance().getBadeCount(item.getTargetUuid());
            if (count > 0 && !unReadListPosition.contains(i)) {
                unReadListPosition.add(i);
            }
        }
    }

    public void scrollToNextUnRead() {
        if (curPostion < unReadListPosition.size()) {
            int index = unReadListPosition.get(curPostion) + 1;
            mLinearLayoutManager.scrollToPositionWithOffset(index, 0);
            curPostion++;
        } else {
            curPostion = 0;
        }
    }

    @Subscribe
    public void onEvent(Object event) {
        final Message message = (Message) event;
        switch (message.what) {
            case Contants.EVENT.changeCorp:
                reqPushMsg();
                break;

        }
    }

}
