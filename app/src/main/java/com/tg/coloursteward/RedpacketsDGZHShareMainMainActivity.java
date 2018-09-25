package com.tg.coloursteward;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
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
 * 兑换给同事（对公账户进）
 *
 * @author Administrator
 */
public class RedpacketsDGZHShareMainMainActivity extends BaseActivity {
    private static final String TAG = "RedpacketsToColleagueMa";
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

    private String receiver_id;
    private String receiverName;
    private String receiverOA;
    private String receiverMobile;


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

    private String money;//对公账户余额
    private String transferNameOA;//转账对方OA账号
    private String transferName;//转账对方名字
    private String transferMobile;//转账对方电话
    private int pay_atid;//支付方类型
    private String pay_ano;//支付方账户

    private String transferNameOA_ONE;
    private String transferName_ONE;
    private String transferMobile_ONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPopup(false);
        transferTo = getIntent().getStringExtra("colleague");
        colleagueInfo = getIntent().getStringExtra(Contants.PARAMETER.OA);
        balance = getIntent().getDoubleExtra(Contants.PARAMETER.BALANCE, 0.00);
        key = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.key);
        secret = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.secret);
        money = getIntent().getStringExtra(Contants.PARAMETER.PUBLIC_ACCOUNT);

        pay_atid = getIntent().getIntExtra(Contants.PARAMETER.PAY_ATID, -1);
        pay_ano = getIntent().getStringExtra(Contants.PARAMETER.PAY_ANO);


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
                intent = new Intent(RedpacketsDGZHShareMainMainActivity.this, RedpacketsContactsActivity.class);
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

        edtColleagueInfo.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                /*if (s.length() == 11 && Tools.isNumeric(s.toString())) {
                    // 通过手机号或者OA账号读取同事信息
					getEmployeeInfo(edtColleagueInfo.getText().toString());
					OA = 0;
				} else if (View.VISIBLE == lloaInfo.getVisibility()) {
					lloaInfo.setVisibility(View.GONE);
				}*/
            }
        });
        pullListView = (PullRefreshListView) findViewById(R.id.pull_listview);
        pullListView.setDividerHeight(0);
        adapter = new HistoryTransferAdapter(this, pageInfoList);
        pullListView.setAdapter(adapter);
        pullListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                HistoryTransferInfo info = pageInfoList.get(position);
                Log.e(TAG, "onItemClick: " + info.receiverOA);
//                Log.e(TAG, "onItemClick: " + info.receiverName);
                transferNameOA = info.receiverOA;
                transferName = info.receiverName;
                transferMobile = info.receiverMobile;
                RequestConfig config = new RequestConfig(RedpacketsDGZHShareMainMainActivity.this, HttpTools.GET_FINACE_BYOA, "查询");
                RequestParams params = new RequestParams();
                params.put("oa_username", info.receiverOA);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/czyprovide/employee/getFinanceByOa", config, params);

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
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onLoadingMore(PullRefreshListView t, Handler hand, int pageIndex) {
                RequestConfig config = new RequestConfig(RedpacketsDGZHShareMainMainActivity.this, PullRefreshListView.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("key", key);
                params.put("secret", secret);
                params.put("page", pageIndex);
                params.put("pagesize", PullRefreshListView.PAGER_SIZE);
                HttpTools.httpGet(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/carryList", config, params);

            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                RequestConfig config = new RequestConfig(RedpacketsDGZHShareMainMainActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("key", key);
                params.put("secret", secret);
                params.put("page", 1);
                params.put("pagesize", PullRefreshListView.PAGER_SIZE);
                HttpTools.httpGet(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/carryList", config, params);
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
        /**
         * 获取版本号
         */
        String versionShort = UpdateManager.getVersionName(this);
        String key = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.key);
        String secret = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.secret);
        Log.e(TAG, "getEmployeeInfo:key " + key);
        Log.e(TAG, "getEmployeeInfo:secret " + secret);
        RequestConfig config = new RequestConfig(RedpacketsDGZHShareMainMainActivity.this, HttpTools.GET_EMPLOYEE_INFO, "查询");
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("version", versionShort);
        params.put("key", key);
        params.put("secret", secret);
        HttpTools.httpPost(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/getEmployeeInfo", config, params);

    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        // TODO Auto-generated method stub
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.GET_EMPLOYEE_INFO) {
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
                        /**
                         * 只有一个账户，直接跳转
                         */
                        transferMobile_ONE = list.get(0).receiverMobile;
                        transferName_ONE = list.get(0).receiverName;
                        transferNameOA_ONE = list.get(0).receiverOA;
                        RequestConfig config = new RequestConfig(this, HttpTools.GET_FINACE_BYOA_ONE, "查询");
                        RequestParams params = new RequestParams();
                        params.put("oa_username", list.get(0).receiverOA);
                        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/czyprovide/employee/getFinanceByOa", config, params);

                        list.clear();
//					finish();
                    } else {
                        /**
                         * 多个账户要进列表选择
                         */


                        intent = new Intent(RedpacketsDGZHShareMainMainActivity.this, RedpacketsDGZHAccountListActivity.class);
                        intent.putExtra(Contants.PARAMETER.BALANCE, balance);
                        //intent.putExtra(RedpacketsAccountListActivity.REDPACKETS_LIST,list);
                        Bundle bundleObject = new Bundle();
                        bundleObject.putSerializable(RedpacketsAccountListActivity.REDPACKETS_LIST, list);
                        bundleObject.putString(Contants.PARAMETER.PUBLIC_ACCOUNT, money);
                        bundleObject.putString(Contants.PARAMETER.PAY_ANO, pay_ano);
                        bundleObject.putInt(Contants.PARAMETER.PAY_ATID, pay_atid);
                        intent.putExtras(bundleObject);
                        startActivity(intent);
                        list.clear();
//					finish();
                    }
                } else {
                    ToastFactory.showToast(RedpacketsDGZHShareMainMainActivity.this, "你输入的账号有误！");
                }
            } else {
                ToastFactory.showToast(RedpacketsDGZHShareMainMainActivity.this, message);
            }
        } else if (msg.arg1 == HttpTools.GET_FINACE_BYOA) {
            if (code == 0) {
                JSONObject contentJSONObject = HttpTools.getContentJSONObject(jsonString);
                if (contentJSONObject != null) {
                    try {
                        JSONObject content = contentJSONObject.getJSONObject("content");
                        if (content != null) {
                            String cano = content.getString("cano");
                            String atid = content.getString("atid");
                            Log.e(TAG, "onSuccess:对公账户详情 " + cano + "\n" + atid);
                            Intent intent = new Intent(RedpacketsDGZHShareMainMainActivity.this, PublicAccountTransferToColleagueActivity.class);
                            intent.putExtra("cano", cano);
                            intent.putExtra("atid", atid);
                            intent.putExtra(Contants.PARAMETER.PUBLIC_ACCOUNT, money);
                            intent.putExtra(Contants.PARAMETER.MOBILE, transferMobile);
                            intent.putExtra(Contants.PARAMETER.PAY_NAME, transferName);
                            intent.putExtra(Contants.PARAMETER.OA, transferNameOA);
                            intent.putExtra(Contants.PARAMETER.PAY_ANO, pay_ano);
                            intent.putExtra(Contants.PARAMETER.PAY_ATID, pay_atid);

                            startActivity(intent);
//                            mDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (msg.arg1 == HttpTools.GET_FINACE_BYOA_ONE) {
            if (code == 0) {
                String error = "";
                JSONObject contentJSONObject = HttpTools.getContentJSONObject(jsonString);
                if (contentJSONObject != null) {
                    try {
                        error = contentJSONObject.getString("message");
                        JSONObject content = contentJSONObject.getJSONObject("content");
                        if (content != null) {
                            String cano = content.getString("cano");
                            String atid = content.getString("atid");
                            Log.e(TAG, "onSuccess:对公账户详情单个 " + cano + "\n" + atid);
                            Intent intent = new Intent(RedpacketsDGZHShareMainMainActivity.this, PublicAccountTransferToColleagueActivity.class);
                            intent.putExtra("cano", cano);
                            intent.putExtra("atid", atid);
                            intent.putExtra(Contants.PARAMETER.PUBLIC_ACCOUNT, money);
                            intent.putExtra(Contants.PARAMETER.MOBILE, transferMobile_ONE);
                            intent.putExtra(Contants.PARAMETER.PAY_NAME, transferName_ONE);
                            intent.putExtra(Contants.PARAMETER.OA, transferNameOA_ONE);
                            intent.putExtra(Contants.PARAMETER.PAY_ANO, pay_ano);
                            intent.putExtra(Contants.PARAMETER.PAY_ATID, pay_atid);

                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        ToastFactory.showToast(RedpacketsDGZHShareMainMainActivity.this, error);
                        e.printStackTrace();
                    }
                }
            }
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
        // TODO Auto-generated method stub
        return getLayoutInflater().inflate(R.layout.activity_redpackets_share_main, null);
    }

    @Override
    public String getHeadTitle() {
        // TODO Auto-generated method stub
        return "兑换给同事";
    }

}
