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
import com.tg.coloursteward.view.ManageMentLinearlayout;
import com.tg.coloursteward.view.ManageMentLinearlayout.NetworkRequestListener;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.activity.IMConnectionActivity;
import com.youmai.hxsdk.utils.GlideRoundTransform;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.StringRes;
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

/**
 * 员工名片
 *
 * @author Administrator
 */
public class EmployeeDataActivity extends BaseActivity {
    public final static String CONTACTS_ID = "contacts_id";
    public final static String CONTACTS_CHECKED = "isChecked";

    private String contactsID;

    //private ManageMentLinearlayout magLinearLayout;
    private CheckBox cbCollect;
    //private LinearLayout llSendSms;
    private LinkManInfo item;
    private EmployeePhoneInfo info;
    private TextView tvName, tvJob, tvBranch;
    private ImageView ivHead;
    private ImageView ivClose;
    private ListView mlListView;
    private View footView;
    private ArrayList<GridViewInfo> gridlist1 = new ArrayList<GridViewInfo>();
    private ArrayList<EmployeePhoneInfo> mPhoneList = new ArrayList<EmployeePhoneInfo>();
    private EmployeePhoneAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_data2);
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
        //magLinearLayout.loaddingData();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        ivHead = (ImageView) findViewById(R.id.iv_head);
        //ivHead.setCircleShape();
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
                EmployeePhoneInfo info = mPhoneList.get(position);
                String tag = info.tag;
                if (tag.equals("phone")) {
                    String phone = mPhoneList.get(position).phone;
                    if (TextUtils.isEmpty(phone)) {
                        ToastFactory.showToast(EmployeeDataActivity.this, "暂无联系电话");
                        return;
                    }
                    Tools.call(EmployeeDataActivity.this, phone);
                } else if (tag.equals("ID")) {
                    Intent intent = new Intent();
                    intent.setClass(EmployeeDataActivity.this, IMConnectionActivity.class);
                    intent.putExtra(IMConnectionActivity.DST_UUID, item.uid);
                    intent.putExtra(IMConnectionActivity.DST_NAME, item.realname);
                    //intent.putExtra(IMConnectionActivity.DST_PHONE, mobile);
                    startActivity(intent);
                } else if (tag.equals("email")) {
                    AuthTimeUtils mAuthTimeUtils = new AuthTimeUtils();
                    mAuthTimeUtils.IsAuthTime(EmployeeDataActivity.this, Contants.Html5.YJ, "xyj", "1", "xyj", "");
                }
            }
        });

//        llSendSms = (LinearLayout) findViewById(R.id.ll_sendsms);
//        magLinearLayout = (ManageMentLinearlayout) findViewById(R.id.ll_sendemail);
//        llSendSms.setOnClickListener(singleListener);
//        magLinearLayout.setOnClickListener(singleListener);
        cbCollect = (CheckBox) findViewById(R.id.cb_collect);
//        magLinearLayout.setNetworkRequestListener(new NetworkRequestListener() {
//
//            @Override
//            public void onSuccess(ManageMentLinearlayout magLearLayout, Message msg,
//                                  String response) {
//                String jsonString = HttpTools.getContentString(response);
//                if (jsonString != null) {
//                    ResponseData app_list = HttpTools.getResponseKey(jsonString, "app_list");
//                    if (app_list.length > 0) {
//                        JSONArray jsonArray = app_list.getJSONArray(0, "list");
//                        ResponseData data = HttpTools.getResponseKeyJSONArray(jsonArray);
//                        gridlist1 = new ArrayList<GridViewInfo>();
//                        GridViewInfo item = null;
//                        for (int i = 0; i < data.length; i++) {
//                            try {
//                                item = new GridViewInfo();
//                                item.name = data.getString(i, "name");
//                                item.oauthType = data.getString(i, "oauthType");
//                                item.developerCode = data.getString(i, "app_code");
//                                item.clientCode = data.getString(i, "app_code");
//                                item.sso = data.getString(i, "url");
//                                JSONObject icon = data.getJSONObject(i, "icon");
//                                if (icon != null || icon.length() > 0) {
//                                    item.icon = icon.getString("android");
//                                }
//                                gridlist1.add(item);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//
//            }
//
//            @Override
//            public void onFail(ManageMentLinearlayout magLearLayout, Message msg, String hintString) {
//                ToastFactory.showToast(EmployeeDataActivity.this, hintString);
//            }
//
//            @Override
//            public void onRequest(MessageHandler msgHand) {
//                String pwd = Tools.getPassWord(EmployeeDataActivity.this);
//                RequestConfig config = new RequestConfig(EmployeeDataActivity.this, 0);
//                config.handler = msgHand.getHandler();
//                RequestParams params = new RequestParams();
//                params.put("user_name", UserInfo.employeeAccount);
//                params.put("password", pwd);
//                params.put("resource", "app");
//                params.put("cate_id", 0);
//                HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newoa/rights/list", config, params);
//            }
//        });
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        if (msg.arg1 == HttpTools.GET_EMPLOYEE_INFO) {
            String response = HttpTools.getContentString(jsonString);
            if (jsonString != null) {
                ResponseData data = HttpTools.getResponseContentObject(response);
                item = new LinkManInfo();
                item.id = data.getString("id");
                item.iscontacts = data.getInt("isFavorite");
                item.username = data.getString("uid");
                item.username = data.getString("username");
                item.realname = data.getString("realname");
                item.icon = data.getString("avatar");
                item.sex = data.getString("sex");
                item.email = data.getString("email");
                item.phone = data.getString("mobile");
                item.job_name = data.getString("jobName");
                item.orgName = data.getString("orgName");
                item.uid = data.getString("uid");
            }

            if (item != null) {
                tvName.setText(item.realname /*+ "(" + item.username + ")"*/);
                if (item.job_name.contains("(")) {
                    int i = item.job_name.indexOf("(");
                    item.job_name = item.job_name.substring(0, i);
                }
                tvJob.setText(item.job_name);
                tvBranch.setText(item.orgName);
                try {
                    Glide.with(this)
                            .load(item.icon)
                            .apply(new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .override(120, 120)
                                    .centerCrop()
                                    .transform(new GlideRoundTransform(this))
                                    .placeholder(R.drawable.default_header)
                                    .error(R.drawable.default_header))
                            .into(ivHead);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //VolleyUtils.getImage(this, item.icon, ivHead, R.drawable.moren_geren);
                if (item.iscontacts == 1) {
                    cbCollect.setChecked(true);
                } else if (item.iscontacts > 1) {
                    cbCollect.setChecked(false);
                }
                cbCollect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked == true) {
                            submit();//添加联系人
                        } else if (isChecked == false) {
                            delete();
                        }
                    }
                });

                //添加按钮
                info = new EmployeePhoneInfo();
                info.phone = item.id;
                info.tag = "ID";
                mPhoneList.add(info);

                if (mlListView.getFooterViewsCount() > 0) {
                    mlListView.removeFooterView(footView);
                }
                if (item.phone != null) {
                    String[] str = item.phone.split("，");
                    for (int i = 0; i < str.length; i++) {
                        info = new EmployeePhoneInfo();
                        info.phone = str[i];
                        info.tag = "phone";
                        mPhoneList.add(info);
                    }
                }

                if (!StringUtils.isEmpty(item.email)) {
                    info = new EmployeePhoneInfo();
                    info.phone = item.email;
                    info.tag = "email";
                    mPhoneList.add(info);
                }


                if (footView == null) {
                    footView = getLayoutInflater().inflate(R.layout.employee_foot_tip, null);
                }
//                TextView tvCornet = (TextView) footView.findViewById(R.id.tv_cornet);
//                TextView tvSection = (TextView) footView.findViewById(R.id.tv_section);
                //RelativeLayout rlEnterpriseCornet = (RelativeLayout) footView.findViewById(R.id.rl_enterprise_cornet);
                //RelativeLayout rlSection = (RelativeLayout) footView.findViewById(R.id.rl_section);
//                rlEnterpriseCornet.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (TextUtils.isEmpty(item.enterprise_cornet)) {
//                            ToastFactory.showToast(EmployeeDataActivity.this, "暂无联系电话");
//                            return;
//                        }
//                        Tools.call(EmployeeDataActivity.this, item.enterprise_cornet);
//                    }
//                });
//
//                rlSection.setOnClickListener(new OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        /*Intent intent1 = new Intent(EmployeeDataActivity.this,Organization01Activity.class);
//                        intent1.putExtra(Organization01Activity.TEXT_ID,UserInfo.propertyCoding);
//						intent1.putExtra(Organization01Activity.TEXT_FAMILY, item.family);
//						intent1.putExtra(Organization01Activity.TEXT_STRUCTURE,item.family);
//						startActivity(intent1);*/
//                    }
//                });
//
//                tvCornet.setText(item.enterprise_cornet);
//                tvSection.setText(item.orgName);
                mlListView.addFooterView(footView);
                adapter = new EmployeePhoneAdapter(this, mPhoneList);
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
        }
    }

//    @Override
//    protected boolean handClickEvent(View v) {
//        String url = null;
//        String oauthType = null;
//        String developerCode = null;
//        String clientCode = null;
//        switch (v.getId()) {
//            case R.id.ll_sendemail:// 发送邮件
//			/*if(gridlist1.size() > 0 ){
//				for (int i = 0; i < gridlist1.size(); i++) {
//					if(gridlist1.get(i).name.equals("新邮件")){
//						url = gridlist1.get(i).sso;
//						oauthType = gridlist1.get(i).oauthType;
//						developerCode = gridlist1.get(i).developerCode;
//						clientCode = gridlist1.get(i).clientCode;
//						break;
//					}
//				}
//				if(url != null && url.length() != 0){
//					AuthTimeUtils mAuthTimeUtils  = new AuthTimeUtils();
//					mAuthTimeUtils.IsAuthTime(EmployeeDataActivity.this,url,developerCode,oauthType, clientCode,"");
//				}
//			}*/
//                AuthTimeUtils mAuthTimeUtils = new AuthTimeUtils();
//                mAuthTimeUtils.IsAuthTime(EmployeeDataActivity.this, Contants.Html5.YJ, "xyj", "1", "xyj", "");
//                break;
//            case R.id.ll_sendsms:// 发送短信
//                String mobile = mPhoneList.get(0).phone;
//                if (Tools.checkTelephoneNumber(mobile)) {
//                    Intent intent = new Intent();
//
//                    //intent.setAction(Intent.ACTION_SENDTO);
//                    //intent.setData(Uri.parse("smsto:" + mobiles));
//
//                    intent.setClass(this, IMConnectionActivity.class);
//                    intent.putExtra(IMConnectionActivity.DST_UUID, item.uid);
//                    intent.putExtra(IMConnectionActivity.DST_NAME, item.realname);
//                    intent.putExtra(IMConnectionActivity.DST_PHONE, mobile);
//                    startActivity(intent);
//
//                } else {
//                    ToastFactory.showToast(EmployeeDataActivity.this, "手机号有误，无法发送");
//                }
//                break;
//        }
//        return super.handClickEvent(v);
//    }

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
