package com.tg.coloursteward;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.calendar.CalendarCard;
import com.tg.coloursteward.calendar.CalendarViewAdapter;
import com.tg.coloursteward.calendar.CustomDate;
import com.tg.coloursteward.view.ActivityHeaderView;
import com.tg.coloursteward.view.dialog.ToastFactory;

/**打卡月历
 * Created by prince70 on 2018/3/29.
 */

public class SignInCalendarActivity extends BaseActivity implements View.OnClickListener, CalendarCard.OnCellClickListener {
    private ActivityHeaderView headerView;
    protected ViewPager mViewPager;
    protected int mCurrentIndex = 498;
    protected CalendarCard[] mShowViews;
    protected CalendarViewAdapter<CalendarCard> adapter;
    protected SildeDirection mDirection = SildeDirection.NO_SILDE;
    protected enum SildeDirection {
        RIGHT, LEFT, NO_SILDE;
    }

    protected ImageButton preImgBtn, nextImgBtn;
    protected TextView monthText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_calendar);
        initView();
    }

    private void initView() {
        headerView= (ActivityHeaderView) findViewById(R.id.header_calendar);
        headerView.setTitle("打卡月历");
        headerView.setRightText("统计");
        headerView.setRightTextColor(Color.WHITE);
        headerView.setListenerRight(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastFactory.showToast(SignInCalendarActivity.this, "统计结果");
            }
        });

        mViewPager = (ViewPager) this.findViewById(R.id.vp_calendar);
        preImgBtn = (ImageButton) this.findViewById(R.id.btnPreMonth);
        nextImgBtn = (ImageButton) this.findViewById(R.id.btnNextMonth);
        monthText = (TextView) this.findViewById(R.id.tvCurrentMonth);
//        closeImgBtn = (ImageButton) this.findViewById(R.id.btnClose);
        preImgBtn.setOnClickListener(this);
        nextImgBtn.setOnClickListener(this);
//        closeImgBtn.setOnClickListener(this);

        CalendarCard[] views = new CalendarCard[3];
        for (int i = 0; i < 3; i++) {
            views[i] = new CalendarCard(this, this);
        }
        adapter = new CalendarViewAdapter<>(views);
        setViewPager();
    }

    protected void setViewPager() {
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(498);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                measureDirection(position);
                updateCalendarView(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    /**
     * 计算方向
     *
     * @param arg0
     */
    private void measureDirection(int arg0) {

        if (arg0 > mCurrentIndex) {
            mDirection = SildeDirection.RIGHT;

        } else if (arg0 < mCurrentIndex) {
            mDirection = SildeDirection.LEFT;
        }
        mCurrentIndex = arg0;
    }

    // 更新日历视图
    private void updateCalendarView(int arg0) {
        mShowViews = adapter.getAllItems();
        if (mDirection == SildeDirection.RIGHT) {
            mShowViews[arg0 % mShowViews.length].rightSlide();
        } else if (mDirection == SildeDirection.LEFT) {
            mShowViews[arg0 % mShowViews.length].leftSlide();
        }
        mDirection = SildeDirection.NO_SILDE;
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPreMonth:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                break;
            case R.id.btnNextMonth:
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                break;
            default:
                break;
        }
    }

    @Override
    public void clickDate(CustomDate date) {
        Toast.makeText(this, ""+date.day,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void changeDate(CustomDate date) {
        monthText.setText(date.year+"年 "+date.month + "月");
    }
}
