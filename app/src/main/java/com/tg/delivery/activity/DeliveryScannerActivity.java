package com.tg.delivery.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.delivery.adapter.DeliveryNumberListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 扫码派件
 */
public class DeliveryScannerActivity extends BaseActivity {
    private EditText ed_input_code;
    private TextView tv_define;
    private TextView tv_choice_num;
    private TextView tv_define_delivery;
    private RecyclerView rv_delivery_infor;

    private List<String> deliveryList=new ArrayList<>();
    private List<String> phoneList=new ArrayList<>();
    private DeliveryNumberListAdapter deliveryNumberListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ed_input_code = findViewById(R.id.ed_input_code);
        tv_define = findViewById(R.id.tv_define);
        tv_choice_num = findViewById(R.id.tv_choice_num);
        tv_define_delivery = findViewById(R.id.tv_define_delivery);
        rv_delivery_infor = findViewById(R.id.rv_delivery_infor);
        tv_define.setOnClickListener(view -> {
            String  tracking_number=ed_input_code.getText().toString().trim();
            if (!TextUtils.isEmpty(tracking_number)){
                //根据订单号 查询手机号
                if (editPosition==-1){
                    //表示用户输入新增
                    if (!deliveryList.contains(tracking_number)){
                        deliveryList.add(0,tracking_number);
                        phoneList.add(0,"12333444");
                        deliveryNumberListAdapter.notifyDataSetChanged();
                    }else{
                        ToastUtil.showShortToast(DeliveryScannerActivity.this,"运单号已录入,请勿重复录入");
                    }
                }else{
                    //表示单号列表的编辑 请求数据成功 将editPosition置为-1
                    deliveryList.set(editPosition,tracking_number);
                    deliveryNumberListAdapter.notifyDataSetChanged();
                }
            }else{
                ToastUtil.showShortToast(DeliveryScannerActivity.this,"请输入运单号");
            }
        });
        tv_define_delivery.setOnClickListener(view -> {
           Intent  it = new Intent(DeliveryScannerActivity.this, DeliveryConfirmActivity.class);
            startActivity(it);
        });
        deliveryList.add("133344");
        phoneList.add("15345343");
        deliveryNumberListAdapter=new DeliveryNumberListAdapter(DeliveryScannerActivity.this,deliveryList,phoneList);
        rv_delivery_infor.setLayoutManager(new LinearLayoutManager(DeliveryScannerActivity.this));
        rv_delivery_infor.setAdapter(deliveryNumberListAdapter);
    }


    /***删除当前的单号***/
    public void  delDeliveryItem(int position){
        deliveryList.remove(position);
        phoneList.remove(position);
        editPosition=-1;
        ed_input_code.setText("");
        deliveryNumberListAdapter.setEditStatus(editPosition);
        deliveryNumberListAdapter.notifyDataSetChanged();
    }
    private int editPosition=-1;
    /***编辑单号***/
    public void  editDeliveryItem(int position){
        editPosition=position;
        deliveryNumberListAdapter.setEditStatus(position);
        ed_input_code.setText(deliveryList.get(position));
        ed_input_code.setSelection(deliveryList.get(position).length());
    }
    /***取消编辑单号***/
    public void  cancelDeliveryItem(){
        deliveryNumberListAdapter.setEditStatus(-1);
        ed_input_code.setText("");
        editPosition=-1;
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_sweep_code, null);
    }

    @Override
    public String getHeadTitle() {
        return "扫码派件";
    }
}
