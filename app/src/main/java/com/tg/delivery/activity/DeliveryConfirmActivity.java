package com.tg.delivery.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.delivery.adapter.DeliveryAddressListAdapter;
import com.tg.delivery.adapter.DeliveryMsgTemplateAdapter;
import com.tg.delivery.utils.SwitchButton;
import com.tg.setting.util.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 确认派件
 */
public class DeliveryConfirmActivity extends BaseActivity  {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv_choice_num=findViewById(R.id.tv_choice_num);
        tv_sms_num=findViewById(R.id.tv_sms_num);
        rv_delivery_address=findViewById(R.id.rv_delivery_address);
        message_sb=findViewById(R.id.message_sb);
        rv_message_list=findViewById(R.id.rv_message_list);
        btn_confirm_delivery=findViewById(R.id.btn_confirm_delivery);
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
        deliveryMsgTemplateAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int var1) {
                deliveryMsgTemplateAdapter.setClickPos(var1);
            }
        });
        btn_confirm_delivery.setOnClickListener(view -> {

        });
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
        stringBuffer.append(2);
        stringBuffer.append("个");
        int length= stringBuffer.toString().length();
        SpannableString spannableString = new SpannableString(stringBuffer.toString());
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#597EF7")), 2, length-1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), length-1, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_choice_num.setText(spannableString);
    }
}
