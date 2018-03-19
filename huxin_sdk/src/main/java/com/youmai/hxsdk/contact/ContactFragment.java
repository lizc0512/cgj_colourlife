package com.youmai.hxsdk.contact;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.contact.IndexBar.widget.IndexBar;
import com.youmai.hxsdk.contact.decoration.DividerItemDecoration;
import com.youmai.hxsdk.contact.decoration.TitleItemDecoration;

import java.util.ArrayList;
import java.util.List;


public class ContactFragment extends Fragment {

    public final static String TAG = ContactFragment.class.getSimpleName();

    private RecyclerView mRv;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mManager;
    private List<CityBean> mDatas;

    private TitleItemDecoration mDecoration;

    /**
     * 右侧边栏导航区域
     */
    private IndexBar mIndexBar;

    /**
     * 显示指示器DialogText
     */
    private TextView mTvSideBarHint;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);
    }


    private void initView(View view) {


        mRv = (RecyclerView) view.findViewById(R.id.rv);
        mRv.setLayoutManager(mManager = new LinearLayoutManager(getContext()));
        //initDatas();
        initDatas(getResources().getStringArray(R.array.provinces));
        //mDatas = new ArrayList<>();//测试为空或者null的情况 已经通过 2016 09 08

        mRv.setAdapter(mAdapter = new CityAdapter(getContext(), mDatas));
        mRv.addItemDecoration(mDecoration = new TitleItemDecoration(getContext(), mDatas));
        //如果add两个，那么按照先后顺序，依次渲染。
        //mRv.addItemDecoration(new TitleItemDecoration2(this,mDatas));
        mRv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));


        //使用indexBar
        mTvSideBarHint = (TextView) view.findViewById(R.id.tvSideBarHint);//HintTextView
        mIndexBar = (IndexBar) view.findViewById(R.id.indexBar);//IndexBar
        mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setmLayoutManager(mManager)//设置RecyclerView的LayoutManager
                .setmSourceDatas(mDatas);//设置数据源

    }

    /**
     * 组织数据源
     *
     * @param data
     * @return
     */
    private void initDatas(String[] data) {
        mDatas = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            CityBean cityBean = new CityBean();
            cityBean.setCity(data[i]);//设置城市名称
            mDatas.add(cityBean);
        }
    }

    /**
     * 更新数据源
     *
     * @param view
     */
    public void updateDatas(View view) {
        for (int i = 0; i < 99; i++) {
            mDatas.add(new CityBean("东京"));
            mDatas.add(new CityBean("大阪"));
        }
        mAdapter.notifyDataSetChanged();
        mIndexBar.setmSourceDatas(mDatas);
    }


/*
    *//**
     * 组织数据源
     *
     * @param data
     * @return
     *//*
    private void initDatas(String[] data) {
        mDatas = new ArrayList<>();
        mSideBarDatas = new ArrayList<>();//导航栏数据源

        for (int i = 0; i < data.length; i++) {
            CityBean cityBean = new CityBean();
            cityBean.setCity(data[i]);//设置城市名称

            StringBuilder pySb = new StringBuilder();
            //取出首个char得到它的拼音
            for (int i1 = 0; i1 < data[i].length(); i1++) {
                //如果c为汉字，则返回大写拼音；如果c不是汉字，则返回String.valueOf(c)
                pySb.append(Pinyin.toPinyin(data[i].charAt(i1)));
            }
            cityBean.setPyCity(pySb.toString());//设置城市名拼音

            //以下代码设置城市拼音首字母
            String sortString = pySb.toString().substring(0, 1);
            if (sortString.matches("[A-Z]")) {//如果是A-Z字母开头
                cityBean.setTag(sortString);
                if (!mSideBarDatas.contains(sortString)) {
                    mSideBarDatas.add(sortString);
                }
            } else {
                cityBean.setTag("#");
                if (!mSideBarDatas.contains("#")) {
                    mSideBarDatas.add("#");
                }
            }
            mDatas.add(cityBean);
        }
        sortData();
    }

    *//**
     * 对数据源排序
     *//*
    private void sortData() {
        //对右侧栏进行排序 将 # 丢在最后
        Collections.sort(mSideBarDatas, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                if (lhs.equals("#")) {
                    return 1;
                } else if (rhs.equals("#")) {
                    return -1;
                } else {
                    return lhs.compareTo(rhs);
                }
            }
        });
        mSideBar.setIndexText(mSideBarDatas);

        //对数据源进行排序
        Collections.sort(mDatas, new Comparator<CityBean>() {
            @Override
            public int compare(CityBean lhs, CityBean rhs) {
                if (lhs.getTag().equals("#")) {
                    return 1;
                } else if (rhs.getTag().equals("#")) {
                    return -1;
                } else {
                    return lhs.getPyCity().compareTo(rhs.getPyCity());
                }
            }
        });
    }


    *//**
     * 根据传入的pos返回tag
     *
     * @param tag
     * @return
     *//*
    private int getPosByTag(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return -1;
        }
        for (int i = 0; i < mDatas.size(); i++) {
            if (tag.equals(mDatas.get(i).getTag())) {
                return i;
            }
        }
        return -1;
    }*/
}
