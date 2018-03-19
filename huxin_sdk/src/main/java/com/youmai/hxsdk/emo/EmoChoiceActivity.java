package com.youmai.hxsdk.emo;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.module.picker.loader.LocalImageLoader;
import com.youmai.hxsdk.module.picker.model.GridSpacingItemDecoration;
import com.youmai.hxsdk.module.picker.model.LocalImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class EmoChoiceActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int LOADER_IMAGE_ID = 1000;
    public static final int REQUEST_CODE_PHOTO = 233;

    public final static int GIRD_COUNT = 4;
    public static final int DRI_WIDTH = 10;

    public static final String ALL_IMAGE_KEY = "最近照片";


    private Context mContext;

    private ImageView img_back;
    private TextView tv_title;
    private TextView tv_cancel;


    private RecyclerView recyclerView;

    private EmoChoiceAdapter mAdapter;

    private ProgressDialog mProgressDialog;

    private boolean isAdd;

    private LoaderManager.LoaderCallbacks imageLoader = new LoaderManager.LoaderCallbacks<HashMap<String, ArrayList<LocalImage>>>() {
        @Override
        public Loader<HashMap<String, ArrayList<LocalImage>>> onCreateLoader(int id, Bundle args) {
            showProgress("", "图片加载中...", -1);
            return new LocalImageLoader(mContext);
        }

        @Override
        public void onLoadFinished(Loader<HashMap<String, ArrayList<LocalImage>>> loader, HashMap<String, ArrayList<LocalImage>> data) {
            if (data == null) {
                return;
            }

            List<LocalImage> dataList = data.get(ALL_IMAGE_KEY);

            if (dataList != null) {
                if (mAdapter == null) {
                    mAdapter = new EmoChoiceAdapter(mContext, dataList);
                    recyclerView.setAdapter(mAdapter);
                    tv_title.setText(ALL_IMAGE_KEY);
                }
            }

            hideProgress();

        }

        @Override
        public void onLoaderReset(Loader<HashMap<String, ArrayList<LocalImage>>> loader) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emo_choice);
        mContext = this;

        initView();
        initData();

        HuxinSdkManager.instance().getStackAct().addActivity(this);

    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(ALL_IMAGE_KEY);

        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        img_back = (ImageView) findViewById(R.id.img_back);

        tv_cancel.setOnClickListener(this);
        img_back.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager imageGridManger = new GridLayoutManager(this, GIRD_COUNT);
        recyclerView.setLayoutManager(imageGridManger);

        GridSpacingItemDecoration itemDecoration = new GridSpacingItemDecoration(GIRD_COUNT, DRI_WIDTH, true);
        recyclerView.addItemDecoration(itemDecoration);

    }


    private void initData() {
        getLoaderManager().initLoader(LOADER_IMAGE_ID, null, imageLoader);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.img_back) {
            onBackPressed();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            isAdd = true;
        }
    }

    @Override
    public void onBackPressed() {
        if (isAdd) {
            setResult(Activity.RESULT_OK);
        }
        finish();
    }


    @Override
    protected void onDestroy() {
        HuxinSdkManager.instance().getStackAct().finishActivity(this);
        super.onDestroy();
    }


    private void showProgress(String title, String message, int theme) {
        if (mProgressDialog == null) {
            if (theme > 0)
                mProgressDialog = new ProgressDialog(this, theme);
            else
                mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
        }

        if (!com.youmai.smallvideorecord.utils.StringUtils.isEmpty(title))
            mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    private void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

}
