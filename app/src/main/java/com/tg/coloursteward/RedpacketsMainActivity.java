package com.tg.coloursteward;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.tg.coloursteward.adapter.ThreeElementsAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.ThreeElementsInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.PwdDialog2;
import com.tg.coloursteward.view.dialog.PwdDialog2.ADialogCallback;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 我的饭票(点击提现与转账进来的)
 *
 * @author Administrator
 */
public class RedpacketsMainActivity extends BaseActivity {
    private int img[] = new int[]{R.drawable.icon_share_with_colleague,
            R.drawable.icon_transfer_to_czy,
            R.drawable.icon_transfer_to_bank_card};
    private double balance = 0d;
    private ListView itemListView;
    private ThreeElementsAdapter adapter;
    private int serialNum;
    private Intent intent;
    private PwdDialog2 aDialog;
    private ADialogCallback aDialogCallback;
    /**
     * 存放几个转账选项说明文字的list
     */
    private ArrayList<ThreeElementsInfo> pageInfoList = new ArrayList<ThreeElementsInfo>();
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    /**
     * 获取饭票余额
     */
    private void getBalance(String key, String secret) {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_BALANCE_INFO);
        RequestParams params = new RequestParams();
        params.put("key", key);
        params.put("secret", secret);
        HttpTools.httpGet(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/getBalance", config, params);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        itemListView = (ListView) findViewById(R.id.lv_item);
        itemListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        if (balance <= 0) {
                            ToastFactory.showToast(RedpacketsMainActivity.this, "饭票余额不足，不能发饭票");
                            return;
                        } else {
                            serialNum = position;
                            // 判断有无密码及卡
                            isSetPwd(position);
                        }
                        break;

                    case 1:
                        if (balance <= 0) {
                            ToastFactory.showToast(RedpacketsMainActivity.this, "饭票余额不足，不能转账");
                            return;
                        } else {
                            serialNum = position;
                            // 判断有无密码及卡
                            type = "1";
                            isSetPwd(position);
                        }
                        break;

                    case 2:
                        if (balance <= 0) {
                            ToastFactory.showToast(RedpacketsMainActivity.this, "饭票余额不足，不能提现");
                            return;
                        } else {
                            serialNum = position;
                            // 判断有无密码及卡
                            isSetPwd(position);
                        }
                        break;
                }
            }
        });
    }

    /**
     * 点击事件判断有误密码以卡
     *
     * @param position
     */
    private void isSetPwd(int position) {
        String key = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.key);
        String secret = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.secret);
        RequestConfig config = new RequestConfig(this, HttpTools.POST_SETPWD_INFO);
        RequestParams params = new RequestParams();
        params.put("position", position);
        params.put("key", key);
        params.put("secret", secret);
        HttpTools.httpPost(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/isSetPwd", config, params);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        if (msg.arg1 == HttpTools.GET_BALANCE_INFO) {
            if (code == 0) {
                JSONObject content = HttpTools.getContentJSONObject(jsonString);
                String sponse = HttpTools.getContentString(jsonString);
                if (content != null) {
                    try {
                        balance = content.getDouble("balance");
                        if (content.getJSONArray("activityInfo") != null) {
                            ResponseData data = HttpTools.getResponseKey(sponse, "activityInfo");
                            ThreeElementsInfo info;
                            for (int i = 0; i < data.length; i++) {
                                info = new ThreeElementsInfo();
                                info.img = img[i];
                                info.title = data.getString(i, "title");
                                info.describe = data.getString(i, "describe");
                                pageInfoList.add(info);
                            }
                        } else {
                            ThreeElementsInfo info;
                            info = new ThreeElementsInfo();
                            info.img = img[0];
                            info.title = "给同事发饭票";
                            info.describe = "红包互转，轻松自在";

                            info = new ThreeElementsInfo();
                            info.img = img[1];
                            info.title = "转到彩之云账户";
                            info.describe = "在彩之云平台支出红包无需扣税";

                            info = new ThreeElementsInfo();
                            info.img = img[2];
                            info.title = "提现到我的银行卡";
                            info.describe = "提现到银行卡将扣除个人所得税";
                            pageInfoList.add(info);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            setItemAdapter();
        } else if (msg.arg1 == HttpTools.POST_SETPWD_INFO) {
            if (code == 0) {
                JSONObject content = HttpTools.getContentJSONObject(jsonString);
                if (content != null) {
                    final String state;
                    try {
                        state = content.getString("state");
                        switch (serialNum) {
                            case 0:// 给同事发饭票
                                if (state != null) {
                                    if ("hasPwd".equals(state)) { // 已设置密码
                                        intent = new Intent(RedpacketsMainActivity.this, RedpacketsShareMainActivity.class);
                                        intent.putExtra(Contants.PARAMETER.BALANCE, balance);
                                        intent.putExtra(Contants.PARAMETER.TRANSFERTO, "colleague");
                                        startActivity(intent);
                                    } else {
                                        aDialogCallback = new ADialogCallback() {
                                            @Override
                                            public void callback() {
                                                intent = new Intent(RedpacketsMainActivity.this, RedpacketsShareMainActivity.class);
                                                intent.putExtra(Contants.PARAMETER.BALANCE, balance);
                                                intent.putExtra(Contants.PARAMETER.TRANSFERTO, "colleague");
                                                startActivity(intent);
                                            }
                                        };
                                        aDialog = new PwdDialog2(
                                                RedpacketsMainActivity.this,
                                                R.style.choice_dialog, state,
                                                aDialogCallback);
                                        aDialog.show();
                                    }
                                } else {
                                    //	ToastFactory.showToast(RedpacketsMainActivity.this, "网络异常");
                                }
                                break;

                            // 转账到彩之云账户
                            case 1:
                                if (state != null) {
                                    if ("hasBind".equals(state)) { // 已绑定彩之云账户
                                        Intent intent = new Intent();
                                        intent.setClass(RedpacketsMainActivity.this, RedpacketsTransferMainActivity.class);
                                        intent.putExtra(Contants.PARAMETER.BALANCE, balance);
                                        intent.putExtra(Contants.PARAMETER.TRANSFERTO, "czy");
                                        content = content.getJSONObject("list");
                                        String userId = content.getString("customer_id");
                                        String mobile = content.getString("mobile");
                                        String id = content.getString("id"); // 绑定记录的id
                                        intent.putExtra(Contants.PARAMETER.USERID, userId);
                                        intent.putExtra(Contants.PARAMETER.MOBILE, mobile);
                                        intent.putExtra("id", id);
                                        startActivity(intent);
                                    } else {//未绑定彩之云账户
                                        aDialogCallback = new ADialogCallback() {
                                            @Override
                                            public void callback() {
                                                if ("1".equals(type)) {
                                                    type="2";
                                                    isSetPwd(serialNum);
                                                } else {
                                                    Intent intent = new Intent();
                                                    intent.setClass(RedpacketsMainActivity.this, RedpacketsBindCZYMainActivity.class);
                                                    intent.putExtra(Contants.PARAMETER.BALANCE, balance);
                                                    startActivity(intent);
                                                }

                                            }
                                        };
                                        aDialog = new PwdDialog2(
                                                RedpacketsMainActivity.this,
                                                R.style.choice_dialog, state,
                                                aDialogCallback);
                                        aDialog.show();
                                    }
                                } else {
                                    //ToastFactory.showToast(RedpacketsMainActivity.this, "网络异常");
                                }
                                break;

                            // 提现
                            case 2:
                                final String desc = pageInfoList.get(2)
                                        .describe.toString();
                                if (state != null) {
                                    if ("hasCard".equals(state)) { // 有卡（一定有密码），转到银行卡列表页
                                        final String rate = content.getString("rate");
                                        Intent intent = new Intent();
                                        intent.putExtra(Contants.PARAMETER.BALANCE, balance);
                                        intent.putExtra("desc", desc);
                                        intent.putExtra("rate", rate);
                                        intent.setClass(RedpacketsMainActivity.this, RedpacketsBankCardListActivity.class);
                                        startActivity(intent);

                                    } else {
                                        aDialogCallback = new ADialogCallback() {
                                            @Override
                                            public void callback() {
                                                Intent intent = new Intent();
                                                intent.setClass(RedpacketsMainActivity.this, RedpacketsBindBankCardActivity.class);
                                                intent.putExtra(Contants.PARAMETER.BALANCE, balance);
                                                intent.putExtra("desc", desc);
                                                startActivity(intent);
                                            }
                                        };
                                        aDialog = new PwdDialog2(
                                                RedpacketsMainActivity.this,
                                                R.style.choice_dialog, state,
                                                aDialogCallback);
                                        aDialog.show();
                                    }
                                } else {
                                    //ToastFactory.showToast(RedpacketsMainActivity.this, "网络异常");
                                }
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onFail(Message msg, String hintString) {
        super.onFail(msg, hintString);
    }

    /**
     * 设置适配器
     */
    private void setItemAdapter() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            adapter = new ThreeElementsAdapter(this, pageInfoList);
            itemListView.setAdapter(adapter);
        }
    }

    // 每次进入都重新获取饭票余额及转账选项的文字
    @Override
    protected void onResume() {
        super.onResume();
        pageInfoList.clear();
        /**
         * 需要及时调用adapter.notifyDataSetChanged()，会导致ListView没有数据而抛java.lang.IllegalStateException这个异常。
         */
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }


        String key = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.key);
        String secret = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.secret);
        getBalance(key, secret);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_redpackets_main, null);
    }

    @Override
    public String getHeadTitle() {
        return "我的饭票";
    }


}
