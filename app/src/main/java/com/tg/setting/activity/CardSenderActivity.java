package com.tg.setting.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.intelspace.library.ErrorConstants;
import com.intelspace.library.StatusConstants;
import com.intelspace.library.api.OnCardStatusCallback;
import com.intelspace.library.api.OnClearCardCallback;
import com.intelspace.library.api.OnInitCardCallback;
import com.intelspace.library.module.Device;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.setting.adapter.ViewPagerAdapter;
import com.tg.setting.entity.CardAccessInforEntity;
import com.tg.setting.fragment.BagCardSenderFragment;
import com.tg.setting.fragment.KeyCardSenderFragment;
import com.tg.setting.model.SendCardModel;
import com.tg.setting.service.LekaiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.tg.setting.activity.KeySendKeyListActivity.COMMUNITY_UUID;

/*
门禁卡的发送 选择钥匙 钥匙包


 */
public class CardSenderActivity extends BaseActivity implements HttpResponse, View.OnClickListener, OnCardStatusCallback {

    private ImageView iv_card_status;
    private TextView tv_card_status;
    private TextView tv_card_operate;
    private TextView tv_card_infor;
    private TabLayout card_tablayout;
    private ViewPager card_viewpager;
    private LinearLayout all_key_layout;
    private ImageView iv_key_check;
    private TextView tv_choice_key;
    private TextView tv_send_key;
    private ProgressDialog mProgressDialog;
    private Device mDevice;
    private String communityUuid;
    private String hairpinId;
    private int mCardStatus;
    private int mInitCount;
    private String cardId;
    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_card_sender, null);
    }

    @Override
    public String getHeadTitle() {
        return "发卡器";
    }


    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 1:
                try {
                    CardAccessInforEntity cardAccessInforEntity = GsonUtils.gsonToBean(result, CardAccessInforEntity.class);
                    cardId = cardAccessInforEntity.getContent().getId();
                } catch (Exception e) {

                }
                break;
            case 2:

                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        Intent intent = new Intent(this, LekaiService.class);
        startService(intent);
        bindService(intent, mConn, Context.BIND_AUTO_CREATE);
    }

    private boolean isClick;

    private void initView() {
        iv_card_status = findViewById(R.id.iv_card_status);
        tv_card_status = findViewById(R.id.tv_card_status);
        tv_card_operate = findViewById(R.id.tv_card_operate);
        tv_card_infor = findViewById(R.id.tv_card_infor);
        card_tablayout = findViewById(R.id.card_tablayout);
        card_viewpager = findViewById(R.id.card_viewpager);
        tv_send_key = findViewById(R.id.tv_send_key);
        tv_choice_key = findViewById(R.id.tv_choice_key);
        all_key_layout = findViewById(R.id.all_key_layout);
        iv_key_check = findViewById(R.id.iv_key_check);
        tv_send_key.setOnClickListener(this);
        tv_card_operate.setOnClickListener(this);
        mProgressDialog = new ProgressDialog(this);
        all_key_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allChoiceKeyList.clear();
                if (isClick) {
                    if (bagCardSenderFragment != null) {
                        allChoiceKeyList.addAll(bagCardSenderFragment.handBagsChoice(1));
                    }
                    if (bagCardSenderFragment != null) {
                        allChoiceKeyList.addAll(keyCardSenderFragment.handDoorChoice(1));
                    }
                    iv_key_check.setImageResource(R.drawable.icon_checked_key_bag);
                } else {
                    if (bagCardSenderFragment != null) {
                        bagCardSenderFragment.handBagsChoice(0);
                    }
                    if (bagCardSenderFragment != null) {
                        keyCardSenderFragment.handDoorChoice(0);
                    }
                    iv_key_check.setImageResource(R.drawable.icon_unchecked_key_bag);
                }
                tv_choice_key.setText("已选:" + allChoiceKeyList.size());
                if (allChoiceKeyList.size() > 0 && type == 2) {
                    tv_send_key.setBackgroundResource(R.color.color_1da1f4);
                    tv_send_key.setEnabled(true);
                } else {
                    tv_send_key.setBackgroundResource(R.color.color_d4d9dc);
                    tv_send_key.setEnabled(false);
                }
                isClick = !isClick;
            }
        });
    }

    private int totalKeys;

    public void initTotalKeys(int keysNum) {
        totalKeys += keysNum;
    }


    public void setChoiceKeyNumbers(int operate, String accessId) {
        if (operate == 0) {
            if (!allChoiceKeyList.contains(accessId)) {
                allChoiceKeyList.add(accessId);
            }
        } else {
            if (allChoiceKeyList.contains(accessId)) {
                allChoiceKeyList.remove(accessId);
            }
        }
        if (allChoiceKeyList.size() > 0 && type == 2) {
            tv_send_key.setBackgroundResource(R.color.color_1da1f4);
            tv_send_key.setEnabled(true);
        } else {
            tv_send_key.setBackgroundResource(R.color.color_d4d9dc);
            tv_send_key.setEnabled(false);
        }
        tv_choice_key.setText("已选:" + allChoiceKeyList.size());
        if (allChoiceKeyList.size() != totalKeys) {
            isClick = true;
            iv_key_check.setImageResource(R.drawable.icon_unchecked_key_bag);
        } else {
            isClick = false;
            iv_key_check.setImageResource(R.drawable.icon_checked_key_bag);
        }
    }

    private ArrayList<String> allChoiceKeyList = new ArrayList<>();


    private BagCardSenderFragment bagCardSenderFragment;
    private KeyCardSenderFragment keyCardSenderFragment;
    private SendCardModel sendCardModel;

    private void initData() {
        Intent intent = getIntent();
        mDevice = intent.getParcelableExtra(CardSenderPhoneActivity.DEVICE);
        hairpinId = intent.getStringExtra(CardSenderPhoneActivity.HAIRPINID);
        communityUuid = intent.getStringExtra(COMMUNITY_UUID);
        String[] tabTitleArray = {"钥匙包", "门禁"};
        for (int i = 0; i < tabTitleArray.length; i++) {
            card_tablayout.addTab(card_tablayout.newTab().setText(tabTitleArray[i]));
        }
        bagCardSenderFragment = BagCardSenderFragment.getKeyCardSenderFragment(communityUuid);
        keyCardSenderFragment = KeyCardSenderFragment.getKeyCardSenderFragment(communityUuid);
        fragmentList.add(bagCardSenderFragment);
        fragmentList.add(keyCardSenderFragment);
        card_tablayout.setTabMode(TabLayout.MODE_FIXED);
        card_tablayout.setSelectedTabIndicatorHeight(4);
        card_tablayout.setSelectedTabIndicatorColor(Color.parseColor("#1DA1F4"));
        card_tablayout.setTabTextColors(Color.parseColor("#333b46"), Color.parseColor("#1DA1F4"));
        card_tablayout.setTabGravity(TabLayout.GRAVITY_FILL);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, tabTitleArray);
        card_viewpager.setAdapter(adapter);
        card_viewpager.setOffscreenPageLimit(fragmentList.size());
        card_tablayout.setupWithViewPager(card_viewpager);
        sendCardModel = new SendCardModel(CardSenderActivity.this);
    }

    private LekaiService mLekaiService;
    private ServiceConnection mConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LekaiService.LocalBinder binder = (LekaiService.LocalBinder) service;
            mLekaiService = binder.getService();
            mLekaiService.cardStatus(CardSenderActivity.this);
            mHandler.sendEmptyMessage(0);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLekaiService = null;
        }
    };

    private int type = 0;
    private int cardNumber;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    type = 0;
                    iv_card_status.setImageResource(R.drawable.icon_device_nocard);
                    tv_card_status.setText("请将卡片放置在发卡器上");
                    tv_card_operate.setVisibility(View.GONE);
                    tv_card_infor.setVisibility(View.GONE);
                    tv_send_key.setEnabled(false);
                    tv_send_key.setBackgroundResource(R.color.color_d4d9dc);
                    break;
                case 1:
                    type = 0;
                    iv_card_status.setImageResource(R.drawable.icon_device_hascard);
                    tv_card_status.setText("卡未知或已被其他厂商使用");
                    tv_card_operate.setVisibility(View.GONE);
                    tv_card_infor.setVisibility(View.GONE);
                    tv_send_key.setEnabled(false);
                    tv_send_key.setBackgroundResource(R.color.color_d4d9dc);
                    break;
                case 2:
                    type = 1;
                    iv_card_status.setImageResource(R.drawable.icon_device_hascard);
                    tv_card_status.setText("空卡，需要初始化");
                    tv_card_operate.setVisibility(View.VISIBLE);
                    tv_card_operate.setText("初始化卡");
                    tv_card_infor.setVisibility(View.GONE);
                    tv_send_key.setEnabled(false);
                    tv_send_key.setBackgroundResource(R.color.color_d4d9dc);
                    break;
                case 3:
                    type = 2;
                    iv_card_status.setImageResource(R.drawable.icon_device_hascard);
                    tv_card_status.setText(String.format(Locale.getDefault(), "卡ID:%d 卡内钥匙数量:%d", msg.arg1, msg.arg2));
                    cardNumber = msg.arg1;
                    tv_card_status.setText("读卡成功,卡号" + cardNumber);
                    tv_card_infor.setText("内含钥匙数" + msg.arg2);
                    tv_card_infor.setVisibility(View.VISIBLE);
                    tv_card_operate.setVisibility(View.VISIBLE);
                    tv_card_operate.setText("清空钥匙");
                    if (allChoiceKeyList.size() > 0) {
                        tv_send_key.setEnabled(true);
                        tv_send_key.setBackgroundResource(R.color.color_1da1f4);
                    } else {
                        tv_send_key.setEnabled(false);
                        tv_send_key.setBackgroundResource(R.color.color_d4d9dc);
                    }
                    sendCardModel.getCardUserInfor(1, communityUuid, cardNumber, CardSenderActivity.this);
                    break;
                case 4:
                    type = 3;
                    iv_card_status.setImageResource(R.drawable.icon_device_hascard);
                    tv_card_status.setText(String.format(Locale.getDefault(), "无法初始化,首个第三方扇区号:%d, 第0扇区需要保留以存储配置", msg.arg1));
                    tv_card_operate.setVisibility(View.GONE);
                    tv_card_infor.setVisibility(View.GONE);
                    tv_send_key.setEnabled(false);
                    tv_send_key.setBackgroundResource(R.color.color_d4d9dc);
                    break;
                case 5:
                    type = 1;
                    tv_card_operate.setText("初始化卡");
                    iv_card_status.setImageResource(R.drawable.icon_device_hascard);
                    tv_card_status.setText(String.format(Locale.getDefault(), "需要初始化,第三方扇区数量:%d,首个第三方扇区号:%d", msg.arg1, msg.arg2));
                    tv_card_status.setText("内含钥匙数" + msg.arg2);
                    tv_send_key.setBackgroundResource(R.color.color_d4d9dc);
                    tv_card_operate.setVisibility(View.GONE);
                    tv_card_infor.setVisibility(View.GONE);
                    tv_send_key.setEnabled(false);
                    break;
                case 6:
                    type = 2;
                    iv_card_status.setImageResource(R.drawable.icon_device_hascard);
                    int[] info = (int[]) msg.obj;
                    tv_card_status.setText(String.format(Locale.getDefault(), "卡ID:%d 卡内钥匙数量:%d 初始化扇区数量:%d 第三方扇区数量:%d 首个第三方扇区号:%d", msg.arg1, msg.arg2, info[0], info[1], info[2]));
                    iv_card_status.setImageResource(R.drawable.icon_device_hascard);
                    cardNumber = msg.arg1;
                    tv_card_status.setText("读卡成功,卡号" + cardNumber);
                    tv_card_infor.setText("内含钥匙数" + msg.arg2);
                    tv_card_infor.setVisibility(View.VISIBLE);
                    tv_card_operate.setVisibility(View.VISIBLE);
                    tv_card_operate.setText("清空钥匙");
                    if (allChoiceKeyList.size() > 0) {
                        tv_send_key.setEnabled(true);
                        tv_send_key.setBackgroundResource(R.color.color_1da1f4);
                    } else {
                        tv_send_key.setEnabled(false);
                        tv_send_key.setBackgroundResource(R.color.color_d4d9dc);
                    }
                    sendCardModel.getCardUserInfor(1, communityUuid, cardNumber, CardSenderActivity.this);
                    break;
                default:
                    type = 0;
                    iv_card_status.setImageResource(R.drawable.icon_device_nocard);
                    tv_card_status.setText("请将卡片放置在发卡器上");
                    tv_card_status.setText(String.format(Locale.getDefault(), "未知状态:%d", msg.arg1));
                    tv_card_operate.setVisibility(View.GONE);
                    tv_card_infor.setVisibility(View.GONE);
                    tv_send_key.setEnabled(false);
                    tv_send_key.setBackgroundResource(R.color.color_d4d9dc);
                    break;
            }
            return true;
        }
    });

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_card_operate:
                if (type == 2) {
                    if (mLekaiService != null) {
                        mProgressDialog = ProgressDialog.show(this, "", "正在清空卡片……");
                        if (!TextUtils.isEmpty(cardId)) {
                            sendCardModel.deleteAllCgjCardRecord(0, cardId, CardSenderActivity.this);
                        }
                        mLekaiService.clearCard(new OnClearCardCallback() {
                            @Override
                            public void onClearCardCallback(final int status, final String message) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mProgressDialog != null) {
                                            mProgressDialog.dismiss();
                                        }
                                        if (status == ErrorConstants.SUCCESS) {
                                            tv_card_status.setText("清空卡片成功");
                                        } else {
                                            tv_card_status.setText(message);
                                        }
                                    }
                                });
                            }
                        });
                    }
                } else {
                    if (mLekaiService != null) {
                        if (mCardStatus == StatusConstants.CARD_STATUS_NEED_INIT) {
                            mProgressDialog = ProgressDialog.show(this, "", "正在初始化卡片……");
                            mLekaiService.initCard(new OnInitCardCallback() {
                                @Override
                                public void onInitCardCallback(final int status, final String message) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mProgressDialog != null) {
                                                mProgressDialog.dismiss();
                                            }
                                            if (status == ErrorConstants.SUCCESS) {
                                                tv_card_status.setText("初始化卡片成功");
                                            } else {
                                                tv_card_status.setText(message);
                                            }
                                        }
                                    });

                                }
                            });
                        } else if (mCardStatus == StatusConstants.CARD_STATUS_NEED_INIT_PARTLY) {
                            mProgressDialog = ProgressDialog.show(this, "", "正在部分初始化卡片……");
                            mLekaiService.initCardPartly(mInitCount, new OnInitCardCallback() {
                                @Override
                                public void onInitCardCallback(final int status, final String message) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mProgressDialog != null) {
                                                mProgressDialog.dismiss();
                                            }
                                            if (status == ErrorConstants.SUCCESS) {
                                                tv_card_status.setText("部分初始化卡片成功");
                                            } else {
                                                tv_card_status.setText(message);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
                break;
            case R.id.tv_send_key:
                if (type == 2) {
                    if (allChoiceKeyList.size() > 0) {
                        Intent intent = new Intent(CardSenderActivity.this, CardSenderPhoneActivity.class);
                        intent.putExtra(CardSenderPhoneActivity.DEVICE, mDevice);
                        intent.putExtra(COMMUNITY_UUID, communityUuid);
                        intent.putExtra(CardSenderPhoneActivity.CARDNUMBER, cardNumber);
                        intent.putExtra(CardSenderPhoneActivity.HAIRPINID, hairpinId);
                        intent.putStringArrayListExtra(CardSenderPhoneActivity.DEVICEKEYS, allChoiceKeyList);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "请选择要发送的钥匙", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "未检测到门禁卡", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }


    /**
     * 读卡状态改变的回调
     * 字节的每一个bit代表一个扇区,initSection和thirdPartySection长度均为2个字节,即16个bit,表示16个扇区.每一个bit,0表示对应扇区没有使用,1表示被使用
     *
     * @param status            卡片状态
     * @param message           卡片状态文字说明
     * @param cardId            卡片ID
     * @param keyCount          卡片包含的钥匙数量
     * @param initSection       卡片已经初始化过的扇区信息
     * @param thirdPartySection 卡片被第三方使用的扇区信息
     */
    @Override
    public void onCardStatusCallback(int status, String message, int cardId, int keyCount, byte[] initSection, byte[] thirdPartySection) {
        mCardStatus = status;
        switch (status) {
            case StatusConstants.CARD_STATUS_NO_CARD:
                mHandler.sendEmptyMessage(0);
                break;
            case StatusConstants.CARD_STATUS_CARD_USED:
                mHandler.sendEmptyMessage(1);
                break;
            case StatusConstants.CARD_STATUS_NEED_INIT:
                mHandler.sendEmptyMessage(2);
                break;
            case StatusConstants.CARD_STATUS_INIT_DONE:
                Message msg = new Message();
                msg.what = 3;
                msg.arg1 = cardId;
                msg.arg2 = keyCount;
                mHandler.sendMessage(msg);
                break;
            case StatusConstants.CARD_STATUS_NEED_INIT_PARTLY:
                int thirdPartySectorCount = getUsedSectorCountWithSectorInfo(thirdPartySection);
                int firstThirdPartySector = getFirstUsedSectorWithSectorInfo(thirdPartySection);

                Message msg2 = new Message();
                if (firstThirdPartySector <= 1) {
                    msg2.what = 4;
                    msg2.arg1 = firstThirdPartySector;
                } else {
                    msg2.what = 5;
                    msg2.arg1 = thirdPartySectorCount;
                    msg2.arg2 = firstThirdPartySector;
                }
                mHandler.sendMessage(msg2);

                mInitCount = firstThirdPartySector;
                break;
            case StatusConstants.CARD_STATUS_INIT_DONE_PARTLY:
                int initSectorCount = getUsedSectorCountWithSectorInfo(initSection);
                int thirdPartySectorCount2 = getUsedSectorCountWithSectorInfo(thirdPartySection);
                int firstThirdPartySector2 = getFirstUsedSectorWithSectorInfo(thirdPartySection);

                Message msg3 = new Message();
                msg3.what = 6;
                msg3.arg1 = cardId;
                msg3.arg2 = keyCount;
                msg3.obj = new int[]{initSectorCount, thirdPartySectorCount2, firstThirdPartySector2};
                mHandler.sendMessage(msg3);
                break;
            default:
                Message msg4 = new Message();
                msg4.what = 7;
                msg4.arg1 = status;
                mHandler.sendMessage(msg4);
                break;
        }
    }

    /**
     * 根据扇区的使用情况获取使用的扇区数量
     *
     * @param data 扇区信息
     * @return 扇区数量
     */
    private int getUsedSectorCountWithSectorInfo(byte[] data) {
        int usedSectorCount = 0;
        for (int i = 0; i < 16; i++) {
            boolean isUsed = ((data[i / 8] << (i % 8)) & 0x80) == 0x80;
            if (isUsed) {
                usedSectorCount++;
            }
        }
        return usedSectorCount;
    }

    /**
     * 根据使用的扇区情况获取首个被使用的扇区号
     *
     * @param data 扇区信息
     * @return 被使用的扇区号
     */
    private int getFirstUsedSectorWithSectorInfo(byte[] data) {
        int firstUsedSector = 0;
        for (int i = 0; i < 16; i++) {
            boolean isUsed = ((data[i / 8] << (i % 8)) & 0x80) == 0x80;
            if (isUsed) {
                firstUsedSector = i;
                break;
            }
        }
        return firstUsedSector;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConn);
        if (mLekaiService != null) {
            mLekaiService.disConnect(mDevice);
        }
    }
}
