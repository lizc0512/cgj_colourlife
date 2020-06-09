package com.tg.delivery.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.LinkParseUtil;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.delivery.adapter.DeliveryMsgTemplateAdapter;
import com.tg.delivery.entity.DeliveryAddressEntity;
import com.tg.delivery.entity.DeliverySmsTemplateEntity;
import com.tg.delivery.model.DeliveryModel;
import com.tg.delivery.utils.SwitchButton;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.tg.coloursteward.constant.UserMessageConstant.DELIVERY_OPERATE_SUCCESS;

/**
 * 确认派件
 */
public class DeliveryConfirmActivity extends BaseActivity {

    public static final String COURIERNUMBERS = "couriernumbers";
    public static final String COURIERSIZE = "couriersize";
    public static final String COURIERLENGTHLIST = "courierlengthlist";
    private DeliveryMsgTemplateAdapter deliveryMsgTemplateAdapter;
    private TextView tv_choice_num;
    private TextView tv_sms_num;
    private RelativeLayout delivery_address_layout;
    private TextView tv_delivery_position;
    private TextView tv_delivery_default;
    private TextView tv_delivery_address;
    private SwitchButton message_sb;
    private RecyclerView rv_message_list;
    private Button btn_confirm_delivery;
    private List<String> addressList = new ArrayList<>();
    private List<DeliverySmsTemplateEntity.ContentBean.ListBean.ChildListBean> templateMsgList = new ArrayList<>();

    private String courierNumbers;
    private String deliveryAddress;
    private int courierTotal;


    private DeliveryModel deliveryModel;
    private String finishType;
    private int templateTotal;
    private int jumpWeb = 0;
    private int jumpAddress = 0;
    private String smsTemplateId = "";
    private int smsContentLength;
    private ArrayList<Integer> lengthsList;
    private int currentTemplatePos = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv_choice_num = findViewById(R.id.tv_choice_num);
        tv_sms_num = findViewById(R.id.tv_sms_num);
        delivery_address_layout = findViewById(R.id.delivery_address_layout);
        delivery_address_layout.setOnClickListener(v -> {
            jumpAddress = 1;
            LinkParseUtil.parse(DeliveryConfirmActivity.this, Contants.URl.DELIVERY_ADDRESS_URL, "");
        });
        tv_delivery_position = findViewById(R.id.tv_delivery_position);
        tv_delivery_default = findViewById(R.id.tv_delivery_default);
        tv_delivery_address = findViewById(R.id.tv_delivery_address);
        message_sb = findViewById(R.id.message_sb);
        rv_message_list = findViewById(R.id.rv_message_list);
        btn_confirm_delivery = findViewById(R.id.btn_confirm_delivery);
        Intent intent = getIntent();
        courierNumbers = intent.getStringExtra(COURIERNUMBERS);
        courierTotal = intent.getIntExtra(COURIERSIZE, 0);
        lengthsList = intent.getIntegerArrayListExtra(COURIERLENGTHLIST);
        showTotalNum();
        addressList.add("自提点");
        addressList.add("家门口");
        addressList.add("快递柜");
        addressList.add("其他");
        message_sb.setOnStateChangedListener(new SwitchButton.OnStateChangedListener() {
            @Override
            public void toggleToOn(View view) {
                message_sb.setOpened(true);
                if (templateTotal == 0) {
                    DialogFactory.getInstance().showDialog(DeliveryConfirmActivity.this, v -> {
                        jumpWeb = 1;
                        LinkParseUtil.parse(DeliveryConfirmActivity.this, Contants.URl.SMS_TEMPLATE_URL, "");

                    }, view1 -> {
                        message_sb.setOpened(false);
                    }, "您未设置短信内容，请先设置？", null, null);
                } else {
                    if (templateMsgList.size() > 0) {
                        smsTemplateId = templateMsgList.get(currentTemplatePos).getSmsUserTemplateId();
                    }
                }
                rv_message_list.setVisibility(View.VISIBLE);
                tv_sms_num.setVisibility(View.VISIBLE);
            }

            @Override
            public void toggleToOff(View view) {
                message_sb.setOpened(false);
                rv_message_list.setVisibility(View.INVISIBLE);
                tv_sms_num.setVisibility(View.GONE);
                smsTemplateId = "";
            }
        });
        deliveryMsgTemplateAdapter = new DeliveryMsgTemplateAdapter(DeliveryConfirmActivity.this, templateMsgList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DeliveryConfirmActivity.this, LinearLayoutManager.VERTICAL, false);
        rv_message_list.setVisibility(View.INVISIBLE);
        tv_sms_num.setVisibility(View.GONE);
        rv_message_list.setLayoutManager(linearLayoutManager);
        rv_message_list.setAdapter(deliveryMsgTemplateAdapter);
        deliveryMsgTemplateAdapter.setOnItemClickListener(var1 -> {
            currentTemplatePos = var1;
            deliveryMsgTemplateAdapter.setClickPos(var1);
            DeliverySmsTemplateEntity.ContentBean.ListBean.ChildListBean listBean = templateMsgList.get(var1);
            smsTemplateId = listBean.getSmsUserTemplateId();
            smsContentLength = listBean.getSmsContentLength();
            showMssageCount();
        });
        deliveryModel = new DeliveryModel(DeliveryConfirmActivity.this);
        btn_confirm_delivery.setOnClickListener(view -> {
            if (TextUtils.isEmpty(deliveryAddress)) {
                DialogFactory.getInstance().showDialog(DeliveryConfirmActivity.this, v -> {
                    jumpAddress = 1;
                    LinkParseUtil.parse(DeliveryConfirmActivity.this, Contants.URl.DELIVERY_ADDRESS_URL, "");
                }, null, "您未添加地址，请先添加地址？", null, null);
            } else {
                if (fastClick()) {
                    deliveryModel.submitDeliveryCourierNumbers(0, courierNumbers, deliveryAddress, "2", UserInfo.mobile, UserInfo.realname, smsTemplateId, finishType, DeliveryConfirmActivity.this);
                }
            }
        });
        deliveryModel.getDeliveryDefaultAddresses(2, DeliveryConfirmActivity.this);
        deliveryModel.getDeliverySmsTemplateList(1, DeliveryConfirmActivity.this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (jumpWeb == 1) {
            deliveryModel.getDeliverySmsTemplateList(1, DeliveryConfirmActivity.this);
            jumpWeb = 0;
        }
        if (jumpAddress == 1) {
            jumpAddress = 0;
            deliveryModel.getDeliveryDefaultAddresses(2, DeliveryConfirmActivity.this);
        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_confirm_delivery, null);
    }

    @Override
    public String getHeadTitle() {
        return "确认派件";
    }

    private void showTotalNum() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("共计");
        stringBuffer.append(courierTotal);
        stringBuffer.append("个");
        int length = stringBuffer.toString().length();
        SpannableString spannableString = new SpannableString(stringBuffer.toString());
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#597EF7")), 2, length - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), length - 1, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_choice_num.setText(spannableString);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                spUtils.saveStringData("scannerDeliveryList", "");
                ToastUtil.showShortToast(DeliveryConfirmActivity.this, "派件成功");
                EventBus eventBus = EventBus.getDefault();
                Message message = Message.obtain();
                message.what = DELIVERY_OPERATE_SUCCESS;
                eventBus.post(message);
                finish();
                break;
            case 1:
                try {
                    templateMsgList.clear();
                    DeliverySmsTemplateEntity deliverySmsTemplateEntity = GsonUtils.gsonToBean(result, DeliverySmsTemplateEntity.class);
                    DeliverySmsTemplateEntity.ContentBean contentBean = deliverySmsTemplateEntity.getContent();
                    if (null != contentBean) {
                        List<DeliverySmsTemplateEntity.ContentBean.ListBean> beanList = contentBean.getList();
                        if (null != beanList && beanList.size() > 0) {
                            for (DeliverySmsTemplateEntity.ContentBean.ListBean listBean : beanList) {
                                List<DeliverySmsTemplateEntity.ContentBean.ListBean.ChildListBean> childListBeanList = listBean.getChildList();
                                if (null != childListBeanList && childListBeanList.size() > 0) {
                                    for (int j = 0; j < childListBeanList.size(); j++) {
                                        DeliverySmsTemplateEntity.ContentBean.ListBean.ChildListBean childListBean = childListBeanList.get(j);
                                        StringBuffer sb = new StringBuffer();
                                        sb.append("模板");
                                        sb.append(" ");
                                        sb.append(listBean.getSmsTemplateId());
                                        sb.append("-");
                                        sb.append(j + 1);
                                        childListBean.setShowSmsTemplatePlace(sb.toString());
                                        templateMsgList.add(childListBean);
                                    }
                                }
                            }
                        }
                        templateTotal = templateMsgList.size();
                        if (templateTotal > 0) {
                            DeliverySmsTemplateEntity.ContentBean.ListBean.ChildListBean childListBean = templateMsgList.get(0);
                            if (message_sb.isOpened()) {
                                smsTemplateId = childListBean.getSmsUserTemplateId();
                            }
                            smsContentLength = childListBean.getSmsContentLength();
                            showMssageCount();
                            deliveryMsgTemplateAdapter.notifyDataSetChanged();
                        } else {
                            if (message_sb.isOpened()) {
                                message_sb.setOpened(false);
                            }
                        }
                    }
                } catch (Exception e) {

                }
                break;
            case 2:
                try {
                    DeliveryAddressEntity deliveryAddressEntity = GsonUtils.gsonToBean(result, DeliveryAddressEntity.class);
                    DeliveryAddressEntity.ContentBean contentBean = deliveryAddressEntity.getContent();
                    String isDefault = contentBean.getIsDefault();
                    if ("1".equals(isDefault)) {
                        tv_delivery_default.setVisibility(View.VISIBLE);
                    } else {
                        tv_delivery_default.setVisibility(View.GONE);
                    }
                    finishType = contentBean.getSendType();
                    switch (finishType) {
                        case "1":
                            tv_delivery_position.setText("自提点 ");
                            break;
                        case "2":
                            tv_delivery_position.setText("快递柜");
                            break;
                        case "3":
                            tv_delivery_position.setText("家门口 ");
                            break;
                        default:
                            tv_delivery_position.setText("其他");
                            break;
                    }
                    deliveryAddress = contentBean.getSendAddress();
                    if (TextUtils.isEmpty(deliveryAddress)) {
                        DialogFactory.getInstance().showDialog(DeliveryConfirmActivity.this, v -> {
                            jumpAddress = 1;
                            LinkParseUtil.parse(DeliveryConfirmActivity.this, Contants.URl.DELIVERY_ADDRESS_URL, "");

                        }, null, "您未添加地址，请先添加地址？", null, null);
                    } else {
                        tv_delivery_address.setText(deliveryAddress);
                    }
                } catch (Exception e) {

                }
                break;
        }
    }


    private void showMssageCount() {
        int totalMsgNumber = 0;
        for (int i = 0; i < lengthsList.size(); i++) {
            int singleLength = lengthsList.get(i) + smsContentLength;
            int everyMsgNumber = singleLength / 70;
            if (singleLength % 70 != 0) {
                everyMsgNumber++;
            }
            totalMsgNumber += everyMsgNumber;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("短信计费条数:");
        stringBuffer.append(totalMsgNumber);
        stringBuffer.append("条");
        int length = stringBuffer.toString().length();
        SpannableString spannableString = new SpannableString(stringBuffer.toString());
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), 0, 7, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#597EF7")), 7, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_sms_num.setText(spannableString);
    }
}
