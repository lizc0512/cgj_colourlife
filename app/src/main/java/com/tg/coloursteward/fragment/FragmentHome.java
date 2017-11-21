package com.tg.coloursteward.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tg.coloursteward.DeskTopActivity;
import com.tg.coloursteward.InviteRegisterActivity;
import com.tg.coloursteward.MainActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.UserInfoActivity;
import com.tg.coloursteward.adapter.HomeDeskTopAdapter;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.GridViewInfo;
import com.tg.coloursteward.info.HomeDeskTopInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.inter.SingleClickListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.util.AuthTimeUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.ManageMentLinearlayout;
import com.tg.coloursteward.view.PullRefreshListViewFind;
import com.tg.coloursteward.view.RoundImageView;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 首页（城关）
 */

public class FragmentHome extends Fragment implements AdapterView.OnItemClickListener {
    private View mView;
    private static final int ISTREAD=1;
    private TextView tvName,tvJob,tvGeneral;
    private MainActivity mActivity;
    private PullRefreshListViewFind pullListView;
    private HomeDeskTopAdapter adapter;
    private ManageMentLinearlayout magLinearLayoutDoor,magLinearLayoutWork;
    private LinearLayout llRepairs,llComplaint;
    private ArrayList<GridViewInfo> gridlist1 = new ArrayList<GridViewInfo>();
    private ArrayList<HomeDeskTopInfo> listType = new ArrayList<HomeDeskTopInfo>();
    private Intent intent ;
    private int size;
    private String homeListCache;
    private RelativeLayout rlSaoYiSao;
    private RoundImageView ivHead;
    private LinearLayout llHomeHead;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    private HomeService homeService;
    private int readPosition = -1;
    private SingleClickListener singleListener = new SingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            // TODO Auto-generated method stub
            String url = null ;
            String oauthType = null;
            String developerCode = null;
            String clientCode = null;
            AuthTimeUtils mAuthTimeUtils;
            switch(v.getId()){
                case R.id.ll_repairs://新邮件
                    mAuthTimeUtils = new AuthTimeUtils();
                    break;
                case R.id.ll_door://工作审批
                    mAuthTimeUtils  = new AuthTimeUtils();
                    mAuthTimeUtils.IsAuthTime(mActivity,Contants.Html5.SP, "sp", "0", "sp","");
                    break;
                case R.id.ll_complaint://蜜蜂协同
                    mAuthTimeUtils  = new AuthTimeUtils();
                    mAuthTimeUtils.IsAuthTime(mActivity,Contants.Html5.CASE, "case", "1", "case","");
                    break;
                case R.id.ll_work://签到
                    mAuthTimeUtils  = new AuthTimeUtils();
                    mAuthTimeUtils.IsAuthTime(mActivity,Contants.Html5.QIANDAO, "qiandao", "1", "qiandao","");
                    break;
                case R.id.rl_saoyisao://扫一扫
                    startActivity(new Intent(mActivity,InviteRegisterActivity.class));
                    break;
                case R.id.iv_head://头像
                    startActivity(new Intent(mActivity,UserInfoActivity.class));
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        mView = inflater.inflate(R.layout.fragment_home_layout, container, false);
        initHomeNewsView(mView);
        initListener();
        requestData();
        return mView;
    }
    /**
     * 自定义消息盒子
     */
    private void setData (){
        HomeDeskTopInfo info;
        info = new HomeDeskTopInfo();
        info.id = 0;
        info.auth_type = 0;
        info.icon = "";
        info.owner_name = "暂无";
        info.owner_avatar = " ";
        info.modify_time = " ";
        info.title = "暂无新消息";
        info.source_id = " ";
        info.comefrom = "审批";
        info.url = "";
        info.client_code = "sp";
        info.notread = 0;
        listType.add(info);

        info = new HomeDeskTopInfo();
        info.id = 0;
        info.auth_type = 0;
        info.icon = "";
        info.owner_name = "暂无";
        info.owner_avatar = " ";
        info.modify_time = " ";
        info.title = "暂无新消息";
        info.source_id = " ";
        info.comefrom = "通知公告";
        info.url = "";
        info.client_code = "ggtz";
        info.notread = 0;
        listType.add(info);


        info = new HomeDeskTopInfo();
        info.id = 0;
        info.auth_type = 0;
        info.icon = "";
        info.owner_name = "暂无";
        info.owner_avatar = " ";
        info.modify_time = " ";
        info.title = "暂无新消息";
        info.source_id = " ";
        info.comefrom = "邮件";
        info.url = "";
        info.client_code = "yj";
        info.notread = 0;
        listType.add(info);

        info = new HomeDeskTopInfo();
        info.id = 0;
        info.auth_type = 0;
        info.icon = "";
        info.owner_name = "暂无";
        info.owner_avatar = " ";
        info.modify_time = " ";
        info.title = "暂无新消息";
        info.source_id = " ";
        info.comefrom = "蜜蜂协同";
        info.url = "";
        info.client_code = "case";
        info.notread =0;
        listType.add(info);

    }
    private void requestData() {
      //  magLinearLayoutDoor.loaddingData();
    }
    private void initHomeNewsView(View view) {
        initOptions();
        llHomeHead = (LinearLayout) mView.findViewById(R.id.ll_home_head);
        mView.findViewById(R.id.ll_repairs).setOnClickListener(singleListener);//在线报修
        mView.findViewById(R.id.ll_complaint).setOnClickListener(singleListener);//我要投诉
        magLinearLayoutDoor=(ManageMentLinearlayout) mView.findViewById(R.id.ll_door);
        magLinearLayoutWork =(ManageMentLinearlayout) mView.findViewById(R.id.ll_work);
        magLinearLayoutDoor.setOnClickListener(singleListener);
        magLinearLayoutWork.setOnClickListener(singleListener);
        pullListView=(PullRefreshListViewFind) mView.findViewById(R.id.pull_listview);
        //读取本地缓存列表
        getCacheList();
        pullListView.setOnItemClickListener(this);
        pullListView.setDividerHeight(0);
        pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListViewFind>() {
            @Override
            public void refreshData(PullRefreshListViewFind t, boolean isLoadMore,
                                    Message msg, String response) {
                // TODO Auto-generated method stub
                String jsonString = HttpTools.getContentString(response);
                if (StringUtils.isNotEmpty(jsonString)) {
                    Tools.saveHomeList(mActivity,response);
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
                        listType.add(item);
                    }
                    setData();
                    for (int i = 0; i < listType.size(); i++){ //外循环是循环的次数
                        for (int j = listType.size() - 1 ; j > i; j--)  //内循环是 外循环一次比较的次数
                        {

                            if (listType.get(i).client_code.equals(listType.get(j).client_code))
                            {
                                listType.remove(j);
                            }
                        }
                    }
                }else{
                    setData();
                    for (int i = 0; i < listType.size(); i++){ //外循环是循环的次数
                        for (int j = listType.size() - 1 ; j > i; j--)  //内循环是 外循环一次比较的次数
                        {
                            if (listType.get(i).client_code.equals(listType.get(j).client_code))
                            {
                                listType.remove(j);
                            }
                        }
                    }
                }
                getNotReadNum();
            }

            @Override
            public void onLoadingMore(PullRefreshListViewFind t, Handler hand, int pageIndex) {
                // TODO Auto-generated method stub
                RequestConfig config = new RequestConfig(mActivity,PullRefreshListViewFind.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("username", UserInfo.employeeAccount);
                params.put("corp_id", Tools.getStringValue(mActivity,Contants.storage.CORPID));
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/push2/homepush/gethomePushBybox",config, params);
            }

            @Override
            public void onLoading(PullRefreshListViewFind t, Handler hand) {
                // TODO Auto-generated method stub
                RequestConfig config = new RequestConfig(mActivity,PullRefreshListViewFind.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("username", UserInfo.employeeAccount);
                params.put("corp_id", Tools.getStringValue(mActivity,Contants.storage.CORPID));
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/push2/homepush/gethomePushBybox",config, params);
            }
        });
        /**
         * list添加头部
         */
        addHead();
        pullListView.setAdapter(adapter);
        pullListView.performLoading();
        pullListView.setNetPullRefreshOnScroll(new PullRefreshListViewFind.NetPullRefreshOnScroll() {

            @Override
            public void refreshOnScroll(AbsListView view, int firstVisibleItem,
                                        int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem > 0){
                    llHomeHead.setVisibility(View.VISIBLE);
                }else{
                    llHomeHead.setVisibility(View.GONE);
                }
            }
        });

    }
    private void initOptions()
    {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder2)
                .showImageForEmptyUri(R.drawable.placeholder2)
                .showImageOnFail(R.drawable.placeholder2).cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(true)
                .build();
    }
    /**
     * 获取首页缓存列表
     */
    private void getCacheList() {
        homeListCache = Tools.getHomeList(mActivity);
        if(StringUtils.isNotEmpty(homeListCache)){
            String jsonString = HttpTools.getContentString(homeListCache);
            if (StringUtils.isNotEmpty(jsonString)) {
                ResponseData data = HttpTools.getResponseData(jsonString);
                HomeDeskTopInfo item;
                for (int i = 0; i < data.length; i++) {
                    item = new HomeDeskTopInfo();
                    item.id = data.getInt(i, "id");
                    item.auth_type = data.getInt(i, "auth_type");
                    item.icon = data.getString(i, "app_icon_android");
                    item.owner_name = data.getString(i, "owner_name");
                    item.owner_avatar = data.getString(i, "owner_avatar");
                    item.modify_time = data.getString(i, "modify_time");
                    item.title = data.getString(i, "title");
                    item.source_id = data.getString(i, "source_id");
                    item.comefrom = data.getString(i, "comefrom");
                    item.url = data.getString(i, "url");
                    item.client_code = data.getString(i, "client_code");
                    item.notread = data.getInt(i, "notread");
                    listType.add(item);
                }
                setData();
                for (int i = 0; i < listType.size(); i++){ //外循环是循环的次数
                    for (int j = listType.size() - 1 ; j > i; j--)  //内循环是 外循环一次比较的次数
                    {
                        if (listType.get(i).client_code.equals(listType.get(j).client_code))
                        {
                            listType.remove(j);
                        }
                    }
                }
            }else{
                setData();
                for (int i = 0; i < listType.size(); i++){ //外循环是循环的次数
                    for (int j = listType.size() - 1 ; j > i; j--)  //内循环是 外循环一次比较的次数
                    {
                        if (listType.get(i).client_code.equals(listType.get(j).client_code))
                        {
                            listType.remove(j);
                        }
                    }
                }
            }
            pullListView.setAdapter(adapter);
            getNotReadNum();
        }
    }
    private void addHead(){
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View userinfoView = inflater.inflate(R.layout.fragment_home_item_userinfo, null);
        View serviceView = inflater.inflate(R.layout.fragment_home_item_2, null);
        pullListView.addHeaderView(userinfoView);
        pullListView.addHeaderView(serviceView);
		/*
		 * 第一个头部
		 */
        rlSaoYiSao=(RelativeLayout) userinfoView.findViewById(R.id.rl_saoyisao);
        ivHead=(RoundImageView) userinfoView.findViewById(R.id.iv_head);
        ivHead.setCircleShape();
        tvName=(TextView) userinfoView.findViewById(R.id.tv_name);
        tvJob=(TextView) userinfoView.findViewById(R.id.tv_job);
        tvGeneral=(TextView) userinfoView.findViewById(R.id.tv_general);
        rlSaoYiSao.setOnClickListener(singleListener);
        ivHead.setOnClickListener(singleListener);
        initData();
        tvName.setText(UserInfo.realname+"("+UserInfo.employeeAccount+")");
        tvJob.setText(UserInfo.jobName);
        tvGeneral.setText(UserInfo.familyName);
        /**
         * 第二个头部
         */
        llRepairs =(LinearLayout) serviceView.findViewById(R.id.ll_repairs);
        llComplaint =(LinearLayout) serviceView.findViewById(R.id.ll_complaint);
        magLinearLayoutDoor =(ManageMentLinearlayout)serviceView.findViewById(R.id.ll_door);
        magLinearLayoutWork =(ManageMentLinearlayout)serviceView.findViewById(R.id.ll_work);
        llRepairs .setOnClickListener(singleListener);//在线报修
        llComplaint .setOnClickListener(singleListener);////我要投诉
        magLinearLayoutDoor.setOnClickListener(singleListener);//任务工单
        magLinearLayoutWork.setOnClickListener(singleListener);//邮件管理
    }
    //更新UI
    public void freshUI(){
        initData();
        tvName.setText(UserInfo.realname+"("+UserInfo.employeeAccount+")");
        tvJob.setText(UserInfo.jobName);
        tvGeneral.setText(UserInfo.familyName);
    }

    /**
     * 初始化
     */
    private void initListener(){
        /**
         * 获取URl
         */
        magLinearLayoutDoor.setNetworkRequestListener(new ManageMentLinearlayout.NetworkRequestListener() {

            @Override
            public void onSuccess(ManageMentLinearlayout magLearLayout,Message msg, String response) {
                String  jsonString = HttpTools.getContentString(response);
                if (jsonString != null) {
                    ResponseData app_list = HttpTools.getResponseKey(jsonString,"app_list");
                    if (app_list.length > 0) {
                        Tools.saveElseInfo(mActivity, response);
                        JSONArray jsonArray = app_list.getJSONArray(0,"list");
                        ResponseData data = HttpTools.getResponseKeyJSONArray(jsonArray);
                        gridlist1 = new ArrayList<GridViewInfo>();
                        GridViewInfo item = null;
                        if(data.length % 4 == 0){
                            for (int i = 0; i < data.length; i++) {
                                try {
                                    item = new GridViewInfo();
                                    item.name = data.getString(i, "name");
                                    item.oauthType = data.getString(i, "oauthType");
                                    item.developerCode = data.getString(i, "app_code");
                                    item.clientCode = data.getString(i, "app_code");
                                    item.sso= data.getString(i,"url");
                                    JSONObject icon = data.getJSONObject(i,"icon");
                                    if(icon != null || icon.length() > 0){
                                        item.icon = icon.getString("android");
                                    }
                                    gridlist1.add(item);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }else{
                            for (int i = 0; i < data.length + 1; i++) {
                                try {
                                    if(i == data.length){
                                        item = new GridViewInfo();
                                    }else{
                                        item = new GridViewInfo();
                                        item.name = data.getString(i, "name");
                                        item.oauthType = data.getString(i, "oauthType");
                                        item.developerCode = data.getString(i, "app_code");
                                        item.clientCode = data.getString(i, "app_code");
                                        item.sso = data.getString(i,"url");
                                        JSONObject icon = data.getJSONObject(i,"icon");
                                        if(icon != null || icon.length() > 0){
                                            item.icon = icon.getString("android");
                                        }
                                        gridlist1.add(item);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
            }
            }

            @Override
            public void onFail(ManageMentLinearlayout magLearLayout, Message msg, String hintString) {

            }

            @Override
            public void onRequest(MessageHandler msgHand) {
                String pwd = Tools.getPassWord(mActivity);
                RequestConfig config = new RequestConfig(mActivity,0);
                config.handler = msgHand.getHandler();
                RequestParams params = new RequestParams();
                params.put("user_name", UserInfo.employeeAccount);
                params.put("password", pwd);
                params.put("resource", "app");
                params.put("cate_id",0);
                HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newoa/rights/list",config, params);
            }
        });
    }

    public void initData(){
        String str = Contants.URl.HEAD_ICON_URL +"avatar?uid=" + UserInfo.employeeAccount;
        imageLoader.clearMemoryCache();
        imageLoader.clearDiskCache();
        imageLoader.displayImage(str, ivHead, options);
    }
    /**
     * 首页消息盒子列表设置为已读，更新适配器
     */
    public void freshUIListType(){
        if(readPosition != -1){
            listType.get(readPosition).notread = 0;
            adapter.notifyDataSetChanged();
            getNotReadNum();
        }else{
            pullListView.performLoading();
        }
    }

    private void getNotReadNum(){
        int notNum = 0;
        for (int i = 0; i < listType.size(); i++){ //外循环是循环的次数
            notNum += listType.get(i).notread;
        }
        /**
         * 未读消息数量更新
         */
        MainActivity mainactivity = (MainActivity) getActivity();
        mainactivity.UpdataUnreadNum(notNum);
    }
    /**
     * 接受到首页推送消息，更新适配器
     */
    public void freshPushUIListType(ArrayList<HomeDeskTopInfo> list){
        listType.clear();
        listType.addAll(list);
        setData();
        for (int i = 0; i < listType.size(); i++){ //外循环是循环的次数
            for (int j = listType.size() - 1 ; j > i; j--)  //内循环是 外循环一次比较的次数
            {

                if (listType.get(i).client_code.equals(listType.get(j).client_code))
                {
                    listType.remove(j);
                }
            }
        }
        adapter.notifyDataSetChanged();
        getNotReadNum();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().register(this);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    /**
     * 设置为已读
     * @param Position
     */
    private  void readMsg(int Position){
        readPosition = Position;
        Intent intent = new  Intent(MainActivity.ACTION_READ_MESSAGEINFO);
        intent.putExtra("messageId", listType.get(Position).client_code);
        mActivity.sendBroadcast(intent);

    }
    @Subscribe
    public void onEvent(Object event) {
        final Message message = (Message) event;
        if (message.what == Contants.LOGO.CLEAR_HOMELIST){
            if(listType != null){
                listType.clear();
                pullListView.performLoading();
                adapter.notifyDataSetChanged();
            }
        }else if (message.what == Contants.LOGO.UPDATE_HOMELIST){
            int position = message.arg1;
            if( listType.get(position).notread > 0){
                readMsg(position);
            }
        }
    }
    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        size = getResources().getDimensionPixelSize(R.dimen.margin_88);
        mActivity = (MainActivity)activity;
        adapter = new HomeDeskTopAdapter(mActivity, listType);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if((int) parent.getAdapter().getItemId(position) != -1){
            HomeDeskTopInfo info = listType.get((int) parent.getAdapter().getItemId(position));
            Intent intent = new Intent(mActivity, DeskTopActivity.class);
            intent.putExtra(DeskTopActivity.DESKTOP_WEIAPPCODE, info);
            intent.putExtra(DeskTopActivity.DESKTOP_POSTION, (int) parent.getAdapter().getItemId(position));
            startActivity(intent);
        }else{
            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==ISTREAD){
            pullListView.performLoading();
        }
    }
}
