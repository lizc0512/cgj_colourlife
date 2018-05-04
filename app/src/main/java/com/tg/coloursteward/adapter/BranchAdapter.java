package com.tg.coloursteward.adapter;

import android.content.Context;
import android.media.Image;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.FamilyInfo;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.view.MyGridView;
import com.tg.coloursteward.view.dialog.ToastFactory;

import java.util.ArrayList;

public class BranchAdapter extends MyBaseAdapter<FamilyInfo>{
	private ArrayList<FamilyInfo> list;
	private LayoutInflater inflater;
	private FamilyInfo item;
	private Context context;
    public interface NetBranchRequestListener {
        void onNext(FamilyInfo info);
    }
    private NetBranchRequestListener requestListener;
	public BranchAdapter(Context con, ArrayList<FamilyInfo> list) {
		super(list);
		this.list = list;
		this.context = con;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.branch_item, null);
		}
		item = list.get(position);
		TextView tvFamily= (TextView) convertView.findViewById(R.id.tv_family);
		RelativeLayout rlNext= (RelativeLayout)convertView.findViewById(R.id.rl_next);
		tvFamily.setText(item.name);
		rlNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				FamilyInfo info = list.get(position);
				if(item.orgType.equals("彩生活集团") || item.orgType.equals("大区")|| item.orgType.equals("事业部")){
					requestListener.onNext(info);
				}else {
					ToastFactory.showToast(context,"已经是最后一级了!");
				}

            }
        });
		return convertView;
	}
    public void setNetBranchRequestListener(NetBranchRequestListener l) {
        requestListener = l;
    }
}
