package com.tg.setting.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.tg.coloursteward.R;
import com.tg.setting.activity.KeySendKeyPhoneActivity;
import com.tg.setting.activity.KeySendKeyQrCodeActivity;
import com.tg.setting.adapter.KeyTimeHorAdapter;
import com.tg.setting.adapter.KeyTimeVerAdapter;
import com.tg.setting.entity.KeyPopEntity;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.youmai.hxsdk.view.chat.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 乐开-时间
 * hxg 2019.07.26
 */
public class KeyTimePopWindowView extends PopupWindow {
    private View contentView;
    private Activity context;
    private RelativeLayout rl_pop;
    private SwipeRecyclerView rv_horizontal;
    private SwipeRecyclerView rv_vertical;
    private KeyTimeHorAdapter hAdapter;
    private KeyTimeVerAdapter vAdapter;

    private int type = 1;//1年，2月，3日，4时，5分
    private boolean month29 = false;//true闰年
    private int inputText;

    private int y = 0;
    private int m = 0;
    private int d = 0;
    private int h = 0;
    private int mi = 0;

    public KeyTimePopWindowView(final Activity context, int inputText) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.view_key_pop_time, null);
        this.inputText = inputText;
        initWindow();
        initView();
        initListener();
    }

    private void initListener() {

    }

    private void initWindow() {
        // 设置SelectPicPopupWindow的View
        this.setContentView(contentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(DisplayUtils.dp2px(context, 344));
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));
        // 设置SelectPicPopupWindow弹出窗体动画效果
        setWindowBackgroundAlpha();
    }

    /**
     * 设置背景
     */
    private void setWindowBackgroundAlpha() {
        if (context != null) {
            Window window = context.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.alpha = 0.5f;
            window.setAttributes(layoutParams);
        }
    }

    private List<String> hList = new ArrayList<>();

    private void initView() {
        rl_pop = contentView.findViewById(R.id.rl_pop);
        rv_horizontal = contentView.findViewById(R.id.rv_horizontal);
        rv_vertical = contentView.findViewById(R.id.rv_vertical);

        rv_horizontal.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        hAdapter = new KeyTimeHorAdapter(context, hList, this);
        rv_horizontal.setAdapter(hAdapter);

        List<KeyPopEntity> defaultYearList = new ArrayList<>();
        for (int i = 2019; i <= 2029; i++) {
            KeyPopEntity entity = new KeyPopEntity();
            entity.setSelect(false);
            entity.setName(i + "");
            defaultYearList.add(entity);
        }

        rv_vertical.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        vAdapter = new KeyTimeVerAdapter(context, defaultYearList, this);
        rv_vertical.setAdapter(vAdapter);
    }

    public void selectVer(int position) {
        int year = 0;
        String yearString = "";
        int month;
        String monthString = "";
        int day;
        String dayString = "";
        int hour;
        String hourString = "";
        int minute;
        String minuteString = "";
        switch (type) {
            case 1://年
                try {
                    type = 2;
                    try {
                        year = Integer.parseInt(vAdapter.list.get(position).getName());
                        month29 = year % 4 == 0;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    vAdapter.list.clear();
                    int start = 1;
                    if (year == getStartTime(1)) {
                        start = getStartTime(2);
                    }
                    for (int i = start; i <= 12; i++) {
                        KeyPopEntity entity = new KeyPopEntity();
                        entity.setName(i + "月");
                        entity.setSelect(false);
                        vAdapter.list.add(entity);
                    }
                    vAdapter.notifyDataSetChanged();

                    hAdapter.list.clear();
                    hAdapter.list.add(year + "");
                    y = year;
                    hAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 2://月
                try {
                    type = 3;
                    monthString = vAdapter.list.get(position).getName();
                    m = Integer.parseInt(monthString.split("月")[0]);
                    vAdapter.list.clear();
                    int start = 1;
                    if (y == getStartTime(1) && m == getStartTime(2)) {
                        start = getStartTime(3);
                    }
                    switch (monthString) {
                        case "1月":
                            for (day = start; day <= 31; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setSelect(false);
                                entity.setName(day + "日");
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "2月":
                            for (day = start; day <= 29; day++) {
                                if (!month29 && 29 == day) {
                                    break;
                                }
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setSelect(false);
                                entity.setName(day + "日");
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "3月":
                            for (day = start; day <= 31; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setSelect(false);
                                entity.setName(day + "日");
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "4月":
                            for (day = start; day <= 30; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setSelect(false);
                                entity.setName(day + "日");
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "5月":
                            for (day = start; day <= 31; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setSelect(false);
                                entity.setName(day + "日");
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "6月":
                            for (day = start; day <= 30; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setSelect(false);
                                entity.setName(day + "日");
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "7月":
                            for (day = start; day <= 31; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setSelect(false);
                                entity.setName(day + "日");
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "8月":
                            for (day = start; day <= 31; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setSelect(false);
                                entity.setName(day + "日");
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "9月":
                            for (day = start; day <= 30; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setSelect(false);
                                entity.setName(day + "日");
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "10月":
                            for (day = start; day <= 31; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setSelect(false);
                                entity.setName(day + "日");
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "11月":
                            for (day = start; day <= 30; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setSelect(false);
                                entity.setName(day + "日");
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "12月":
                            for (day = start; day <= 31; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setSelect(false);
                                entity.setName(day + "日");
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                    }

                    if (hAdapter.list.size() > 0) {
                        yearString = hAdapter.list.get(0);
                        hAdapter.list.clear();
                        hAdapter.list.add(yearString);
                        hAdapter.list.add(monthString);
                        hAdapter.notifyDataSetChanged();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            case 3://日
                try {
                    type = 4;
                    dayString = vAdapter.list.get(position).getName();
                    d = Integer.parseInt(dayString.split("日")[0]);
                    vAdapter.list.clear();
                    int start = 0;
                    if (y == getStartTime(1) && m == getStartTime(2) && d == getStartTime(3)) {
                        start = getStartTime(4);
                    }
                    for (hour = start; hour <= 23; hour++) {
                        KeyPopEntity entity = new KeyPopEntity();
                        entity.setSelect(false);
                        entity.setName(hour + "点");
                        vAdapter.list.add(entity);
                    }
                    vAdapter.notifyDataSetChanged();

                    if (hAdapter.list.size() > 1) {
                        yearString = hAdapter.list.get(0);
                        monthString = hAdapter.list.get(1);
                        hAdapter.list.clear();
                        hAdapter.list.add(yearString);
                        hAdapter.list.add(monthString);
                        hAdapter.list.add(dayString);
                        hAdapter.notifyDataSetChanged();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            case 4://时
                try {
                    type = 5;
                    hourString = vAdapter.list.get(position).getName();
                    h = Integer.parseInt(hourString.split("点")[0]);
                    vAdapter.list.clear();
                    int start = 0;
                    if (y == getStartTime(1) && m == getStartTime(2) && d == getStartTime(3) && h == getStartTime(4)) {
                        start = getStartTime(5);
                    }
                    for (minute = start; minute <= 59; minute++) {
                        KeyPopEntity entity = new KeyPopEntity();
                        entity.setSelect(false);
                        entity.setName(minute + "分");
                        vAdapter.list.add(entity);
                    }
                    vAdapter.notifyDataSetChanged();

                    if (hAdapter.list.size() > 2) {
                        yearString = hAdapter.list.get(0);
                        monthString = hAdapter.list.get(1);
                        dayString = hAdapter.list.get(2);
                        hAdapter.list.clear();
                        hAdapter.list.add(yearString);
                        hAdapter.list.add(monthString);
                        hAdapter.list.add(dayString);
                        hAdapter.list.add(hourString);
                        hAdapter.notifyDataSetChanged();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            case 5://分
                try {
                    dismiss();
                    minuteString = vAdapter.list.get(position).getName();
                    mi = Integer.parseInt(minuteString.split("分")[0]);
                    vAdapter.list.clear();
                    if (hAdapter.list.size() > 3) {
                        yearString = hAdapter.list.get(0);
                        monthString = hAdapter.list.get(1);
                        dayString = hAdapter.list.get(2);
                        hourString = hAdapter.list.get(3);
                    }
                    String time = yearString + "年 " + monthString + dayString + " " + hourString + minuteString;
                    if (context instanceof  KeySendKeyPhoneActivity){
                        ((KeySendKeyPhoneActivity) context).setTimeText(inputText, time, y, m, d, h, mi);
                    }else if (context instanceof KeySendKeyQrCodeActivity){
                        ((KeySendKeyQrCodeActivity) context).setTimeText(inputText, time, y, m, d, h, mi);
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
        }
        rv_vertical.scrollToPosition(0);
    }

    public void selectHor(int position) {
        type = position + 1;
        int year = 0;
        String yearString;
        String monthString = "";
        int day;
        String dayString;
        int hour;
        String hourString;
        switch (type) {
            case 1://年
                year = Integer.parseInt(hAdapter.list.get(0));
                List<KeyPopEntity> defaultYearList = new ArrayList<>();
                for (int i = 2019; i <= 2029; i++) {
                    KeyPopEntity entity = new KeyPopEntity();
                    entity.setName(i + "");
                    entity.setSelect(year == i);
                    defaultYearList.add(entity);
                }
                vAdapter.list.clear();
                vAdapter.list.addAll(defaultYearList);
                vAdapter.notifyDataSetChanged();
                hAdapter.list.clear();
                hAdapter.notifyDataSetChanged();
                break;
            case 2://月
                try {
                    try {
                        monthString = hAdapter.list.get(1);
                        year = Integer.parseInt(hAdapter.list.get(0));
                        month29 = year % 4 == 0;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    vAdapter.list.clear();
                    int start = 1;
                    if (year == getStartTime(1)) {
                        start = getStartTime(2);
                    }
                    for (int i = start; i <= 12; i++) {
                        KeyPopEntity entity = new KeyPopEntity();
                        entity.setName(i + "月");
                        entity.setSelect(monthString.equals(i + "月"));
                        vAdapter.list.add(entity);
                    }
                    vAdapter.notifyDataSetChanged();

                    hAdapter.list.clear();
                    hAdapter.list.add(year + "");
                    hAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 3://日
                try {
                    dayString = hAdapter.list.get(2);
                    monthString = hAdapter.list.get(1);
                    vAdapter.list.clear();
                    int start = 1;
                    if (y == getStartTime(1) && m == getStartTime(2)) {
                        start = getStartTime(3);
                    }
                    switch (monthString) {
                        case "1月":
                            for (day = start; day <= 31; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setName(day + "日");
                                entity.setSelect(dayString.equals(day + "日"));
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "2月":
                            for (day = start; day <= 29; day++) {
                                if (!month29 && 29 == day) {
                                    break;
                                }
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setName(day + "日");
                                entity.setSelect(dayString.equals(day + "日"));
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "3月":
                            for (day = start; day <= 31; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setName(day + "日");
                                entity.setSelect(dayString.equals(day + "日"));
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "4月":
                            for (day = start; day <= 30; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setName(day + "日");
                                entity.setSelect(dayString.equals(day + "日"));
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "5月":
                            for (day = start; day <= 31; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setName(day + "日");
                                entity.setSelect(dayString.equals(day + "日"));
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "6月":
                            for (day = start; day <= 30; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setName(day + "日");
                                entity.setSelect(dayString.equals(day + "日"));
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "7月":
                            for (day = start; day <= 31; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setName(day + "日");
                                entity.setSelect(dayString.equals(day + "日"));
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "8月":
                            for (day = start; day <= 31; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setName(day + "日");
                                entity.setSelect(dayString.equals(day + "日"));
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "9月":
                            for (day = start; day <= 30; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setName(day + "日");
                                entity.setSelect(dayString.equals(day + "日"));
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "10月":
                            for (day = start; day <= 31; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setName(day + "日");
                                entity.setSelect(dayString.equals(day + "日"));
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "11月":
                            for (day = start; day <= 30; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setName(day + "日");
                                entity.setSelect(dayString.equals(day + "日"));
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                        case "12月":
                            for (day = start; day <= 31; day++) {
                                KeyPopEntity entity = new KeyPopEntity();
                                entity.setName(day + "日");
                                entity.setSelect(dayString.equals(day + "日"));
                                vAdapter.list.add(entity);
                            }
                            vAdapter.notifyDataSetChanged();
                            break;
                    }

                    if (hAdapter.list.size() > 0) {
                        yearString = hAdapter.list.get(0);
                        hAdapter.list.clear();
                        hAdapter.list.add(yearString);
                        hAdapter.list.add(monthString);
                        hAdapter.notifyDataSetChanged();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            case 4://时
                try {
                    hourString = hAdapter.list.get(3);
                    dayString = hAdapter.list.get(2);
                    vAdapter.list.clear();
                    int start = 0;
                    if (y == getStartTime(1) && m == getStartTime(2) && d == getStartTime(3)) {
                        start = getStartTime(4);
                    }
                    for (hour = start; hour <= 23; hour++) {
                        KeyPopEntity entity = new KeyPopEntity();
                        entity.setName(hour + "点");
                        entity.setSelect(hourString.equals(hour + "点"));
                        vAdapter.list.add(entity);
                    }
                    vAdapter.notifyDataSetChanged();

                    if (hAdapter.list.size() > 1) {
                        yearString = hAdapter.list.get(0);
                        monthString = hAdapter.list.get(1);
                        hAdapter.list.clear();
                        hAdapter.list.add(yearString);
                        hAdapter.list.add(monthString);
                        hAdapter.list.add(dayString);
                        hAdapter.notifyDataSetChanged();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private int getStartTime(int type) {
        int num = 0;
        Calendar nowCalender = Calendar.getInstance();
        switch (type) {
            case 1:
                num = nowCalender.get(Calendar.YEAR);
                break;
            case 2:
                num = nowCalender.get(Calendar.MONTH) + 1;
                break;
            case 3:
                num = nowCalender.get(Calendar.DAY_OF_MONTH);
                break;
            case 4:
                num = nowCalender.get(Calendar.HOUR_OF_DAY);
                break;
            case 5:
                num = nowCalender.get(Calendar.MINUTE);
                break;
        }
        return num;
    }

    /**
     * 显示popupWindow
     **/
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        } else {
            this.dismiss();
        }
    }

}