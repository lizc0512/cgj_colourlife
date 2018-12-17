package com.tg.coloursteward.module.meassage;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tg.coloursteward.DeskTopActivity;
import com.tg.coloursteward.InviteRegisterActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.entity.HomeDialogEntitiy;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.module.MainActivity1;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.TokenUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.PopWindowView;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.ProtoCallback;
import com.youmai.hxsdk.adapter.MessageAdapter;
import com.youmai.hxsdk.chatgroup.IMGroupActivity;
import com.youmai.hxsdk.chatsingle.IMConnectionActivity;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.data.ExCacheMsgBean;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.db.helper.CacheMsgHelper;
import com.youmai.hxsdk.entity.MsgConfig;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.http.OkHttpConnector;
import com.youmai.hxsdk.im.IMMsgCallback;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.search.GlobalSearchActivity;
import com.youmai.hxsdk.utils.GsonUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主页-沟通
 * A simple {@link Fragment} subclass.
 */
public class MsgListFragment extends Fragment implements IMMsgCallback, View.OnClickListener, MessageHandler.ResponseListener {

    private final String TAG = MsgListFragment.class.getSimpleName();
    private Activity mActivity;
    private MessageHandler msgHandler;
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

    // 空页面 start
    private LinearLayout mEmptyView;
    private EmptyRecyclerViewDataObserver mEmpty = new EmptyRecyclerViewDataObserver();
    // 空页面 end
    private ImageView iv_contactfragment_qrcode;
    private ImageView iv_contactfragment_scan;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_contactfragment_qrcode:
                startActivity(new Intent(mContext, InviteRegisterActivity.class));
                break;
            case R.id.iv_contactfragment_scan:
                PopWindowView popWindowView = new PopWindowView((Activity) mContext, null);
                popWindowView.setOnDismissListener(new PopupDismissListener());
                popWindowView.showPopupWindow(iv_contactfragment_scan);
                lightoff();
                break;
        }
    }

    private void lightoff() {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.3f;
        getActivity().getWindow().setAttributes(lp);
    }

    class PopupDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
            lp.alpha = 1.0f;
            getActivity().getWindow().setAttributes(lp);
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
        msgHandler = new MessageHandler(mActivity);
        msgHandler.setResponseListener(this);
        initView(view);
        reqPushMsg();
        initDialog();
    }

    private void initDialog() {
        RequestConfig config = new RequestConfig(mActivity, HttpTools.GET_DIALOG, "");
        config.handler = msgHandler.getHandler();
        Map<String, Object> map = new HashMap();
        Map<String, String> params = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(mActivity, map));
        HttpTools.httpGet_Map(Contants.URl.URL_NEW, "app/home/utility/getPopup", config, (HashMap) params);
    }

    @Override
    public void onRequestStart(Message msg, String hintString) {
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        int code = HttpTools.getCode(jsonString);
        if (msg.arg1 == HttpTools.GET_DIALOG) {
            if (code == 0) {
                try {
                    HomeDialogEntitiy homeDialogEntitiy = GsonUtils.gsonToBean(jsonString, HomeDialogEntitiy.class);
                    if (!TextUtils.isEmpty(homeDialogEntitiy.getContent().getContent())) {
                        if (null != homeDialogEntitiy.getContent().getButton() && homeDialogEntitiy.getContent().getButton().size() > 0) {
                            creatreDialog();
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    private void creatreDialog() {
        DialogFactory.getInstance().showDialog(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }, "content", "ok", "cancel");


    }

    @Override
    public void onFail(Message msg, String hintString) {
    }

    private void initView(View rootView) {

        View header_item = rootView.findViewById(R.id.list_item_header_search_root);
        iv_contactfragment_qrcode = rootView.findViewById(R.id.iv_contactfragment_qrcode);
        iv_contactfragment_scan = rootView.findViewById(R.id.iv_contactfragment_scan);
        iv_contactfragment_qrcode.setOnClickListener(this);
        iv_contactfragment_scan.setOnClickListener(this);
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
        mEmptyView = (LinearLayout) rootView.findViewById(R.id.message_empty_view);
        recyclerView.setAdapter(mMessageAdapter);
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                //refresh data here
                mHandler.removeMessages(RELOGIN_HUXIN_SERVER);
                mHandler.sendEmptyMessageDelayed(RELOGIN_HUXIN_SERVER, 1000);
                recyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                // load more data here
            }
        });
        recyclerView.setLoadingMoreEnabled(false);

        mMessageAdapter.registerAdapterDataObserver(mEmpty);

        mMessageAdapter.setOnItemClickListener(new MessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ExCacheMsgBean bean, int position) {
                if (bean.getUiType() == MessageAdapter.ADAPTER_TYPE_PUSHMSG) {
                    Intent intent = new Intent(getActivity(), DeskTopActivity.class);
                    intent.putExtra(DeskTopActivity.DESKTOP_WEIAPPCODE, bean.getPushMsg());
                    startActivity(intent);
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
                    delPopUp(v, bean);
                } else {//公告审批
                    TopPopUp(v, bean);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        HuxinSdkManager.instance().setImMsgCallback(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        HuxinSdkManager.instance().removeImMsgCallback(this);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        if (mMessageAdapter != null) {
            mMessageAdapter.unregisterAdapterDataObserver(mEmpty);
        }
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
    }

    /**
     * @param v
     * @param bean 审批公告类item置顶
     */
    private void TopPopUp(View v, ExCacheMsgBean bean) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.hx_im_top_lay, null);
        final PopupWindow popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(v, 400, 0);
        TextView tv_del = view.findViewById(R.id.tv_del);
        final int id = bean.getPushMsg().getId();
        final boolean isTop = HuxinSdkManager.instance().getMsgTop(id);
        if (isTop) {
            tv_del.setText("取消置顶");
        } else {
            tv_del.setText("置顶");
        }
        tv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTop) {
                    HuxinSdkManager.instance().removeMsgTop(id);
                } else {
                    HuxinSdkManager.instance().setMsgTop(id);
                }
                mMessageAdapter.setRefresh(id, isTop);
                popupWindow.dismiss();
            }
        });
    }

    private void delPopUp(View v, ExCacheMsgBean bean) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.hx_im_del_lay, null);
        final PopupWindow popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(v, 400, 0);
        TextView tv_del = (TextView) view.findViewById(R.id.tv_del);
        TextView tv_itemTop = (TextView) view.findViewById(R.id.tv_itemTop);
        final String targetUuid = bean.getTargetUuid();
        final boolean isTop = HuxinSdkManager.instance().getMsgTop(targetUuid);
        if (isTop) {
            tv_itemTop.setText("取消置顶");
        } else {
            tv_itemTop.setText("置顶");
        }
        tv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delMsgChat(targetUuid);
                popupWindow.dismiss();
            }
        });
        tv_itemTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTop) {
                    HuxinSdkManager.instance().removeMsgTop(targetUuid);
                } else {
                    HuxinSdkManager.instance().setMsgTop(targetUuid);
                }
                mMessageAdapter.setRefresh(targetUuid, isTop);
                popupWindow.dismiss();
            }
        });
    }

    public void delMsgChat(String targetUuid) {
        CacheMsgHelper.instance().deleteAllMsg(getActivity(), targetUuid);
        //去掉未读消息计数
        IMMsgManager.instance().removeBadge(targetUuid);
        mMessageAdapter.deleteMessage(targetUuid);
    }


    private void reqPushMsg() {
        String url = Contants.URl.URL_ICETEST + "/push2/homepush/gethomePushBybox";

        ContentValues params = new ContentValues();
        params.put("username", UserInfo.employeeAccount);
        params.put("corp_id", Tools.getStringValue(getContext(), Contants.storage.CORPID));

        ColorsConfig.commonParams(params);

        OkHttpConnector.httpGet(mContext,url, params, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                MsgConfig config = GsonUtil.parse(response, MsgConfig.class);
                if (config != null && config.isSuccess() && isAdded()) {
                    List<MsgConfig.ContentBean.DataBean> list = config.getContent().getData();
                    if (list != null && list.size() > 0) {
                        for (MsgConfig.ContentBean.DataBean item : list) {
                            ExCacheMsgBean bean = new ExCacheMsgBean(item);
                            mMessageAdapter.addPushMsgItem(bean);
                        }
                        initUnreadList();
                    }
                }
            }
        });
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

            if (getActivity() instanceof MainActivity1) {
                MainActivity1 act = (MainActivity1) getActivity();
                act.refreshUnReadCount();
            }
            initUnreadList();
        }
    }


    private class EmptyRecyclerViewDataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    }

    private void checkIfEmpty() {
        /*Log.e(TAG, "onSaveInstanceState--showGuide");
        if (mMessageAdapter != null) {
            int count = mMessageAdapter.getItemCount();
            //正常状态
            if (count == 1) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
        }*/
    }


    private void initUnreadList() {
        List<ExCacheMsgBean> list = mMessageAdapter.getMsgList();
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
}
