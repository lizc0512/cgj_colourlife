package com.youmai.hxsdk.keep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.dialog.HxKeepDialog;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.http.HttpConnector;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.view.full.MapViewUtil;

/**
 * 作者：create by YW
 * 日期：2016.08.24 10:18
 * 描述：
 */
public class KeepMapActivity extends SdkBaseActivity {

    public static final String KEEP_ID = "keep_id";

    private MapViewUtil mMapViewUtil;

    private CacheMsgBean cacheMsgBean;
    private String keepId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hx_keep_map_view);

        initTitle();

        initView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hx_keep_more_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_more) {
            forwardOrDeleteItem();
        }
        return false;
    }

    private void forwardOrDeleteItem() {
        HxKeepDialog hxDialog = new HxKeepDialog(mContext);
        HxKeepDialog.HxCallback callback =
                new HxKeepDialog.HxCallback() {
                    @Override
                    public void onForward() {
                        Intent intent = new Intent();
                        intent.setAction("com.youmai.huxin.recent");
                        intent.putExtra("type", "forward_msg");
                        intent.putExtra("data", cacheMsgBean);
                        mContext.startActivity(intent);
                    }

                    @Override
                    public void onDelete() {
                        delKeep();
                    }
                };
        hxDialog.setHxCollectDialog(callback);
        hxDialog.show();
    }

    private void initTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("位置");

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        String location = getIntent().getStringExtra("location");

        String mLabelAddress = getIntent().getStringExtra("labelAddress");

        cacheMsgBean = getIntent().getParcelableExtra(KeepAdapter.CACHE_MSG_BEAN);
        keepId = getIntent().getStringExtra(KEEP_ID);


        MapView mMapView = (MapView) findViewById(R.id.fm_msg_map);
        TextView tv_location_address = (TextView) findViewById(R.id.tv_location_address);
        TextView tv_location_long_address = (TextView) findViewById(R.id.tv_location_long_address);
        ImageView iv_navigate = (ImageView) findViewById(R.id.iv_navigate);
        ImageView iv_half_back = (ImageView) findViewById(R.id.iv_half_back);


        mMapViewUtil = new MapViewUtil(this, mMapView);
        mMapViewUtil.onCreate(null);
        mMapViewUtil.setLocation(location);//标志物

        if (!StringUtils.isEmpty(location)) {
            final LatLng latLng1 = new LatLng(
                    Double.parseDouble(location.split(",")[1]),
                    Double.parseDouble(location.split(",")[0]));
            iv_navigate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//导航
                    setFloatView(false);
                    mMapViewUtil.toDaoHang(latLng1);
                    /*if (!FloatLogoUtil.instance().isShow()) {
                        FloatLogoUtil.instance().showFloat(KeepMapActivity.this, HuxinService.MODEL_TYPE_FULL, false);
                    }*/
                }
            });
        }

        if (!StringUtils.isEmpty(mLabelAddress)) {
            try {
                if (mLabelAddress.contains(":")) {
                    tv_location_address.setVisibility(View.VISIBLE);
                    tv_location_address.setText(mLabelAddress.split(":")[0]);
                    tv_location_long_address.setVisibility(View.VISIBLE);
                    tv_location_long_address.setText(mLabelAddress.split(":")[1]);
                } else {
                    tv_location_address.setVisibility(View.VISIBLE);
                    tv_location_long_address.setVisibility(View.GONE);
                    tv_location_address.setText(mLabelAddress);
                }
            } catch (Exception e) {
                e.printStackTrace();
                tv_location_address.setVisibility(View.VISIBLE);
                tv_location_address.setText(mLabelAddress);
                LogUtils.e(Constant.SDK_UI_TAG, "地图位置拆分有异常");
            }
        }


    }


    private void delKeep() {
        String url = AppConfig.COLLECT_DEL;
        ReqKeepDel del = new ReqKeepDel(this);
        del.setIds(keepId);

        HttpConnector.httpPost(url, del.getParams(), new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                RespBaseBean baseBean = GsonUtil.parse(response, RespBaseBean.class);
                if (baseBean != null && baseBean.isSuccess()) {
                    Toast.makeText(mContext, R.string.collect_del_success, Toast.LENGTH_SHORT).show();
                    Intent data = new Intent();
                    data.putExtra(KEEP_ID, keepId);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                } else {
                    Toast.makeText(mContext, R.string.collect_del_fail, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
