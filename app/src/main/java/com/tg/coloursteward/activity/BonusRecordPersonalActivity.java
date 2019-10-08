package com.tg.coloursteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.BonusRecordPersonalAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.entity.BonusRecordEntity;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.view.PullRefreshListView;
import com.youmai.hxsdk.router.APath;
import com.youmai.hxsdk.utils.ToastUtil;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 奖金包(个人)
 *
 * @author Administrator
 */
@Route(path = APath.PERSONALACCOUNT)
public class BonusRecordPersonalActivity extends BaseActivity implements OnItemClickListener {
    private PullRefreshListView pullListView;
    private BonusRecordPersonalAdapter adapter;
    private ArrayList<BonusRecordEntity.ContentBean.DataBean> listinfo = new ArrayList<>();
    private BonusRecordEntity bonusRecordEntity;
    private String orguuid;
    private ImageView iv_bonus_person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            orguuid = intent.getStringExtra("orguuid");
        }
        initView();
    }

    private void initView() {
        pullListView = (PullRefreshListView) findViewById(R.id.pull_listview);
        iv_bonus_person = (ImageView) findViewById(R.id.iv_bonus_person);
        adapter = new BonusRecordPersonalAdapter(this, listinfo);
        pullListView.setAdapter(adapter);
        pullListView.setDividerHeight(0);
        pullListView.setOnItemClickListener(this);
        pullListView.setMinPageSize(15);
        pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {

            @Override
            public void refreshData(PullRefreshListView t,
                                    boolean isLoadMore, Message msg, String response) {
                int code = HttpTools.getCode(response);
                String message = HttpTools.getMessageString(response);
                if (code == 0) {
                    JSONObject content = HttpTools.getContentJSONObject(response);
                    if (content != null && content.length() > 0) {
                        iv_bonus_person.setVisibility(View.GONE);
                        bonusRecordEntity = GsonUtils.gsonToBean(response, BonusRecordEntity.class);
                        if (bonusRecordEntity.getContent() != null) {
                            listinfo.addAll(bonusRecordEntity.getContent().getData());
                        }
                    } else {
                        iv_bonus_person.setVisibility(View.VISIBLE);
                    }
                } else {
                    ToastUtil.showToast(BonusRecordPersonalActivity.this, message);
                }
            }

            @Override
            public void onLoadingMore(PullRefreshListView t, Handler hand, int pagerIndex) {
                RequestConfig config = new RequestConfig(BonusRecordPersonalActivity.this, PullRefreshListView.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("username", UserInfo.employeeAccount);
//                params.put("orguuid", orguuid);
                params.put("page", "1");
                params.put("pagesize", "10");
//                params.put("user_uuid", UserInfo.uid);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/jxjjb/userjjb/list", config, params);
            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                RequestConfig config = new RequestConfig(BonusRecordPersonalActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("username", UserInfo.employeeAccount);
//                params.put("orguuid", orguuid);
                params.put("page", "1");
                params.put("pagesize", "10");
//                params.put("user_uuid", UserInfo.uid);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/jxjjb/userjjb/list", config, params);
            }
        });
        pullListView.performLoading();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Intent intent = new Intent(BonusRecordPersonalActivity.this, BonusRecordDetailNewActivity.class);
        intent.putExtra("calculid", String.valueOf(listinfo.get(position).getCalculid()));
        intent.putExtra("rummagerid", String.valueOf(listinfo.get(position).getRummagerid()));
        intent.putExtra("name", listinfo.get(position).getOrg_name());
        startActivity(intent);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_bonus_record_personal, null);
    }

    @Override
    public String getHeadTitle() {
        return "我的奖金包明细";
    }
}
