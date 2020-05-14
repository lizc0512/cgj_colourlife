package com.tg.coloursteward.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsReaderView;
import com.tencent.smtt.sdk.ValueCallback;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.util.Tools;

import java.io.File;

public class OpenFileActivity extends BaseActivity implements TbsReaderView.ReaderCallback, ValueCallback<String> {

    private RelativeLayout rl_view;
    private TbsReaderView mTbsReaderView;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_file);
        filePath = getIntent().getStringExtra("filepath");
        rl_view = findViewById(R.id.rl_view);
        mTbsReaderView = new TbsReaderView(this, this);
        rl_view.addView(mTbsReaderView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //创建TbsReaderView需要的文件夹
        String bsReaderTemp = Environment.getExternalStorageDirectory().toString() + "/TbsReaderTemp";
        File bsReaderTempFile = new File(bsReaderTemp);
        if (!bsReaderTempFile.exists()) {
            bsReaderTempFile.mkdir();
        }
        File file = new File(filePath);
        Bundle bundle = new Bundle();
        bundle.putString("filePath", file.toString());
        bundle.putString("tempPath", Environment.getExternalStorageDirectory().toString() + "/" + "TbsReaderTemp");
        //查看文件格式是否支持
        boolean isOpen = mTbsReaderView.preOpen(parseFormat(file.toString()), false);
        if (isOpen) {
            //打开文件
            mTbsReaderView.openFile(bundle);
        } else {
            try {
                QbSdk.openFileReader(this, filePath, null, this);
            } catch (Exception e) {
                File fileLocal = new File(filePath);
                try {
                    Intent fileIntent = Tools.getFileIntent(fileLocal);
                    startActivity(fileIntent);
                    Intent.createChooser(fileIntent, "请选择对应的软件打开该附件！");
                } catch (ActivityNotFoundException a) {
                    ToastUtil.showShortToast(OpenFileActivity.this, "附件不能打开，请下载相关软件！");
                }
            }
        }
    }

    private String parseFormat(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //需要将预览服务停止，一定不要忘了
        if (null != mTbsReaderView) {
            mTbsReaderView.onStop();
        }
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }

    @Override
    public void onReceiveValue(String s) {

    }
}
