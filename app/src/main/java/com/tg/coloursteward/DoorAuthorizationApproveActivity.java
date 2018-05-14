package com.tg.coloursteward;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.reflect.TypeToken;
import com.tg.coloursteward.adapter.SelectCommunityAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.AuthorizationListResp;
import com.tg.coloursteward.info.CommunityResp;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.SelectCommunityDialog;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.os.Bundle;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 门禁授权申请
 */
public class DoorAuthorizationApproveActivity extends BaseActivity {

    private TextView tv_time,//申请时间
            tv_memo,//申请备注
            tv_community;//授权小区
    private Button btn_hour,//2小时
            btn_one_day,//一天
            btn_seven_days,//7天
            btn_years,//一年
            btn_permanent;//永久;
    private Button btn_pass,//通过
            btn_refuse;//拒绝

    private String usertype = "4";
    private AuthorizationListResp authorizationListResp;

    private boolean refuse;

    // 小区列表
    private List<CommunityResp> communityList = new ArrayList<CommunityResp>();
    // 当前小区
    private CommunityResp communityResp;
    // 保存小区选中状态 哪一个小区被选中
    private int whichCommunitySel = 0;
    private String czyid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareView();
        getIntentData();
        prepareDate();
    }

    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.btn_hour:
                usertype = "4";
                setChooseBtnSelector(1);
                break;
            case R.id.btn_one_day:
                usertype = "3";
                setChooseBtnSelector(2);
                break;
            case R.id.btn_seven_days:
                usertype = "2";
                setChooseBtnSelector(3);
                break;
            case R.id.btn_years:
                usertype = "5";
                setChooseBtnSelector(4);
                break;
            case R.id.btn_permanent:
                usertype = "1";
                setChooseBtnSelector(5);
                break;
            case R.id.btn_pass:
                if (refuse) {
                    //通过
                    approve("1");
                } else {
                    //再次授权
                    authorize();
                }
                break;
            //拒绝
            case R.id.btn_refuse:
                approve("2");
                break;
            case R.id.tv_community:
                if (communityList.size() > 1) {
                    selectCommunity();
                }
                break;


        }
        return super.handClickEvent(v);
    }

    /**
     * 获取Intent传过来的数据
     */
    private void getIntentData() {

        Intent intent = getIntent();
        authorizationListResp = (AuthorizationListResp) intent
                .getSerializableExtra("authorListResp");
        refuse = intent.getBooleanExtra("refuse", true);
        czyid = Tools.getCZYID(this);
        List<CommunityResp> czy_CommunityList = Tools.getCZY_CommunityList(this, czyid);
        communityList.addAll(czy_CommunityList);
    }

    private void prepareView() {

        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_memo = (TextView) findViewById(R.id.tv_memo);
        tv_community = (TextView) findViewById(R.id.tv_community);
        tv_community.setOnClickListener(singleListener);
        btn_hour = (Button) findViewById(R.id.btn_hour);
        btn_hour.setOnClickListener(singleListener);
        btn_hour.setSelected(true);
        btn_one_day = (Button) findViewById(R.id.btn_one_day);
        btn_one_day.setOnClickListener(singleListener);
        btn_seven_days = (Button) findViewById(R.id.btn_seven_days);
        btn_seven_days.setOnClickListener(singleListener);
        btn_years = (Button) findViewById(R.id.btn_years);
        btn_years.setOnClickListener(singleListener);
        btn_permanent = (Button) findViewById(R.id.btn_permanent);
        btn_permanent.setOnClickListener(singleListener);

        btn_pass = (Button) findViewById(R.id.btn_pass);
        btn_pass.setOnClickListener(singleListener);
        btn_refuse = (Button) findViewById(R.id.btn_refuse);
        btn_refuse.setOnClickListener(singleListener);


    }

    private void prepareDate() {
        if (authorizationListResp != null && communityList != null && communityList.size() > 0) {
            tv_memo.setText(authorizationListResp.getMemo());
            String date = authorizationListResp.getCreationtime() + "000";
            String dateTime = "";
            try {
                Long dateLong = Long.parseLong(date);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                dateTime = df.format(dateLong);
            } catch (Exception e) {
            }
            tv_time.setText(dateTime);
            if (refuse) {
                btn_refuse.setVisibility(View.VISIBLE);
            } else {
                btn_refuse.setVisibility(View.GONE);
            }
            communityResp = communityList.get(0);
            tv_community.setText(communityResp.getName());
        }
    }

    /**
     * 批复
     *
     * @param approve 1表示通过 2表示拒绝
     */
    private void approve(String approve) {

        // 没有授权任何人
        if (authorizationListResp == null) {
            return;
        }
        // 申请编号
        String id = authorizationListResp.getId();

        String bid = "";
        if (communityResp != null) {
            bid = communityResp.getBid();
        } else {
            ToastFactory.showToast(DoorAuthorizationApproveActivity.this, "请选择小区");
            return;
        }
        long currentTime = System.currentTimeMillis() / 1000;
        long stop = 0;
        // 二次授权，0没有，1有
        String granttype = "";
        // 授权类型，0临时，1限时，2永久
        String autype = "";
        String starttime = currentTime + "";
        String stoptime = "";

        if ("1".equals(usertype)) {
            starttime = "0";
            stoptime = "0";
            granttype = "1";
            autype = "2";
        } else {
            autype = "1";
            granttype = "0";

            if ("2".equals(usertype)) {

                stop = currentTime + 3600 * 24 * 7;

            } else if ("3".equals(usertype)) {

                stop = currentTime + 3600 * 24;

            } else if ("4".equals(usertype)) {

                stop = currentTime + 3600 * 2;

            } else if ("5".equals(usertype)) {
                stop = currentTime + 3600 * 24 * 365;
            }

            starttime = DoorAuthorizationActivity.phpToTimeString(currentTime + "");
            stoptime = DoorAuthorizationActivity.phpToTimeString(stop + "");
        }
        // 备注
        String memo = "0";
        RequestConfig config = new RequestConfig(this, HttpTools.POST_APPLY_INFO);
        RequestParams params = new RequestParams();
        params.put("customer_id", czyid);
        params.put("applyid", id);
        params.put("bid", bid);
        params.put("approve", approve);
        params.put("autype", autype);
        params.put("usertype", usertype);
        params.put("granttype", granttype);
        if (!"1".equals(usertype)) {
            params.put("starttime", starttime);
            params.put("stoptime", stoptime);
        }
        params.put("memo", memo);
        HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newczy/wetown/ApplyApprove", config, params);
    }

    /**
     * 再次授权
     *
     * @param
     */
    private void authorize() {
        // 没有授权任何人
        if (authorizationListResp == null) {
            return;
        }

        String toid = authorizationListResp.getToid();

        String bid = "";
        if (communityResp != null) {
            bid = communityResp.getBid();
        } else {
            ToastFactory.showToast(DoorAuthorizationApproveActivity.this, "请选择小");
            return;
        }
        long currentTime = System.currentTimeMillis() / 1000;
        long stop = 0;
        // 二次授权，0没有，1有
        String granttype = "";
        // 授权类型，0临时，1限时，2永久
        String autype = "";
        String starttime = currentTime + "";
        String stoptime = "";

        if ("1".equals(usertype)) {
            starttime = "0";
            stoptime = "0";
            granttype = "1";
            autype = "2";
        } else {
            autype = "1";
            granttype = "0";

            if ("2".equals(usertype)) {

                stop = currentTime + 3600 * 24 * 7;

            } else if ("3".equals(usertype)) {

                stop = currentTime + 3600 * 24;

            } else if ("4".equals(usertype)) {

                stop = currentTime + 3600 * 2;

            } else if ("5".equals(usertype)) {
                stop = currentTime + 3600 * 24 * 365;
            }
            starttime = DoorAuthorizationActivity.phpToTimeString(currentTime + "");
            stoptime = DoorAuthorizationActivity.phpToTimeString(stop + "");
        }

        // 备注
        String memo = "";
        RequestConfig config = new RequestConfig(this, HttpTools.POST_AUTOR_INFO);
        RequestParams params = new RequestParams();
        params.put("customer_id", czyid);
        params.put("toid", toid);
        params.put("bid", bid);
        params.put("usertype", usertype);
        params.put("granttype", granttype);
        params.put("autype", autype);
        if (!"1".equals(usertype)) {
            params.put("starttime", starttime);
            params.put("stoptime", stoptime);
        }
        params.put("memo", memo);
        HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newczy/wetown/AuthorizationAuthorize4mobile", config, params);
    }

    /**
     * 选中状态设置
     */
    private void setChooseBtnSelector(int index) {
        btn_hour.setSelected(false);
        btn_one_day.setSelected(false);
        btn_seven_days.setSelected(false);
        btn_years.setSelected(false);
        btn_permanent.setSelected(false);
        switch (index) {
            case 1:
                btn_hour.setSelected(true);
                break;
            case 2:
                btn_one_day.setSelected(true);
                break;
            case 3:
                btn_seven_days.setSelected(true);
                break;
            case 4:
                btn_years.setSelected(true);
                break;
            case 5:
                btn_permanent.setSelected(true);
                break;
        }
    }

    /**
     * 选择小区弹窗
     */
    private void selectCommunity() {

        final SelectCommunityDialog dialog = new SelectCommunityDialog(
                DoorAuthorizationApproveActivity.this,
                R.style.selectorDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        // 添加选项名称
        TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
        tv_title.setText("选择小区");

        ListView listView = (ListView) dialog.findViewById(R.id.listview);

        final SelectCommunityAdapter adapter = new SelectCommunityAdapter(
                DoorAuthorizationApproveActivity.this, communityList,
                whichCommunitySel);

        listView.setAdapter(adapter);

        if (communityList != null && communityList.size() > 3) {
            setListViewHeightBasedOnChildren(listView);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                CommunityResp data = communityList.get(position);

                if (data != null) {
                    communityResp = data;
                    whichCommunitySel = position;
                    adapter.setWhichCommunitySel(whichCommunitySel);
                    tv_community.setText(communityResp.getName());
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * 动态计算设置AbsListView高度
     *
     * @param absListView
     * @函数名 setAbsListViewHeightBasedOnChildren
     * @功能 TODO
     * @备注 <其它说明>
     */
    private void setListViewHeightBasedOnChildren(ListView absListView) {

        ListAdapter listAdapter = absListView.getAdapter();
        if (listAdapter != null && listAdapter.getCount() > 0) {

            View view = listAdapter.getView(0, null, absListView);
            view.measure(0, 0);
            int totalHeight = 0;

            totalHeight = view.getMeasuredHeight() * 3;


            ViewGroup.LayoutParams params = absListView.getLayoutParams();
            params.height = totalHeight;
            absListView.setLayoutParams(params);
        }
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        // TODO Auto-generated method stub
        super.onSuccess(msg, jsonString, hintString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.POST_APPLY_INFO) {//
            try {
                JSONObject response = new JSONObject(jsonString);
                String result = response.get("result").toString();
                String reason = response.get("reason").toString();
                if ("0".equals(result)) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastFactory.showToast(DoorAuthorizationApproveActivity.this, message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject response = new JSONObject(jsonString);
                String result = response.get("result").toString();
                String reason = response.get("reason").toString();
                if ("0".equals(result)) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastFactory.showToast(DoorAuthorizationApproveActivity.this, message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View getContentView() {
        // TODO Auto-generated method stub
        return getLayoutInflater().inflate(R.layout.activity_door_authorization_approve, null);
    }

    @Override
    public String getHeadTitle() {
        // TODO Auto-generated method stub
        return "授权";
    }


}
