package com.tg.coloursteward;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tg.coloursteward.adapter.DoorControlAdapter;
import com.tg.coloursteward.adapter.DoorFixedAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.info.door.DoorFixedResp;
import com.tg.coloursteward.info.door.DoorOpenLogResp;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.MyGridView;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 门禁  已去除
 */
public class DoorActivity extends BaseActivity {
	public static int INTENT_SCAN = 1;
 	public static int INTENT_UPDATEFIXEDDOOR = 2;//编辑常用门禁
	private LayoutInflater mInflater;
	private View mHeaderView;
	private LinearLayout li_scan;
	private LinearLayout li_nodoorfixed;
	private LinearLayout li_doorfixed;
	private TextView txt_clear;
	private TextView txt_doorlog;
	private TextView txt_dredge;
	private RelativeLayout rl_applydoor;
	private MyGridView my_doorfixeds;
	private DoorFixedAdapter mDoorFixedAdapter;//常用门禁
	private ListView listview;
	private View mFooterView;
	private RelativeLayout rl_no_doorlog;
	private ImageView img_splash;
	private DoorControlAdapter adapter;
	private PopupWindow popupWindow;
	private LinearLayout mPopScanLayout;
	private LinearLayout li_apply;
	private LinearLayout li_authorization;
	private LinearLayout li_compile;
	private LinearLayout li_open_help;
	private List<DoorFixedResp> doorFixedResps;
	private List<DoorOpenLogResp> openLogResps;
	private Gson gson = new Gson();
	private String czyid;
	private String isgranted;
	private String bid;
	private boolean isopen1 = false;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prepareView();
        prepareCZYID();
	}
	private void loadCommunity() {
		String czy_Community_ID = Tools.getCZY_Community_ID(this);
		getCommunityByColorID(czyid,czy_Community_ID);
    }
	/**
	 * 初始化控件
	 */
	private void prepareView() {
        doorFixedResps = new ArrayList<DoorFixedResp>();
        openLogResps = new ArrayList<DoorOpenLogResp>();
        mInflater = LayoutInflater.from(DoorActivity.this);
        mHeaderView = mInflater.inflate(R.layout.door_header, null);
        li_scan = (LinearLayout) mHeaderView.findViewById(R.id.li_scan);
        li_scan.setOnClickListener(singleListener);

        li_nodoorfixed = (LinearLayout) mHeaderView.findViewById(R.id.li_nodoorfixed);
        li_doorfixed = (LinearLayout) mHeaderView.findViewById(R.id.li_doorfixed);

        txt_clear = (TextView) mHeaderView.findViewById(R.id.txt_clear);
        txt_clear.setOnClickListener(singleListener);
        txt_doorlog = (TextView) mHeaderView.findViewById(R.id.txt_doorlog);
        txt_dredge = (TextView) mHeaderView.findViewById(R.id.txt_dredge);
        txt_dredge.setOnClickListener(singleListener);
        rl_applydoor = (RelativeLayout) mHeaderView.findViewById(R.id.rl_applydoor);
        rl_applydoor.setVisibility(View.VISIBLE);
        txt_dredge.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mDoorFixedAdapter = new DoorFixedAdapter(this, doorFixedResps);
        my_doorfixeds = (MyGridView) mHeaderView.findViewById(R.id.my_doorfixeds);
        my_doorfixeds.setAdapter(mDoorFixedAdapter);
        my_doorfixeds.setVisibility(View.GONE);
        my_doorfixeds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DoorFixedResp fixedResp = (DoorFixedResp) my_doorfixeds.getItemAtPosition(position);
                if (fixedResp == null || StringUtils.isEmpty(fixedResp.getName())) {
                    if (my_doorfixeds.getItemAtPosition(position - 1) != null && StringUtils.isNotEmpty(((DoorFixedResp) my_doorfixeds.getItemAtPosition(position - 1)).getName())) {
                        Intent intent = new Intent(DoorActivity.this, DoorEditActivity.class);
                        startActivityForResult(intent, INTENT_UPDATEFIXEDDOOR);
                    }
                    return;
                }
                Intent intent = new Intent(DoorActivity.this,
                        DoorOpenActivity.class);
                intent.putExtra("qrcode", fixedResp.getQrcode());
                intent.putExtra("doorCache", false);
                intent.putExtra("qrBle", Integer.parseInt(fixedResp.getConntype()));
                intent.putExtra("scancode", 1);
                startActivityForResult(intent, INTENT_SCAN);
            }
        });
        listview = (ListView) findViewById(R.id.listview);

        mFooterView = mInflater.inflate(R.layout.door_footer, null);
        rl_no_doorlog = (RelativeLayout) mFooterView.findViewById(R.id.rl_no_doorlog);
        img_splash = (ImageView) mFooterView.findViewById(R.id.img_splash);
        adapter = new DoorControlAdapter(this, openLogResps);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DoorOpenLogResp openLogResp = (DoorOpenLogResp) listview.getItemAtPosition(position);
                if (openLogResp == null) {
                    return;
                }

	                Intent intent = new Intent(DoorActivity.this,
	                        DoorOpenActivity.class);
	                intent.putExtra("qrcode", openLogResp.getQrcode());
	                intent.putExtra("doorCache", false);
	                intent.putExtra("qrBle", Integer.parseInt(openLogResp.getConntype()));
	                intent.putExtra("scancode", 1);
	                startActivityForResult(intent, INTENT_SCAN);
            }
        });
	}
	/**
	 * 数据
	 */
	private void prepareCZYID() {
  		czyid = Tools.getCZYID(this);
  		if(czyid == null){
  			OAtoCZY();
  		}else{
            prepareDate();
        }

  }
  private void prepareDate(){
      try {
          ArrayList<DoorFixedResp> list = Tools.getCommonDoorList(this,czyid);
          doorFixedResps.clear();
          openLogResps.clear();
          boolean flag = true;
          if (list != null && list.size() > 0) {
              flag = false;
              doorFixedResps.addAll(list);
              showViewDoorListSum();
          }
          if (doorFixedResps.size() > 0) {
              ArrayList<DoorOpenLogResp> openloglist = Tools.getOpenLogList(this,czyid);
              if (openloglist != null && openloglist.size() > 0) {

                  openLogResps.addAll(openloglist);
              }
          }
          showDoorFixedView();
          showDoorLogView();
          showApplyOpen();
          adapter.notifyDataSetChanged();
          mDoorFixedAdapter.notifyDataSetChanged();
          getCommonDoorList();

      } catch (Exception e) {
          e.printStackTrace();
      }
    }
	/**
	 * 小区是否已经开通门禁
	 */
    private void isCheckopen() {
		Map map = new HashMap<String, String>();
		map.put("bid", bid);
		String bid = new Gson().toJson(map);
		RequestConfig config = new RequestConfig(this, HttpTools.GET_CHECK_OPEN);
		RequestParams params = new RequestParams();
		params.put("params", bid);
		params.put("customer_id", czyid);
		params.put("module", "wetown");
		params.put("func", "door/checkopen");
		HttpTools.httpPost(Contants.URl.URL_ICETEST,"/newczy/wetown/BusinessAgentRequest", config, params);
    }
    
    // 获取扫码开门权限
    private void OAtoCZY() {
    	RequestConfig config = new RequestConfig(this, HttpTools.GET_CZY_ID);
    	RequestParams params = new RequestParams();
    	params.put("oa", UserInfo.employeeAccount);
		HttpTools.httpGet(Contants.URl.URL_ICETEST,"/newczy/customer/infoByOa", config, params);
    }
	/**
	 * 获取是否可授权
	 */
    private void getDoorIsGranted() {
    	String key = Tools.getStringValue(this,Contants.EMPLOYEE_LOGIN.key);
        String secret = Tools.getStringValue(this,Contants.EMPLOYEE_LOGIN.secret);
    	RequestConfig config = new RequestConfig(this, HttpTools.GET_DOOR_GRANTED);
		RequestParams params = new RequestParams();
		params.put("customer_id",czyid);
		HttpTools.httpGet(Contants.URl.URL_ICETEST, "/newczy/wetown/AuthorizationIsGranted", config, params);
    }
	/**
	 * 获取常用门禁
	 */
	private void getCommonDoorList() {
		RequestConfig config = new RequestConfig(this, HttpTools.PSOT_WETOWN_INFO,"获取门禁");
		RequestParams params = new RequestParams();
		params.put("customer_id",czyid);
		params.put("module", "wetown");
		params.put("func", "doorfixed/getlist");
		HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newczy/wetown/BusinessAgentRequest", config, params);
	}
	 /**
	  * 获取开门记录
	  */
    private void getopenlog() {
    	RequestConfig config = new RequestConfig(this, HttpTools.PSOT_LOGWETOWN_INFO,"获取开门记录");
		RequestParams params = new RequestParams();
		params.put("customer_id", czyid);
		params.put("module", "wetown");
		params.put("func", "door/openlog");
		HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newczy/wetown/BusinessAgentRequest", config, params);
	}
    /**
     * 清除门禁记录
     */
    private void clearOpenLog() {
    	RequestConfig config = new RequestConfig(this, HttpTools.PSOT_CLEARLOGWETOWN_INFO,"清空");
    	RequestParams params = new RequestParams();
    	params.put("customer_id", czyid);
    	params.put("module", "wetown");
    	params.put("func", "door/clearlog");
    	HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newczy/wetown/BusinessAgentRequest", config, params);
    }
    /**
     * 获取小区编号
     *
     * @param czyid
     * @param communityid
     *  彩之云小区编号
     */
    private void getCommunityByColorID(String czyid, String communityid) {
    	RequestConfig config = new RequestConfig(this, HttpTools.PSOT_COMMUNITY_INFO);
		RequestParams params = new RequestParams();
		params.put("customer_id", czyid);
		params.put("community_id",communityid);
		HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newczy/wetown/CommunityGetByColorID", config, params);
		
    }
	//常用门禁数量判断
    private void showViewDoorListSum() {
        if (doorFixedResps.size() > 6) {
            List<DoorFixedResp> list = new ArrayList<DoorFixedResp>();
            list.addAll(doorFixedResps);
            doorFixedResps.clear();
            for (int i = 0; i < 6; i++) {
                doorFixedResps.add(list.get(i));
            }
        }
        if (doorFixedResps.size() > 0) {
            if (doorFixedResps.size() < 6) {
                mDoorFixedAdapter.setIsadd(false);
                if (doorFixedResps.size() == 3) {
                    DoorFixedResp fixedResp = new DoorFixedResp();
                    doorFixedResps.add(fixedResp);
                }
                if (doorFixedResps.size() % 3 != 0) {
                    for (int i = 0; i < doorFixedResps.size() % 3; i++) {
                        DoorFixedResp fixedResp = new DoorFixedResp();
                        doorFixedResps.add(fixedResp);
                    }
                }
            }
        }
        mDoorFixedAdapter.notifyDataSetChanged();
    }
    //门禁常用门禁展示界面
    private void showDoorFixedView() {
        if (doorFixedResps.size() > 0) {
            li_nodoorfixed.setVisibility(View.GONE);
            my_doorfixeds.setVisibility(View.VISIBLE);
            li_doorfixed.setVisibility(View.VISIBLE);
        } else {
            li_nodoorfixed.setVisibility(View.VISIBLE);
            my_doorfixeds.setVisibility(View.GONE);
            li_doorfixed.setVisibility(View.GONE);
        }
    }
    //门禁记录展示界面
    private void showDoorLogView() {

    	if (openLogResps.size() > 0) {
            if (doorFixedResps.size() > 0) {
                mFooterView.setVisibility(View.GONE);
                rl_no_doorlog.setVisibility(View.VISIBLE);
                txt_doorlog.setVisibility(View.VISIBLE);
                img_splash.setVisibility(View.GONE);
            } else {

                mFooterView.setVisibility(View.VISIBLE);
                rl_no_doorlog.setVisibility(View.GONE);
                img_splash.setVisibility(View.VISIBLE);
                txt_doorlog.setVisibility(View.GONE);
            }

        } else {
            mFooterView.setVisibility(View.VISIBLE);
            if (doorFixedResps.size() > 0) {
                img_splash.setVisibility(View.GONE);
                rl_no_doorlog.setVisibility(View.VISIBLE);
                txt_doorlog.setVisibility(View.VISIBLE);
            } else {
                img_splash.setVisibility(View.VISIBLE);
                txt_doorlog.setVisibility(View.GONE);
                rl_no_doorlog.setVisibility(View.GONE);
            }


        }
    }
    private void showApplyOpen() {
        if (listview.getHeaderViewsCount() > 0 || listview.getFooterViewsCount() > 0) {
            return;
        }
        listview.addHeaderView(mHeaderView);
        listview.addFooterView(mFooterView);
        listview.setAdapter(adapter);
    }
    /**
     * 显示popwindow
     *
     * @param parent
     */
    private void showPopWindow(View parent) {

        if (popupWindow == null) {
            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View popWindowView = layoutInflater.inflate(R.layout.door_pop, null);
            //扫一扫
            mPopScanLayout = (LinearLayout) popWindowView.findViewById(R.id.pop_scan_layout);
            mPopScanLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {//扫一扫
                	intent = new Intent(DoorActivity.this, MipcaActivityCapture.class);
                    startActivityForResult(intent, INTENT_SCAN);
                    popupWindow.dismiss();
                }
            });
            //申请
            li_apply = (LinearLayout) popWindowView.findViewById(R.id.li_apply);
            li_apply.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) { 
                    intent = new Intent(DoorActivity.this, DoorApplyActivity.class);
                    startActivity(intent);
                    popupWindow.dismiss();
                }
            });
            //门禁授权
            li_authorization = (LinearLayout) popWindowView.findViewById(R.id.li_authorization);
            li_authorization.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                   if (!"1".equals(isgranted)) {
                    	ToastFactory.showToast(DoorActivity.this, "您没有授权权限");
                        return;
                    }
                    intent = new Intent(DoorActivity.this, DoorAuthorizationActivity.class);
                    startActivity(intent);
                    popupWindow.dismiss();
                }
            });
            popupWindow = new PopupWindow(popWindowView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            // 使其聚集
            popupWindow.setFocusable(true);
            // 设置允许在外点击消失
            popupWindow.setOutsideTouchable(true);

            // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.showAsDropDown(parent, 0, Tools.dip2px(DoorActivity.this, 7));

            //编辑门禁
            li_compile = (LinearLayout) popWindowView.findViewById(R.id.li_compile);
            li_compile.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(DoorActivity.this, DoorEditActivity.class);
                    startActivityForResult(intent, INTENT_UPDATEFIXEDDOOR);
                    popupWindow.dismiss();
                }
            });

            //门禁说明
            li_open_help = (LinearLayout) popWindowView.findViewById(R.id.li_open_help);
            li_open_help.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                	intent  = new Intent(DoorActivity.this, MyBrowserActivity.class);
                	intent.putExtra(MyBrowserActivity.KEY_URL, "http://www.colourlife.com/Advertisement/Menjin");
                    startActivity(intent);
                    popupWindow.dismiss();
                }
            });
        }
        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);

        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(parent, 0, Tools.dip2px(DoorActivity.this, 7));
    }
  
    @Override
	protected boolean handClickEvent(View v) {
		switch (v.getId()) {
		case R.id.right_layout://右上角弹出框
			 showPopWindow(v);
			break;
		case R.id.txt_clear://清空历史记录
			DialogFactory.getInstance().showDialog(DoorActivity.this, new OnClickListener() {
				@Override
				public void onClick(View v) {
					 clearOpenLog();
				}
			}, null, "是否确定清空开门记录？", null, null);
			break;
		case R.id.li_scan://扫一扫
			  Intent intent = new Intent(getApplicationContext(), MipcaActivityCapture.class);
	          startActivityForResult(intent, INTENT_SCAN);
			break;
			
		 case R.id.txt_dredge://点击申请
			  intent = new Intent(DoorActivity.this, DoorApplyDredgeActivity.class);
			  intent.putExtra("bid", bid);
	          startActivity(intent);
	            break;
		}
		return super.handClickEvent(v);
	}
   
    @Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		int code = HttpTools.getCode(jsonString);
		String message = HttpTools.getMessageString(jsonString);
		if(msg.arg1 == HttpTools.PSOT_WETOWN_INFO){//获取常用门禁
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					JSONArray content = response.getJSONArray("list");
					Type type = new TypeToken<List<DoorFixedResp>>() {}.getType();
					List<DoorFixedResp> data = gson.fromJson(content.toString(), type);
					if(data!= null && data.size() > 0)
					{
						Tools.saveCommonDoorList(DoorActivity.this, data,czyid);
						doorFixedResps.clear();
						doorFixedResps.addAll(data);
						showViewDoorListSum();
						getopenlog();
						getDoorIsGranted();
					}
					else
					{
						loadCommunity();
						doorFixedResps.clear();
			            adapter.notifyDataSetChanged();
					}
                    showDoorFixedView();
				} else {
					ToastFactory.showToast(DoorActivity.this, message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(msg.arg1 == HttpTools.PSOT_LOGWETOWN_INFO){//获取开门记录
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					JSONArray content = response.getJSONArray("list");
					Type type = new TypeToken<List<DoorOpenLogResp>>() {}.getType();
					List<DoorOpenLogResp> data = gson.fromJson(content.toString(), type);
					openLogResps.clear();
		            if (data != null && data.size() > 0) {
		            		Tools.saveOpenLogList(DoorActivity.this, data,czyid);
		                if (doorFixedResps.size() > 0) {
		                    openLogResps.clear();
		                    openLogResps.addAll(data);
		                    adapter.notifyDataSetChanged();
		                }
		            }
		            showDoorLogView();
		            if (openLogResps.size() > 0) {
		                if (doorFixedResps.size() > 0) {
		                    listview.setDividerHeight(1);
		                    txt_clear.setVisibility(View.VISIBLE);
		                    rl_no_doorlog.setVisibility(View.GONE);
		                    img_splash.setVisibility(View.GONE);
		                }
		            } else {
		                listview.setDividerHeight(0);
		                txt_clear.setVisibility(View.GONE);
		            }
				} else {
					ToastFactory.showToast(DoorActivity.this, message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(msg.arg1 == HttpTools.PSOT_COMMUNITY_INFO){//获取小区编号
			try {
			JSONObject response = new JSONObject(jsonString);
			String result = response.get("result").toString();
			String reason = response.get("reason").toString();
			if ("0".equals(result)) {
				JSONObject jsonObject = response.getJSONObject("community");
				String bid = jsonObject.getString("bid");
				DoorActivity.this.bid=bid;
                isCheckopen();
                getDoorIsGranted();
			}else {
				ToastFactory.showToast(DoorActivity.this, message);
			}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(msg.arg1 == HttpTools.GET_DOOR_GRANTED){//获取是否可授权
			try {
			JSONObject response = new JSONObject(jsonString);
			String result = response.get("result").toString();
			String reason = response.get("reason").toString();
			if ("0".equals(result)) {
				isgranted = response.getString("isgranted");
			}else {
				ToastFactory.showToast(DoorActivity.this, message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(msg.arg1 == HttpTools.GET_CHECK_OPEN){//小区是否已经开通门禁
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					String isopen = response.getString("isopen");
					if (StringUtils.isNotEmpty(isopen)) {
	                    DoorActivity.this.isopen1 = Boolean.parseBoolean(isopen);
	                }
	                //false表示未开通，获取申请人数，和是否申请开通
	                if (!isopen1) {
	                	ToastFactory.showToast(DoorActivity.this,"当前小区未开通手机开门");
	                    showApplyOpen();
	                    mHeaderView.setVisibility(View.VISIBLE);
	                    showDoorFixedView();
	                    showDoorLogView();
	                } else {
	                    rl_applydoor.setVisibility(View.GONE);
	                    showDoorFixedView();
	                    showDoorLogView();
	                    showApplyOpen();
	                }
				}else {
					ToastFactory.showToast(DoorActivity.this, message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(msg.arg1 == HttpTools.PSOT_CLEARLOGWETOWN_INFO){//清除门禁记录
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					if (result.equalsIgnoreCase("0")) {
						ToastFactory.showToast(DoorActivity.this, "清除成功");
		                openLogResps.clear();
		                adapter.notifyDataSetChanged();
		                if (openLogResps.size() > 0) {
		                	 	listview.setDividerHeight(1);
		                     mFooterView.setVisibility(View.GONE);
		                     txt_clear.setVisibility(View.VISIBLE);
		                } else {
		                    listview.setDividerHeight(0);
		                    txt_clear.setVisibility(View.GONE);
		                    Tools.saveOpenLogList(DoorActivity.this, null,czyid);
		                    showDoorLogView();

		                    openLogResps.clear();
		                    img_splash.setVisibility(View.GONE);
		                    rl_no_doorlog.setVisibility(View.VISIBLE);
		                    adapter.notifyDataSetChanged();
		                }
		            }
				}else {
					ToastFactory.showToast(DoorActivity.this, message);
				}
				} catch (JSONException e) {
					e.printStackTrace();
				}
		}else if(msg.arg1 == HttpTools.GET_CZY_ID){//获取彩之云账户id
			if(code == 1){
				JSONArray jsonArray =HttpTools.getContentJsonArray(jsonString);
				try {
					JSONObject object = (JSONObject) jsonArray.get(0);
					String CZY_id = object.getString("id");
					String community_id = object.getString("community_id");
					Tools.saveCZYID(DoorActivity.this, CZY_id);
					Tools.saveCZY_Community_ID(DoorActivity.this, community_id);
                    prepareDate();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_SCAN) {
            if (resultCode == RESULT_OK) {

                //获取常用门禁
            		getCommonDoorList();

            } else if (resultCode == 101) {

                if (doorFixedResps.size() > 0) {
                    //获取开门记录
                	getopenlog();
                }


            }
        } else if (requestCode == INTENT_UPDATEFIXEDDOOR) {
            if (resultCode == RESULT_OK) {
                prepareDate();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
  	public View getContentView() {
  		// TODO Auto-generated method stub
  		return getLayoutInflater().inflate(R.layout.activity_door, null);
  	}
	@Override
	public String getHeadTitle() {
		headView.setRightImage(R.drawable.icon_nav_more);
		headView.setListenerRight(singleListener);
		return "门禁";
	}


}
