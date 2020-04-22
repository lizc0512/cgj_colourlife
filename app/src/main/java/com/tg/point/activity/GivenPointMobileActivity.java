package com.tg.point.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.baseModel.RequestEncryptionUtils;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.constant.UserMessageConstant;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.ClearEditText;
import com.tg.point.adapter.GivenMobileAdapter;
import com.tg.point.entity.GivenMobileEntity;
import com.tg.point.entity.PointAccountLimitEntity;
import com.tg.point.entity.PointCzyUserEntity;
import com.tg.point.entity.UserIdInforEntity;
import com.tg.point.model.PointModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.tg.point.activity.GivenPointAmountActivity.GIVENMOBILE;
import static com.tg.point.activity.GivenPointAmountActivity.TYPE;
import static com.tg.point.activity.PointTransactionListActivity.POINTTPANO;


/***
 * 赠送积分输入手机号
 */
public class GivenPointMobileActivity extends BaseActivity implements View.OnClickListener, TextWatcher, HttpResponse {


    public static final String GIVENNAME = "givenname";
    private ImageView mBack;
    private TextView mTitle;
    private TextView user_top_view_right;
    private TextView tv_given_contact;
    private ClearEditText input_given_mobile;//赠送人的手机号
    private Button btn_next_step;
    private TextView tv_remain_notice;
    private String givePhone = "";
    private String type;
    private String pano;//积分或饭票的类型
    private String keyword_sign; //积分或饭票的标识
    private PointModel pointModel;
    private boolean canGiven = true;
    private int last_times;//剩余次数
    private float last_amount;//剩余金额
    private boolean isColleag;
    private RecyclerView rv_point_mobile;
    private GivenMobileAdapter adapter;
    private String givenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setContentView(R.layout.activity_point_given_mobile);
        mBack = findViewById(R.id.iv_base_back);
        mTitle = findViewById(R.id.tv_base_title);
        tv_given_contact = findViewById(R.id.tv_given_contact);
        user_top_view_right = findViewById(R.id.tv_base_confirm);
        input_given_mobile = findViewById(R.id.input_given_mobile);
        btn_next_step = findViewById(R.id.btn_next_step);
        tv_remain_notice = findViewById(R.id.tv_remain_notice);
        rv_point_mobile = findViewById(R.id.rv_point_mobile);
        rv_point_mobile.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBack.setOnClickListener(this);
        btn_next_step.setEnabled(false);
        btn_next_step.setOnClickListener(this);
        user_top_view_right.setVisibility(View.GONE);
        user_top_view_right.setText("历史记录");
        user_top_view_right.setOnClickListener(this::onClick);
        input_given_mobile.addTextChangedListener(this);
        Intent intent = getIntent();
        pano = intent.getStringExtra(POINTTPANO);
        givePhone = intent.getStringExtra(GIVENMOBILE);
        type = intent.getStringExtra(TYPE);
        givenName = intent.getStringExtra(GIVENNAME);
        keyword_sign = spUtils.getStringData(SpConstants.storage.COLOUR_WALLET_KEYWORD_SIGN, "饭票");
        if (!TextUtils.isEmpty(givenName)) {
            mTitle.setText(givenName + "赠送");
        } else {
            mTitle.setText(keyword_sign + "赠送");
        }
        pointModel = new PointModel(GivenPointMobileActivity.this);
        pointModel.getAccountLimit(0, pano, GivenPointMobileActivity.this);
        if (!EventBus.getDefault().isRegistered(GivenPointMobileActivity.this)) {
            EventBus.getDefault().register(GivenPointMobileActivity.this);
        }
        if (!TextUtils.isEmpty(givePhone)) {
            input_given_mobile.setText(givePhone);
            input_given_mobile.setSelection(givePhone.length());
        }
        if (!TextUtils.isEmpty(type)) {
            if ("cgj-cgj".equals(type)) {
                isColleag = true;
                input_given_mobile.setHint("请输入获赠人手机号、OA、姓名搜索");
                input_given_mobile.setInputType(InputType.TYPE_CLASS_TEXT);
                tv_given_contact.setVisibility(View.VISIBLE);
                pointModel.getHistoryAmount(2, this);
            } else {
                isColleag = false;
            }
        }
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
            case R.id.tv_base_confirm:
                Intent intent = new Intent(GivenPointMobileActivity.this, GivenPointHistoryActivity.class);
                intent.putExtra(POINTTPANO, pano);
                startActivityForResult(intent, 200);
                break;
            case R.id.btn_next_step:
                givePhone = input_given_mobile.getText().toString().trim();
                if (isColleag) {
                    if (UserInfo.mobile.equals(givePhone)) {
                        ToastUtil.showShortToast(GivenPointMobileActivity.this, "不能给自己赠送" + keyword_sign);
                        return;
                    }
                    if (!TextUtils.isEmpty(givePhone)) {
                        pointModel.getUserInfor(1, givePhone, GivenPointMobileActivity.this);
                    } else {
                        ToastUtil.showShortToast(this, "输入内容不能为空");
                    }
                } else {
                    if (givePhone.length() == 11) {
                        pointModel.getCzyUser(3, givePhone, GivenPointMobileActivity.this);
                    } else {
                        ToastUtil.showShortToast(this, "请输入11位手机号");
                    }
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Subscribe
    public void onEvent(Object event) {
        final Message message = (Message) event;
        switch (message.what) {
            case UserMessageConstant.POINT_SUCCESS_RETURN:
                finish();
                break;
            case UserMessageConstant.POINT_CONTINUE_GIVEN:
                pointModel.getAccountLimit(0, pano, GivenPointMobileActivity.this);
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(GivenPointMobileActivity.this)) {
            EventBus.getDefault().unregister(GivenPointMobileActivity.this);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        givePhone = s.toString().trim();
        setBtnClick();
    }


    private void setBtnClick() {
        if (TextUtils.isEmpty(givePhone) || !canGiven) {
            btn_next_step.setEnabled(false);
            btn_next_step.setBackgroundResource(R.drawable.point_password_default_bg);
        } else {
            btn_next_step.setEnabled(true);
            btn_next_step.setBackgroundResource(R.drawable.point_password_click_bg);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200) {
            givePhone = data.getStringExtra(GIVENMOBILE);
            input_given_mobile.setText(givePhone);
            input_given_mobile.setSelection(givePhone.length());
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                try {
                    PointAccountLimitEntity pointAccountLimitEntity = GsonUtils.gsonToBean(result, PointAccountLimitEntity.class);
                    PointAccountLimitEntity.ContentBean contentBean = pointAccountLimitEntity.getContent();
                    last_times = contentBean.getLast_times();
                    last_amount = contentBean.getLast_amount();
                    if (last_amount > 0 && last_times > 0) {
                        canGiven = true;
                    } else if (last_amount == -1 || last_times == -1) {
                        canGiven = true;
                        tv_remain_notice.setVisibility(View.GONE);
                    } else {
                        canGiven = false;
                    }
                    setBtnClick();
                    tv_remain_notice.setText("今天可赠送" + last_times + "次，剩余额度" + last_amount + keyword_sign);
                } catch (Exception e) {

                }
                break;
            case 1:
                if (!TextUtils.isEmpty(RequestEncryptionUtils.getContentString(result))) {
                    UserIdInforEntity userIdInforEntity = GsonUtils.gsonToBean(result, UserIdInforEntity.class);
                    List<UserIdInforEntity.ContentBean> mList = new ArrayList<>(userIdInforEntity.getContent());
                    if (mList.size() == 1) {
                        String real_name = mList.get(0).getName();
                        String icon = mList.get(0).getPortrait();
                        String uuid = mList.get(0).getAccount_uuid();
                        Intent amount_Intent = new Intent(GivenPointMobileActivity.this, GivenPointAmountActivity.class);
                        amount_Intent.putExtra(POINTTPANO, pano);
                        amount_Intent.putExtra(GIVENMOBILE, givePhone);
                        amount_Intent.putExtra(TYPE, type);
                        amount_Intent.putExtra(GivenPointAmountActivity.USERPORTRAIT, icon);
                        amount_Intent.putExtra(GivenPointAmountActivity.USERID, uuid);
                        amount_Intent.putExtra(GivenPointAmountActivity.USERNAME, real_name);
                        amount_Intent.putExtra(GivenPointAmountActivity.LASTAMOUNT, last_amount);
                        amount_Intent.putExtra(GivenPointAmountActivity.LASTTIME, last_times);
                        startActivity(amount_Intent);
                    } else {
                        Intent it = new Intent(GivenPointMobileActivity.this, MultiCollegeActivity.class);
                        it.putExtra("json", result);
                        it.putExtra("last_amount", last_amount);
                        it.putExtra("last_times", last_times);
                        it.putExtra("pano", pano);
                        it.putExtra("givePhone", givePhone);
                        it.putExtra("type", type);
                        startActivity(it);
                    }
                } else {
                    ToastUtil.showShortToast(this, "暂无此用户");
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(result)) {
                    GivenMobileEntity entity = new GivenMobileEntity();
                    entity = GsonUtils.gsonToBean(result, GivenMobileEntity.class);
                    List<GivenMobileEntity.ContentBean> mHistoryList = new ArrayList<>();
                    mHistoryList.addAll(entity.getContent());
                    if (null == adapter) {
                        adapter = new GivenMobileAdapter(R.layout.item_given_mobile, mHistoryList);
                        rv_point_mobile.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            String oa = mHistoryList.get(position).getUsername();
                            input_given_mobile.getText().clear();
                            input_given_mobile.setText(oa);
                            input_given_mobile.setSelection(oa.length());
                        }
                    });
                }
                break;
            case 3:
                if (!TextUtils.isEmpty(result)) {
                    PointCzyUserEntity userEntity = new PointCzyUserEntity();
                    userEntity = GsonUtils.gsonToBean(result, PointCzyUserEntity.class);
                    Intent amount_Intent = new Intent(GivenPointMobileActivity.this, GivenPointAmountActivity.class);
                    amount_Intent.putExtra(POINTTPANO, pano);
                    amount_Intent.putExtra(GIVENMOBILE, givePhone);
                    amount_Intent.putExtra(TYPE, type);
                    amount_Intent.putExtra(GivenPointAmountActivity.USERPORTRAIT, userEntity.getContent().getPortrait());
                    amount_Intent.putExtra(GivenPointAmountActivity.USERID, userEntity.getContent().getUuid());
                    amount_Intent.putExtra(GivenPointAmountActivity.USERNAME, userEntity.getContent().getNick_name());
                    amount_Intent.putExtra(GivenPointAmountActivity.LASTAMOUNT, last_amount);
                    amount_Intent.putExtra(GivenPointAmountActivity.LASTTIME, last_times);
                    startActivity(amount_Intent);
                }
                break;
        }
    }
}
