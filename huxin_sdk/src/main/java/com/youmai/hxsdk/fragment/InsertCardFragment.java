package com.youmai.hxsdk.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.protobuf.InvalidProtocolBufferException;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.entity.UploadFile;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiBizCard;
import com.youmai.hxsdk.proto.YouMaiUser;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GlideCircleTransform;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.StringUtils;

import org.json.JSONObject;


public class InsertCardFragment extends BaseFragment implements View.OnClickListener {
    public final static String TAG = InsertCardFragment.class.getSimpleName();
    private static final int SELECT_IMAGE = 1;
    private static final int SELECT_NAMEIMAGE = 2;


    private EditText ed_nickname;
    private EditText ed_area;
    private EditText ed_summary;
    private EditText ed_mail;
    private EditText ed_job;
    private EditText ed_tel;
    private EditText ed_addr;
    private EditText ed_web;
    private EditText ed_company;
    private ImageView img_headImage;
    private Button btn_sendheadImage;

    private RadioButton rd_male, rd_female;
    private Button btn_send;
    private ImageView img_nameImage;
    private Button btn_updateImage;
    private ProgressBar ed_progressBar;

    // --------------------handler what end--------------------


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hx_fragment_edit_card, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        img_headImage = (ImageView) view.findViewById(R.id.ed_headImage);
        btn_sendheadImage = (Button) view.findViewById(R.id.ed_sendheadImage);
        ed_nickname = (EditText) view.findViewById(R.id.ed_nickname);
        ed_area = (EditText) view.findViewById(R.id.ed_area);
        ed_summary = (EditText) view.findViewById(R.id.ed_summary);
        ed_mail = (EditText) view.findViewById(R.id.ed_mail);
        ed_job = (EditText) view.findViewById(R.id.ed_job);
        ed_tel = (EditText) view.findViewById(R.id.ed_tel);
        ed_addr = (EditText) view.findViewById(R.id.ed_addr);
        ed_web = (EditText) view.findViewById(R.id.ed_web);
        ed_company = (EditText) view.findViewById(R.id.ed_company);
        rd_male = (RadioButton) view.findViewById(R.id.rd_male);
        rd_female = (RadioButton) view.findViewById(R.id.rd_female);
        btn_send = (Button) view.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        btn_sendheadImage.setOnClickListener(this);
        img_nameImage = (ImageView) view.findViewById(R.id.img_nameImage);
        btn_updateImage = (Button) view.findViewById(R.id.btn_updateImage);
        btn_updateImage.setOnClickListener(this);
        ed_progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        ed_progressBar.setVisibility(View.GONE);
        getCardInfo();

        String phone = HuxinSdkManager.instance().getPhoneNum();
        String url = AppConfig.getThumbHeaderUrl(mAct, AppConfig.IMG_HEADER_W, AppConfig.IMG_HEADER_H, phone);
        Glide.with(mAct).load(url)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.hx_icon_rd).circleCrop())
                .into(img_headImage);

    }


    private void getCardInfo() {
        int userId = HuxinSdkManager.instance().getUserId();
        String srcPhone = HuxinSdkManager.instance().getPhoneNum();
        String desPhone = srcPhone;  //for self

        ReceiveListener listener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiBizCard.BizCard_Get_ByPhone_Ack ack = YouMaiBizCard.BizCard_Get_ByPhone_Ack.parseFrom(pduBase.body);
                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK && ack.getBizcardsCount() > 0) {
                        YouMaiBizCard.BizCard card = ack.getBizcards(0);
                        String fid = card.getCardFid() + "";

                        String url = AppConfig.getImageUrl(mAct, fid);
                        Glide.with(mAct).load(url)
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                        .placeholder(R.drawable.hx_icon_rd))
                                .into(img_nameImage);


                        int gender = card.getGender();

                        if (gender == 0) {
                            rd_female.setChecked(true);
                        } else {
                            rd_male.setChecked(true);
                        }

                        ed_nickname.setText(card.getNickname());
                        ed_area.setText(card.getArea());
                        ed_summary.setText(card.getSummary());
                        ed_mail.setText(card.getEmail());
                        ed_job.setText(card.getJob());
                        ed_tel.setText(card.getTelephone());
                        ed_addr.setText(card.getAddress());
                        ed_web.setText(card.getWebsite());
                        ed_company.setText(card.getCompanyName());
                    }

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        };
        HuxinSdkManager.instance().getCardInfo(userId, srcPhone, desPhone, listener);

    }

    private void insertCardInfo(YouMaiBizCard.BizCard.Builder cardBulider) {
        int userId = HuxinSdkManager.instance().getUserId();
        String phone = HuxinSdkManager.instance().getPhoneNum();

        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiBizCard.BizCard_Insert_Ack ack = YouMaiBizCard.BizCard_Insert_Ack.parseFrom(pduBase.body);
                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        Toast.makeText(mAct, getString(R.string.hx_toast_35), Toast.LENGTH_SHORT).show();
                    }

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        };

        HuxinSdkManager.instance().insertCardInfo(userId, phone, cardBulider, callback);
    }

    private YouMaiBizCard.BizCard.Builder getCardBuilder() {

        int gender;
        if (rd_female.isChecked()) {
            gender = 0;
        } else {
            gender = 1;
        }

        String nickname = ed_nickname.getText().toString();
        String area = ed_area.getText().toString();
        String summary = ed_summary.getText().toString();

        String email = ed_mail.getText().toString();
        String job = ed_job.getText().toString();
        String tel = ed_tel.getText().toString();
        String addr = ed_addr.getText().toString();
        String web = ed_web.getText().toString();
        String com = ed_company.getText().toString();


        YouMaiBizCard.BizCard.Builder cardBuilder = YouMaiBizCard.BizCard.newBuilder();
        cardBuilder.setEmail(email);
        cardBuilder.setJob(job);
        cardBuilder.setTelephone(tel);
        cardBuilder.setAddress(addr);
        cardBuilder.setWebsite(web);
        cardBuilder.setCompanyName(com);
        cardBuilder.setNickname(nickname);
        cardBuilder.setGender(gender);
        cardBuilder.setArea(area);
        cardBuilder.setSummary(summary);
        try {
            int id = Integer.parseInt(AppUtils.getStringSharedPreferences(mAct, "name_image_fid", ""));
            cardBuilder.setCardFid(id);
        } catch (NumberFormatException e) {
            LogUtils.e(TAG, "类型转换异常:" + e.getMessage());
        }

        return cardBuilder;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_send) {
            if (checkInput()) {
                insertCardInfo(getCardBuilder());
            }
        } else if (id == R.id.ed_sendheadImage) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.hx_chat_fragment_choose_pic)), SELECT_IMAGE);
        } else if (id == R.id.btn_updateImage) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.hx_chat_fragment_choose_pic)), SELECT_NAMEIMAGE);
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

    /**
     * 登录接口
     *
     * @param phone    电话号码
     * @param userId   用户ID
     * @param imei     设备IMEI
     * @param session  用户session
     * @param callback 协议回调
     */
    public void login(String phone, int userId, String imei,
                      String session, ReceiveListener callback) {
        YouMaiUser.User_Login.Builder login = YouMaiUser.User_Login.newBuilder();
        login.setPhone(phone);
        login.setUserId(userId);
        login.setDeviceId(imei);
        login.setDeviceType(YouMaiBasic.Device_Type.DeviceType_Android);
        login.setSessionId(session);
        YouMaiUser.User_Login user_Login = login.build();
        HuxinSdkManager.instance().sendProto(user_Login, callback);
    }

    public boolean checkInput() {

        String email = ed_mail.getText().toString();
        String tel = ed_tel.getText().toString();
        if (!StringUtils.isEmpty(email) && !AppUtils.isEmail(email)) {      //邮箱格式不正确
            Toast.makeText(getActivity().getApplicationContext(), R.string.hx_get_card_fragment_email_error,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!StringUtils.isEmpty(tel) && !AppUtils.isTelephone(tel)) {        //座机格式不正确

            Toast.makeText(getActivity().getApplicationContext(), R.string.hx_get_card_fragment_special_plane_error,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_IMAGE: {
                try {
                    Uri uri = data.getData();
                    sendHeadImage(uri);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            }
            case SELECT_NAMEIMAGE: {
                try {
                    Uri uri = data.getData();
                    sendNameImage(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void updateNameImage(YouMaiBizCard.BizCard.Builder cardBulider) {
        int userId = HuxinSdkManager.instance().getUserId();
        String phone = HuxinSdkManager.instance().getPhoneNum();
        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiBizCard.BizCard_Update_Ack ack = YouMaiBizCard.BizCard_Update_Ack.parseFrom(pduBase.body);
                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        Toast.makeText(mAct, R.string.hx_get_card_fragment_update_pic_success, Toast.LENGTH_SHORT).show();
                    }

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        };
        HuxinSdkManager.instance().updateCardInfo(userId, phone, cardBulider, callback);
    }

    public void sendHeadImage(final Uri uri) {
        UpCompletionHandler completionHandler = new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                UploadFile resp = GsonUtil.parse(response.toString(), UploadFile.class);
                if (resp.isSucess()) {

                    String phone = HuxinSdkManager.instance().getPhoneNum();
                    String url = AppConfig.getThumbHeaderUrl(mAct, AppConfig.IMG_HEADER_W, AppConfig.IMG_HEADER_H, phone);

                    Glide.with(mAct).load(url)
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .placeholder(R.drawable.hx_icon_rd).circleCrop())
                            .into(img_headImage);
                }
            }
        };

        HuxinSdkManager.instance().sendHeadImage(uri, completionHandler, null);
    }


    /**
     * 上传名片照
     *
     * @param fileUri 上传的照片Uri
     */


    public void sendNameImage(final Uri fileUri) {
        UpCompletionHandler completionHandler = new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                UploadFile resp = GsonUtil.parse(response.toString(), UploadFile.class);
                if (resp.isSucess()) {
                    String fileid = resp.getD().getFileid();
                    AppUtils.setStringSharedPreferences(mAct, "name_image_fid", fileid);
                    updateNameImage(getNmaeImageCardBulider());
                    Glide.with(mAct).load(fileUri)
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .placeholder(R.drawable.hx_icon_rd))
                            .into(img_nameImage);
                }
            }
        };
        UpProgressHandler progressHandler = new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                int value = (int) percent;
                if (value == 0) {
                    ed_progressBar.setVisibility(View.VISIBLE);
                } else if (value == 100) {
                    ed_progressBar.setVisibility(View.GONE);
                } else {
                    ed_progressBar.setProgress(value);
                }
            }
        };

        HuxinSdkManager.instance().sendHeadImage(fileUri, completionHandler, progressHandler);
    }

    private YouMaiBizCard.BizCard.Builder getNmaeImageCardBulider() {
        YouMaiBizCard.BizCard.Builder cardBulider = YouMaiBizCard.BizCard.newBuilder();
        try {
            int id = Integer.parseInt(AppUtils.getStringSharedPreferences(mAct, "name_image_fid", ""));
            cardBulider.setCardFid(id);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return cardBulider;
    }


}
