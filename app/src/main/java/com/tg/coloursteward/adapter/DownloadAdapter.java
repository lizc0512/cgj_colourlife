package com.tg.coloursteward.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.Tools;

import java.io.File;
import java.util.List;

public class DownloadAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<String> items;
    private List<String> paths;
    private Context mContext;
    private static final int TYPE_PDF = 0;
    private static final int TYPE_AUDIO = 1;
    private static final int TYPE_VIDEO = 2;
    private static final int TYPE_IMAGE = 3;
    private static final int TYPE_APP = 4;
    private static final int TYPE_OTHERS = 9;

    public DownloadAdapter(Context context, List<String> it, List<String> pa) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        items = it;
        paths = pa;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.file_row, null);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            File f = new File(paths.get(position).toString());
            String name;
            if (f.getName().contains("UTF-8''")) {
                name = f.getName().replaceAll("UTF-8''", "");
            } else {
                name = f.getName();
            }
            holder.text.setText(name);
            if (f.isDirectory()) {
                GlideUtils.loadImageView(mContext, R.drawable.folder, holder.icon);
            } else {
                switch (Tools.getFileIntType(f)) {
                    case TYPE_PDF:
                        GlideUtils.loadImageView(mContext, R.drawable.doc, holder.icon);
                        break;
                    case TYPE_AUDIO:
                        GlideUtils.loadImageView(mContext, R.drawable.icon_audio, holder.icon);
                        break;
                    case TYPE_VIDEO:
                        GlideUtils.loadImageView(mContext, R.drawable.icon_video, holder.icon);
                        break;
                    case TYPE_IMAGE:
                        GlideUtils.loadImageView(mContext, R.drawable.ic_empty, holder.icon);
                        break;
                    case TYPE_APP:
                        GlideUtils.loadImageView(mContext, R.drawable.icon_apk, holder.icon);
                        break;
                    case TYPE_OTHERS:
                        GlideUtils.loadImageView(mContext, R.drawable.icon_others, holder.icon);
                        break;
                    default:
                        GlideUtils.loadImageView(mContext, R.drawable.doc, holder.icon);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private class ViewHolder {
        TextView text;
        ImageView icon;
    }

}
