package com.tg.delivery.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.intsig.exp.sdk.ISCardScanActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.SoftKeyboardUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.delivery.adapter.DeliveryNumberListAdapter;
import com.tg.delivery.entity.DeliveryInforEntity;
import com.tg.delivery.entity.DeliveryStateEntity;
import com.tg.delivery.model.DeliveryModel;

import java.util.ArrayList;
import java.util.List;

import static com.tg.delivery.activity.DeliveryConfirmActivity.COURIERNUMBERS;
import static com.tg.delivery.activity.DeliveryConfirmActivity.COURIERSIZE;

/**
 * 扫码派件
 */
public class DeliveryTransferActivity extends BaseActivity {
    private EditText ed_input_code;
    private TextView tv_define;
    private TextView tv_choice_num;
    private TextView tv_define_delivery;
    private RecyclerView rv_delivery_infor;

    private List<DeliveryInforEntity.DataBean> deliveryInforList = new ArrayList<>();
    private DeliveryNumberListAdapter deliveryNumberListAdapter;

    private Activity currentActivity;
    private String courierNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!XXPermissions.isHasPermission(DeliveryTransferActivity.this, Manifest.permission.CAMERA)) {
                XXPermissions.with(DeliveryTransferActivity.this)
                        .permission(Manifest.permission.CAMERA)
                        .request(new OnPermission() {
                            @Override
                            public void hasPermission(List<String> granted, boolean isAll) {
                                useCamareSdk(true);
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean quick) {
                                XXPermissions.gotoPermissionSettings(DeliveryTransferActivity.this);
                            }
                        });
            } else {
                useCamareSdk(true);
            }
        } else {
            useCamareSdk(true);
        }
    }


    /***删除当前的单号***/
    public void delDeliveryItem(int position) {
        ToastUtil.showShortToast(currentActivity, "删除成功");
        deliveryInforList.remove(position);
        editPosition = -1;
        ed_input_code.setText("");
        deliveryNumberListAdapter.setEditStatus(editPosition);
        deliveryNumberListAdapter.notifyDataSetChanged();
    }

    private int editPosition = -1;

    /***编辑单号***/
    public void editDeliveryItem(int position) {
        editPosition = position;
        deliveryNumberListAdapter.setEditStatus(position);
        DeliveryInforEntity.DataBean dataBean = deliveryInforList.get(position);
        String courierNumber = dataBean.getCourierNumber();
        ed_input_code.setText(courierNumber);
        ed_input_code.setSelection(courierNumber.length());
    }

    /***取消编辑单号***/
    public void cancelDeliveryItem() {
        deliveryNumberListAdapter.setEditStatus(-1);
        ed_input_code.setText("");
        editPosition = -1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != currentActivity && currentActivity.isFinishing()) {
            finish();
        }
    }

    private void showTotalNum() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("共计");
        stringBuffer.append(deliveryInforList.size());
        stringBuffer.append("个");
        int length = stringBuffer.toString().length();
        SpannableString spannableString = new SpannableString(stringBuffer.toString());
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#597EF7")), 2, length - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), length - 1, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_choice_num.setText(spannableString);
    }

    private void initView(View rootView) {
        ImageView iv_base_location = rootView.findViewById(R.id.iv_base_location);
        iv_base_location.setOnClickListener(view -> {
            currentActivity.finish();
            finish();
        });
        TextView tv_base_title = rootView.findViewById(R.id.tv_base_title);
        iv_base_location.setVisibility(View.VISIBLE);
        tv_base_title.setVisibility(View.VISIBLE);
        tv_base_title.setText("交接快件");
        ed_input_code = rootView.findViewById(R.id.ed_input_code);
        tv_define = rootView.findViewById(R.id.tv_define);
        tv_choice_num = rootView.findViewById(R.id.tv_choice_num);
        tv_define_delivery = rootView.findViewById(R.id.tv_define_delivery);
        rv_delivery_infor = rootView.findViewById(R.id.rv_delivery_infor);
        tv_define_delivery.setText("接收快件");
        tv_define.setOnClickListener(view -> {
            courierNumber = ed_input_code.getText().toString().trim();
            if (!TextUtils.isEmpty(courierNumber)) {
                //根据订单号 查询手机号
                if (editPosition == -1) {
                    //表示用户输入新增
                    if (deliveryInforList.size() > 50) {
                        ToastUtil.showShortToast(currentActivity, "运单号最大只能录入50个");
                    } else {
                        if (!includeDelivery(courierNumber)) {
                            getDeliverStatus();
                        } else {
                            ToastUtil.showShortToast(currentActivity, "运单号已录入,请勿重复录入");
                        }
                    }
                } else {
                    //表示单号列表的编辑 请求数据成功 将editPosition置为-1
                    deliveryInforList.get(editPosition).setCourierNumber(courierNumber);
                    deliveryNumberListAdapter.notifyDataSetChanged();
                }
            } else {
                ToastUtil.showShortToast(currentActivity, "请输入运单号");
            }
            SoftKeyboardUtils.showSoftKeyboard(currentActivity);
            hideBottomUIMenu();
        });
        tv_define_delivery.setOnClickListener(view -> {
            if (deliveryInforList.size()>0){
            List<String> deliveryNumberList=new ArrayList<>();
            for (DeliveryInforEntity.DataBean dataBean : deliveryInforList){
                deliveryNumberList.add(dataBean.getCourierNumber());
            }
            if (null==deliveryModel){
                deliveryModel=new DeliveryModel(currentActivity);
            }
            deliveryModel.submitDeliveryCourierNumbers(2,GsonUtils.gsonString(deliveryNumberList),"3", UserInfo.mobile,"","",0,DeliveryTransferActivity.this);
            }else{
                ToastUtil.showShortToast(currentActivity, "暂无运单进行交接");
            }

        });
        deliveryNumberListAdapter = new DeliveryNumberListAdapter(currentActivity, DeliveryTransferActivity.this
                , deliveryInforList);
        rv_delivery_infor.setLayoutManager(new LinearLayoutManager(currentActivity));
        rv_delivery_infor.setAdapter(deliveryNumberListAdapter);
    }

    private boolean includeDelivery(String deliveryNumber) {
        boolean isContain = false;
        for (DeliveryInforEntity.DataBean dataBean : deliveryInforList) {
            if (deliveryNumber.equals(dataBean.getCourierNumber())) {
                isContain = true;
                break;
            } else {
                continue;
            }
        }
        return isContain;
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return "";
    }

    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide
                    //  | View.SYSTEM_UI_FLAG_FULLSCREEN // 不隐藏状态栏，因为隐藏了比如时间电量等信息也会隐藏
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public void useCamareSdk(boolean boolkeep) {
        ISCardScanActivity.setListener(new ISCardScanActivity.OnCardResultListener() {
            @Override
            public void updatePreviewUICallBack(Activity activity,
                                                RelativeLayout rootView, final Camera camera) {// 支持简单自定义相机页面，在相机页面上添加一层ui
                // TODO Auto-generated method stub
                currentActivity = activity;
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.CENTER_HORIZONTAL,
                        RelativeLayout.TRUE);
                // **********************************添加动态的布局
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.activity_sweep_code, null);
                initView(view);
                rootView.addView(view, lp);
            }

            @Override
            public void resultSuccessCallback(final String phone,
                                              final String barcode) {// 识别标识 手机号 一维码
                // TODO Auto-generated method stub
                ToastUtil.showShortToast(currentActivity, "phone:" + phone + ":barcode" + barcode);
            }

            @Override
            public void resultErrorCallBack(final int error) {// 识别错误返回错误码，并关闭相机页面
                ToastUtil.showShortToast(currentActivity, "error:" + error);

            }

            @Override
            public void resultSuccessKeepPreviewCallback(final String result,
                                                         final String comment, int type) {
                if (deliveryInforList.size() > 50) {
                    ToastUtil.showShortToast(currentActivity, "运单号最大只能录入50个");
                } else {
                    if (!includeDelivery(result)) {
                        courierNumber = result;
                        getDeliverStatus();
                    } else {
                        ToastUtil.showShortToast(currentActivity, "运单号已录入,请勿重复录入");
                    }
                }
            }
        });
        Intent intent = new Intent(this, ISCardScanActivity.class);
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_APP_KEY, "Q9PDXKXJbBCHDWF0CFS8MLeX");
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_BOOL_CONTIUE_AUTOFOCUS,
                true);// true 表示参数自动对焦模式 false 采用默认的定时对焦
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_BOOL_BAR, false);// 是否开启同时识别
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_BOOL_KEEP_PREVIEW, boolkeep);// true连续预览识别
        // false
        // 单次识别则结束
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_PREVIEW_HEIGHT, false ? 1f : 70f);// 预览框高度 根据是否同时识别 变化预览框高度
        // 单位dp
        // 一定使用float数值否则设置无效
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_PREVIEW_MATCH_LEFT, 15f);// 预览框左边距
        // 单位dp
        // 一定使用float数值否则设置无效
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_PREVIEW_MATCH_TOP, 70f);// 预览框上边距
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_SHOW_CLOSE, false);// true打开闪光灯和关闭按钮
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_COLOR_MATCH, 0xffffffff);// 指定SDK相机模块ISCardScanActivity四边框角线条,检测到身份证图片后的颜色
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_COLOR_NORMAL, 0xffffffff);// 指定SDK相机模块ISCardScanActivity四边框角线条颜色，正常显示颜色
        startActivity(intent);
    }

    private DeliveryModel deliveryModel;

    private void getDeliverInfor() {
        if (null == deliveryModel) {
            deliveryModel = new DeliveryModel(currentActivity);
        }
        deliveryModel.getDeliveryInfor(1, courierNumber, DeliveryTransferActivity.this);
    }

    private void getDeliverStatus() {
        if (null == deliveryModel) {
            deliveryModel = new DeliveryModel(currentActivity);
        }
        deliveryModel.getDeliveryStatus(0, courierNumber, DeliveryTransferActivity.this);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                try {
                    DeliveryStateEntity deliveryStateEntity = GsonUtils.gsonToBean(result, DeliveryStateEntity.class);
                    String status=deliveryStateEntity.getContent();
                    if ("1".equals(status)) {
                        ToastUtil.showShortToast(currentActivity, "运单已经派送,请勿重复扫描");
                    } else if("0".equals(status)){
                        ToastUtil.showShortToast(currentActivity, "该运单在系统中未找到,请确认是否录入系统");
                    }else{
                        getDeliverInfor();
                    }
                } catch (Exception e) {

                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        DeliveryInforEntity deliveryInforEntity = GsonUtils.gsonToBean(result, DeliveryInforEntity.class);
                        deliveryInforList.add(0, deliveryInforEntity.getData());
                        deliveryNumberListAdapter.notifyDataSetChanged();
                        showTotalNum();
                    } catch (Exception e) {

                    }
                }
                break;
            case 2:


                break;
        }
    }
}
