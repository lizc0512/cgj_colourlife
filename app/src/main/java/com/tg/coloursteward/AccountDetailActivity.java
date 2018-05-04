package com.tg.coloursteward;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.adapter.AccountDetailAdapter;
import com.tg.coloursteward.adapter.ViewPagerAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.AccountDetailInfo;
import com.tg.coloursteward.info.RedpacketsRecordInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.view.MyViewPager;
import com.tg.coloursteward.view.PullRefreshListView;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 即时分配明细
 */
public class AccountDetailActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {
    public  final static String TOTAL_ACCOUNT = "totalAccount";
    private AccountDetailAdapter adapter1;
    private AccountDetailAdapter adapter2;
    private MyViewPager viewPager;
    private RadioGroup radioGroup;
    private ViewPagerAdapter pagerAdapter;
    private ArrayList<View> pagerList = new ArrayList<View>();
    private PullRefreshListView listViewReceive;
    private PullRefreshListView listViewExpend;
    private ArrayList<AccountDetailInfo> list1 = new ArrayList<AccountDetailInfo>();
    private ArrayList<AccountDetailInfo> list2 = new ArrayList<AccountDetailInfo>();
    private String totalAccount ;
    private ImageView iv_notdata;
    private TextView tvTotalReceive,tvTotalExpend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null){
            totalAccount = intent.getStringExtra(TOTAL_ACCOUNT);
        }
        initView();
        radioGroup.setOnCheckedChangeListener(this);
        viewPager.setOnPageChangeListener(this);
    }
    private void initView(){
        radioGroup = (RadioGroup)findViewById(R.id.radio_group);
        viewPager = (MyViewPager)findViewById(R.id.viewPager);
        RadioButton btn1 = (RadioButton)findViewById(R.id.rb_noticBtn);
        RadioButton btn2 = (RadioButton)findViewById(R.id.rb_notifiicationBtn);
        btn1.setText("分成收入");
        btn2.setText("领取记录");
        /**
         * 分成收入
         */
        LayoutInflater inflater = (LayoutInflater) AccountDetailActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View accountView = inflater.inflate(R.layout.account_income, null);
        listViewReceive = (PullRefreshListView) accountView.findViewById(R.id.pull_listview);
        iv_notdata = (ImageView) accountView.findViewById(R.id.iv_notdata);
      //  listViewReceive = new PullRefreshListView(this);
        listViewReceive.setKeyName("Receive");
        listViewReceive.setDividerHeight(0);
        adapter1 = new AccountDetailAdapter(this, list1,0);
        listViewReceive.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
            @Override
            public void refreshData(PullRefreshListView t, boolean isLoadMore,
                                    Message msg, String response) {
                int code = HttpTools.getCode(response);
                String message = HttpTools.getMessageString(response);
                if(code == 0){
                    JSONArray content = HttpTools.getContentJsonArray(response);
                    if(content.length() > 0){
                        iv_notdata.setVisibility(View.GONE);
                        listViewReceive.setVisibility(View.VISIBLE);
                        ResponseData data = HttpTools.getResponseContent(content);
                        if(data.length > 0 ){
                            AccountDetailInfo info ;
                            for (int i = 0; i < data.length; i++) {
                                info = new AccountDetailInfo();
                                info.result_id = data.getString(i,"result_id");
                                info.app_id = data.getString(i,"app_id");
                                info.app_name = data.getString(i,"app_name");
                                info.tag_id = data.getString(i,"tag_id");
                                info.tag_name = data.getString(i,"tag_name");
                                info.station_uuid = data.getString(i,"station_uuid");
                                info.station_name = data.getString(i,"station_name");
                                info.target_type = data.getString(i,"target_type");
                                info.target = data.getString(i,"target");
                                info.target_display = data.getString(i,"target_display");
                                info.jr_account = data.getString(i,"jr_account");
                                info.money = data.getString(i,"money");
                                info.pay = data.getString(i,"pay");
                                info.tno = data.getString(i,"tno");
                                info.time_at = data.getString(i,"time_at");
                                list1.add(info);
                            }
                        }
                    }else {
                        iv_notdata.setVisibility(View.VISIBLE);
                        listViewReceive.setVisibility(View.GONE);
                    }
                }else{
                    ToastFactory.showToast(AccountDetailActivity.this, message);
                }
            }

            @Override
            public void onLoadingMore(PullRefreshListView t, Handler hand,
                                      int pagerIndex) {
                // TODO Auto-generated method stub
                RequestConfig config = new RequestConfig(AccountDetailActivity.this, PullRefreshListView.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("access_token", "1521ac83521b8063e7a9a49dc22e79b0");
                params.put("target_type", "2");
                params.put("target", UserInfo.employeeAccount);
                params.put("page", pagerIndex);
                params.put("pagesize", PullRefreshListView.PAGER_SIZE);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/splitdivide/api/bill",config, params);
            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                // TODO Auto-generated method stub
                RequestConfig config = new RequestConfig(AccountDetailActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("access_token", "1521ac83521b8063e7a9a49dc22e79b0");
                params.put("target_type", "2");
                params.put("target", UserInfo.employeeAccount);
                params.put("page", 1);
                params.put("pagesize", PullRefreshListView.PAGER_SIZE);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/splitdivide/api/bill",config, params);
            }
        });
        /**
         * list添加头部(Receive)
         */
        addHeadReceive();
        listViewReceive.setAdapter(adapter1);
        //pagerList.add(listViewReceive);
        pagerList.add(accountView);
        /**
         * 领取记录
         */
        listViewExpend = new PullRefreshListView(this);
        listViewExpend.setKeyName("Expend");
        listViewExpend.setDividerHeight(0);
        listViewExpend.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
            @Override
            public void refreshData(PullRefreshListView t, boolean isLoadMore,
                                    Message msg, String response) {
                int code = HttpTools.getCode(response);
                String message = HttpTools.getMessageString(response);
                if(code == 0){
                    String content = HttpTools.getContentString(response);
                    if(content.length() > 0){
                        ResponseData data = HttpTools.getResponseKey(content, "expend");
                        if(data.length > 0 ){
                            AccountDetailInfo info ;
                            for (int i = 0; i < data.length; i++) {
                                info = new AccountDetailInfo();
                                list2.add(info);
                            }
                        }
                    }
                }else{
                    ToastFactory.showToast(AccountDetailActivity.this, message);
                }
            }

            @Override
            public void onLoadingMore(PullRefreshListView t, Handler hand,
                                      int pagerIndex) {
                // TODO Auto-generated method stub
                RequestConfig config = new RequestConfig(AccountDetailActivity.this, PullRefreshListView.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("access_token", "1521ac83521b8063e7a9a49dc22e79b0");
                params.put("target_type", "1");
                params.put("target", "13971393183");
                params.put("page", pagerIndex);
                params.put("pagesize", PullRefreshListView.PAGER_SIZE);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/hongbao/redPacketExpend",config, params);
            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                // TODO Auto-generated method stub
                RequestConfig config = new RequestConfig(AccountDetailActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("access_token", "1521ac83521b8063e7a9a49dc22e79b0");
                params.put("target_type", "1");
                params.put("target", "13971393183");
                params.put("page", 1);
                params.put("pagesize", PullRefreshListView.PAGER_SIZE);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/hongbao/redPacketExpend",config, params);
            }
        });

        adapter2 = new AccountDetailAdapter(this, list2,1);
        /**
         * list添加头部(Expend)
         */
        addHeadExpend();
        listViewExpend.setAdapter(adapter2);
        /**
         * 暂未开通
         */
        inflater = (LayoutInflater) AccountDetailActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View receiveView = inflater.inflate(R.layout.receive_record, null);
        pagerList.add(receiveView);

        pagerAdapter = new ViewPagerAdapter(pagerList,this);
        viewPager.setAdapter(pagerAdapter);
        listViewReceive.performLoading();
    }
    /**
     * Receive添加头部
     */
    private void addHeadReceive() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View headView = inflater.inflate(R.layout.redpackets_recoed_head, null);
        listViewReceive.addHeaderView(headView);
        tvTotalReceive = (TextView) headView.findViewById(R.id.tv_total);
        if(StringUtils.isNotEmpty(totalAccount)){
            tvTotalReceive.setText("总收入："+totalAccount);
        }else{
            tvTotalReceive.setText("总收入："+0.00);
        }

    }
    /**
     * Expend添加头部
     */
    private void addHeadExpend() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View headView = inflater.inflate(R.layout.redpackets_recoed_head, null);
        listViewExpend.addHeaderView(headView);
        tvTotalExpend = (TextView) headView.findViewById(R.id.tv_total);
    }
    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int position) {
        if(position == 0){
            radioGroup.check(R.id.rb_noticBtn);
        }else{
            radioGroup.check(R.id.rb_notifiicationBtn);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == R.id.rb_noticBtn){
            if(viewPager.getCurrentItem() != 0){
                viewPager.setCurrentItem(0);
                listViewReceive.performLoading();
            }
        }else{
            if(viewPager.getCurrentItem() != 1){
                viewPager.setCurrentItem(1);
             //   listViewExpend.performLoading();
            }
        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_account_detail,null);
    }

    @Override
    public String getHeadTitle() {
        return "即时分配明细";
    }
}
