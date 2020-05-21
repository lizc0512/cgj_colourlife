package com.tg.delivery.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.delivery.adapter.DeliveryAddressListAdapter;
import com.tg.delivery.adapter.DeliveryMsgTemplateAdapter;
import com.tg.delivery.model.DeliveryModel;
import com.tg.delivery.utils.SwitchButton;
import com.tg.setting.activity.SettingActivity;
import com.tg.setting.util.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 确认派件
 */
public class DeliveryConfirmActivity extends BaseActivity  {

    public static final String COURIERNUMBERS="couriernumbers";
    public static final String COURIERSIZE="couriersize";
    private DeliveryAddressListAdapter deliveryAddressListAdapter;
    private DeliveryMsgTemplateAdapter deliveryMsgTemplateAdapter;
   private TextView tv_choice_num;
   private TextView tv_sms_num;
   private RecyclerView rv_delivery_address;
   private SwitchButton message_sb;
   private RecyclerView rv_message_list;
   private Button btn_confirm_delivery;
   private List<String> addressList=new ArrayList<>();
   private List<String> messageTitleList=new ArrayList<>();
   private List<String> messageContentList=new ArrayList<>();

   private String courierNumbers;
   private int courierTotal;

   private DeliveryModel deliveryModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv_choice_num=findViewById(R.id.tv_choice_num);
        tv_sms_num=findViewById(R.id.tv_sms_num);
        rv_delivery_address=findViewById(R.id.rv_delivery_address);
        message_sb=findViewById(R.id.message_sb);
        rv_message_list=findViewById(R.id.rv_message_list);
        btn_confirm_delivery=findViewById(R.id.btn_confirm_delivery);
        Intent intent=getIntent();
        courierNumbers=intent.getStringExtra(COURIERNUMBERS);
        courierTotal=intent.getIntExtra(COURIERSIZE,0);
        showTotalNum();
        addressList.add("自提点");
        addressList.add("家门口");
        addressList.add("快递柜");
        addressList.add("其他");

        messageTitleList.add("模板1-1");
        messageTitleList.add("模板1-2");
        messageContentList.add("您好，您的包裹##快递公司##运单号##已到达，请到B栋楼下取件。");
        messageContentList.add("您好，您的包裹##快递公司##运单号##已到达，请到快递柜取件。");
        GridLayoutManager gridLayoutManager=new GridLayoutManager(DeliveryConfirmActivity.this, 3);
        rv_delivery_address.setLayoutManager(gridLayoutManager);
        deliveryAddressListAdapter =new DeliveryAddressListAdapter(DeliveryConfirmActivity.this,addressList);
        rv_delivery_address.setAdapter(deliveryAddressListAdapter);
        deliveryAddressListAdapter.setOnItemClickListener(var1 -> {
            deliveryAddressListAdapter.setClickPos(var1);
        });
        message_sb.setOnStateChangedListener(new SwitchButton.OnStateChangedListener() {
            @Override
            public void toggleToOn(View view) {
                message_sb.setOpened(true);
            }

            @Override
            public void toggleToOff(View view) {
                message_sb.setOpened(false);
            }
        });
         deliveryMsgTemplateAdapter=new DeliveryMsgTemplateAdapter(DeliveryConfirmActivity.this,messageTitleList,messageContentList);
         LinearLayoutManager  linearLayoutManager=new LinearLayoutManager(DeliveryConfirmActivity.this,LinearLayoutManager.VERTICAL,false);
         rv_message_list.setLayoutManager(linearLayoutManager);
         rv_message_list.setAdapter(deliveryMsgTemplateAdapter);
        deliveryMsgTemplateAdapter.setOnItemClickListener(var1 -> deliveryMsgTemplateAdapter.setClickPos(var1));
        deliveryModel=new DeliveryModel(DeliveryConfirmActivity.this);
        btn_confirm_delivery.setOnClickListener(view -> {
            deliveryModel.submitDeliveryCourierNumbers(0,courierNumbers,"2", UserInfo.mobile,"","1",DeliveryConfirmActivity.this);
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        DialogFactory.getInstance().showDialog(DeliveryConfirmActivity.this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }, null, "您未设置短信内容，请先设置？", null, null);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_confirm_delivery, null);
    }

    @Override
    public String getHeadTitle() {
        return "确认派件";
    }

    private void showTotalNum(){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("共计");
        stringBuffer.append(courierTotal);
        stringBuffer.append("个");
        int length= stringBuffer.toString().length();
        SpannableString spannableString = new SpannableString(stringBuffer.toString());
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#597EF7")), 2, length-1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), length-1, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_choice_num.setText(spannableString);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:

                break;
        }
    }
}
