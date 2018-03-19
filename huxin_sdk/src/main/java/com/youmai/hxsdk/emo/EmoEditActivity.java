package com.youmai.hxsdk.emo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.dialog.HxCardDialog;
import com.youmai.hxsdk.entity.EmoItem;
import com.youmai.hxsdk.module.picker.model.GridSpacingItemDecoration;

import java.util.List;

/**
 * Created by colin on 2017/6/8.
 */

public class EmoEditActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_CODE_EMO = 1000;

    private Context mContext;

    private ImageView img_back;
    private TextView tv_title;
    private TextView tv_confirm;


    private RecyclerView recyclerView;

    private LinearLayout linear_bar;
    private TextView tv_move_first;
    private TextView tv_del;

    private List<EmoItem> emoList;
    private EmoEditAdapter mAdapter;

    private boolean isAdd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emo_edit);
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

        linear_bar = (LinearLayout) findViewById(R.id.linear_bar);

        tv_move_first = (TextView) findViewById(R.id.tv_move_first);
        tv_move_first.setOnClickListener(this);

        tv_del = (TextView) findViewById(R.id.tv_del);
        tv_del.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager imageGridManger = new GridLayoutManager(this, EmoEditAdapter.GIRD_COUNT);
        recyclerView.setLayoutManager(imageGridManger);

        GridSpacingItemDecoration itemDecoration = new GridSpacingItemDecoration(EmoEditAdapter.GIRD_COUNT, EmoEditAdapter.DRI_WIDTH, true);
        recyclerView.addItemDecoration(itemDecoration);

        emoList = HuxinSdkManager.instance().getEmoList();

        mAdapter = new EmoEditAdapter(this, emoList);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setRefreshListener(new EmoEditAdapter.IRefreshEmoListener() {
            @Override
            public void onRefresh() {
                isAdd = true;
            }

            @Override
            public void onItemNull() {
                tv_move_first.setEnabled(false);
                tv_del.setEnabled(false);
            }
        });
    }


    private void editIsClick(TextView textView) {
        if (mAdapter.isEdit()) {
            linear_bar.setVisibility(View.GONE);
            mAdapter.editModel(false);
            textView.setText("整理");
        } else {
            linear_bar.setVisibility(View.VISIBLE);
            mAdapter.editModel(true);
            textView.setText("完成");
        }
    }

    public void checkChange() {
        boolean isSelect = false;

        List<EmoItem> list = mAdapter.getDataList();
        for (EmoItem item : list) {
            if (item.isCheck()) {
                isSelect = true;
                break;
            }
        }

        if (isSelect) {
            tv_move_first.setEnabled(true);
            tv_del.setEnabled(true);
            //tv_move_first.setTextColor(ContextCompat.getColor(this, R.color.hxs_color_blue1));
            //tv_del.setTextColor(ContextCompat.getColor(this, R.color.hxs_color_red1));
        } else {
            tv_move_first.setEnabled(false);
            tv_del.setEnabled(false);
            //tv_move_first.setTextColor(ContextCompat.getColor(this, R.color.hxs_color_blue1));
            //tv_del.setTextColor(ContextCompat.getColor(this, R.color.hxs_color_red1));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_EMO && resultCode == Activity.RESULT_OK) {
            isAdd = true;
            emoList = HuxinSdkManager.instance().getEmoList();
            mAdapter.setList(emoList);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_confirm) {
            editIsClick(tv_confirm);
        } else if (id == R.id.tv_move_first) {
            mAdapter.moveToFirst();
        } else if (id == R.id.tv_del) {
            HxCardDialog.Builder builder = new HxCardDialog.Builder(this)
                    .setOutSide(false)
                    .setTitle("提示")
                    .setContent("表情删除后不可恢复，是否删除?")
                    .setSubmit("删除", R.color.hxs_color_red2)
                    .setCancel("取消")
                    .setOnListener(new HxCardDialog.OnClickListener() {
                        @Override
                        public void onSubmitClick(DialogInterface dialog) {
                            mAdapter.emoDel();
                        }

                        @Override
                        public void onCancelClick(DialogInterface dialog) {

                        }
                    });
            builder.create().show();
        }
    }

    @Override
    public void onBackPressed() {
        if (isAdd) {
            setResult(Activity.RESULT_OK);
        }
        finish();
    }
}
