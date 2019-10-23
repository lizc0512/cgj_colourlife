package com.tg.setting.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.intelspace.library.ErrorConstants;
import com.intelspace.library.api.OnEntryCardReaderModeCallback;
import com.intelspace.library.api.OnFoundDeviceListener;
import com.intelspace.library.module.Device;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.LinkParseUtil;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.setting.entity.KeyCommunityListEntity;
import com.tg.setting.entity.KeyMessageEntity;
import com.tg.setting.fragment.KeyCardReaderFragment;
import com.tg.setting.fragment.KeyDoorBagsFragment;
import com.tg.setting.fragment.KeyDoorListFragment;
import com.tg.setting.fragment.KeyDoorStatisticsFragment;
import com.tg.setting.model.KeyDoorModel;
import com.tg.setting.model.SendCardModel;
import com.tg.setting.service.LekaiService;
import com.tg.setting.view.KeyCommunityPopWindowView;
import com.tg.user.model.UserModel;
import com.youmai.hxsdk.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

import static com.tg.setting.activity.CardSenderPhoneActivity.DEVICE;
import static com.tg.setting.activity.CardSenderPhoneActivity.HAIRPINID;

/**
 * 乐开-门禁管理
 *
 * @author hxg 2019.07.18
 */
public class KeyDoorManagerActivity extends BaseActivity implements HttpResponse {
    private ImageView back_finish_img;
    private LinearLayout ll_title;
    private TextView tv_key_title;
    private ImageView iv_title_down;
    private ImageView iv_add;
    private ImageView iv_msg;
    private TextView tv_door;
    private TextView tv_key;
    private TextView tv_send_card;
    private TextView tv_statistics;
    private List<KeyCommunityListEntity.ContentBeanX.ContentBean> communityList = new ArrayList<>();
    private String accountUuid;
    private String communityUuid = "";
    private String communityName = "";
    private int selectPosition = 0;
    private UserModel userModel;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private KeyDoorListFragment keyDoorListFragment;
    private KeyDoorBagsFragment keyDoorBagsFragment;
    private KeyCardReaderFragment keyCardReaderFragment;
    private KeyDoorStatisticsFragment keyDoorStatisticsFragment;
    private KeyDoorModel keyDoorModel;
    private int addType = 0;
    private Device mDevice;
    private   ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        headView.setVisibility(View.GONE);
        userModel = new UserModel(this);
        fragmentManager = getSupportFragmentManager();
        Intent intent = new Intent(this, LekaiService.class);
        startService(intent);
        bindService(intent, mConn, Context.BIND_AUTO_CREATE);
        initView();
        initData();
    }

    private LekaiService mLekaiService;
    private ServiceConnection mConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LekaiService.LocalBinder binder = (LekaiService.LocalBinder) service;
            mLekaiService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLekaiService = null;
        }
    };

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_key_door_manager, null);
    }

    private void initView() {
        back_finish_img = findViewById(R.id.back_finish_img);
        ll_title = findViewById(R.id.ll_title);
        iv_title_down = findViewById(R.id.iv_title_down);
        tv_key_title = findViewById(R.id.tv_key_title);
        iv_add = findViewById(R.id.iv_add);
        iv_msg = findViewById(R.id.iv_msg);
        tv_door = findViewById(R.id.tv_door);
        tv_key = findViewById(R.id.tv_key);
        tv_statistics = findViewById(R.id.tv_statistics);
        tv_send_card = findViewById(R.id.tv_send_card);

        back_finish_img.setOnClickListener(singleListener);
        iv_add.setOnClickListener(singleListener);
        iv_msg.setOnClickListener(singleListener);
        tv_door.setOnClickListener(singleListener);
        tv_key.setOnClickListener(singleListener);
        tv_statistics.setOnClickListener(singleListener);
        tv_send_card.setOnClickListener(singleListener);
        ll_title.setOnClickListener(singleListener);
    }

    private void initData() {
        DisplayUtil.showInput(false, KeyDoorManagerActivity.this);
        accountUuid = UserInfo.uid;
        userModel.getCommunityList(1, accountUuid, 1, 20, true, this);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 1:
                try {
                    KeyCommunityListEntity keyCommunityListEntity = GsonUtils.gsonToBean(result, KeyCommunityListEntity.class);
                    KeyCommunityListEntity.ContentBeanX content = keyCommunityListEntity.getContent();
                    communityList.addAll(content.getContent());
                    if (communityList.size() > 0) {
                        iv_title_down.setVisibility(View.VISIBLE);
                        KeyCommunityListEntity.ContentBeanX.ContentBean contentBean = communityList.get(0);
                        communityName = contentBean.getName();
                        tv_key_title.setText(communityName);
                        communityUuid = contentBean.getCommunityUuid();
                        if (!TextUtils.isEmpty(communityUuid)) {
                            iv_add.setVisibility(View.VISIBLE);
                            getMessageCount();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                transaction = fragmentManager.beginTransaction();
                if (keyDoorListFragment == null) {
                    keyDoorListFragment = KeyDoorListFragment.getDoorListFragment(communityUuid, communityName);
                    transaction.add(R.id.content_layout, keyDoorListFragment);
                } else {
                    transaction.show(keyDoorListFragment);
                }
                transaction.commitAllowingStateLoss();

                break;
            case 2:
                try {
                    KeyMessageEntity keyMessageEntity = GsonUtils.gsonToBean(result, KeyMessageEntity.class);
                    KeyMessageEntity.ContentBean contentBean = keyMessageEntity.getContent();
                    int unReadCount = contentBean.getCount();
                    messageUrl = contentBean.getUrl();
                    if (unReadCount > 0) {
                        iv_msg.setImageResource(R.drawable.ic_key_red_msg);
                    } else {
                        iv_msg.setImageResource(R.drawable.ic_key_msg);
                    }
                } catch (Exception e) {

                }
                break;
            case 3:
                if (null != keyCardReaderFragment) {
                    keyCardReaderFragment.removeHairpin(delPos);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(communityUuid)) {
            getMessageCount();
        }
    }


    private String messageUrl;

    private void getMessageCount() {
        if (null == keyDoorModel) {
            keyDoorModel = new KeyDoorModel(KeyDoorManagerActivity.this);
        }
        keyDoorModel.getApplicationCountAndUrl(2, communityUuid, UserInfo.uid, this::OnHttpResponse);
    }


    /**
     * 选择小区
     */
    public void selectCommunity(int position, String name, String uuid) {
        selectPosition = position;
        tv_key_title.setText(name);
        communityName = name;
        communityUuid = uuid;
        if (null != keyDoorListFragment) {
            keyDoorListFragment.changeCommunity(communityUuid, communityName);
        }
        if (null != keyDoorBagsFragment) {
            keyDoorBagsFragment.changeCommunity(communityUuid, communityName);
        }
        if (null != keyDoorStatisticsFragment) {
            keyDoorStatisticsFragment.changeCommunity(communityUuid, communityName);
        }
        if (null != keyCardReaderFragment) {
            keyCardReaderFragment.changeCommunity(communityUuid, communityName);
        }
        getMessageCount();
    }

    /**
     * 发送钥匙 绑定门禁
     */
    public void todo(int position, int status) {
        if (null != keyDoorListFragment) {
            keyDoorListFragment.todo(position, status);
        }
    }

    public void toKeyInfor(int position, int status) {
        if (null != keyDoorListFragment) {
            keyDoorListFragment.toKeyInfor(position, status);
        }
    }

    public void toSendPackge(int position) {
        if (null != keyDoorBagsFragment) {
            keyDoorBagsFragment.toSendPackge(position);
        }
    }

    /**
     * 开始扫描设备
     */
    public void startScanDevice() {
        if (null != mLekaiService) {
            mLekaiService.startScanDevice();
        }
    }

    /**
     * 停止扫描设备
     */
    public void stopScanDevice() {
        if (null != mLekaiService) {
            mLekaiService.stopScanDevice();
        }
    }

    private boolean isConnected = false;

    /**
     * 连接发卡器进去发卡模式
     */
    public void connectSendCard(String macAddress, String hairpinId) {
        isConnected = false;
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (null != blueAdapter) {
            boolean openBluetooth = blueAdapter.isEnabled();
            if (!openBluetooth) {
                ToastUtil.showShortToast(this, "设备未打开蓝牙，请在设置中打开");
            } else {
                startScanDevice();
            }
        }
        if (null != mLekaiService) {
             mProgressDialog = ProgressDialog.show(this, null, "正在连接设备中...");
            mLekaiService.setOnFoundDeviceListener(new OnFoundDeviceListener() {
                @Override
                public void foundDevice(Device device) {
                    // 添加设备时扫描监听
                    // 只展示发卡器类型的设备
                    if (Device.LOCK_VERSION_CARDREADER.equals(device.getLockVersion())) {
                        String deviceMac = device.getBluetoothDevice().getAddress();
                        if (deviceMac.equals(macAddress) && !isConnected) {
                            mDevice = device;
                            enterCardMode(hairpinId);
                            stopScanDevice();
                            isConnected = true;
                        }
                    }
                }
            });
        }
        mHand.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null == mDevice) {
                    mProgressDialog.dismiss();
                    ToastUtil.showLongToastCenter(KeyDoorManagerActivity.this, "当前发卡器不在范围,或没有通电");
                    stopScanDevice();
                }
            }
        }, 10000);
    }

    private void enterCardMode(String hairpinId) {
        mLekaiService.entryCardReaderMode(mDevice, new OnEntryCardReaderModeCallback() {
            @Override
            public void onEntryCardReaderModeCallback(int status, String message) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                if (status == ErrorConstants.SUCCESS) {
                    Intent intent = new Intent(KeyDoorManagerActivity.this, CardSenderActivity.class);
                    intent.putExtra(DEVICE, mDevice);
                    intent.putExtra(KeySendKeyListActivity.COMMUNITY_UUID, communityUuid);
                    intent.putExtra(HAIRPINID, hairpinId);
                    startActivity(intent);
                } else {
                    Toast.makeText(KeyDoorManagerActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private int delPos = 0;

    public void delDeviceCard(String hairpinId, int delPos) {
        this.delPos = delPos;
        DialogFactory.getInstance().showDialog(KeyDoorManagerActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //删除
                        SendCardModel sendCardModel = new SendCardModel(KeyDoorManagerActivity.this);
                        sendCardModel.delCgjHairpin(3, hairpinId, KeyDoorManagerActivity.this::OnHttpResponse);
                    }
                }, null, "是否删除当前发卡器？", null,
                null);

    }

    public void enterCardList(String hairpinId) {
        Intent intent = new Intent(KeyDoorManagerActivity.this, CardSenderRecordActivity.class);
        intent.putExtra(HAIRPINID, hairpinId);
        intent.putExtra(KeySendKeyListActivity.COMMUNITY_NAME, communityName);
        intent.putExtra(KeySendKeyListActivity.COMMUNITY_UUID, communityUuid);
        startActivity(intent);
    }


    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.back_finish_img:
                finish();
                break;
            case R.id.ll_title:
                iv_title_down.setRotation(180);
                KeyCommunityPopWindowView areaPop = new KeyCommunityPopWindowView(this, communityList, accountUuid, selectPosition);
                areaPop.showPopupWindow(contentLayout);
                areaPop.setOnDismissListener(() -> {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 1.0f;
                    getWindow().setAttributes(lp);
                    iv_title_down.setRotation(0);
                });
                break;
            case R.id.iv_add:
                if (addType == 0) {
                    Intent intent = new Intent(this, KeyAddDoorActivity.class);
                    intent.putExtra(KeySendKeyListActivity.COMMUNITY_UUID, communityUuid);
                    startActivityForResult(intent, 1);
                } else if (addType == 2) {
                    Intent intent = new Intent(this, CardReaderActivity.class);
                    intent.putExtra(KeySendKeyListActivity.COMMUNITY_UUID, communityUuid);
                    startActivityForResult(intent, 2);
                }
                break;
            case R.id.iv_msg:
                LinkParseUtil.parse(KeyDoorManagerActivity.this, messageUrl, "");
                break;
            case R.id.tv_door:
                addType = 0;
                setTabStyle(tv_door, R.drawable.ic_key_door_select);
                setTabStyle(tv_key, R.drawable.ic_key_key);
                setTabStyle(tv_statistics, R.drawable.ic_key_door_statistics);
                setTabStyle(tv_send_card, R.drawable.ic_key_send_card);
                iv_add.setVisibility(View.VISIBLE);
                hideTransaction();
                if (keyDoorListFragment == null) {
                    keyDoorListFragment = KeyDoorListFragment.getDoorListFragment(communityUuid, communityName);
                    transaction.add(R.id.content_layout, keyDoorListFragment);
                } else {
                    transaction.show(keyDoorListFragment);
                }
                transaction.commitAllowingStateLoss();
                break;
            case R.id.tv_key:
                addType = 1;
                setTabStyle(tv_door, R.drawable.ic_key_door);
                setTabStyle(tv_key, R.drawable.ic_key_key_select);
                setTabStyle(tv_send_card, R.drawable.ic_key_send_card);
                setTabStyle(tv_statistics, R.drawable.ic_key_door_statistics);
                iv_add.setVisibility(View.GONE);
                hideTransaction();
                if (keyDoorBagsFragment == null) {
                    keyDoorBagsFragment = KeyDoorBagsFragment.getKeyBagsFragment(communityUuid, communityName);
                    transaction.add(R.id.content_layout, keyDoorBagsFragment);
                } else {
                    transaction.show(keyDoorBagsFragment);
                }
                transaction.commitAllowingStateLoss();
                break;
            case R.id.tv_send_card:
                addType = 2;
                setTabStyle(tv_door, R.drawable.ic_key_door);
                setTabStyle(tv_key, R.drawable.ic_key_key);
                setTabStyle(tv_send_card, R.drawable.ic_key_send_card_select);
                setTabStyle(tv_statistics, R.drawable.ic_key_door_statistics);
                iv_add.setVisibility(View.VISIBLE);
                hideTransaction();
                if (keyCardReaderFragment == null) {
                    keyCardReaderFragment = KeyCardReaderFragment.getKeyCardReaderFragment(communityUuid, communityName);
                    transaction.add(R.id.content_layout, keyCardReaderFragment);
                } else {
                    transaction.show(keyCardReaderFragment);
                }
                transaction.commitAllowingStateLoss();
                break;
            case R.id.tv_statistics:
                addType = 3;
                setTabStyle(tv_door, R.drawable.ic_key_door);
                setTabStyle(tv_key, R.drawable.ic_key_key);
                setTabStyle(tv_send_card, R.drawable.ic_key_send_card);
                setTabStyle(tv_statistics, R.drawable.ic_key_door_statistics_select);
                iv_add.setVisibility(View.GONE);
                hideTransaction();
                if (keyDoorStatisticsFragment == null) {
                    keyDoorStatisticsFragment = KeyDoorStatisticsFragment.getKeyStatisticsFragment(communityUuid, communityName);
                    transaction.add(R.id.content_layout, keyDoorStatisticsFragment);
                } else {
                    transaction.show(keyDoorStatisticsFragment);
                }
                transaction.commitAllowingStateLoss();
                break;
        }
        return super.handClickEvent(v);
    }


    private void setTabStyle(TextView selectText, int drawableId) {
        Drawable drawableDoor = this.getResources().getDrawable(drawableId);
        selectText.setCompoundDrawablesWithIntrinsicBounds(null, drawableDoor, null, null);
    }

    private void hideTransaction() {
        transaction = fragmentManager.beginTransaction();
        if (null != keyDoorBagsFragment) {
            transaction.hide(keyDoorBagsFragment);
        }
        if (null != keyDoorListFragment) {
            transaction.hide(keyDoorListFragment);
        }
        if (null != keyCardReaderFragment) {
            transaction.hide(keyCardReaderFragment);
        }
        if (null != keyDoorStatisticsFragment) {
            transaction.hide(keyDoorStatisticsFragment);
        }

    }

    @Override
    public String getHeadTitle() {
        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (1 == requestCode) {
                if (null != keyDoorListFragment) {
                    keyDoorListFragment.changeCommunity(communityUuid, communityName);
                }
            } else if (2 == requestCode) {
                if (null != keyCardReaderFragment) {
                    keyCardReaderFragment.changeCommunity(communityUuid, communityName);
                }
            }
        }
    }

    public void hideTitleBottomLayout() {  //没有权限时隐藏标题栏 和tab
        findViewById(R.id.title_layout).setVisibility(View.GONE);
        findViewById(R.id.ll_bottom).setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mConn) {
            unbindService(mConn);
        }
        if (mLekaiService != null && null != mDevice) {
            mLekaiService.disConnect(mDevice);
        }
    }
}
