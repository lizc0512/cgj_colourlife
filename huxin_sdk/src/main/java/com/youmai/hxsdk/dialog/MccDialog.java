package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.adapter.MccAdapter;
import com.youmai.hxsdk.entity.MccCode;

import java.util.List;


/**
 * mcc区号 dialog
 * Created by fylder on 2017/1/5.
 */
public class MccDialog extends Dialog {


    public MccDialog(Context context) {
        super(context);
    }

    public MccDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        List<MccCode> mccCodes;
        MccDialog dialog;

        private Context context;
        private String title;
        private OnClickListener positiveClickListener;
        private DialogInterface.OnClickListener negativeClickListener;

        private TextView titleText;
        private RecyclerView recyclerView;
        private MccAdapter adapter;

        private int index = 0;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMccCodes(List<MccCode> mccCodes) {
            this.mccCodes = mccCodes;
            return this;
        }

        public Builder setIndex(int index) {
            this.index = index;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }


        /**
         * 设置dialog信息
         *
         * @param title 资源内容
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }


        public Builder setPositiveListener(OnClickListener listener) {
            this.positiveClickListener = listener;
            return this;
        }

        public Builder setCloseListener(DialogInterface.OnClickListener listener) {
            this.negativeClickListener = listener;
            return this;
        }


        public void dismiss() {
            this.dialog.dismiss();
        }


        public MccDialog create() {
            if (dialog == null) {
                dialog = new MccDialog(context, R.style.PhoneDialog);
            }
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.hx_dialog_mcc_layout, null);
            titleText = (TextView) view.findViewById(R.id.dialog_mcc_title);
            recyclerView = (RecyclerView) view.findViewById(R.id.dialog_mcc_recycler);

            LinearLayoutManager manager = new LinearLayoutManager(context);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(manager);
            adapter = new MccAdapter(context, mccCodes, new MccAdapter.OnClickListener() {
                @Override
                public void onClick(String code) {
                    if (positiveClickListener != null) {
                        positiveClickListener.onClick(dialog, code);
                    }
                }
            });
            recyclerView.setAdapter(adapter);

            dialog.addContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

            if (title != null) {
                titleText.setText(title);
            }
            if (index != 0) {
                recyclerView.smoothScrollToPosition(index);
                moveToPosition(manager, recyclerView, index);
            }
            dialog.setContentView(view);
            return dialog;
        }
    }

    /**
     * RecyclerView 移动到当前位置，
     *
     * @param manager       只要是个manager  对象就可以，重新new一个也可以
     * @param mRecyclerView 当前的RecyclerView
     * @param n             要跳转的位置
     */
    private static void moveToPosition(LinearLayoutManager manager, RecyclerView mRecyclerView, int n) {


        int firstItem = manager.findFirstVisibleItemPosition();
        int lastItem = manager.findLastVisibleItemPosition();
        if (n <= firstItem) {
            mRecyclerView.scrollToPosition(n);
        } else if (n <= lastItem) {
            int top = mRecyclerView.getChildAt(n - firstItem).getTop();
            mRecyclerView.scrollBy(0, top);
        } else {
            mRecyclerView.scrollToPosition(n);
        }

    }

    /**
     * 提交回调
     */
    public interface OnClickListener {
        void onClick(DialogInterface dialog, String code);
    }


}
