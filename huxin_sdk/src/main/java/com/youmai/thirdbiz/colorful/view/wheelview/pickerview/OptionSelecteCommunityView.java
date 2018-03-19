package com.youmai.thirdbiz.colorful.view.wheelview.pickerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.youmai.thirdbiz.colorful.view.wheelview.pickerview.view.BasePickerView;
import com.youmai.thirdbiz.colorful.view.wheelview.pickerview.view.WheelOptions;

import java.util.ArrayList;
import java.util.List;

import com.youmai.hxsdk.R;


/**
 * Created by Administrator on 2016/12/26 0026.
 */

public class OptionSelecteCommunityView<T> extends BasePickerView implements View.OnClickListener {
    private View btnSubmit;
    private OnOptionsSelectListener optionsSelectListener;

    private List<T> list = new ArrayList<T>();
    WheelOptions<T> wheelOptions;
    public OptionSelecteCommunityView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.cgj_wheelview_selecte_community, contentContainer);

        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(this);

        // ----转轮
        final View optionspicker = findViewById(R.id.optionspicker);
        wheelOptions = new WheelOptions(optionspicker);
    }

    public void setPicker(ArrayList<T> optionsItems) {
        wheelOptions.setPicker(optionsItems, null, null, false);
    }

    /**
     * 设置选中的item位置
     * @param option1 位置
     */
    public void setSelectOptions(int option1){
        wheelOptions.setCurrentItems(option1);
    }

    /**
     * 设置选项的单位
     * @param label1 单位
     */
    public void setLabels(String label1){
        wheelOptions.setLabels(label1);
    }

    public void setCyclic(boolean cyclic){
        wheelOptions.setCyclic(cyclic);
    }

    @Override
    public void onClick(View v) {
        String tag=(String) v.getTag();
            if(optionsSelectListener!=null)
            {
                int[] optionsCurrentItems=wheelOptions.getCurrentItems();
                optionsSelectListener.onOptionsSelect(optionsCurrentItems[0]);
            }
            dismiss();
            return;
    }



    public interface OnOptionsSelectListener {
        void onOptionsSelect(int options);
    }

    public void setOnoptionsSelectListener(
            OnOptionsSelectListener optionsSelectListener) {
        this.optionsSelectListener = optionsSelectListener;
    }


}
