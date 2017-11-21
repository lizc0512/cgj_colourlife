package com.tg.coloursteward.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.MyBaseAdapter;
import com.tg.coloursteward.info.BankCardInfo;

public class SwipeAdapter extends MyBaseAdapter<BankCardInfo>{
	private ArrayList<BankCardInfo> list;
	private LayoutInflater inflater;
	private BankCardInfo item;
	private Context context;
	/**
     * 
     */
	private int mRightWidth = 0;
	/**
	 * 单击事件监听器
	 */
	private IOnItemRightClickListener mListener = null;
	
	public interface IOnItemRightClickListener {
		void onRightClick(View v, int position);
	}
	public SwipeAdapter(Context con, int rightWidth,
			IOnItemRightClickListener l,ArrayList<BankCardInfo> list) {
		super(list);
		this.context = con;
		mRightWidth = rightWidth;
		mListener = l;
		this.list = list;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final int thisPosition = position;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_bank_card, null);
		}
		item = list.get(position);
		RelativeLayout item_left = (RelativeLayout) convertView.findViewById(R.id.item_left);
		RelativeLayout item_right = (RelativeLayout) convertView.findViewById(R.id.item_right);
		TextView tvBankName= (TextView) convertView.findViewById(R.id.tv_bank_name);
		TextView tvBankCardNumber = (TextView) convertView.findViewById(R.id.tv_bank_card_number);
		TextView item_delete = (TextView) convertView.findViewById(R.id.item_delete);//删除
		LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		item_left.setLayoutParams(lp1);
		LayoutParams lp2 = new LayoutParams(mRightWidth,LayoutParams.MATCH_PARENT);
		item_right.setLayoutParams(lp2);
		item_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onRightClick(v, thisPosition);
				}
			}
		});
		tvBankName.setText(item.bankName);

		String bankCardNum = item.cardNo;
		if (bankCardNum.length() == 16) {
			tvBankCardNumber.setText(bankCardNum.subSequence(0, 4)
					+ " **** **** "
					+ bankCardNum.subSequence(bankCardNum.length() - 4,
							bankCardNum.length()));
		} else if (bankCardNum.length() == 19) {
			tvBankCardNumber.setText(bankCardNum.subSequence(0, 4)
					+ " **** **** *** "
					+ bankCardNum.subSequence(bankCardNum.length() - 4,
							bankCardNum.length()));
		} else {
			tvBankCardNumber.setText("卡号异常");
		}
		return convertView;
	}

}

