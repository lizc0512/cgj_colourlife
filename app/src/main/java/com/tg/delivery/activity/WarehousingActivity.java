package com.tg.delivery.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
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

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.intsig.exp.sdk.ISCardScanActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ScreenUtils;
import com.tg.coloursteward.util.SoftKeyboardUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.ClearEditText;
import com.tg.delivery.adapter.DeliveryCompanyAdapter;
import com.tg.delivery.adapter.WareHouseAdapter;
import com.tg.delivery.entity.DeliveryCompanyEntity;
import com.tg.delivery.entity.WareHouseEntity;
import com.tg.delivery.model.DeliveryModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
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
    private RelativeLayout rl_warehouse_card;
    private TextView tv_warehouse_commit;
    private TextView tv_warehouse_done;
    private ClearEditText et_warehouse_company;
    private DeliveryModel deliveryModel;
    private List<String> companyList = new ArrayList<>();
    private List<String> companySerachList = new ArrayList<>();
    private boolean isShowCompay = true;
    private Activity currentActivity;
    private boolean isSelectCompany = false;
    private boolean isSelectOrderNum = false;
    private String tempResult = "";
    private String tempPhoneResult = "";
    private int editPosition = -1;
    private String editPhone;
    private String editOrderNum;
    private String editCompany;
    private boolean isCheckOrderFinish;
    private int isEditItemPostion;//处理编辑状态的Item位置
    private boolean isEditItemStatus;//是否是编辑状态
    private boolean isClickCompany = false;
    private String readyCheckNum;//当次检验通过的运单号
    private long scannCurrentTime;//当前扫码时间
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XXPermissions.with(this)
                .constantRequest()
                .permission(Manifest.permission.CAMERA)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        useCamareSdk(true);
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        ToastUtil.showShortToast(WarehousingActivity.this, "请在程序管理中，打开彩管家的拍照权限");
                        XXPermissions.gotoPermissionSettings(WarehousingActivity.this);
                    }
                });
    }

    private void initData() {
        deliveryModel = new DeliveryModel(currentActivity);
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
        rl_warehouse_card = view.findViewById(R.id.rl_warehouse_card);
        tv_warehouse_commit = view.findViewById(R.id.tv_warehouse_commit);
        et_warehouse_company = view.findViewById(R.id.et_warehouse_company);
        et_warehouse_num = view.findViewById(R.id.et_warehouse_num);
        et_warehouse_phone = view.findViewById(R.id.et_warehouse_phone);
        rv_warehouse = view.findViewById(R.id.rv_warehouse);
        LinearLayoutManager layoutManager = new LinearLayoutManager(currentActivity, LinearLayoutManager.VERTICAL, false);
        rv_warehouse.setLayoutManager(layoutManager);
        tv_warehouse_done = view.findViewById(R.id.tv_warehouse_done);
        rv_warehouse_delivery_company = view.findViewById(R.id.rv_warehouse_delivery_company);
        LinearLayoutManager layoutManagerCompany = new LinearLayoutManager(currentActivity, LinearLayoutManager.VERTICAL, false);
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
                        isSelectCompany = true;
                        isClickCompany = true;
                        tempResult = "";
                        tempPhoneResult = "";
                        et_warehouse_num.requestFocus();
                        rv_warehouse_delivery_company.setVisibility(View.GONE);
                        et_warehouse_company.setText(companyList.get(position));
                        et_warehouse_company.setSelection(companyList.get(position).length());
                        rl_warehouse_card.setBackgroundResource(R.drawable.bg_warehouse_et_blue);
                        SoftKeyboardUtils.hideSoftKeyboard(currentActivity, et_warehouse_company);
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
                if (num.length() == 0) {
                    tempResult = "";
                }
            }
        });

        et_warehouse_company.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String word = s.toString();
                if (word.length() > 0) {
                    if (!isClickCompany) {//未点击，则允许搜索
                        if (!isEditItemStatus) {//编辑状态下，不搜索和处理
                            deliveryModel.getSearchDelivery(4, word, WarehousingActivity.this);
                        }
                    } else {
                        rv_warehouse_delivery_company.setVisibility(View.GONE);
                    }
                } else {
                    rv_warehouse_delivery_company.setVisibility(View.GONE);
                    isClickCompany = false;
                    isSelectCompany = false;
                }
            }
        });
        tv_warehouse_commit.setOnClickListener(v -> {
            if (fastClick()) {//间隔大于2秒，才执行
                if (listItem.size() > 0) {
                    initCommitData(listItem);
                } else {
                    ToastUtil.showShortToast(currentActivity, "请先添加订单");
                }
            }
        });
        tv_warehouse_done.setOnClickListener(v -> {
            editPhone = et_warehouse_phone.getText().toString().trim();
            editOrderNum = et_warehouse_num.getText().toString().trim();
            editCompany = et_warehouse_company.getText().toString().trim();
            SoftKeyboardUtils.hideSystemSoftKeyboard(currentActivity, et_warehouse_num);
            if (isSelectCompany) {
                if (!TextUtils.isEmpty(editOrderNum)) {
                    if (!TextUtils.isEmpty(editPhone)) {
                        if (!TextUtils.isEmpty(editCompany)) {
                            if (isEditItemStatus) {
                                if (isHaveRepeat(editOrderNum)) {//listitem中是否包含了当前运单号
                                    if (isEditHaveRepeat(editOrderNum, isEditItemPostion)) {//编辑的运单号是否等于输入框运单号
                                        UpdateData();
                                    } else {//跟list中其他数据重复,则提示
                                        ToastUtil.showShortToastCenter(currentActivity, "请勿重复输入运单号");
                                    }
                                } else {//不包含表示为全新运单号,直接修改
                                    checkOrderNum(3, editOrderNum, editCompany);
                                }
                            } else {
                                if (!isHaveRepeat(editOrderNum)) {
                                    checkOrderNum(3, editOrderNum, editCompany);
                                } else {
                                    ToastUtil.showShortToastCenter(currentActivity, "请勿重复输入运单号");
                                }
                            }
                        } else {
                            etCompanyFocus();
                        }
                    } else {
                        ToastUtil.showShortToast(currentActivity, "请输入手机号");
                        et_warehouse_phone.requestFocus();
                    }
                } else {
                    ToastUtil.showShortToast(currentActivity, "请输入运单号");
                    et_warehouse_num.requestFocus();
                }
            } else {
                etCompanyFocus();
            }
        });
        String cache = spUtils.getStringData(SpConstants.UserModel.WAREHOUSECACHE, "");
        if (!TextUtils.isEmpty(cache)) {
            listItem = GsonUtils.jsonToList(cache, WareHouseEntity.class);
            setAdapter(currentActivity);
        }
    }

    private void etCompanyFocus() {
        ToastUtil.showShortToast(currentActivity, "请选择快递公司");
        rl_warehouse_card.setBackgroundResource(R.drawable.bg_delivery_item_red);
        et_warehouse_company.requestFocus();
    }

    private void initCommitData(List<WareHouseEntity> mList) {
        JSONArray jsonArray = new JSONArray();
        String communityUuid = spUtils.getStringData(SpConstants.storage.DELIVERYUUID, "");
        String communityName = spUtils.getStringData(SpConstants.storage.DELIVERYNAME, "");
        String cropId = spUtils.getStringData(SpConstants.storage.CORPID, "");
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
                jsonObject.put("cropId", cropId);
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        deliveryModel.postDeliveryCommit(1, jsonArray.toString(), this);
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
                    ToastUtil.showShortToast(this, "录入成功");
                    listItem.clear();
                    adapter.setData(listItem);
                    setNum();
                    spUtils.saveStringData(SpConstants.UserModel.WAREHOUSECACHE, "");
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(result)) {
                    et_warehouse_num.setText(editOrderNum);
                    et_warehouse_num.setSelection(editOrderNum.length());
                    isSelectOrderNum = true;
                    readyCheckNum = editOrderNum;
                    checkPhoneContent();
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
            case 4:
                if (!TextUtils.isEmpty(result)) {
                    DeliveryCompanyEntity deliveryCompanyEntity = new DeliveryCompanyEntity();
                    deliveryCompanyEntity = GsonUtils.gsonToBean(result, DeliveryCompanyEntity.class);
                    companySerachList.clear();
                    companySerachList.addAll(deliveryCompanyEntity.getContent());
                    if (null != companySerachList && companySerachList.size() > 0) {
                        rv_warehouse_delivery_company.setVisibility(View.VISIBLE);
                        DeliveryCompanyAdapter companyAdapter = new DeliveryCompanyAdapter(currentActivity, companySerachList);
                        rv_warehouse_delivery_company.setAdapter(companyAdapter);
                        companyAdapter.setDelCallBack((position, url, auth_type) ->
                                runOnUiThread(() -> {
                                    isSelectCompany = true;
                                    tempResult = "";
                                    tempPhoneResult = "";
                                    rv_warehouse_delivery_company.setVisibility(View.GONE);
                                    isClickCompany = true;
                                    SoftKeyboardUtils.hideSoftKeyboard(currentActivity, et_warehouse_company);
                                    et_warehouse_company.setText(companySerachList.get(position));
                                    et_warehouse_company.setSelection(companySerachList.get(position).length());
                                    rl_warehouse_card.setBackgroundResource(R.drawable.bg_warehouse_et_blue);

                                }));
                    }
                }
                break;
        }
    }

    private void checkPhoneContent() {
        String phone = et_warehouse_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            et_warehouse_phone.requestFocus();
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
                // **********************************添加动态的布局
                if (null == currentActivity) {
                    currentActivity = activity;
                    mCamera = camera;
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.CENTER_HORIZONTAL,
                            RelativeLayout.TRUE);
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.activity_warehousing, null);
                    initView(view);
                    initData();
                    rootView.addView(view, lp);
                }
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
                    if (delayScan()) {
                        setTouchFocus();
                        if (isSelectCompany) {
                            String phone = et_warehouse_num.getText().toString().trim();
                            if (!phone.equals(result)) {
                                tempPhoneResult = result;
                                if (isSelectOrderNum && !TextUtils.isEmpty(et_warehouse_num.getText().toString().trim())) {
                                    soundPlay();
                                    et_warehouse_phone.setText(result);
                                    et_warehouse_phone.setSelection(et_warehouse_phone.getText().toString().length());
                                    SoftKeyboardUtils.hideSystemSoftKeyboard(currentActivity, et_warehouse_phone);
                                    String num = et_warehouse_num.getText().toString().trim();
                                    if (num.equals(readyCheckNum)) {//运单号是否经过检验了
                                        setData(currentActivity);
                                    }
                                } else {
                                    ToastUtil.showShortToastCenter(currentActivity, "请先扫码运单号");
                                    et_warehouse_num.requestFocus();
                                }
                            } else {
                                ToastUtil.showShortToastCenter(currentActivity, "请勿重复扫描手机号");
                            }
                        } else {
                            ToastUtil.showShortToastCenter(currentActivity, "请先选择快递公司");
                            rl_warehouse_card.setBackgroundResource(R.drawable.bg_delivery_item_red);
                        }
                    }
                } else if (type == 2) { //一维码
                    if (delayScan()) {
                        setTouchFocus();
                        if (isSelectCompany) {
                            String num = et_warehouse_num.getText().toString().trim();
                            if (!num.equals(result)) {
                                if (!isHaveRepeat(result)) {
                                    soundPlay();
                                    tempResult = result;
                                    editCompany = et_warehouse_company.getText().toString().trim();
                                    editPhone = et_warehouse_phone.getText().toString().trim();
                                    editOrderNum = result;
                                    checkOrderNum(2, result, editCompany);
                                } else {
                                    ToastUtil.showShortToastCenter(currentActivity, "请勿重复扫描运单号");
                                }
                            } else {
                                ToastUtil.showShortToastCenter(currentActivity, "请勿重复扫描运单号");
                            }
                        } else {
                            ToastUtil.showShortToastCenter(currentActivity, "请先选择快递公司");
                            et_warehouse_company.requestFocus();
                            rl_warehouse_card.setBackgroundResource(R.drawable.bg_delivery_item_red);
                        }
                    }
                }
            }
        });
        Intent intent = new Intent(this, ISCardScanActivity.class);

        intent.putExtra(ISCardScanActivity.EXTRA_KEY_APP_KEY, Contants.APP.APP_KEY);
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_BOOL_CONTIUE_AUTOFOCUS,
                false);// true 表示参数自动对焦模式 false 采用默认的定时对焦
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_BOOL_BAR, false);// 是否开启同时识别
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_BOOL_KEEP_PREVIEW,
                boolkeep);// true连续预览识别
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
        intent.putExtra(ISCardScanActivity.EXTRA_KEY_BOOL_OPEN_VOICE, false);// 是否开启提示音
        startActivity(intent);

    }

    private void setTouchFocus() {
        if (mCamera != null) {
            //auto  //自动 infinity //无穷远 macro //微距 continuous-picture //持续对焦 fixed //固定焦距
            String focusMode = "auto";
            try {
                Camera.Parameters params = mCamera.getParameters();
                List<String> modes = params.getSupportedFocusModes();
                if (modes.contains("continuous-picture")) {
                    params.setFocusMode("continuous-picture");
                } else if (modes.contains("fixed")) {
                    params.setFocusMode("fixed");
                } else if (modes.contains("infinity")) {
                    params.setFocusMode("infinity");
                } else {
                    params.setFocusMode((String) modes.get(0));
                }
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                mCamera.cancelAutoFocus();
                mCamera.setParameters(params);
                mCamera.autoFocus((b, camera) -> {
                    if (!b) {
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 响铃表示扫码成功
     */
    private void soundPlay() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone rt = RingtoneManager.getRingtone(currentActivity, uri);
        rt.play();
    }

    /**
     * 延迟扫码,避免回调过快，影响体验
     */
    private boolean delayScan() {
        long nowCurrentTime = System.currentTimeMillis();
        if (nowCurrentTime - scannCurrentTime >= 1500) {
            scannCurrentTime = nowCurrentTime;
            return true;
        }
        return false;
    }

    private boolean isHaveRepeat(String result) {
        boolean ishave = false;
        for (WareHouseEntity dataBean : listItem) {
            if (result.equals(dataBean.getCourierNumber())) {
                ishave = true;
                break;
            } else {
                continue;
            }
        }
        return ishave;
    }

    private boolean isEditHaveRepeat(String result, int postion) {
        boolean ishave = false;
        for (int i = 0; i < listItem.size(); i++) {
            if (result.equals(listItem.get(i).getCourierNumber())) {
                if (postion == i) {
                    ishave = true;
                } else {
                    ishave = false;
                }
                break;
            }
        }
        return ishave;
    }

    private void setAdapter(Activity activity) {
        Collections.reverse(listItem);
        et_warehouse_phone.getText().clear();
        et_warehouse_num.getText().clear();
        et_warehouse_num.requestFocus();
        spUtils.saveStringData(SpConstants.UserModel.WAREHOUSECACHE, GsonUtils.gsonString(listItem));
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
            spUtils.saveStringData(SpConstants.UserModel.WAREHOUSECACHE, GsonUtils.gsonString(listItem));
        });
        adapter.setEditCallBack((position, url, auth_type) -> {
            isEditItemStatus = true;
            editPosition = position;
            adapter.setEditStatus(position);
            et_warehouse_num.setText(listItem.get(position).getCourierNumber());
            et_warehouse_phone.setText(listItem.get(position).getRecipientMobile());
            et_warehouse_company.setText(listItem.get(position).getCourierCompany());
            et_warehouse_num.setSelection(listItem.get(position).getCourierNumber().length());
            et_warehouse_num.requestFocus();
            isEditItemPostion = position;
            isSelectCompany = true;
            rl_warehouse_card.setBackgroundResource(R.drawable.bg_warehouse_et_blue);

        });
        adapter.setCancelCallBack((position, url, auth_type) -> {
            isEditItemStatus = false;
            adapter.setEditStatus(-1);
            et_warehouse_num.setText("");
            et_warehouse_phone.setText("");
            editPosition = -1;
            rl_warehouse_card.setBackgroundResource(R.drawable.bg_warehouse_et_blue);
        });
        setNum();
    }

    private void setData(Activity activity) {
        String phone = et_warehouse_phone.getText().toString().trim();
        String orderNum = et_warehouse_num.getText().toString().trim();
        String company = et_warehouse_company.getText().toString().trim();
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(orderNum)) {
            WareHouseEntity entity = new WareHouseEntity(orderNum, phone, company);
            listItem.add(entity);
            shock();
            setAdapter(activity);
        }
    }

    /**
     * 手机震动
     */
    private void shock() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200l);
    }

    private void checkOrderNum(int what, String num, String company) {
        if (listItem.size() > 49) {
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