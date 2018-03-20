package com.youmai.hxsdk;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.adapter.MessageAdapter;
import com.youmai.hxsdk.chat.ContentText;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.db.bean.ChatMsg;
import com.youmai.hxsdk.db.dao.ChatMsgDao;
import com.youmai.hxsdk.db.manager.GreenDbManager;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2016/7/19.
 */
public class MessageActivity extends Activity {

    private ChatMsgDao mChatMsgDao;


    private RelativeLayout rel_content;
    private GifImageView img_gif;
    private ViewPager mViewPager;


    private List<View> mListViews = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnTouchOutside(true);
        setContentView(R.layout.hx_activity_message);


        rel_content = (RelativeLayout) findViewById(R.id.rel_content);
        img_gif = (GifImageView) findViewById(R.id.img_gif);


        ChatMsg msg = getIntent().getParcelableExtra("ChatMsg");

        mChatMsgDao = GreenDbManager.instance(this).getChatMsgDao();
        mChatMsgDao.insertOrReplace(msg);

        //读数据库数据
        /*mChatMsgDao.startReadableDatabase();
        List<ChatMsg> list = mChatMsgDao.queryList(" targetPhone=?",
                new String[]{"18664923439"});
        mChatMsgDao.closeDatabase();*/

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setPageMargin(60);
        init(msg);
    }


    private void init(ChatMsg msg) {
        switch (msg.getMsgType()) {
            case TEXT:
            break;
            case PICTURE: {
                rel_content.setVisibility(View.VISIBLE);
                img_gif.setVisibility(View.GONE);

                String fid = msg.getMsgContent().getPicture().getPicUrl();
                String url = AppConfig.getImageUrl(this, fid);
                ImageView imageView = new ImageView(this);

                Glide.with(this)
                        .load(url)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).centerCrop())
                        .into(imageView);
                mListViews.add(imageView);
            }
            break;
            case AUDIO:
                break;
            case VIDEO:
                break;
            case URL:
            case LOCATION:
            case BEGIN_LOCATION:
                break;
        }


        ImageView view2 = new ImageView(this);
        view2.setImageResource(R.drawable.hx_float_icon);
        mListViews.add(view2);


        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.hx_video_item, mViewPager, false);
        mListViews.add(view);

        MessageAdapter adapter = new MessageAdapter(mListViews);
        mViewPager.setAdapter(adapter);

    }
}
