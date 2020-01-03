package com.tg.coloursteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.dashuview.library.keep.Cqb_PayUtil;
import com.dashuview.library.keep.ListenerUtils;
import com.dashuview.library.keep.MyListener;
import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.EmployeePhoneAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.entity.ContactsEntity;
import com.tg.coloursteward.entity.EmployeeEntity;
import com.tg.coloursteward.fragment.ContactsFragment;
import com.tg.coloursteward.info.EmployeePhoneInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.model.ContactModel;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.MicroAuthTimeUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.ManageMentLinearlayout;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.tg.point.activity.GivenPointAmountActivity;
import com.tg.point.activity.MyPointActivity;
import com.youmai.hxsdk.chatsingle.IMConnectionActivity;
import com.youmai.hxsdk.db.bean.EmployeeBean;
import com.youmai.hxsdk.db.helper.CacheEmployeeHelper;
import com.youmai.hxsdk.router.APath;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import static com.tg.coloursteward.module.MainActivity.getEnvironment;
import static com.tg.coloursteward.module.MainActivity.getPublicParams;

/**
 * 员工名片
 *
 * @author Administrator
 */
@Route(path = APath.EMPLOYEE_DATA_ACT)
public class EmployeeDataActivity extends BaseActivity implements MyListener, HttpResponse {
    public final static String CONTACTS_ID = "contacts_id";
    private String contactsID = "";
    private ManageMentLinearlayout magLinearLayout;
    private ManageMentLinearlayout llRedpackets;
    private CheckBox cbCollect;
    private LinearLayout llSendSms;
    private EmployeeBean item;
    private EmployeePhoneInfo info;
    private TextView tvName, tvJob, tvBranch;
    private ImageView ivHead;
    private ImageView ivSex;
    private ImageView ivClose;
    private ListView mlListView;
    private View footView;
    private ArrayList<EmployeePhoneInfo> PhoneList = new ArrayList<EmployeePhoneInfo>();
    private EmployeePhoneAdapter adapter;
    private AuthAppService authAppService;//2.0授权
    private String accessToken;
    private static String personCode;
    private ContactModel contactModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_data);
        contactModel = new ContactModel(this);
        ListenerUtils.setCallBack(this);
        Intent intent = getIntent();
        if (intent != null) {
            contactsID = intent.getStringExtra(CONTACTS_ID);
        }
        String expireTime = Tools.getStringValue(EmployeeDataActivity.this, Contants.storage.APPAUTHTIME);
        Date dt = new Date();
        Long time = dt.getTime();
        if (StringUtils.isNotEmpty(expireTime)) {
            if (Long.parseLong(expireTime) * 1000 <= time) {//token过期
                getAuthAppInfo();
            } else {
                getData();
            }
        } else {
            getAuthAppInfo();
        }
        initView();
        requestData();//加载数据

    }

    /**
     * 加载数据
     */
    private void requestData() {
        magLinearLayout.loaddingData();
    }

    /**
     * 加载名片
     */
    private void getData() {
        contactModel.getEmployeeData(0, contactsID, contactsID, UserInfo.employeeAccount,
                Tools.getStringValue(EmployeeDataActivity.this, Contants.storage.CORPID), this);
        accessToken = Tools.getStringValue(EmployeeDataActivity.this, Contants.storage.APPAUTH);
    }

    /**
     * 获取token(2.0)
     * sectet
     */
    private void getAuthAppInfo() {
        if (authAppService == null) {
            authAppService = new AuthAppService(EmployeeDataActivity.this);
        }
        authAppService.getAppAuth(new GetTwoRecordListener<String, String>() {
            @Override
            public void onFinish(String jsonString, String data2, String data3) {
                int code = HttpTools.getCode(jsonString);
                if (code == 0) {
                    JSONObject content = HttpTools.getContentJSONObject(jsonString);
                    if (content.length() > 0) {
                        try {
                            accessToken = content.getString("accessToken");
                            String expireTime = content.getString("expireTime");
                            Tools.saveStringValue(EmployeeDataActivity.this, Contants.storage.APPAUTH, accessToken);
                            Tools.saveStringValue(EmployeeDataActivity.this, Contants.storage.APPAUTHTIME, expireTime);
                            getData();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailed(String Message) {
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        ivHead = (ImageView) findViewById(R.id.iv_head);
        ivSex = (ImageView) findViewById(R.id.iv_sex);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvJob = (TextView) findViewById(R.id.tv_job);
        tvBranch = (TextView) findViewById(R.id.tv_branch);
        mlListView = (ListView) findViewById(R.id.lv_employee_phone);
        ivClose = (ImageView) findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mlListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone = PhoneList.get(position).phone;
                if (TextUtils.isEmpty(phone)) {
                    ToastFactory.showToast(EmployeeDataActivity.this, "暂无联系电话");
                    return;
                }
                Tools.call(EmployeeDataActivity.this, phone);
            }
        });

        llSendSms = (LinearLayout) findViewById(R.id.ll_sendsms);
        magLinearLayout = (ManageMentLinearlayout) findViewById(R.id.ll_sendemail);
        llRedpackets = (ManageMentLinearlayout) findViewById(R.id.ll_redpackets);
        llSendSms.setOnClickListener(singleListener);
        magLinearLayout.setOnClickListener(singleListener);
        llRedpackets.setOnClickListener(singleListener);
        cbCollect = (CheckBox) findViewById(R.id.cb_collect);
    }

    //给动态广播发送信息
    public void sendToJava(String status) {
        Intent intent = new Intent(ContactsFragment.BROADCAST_INTENT_FILTER);
        intent.putExtra(ContactsFragment.ACTION, status);
        sendBroadcast(intent);
    }

    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.ll_sendemail:// 发送邮件
                MicroAuthTimeUtils microAuthTimeUtils = new MicroAuthTimeUtils();
                microAuthTimeUtils.IsAuthTime(this, Contants.Html5.YJ, "2", "");
                break;
            case R.id.ll_sendsms:// 发送短信
                if (null != item) {
                    Intent intent = new Intent(this, IMConnectionActivity.class);
                    intent.putExtra(IMConnectionActivity.DST_UUID, item.getUid());
                    intent.putExtra(IMConnectionActivity.DST_USERNAME, item.getUsername());
                    intent.putExtra(IMConnectionActivity.DST_NAME, item.getRealname());
                    intent.putExtra(IMConnectionActivity.DST_AVATAR, item.getAvatar());
                    startActivity(intent);
                }
                break;
            case R.id.ll_redpackets:// 转账
                if (null != item && !TextUtils.isEmpty(item.getUsername())) {
                    Intent intent = new Intent(this, MyPointActivity.class);
                    intent.putExtra(GivenPointAmountActivity.TYPE, "cgj-cgj");
                    intent.putExtra(GivenPointAmountActivity.GIVENMOBILE, item.getMobile());
                    startActivity(intent);
                }
                break;
        }
        return super.handClickEvent(v);
    }

    /**
     * 添加常用联系人
     */
    private void submit() {
        contactModel.postCollectData(1, contactsID, item, this);
    }

    /**
     * 删除常用联系人
     */
    private void delete() {
        contactModel.delCollectData(2, personCode, this);
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
    public void authenticationFeedback(String s, int i) {
        switch (i) {
            case 16://密码校验成功
                break;
            case 17://密码检验时主动中途退出
                ToastFactory.showToast(EmployeeDataActivity.this, "已取消");
                break;
            case 18://没有设置支付密码
                ToastFactory.showToast(EmployeeDataActivity.this, "未设置支付密码，即将跳转到彩钱包页面");
                Cqb_PayUtil.getInstance(this).createPay(getPublicParams(), getEnvironment());
                break;
            case 19://绑定银行卡并设置密码成功
                break;
            case 20://名片赠送成功
//                ToastFactory.showToast(EmployeeDataActivity.this,"转账成功");
                break;
        }
    }

    @Override
    public void toCFRS(String s) {

    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    EmployeeEntity employeeEntity = GsonUtils.gsonToBean(result, EmployeeEntity.class);
                    if (employeeEntity.getContent() != null && employeeEntity.getContent().size() > 0) {
                        item = new EmployeeBean();
                        item.setIsFavorite(String.valueOf(employeeEntity.getContent().get(0).getIsFavorite()));
                        item.setUid(employeeEntity.getContent().get(0).getAccountUuid());
                        item.setUsername(employeeEntity.getContent().get(0).getUsername());
                        item.setRealname(employeeEntity.getContent().get(0).getName());
                        item.setAvatar("http://avatar.ice.colourlife.com/avatar?uid=" + employeeEntity.getContent().get(0).getUsername());
                        if (employeeEntity.getContent().get(0).getSex().equals("1") || employeeEntity.getContent().get(0).getSex().equals("男")) {
                            item.setSex("男");// 1男2女,
                        } else {
                            item.setSex("女");// 1男2女,
                        }
                        item.setEmail(employeeEntity.getContent().get(0).getEmail());
                        item.setMobile(employeeEntity.getContent().get(0).getMobile());
                        item.setJobName(employeeEntity.getContent().get(0).getJobType());
                        item.setOrgName(employeeEntity.getContent().get(0).getOrgName());
                        item.setLandline(employeeEntity.getContent().get(0).getLandline());
                        item.setName(employeeEntity.getContent().get(0).getName());
                        item.setFavoriteid(String.valueOf(employeeEntity.getContent().get(0).getFavoriteid()));
                        personCode = item.getFavoriteid();
                    }
                }

                if (item != null) {
                    CacheEmployeeHelper.instance().insertOrUpdate(this, item);

                    tvName.setText(item.getRealname());
                    try {
                        if (item.getJobName().contains("(")) {
                            int i = item.getJobName().indexOf("(");
                            item.setJobName(item.getJobName().substring(0, i));
                        }
                    } catch (Exception e) {
                    }
                    tvJob.setText(item.getJobName());
                    tvBranch.setText(item.getOrgName());
                    GlideUtils.loadImageDefaultDisplay(this, item.getAvatar(), ivHead, R.drawable.default_header, R.drawable.default_header);
                    if (item.getSex().equals("女")) {
                        ivSex.setImageResource(R.drawable.employee_female);
                    } else {
                        ivSex.setImageResource(R.drawable.employee_male);
                    }
                    if (item.getIsFavorite().equals("1")) {
                        cbCollect.setChecked(true);
                    } else {
                        cbCollect.setChecked(false);
                    }
                    cbCollect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                submit();//添加联系人
                            } else {
                                delete();
                            }
                        }
                    });

                    if (mlListView.getFooterViewsCount() > 0) {
                        mlListView.removeFooterView(footView);
                    }
                    if (item.getMobile() != null) {
                        String[] str = item.getMobile().split("，");
                        for (int i = 0; i < str.length; i++) {
                            info = new EmployeePhoneInfo();
                            info.phone = str[i];
                            PhoneList.add(info);
                        }
                    }
                    if (footView == null) {
                        footView = getLayoutInflater().inflate(R.layout.employee_foot, null);
                    }
                    TextView tvCornet = (TextView) footView.findViewById(R.id.tv_cornet);
                    TextView tvSection = (TextView) footView.findViewById(R.id.tv_section);
                    RelativeLayout rlEnterpriseCornet = (RelativeLayout) footView.findViewById(R.id.rl_enterprise_cornet);
                    RelativeLayout rlSection = (RelativeLayout) footView.findViewById(R.id.rl_section);
                    rlEnterpriseCornet.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (TextUtils.isEmpty(item.getEnterprise_cornet())) {
                                ToastFactory.showToast(EmployeeDataActivity.this, "暂无联系电话");
                                return;
                            }
                            Tools.call(EmployeeDataActivity.this, item.getEnterprise_cornet());
                        }
                    });

                    rlSection.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                        }
                    });

                    tvCornet.setText(item.getEnterprise_cornet());
                    tvSection.setText(item.getOrgName());
                    mlListView.addFooterView(footView);
                    adapter = new EmployeePhoneAdapter(this, PhoneList);
                    mlListView.setAdapter(adapter);
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    ContactsEntity contactsEntity = GsonUtils.gsonToBean(result, ContactsEntity.class);
                    personCode = contactsEntity.getContent().getId();
                    ToastFactory.showToast(EmployeeDataActivity.this, "添加收藏成功");
                    sendToJava("insert");
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(result)) {
                    ToastFactory.showToast(EmployeeDataActivity.this, "取消收藏成功");
                    sendToJava("delete");
                }
                break;
        }
    }
}
