package com.tg.coloursteward.meassage;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tg.coloursteward.R;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.activity.IMConnectionActivity;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.im.IMMsgCallback;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;
import com.youmai.hxsdk.router.APath;
import com.youmai.hxsdk.utils.ToastUtil;
import com.youmai.hxsdk.view.refresh.OnRecyclerScrollListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 主页-沟通
 * A simple {@link Fragment} subclass.
 */
public class MsgListFragment extends Fragment implements IMMsgCallback, LoaderManager.LoaderCallbacks<List<ExCacheMsgBean>> {
    //刷新未读或者开启权限都重新获取数据
    public static final int INTENT_REQUEST_FOR_UPDATE_UI = 101;
    public static final int INTENT_REQUEST_PERMISSION = 102;

    private final String TAG = MsgListFragment.class.getSimpleName();
    private final int LOADER_ID_GEN_MESSAGE_LIST = 100;

    //同步通话记录成功标记
    private static final int INSERT_CALLLOG_SUCCESS = 100;
    //同步通话记录失败
    private static final int INSERT_CALLLOG_FAILED = 101;
    //获取数据库记录为空
    private static final int GET_DABABASE_DATA_FAILED = 400;
    //重新登录或重新连接
    private static final int RELOGIN_HUXIN_SERVER = 2000;

    //progress加载完成显示进度
    private static final int LOAD_PROGRESS_DISMISS = 200;

    // incoming new message start
    private HandlerThread mIncomingMessageHandlerThread;
    private Handler mIncomingMessageHandler;
    private final int NEW_MESSAGE_UPDATE = 11;
    private final int NEW_MESSAGE_SET_TOP = 12;
    private final int NEW_MESSAGE_PROCESS = 13;
    private List<CacheMsgBean> mIncomingMsgList = new ArrayList<>();
    // incoming new message end

    boolean isTipWindowShow = true;

    private XRecyclerView mRefreshRecyclerView;
    /*********此处从XRecyclerView源码 onBindViewHolder得知，POSITION被修改，不知原因*******/
    private int adjSize = 1;
    /*********此处从XRecyclerView源码 onBindViewHolder得知，POSITION被修改，不知原因*******/
    private MessageAdapter mMessageAdapter;
    private List<ExCacheMsgBean> messageList;

    private ProgressDialog mProgressDialog;
    private Handler mHandler;
    // 换号登录需要去判断改变
    private String mCurrPhoneNum = "";
    private LinearLayoutManager mLinearLayoutManager;

    // 空页面 start
    private LinearLayout mEmptyView;
    private LinearLayout mSearchEmptyView;
    private EmptyRecyclerViewDataObserver mEmptyRecyclerViewDataObserver = new EmptyRecyclerViewDataObserver();
    // 空页面 end

    //接收通讯录改变的广播
    private BroadcastReceiver mUpdateContactNameReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //initMessageList();
        }
    };

    private OnRecyclerScrollListener mOnRecyclerScrollListener = new OnRecyclerScrollListener() {
        @Override
        public void onStateChanged(RecyclerView recyclerView, int newState) {

        }

        @Override
        public void onScrollUp(RecyclerView recyclerView, int dy) {
        }

        @Override
        public void onScrollDown(RecyclerView recyclerView, int dy) {
        }

        @Override
        public void onScrollToTop() {
        }

        @Override
        public void onScrollToBottom() {
        }
    };

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
                //获取记录成功
                case INSERT_CALLLOG_SUCCESS: {
                    fragment.reloadMessageList();
                    //刷新数据
                    fragment.dismissProgress("成"/*getString(R.string.sync_callLog_success)*/);
                }
                break;
                //获取通话记录失败 同步通讯了成功 插入数据库失败
                case INSERT_CALLLOG_FAILED: {
                    fragment.dismissProgress("败"/*getString(R.string.sync_callLog_success_insert_failed)*/);
                }
                break;
                //获取消息数据库数据为空！加载本地通话记录
                case GET_DABABASE_DATA_FAILED: {
                    //getCallLogData();
                }
                break;
                case RELOGIN_HUXIN_SERVER: {
                    HuxinSdkManager.instance().imReconnect();
                }
                break;
                case LOAD_PROGRESS_DISMISS: {
                    if (fragment.mProgressDialog != null) {
                        fragment.mProgressDialog.dismiss();
                        fragment.mProgressDialog = null;
                    }
                }
                break;
            }
        }
    }

    public MsgListFragment() {
        // Required empty public constructor
        startIncomingMessageProcess();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

        initMessageList();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        IMMsgManager.getInstance().removeImMsgCallback(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        IMMsgManager.getInstance().setImMsgCallback(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (INTENT_REQUEST_FOR_UPDATE_UI == requestCode) {
//            Log.e(TAG, "onActivityResult");
//            if (data != null) { //刷新未读标记
//                String upPhone = data.getStringExtra("updatePhone");
//                if (TextUtils.isEmpty(upPhone)) {
//                    return;
//                }
//
//                //呼信小助手处理 start
//                if (upPhone.equals(HUXIN_ASSISTANT_NUMBER)) {
//                    mHuxinAssistant.setVisibility(View.GONE);
//                    return;
//                }
//                //呼信小助手处理 end
//
//                int deleteType = data.getIntExtra("isDeleteMsgType", 0);
//                int index = 0;
//                int count = messageList.size();
//                for (index = 0; index < count; index++) {
//                    ExCacheMsgBean cacheMsgBean = messageList.get(index);
//                    if (cacheMsgBean.getPhone().equals(upPhone)) {
//                        //聊天界面删除记录的情况
//                        if (0 != deleteType) {
//                            List<CacheMsgBean> lastMsg = CacheMsgHelper.instance(getActivity()).toQueryLastMsgByPhone(upPhone);
//                            if (lastMsg == null || lastMsg.size() <= 0) {
//                                messageList.remove(index);
//                                mMessageAdapter.notifyItemRemoved(index + mMessageAdapter.getHeaderCount() + adjSize);
//                                return;
//                            }
//                            CacheMsgBean newMsgBean = lastMsg.get(0);
//                            cacheMsgBean.setMsgTime(newMsgBean.getMsgTime());
//                            cacheMsgBean.setContentJsonBody(newMsgBean.getContentJsonBody());
//                            cacheMsgBean.setMsgType(newMsgBean.getMsgType());
//                            break;
//                        } else {
//                            // 设为已读
//                            messageList.get(index).setIs_read(CacheMsgBean.MSG_READ_STATUS);
//                            break;
//                        }
//                    }
//                }
//                if (index >= 0 && index < (count - 1)) {
//                    mMessageAdapter.notifyDataSetChanged();//notifyItemChanged(index + mMessageAdapter.getHeaderCount() + adjSize);
//                }
//            }
        } else if (INTENT_REQUEST_PERMISSION == requestCode) {
            //从开启权限页面返回 从新拉去数据
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState--" + outState.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopIncomingMessageProcess();

        unRegisterBroadcast();

        if (mHandler != null) {//处理dismiss 时崩溃
            mHandler.removeMessages(LOAD_PROGRESS_DISMISS);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mMessageAdapter != null) {
            mMessageAdapter.unregisterAdapterDataObserver(mEmptyRecyclerViewDataObserver);
        }
    }

    private void initView(View rootView) {
        mHandler = new MsgHandler(this);
        mRefreshRecyclerView = (XRecyclerView) rootView.findViewById(R.id.message_refresh_recycler);

        //监听列表滚动 隐藏键盘
        /*mRefreshRecyclerView.setOnRecyclerScrollListener(mOnRecyclerScrollListener);
        mRefreshRecyclerView.setRefreshEnable(true);*/
        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRefreshRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageAdapter = new MessageAdapter(getActivity());
        mMessageAdapter.setAdapterType(MessageAdapter.ADAPTER_TYPE_HEADER);
        mMessageAdapter.setShowRightIcon(false);
        mEmptyView = (LinearLayout) rootView.findViewById(R.id.message_empty_view);
        mRefreshRecyclerView.setAdapter(mMessageAdapter);
        mRefreshRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                //refresh data here
                mHandler.removeMessages(RELOGIN_HUXIN_SERVER);
                mHandler.sendEmptyMessageDelayed(RELOGIN_HUXIN_SERVER, 1000);
                mRefreshRecyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                // load more data here
            }
        });
        mRefreshRecyclerView.setLoadingMoreEnabled(false);
        messageList = new ArrayList<>();
        mSearchEmptyView = (LinearLayout) rootView.findViewById(R.id.message_search_empty_view);

        mMessageAdapter.registerAdapterDataObserver(mEmptyRecyclerViewDataObserver);

        mMessageAdapter.setOnItemClickListener(new MessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ExCacheMsgBean bean, int position) {
                if (position == 0) {
                    ARouter.getInstance()
                            .build(APath.SEARCH_GLOBAL_GROUP)
                            .navigation();
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(getActivity(), IMConnectionActivity.class);
                intent.putExtra(IMConnectionActivity.DST_PHONE, bean.getTargetPhone());
                intent.putExtra(IMConnectionActivity.DST_NAME, bean.getDisplayName());
                startActivityForResult(intent, INTENT_REQUEST_FOR_UPDATE_UI);
            }
        });

        mMessageAdapter.setOnLongItemClickListener(new MessageAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                ToastUtil.showToast(getContext(), "删除成功：" + position);
                ExCacheMsgBean cacheMsgBean = mMessageAdapter.getMessageList().get(position);
                mMessageAdapter.deleteMessage(position);
                CacheMsgHelper.instance(getActivity()).deleteAllMsg(cacheMsgBean.getTargetPhone());
                //去掉未读消息计数
                IMMsgManager.getInstance().removeBadge(cacheMsgBean.getTargetPhone());
            }
        });

        registerBroadcast();
    }

    private void registerBroadcast() {
        //注册广播接收器来接收  通讯录改变的广播
        IntentFilter homeFilter = new IntentFilter("com.youmai.hxsdk.updatecontact");
        getActivity().registerReceiver(mUpdateContactNameReceiver, homeFilter);
    }

    private void unRegisterBroadcast() {
        getActivity().unregisterReceiver(mUpdateContactNameReceiver);
    }

    private void showProgress(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(msg);
        }

        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            try {
                mProgressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void dismissProgress(final String mes) {
        dismissProgress(mes, 1000);
    }

    private void dismissProgress(final String mes, int delayed) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            try {
                if (mes != null) {
                    mProgressDialog.setMessage(mes);
                }
                mHandler.sendEmptyMessageDelayed(LOAD_PROGRESS_DISMISS, delayed);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initMessageList() {
        if (HuxinSdkManager.instance().getPhoneNum().equals("")) {
            return;
        }
        if (getLoaderManager().getLoader(LOADER_ID_GEN_MESSAGE_LIST) == null) {
            startLoading();
        } else {
            getLoaderManager().restartLoader(LOADER_ID_GEN_MESSAGE_LIST, null, this);
        }
    }

    public void reloadMessageList() {
        if (HuxinSdkManager.instance().getPhoneNum().equals("")) {
            return;
        }
        if (getLoaderManager().getLoader(LOADER_ID_GEN_MESSAGE_LIST) == null) {
            startLoading();
        } else {
            getLoaderManager().getLoader(LOADER_ID_GEN_MESSAGE_LIST).startLoading();
        }
    }

    /**
     * IM消息的入口回调
     *
     * @param imComingMsg
     */
    @Override
    public void onCallback(CacheMsgBean imComingMsg) {
        if (imComingMsg != null & mIncomingMsgList != null) {
            mIncomingMsgList.add(imComingMsg);
            ExCacheMsgBean bean = new ExCacheMsgBean(imComingMsg);
            bean.setDisplayName(imComingMsg.getTargetPhone());
            mMessageAdapter.addTop(bean);
        }
    }

    //在切换页面中  监听当前Fragment的显示和隐藏
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isTipWindowShow = isVisibleToUser;
        if (isVisibleToUser) {

        }
    }

    protected void startLoading() {
        getLoaderManager().initLoader(LOADER_ID_GEN_MESSAGE_LIST, null, this);
    }

    @Override
    public Loader<List<ExCacheMsgBean>> onCreateLoader(int id, Bundle args) {
        return new MsgAsyncTaskLoader(getContext(), false);
    }

    @Override
    public void onLoadFinished(Loader<List<ExCacheMsgBean>> loader, List<ExCacheMsgBean> data) {
        Log.d(TAG, "onLoadFinished" + data.toString());
        if (data.isEmpty()) {
            return;
        }
        if (!HuxinSdkManager.instance().getPhoneNum().equals("")) {
            if (messageList.isEmpty()) {
                messageList.addAll(data);
                mMessageAdapter.setMessageList(messageList);

            } else {
                int newSize = data.size();
                if (newSize > 0) {
                    List<ExCacheMsgBean> oldList = new ArrayList<>();
                    for (int newIndex = newSize - 1; newIndex >= 0; newIndex--) {
                        for (int i = 0; i < messageList.size(); i++) {
                            String targetPhone = data.get(newIndex).getTargetPhone();
                            if (!TextUtils.isEmpty(targetPhone) && targetPhone.equals(messageList.get(i).getTargetPhone())) {
                                oldList.add(messageList.get(i));
                                break;
                            }
                        }
                    }
                    messageList.removeAll(oldList);
                    messageList.addAll(0, data);
                    mMessageAdapter.notifyDataSetChanged();
                }
            }
            mCurrPhoneNum = HuxinSdkManager.instance().getPhoneNum();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ExCacheMsgBean>> loader) {
        Log.d(TAG, "onLoaderReset");
    }

    /**
     * 异步监听新消息的更新 - HandlerThread
     */
    private void startIncomingMessageProcess() {
        mIncomingMessageHandlerThread = new HandlerThread("MsgListFragmentImcomingMsg");
        mIncomingMessageHandlerThread.start();
        mIncomingMessageHandler = new Handler(mIncomingMessageHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //这个方法是运行在 handler-thread 线程中的 ，可以执行耗时操作
                switch (msg.what) {
                    case NEW_MESSAGE_PROCESS:
                        break;
                    case NEW_MESSAGE_UPDATE:

                        break;
                    case NEW_MESSAGE_SET_TOP:
                        mMessageAdapter.addTop((ExCacheMsgBean) msg.obj);
                        break;
                }
            }
        };
        mIncomingMessageHandler.sendEmptyMessageDelayed(NEW_MESSAGE_PROCESS, 1000 * 5);
    }

    private void stopIncomingMessageProcess() {
        if (mIncomingMessageHandler != null) {
            mIncomingMessageHandler.removeCallbacksAndMessages(null);
        }
        if (mIncomingMessageHandlerThread != null) {
            mIncomingMessageHandlerThread.quit();
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
        Log.e(TAG, "onSaveInstanceState--showGuide");
        if (mMessageAdapter != null) {
            int count = mMessageAdapter.getItemCount();

            //正常状态
            if (count == mMessageAdapter.getHeaderCount()) {
                mEmptyView.setVisibility(View.VISIBLE);
                mRefreshRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
                mRefreshRecyclerView.setVisibility(View.VISIBLE);
            }
            mSearchEmptyView.setVisibility(View.GONE);

        }
    }

}
