package com.tg.coloursteward;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tg.coloursteward.adapter.DragAdapter;
import com.tg.coloursteward.adapter.DragAdapterOther;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.COMMONDLIST;
import com.tg.coloursteward.info.door.DoorFixedResp;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.DragGridView;
import com.tg.coloursteward.view.dialog.DoorEditDialog;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 编辑门禁
 * @author Administrator
 *
 */
public class DoorEditActivity extends BaseActivity {
	    private LinearLayout ll_parent;
	    private LayoutInflater layoutInflater;
	    private DragAdapter mDragAdapter;
	    private DragAdapterOther mDragAdapter1;
	    private List<DoorFixedResp> dataSourceList = new ArrayList<DoorFixedResp>();//常用门禁
	    private ArrayList<COMMONDLIST> dataSourceList1 = new ArrayList<COMMONDLIST>();//非常用门禁
	    private List<DoorFixedResp> dataSourceList2 = new ArrayList<DoorFixedResp>();//常用门禁（编辑后）
	    private DragGridView mDragGridView;
	    private DragGridView mDragGridView1;
	    private DoorEditDialog editDialog;
	    private DoorFixedResp resp;
	    private LinearLayout mEmptyLayout;
	    private TextView empty_tv;
	    private boolean isclick;
	    private Gson gson = new Gson();
	    private boolean isChange = false;
		private String czyid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ll_parent = (LinearLayout) findViewById(R.id.ll_parent);
        layoutInflater = LayoutInflater.from(this);
        editDialog = new DoorEditDialog(this,R.style.qr_dialog);
        initEmptyView();
        czyid = Tools.getCZYID(this);
        getCommonDoorList();
	}
	@Override
	protected boolean handClickEvent(View v) {
		 switch (v.getId()) {
         case R.id.back_layout:
         	 if (isChange) {
             if (null != mDragAdapter && mDragAdapter.list.size() > 0) {
                 changePisition(mDragAdapter.list);
             }
         	 }
             new Handler().postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     Intent intent = new Intent();
                     if (isChange) {
                         setResult(RESULT_OK, intent);
                     }
                     finish();
                 }
             }, 400);
             break;
     }
	return super.handClickEvent(v);
	}
	//初始化空布局
    private void initEmptyView() {
        empty_tv = (TextView) findViewById(R.id.empty_tv);
        mEmptyLayout = (LinearLayout) findViewById(R.id.ll_layout_emp);
        empty_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent authorIntent = new Intent(DoorEditActivity.this, DoorApplyActivity.class);
                startActivity(authorIntent);
            }
        });
    }
    //常用门禁列表
    private void getCommonDoorList() {
    	RequestConfig config = new RequestConfig(this, HttpTools.PSOT_WETOWN_INFO,"获取门禁");
		RequestParams params = new RequestParams();
		params.put("customer_id",czyid);
		params.put("module", "wetown");
		params.put("func", "doorfixed/getlist");
		HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newczy/wetown/BusinessAgentRequest", config, params);
    }
  //其他门禁列表
    private void getOtherListFixed() {
    	RequestConfig config = new RequestConfig(this, HttpTools.PSOT_OTHER_INFO,"获取门禁");
		RequestParams params = new RequestParams();
		params.put("customer_id",czyid);
		params.put("module", "wetown");
		params.put("func", "doorfixed/getlistnotfixed");
		HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newczy/wetown/BusinessAgentRequest", config, params);
    }
    //添加常用门禁
    private void addListFixed(String param) {
    	RequestConfig config = new RequestConfig(this, HttpTools.PSOT_ADDFIXED_INFO,"添加门禁");
		RequestParams params = new RequestParams();
		params.put("customer_id",czyid);
		params.put("params",param);
		params.put("module", "wetown");
		params.put("func", "doorfixed/add");
		HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newczy/wetown/BusinessAgentRequest", config, params);
    }
    //删除常用门禁
    private void deleteDoor(String doorid) {
    	 Map map = new HashMap<String, String>();
         map.put("doorid", doorid);
         String param = new Gson().toJson(map);
    	RequestConfig config = new RequestConfig(this, HttpTools.PSOT_DELETEFIXED_INFO,"删除门禁");
    	RequestParams params = new RequestParams();
    	params.put("customer_id",czyid);
    	params.put("params",param);
    	params.put("module", "wetown");
    	params.put("func", "doorfixed/remove");
    	HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newczy/wetown/BusinessAgentRequest", config, params);
    }
  //编辑常用门禁名称
    public void editDoorName(String param) {
    	RequestConfig config = new RequestConfig(this, HttpTools.PSOT_MODIFY_INFO,"编辑门禁名称");
    	RequestParams params = new RequestParams();
    	params.put("customer_id",czyid);
    	params.put("params",param);
    	params.put("module", "wetown");
    	params.put("func", "doorfixed/modify");
    	HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newczy/wetown/BusinessAgentRequest", config, params);
    }
    //调整常用门禁顺序
    private void changePositionListFixed(String param) {
    	RequestConfig config = new RequestConfig(this, HttpTools.PSOT_INDEX_INFO);
    	RequestParams params = new RequestParams();
    	params.put("customer_id",czyid);
    	params.put("params",param);
    	params.put("module", "wetown");
    	params.put("func", "doorfixed/index");
    	HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newczy/wetown/BusinessAgentRequest", config, params);
    }
  //增加常用门禁刷新常用门禁列表
    public void doorRefresh(DoorFixedResp doorFixedResp) {
        COMMONDLIST commond = new COMMONDLIST();
        commond.doortype = doorFixedResp.getDoortype();
        commond.conntype = doorFixedResp.getConntype();
        commond.doorid = doorFixedResp.getDoorid();
        commond.name = doorFixedResp.getName();
        commond.position = doorFixedResp.getPosition();
        commond.qrcode = doorFixedResp.getQrcode();
        commond.type = doorFixedResp.getType();
        String position = null;
        if (mDragAdapter == null) {
            position = "0";
            List<DoorFixedResp> lists = new ArrayList<DoorFixedResp>();
            lists.add(doorFixedResp);
            mDragAdapter = new DragAdapter(this, lists);
            mDragGridView.setAdapter(mDragAdapter);
            for (int i = 0; i < mDragAdapter1.list.size(); i++) {
                if (mDragAdapter1.list.get(i).doorid.equals(commond.doorid)) {
                    mDragAdapter1.list.remove(i);
                }
            }
            mDragAdapter1.setItemBg(-1);
        } else {

            if (mDragGridView.getAdapter().getCount() < 6) {
                position = String.valueOf(mDragAdapter.list.size());
                doorFixedResp.setPosition(position);//设置常用门禁的位置
                mDragAdapter.list.add(doorFixedResp);
                if (null == mDragAdapter) {
                    mDragAdapter = new DragAdapter(this, mDragAdapter.list);
                    mDragGridView.setAdapter(mDragAdapter);
                }
                mDragAdapter.setItemBg(-1);

                for (int i = 0; i < mDragAdapter1.list.size(); i++) {
                    if (mDragAdapter1.list.get(i).doorid.equals(commond.doorid)) {
                        mDragAdapter1.list.remove(i);
                    }
                }
                mDragAdapter1.setItemBg(-1);
            } else {
            	ToastFactory.showToast(DoorEditActivity.this,"最多能添加6个常用门禁哦");
                mDragAdapter1.setItemBg(-1);
            }
        }
        Tools.saveCommonDoorList(this, mDragAdapter.list, czyid);
    }

	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		// TODO Auto-generated method stub
		super.onSuccess(msg, jsonString, hintString);
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
					if (data != null && data.size() > 0) {
		                if (data.size() > 6) {
		                    for (int i = 0; i < 6; i++) {
		                        dataSourceList2.add(data.get(i));
		                    }
		                } else {
		                    dataSourceList2 = data;
		                }
		                dataSourceList.clear();
		                dataSourceList.addAll(dataSourceList2);
		                setGridview(dataSourceList);
		                getOtherListFixed();
		            } else {
		                setGridview(dataSourceList);
		                getOtherListFixed();
		            }
				} else {
					ToastFactory.showToast(DoorEditActivity.this, message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(msg.arg1 == HttpTools.PSOT_OTHER_INFO){//其他门禁
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					JSONArray content = response.getJSONArray("list");
					Type type = new TypeToken<List<COMMONDLIST>>() {
					}.getType();
					List<COMMONDLIST> data = gson.fromJson(content.toString(), type);
					if (null != data && data.size() > 0) {
		                dataSourceList1.clear();
		                dataSourceList1.addAll(data);
		                setGridview2(dataSourceList1);
		                mEmptyLayout.setVisibility(View.GONE);
		            } else {
		                mEmptyLayout.setVisibility(View.VISIBLE);
		            }
				} else {
					ToastFactory.showToast(DoorEditActivity.this, message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(msg.arg1 == HttpTools.PSOT_ADDFIXED_INFO){//添加门禁
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					 ToastFactory.showToast(DoorEditActivity.this, "添加成功");
					 Tools.saveCommonDoorList(DoorEditActivity.this, mDragAdapter.list, czyid);
				} else {
					ToastFactory.showToast(DoorEditActivity.this, message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(msg.arg1 == HttpTools.PSOT_DELETEFIXED_INFO){//删除门禁
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					Tools.saveCommonDoorList(DoorEditActivity.this, mDragAdapter.list, czyid);
				} else {
					ToastFactory.showToast(DoorEditActivity.this, message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(msg.arg1 == HttpTools.PSOT_MODIFY_INFO){//编辑门禁名称
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					Tools.saveCommonDoorList(DoorEditActivity.this, mDragAdapter.list, czyid);
				} else {
					ToastFactory.showToast(DoorEditActivity.this, message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(msg.arg1 == HttpTools.PSOT_INDEX_INFO){//调整门禁顺序
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					
				} else {
					ToastFactory.showToast(DoorEditActivity.this, message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	//常用门禁
    private void setGridview(List<DoorFixedResp> list) {
        View view = layoutInflater.inflate(R.layout.dooredit_layout_gridview, null);
        TextView title = (TextView) view.findViewById(R.id.tv_grid_title);
        title.setVisibility(View.GONE);
        mDragGridView = (DragGridView) view.findViewById(R.id.gv_dooredit);
        if (list.size() > 0) {
            mDragAdapter = new DragAdapter(this, list);
            mDragGridView.setAdapter(mDragAdapter);
        }
        ll_parent.addView(view);

        mDragGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isclick) {
                    mDragAdapter.setItemBg(-1);
                    mDragAdapter1.setItemBg(-1);
                    isclick = false;
                } else {
                    DoorFixedResp item = (DoorFixedResp) parent.getAdapter().getItem(position);
                    mDragAdapter.setItemBg(-1);
                    Intent intent = new Intent(DoorEditActivity.this,
                            DoorOpenActivity.class);
                    intent.putExtra("qrcode", item.getQrcode());
                    intent.putExtra("doorCache", false);
                    intent.putExtra("qrBle", Integer.parseInt(item.getConntype()));
                    intent.putExtra("scancode", 1);
                    startActivityForResult(intent, 1);
                }
            }
        });
    }

    //非常用门禁
    private void setGridview2(ArrayList<COMMONDLIST> list) {
        View view = layoutInflater.inflate(R.layout.dooredit_layout_gridview, null);
        TextView title = (TextView) view.findViewById(R.id.tv_grid_title);
        title.setText("其他门禁");
        mDragGridView1 = (DragGridView) view.findViewById(R.id.gv_dooredit);
        mDragAdapter1 = new DragAdapterOther(this, list);
        mDragGridView1.setAdapter(mDragAdapter1);
        ll_parent.addView(view);
        mDragGridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isclick) {
                    mDragAdapter.setItemBg(-1);
                    mDragAdapter1.setItemBg(-1);
                    isclick = false;
                } else {
                    COMMONDLIST item = (COMMONDLIST) parent.getAdapter().getItem(position);
                    mDragAdapter1.setItemBg(-1);
                    if (null != item) {
                        Intent intent = new Intent(DoorEditActivity.this,
                                DoorOpenActivity.class);
                        intent.putExtra("qrcode", item.qrcode);
                        intent.putExtra("doorCache", false);
                        intent.putExtra("qrBle", Integer.parseInt(item.conntype));
                        intent.putExtra("scancode", 1);
                        startActivityForResult(intent, 1);
                    }
                }
            }
        });
    }
    @Subscribe 
    public void onEvent(Object event) {
        final Message message = (Message) event;
        if (message.what == Contants.LOGO.DOOREDIT_LOGCLICK) {
            isclick = true;
        } else if (message.what == Contants.LOGO.DOOREDIT_MOVE) {
            isChange = true;
        } else if (message.what == Contants.LOGO.DOOREDIT_ADD) {
            isChange = true;
            addDoor(message.obj.toString());
        } else if (message.what == Contants.LOGO.DOOREDIT_ADD_REFRESH) {
            isChange = true;
            doorRefresh((DoorFixedResp) message.obj);
        } else if (message.what == Contants.LOGO.DOOREDIT_DELETE) {
            isChange = true;
            mEmptyLayout.setVisibility(View.GONE);
            final DoorFixedResp doorFixedResp = (DoorFixedResp) message.obj;
            COMMONDLIST commond = new COMMONDLIST();
            commond.doortype = doorFixedResp.getDoortype();
            commond.conntype = doorFixedResp.getConntype();
            commond.doorid = doorFixedResp.getDoorid();
            commond.name = doorFixedResp.getName();
            commond.position = doorFixedResp.getPosition();
            commond.qrcode = doorFixedResp.getQrcode();
            commond.type = doorFixedResp.getType();

            mDragAdapter1.list.add(commond);
            mDragAdapter1.setItemBg(-1);
            deleteDoor(commond.doorid);

        } else if (message.what == Contants.LOGO.DOOREDIT_DOOR_NAME) {
        		isChange = true;
            resp = (DoorFixedResp) message.obj;
            editDialog.show();
            TextView tv_cancel = (TextView) editDialog.findViewById(R.id.tv_cancel);
            TextView tv_ok = (TextView) editDialog.findViewById(R.id.tv_ok);
            final EditText edit_doorname = (EditText) editDialog.findViewById(R.id.edit_doorname);
            edit_doorname.setText(resp.getName());
            edit_doorname.setSelection(resp.getName().length());
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editDialog.dismiss();
                }
            });
            tv_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = edit_doorname.getText().toString();
                    if (TextUtils.isEmpty(name)) {
                    	ToastFactory.showToast(DoorEditActivity.this,"门禁名称不能为空");
                    } else {
                        Map map = new HashMap<String, String>();
                        map.put("doorid", resp.getDoorid());
                        map.put("name", name);
                        for (int i = 0; i < mDragAdapter.list.size(); i++) {
                        	if (mDragAdapter.list.get(i).getDoorid().equals(resp.getDoorid())) {
                        		mDragAdapter.list.get(i).setName(name);
                        		mDragAdapter.setItemBg(-1);
                        	}
                        }
                        editDoorName(new Gson().toJson(map));
                        editDialog.dismiss();
                    }

                }
            });
        }
        
    }
    
  //调用增加常用门禁接口
    public void addDoor(String params) {
        if (null == mDragAdapter || mDragGridView.getAdapter().getCount() < 6) {
        	addListFixed(params);
        } else {

        }
    }
    //调整常用门禁顺序
    public void changePisition(List<DoorFixedResp> list) {
        for (int i = 0; i < list.size(); i++) {
            Map map = new HashMap<String, String>();
            map.put("doorid", list.get(i).getDoorid());
            map.put("newposition", String.valueOf(i));
            changePositionListFixed(new Gson().toJson(map));
        }
        Tools.saveCommonDoorList(DoorEditActivity.this, mDragAdapter.list, czyid);
    }
    @Override
    protected void onResume() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        super.onResume();
    }
	
	 @Override
	 protected void onDestroy() {
	        EventBus.getDefault().unregister(this);
	        mDragAdapter = null;
	        mDragAdapter1 = null;
	        super.onDestroy();
	    }
	  @Override
	   public void onBackPressed() {
	    	 if (isChange) {
	        if (null != mDragAdapter && mDragAdapter.list.size() > 0) {
	            changePisition(mDragAdapter.list);
	        }
	    	 }
	        new Handler().postDelayed(new Runnable() {
	            @Override
	            public void run() {
	                Intent intent = new Intent();
	                if (isChange) {
	                    setResult(RESULT_OK, intent);
	                }

	                finish();
	            }
	        }, 400);
	    }

	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        setResult(RESULT_OK, data);
	        finish();
	        super.onActivityResult(requestCode, resultCode, data);

	    }
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_door_edit,null);
	}

	@Override
	public String getHeadTitle() {
		headView.setListenerBack(singleListener);
		return "门禁";
	}


}
