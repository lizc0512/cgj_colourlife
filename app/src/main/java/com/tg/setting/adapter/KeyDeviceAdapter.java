package com.tg.setting.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intelspace.library.module.Device;
import com.tg.coloursteward.R;

import java.util.List;


public class KeyDeviceAdapter extends BaseAdapter {

    private Context mContext;
    private List<Device> mDevices;

    public KeyDeviceAdapter(Context context, List<Device> devices) {
        mContext = context;
        mDevices = devices;
    }

    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public Device getItem(int position) {
        return mDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_key_bind_door, parent, false);
            holder.ll_item = convertView.findViewById(R.id.ll_item);
            holder.tv_door = convertView.findViewById(R.id.tv_door);
            holder.tv_type = convertView.findViewById(R.id.tv_type);
            holder.tv_mac = convertView.findViewById(R.id.tv_mac);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Device device = mDevices.get(position);

        String deviceType;
        switch (device.getLockVersion()) {
            case Device.LOCK_VERSION_DOOR:
                deviceType = "门锁";
                break;
            case Device.LOCK_VERSION_ENTRANCE:
                deviceType = "门禁";
                break;
            case Device.LOCK_VERSION_PARK_LOCK:
                deviceType = "地锁";
                break;
            case Device.LOCK_VERSION_BARRIER:
                deviceType = "道闸";
                break;
            case Device.LOCK_VERSION_LIFE_CONTROLLER:
                deviceType = "电梯";
                break;
            case Device.LOCK_VERSION_CARDREADER:
                deviceType = "发卡器";
                break;
            case Device.LOCK_VERSION_HELMINTH:
                deviceType = "八爪鱼";
                break;
            case Device.LOCK_VERSION_MODULE:
                deviceType = "模块";
                break;
            case Device.LOCK_VERSION_REMOTE_CONTROLLER:
                deviceType = "遥控器";
                break;
            case Device.LOCK_VERSION_GATEWAY:
                deviceType = "网关";
                break;
            default:
                deviceType = "未知";
                break;
        }
        holder.tv_door.setText(deviceType);

        if (device.getRssi() >= -40) {
            Drawable drawableKey = mContext.getResources().getDrawable(R.drawable.ic_key_signal5);
            holder.tv_door.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableKey, null);
        } else if (device.getRssi() >= -50) {
            Drawable drawableKey = mContext.getResources().getDrawable(R.drawable.ic_key_signal4);
            holder.tv_door.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableKey, null);
        } else if (device.getRssi() >= -60) {
            Drawable drawableKey = mContext.getResources().getDrawable(R.drawable.ic_key_signal3);
            holder.tv_door.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableKey, null);
        } else if (device.getRssi() >= -75) {
            Drawable drawableKey = mContext.getResources().getDrawable(R.drawable.ic_key_signal2);
            holder.tv_door.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableKey, null);
        } else if (device.getRssi() >= -90) {
            Drawable drawableKey = mContext.getResources().getDrawable(R.drawable.ic_key_signal1);
            holder.tv_door.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableKey, null);
        } else {
            Drawable drawableKey = mContext.getResources().getDrawable(R.drawable.ic_key_signal0);
            holder.tv_door.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableKey, null);
        }

        holder.tv_type.setText("型号     " + device.getBluetoothDevice().getName());
        holder.tv_mac.setText("MAC    " + device.getLockMac());

//        holder.ll_item.setOnClickListener(v -> ((KeyBindDoorActivity) mContext).bindDevice(position));
        return convertView;
    }

    public class ViewHolder {
        LinearLayout ll_item;
        TextView tv_door;
        TextView tv_type;
        TextView tv_mac;
    }


    //    public List<Device> list;
//    public Context mContext;
//
//    public KeyDeviceAdapter(Context mContext, List<Device> list) {
//        this.list = list;
//        this.mContext = mContext;
//    }
//
//    @NonNull
//    @Override
//    public KeyDeviceAdapter.DefaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.item_key_bind_door, parent, false);
//        return new DefaultViewHolder(view);
//    }
//
//    @SuppressLint("SetTextI18n")
//    @Override
//    public void onBindViewHolder(@NonNull KeyDeviceAdapter.DefaultViewHolder holder, int position) {
//        Device device = list.get(position);
//
//        String deviceType;
//        switch (device.getLockVersion()) {
//            case Device.LOCK_VERSION_DOOR:
//                deviceType = "门锁";
//                break;
//            case Device.LOCK_VERSION_ENTRANCE:
//                deviceType = "门禁";
//                break;
//            case Device.LOCK_VERSION_PARK_LOCK:
//                deviceType = "地锁";
//                break;
//            case Device.LOCK_VERSION_BARRIER:
//                deviceType = "道闸";
//                break;
//            case Device.LOCK_VERSION_LIFE_CONTROLLER:
//                deviceType = "电梯";
//                break;
//            case Device.LOCK_VERSION_CARDREADER:
//                deviceType = "发卡器";
//                break;
//            case Device.LOCK_VERSION_HELMINTH:
//                deviceType = "八爪鱼";
//                break;
//            case Device.LOCK_VERSION_MODULE:
//                deviceType = "模块";
//                break;
//            case Device.LOCK_VERSION_REMOTE_CONTROLLER:
//                deviceType = "遥控器";
//                break;
//            case Device.LOCK_VERSION_GATEWAY:
//                deviceType = "网关";
//                break;
//            default:
//                deviceType = "未知";
//                break;
//        }
//        holder.tv_door.setText(deviceType);
//
//        if (device.getRssi() >= -40) {
//            Drawable drawableKey = mContext.getResources().getDrawable(R.drawable.ic_key_signal5);
//            holder.tv_door.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableKey, null);
//        } else if (device.getRssi() >= -50) {
//            Drawable drawableKey = mContext.getResources().getDrawable(R.drawable.ic_key_signal4);
//            holder.tv_door.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableKey, null);
//        } else if (device.getRssi() >= -60) {
//            Drawable drawableKey = mContext.getResources().getDrawable(R.drawable.ic_key_signal3);
//            holder.tv_door.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableKey, null);
//        } else if (device.getRssi() >= -75) {
//            Drawable drawableKey = mContext.getResources().getDrawable(R.drawable.ic_key_signal2);
//            holder.tv_door.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableKey, null);
//        } else if (device.getRssi() >= -90) {
//            Drawable drawableKey = mContext.getResources().getDrawable(R.drawable.ic_key_signal1);
//            holder.tv_door.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableKey, null);
//        } else {
//            Drawable drawableKey = mContext.getResources().getDrawable(R.drawable.ic_key_signal0);
//            holder.tv_door.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableKey, null);
//        }
//
//        holder.tv_type.setText("型号     " + device.getBluetoothDevice().getName());
//        holder.tv_mac.setText("MAC    " + device.getLockMac());
//
//        holder.ll_item.setOnClickListener(v -> ((KeyBindDoorActivity) mContext).bindDevice(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return list == null ? 0 : list.size();
//    }
//
//    static class DefaultViewHolder extends RecyclerView.ViewHolder {
//        LinearLayout ll_item;
//        TextView tv_door;
//        TextView tv_type;
//        TextView tv_mac;
//
//        DefaultViewHolder(View itemView) {
//            super(itemView);
//            ll_item = itemView.findViewById(R.id.ll_item);
//            tv_door = itemView.findViewById(R.id.tv_door);
//            tv_type = itemView.findViewById(R.id.tv_type);
//            tv_mac = itemView.findViewById(R.id.tv_mac);
//        }
//    }

}
