package com.youmai.hxsdk.emo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.entity.EmoItem;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.interfaces.OnFileListener;
import com.youmai.hxsdk.utils.CompressImage;
import com.youmai.hxsdk.utils.GsonUtil;

import java.util.Collections;
import java.util.List;

/**
 * Created by colin on 2017/6/8.
 */

public class EmoPreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;

    private ImageView img_back;
    private TextView tv_title;
    private TextView tv_confirm;

    private ImageView img_content;

    private String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emo_preview);
        mContext = this;
        initView();
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(this);

        img_content = (ImageView) findViewById(R.id.img_content);


        url = getIntent().getStringExtra("img_path");

        if (url.endsWith(".gif")) {
            Glide.with(mContext)
                    .asGif()
                    .load(url)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .fitCenter())
                    .into(img_content);
        } else {
            Glide.with(mContext)
                    .load(url)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .fitCenter()
                            .error(R.drawable.hx_icon_rd))
                    .into(img_content);
        }

    }

    private void setEmoPicture(final String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("正在设置，请稍后...");
        dialog.show();

        boolean isGifTemp = false;
        String temp = path;

        if (!path.endsWith(".gif")) {
            temp = CompressImage.compressImage(path);
        } else {
            isGifTemp = true;
        }

        final boolean isGif = isGifTemp;

        HuxinSdkManager.instance().postFile(temp, "4", new OnFileListener() {
            @Override
            public void onProgress(double progress) {

            }

            @Override
            public void onSuccess(String fid) {
                String phoneNum = HuxinSdkManager.instance().getPhoneNum();

                final List<EmoItem> list = HuxinSdkManager.instance().getEmoList();

                EmoItem item = new EmoItem();

                item.setFid(fid);

                if (list == null) {
                    item.setRank(0);
                } else {
                    item.setRank(list.size());
                }

                if (isGif) {
                    item.setIsGif(1);
                }

                list.add(item);

                Collections.sort(list);

                String content = GsonUtil.format(list);

                HuxinSdkManager.instance().userInfoSave(phoneNum, 1, content, new IPostListener() {
                    @Override
                    public void httpReqResult(String response) {
                        RespBaseBean baseBean = GsonUtil.parse(response, RespBaseBean.class);
                        if (baseBean != null && baseBean.isSuccess()) {
                            Toast.makeText(mContext, "设置表情成功", Toast.LENGTH_SHORT).show();

                            HuxinSdkManager.instance().setEmoList(list);

                            dialog.dismiss();

                            setResult(Activity.RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(mContext, "设置表情失败，请重试", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onFail(String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "设置表情失败，请重试", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_confirm) {
            setEmoPicture(url);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
