package com.tg.coloursteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dashuview.library.keep.Cqb_PayUtil;
import com.dashuview.library.keep.ListenerUtils;
import com.dashuview.library.keep.MyListener;
import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.EmployeePhoneAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.entity.ContactsEntity;
import com.tg.coloursteward.entity.EmployeeEntity;
import com.tg.coloursteward.info.EmployeePhoneInfo;
import com.tg.coloursteward.info.GridViewInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.fragment.ContactsFragment;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.util.AuthTimeUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.ManageMentLinearlayout;
import com.tg.coloursteward.view.ManageMentLinearlayout.NetworkRequestListener;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.chatsingle.IMConnectionActivity;
import com.youmai.hxsdk.db.bean.EmployeeBean;
import com.youmai.hxsdk.db.helper.CacheEmployeeHelper;
import com.youmai.hxsdk.router.APath;
import com.youmai.hxsdk.utils.GlideRoundTransform;

import org.json.JSONArray;
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
public class EmployeeDataActivity extends BaseActivity implements MyListener {
    public final static String CONTACTS_ID = "contacts_id";
    public final static String CONTACTS_CHECKED = "isChecked";
    private String contactsID, username;
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
    private ArrayList<GridViewInfo> gridlist1 = new ArrayList<GridViewInfo>();
    private ArrayList<EmployeePhoneInfo> PhoneList = new ArrayList<EmployeePhoneInfo>();
    private EmployeePhoneAdapter adapter;
    private AuthAppService authAppService;//2.0授权
    private String accessToken;
    private static String personCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_data);
        ListenerUtils.setCallBack(this);
        Intent intent = getIntent();
        if (intent != null) {
            contactsID = intent.getStringExtra(CONTACTS_ID);
        }
        if (contactsID == null) {
            ToastFactory.showToast(this, "参数错误");
            finish();
            return;
        }
        getKey();
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

    private void getKey() {
        String key = Tools.getStringValue(EmployeeDataActivity.this, Contants.EMPLOYEE_LOGIN.key);
        String secret = Tools.getStringValue(EmployeeDataActivity.this, Contants.EMPLOYEE_LOGIN.secret);
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(secret)) {
            getKeyAndSecret();
        }
    }

    private void getKeyAndSecret() {
        RequestConfig config = new RequestConfig(EmployeeDataActivity.this, HttpTools.GET_KEYSECERT, null);
        HttpTools.httpPost(Contants.URl.URL_CPMOBILE, "/1.0/auth", config, null);
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
        accessToken = Tools.getStringValue(EmployeeDataActivity.this, Contants.storage.APPAUTH);
        RequestConfig config = new RequestConfig(this, HttpTools.GET_EMPLOYEE_INFO, "获取详细信息");
        RequestParams params = new RequestParams();
        params.put("keyword", contactsID);
        params.put("username", contactsID);
        params.put("owner", UserInfo.employeeAccount);
        params.put("corpId", Tools.getStringValue(EmployeeDataActivity.this, Contants.storage.CORPID));
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/txl2/contacts/search", config, params);
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
        //ivHead.setCircleShape();
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
        /**
         * 邮件
         */
        magLinearLayout.setNetworkRequestListener(new NetworkRequestListener() {

            @Override
            public void onSuccess(ManageMentLinearlayout magLearLayout, Message msg,
                                  String response) {
                String jsonString = HttpTools.getContentString(response);
                if (jsonString != null) {
                    ResponseData app_list = HttpTools.getResponseKey(jsonString, "app_list");
                    if (app_list.length > 0) {
                        JSONArray jsonArray = app_list.getJSONArray(0, "list");
                        ResponseData data = HttpTools.getResponseKeyJSONArray(jsonArray);
                        gridlist1 = new ArrayList<GridViewInfo>();
                        GridViewInfo item = null;
                        for (int i = 0; i < data.length; i++) {
                            try {
                                item = new GridViewInfo();
                                item.name = data.getString(i, "name");
                                item.oauthType = data.getString(i, "oauthType");
                                item.developerCode = data.getString(i, "app_code");
                                item.clientCode = data.getString(i, "app_code");
                                item.sso = data.getString(i, "url");
                                JSONObject icon = data.getJSONObject(i, "icon");
                                if (icon != null || icon.length() > 0) {
                                    item.icon = icon.getString("android");
                                }
                                gridlist1.add(item);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }

            @Override
            public void onFail(ManageMentLinearlayout magLearLayout, Message msg, String hintString) {
//                ToastFactory.showToast(EmployeeDataActivity.this, hintString);
            }

            @Override
            public void onRequest(MessageHandler msgHand) {
                String pwd = Tools.getPassWord(EmployeeDataActivity.this);
                RequestConfig config = new RequestConfig(EmployeeDataActivity.this, 0);
                config.handler = msgHand.getHandler();
                RequestParams params = new RequestParams();
                params.put("user_name", UserInfo.employeeAccount);
                params.put("password", pwd);
                params.put("resource", "app");
                params.put("cate_id", 0);
//                HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newoa/rights/list", config, params);
            }
        });
    }

    //给动态广播发送信息
    public void sendToJava(String status) {
        Intent intent = new Intent(ContactsFragment.BROADCAST_INTENT_FILTER);
        intent.putExtra(ContactsFragment.ACTION, status);
        sendBroadcast(intent);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.GET_EMPLOYEE_INFO) {
            if (code == 0) {
                if (jsonString != null) {
                    EmployeeEntity employeeEntity = GsonUtils.gsonToBean(jsonString, EmployeeEntity.class);
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
                    try {
                        Glide.with(this)
                                .load(item.getAvatar())
                                .apply(new RequestOptions()
                                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                        .override(120, 120)
                                        .centerCrop()
                                        .transform(new GlideRoundTransform())
                                        .placeholder(R.drawable.default_header)
                                        .error(R.drawable.default_header))
                                .into(ivHead);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

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
                        /*Intent intent1 = new Intent(EmployeeDataActivity.this,Organization01Activity.class);
                        intent1.putExtra(Organization01Activity.TEXT_ID,UserInfo.propertyCoding);
						intent1.putExtra(Organization01Activity.TEXT_FAMILY, item.family);
						intent1.putExtra(Organization01Activity.TEXT_STRUCTURE,item.family);
						startActivity(intent1);*/
                        }
                    });

                    tvCornet.setText(item.getEnterprise_cornet());
                    tvSection.setText(item.getOrgName());
                    mlListView.addFooterView(footView);
                    adapter = new EmployeePhoneAdapter(this, PhoneList);
                    mlListView.setAdapter(adapter);
                }
            } else {
                ToastFactory.showToast(EmployeeDataActivity.this, message);
            }

        } else if (msg.arg1 == HttpTools.SET_EMPLOYEE_INFO) {
            if (code == 0) {
                ContactsEntity contactsEntity = GsonUtils.gsonToBean(jsonString, ContactsEntity.class);
                personCode = contactsEntity.getContent().getId();
                ToastFactory.showToast(EmployeeDataActivity.this, "添加收藏成功");
                sendToJava("insert");
            } else {
                ToastFactory.showToast(EmployeeDataActivity.this, hintString);
            }
        } else if (msg.arg1 == HttpTools.DELETE_EMPLOYEE_INFO) {
            if (code == 0) {
                ToastFactory.showToast(EmployeeDataActivity.this, "取消收藏成功");
                sendToJava("delete");
            } else {
                ToastFactory.showToast(EmployeeDataActivity.this, hintString);
            }
        } else if (msg.arg1 == HttpTools.GET_KEYSECERT) {
            if (code == 0) {
                try {
                    String contentString = HttpTools.getContentString(jsonString);
                    JSONObject sonJon = new JSONObject(contentString);
                    String key = sonJon.optString("key");
                    String secret = sonJon.optString("secret");
                    Tools.saveStringValue(EmployeeDataActivity.this, Contants.EMPLOYEE_LOGIN.key, key);
                    Tools.saveStringValue(EmployeeDataActivity.this, Contants.EMPLOYEE_LOGIN.secret, secret);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.ll_sendemail:// 发送邮件
                AuthTimeUtils mAuthTimeUtils = new AuthTimeUtils();
                mAuthTimeUtils.IsAuthTime(EmployeeDataActivity.this, Contants.Html5.YJ, "xyj", "1", "xyj", "");
                break;
            case R.id.ll_sendsms:// 发送短信
                if (null != item) {
                    Intent intent = new Intent(this, IMConnectionActivity.class);
                    intent.putExtra(IMConnectionActivity.DST_UUID, item.getUid());
                    intent.putExtra(IMConnectionActivity.DST_USERNAME, item.getUsername());
                    intent.putExtra(IMConnectionActivity.DST_NAME, item.getRealname());
                    intent.putExtra(IMConnectionActivity.DST_AVATAR, item.getAvatar());
                    //intent.putExtra(IMConnectionActivity.DST_PHONE, mobile);
                    startActivity(intent);
                }
                break;
            case R.id.ll_redpackets:// 转账
                if (null != item && !TextUtils.isEmpty(item.getUsername())) {
                    Cqb_PayUtil.getInstance(EmployeeDataActivity.this).VisitingCardTransfer(getPublicParams(),
                            getEnvironment(), item.getUsername(), "");

                }
                break;
        }
        return super.handClickEvent(v);
    }

    /**
     * 添加常用联系人
     */
    private void submit() {
        RequestConfig config = new RequestConfig(this, HttpTools.SET_EMPLOYEE_INFO, "添加常用联系人");
        RequestParams params = new RequestParams();
        params.put("name", item.getName());//联系人姓名
        params.put("uid", item.getUid());//联系人OA帐号
        params.put("username", contactsID);//联系人OA帐号
        params.put("owner", UserInfo.employeeAccount);//所有者OA帐号
        params.put("jobName", item.getJobName());
        params.put("sex", item.getSex());
        params.put("email", item.getEmail());
        params.put("qq", item.getQq());
        params.put("phone_number", item.getMobile());
        params.put("groupId", "0");//联系人组编号,默认0，常用联系人
        params.put("enterprise_cornet", item.getEnterprise_cornet());//企业短号
        HttpTools.httpPost(Contants.URl.URL_ICETEST, "/txl2/contacts", config, params);
    }

    /**
     * 删除常用联系人
     */
    private void delete() {
        RequestConfig config = new RequestConfig(this, HttpTools.DELETE_EMPLOYEE_INFO, "删除常用联系人");
        RequestParams params = new RequestParams();
        HttpTools.httpDelete(Contants.URl.URL_ICETEST, "/txl2/contacts/" + personCode, config, params);
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
}