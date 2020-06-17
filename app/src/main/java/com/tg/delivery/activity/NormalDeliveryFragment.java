package com.tg.delivery.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tg.coloursteward.R;
import com.tg.coloursteward.baseModel.HttpResponse;

/**
 * @name
 * @class name：com.tg.delivery.activity
 * @class describe
 * @anthor QQ:510906433
 * @time 2020/6/17 14:28
 * @change
 * @chang time
 * @class describe
 */
public class NormalDeliveryFragment extends Fragment implements HttpResponse {

    private TextView tv_list_num;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_normal_deliviery, null);
        initView(view);
        initData();
        showTotalNum(0);
        return view;
    }

    private void initView(View view) {
        tv_list_num = view.findViewById(R.id.tv_list_num);

    }

    private void initData() {

    }

    private void showTotalNum(int size) {
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("共 ");
        stringBuffer.append(size);
        stringBuffer.append(" 个");
        int length = stringBuffer.toString().length();
        SpannableString spannableString = new SpannableString(stringBuffer.toString());
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#597EF7")), 2, length - 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#666666")), length - 2, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_list_num.setText(spannableString);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {

                }
                break;

        }
    }
}
