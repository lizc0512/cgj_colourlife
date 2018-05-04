package com.tg.coloursteward.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.info.WiFiDetail;

import java.util.List;

/**
 * 搜索到的WiFi列表
 * Created by prince70 on 2018/3/28.
 */

public class WifiListAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<WiFiDetail> mDatas;
    private int tempPosition = -1;  //记录已经点击的CheckBox的位置

    public WifiListAdapter(Activity mContext, List<WiFiDetail> mDatas) {
        this.mActivity = mContext;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.size()>0?mDatas.size():0;
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.list_wifi_item, null);
            holder.SSID= (TextView) convertView.findViewById(R.id.tv_wifi_name);
            holder.BSSID=(TextView)convertView.findViewById(R.id.tv_bssid);
            holder.checkBox= (CheckBox) convertView.findViewById(R.id.cb_wifi);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.SSID.setText(mDatas.get(position).getSSID());
        holder.BSSID.setText(mDatas.get(position).getBSSID());

        holder.checkBox.setId(position);    //设置当前position为CheckBox的id
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (tempPosition != -1) {
                        //根据id找到上次点击的CheckBox,将它设置为false.
                        CheckBox tempCheckBox = (CheckBox) mActivity.findViewById(tempPosition);
                        if (tempCheckBox != null) {
                            tempCheckBox.setChecked(false);
                        }
                    }
                    //保存当前选中CheckBox的id值
                    tempPosition = buttonView.getId();

                } else {    //当CheckBox被选中,又被取消时,将tempPosition重新初始化.
                    tempPosition = -1;
                }
            }
        });
        if (position == tempPosition)   //比较位置是否一样,一样就设置为选中,否则就设置为未选中.
            holder.checkBox.setChecked(true);
        else holder.checkBox.setChecked(false);
        return convertView;
    }


    //返回当前CheckBox选中的位置,便于获取值.
    public int getSelectPosition() {
        return tempPosition;
    }

    public static class ViewHolder {
        public TextView SSID;
        public TextView BSSID;
        public CheckBox checkBox;
    }
}
