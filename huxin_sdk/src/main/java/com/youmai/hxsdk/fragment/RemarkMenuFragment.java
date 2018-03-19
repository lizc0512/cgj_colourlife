package com.youmai.hxsdk.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.dialog.listener.FragmentListener;
import com.youmai.hxsdk.sp.SPDataUtil;
import com.youmai.hxsdk.utils.DisplayUtil;

/**
 * Created by fylder on 2017/4/17.
 */

public class RemarkMenuFragment extends Fragment implements View.OnClickListener {

    private ImageView remarkImg;
    private TextView remarkText;

    private boolean hasRemark;

    FragmentListener listener;

    PopupWindow popupWindow;

    public void setListener(FragmentListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hx_item_card_menu_lay, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hasRemark = getArguments().getBoolean("hasRemark");

        remarkImg = (ImageView) view.findViewById(R.id.item_card_bottom_sheet_remark_img);
        remarkText = (TextView) view.findViewById(R.id.item_card_bottom_sheet_remark_text);
        view.findViewById(R.id.item_card_bottom_sheet_remark_lay).setOnClickListener(this);
        view.findViewById(R.id.item_card_bottom_sheet_send_remark_lay).setOnClickListener(this);
        view.findViewById(R.id.item_card_bottom_sheet_tip_lay).setOnClickListener(this);

        if (!hasRemark) {
            remarkImg.setImageResource(R.drawable.hx_im_card_menu_remark_selector);
            remarkText.setText(R.string.hx_im_card_sheet_new_remark);
        } else {
            showNewGuide(remarkImg);
            remarkImg.setImageResource(R.drawable.hx_im_card_menu_remark_edit_selector);
            remarkText.setText(R.string.hx_im_card_sheet_update_remark);

        }
    }

    @Override
    public void onClick(View v) {
        int index = 1;
        if (v.getId() == R.id.item_card_bottom_sheet_remark_lay) {
            index = 1;
        } else if (v.getId() == R.id.item_card_bottom_sheet_send_remark_lay) {
            index = 2;
        } else if (v.getId() == R.id.item_card_bottom_sheet_tip_lay) {
            index = 3;
        }
        if (listener != null) {
            listener.clickItem(index);
        }
    }

    //备注修改提示
    private void showNewGuide(final View view) {

        if (SPDataUtil.getFirstCardRemark(getContext()) && view != null) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    View popupView = LayoutInflater.from(getContext()).inflate(R.layout.hx_card_remark_tip_lay, null);
                    popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    popupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    popupWindow.setTouchable(true);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    popupWindow.setAnimationStyle(R.style.PopupAnimation2);
                    popupWindow.update();
                    int xOffset = view.getWidth() / 2 - popupView.getMeasuredWidth() / 2;//左偏移
                    int yOffset = -view.getHeight() - popupView.getMeasuredHeight() - DisplayUtil.dip2px(getContext(), 2);//上移高度
                    popupWindow.showAsDropDown(view, xOffset, yOffset);
                }
            });
            SPDataUtil.setFirstCardRemark(getContext(), false);//设置沟通功能已提示使用
        }
    }

}
