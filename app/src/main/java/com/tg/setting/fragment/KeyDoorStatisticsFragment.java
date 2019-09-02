package com.tg.setting.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tg.coloursteward.R;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.setting.adapter.KeyDoorStatisticsAdapter;
import com.tg.setting.entity.KeyTotalDataEntity;
import com.tg.setting.entity.KeyTypeDataEntity;
import com.tg.setting.model.KeyDoorModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

import static com.tg.setting.activity.KeySendKeyListActivity.COMMUNITY_NAME;
import static com.tg.setting.activity.KeySendKeyListActivity.COMMUNITY_UUID;

public class KeyDoorStatisticsFragment extends Fragment implements HttpResponse, View.OnClickListener {


    private KeyDoorModel keyDoorModel;
    private String community_uuid;
    private String community_name;

    private RecyclerView rv_statistic;


    /****用户数的统计**/
    private TextView tv_user_month;
    private TextView tv_user_week;
    private TextView tv_user_day;
    private LineChartView usr_linechart;

    /****开锁次数的统计**/
    private TextView tv_lock_month;
    private TextView tv_lock_week;
    private TextView tv_lock_day;
    private LineChartView lock_linechart;

    /****活跃次数的统计**/
    private TextView tv_key_month;
    private TextView tv_key_week;
    private TextView tv_key_day;
    private LineChartView key_linechart;


    private KeyDoorStatisticsAdapter keyDoorStatisticsAdapter;

    public List<String> mNameList = new ArrayList<>();
    public List<String> mValuesList = new ArrayList<>();


    public static KeyDoorStatisticsFragment getKeyStatisticsFragment(String community_uuid, String community_name) {
        KeyDoorStatisticsFragment keyDoorStatisticsFragment = new KeyDoorStatisticsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(COMMUNITY_UUID, community_uuid);
        bundle.putString(COMMUNITY_NAME, community_name);
        keyDoorStatisticsFragment.setArguments(bundle);
        return keyDoorStatisticsFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_key_door_statistics, container, false);
        rv_statistic = rootView.findViewById(R.id.rv_statistic);
        tv_user_month = rootView.findViewById(R.id.tv_user_month);
        tv_user_week = rootView.findViewById(R.id.tv_user_week);
        tv_user_day = rootView.findViewById(R.id.tv_user_day);
        usr_linechart = rootView.findViewById(R.id.usr_linechart);
        tv_user_month.setOnClickListener(this::onClick);
        tv_user_week.setOnClickListener(this::onClick);
        tv_user_day.setOnClickListener(this::onClick);
        tv_lock_month = rootView.findViewById(R.id.tv_lock_month);
        tv_lock_week = rootView.findViewById(R.id.tv_lock_week);
        tv_lock_day = rootView.findViewById(R.id.tv_lock_day);
        lock_linechart = rootView.findViewById(R.id.lock_linechart);
        tv_lock_month.setOnClickListener(this::onClick);
        tv_lock_week.setOnClickListener(this::onClick);
        tv_lock_day.setOnClickListener(this::onClick);
        tv_key_month = rootView.findViewById(R.id.tv_key_month);
        tv_key_week = rootView.findViewById(R.id.tv_key_week);
        tv_key_day = rootView.findViewById(R.id.tv_key_day);
        key_linechart = rootView.findViewById(R.id.key_linechart);
        tv_key_month.setOnClickListener(this::onClick);
        tv_key_week.setOnClickListener(this::onClick);
        tv_key_day.setOnClickListener(this::onClick);
        Bundle bundle = getArguments();
        community_uuid = bundle.getString(COMMUNITY_UUID);
        community_name = bundle.getString(COMMUNITY_NAME);
        mNameList.add("用户数");
        mNameList.add("钥匙数");
        mNameList.add("锁总数");
        mNameList.add("开锁次数");
        keyDoorStatisticsAdapter = new KeyDoorStatisticsAdapter(getActivity(), mNameList, mValuesList);
        rv_statistic.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rv_statistic.setNestedScrollingEnabled(false);
        rv_statistic.setAdapter(keyDoorStatisticsAdapter);
        getDoorStatis();
        return rootView;
    }


    private void initLineChart(List<String> date, List<String> score, LineChartView lineChartView) {
        List<PointValue> mPointValues = new ArrayList<PointValue>();
        List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
        for (int i = 0; i < date.size(); i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(date.get(i)));
        }

        for (int i = 0; i < score.size(); i++) {
            // 构造函数传参 位置 值
            String value = score.get(i);
            try {
                mPointValues.add(new PointValue(i, Float.valueOf(value)));
            } catch (Exception e) {
                mPointValues.add(new PointValue(i, 0));
            }
        }

        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));  //折线的颜色（橙色）
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setStrokeWidth(1);
        line.setPointRadius(2);
        line.setFilled(true);//是否填充曲线的面积
        line.setHasLabels(false);//曲线的数据坐标是否加上备注
        line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.BLACK);  //设置字体颜色
        //axisX.setName("date");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        axisX.setMaxLabelChars(8); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        axisX.setLineColor(Color.BLACK);
        data.setAxisXBottom(axisX); //x 轴在底部  （顶部底部一旦设置就意味着x轴）
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(false); //x 轴分割线  每个x轴上 面有个虚线 与x轴垂直

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        Axis axisY = new Axis();  //Y轴
        axisY.setName("");//y轴标注
        axisY.setTextSize(9);//设置字体大小
        axisY.setTextColor(Color.BLACK);
        axisY.setSeparationColor(Color.WHITE);
        axisY.setMaxLabelChars(6);
        axisY.setHasLines(true);
        axisY.setHasSeparationLine(true);
        data.setAxisYLeft(axisY);  //Y轴设置在左边（左面右面一旦设定就意味着y轴）
        //data.setAxisYRight(axisY);  //y轴设置在右边


        //设置行为属性，支持缩放、滑动以及平移
        lineChartView.setZoomEnabled(true);
        lineChartView.setInteractive(true);
        lineChartView.setZoomType(ZoomType.HORIZONTAL);
        lineChartView.setMaxZoom((float) 3);//最大方法比例
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartView.setLineChartData(data);
        //设置触摸事件
        lineChartView.setOnValueTouchListener(new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                Toast.makeText(getActivity(), "" + value.getY(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {

            }
        });//为图表设置值得触摸事件
        lineChartView.setVisibility(View.VISIBLE);
        /**注：下面的7，10只是代表一个数字去类比而已
         * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
         */
        // 下面的这个api控制 滑动

        Viewport v = new Viewport(lineChartView.getMaximumViewport());
        v.left = 0;
        v.right = 7;
        int size = score.size();
        if (size > 0) {
            try {
                List<String> yValues = new ArrayList<>();
                yValues.addAll(score);
                Collections.sort(yValues);
                v.top = Float.valueOf(yValues.get(size - 1));
                v.bottom = Float.valueOf(yValues.get(0));
            } catch (Exception e) {
                v.top = 0;
                v.bottom = 0;
            }
        }
        lineChartView.setCurrentViewport(v);
    }


    private void getDoorStatis() {
        if (null == keyDoorModel) {
            keyDoorModel = new KeyDoorModel(getActivity());
        }
        keyDoorModel.getTotalCommunityStatistics(0, community_uuid, this::OnHttpResponse);
        getTypeStatistics("month");
        userIndent = "month";
        lockIndent = "month";
        keyIndent = "month";
        getMonthSelect();
    }

    private void getMonthSelect() {
        clearUserData();
        clearLockData();
        clearKeyData();
        setTextBgColor(tv_user_month, R.drawable.shape_key_leftbottom_select, getResources().getColor(R.color.white));
        setTextBgColor(tv_user_week, R.drawable.shape_key_middle, getResources().getColor(R.color.color_999faa));
        setTextBgColor(tv_user_day, R.drawable.shape_key_rightbottom, getResources().getColor(R.color.color_999faa));
        setTextBgColor(tv_lock_month, R.drawable.shape_key_leftbottom_select, getResources().getColor(R.color.white));
        setTextBgColor(tv_lock_week, R.drawable.shape_key_middle, getResources().getColor(R.color.color_999faa));
        setTextBgColor(tv_lock_day, R.drawable.shape_key_rightbottom, getResources().getColor(R.color.color_999faa));
        setTextBgColor(tv_key_month, R.drawable.shape_key_leftbottom_select, getResources().getColor(R.color.white));
        setTextBgColor(tv_key_week, R.drawable.shape_key_middle, getResources().getColor(R.color.color_999faa));
        setTextBgColor(tv_key_day, R.drawable.shape_key_rightbottom, getResources().getColor(R.color.color_999faa));
    }

    private void getTypeStatistics(String keyIndent) {
        if (null == keyDoorModel) {
            keyDoorModel = new KeyDoorModel(getActivity());
        }
        keyDoorModel.getTypeCommunityStatistics(1, community_uuid, keyIndent, this::OnHttpResponse);
    }

    private String userIndent = "month";
    private String lockIndent = "month";
    private String keyIndent = "month";


    public void changeCommunity(String community_uuid, String community_name) {
        this.community_uuid = community_uuid;
        this.community_name = community_name;
        getDoorStatis();
    }

    private void clearUserData() {
        mUserXaxisList.clear();
        mUserValuesList.clear();
        if (lockIndent.equals(userIndent)) {
            mLockXaxisList.clear();
            mLockValuesList.clear();
        }
        if (keyIndent.equals(userIndent)) {
            mKeyXaxisList.clear();
            mKeyValuesList.clear();
        }
    }

    private void clearLockData() {
        mLockXaxisList.clear();
        mLockValuesList.clear();
        if (keyIndent.equals(lockIndent)) {
            mKeyXaxisList.clear();
            mKeyValuesList.clear();
        }
        if (userIndent.equals(lockIndent)) {
            mUserXaxisList.clear();
            mUserValuesList.clear();
        }

    }

    private void clearKeyData() {
        mKeyXaxisList.clear();
        mKeyValuesList.clear();
        if (userIndent.equals(keyIndent)) {
            mUserXaxisList.clear();
            mUserValuesList.clear();
        }
        if (lockIndent.equals(keyIndent)) {
            mLockXaxisList.clear();
            mLockValuesList.clear();
        }
    }

    private List<String> mUserXaxisList = new ArrayList<>();
    private List<String> mUserValuesList = new ArrayList<>();
    private List<String> mLockXaxisList = new ArrayList<>();
    private List<String> mLockValuesList = new ArrayList<>();
    private List<String> mKeyXaxisList = new ArrayList<>();
    private List<String> mKeyValuesList = new ArrayList<>();

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                try {
                    KeyTotalDataEntity keyTotalDataEntity = GsonUtils.gsonToBean(result, KeyTotalDataEntity.class);
                    KeyTotalDataEntity.ContentBean contentBean = keyTotalDataEntity.getContent();
                    mValuesList.clear();
                    mValuesList.add(contentBean.getUserCount());
                    mValuesList.add(contentBean.getKeyCount());
                    mValuesList.add(contentBean.getAccessCount());
                    mValuesList.add(contentBean.getOpenLogCount());
                    if (null != keyDoorStatisticsAdapter) {
                        keyDoorStatisticsAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {

                }
                break;
            case 1:
                try {
                    KeyTypeDataEntity keyTypeDataEntity = GsonUtils.gsonToBean(result, KeyTypeDataEntity.class);
                    List<KeyTypeDataEntity.ContentBean> mContentList = keyTypeDataEntity.getContent();
                    for (KeyTypeDataEntity.ContentBean contentBean : mContentList) {
                        String dateIndent = contentBean.getDateIdent();
                        if (dateIndent.equals(userIndent)) {
                            mUserXaxisList.add(contentBean.getXaxisContent());
                            mUserValuesList.add(contentBean.getUserCount());
                        }
                        if (dateIndent.equals(lockIndent)) {
                            mLockXaxisList.add(contentBean.getXaxisContent());
                            mLockValuesList.add(contentBean.getOpenlogCount());
                        }
                        if (dateIndent.equals(keyIndent)) {
                            mKeyXaxisList.add(contentBean.getXaxisContent());
                            mKeyValuesList.add(contentBean.getActiceKeyCount());
                        }
                    }
                    if (mUserXaxisList.size() > 0) {
                        usr_linechart.setVisibility(View.VISIBLE);
                        initLineChart(mUserXaxisList, mUserValuesList, usr_linechart);
                    } else {
                        usr_linechart.setVisibility(View.INVISIBLE);
                    }
                    if (mLockXaxisList.size() > 0) {
                        lock_linechart.setVisibility(View.VISIBLE);
                        initLineChart(mLockXaxisList, mLockValuesList, lock_linechart);
                    } else {
                        lock_linechart.setVisibility(View.INVISIBLE);
                    }
                    if (mKeyXaxisList.size() > 0) {
                        key_linechart.setVisibility(View.VISIBLE);
                        initLineChart(mKeyXaxisList, mKeyValuesList, key_linechart);
                    } else {
                        key_linechart.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {

                }
                break;
        }
    }

    private void setTextBgColor(TextView textView, int drawableId, int colorId) {
        textView.setTextColor(colorId);
        textView.setBackgroundResource(drawableId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_user_month:
                if (!"month".equals(userIndent)) {
                    userIndent = "month";
                    clearUserData();
                    setTextBgColor(tv_user_month, R.drawable.shape_key_leftbottom_select, getResources().getColor(R.color.white));
                    setTextBgColor(tv_user_week, R.drawable.shape_key_middle, getResources().getColor(R.color.color_999faa));
                    setTextBgColor(tv_user_day, R.drawable.shape_key_rightbottom, getResources().getColor(R.color.color_999faa));
                    getTypeStatistics(userIndent);
                }
                break;
            case R.id.tv_user_week:
                if (!"week".equals(userIndent)) {
                    userIndent = "week";
                    clearUserData();
                    setTextBgColor(tv_user_month, R.drawable.shape_key_leftbottom, getResources().getColor(R.color.color_999faa));
                    setTextBgColor(tv_user_week, R.drawable.shape_key_middle_select, getResources().getColor(R.color.white));
                    setTextBgColor(tv_user_day, R.drawable.shape_key_rightbottom, getResources().getColor(R.color.color_999faa));
                    getTypeStatistics(userIndent);
                }

                break;
            case R.id.tv_user_day:
                if (!"day".equals(userIndent)) {
                    userIndent = "day";
                    clearUserData();
                    setTextBgColor(tv_user_month, R.drawable.shape_key_leftbottom, getResources().getColor(R.color.color_999faa));
                    setTextBgColor(tv_user_week, R.drawable.shape_key_middle, getResources().getColor(R.color.color_999faa));
                    setTextBgColor(tv_user_day, R.drawable.shape_key_rightbottom_select, getResources().getColor(R.color.white));
                    getTypeStatistics(userIndent);
                }
                break;
            case R.id.tv_lock_month:
                if (!"month".equals(lockIndent)) {
                    lockIndent = "month";
                    clearLockData();
                    setTextBgColor(tv_lock_month, R.drawable.shape_key_leftbottom_select, getResources().getColor(R.color.white));
                    setTextBgColor(tv_lock_week, R.drawable.shape_key_middle, getResources().getColor(R.color.color_999faa));
                    setTextBgColor(tv_lock_day, R.drawable.shape_key_rightbottom, getResources().getColor(R.color.color_999faa));
                    getTypeStatistics(lockIndent);
                }

                break;
            case R.id.tv_lock_week:
                if (!"week".equals(lockIndent)) {
                    lockIndent = "week";
                    clearLockData();
                    setTextBgColor(tv_lock_month, R.drawable.shape_key_leftbottom, getResources().getColor(R.color.color_999faa));
                    setTextBgColor(tv_lock_week, R.drawable.shape_key_middle_select, getResources().getColor(R.color.white));
                    setTextBgColor(tv_lock_day, R.drawable.shape_key_rightbottom, getResources().getColor(R.color.color_999faa));
                    getTypeStatistics(lockIndent);
                }

                break;
            case R.id.tv_lock_day:
                if (!"day".equals(lockIndent)) {
                    lockIndent = "day";
                    clearLockData();
                    setTextBgColor(tv_lock_month, R.drawable.shape_key_leftbottom, getResources().getColor(R.color.color_999faa));
                    setTextBgColor(tv_lock_week, R.drawable.shape_key_middle, getResources().getColor(R.color.color_999faa));
                    setTextBgColor(tv_lock_day, R.drawable.shape_key_rightbottom_select, getResources().getColor(R.color.white));
                    getTypeStatistics(lockIndent);
                }

                break;
            case R.id.tv_key_month:
                if (!"month".equals(keyIndent)) {
                    keyIndent = "month";
                    clearKeyData();
                    setTextBgColor(tv_key_month, R.drawable.shape_key_leftbottom_select, getResources().getColor(R.color.white));
                    setTextBgColor(tv_key_week, R.drawable.shape_key_middle, getResources().getColor(R.color.color_999faa));
                    setTextBgColor(tv_key_day, R.drawable.shape_key_rightbottom, getResources().getColor(R.color.color_999faa));
                    getTypeStatistics(keyIndent);
                }

                break;
            case R.id.tv_key_week:
                if (!"week".equals(keyIndent)) {
                    keyIndent = "week";
                    clearKeyData();
                    setTextBgColor(tv_key_month, R.drawable.shape_key_leftbottom, getResources().getColor(R.color.color_999faa));
                    setTextBgColor(tv_key_week, R.drawable.shape_key_middle_select, getResources().getColor(R.color.white));
                    setTextBgColor(tv_key_day, R.drawable.shape_key_rightbottom, getResources().getColor(R.color.color_999faa));
                    getTypeStatistics(keyIndent);
                }
                break;
            case R.id.tv_key_day:
                if (!"day".equals(keyIndent)) {
                    keyIndent = "day";
                    clearKeyData();
                    setTextBgColor(tv_key_month, R.drawable.shape_key_leftbottom, getResources().getColor(R.color.color_999faa));
                    setTextBgColor(tv_key_week, R.drawable.shape_key_middle, getResources().getColor(R.color.color_999faa));
                    setTextBgColor(tv_key_day, R.drawable.shape_key_rightbottom_select, getResources().getColor(R.color.white));
                    getTypeStatistics(keyIndent);
                }
                break;
        }
    }
}
