package com.youmai.hxsdk.fragment;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.IMConnectionActivity;
import com.youmai.hxsdk.db.bean.ChatMsg;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiBizCard;
import com.youmai.hxsdk.push.http.HttpPushManager;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GlideCircleTransform;
import com.youmai.hxsdk.utils.LogUtils;

import java.util.List;


public class GetCardFragment extends BaseFragment implements View.OnClickListener {
    public final static String TAG = GetCardFragment.class.getSimpleName();


    private ImageView img_head;


    private TextView tv_fid;
    private ImageView img_show;


    private TextView tv_nickname;
    private TextView tv_gender;
    private TextView tv_area;
    private TextView tv_summary;
    private TextView tv_email;
    private TextView tv_job;
    private TextView tv_tel;
    private TextView tv_addr;
    private TextView tv_web;
    private TextView tv_company;


    private Button btn_card;

    // --------------------handler what end--------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hx_fragment_get_card, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        img_head = (ImageView) view.findViewById(R.id.img_head);

        tv_fid = (TextView) view.findViewById(R.id.tv_fid);
        img_show = (ImageView) view.findViewById(R.id.img_show);

        tv_nickname = (TextView) view.findViewById(R.id.tv_nickname);
        tv_gender = (TextView) view.findViewById(R.id.tv_gender);
        tv_area = (TextView) view.findViewById(R.id.tv_area);
        tv_summary = (TextView) view.findViewById(R.id.tv_summary);
        tv_email = (TextView) view.findViewById(R.id.tv_email);
        tv_job = (TextView) view.findViewById(R.id.tv_job);
        tv_tel = (TextView) view.findViewById(R.id.tv_tel);
        tv_addr = (TextView) view.findViewById(R.id.tv_addr);
        tv_web = (TextView) view.findViewById(R.id.tv_web);
        tv_company = (TextView) view.findViewById(R.id.tv_company);


        btn_card = (Button) view.findViewById(R.id.btn_card);
        btn_card.setOnClickListener(this);

        String phone = HuxinSdkManager.instance().getPhoneNum();
        String url = AppConfig.getThumbHeaderUrl(mAct, AppConfig.IMG_HEADER_W, AppConfig.IMG_HEADER_H, phone);

        Glide.with(mAct).load(url)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.hx_ic_launcher).circleCrop())
                .into(img_head);

        String cardfid = AppUtils.getStringSharedPreferences(mAct, "name_image_fid", "");
        Glide.with(mAct).load(AppConfig.getImageUrl(mAct, cardfid))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.hx_ic_launcher))
                .into(img_show);

        getCardInfo();
    }


    private void getCardInfo() {
        int userId = HuxinSdkManager.instance().getUserId();
        String srcPhone = HuxinSdkManager.instance().getPhoneNum();
        String targetPhone = srcPhone;  //for test

        YouMaiBizCard.BizCard_Get_ByPhone.Builder builder = YouMaiBizCard.BizCard_Get_ByPhone.newBuilder();
        builder.setPhone(srcPhone);
        builder.setUserId(userId);
        builder.addTargetPhones(targetPhone);

        YouMaiBizCard.BizCard_Get_ByPhone bizCard = builder.build();

        ReceiveListener listener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiBizCard.BizCard_Get_ByPhone_Ack ack = YouMaiBizCard.BizCard_Get_ByPhone_Ack.parseFrom(pduBase.body);

                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK && ack.getBizcardsCount() > 0) {
                        YouMaiBizCard.BizCard card = ack.getBizcards(0);
                        String cardFid = card.getCardFid() + "";
                        AppUtils.setStringSharedPreferences(mAct, "name_image_fid", cardFid);
                        tv_fid.setText("fid:" + cardFid);
                        String url = AppConfig.getImageUrl(mAct, cardFid);

                        if (isAdded()) {
                            Glide.with(mAct).load(url)
                                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                            .placeholder(R.drawable.hx_ic_launcher))
                                    .into(img_show);
                        }
                        tv_nickname.setText(getString(R.string.hx_get_card_fragment_nickname) + card.getNickname());
                        tv_gender.setText(getString(R.string.hx_get_card_fragment_sex) + card.getGender());
                        tv_area.setText(getString(R.string.hx_get_card_fragment_area) + card.getArea());
                        tv_summary.setText(getString(R.string.hx_get_card_fragment_sign) + card.getSummary());
                        tv_email.setText(getString(R.string.hx_get_card_fragment_email) + card.getEmail());
                        tv_job.setText(getString(R.string.hx_get_card_fragment_position) + card.getJob());
                        tv_tel.setText(getString(R.string.hx_get_card_fragment_special_plane) + card.getTelephone());
                        tv_addr.setText(getString(R.string.hx_get_card_fragment_adress) + card.getAddress());
                        tv_web.setText(getString(R.string.hx_get_card_fragment_url) + card.getWebsite());
                        tv_company.setText(getString(R.string.hx_get_card_fragment_company_name) + card.getCompanyName());
                    }

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        };
        HuxinSdkManager.instance().sendProto(bizCard, listener);
    }


    private void test() {
        String phoneNum = HuxinSdkManager.instance().getPhoneNum();

        List<ChatMsg> lists = HuxinSdkManager.instance().getChatMsgFromCache(phoneNum);
        for (ChatMsg item : lists) {
            ChatMsg.MsgType test = item.getMsgType();
        }


        IPostListener callback = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                Log.v(TAG, "response");
            }
        };
        HuxinSdkManager.instance().sendFeedback(getString(R.string.hx_get_card_fragment_feedback_content), callback);

        HuxinSdkManager.instance().getShowData("4000", null);
        HuxinSdkManager.instance().getUIData("4000");

        //查询数据
        //List<CallLogBean> callLogs = CallRecordUtil.getCallRecord(getActivity());//手机通话记录

        //String appkey0 = SignUtils.genSignature("com.szsfm.mipay", "100000");
        //String appkey1 = SignUtils.genSignature("com.szsfm.querymobile", "100001");
        //String appkey2 = SignUtils.genSignature("com.googfit", "100002"); 海能科技渠道号
        //String appkey3 = SignUtils.genSignature("com.yscoco.smart.bra", "100003");
        //String appkey4 = SignUtils.genSignature("com.yscoco.lines.housekeeper", "100004");
        //String appkey5 = SignUtils.genSignature("com.yscoco.wew.bra", "100005");
        //String appkey6 = SignUtils.genSignature("com.gzftop.demo", "100006");
        //String appkey7 = SignUtils.genSignature("com.happyspace.happyspace", "100007");幸福空间

        //String appkey8 = SignUtils.genSignature("com.nesun.jyt_s", "100008");
        //String appkey9 = SignUtils.genSignature("com.yscoco.autorepair", "100009");
        //String appkey10 = SignUtils.genSignature("com.yscoco.inew", "100010");
        //String appkey11 = SignUtils.genSignature("com.yscoco.wallet", "100011");
        //String appkey12 = SignUtils.genSignature("com.ys.tooth", "100012");
        //String appkey13 = SignUtils.genSignature("com.ys.onecard", "100013");
        //String appkey14 = SignUtils.genSignature("com.yscoco.yz", "100014");
        //String appkey15 = SignUtils.genSignature("com.nesec.jxjy_phone", "100015");能信安继续教育
        // String appkey16 = SignUtils.genSignature("com.lianhehuacheng.huachenggongniu", "100016");富络电子/中继广告

    }


    private void pushMsg() {
        final String phoneNum = "13510462303";
        final String content = "push message";
        final int userId = HuxinSdkManager.instance().getUserId();
        HttpPushManager.pushMsgForText(getContext(), userId, phoneNum, content,
                new HttpPushManager.PushListener() {
                    @Override
                    public void success(String msg) {
                        LogUtils.w(TAG, msg);
                    }

                    @Override
                    public void fail(String msg) {
                        LogUtils.e(TAG, "推送消息异常:" + msg);
                    }
                });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_card) {
            getCardInfo();
            //getShowModel();
            //addShowModel();
            //getCurShow();
            //test();
            //
            //int[] soundId = {R.raw.bye, R.raw.hi, R.raw.icome, R.raw.kb, R.raw.kiss, R.raw.laugh};
            //int index = new Random().nextInt(soundId.length);
            //HuxinSdkManager.instance().playSound(soundId[index]);

            //BackGroundJob.intance().reqConfig(mAct);
            //HxShowHelper.instance().updateAllShow(mAct);

            //StatisticsMgr.instance().addEvent(1);
            //pushMsg();

            /*HuxinSdkManager.instance().getUploadFileToken(new IPostListener() {
                @Override
                public void httpReqResult(String response) {
                    FileToken resp = GsonUtil.parse(response, FileToken.class);
                    if (resp.isSucess()) {
                        String token = resp.getD();
                        int a = 0;
                    }
                }
            });*/

            //notifyTest();
            //pushNotify();

            //progressNotiy();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    private void notifyTest() {
        int notifyID = 1;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mAct)
                        .setSmallIcon(getNotificationIcon())
                        .setContentTitle("My notification")
                        .setContentText("Hello World!")
                        //.setColor(Color.GREEN)
                        .setAutoCancel(true);


        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mAct, IMConnectionActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mAct);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(IMConnectionActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) mAct.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(notifyID, mBuilder.build());
    }


    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.hx_phiz_normal : R.drawable.hx_ic_launcher;
    }


    private void pushNotify() {
        String message = "[{\"CONTEXT_MSGID\":\"22645708\"},{\"CONTENT_PHONE\":\"18664923439\"},{\"CONTEXT_TEXT_TYPE\":\"1\"},{\"CONTENT_TEXT\":\"push message\"}]";
        ChatMsg msg = new ChatMsg(message);
        IMMsgManager.getInstance().parseCharMsg(msg);

    }


    private void progressNotiy() {
        final int id = 1;

        final NotificationManager mNotifyManager =
                (NotificationManager) mAct.getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mAct);
        mBuilder.setContentTitle("Picture Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.hx_phiz_normal);
        // Start a lengthy operation in a background thread
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int incr;
                        // Do the "lengthy" operation 20 times
                        for (incr = 0; incr <= 100; incr += 5) {
                            // Sets the progress indicator to a max value, the
                            // current completion percentage, and "determinate"
                            // state
                            mBuilder.setProgress(100, incr, false);
                            // Displays the progress bar for the first time.
                            mNotifyManager.notify(id, mBuilder.build());
                            // Sleeps the thread, simulating an operation
                            // that takes time
                            try {
                                // Sleep for 5 seconds
                                Thread.sleep(5 * 1000);
                            } catch (InterruptedException e) {
                                Log.d(TAG, "sleep failure");
                            }
                        }
                        // When the loop is finished, updates the notification
                        mBuilder.setContentText("Download complete")
                                // Removes the progress bar
                                .setProgress(0, 0, false);
                        mNotifyManager.notify(id, mBuilder.build());
                    }
                }
                // Starts the thread by calling the run() method in its Runnable
        ).start();
    }
}
