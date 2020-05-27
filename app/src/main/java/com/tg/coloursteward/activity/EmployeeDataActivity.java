package com.tg.coloursteward.activity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.entity.ContactsEntity;
import com.tg.coloursteward.entity.EmployeeEntity;
import com.tg.coloursteward.fragment.ContactsFragment;
import com.tg.coloursteward.model.ContactModel;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.MicroAuthTimeUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.tg.point.activity.GivenPointAmountActivity;
import com.tg.point.activity.MyPointActivity;
import com.youmai.hxsdk.chatsingle.IMConnectionActivity;
import com.youmai.hxsdk.db.bean.EmployeeBean;
import com.youmai.hxsdk.db.helper.CacheEmployeeHelper;
import com.youmai.hxsdk.router.APath;

import java.util.List;

/**
 * 员工名片
 *
 * @author Administrator
 */
@Route(path = APath.EMPLOYEE_DATA_ACT)
public class EmployeeDataActivity extends BaseActivity implements HttpResponse, OnClickListener {
    public final static String CONTACTS_ID = "contacts_id";
    private String contactsID = "";
    private RelativeLayout rl_employee_email;
    private RelativeLayout rl_employee_money;
    private CheckBox cbCollect;
    private RelativeLayout rl_employee_msg;
    private RelativeLayout rl_employee_call;
    private EmployeeBean item;
    private TextView tvName, tvJob, tvBranch;
    private ImageView ivHead;
    private ImageView ivSex;
    private ImageView ivClose;
    private TextView tv_employee_phone;
    private TextView tv_employee_depart;
    private TextView tv_employee_jobname;
    private TextView tv_employee_phonecopy;
    private TextView tv_employee_email;
    private TextView tv_employee_emailcopy;
    private TextView tv_employee_company;
    private static String personCode;
    private ContactModel contactModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_data);
        contactModel = new ContactModel(this);
        Intent intent = getIntent();
        if (intent != null) {
            contactsID = intent.getStringExtra(CONTACTS_ID);
        }
        initView();
        getData();

    }

    /**
     * 加载名片
     */
    private void getData() {
        String corpId = spUtils.getStringData(SpConstants.storage.CORPID, "");
        contactModel.getEmployeeData(0, contactsID, contactsID, corpId, this);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        ivHead = findViewById(R.id.view_employee_head);
        ivSex = findViewById(R.id.iv_employee_sex);
        tvName = findViewById(R.id.tv_employee_name);
        tvJob = findViewById(R.id.tv_employee_job);
        tvBranch = findViewById(R.id.tv_employee_department);
        tv_employee_phone = findViewById(R.id.tv_employee_phone);
        tv_employee_depart = findViewById(R.id.tv_employee_depart);
        ivClose = findViewById(R.id.iv_close);
        rl_employee_msg = findViewById(R.id.rl_employee_msg);
        rl_employee_call = findViewById(R.id.rl_employee_call);
        rl_employee_email = findViewById(R.id.rl_employee_email);
        rl_employee_money = findViewById(R.id.rl_employee_money);
        tv_employee_phonecopy = findViewById(R.id.tv_employee_phonecopy);
        tv_employee_email = findViewById(R.id.tv_employee_email);
        tv_employee_emailcopy = findViewById(R.id.tv_employee_emailcopy);
        tv_employee_jobname = findViewById(R.id.tv_employee_jobname);
        tv_employee_company = findViewById(R.id.tv_employee_company);
        rl_employee_msg.setOnClickListener(this);
        rl_employee_call.setOnClickListener(this);
        cbCollect = findViewById(R.id.cb_collect);
        rl_employee_email.setOnClickListener(this);
        rl_employee_money.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        tv_employee_phonecopy.setOnClickListener(this);
        tv_employee_emailcopy.setOnClickListener(this);
        String corpName = spUtils.getStringData(SpConstants.storage.CORPNAME, "");
        tv_employee_company.setText(corpName);
        String corpId = spUtils.getStringData(SpConstants.storage.CORPID, "");
        if (Contants.APP.CORP_UUID.equals(corpId)) {
            cbCollect.setVisibility(View.VISIBLE);
        } else {
            cbCollect.setVisibility(View.GONE);
        }
    }

    //给动态广播发送信息
    public void sendToJava(String status) {
        Intent intent = new Intent(ContactsFragment.BROADCAST_INTENT_FILTER);
        intent.putExtra(ContactsFragment.ACTION, status);
        sendBroadcast(intent);
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
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    EmployeeEntity employeeEntity = GsonUtils.gsonToBean(result, EmployeeEntity.class);
                    if (employeeEntity.getContent() != null && employeeEntity.getContent().size() > 0) {
                        item = new EmployeeBean();
                        if (!TextUtils.isEmpty(String.valueOf(employeeEntity.getContent().get(0).getIsFavorite()))) {
                            item.setIsFavorite(String.valueOf(employeeEntity.getContent().get(0).getIsFavorite()));
                        }
                        item.setUid(employeeEntity.getContent().get(0).getId());
                        item.setUsername(employeeEntity.getContent().get(0).getUsername());
                        item.setRealname(employeeEntity.getContent().get(0).getName());
                        item.setAvatar(employeeEntity.getContent().get(0).getAvatar());
                        if (employeeEntity.getContent().get(0).getSex().equals("1")) {
                            item.setSex("男");// 1男2,0女,
                        } else {
                            item.setSex("女");// 1男2,0女,
                        }
                        item.setEmail(employeeEntity.getContent().get(0).getEmail());
                        item.setMobile(employeeEntity.getContent().get(0).getMobile());
                        item.setJobName(employeeEntity.getContent().get(0).getJobName());
                        item.setOrgName(employeeEntity.getContent().get(0).getOrgName());
                        item.setName(employeeEntity.getContent().get(0).getName());
                        if (!TextUtils.isEmpty(String.valueOf(employeeEntity.getContent().get(0).getFavoriteid()))) {
                            item.setFavoriteid(String.valueOf(employeeEntity.getContent().get(0).getFavoriteid()));
                            personCode = item.getFavoriteid();
                        }
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
                    tv_employee_jobname.setText(item.getJobName());
                    tvBranch.setText(item.getOrgName());
                    tv_employee_depart.setText(item.getOrgName());
                    tv_employee_phone.setText(item.getMobile());
                    tv_employee_email.setText(item.getEmail());
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_employee_phonecopy:
                copyText("手机号已复制", tv_employee_phone.getText().toString());
                break;
            case R.id.tv_employee_emailcopy:
                copyText("邮箱已复制", tv_employee_email.getText().toString());
                break;
            case R.id.rl_employee_email:// 发送邮件
                MicroAuthTimeUtils microAuthTimeUtils = new MicroAuthTimeUtils();
                microAuthTimeUtils.IsAuthTime(this, Contants.Html5.YJ, "2", "");
                break;
            case R.id.rl_employee_call:
                XXPermissions.with(this).permission(Manifest.permission.CALL_PHONE)
                        .constantRequest()
                        .request(new OnPermission() {
                            @Override
                            public void hasPermission(List<String> granted, boolean isAll) {
                                if (!TextUtils.isEmpty(item.getMobile())) {
                                    Intent it = new Intent(Intent.ACTION_CALL);
                                    Uri data = Uri.parse("tel:" + item.getMobile());
                                    it.setData(data);
                                    startActivity(it);
                                }
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean quick) {
                                ToastUtil.showShortToast(EmployeeDataActivity.this, "请到程序管理中" +
                                        "打开彩管家拨打电话权限");
                            }
                        });

                break;
            case R.id.rl_employee_msg:// 发送IM消息
                if (null != item) {
                    Intent intent = new Intent(this, IMConnectionActivity.class);
                    intent.putExtra(IMConnectionActivity.DST_UUID, item.getUid());
                    intent.putExtra(IMConnectionActivity.DST_USERNAME, item.getUsername());
                    intent.putExtra(IMConnectionActivity.DST_NAME, item.getRealname());
                    intent.putExtra(IMConnectionActivity.DST_AVATAR, item.getAvatar());
                    startActivity(intent);
                }
                break;
            case R.id.rl_employee_money:// 转账
                if (null != item && !TextUtils.isEmpty(item.getUsername())) {
                    Intent intent = new Intent(this, MyPointActivity.class);
                    intent.putExtra(GivenPointAmountActivity.TYPE, "cgj-cgj");
                    intent.putExtra(GivenPointAmountActivity.GIVENMOBILE, item.getMobile());
                    startActivity(intent);
                }
                break;
            case R.id.iv_call:
                Tools.call(EmployeeDataActivity.this, item.getMobile());
                break;
            case R.id.iv_close:
                finish();
                break;
        }
    }

    private void copyText(String tip, String word) {
        if (!TextUtils.isEmpty(word)) {
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", word);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            ToastUtil.showShortToast(this, tip);
        }

    }
}
