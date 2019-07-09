package com.tg.coloursteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.PublicAccountSearchAdapter;
import com.tg.coloursteward.adapter.PublicAccountSearchOrgAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.PublicAccountInfo;
import com.tg.coloursteward.info.PublicAccountSearchInfo;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对公账户搜索页
 */
public class PublicAccountSearchActivity extends BaseActivity {
    /**
     *搜索当前架构/卡号
     */
    private EditText etAccount;
    private ListView mListView,listviewHistory;
    private ImageView ivDelete;
    private boolean isAno= false;
    private String accessToken;
    private AuthAppService authAppService;//2.0授权
    private ArrayList<PublicAccountInfo> list = new ArrayList<PublicAccountInfo>();
    private ArrayList<PublicAccountSearchInfo> listSrarch = new ArrayList<PublicAccountSearchInfo>();
    private ArrayList<PublicAccountSearchInfo> listOrg = new ArrayList<PublicAccountSearchInfo>();
    private PublicAccountSearchAdapter adapter1;
    private PublicAccountSearchOrgAdapter adapter2;
    private String money;
    private String  ano;
    private String  typeName;
    private String  name;
    private int atid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null){
            money = intent.getStringExtra(Contants.PARAMETER.PUBLIC_ACCOUNT);
            atid = intent.getIntExtra(Contants.PARAMETER.PAY_ATID,-1);
            ano = intent.getStringExtra(Contants.PARAMETER.PAY_ANO);
            typeName = intent.getStringExtra(Contants.PARAMETER.PAY_TYPE_NAME);
            name = intent.getStringExtra(Contants.PARAMETER.PAY_NAME);
        }
        initView();
    }

    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.right_layout://下一步
                listOrg.clear();
                if(etAccount.getText().toString().length() == 0){
                    ToastFactory.showToast(PublicAccountSearchActivity.this,"请先输入组织架构或者卡号");
                    return false;
                }
                PublicAccountSearchInfo info = new PublicAccountSearchInfo();
                info.SearchStr = etAccount.getText().toString();
                if(listSrarch.size() > 0){
                    boolean state = false;
                    for (int i = 0 ; i < listSrarch.size();i++ ){
                        if(listSrarch.get(i).SearchStr.equals(info.SearchStr)){
                            state = true;
                        }
                    }
                    if(!state){
                        listSrarch.add(info);
                    }
                }else{
                    listSrarch.add(info);
                }
                adapter1.notifyDataSetChanged();
                /**
                 * 进行缓存
                 */
                String strResult = "";
                for(int i= 0 ;i <listSrarch.size();i++){
                    strResult += listSrarch.get(i).SearchStr+",";
                }
                strResult = strResult.substring(0,strResult.length()-1);
                Tools.saveStringValue(PublicAccountSearchActivity.this,Contants.storage.PUBLIC_LIST,strResult);
                /**
                 * 搜索
                 */
                if(isContainChinese(etAccount.getText().toString())){
                    isAno = false;
                    getOrgPageInfo(etAccount.getText().toString());
                }else {
                    isAno = true;
                    check(etAccount.getText().toString());
                }

                break;
            case R.id.iv_delete://清空
                DialogFactory.getInstance().showDialog(PublicAccountSearchActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listSrarch.clear();
                        adapter1.notifyDataSetChanged();
                        Tools.saveStringValue(PublicAccountSearchActivity.this,Contants.storage.PUBLIC_LIST,"");
                    }
                }, null, "确定要清空历史记录吗", null, null);
                break;
        }
        return super.handClickEvent(v);
    }

    private void initView() {
        accessToken = Tools.getStringValue(PublicAccountSearchActivity.this,Contants.storage.APPAUTH);
        etAccount = (EditText) findViewById(R.id.edt_colleague_info);
        mListView = (ListView) findViewById(R.id.listview);//组织架构
        listviewHistory = (ListView) findViewById(R.id.listview_history);//历史
        ivDelete = (ImageView) findViewById(R.id.iv_delete);
        ivDelete.setOnClickListener(singleListener);
        adapter1 = new PublicAccountSearchAdapter(PublicAccountSearchActivity.this,listSrarch);
        adapter2 = new PublicAccountSearchOrgAdapter(PublicAccountSearchActivity.this,listOrg);
        listviewHistory.setAdapter(adapter1);
        mListView.setAdapter(adapter2);
        String saveStr = Tools.getStringValue(PublicAccountSearchActivity.this,Contants.storage.PUBLIC_LIST);
        if(StringUtils.isNotEmpty(saveStr)){
            String str[] = saveStr.split(",");
            PublicAccountSearchInfo info;
            if(str.length > 0){
                for (int i=0;i< str.length ;i++){
                info = new PublicAccountSearchInfo();
                info.SearchStr = str[i];
                listSrarch.add(info);
                }
            }
        }
        listviewHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {//搜索
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isContainChinese(listSrarch.get(position).SearchStr)){
                    isAno = false;
                    getOrgPageInfo(listSrarch.get(position).SearchStr);
                }else{
                    isAno = true;
                    check(listSrarch.get(position).SearchStr);
                }

            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//组织架构
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                check(listOrg.get(position).uuid);
            }
        });

    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if(msg.arg1 == HttpTools.GET_ACCOUNT_LIST){
            if(code == 0){
                String  response = HttpTools.getContentString(jsonString);
                if (response != null) {
                    ResponseData data = HttpTools.getResponseKey(response,"list");
                    if(data.length > 0){
                        PublicAccountInfo info ;
                        for (int i = 0; i < data.length ;i ++){
                            info = new PublicAccountInfo();
                            info.title = data.getString(i,"name");
                            info.typeName = data.getString(i,"typeName");
                            info.ano = data.getString(i,"ano");
                            info.bno = data.getString(i,"bno");
                            info.pano = data.getString(i,"pano");
                            info.money = data.getString(i,"money");
                            info.pid = data.getString(i,"pid");
                            info.atid = data.getInt(i,"atid");
                            list.add(info);
                        }
                    }
                    if(list.size() > 0){
                        for (int i = 0; i < list.size();i++){
                            if( list.get(i).ano.equals(ano)){
                                list.remove(i);
                            }
                            if(list.size() > 0){
                                if (!list.get(i).typeName.equals(typeName)){
                                    list.remove(i);
                                }
                            }
                        }
                        if(list.size() > 0){
                            Intent intent = new Intent(PublicAccountSearchActivity.this,PublicAccountListActivity.class);
                            Bundle bundleObject = new Bundle();
                            bundleObject.putSerializable(PublicAccountListActivity.PUBLICACCOUNT_LIST, list);
                            intent.putExtras(bundleObject);
                            intent.putExtra(Contants.PARAMETER.PUBLIC_ACCOUNT,money);
                            intent.putExtra(Contants.PARAMETER.PAY_ATID,atid);
                            intent.putExtra(Contants.PARAMETER.PAY_ANO,ano);
                            startActivity(intent);
                            finish();
                        }else {
                            ToastFactory.showBottomToast(PublicAccountSearchActivity.this,"未找到该账户信息");
                        }
                    }else{
                        ToastFactory.showBottomToast(PublicAccountSearchActivity.this,"未找到该账户信息");
                    }
                }else{
                    ToastFactory.showBottomToast(PublicAccountSearchActivity.this,message);
                }
            }else{
                ToastFactory.showBottomToast(PublicAccountSearchActivity.this,message);
            }
        }else if(msg.arg1 == HttpTools.GET_ORG_PAGE){//组织架构
            if(code == 0){
                String content = HttpTools.getContentString(jsonString);
                if(StringUtils.isNotEmpty(content)){
                    ResponseData data = HttpTools.getResponseKey(content,"list");
                    if(data.length > 0){
                        mListView.setVisibility(View.VISIBLE);
                        listviewHistory.setVisibility(View.GONE);
                        PublicAccountSearchInfo info;
                        for (int i = 0;i < data.length ;i++){
                            if(!data.getString(i,"name").equals(name)){
                                info = new PublicAccountSearchInfo();
                                info.SearchStr = data.getString(i,"name");
                                info.uuid = data.getString(i,"uuid");
                                listOrg.add(info);
                            }
                        }
                        adapter2.notifyDataSetChanged();
                    }else{
                        ToastFactory.showBottomToast(PublicAccountSearchActivity.this,"未找到该账户信息");
                        mListView.setVisibility(View.GONE);
                        listviewHistory.setVisibility(View.VISIBLE);
                    }
                }
            }else{
                ToastFactory.showBottomToast(PublicAccountSearchActivity.this,message);
            }
        }

    }

    /**
     * 判断是否有中文
     * @param str
     * @return
     */
    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }
    /**
     * 进行搜索
     * @param s
     */
    private void getOrgPageInfo(String s) {
        try {
            s = URLEncoder.encode(s, "UTF-8");
            RequestConfig config = new RequestConfig(this, HttpTools.GET_ORG_PAGE);
            RequestParams params = new RequestParams();
            params.put("keyword",s);
            HttpTools.httpGet(Contants.URl.URL_ICETEST,"/org/page",config, params);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    /**
     * 下一步
     */
    public void check(String str) {
            String expireTime = Tools.getStringValue(PublicAccountSearchActivity.this,Contants.storage.APPAUTHTIME);
            Date dt = new Date();
            Long time = dt.getTime();
            /**
             * 获取对公账户数据
             */
            if(StringUtils.isNotEmpty(expireTime)){
                if(Long.parseLong(expireTime) <= time) {//token过期
                    getAuthAppInfo();
                }else{
                    getAccountSearchInfo(str);
                }
            }else{
                getAuthAppInfo();
            }
    }

    /**
     * 进行搜索
     * @param s
     */
    private void getAccountSearchInfo(String s) {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_ACCOUNT_LIST);
        RequestParams params = new RequestParams();
        params.put("showmoney",1);
        params.put("status",1);
        params.put("token",accessToken);
        if(isAno){
            params.put("ano",s);
        }else {
            params.put("familyUuid",s);
        }
        HttpTools.httpPost(Contants.URl.URL_ICETEST,"/dgzh/account/search4web",config, params);
    }
    /**
     * 获取token(2.0)
     * sectet
     */
    private void getAuthAppInfo() {
        if(authAppService == null){
            authAppService = new AuthAppService(PublicAccountSearchActivity.this);
        }
        authAppService.getAppAuth(new GetTwoRecordListener<String, String>() {
            @Override
            public void onFinish(String jsonString, String data2,String data3) {
                int code = HttpTools.getCode(jsonString);
                if(code == 0){
                    JSONObject content = HttpTools.getContentJSONObject(jsonString);
                    if(content.length() > 0){
                        try {
                            accessToken = content.getString("accessToken");
                            String expireTime = content.getString("expireTime");
                            Tools.saveStringValue(PublicAccountSearchActivity.this,Contants.storage.APPAUTH,accessToken);
                            Tools.saveStringValue(PublicAccountSearchActivity.this,Contants.storage.APPAUTHTIME,expireTime);
                            getAccountSearchInfo(etAccount.getText().toString());
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
    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_public_account_search,null);
    }

    @Override
    public String getHeadTitle() {
        headView.setRightText("下一步");
        headView.setRightTextColor(getResources().getColor(R.color.white));
        headView.setListenerRight(singleListener);
        return "对公账户";
    }
}
