package com.tg.delivery.activity;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.intsig.exp.sdk.ISCardScanActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ScreenUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.ClearEditText;
import com.tg.delivery.adapter.DeliveryCompanyAdapter;
import com.tg.delivery.adapter.WareHouseAdapter;
import com.tg.delivery.entity.DeliveryCompanyEntity;
import com.tg.delivery.entity.WareHouseEntity;
import com.tg.delivery.model.DeliveryModel;
import com.youmai.hxsdk.adapter.DividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WarehousingActivity extends BaseActivity implements View.OnClickListener {

    private ClearEditText et_warehouse_num;
    private ClearEditText et_warehouse_phone;
    private List<WareHouseEntity> listItem = new ArrayList<>();
    private RecyclerView rv_warehouse;
    private RecyclerView rv_warehouse_delivery_company;
    private WareHouseAdapter adapter;
    private String tempOrderNum = "";
    private TextView tv_warehouse_allnum;
    private TextView tv_warehouse_commit;
    private TextView tv_warehouse_done;
    private ClearEditText et_warehouse_company;
    private DeliveryModel deliveryModel;
    private List<String> companyList = new ArrayList<>();
    private boolean isShowCompay = true;
    private Activity currentActivity;
    private boolean isSelectCompany = false;
    private boolean isSelectOrderNum = false;
    private String tempResult = "";
    private int editPosition = -1;
    private String editPhone;
    private String editOrderNum;
    private String editCompany;
    private boolean isCheckOrderFinish;
    private int isEditItemPostion;
    private boolean isEditItemStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        useCamareSdk(true);

    }

    private void initData() {
        deliveryModel.getDeliveryCompany(0, this);

    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }

    private void initView(View view) {
        ImageView iv_base_back = view.findViewById(R.id.iv_base_back);
        ImageView iv_delivery_next = view.findViewById(R.id.iv_delivery_next);
        TextView tv_base_title = view.findViewById(R.id.tv_base_title);
        tv_warehouse_allnum = view.findViewById(R.id.tv_warehouse_allnum);
        tv_warehouse_commit = view.findViewById(R.id.tv_warehouse_commit);
        et_warehouse_company = view.findViewById(R.id.et_warehouse_company);
        et_warehouse_num = view.findViewById(R.id.et_warehouse_num);
        et_warehouse_phone = view.findViewById(R.id.et_warehouse_phone);
        rv_warehouse = view.findViewById(R.id.rv_warehouse);
        tv_warehouse_done = view.findViewById(R.id.tv_warehouse_done);
        rv_warehouse_delivery_company = view.findViewById(R.id.rv_warehouse_delivery_company);
        LinearLayoutManager layoutManager = new LinearLayoutManager(currentActivity, LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager layoutManagerCompany = new LinearLayoutManager(currentActivity, LinearLayoutManager.VERTICAL, false);
        rv_warehouse.setLayoutManager(layoutManager);
        rv_warehouse.addItemDecoration(new DividerItemDecoration(this, 5, layoutManager.getOrientation()));
        rv_warehouse_delivery_company.setLayoutManager(layoutManagerCompany);
        tv_base_title.setText("快件入仓");
        iv_base_back.setOnClickListener(v -> {
            currentActivity.finish();
            finish();
        });
        iv_delivery_next.setOnClickListener(v -> {
            if (isShowCompay) {
                if (null != companyList && companyList.size() > 0) {
                    rv_warehouse_delivery_company.setVisibility(View.VISIBLE);
                    DeliveryCompanyAdapter companyAdapter = new DeliveryCompanyAdapter(currentActivity, companyList);
                    rv_warehouse_delivery_company.setAdapter(companyAdapter);
                    companyAdapter.setDelCallBack((position, url, auth_type) -> runOnUiThread(() -> {
                        et_warehouse_company.setText(companyList.get(position));
                        isSelectCompany = true;
                        tempResult = "";
                        rv_warehouse_delivery_company.setVisibility(View.GONE);
                    }));
                }
                isShowCompay = false;
            } else {
                rv_warehouse_delivery_company.setVisibility(View.GONE);
                isShowCompay = true;
            }
        });
        et_warehouse_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String num = s.toString();
                if (num.length() > 0) {
                    if (num.equals(tempOrderNum)) {
                        return;
                    }
                    tempOrderNum = num;
                }
            }
        });
        et_warehouse_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = s.toString();
                if (phone.length() > 0) {
                }
            }
        });
        tv_warehouse_commit.setOnClickListener(v -> {
            if (listItem.size() > 0) {
                initCommitData(listItem);
            } else {
                ToastUtil.showShortToast(currentActivity, "请先添加订单");
            }
        });
        tv_warehouse_done.setOnClickListener(v -> {
            editPhone = et_warehouse_phone.getText().toString().trim();
            editOrderNum = et_warehouse_num.getText().toString().trim();
            editCompany = et_warehouse_company.getText().toString().trim();
            if (!TextUtils.isEmpty(editPhone) && !TextUtils.isEmpty(editOrderNum)) {
                if (!TextUtils.isEmpty(editCompany)) {
                    checkOrderNum(3, editOrderNum, editCompany);
                } else {
                    ToastUtil.showShortToast(currentActivity, "请选择快递公司");
                }
            } else {
                ToastUtil.showShortToast(currentActivity, "请输入运单号和手机号");
            }
        });
    }

    private void initCommitData(List<WareHouseEntity> mList) {
        JSONArray jsonArray = new JSONArray();
        String communityUuid = spUtils.getStringData(SpConstants.storage.DELIVERYUUID, "");
        String communityName = spUtils.getStringData(SpConstants.storage.DELIVERYNAME, "");
        for (int i = 0; i < mList.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("courierNumber", mList.get(i).getCourierNumber());
                jsonObject.put("recipientMobile", mList.get(i).getRecipientMobile());
                jsonObject.put("courierCompany", mList.get(i).getCourierCompany());
                jsonObject.put("sendMobile", UserInfo.mobile);
                jsonObject.put("sendName", UserInfo.realname);
                jsonObject.put("communityUuid", communityUuid);
                jsonObject.put("communityName", communityName);
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject js = new JSONObject();
        try {
            js.put("expressInfoList", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        deliveryModel.postDeliveryCommit(1, js.toString(), this);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        ScreenUtils.hideBottomUIMenu(currentActivity);
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    DeliveryCompanyEntity deliveryCompanyEntity = new DeliveryCompanyEntity();
                    deliveryCompanyEntity = GsonUtils.gsonToBean(result, DeliveryCompanyEntity.class);
                    companyList.clear();
                    companyList.addAll(deliveryCompanyEntity.getContent());
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    ToastUtil.showShortToast(currentActivity, "录入成功");
                    listItem.clear();
                    adapter.setData(listItem);
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(result)) {
                    et_warehouse_num.setText(editOrderNum);
                    isSelectOrderNum = true;
                } else {
                    isCheckOrderFinish = true;
                }
                break;
            case 3:
                if (!TextUtils.isEmpty(result)) {
                    if (isEditItemStatus) {
                        UpdateData();
                    } else {
                        setData(currentActivity);
                    }
                }
                break;
        }
    }

    private void UpdateData() {
        listItem.get(isEditItemPostion).setCourierCompany(editCompany);
        listItem.get(isEditItemPostion).setCourierNumber(editOrderNum);
        listItem.get(isEditItemPostion).setRecipientMobile(editPhone);
        adapter.setData(listItem);
        et_warehouse_num.getText().clear();
        et_warehouse_phone.getText().clear();
        adapter.setEditStatus(-1);
        isEditItemStatus = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_base_back:
                finish();
                break;
        }
    }

    public void useCamareSdk(boolean boolkeep) {
        ISCardScanActivity.setListener(new ISCardScanActivity.OnCardResultListener() {
            @Override
            public void updatePreviewUICallBack(final Activity activity,
                                                RelativeLayout rootView, final Camera camera) {// 支持简单自定义相机页面，在相机页面上添加一层ui
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.CENTER_HORIZONTAL,
                        RelativeLayout.TRUE);
                // **********************************添加动态的布局
                currentActivity = activity;
                deliveryModel = new DeliveryModel(currentActivity);
                initData();
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.activity_warehousing, null);
                initView(view);
                rootView.addView(view, lp);
            }

            @Override
            public void resultSuccessCallback(final String phone,
                                              final String barcode) {// 识别标识 手机号 一维码

            }

            @Override
            public void resultErrorCallBack(final int error) {// 识别错误返回错误码，并关闭相机页面

            }

            @Override
            public void resultSuccessKeepPreviewCallback(final String result,
                                                         final String comment, int type) {

                if (type == 1) { //手机号
                    if (isSelectOrderNum) {
                        et_warehouse_phone.setText(result);
                        setData(currentActivity);
                    } else {
                        ToastUtil.showShortToast(currentActivity, "请先扫码运单号");
                    }
                } else if (type == 2) { //一维码
                    if (!tempResult.equals(result)) {
                        tempResult = result;
                        if (isSelectCompany) {
                            editCompany = et_warehouse_company.getText().toString().trim();
                            editPhone = et_warehouse_phone.getText().toString().trim();
                            editOrderNum = result;
                            checkOrderNum(2, result, editCompany);
                        } else {
                            ToastUtil.showShortToast(currentActivity, "请先选择快递公司");
                        }
                    }

                }
            }

        });
        Intent intent = new Intent(this, ISCardScanActivity.class);

        intent.putExtra(ISCardScanActivity.EXTRA_KEY_APP_KEY, Contants.APP.APP_KEY);
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_BOOL_CONTIUE_AUTOFOCUS,
                true);// true 表示参数自动对焦模式 false 采用默认的定时对焦
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_BOOL_BAR, false);// 是否开启同时识别
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_BOOL_KEEP_PREVIEW,
                boolkeep);// true连续预览识别
        // false
        // 单次识别则结束

        intent.putExtra(ISCardScanActivity.EXTRA_KEY_PREVIEW_HEIGHT, false ? 1f : 55f);// 预览框高度 根据是否同时识别 变化预览框高度
        // 单位dp
        // 一定使用float数值否则设置无效
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_PREVIEW_MATCH_LEFT, 15f);// 预览框左边距
        // 单位dp
        // 一定使用float数值否则设置无效
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_PREVIEW_MATCH_TOP, 60f);// 预览框上边距
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_SHOW_CLOSE, false);// true打开闪光灯和关闭按钮
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_COLOR_MATCH, 0xffffffff);// 指定SDK相机模块ISCardScanActivity四边框角线条,检测到身份证图片后的颜色
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_COLOR_NORMAL, 0xffffffff);// 指定SDK相机模块ISCardScanActivity四边框角线条颜色，正常显示颜色
        startActivity(intent);

    }

    private void setData(Activity activity) {
        String phone = et_warehouse_phone.getText().toString().trim();
        String orderNum = et_warehouse_num.getText().toString().trim();
        String company = et_warehouse_company.getText().toString().trim();
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(orderNum)) {
            WareHouseEntity entity = new WareHouseEntity(orderNum, phone, company);
            listItem.add(entity);
            et_warehouse_phone.getText().clear();
            et_warehouse_num.getText().clear();
            if (null == adapter) {
                adapter = new WareHouseAdapter(activity, listItem);
                rv_warehouse.setAdapter(adapter);
            } else {
                adapter.setData(listItem);
            }
            adapter.setDelCallBack((position, url, auth_type) -> {
                ToastUtil.showShortToast(currentActivity, "删除成功");
                editPosition = -1;
                et_warehouse_num.setText("");
                et_warehouse_phone.setText("");
                listItem.remove(position);
                adapter.setEditStatus(editPosition);
                adapter.setData(listItem);
                setNum();
            });
            adapter.setEditCallBack((position, url, auth_type) -> {
                editPosition = position;
                adapter.setEditStatus(position);
                et_warehouse_num.setText(listItem.get(position).getCourierNumber());
                et_warehouse_phone.setText(listItem.get(position).getRecipientMobile());
                et_warehouse_company.setText(listItem.get(position).getCourierCompany());
                et_warehouse_num.setSelection(listItem.get(position).getCourierNumber().length());
                et_warehouse_phone.setSelection(listItem.get(position).getRecipientMobile().length());
                isEditItemPostion = position;
                isEditItemStatus = true;

            });
            adapter.setCancelCallBack((position, url, auth_type) -> {
                isEditItemStatus = false;
                adapter.setEditStatus(-1);
                et_warehouse_num.setText("");
                et_warehouse_phone.setText("");
                editPosition = -1;
            });
            setNum();
        }
    }

    private void checkOrderNum(int what, String num, String company) {
        if (listItem.size() > 50) {
            ToastUtil.showShortToast(currentActivity, "一次最多录入50条数据");
        } else {
            deliveryModel.getDeliveryCheckOrder(what, num, company, this);
        }
    }

    private void setNum() {
        String size = String.valueOf(listItem.size());
        tv_warehouse_allnum.setText(size);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != currentActivity && currentActivity.isFinishing()) {
            finish();
        }
    }
}