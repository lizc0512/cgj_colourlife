package com.tg.coloursteward;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.OffLineDoorAuthorizationResp;
import com.tg.coloursteward.info.OffLineDoorOpenLogResp;
import com.tg.coloursteward.info.door.DoorFixedResp;
import com.tg.coloursteward.info.door.DoorInfoResp;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.serice.BluetoothLeService;
import com.tg.coloursteward.util.NetworkUtil;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.ClearEditText;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 开门结果处理
 * @author Administrator
 *
 */
public class DoorOpenActivity extends BaseActivity {
	 private LinearLayout user_top_view_back;//返回
	    private TextView user_top_view_title;//title
	    private  Button btn_back;//返回

	    private ImageView img_open_status;
	    private TextView txt_open_status;
	    private Button btn_add;

	    private String qrcode;
	    // 所有服务器地址
	    private ArrayList<String> openDoorServiceListAll = new ArrayList<String>();
	    // 当前可用服务器地址
	    private List<String> openDoorServiceListUse = new ArrayList<String>();

	    private String URL = "";// 访问服务器url

	    private DoorInfoResp doorInfoResp;
	    private int id;
	    private Gson gson = new Gson();


	    // 蓝牙相关
	    private String mDeviceName;
	    private String mDeviceAddress;
	    private BluetoothLeService mBluetoothLeService;
	    private boolean mConnected = false;
	    private BluetoothAdapter mBluetoothAdapter;
	    private boolean mScanning = false;
	    private BluetoothAdapter.LeScanCallback mLeScanCallback;
	    private static final long SCAN_PERIOD = 10000;
	    private CountDownTimer bleTimer;// 蓝牙开门计时器
	    private long bleTime;// 蓝牙开门时间
	    private int netCode;

	    private String bleMac = "";

	    private OffLineDoorAuthorizationResp offLineAuthorizationResp;

	    String floor = "";//电梯

	    private boolean isSaveFlag = false;// 缓存门信息 false为不缓存
	    private boolean doorCache;
	    private List<DoorFixedResp> doorInfoCacheResps;// 本地f门禁实体列表

	    private int qrBle;// 开门门禁前缀,1代表普通蓝牙，2代表支持蓝牙
	    private int scancode;

	    private boolean isOpenDoor = false;

	    OffLineDoorOpenLogResp bleOpenLog;//蓝牙开门日志


	    private ClearEditText edit_compile;
		private String czyid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        prepareView();
        initData();
	}

	@Override
	protected boolean handClickEvent(View v) {
		switch (v.getId()) {
		 case R.id.btn_back:
             if (isOpenDoor) {
                 setResult(101);
             }
             finish();
             break;
         case R.id.btn_add:
             if (doorCache) {
                 addDoorFixed();
             } else {
             	 	Intent intent = new Intent(getApplicationContext(), MipcaActivityCapture.class);
             	 	startActivity(intent);
                 overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                 finish();
             }
             break;
		}
		return super.handClickEvent(v);
	}

	private void prepareView() {
    	czyid = Tools.getCZYID(this);
        doorInfoCacheResps = new ArrayList<DoorFixedResp>();

        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(singleListener);

        img_open_status = (ImageView) findViewById(R.id.img_open_status);
        txt_open_status = (TextView) findViewById(R.id.txt_open_status);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(singleListener);

        edit_compile = (ClearEditText) findViewById(R.id.edit_compile);


        try {
        		List<DoorFixedResp> list = Tools.getCommonDoorList(this,czyid);
            if (list != null) {
                doorInfoCacheResps.clear();
                doorInfoCacheResps.addAll(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        bleTimer = new CountDownTimer(15 * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                bleTime = millisUntilFinished;
            }

            @Override
            public void onFinish() {

                txt_open_status.setText("开门失败");
                doorCache = false;
            }
        };

    }

    private void initData() {
        Intent intent = getIntent();
        qrcode = intent.getStringExtra("qrcode");
        doorCache = intent.getBooleanExtra("doorCache", true);
        qrBle = intent.getIntExtra("qrBle", 0);
        scancode = intent.getIntExtra("scancode", 0);
        Log.e("", "qrcode = " + qrcode);
        if (offLineAuthorizationResp == null) {
            qrBle = 1;
            getDoorOrFloor();
            return;
        }

        // 此处需要判断授权时间，如果为临时授权则判断是否过期(暂时未做)
        if (!offLineAuthorizationResp.getDoortype().equals("2")
                && offLineAuthorizationResp.getWifienable().equals("1")) {
            getDoorOrFloor();
            return;
        }
    }
    //添加常用门禁
    private void addDoorFixed() {
        String name = edit_compile.getText().toString();
        if (StringUtils.isEmpty(name)) {
        	ToastFactory.showToast(DoorOpenActivity.this, "请输入门禁名称");
            return;
        }
        Map map = new HashMap();
        map.put("doorid", doorInfoResp.getId());
        map.put("name", name);
        String bid = new Gson().toJson(map);
        RequestConfig config = new RequestConfig(this, HttpTools.GET_ADD_OPEN,"添加常用门禁");
		RequestParams params = new RequestParams();
		params.put("params", bid);
		params.put("customer_id", czyid);
		params.put("module", "wetown");
		params.put("func", "doorfixed/add");
		HttpTools.httpPost(Contants.URl.URL_ICETEST,"/newczy/wetown/BusinessAgentRequest", config, params);
    }
    private void getDoorOrFloor() {
        if (qrBle == 1) {
            OpenDoor();
        } else if (qrBle == 3) {
            initBle();
        } else {
            netCode = NetworkUtil.isNetworkEnabled(DoorOpenActivity.this);

            if (netCode != -1) {
                OpenDoor();
                setStop();
            }

            initBle();
        }
    }
    private void OpenDoor() {
        List<String> datas = Arrays.asList(getResources().getStringArray(
                R.array.open_door_service_list));
        if (datas != null) {

            openDoorServiceListAll.addAll(datas);

            for (int i = 0; i < 2; i++) {

                long currenttime = System.currentTimeMillis() / 1000;
            }
        }
        if (openDoorServiceListUse.size() > 0) {
            // 当前可用服务器
            getRandom(true);
        } else {
            // 无可用服务器
            getRandom(false);
        }
       }
    /**
     * 随机获取服务器
     *
     * @param flag 是否有可用服务器
     */
    private void getRandom(boolean flag) {

        Random random = new Random();
        int position = 0;
        if (flag) {
            position = random.nextInt(openDoorServiceListUse.size());

        } else {
            position = random.nextInt(openDoorServiceListAll.size());

        }

        getServiceUrl(position, flag);
    }
    
    /**
     * 执行当前服务
     *
     * @param position
     * @param flag
     */
    private void getServiceUrl(int position, boolean flag) {

        if (flag) {
            if (openDoorServiceListUse.size() > position) {
                URL = openDoorServiceListUse.get(position);
            }
        } else {
            if (openDoorServiceListAll.size() > position) {
                URL = openDoorServiceListAll.get(position);
            }
        }

        openDoor(URL, qrcode);
    }
    //开门
    private void openDoor(String URL, final String qrcode) {
    	RequestConfig config = new RequestConfig(this, HttpTools.GET_OPEN_DOOR,"开门");
 		RequestParams params = new RequestParams();
 		params.put("customer_id", czyid);
 		params.put("qrcode", qrcode);
 		HttpTools.httpPost(Contants.URl.URL_ICETEST,"/newczy/wetown/DoorOpen", config, params);
    }
    //获取门信息
    private void DoorInfo(String qrcode) {
    	RequestConfig config = new RequestConfig(this, HttpTools.GET_DOOR_MESSAGE);
    	RequestParams params = new RequestParams();
    	params.put("customer_id", czyid);
    	params.put("qrcode", qrcode);
    	HttpTools.httpPost(Contants.URl.URL_ICETEST,"/newczy/wetown/DoorGet", config, params);
    }
    /**
     *  有网则不管，没有网络的话结束掉该界面或者弹框
     */
    private void setStop() {
        if (netCode == -1) {
            // 如果没网
            ToastFactory.showToast(DoorOpenActivity.this,"您的设备不支持BLE");
            finish();
            return;
        }
        if (offLineAuthorizationResp == null) {
            return;
        }
    }
    /**
     * Ble相关
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initBle() {
        if (Build.VERSION.SDK_INT < 18) {

            setStop();
            return;
        }

        if (!offLineAuthorizationResp.getStoptime().equals("0")
                && Long.parseLong(offLineAuthorizationResp.getStoptime()) < System
                .currentTimeMillis()) {
            if (netCode == -1 || qrBle == 3) {
              ToastFactory.showToast(DoorOpenActivity.this,"离线授权权限已经过期，请连接网络重新获取授权数据");
                finish();
            }
            return;
        }
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            setStop();
            return;
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            setStop();
            return;
        }
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.enable();
            mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device,
                                     final int rssi, final byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Message msg1 = mHandler.obtainMessage(2);
                            MsgData md = new MsgData();
                            md.address = device.getAddress();
                            md.name = device.getName();
                            msg1.obj = md;
                            mHandler.sendMessage(msg1);
                        }
                    });
                }
            };
        }
        Thread t = new Thread(new MyRunnable());// 这里比第一种创建线程对象多了个任务对象
        t.start();
    }
    
    class MyRunnable implements Runnable {
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (mBluetoothAdapter != null) {
                scanLeDevice(true);
            }

        }
    }
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressLint("NewApi")
    private void scanLeDevice(final boolean enable) {

        if (enable) {
            bleTimer.start();
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @SuppressLint("NewApi")
                @Override
                public void run() {
                    if (mScanning) {
                        mScanning = false;
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);

                    }
                }
            }, SCAN_PERIOD);

            mScanning = true;
            // F000E0FF-0451-4000-B000-000000000000
            mHandler.sendEmptyMessage(1);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

    }
    
    class MsgData {

        public String name;
        public String address;
    }
    
    public final Handler mHandler = new Handler() {
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: // Notify change

                    break;
                case 2:

                    MsgData md = (MsgData) msg.obj;
                    mDeviceName = md.name;
                    mDeviceAddress = md.address;
                    bleMac = mBluetoothAdapter.getRemoteDevice(mDeviceAddress)
                            .toString().replaceAll(":", "").toLowerCase();

                    if (offLineAuthorizationResp
                            .getWificode() != null) {

                        if (bleMac.equals(offLineAuthorizationResp.getWificode())) {
                            Log.e("", "found" + mDeviceName + " add "
                                    + mDeviceAddress);

                            if (mScanning) {
                                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                                mScanning = false;
                            }

                            Intent gattServiceIntent = new Intent(
                                    DoorOpenActivity.this,
                                    BluetoothLeService.class);
                            Log.d("",
                                    "Try to bindService="
                                            + bindService(gattServiceIntent,mServiceConnection,
                                            BIND_AUTO_CREATE));

                            registerReceiver(mGattUpdateReceiver,makeGattUpdateIntentFilter());
                            if (mBluetoothLeService != null) {
                                mBluetoothLeService.connect(mDeviceAddress);
                            } else {
                                Log.d("", "mBluetoothLeService is null");
                            }

                        }
                    } else {
                        if (netCode == -1) {
                            ToastFactory.showToast(DoorOpenActivity.this, "离线数据与二维码不匹配");
                            finish();
                        }

                    }
                    break;
            }
        }
    };
    
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e("", "Unable to initialize Bluetooth");
                setStop();
                return;
            }

            Log.e("", "mBluetoothLeService is okay");
            mBluetoothLeService.connect(mDeviceAddress);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) { // 连接成功
                Log.e("", "Only gatt, just wait");

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
                    .equals(action)) { // 断开连接
                mConnected = false;

                // btnSend.setEnabled(false);
                // clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) // 可以开始干活了
            {
                mConnected = true;

                byte[] password = getBleWriteDate();
                int len = password.length + 3;
                byte[] data = new byte[len];
                data[0] = (byte) (len);
                for (int i = 0; i < password.length; i++) {
                    data[i + 1] = password[i];
                }
                data[len - 2] = 13;
                data[len - 1] = 10;

                mBluetoothLeService.WriteByets(data);

                // btnSend.setEnabled(true);
                Log.e("", "In what we need");

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) { // 收到数据
                Log.e("", "RECV DATA");

                String readBleData = intent
                        .getStringExtra(BluetoothLeService.EXTRA_DATA);
                if (bleTimer != null) {
                    bleTimer.cancel();
                }

                if (isOpenDoor) {
                    return;
                }
                isOpenDoor = true;
                DecimalFormat df = new DecimalFormat("0.000");

                String result = "";// 开门结果
                // 成功则缓存开门数据，计算开门时间,结束界面
                if (readBleData.trim().equals("success")) {

                    result = "0";

                } else {

                    result = "1";

                }

                bleOpenLog = new OffLineDoorOpenLogResp();
                bleOpenLog.setResult(result);
                bleOpenLog.setDoorid(offLineAuthorizationResp.getDoorid());
                bleOpenLog.setActiontime("" + ((15 * 1000 - bleTime) * 0.4));
                bleOpenLog.setOpentime(System.currentTimeMillis() + "");
                // resp.setResponsetime(bleTemp);

                if (result.equals("0")) {
                    txt_open_status.setText("开门成功");

                    img_open_status.setImageResource(R.drawable.img_open_successes);
                    for (DoorFixedResp doorInfoCacheResp : doorInfoCacheResps) {
                        if (doorInfoCacheResp.getQrcode().equals(
                                offLineAuthorizationResp.getQrcode())) {
                            isSaveFlag = true;

                            return;
                        }
                    }
                    if (isSaveFlag) {

                        btn_add.setVisibility(View.VISIBLE);
                        btn_add.setText("添加常用门禁");
                        //此处暂时隐藏缓存
                        btn_add.setVisibility(View.GONE);
                    } else {
                        btn_add.setVisibility(View.GONE);
                    }


                } else {
                    btn_add.setVisibility(View.GONE);
                    img_open_status.setImageResource(R.drawable.img_open_fail);

                    txt_open_status.setText("开门失败");

                }

                getOffLineOpenLog(bleOpenLog);

            }
        }
    };
    
	private void saveOfflineOpenDoorLog(OffLineDoorOpenLogResp resp) {
		List<OffLineDoorOpenLogResp> offLineDoorOpenLogResps = Tools.getOfflineOpenDoorLog(this, czyid);
		if (offLineDoorOpenLogResps == null) {
			offLineDoorOpenLogResps = new ArrayList<OffLineDoorOpenLogResp>();
		}
		offLineDoorOpenLogResps.add(resp);
		Tools.saveOfflineOpenDoorLog(this,offLineDoorOpenLogResps, czyid);
	}
    // 如果是有网的状态下，直接上传到服务器,不用保存在本地。如果上传失败则保存在本地
    private void getOffLineOpenLog(OffLineDoorOpenLogResp openLogResp) {

        List<OffLineDoorOpenLogResp> openLogResps = new ArrayList<OffLineDoorOpenLogResp>();

        openLogResps.add(openLogResp);

        if (netCode != -1) {
        	     getOffLineOpenLog(openLogResps);
        } else {
            saveOfflineOpenDoorLog(openLogResp);
        }
    }
    
   private void getOffLineOpenLog(List<OffLineDoorOpenLogResp> openLogResps) {
    			String param = gson.toJson(openLogResps);
    			RequestConfig config = new RequestConfig(this, HttpTools.POST_LINE_LOG);
    			RequestParams params = new RequestParams();
    			params.put("customer_id", czyid);
    			params.put("module", "door");
    			params.put("func", "offlineopenlog");
    			HttpTools.httpPost(Contants.URl.URL_ICETEST,"/newczy/wetown/BusinessAgentRequest", config, params);
    	}
   
   @Override
   protected void onPause() {
       super.onPause();
       if (mConnected) {
           scanLeDevice(false);
           try {
               unregisterReceiver(mGattUpdateReceiver);
           } catch (Exception e) {
               // TODO: handle exception
           }

           unbindService(mServiceConnection);
           if (mBluetoothLeService != null) {
               mBluetoothLeService.close();
               mBluetoothLeService = null;
           }
       }
   }

    /**
     * 注册接收的事件
     * @return
     */
    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        return intentFilter;
    }

    // 发送指令开门的数据
    /*
     * 时间格式 A= yyyymmddmmddyyyy 附加字符串A1=OW15LjrCaY2bfcMl 随机从数组选一个密文 B=
	 * xxxxxxxxxxxxxxxx 附加字符串B1=aWuK48JRDPePsYXE 密码= substr(md5((A&A1) XOR (B |
	 * B1)),0,12)
	 */
    private byte[] getBleWriteDate() {

        // 当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String syncTime1 = df.format(new Date());
        df = new SimpleDateFormat("MMddyyyy");
        String syncTime2 = df.format(new Date());
        String StrigA = syncTime1 + syncTime2;
        String[] openOrder = {"rYLTdl8cEUn0x1jH", "hRDs6uXcWkUx5KSU",
                "AyLbIoIAxESIFaJ4", "KnxL2uHPiZLYf8t2", "gsQTGUC5Esc4hhvF",
                "VEHh0rWNw20L2Js8", "7FgEevVryWDpHXzq", "lth55oTh6fFL2jZo",
                "LoRpYZMl85CHVytl", "2VOYAkfOB1ZoeYNd", "qgkwTZjdfwVzZFl9",
                "FG3BHrtZZoKUoArB", "5LjSFeOZCTz8Ffse", "KB2okpE6v95B1MwM",
                "pOpi1Q63WHCoGdwu", "teFuwVYW29uJtXV6", "PZO68GWtUxWTnD49",
                "BEyDyurtW9FCiUfk", "fKccW5JMF4ISz82b", "a3ajkVGxOz4DQpt2"};
        int random = new Random().nextInt(openOrder.length);// 获取随机数（下标）
        String openDoorKey = openOrder[random];// 开门密码

        String StrigA1 = "OW15LjrCaY2bfcMl";
        String StrigB1 = "aWuK48JRDPePsYXE";

        byte[] A = StrigA.getBytes();
        byte[] A1 = StrigA1.getBytes();
        byte[] B = openDoorKey.getBytes();
        byte[] B1 = StrigB1.getBytes();
        byte[] bleData = new byte[StrigA.length()];
        for (int i = 0; i < StrigA.length(); i++) {
            byte left = (byte) (A[i] & A1[i]);
            byte right = (byte) (B[i] | B1[i]);
            bleData[i] = (byte) (left ^ right);
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {

            e.printStackTrace();
        }

        byte[] md5Bytes = md5.digest(bleData);
        // ////////////////////////////////////
        byte[] finalData = new byte[12];
        for (int i = 0; i < 12; i++) {
            finalData[i] = md5Bytes[i];
        }
        return finalData;

    }
    @Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		super.onSuccess(msg, jsonString, hintString);
		String message = HttpTools.getMessageString(jsonString);
		if(msg.arg1 == HttpTools.GET_ADD_OPEN){//添加常用门禁
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					ToastFactory.showToast(DoorOpenActivity.this,"添加成功");
		             setResult(RESULT_OK);
		             finish();
				}else{
					ToastFactory.showToast(DoorOpenActivity.this,message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(msg.arg1 == HttpTools.POST_LINE_LOG){//获取离线开门记录
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					 saveOfflineOpenDoorLog(bleOpenLog);
				}else{
					ToastFactory.showToast(DoorOpenActivity.this,message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(msg.arg1 == HttpTools.GET_OPEN_DOOR){//开门
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					  isOpenDoor = true;
		                img_open_status.setImageResource(R.drawable.img_open_successes);
		                txt_open_status.setText("开门成功");
		                if (doorInfoCacheResps.size() >= 6) {
		                    doorCache = false;
		                    return;
		                }
		                if (doorInfoCacheResps != null && doorInfoCacheResps.size() > 0) {
		                    for (DoorFixedResp doorInfoResp : doorInfoCacheResps) {

		                        if (doorInfoResp.getQrcode().equals(qrcode)) {
		                            doorInfoCacheResps.remove(doorInfoResp);
		                            doorInfoCacheResps.add(0, doorInfoResp);
		                            Tools.saveCommonDoorList(DoorOpenActivity.this,doorInfoCacheResps,czyid);
		                            doorCache = false;
		                            return;
		                        }
		                    }
		                }
		                doorCache = true;
		                DoorInfo(qrcode);
				}else if ("2306".equals(result)) {
					doorCache = false;
					btn_add.setVisibility(View.GONE);
					img_open_status.setImageResource(R.drawable.img_open_fail);
					txt_open_status.setText(reason);
					ToastFactory.showToast(DoorOpenActivity.this,reason);
				} else{
					doorCache = false;
	                if (scancode == 1) {
	                    btn_add.setVisibility(View.GONE);
	                } else {
	                    btn_add.setVisibility(View.VISIBLE);
	                }
	                img_open_status.setImageResource(R.drawable.img_open_fail);
	                txt_open_status.setText("开门失败");
	                btn_add.setText("重新扫描");
					ToastFactory.showToast(DoorOpenActivity.this,message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(msg.arg1 == HttpTools.GET_DOOR_MESSAGE){//获取门禁信息
			try {
				JSONObject response = new JSONObject(jsonString);
				String result = response.get("result").toString();
				String reason = response.get("reason").toString();
				if ("0".equals(result)) {
					JSONObject door = response.getJSONObject("door");
					Type type = new TypeToken<DoorInfoResp>() {
					}.getType();
					DoorInfoResp data = gson.fromJson(door.toString(), type);
					if (data != null) {
		                doorInfoResp = data;
		                Log.e("", "doorInfoResp = " + doorInfoResp.toString());
		                if (doorCache) {
		                    edit_compile.setVisibility(View.VISIBLE);
		                    edit_compile.setText(doorInfoResp.getName());
		                    btn_add.setText("添加常用门禁");
		                    btn_add.setVisibility(View.VISIBLE);
		                } else {
		                    edit_compile.setVisibility(View.GONE);
		                    btn_add.setVisibility(View.GONE);
		                }
		            }
				}else{
					ToastFactory.showToast(DoorOpenActivity.this,message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public View getContentView() {
		// TODO Auto-generated method stub
		return getLayoutInflater().inflate(R.layout.activity_door_open, null);
	}

	@Override
	public String getHeadTitle() {
		
		return "门禁";
	}

}
