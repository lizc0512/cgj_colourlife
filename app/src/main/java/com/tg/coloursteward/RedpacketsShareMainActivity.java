package com.tg.coloursteward;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.adapter.HistoryTransferAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.HistoryTransferInfo;
import com.tg.coloursteward.info.RedpacketsInfo;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.updateapk.UpdateManager;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.PullRefreshListView;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 给同事发饭票页面
 *
 * @author Administrator
 */
public class RedpacketsShareMainActivity extends BaseActivity {
    private static final String TAG = "RedpacketsShareMainActi";
    /**
     * 同事OA账号或手机号码
     */
    private EditText edtColleagueInfo;

    /**
     * 下一步
     */
    private RelativeLayout rlSubmit;

    /**
     * 历史记录ListView
     */
    private PullRefreshListView pullListView;

    /**
     * 获取到的员工oa信息
     */
    private LinearLayout lloaInfo;

    /**
     * 获取到的员工oa姓名
     */
    private TextView tvOaName;

    /**
     * 获取到的员工oa用户名
     */
    private TextView tvOaUsername;

    /**
     * 获取到的员工oa手机号
     */
    private TextView tvOaMobile;

    /**
     * oa账号或手机号
     */
    private String colleagueInfo;
    /**
     * 给同事发红包（colleague）/转账到彩之云（czy）/提现到银行卡（bank）
     */
    private String transferTo;

    /**
     * 红包余额
     */
    private Double balance;


    private ImageButton btnNext;

    /**
     * oa发红包记录的Adapter
     */
    private HistoryTransferAdapter adapter;
    private String key;
    private String secret;
    private Intent intent;
    private int OA;
    final ArrayList<RedpacketsInfo> list = new ArrayList<RedpacketsInfo>();
    private ArrayList<HistoryTransferInfo> pageInfoList = new ArrayList<HistoryTransferInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPopup(false);
        transferTo = getIntent().getStringExtra("colleague");
        colleagueInfo = getIntent().getStringExtra(Contants.PARAMETER.OA);
        balance = getIntent().getDoubleExtra(Contants.PARAMETER.BALANCE, 0.00);
        key = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.key);
        secret = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.secret);
        initView();
        if (StringUtils.isNotEmpty(colleagueInfo)) {
            edtColleagueInfo.setText(colleagueInfo);
            check();
        }
    }

    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.imgbtn_contact://通讯录
                intent = new Intent(RedpacketsShareMainActivity.this, RedpacketsContactsActivity.class);
                startActivityForResult(intent, 1001);
                break;
            case R.id.rl_submit://下一步
                check();
                break;
        }
        return super.handClickEvent(v);
    }

    private void initView() {
        edtColleagueInfo = (EditText) findViewById(R.id.edt_colleague_info);
        lloaInfo = (LinearLayout) findViewById(R.id.ll_oa_info);
        tvOaName = (TextView) findViewById(R.id.tv_oa_name);
        tvOaUsername = (TextView) findViewById(R.id.tv_oa_username);
        tvOaMobile = (TextView) findViewById(R.id.tv_oa_mobile);
        rlSubmit = (RelativeLayout) findViewById(R.id.rl_submit);
        btnNext = (ImageButton) findViewById(R.id.imgbtn_contact);
        rlSubmit.setOnClickListener(singleListener);
        btnNext.setOnClickListener(singleListener);
        lloaInfo.setVisibility(View.GONE);

        pullListView = (PullRefreshListView) findViewById(R.id.pull_listview);
        pullListView.setDividerHeight(0);
        adapter = new HistoryTransferAdapter(this, pageInfoList);
        pullListView.setAdapter(adapter);
        pullListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HistoryTransferInfo info = pageInfoList.get(position);
                Intent intent = new Intent(RedpacketsShareMainActivity.this,
                        RedpacketsTransferMainActivity.class);
                intent.putExtra(Contants.PARAMETER.TRANSFERTO, "colleague");
                intent.putExtra(Contants.PARAMETER.BALANCE, balance);
                intent.putExtra("name", info.receiverName);
                intent.putExtra("username", info.receiverOA);
                intent.putExtra(Contants.PARAMETER.MOBILE, info.receiverMobile);
                intent.putExtra(Contants.PARAMETER.USERID, info.receiver_id);
                startActivity(intent);
            }
        });
        pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {

            @Override
            public void refreshData(PullRefreshListView t, boolean isLoadMore,
                                    Message msg, String response) {
                int code = HttpTools.getCode(response);
                if (code == 0) {
                    JSONObject content = HttpTools.getContentJSONObject(response);
                    String sponse = HttpTools.getContentString(response);
                    if (content != null) {
                        try {
                            if (content.getJSONArray("CarryJsonList") != null) {
                                ResponseData data = HttpTools.getResponseKey(sponse, "CarryJsonList");
                                HistoryTransferInfo info;
                                for (int i = 0; i < data.length; i++) {
                                    info = new HistoryTransferInfo();
                                    info.receiver_id = data.getString(i, "receiver_id");
                                    info.receiverName = data.getString(i, "receiverName");
                                    info.receiverOA = data.getString(i, "receiverOA");
                                    info.receiverMobile = data.getString(i, "receiverMobile");
                                    pageInfoList.add(info);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onLoadingMore(PullRefreshListView t, Handler hand, int pageIndex) {
                RequestConfig config = new RequestConfig(RedpacketsShareMainActivity.this, PullRefreshListView.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("key", key);
                params.put("secret", secret);
                params.put("page", pageIndex);
                params.put("pagesize", PullRefreshListView.PAGER_SIZE);
//                HttpTools.httpGet(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/carryList", config, params);

            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                RequestConfig config = new RequestConfig(RedpacketsShareMainActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("key", key);
                params.put("secret", secret);
                params.put("page", 1);
                params.put("pagesize", PullRefreshListView.PAGER_SIZE);
//                HttpTools.httpGet(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/carryList", config, params);
            }
        });
        pullListView.performLoading();
    }

    /**
     * 根据输入手机号获oa账号搜索
     *
     * @param username
     */
    private void getEmployeeInfo(String username) {
//		这个接口改造过
        String versionShort = UpdateManager.getVersionName(this);
        String key = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.key);
        String secret = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.secret);
        RequestConfig config = new RequestConfig(RedpacketsShareMainActivity.this, HttpTools.GET_EMPLOYEE_INFO, "查询");
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("single", "1");
        params.put("version", versionShort);
        params.put("key", key);
        params.put("secret", secret);//接口txl2/contacts/search
//        HttpTools.httpPost(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/getEmployeeInfo", config, params);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (code == 0) {
            JSONArray content = HttpTools.getContentJsonArray(jsonString);
            if (content != null) {
                ResponseData data = HttpTools.getResponseContent(content);
                if (data.length > 0) {
                    RedpacketsInfo info;
                    for (int i = 0; i < data.length; i++) {
                        info = new RedpacketsInfo();
                        info.receiver_id = data.getString(i, "id");
                        info.receiverName = data.getString(i, "name");
                        info.receiverOA = data.getString(i, "username");
                        info.receiverMobile = data.getString(i, "mobile");
                        list.add(info);
                    }
                }
            }
            if (list.size() > 0) {
                if (list.size() == 1) {
                    intent = new Intent(RedpacketsShareMainActivity.this, RedpacketsTransferMainActivity.class);
                    intent.putExtra(Contants.PARAMETER.TRANSFERTO, "colleague");
                    intent.putExtra(Contants.PARAMETER.BALANCE, balance);
                    intent.putExtra("name", list.get(0).receiverName);
                    intent.putExtra("username", list.get(0).receiverOA);
                    intent.putExtra(Contants.PARAMETER.MOBILE, list.get(0).receiverMobile);
                    intent.putExtra(Contants.PARAMETER.USERID, list.get(0).receiver_id);
                    startActivity(intent);
                    list.clear();
                } else {
                    intent = new Intent(RedpacketsShareMainActivity.this, RedpacketsAccountListActivity.class);
                    intent.putExtra(Contants.PARAMETER.BALANCE, balance);
                    Bundle bundleObject = new Bundle();
                    bundleObject.putSerializable(RedpacketsAccountListActivity.REDPACKETS_LIST, list);
                    intent.putExtras(bundleObject);
                    startActivity(intent);
                    list.clear();
                }
            } else {
                ToastFactory.showToast(RedpacketsShareMainActivity.this, "你输入的账号有误！");
            }
        } else {
            ToastFactory.showToast(RedpacketsShareMainActivity.this, message);
        }
    }

    /**
     * 下一步
     */
    public void check() {
        if (edtColleagueInfo.getText().length() == 0) {
            ToastFactory.showToast(this, "请输入同事信息");
            return;
        }
        getEmployeeInfo(edtColleagueInfo.getText().toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 1002:
                colleagueInfo = data.getStringExtra("phoneNum");
                edtColleagueInfo.setText(colleagueInfo);
                break;
            default:
                break;
        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_redpackets_share_main, null);
    }

    @Override
    public String getHeadTitle() {
        return "给同事发饭票";
    }

}
