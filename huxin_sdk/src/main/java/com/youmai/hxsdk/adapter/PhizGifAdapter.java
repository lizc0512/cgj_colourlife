package com.youmai.hxsdk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


import com.youmai.hxsdk.R;

import java.util.ArrayList;
import java.util.List;


public class PhizGifAdapter extends RecyclerView.Adapter<PhizGifAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mList;


    public PhizGifAdapter(Context context) {
        this.mContext = context;
        mList = new ArrayList<String>();
    }


    public PhizGifAdapter(Context context, List<String> list) {
        this.mContext = context;
        this.mList = list;
    }


    public void setList(List<String> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    @Override
    public PhizGifAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.hx_phiz_item, null);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final String item = mList.get(position);
        viewHolder.imgGif.setImageResource(R.drawable.hx_hi);

        viewHolder.imgGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "position:" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgGif;

        public ViewHolder(View itemView) {
            super(itemView);

            imgGif = (ImageView) itemView.findViewById(R.id.img_gif);

        }
    }

}
