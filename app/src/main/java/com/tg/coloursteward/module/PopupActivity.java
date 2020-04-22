package com.tg.coloursteward.module;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.ImagePagerAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.util.LinkParseUtil;
import com.tg.user.view.CirclePageIndicator;

import java.util.List;

/**
 * 作者: zhudewei
 * 时间： 2016/12/18 09:16
 * God bless the program never bug
 */

public class PopupActivity extends BaseActivity {

    private ViewPager viewpager;
    private ImageView btn1;
    private LinearLayout llContainer;
    private LinearLayout llRoot;
    private FrameLayout fl_indicator;
    private CirclePageIndicator indicator;


    private List<String> imageList;
    private List<String> urlList;
    private List<String> nameList;
    private boolean isShowpop = false;
    private SharedPreferences mShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        btn1 = (ImageView) findViewById(R.id.btn1);
        llContainer = (LinearLayout) findViewById(R.id.ll_container);
        llRoot = (LinearLayout) findViewById(R.id.ll_all_container);
        fl_indicator = (FrameLayout) findViewById(R.id.fl_indicator);
        indicator = (CirclePageIndicator) findViewById(R.id.indicator);

        getIntentData();
        initEnterAnim(-500);
        iniData();
        initListener();
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }

    private void getIntentData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        urlList = bundle.getStringArrayList("urldata");
        imageList = bundle.getStringArrayList("imagedata");
        nameList = bundle.getStringArrayList("namedata");

    }

    private void iniData() {
        if (null != urlList && urlList.size() > 1) {
            fl_indicator.setVisibility(View.VISIBLE);
        } else {
            fl_indicator.setVisibility(View.INVISIBLE);
        }

        ImagePagerAdapter adapter = new ImagePagerAdapter(this, imageList, urlList, nameList);
        viewpager.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.setOnImgClickListener(new ImagePagerAdapter.OnImgClickListener() {
            @Override
            public void OnImgClick(int pos, String url, String name) {
                if (url != null) {
                    LinkParseUtil.parse(PopupActivity.this, url, name);
                    PopupActivity.this.finish();
                }
            }
        });
    }

    private void initListener() {
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initExitAnim(0f);
            }
        });

        indicator.setViewPager(viewpager, 0);
        indicator.requestLayout();
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                indicator.setCurrentItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        llRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                initExitAnim(0f);
                return false;
            }
        });
    }

    private void initEnterAnim(double values) {
        Spring spring = getSpring();
        spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(50, 6));
        spring.setCurrentValue(values);
        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                super.onSpringUpdate(spring);
                llContainer.setTranslationY((float) spring.getCurrentValue());
            }

            @Override
            public void onSpringAtRest(Spring spring) {
                super.onSpringAtRest(spring);
                isShowpop = true;
            }
        });
        spring.setEndValue(1f);
    }

    private void initExitAnim(double values) {
        if (isShowpop) {
            finishThis();
        }
    }

    private void finishThis() {
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(llRoot, "translationY", 0f, 2000f);
        animator.setInterpolator(new AnticipateOvershootInterpolator());
        animator.setDuration(300).start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                finish();
                overridePendingTransition(0, -1);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            initExitAnim(0f);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private Spring getSpring() {
        SpringSystem system = SpringSystem.create();
        return system.createSpring();
    }
}
