package com.tg.delivery.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
 * @time 2020/6/17 14:30
 * @change
 * @chang time
 * @class describe
 */
public class ErrorDeliveryFragment extends Fragment implements HttpResponse {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_error_deliviery, null);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {


    }

    private void initData() {

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
