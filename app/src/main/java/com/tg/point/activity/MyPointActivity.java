package com.tg.point.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dashuview.library.keep.Cqb_PayUtil;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.UserMessageConstant;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.point.adapter.PointListAdapter;
import com.tg.point.entity.PointAccountListEntity;
import com.tg.point.entity.PointKeywordEntity;
import com.tg.point.model.PointModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.tg.coloursteward.constant.SpConstants.storage.COLOUR_WALLET_ACCOUNT_LIST;
import static com.tg.coloursteward.constant.SpConstants.storage.COLOUR_WALLET_KEYWORD_SIGN;
import static com.tg.coloursteward.module.MainActivity.getEnvironment;
import static com.tg.coloursteward.module.MainActivity.getPublicParams;
import static com.tg.point.activity.GivenPointAmountActivity.GIVENMOBILE;


/***
 * 积分的列表和各种类型
 */
public class MyPointActivity extends BaseActivity implements View.OnClickListener, HttpResponse {

    private ImageView mBack;
    private TextView mTitle;
    private ImageView iv_point_desc;
    private TextView tv_point_title;//显示是积分还是饭票的类型
    private TextView tv_point_total; //积分或饭票的余额
    private RecyclerView rv_point;//饭票或积分类型的列表
    private PointModel pointModel;
    private PointListAdapter pointListAdapter;
    private List<PointAccountListEntity.ContentBean.ListBean> listBeanList = new ArrayList<>();
    private String mobilePhone;
    private boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_list);
        mBack = findViewById(R.id.iv_base_back);
        mTitle = findViewById(R.id.tv_base_title);
        iv_point_desc = findViewById(R.id.iv_point_desc);
        tv_point_title = findViewById(R.id.tv_point_title);
        tv_point_total = findViewById(R.id.tv_point_total);
        rv_point = findViewById(R.id.rv_point);
        mBack.setOnClickListener(this);
        iv_point_desc.setOnClickListener(this);
        tv_point_total.setOnClickListener(this);
        pointModel = new PointModel(MyPointActivity.this);
        Intent intent = getIntent();
        mobilePhone = intent.getStringExtra(GIVENMOBILE);
        String keyWordSign = spUtils.getStringData(COLOUR_WALLET_KEYWORD_SIGN, "积分");
        String accountList = spUtils.getStringData(COLOUR_WALLET_ACCOUNT_LIST, "");
        if (!TextUtils.isEmpty(keyWordSign)) {
            mTitle.setText("彩" + keyWordSign);
        }
        if (!TextUtils.isEmpty(accountList)) {
            isLoading = false;
            showAccountList(accountList);
        }
        if (!EventBus.getDefault().isRegistered(MyPointActivity.this)) {
            EventBus.getDefault().register(MyPointActivity.this);
        }
        getPointList();
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_base_back:
                finish();
                break;
            case R.id.iv_point_desc:
                if (null == pointDescDialog) {
                    pointDescDialog = new PointDescDialog(MyPointActivity.this);
                }
                pointDescDialog.show();
                break;
            case R.id.tv_base_confirm:
                Cqb_PayUtil.getInstance(MyPointActivity.this).createPay(getPublicParams(), getEnvironment());//彩钱包
                break;
        }
    }

    private PointDescDialog pointDescDialog;

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    PointKeywordEntity pointKeywordEntity = GsonUtils.gsonToBean(result, PointKeywordEntity.class);
                    PointKeywordEntity.ContentBean contentBean = pointKeywordEntity.getContent();
                    String keywordSign = contentBean.getKeyword();
                    if (!TextUtils.isEmpty(keywordSign)) {
                        spUtils.saveStringData(COLOUR_WALLET_KEYWORD_SIGN, keywordSign);
                        mTitle.setText("彩" + keywordSign);
                    }
                }
                break;
            case 1:
                showAccountList(result);
                break;
        }
    }

    private void showAccountList(String result) {
        try {
            PointAccountListEntity pointAccountListEntity = GsonUtils.gsonToBean(result, PointAccountListEntity.class);
            PointAccountListEntity.ContentBean contentBean = pointAccountListEntity.getContent();
            PointAccountListEntity.ContentBean.TotalBean totalBean = contentBean.getTotal();
            if (null != totalBean) {
                tv_point_title.setText(totalBean.getName());
                tv_point_total.setText(String.valueOf(totalBean.getBalance() / 100.0f));
            }
            List<PointAccountListEntity.ContentBean.ListBean> contentBeanList = contentBean.getList();
            if (null != contentBeanList) {
                listBeanList.clear();
                listBeanList.addAll(contentBeanList);
            }
            if (null == pointListAdapter) {
                pointListAdapter = new PointListAdapter(listBeanList);
                pointListAdapter.setUserInfor(mobilePhone);
                rv_point.setLayoutManager(new LinearLayoutManager(MyPointActivity.this, LinearLayoutManager.VERTICAL, false));
                rv_point.setAdapter(pointListAdapter);

            } else {
                pointListAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {

        }
    }

    @Subscribe
    public void onEvent(Object event) {
        final Message message = (Message) event;
        switch (message.what) {
            case UserMessageConstant.POINT_SUCCESS_RETURN:
            case UserMessageConstant.POINT_CONTINUE_GIVEN:
                getPointList();
                break;

        }
    }

    private void getPointList() {
        pointModel.getWalletKeyWord(0, MyPointActivity.this);
        pointModel.getAccountList(1, isLoading, MyPointActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(MyPointActivity.this)) {
            EventBus.getDefault().unregister(MyPointActivity.this);
        }
    }
}
