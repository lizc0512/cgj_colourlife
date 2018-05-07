package com.tg.coloursteward;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tg.coloursteward.adapter.EmployeePhoneAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.EmployeePhoneInfo;
import com.tg.coloursteward.info.GridViewInfo;
import com.tg.coloursteward.info.LinkManInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.net.image.VolleyUtils;
import com.tg.coloursteward.util.AuthTimeUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.CircularImageView;
import com.tg.coloursteward.view.ManageMentLinearlayout;
import com.tg.coloursteward.view.ManageMentLinearlayout.NetworkRequestListener;
import com.tg.coloursteward.view.dialog.PwdDialog2;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.activity.IMConnectionActivity;
import com.youmai.hxsdk.db.bean.EmployeeBean;
import com.youmai.hxsdk.db.helper.CacheEmployeeHelper;
import com.youmai.hxsdk.utils.GlideRoundTransform;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
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

/**
 * 员工名片
 *
 * @author Administrator
 */
public class EmployeeDataActivity extends BaseActivity {
    public final static String CONTACTS_ID = "contacts_id";
    public final static String CONTACTS_CHECKED = "isChecked";
    private String contactsID;
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
    private double balance = 0.00d;
    private Intent intent;
    private PwdDialog2 aDialog;
    private PwdDialog2.ADialogCallback aDialogCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_data);
        Intent intent = getIntent();
        if (intent != null) {
            contactsID = intent.getStringExtra(CONTACTS_ID);
        }
        if (contactsID == null) {
            ToastFactory.showToast(this, "参数错误");
            finish();
            return;
        }
        RequestConfig config = new RequestConfig(this, HttpTools.GET_EMPLOYEE_INFO, "获取详细信息");
        RequestParams params = new RequestParams();
        params.put("uid", UserInfo.employeeAccount);
        params.put("contactsID", contactsID);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/phonebook/contacts", config, params);
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
        /**
         * 获取饭票
         */
        String jsonStr = Tools.getStringValue(EmployeeDataActivity.this, Contants.storage.TICKET);//饭票页面缓存
        String TicketStr = Tools.getStringValue(EmployeeDataActivity.this, Contants.storage.TICKETHOME);//首页缓存
        if (StringUtils.isNotEmpty(TicketStr)) {//首页
            balance = Double.parseDouble(TicketStr);
        } else if (StringUtils.isNotEmpty(jsonStr)) {//饭票页面
            JSONObject jsonObject = HttpTools.getContentJSONObject(jsonStr);
            if (jsonObject != null) {
                try {
                    balance = jsonObject.getDouble("balance");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
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
                ToastFactory.showToast(EmployeeDataActivity.this, hintString);
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
                HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newoa/rights/list", config, params);
            }
        });
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        if (msg.arg1 == HttpTools.GET_EMPLOYEE_INFO) {
            String response = HttpTools.getContentString(jsonString);
            if (jsonString != null) {
                ResponseData data = HttpTools.getResponseContentObject(response);
                item = new EmployeeBean();
                item.setIsFavorite(data.getString("isFavorite"));
                item.setUid(data.getString("uid"));
                item.setUsername(data.getString("username"));
                item.setRealname(data.getString("realname"));
                item.setAvatar(data.getString("avatar"));
                item.setSex(data.getString("sex"));
                item.setEmail(data.getString("email"));
                item.setMobile(data.getString("mobile"));
                item.setJobName(data.getString("jobName"));
                item.setOrgName(data.getString("orgName"));
                item.setLandline(data.getString("landline"));
            }

            if (item != null) {
                CacheEmployeeHelper.instance().insertOrUpdate(this, item);

                tvName.setText(item.getRealname());
                if (item.getJobName().contains("(")) {
                    int i = item.getJobName().indexOf("(");
                    item.setJobName(item.getJobName().substring(0, i));
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
                //VolleyUtils.getImage(this, item.icon, ivHead, R.drawable.moren_geren);
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
        } else if (msg.arg1 == HttpTools.SET_EMPLOYEE_INFO) {
            if (code == 0) {
                ToastFactory.showToast(EmployeeDataActivity.this, "添加收藏成功");
            }
        } else if (msg.arg1 == HttpTools.DELETE_EMPLOYEE_INFO) {
            if (code == 0) {
                ToastFactory.showToast(EmployeeDataActivity.this, "取消收藏成功");
            }
        } else if (msg.arg1 == HttpTools.POST_SETPWD_INFO) {//判断有无密码
            if (code == 0) {
                JSONObject content = HttpTools.getContentJSONObject(jsonString);
                if (content != null) {
                    String state;
                    try {
                        state = content.getString("state");
                        switch (0) {
                            case 0:// 给同事发饭票
                                if (state != null) {
                                    if ("hasPwd".equals(state)) { // 已设置密码
                                        intent = new Intent(EmployeeDataActivity.this, RedpacketsShareMainActivity.class);
                                        intent.putExtra(Contants.PARAMETER.BALANCE, balance);
                                        intent.putExtra(Contants.PARAMETER.OA, item.getUsername());
                                        intent.putExtra(Contants.PARAMETER.TRANSFERTO, "colleague");
                                        startActivity(intent);
                                    } else {
                                        aDialogCallback = new PwdDialog2.ADialogCallback() {
                                            @Override
                                            public void callback() {
                                                intent = new Intent(EmployeeDataActivity.this, RedpacketsShareMainActivity.class);
                                                intent.putExtra(Contants.PARAMETER.BALANCE, balance);
                                                intent.putExtra(Contants.PARAMETER.OA, item.getUsername());
                                                intent.putExtra(Contants.PARAMETER.TRANSFERTO, "colleague");
                                                startActivity(intent);
                                            }
                                        };
                                        aDialog = new PwdDialog2(
                                                EmployeeDataActivity.this,
                                                R.style.choice_dialog, state,
                                                aDialogCallback);
                                        aDialog.show();
                                    }
                                } else {
                                    //	ToastFactory.showToast(RedpacketsMainActivity.this, "网络异常");
                                }
                                break;
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected boolean handClickEvent(View v) {
        String url = null;
        String oauthType = null;
        String developerCode = null;
        String clientCode = null;
        switch (v.getId()) {
            case R.id.ll_sendemail:// 发送邮件
			/*if(gridlist1.size() > 0 ){
				for (int i = 0; i < gridlist1.size(); i++) {
					if(gridlist1.get(i).name.equals("新邮件")){
						url = gridlist1.get(i).sso;
						oauthType = gridlist1.get(i).oauthType;
						developerCode = gridlist1.get(i).developerCode;
						clientCode = gridlist1.get(i).clientCode;
						break;
					}
				}
				if(url != null && url.length() != 0){
					AuthTimeUtils mAuthTimeUtils  = new AuthTimeUtils();
					mAuthTimeUtils.IsAuthTime(EmployeeDataActivity.this,url,developerCode,oauthType, clientCode,"");
				}
			}*/
                AuthTimeUtils mAuthTimeUtils = new AuthTimeUtils();
                mAuthTimeUtils.IsAuthTime(EmployeeDataActivity.this, Contants.Html5.YJ, "xyj", "1", "xyj", "");
                break;
            case R.id.ll_sendsms:// 发送短信
                Intent intent = new Intent(this, IMConnectionActivity.class);
                intent.putExtra(IMConnectionActivity.DST_UUID, item.getUid());
                intent.putExtra(IMConnectionActivity.DST_USERNAME, item.getUsername());
                intent.putExtra(IMConnectionActivity.DST_NAME, item.getRealname());
                intent.putExtra(IMConnectionActivity.DST_AVATAR, item.getAvatar());
                //intent.putExtra(IMConnectionActivity.DST_PHONE, mobile);
                startActivity(intent);

                break;
            case R.id.ll_redpackets:// 转账
                if (balance <= 0) {
                    ToastFactory.showToast(EmployeeDataActivity.this, "饭票余额不足，不能发饭票");
                } else {
                    // 判断有无密码及卡
                    isSetPwd(0);
                }
                break;
        }
        return super.handClickEvent(v);
    }

    /**
     * 点击事件判断有误密码以卡
     *
     * @param position
     */
    private void isSetPwd(int position) {
        String key = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.key);
        String secret = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.secret);
        RequestConfig config = new RequestConfig(this, HttpTools.POST_SETPWD_INFO);
        RequestParams params = new RequestParams();
        params.put("position", position);
        params.put("key", key);
        params.put("secret", secret);
        HttpTools.httpPost(Contants.URl.URL_ICETEST, "/hongbao/isSetPwd", config, params);
    }

    /**
     * 添加常用联系人
     */
    private void submit() {
        RequestConfig config = new RequestConfig(this, HttpTools.SET_EMPLOYEE_INFO, "添加常用联系人");
        RequestParams params = new RequestParams();
        params.put("uid", UserInfo.employeeAccount);
        params.put("contactsID", contactsID);
        HttpTools.httpPost(Contants.URl.URL_ICETEST, "/phonebook/favoriteContacts", config, params);
    }

    /**
     * 删除常用联系人
     */
    private void delete() {
        RequestConfig config = new RequestConfig(this, HttpTools.DELETE_EMPLOYEE_INFO, "删除常用联系人");
        RequestParams params = new RequestParams();
        params.put("uid", UserInfo.employeeAccount);
        params.put("contactsID", contactsID);
        HttpTools.httpDelete(Contants.URl.URL_ICETEST, "/phonebook/favoriteContacts", config, params);
    }

    @Override
    public View getContentView() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getHeadTitle() {
        // TODO Auto-generated method stub
        return null;
    }
}
