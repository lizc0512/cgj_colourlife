package com.youmai.hxsdk.contact.letter.widget;

import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.youmai.hxsdk.contact.letter.bean.BaseIndexPinyinBean;
import com.youmai.hxsdk.contact.pinyin.Pinyin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2018.03.20 14:23
 * 描述：Letter辅助类
 */
public class LetterBarBuilder {

    public static String[] INDEX_STRING = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};//#在最后面（默认的数据源）

    private static class SingleTonHelper {
        static LetterBarBuilder instance = new LetterBarBuilder();
    }

    public static LetterBarBuilder instance() {
        return SingleTonHelper.instance;
    }

    private List<String> mIndexDatas = new ArrayList<>();//索引数据源

    //以下边变量是外部set进来的
    private List<? extends BaseIndexPinyinBean> mSourceDatas;//Adapter的数据源
    private TextView mPressedShowTextView;//用于特写显示正在被触摸的index值
    private boolean isNeedRealIndex;//是否需要根据实际的数据来生成索引数据源（例如 只有 A B C 三种tag，那么索引栏就 A B C 三项）
    private LinearLayoutManager mLayoutManager;

    public void init(Builder param) {
        Builder apply = new Builder().apply(param);
        mSourceDatas = apply.mSourceDatas;
        mPressedShowTextView = apply.mPressedShowTextView;
        isNeedRealIndex = apply.isNeedRealIndex;
        mLayoutManager = apply.mLayoutManager;
        initSourceDatas();
    }

    public List<String> loadIndex() {
        if (!isNeedRealIndex) {//不需要真实的索引数据源
            mIndexDatas = Arrays.asList(INDEX_STRING);
        }
        //设置index触摸监听器
        setOnIndexPressedListener(new IOnIndexPressedListener() {
            @Override
            public void onIndexPressed(int index, String text) {
                if (mPressedShowTextView != null) { //显示hintTexView
                    mPressedShowTextView.setVisibility(View.VISIBLE);
                    mPressedShowTextView.setText(text);
                }
                //滑动Rv
                if (mLayoutManager != null) {
                    int position = getPosByTag(text);
                    if (position != -1) {
                        mLayoutManager.scrollToPositionWithOffset(position, 0);
                    }
                }
            }

            @Override
            public void onMotionEventEnd() {
                //隐藏hintTextView
                if (mPressedShowTextView != null) {
                    mPressedShowTextView.setVisibility(View.GONE);
                }
            }
        });
        return mIndexDatas;
    }

    /**
     * 数据变更
     * @param sourceData
     */
    public void setSourceData(List<? extends BaseIndexPinyinBean> sourceData) {
        this.mSourceDatas = sourceData;
        initSourceDatas();
    }

    /**
     * 初始化原始数据源，并取出索引数据源
     *
     * @return
     */
    private void initSourceDatas() {
        //解决源数据为空 或者size为0的情况
        if (null == mSourceDatas || mSourceDatas.isEmpty()) {
            return;
        }
        int size = mSourceDatas.size();
        for (int i = 0; i < size; i++) {
            BaseIndexPinyinBean indexPinyinBean = mSourceDatas.get(i);
            StringBuilder pySb = new StringBuilder();
            String target = indexPinyinBean.getTarget();//取出需要被拼音化的字段
            //遍历target的每个char得到它的全拼音
            for (int i1 = 0; i1 < target.length(); i1++) {
                //利用TinyPinyin将char转成拼音
                //查看源码，方法内 如果char为汉字，则返回大写拼音
                //如果c不是汉字，则返回String.valueOf(c)
                pySb.append(Pinyin.toPinyin(target.charAt(i1)).toUpperCase());
            }
            indexPinyinBean.setBaseIndexPinyin(pySb.toString());//设置城市名全拼音

            //以下代码设置城市拼音首字母
            String tagString = pySb.toString().substring(0, 1);
            if (tagString.matches("[A-Z]")) {//如果是A-Z字母开头
                indexPinyinBean.setBaseIndexTag(tagString);
            } else {//特殊字母这里统一用#处理
                indexPinyinBean.setBaseIndexTag("#");
            }
        }

        for (int j = 0; j < INDEX_STRING.length; j++) {
            if (isNeedRealIndex) {//如果需要真实的索引数据源
                if (!mIndexDatas.contains(INDEX_STRING[j])) {//则判断是否已经将这个索引添加进去，若没有则添加
                    mIndexDatas.add(INDEX_STRING[j]);
                }
            }
        }
        sortData();
    }

    /**
     * 对数据源排序
     */
    private void sortData() {
        //对右侧栏进行排序 将 # 丢在最后
        Collections.sort(mIndexDatas, new Comparator<String>() {
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

        //对数据源进行排序
        Collections.sort(mSourceDatas, new Comparator<BaseIndexPinyinBean>() {
            @Override
            public int compare(BaseIndexPinyinBean lhs, BaseIndexPinyinBean rhs) {
                if (lhs.getBaseIndexTag().equals("#")) {
                    return 1;
                } else if (rhs.getBaseIndexTag().equals("#")) {
                    return -1;
                } else {
                    return lhs.getBaseIndexPinyin().compareTo(rhs.getBaseIndexPinyin());
                }
            }
        });
    }

    /**
     * 根据传入的pos返回tag
     *
     * @param tag
     * @return
     */
    private int getPosByTag(String tag) {
        //add by zhangxutong 2016 09 08 :解决源数据为空 或者size为0的情况,
        if (null == mSourceDatas || mSourceDatas.isEmpty()) {
            return -1;
        }
        if (TextUtils.isEmpty(tag)) {
            return -1;
        }
        if (tag.equals("")) {

        }
        for (int i = 0; i < mSourceDatas.size(); i++) {
            if (tag.equals(mSourceDatas.get(i).getBaseIndexTag())) {
                return i;
            }
        }
        return -1;
    }

    public static class Builder {
        TextView mPressedShowTextView;//用于特写显示正在被触摸的index值
        boolean isNeedRealIndex;
        LinearLayoutManager mLayoutManager;
        List<? extends BaseIndexPinyinBean> mSourceDatas;

        public Builder build() {
            Builder builder = new Builder();
            builder.mPressedShowTextView = this.mPressedShowTextView;
            builder.isNeedRealIndex = this.isNeedRealIndex;
            builder.mLayoutManager = this.mLayoutManager;
            builder.mSourceDatas = this.mSourceDatas;
            return builder;
        }

        public Builder setPressedShowTextView(TextView pressedShowTextView) {
            this.mPressedShowTextView = pressedShowTextView;
            return this;
        }

        public Builder setNeedRealIndex(boolean needRealIndex) {
            isNeedRealIndex = needRealIndex;
            return this;
        }

        public Builder setLayoutManager(LinearLayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
            return this;
        }

        public Builder setSourceData(List<? extends BaseIndexPinyinBean> sourceData) {
            this.mSourceDatas = sourceData;
            return this;
        }

        public Builder apply(Builder builder) {
            this.mPressedShowTextView = builder.mPressedShowTextView;
            this.isNeedRealIndex = builder.isNeedRealIndex;
            this.mLayoutManager = builder.mLayoutManager;
            this.mSourceDatas = builder.mSourceDatas;
            return this;
        }
    }

    /**
     * 当前被按下的index的监听器
     */
    public interface IOnIndexPressedListener {
        void onIndexPressed(int index, String text);//当某个Index被按下
        void onMotionEventEnd();//当触摸事件结束（UP CANCEL）
    }

    private IOnIndexPressedListener mOnIndexPressedListener;

    public IOnIndexPressedListener getOnIndexPressedListener() {
        return mOnIndexPressedListener;
    }

    public void setOnIndexPressedListener(IOnIndexPressedListener listener) {
        this.mOnIndexPressedListener = listener;
    }

}
